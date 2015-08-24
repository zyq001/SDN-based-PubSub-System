package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

public class AskForGroupMap implements Serializable{
	/**
	 * 向集群请求groupMap
	 */
	private static final long serialVersionUID = 1L;
	public String askForGroupMap;
	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, AskForGroupMap af){
		AState state = RtMgr.getInstance().getState();
		System.out.println("asked for groupmap");
		GroupMap gm = new GroupMap();
		for(GroupUnit gu : state.getGroupMap().values()) {
			gm.gu.add(gu);
		}
		try {
			oos.writeObject(gm);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}