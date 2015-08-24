/**
 * @author shoren
 * @date 2013-6-5
 */
package org.Mina.shorenMinaTest.handlers;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.servicemix.wsn.router.msg.tcp.DistBtnNebr;
import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.mgr.RtMgr;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.msg.tcp.BrokerUnit;
import org.Mina.shorenMinaTest.msg.tcp.MsgNotis;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
/**
 *
 */

public class SocketAcceptorHandler extends IoHandlerAdapter {
	

	  protected static  Logger logger  =  Logger.getLogger(SocketAcceptorHandler.class );
	  
	    // 当一个新客户端连接后触发此方法.
	    public void sessionCreated(IoSession session) {
	    	System.out.println("new client accept...");
			 MinaUtil.iniSessionReferance(session); 
			 System.out.println("session" + session.toString() +  
		       		 "###" + "create time:" + System.currentTimeMillis());
	    }
	 
	    // 当一个客端端连结进入时 
	    @Override
	    public void sessionOpened(IoSession session) throws Exception {
			System.out.println("session" + session.toString() +  
		       		 "###" + "open time:" + System.currentTimeMillis());
	    }
	 
	    // 当客户端发送的消息到达时:
	    @Override
	    public void messageReceived(IoSession session, Object message) throws Exception{
	    	if(message instanceof WsnMsg){
	    		WsnMsg msg = (WsnMsg)message;
	    		RtMgr.getInstance().getState().processMsg(session, msg);
					    	}
	    	
	    }
	 
	    // 当信息已经传送给客户端后触发此方法.
	    @Override
	    public void messageSent(IoSession session, Object message) {
	        System.out.println("信息已经传送给客户端");
	 
	    }
	 
	    // 当一个客户端关闭时
	    @Override
	    public void sessionClosed(IoSession session) {
	        System.out.println("one Clinet Disconnect !");
	    }
	 
	    // 当连接空闲时触发此方法.
	    @Override
	    public void sessionIdle(IoSession session, IdleStatus status) {

	    }
	 
	    // 当接口中其他方法抛出异常未被捕获时触发此方法
	    @Override
	    public void exceptionCaught(IoSession session, Throwable cause) {
	    	System.out.println("其他方法抛出异常+TCP" + session.toString() + cause.toString());
	    	cause.printStackTrace();
	    }
}
