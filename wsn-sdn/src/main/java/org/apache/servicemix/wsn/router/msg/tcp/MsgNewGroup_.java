package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.HashMap;

public class MsgNewGroup_ implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public boolean isOK;

	public String description;

	public HashMap<String, GroupUnit> groups;

	public MsgNewGroup_() {
		groups = new HashMap<String, GroupUnit>();
	}
}
