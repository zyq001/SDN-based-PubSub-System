package org.apache.servicemix.wsn.router.mgr;

import java.io.Serializable;

public class BrokerUnit implements Serializable {

	public String addr;//代理的地址

	public long id;//由代表分配

	public int tPort;//tcp连接端口号

}
