package org.Mina.shorenMinaTest.mgr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.Mina.shorenMinaTest.mgr.base.AState;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.msg.tcp.MsgNotis;
import org.Mina.shorenMinaTest.msg.tcp.highPriority;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.servicemix.wsn.push.SendNotification;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;

public class RepState extends AState {
	private static Log log = LogFactory.getLog(RepState.class);
	RtMgr mgr;

	public RtMgr getMgr() {

		return mgr;
	}

	public void setMgr(RtMgr mgr) {
		this.mgr = mgr;
	}

	public RepState(RtMgr mgr) {
		this.mgr = mgr;

	}

	/*
	 * public void processMsg(IoSession session, WsnMsg msg) {
	 * msg.processRepMsg(session); }
	 */

	public void processMsg(IoSession session, WsnMsg msg) {

		// log.info("to processNotisMsg");
		// System.out.println("process notis msg");

		if (msg instanceof MsgNotis) {

			final org.Mina.shorenMinaTest.msg.tcp.MsgNotis mns = (org.Mina.shorenMinaTest.msg.tcp.MsgNotis) msg;

			mns.sender = localAddr;

			String splited[] = mns.topicName.split(":");
			String ex = "";
			boolean filtered = false;
			for (int i = 0; i < splited.length; i++) {
				if (i > 0)
					ex += ":";
				ex += splited[i];
				WsnPolicyMsg wpm = ShorenUtils.decodePolicyMsg(ex);
				if (wpm != null) {
					for (TargetGroup tg : wpm.getAllGroups()) {
						if (tg.getName().equals(groupName) && tg.isAllMsg()) {
							filtered = true;
							break;
						}
					}
				}
			}

			if (!filtered) {
				boolean send = false;
				for (String topic : org.apache.servicemix.wsn.router.mgr.base.SysInfo.clientTable) {
					if (this.isIncluded(topic, mns.topicName)) {
						send = true;
						break;
					}
				}
				if (send
						&& !mns.originatorAddr
								.equals(org.apache.servicemix.wsn.router.mgr.base.SysInfo.localAddr))// 本地有订阅并且不是消息产生者，则上交wsn。在这个地方不适合做多线程，全局变量，只读，也会出现问题
				{

					try {
						SendNotification SN = new SendNotification();// 调用上层wsn的接口
						SN.send(mns.doc);
						org.apache.servicemix.wsn.router.mgr.RtMgr.subtract();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						log.warn(e1);
					}
				}

				send = false;
				for (String topic : org.apache.servicemix.wsn.router.mgr.base.SysInfo.brokerTable
						.keySet()) {
					if (this.isIncluded(topic, mns.topicName)) {
						send = true;
						break;
					}
				}
				if (send) {
					this.spreadInLocalGroup(mns);
				}

			}

		} else if (msg instanceof highPriority) {

			final org.Mina.shorenMinaTest.msg.tcp.highPriority mns = (org.Mina.shorenMinaTest.msg.tcp.highPriority) msg;

			mns.sender = localAddr;

			String splited[] = mns.topicName.split(":");
			String ex = "";
			boolean filtered = false;
			for (int i = 0; i < splited.length; i++) {
				if (i > 0)
					ex += ":";
				ex += splited[i];
				WsnPolicyMsg wpm = ShorenUtils.decodePolicyMsg(ex);
				if (wpm != null) {
					for (TargetGroup tg : wpm.getAllGroups()) {
						if (tg.getName().equals(groupName) && tg.isAllMsg()) {
							filtered = true;
							break;
						}
					}
				}
			}

			if (!filtered) {
				boolean send = false;
				for (String topic : org.apache.servicemix.wsn.router.mgr.base.SysInfo.clientTable) {
					if (this.isIncluded(topic, mns.topicName)) {
						send = true;
						break;
					}
				}
				if (send
						&& !mns.originatorAddr
								.equals(org.apache.servicemix.wsn.router.mgr.base.SysInfo.localAddr))// 本地有订阅并且不是消息产生者，则上交wsn。在这个地方不适合做多线程，全局变量，只读，也会出现问题
				{

					try {
						SendNotification SN = new SendNotification();// 调用上层wsn的接口
						SN.send(mns.doc);
						org.apache.servicemix.wsn.router.mgr.RtMgr.subtract();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						log.warn(e1);
					}
				}

				send = false;
				for (String topic : org.apache.servicemix.wsn.router.mgr.base.SysInfo.brokerTable
						.keySet()) {
					if (this.isIncluded(topic, mns.topicName)) {
						send = true;
						break;
					}
				}
				if (send) {
					this.spreadInLocalGroup(mns);
				}

			}
		} else if (msg instanceof org.Mina.shorenMinaTest.msg.tcp.lowPriority) {

			final org.Mina.shorenMinaTest.msg.tcp.lowPriority mns = (org.Mina.shorenMinaTest.msg.tcp.lowPriority) msg;

			mns.sender = localAddr;

			String splited[] = mns.topicName.split(":");
			String ex = "";
			boolean filtered = false;
			for (int i = 0; i < splited.length; i++) {
				if (i > 0)
					ex += ":";
				ex += splited[i];
				WsnPolicyMsg wpm = ShorenUtils.decodePolicyMsg(ex);
				if (wpm != null) {
					for (TargetGroup tg : wpm.getAllGroups()) {
						if (tg.getName().equals(groupName) && tg.isAllMsg()) {
							filtered = true;
							break;
						}
					}
				}
			}

			if (!filtered) {
				boolean send = false;
				for (String topic : org.apache.servicemix.wsn.router.mgr.base.SysInfo.clientTable) {
					if (this.isIncluded(topic, mns.topicName)) {
						send = true;
						break;
					}
				}
				if (send
						&& !mns.originatorAddr
								.equals(org.apache.servicemix.wsn.router.mgr.base.SysInfo.localAddr))// 本地有订阅并且不是消息产生者，则上交wsn。在这个地方不适合做多线程，全局变量，只读，也会出现问题
				{

					try {
						SendNotification SN = new SendNotification();// 调用上层wsn的接口
						SN.send(mns.doc);
						org.apache.servicemix.wsn.router.mgr.RtMgr.subtract();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						log.warn(e1);
					}
				}

				send = false;
				for (String topic : org.apache.servicemix.wsn.router.mgr.base.SysInfo.brokerTable
						.keySet()) {
					if (this.isIncluded(topic, mns.topicName)) {
						send = true;
						break;
					}
				}
				if (send) {
					this.spreadInLocalGroup(mns);
				}
			}
		}
		msg.processRepMsg(session);
	}

	public boolean isIncluded(String mother, String child) {
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

	public void spreadInLocalGroup(Object obj) {
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
			p = new DatagramPacket(
					buf,
					buf.length,
					InetAddress
							.getByName(org.apache.servicemix.wsn.router.mgr.base.SysInfo.multiAddr),
					org.apache.servicemix.wsn.router.mgr.base.SysInfo.uPort);
			s.send(p);

			System.out
					.println("组播地址是:"
							+ org.apache.servicemix.wsn.router.mgr.base.SysInfo.multiAddr);

		} catch (IOException e) {
			e.printStackTrace();
			log.warn(e);
		}
	}

}
