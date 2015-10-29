package org.apache.servicemix.wsn.router.msg.tcp;

import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;
import org.apache.servicemix.wsn.router.msg.udp.MsgLost;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

public class MsgGroupLost implements Serializable {

	/**
	 * ���м�Ⱥ���ھӶ�ʧʱ�㲥����Ϣ
	 */
	private static final long serialVersionUID = 1L;

	public String name;//lost group name

	public String sender;

	public boolean needRoot;//return root address or not

	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
	                          ObjectOutputStream oos, Socket s, MsgGroupLost mgl) {
		AState state = RtMgr.getInstance().getState();
		System.out.println("group lost message: "
				+ s.getInetAddress().getHostAddress());

		if (!state.getGroupMap().containsKey(mgl.name)) {
			return;
		}

		// ת�����ھӼ�Ⱥ�����˷��ʹ���Ϣ�ļ�Ⱥ
		mgl.sender = state.getGroupName();

		state.sendObjectToNeighbors(mgl);

		// �ڱ���Ⱥ��ת��
		MsgLost ml = new MsgLost();
		ml.indicator = mgl.name;
		ml.inside = false;

		state.spreadInLocalGroup(ml);

		// ɾ���˼�Ⱥ����Ϣ
		if (state.getLsdb().containsKey(ml.indicator)) {
			state.getLsdb().remove(ml.indicator);
		}
		ArrayList<String> topics = RtMgr.getInstance().DeleteAllSubsOfGroup(ml.indicator);
		for (String topic : topics) {
			RtMgr.getInstance().route(topic);
		}
		state.getGroupMap().remove(mgl.name);
	}
}
