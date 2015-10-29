package org.Mina.shorenMinaTest.msg.udp;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.handlers.Start;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.UDPForwardMsg;
import org.Mina.shorenMinaTest.router.searchRoute;
import org.apache.mina.core.session.IoSession;

import java.io.Serializable;
import java.util.ArrayList;

public class MsgSubs extends WsnMsg implements Serializable {

	public String sender;//ת���ߵ���Ϣ

	public String originator;//˭�Ķ�����Ϣ

	public int type;//0 for subscribe, 1 for cancel

	public ArrayList<String> topics = new ArrayList<String>();

	public void initMsgSubs() {
		this.topics = new ArrayList<String>();
	}

	private ArrayList<String> getForwardIp() {
		return Start.forwardIP = searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);
	}

	public void processRegMsg(IoSession session) {
		ArrayList<String> forwardIp = getForwardIp();
		//���Կ��λ�ã��ɲ��Կ�������ip
		ForwardMsg forwardMsg = new UDPForwardMsg(forwardIp, MinaUtil.uPort, this);
		MsgQueueMgr.addUDPMsgInQueue(forwardMsg);
	}

	public void processRepMsg(IoSession session) {
		ArrayList<String> forwardIp = getForwardIp();
		//���Կ��λ�ã��ɲ��Կ�������ip
		ForwardMsg forwardMsg = new UDPForwardMsg(forwardIp, MinaUtil.uPort, this);
		MsgQueueMgr.addUDPMsgInQueue(forwardMsg);
	}
}
