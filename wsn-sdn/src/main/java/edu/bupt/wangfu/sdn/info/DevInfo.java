package edu.bupt.wangfu.sdn.info;

import edu.bupt.wangfu.sdn.floodlight.RestProcess;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DevInfo {
	private static DevInfo INSTANCE = null;
	private String url;
	private String mac;
	private String port;
	private String errorStatus;
	private String lastSeen;
	private String remark;
	private Map<String, Switch> switchs;
	private Map<Switch, List<Switch>> topology;


//	private DevInfo(){
//
//		String floodLightIP = Configure.floodlightIP;
//
//
//	}

	public static DevInfo getINSTANCE() {
		if (INSTANCE == null)
			INSTANCE = new DevInfo();
		return INSTANCE;
	}

	public Map<Switch, List<Switch>> getRuntimeTopology() {

		RestProcess.downRuntimeTopology();
		return topology;
	}

	public Map<Switch, List<Switch>> getTopology() {
		return topology;
	}

	public void setTopology(Map<Switch, List<Switch>> topology) {
		this.topology = topology;
	}

	public Map<String, Switch> getSwitchs() {
		return switchs;
	}

	public void setSwitchs(Map<String, Switch> switchs) {
		this.switchs = switchs;
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(String errorStatus) {
		this.errorStatus = errorStatus;
	}

	public String getLastSeen() {//transfer to Date
		return new SimpleDateFormat("HH:mm:ss dd/MM/yy").format(new Date(Long.valueOf(lastSeen)));
//		return lastSeen;
	}

	public void setLastSeen(String lastSeen) {
		this.lastSeen = lastSeen;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


}
