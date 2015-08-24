package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.servicemix.wsn.router.mgr.GroupUnit;

public class MsgInsert_ implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public boolean isOK;//是否插入成功
	
	public HashMap<String, TreeSet<String>> groupTab;//subscription info about groups
	
	public HashMap<String, GroupUnit> groupMap;
	
	public String next;//下个集群的地址
	
	public int tPort;//下一个集群的tcp端口号
	
	public String name;//集群名字
	
	public int uPort;//本集群的udp端口号
	
	public long id;
	
	public MsgInsert_() {
		
		groupTab = new HashMap<String, TreeSet<String>>();
		
		groupMap = new HashMap<String, GroupUnit>();
		
	}
	
}
