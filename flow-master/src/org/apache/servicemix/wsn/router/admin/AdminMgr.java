package org.apache.servicemix.wsn.router.admin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.swing.JOptionPane;

import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgHeartCheck;
import org.apache.servicemix.wsn.router.msg.tcp.MsgHeartCheck_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgIsPrimary;
import org.apache.servicemix.wsn.router.msg.tcp.MsgIsPrimary_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupMember;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupMember_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupSubscriptions;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupSubscriptions_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupMemberSubscriptions;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupMemberSubscriptions_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgSetAddr;
import org.apache.servicemix.wsn.router.msg.tcp.MsgSetConf;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.admin.base.AState;
import org.apache.servicemix.wsn.router.admin.detection.DtAdmin;
import org.apache.servicemix.wsn.router.admin.detection.HrtMsgHdlr;
import org.apache.servicemix.wsn.router.admin.detection.IDt;
import org.apache.servicemix.wsn.router.design.PSManagerUI;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;

public class AdminMgr extends AdminBase implements HrtMsgHdlr, Runnable, IAdmin {

	public ServerSocket serverSocket;//tcp socket

	private Socket s;//socket

	private ObjectInputStream ois;//对象输入流

	private ObjectOutputStream oos;//对象输出流

	//管理员备份所需信息
	public String localAddr;//管理员本地地址

	public String backup;//备份管理员地址
	
	public static String ldapAddr;//ldap地址

	public int uPort;//与备份管理员交互的UDP端口号

	public int tPort;//与备份管理员交互的TCP端口号

	public int IsPrimary;//是否是主管理员，从配置文件中读取

	private AState priState;//主管理员

	private AState backupState;//备份管理员

	private AState state;//当前管理员状态

	protected static String SendHrtObj;//存放需要发送心跳和同步信息的对象

	private IDt dt;//心跳检测模块

	private Thread tdt;//心跳线程
	private Thread tmt;//监听tcp连接的线程
	private Thread umt;//监听udp消息的线程

	public static int port2;//负责监听tcp的端口，暂存配置文件中的端口

	public AdminMgr() {

		dt = new DtAdmin(this);

		priState = new PriState(this, dt);
		backupState = new BackupState(this, dt);

		//初始化一些基本变量
		Configuration configuration = new Configuration(this);
		configuration.configure();
		groups = new ConcurrentHashMap<String, GroupUnit>();
		

		tmt = new Thread(new TcpMsgThread(this));//开tcp监听端口tPort
		umt = new Thread(new UdpMsgThread(this));//开udp监听端口uPort

		tmt.start();
		umt.start();
		int BackupIsPrimary = BackupIsPrimary();//与备份服务器建立连接，探知对方是否是主管理员
		if (BackupIsPrimary == 0) {//对方不是主管理员

			IsPrimary = 1;

			setState(this.getPriState());

			//心跳模块
			dt.setThreshold(45000);
			dt.setSendPeriod(10000);
			dt.setScanPeriod(15000);
			dt.setSynPeriod(20000);
			tdt = new Thread((Runnable) dt);
			tdt.start();

			port = port2;

			try {
				serverSocket = new ServerSocket(port);//监听30006,与集群代表交互
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				ShorenUtils.encodeAllPolicy();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				System.out.println("将所有策略信息写入xml文件时出错");
				e.printStackTrace();
			}
			System.out.println("策略加载完成");


			new Thread(this).start();
			ui = new PSManagerUI(this);

				Iterator<GroupUnit> Itr = groups.values().iterator();
				while (Itr.hasNext()) {
					GroupUnit g = Itr.next();
					ui.recoverGroup(g);//恢复集群信息，便于管理员查询
				}

			ui.open();
			
		} else if (BackupIsPrimary == 1) {//对方是主管理员
			setState(this.getBackupState());

			dt.setThreshold(20000);
			dt.setSendPeriod(10000);
			dt.setScanPeriod(15000);
			dt.setSynPeriod(20000);
			tdt = new Thread((Runnable) dt);
			tdt.start();

			IsPrimary = 0;
			dt.addTarget(backup);
			ui = new PSManagerUI(this);
			
			//从主管理员出获得集群信息，并开启监听端口适时接收新的集群信息
//			port = port2;
//
//			try {
//				serverSocket = new ServerSocket(port);//监听30006
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			askGroupBase();
		}

	}

	public AState getPriState() {
		return priState;
	}

	public AState getBackupState() {
		return backupState;
	}

	public void setState(AState state) {
		this.state = state;
	}

	public AState getState() {
		return state;
	}
	
	//被报告丢失，但是管理员可以探测到，则不把该集群从groups中删除，只做标记
	public void tagReportLost(String groupName){
		
//		ui.allGroupsPane.g
		
		
	}
	
	public void askGroupBase(){
		
		Socket s = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		Object msg = null;
		try{
			
			s = new Socket();
			s.connect(new InetSocketAddress(backup, tPort), 5000);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			GroupAllInfo mia = new GroupAllInfo("Ask");
			oos.writeObject(mia);
			Object msg2 = (Object)ois.readObject();
			if (msg2 instanceof GroupAllInfo){
				
				GroupAllInfo grps = (GroupAllInfo) msg2;
				if(!grps.groups.isEmpty()){
				System.out.println("get all groups,count is " + grps.groups.size());
				Iterator<String> Itr = grps.groups.keySet().iterator();
				while (Itr.hasNext()) {
					String g = Itr.next();
					groups.put(g, grps.groups.get(g));
					//groupconfs.put(g, grps.groupconfs.get(g));
					ui.updateConfigureFile(g,grps.groupconfs.get(g));
					System.out.println(groups);
			}
			}else{
				System.out.println("获得的集群信息为空");
			}
			}
			s.close();
			
		}catch (IOException e){
			System.out.println("请求所有集群信息连接失败");
			System.out.println(e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void GroupsChangeNtfyBkp(){
		
		try{
		Socket s = null;
		ObjectInputStream ois;//对象输入流

		ObjectOutputStream oos;//对象输出流
	//	if (s == null){
		
		s = new Socket();
		s.connect(new InetSocketAddress(backup, tPort), 5000);
		oos = new ObjectOutputStream(s.getOutputStream());
	//	ois = new ObjectInputStream(s.getInputStream());
		GroupAllInfo sendInfo = new GroupAllInfo("Send");
		if (!groups.isEmpty()){
		Iterator<String> Itr = groups.keySet().iterator();
		while (Itr.hasNext()){					
			String g = Itr.next();
			MsgConf_ conf_ = ui.getConfiguration(g);
			sendInfo.groupconfs.put(g, conf_);					
		}
//		if (!groups.isEmpty()){
			sendInfo.groups.putAll(groups);

		}
		oos.writeObject(sendInfo);
		s.close();
		System.out.println("通知备份管理员变化信息成功！");
		
//		}else{//当前socket未关闭
//			System.out.println("当前socket尚未关闭，可能连续发生两次变化，如两个集群几乎同时丢失");
//			MyObjectOutputStream myoos = new MyObjectOutputStream(s.getOutputStream());
//			GroupAllInfo sendInfo = new GroupAllInfo("Send");
//			if (!groups.isEmpty()){
//			Iterator<String> Itr = groups.keySet().iterator();
//			while (Itr.hasNext()){					
//				String g = Itr.next();
//				MsgConf_ conf_ = ui.getConfiguration(g);
//				sendInfo.groupconfs.put(g, conf_);					
//			}
////			if (!groups.isEmpty()){
//				sendInfo.groups.putAll(groups);
//
//			}
//			myoos.writeObject(sendInfo);
//			s.close();
//			System.out.println("通知备份管理员变化信息成功！");
//		}
		
		}catch (IOException e){
			System.out.println("通知备份管理员变化信息失败，连接出错或备份管理员未启动");
			//System.out.println(e.toString());
		} 
//		catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	


	public int BackupIsPrimary() {

		Socket s = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		Object msg = null;
		try {
			s = new Socket();
			s.connect(new InetSocketAddress(backup, tPort), 5000);
		} catch (IOException e) {
			return 0;
		}

		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());

			MsgIsPrimary mia = new MsgIsPrimary();
			oos.writeObject(mia);

			msg = ois.readObject();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (msg instanceof MsgIsPrimary_) {
			MsgIsPrimary_ mia_ = (MsgIsPrimary_) msg;
			if (mia_.BackupIsPrimary == 1) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public void run() {

		try {
			while (true) {
				//监听，得到连接
				Socket socket = serverSocket.accept();

				//分配一个线程处理这个连接
				new Thread(new AdMsgService(this, socket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public GroupUnit[] lookupGroups() {
		return groups.values().toArray(new GroupUnit[] {});
	}

	@Override
	public MsgLookupGroupMember_ lookupGroupMember(String name) {
		MsgLookupGroupMember mlgm = new MsgLookupGroupMember();
		mlgm.name = name;//赋集群名

		//发送消息并等待返回
		Object groupMem= doLookup(name, mlgm);
		if(groupMem != null)
		return (MsgLookupGroupMember_)groupMem ;
		else
			return null;
	}

	@Override
	public Map<String,ArrayList<String>> lookupGroupSubscriptions(String name) {
		MsgLookupGroupSubscriptions mlgs = new MsgLookupGroupSubscriptions();
		mlgs.name = name;
		MsgLookupGroupSubscriptions_ mlgm_ = (MsgLookupGroupSubscriptions_) doLookup(name, mlgs);
		if (mlgm_ == null) {
			return null;
		}
		return mlgm_.topicsASubers;
	}

	@Override
	public Map<String,ArrayList<String>> lookupMemberSubscriptions(String name, String address) {
		MsgLookupMemberSubscriptions mlms = new MsgLookupMemberSubscriptions();
		mlms.name = name;
		mlms.addr = address;
		MsgLookupMemberSubscriptions_ mlms_ = (MsgLookupMemberSubscriptions_) doLookup(name, mlms);

		if (mlms_ == null) {//如果输入地址不在集群内部或者输入格式错误
			return null;
		}
		return mlms_.topicAsubers;
	}
	
	public static boolean sendHeartCheck(String ipAddr, int tPort){
		
		
		Socket s = null;
		ObjectInputStream ois;//对象输入流

		ObjectOutputStream oos;//对象输出流		
	
			Object o = null;		
			try {
				s = new Socket();
				s.connect(new InetSocketAddress(ipAddr, tPort), 5000);
				oos = new ObjectOutputStream(s.getOutputStream());
				MsgHeartCheck msg = new MsgHeartCheck();
				oos.writeObject(msg);//发送

				ois = new ObjectInputStream(s.getInputStream());
				o = ois.readObject();//接收
				if(o instanceof MsgHeartCheck_){
					s.close();
					System.out.println("探测成功：" + MsgHeartCheck_.class);
					return true;
				}else{
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					oos.writeObject(msg);//发送
					ois = new ObjectInputStream(s.getInputStream());
					o = ois.readObject();//接收
					if(o instanceof MsgHeartCheck_){
						s.close();
						System.out.println("探测成功：" + MsgHeartCheck_.class);
						return true;
					}
					s.close();
					System.out.println("探测返回不正确 return false");
					return false;					
				}			
			} catch (IOException e) {
				System.out.println("查询集群成员订阅时连接出错");
				return false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}			
			System.out.println("最终返回");
		return false;		
	}

	private Object doLookup(String name, Object msg) {
		
		Socket s = null;
		ObjectInputStream ois;//对象输入流

		ObjectOutputStream oos;//对象输出流
		
		if (msg instanceof MsgLookupMemberSubscriptions) {
			Object o = null;

			GroupUnit g = groups.get(name);//查表

			MsgLookupMemberSubscriptions mlms = (MsgLookupMemberSubscriptions) msg;
			try {
				s = new Socket();
				s.connect(new InetSocketAddress(mlms.addr, g.tPort), 5000);
				oos = new ObjectOutputStream(s.getOutputStream());
				oos.writeObject(msg);//发送

				ois = new ObjectInputStream(s.getInputStream());
				o = ois.readObject();//接收

				s.close();
			} catch (IOException e) {
				System.out.println("查询集群成员订阅时连接出错");
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			return o;
		}
		else {
			Object o = null;

			GroupUnit g = groups.get(name);//查表
			if(g!=null&&g.addr!=null&g.tPort!=0){
			try {
				s = new Socket();
				s.connect(new InetSocketAddress(g.addr, g.tPort), 5000);
				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());

				oos.writeObject(msg);//发送
				oos.flush();
				o = ois.readObject();//接收

				s.close();
			} catch (IOException e) {
				System.out.println("查询集群信息时连接出错");
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return o;
		}else{
			//警告框 不存在该集群
			System.out.println("该集群已被报告丢失并删除");
		}
	
		}
		return null;
	}

	@Override
	public void setConfiguration(String name, MsgConf_ conf_) {
		
		Socket s = null;
		ObjectInputStream ois;//对象输入流

		ObjectOutputStream oos;//对象输出流
		
		GroupUnit g = groups.get(name);
		MsgSetConf msc = new MsgSetConf();
		msc.conf_ = conf_;
		msc.address = null;

		try {

			s = new Socket(InetAddress.getByName(g.addr), g.tPort);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			oos.writeObject(msc);

			ois.close();
			oos.close();
			s.close();
			GroupsChangeNtfyBkp();//通知备份管理员更新
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addSubscription() {

	}

	@Override
	public void removeSubscription() {

	}

	@Override
	public void sendHrtMsg() {
		state.sendHrt();
	}

	@Override
	public void synTopoInfo() {
		state.synTopoInfo();

	}

	@Override
	public void lost(String indicator) {
		this.SendHrtObj = null;
		state.lost(indicator);
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		new Thread(){
//			void testprint() {
//				while (true){
//					System.out.println("===================grousp:" + groups);
//					try {
//						sleep(2);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		};
		new AdminMgr();
		
		//PSManagerUI ui = new PSManagerUI(null);
		//ui.open();
		//GroupUnit gu = new GroupUnit("10.108.166.236",10035,"G13");
		//ui.newGroup(gu);
		//		AdminMgr.groups.put("ABC", new GroupUnit("123", 123, "ABC"));
		//		AdminMgr.groups.put("HHH", new GroupUnit("234", 234, "HHH"));
		//		GroupUnit[] names = ad.lookupGroups();
		//		for (int i = 0; i < names.length; i++)
		//			System.out.println(names[i]);
	}

	@Override
	public boolean setAddress(String oldAddr, int oldPort, String newAddr, int newPort) {
		MsgSetAddr msa = new MsgSetAddr();
		msa.addr = newAddr;
		msa.port = newPort;

		Socket s = null;
		ObjectOutputStream oos = null;

		try {
			s = new Socket(oldAddr, oldPort);
			oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(msa);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
