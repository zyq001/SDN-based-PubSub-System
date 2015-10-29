/**
 * @author shoren
 * @date 2013-6-5
 */
package org.Mina.shorenMinaTest.handlers;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.mgr.RtMgr;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 *
 */

public class SocketAcceptorHandler extends IoHandlerAdapter {


	protected static Logger logger = Logger.getLogger(SocketAcceptorHandler.class);

	// ��һ���¿ͻ������Ӻ󴥷��˷���.
	public void sessionCreated(IoSession session) {
		System.out.println("new client accept...");
		MinaUtil.iniSessionReferance(session);
		System.out.println("session" + session.toString() +
				"###" + "create time:" + System.currentTimeMillis());
	}

	// ��һ���Ͷ˶��������ʱ
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("session" + session.toString() +
				"###" + "open time:" + System.currentTimeMillis());
	}

	// ���ͻ��˷��͵���Ϣ����ʱ:
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message instanceof WsnMsg) {
			WsnMsg msg = (WsnMsg) message;
			RtMgr.getInstance().getState().processMsg(session, msg);
		}

	}

	// ����Ϣ�Ѿ����͸��ͻ��˺󴥷��˷���.
	@Override
	public void messageSent(IoSession session, Object message) {
		System.out.println("��Ϣ�Ѿ����͸��ͻ���");

	}

	// ��һ���ͻ��˹ر�ʱ
	@Override
	public void sessionClosed(IoSession session) {
		System.out.println("one Clinet Disconnect !");
	}

	// �����ӿ���ʱ�����˷���.
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {

	}

	// ���ӿ������������׳��쳣δ������ʱ�����˷���
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		System.out.println("���������׳��쳣+TCP" + session.toString() + cause.toString());
		cause.printStackTrace();
	}
}
