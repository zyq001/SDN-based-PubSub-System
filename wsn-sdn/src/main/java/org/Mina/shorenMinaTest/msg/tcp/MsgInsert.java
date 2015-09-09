package org.Mina.shorenMinaTest.msg.tcp;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.apache.mina.core.session.IoSession;
import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.handlers.Start;
import org.Mina.shorenMinaTest.mgr.RtMgr;
import org.Mina.shorenMinaTest.mgr.base.AState;
import org.Mina.shorenMinaTest.mgr.base.SysInfo;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.TCPForwardMsg;
import org.Mina.shorenMinaTest.queues.UDPForwardMsg;
import org.Mina.shorenMinaTest.router.searchRoute;



@SuppressWarnings("serial")
public class MsgInsert extends WsnMsg implements Serializable {

	public String tagetGroupName;
	
	//my information
	public String name;
	
	public int uPort;
	
	public String addr;
	
	public long id;
	
	public int tPort;
	
	public boolean needInit;//return group map and group subscriptions if true
	
	private ArrayList<String> getForwardIp(){
		return Start.forwardIP=searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);
	}
	
    public void processRegMsg(IoSession session){	
		ArrayList<String> forwardIp = getForwardIp();
		//策略库的位置，由策略库来过滤ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);

	}
	
	public void processRepMsg(IoSession session){
		ArrayList<String> forwardIp = getForwardIp();
		//策略库的位置，由策略库来过滤ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
	}
	
	
	
	/*
	@SuppressWarnings("static-access")
	@Override
	public void processRepMsg(IoSession session){
		
		AState state = RtMgr.getInstance().getState();

		MsgInsert_ mi_ = new MsgInsert_();
		
		if (true) {

			MsgGroup mg = new MsgGroup();
			mg.sender = state.groupName;
			mg.g.name = name;
			mg.g.uPort = uPort;
			mg.g.rep.addr = addr;
			mg.g.rep.id = id;
			mg.g.rep.tPort = tPort;
			mg.g.date = new Date();

			//信息写回
			session.write(mg);

			//tell brokers about this group	
			try {				
				ForwardMsg forwardMsg = new UDPForwardMsg(InetAddress.getByName(state.multiAddr).getHostAddress(),
						uPort, this);
				MsgQueueMgr.addUDPMsgInQueue(forwardMsg);
			} catch (IOException e) {
				e.printStackTrace();
				log.warn(e);
			}

			if (needInit) {

				//tell neighbor groups about this group
				ArrayList<String> li = new ArrayList<String>(state.children);
				li.add(state.parent);
				for (String n : li)
					if (state.groupMap.get(n) != null) {
						try {
							ForwardMsg forwardMsg = new UDPForwardMsg(InetAddress.getByName(state.groupMap
									.get(n).rep.addr).getHostAddress(), state.groupMap.get(n).uPort, this);
							MsgQueueMgr.addUDPMsgInQueue(forwardMsg);
						} catch (IOException e) {
							e.printStackTrace();
							log.warn(e);
						}

					}

				mi_.groupMap.putAll(state.groupMap);
				//add this group's subscriptions information
				for (String t : state.clientTable) {
					TreeSet<String> set = new TreeSet<String>();
					set.add(state.groupName);
					mi_.groupTab.put(t, set);
				}
				for (String t : state.brokerTable.keySet())
					if (!mi_.groupTab.keySet().contains(t)) {
						TreeSet<String> set = new TreeSet<String>();
						set.add(state.groupName);
						mi_.groupTab.put(t, set);
					}
				//add other groups' subscriptions information
				for (String t : state.groupTable.keySet()) {
					if (mi_.groupTab.keySet().contains(t))
						mi_.groupTab.get(t).addAll(state.groupTable.get(t));
					else
						mi_.groupTab.put(t, state.groupTable.get(t));
				}

			}

			mi_.name = state.groupName;
			mi_.uPort = uPort;
			mi_.id = id;
			mi_.isOK = true;

			//add this new group as a child 
			if (state.groupMap.keySet().contains(name)) {
				//if this group already exists, update its information
				state.groupMap.get(name).uPort = uPort;
				state.groupMap.get(name).rep.addr = addr;
				state.groupMap.get(name).rep.id = id;
				state.groupMap.get(name).rep.tPort = tPort;

			} else {
				GroupUnit g = new GroupUnit();
				g.rep.addr = addr;
				g.name = name;
				g.rep.tPort = tPort;
				g.uPort = uPort;
				g.rep.id = id;
				g.date = new Date();

				state.groupMap.put(g.name, g);
			}
			state.children.add(name);



		} else {
			//set one child for it to insert
			mi_.isOK = false;

			mi_.name = state.children.get(state.nextInsertChild);
			mi_.next = state.groupMap.get(state.children.get(state.nextInsertChild)).rep.addr;
			mi_.tPort = state.groupMap.get(state.children.get(state.nextInsertChild)).rep.tPort;
			state.nextInsertChild = (state.nextInsertChild + 1) % state.childrenSize;
		}

		//信息写回
		session.write(mi_);

	}
	
	
	*/
}
