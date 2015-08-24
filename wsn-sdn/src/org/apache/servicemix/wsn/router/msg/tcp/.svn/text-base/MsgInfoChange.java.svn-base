package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.servicemix.wsn.router.mgr.BrokerUnit;
import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

public class MsgInfoChange implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String originator;
	
	public String sender;
	
	public String addr;
	
	public int port;//tcp
		
	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, MsgInfoChange mic) {
		AState state = RtMgr.getInstance().getState();
		System.out.println("some group info change");

		if (state.getFellows().keySet().contains(mic.originator)) {
			// inside the group
			state.getFellows().get(mic.originator).addr = mic.addr;
			state.getFellows().get(mic.originator).tPort = mic.port;

		} else if (state.getGroupMap().containsKey(mic.originator)) {
			if(state.getGroupMap().get(mic.originator).addr.equals(mic.addr) && state.getGroupMap().get(mic.originator).tPort == mic.port) {
				return;
			}
			state.getGroupMap().get(mic.originator).addr = mic.addr;
			state.getGroupMap().get(mic.originator).tPort = mic.port;

			// 转发给其他集群
			mic.sender = state.getGroupName();
			state.sendObjectToNeighbors(mic);

			// 集群内转发
			ArrayList<BrokerUnit> b = new ArrayList<BrokerUnit>(
					state.getFellows().values());
			for (BrokerUnit bu : b) {
				try {
					Socket s1 = new Socket(bu.addr, bu.tPort);
					ObjectOutputStream oos1 = new ObjectOutputStream(s1.getOutputStream());
					oos1.writeObject(mic);

					oos1.close();
					s1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
