package org.Mina.shorenMinaTest.handlers;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.msg.tcp.MsgNotis;
import org.Mina.shorenMinaTest.msg.tcp.highPriority;
import org.Mina.shorenMinaTest.msg.tcp.lowPriority;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.TCPForwardMsg;
import org.Mina.shorenMinaTest.router.searchRoute;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class GenerateMsg {

	private static ArrayList<String> getForwardIp() {
		return Start.forwardIP = searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);
	}

	@SuppressWarnings("unused")
	private static ArrayList<String> getForwardIp(String topicStr, String origin) {
		return Start.forwardIP = searchRoute.calForwardIP(topicStr, origin, Start.testMap);
	}

	static void generateMsg(WsnMsg msg, String topicStr, String origin) {
		//ArrayList<String> forwardIp = getForwardIp(topicStr, origin);
		ArrayList<String> forwardIp = new ArrayList<String>();
		forwardIp.add("10.109.253.15");
		System.out.println(forwardIp);
		IoSession session = null;
		//????????????????????????????ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, 30008, msg);
		//MsgQueueMgr.addTCPMsgInQueue(forwardMsg);

		for (int i = 0; i < forwardIp.size(); i++) {
			System.out.println(forwardIp.size());
			NioSocketConnector connector = MinaUtil.createSocketConnector();
			//ConnectFuture cf = connector.connect(new InetSocketAddress(forwardIp.get(i), SysInfo.gettPort()));//????????   
			ConnectFuture cf = connector.connect(new InetSocketAddress(forwardIp.get(i), 30008));//????????   
			cf.awaitUninterruptibly();//????????????????   

			try {
				session = cf.getSession();


				session.write(forwardMsg.getMsg());
				
			    

			    
			    /*final org.Mina.shorenMinaTest.msg.tcp.MsgNotis mns = 
					(org.Mina.shorenMinaTest.msg.tcp.MsgNotis) msg;
			    SendNotification SN = new SendNotification();// ????????wsn??????
				try {
					SN.send(mns.doc);
					org.apache.servicemix.wsn.router.mgr.RtMgr.subtract();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

			} catch (Exception e) {
				//System.out.println("????????????????????"+ip+"??????");
				return;
				// TODO: handle exception
			}
			session.close(true);
		}
	}

	static void generateMsg(String message) {
		IoSession session = null;

		NioSocketConnector connector = MinaUtil.createSocketConnector();
		//ConnectFuture cf = connector.connect(new InetSocketAddress(forwardIp.get(i), SysInfo.gettPort()));//????????   
		ConnectFuture cf = connector.connect(new InetSocketAddress("10.109.253.40", 30008));//????????   
		cf.awaitUninterruptibly();//????????????????   

		try {
			session = cf.getSession();
			WriteFuture future = session.write(message);


		} catch (Exception e) {
			//System.out.println("????????????????????"+ip+"??????");
			return;
			// TODO: handle exception
		}

	}

	public static void main(String args[]) {
		//	Start.initNode();


		MsgNotis mn = new MsgNotis();
		mn.sender = "10.109.253.21";
		mn.originatorGroup = "G9";
		mn.originatorAddr = "10.109.253.21";
		mn.topicName = "all:alarm:alarm1";
		mn.doc = "<wsnt:NotificationMessage xmlns:wsnt=\"http://docs.oasis-open.org/wsn/b-2\"><wsnt:Topic Dialect=\"http://docs.oasis-open.org/wsn/t-1/TopicExpression/Simple\">all:alarm:alarm1</wsnt:Topic><wsnt:Package><Identification>10481519:1</Identification><Fragment>0-0-1-0</Fragment></wsnt:Package><wsnt:Message>This sentence has 64B : a  message about topic all:alarm:alarm1!This sentence has 64B : a  message about topic all:alarm:alarm1!This sentence has 64B : a  message about topic all:alarm:alarm1!This sentence has 64B : a  message about topic all:alarm:alarm1!This sentence has 64B : a  message about topic all:alarm:alarm1!</wsnt:Message></wsnt:NotificationMessage>";
		mn.sendDate = "2014.02.20";

		highPriority hp = new highPriority();
		hp.sender = "10.109.253.21";
		hp.originatorGroup = "G9";
		hp.originatorAddr = "10.109.253.21";
		hp.topicName = "all:alarm:alarm1";
		hp.doc = "<wsnt:NotificationMessage xmlns:wsnt=\"http://docs.oasis-open.org/wsn/b-2\"><wsnt:Topic Dialect=\"http://docs.oasis-open.org/wsn/t-1/TopicExpression/Simple\">all:alarm:alarm1</wsnt:Topic><wsnt:Package><Identification>10481519:1</Identification><Fragment>0-0-1-0</Fragment></wsnt:Package><wsnt:Message>This sentence has 64B : a  message about topic all:alarm:alarm1!This sentence has 64B : a  message about topic all:alarm:alarm1!This sentence has 64B : a  message about topic all:alarm:alarm1!This sentence has 64B : a  message about topic all:alarm:alarm1!This sentence has 64B : a  message about topic all:alarm:alarm1!</wsnt:Message></wsnt:NotificationMessage>";
		hp.sendDate = "2014.02.20";

		lowPriority lp = new lowPriority();
		lp.sender = "10.109.253.21";
		lp.originatorGroup = "G9";
		lp.originatorAddr = "10.109.253.21";
		lp.topicName = "all:alarm:alarm1";
		lp.doc = "<wsnt:NotificationMessage xmlns:wsnt=\"http://docs.oasis-open.org/wsn/b-2\"><wsnt:Topic Dialect=\"http://docs.oasis-open.org/wsn/t-1/TopicExpression/Simple\">all:alarm:alarm1</wsnt:Topic><wsnt:Package><Identification>10481519:1</Identification><Fragment>0-0-1-0</Fragment></wsnt:Package><wsnt:Message>This sentence has 64B : a  message about topic all:alarm:alarm1!This sentence has 64B : a  message about topic all:alarm:alarm1!This sentence has 64B : a  message about topic all:alarm:alarm1!This sentence has 64B : a  message about topic all:alarm:alarm1!This sentence has 64B : a  message about topic all:alarm:alarm1!</wsnt:Message></wsnt:NotificationMessage>";
		lp.sendDate = "2014.02.20";


		generateMsg(mn, "500:3:6:10:15:20:26", "m");
		//generateMsg(hp,"500:3:6:10:15:20:26","m");
		generateMsg(mn, "500:3:6:10:15:20:26", "m");

		//generateMsg(mn.doc+mn.doc2);


	}
}
