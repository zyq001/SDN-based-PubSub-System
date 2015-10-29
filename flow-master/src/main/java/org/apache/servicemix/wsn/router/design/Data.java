package org.apache.servicemix.wsn.router.design;

import org.apache.servicemix.wsn.router.admin.AdminBase;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.msg.tcp.UpdateTree;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

//组信息数据结构
class GroupInfo {
	public String GroupName;
	public String GroupAddress;
	public Date date;
	public int port;

	GroupInfo() {
	}
}

//主机信息数据结构
class HostInfoStr {
	public String GroupName;
	public String HostAddress;
	public String SubInfo;

	HostInfoStr() {
	}
}

//界面的窗口类
class Window {
	public TabFolder tabFolder;
	public TabItem tabItem;
	public Label label_operate;
	public Label label_event;

	Window(TabFolder tabFolder, TabItem tabItem, Label label_operate, Label label_event) {
		this.tabFolder = tabFolder;
		this.tabItem = tabItem;
		this.label_event = label_event;
		this.label_operate = label_operate;
	}
}

//主机配置信息结构
class GroupConfiguration {
	//基本配置项
	public String GroupName;  //集群的名称 

	public String repAddr;//代表地址

	public int tPort;//代表的TCP端口号

	public int childrenSize;//子节点数目

	public String mutltiAddr;//组播地址

	public int uPort;//组播端口号

	public int joinTimes;//加入超时时间

	public long synPeriod;//

	//心跳检测配置项
	public long lostThreshold;//判定失效的阀值

	public long scanPeriod;//扫描周期

	public long sendPeriod;//发送周期
}

//数据类
public class Data {
	/**
	 * @param args
	 */
	private List<GroupInfo> groupList = new ArrayList<GroupInfo>();
	private List<HostInfoStr> hostList = new ArrayList<HostInfoStr>();
	private FileOperation file = new FileOperation();
	private List<String> historyList = new ArrayList<String>();
	/*
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InitalHostInfo();
		String SpecGroupName="G1";
		List<HostInfoStr> SpecHost=new ArrayList<HostInfoStr>();
		//ShowHostList(HostList);
		SpecHost=getSpecGroup(SpecGroupName);
		ShowHostList(SpecHost);
	}
	*/

	public static void sendNotification(UpdateTree ut) {
		Socket s = null;
		ObjectOutputStream oos = null;

		int times = 3;
		for (GroupUnit g : AdminBase.groups.values()) {
			try {
				s = new Socket(g.addr, g.tPort);
				oos = new ObjectOutputStream(s.getOutputStream());
				oos.writeObject(ut);
				if (--times == 0) {
					break;
				}
			} catch (UnknownHostException e) {
				System.out.println("Unkonwn host " + g.name);
			} catch (IOException e) {
				System.out.println("can't connect host " + g.name);
			}
		}
	}

	//根据特定组查询组内主机信息函数
	public List<HostInfoStr> getSpecGroup(String GroupName) {
		Iterator<HostInfoStr> itr = hostList.iterator();
		ShowHostList(hostList);
		List<HostInfoStr> SpecGroup = new ArrayList<HostInfoStr>();
		while (itr.hasNext()) {
			//System.out.println("开始查找");
			HostInfoStr SpecHost = (HostInfoStr) itr.next();
			if (SpecHost.GroupName.equals(GroupName)) {
				SpecGroup.add(SpecHost);
				System.out.println("找到一条记录");
			}
		}
		return SpecGroup;

	}

	//返回某个特定的集群信息
	public GroupInfo getGroup(String grpName) {
		int index = getGroupIndex(grpName);
		if (index != -1) {
			return groupList.get(index);
		} else {
			return null;
		}

	}

	//组内主机信息初始化
	public void InitalHostInfo() {
		for (int i = 0; i < 10; i++) {
			HostInfoStr item = new HostInfoStr();
			item.GroupName = new String("G" + i);
			item.HostAddress = new String("118.229.134." + i);
			item.SubInfo = new String("订阅第" + i + "条记录");
			hostList.add(item);
		}
		System.out.println("初始化完毕");
	}

	//打印某写特定的主机信息
	public void ShowHostList(List<HostInfoStr> ShowedList) {
		Iterator<HostInfoStr> itr = ShowedList.iterator();
		while (itr.hasNext()) {
			HostInfoStr SpecHost = (HostInfoStr) itr.next();
			System.out.println("组号" + SpecHost.GroupName + "主机地址" + SpecHost.HostAddress + "订阅信息" + SpecHost.SubInfo);
		}

	}

	//清除订阅信息表
	public void ClearHostList() {
		hostList.clear();
	}

	//清除组列表
	public void ClearGroupList() {
		groupList.clear();

	}

	//返回GroupList
	public List<GroupInfo> getAllGroup() {
		return groupList;
	}

	//返回HistoryList
	public List<String> getHistory() {
		return historyList;
	}

	//增加组成员
	public int addGroup(GroupInfo aGroup) {
		int stat = 0;
		//待添加检查十分存在该组的代码
		if (getGroupIndex(aGroup.GroupName) == -1) {
			//		System.out.println("准备添加");
			groupList.add(aGroup);
			//		System.out.println("添加完毕");
			stat = 1;
		} else {
			stat = -1;
			//		System.out.println("已存在该组");
		}


		//-------------------------//
		return stat;

	}

	//删除组成员
	public int removeGroup(String grpName) {
		int stat = 0;
		int index = -1;
		index = getGroupIndex(grpName);
		//System.out.println("要删除的组名称是"+grpName);

		if (index != -1) {
			GroupInfo deletedGroup = groupList.get(index);
			SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
			Date date = new Date();
			String history = new String("集群名称:" + deletedGroup.GroupName
					+ "\t组代表地址:" + deletedGroup.GroupAddress
					+ "\t删除时间:" + sdf.format(date) + "\n");
			historyList.add(history);
			file.WriteHistoryFile(deletedGroup);

			groupList.remove(index);
			stat = 1;
		} else {
			stat = -1;
			//	System.out.println("不存在该组");
		}
		//-------------------------//
		return stat;
	}

	//根据特定的组名称查找对应的序号
	public int getGroupIndex(String grpName) {
		int index = -1;
		int count = 0;
		Iterator<GroupInfo> itr = groupList.iterator();
		while (itr.hasNext()) {
			GroupInfo temGroup = new GroupInfo();
			temGroup = (GroupInfo) itr.next();
			if (temGroup.GroupName.equals(grpName)) {
				index = count;
			}
			count++;
		}
		return index;

	}

	//根据特定的主机地址查找对应的序号
	public int getHostIndex(String hstAddr) {
		int index = -1;
		int count = 0;
		Iterator<HostInfoStr> itr = hostList.iterator();
		while (itr.hasNext()) {
			HostInfoStr temGroup = new HostInfoStr();
			temGroup = (HostInfoStr) itr.next();
			if (temGroup.HostAddress.equals(hstAddr)) {
				index = count;
			}
			count++;
		}
		return index;

	}

	//根据特定的订阅信息查找对应的序号
	public int getTopicIndex(String tpcString) {
		int index = -1;
		int count = 0;
		Iterator<HostInfoStr> itr = hostList.iterator();
		while (itr.hasNext()) {
			HostInfoStr temGroup = new HostInfoStr();
			temGroup = (HostInfoStr) itr.next();
			if (temGroup.SubInfo.equals(tpcString)) {
				index = count;
			}
			count++;
		}
		return index;

	}
	//add by goucanran
	//向订阅的所有集群推送通知，告知OPENLDAP数据库已修改，需要刷新

	//修改集群信息
	public int updateGroup(String name, String newAddress) {
		int stat = 0;
		int index = getGroupIndex(name);

		if (index == -1) {
			stat = -1;
		} else {
			GroupInfo theGroup = new GroupInfo();
			theGroup = groupList.get(index);
			if (theGroup.GroupAddress.equals(newAddress)) {
				stat = 0;
			} else {
				GroupInfo newGroup = new GroupInfo();
				newGroup.date = theGroup.date;
				newGroup.GroupName = name;
				newGroup.GroupAddress = newAddress;
				groupList.remove(index);
				groupList.add(newGroup);
				stat = 1;
			}
		}
		return stat;
	}

}

