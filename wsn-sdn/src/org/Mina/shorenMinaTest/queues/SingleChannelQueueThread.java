/**
 * @author shoren
 * @date 2013-4-28
 */
package org.Mina.shorenMinaTest.queues;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.handlers.Start;

/**
 *
 *用于信息分发，将各个信息送入特定的通道，若没有则新建通道。
 *发送线程池的线程任务，其中forwardQueue是线程池的任务队列。
 *信息分发部分不会出现阻塞，因为线程只是将信息送入通道，通道内的队列满时，单通道会自己处理，
 *从而保证信息分发不会阻塞。
 *
 *全局阻塞时，控制丢包操作。在分发前，先判断是否丢弃信息。
 *全局阻塞的判断，放在单通道中，若自己阻塞，则增加阻塞计数器，同时查看是否全局阻塞，再相应的
 *修改全局阻塞标识。
 *
 *此处线程的操作比较简单，是为了避免此处分发成为发送部分的瓶颈。
 *有两种可能成为瓶颈：
 *   一是线程数有限，后面的通道数较多，供不应求；
 *   二是读取发送队列的队头信息时，只允许单一线程读取，采用同步操作，可能会影响分发速度。
 */
public class SingleChannelQueueThread implements Runnable {
	
	private final Log log = LogFactory.getLog(SingleChannelQueueThread.class);
	private static Object synObject = new Object();

	int count = 0;
	@Override
	public void run() {	
		//获取，移除，若发送不成功，则再加入
		LinkedList<ForwardMsg> temp = new LinkedList<ForwardMsg>();
		MsgQueueMgr.getForwardMsg(temp, 20);
		if(temp.size() < 1)
			return;
		
		for(ForwardMsg forwardMsg : temp){
			if(forwardMsg == null) return;
			
			//判断是否全局阻塞，若阻塞则去掉低优先级的信息，若正常，则直接分发
			int state = MinaUtil.getState();
			if(isState(state, MinaUtil.ILL_TCP)){
				//阻塞则去掉低优先级的信息,然后返回。若信息不应丢弃，则正常发送
				MsgQueueMgr.fourPro = 0;
				if(forwardMsg instanceof UDPForwardMsg)
					return;
				
				if(forwardMsg instanceof TCPForwardMsg){
					//若第三优先级队列非空，且信息是第三优先级的，则丢，同时禁止信息入队列
					if((MsgQueueMgr.PRIMSG & MsgQueueMgr.THIRDPRIMSG) != 0){
						MsgQueueMgr.thirdPro = 0;
						if(forwardMsg.getPriority() == MsgQueueMgr.THIRDPRIMSG)
							return;
					}else if((MsgQueueMgr.PRIMSG & MsgQueueMgr.SECONDPRIMSG) != 0){
						MsgQueueMgr.secPro = 0;
						if(forwardMsg.getPriority() == MsgQueueMgr.SECONDPRIMSG)
							return;
					}
				}
				
			}
			if(isState(state, MinaUtil.ILL_UDP)){
				//若udp通道阻塞严重，则丢包
				MsgQueueMgr.fourPro = 0;
				if(forwardMsg instanceof UDPForwardMsg)
					return;
			}
			//正常，则直接分发
			forwardQueueMsg(forwardMsg);
		}
		
	}	//finally, it is over~~~	
		
		
	@SuppressWarnings("rawtypes")
	protected void forwardQueueMsg(ForwardMsg forwardMsg){
		if(forwardMsg == null) return;		
		IoSession session = null;
		
		
		//先看dest_session中是否有session，若有则使用session，否则，新建connector
		ConcurrentHashMap destmap = MsgQueueMgr.getDest_session();

		for(int i=0;i<forwardMsg.getDestination().size();i++){

			
		if(destmap.containsKey(forwardMsg.getDestination().get(i).toString())){
			session = (IoSession) destmap.get(forwardMsg.getDestination().get(i).toString());
			
		}else{
			synchronized(synObject){//线程同步，同一时间只允许一个线程做以下操作。DoubleCheck 防止对同一个ip生成多个session

				if(destmap.containsKey(forwardMsg.getDestination().get(i).toString())){
					session = (IoSession) destmap.get(forwardMsg.getDestination().get(i).toString());
				}else{
					
					String ip = forwardMsg.getDestination().get(i).getAddr();
					int port = forwardMsg.getDestination().get(i).getPort();
					
					System.out.println("新建的ip是：\n"+ip+"新建的port是："+port);
					
			//		if(isIn(ip)){
					if(forwardMsg instanceof TCPForwardMsg){
							NioSocketConnector connector = MinaUtil.createSocketConnector();
							ConnectFuture cf = connector.connect(new InetSocketAddress(ip, port));//建立连接   
							cf.awaitUninterruptibly();//等待连接创建完成   

							try {
								session = cf.getSession();
							} catch (Exception e) {
								//System.out.println("建立连接失败！请检查"+ip+"节点！");
								Start.removeAimAddr(ip);
								return;
								// TODO: handle exception
							}
							    session.setAttribute("addr",forwardMsg.getDestination().get(i).toString());
							    MsgQueueMgr.addDestMsg(forwardMsg.getDestination().get(i).toString(),session);
					}else if(forwardMsg instanceof UDPForwardMsg){
						NioDatagramConnector connector = MinaUtil.createDatagramConnector();
						ConnectFuture cf = connector.connect(new InetSocketAddress(ip, port));//建立连接   
						cf.awaitUninterruptibly();								
						session = cf.getSession();	
						session.setAttribute("addr", forwardMsg.getDestination().get(i).toString());
						MsgQueueMgr.addDestMsg(forwardMsg.getDestination().get(i).toString(), session);
					}
					//保存session与destination对	
					/*}else{
						return;
					}*/
				}				
			
		}
		}
		
		

		//若是第一优先级信息，直接发送
		if(forwardMsg.getPriority() != MsgQueueMgr.FIRSTPRIMSG){
			//若阻塞严重，且session为dead，则不用分发
			int state = MinaUtil.getState();
	    	 //136 = 8 + 128
	    	 if(((state & 136) != 0) && 
	    			 ((String)session.getAttribute("state")).equals(MinaUtil.SDead)){
	    		 return;
	    	 }
	    	 
	    	    int lowestPri = 2;
	 		    int curPri = forwardMsg.getPriority();
	 		
			//丢包的具体操作
	    	 int lossCount = (Integer) session.getAttribute("lossCount");
	    	 if(lossCount != 0){   		
	    		if(curPri >= lowestPri)  //若此信息的优先级低，则丢弃，不发送
	    		{
	    			//log.info("drop the data");
	    			return;
	    		}	   		
	    	 }
			
			if(curPri > lowestPri){
				lowestPri = curPri;
			}
			//当优先级队列多时，需要再考虑。
			if((MsgQueueMgr.PRIMSG & lowestPri) == 0){
				lowestPri = MsgQueueMgr.LOWESTPRI;
			}
			session.setAttribute("lowestPriority", lowestPri);
		}
		
		try {
			//此处应该是将信息入三级队列,send message
			if(session != null)
			    session.write(forwardMsg.getMsg());
		} catch (Exception e) {
			System.out.println("消息分发出现问题");
			// TODO: handle exception
		}
		//发送数据加1
		    int count;
		    if(session != null){
			    count = (Integer) session.getAttribute("count");
			    ++count;
			    session.setAttribute("count", count);
		    }
		    
		}

	}
	
	protected boolean isState(int state, int targetState){
		return ((state & targetState) != 0);
	}

	public boolean isIn(String ip){
			if(Start.forwardIP.contains(ip)){
				return true;
			}
		return false;
	}
}
