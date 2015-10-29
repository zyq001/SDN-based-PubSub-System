package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

public class MsgInfoChange implements Serializable {

	private static final long serialVersionUID = 1L;

	public String originator;

	public String sender;

	public String addr;

	public int port;//tcp

}
