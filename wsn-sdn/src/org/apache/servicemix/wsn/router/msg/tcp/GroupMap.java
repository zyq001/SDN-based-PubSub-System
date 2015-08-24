package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupMap implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<GroupUnit> gu;
	public GroupMap() {
		gu = new ArrayList<GroupUnit>();
	}
}