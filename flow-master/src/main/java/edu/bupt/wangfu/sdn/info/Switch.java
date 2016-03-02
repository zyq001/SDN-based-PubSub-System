package edu.bupt.wangfu.sdn.info;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-7-14.
 */
public class Switch extends DevInfo {


	private String DPID;
	private String mac;
	private String ipAddr;//ipv4

	private double load;//参数无法通过flootlight获取
	//    private Map<Integer, Integer> portList;
	private Long lastSeen;

	private List<Flow> flows;

	//key是端口号，value是设备
	private Map<Integer, DevInfo> wsnDevMap = new ConcurrentHashMap<Integer, DevInfo>();

	public Map<Integer, DevInfo> getWsnDevMap() {
		return wsnDevMap;
	}

	public void setWsnDevMap(Map<Integer, DevInfo> wsnDevMap) {
		this.wsnDevMap = wsnDevMap;
	}

	public void put(int port, DevInfo devInfo) {
		this.wsnDevMap.put(port, devInfo);
	}

	//    public Long getLastSeen() {
//        return lastSeen;
//    }
//
//    public void setLastSeen(Long lastSeen) {
//        this.lastSeen = lastSeen;
//    }
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

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

//    public Map<Integer, Integer> getPortList() {
//        return portList;
//    }
//
//    public void setPortList(Map<Integer, Integer> portList) {
//        this.portList = portList;
//    }

	public double getLoad() {
		return load;
	}

	public void setLoad(double load) {
		this.load = load;
	}

//	public Map<Integer, DevInfo> getWsnHostMap() {
//		return wsnHostMap;
//	}
//
//	public void setWsnHostMap(Map<Integer, DevInfo> wsnHostMap) {
//		this.wsnHostMap = wsnHostMap;
//	}
}