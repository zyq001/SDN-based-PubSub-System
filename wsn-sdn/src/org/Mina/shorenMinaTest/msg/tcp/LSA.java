package org.Mina.shorenMinaTest.msg.tcp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.handlers.Start;
import org.Mina.shorenMinaTest.mgr.base.SysInfo;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.TCPForwardMsg;
import org.Mina.shorenMinaTest.router.searchRoute;
import org.apache.mina.core.session.IoSession;

public class LSA extends WsnMsg implements Serializable{
	public int seqNum; // 序列号
	public int syn; // 0为普通LSA，1为同步LSA
	public String originator; // 发送源名称
	public ArrayList<String> lostGroup; // 丢失集群，若无丢失则为空
	public ArrayList<String> subsTopics; // 发送源的订阅
	public ArrayList<String> cancelTopics; //发送源取消的订阅
	public  ConcurrentHashMap<String, DistBtnNebr> distBtnNebrs; // 发送源与邻居的距离
	public long sendTime; //发送时间
	
	public void initLSA() {
		this.lostGroup = new ArrayList<String>();
		this.subsTopics = new ArrayList<String>();
		this.cancelTopics = new ArrayList<String>();
		this.distBtnNebrs = new ConcurrentHashMap<String, DistBtnNebr>();
	}
	
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
	
}
