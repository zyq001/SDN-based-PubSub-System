package org.apache.servicemix.wsn.router.msg.udp;

import org.apache.servicemix.wsn.router.mgr.BrokerUnit;

import java.io.Serializable;

public class MsgNewBroker implements Serializable {

	private static final long serialVersionUID = 1L;

	public String name;//group name

	public BrokerUnit broker;

}
