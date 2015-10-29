package org.apache.servicemix.wsn.router.msg.tcp;

import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class MsgAdminChange implements Serializable {

	/**
	 * ����Ա�ĵ�ַ�����ı�
	 */
	private static final long serialVersionUID = 1L;
	public String NewAdminAddr;

	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
	                          ObjectOutputStream oos, Socket s, MsgAdminChange mac) {
		AState state = RtMgr.getInstance().getState();
		if (state.getAdminAddr().equals(mac.NewAdminAddr)) {
			return;
		}
		state.setAdminAddr(mac.NewAdminAddr);
		System.out.println("AdminAddress change to:" + state.getAdminAddr());

		state.sendObjectToNeighbors(mac);
		state.spreadInLocalGroup(mac);
	}
}
