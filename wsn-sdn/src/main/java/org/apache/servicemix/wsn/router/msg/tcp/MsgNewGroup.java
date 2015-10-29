package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;


public class MsgNewGroup implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String name;

	public int tPort;

	public String addr;

	public String controllerAddr;
}
