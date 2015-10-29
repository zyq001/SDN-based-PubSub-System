package org.apache.servicemix.wsn.router.msg.tcp;

import org.apache.servicemix.wsn.router.mgr.BrokerUnit;
import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;
import org.apache.servicemix.wsn.router.msg.udp.MsgNewBroker;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Date;
import java.util.TreeSet;

public class MsgJoinGroup implements Serializable {

	//�´�������ʱ����Ⱥ������

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String name;//group name

	public int tPort;//sender's TCP port

	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
	                          ObjectOutputStream oos, Socket s, MsgJoinGroup mjg) {
		// �д���������뵽����Ⱥ��
		AState state = RtMgr.getInstance().getState();
		System.out.println("join group message: "
				+ s.getInetAddress().getHostAddress());

		if (mjg.name.equals(state.getGroupName())) {

			BrokerUnit b = new BrokerUnit();
			b.addr = s.getInetAddress().getHostAddress();
			b.id = new Date().getTime();// use this value as id
			b.tPort = mjg.tPort;

			// first multicast in this group about this new broker
			MsgNewBroker mnm = new MsgNewBroker();
			mnm.name = state.getGroupName();
			mnm.broker = b;

			state.spreadInLocalGroup(mnm);

			// response this join request
			MsgJoinGroup_ mjg_ = new MsgJoinGroup_();
			mjg_.id = b.id;
			// broker table
			// mjg_.brokerTab.putAll(brokerTable);
			for (String key : state.getBrokerTable().keySet()) {
				TreeSet<String> ts = new TreeSet<String>();
				for (String addr : state.getBrokerTable().get(key)) {
					ts.add(addr);
				}
				mjg_.brokerTab.put(key, ts);
			}
			for (String t : state.getClientTable()) {
				if (mjg_.brokerTab.keySet().contains(t)) {
					mjg_.brokerTab.get(t).add(state.getLocalAddr());
					// brokerTable.get(t).remove(localAddr);
				} else {
					TreeSet<String> set = new TreeSet<String>();
					set.add(state.getLocalAddr());
					mjg_.brokerTab.put(t, set);
				}
			}
			// lsdb
			mjg_.lsdb.putAll(state.getLsdb());
			mjg_.pdb.clearAll = true;
			mjg_.pdb.time = state.getPolicyTime();
			mjg_.pdb.pdb.addAll(ShorenUtils.getAllPolicy());
			// fellows table
			mjg_.fellows.putAll(state.getFellows());
			mjg_.fellows.put(state.getRep().addr, state.getRep());
			//neighbors
			mjg_.neighbors.addAll(state.getNeighbors());
			// group map
			mjg_.groupMap.putAll(state.getGroupMap());

			try {
				oos.writeObject(mjg_);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// send heart to new broker
			RtMgr.getInstance().addTarget(b.addr);

			// ���ô�����ӽ���
			state.getFellows().put(b.addr, b);
		}
	}

}
