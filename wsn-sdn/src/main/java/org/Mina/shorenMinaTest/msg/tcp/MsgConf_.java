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


@SuppressWarnings("serial")
public class MsgConf_ extends WsnMsg implements Serializable {


	//representative's information
	public String repAddr;//代表地址

	public int tPort;//代表的TCP端口号

	//
	public int neighborSize;//子节点数目

	public String multiAddr;//组播地址

	public int uPort;//组播端口号

	public int joinTimes;

	public long synPeriod;

	//below heart detection
	public long lostThreshold;//判定失效的阀值

	public long scanPeriod;//扫描周期

	public long sendPeriod;//发送周期

	private ArrayList<String> getForwardIp() {

		return Start.forwardIP = searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);
	}

	public void processRegMsg(IoSession session) {

		ArrayList<String> forwardIp = getForwardIp();
		//策略库的位置，由策略库来过滤ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);

	}

	public void processRepMsg(IoSession session) {

		ArrayList<String> forwardIp = getForwardIp();
		//策略库的位置，由策略库来过滤ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
	}

}
