package org.apache.servicemix.wsn.router.msg.tcp;

import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class MsgLookupMemberSubscriptions implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String name;

	public String addr;

	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
	                          ObjectOutputStream oos, Socket s, MsgLookupMemberSubscriptions mlms) {
		AState state = RtMgr.getInstance().getState();
		System.out.println("look up member Subscriptions");
		if (mlms.name.equals(state.getGroupName()) && mlms.addr.equals(state.getLocalAddr())) {
			MsgLookupMemberSubscriptions_ mlms_ = new MsgLookupMemberSubscriptions_();

			for (String t : state.getClientTable()) {
				mlms_.topics.add(t);
			}// for

			try {
				oos.writeObject(mlms_);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}// if
	}

}
