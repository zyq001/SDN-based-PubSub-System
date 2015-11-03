package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class MsgLookupGroupSubscriptions_ implements Serializable {

	private static final long serialVersionUID = 1L;

	public TreeSet<String> topics;
	public Map<String, ArrayList<String>> topicsASubers;

	public MsgLookupGroupSubscriptions_() {
		topicsASubers = new ConcurrentHashMap<String, ArrayList<String>>();
		topics = new TreeSet<String>();
	}

}
