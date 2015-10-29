package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

public class MsgNewRep implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String sender;

	public String name;

	public int uPort;

	//below is representative;'s information
	public long id;

	public String addr;

	public int tPort;

}
