package org.apache.servicemix.wsn.router.msg.tcp;

import org.apache.servicemix.wsn.router.mgr.BrokerUnit;

import java.io.Serializable;
import java.util.ArrayList;

public class MsgLookupGroupMember_ implements Serializable {

	private static final long serialVersionUID = 1L;

	public ArrayList<BrokerUnit> members;

	public MsgLookupGroupMember_() {
		members = new ArrayList<BrokerUnit>();
	}

}
