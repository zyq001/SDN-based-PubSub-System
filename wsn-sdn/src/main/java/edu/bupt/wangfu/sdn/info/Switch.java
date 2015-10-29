package edu.bupt.wangfu.sdn.info;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-7-14.
 */
public class Switch extends DevInfo {


	private String DPID;
	private String ipAddr;

	private double load;
	private Map<Integer, Integer> portList;

	private List<Flow> flows;

	//devices connected to each port of the switch
	private Map<Integer, DevInfo> wsnHostMap = new ConcurrentHashMap<Integer, DevInfo>();


	public String getDPID() {
		return DPID;
	}

	public void setDPID(String DPID) {
		this.DPID = DPID;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public Map<Integer, Integer> getPortList() {
		return portList;
	}

	public void setPortList(Map<Integer, Integer> portList) {
		this.portList = portList;
	}

	public double getLoad() {
		return load;
	}

	public void setLoad(double load) {
		this.load = load;
	}
}