package org.apache.servicemix.wsn.router.msg.tcp;

import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class MsgAskForLSDB implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public String askMessage;

	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
	                          ObjectOutputStream oos, Socket s, MsgAskForLSDB ask) {
		AState state = RtMgr.getInstance().getState();
		if (ask.askMessage.equals(state.getAskMsg())) {
			LSDB db = new LSDB();
			db.lsdb.addAll(state.getLsdb().values());
			try {
				oos.writeObject(db);
			} catch (IOException e) {
				System.out.println("LSDB cannot write back");
			}
		}
	}
}