/**
 * @author shoren
 * @date 2013-3-29
 */
package org.apache.servicemix.wsn.router.wsnPolicy.msgs;


/**
 *
 */
public class TargetHost extends TargetMsg {
	private static final long serialVersionUID = 1L;

	protected String hostIp;

	public TargetHost() {
		this(null);
	}

	public TargetHost(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
}
