package org.apache.servicemix.wsn.router.design;

import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;

public interface IAdminUI {

	public void newGroup(GroupUnit newGroup);
	
	public void removeGroup(String name, String address);
	
	public MsgConf_ getConfiguration(String name);
	/*接口说明
	 * 参数为要查找的group的名字
	 * 返回值是String
	 *  1.不存在该组，即该组没有向管理者注册,返回null
	 *  2.该组已经注册，且存在配置文件,返回String变量的格式为"组名称#组播地址#组播端口号#超时#集群规模#子集群数量"
	 *  3.如果该组没有配置文件，则返回默认配置文件内容，格式"Default#组播地址#组播端口号#超时#集群规模#子集群数量"
	 *  4.用‘#’分隔不同数据项
	 * */
	
	void updateGroup(String name,String newAddress);
	
}
