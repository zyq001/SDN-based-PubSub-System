package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.ArrayList;

public class MsgSynSubs implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String originator;
	
	public ArrayList<String> topics;
	
	public MsgSynSubs() {
		topics = new ArrayList<String>();
	}
	
}
