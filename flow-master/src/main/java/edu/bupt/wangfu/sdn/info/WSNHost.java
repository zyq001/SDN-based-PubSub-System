package edu.bupt.wangfu.sdn.info;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-5.
 */
public class WSNHost extends DevInfo {
	public String mac;
	private Map<String, List<String>> subers = new ConcurrentHashMap<String, List<String>>();

	@Override
	public String getMac() {
		return mac;
	}

	@Override
	public void setMac(String mac) {
		this.mac = mac;
	}
}
