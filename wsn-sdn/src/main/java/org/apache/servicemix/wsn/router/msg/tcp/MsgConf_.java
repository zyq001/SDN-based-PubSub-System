package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

public class MsgConf_ implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//representative's information
	public String repAddr;//代表地址

	public int tPort;//代表的TCP端口号

	//
	public int neighborSize;//子节点数目

	public String multiAddr;//组播地址

	public int uPort;//组播端口号

	public int joinTimes;

	public long synPeriod;

	//below heart detection
	public long lostThreshold;//判定失效的阀值

	public long scanPeriod;//扫描周期

	public long sendPeriod;//发送周期

}
