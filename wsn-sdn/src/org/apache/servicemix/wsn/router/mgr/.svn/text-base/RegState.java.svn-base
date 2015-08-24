package org.apache.servicemix.wsn.router.mgr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.wsn.push.SendNotification;
import org.apache.servicemix.wsn.router.detection.IDt;
import org.apache.servicemix.wsn.router.mgr.base.AState;
import org.apache.servicemix.wsn.router.mgr.MsgNotis;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.msg.tcp.LSA;
import org.apache.servicemix.wsn.router.msg.tcp.MsgAdminChange;
import org.apache.servicemix.wsn.router.msg.tcp.MsgGroupJunk;
import org.apache.servicemix.wsn.router.msg.tcp.MsgInfoChange;
import org.apache.servicemix.wsn.router.msg.tcp.MsgJoinGroup;
import org.apache.servicemix.wsn.router.msg.tcp.MsgJoinGroup_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupMemberSubscriptions;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupMemberSubscriptions_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgNewRep;
import org.apache.servicemix.wsn.router.msg.tcp.MsgSetAddr;
import org.apache.servicemix.wsn.router.msg.tcp.MsgSetConf;
import org.apache.servicemix.wsn.router.msg.tcp.MsgSynSubs;
import org.apache.servicemix.wsn.router.msg.tcp.PolicyDB;
import org.apache.servicemix.wsn.router.msg.tcp.UpdateTree;
import org.apache.servicemix.wsn.router.msg.udp.MsgHello;
import org.apache.servicemix.wsn.router.msg.udp.MsgLost;
import org.apache.servicemix.wsn.router.msg.udp.MsgNewBroker;
import org.apache.servicemix.wsn.router.msg.udp.MsgSubs;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;

public class RegState extends AState {
	private static Log log = LogFactory.getLog(RegState.class);
	private RtMgr mgr;

	private IDt dt;

	private Timer timer;

	private Object addLSA;

	private Object addSubs;

	public RegState(RtMgr mgr, IDt dt) {
		this.mgr = mgr;
		this.dt = dt;
		timer = new Timer();
		addLSA = new Object();
		addSubs = new Object();
	}

	@Override
	public void join() {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		Socket s = null;

		MsgJoinGroup mjg = new MsgJoinGroup();
		mjg.name = groupName;
		mjg.tPort = tPort;

		MsgJoinGroup_ mjg_ = null;
		for (int i = 0; i < joinTimes && !joinOK; i++) {
			try {

				s = new Socket(rep.addr, rep.tPort);
				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());
				oos.writeObject(mjg);
				mjg_ = (MsgJoinGroup_) ois.readObject();

				oos.close();
				ois.close();
				s.close();

			} catch (IOException e) {
				System.out.println("can't connect " + rep.addr);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				log.warn(e);
			}

			if (mjg_ != null) {

				System.out.println("successfully join into group:" + mjg.name);
				log.info("successfully join into group:" + mjg.name);

				id = mjg_.id;

				brokerTable.putAll(mjg_.brokerTab);
				fellows.putAll(mjg_.fellows);
				neighbors.addAll(mjg_.neighbors);
				groupMap.putAll(mjg_.groupMap);
				for (LSA lsa : mjg_.lsdb.values()) {
					this.addLSAToLSDB(lsa);
				}
				setPolicyTime(mjg_.pdb.time);
				ShorenUtils.deleteAllPolicyMsg();
				for (WsnPolicyMsg msg : mjg_.pdb.pdb) {
					ShorenUtils.encodePolicyMsg(msg);
				}

				joinOK = true;

				this.setClock(false);

				// send heart to representative
				dt.addTarget(groupName);
			}
		}

		// 加入不成功，说明管理者处保存的代表实际已不再存在，所以通知其删去，并转换成rep
		if (!joinOK) {
			System.out
					.println("rep of group " + groupName + " no longer exist");
			log.info("rep of group " + groupName + " no longer exist");
			try {

				s = new Socket(adminAddr, adminPort);
				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());

				MsgGroupJunk mrl = new MsgGroupJunk();
				mrl.name = groupName;
				oos.writeObject(mrl);

				oos.close();
				ois.close();
				s.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.warn(e);
			}

			mgr.setState(mgr.getRepState());
			System.out.println("switch to rep state");
			log.info("switch to rep state");
		}

	}

	@Override
	public void setClock(boolean isRep) {
		timer.schedule(new SynTask(), RtMgr.synPeriod * 60 * 1000,
				RtMgr.synPeriod * 60 * 1000);
	}

	class SynTask extends TimerTask {

		@Override
		public void run() {
			synSubs();
		}

	}

	// judge if the mother is the mother of the child
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

	public LSA addTopicsToLSA(LSA lsa, ArrayList<String> topics, int type) {
		// cache of the topics that need to be deleted from the subsTopics
		ArrayList<String> listAdd = new ArrayList<String>();
		// cache of the topics that need to be deleted from the cancelTopics
		ArrayList<String> listCan = new ArrayList<String>();

		if (lsa == null)
			lsa = new LSA();
		if (type == 0) {
			for (String ss : topics) {
				boolean ad = true;
				for (String sub : lsa.subsTopics) {
					if (this.isIncluded(sub, ss))
						ad = false;
					else if (this.isIncluded(ss, sub))
						listAdd.add(sub);
				}
				// if the new topic ss is not included by subsTopics, add it to
				// the list and remove the children of the new topic in
				// cancelTopics
				if (ad) {
					lsa.subsTopics.add(ss);
					for (String ca : lsa.cancelTopics) {
						if (this.isIncluded(ss, ca)) {
							listCan.add(ca);
						}
					}
					if (!listCan.isEmpty()) {
						lsa.cancelTopics.removeAll(listCan);
						listCan.clear();
					}
				}

				if (!listAdd.isEmpty()) {
					lsa.subsTopics.removeAll(listAdd);
					listAdd.clear();
				}
			}
		} else {
			for (String ss : topics) {
				boolean ad = true;
				for (String can : lsa.cancelTopics) {
					if (this.isIncluded(can, ss))
						ad = false;
					else if (this.isIncluded(ss, can))
						listCan.add(can);
				}
				// if the new topic ss is not included by subsTopics, add it to
				// the list and remove the children of the new topic in
				// cancelTopics
				if (ad) {
					lsa.cancelTopics.add(ss);
					for (String ca : lsa.subsTopics)
						if (this.isIncluded(ss, ca))
							listAdd.add(ca);
					if (!listAdd.isEmpty()) {
						lsa.subsTopics.removeAll(listAdd);
						listAdd.clear();
					}
				}

				if (!listCan.isEmpty()) {
					lsa.subsTopics.removeAll(listCan);
					listCan.clear();
				}
			}
		} 
		return lsa;
	}

	@Override
	public boolean addLSAToLSDB(LSA lsa) {
		synchronized (addLSA) {
			if (lsa.originator.equals(groupName) && lsa.seqNum > lsaSeqNum) {
				lsaSeqNum = lsa.seqNum;
			}
			if (lsdb.containsKey(lsa.originator)) {
				LSA old = lsdb.get(lsa.originator);

				System.out
						.println("add lsa from " + lsa.originator + "to lsdb");
				if (lsa.syn == 1) { // 同步LSA
					lsa.sendTime = System.currentTimeMillis();

					if (old.seqNum < lsa.seqNum) {
						lsdb.remove(old.originator);
						lsdb.put(lsa.originator, lsa);
					} else {
						old.seqNum = lsa.seqNum;
						old.sendTime = lsa.sendTime;
						old.distBtnNebrs = lsa.distBtnNebrs;
						old.subsTopics.addAll(lsa.subsTopics);
					}

					if (lsa.originator.equals(groupName)) {
						neighbors.clear();
						neighbors.addAll(lsa.distBtnNebrs.keySet());
					}

					mgr.DeleteAllSubsOfGroup(lsa.originator);
					mgr.AddSubsByGroup(lsa.originator, old.subsTopics);

					return true;
				} else {
					old.seqNum = lsa.seqNum;
					old.sendTime = System.currentTimeMillis();

					// cancel topics from lsa to lsdb
					if (!lsa.cancelTopics.isEmpty()) {
						old = addTopicsToLSA(old, lsa.cancelTopics, 1);
						mgr.DeleteSubsByGroup(lsa.originator, lsa.cancelTopics);
					} // if

					old = addTopicsToLSA(old, lsa.subsTopics, 0);
				} // else lsa.syn!=1
					// the originator of lsa lost its neighbor
				if (!lsa.lostGroup.isEmpty()) {
					for (String lost : lsa.lostGroup) {
						this.deleteLineFromLSDB(lost, lsa.originator);
						if (lsa.originator.equals(groupName)
								&& lsa.originator.equals(groupName)
								&& neighbors.contains(lost)) {
							neighbors.remove(lost);
						}
					}
				}

				// refresh the distance between the originator of lsa and its
				// neighbors
				for (String nei : lsa.distBtnNebrs.keySet()) {
					if (lsa.originator.equals(groupName)) {
						if (!neighbors.contains(nei)) {
							neighbors.add(nei);
						}
					}
					if (old.distBtnNebrs.containsKey(nei)) {
						if (!(old.distBtnNebrs.get(nei).dist == lsa.distBtnNebrs
								.get(nei).dist)) {
							old.distBtnNebrs.get(nei).dist = lsa.distBtnNebrs
									.get(nei).dist;
						}
					} else {
						old.distBtnNebrs.put(nei, lsa.distBtnNebrs.get(nei));
					} // else
				} // if
			} else {
				if (lsa.cancelTopics.isEmpty() && lsa.distBtnNebrs.isEmpty()
						&& lsa.subsTopics.isEmpty())
					return false;

				if (lsa.originator.equals(groupName)) {
					neighbors.clear();
					neighbors.addAll(lsa.distBtnNebrs.keySet());
				}
				System.out.println("new lsa from " + lsa.originator);
				lsa.sendTime = System.currentTimeMillis();
				lsdb.put(lsa.originator, lsa);
			}

			mgr.AddSubsByGroup(lsa.originator, lsa.subsTopics);

			return true;
		}
	}

	public void deleteLineFromLSDB(String lostGroup, String sender) {
		// delete sender from lostGroup's LSA
		System.out.println("delete " + lostGroup + " sender :" + sender);
		if (RtMgr.lsdb.containsKey(sender)) {
			LSA sd = RtMgr.lsdb.get(sender);
			for (String s : sd.distBtnNebrs.keySet())
				if (s.equals(lostGroup)) {
					sd.distBtnNebrs.remove(s);
					break;
				}
		}
		// delete lostGroup from sender's LSA
		if (RtMgr.lsdb.containsKey(lostGroup)) {
			LSA sd = RtMgr.lsdb.get(lostGroup);
			for (String s : sd.distBtnNebrs.keySet())
				if (s.equals(sender)) {
					sd.distBtnNebrs.remove(s);
					break;
				}
			;
			if (sd.distBtnNebrs.isEmpty()) {
				System.out.println("group " + lostGroup + " is empty in lsdb");
				// delete the group's subscriptions and recalculate these topics
				mgr.DeleteAllSubsOfGroup(lostGroup);
				RtMgr.lsdb.remove(lostGroup);
				if (groupMap.containsKey(lostGroup))
					groupMap.remove(lostGroup);
			}
		}
	}

	@Override
	public void sendSbp(Object msg) {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		DatagramSocket s = null;

		MsgSubs mss = (MsgSubs) msg;
		mss.originator = localAddr;
		mss.sender = localAddr;

		try {

			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);

			oos.writeObject(mss);
			byte[] buf = baos.toByteArray();

			DatagramPacket p = new DatagramPacket(buf, buf.length,
					InetAddress.getByName(multiAddr), uPort);
			s = new DatagramSocket();
			s.send(p);

		} catch (IOException e) {
			e.printStackTrace();
			log.warn(e);
		}
	}

	@Override
	public void lost(String indicator) {
		System.out.println("lost: " + indicator);
		log.info("lost: " + indicator);
		String repAddr = rep.addr;

		fellows.remove(repAddr);
		// 将id值最小的代理记录在tmp中
		BrokerUnit tmp = null;
		long tmpId = id; 
		for (BrokerUnit b : fellows.values()) {
			if (tmpId > b.id) {
				tmpId = b.id;
				tmp = b;
			}
		}
		
		for (String t : brokerTable.keySet()) {
			if (brokerTable.get(t).contains(repAddr)) {
				brokerTable.get(t).remove(repAddr);
				if (brokerTable.get(t).isEmpty()) {
					brokerTable.remove(t);
				}
			}
		}

		if (tmp == null) {
			// 本代理成为新的代表
			
			// add target to heart detection
			for (String ad : fellows.keySet()) {
				dt.addTarget(ad);
			}

			for (String n : neighbors) {
				dt.addTarget(n);
			}

			groupMap.get(groupName).addr = localAddr;
			groupMap.get(groupName).tPort = tPort;
			groupMap.get(groupName).uPort = uPort;
			groupMap.get(groupName).id = id;

			MsgNewRep mnr = new MsgNewRep();
			mnr.name = groupName;
			mnr.addr = localAddr;
			mnr.tPort = tPort;
			mnr.uPort = uPort;
			mnr.id = id;
			mnr.sender = groupName;
			mnr.netmask = localNetmask;

			Socket s = null;
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;

			// notify administrator
			try {
				s = new Socket(adminAddr, adminPort);
				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());
				oos.writeObject(mnr);

				ois.close();
				oos.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
				log.warn(e);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream doos = null;
			DatagramSocket d = null;
			DatagramPacket p = null;
			byte[] buf = null;

			try {
				doos = new ObjectOutputStream(baos);
				d = new DatagramSocket();
				doos.writeObject(mnr);
				buf = baos.toByteArray();

				// multicast it in this group
				p = new DatagramPacket(buf, buf.length,
						InetAddress.getByName(multiAddr), uPort);
				d.send(p);
			} catch (IOException e) {
				e.printStackTrace();
				log.warn(e);
			}

			timer.cancel();
			mgr.setState(mgr.getRepState());// change state
			mgr.setClock(false);
		}
		else {
			rep = tmp;

			dt.addTarget(groupName);
		}
	}

	@Override
	public void processUdpMsg(Object msg) {
		if (msg instanceof MsgHello) {

			dt.onMsg(msg);
			System.out.println("hello from " + ((MsgHello) msg).indicator);
			log.info("hello from " + ((MsgHello) msg).indicator);

		} else if (msg instanceof MsgAdminChange) {

			MsgAdminChange mac = (MsgAdminChange) msg;
			adminAddr = mac.NewAdminAddr;
			System.out.println("AdminAddress change to:" + adminAddr);
			log.info("AdminAddress change to：" + adminAddr);

		} else if (msg instanceof MsgNewBroker) {// 如果本集群有新的成员

			MsgNewBroker mnm = (MsgNewBroker) msg;
			if (mnm.broker.addr.equals(localAddr)) {
				return;
			}
			System.out.println("new broker: " + mnm.broker.addr);
			log.info("new broker: " + mnm.broker.addr);

			if (mnm.name.equals(groupName)) {

				fellows.put(mnm.broker.addr, mnm.broker);
			}

		} else if (msg instanceof MsgNewRep) {

			MsgNewRep mnr = (MsgNewRep) msg;
			if (mnr.addr.equals(localAddr)) {
				return;
			}
			System.out.println("group: " + mnr.name + " new rep" + mnr.addr);
			log.info("group: " + mnr.name + " new rep");

			GroupUnit g = groupMap.get(mnr.name);
			if (g == null) {
				g = new GroupUnit();
				groupMap.put(g.name, g);
			}
			g.addr = mnr.addr;
			g.tPort = mnr.tPort;
			g.uPort = mnr.uPort;
			g.id = mnr.id;
			g.date = new Date();

			if (g.name.equals(groupName)) {
				for (String t : brokerTable.keySet()) {
					if (brokerTable.get(t).contains(rep.addr)) {
						brokerTable.get(t).remove(rep.addr);
						if (brokerTable.get(t).isEmpty()) {
							brokerTable.remove(t);
						}
					}
				}
				
				rep.addr = g.addr;
				rep.tPort = g.tPort;

				ObjectOutputStream oos = null;
				ObjectInputStream ois = null;
				Socket s = null;

				MsgJoinGroup mjg = new MsgJoinGroup();
				mjg.name = groupName;
				mjg.tPort = tPort;
				MsgJoinGroup_ mjg_ = null;

				for (int i = 0; i < joinTimes; i++) {
					try {

						s = new Socket(rep.addr, rep.tPort);
						oos = new ObjectOutputStream(s.getOutputStream());
						ois = new ObjectInputStream(s.getInputStream());
						oos.writeObject(mjg);
						mjg_ = (MsgJoinGroup_) ois.readObject();
						neighbors.clear();
						neighbors.addAll(mjg_.neighbors);

						oos.close();
						ois.close();
						s.close();

					} catch (IOException e) {
						System.out.println("can't connect " + rep.addr);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						log.warn(e);
					}
				}
				
				
			}

		} else if (msg instanceof MsgSubs) {

			MsgSubs mss = (MsgSubs) msg;
			
			log.info("subs: " + mss.originator);

			if (fellows.containsKey(mss.originator)) {
				System.out.println("subs: " + mss.originator);
				synchronized (addSubs) {
					// if the msg comes from this group
					for (String t : mss.topics) {
						if (brokerTable.containsKey(t)) {// if this topic exists
							if (mss.type == 0)// add
								brokerTable.get(t).add(mss.originator);
							else {// remove
								brokerTable.get(t).remove(mss.originator);
								if (brokerTable.get(t).isEmpty()) {
									brokerTable.remove(t);
								}
							}
						} else if (mss.type == 0) {// if not exists and the type
													// is
													// 0
							TreeSet<String> ts = new TreeSet<String>();
							ts.add(mss.originator);
							brokerTable.put(t, ts);
						}
					}
				}
			}

		} else if (msg instanceof MsgLost) {

			MsgLost ml = (MsgLost) msg;
			System.out.println("lost: " + ml.indicator);
			log.info("lost: " + ml.indicator);

			if (ml.inside) {

				for (String t : brokerTable.keySet()) {
					if (brokerTable.get(t).contains(ml.indicator)) {
						brokerTable.get(t).remove(ml.indicator);
						if (brokerTable.get(t).isEmpty())
							brokerTable.remove(t);
					}
				}
				fellows.remove(ml.indicator);
			} else {
				if (lsdb.containsKey(ml.indicator)) {
					lsdb.remove(ml.indicator);
				}
				mgr.DeleteAllSubsOfGroup(ml.indicator);
				groupMap.remove(ml.indicator);
			}
		} else if (msg instanceof GroupUnit) {

			GroupUnit mg = (GroupUnit) msg;
			System.out.println("comes group: " + mg.name);
			log.info("comes group: " + mg.name);

			// add this new group as a child
			if (groupMap.keySet().contains(mg.name)) {
				// if this group already exists, update its information
				groupMap.get(mg.name).uPort = mg.uPort;
				groupMap.get(mg.name).addr = mg.addr;
				groupMap.get(mg.name).id = mg.id;
				groupMap.get(mg.name).tPort = mg.tPort;

			} else {
				groupMap.put(mg.name, mg);
			}
		} else if (msg instanceof MsgSynSubs) {

			MsgSynSubs mss = (MsgSynSubs) msg;
			System.out.println("syn subs message: " + mss.originator);

			// 更新相关订阅信息
			for (String s : brokerTable.keySet()) {
				if (brokerTable.get(s).contains(mss.originator)
						&& !mss.topics.contains(s)) {
					brokerTable.get(s).remove(mss.originator);
				}
			}
			for (String s : mss.topics) {
				if (brokerTable.containsKey(s)) {
					if (!brokerTable.get(s).contains(mss.originator)) {
						brokerTable.get(s).add(mss.originator);
					}
				} else {
					TreeSet<String> ts = new TreeSet<String>();
					ts.add(mss.originator);
					brokerTable.put(s, ts);
				}
			}
		} else if (msg instanceof LSA) {
			LSA lsa = (LSA) msg;
			System.out.println("receive lsa ");
			this.addLSAToLSDB(lsa);
		} else if (msg instanceof PolicyDB) {
			PolicyDB pdb = (PolicyDB) msg;
			pdb.processRegMsg(pdb);
		} else if (msg instanceof UpdateTree) {
			UpdateTree ut = (UpdateTree) msg;
			ut.processRegMsg(ut);
		} else if (msg instanceof org.Mina.shorenMinaTest.msg.tcp.MsgNotis) {

			final org.Mina.shorenMinaTest.msg.tcp.MsgNotis mns = (org.Mina.shorenMinaTest.msg.tcp.MsgNotis) msg;

			SendNotification SN = new SendNotification();
			try {
				SN.send(mns.doc);
				org.apache.servicemix.wsn.router.mgr.RtMgr.subtract();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (msg instanceof org.Mina.shorenMinaTest.msg.tcp.highPriority) {

			final org.Mina.shorenMinaTest.msg.tcp.highPriority mns = (org.Mina.shorenMinaTest.msg.tcp.highPriority) msg;

			SendNotification SN = new SendNotification();
			try {
				SN.send(mns.doc);
				org.apache.servicemix.wsn.router.mgr.RtMgr.subtract();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (msg instanceof org.Mina.shorenMinaTest.msg.tcp.lowPriority) {

			final org.Mina.shorenMinaTest.msg.tcp.lowPriority mns = (org.Mina.shorenMinaTest.msg.tcp.lowPriority) msg;

			SendNotification SN = new SendNotification();
			try {
				SN.send(mns.doc);
				org.apache.servicemix.wsn.router.mgr.RtMgr.subtract();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("No this UDP msg!");
			System.out.println(msg);
			log.info("No this UDP msg!");
		}
	}

	@Override
	public void processTcpMsg(final Socket s) {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				ObjectInputStream ois = null;
				ObjectOutputStream oos = null;
				Object msg = null;

				try {
					ois = new ObjectInputStream(s.getInputStream());
					oos = new ObjectOutputStream(s.getOutputStream());
					while (true) {
						msg = ois.readObject();// read 阻塞了，如果对方关闭了socket
												// 系统会抛出io异常，从而关闭此方的socket，因为socket全双工，双方都要关
						if ((msg != null)) {
							boolean isLong = processKindTcpMsg(ois, oos, s, msg);
							if (!isLong) {
								break;
							}
						} else
							break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.warn(e);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.warn(e);
				}
				try {
					oos.close();
					ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.warn(e);
				}
			}
		}.start();
	}

	private boolean processKindTcpMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, Object msg) {
		if (msg instanceof MsgSetAddr) {
			MsgSetAddr msa = (MsgSetAddr) msg;
			processSpecificTcpMsg(ois, oos, s, msa);
			return false;
		} else if (msg instanceof MsgInfoChange) {
			MsgInfoChange mic = (MsgInfoChange) msg;
			processSpecificTcpMsg(ois, oos, s, mic);
			return false;
		} else if (msg instanceof MsgSetConf) {
			MsgSetConf msc = (MsgSetConf) msg;
			processSpecificTcpMsg(ois, oos, s, msc);
			return false;
		} else if (msg instanceof MsgLookupMemberSubscriptions) {
			MsgLookupMemberSubscriptions mlms = (MsgLookupMemberSubscriptions) msg;
			processSpecificTcpMsg(ois, oos, s, mlms);
			return false;
		} else if (msg instanceof MsgNotis) {
			MsgNotis mns = (MsgNotis) msg;
			mgr.addMqLast(mns);
			Date timeNow2 = new Date();
			log.info("add a msg " + "Delay:"
					+ (timeNow2.getTime() - mns.sendDate.getTime())
					+ ",and the Queue size is" + mgr.getMqSize());

			// System.out.println("Receive:topic" + mns.topicName + ":content" +
			// mns.doc + ":startTime" + mns.sendDate);
			log.info("Receive:topic" + mns.topicName + ":content" + mns.doc
					+ ":startTime" + mns.sendDate);
			return true;
		} else {
			System.out.println("No this TCP msg!");
			log.info("No this TCP msg!");
			return false;
		}
	}

	public void synSubs() {
		MsgSynSubs mss = new MsgSynSubs();
		mss.topics.addAll(clientTable);
		mss.originator = localAddr;

		// multicast this message in local group
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream doos = null;
		DatagramSocket s = null;
		DatagramPacket p = null;
		byte[] buf = null;

		try {
			doos = new ObjectOutputStream(baos);
			s = new DatagramSocket();
			doos.writeObject(mss);
			buf = baos.toByteArray();

			p = new DatagramPacket(buf, buf.length,
					InetAddress.getByName(multiAddr), uPort);
			s.send(p);
		} catch (IOException e) {
			e.printStackTrace();
			log.warn(e);
		}
	}

	private void processSpecificTcpMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, MsgSetAddr msa) {
		System.out.println("set address: " + msa.addr);
		log.info("set address: " + msa.addr);

		if (!localAddr.equals(msa.addr) || tPort != msa.port) {
			MsgInfoChange mic = new MsgInfoChange();
			mic.originator = localAddr;
			mic.sender = localAddr;
			mic.addr = msa.addr;
			mic.port = msa.port;

			Socket s1 = null;
			ObjectOutputStream oos1 = null;
			ObjectInputStream ois1 = null;

			ArrayList<BrokerUnit> brokers = new ArrayList<BrokerUnit>(
					fellows.values());
			brokers.add(rep);
			for (BrokerUnit b : brokers) {
				try {
					s1 = new Socket(b.addr, b.tPort);
					oos1 = new ObjectOutputStream(s1.getOutputStream());
					ois1 = new ObjectInputStream(s1.getInputStream());
					oos1.writeObject(mic);

					oos1.close();
					ois1.close();
					s1.close();
				} catch (IOException e) {
					System.out.println("TCP message cannot reach " + b.addr);
					log.warn(e);
				}
			}

			localAddr = msa.addr;
			tPort = msa.port;
			mgr.updateTcpSkt();
			mgr.updateUdpSkt();
		}
	}

	private void processSpecificTcpMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, MsgInfoChange mic) {
		if (rep.addr.equals(mic.originator)) {
			// inside
			dt.removeTarget(rep.addr);
			rep.addr = mic.addr;
			rep.tPort = mic.port;
			dt.addTarget(rep.addr);

		} else if (fellows.containsKey(mic.originator)) {
			// inside
			fellows.get(mic.originator).addr = mic.addr;
			fellows.get(mic.originator).tPort = mic.port;

		} else if (groupMap.contains(mic.originator)) {
			// outside
			groupMap.get(mic.originator).addr = mic.addr;
			groupMap.get(mic.originator).tPort = mic.port;

		}
	}

	private void processSpecificTcpMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, MsgSetConf msc) {
		System.out.println("set configurations");
		log.info("set configurations");

		if (msc.address == null || msc.address.equals(localAddr)) {

			neighborSize = msc.conf_.neighborSize;
			joinTimes = msc.conf_.joinTimes;

			if (threshold != msc.conf_.lostThreshold) {
				threshold = msc.conf_.lostThreshold;
				dt.setThreshold(threshold);
			}
			if (scanPeriod != msc.conf_.scanPeriod) {
				scanPeriod = msc.conf_.scanPeriod;
			}
			if (sendPeriod != msc.conf_.sendPeriod) {
				sendPeriod = msc.conf_.sendPeriod;
				dt.setSendPeriod(sendPeriod);
			}
			if (uPort != msc.conf_.uPort
					|| !multiAddr.equals(msc.conf_.multiAddr)) {
				uPort = msc.conf_.uPort;
				multiAddr = msc.conf_.multiAddr;
				mgr.updateUdpSkt();
			}

			System.out.println("configuration updated");
			log.info("configuration updated");

		}
	}

	private void processSpecificTcpMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, MsgLookupMemberSubscriptions mlms) {
		System.out.println("look up member Subscriptions");
		log.info("look up member Subscriptions");

		if (mlms.name.equals(groupName) && mlms.addr.equals(localAddr)) {
			MsgLookupMemberSubscriptions_ mlms_ = new MsgLookupMemberSubscriptions_();
			for (String t : clientTable) {
				mlms_.topics.add(t);
			}// for

			try {
				oos.writeObject(mlms_);
			} catch (IOException e) {
				e.printStackTrace();
				log.warn(e);
			}
		}// if
	}

	@Override
	public void addNeighbor(String target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendObjectToNeighbors(Object obj) {

	}

	@Override
	public void spreadInLocalGroup(Object obj) {

	}

	@Override
	public void synLSA() {
		// TODO Auto-generated method stub

	}

	@Override
	public void spreadLSAInLocalGroup(LSA lsa) {
		// TODO Auto-generated method stub

	}

}
