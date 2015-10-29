/**
 * @author shoren
 * @date 2013-6-5
 */
package org.Mina.shorenMinaTest.handlers;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequestQueue;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 */
public class SocketConnectorHandler extends IoHandlerAdapter {

	public static boolean check = true;
	public static String TcpExceptionSessionIP = null;
	protected static Logger logger = Logger.getLogger(SocketConnectorHandler.class);

	public static String getTcpExceptionSession() {
		if (TcpExceptionSessionIP != null)
			return TcpExceptionSessionIP;
		else
			return null;
	}

	// 当连上服务器后触发此方法.
	public void sessionCreated(IoSession session) {
		MinaUtil.iniSessionReferance(session);
	}

	// 当连结进入时
	@Override
	public void sessionOpened(IoSession session) throws Exception {

	}

	// 当收到消息时
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

		if (message instanceof WsnMsg) {
			WsnMsg msg = (WsnMsg) message;
		} else
			System.out.println("TCP receive message:" + message.toString());

	}

	// 当信息已经传送给服务器后触发此方法. || 每发送一个信息后调用
	@Override
	public void messageSent(IoSession session, Object message) {

		int length = 0;
		WriteRequestQueue rqueue = session.getWriteRequestQueue(); ///写请求队列

		length = rqueue.size() / 2;
		session.setAttribute("qLength", length);
		if (length > MinaUtil.maxThree)
			MinaUtil.maxThree = length;

		if (length <= MinaUtil.getSingleminth()) {
			if (!((String) session.getAttribute("state")).equals(MinaUtil.SHealthy)) {
				MinaUtil.deTCPBlockCount();

				String last_state = (String) session.getAttribute("state");
				session.setAttribute("state", MinaUtil.SHealthy);
				session.setAttribute("last_state", last_state);

				session.setAttribute("lossCount", 0);
			}

		} else if (length > MinaUtil.getSingleminth() && length < MinaUtil.getSinglemaxth()) {
			String last_state = (String) session.getAttribute("state");
			if (((String) session.getAttribute("state")).equals(MinaUtil.SHealthy)) {
				MinaUtil.inTCPBlockCount();
			}
			session.setAttribute("state", MinaUtil.SSick);
			session.setAttribute("last_state", last_state);
			//计算丢包率，保存
			int lossCount = MinaUtil.calPacket_loss_rate(session);
			session.setAttribute("lossCount", lossCount);

		} else if (length >= MinaUtil.getSinglemaxth()) {
			//若是UDP，根据全局状态，判断是否关闭线程
			String last_state = (String) session.getAttribute("state");

			if (((String) session.getAttribute("state")).equals(MinaUtil.SHealthy)) {
				MinaUtil.inTCPBlockCount();
			}
			session.setAttribute("state", MinaUtil.SSick);
			session.setAttribute("last_state", last_state);
		}

		// MsgNotis mn = (MsgNotis) message;
		//System.out.println("发送的消息为:   "+mn.doc);
		//  System.out.println("MessageSent!");
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
		while (it.hasNext()) {
			String key = (String) it.next();
			IoSession se = map.get(key);
		}
		//关闭连接
		session.getService().dispose();
	}

	// 当连接空闲时触发此方法.
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		session.close(true);  //close right now，关闭通道
	}

	// 当接口中其他方法抛出异常未被捕获时触发此方法
	public void exceptionCaught(IoSession session, Throwable cause) {
		System.out.println("其他方法抛出异常" + session.toString() + cause.toString());

		session.close(true);  //close right now，关闭通道
		return;
	}
}
