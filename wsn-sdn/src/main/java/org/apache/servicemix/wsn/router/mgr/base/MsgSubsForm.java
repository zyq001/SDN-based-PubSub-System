package org.apache.servicemix.wsn.router.mgr.base;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MsgSubsForm {

	public String topicComponent;//本级名称

	public ConcurrentHashMap<String, MsgSubsForm> topicChildList; //下一级消息，key为消息名称

	public ArrayList<String> subs; //订阅此消息的集群,value为其订阅时间

	public ArrayList<String> routeNext; //针对该名称的转发下一跳节点

	public String routeRoot; //该名称转发树的根节点

	public MsgSubsForm() {
		topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		subs = new ArrayList<String>();
		routeNext = new ArrayList<String>();
	}

}