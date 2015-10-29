package edu.bupt.wangfu.sdn.info;

import org.apache.servicemix.wsn.router.router.GlobleUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-5.
 */
public class Controller {
	public String url;

	private Map<String, Switch> switchMap = new ConcurrentHashMap<String, Switch>();

	public Controller(String controllerAddr) {
		this.url = controllerAddr;
	}

	public boolean isAlive() {

		return true;
	}

	public Map<String, Switch> getSwitchMap() {
		return switchMap;
	}

	public void setSwitchMap(Map<String, Switch> switchMap) {
		this.switchMap = switchMap;
	}

	public void reflashSwitchMap() {

		switchMap = GlobleUtil.getRealtimeSwitchs(this);

	}

}
