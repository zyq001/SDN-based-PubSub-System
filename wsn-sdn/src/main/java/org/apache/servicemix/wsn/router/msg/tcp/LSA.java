package org.apache.servicemix.wsn.router.msg.tcp;

import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LSA implements Serializable {
	/**
	 * 链路状态消息
	 */
	private static final long serialVersionUID = 1L;
	public int seqNum; // 序列号
	public int syn; // 0为普通LSA，1为同步LSA
	public String originator; // 发送源名称
	public ArrayList<String> lostGroup; // 丢失集群，若无丢失则为空
	public ArrayList<String> subsTopics; // 发送源的订阅
	public ArrayList<String> cancelTopics; //发送源取消的订阅
	public ConcurrentHashMap<String, DistBtnNebr> distBtnNebrs; // 集群及其邻居
	public long sendTime; //发送时间

	public LSA() {
		lostGroup = new ArrayList<String>();
		subsTopics = new ArrayList<String>();
		cancelTopics = new ArrayList<String>();
//		distBtnNebrs = new ConcurrentHashMap<String, DistBtnNebr>();
	}

	public void copyLSA(LSA lsa) {
		this.seqNum = lsa.seqNum;
		this.syn = lsa.syn;
		this.originator = lsa.originator;
		this.lostGroup.addAll(lsa.lostGroup);
		this.subsTopics.addAll(lsa.subsTopics);
		this.cancelTopics.addAll(lsa.cancelTopics);
		this.distBtnNebrs.putAll(lsa.distBtnNebrs);
	}

	public void copyPartLSA(LSA lsa) {
		this.seqNum = lsa.seqNum;
		this.syn = lsa.syn;
		this.originator = lsa.originator;
		this.lostGroup.addAll(lsa.lostGroup);
		this.distBtnNebrs.putAll(lsa.distBtnNebrs);
	}

	public void processRepMsg(ObjectInputStream ois,
	                          ObjectOutputStream oos, Socket s, LSA lsa) {
		AState state = RtMgr.getInstance().getState();
		if (!state.addLSAToLSDB(lsa))
			return;
		// receive lsa from other groups
		System.out.println("lsa from " + lsa.originator + " lsa seqNum: "
				+ lsa.seqNum);

		// 转发到其他集群
		state.sendObjectToNeighbors(lsa);

		// 在本集群中组播
		state.spreadLSAInLocalGroup(lsa);
	}
}
 