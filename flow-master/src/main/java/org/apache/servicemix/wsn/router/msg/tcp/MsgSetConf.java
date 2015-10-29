package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

public class MsgSetConf implements Serializable {

	private static final long serialVersionUID = 1L;

	public String address;//specify the broker, all if null

	public MsgConf_ conf_;

}
