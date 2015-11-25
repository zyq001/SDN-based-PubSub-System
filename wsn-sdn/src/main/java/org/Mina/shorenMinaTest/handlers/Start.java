package org.Mina.shorenMinaTest.handlers;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.mgr.RtMgr;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.msg.tcp.MsgNotis;
import org.Mina.shorenMinaTest.msg.tcp.highPriority;
import org.Mina.shorenMinaTest.msg.tcp.lowPriority;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.UDPForwardMsg;
import org.Mina.shorenMinaTest.router.MsgSubsForm;
import org.Mina.shorenMinaTest.router.generateNode;
import org.apache.log4j.PropertyConfigurator;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.servicemix.application.WsnProcessImpl;
import org.apache.servicemix.wsn.CrossGroupMsgForwardQueue;
import org.apache.servicemix.wsn.router.router.Router;

import javax.xml.bind.JAXBException;
import javax.xml.ws.Endpoint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import org.Mina.shorenMinaTest.queues.Destination;

public class Start {
	public static RtMgr mgr;
	public static ArrayList<String> forwardIP = new ArrayList<String>();//存储转发目的ip的变量
	public static ArrayList<MsgSubsForm> nodeList = new ArrayList<MsgSubsForm>();
	public static MsgSubsForm groupTableRoot = null;
	public static generateNode gN = new generateNode();
	public static ConcurrentHashMap<String, MsgSubsForm> testMap = new ConcurrentHashMap<String, MsgSubsForm>();

	private static int UDPtotalCount = 0;
	private static int totalCount = 0;

	public static void main(String[] args) {

		PropertyConfigurator.configure("log4j.properties");

//		MsgNotis msg = new MsgNotis();
//		msg.doc = "test";
//		msg.originatorAddr = "localhost";
//		msg.topicName = "all";
//		msg.sender = "localhost";
////		msg.topicName = nom.getTopicName();
////		msg.doc = nom.getDoc();
//		msg.originatorGroup = "G1";
////		msg.originatorAddr = localAddr;
//		msg.sendDate = new Date().toString();
//
//		NioDatagramConnector connector = MinaUtil.createDatagramConnector();
//		ConnectFuture cf = null;
//		try {
//			cf = connector.connect(new InetSocketAddress(Inet6Address.getByName("FF01:0000:0000:0000:0001:2345:6789:abcd"), 7777));
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
////		CrossGroupMsgForwardQueue.grtInstance().start();
//
//		cf.awaitUninterruptibly();
//
//		int counter = 100000;
//		while (counter-- > 1) {
////			System.out.println("counter: "+counter);
////            send();
////			generateMsgNoticeMsg(msg);
//
////			cf.awaitUninterruptibly();
//
////			IoBuffer buffer = IoBuffer.allocate(64);
////
////			buffer.put("ttttttttttttttttt".getBytes());
////
////			buffer.flip();
//			cf.getSession().write("tttttttttttttttttttttttttttttttttttttttttttttt");
//
//
//
//
////			try {
////				Thread.sleep(100);
////			} catch (InterruptedException e) {
////				e.printStackTrace();
////			}
//		}
//		cf.getSession().write("tttt!");

		WsnProcessImpl wsnprocess = new WsnProcessImpl();
		try {
			wsnprocess.init();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Endpoint.publish(args[0], wsnprocess);

		System.out.println("bef start");
		new Thread(CrossGroupMsgForwardQueue.grtInstance()).start();
		System.out.println("aft start");

		RtMgr.getInstance();
		initNode();
		MsgQueueMgr.getInstance();


	}
    public  static  void send(){
		try{
			byte[] msg = new byte[] { 'h', 'e', 'l', 'l', 'o' };
			String ipv6Add = "FF01:0000:0000:0000:0001:2345:6789:abcd";

			Inet6Address inetAddress = (Inet6Address) Inet6Address.getByName(ipv6Add);
			System.out.println(new String(msg));
			DatagramPacket datagramPacket = new DatagramPacket(msg, msg.length,inetAddress, 7776);
			MulticastSocket multicastSocket = new MulticastSocket();
			multicastSocket.send(datagramPacket);
		}catch  (Exception exception) {

			exception.printStackTrace();

		}

	}
	public static void initNode() {
		generateNode.generateNodeList(nodeList);
		groupTableRoot = nodeList.get(0);

		for (int i = 0; i < 10000; i++) {
			if (i == 500) {
				testMap.put("500", groupTableRoot);
			} else {
				MsgSubsForm tempNode = generateNode.generateTestNode(Integer.toString(i));
				testMap.put(tempNode.topicComponent, tempNode);
			}
		}
	}

	public static void removeAimAddr(String ip) {
		for (int i = 0; i < forwardIP.size(); i++) {
			if (forwardIP.get(i).equals(ip)) {
				//System.out.println("要移除的ip是："+ip);
				forwardIP.remove(i);
				//System.out.println("移除后的forwardip为："+forwardIP.toString());
			}
		}
	}

	//获取收到的TCP消息数量
	public static int GetTCPRecievedCount() {
		return totalCount;
	}

	//获取收到的UDP消息数量
	public static int GetUDPRecievedCount() {
		return UDPtotalCount;
	}

	//将计数器置为零
	public static void SetTCPRecievedCountToZero() {
		totalCount = 0;
	}

	public static void SetUDPRecievedToZero() {
		UDPtotalCount = 0;
	}


//	public static void 

	public static void generateMsgNoticeMsg(MsgNotis msg) {
		ArrayList<String> fwIP = new ArrayList<>();

		String v6MutiAddr = Router.topicName2mutiv6Addr(msg.topicName);
		System.out.println(v6MutiAddr);
		fwIP.add(v6MutiAddr);
		ForwardMsg forwardMsg = new UDPForwardMsg(fwIP, MinaUtil.uPort, (WsnMsg) msg);
		CrossGroupMsgForwardQueue.grtInstance().enqueque(forwardMsg);
		//ArrayList<String> forwardIp = getForwardIp(topicStr, origin);
//		ArrayList<String> ret = org.apache.servicemix.wsn.router.mgr.RtMgr.calForwardGroups(msg.topicName,
//				msg.originatorGroup);
//		ArrayList<String> forwardIp = new ArrayList<String>();
//		Iterator<String> it = ret.iterator();
//		while (it.hasNext()) {
//			String itNext = it.next();
//			String addr = org.apache.servicemix.wsn.router.mgr.base.SysInfo.groupMap.get(itNext).addr;
//			forwardIp.add(addr);
//		}
//
//		System.out.println("第二级消息++++++"+forwardIp);
//
//		String splited[] = msg.topicName.split(":");
//		String ex = "";
//		boolean filtered = false;
//		for (int i = 0; i < splited.length; i++) {
//			if (i > 0)
//				ex += ":";
//			ex += splited[i];
//			WsnPolicyMsg wpm = ShorenUtils.decodePolicyMsg(ex);
//			if (wpm != null) {
//				for (TargetGroup tg : wpm.getAllGroups()) {
//					if (tg.getName().equals(org.Mina.shorenMinaTest.mgr.base.SysInfo.groupName) && tg.isAllMsg()) {
//						filtered = true;
//						break;
//					}
//				}
//			}
//		}
//
//		if (!filtered) {
//			boolean send = false;
//			for(String topic :
//				org.apache.servicemix.wsn.router.mgr.base.SysInfo.clientTable) {
//				if(isIncluded(topic, msg.topicName)) {
//					send = true;
//					break;
//				}
//			}
//			if (send && !msg.originatorAddr.equals(org.apache.servicemix.wsn.router.mgr.base.SysInfo.localAddr))// 本地有订阅并且不是消息产生者，则上交wsn。在这个地方不适合做多线程，全局变量，只读，也会出现问题
//			{
//				final org.Mina.shorenMinaTest.msg.tcp.MsgNotis mns =
//					(org.Mina.shorenMinaTest.msg.tcp.MsgNotis) msg;
//			    SendNotification SN = new SendNotification();// 调用上层wsn的接口
//				try {
//					SN.send(mns.doc);
//					org.apache.servicemix.wsn.router.mgr.RtMgr.subtract();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//			send = false;
//			for (String topic : org.apache.servicemix.wsn.router.mgr.base.SysInfo.brokerTable.keySet()) {
//				if (isIncluded(topic, msg.topicName)) {
//					send = true;
//					break;
//				}
//			}
//			if (send) {
//				spreadInLocalGroup(msg);
//			}
//
//			}
//
//		//策略库的位置，由策略库来过滤ip
//		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, 30008, (WsnMsg)msg);
//		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
		
/*		for(int i=0;i<forwardIp.size();i++){
			System.out.println(forwardIp.size());
			NioSocketConnector connector = MinaUtil.createSocketConnector();
			//ConnectFuture cf = connector.connect(new InetSocketAddress(forwardIp.get(i), SysInfo.gettPort()));//建立连接   
			ConnectFuture cf = connector.connect(new InetSocketAddress(forwardIp.get(i), 30008));//建立连接   
			cf.awaitUninterruptibly();//等待连接创建完成   
				
		    try {
			    session = cf.getSession();
				session.write(forwardMsg.getMsg());
		    }catch (Exception e) {
			    //System.out.println("建立连接失败！请检查"+ip+"节点！");
			    return;
			    // TODO: handle exception
		    }
		    session.close(true);
		}*/
	}


	public static void generateHighPrioriyMsg(highPriority msg) {
		//ArrayList<String> forwardIp = getForwardIp(topicStr, origin);
//		ArrayList<String> ret = org.apache.servicemix.wsn.router.mgr.RtMgr.calForwardGroups(msg.topicName,
//				msg.originatorGroup);
//		
//		ArrayList<String> forwardIp = new ArrayList<String>();
//		Iterator<String> it = ret.iterator();
//		while (it.hasNext()) {
//			String itNext = it.next();
//			//System.out.println("@@@@@@@@@@@@@@@@@@@@@:"+org.apache.servicemix.wsn.router.mgr.base.SysInfo.groupMap.get(itNext).addr);
//			String addr = org.apache.servicemix.wsn.router.mgr.base.SysInfo.groupMap.get(itNext).addr;
//			forwardIp.add(addr);
//		}
//		
//		String splited[] = msg.topicName.split(":");
//		String ex = "";
//		boolean filtered = false;
//		for (int i = 0; i < splited.length; i++) {
//			if (i > 0)
//				ex += ":";
//			ex += splited[i];
//			WsnPolicyMsg wpm = ShorenUtils.decodePolicyMsg(ex);
//			if (wpm != null) {
//				for (TargetGroup tg : wpm.getAllGroups()) {
//					if (tg.getName().equals(org.Mina.shorenMinaTest.mgr.base.SysInfo.groupName) && tg.isAllMsg()) {
//						filtered = true;
//						break;
//					}
//				}
//			}
//		}
//		
//		if (!filtered) {
//			boolean send = false;
//			for(String topic :  
//				org.apache.servicemix.wsn.router.mgr.base.SysInfo.clientTable) {
//				if(isIncluded(topic, msg.topicName)) {
//					send = true;
//					break;
//				}
//			}
//			if (send && !msg.originatorAddr.equals(org.apache.servicemix.wsn.router.mgr.base.SysInfo.localAddr))// 本地有订阅并且不是消息产生者，则上交wsn。在这个地方不适合做多线程，全局变量，只读，也会出现问题
//			{
//			}
//			
//			send = false;
//			for (String topic : org.apache.servicemix.wsn.router.mgr.base.SysInfo.brokerTable.keySet()) {
//				if (isIncluded(topic, msg.topicName)) {
//					send = true;
//					break;
//				}
//			}
//			if (send) {
//				spreadInLocalGroup(msg);
//			}
//			
//			}
//		
//		System.out.println(forwardIp);
		//策略库的位置，由策略库来过滤ip
		ArrayList<String> fwIP = new ArrayList<String>();
//		fwIP.add("fe80::5054:ff:feb4:4640");
//		fwIP.add("fe80::5054:ff:fe98:bec0");
//		fwIP.add("192.168.1.11");
		String v6MutiAddr = getv6MutiAddr(msg);
		fwIP.add(v6MutiAddr);
		ForwardMsg forwardMsg = new UDPForwardMsg(fwIP, MinaUtil.highUPort, (WsnMsg) msg);
		CrossGroupMsgForwardQueue.grtInstance().enqueque(forwardMsg);


//		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, 30008, (WsnMsg)msg);
//		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
		
/*		for(int i=0;i<forwardIp.size();i++){
			System.out.println(forwardIp.size());
			NioSocketConnector connector = MinaUtil.createSocketConnector();
			//ConnectFuture cf = connector.connect(new InetSocketAddress(forwardIp.get(i), SysInfo.gettPort()));//建立连接   
			ConnectFuture cf = connector.connect(new InetSocketAddress(forwardIp.get(i), 30008));//建立连接   
			cf.awaitUninterruptibly();//等待连接创建完成   
				
		    try {
			    session = cf.getSession();
				session.write(forwardMsg.getMsg());
			    final org.Mina.shorenMinaTest.msg.tcp.highPriority mns = 
					(org.Mina.shorenMinaTest.msg.tcp.highPriority) msg;
			    SendNotification SN = new SendNotification();// 调用上层wsn的接口
				try {
					SN.send(mns.doc);
					org.apache.servicemix.wsn.router.mgr.RtMgr.subtract();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
		    }catch (Exception e) {
			    //System.out.println("建立连接失败！请检查"+ip+"节点！");
			    return;
			    // TODO: handle exception
		    }
		    session.close(true);
		}*/
	}

	private static String getv6MutiAddr(WsnMsg msg) {
		//generate ipv6 mutiBrordcast Address based on topic
		String v6MutiAddr = "";

		return v6MutiAddr;

	}


	public static void generateLowPrioriyMsg(lowPriority msg) {

		ArrayList<String> fwIP = new ArrayList<String>();
//		fwIP.add("fe80::5054:ff:feb4:4640");
//		fwIP.add("fe80::5054:ff:fe98:bec0");
//		fwIP.add("192.168.1.11");
		String v6MutiAddr = getv6MutiAddr(msg);
		fwIP.add(v6MutiAddr);
		ForwardMsg forwardMsg = new UDPForwardMsg(fwIP, MinaUtil.lowUPort, (WsnMsg) msg);
		CrossGroupMsgForwardQueue.grtInstance().enqueque(forwardMsg);

		//ArrayList<String> forwardIp = getForwardIp(topicStr, origin);
//		ArrayList<String> ret = org.apache.servicemix.wsn.router.mgr.RtMgr.calForwardGroups(msg.topicName,
//				msg.originatorGroup);
//
//		ArrayList<String> forwardIp = new ArrayList<String>();
//		Iterator<String> it = ret.iterator();
//		while (it.hasNext()) {
//			String itNext = it.next();
//			//System.out.println("@@@@@@@@@@@@@@@@@@@@@:"+org.apache.servicemix.wsn.router.mgr.base.SysInfo.groupMap.get(itNext).addr);
//			String addr = org.apache.servicemix.wsn.router.mgr.base.SysInfo.groupMap.get(itNext).addr;
//			forwardIp.add(addr);
//		}
//
//		String splited[] = msg.topicName.split(":");
//		String ex = "";
//		boolean filtered = false;
//		for (int i = 0; i < splited.length; i++) {
//			if (i > 0)
//				ex += ":";
//			ex += splited[i];
//			WsnPolicyMsg wpm = ShorenUtils.decodePolicyMsg(ex);
//			if (wpm != null) {
//				for (TargetGroup tg : wpm.getAllGroups()) {
//					if (tg.getName().equals(org.Mina.shorenMinaTest.mgr.base.SysInfo.groupName) && tg.isAllMsg()) {
//						filtered = true;
//						break;
//					}
//				}
//			}
//		}
//
//		if (!filtered) {
//			boolean send = false;
//			for(String topic :
//				org.apache.servicemix.wsn.router.mgr.base.SysInfo.clientTable) {
//				if(isIncluded(topic, msg.topicName)) {
//					send = true;
//					break;
//				}
//			}
//			if (send && !msg.originatorAddr.equals(org.apache.servicemix.wsn.router.mgr.base.SysInfo.localAddr))// 本地有订阅并且不是消息产生者，则上交wsn。在这个地方不适合做多线程，全局变量，只读，也会出现问题
//			{
//			}
//
//			send = false;
//			for (String topic : org.apache.servicemix.wsn.router.mgr.base.SysInfo.brokerTable.keySet()) {
//				if (isIncluded(topic, msg.topicName)) {
//					send = true;
//					break;
//				}
//			}
//			if (send) {
//				spreadInLocalGroup(msg);
//			}
//
//			}
//
//		System.out.println(forwardIp);
//		//策略库的位置，由策略库来过滤ip
//		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, 30008, (WsnMsg)msg);
//		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
		
/*		for(int i=0;i<forwardIp.size();i++){
			System.out.println(forwardIp.size());
			NioSocketConnector connector = MinaUtil.createSocketConnector();
			//ConnectFuture cf = connector.connect(new InetSocketAddress(forwardIp.get(i), SysInfo.gettPort()));//建立连接   
			ConnectFuture cf = connector.connect(new InetSocketAddress(forwardIp.get(i), 30008));//建立连接   
			cf.awaitUninterruptibly();//等待连接创建完成   
				
		    try {
			    session = cf.getSession();
				session.write(forwardMsg.getMsg());
			    final org.Mina.shorenMinaTest.msg.tcp.lowPriority mns = 
					(org.Mina.shorenMinaTest.msg.tcp.lowPriority) msg;
			    SendNotification SN = new SendNotification();// 调用上层wsn的接口
				try {
					SN.send(mns.doc);
					org.apache.servicemix.wsn.router.mgr.RtMgr.subtract();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
		    }catch (Exception e) {
			    //System.out.println("建立连接失败！请检查"+ip+"节点！");
			    return;
			    // TODO: handle exception
		    }
		    session.close(true);
		}*/
	}

	public static boolean isIncluded(String mother, String child) {
		String splited[] = mother.split(":");
		String spliCh[] = child.split(":");
		if (spliCh.length < splited.length)
			return false;
		String temp = spliCh[0];
		for (int i = 1; i < splited.length; i++)
			temp += ":" + spliCh[i];
		if (temp.equals(mother))
			return true;
		else
			return false;
	}


	public static void spreadInLocalGroup(Object obj) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream doos = null;
		DatagramSocket s = null;
		DatagramPacket p = null;
		byte[] buf = null;

		try {
			doos = new ObjectOutputStream(baos);
			s = new DatagramSocket();
			doos.writeObject(obj);
			buf = baos.toByteArray();

			// multicast it in this group
			p = new DatagramPacket(buf, buf.length,
					InetAddress.getByName(org.apache.servicemix.wsn.router.mgr.base.SysInfo.multiAddr),
					org.apache.servicemix.wsn.router.mgr.base.SysInfo.uPort);
			s.send(p);

			//System.out.println("组播地址是:"+org.apache.servicemix.wsn.router.mgr.base.SysInfo.multiAddr);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
