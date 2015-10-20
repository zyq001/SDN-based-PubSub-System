package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;

public class PolicyDB implements Serializable {

	/**
	 * 策略信息库
	 */
	private static final long serialVersionUID = 1L;
	public long time;
	public boolean clearAll; //是否为全库更新
	public ArrayList<WsnPolicyMsg> pdb;
	public  HashMap<String, GroupUnit> groupMsg;
	
	public PolicyDB () {
		pdb = new ArrayList<WsnPolicyMsg>();
		groupMsg = new HashMap<String, GroupUnit>();
	}
}