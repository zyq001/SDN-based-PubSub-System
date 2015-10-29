package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

public class MsgGroupLost implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String name;//lost group name

	public String sender;

	public boolean needRoot;//return root address or not

}
