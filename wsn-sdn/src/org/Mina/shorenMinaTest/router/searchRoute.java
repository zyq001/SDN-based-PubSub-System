package org.Mina.shorenMinaTest.router;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class searchRoute {
	
	static ArrayList<MsgSubsForm> nodeList = new ArrayList<MsgSubsForm>();
	static MsgSubsForm groupTableRoot = null;
	static generateNode gN = new generateNode();

	static ConcurrentHashMap<String, MsgSubsForm> testMap = new ConcurrentHashMap<String, MsgSubsForm>();

	public static ArrayList<String> calForwardIP(String topic, String originator,  ConcurrentHashMap<String, MsgSubsForm> testMap) {
		/*
		 * 参数：
		 * topic:主题。
		 * originator:转发源。
		 */


			ArrayList<String> forwardIP = new ArrayList<String>();
			//forwardIP:用来存储目的转发ip地址
			
			//MsgSubsForm msf = groupTableRoot;
			//msf-->groupTableRoot是干嘛的？？
			
			boolean thisGroup = false;

			/*
			 * String-->groupName:本集群的名字
			 * BrokerUnit-->rep：本集群代表的地址
			 * ConcurrentHashMap-->neighbors：本机所在集群的其他代理，key为代理地址
			 */
			
			String[] splited = topic.split(":");
			
			MsgSubsForm msf = testMap.get(splited[0]);
			
			
			String temp = "";
			//temp：将分离开的主题再结合在一起。
			
			//System.out.println(groupTableRoot.topicChildList.size());

			if(msf.topicComponent.equals(splited[0]))
			for (int i = 1; i < splited.length; i++) {
				if (msf.topicChildList.containsKey(splited[i])){
					//System.out.println(msf.topicComponent+":"+msf.routeNext);
					msf = msf.topicChildList.get(splited[i]);
				}
				else{
					//System.out.println(splited[i]);
					break;
				}
				if (thisGroup)
					forwardIP.add(msf.routeRoot);
				if(i > 0)
					temp += ":";
				temp += splited[i];
				/*if (brokerTable.containsKey(temp) || clientTable.contains(temp)) {
					
					 * ConcurrentHashMap-->brokerTable:本集群里其他代理的订阅信息，key为主题，value为订阅代理的地址
					 * ArrayList<String-->clientTable:本地的订阅信息,本地broker订阅主题的集合
					 
					
					forwardIP.addAll(msf.routeNext);
					if (!thisGroup)
						break;
				}*/
			}
			
			forwardIP.addAll(msf.routeNext);
			if (forwardIP.contains(originator))
				forwardIP.remove(originator);
			return forwardIP;

	}
}
