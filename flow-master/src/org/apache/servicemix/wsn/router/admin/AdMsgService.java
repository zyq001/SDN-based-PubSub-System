package org.apache.servicemix.wsn.router.admin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.servicemix.application.WSNTopicObject;
import org.apache.servicemix.wsn.router.design.Data;
import org.apache.servicemix.wsn.router.design.PSManagerUI;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgGroupJunk;
import org.apache.servicemix.wsn.router.msg.tcp.MsgGroupLost;
import org.apache.servicemix.wsn.router.msg.tcp.MsgNewGroup;
import org.apache.servicemix.wsn.router.msg.tcp.MsgNewGroup_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgNewRep;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;

import com.bupt.wangfu.ldap.MsgTopicModify;

import org.apache.servicemix.wsn.router.msg.tcp.PolicyDB;
import org.apache.servicemix.wsn.router.msg.tcp.UpdateTree;
import org.apache.servicemix.wsn.router.topictree.TopicTreeManager;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;

import com.bupt.wangfu.ldap.Ldap;
import com.bupt.wangfu.ldap.MsgTopicModify_;
import com.bupt.wangfu.ldap.TopicEntry;


class cacheLostReportInfo {
	public String name;
	public Long lastTime;
	boolean whetherDelete;
}
public class AdMsgService extends AdminBase implements Runnable {

	private Socket s;

	private ObjectInputStream ois;

	private ObjectOutputStream oos;
	
	private List<String> cacheLostGroup;
	
	private Map<String, cacheLostReportInfo> cacheLostreportMap;
	
	private AdminMgr Amgr;

	public AdMsgService(AdminMgr amgr, Socket s) {
		
		Amgr = amgr;
		cacheLostGroup = new ArrayList<String>();
		cacheLostreportMap = new HashMap<String, cacheLostReportInfo>();

		this.s = s;
		if (s != null){
			try {

				ois = new ObjectInputStream(s.getInputStream());
				oos = new ObjectOutputStream(s.getOutputStream());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

	}

	@Override
	public void run() {
		try {

			Object msg = (Object) ois.readObject();
			System.out.println("msg type:" + msg.toString());
			process(msg);
			oos.close();
			ois.close();
			s.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("static-access")
	private void process(Object msg) {

		try {
			if (msg instanceof MsgConf) {// 新加入成员从管理员获取配置的消息

				MsgConf conf = (MsgConf) msg;

				System.out.println(conf.name + " : configuration");

				// 获取配置文件然后进行赋值
				MsgConf_ conf_ = ui.getConfiguration(conf.name);

				conf_.repAddr = "";
				if (groups.keySet().contains(conf.name)) {
					conf_.repAddr = groups.get(conf.name).addr;
					conf_.tPort = groups.get(conf.name).tPort;
				} else {// 第一次产生一个集群,更新管理员ConfigFile目录下对应集群的配置文件
					ui.updateConfigureFile(conf.name, conf.addr);
				}
				oos.writeObject(conf_);

			} else if (msg instanceof MsgNewGroup) {// 新集群加入的消息

				MsgNewGroup mng = (MsgNewGroup) msg;
				MsgNewGroup_ mng_ = new MsgNewGroup_();

				if (groups.keySet().contains(mng.name) && !groups.get(mng.name).addr.equals(mng.addr)) {
					mng_.isOK = false;
					mng_.description = "Group \"" + mng.name + "\" exists.\n";
				} else {
					mng_.isOK = true;
					mng_.groups.putAll(groups);
				}

				oos.writeObject(mng_);

			} else if (msg instanceof GroupUnit) {

				GroupUnit gu = (GroupUnit) msg;
				PolicyDB pdb = new PolicyDB();
				// GroupUnit g = new
				// GroupUnit(s.getInetAddress().getHostAddress(), mng.tPort,
				// mng.name);
				if (!groups.keySet().contains(gu.name)) {
					pdb.time = System.currentTimeMillis();
					pdb.clearAll = true;
					pdb.pdb.addAll(ShorenUtils.getAllPolicy());
					
					System.out.println("groups add" + gu.name);
					groups.put(gu.name, gu);
					pdb.groupMsg.putAll(groups);

					ui.newGroup(gu);
					Amgr.GroupsChangeNtfyBkp();
					ui.reloadAllGroup();
				} else {
					pdb.clearAll = false;
				}
				System.out.println(pdb.clearAll);
				oos.writeObject(pdb);;
				System.out.println("write finished!");
				

			} else if(msg instanceof WSNTopicObject){
				WSNTopicObject currenTopicTree = TopicTreeManager.topicTree;
				oos.writeObject(currenTopicTree);
				
			}
			
			else if (msg instanceof MsgGroupJunk) {//收到某集群不存在的消息

				MsgGroupJunk mgj = (MsgGroupJunk) msg;
				System.out.println("nonexist rep : " + mgj.name);
				ui.removeGroup(mgj.name, groups.get(mgj.name).addr);
				groups.remove(mgj.name);
				Amgr.GroupsChangeNtfyBkp();
				
			} else if (msg instanceof MsgNewRep) {

				MsgNewRep mnr = (MsgNewRep) msg;
				System.out.println(mnr.name + " : new rep");
				if(groups.contains(mnr.name)){
					groups.get(mnr.name).addr = mnr.addr;
					groups.get(mnr.name).tPort = mnr.tPort;

					ui.updateGroup(mnr.name, mnr.addr);
					ui.updateConfigureFile(mnr.name, mnr.addr);

				}
				
			} else if (msg instanceof MsgGroupLost) {//无验证

				MsgGroupLost mgl = (MsgGroupLost) msg;
				System.out.println("group lost : " + mgl.name+" sender "+mgl.sender);
				if (groups.containsKey(mgl.name)&&groups.containsKey(mgl.sender)) {
					if(cacheLostGroup.contains(mgl.name)) {
						return;
					}
					ui.text.append("收到"+mgl.sender+"报告："+mgl.name+"丢失");
					ui.text.paintImmediately(ui.text.getBounds());
					if(!cacheLostreportMap.containsKey(mgl.name) || (System.currentTimeMillis()-cacheLostreportMap.get(mgl.name).lastTime > 180000)){
						//需要探测
						cacheLostReportInfo chLRI = new cacheLostReportInfo();
						chLRI.whetherDelete = AdminMgr.sendHeartCheck(groups.get(mgl.name).addr, groups.get(mgl.name).tPort);
						System.out.println("groups.get(mgl.name).addr: "+"mhl.name" + mgl.name + groups.get(mgl.name).addr + "groups.get(mgl.name).tPort)" + groups.get(mgl.name).tPort);
						System.out.println("chLRI.whetherDelete : " + chLRI.whetherDelete);
						chLRI.lastTime = System.currentTimeMillis();
						
						
						if(!chLRI.whetherDelete){
							cacheLostGroup.add(mgl.name);
							ui.text.append("探测"+mgl.name+"发现其已掉线，将删除之..");
							ui.text.paintImmediately(ui.text.getBounds());
							ui.removeGroup(mgl.name, groups.get(mgl.name).addr);
							groups.remove(mgl.name);
							cacheLostGroup.remove(mgl.name);
							//回复发送者，已丢失，可删除
							oos.writeBoolean(true);
							Amgr.GroupsChangeNtfyBkp();
							ui.reloadAllGroup();
						}else {
							//回复发送者，仍在线，不可删除
							oos.writeBoolean(false);
							ui.text.append("探测"+mgl.name+"发现仍在线,不可删除..");
							ui.text.paintImmediately(ui.text.getBounds());
						}
					}else if(cacheLostreportMap.containsKey(mgl.name)&&cacheLostreportMap.get(mgl.name).whetherDelete){
						//三分钟前有人报告过，并且以探测可以并已删除，直接回复发送者						
						oos.writeBoolean(true);	
						ui.text.append(mgl.sender+"请求探测"+mgl.name+"，经查询发现最近有集群请求探测过，并且已知其已掉线，回复可删除");
						ui.text.paintImmediately(ui.text.getBounds());
					}else if(cacheLostreportMap.containsKey(mgl.name)&&!cacheLostreportMap.get(mgl.name).whetherDelete){
						
						ui.text.append(mgl.sender+"请求探测"+mgl.name+"，经查询发现最近有集群请求探测过，但其在线，回复不可删除");
						ui.text.paintImmediately(ui.text.getBounds());
						oos.writeBoolean(false);	
					}
					/*
					//探测该集群是否真的不存在了
					if(!AdminMgr.sendHeartCheck(groups.get(mgl.name).addr, groups.get(mgl.name).tPort)){
						//探测不到，直接删除
						cacheLostGroup.add(mgl.name);
						ui.text.append("探测"+mgl.name+"发现其已掉线，将删除之..");
						ui.text.paintImmediately(ui.text.getBounds());
						ui.removeGroup(mgl.name, groups.get(mgl.name).addr);
						groups.remove(mgl.name);
						cacheLostGroup.remove(mgl.name);
						//回复发送者，已丢失，可删除
						oos.writeBoolean(true);
						Amgr.GroupsChangeNtfyBkp();
						ui.reloadAllGroup();
						
					}else{
						//仍然存在，管理员处保留
						
						
					}
					
					*/
				}
			}else if (msg instanceof GroupAllInfo) {//当前主管理员发来的集群信息
				
				GroupAllInfo grps = (GroupAllInfo) msg;
//				if (grps.sendFlag.equals("Send") && grps != null){//当前程序为备份管理员，收到主管理员传来的集群信息，保存
//					
//				
//				System.out.println("get all groups,count is " + grps.groups.size());
//				Iterator<String> Itr = grps.groups.keySet().iterator();
//				while (Itr.hasNext()) {
//					String g = Itr.next();
//					groups.put(g, grps.groups.get(g));
//					//groupconfs.put(g, grps.groupconfs.get(g));
//					ui.updateConfigureFile(g,grps.groupconfs.get(g));
//					//ui.recoverGroup(g);//恢复集群信息，便于管理员查询
//				}
//			}else
				if (grps.sendFlag.equals("Ask")){//当前程序是主管理员，收到备份管理员的请求，回复所有集群信息
				
				GroupAllInfo sendInfo = new GroupAllInfo("Send");
				if (!groups.isEmpty()){
				Iterator<String> Itr = groups.keySet().iterator();
				while (Itr.hasNext()){					
					String g = Itr.next();
					MsgConf_ conf_ = ui.getConfiguration(g);
					sendInfo.groupconfs.put(g, conf_);					
				}				
				sendInfo.groups.putAll(groups);
				oos.writeObject(sendInfo);
			}
			}else if(grps.sendFlag.equals("Send")){
				System.out.println(grps);
				if(!grps.groups.isEmpty()){
				System.out.println("get all groups,count is " + grps.groups.size());
				Iterator<String> Itr = grps.groups.keySet().iterator();
				while (Itr.hasNext()) {
					String g = Itr.next();
					//groups.put(g, grps.groups.get(g));
					//groupconfs.put(g, grps.groupconfs.get(g));
					ui.updateConfigureFile(g,grps.groupconfs.get(g));
			}
				if(!groups.equals(grps.groups)) groups.putAll(grps.groups);
				System.out.println("groups变化,新的groups为" + groups.keys());
			}
			}
			}else if (msg instanceof MsgTopicModify) {
				
				try {
					PSManagerUI.topicTreeManager.lu = new Ldap();
					PSManagerUI.topicTreeManager.lu.connectLdap(AdminMgr.ldapAddr,"cn=Manager,dc=wsn,dc=com","123456");
					
						PSManagerUI.topicTreeManager.reload_LibTrees();
						PSManagerUI.topicTreeM.invalidate();
						PSManagerUI.topicTreeManager.data.sendNotification(new UpdateTree(System.currentTimeMillis()));					Data.sendNotification(new UpdateTree(System.currentTimeMillis()));
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				oos.writeObject(new MsgTopicModify_());
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
