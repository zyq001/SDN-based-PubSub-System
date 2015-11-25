/**
 * @author shoren
 * @date 2013-6-5
 */
package org.Mina.shorenMinaTest;

import org.Mina.shorenMinaTest.filters.ShorenCodecFactory;
import org.Mina.shorenMinaTest.handlers.DatagramAcceptorHandler;
import org.Mina.shorenMinaTest.handlers.DatagramConnectorHandler;
import org.Mina.shorenMinaTest.handlers.SocketAcceptorHandler;
import org.Mina.shorenMinaTest.handlers.SocketConnectorHandler;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.msg.tcp.MsgInsert;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.*;
import java.lang.reflect.Field;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//import shorenMinaTest.test.ServerTest;

/**
 *
 */
public class MinaUtil {
	//整体状态，用一个数字表示所有状态
	public static final int HEALTHY = 17;
	public static final int HEALTHY_TCP = 1;
	public static final int UNWELL_TCP = 2;
	public static final int SICK_TCP = 4;
	public static final int ILL_TCP = 8;
	public static final int HEALTHY_UDP = 16;
	public static final int UNWELL_UDP = 32;
	public static final int SICK_UDP = 64;
	public static final int ILL_UDP = 128;
	//单个通道的状态
	public static final String SHealthy = "healthy";
	public static final String SSick = "sick";
	public static final String SDead = "dead";
	/**
	 * unwell表示轻度阻塞，适当调整信息入队比例。
	 * sick是出现阻塞较严重，降低入队速度。
	 * 若tcp、udp均阻塞较严重，就在线程池分发数据时从低优先级开始丢包了，同时将其入队比重将为0。
	 */
	public static final double TcpRatio_unwell = 0.3;
	public static final double TcpRatio_sick = 0.5;
	public static final double UdpRatio_unwell = 0.3;
	public static final double UdpRatio_sick = 0.5;
	//通道允许的空闲时间，即空闲60秒后，关闭通道
	protected static final int IDLE_TIME = 3;  //unit is second
	/**
	 * 每条通道上Queue长度阀值.
	 * 若TCP与UDP阀值不同，则在SingleChannelQueueThread类中设置，作为session的attribute。
	 * 适用范围： 小于minth时，队列健康；大于minth且小于maxth时，队列sick，算丢包率，
	 * 从低优先级开始丢。
	 */
	protected static final int singleMinth = 3000;
	protected static final int singleMaxth = 4000;
	public static int testMax = 300;
	public static int tPort = 3;
	public static int uPort = 30002;
	public static int lowUPort = 30003;
	public static int highUPort = 30001;
	public static int maxThree = 0;//记录发送队列的最大长度
	public static int freeze_timeIni = 75;//初始的入队间隔
	public static int freeze_time = freeze_timeIni; //间隔时间?ms
	public static int freeze_timeMax = 100;//入队时间间隔的上限是100ms
	public static int freeze_timeMin = 50;//入队间隔的下限是20ms
	public static ArrayList<String> forwardIP = null;
	public static int checkRatio = 20;
	public static int a = 0;
	//for test
	protected static int senders = 0;
	//public static int PreConfigedTimeMin = MinaUtil.getMinEnQueueTime();
	protected static Logger logger = Logger.getLogger(MinaUtil.class);
	//单道阻塞的个数,总个数是session的个数
	protected static int TCPBlockedCount = 0;
	protected static int UDPBlockedCount = 0;
	//通道的总个数，在新生成session的时候增加
	protected static int TCPTotalCount = 0;
	protected static int UDPTotalCount = 0;
	protected static int state = HEALTHY;
	protected static int last_state = state;  //delete?

	public static synchronized void incSenders() {
		senders++;
	}

	public static int getSenders() {
		return senders;
	}

	/**
	 * 使用的是最简单的RED算法，计算丢包率。
	 * 参数：
	 * double weight：权重
	 * int avg：队列的平均长度（初始化为0，表示的是当前队列的长久积累值）
	 * int qLength：当前队列的长度
	 * double maxp ：丢包率的最大值
	 * int count：从上次丢包起，到现在接收的信息的数目
	 */
	public static int calPacket_loss_rate(IoSession session) {
		double rate = 0;
		double weight = (Double) session.getAttribute("weight");
		int avg = (Integer) session.getAttribute("avg");
		int qLength = (Integer) session.getAttribute("qLength");
		double maxp = (Double) session.getAttribute("maxp");
		int count = (Integer) session.getAttribute("count");

		avg = (int) ((1 - weight) * avg + weight * qLength);
		double tempRate = maxp * (avg - singleMinth) / (singleMaxth - singleMinth);

		rate = tempRate * (1 - count * tempRate);
		int lossCount = (int) (qLength * rate);

		//System.out.println("丢包率计算：avg = "+avg+"   tempRate = "+tempRate+"   rate = "+rate+"   qLentgh= "+qLength+"   lossCount = "+lossCount);

		session.setAttribute("lossCount", lossCount);
		session.setAttribute("avg", avg);
		session.setAttribute("count", 0);
		if (tempRate < 0)
			return 0;
		else
			return lossCount;
	}

	/**
	 * 初始化一些参数,这些参数需要测试
	 * double weight：权重
	 * int avg：队列的平均长度（初始化为0，表示的是当前队列的长久积累值）
	 * int qLength：当前队列的长度
	 * double maxp ：丢包率的最大值
	 * int count：从上次丢包起，到现在接收的信息的数目
	 */
	public static void iniSessionReferance(IoSession session) {
		session.setAttribute("weight", 0.5);
		session.setAttribute("avg", 0);
		session.setAttribute("qLength", 0);
		session.setAttribute("maxp", 0.5);
		session.setAttribute("count", 0);

		session.setAttribute("lowestPriority", MsgQueueMgr.LOWESTPRI);
		session.setAttribute("lossCount", 0);
		session.setAttribute("state", SHealthy);
		session.setAttribute("last_state", SHealthy);

		/*if(ServerTest.test){
			session.setAttribute("inCount", 0);
			session.setAttribute("outCount", 0);
			session.setAttribute("totalCount", 0);
		}*/

	}
	//获取配置文件中的入队间隔
/*	public static int getMinEnQueueTime(){
		Configuration configuration;
		configuration = new Configuration();
		boolean ManagerOn = configuration.configure2();
		return a = configuration.EnQueueTime;
	}*/

	public static int getSingleminth() {
		return singleMinth;
	}

	public static int getSinglemaxth() {
		return singleMaxth;
	}

	public static int getTCPTotalCount() {
		return TCPTotalCount;
	}

	public static int getUDPTotalCount() {
		return UDPTotalCount;
	}

	//TCP全部通道数量
	public static synchronized void inTCPTotalCount() {
		TCPTotalCount++;
	}

	//UDP全部通道数量
	public static synchronized void deTCPTotalCount() {
		TCPTotalCount--;
	}

	//单通道阻塞后，增加阻塞计数器
	public static synchronized void inUDPTotalCount() {
		UDPTotalCount++;
	}

	//单通道解除阻塞后，减少阻塞计数器
	public static synchronized void deUDPTotalCount() {
		UDPTotalCount--;
	}


	//单通道阻塞后，增加阻塞计数器
	public static synchronized void inTCPBlockCount() {
		TCPBlockedCount++;
		//	System.out.println("TCPblockedCount = "+ TCPBlockedCount);
	}

	//单通道解除阻塞后，减少阻塞计数器
	public static synchronized void deTCPBlockCount() {
		TCPBlockedCount--;
		//	System.out.println("TCPBlockedCount--");
	}

	//单通道阻塞后，增加阻塞计数器
	public static synchronized void inUDPBlockCount() {
		UDPBlockedCount++;
		//	System.out.println("UDPBlockedCount++");
	}

	//单通道解除阻塞后，减少阻塞计数器
	public static synchronized void deUDPBlockCount() {

		UDPBlockedCount--;
		if (UDPBlockedCount < 0) {
			UDPBlockedCount = 0;
		}
		//	System.out.println("UDPBlockedCount--");
	}


	public static int getTCPBlockCount() {
		return TCPBlockedCount;
	}

	public static int getUDPBlockedCount() {
		return UDPBlockedCount;
	}

	public static int getState() {
		return state;
	}

	/**
	 * 不用设为同步，只有在MsgQueueMgr中才会调用。
	 */
	public static void setState(int state) {
		MinaUtil.state = state;
	}

	public static int getLast_state() {
		return last_state;
	}

	public static void setLast_state(int last_state) {
		MinaUtil.last_state = last_state;
	}

	//增加入队时长，减小入队压力
	public static void inFreezeTime() {
		freeze_time += 1;
		if (freeze_time > freeze_timeMax) {
			freeze_time = freeze_timeMax;
		}
	}

	//减少入队时长，增加发送速度
	public static void deFreezeTime() {
		freeze_time -= 1;
		if (freeze_time < freeze_timeMin) {
			freeze_time = freeze_timeMin;
		}
	}


	//create NioSocketAcceptor  with port
	public static NioSocketAcceptor createSocketAcceptor(String ip, int port) {
		// 创建一个非阻塞的server端的Socket
		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		try {
			// 设置过滤器  
			setFilters(acceptor);
			//若绑定threadPool，则使用此线程池建立线程执行业务逻辑（IoHandler）处理
			//acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(3,3));
			// 设置读取数据的缓冲区大小
			acceptor.getSessionConfig().setReadBufferSize(8190000);

			acceptor.getSessionConfig().setMaxReadBufferSize(100000000);//100MB
			// 读写通道10秒内无操作进入空闲状态
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
			// 绑定逻辑处理器
			acceptor.setHandler(new SocketAcceptorHandler());
			// 绑定端口
			acceptor.bind(new InetSocketAddress(InetAddress.getByName(ip), port));
			logger.info("服务端启动成功... tcp端口号为：" + port);
			System.out.println("服务端启动成功... tcp端口号为：" + port);
		} catch (Exception e) {
			logger.error("服务端tcp启动异常....", e);
			e.printStackTrace();
		}

		return acceptor;
	}


	//create NioSocketConnector with port and IP
	//注意connector.dispose();
	public static NioSocketConnector createSocketConnector() {
		NioSocketConnector connector = new NioSocketConnector();
		// 设置过滤器  
		setFilters(connector);
		//若绑定threadPool，则使用此线程池建立线程执行业务逻辑（IoHandler）处理
		connector.getFilterChain().addLast("threadPool", new ExecutorFilter(3, 3));
		connector.setConnectTimeoutCheckInterval(30);
		connector.getSessionConfig().setSendBufferSize(100000000);//100MB
		connector.setHandler(new SocketConnectorHandler());//设置事件处理器
		connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE_TIME);
		//一个connector可以连接多个服务器，所以连接操作应该放在外面
	   /* ConnectFuture cf = connector.connect(new InetSocketAddress(ip, port));//建立连接
	    cf.awaitUninterruptibly();//等待连接创建完成
*/
		return connector;
	}


	//create NioDatagramAcceptor with port
	public static NioDatagramAcceptor createDatagramAcceptor(String ip, int port) {
		NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
		// 设置过滤器  
//		setFilters(acceptor);
		acceptor.getSessionConfig().setReceiveBufferSize(100000000);//100MB
		acceptor.setHandler(new DatagramAcceptorHandler());
		// 绑定端口  
		try {
			acceptor.bind(new InetSocketAddress(Inet6Address.getByName(ip), port));
//			acceptor.bind(new InetSocketAddress(InetAddress.getByName("fe80::5054:ff:fe98:bec0"), port));

			logger.info("服务端启动成功... udp端口号为：" + port);
		} catch (IOException e) {
			logger.error("服务端udp启动异常....", e);
			e.printStackTrace();
		}
		return acceptor;
	}

	//create NioDatagramConnector with port and IP
	public static NioDatagramConnector createDatagramConnector() {
		NioDatagramConnector connector = new NioDatagramConnector();
		// 设置过滤器  
//		setFilters(connector);
		connector.setHandler(new DatagramConnectorHandler());
		connector.getSessionConfig().setSendBufferSize(100000000);//100MB
		connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE_TIME);
		//	ConnectFuture cf = connector.connect(new InetSocketAddress(ip, port));//建立连接
		//     cf.awaitUninterruptibly();//等待连接创建完成
		return connector;
	}

	public static NioDatagramConnector CreatBoardcast() {
		NioDatagramConnector connector = new NioDatagramConnector();
		// 设置过滤器  
		setFilters(connector);
		connector.setHandler(new DatagramConnectorHandler());
		connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE_TIME);
		//	ConnectFuture cf = connector.connect(new InetSocketAddress(ip, port));//建立连接
		//     cf.awaitUninterruptibly();//等待连接创建完成
		return connector;
	}

	//每一对client和server的配置一致,这里主要是对过滤链的配置
	//config acceptor and connector with filters
	//要加上自己的codec
	public static void setFilters(IoService service) {

//		service.getFilterChain().addLast("logger", new LoggingFilter());  
		service.getFilterChain().addLast("codec1",
				//new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
				new ProtocolCodecFilter(new ShorenCodecFactory(Charset.forName("UTF-8"))));

//		service.getFilterChain().addLast("filterthreadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
	}


	@SuppressWarnings({"rawtypes"})
	public static String msgToString(WsnMsg msg) {
		Class type = msg.getClass();
		String className = type.getName();
		StringBuilder content = new StringBuilder();
		if (type != null) {
			content.append("className=");
			content.append(className);
			content.append(";");
			Field[] fields = type.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				try {
					String value = getFieldValue(field, msg);
					if (value != null) {
						content.append(field.getName());
						content.append("=");
						content.append(value);
						content.append(";");
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		return content.toString();
	}


	public static String getFieldValue(Field field, Object obj) {
		String value = null;
		Object v = null;
		if (((field.getModifiers() & 0x05) == 0) || ((field.getModifiers() & 0x08) != 0))  //非public和protected,返回空;或者static
			return value;
		try {
			v = field.get(obj);
			if (v != null) {
				value = v.toString();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return value;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static WsnMsg stringToMsg(String smsg, WsnMsg msg) {
		Class type = msg.getClass();
		if (type != null && smsg != "") {
			//将域名与值保存起来
			String[] values = smsg.split(";");

			Map<String, String> valuesMap = new HashMap();
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				int index = value.indexOf("=");
				String k = value.substring(0, index);
				String v = value.substring(index + 1);
				valuesMap.put(k, v);
			}

			//为对象赋值
			Field[] fields = type.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				Object value = valuesMap.get(field.getName());
				if (field.getType() == Boolean.class) {
					value = new Boolean(value.equals("1")
							|| String.valueOf(value).equalsIgnoreCase("true"));
				}
				try {
					field.set(msg, value);

				} catch (IllegalArgumentException e) {
					if (field != null) {
						Class ftype = field.getType();
						Object v = convertValueFromString(ftype, value);
						try {
							field.set(msg, v);
						} catch (IllegalArgumentException e2) {

							e2.printStackTrace();
						} catch (IllegalAccessException e3) {

							e3.printStackTrace();
						}
					}

				} catch (IllegalAccessException e) {

					e.printStackTrace();
				}
			}
		}

		return msg;
	}


	@SuppressWarnings("deprecation")
	protected static Object convertValueFromString(Class type, Object value) {
		if (value instanceof String && type.isPrimitive()) {
			String tmp = (String) value;

			if (type.equals(boolean.class)) {
				if (tmp.equals("1") || tmp.equals("0")) {
					tmp = (tmp.equals("1")) ? "true" : "false";
				}

				value = new Boolean(tmp);
			} else if (type.equals(char.class)) {
				value = new Character(tmp.charAt(0));
			} else if (type.equals(byte.class)) {
				value = new Byte(tmp);
			} else if (type.equals(short.class)) {
				value = new Short(tmp);
			} else if (type.equals(int.class)) {
				value = new Integer(tmp);
			} else if (type.equals(long.class)) {
				value = new Long(tmp);
			} else if (type.equals(float.class)) {
				value = new Float(tmp);
			} else if (type.equals(double.class)) {
				value = new Double(tmp);
			}
		} else if (value instanceof String) {
			if (type.equals(java.util.Date.class)) {
				value = new Date((String) value);
			}
		}

		return value;
	}

	public static MsgInsert geMsgTest() {
		MsgInsert ms = new MsgInsert();
		ms.tagetGroupName = "this group";
		ms.name = "there is msg inserted";
		ms.addr = "localAddress";
		ms.id = 123456789;
		ms.tPort = 10243;
		ms.uPort = 10244;
		return ms;
	}

	//获取当前的入队间隔
	public static int GetFreezeTimeMin() {
		return freeze_timeMin;
	}

	//设置入队间隔下限
	public static void SetFreezeTimeMin(int aim) {
		freeze_timeMin = aim;
		if (freeze_timeMin > 100) {
			freeze_timeMin = 100;
		}
		try {
			MinaUtil.UpdataConfigure(freeze_timeMin);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//将更新后的入队间隔写入配置文件
	private static void UpdataConfigure(int newTime) throws IOException {
		int aimline = 29;
		BufferedReader br = new BufferedReader(new FileReader("configure.txt"));
		StringBuffer sb = new StringBuffer(4096);
		String temp = null;
		String NewTime = String.valueOf(newTime);
		int line = 0;
		while ((temp = br.readLine()) != null) {
			line++;
			if (line == aimline) {
				sb.append("EnqueueTime:" + NewTime);
				continue;
			}
			sb.append(temp).append("\r\n");
		}
		br.close();
		BufferedWriter bw = new BufferedWriter(new FileWriter("configure.txt"));
		bw.write(sb.toString());
		bw.close();
	}

	//获取阻塞的session
	public static String GetSession(IoSession session) {
		return session.toString();
	}


	//暂时用来设置转发目的ip
	public static ArrayList<String> putIP(ArrayList<String> forwardIP) {

		//forwardIP.add("10.109.253.23");
		forwardIP.add("10.109.253.19");
		return forwardIP;
	}

	@SuppressWarnings("null")
	public static ArrayList<String> putIP() {

		forwardIP.add("10.109.253.17");
		forwardIP.add("10.109.253.16");

		return forwardIP;
	}

}
