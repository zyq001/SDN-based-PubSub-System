package org.Mina.shorenMinaTest.msg.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
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
public class highPriority extends WsnMsg implements Serializable {

	public String sender;//转发者的信息
	
	public String originatorGroup;//提供通知的broker所在集群名字
	
	public String originatorAddr;//提供通知的broker的IP地址
	
	public String topicName;//通知主题
	
	public String doc;//通知内容
	
	public String sendDate;//消息产生的时间
	
	

//
//	public int Ccount;//计数信息
	
/*	private ArrayList<String> getForwardIp(){
		return Start.forwardIP=searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);
	}*/
	
	private ArrayList<String> getForwardIp(){
		
		ArrayList<String> ret =  org.apache.servicemix.wsn.router.mgr.RtMgr.calForwardGroups(this.topicName,
				this.originatorGroup);
		Iterator<String> it = ret.iterator();
		ArrayList<String> forwardIP = new ArrayList<String>();
		while (it.hasNext()) {
			String itNext = it.next();
			//System.out.println("@@@@@@@@@@@@@@@@@@@@@:"+org.apache.servicemix.wsn.router.mgr.base.SysInfo.groupMap.get(itNext).addr);
			String addr = org.apache.servicemix.wsn.router.mgr.base.SysInfo.groupMap.get(itNext).addr;
			forwardIP.add(addr);
		}

		return forwardIP;//Start.forwardIP=searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);
	}
	
    public void processRegMsg(IoSession session){	
		ArrayList<String> forwardIp = getForwardIp();
		//策略库的位置，由策略库来过滤ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, 30008, this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
		
		System.out.println("下一条地址是:"+forwardIp);
	}
	
	public void processRepMsg(IoSession session){
		ArrayList<String> forwardIp = getForwardIp();
		System.out.println("下一条地址是22222222222:"+forwardIp);
		//策略库的位置，由策略库来过滤ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, 30008, this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
		
		
	}
	
}
