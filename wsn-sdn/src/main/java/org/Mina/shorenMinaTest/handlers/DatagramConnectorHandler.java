/**
 * @author shoren
 * @date 2013-4-23
 */
package org.Mina.shorenMinaTest.handlers;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequestQueue;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.mgr.RtMgr;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;


/**
 *
 */
public class DatagramConnectorHandler extends IoHandlerAdapter {
	
    protected static Logger logger  =  Logger.getLogger(SocketAcceptorHandler.class);
	private static String UdpExceptionSessionIP = null;  
    
 // 当连上服务器后触发此方法.
    public void sessionCreated(IoSession session) {
    	
    	MinaUtil.iniSessionReferance(session);
    	
    }
 
    // 当连结进入时 
    @Override
    public void sessionOpened(IoSession session) throws Exception {
 
    }
 
    // 当收到消息时:
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception{
		if(message instanceof WsnMsg){
    		WsnMsg msg = (WsnMsg)message;
    		RtMgr.getInstance().getState().processMsg(session, msg);
    	}else
    		System.out.println("receive UDPmessage:" + message.toString());
    }
 
    // 当信息已经传送给服务器后触发此方法.  || 每发送一个信息后调用
    @Override
    public void messageSent(IoSession session, Object message) {

    	int length = 0;  
        WriteRequestQueue rqueue = session.getWriteRequestQueue(); ///写请求队列
        length = rqueue.size();
        session.setAttribute("qLength", length);        
        
        if(length <= MinaUtil.getSingleminth()){
      	 // System.out.println("*********healthy***********");
        	String last_state = (String)session.getAttribute("state");
        	
        	if(!((String)session.getAttribute("state")).equals(MinaUtil.SHealthy)){         		
        		MinaUtil.deUDPBlockCount();
        	}
        	session.setAttribute("state", MinaUtil.SHealthy);
        	session.setAttribute("last_state", last_state);
        	
        	session.setAttribute("lossCount", 0);
        	
        }else if(length > MinaUtil.getSingleminth() && length < MinaUtil.getSinglemaxth()){
       	 String last_state = (String)session.getAttribute("state");         	
         	if(((String)session.getAttribute("state")).equals(MinaUtil.SHealthy)){         		
         			MinaUtil.inUDPBlockCount();
         	}
         	session.setAttribute("state", MinaUtil.SSick);
         	session.setAttribute("last_state", last_state);     	
        	
        }else if(length >= MinaUtil.getSinglemaxth()){  
       	 //若是UDP，根据全局状态，判断是否关闭线程
       	 int state = MinaUtil.getState();
       	 //136 = 8 + 128
       	 if((state & 136) != 0){
       		 //阻塞严重,kill the session
       		session.setAttribute("state", MinaUtil.SDead);
       		MinaUtil.deUDPBlockCount();
       		session.getWriteRequestQueue().clear(session);
       	 }
        }
    }
 
    // 关闭时
    @Override
    public void sessionClosed(IoSession session) {
    	
  		//源码
  		MinaUtil.deTCPTotalCount();
        //从保存的通道中删除
        MsgQueueMgr.getDest_session().remove(session.getAttribute("addr"));
        System.out.println("TCPsessionClosed:" + session.toString());
        
        ConcurrentHashMap<String, IoSession> map = MsgQueueMgr.getDest_session();
        Iterator it = map.keySet().iterator();
        while(it.hasNext()){
        	String key = (String) it.next();
        	IoSession se = map.get(key);
        }
        //关闭连接
        session.getService().dispose();

    }
 
    // 当连接空闲时触发此方法.
    //这个方法在IoSession 的通道进入空闲状态时调用，对于UDP 协议来说，这个方法始终不会被调用。
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {

        session.close(true);  //close right now，关闭通道 	
    }
 
    // 当接口中其他方法抛出异常未被捕获时触发此方法
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
    	System.out.println("其他方法抛出异常"+session.toString()+cause.toString());
    	System.out.println("UDP Connector 出现问题");

        session.close(true);  //close right now，关闭通道
    }
    
    public static String getUdpExceptionSession(){
    	if(UdpExceptionSessionIP != null)
    	    return UdpExceptionSessionIP;
    	else
    		return null;
    }
}
