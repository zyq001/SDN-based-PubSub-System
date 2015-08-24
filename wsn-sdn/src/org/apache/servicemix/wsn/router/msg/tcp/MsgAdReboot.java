package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

public class MsgAdReboot implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, MsgAdReboot mar) {
		AState state = RtMgr.getInstance().getState();
		MsgAdReboot_ mar_ = new MsgAdReboot_();

		mar_.self.addr = state.getLocalAddr();
		mar_.self.name = state.getGroupName();
		mar_.self.tPort = state.gettPort();

		mar_.c.addAll(state.getGroupMap().values());

		try {
			oos.writeObject(mar_);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
