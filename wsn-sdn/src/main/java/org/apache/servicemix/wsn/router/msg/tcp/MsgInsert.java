package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

public class MsgInsert implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String tagetGroupName;
	
	//my information
	public String name;
	
	public int uPort;
	
	public String addr;
	
	public long id;
	
	public int tPort;
	
	public String netmask;
	
	public boolean needInit; //if need to spread the insert message
	
	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, MsgInsert mi) {
		AState state = RtMgr.getInstance().getState();

		System.out.println("insert message: "
				+ s.getInetAddress().getHostAddress());
		MsgInsert_ mi_ = new MsgInsert_();
		ObjectOutputStream doos = null;

		if (insertOK(mi.name)) {

			GroupUnit gu = new GroupUnit();
			gu.addr = mi.addr;
			gu.date = new Date();
			gu.id = mi.id;
			gu.name = mi.name;
			gu.tPort = mi.tPort;
			gu.uPort = mi.uPort;
			gu.netmask = mi.netmask;

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DatagramSocket ds = null;
			try {
				doos = new ObjectOutputStream(baos);
				ds = new DatagramSocket();
				doos.writeObject(gu);
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] buf = baos.toByteArray();
			// tell brokers about this group
			DatagramPacket p;
			try {
				p = new DatagramPacket(buf, buf.length,
						InetAddress.getByName(state.getMultiAddr()), uPort);
				ds.send(p);
			} catch (IOException e) {
				e.printStackTrace();
			}

			mi_.name = state.getGroupName();
			mi_.uPort = uPort;
			mi_.id = id;
			mi_.isOK = true;

			// add this new group as a neighbor
			if (state.getGroupMap().keySet().contains(mi.name)) {
				// if this group already exists, update its information
				GroupUnit g = state.getGroupMap().get(mi.name);
				g.uPort = mi.uPort;
				g.addr = mi.addr;
				g.id = mi.id;
				g.tPort = mi.tPort;
			} else {
				state.getGroupMap().put(gu.name, gu);
				state.sendObjectToNeighbors(gu);
			}
			state.addNeighbor(mi.name);
			
		} else {
			// set one child for it to insert
			mi_.isOK = false;
		}

		try {
			oos.writeObject(mi_);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("static-access")
	private boolean insertOK(String group) {
		AState state = RtMgr.getInstance().getState();
		// check whether the requesting group can insert as child node
		if (state.getNeighbors().contains(group) || state.getNeighbors().size() < state.getNeighborSize()) {
			return true;
		} else
			return false;
	}
}
