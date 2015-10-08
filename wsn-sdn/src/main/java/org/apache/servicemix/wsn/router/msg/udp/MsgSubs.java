package org.apache.servicemix.wsn.router.msg.udp;

import java.io.Serializable;
import java.util.ArrayList;

public class MsgSubs implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String sender;//转发者的信息

	public String groupName;//
	
	public String originator;//谁的订阅信息

	public int type;//0 for subscribe, 1 for cancel

	public ArrayList<String> topics = new ArrayList<String>();
	
	public MsgSubs() {
		topics = new ArrayList<String>();
	}

}
