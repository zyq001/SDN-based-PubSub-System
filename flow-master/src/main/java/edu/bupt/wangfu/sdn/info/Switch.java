package edu.bupt.wangfu.sdn.info;



import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-7-14.
 */
public class Switch {


    private String DPID;
    private String mac;
    private String ipAddr;
    private Map<Integer, Integer> portList;
    private Long lastSeen;

    private List<Flow> flows;

    private Map<String, WSNHost> wsnHostMap = new ConcurrentHashMap<String, WSNHost>();

    public Long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Long lastSeen) {
        this.lastSeen = lastSeen;
    }

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

    public Map<Integer, Integer> getPortList() {
        return portList;
    }

    public void setPortList(Map<Integer, Integer> portList) {
        this.portList = portList;
    }
}