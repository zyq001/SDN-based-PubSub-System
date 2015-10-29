package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

public class MsgSynTopoInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public String originator;//（主/备）管理员地址(同步消息的来源)

	public GroupUnit syn_root_group;//根节点集群


}
