/**
 * @author shoren
 * @date 2013-4-23
 */
package org.Mina.shorenMinaTest.handlers;

import org.Mina.shorenMinaTest.MinaUtil;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 *
 */
public class DatagramAcceptorHandler extends IoHandlerAdapter {

	protected static Logger logger = Logger.getLogger(SocketAcceptorHandler.class);
	static int counter = 0;

	// 当一个新客户端连接后触发此方法.
	public void sessionCreated(IoSession session) {
		MinaUtil.iniSessionReferance(session);
		System.out.println("recieve UDP MSG");

	}

	// 当一个客端端连结进入时
	@Override
	public void sessionOpened(IoSession session) throws Exception {

		System.out.println("Opened");
	}

	// 当客户端发送的消息到达时:
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println("recieve UDP MSG");
//		if (message instanceof WsnMsg) {
//			WsnMsg msg = (WsnMsg) message;
//			RtMgr.getInstance().getState().processMsg(session, msg);
//		}

		counter++;
//		if (counter % 1000 == 0)
			System.out.println(System.currentTimeMillis() + "counter:" + counter);
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
		System.out.println("其他方法抛出异常" + session.toString() + cause.toString());
	}
}
