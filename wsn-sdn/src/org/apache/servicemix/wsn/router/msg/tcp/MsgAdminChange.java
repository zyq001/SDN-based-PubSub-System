package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

public class MsgAdminChange implements Serializable {

	/**
	 * 管理员的地址发生改变
	 */
	private static final long serialVersionUID = 1L;
	public String NewAdminAddr;
	
	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, MsgAdminChange mac) {
		AState state = RtMgr.getInstance().getState();
		if(state.getAdminAddr().equals(mac.NewAdminAddr)) {
			return;
		}
		state.setAdminAddr(mac.NewAdminAddr);
		System.out.println("AdminAddress change to:" + state.getAdminAddr());

		state.sendObjectToNeighbors(mac);
		state.spreadInLocalGroup(mac);
	}
}
