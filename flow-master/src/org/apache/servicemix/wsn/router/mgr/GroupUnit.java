package org.apache.servicemix.wsn.router.mgr;

import java.io.Serializable;
import java.util.Date;

public class GroupUnit implements Serializable {

	public String name;//group的名字
	
	public int uPort;//udp socket的端口号
	
	public Date date;//加入时间
	
	public BrokerUnit rep;//集群代表
	
	public GroupUnit() {
		rep = new BrokerUnit();
	}
	
}
