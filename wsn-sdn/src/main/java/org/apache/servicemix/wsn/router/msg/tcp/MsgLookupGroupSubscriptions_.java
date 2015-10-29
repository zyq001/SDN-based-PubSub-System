package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.TreeSet;

public class MsgLookupGroupSubscriptions_ implements Serializable {

	private static final long serialVersionUID = 1L;

	public TreeSet<String> topics;

	public MsgLookupGroupSubscriptions_() {
		topics = new TreeSet<String>();
	}

}
