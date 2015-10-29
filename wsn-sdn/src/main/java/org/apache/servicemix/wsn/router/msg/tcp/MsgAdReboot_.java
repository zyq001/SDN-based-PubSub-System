package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.LinkedList;

//import org.apache.servicemix.wsn.router.admin.GroupUnit;

public class MsgAdReboot_ implements Serializable {

	private static final long serialVersionUID = 1L;

	public GroupUnit self;

	public LinkedList<org.apache.servicemix.wsn.router.msg.tcp.GroupUnit> c;

	public MsgAdReboot_() {

		self = new GroupUnit("", 0, "");
		c = new LinkedList<org.apache.servicemix.wsn.router.msg.tcp.GroupUnit>();

	}

}
