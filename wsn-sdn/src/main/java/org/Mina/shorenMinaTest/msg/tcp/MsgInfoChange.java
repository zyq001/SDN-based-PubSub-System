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
public class MsgInfoChange extends WsnMsg implements Serializable {

	public String originator;

	public String sender;

	public String addr;

	public int port;//tcp

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
	
	/*
	@SuppressWarnings("static-access")
	@Override
	public void processRegMsg(IoSession session){
		AState state = RtMgr.getInstance().getState();
		if (state.rep.addr.equals(this.originator)) {
			//inside
			state.rep.addr = this.addr;
			state.rep.tPort = this.port;

		} else if (state.neighbors.containsKey(this.originator)) {
			//inside
			state.neighbors.get(this.originator).addr = this.addr;
			state.neighbors.get(this.originator).tPort = this.port;

		} else if (state.groupMap.contains(this.originator)) {
			//outside
			state.groupMap.get(this.originator).rep.addr = this.addr;
			state.groupMap.get(this.originator).rep.tPort = this.port;

		}
	}
	
	
	@SuppressWarnings("static-access")
	@Override
	public void processRepMsg(IoSession session){
		
		AState state = RtMgr.getInstance().getState();
		System.out.println("some group info change");
		log.info("some group info change");

		if (state.neighbors.keySet().contains(originator)) {
			//inside the group
			state.neighbors.get(originator).addr = addr;
			state.neighbors.get(originator).tPort = port;

		} else if (state.groupMap.containsKey(originator)) {
			state.groupMap.get(originator).rep.addr = addr;
			state.groupMap.get(originator).rep.tPort = port;

			//ת����������Ⱥ
			String sender = this.sender;
			this.sender = state.groupName;

			ArrayList<String> li = new ArrayList<String>(state.children);
			li.add(state.parent);
			for (String n : li)
				if (!n.equals(sender) && state.groupMap.containsKey(n)) {
					ForwardMsg forwardMsg = new TCPForwardMsg(state.groupMap.get(n).rep.addr, state.groupMap.get(n).rep.tPort, this);
					MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
				}

			//��Ⱥ��ת��
			ArrayList<BrokerUnit> b = new ArrayList<BrokerUnit>(state.neighbors.values());
			for (BrokerUnit bu : b) {
				ForwardMsg forwardMsg = new TCPForwardMsg(bu.addr, bu.tPort, this);
				MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
			}
		}

	}
	*/
}
