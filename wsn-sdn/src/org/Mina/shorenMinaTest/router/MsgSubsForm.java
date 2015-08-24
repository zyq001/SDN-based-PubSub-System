package org.Mina.shorenMinaTest.router;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MsgSubsForm {
	public String topicComponent;//本级名称
	
	public ConcurrentHashMap<String,MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>(); //下一级消息，key为消息名称
	
	public ArrayList<String> subs = new ArrayList<String>(); //订阅此消息的集群,value为其订阅时间
	
	public ArrayList<String> routeNext = new ArrayList<String>(); //针对该名称的转发下一跳节点
	
	public String routeRoot; //该名称转发树的根节点
	
	public MsgSubsForm() {
		topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		subs = new ArrayList<String>();
		routeNext = new ArrayList<String>();
	}
	
	public MsgSubsForm(String nodeName,ConcurrentHashMap<String,MsgSubsForm> topicChildList, ArrayList<String> subs,  ArrayList<String> routeNext) {
		
		this.topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		this.subs = new ArrayList<String>();
		this.routeNext = new ArrayList<String>();
		
		this.topicComponent = nodeName;
		this.topicChildList = topicChildList;
		this.subs = subs;
		this.routeNext = routeNext;
		
	}
	
	
	
}
