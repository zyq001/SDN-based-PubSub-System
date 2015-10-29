package org.apache.servicemix.wsn.router.mgr;

import org.apache.servicemix.wsn.router.mgr.base.AState;
import org.apache.servicemix.wsn.router.msg.tcp.MsgAdReboot_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgAdminChange;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Date;

public class MsgNotis implements Serializable {

	private static final long serialVersionUID = 1L;

	public String sender;//ת���ߵ���Ϣ

	public String originatorGroup;//�ṩ֪ͨ��broker���ڼ�Ⱥ����

	public String originatorAddr;//�ṩ֪ͨ��broker��IP��ַ

	public String topicName;//֪ͨ����

	public String doc;//֪ͨ����

	public Date sendDate;//��Ϣ������ʱ��


	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
	                          ObjectOutputStream oos, Socket s, MsgNotis mn) {
		AState state = RtMgr.getInstance().getState();
		if (mn.originatorAddr.equals(state.getAdminAddr())) {
			return;
		}
		System.out.println("Administrator has changed");
		MsgAdReboot_ m = new MsgAdReboot_();
		m.c.addAll(state.getGroupMap().values());
		m.self.addr = state.getLocalAddr();
		m.self.name = state.getGroupName();
		m.self.tPort = state.gettPort();
		m.self.uPort = state.getuPort();
		try {
			oos.writeObject(m);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MsgAdminChange mac = new MsgAdminChange();
		mac.NewAdminAddr = mn.originatorAddr;
		state.spreadInLocalGroup(mac);
		state.sendObjectToNeighbors(mac);
	}

}
