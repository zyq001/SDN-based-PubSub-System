package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;

public class PolicyDB implements Serializable {

	/**
	 * 策略信息库
	 */
	private static final long serialVersionUID = 1L;
	public long time;
	public boolean clearAll; //是否为全库更新
	public ArrayList<WsnPolicyMsg> pdb;
	
	public PolicyDB () {
		pdb = new ArrayList<WsnPolicyMsg>();
	}
	
	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, PolicyDB p) {
		System.out.println("receive policys");
		AState state = RtMgr.getInstance().getState();
		if(p.time <= state.getPolicyTime()) {
			return;
		}
		state.setPolicyTime(p.time);
		if(p.clearAll) {
			ShorenUtils.deleteAllPolicyMsg();
		}
		for(WsnPolicyMsg msg : p.pdb) {
			ShorenUtils.encodePolicyMsg(msg);
		}
		// 计算修改节点路由
		RtMgr rm = RtMgr.getInstance();
		if(p.clearAll) {	
			rm.CalAllTopicRoute();
		} else {
			for(WsnPolicyMsg msg : p.pdb) {
				rm.CalPolicyChildrenRoute(msg.getTargetTopic());
			}
		}
		state.sendObjectToNeighbors(p);
		state.spreadInLocalGroup(p);
	}
	
	@SuppressWarnings("static-access")
	public void processRegMsg(PolicyDB p) {
		AState state = RtMgr.getInstance().getState();
		if(p.time <= state.getPolicyTime()) {
			return;
		}
		state.setPolicyTime(p.time);
		if(p.clearAll) {
			ShorenUtils.deleteAllPolicyMsg();
		}
		for(WsnPolicyMsg msg : p.pdb) {
			ShorenUtils.encodePolicyMsg(msg);
		}
	}
}