package org.apache.servicemix.wsn.router.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


import java.util.Random;

import org.apache.servicemix.wsn.router.admin.base.AState;
import org.apache.servicemix.wsn.router.admin.detection.IDt;
import org.apache.servicemix.wsn.router.mgr.MsgNotis;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgIsPrimary;
import org.apache.servicemix.wsn.router.msg.tcp.MsgIsPrimary_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgSynTopoInfo;
import org.apache.servicemix.wsn.router.msg.udp.MsgHeart;

public class PriState extends AState{
	private AdminMgr Amgr;

	private IDt dt;

	public PriState(AdminMgr Amgr, IDt dt) {
		this.Amgr = Amgr;
		this.dt = dt;
	}
	
	@Override
	public void sendHrt(){
		// TODO Auto-generated method stub
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		MsgHeart heart = new MsgHeart();
		byte[] Bheart = null;

		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			heart.indicator = Amgr.localAddr;
			oos.writeObject(heart);
			Bheart = baos.toByteArray();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!(Amgr.SendHrtObj==null))//判断是否需要给备份发送心跳 
		{
			try {
				DatagramPacket p = new DatagramPacket(Bheart, Bheart.length, InetAddress.getByName(Amgr.backup), Amgr.uPort);
				DatagramSocket s = new DatagramSocket();
				s.send(p);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void lost(String indicator){
		// 当备份管理员丢失时,与任一结点建立连接,将丢失信息以通知消息格式送出
		if (Amgr.groups.size()>0){
		Random random = new Random();
		ArrayList<GroupUnit> list = new ArrayList(Amgr.groups.values());
		GroupUnit gu = list.get(random.nextInt(Amgr.groups.size()));
		MsgNotis backupAdminLost = new MsgNotis();
		backupAdminLost.sender = gu.addr;// root存储根节点的IP地址
		backupAdminLost.originatorGroup = gu.name;
		backupAdminLost.originatorAddr = Amgr.localAddr;
		backupAdminLost.topicName = "backupAdminLost";
		backupAdminLost.doc = "backupAdminLost";
		Date date = new Date();
		backupAdminLost.sendDate = date;

		Socket s = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			s = new Socket(gu.addr, gu.tPort);//与根通信
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			oos.writeObject(backupAdminLost);

			ois.close();
			oos.close();
			s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		System.out.println("Backup administrator lost！");
				
	}
	
	@Override
	public void synTopoInfo(){
		// TODO Auto-generated method stub
		if(!(Amgr.SendHrtObj==null)){
			MsgSynTopoInfo msti = new MsgSynTopoInfo();
			
			msti.originator=Amgr.localAddr;
			
			Socket s = null;
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;
	        
			try {
				s = new Socket(Amgr.backup, Amgr.tPort);
				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());
				oos.writeObject(msti);
				
				ois.close();
				oos.close();
				s.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("与主/备管理员连接断开");
//				e.printStackTrace();
			} 
		}
		
	}
	
	
	@Override
	public void processUdpMsg(Object msg){
		if (msg instanceof MsgHeart) {

			dt.onMsg(msg);
			System.out.println("heart from " + ((MsgHeart)msg).indicator);

		}
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void processTcpMsg(Socket s){
		ObjectInputStream ois=null;
		ObjectOutputStream oos=null;
		Object msg=null;
		
		try {
			ois=new ObjectInputStream(s.getInputStream());
			oos=new ObjectOutputStream(s.getOutputStream());
			msg=ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(msg instanceof MsgIsPrimary){
			MsgIsPrimary_ mia_=new MsgIsPrimary_();
			if(Amgr.IsPrimary==1)
			{
				mia_.BackupIsPrimary=1;
				Amgr.SendHrtObj=Amgr.backup;
				dt.addTarget(Amgr.backup);
			}else if(Amgr.IsPrimary==0){
				mia_.BackupIsPrimary=0;
			}
			
			try {
				oos.writeObject(mia_);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//catch
		}else if (msg instanceof GroupAllInfo) {//当前主管理员发来的集群信息
		
		GroupAllInfo grps = (GroupAllInfo) msg;
		if (grps.sendFlag.equals("Ask")){//当前程序是主管理员，收到备份管理员的请求，回复所有集群信息
		
		GroupAllInfo sendInfo = new GroupAllInfo("Send");
		if (!Amgr.groups.isEmpty()){
		Iterator<String> Itr = Amgr.groups.keySet().iterator();
		while (Itr.hasNext()){					
			String g = Itr.next();
			MsgConf_ conf_ = Amgr.ui.getConfiguration(g);
			sendInfo.groupconfs.put(g, conf_);					
		}
//		if (!groups.isEmpty()){
			sendInfo.groups.putAll(Amgr.groups);

		}
		try {
			oos.writeObject(sendInfo);
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		else if(grps.sendFlag.equals("Send")){		
			System.out.println(grps);
			if(!grps.groups.isEmpty()){
			System.out.println("get all groups,count is " + grps.groups.size());
			Iterator<String> Itr = grps.groups.keySet().iterator();
			while (Itr.hasNext()) {
				String g = Itr.next();
				//groups.put(g, grps.groups.get(g));
				//groupconfs.put(g, grps.groupconfs.get(g));
				Amgr.ui.updateConfigureFile(g,grps.groupconfs.get(g));
		}
			Amgr.groups.putAll(grps.groups);
			System.out.println("groups变化,新的groups为" + Amgr.groups);
		}
		}
		
		try {
			oos.close();
			ois.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}}