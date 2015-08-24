package org.Mina.shorenMinaTest.mgr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.mgr.base.AConfiguration;
import org.Mina.shorenMinaTest.msg.tcp.BrokerUnit;
import org.Mina.shorenMinaTest.msg.tcp.GroupUnit;
import org.Mina.shorenMinaTest.msg.tcp.MsgConf;
import org.Mina.shorenMinaTest.msg.tcp.MsgConf_;


public class Configuration extends AConfiguration {
	private static Log log = LogFactory.getLog(Configuration.class);
	private RtMgr mgr;
	private int mgr2;
	private Properties props;
	
	public Configuration(RtMgr mgr) {
		this.mgr = mgr;
	}
	
	public Configuration() {
		// TODO Auto-generated constructor stub
		this.mgr2 = EnQueueTime;
	}
	
	public boolean configure2(){
		
		File file = new File("configure.txt");//��ȡ���������ļ�

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));

			String l;
			String[] s;

			while ((l = reader.readLine()) != null) {

				s = l.split(":");

				s[0] = s[0].trim();

				String adminAddr3;
				String adminAddr4;
				if (s[0].equals("administrator's address1"))
					adminAddr3 = s[1].trim();
				else if (s[0].equals("administrator's address2"))
					adminAddr4 = s[1].trim();
				else if (s[0].equals("administrator's port"))
					adminPort = Integer.parseInt(s[1].trim());
				else if (s[0].equals("queueSize"))
					queueSize = Integer.parseInt(s[1].trim());
				else if (s[0].equals("poolCount"))
					poolCount = Integer.parseInt(s[1].trim());
				else if (s[0].equals("connectCount"))
					connectCount = Integer.parseInt(s[1].trim());
				else if (s[0].equals("local group name"))
					groupName = s[1].trim();
				else if (s[0].equals("local address"))
					localAddr = s[1].trim();
				else if (s[0].equals("local port")){
					tPort = Integer.parseInt(s[1].trim());
				}
				else if (s[0].equals("EnqueueTime"))
					EnQueueTime = Integer.parseInt(s[1].trim());
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.warn(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.warn(e);
		}
		
		
		return true;
		
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
					adminAddr1 = props.getProperty ("adminAddress1");
//				else if (s[0].equals("administrator's address2"))
					adminAddr2 = props.getProperty ("adminAddress2");
//				else if (s[0].equals("administrator's port"))
					adminPort = Integer.valueOf(props.getProperty ("adminPort"));
//				else if (s[0].equals("queueSize"))
					queueSize = Integer.valueOf(props.getProperty ("queueSize"));
//				else if (s[0].equals("poolCount"))
					poolCount = Integer.valueOf(props.getProperty ("poolCount"));
//				else if (s[0].equals("connectCount"))
					connectCount = Integer.valueOf(props.getProperty ("connectCount"));
//				else if (s[0].equals("local group name"))
					groupName = props.getProperty ("localGroupName");
//				else if (s[0].equals("local address"))
					localAddr = props.getProperty ("localAddress");
//				else if(s[0].equals("local netmask"))
//					localNetmask = props.getProperty ("localNetmask");
////				else if (s[0].equals("local port"))
//					tPort = Integer.valueOf(props.getProperty ("localPort"));
//				
//					groupController = props.getProperty ("groupController");
//					
//					globalController = props.getProperty ("globalController");
		
//		String adminAddr1 = "";//������õ����ӹ���Ա��ַ
//		String adminAddr2 = "";//������õ����ӹ���Ա��ַ
//
//		File file = new File("configure.txt");//��ȡ���������ļ�
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
//
//				if (s[0].equals("administrator's address1"))
//					adminAddr1 = s[1].trim();
//				else if (s[0].equals("administrator's address2"))
//					adminAddr2 = s[1].trim();
//				else if (s[0].equals("administrator's port"))
//					adminPort = Integer.parseInt(s[1].trim());
//				else if (s[0].equals("queueSize"))
//					queueSize = Integer.parseInt(s[1].trim());
//				else if (s[0].equals("poolCount"))
//					poolCount = Integer.parseInt(s[1].trim());
//				else if (s[0].equals("connectCount"))
//					connectCount = Integer.parseInt(s[1].trim());
//				else if (s[0].equals("local group name"))
//					groupName = s[1].trim();
//				else if (s[0].equals("local address"))
//					localAddr = s[1].trim();
//				else if (s[0].equals("local port")){
//					tPort = Integer.parseInt(s[1].trim());
//			        System.out.println("��ǰ��TCP�˿ں�Ϊ��"+tPort);
//				}
//				else if (s[0].equals("EnqueueTime"))
//					EnQueueTime = Integer.parseInt(s[1].trim());
////				else if (s[0].equals("thresholdInitialize"))
////					thresholdInitialize = Long.parseLong(s[1].trim());
////				else if (s[0].equals("sendPeriodInitialize"))
////					sendPeriodInitialize = Long.parseLong(s[1].trim());
////				else if (s[0].equals("scanPeriodInitialize"))
////					scanPeriodInitialize = Long.parseLong(s[1].trim());
////				else if (s[0].equals("synPeriodInitialize"))
////					synPeriodInitialize = Long.parseLong(s[1].trim());
//			}
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
/*		while (!FindAdmin) {
			int flag = 1;//����Ա�Ƿ�������״̬
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
			if (flag == 1) {//����״̬,�����Ա�������ã����ܷ񷵻���ȷ������Ϣ����ȷ���Ƿ���������Ա
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
						childrenSize = conf_.childrenSize;
						nextInsertChild = 0;
						uPort = conf_.uPort;
						joinTimes = conf_.joinTimes;

						threshold = conf_.lostThreshold;
						sendPeriod = conf_.sendPeriod;
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
				adminAddr = AnotherAddr;//����������Ա�����ù���Ա��ַΪadminAddr2
				AnotherAddr = str;
			}
		}//while
*/
		//�������
		rep = new BrokerUnit();
		rep.addr = "";
		rep.tPort = tPort;
		uPort = 30002;
		//�������
		
		id = 0;

		joinOK = false;

		String repAddr = rep.addr;

		mgr.setState(rep.addr.equals("") ? mgr.getRepState() : mgr.getRegState());

		groupMap = new ConcurrentHashMap<String, GroupUnit>();

		clientTable = new ArrayList<String>();

		brokerTable = new ConcurrentHashMap<String, TreeSet<String>>();

		groupTable = new ConcurrentHashMap<String, TreeSet<String>>();

		neighbors = new ConcurrentHashMap<String, BrokerUnit>();

		children = new ArrayList<String>();

		wait4Hrt = new ArrayList<String>();

		udpMsgThreadSwitch = true;
		tcpMsgThreadSwitch = true;

		System.out.println("configuration finished! It's " + (repAddr.equals("") ? "representative" : "regular"));
		log.info("configuration finished! It's " + (repAddr.equals("") ? "representative" : "regular"));

		return FindAdmin;
	}
}
