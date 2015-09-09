package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.ArrayList;

public class LSDB implements Serializable{
	/**
	 * initialize the new neighbor
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<LSA> lsdb;
	
	public LSDB() {
		lsdb = new ArrayList<LSA>();
	}
}