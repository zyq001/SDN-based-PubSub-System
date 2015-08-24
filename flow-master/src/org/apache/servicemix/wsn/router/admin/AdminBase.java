package org.apache.servicemix.wsn.router.admin;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.servicemix.wsn.router.design.PSManagerUI;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;

public abstract class AdminBase  {
	

	private static final long serialVersionUID = 1L;
	
//	public static ConcurrentHashMap<String, MsgConf_> groupconfs;//保存所有集群的配置信息，用于主从管理员之间同步
	
	public static  ConcurrentHashMap<String, GroupUnit> groups;//名字:group信息，保存所有group的信息
	
	
	static int port;//管理者监听的TCP端口号
	
	static PSManagerUI ui;
	
	
	
	
}
