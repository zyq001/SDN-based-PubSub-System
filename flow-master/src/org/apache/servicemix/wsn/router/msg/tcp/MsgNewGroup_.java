package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;

public class MsgNewGroup_ implements Serializable {

	private static final long serialVersionUID = 1L;

	public boolean isOK;
	
	public String description;
	
	public  HashMap<String, GroupUnit> groups;
	
	public PolicyDB policys;
	
	public MsgNewGroup_() {
		groups = new HashMap<String, GroupUnit>();
		policys = new PolicyDB();
	}
}
