package org.apache.servicemix.wsn.router.mgr.base;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MsgSubsForm{

	public String topicComponent;//????

	public ConcurrentHashMap<String,MsgSubsForm> topicChildList; //??????key?????

	public ArrayList<String> subs; //????????,value??????

	public ArrayList<String> routeNext; //?????????????

	public String routeRoot; //??????????

	public MsgSubsForm() {
		topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		subs = new ArrayList<String>();
		routeNext = new ArrayList<String>();
	}

}