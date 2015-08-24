package org.apache.servicemix.wsn.router.admin;

import java.util.ArrayList;
import java.util.Map;

import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupMember_;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
public interface IAdmin {

	public GroupUnit[] lookupGroups();//��ѯ��ǰgroups
	
	public MsgLookupGroupMember_ lookupGroupMember(String name);//��ѯĳgroup�ĳ�Ա����Щ������Ϊ��Ⱥ��
	
	public Map<String,ArrayList<String>> lookupGroupSubscriptions(String name);//��ѯĳ��Ⱥ�Ķ��ģ�����Ϊ��Ⱥ��
	
	public Map<String,ArrayList<String>> lookupMemberSubscriptions(String name, String address);//��ѯĳ��Ⱥĳ��Ա�Ķ��ģ�����Ϊ��Ⱥ��ͳ�Ա��ַ
	
	public void setConfiguration(String name, MsgConf_ conf);//�������ã�����Ϊ��Ⱥ���������Ϣ
		
	public boolean setAddress(String oldAddr, int oldPort, String newAddr, int newPort);
	
	public void addSubscription();//����ĳ���ģ��Ȳ�Ū
	
	public void removeSubscription();//ɾ��ĳ���ģ��Ȳ�Ū
	
}
