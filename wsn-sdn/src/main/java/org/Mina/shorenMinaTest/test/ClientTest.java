package org.Mina.shorenMinaTest.test;


import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.mgr.RtMgr;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.msg.tcp.MsgInsert;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ClientTest {

	private static final Log log = LogFactory.getLog(ClientTest.class);
	static NioSocketConnector connector = MinaUtil.createSocketConnector();
	static ConnectFuture cf = connector.connect(new InetSocketAddress("10.109.253.41", 30001));//建立连接
	static long t1 = 0;
	static long t2 = 0;
	
	/*static NioDatagramConnector connector2 = MinaUtil.createDatagramConnector();	
	static ConnectFuture df = connector2.connect(new InetSocketAddress("10.108.166.217", 30002));//建立连接
	*/
	/*static NioSocketConnector connector3 = MinaUtil.createSocketConnector();	
	static ConnectFuture cf2 = connector3.connect(new InetSocketAddress("10.108.166.217", 30003));//建立连接
	
	static NioDatagramConnector connector4 = MinaUtil.createDatagramConnector();	
	static ConnectFuture df2 = connector4.connect(new InetSocketAddress("10.108.166.217", 30004));//建立连接
*/

	//static NioSocketConnector connector1 = MinaUtil.createSocketConnector();	
	//static ConnectFuture cf1 = connector1.connect(new InetSocketAddress("10.109.253.17", 30001));//建立连接
	static int t = 0;
	private static List<WsnMsg> msgList = MsgGenerator.generateMsgs();
	private static int currentAccount = 0;
	RtMgr mgr = RtMgr.getInstance();

	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		cf.awaitUninterruptibly();//等待连接创建完成
		//cf1.awaitUninterruptibly();//等待连接创建完成
		//df.awaitUninterruptibly();
		//df2.awaitUninterruptibly();
		/*
		NioSocketConnector connector2 = MinaUtil.createSocketConnector();
		ConnectFuture cf2 = connector2.connect(new InetSocketAddress("10.108.164.66", 30001));//建立连接
		cf2.awaitUninterruptibly();//等待连接创建完成

		NioSocketConnector connector3 = MinaUtil.createSocketConnector();
		ConnectFuture cf3 = connector3.connect(new InetSocketAddress("10.108.164.66", 30001));//建立连接
		cf3.awaitUninterruptibly();//等待连接创建完成
		*/
//		NioDatagramConnector connector = MinaUtil.createDatagramConnector();
		//	ConnectFuture cf = connector.connect(new InetSocketAddress(mgr.localAddr, mgr.uPort));//建立连接

		//	NioDatagramConnector connector = MinaUtil.CreatBoardcast();
		//	ConnectFuture cf = connector.connect(new InetSocketAddress("255.255.255.255", 9123));
		for (int r = 0; r < 1; r++) {


			for (int j = 0; j < 30; j++) {
				System.out.println("---------------------------第" + (j + 1) + "次发送----------------------");
				for (int i = 0; i < msgList.size(); i++) {
					cf.getSession().write(msgList.get(i));
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//TCP通道
	public static void TCPSessionOpened(IoSession session) {

	}

	public static void TCPSessionCreated(IoSession session) {
		log.info("新客户端连接");
		log.info("session" + session.toString() +
				"###" + "create time:" + System.currentTimeMillis());

		MinaUtil.inTCPTotalCount();
		MinaUtil.iniSessionReferance(session);
	}

	public static void TCPMessageReceived(IoSession session, Object message) {

		if (message.toString().equals("5First")) {
		}

		if (message instanceof WsnMsg) {
			WsnMsg msg = (WsnMsg) message;
		} else {

		}
	}

	public static void TCPMessageSent(IoSession session, Object message) {

	}

	public static void TCPMessageIdle(IoSession session, IdleStatus status) {

	}

	public static void TCPSessionClosed(IoSession session) {
		System.out.println("one Clinet Disconnect !");

		//源码
		MinaUtil.deTCPTotalCount();
		//从保存的通道中删除
		MsgQueueMgr.getDest_session().remove(session.getAttribute("addr"));
		log.info("sessionClosed:" + session.toString());

		ConcurrentHashMap<String, IoSession> map = MsgQueueMgr.getDest_session();
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			IoSession se = map.get(key);
			log.info("key:" + key);
			log.info("value:" + session.toString());
		}
		//关闭连接
		session.getService().dispose();
	}

	//udp通道
	public static void UDPSessionOpened(IoSession session) {

	}

	public static void UDPSessionCreated(IoSession session) {
		//	session.write("send message...");
		MsgInsert mi = MsgGenerator.createMsgInsert();
		session.write(mi);

		System.out.println("新客户端连接");
		MinaUtil.inUDPTotalCount();
		MinaUtil.iniSessionReferance(session);
	}

	public static void UDPMessageReceived(IoSession session, Object message) {
		if (message instanceof WsnMsg) {
			WsnMsg msg = (WsnMsg) message;
			RtMgr.getInstance().getState().processMsg(session, msg);
		} else
			System.out.println("receive message:" + message.toString());
	}

	public static void UDPMessageIdle(IoSession session, IdleStatus status) {
		//System.out.println("连接空闲");
		System.out.println(new Date(System.currentTimeMillis()).toString());
		//    session.close(true);  //close right now，关闭通道
	}

	public static void UDPSessionClosed(IoSession session) {
		System.out.println("one Clinet Disconnect !");
		MinaUtil.deUDPTotalCount();
		MsgQueueMgr.getDest_session().remove(session.getAttribute("addr"));
		//关闭连接
		session.getService().dispose();
	}

	public static void UDPMessageSent(IoSession session, Object message) {

	}
}
