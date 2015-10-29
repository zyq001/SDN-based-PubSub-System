package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

public class MsgConf_ implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//representative's information
	public String repAddr;//????????

	public int tPort;//??????TCP??????

	//
	public int neighborSize;//??????????

	public String multiAddr;//????????

	public int uPort;//??????????

	public int joinTimes;

	public long synPeriod;

	//below heart detection
	public long lostThreshold;//??????????????

	public long scanPeriod;//????????

	public long sendPeriod;//????????

}
