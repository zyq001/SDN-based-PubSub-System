package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.apache.servicemix.wsn.router.mgr.BrokerUnit;
import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

public class MsgLookupGroupMember implements Serializable {

	/**
	 * 请求查阅某一集群的信息
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	
	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, MsgLookupGroupMember mlgm) {
		AState state = RtMgr.getInstance().getState();
		System.out.println("look up group memeber");

		if (mlgm.name.equals(state.getGroupName())) {
			MsgLookupGroupMember_ mlgm_ = new MsgLookupGroupMember_();
			BrokerUnit tmp = new BrokerUnit();
			tmp.addr = state.getLocalAddr();
			tmp.tPort = state.gettPort();
			tmp.id = state.getId();
			mlgm_.members.add(tmp);
			mlgm_.members.addAll(state.getFellows().values());

			try {
				oos.writeObject(mlgm_);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
