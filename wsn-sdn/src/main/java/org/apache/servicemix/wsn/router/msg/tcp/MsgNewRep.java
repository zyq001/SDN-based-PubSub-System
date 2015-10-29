package org.apache.servicemix.wsn.router.msg.tcp;

import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

public class MsgNewRep implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String sender;

	public String name;// 集群名字

	public String netmask;

	public int uPort;

	// below is representative;'s information
	public long id;

	public String addr;

	public int tPort;

	@SuppressWarnings("static-access")
	public boolean processRepMsg(ObjectInputStream ois, ObjectOutputStream oos,
	                             Socket s, MsgNewRep mnr) {
		AState state = RtMgr.getInstance().getState();

		if (state.getGroupMap().containsKey(mnr.name)
				&& state.getGroupMap().get(mnr.name).addr.equals(mnr.addr)) {
			try {
				oos.writeObject(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		System.out.println("group: " + mnr.name + " new rep " + mnr.addr);


		if (state.getGroupMap().containsKey(mnr.name)) {
			// find the group and update its information
			state.getGroupMap().get(mnr.name).uPort = mnr.uPort;
			state.getGroupMap().get(mnr.name).addr = mnr.addr;
			state.getGroupMap().get(mnr.name).tPort = mnr.tPort;
			state.getGroupMap().get(mnr.name).id = mnr.id;
			state.getGroupMap().get(mnr.name).netmask = mnr.netmask;
		} else {
			GroupUnit gu = new GroupUnit();
			gu.addr = mnr.addr;
			gu.uPort = mnr.uPort;
			gu.tPort = mnr.tPort;
			gu.id = mnr.id;
			gu.netmask = mnr.netmask;
			gu.name = mnr.name;
			state.getGroupMap().put(gu.name, gu);
		}

		Object obj = null;
		boolean wait = false;
		if (mnr.sender.equals(mnr.name)) {

			if (state.getNeighbors().contains(mnr.name)) {
				if (state.getWaitHello().contains(mnr.name)) {
					state.getWaitHello().remove(mnr.name);
					RtMgr.getInstance().addTarget(mnr.name);
				}
				if (state.getLsdb().containsKey(mnr.name)) {
					for (String gu : state.getLsdb().get(mnr.name).distBtnNebrs
							.keySet()) {
						if (gu.equals(state.getGroupName())) {
							LSDB lsdb = new LSDB();
							for (LSA l : state.getLsdb().values()) {
								lsdb.lsdb.add(l);
							}
							obj = lsdb;
							wait = true;
						}
						break;
					}
				}
			}
		}

		try {
			oos.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mnr.sender = state.getGroupName();
		state.spreadInLocalGroup(mnr);

		Socket s1 = null;
		ObjectOutputStream oos1 = null;
		ObjectInputStream ois1 = null;

		ArrayList<String> li = new ArrayList<String>(state.getNeighbors());
		if (li.isEmpty())
			return wait;
		for (String n : li)
			if (!n.equals(mnr.name) && !n.equals(mnr.sender)
					&& state.getGroupMap().containsKey(n)
					&& !state.getWaitHello().contains(n)) {
				try {
					s1 = new Socket(state.getGroupMap().get(n).addr, state
							.getGroupMap().get(n).tPort);
					oos1 = new ObjectOutputStream(s1.getOutputStream());
					ois1 = new ObjectInputStream(s1.getInputStream());
					oos1.writeObject(mnr);
					ois1.readObject();

					ois1.close();
					oos1.close();
					s1.close();
				} catch (IOException e) {
					System.out.println("TCP message cannot reach " + n);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		return wait;
	}
}
