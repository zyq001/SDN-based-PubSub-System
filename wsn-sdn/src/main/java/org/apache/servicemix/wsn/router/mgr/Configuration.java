package org.apache.servicemix.wsn.router.mgr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.wsn.router.mgr.base.AConfiguration;
import org.apache.servicemix.wsn.router.mgr.base.MsgSubsForm;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.msg.tcp.LSA;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration extends AConfiguration {
	private static Log log = LogFactory.getLog(Configuration.class);
	private RtMgr mgr;
	private Properties props;

	public Configuration(RtMgr mgr) {
		this.mgr = mgr;
	}

	public boolean configure() {
		// TODO Auto-generated method stub
		String adminAddr1 = "";//存放配置的主从管理员地址
		String adminAddr2 = "";//存放配置的主从管理员地址
		props = new Properties();
		String propertiesPath = "confige.properties";
		try {
			props.load(new FileInputStream(propertiesPath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("找不到公共配置文件");
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("读取公共配置文件时发生ioEception");

		}

//        props.getProperty ("ip");

//		File file = new File("configure.txt");//读取本地配置文件
//
//		BufferedReader reader = null;
//
//		try {
//			reader = new BufferedReader(new FileReader(file));
//
//			String l;
//			String[] s;
//
//			while ((l = reader.readLine()) != null) {
//
//				s = l.split(":");
//
//				s[0] = s[0].trim();

//				if (s[0].equals("administrator's address1"))
		adminAddr1 = props.getProperty("adminAddress1");
//				else if (s[0].equals("administrator's address2"))
		adminAddr2 = props.getProperty("adminAddress2");
//				else if (s[0].equals("administrator's port"))
		adminPort = Integer.valueOf(props.getProperty("adminPort"));
//				else if (s[0].equals("queueSize"))
		queueSize = Integer.valueOf(props.getProperty("queueSize"));
//				else if (s[0].equals("poolCount"))
		poolCount = Integer.valueOf(props.getProperty("poolCount"));
//				else if (s[0].equals("connectCount"))
		connectCount = Integer.valueOf(props.getProperty("connectCount"));
//				else if (s[0].equals("local group name"))
		groupName = props.getProperty("localGroupName");
//				else if (s[0].equals("local address"))
		localAddr = props.getProperty("localAddress");
//				else if(s[0].equals("local netmask"))
		localNetmask = props.getProperty("localNetmask");
//				else if (s[0].equals("local port"))
		tPort = Integer.valueOf(props.getProperty("localPort"));

		groupController = props.getProperty("groupController");

		globalController = props.getProperty("globalController");
//				else if (s[0].equals("thresholdInitialize"))
//					thresholdInitialize = Long.parseLong(s[1].trim());
//				else if (s[0].equals("sendPeriodInitialize"))
//					sendPeriodInitialize = Long.parseLong(s[1].trim());
//				else if (s[0].equals("scanPeriodInitialize"))
//					scanPeriodInitialize = Long.parseLong(s[1].trim());
//				else if (s[0].equals("synPeriodInitialize"))
//					synPeriodInitialize = Long.parseLong(s[1].trim());
//			}
//			reader.close();
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			log.warn(e);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			log.warn(e);
//		}

		Socket s = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;

		boolean FindAdmin = false;
		adminAddr = adminAddr1;
		String AnotherAddr = adminAddr2;
		while (!FindAdmin) {
			int flag = 1;//管理员是否在启动状态
			try {
				//get configuration
				s = new Socket();
				s.setSoTimeout(10000);
				s.connect(new InetSocketAddress(adminAddr, adminPort));
				//	s = new Socket(adminAddr, adminPort);
			} catch (Exception e) {
				log.warn(e);
				try {
					s.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				flag = 0;
			}
			if (flag == 1) {//启动状态,向管理员发送配置，看能否返回正确配置信息，以确定是否是主管理员
				try {
					oos = new ObjectOutputStream(s.getOutputStream());
					ois = new ObjectInputStream(s.getInputStream());

					//ask admin for configuration 
					MsgConf conf = new MsgConf();
					conf.name = groupName;
					conf.addr = localAddr;
					oos.writeObject(conf);

					//configurations returned and set them

					Object msg = ois.readObject();

					oos.close();
					ois.close();
					s.close();

					if (msg instanceof MsgConf_) {
						FindAdmin = true;
						MsgConf_ conf_ = (MsgConf_) msg;
						rep = new BrokerUnit();
						rep.addr = conf_.repAddr;
						rep.tPort = conf_.tPort;

						multiAddr = conf_.multiAddr;
						neighborSize = conf_.neighborSize;
						uPort = conf_.uPort;
						joinTimes = conf_.joinTimes;

						threshold = conf_.lostThreshold;
						System.out.println("lostthreshold:" + threshold);
						sendPeriod = conf_.sendPeriod;
						System.out.println("sendPeriod:" + sendPeriod);
						scanPeriod = conf_.scanPeriod;
						synPeriod = conf_.synPeriod;
					}//if	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.warn(e);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.warn(e);
				}
			}//if(flag==1)
			else {
				String str = adminAddr;
				adminAddr = AnotherAddr;//不是主管理员，设置管理员地址为adminAddr2
				AnotherAddr = str;
			}
		}//while

		id = 0;

		joinOK = false;

		String repAddr = rep.addr;

		mgr.setState(rep.addr.equals("") ? mgr.getRepState() : mgr.getRegState());

		groupMap = new ConcurrentHashMap<String, GroupUnit>();

		clientTable = new ArrayList<String>();

		brokerTable = new ConcurrentHashMap<String, TreeSet<String>>();

		fellows = new ConcurrentHashMap<String, BrokerUnit>();

		waitHello = new ArrayList<String>();

		lsdb = new ConcurrentHashMap<String, LSA>();

		neighbors = new ArrayList<String>();

		groupTableRoot = new MsgSubsForm();

		udpMsgThreadSwitch = true;
		tcpMsgThreadSwitch = true;
		lsaSeqNum = 0;
		cacheLSA = null;
		askMsg = "ask for lsdb and policys";
		treeTime = 0;

		System.out.println("configuration finished! It's " + (repAddr.equals("") ? "representative" : "regular"));
		log.info("configuration finished! It's " + (repAddr.equals("") ? "representative" : "regular"));

		return FindAdmin;
	}
}
