package org.apache.servicemix.wsn.router.admin;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalSubers {

	//?????????Map<ip,map<subaddr,list<topics>>>
	public static Map<String, ConcurrentHashMap<String, ArrayList<String>>> globalsubers = new ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<String>>>();

	public GlobalSubers() {

	}
}
