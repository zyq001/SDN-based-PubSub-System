package org.apache.servicemix.wsn.router.msg.tcp;

import org.apache.servicemix.wsn.router.mgr.BrokerUnit;
import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class MsgSetAddr implements Serializable {

	//�ɹ�����ָ������ʹ�õĵ�ַ 

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String addr;

	public int port;

	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
	                          ObjectOutputStream oos, Socket s, MsgSetAddr msa) {
		System.out.println("set address message");
		AState state = RtMgr.getInstance().getState();

		if (!state.getLocalAddr().equals(msa.addr) || state.gettPort() != msa.port) {
			MsgInfoChange mic = new MsgInfoChange();
			mic.addr = msa.addr;
			mic.port = msa.port;

			ObjectOutputStream oos1 = null;
			Socket s1 = null;

			// notify in this group
			mic.originator = state.getLocalAddr();
			mic.sender = state.getLocalAddr();
			for (BrokerUnit b : state.getFellows().values()) {
				try {
					s1 = new Socket(b.addr, b.tPort);
					oos1 = new ObjectOutputStream(s1.getOutputStream());
					oos1.writeObject(mic);

					oos1.close();
					s1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// notify neighbor groups
			mic.originator = state.getGroupName();
			mic.sender = state.getGroupName();
			state.sendObjectToNeighbors(mic);

			state.setLocalAddr(msa.addr);
			state.settPort(msa.port);
			RtMgr.getInstance().updateUdpSkt();
			RtMgr.getInstance().updateTcpSkt();
		}
	}
}
