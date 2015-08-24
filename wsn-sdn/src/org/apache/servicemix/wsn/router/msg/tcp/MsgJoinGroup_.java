package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.servicemix.wsn.router.mgr.BrokerUnit;

public class MsgJoinGroup_ implements Serializable {

	/**
	 * 代理加入时的返回信息
	 */
	private static final long serialVersionUID = 1L;

	public long id;
	
	public PolicyDB pdb;
	
	public HashMap<String, TreeSet<String>> brokerTab;
	
	public HashMap<String, LSA> lsdb;
	
	public HashMap<String, BrokerUnit> fellows;
	
	public ArrayList<String> neighbors;
	
	public HashMap<String, GroupUnit> groupMap;
	
	public MsgJoinGroup_() {
		
		brokerTab = new HashMap<String, TreeSet<String>>();
		lsdb = new HashMap<String, LSA>();
		fellows = new HashMap<String, BrokerUnit>();
		neighbors = new ArrayList<String>();
		groupMap = new HashMap<String, GroupUnit>();
		pdb = new PolicyDB();
		
	}
	
}
