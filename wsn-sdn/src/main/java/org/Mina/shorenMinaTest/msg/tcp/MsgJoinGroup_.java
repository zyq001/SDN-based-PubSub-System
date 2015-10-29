package org.Mina.shorenMinaTest.msg.tcp;

import org.Mina.shorenMinaTest.handlers.Start;
import org.Mina.shorenMinaTest.mgr.base.SysInfo;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.TCPForwardMsg;
import org.Mina.shorenMinaTest.router.searchRoute;
import org.apache.mina.core.session.IoSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class MsgJoinGroup_ extends WsnMsg implements Serializable {

	public long id;

	public HashMap<String, TreeSet<String>> brokerTab;

	public HashMap<String, LSA> lsdb;

	public HashMap<String, BrokerUnit> neighbors;

	public HashMap<String, GroupUnit> groupMap;

	public void initMsgJoinGroup_() {

		this.brokerTab = new HashMap<String, TreeSet<String>>();
		this.lsdb = new HashMap<String, LSA>();
		this.neighbors = new HashMap<String, BrokerUnit>();
		this.groupMap = new HashMap<String, GroupUnit>();

	}

	private ArrayList<String> getForwardIp() {
		return Start.forwardIP = searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);
	}

	public void processRegMsg(IoSession session) {
		ArrayList<String> forwardIp = getForwardIp();
		//���Կ��λ�ã��ɲ��Կ�������ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);

	}

	public void processRepMsg(IoSession session) {
		ArrayList<String> forwardIp = getForwardIp();
		//���Կ��λ�ã��ɲ��Կ�������ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
	}

}
