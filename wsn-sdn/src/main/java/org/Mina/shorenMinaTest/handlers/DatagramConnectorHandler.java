/**
 * @author shoren
 * @date 2013-4-23
 */
package org.Mina.shorenMinaTest.handlers;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.mgr.RtMgr;
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
public class DatagramConnectorHandler extends IoHandlerAdapter {

	protected static Logger logger = Logger.getLogger(SocketAcceptorHandler.class);
	private static String UdpExceptionSessionIP = null;

	public static String getUdpExceptionSession() {
		if (UdpExceptionSessionIP != null)
			return UdpExceptionSessionIP;
		else
			return null;
	}

	// �����Ϸ������󴥷��˷���.
	public void sessionCreated(IoSession session) {

		MinaUtil.iniSessionReferance(session);

	}

	// ���������ʱ
	@Override
	public void sessionOpened(IoSession session) throws Exception {

	}

	// ���յ���Ϣʱ:
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message instanceof WsnMsg) {
			WsnMsg msg = (WsnMsg) message;
			RtMgr.getInstance().getState().processMsg(session, msg);
		} else
			System.out.println("receive UDPmessage:" + message.toString());
	}

	// ����Ϣ�Ѿ����͸��������󴥷��˷���.  || ÿ����һ����Ϣ�����
	@Override
	public void messageSent(IoSession session, Object message) {

		int length = 0;
		WriteRequestQueue rqueue = session.getWriteRequestQueue(); ///д�������
		length = rqueue.size();
		session.setAttribute("qLength", length);

		if (length <= MinaUtil.getSingleminth()) {
			// System.out.println("*********healthy***********");
			String last_state = (String) session.getAttribute("state");

			if (!((String) session.getAttribute("state")).equals(MinaUtil.SHealthy)) {
				MinaUtil.deUDPBlockCount();
			}
			session.setAttribute("state", MinaUtil.SHealthy);
			session.setAttribute("last_state", last_state);

			session.setAttribute("lossCount", 0);

		} else if (length > MinaUtil.getSingleminth() && length < MinaUtil.getSinglemaxth()) {
			String last_state = (String) session.getAttribute("state");
			if (((String) session.getAttribute("state")).equals(MinaUtil.SHealthy)) {
				MinaUtil.inUDPBlockCount();
			}
			session.setAttribute("state", MinaUtil.SSick);
			session.setAttribute("last_state", last_state);

		} else if (length >= MinaUtil.getSinglemaxth()) {
			//����UDP������ȫ��״̬���ж��Ƿ�ر��߳�
			int state = MinaUtil.getState();
			//136 = 8 + 128
			if ((state & 136) != 0) {
				//��������,kill the session
				session.setAttribute("state", MinaUtil.SDead);
				MinaUtil.deUDPBlockCount();
				session.getWriteRequestQueue().clear(session);
			}
		}
	}

	// �ر�ʱ
	@Override
	public void sessionClosed(IoSession session) {

		//Դ��
		MinaUtil.deTCPTotalCount();
		//�ӱ����ͨ����ɾ��
		MsgQueueMgr.getDest_session().remove(session.getAttribute("addr"));
		System.out.println("TCPsessionClosed:" + session.toString());

		ConcurrentHashMap<String, IoSession> map = MsgQueueMgr.getDest_session();
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			IoSession se = map.get(key);
		}
		//�ر�����
		session.getService().dispose();

	}

	// �����ӿ���ʱ�����˷���.
	//���������IoSession ��ͨ���������״̬ʱ���ã�����UDP Э����˵���������ʼ�ղ��ᱻ���á�
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {

		session.close(true);  //close right now���ر�ͨ��
	}

	// ���ӿ������������׳��쳣δ������ʱ�����˷���
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		System.out.println("���������׳��쳣" + session.toString() + cause.toString());
		System.out.println("UDP Connector ��������");

		session.close(true);  //close right now���ر�ͨ��
	}
}
