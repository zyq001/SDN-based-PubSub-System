/**
 * @author shoren
 * @date 2013-4-24
 */
package org.Mina.shorenMinaTest.queues;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.msg.tcp.EmergencyMsg;
import org.Mina.shorenMinaTest.msg.tcp.MsgNotis;
import org.Mina.shorenMinaTest.msg.tcp.highPriority;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.*;

/**
 * 队列管理器：
 * 队列管理器只有一个，使用单例模式.
 * <p/>
 * 使用三级队列，第一级存储信息，第二级是发送队列，第三级是MINA client端的通道发送缓冲队列。
 * <p/>
 * 发送队列作为分发线程池的任务队列。
 * 发送队列加一个监听，控制信息入队.用一个单独的线程来做吧。同时监听紧急信息，有信息就优先发送。
 * 保存发送队列中的队列信息，以及各种信息入队的比例。
 * <p/>
 * 若队列已满，该丢弃还是阻塞？
 */
public class MsgQueueMgr {

	public static final int FIRSTPRIMSG = 1;   //0x0001,紧急信息单独一个通道,直接送到第三级队列
	public static final int SECONDPRIMSG = 2;  //0x0010
	public static final int THIRDPRIMSG = 4;   //0x0100
	public static final int FOURTHPRIMSG = 8;  //0x1000
	public static final int LOWESTPRI = 2;  //最低优先级
	//所有一级队列的大小,能够缓存所有的信息
	private static final int QUEUESIZE = 10000;
	//各级信息入队的比例,可调整
	private static final double INISECPRO = 0.5;  //第二优先级信息入队初始比例，十分之五
	private static final double INITHIRDPRO = 0.3;  //第三优先级信息入队初始比例，十分之三
	private static final double INIFOURPRO = 0.2;
	private static final double proin = 0.05;  //增加比重
	private static final double prode = 0.08;  //减少比重
	private static final double MINRATIO = 0.1;  //最低比例是0.1
	private static final int iniCoreSize = 4;
	private static final int iniMaxSize = 8;
	//	private static final int minSize = 2;
	private static final int stepde = 2;
	private static final int stepin = 1;
	private static final int taskSize = 5;  //任务队列的长度
//	private static int PRIORITYMSG = 14;        //默认包含除紧急信息之外的所有信息，0x1110
	protected static int PRIMSG = 0;
	protected static double secPro = INISECPRO;
	protected static double thirdPro = INITHIRDPRO;
	protected static double fourPro = INIFOURPRO;
	private static Log log = LogFactory.getLog(MsgQueueMgr.class);
	/******************************
	 * 信息部分
	 **********************/

	//有界阻塞队列,作为第一级队列
	//紧急信息,单独一个通道发送
	private static LinkedBlockingQueue<ForwardMsg> emergencyMsgQueue;   //优先级1
// 	private static final double MAXRATIO = 0.6;  //最高比例是0.6
	//TCP通知消息
	private static LinkedBlockingQueue<ForwardMsg> TCPNoticeMsgQueue;   //优先级2
	//TCP其他消息
	private static LinkedBlockingQueue<ForwardMsg> otherMsgQueue;       //优先级3
	//udp消息
	private static LinkedBlockingQueue<ForwardMsg> UDPMsgQueue;          //优先级4
	//发送队列，作为二级队列
	private static LinkedList<ForwardMsg> forwardQueue;
	private static double inQueueRatio = 0.8;  //十分之五
	//一次入队的数量
	private static int inQueueNum = 800;
	//优先级与队列的对应
//	private HashMap<String, ArrayBlockingQueue<ForwardMsg>> priority_queue;
	//线程安全的map
	private static ConcurrentHashMap<String, IoSession> dest_session;
	private static MsgQueueMgr INSTANCE = null;
	/**
	 * 调整入对信息的相关参数
	 */
	private static long last_update = 0;
	private static long now = 0;
	//线程池
	private ThreadPoolExecutor deliverWorker;

	private MsgQueueMgr() {
		//指定一个足够大小的容量
		emergencyMsgQueue = new LinkedBlockingQueue<ForwardMsg>(QUEUESIZE);
		TCPNoticeMsgQueue = new LinkedBlockingQueue<ForwardMsg>(QUEUESIZE);
		otherMsgQueue = new LinkedBlockingQueue<ForwardMsg>(QUEUESIZE);
		UDPMsgQueue = new LinkedBlockingQueue<ForwardMsg>(QUEUESIZE);
		forwardQueue = new LinkedList<ForwardMsg>();

		dest_session = new ConcurrentHashMap<String, IoSession>();
		//发送队列的入口
		new ForwardQueueExecutor().start();

		//发送队列的出口
		initThreadPool();
	}

	/***********************
	 * 方法区
	 *******************/
	public static MsgQueueMgr getInstance() {
		if (INSTANCE == null)
			INSTANCE = new MsgQueueMgr();
		return INSTANCE;
	}

	//根据策略，获取要发送的信息，放入发送队列中。
	protected static void addSubmitMsgToQueue() {
		ForwardMsg msg = null; //how to get msg ????????
		forwardQueue.offer(msg);
	}

	//从发送队列中获取发送信息，若队列为空，返回null.
	public static void getForwardMsg(LinkedList<ForwardMsg> temp, int count) {
		synchronized (forwardQueue) {
			for (int i = 0; i < count; i++) {
				ForwardMsg msg = forwardQueue.poll();
				if (msg == null)
					break;
				temp.offer(msg);
			}
		}
	}

	public static void addDestMsg(String dest, IoSession session) {
		dest_session.put(dest, session);
	}

	/**
	 * 加入优先级队列中，同时为每条信息指定优先级
	 */
	public static void addUDPMsgInQueue(ForwardMsg msg) {
		try {
			UDPMsgQueue.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg.setPriority(FOURTHPRIMSG);
		//	System.out.println("udpMsgQueue:" + msg.getMsg().msgToString());
	}

	/**
	 * 加入优先级队列中，同时为每条信息指定优先级
	 */
	public static void addTCPMsgInQueue(ForwardMsg forwardMsg) {
		WsnMsg msg = forwardMsg.getMsg();

		if (msg instanceof EmergencyMsg || msg instanceof highPriority) {
			try {
				emergencyMsgQueue.put(forwardMsg);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			forwardMsg.setPriority(FIRSTPRIMSG);
			//		System.out.println("emergencyMsgQueue:" + msg.msgToString());
		} else if (msg instanceof MsgNotis) {
			try {
				TCPNoticeMsgQueue.put(forwardMsg);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			forwardMsg.setPriority(SECONDPRIMSG);
			//		System.out.println("TCPNoticeMsgQueue:" + msg.msgToString());
		} else {
			try {
				otherMsgQueue.put(forwardMsg);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			forwardMsg.setPriority(THIRDPRIMSG);
			//		System.out.println("otherTCPMsgQueue:" + msg.msgToString());
		}

	}

	public static ConcurrentHashMap<String, IoSession> getDest_session() {
		return dest_session;
	}

	public static void setDest_session(ConcurrentHashMap<String, IoSession> dest_session) {
		MsgQueueMgr.dest_session = dest_session;
	}

	public static void deleteDest_session() {
		dest_session.clear();
	}

	public static void main(String[] args) {
		try {
			emergencyMsgQueue.put(new TCPForwardMsg("ip", 1234, null));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ForwardMsg msg = emergencyMsgQueue.peek();
		System.out.println(msg.getClass().getName());

	}

	//初始化线程池
	public void initThreadPool() {
		//core=4; max=8; time=3s; queuelen=3;
		deliverWorker = new ThreadPoolExecutor(iniCoreSize, iniMaxSize, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(taskSize), new ThreadPoolExecutor.CallerRunsPolicy());
		while (true) {
			if (forwardQueue.isEmpty()) {
				try {
					Thread.sleep(MinaUtil.freeze_time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				deliverWorker.execute(new SingleChannelQueueThread());


			}
		}

	}

	protected int getForwardQueueSize() {
		synchronized (forwardQueue) {
			return forwardQueue.size();
		}
	}

	//发送队列的监听线程，负责信息入队
	class ForwardQueueExecutor extends Thread {
		private final Log log = LogFactory.getLog(ForwardQueueExecutor.class);

		//入队的时候先用一个临时队列，然后一起加入到发送队列中
		//因为发送队列是线程安全的，不断地获取队列填入信息，会占用很多资源和时间
		private LinkedList<ForwardMsg> tempQueue;

		public ForwardQueueExecutor() {
			super();
			tempQueue = new LinkedList<ForwardMsg>();
		}

		public void run() {
			last_update = System.currentTimeMillis();
			while (true) {
				try {
					Thread.sleep(MinaUtil.freeze_time);
					adjustRatio();
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				enQueueWithRatio();
			}
		}

		/**
		 * 根据全局队列情况，调整入队比例.
		 * 还是要用两个state啊，状态改变后，间隔一定时间判断一次，不要一直索求资源.
		 * 我去，太复杂了，可读性也太差了。。。
		 */
		protected void adjustRatio() {

			now = System.currentTimeMillis();
			//间隔一定时间检查一次

			if (now - last_update < MinaUtil.checkRatio)
				return;

			int tcp_blocked = MinaUtil.getTCPBlockCount();
			int tcp_total = (MinaUtil.getTCPTotalCount() != 0) ? MinaUtil.getTCPTotalCount() : 1;
			int udp_blocked = MinaUtil.getUDPBlockedCount();
			int udp_total = (MinaUtil.getUDPTotalCount() != 0) ? MinaUtil.getUDPTotalCount() : 1;
			double tcpRatio = (double) tcp_blocked / tcp_total;
			double udpRatio = (double) udp_blocked / udp_total;
			int state = MinaUtil.getState();

			//当前健康状态
			if ((tcpRatio <= MinaUtil.TcpRatio_unwell) && (udpRatio <= MinaUtil.UdpRatio_unwell)) {
				//将所有参数调为正常状态
				MinaUtil.setState(MinaUtil.HEALTHY);
				deliverWorker.setCorePoolSize(iniCoreSize);
				deliverWorker.setMaximumPoolSize(iniMaxSize);
				secPro = INISECPRO;
				thirdPro = INITHIRDPRO;
				fourPro = INIFOURPRO;
				MinaUtil.deFreezeTime();

			}

			//当前ill状态，此时tcp、udp通道均为sick,丢包
			else if ((tcpRatio >= MinaUtil.TcpRatio_sick) && (udpRatio >= MinaUtil.UdpRatio_sick)) {

				state = (MinaUtil.ILL_TCP | MinaUtil.ILL_UDP);
				MinaUtil.setState(state);
				deliverWorker.setCorePoolSize(iniCoreSize);
				deliverWorker.setMaximumPoolSize(iniCoreSize);
				//System.out.println("线程池容量减小---0");

				MinaUtil.inFreezeTime();
			} else {
				//先结合之前的状态，查看是否有缓解，确定状态state，再采取措施
				if ((tcpRatio >= MinaUtil.TcpRatio_sick) || (udpRatio >= MinaUtil.UdpRatio_sick)) {
					//当前为sick状态
					if (tcpRatio >= MinaUtil.TcpRatio_sick) {
						//若之前也是sick状态，则情况在变糟，变为ill状态
						if (isState(state, MinaUtil.SICK_TCP)) {
							state &= (~MinaUtil.SICK_TCP);
							state |= MinaUtil.ILL_TCP;

							int curSize = deliverWorker.getMaximumPoolSize();
							curSize -= stepde;
							if (curSize < iniCoreSize) {
								curSize = iniCoreSize;
							}
							deliverWorker.setMaximumPoolSize(curSize);
							//System.out.println("线程池容量减小---1");

							MinaUtil.inFreezeTime();

						} else {
							//仍然是sick状态
							state &= 0x11110000;
							state |= MinaUtil.SICK_TCP;
						}
					}
					//so as udp
					if (udpRatio >= MinaUtil.UdpRatio_sick) {
						//若之前也是sick状态，则情况在变糟，变为ill状态
						if (isState(state, MinaUtil.SICK_UDP)) {
							state &= (~MinaUtil.SICK_UDP);
							state |= MinaUtil.ILL_UDP;

							int curSize = deliverWorker.getMaximumPoolSize();
							curSize -= stepde;
							if (curSize < iniCoreSize) {
								curSize = iniCoreSize;
							}
							deliverWorker.setMaximumPoolSize(curSize);
							//System.out.println("线程池容量减小----2");
						} else {
							//仍然是sick状态
							state &= 0x1111;
							state |= MinaUtil.SICK_UDP;
						}
					}
				} else {
					//当前为unwell，调节比例				
					if ((tcpRatio > MinaUtil.TcpRatio_unwell) && (tcpRatio < MinaUtil.TcpRatio_sick)) {
						//若之前还是unwell状态，说明情况在恶化，变为sick状态
						if (isState(state, MinaUtil.UNWELL_TCP)) {
							state &= (~MinaUtil.UNWELL_TCP);
							state |= MinaUtil.SICK_TCP;
						} else {
							//其他情况，还是unwell状态
							state &= 0x11110000;
							state |= MinaUtil.UNWELL_TCP;
						}

						MinaUtil.inFreezeTime();

					}

					if ((udpRatio > MinaUtil.UdpRatio_unwell) && (udpRatio < MinaUtil.UdpRatio_sick)) {
						//若之前还是unwell状态，说明情况在恶化，变为sick状态
						if (isState(state, MinaUtil.UNWELL_UDP)) {
							state &= (~MinaUtil.UNWELL_UDP);
							state |= MinaUtil.SICK_UDP;
						} else {
							//其他情况，还是unwell状态
							state &= 0x1111;
							state |= MinaUtil.UNWELL_UDP;
						}

						MinaUtil.inFreezeTime();

					}

				}

				//根据状态，采取措施	
				//若是sick状态，则降低分发速度
				if (isState(state, MinaUtil.SICK_TCP) || isState(state, MinaUtil.SICK_UDP)) {
					int curSize = deliverWorker.getMaximumPoolSize();
					curSize -= stepde;
					if (curSize < iniCoreSize) {
						curSize = iniCoreSize;
					}
					deliverWorker.setMaximumPoolSize(curSize); //???????????
					//System.out.println("线程池容量减小----3");
				} else {
					//若是unwell状态，调节比例
					if (isState(state, MinaUtil.UNWELL_TCP)) {
						thirdPro -= prode;
						if (thirdPro < MINRATIO)
							thirdPro = MINRATIO;
					}
					if (isState(state, MinaUtil.UNWELL_UDP)) {
						fourPro -= prode;
						if (fourPro < MINRATIO)
							fourPro = MINRATIO;
					}

					if (tcpRatio < MinaUtil.TcpRatio_unwell) {
						if ((state & 0x1111) > MinaUtil.HEALTHY_TCP) {
							//增加比例
							if (secPro < INISECPRO) {
								secPro += proin;
								if (secPro > INISECPRO)
									secPro = INISECPRO;
							} else if (thirdPro < INITHIRDPRO) {
								thirdPro += proin;
								if (thirdPro > INITHIRDPRO)
									thirdPro = INITHIRDPRO;
							}
						}
					}
					if (udpRatio < MinaUtil.UdpRatio_unwell) {
						if ((state & 0x11110000) > MinaUtil.HEALTHY_UDP) {
							//增加比例
							if (fourPro < INIFOURPRO) {
								fourPro += proin;
								if (fourPro > INIFOURPRO)
									fourPro = INIFOURPRO;
							}
						}
					}

					//增加分发速度
					int curSize = deliverWorker.getMaximumPoolSize();
					curSize += stepin;
					if (curSize > iniMaxSize)
						curSize = iniMaxSize;
					deliverWorker.setMaximumPoolSize(curSize);
					//System.out.println("线程池容量增大");

					MinaUtil.deFreezeTime();

				}

				MinaUtil.setState(state);
				//over					
			}
			last_update = System.currentTimeMillis();

			ConcurrentHashMap destmap = MsgQueueMgr.getDest_session();


		}

		protected int updateState(double tcpRatio, double udpRatio) {
			int state = 0;
			if (tcpRatio < MinaUtil.TcpRatio_unwell)
				state |= MinaUtil.HEALTHY_TCP;
			else if (tcpRatio > MinaUtil.TcpRatio_sick)
				state |= MinaUtil.SICK_TCP;
			else
				state |= MinaUtil.UNWELL_TCP;

			if (udpRatio < MinaUtil.UdpRatio_unwell)
				state |= MinaUtil.HEALTHY_UDP;
			else if (udpRatio > MinaUtil.UdpRatio_sick)
				state |= MinaUtil.SICK_TCP;
			else
				state |= MinaUtil.UNWELL_UDP;

			return state;
		}

		public boolean isState(int state, int targetState) {
			return ((state & targetState) != 0);
		}

		//一次只有一个标志位有标识，不存在最坏的
		protected int worstTcpState(int state) {
			if (isState(state, MinaUtil.ILL_TCP))
				return MinaUtil.ILL_TCP;
			if (isState(state, MinaUtil.SICK_TCP))
				return MinaUtil.SICK_TCP;
			if (isState(state, MinaUtil.UNWELL_TCP))
				return MinaUtil.UNWELL_TCP;

			return MinaUtil.HEALTHY_TCP;
		}

		protected int WorstUdpState(int state) {
			if (isState(state, MinaUtil.ILL_UDP))
				return MinaUtil.ILL_UDP;
			if (isState(state, MinaUtil.SICK_UDP))
				return MinaUtil.SICK_UDP;
			if (isState(state, MinaUtil.UNWELL_UDP))
				return MinaUtil.UNWELL_UDP;

			return MinaUtil.HEALTHY_UDP;
		}

		/**
		 * 各类信息按比例入队列
		 */
		protected void enQueueWithRatio() {
			//入队长度阀值
			int minth = (int) (inQueueNum * inQueueRatio);
			int msgCount = 0;  //还可入队的信息数
			//	log.info("发送队列长度：" + forwardQueue.size());
			//队列略空，信息入队
			if (getForwardQueueSize() < minth) {
				//	if(forwardQueue.size() > 0){
				//		log.info("发送队列饥饿……队列长度：" + forwardQueue.size());
				//	}

				msgCount = inQueueNum;
				//若出现紧急信息，优先入队
				if (!emergencyMsgQueue.isEmpty() && msgCount > 0) {
					PRIMSG |= FIRSTPRIMSG;
					msgCount = calculateMsg(emergencyMsgQueue, tempQueue, msgCount);
//					log.info("####第一优先级入队数：" + (inQueueNum - msgCount) + "  剩余：" + msgCount);
				} else {
					//剔除信息标志位
					PRIMSG &= (~FIRSTPRIMSG);
				}

				//若队列未填满，剩下的信息继续按比例入队
				if (!TCPNoticeMsgQueue.isEmpty() && msgCount > 0 && secPro != 0) {
					PRIMSG |= SECONDPRIMSG;
					int num = calculateMsg(TCPNoticeMsgQueue, tempQueue,
							(int) (msgCount * secPro));
					msgCount = msgCount - (int) (msgCount * secPro - num);
					//log.info("####第二优先级入队数：" + test1 + "  剩余：" + msgCount);

				} else {
					//剔除信息标志位
					PRIMSG &= (~SECONDPRIMSG);
				}

				//若上级队列没有填满，则剩下的按比例填入。除数加上0.0001，是为了防止除数为0.
				if (!otherMsgQueue.isEmpty() && msgCount > 0 && thirdPro != 0) {
					PRIMSG |= THIRDPRIMSG;
					int temp = Math.round((float) (msgCount * thirdPro / (fourPro + thirdPro + 0.0001)));
					int num = calculateMsg(otherMsgQueue, tempQueue,
							temp);
					msgCount = msgCount - (temp - num);
					//log.info("####第三优先级入队数：" + (temp - num) + "  剩余：" + msgCount);
				} else {
					//剔除信息标志位
					PRIMSG &= (~THIRDPRIMSG);
				}

				//剩下的全填入低级信息
				if (!UDPMsgQueue.isEmpty() && msgCount > 0 && fourPro != 0) {
					PRIMSG |= FOURTHPRIMSG;
					calculateMsg(UDPMsgQueue, tempQueue, msgCount);
				} else {
					//剔除信息标志位
					PRIMSG &= (~FOURTHPRIMSG);
				}

				//将临时队列中的信息全部入发送队列
				synchronized (forwardQueue) {
					while (true) {
						ForwardMsg msg = tempQueue.poll();   //获取并移除此列表的头（第一个元素)
						if (msg == null)
							break;
						forwardQueue.offer(msg);

					}
				}
			}

		}


		//将count条信息从一个队列复制到另一个队列中,返回未填入信息的个数
		protected int calculateMsg(LinkedBlockingQueue<ForwardMsg> source,
		                           LinkedList<ForwardMsg> dest, int count) {
			synchronized (source) {
				while (count > 0) {
					ForwardMsg msg = source.poll();
					if (msg == null)
						break;

					dest.offer(msg);  //add to tail
					count--;
				}
			}
			return count;
		}
	}


	public class listen extends Thread {
		ArrayList<String> aimAddr = null;

		public listen() {
			ArrayList<String> aimAddr = MinaUtil.putIP();
		}

		public void run() {
			for (int i = 0; i < aimAddr.size(); i++) {

			}
		}
	}

}
