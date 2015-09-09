package org.apache.servicemix.wsn.router.admin;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;

public class GroupAllInfo extends AdminBase implements Serializable{
	/**
	 * 存放集群所有信息，包括配置信息，用于主管理员向备份管理员传送
	 * 
	 * */
	
	private static final long serialVersionUID = 1L;
	
	public  ConcurrentHashMap<String, MsgConf_> groupconfs;//保存所有集群的配置信息，用于主从管理员之间同步

	public   ConcurrentHashMap<String, GroupUnit> groups;//名字:group信息，保存所有group的信息
	
	public  String sendFlag = "Ask";
	
	public GroupAllInfo(String sendflag){
		
		this.sendFlag = sendflag;
		this.groupconfs = new ConcurrentHashMap<String, MsgConf_>();
		this.groups = new ConcurrentHashMap<String, GroupUnit> ();
	}


}
