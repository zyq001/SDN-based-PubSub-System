package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.apache.servicemix.wsn.router.mgr.BrokerUnit;
import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

public class MsgSetConf implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String address;//specify the broker, all if null
	
	public MsgConf_ conf_;
	
	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, MsgSetConf msc) {
		AState state = RtMgr.getInstance().getState();
		System.out.println("set configurations");
		Socket s1 = null;
		ObjectOutputStream oos1 = null;
		ObjectInputStream ois1 = null;

		for (BrokerUnit b : state.getFellows().values()) {
			try {
				s1 = new Socket(b.addr, b.tPort);
				oos1 = new ObjectOutputStream(s1.getOutputStream());
				ois1 = new ObjectInputStream(s1.getInputStream());
				oos1.writeObject(msc);

				ois1.close();
				oos1.close();
				s1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		state.setNeighborSize(msc.conf_.neighborSize);
		state.setJoinTimes(msc.conf_.joinTimes);

		if (state.getThreshold() != msc.conf_.lostThreshold) {
			state.setThreshold(msc.conf_.lostThreshold);
			RtMgr.getInstance().setThreshold(msc.conf_.lostThreshold);
		}
		if (state.getScanPeriod() != msc.conf_.scanPeriod) {
			state.setScanPeriod(msc.conf_.scanPeriod);
		}
		if (state.getSendPeriod() != msc.conf_.sendPeriod) {
			state.setSendPeriod(msc.conf_.sendPeriod);
			RtMgr.getInstance().setSendPeriod(msc.conf_.sendPeriod);
		}

		if (state.getuPort() != msc.conf_.uPort || !state.getMultiAddr().equals(msc.conf_.multiAddr)) {
			state.setuPort(msc.conf_.uPort);
			state.setMultiAddr(msc.conf_.multiAddr);
			RtMgr.getInstance().updateUdpSkt();
		}
		System.out.println("configuration updated");
	}
	
}
