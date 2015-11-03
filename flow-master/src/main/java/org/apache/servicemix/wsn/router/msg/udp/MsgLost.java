package org.apache.servicemix.wsn.router.msg.udp;

import java.io.Serializable;

public class MsgLost implements Serializable {

	private static final long serialVersionUID = 1L;

	public String indicator;//lost broker's address

	public boolean inside;

}
