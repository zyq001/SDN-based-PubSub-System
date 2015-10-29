package org.apache.servicemix.wsn.router.msg.udp;

import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;

import java.io.Serializable;

public class MsgGroup implements Serializable {

	//���м�Ⱥ���뵽����Ⱥʱ������ת������Ϣ

	private static final long serialVersionUID = 1L;

	public String sender;//sender's group name

	public GroupUnit g = new GroupUnit();

}
