package org.apache.servicemix.wsn.router.mgr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.wsn.push.SendNotification;
import org.apache.servicemix.wsn.router.detection.IDt;
import org.apache.servicemix.wsn.router.mgr.base.AState;
import org.apache.servicemix.wsn.router.mgr.MsgNotis;
import org.apache.servicemix.wsn.router.msg.tcp.AskForGroupMap;
import org.apache.servicemix.wsn.router.msg.tcp.DistBtnNebr;
import org.apache.servicemix.wsn.router.msg.tcp.GroupMap;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.msg.tcp.LSDB;
import org.apache.servicemix.wsn.router.msg.tcp.MsgAdReboot;
import org.apache.servicemix.wsn.router.msg.tcp.MsgAdminChange;
import org.apache.servicemix.wsn.router.msg.tcp.MsgAskForLSDB;
import org.apache.servicemix.wsn.router.msg.tcp.MsgGroupJunk;
import org.apache.servicemix.wsn.router.msg.tcp.MsgGroupLost;
import org.apache.servicemix.wsn.router.msg.tcp.MsgInfoChange;
import org.apache.servicemix.wsn.router.msg.tcp.MsgInsert;
import org.apache.servicemix.wsn.router.msg.tcp.MsgInsert_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgJoinGroup;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupMember;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupSubscriptions;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupMemberSubscriptions;
import org.apache.servicemix.wsn.router.msg.tcp.MsgNewGroup;
import org.apache.servicemix.wsn.router.msg.tcp.MsgNewGroup_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgNewRep;
import org.apache.servicemix.wsn.router.msg.tcp.MsgSetAddr;
import org.apache.servicemix.wsn.router.msg.tcp.MsgSetConf;
import org.apache.servicemix.wsn.router.msg.tcp.MsgSynSubs;
import org.apache.servicemix.wsn.router.msg.tcp.LSA;
import org.apache.servicemix.wsn.router.msg.tcp.PolicyDB;
import org.apache.servicemix.wsn.router.msg.tcp.UpdateTree;
import org.apache.servicemix.wsn.router.msg.udp.MsgHello;
import org.apache.servicemix.wsn.router.msg.udp.MsgLost;
import org.apache.servicemix.wsn.router.msg.udp.MsgNewBroker;
import org.apache.servicemix.wsn.router.msg.udp.MsgSubs;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;

public class RepState extends AState {
	private static Log log = LogFactory.getLog(RepState.class);
	private RtMgr mgr;

	private IDt dt;

	private Object synCache;

	private Object addLSA;

	public RepState(RtMgr mgr, IDt dt) {
		this.mgr = mgr;
		this.dt = dt;
		synCache = new Object();
		addLSA = new Object();
	}
	

	@Override
	public void join() {
		Socket s = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;

		MsgNewGroup_ mng_ = null;
		// send new group message to administrator
		try {
			s = new Socket(adminAddr, adminPort);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());

			MsgNewGroup mng = new MsgNewGroup();
			mng.name = groupName;
			mng.tPort = tPort;
			mng.controllerAddr = groupController;
			oos.writeObject(mng);
			Object obj = ois.readObject();
			mng_ = (MsgNewGroup_) obj;
			log.info(mng_);

			oos.close();
			ois.close();
			s.close();
		} catch (IOException e) {

			e.printStackTrace();
			log.warn(e);
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			log.warn(e);
		}

		// 管理员告知可以加入
		if (mng_ != null && mng_.isOK) {
			// 标记是否需要初始化本集群LSA
			boolean needClear = false;

			if (mng_.groups != null && !mng_.groups.isEmpty()) {
				System.out.println("groups not empty!");
				for (GroupUnit gu : mng_.groups.values()) {
					groupMap.put(gu.name, gu);
				}

				// ask a random group for lsdb
				boolean getOK = false;
				MsgAskForLSDB ask = new MsgAskForLSDB();
				ask.askMessage = askMsg;
				if (!mng_.groups.values().isEmpty()) {
					int times = mng_.groups.values().size();
					ArrayList<GroupUnit> list = new ArrayList<GroupUnit>();
					for (GroupUnit gu : mng_.groups.values()) {
						list.add(gu);
					}
					for (int i = 0; i < times; i++) {
						Random random = new Random();
						GroupUnit g = list.get(random.nextInt(list.size()));
						System.out.println("ask lsdb from :" + g.name);
						if (g != null) {
							try {
								s = new Socket(g.addr, g.tPort);
								oos = new ObjectOutputStream(
										s.getOutputStream());
								ois = new ObjectInputStream(s.getInputStream());
								oos.writeObject(ask);
								Object obj = ois.readObject();
								if (obj instanceof LSDB) {
									System.out.println("receive lsdb");
									for (LSA l : ((LSDB) obj).lsdb) {
										l.sendTime = System.currentTimeMillis();
										mgr.AddSubsByGroup(l.originator,
												l.subsTopics);
										lsdb.put(l.originator, l);
									}
									if (lsdb.containsKey(groupName)) {
										needClear = true;
									}
									mgr.CalAllTopicRoute();
									getOK = true;

								}

								ois.close();
								oos.close();
								s.close();
							} catch (IOException e) {
								System.out.println("Group " + g.name
										+ " refused to offer LSDB");
								log.warn(e);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
								log.warn(e);
							}
						}
						if (getOK) {
							break;
						}
						list.remove(g);
					} // for
				} // if
					// 将groupMap交给拓扑计算，返回已经建立好的邻居
				ArrayList<String> nbs;
				nbs = nb.BuildAGetNeigs();
				if (nbs.size() > 0) {
					int i = 0;
					for (String neighbor : nbs) {
						if (this.AskToInsert(neighbor, i++)) {
							this.addNeighbor(neighbor);
						}
					}
					this.neighborReduced(i);
				} // if
			} // if

			System.out.println("neighbor built");

			try {
				s = new Socket(adminAddr, adminPort);
				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());

				GroupUnit localGroup = new GroupUnit(localAddr, localNetmask,
						tPort, uPort, new Date().getTime(), groupName);
				oos.writeObject(localGroup);
				PolicyDB db = (PolicyDB) ois.readObject();
				joinOK = db.clearAll;
				if (joinOK) {
					groupMap.put(groupName, localGroup);
					ShorenUtils.deleteAllPolicyMsg();
					for (WsnPolicyMsg msg : db.pdb) {
						ShorenUtils.encodePolicyMsg(msg);
					}
				}

				oos.close();
				ois.close();
				s.close();

				this.setClock(true);
				System.out.println("insert OK ");
				(new Thread(new Init(needClear))).start();

			} catch (IOException e) {

				e.printStackTrace();
				log.warn(e);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				log.warn(e);
			}
			if (!joinOK)
				log.info("cannot register in administrator, try again");
		} else {
			System.out.println(mng_.description);
			log.info(mng_.description);
			System.out.println("Enter a new name:");
			log.info("Enter a new name:");
			Scanner sc = new Scanner(System.in);
			groupName = sc.next();
			log.info("try again now");
			joinOK = false;
		}
	}

	public class Init implements Runnable {
		boolean needClear;

		public Init(boolean needClear) {
			this.needClear = needClear;
		}

		@Override
		public void run() {
			if (needClear) {
				lsaSeqNum = lsdb.get(groupName).seqNum + 1;
				MsgSubs mss = new MsgSubs();
				mss.topics.addAll(lsdb.get(groupName).subsTopics);
				mss.type = 1;
				mss.originator = localAddr;
				sendSbp(mss);
			}
			MsgNewRep mnr = new MsgNewRep();
			mnr.addr = localAddr;
			mnr.id = System.currentTimeMillis();
			mnr.name = groupName;
			mnr.netmask = localNetmask;
			mnr.tPort = tPort;
			mnr.uPort = uPort;
			spreadInLocalGroup(mnr);
		}
	}

	public void neighborReduced(int i) {
		ArrayList<String> nbs;
		ArrayList<String> out = new ArrayList<String>();
		if (cacheLSA != null && !cacheLSA.lostGroup.isEmpty()) {
			out.addAll(cacheLSA.lostGroup);
		}
		while (out.size() < groupMap.size()) {
			if (groupMap.size() > neighborSize / 2
					&& neighbors.size() < neighborSize / 3) {
				nbs = nb.NeigsChange(out);
				for (String neighbor : nbs) {
					if (this.AskToInsert(neighbor, i++)) {
						this.addNeighbor(neighbor);
					} else {
						out.add(neighbor);
					}
				}
			} else {
				break;
			}
		}
	}

	public boolean AskToInsert(String neighbor, int i) {
		Socket s = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;

		MsgInsert mi = new MsgInsert();
		MsgInsert_ mi_ = new MsgInsert_();

		boolean OK = false;
		boolean neighborJunk = false;
		// send new group message to administrator
		for (int k = 0; k < joinTimes; k++) {
			try {
				s = new Socket(groupMap.get(neighbor).addr,
						groupMap.get(neighbor).tPort);
				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());

				mi.name = groupName;
				mi.addr = localAddr;
				mi.id = id;
				mi.tPort = tPort;
				mi.uPort = uPort;
				mi.tagetGroupName = neighbor;
				mi.netmask = localNetmask;
				if (i == 0)
					mi.needInit = true;
				else
					mi.needInit = false;
				oos.writeObject(mi);
				Object obj = ois.readObject();
				mi_ = (MsgInsert_) obj;
				OK = mi_.isOK;

				oos.close();
				ois.close();
				s.close();
				break;
			} catch (IOException e) {
				System.out.println("can't reach " + neighbor);
				if (!neighborJunk) {
					neighborJunk = true;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				log.warn(e);
			}
		}
		if (neighborJunk) {
			try {
				s = new Socket(adminAddr, adminPort);
				oos = new ObjectOutputStream(s.getOutputStream());

				MsgGroupJunk mgj = new MsgGroupJunk();
				mgj.name = neighbor;
				oos.writeObject(mgj);

				oos.close();
				s.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return OK;
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

		if (topics == null || topics.isEmpty()) {
			return lsa;
		}
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

	public void addSynLSAToLSDB(LSA lsa) {
		lsa.sendTime = System.currentTimeMillis();
		LSA old = lsdb.get(lsa.originator);
		boolean calGroup = false; // 订阅改变时需计算该集群对应路由
		boolean calAll = false;
		if (old.distBtnNebrs.size() != lsa.distBtnNebrs.size()) {
			calAll = true;
		} else if (old.subsTopics.size() != lsa.subsTopics.size()) {
			calGroup = true;
		} else {
			for (String nbr : lsa.distBtnNebrs.keySet()) {
				if ((!old.distBtnNebrs.containsKey(nbr))
						|| (old.distBtnNebrs.get(nbr).dist != lsa.distBtnNebrs
								.get(nbr).dist)) {
					calAll = true;
					break;
				}
			}
			if (!calAll) {
				for (String t : lsa.subsTopics) {
					if (!old.subsTopics.contains(t)) {
						calGroup = true;
						break;
					}
				}
			}
		}

		lsdb.remove(old.originator);
		lsdb.put(lsa.originator, lsa);

		ArrayList<String> to1 = null;
		if (calGroup) {
			to1 = mgr.DeleteAllSubsOfGroup(lsa.originator);
			ArrayList<String> to2 = mgr.AddSubsByGroup(lsa.originator,
					lsa.subsTopics);
			if (to1 == null) {
				to1 = new ArrayList<String>();
			}

			if (to2 != null && !to2.equals("")) {
				for (String t : to2) {
					if (!to1.contains(t))
						to1.add(t);
				}
			}
		}
		if (calAll) {
			mgr.CalAllTopicRoute();
		} else if (calGroup) {
			for (String t : to1) {
				mgr.route(t);
			}
		}
	}

	@Override
	public boolean addLSAToLSDB(LSA lsa) {
		synchronized (addLSA) {
			ArrayList<String> changedTopics = new ArrayList<String>();
			boolean calAll = false;
			if (lsdb.containsKey(lsa.originator)) {
				LSA old = lsdb.get(lsa.originator);
				// ignore the lsa if it is not the newest that is received from
				// lsa.originator
				if (old.seqNum >= lsa.seqNum)
					return false;
				System.out
						.println("add lsa from " + lsa.originator + "to lsdb");
				
				if (lsa.syn == 1) { // 同步LSA
					addSynLSAToLSDB(lsa);
					return true;
				} else {
					old.seqNum = lsa.seqNum;
					old.sendTime = System.currentTimeMillis();

					// cancel topics from lsa to lsdb
					if (!lsa.cancelTopics.isEmpty()) {
						old = addTopicsToLSA(old, lsa.cancelTopics, 1);
						changedTopics.addAll(mgr.DeleteSubsByGroup(
								lsa.originator, lsa.cancelTopics));
					} // if

					old = addTopicsToLSA(old, lsa.subsTopics, 0);
				} // else lsa.syn!=1
					// the originator of lsa lost its neighbor
				if (!lsa.lostGroup.isEmpty()) {
					for (String lost : lsa.lostGroup) {
						this.deleteLineFromLSDB(lost, lsa.originator);
					}
					calAll = true;
				}

				// refresh the distance between the originator of lsa and its
				// neighbors
				for (String nei : lsa.distBtnNebrs.keySet()) {
					if (old.distBtnNebrs.containsKey(nei)) {
						if (!(old.distBtnNebrs.get(nei).dist == lsa.distBtnNebrs
								.get(nei).dist)) {
							old.distBtnNebrs.get(nei).dist = lsa.distBtnNebrs
									.get(nei).dist;
							calAll = true;
						}
					} else {
						old.distBtnNebrs.put(nei, lsa.distBtnNebrs.get(nei));
						if (lsdb.containsKey(nei)) {
							if (!lsdb.get(nei).distBtnNebrs
									.containsKey(old.originator)) {
								calAll = true;
							} // if
						} // if
					} // else
				} // if
			} else {
				if (lsa.cancelTopics.isEmpty() && lsa.distBtnNebrs.isEmpty()
						&& lsa.subsTopics.isEmpty()) {
					return false;
				}

				System.out.println("new lsa from " + lsa.originator);
				lsa.sendTime = System.currentTimeMillis();
				lsdb.put(lsa.originator, lsa);
				calAll = true;
			}
			// calculate the topics whose subscribers changed
			// add topics from lsa to lsdb
			if (!lsa.subsTopics.isEmpty()) {
				for (String t : mgr.AddSubsByGroup(lsa.originator,
						lsa.subsTopics)) {
					if (!changedTopics.contains(t)) {
						changedTopics.add(t);
					}
				}
			}

			if (calAll) {
				mgr.CalAllTopicRoute();
			} else {
				for (String topic : changedTopics) {
					mgr.route(topic);
				} // for
			}
			return true;
		}
	}

	// send the local subscriptions
	@Override
	public void sendSbp(Object msg) {

		MsgSubs mss = (MsgSubs) msg;
		if (!mss.originator.equals(localAddr + "!")) {
			ArrayList<String> remove = new ArrayList<String>();
			if (mss.originator.equals(localAddr)) {
				for (String sub : mss.topics) {
					for (String s : brokerTable.keySet()) {
						if (this.isIncluded(s, sub)) {
							remove.add(sub);
						}
					}
				}
			} else {
				for (String sub : mss.topics) {
					for (String s : clientTable) {
						if (this.isIncluded(s, sub)) {
							remove.add(sub);
						}
					}
					if (!remove.contains(sub)) {
						for (String s : brokerTable.keySet()) {
							if (this.isIncluded(s, sub)
									&& !brokerTable.get(s).contains(
											mss.originator)) {
								remove.add(sub);
							}
						}
					} // if
				} // for
			} // else

			if (!remove.isEmpty()) {
				mss.topics.removeAll(remove);
			}
			if (mss.topics.isEmpty()) {
				return;
			}
		}

		// add the message to cacheLSA
		System.out.println("send local subscription:" + mss.topics.toString());

		if (cacheLSA == null) {
			synchronized (synCache) {
				if (cacheLSA == null) {
					cacheLSA = new LSA();
					Timer timer = new Timer();
					timer.schedule(new SendTask(), 5000);
				}
			}
		}
		synchronized (synCache) {
			cacheLSA = this.addTopicsToLSA(cacheLSA, mss.topics, mss.type);
		}

		// spread mss in local group if this is the originator
		if (mss.originator != null
				&& (mss.originator.equals(groupName) || mss.originator
						.equals(localAddr))) {
			mss.originator = localAddr;
			this.spreadInLocalGroup(mss);
		}

		if (mss.type == 1) {
			MsgSubs ms = new MsgSubs();
			ms.originator = localAddr + "!";
			for (String can : mss.topics) {
				for (String c : clientTable) {
					if (this.isIncluded(can, c)) {
						ms.topics.add(c);
					}
				}
				for (String c : brokerTable.keySet()) {
					if (this.isIncluded(can, c)) {
						ms.topics.add(c);
					}
				}
			}
			ms.type = 0;
			if (!ms.topics.isEmpty()) {
				this.sendSbp(ms);
			}
		}
	}

	public void addNeighbor(String neighbor) {
		if (neighbors.contains(neighbor)) {
			return;
		}
		if (!groupMap.containsKey(neighbor)) {
			return;
		}
		neighbors.add(neighbor);
		dt.addTarget(neighbor);

		boolean needInit = false;
		if (cacheLSA == null) {
			synchronized (synCache) {
				if (cacheLSA == null) {
					cacheLSA = new LSA();
					needInit = true;
				}
			}
		}

		if (!cacheLSA.distBtnNebrs.containsKey(neighbor)) {
			cacheLSA.distBtnNebrs.put(neighbor, new DistBtnNebr(1));
		}

		if (needInit) {
			Timer timer = new Timer();
			timer.schedule(new SendTask(), 5000);
		}
	}

	class SynTask extends TimerTask {

		@Override
		public void run() {
			synLSA();
		}

	}

	class CheckTask extends TimerTask {

		@Override
		public void run() {
			for (LSA lsa : lsdb.values()) {
				if ((System.currentTimeMillis() - lsa.sendTime) > synPeriod * 60 * 1000 * 1.5) {
					System.out.println("集群 " + lsa.originator + " LSA超时");
					boolean invalid = true;
					for (String n : lsa.distBtnNebrs.keySet()) {
						if (lsdb.contains(n)
								&& (System.currentTimeMillis() - lsdb.get(n).sendTime) < synPeriod * 60 * 1000 * 1.5) {
							invalid = false;
						}
					}

					if (neighbors.contains(lsa.originator)
							&& !waitHello.contains(lsa.originator)) {
						dt.removeTarget(lsa.originator);
						lost(lsa.originator);
					}

					lsdb.remove(lsa.originator);
					ArrayList<String> topics = mgr
							.DeleteAllSubsOfGroup(lsa.originator);
					for (String topic : topics) {
						mgr.route(topic);
					}
					if (invalid) {
						groupMap.remove(lsa.originator);
						MsgLost ml = new MsgLost();
						ml.indicator = lsa.originator;
						ml.inside = false;

						spreadInLocalGroup(ml);
						int dist = groupName
								.compareToIgnoreCase(lsa.originator);
						int num = 0;
						boolean send = true;
						for (String gu : groupMap.keySet()) {
							if (gu.compareToIgnoreCase(lsa.originator) < dist) {
								num++;
							}
							if (num > 2) {
								send = false;
								break;
							}
						}
						if (send) {
							System.out.println("MsgGroupLost message of "
									+ lsa.originator
									+ " is sent to administrator");
							MsgGroupLost mgl = new MsgGroupLost();
							mgl.name = lsa.originator;
							mgl.sender = groupName;

							sendObjectToAdministrator(mgl);
						}
					}
				}
			}
		}
	}

	class SendTask extends TimerTask {

		@Override
		public void run() {
			// send the messages in cacheLSA to all of the neighbors
			if (cacheLSA != null) {
				cacheLSA.seqNum = lsaSeqNum++;
				cacheLSA.originator = groupName;
				cacheLSA.sendTime = System.currentTimeMillis();

				System.out.println("send cacheLSA");
				LSA send = new LSA();
				// spread the cacheLSA to the agents of the local group
				synchronized (synCache) {
					send.copyLSA(cacheLSA);
					if (cacheLSA != null)
						cacheLSA = null;
				}
				spreadLSAInLocalGroup(send);

				// send the cacheLSA to the neighbors
				sendObjectToNeighbors(send);

				addLSAToLSDB(send);
			}
		}
	}

	class LostTask extends TimerTask {

		String name;

		public LostTask(String name) {
			this.name = name;
		}

		@Override
		public void run() {
			if (waitHello.contains(name)) {
				waitHello.remove(name);
				if (cacheLSA == null) {
					synchronized (synCache) {
						if (cacheLSA == null) {
							cacheLSA = new LSA();
							Timer timer = new Timer();
							timer.schedule(new SendTask(), RtMgr.sendPeriod);
						}
					}
					if (!cacheLSA.lostGroup.contains(name))
						cacheLSA.lostGroup.add(name);
					if (cacheLSA.distBtnNebrs.containsKey(name))
						cacheLSA.distBtnNebrs.remove(name);
				} else {
					if (!cacheLSA.lostGroup.contains(name))
						cacheLSA.lostGroup.add(name);
					if (cacheLSA.distBtnNebrs.containsKey(name))
						cacheLSA.distBtnNebrs.remove(name);
				}
				if (neighbors.contains(name)) {
					neighbors.remove(name);
					neighborReduced(1);
				}

				System.out.println("The neighbor " + name + " is invalid!");
				log.info("The neighbor " + name + " is invalid!");
			}
		}
	}

	// send object to administrator ---- TCP way
	// send object to neighbors ---- TCP way
	public void sendObjectToAdministrator(Object obj) {
		Socket s = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;

		try {
			s = new Socket(adminAddr, adminPort);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			oos.writeObject(obj);

			ois.close();
			oos.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// send object to neighbors ---- TCP way
	@Override
	public void sendObjectToNeighbors(Object obj) {
		Socket s = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;

		ArrayList<String> li = new ArrayList<String>(neighbors);
		// li.add(parent);

		if (li.isEmpty())
			return;
		for (String n : li)
			if (groupMap.get(n) != null && !waitHello.contains(n)) {
				try {
					s = new Socket(groupMap.get(n).addr, groupMap.get(n).tPort);
					oos = new ObjectOutputStream(s.getOutputStream());
					ois = new ObjectInputStream(s.getInputStream());
					oos.writeObject(obj);

					ois.close();
					oos.close();
					s.close();
				} catch (IOException e) {
					System.out.println("TCP message cannot reach " + n);
				}
			}
	}

	public void spreadLSAInLocalGroup(LSA lsa) {
		LSA send = new LSA();
		send.copyPartLSA(lsa);

		ArrayList<String> subs = new ArrayList<String>();
		ArrayList<String> cncl = new ArrayList<String>();
		int count = 0;
		for (String sub : lsa.subsTopics) {
			if (count + sub.length() > 2000) {
				count = 0;
				send.subsTopics = subs;
				this.spreadInLocalGroup(send);
				subs.clear();
			}
			subs.add(sub);
			count += sub.length();
		}
		if (!subs.isEmpty()) {
			send.subsTopics = subs;
			this.spreadInLocalGroup(send);
			subs.clear();
			count = 0;
		}
		for (String can : lsa.cancelTopics) {
			if (count + can.length() > 2000) {
				count = 0;
				send.cancelTopics = cncl;
				this.spreadInLocalGroup(send);
				cncl.clear();
			}
			cncl.add(can);
			count += can.length();
		}
		if (!cncl.isEmpty()) {
			send.cancelTopics = cncl;
			this.spreadInLocalGroup(send);
		}
	}

	// spread object in this group ---- UDP way
	@Override
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
			p = new DatagramPacket(buf, buf.length,
					InetAddress.getByName(multiAddr), uPort);
			s.send(p);
		} catch (IOException e) {
			e.printStackTrace();
			log.warn(e);
		}
	}

	@Override
	public void setClock(boolean isRep) {
		Timer timer = new Timer();
		if (isRep) {
			timer.schedule(new SynTask(), synPeriod * 60 * 1000,
					synPeriod * 60 * 1000);
		} else {
			lsaSeqNum++;
			timer.schedule(new SynTask(), 0, synPeriod * 60 * 1000);

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

			// notify neighbors
			ArrayList<String> li = new ArrayList<String>(neighbors);
			for (String n : li)
				if (groupMap.containsKey(n)) {
					System.out.println("send rep switch msg to " + n);
					try {
						s = new Socket(groupMap.get(n).addr,
								groupMap.get(n).tPort);
						oos = new ObjectOutputStream(s.getOutputStream());
						ois = new ObjectInputStream(s.getInputStream());
						oos.writeObject(mnr);
						Object obj = ois.readObject();
						if (obj != null && obj instanceof LSDB) {
							boolean ask = false;
							LSDB ld = (LSDB) obj;
							for (LSA l : ld.lsdb) {
								if (!l.originator.equals(groupName)) {
									this.addSynLSAToLSDB(l);
									if (!groupMap.containsKey(l.originator)) {
										ask = true;
									}
								}
							}
							if (ask) {
								System.out.println("ask for groupmap");
								AskForGroupMap af = new AskForGroupMap();
								oos.writeObject(af);
								obj = ois.readObject();
								if (obj instanceof GroupMap) {
									GroupMap gm = (GroupMap) obj;
									for (GroupUnit gu : gm.gu) {
										if (!groupMap.containsKey(gu.name)) {
											groupMap.put(gu.name, gu);
										}
									}
								}
							} else {
								oos.writeObject(null);
							}
						}

						oos.close();
						ois.close();
						s.close();
					} catch (IOException e) {
						e.printStackTrace();
						log.warn(e);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			mgr.CalAllTopicRoute();
		}
		timer.schedule(new CheckTask(), scanPeriod, scanPeriod);
	}

	public static void topicAvoid(String groupIP) {

	}

	@Override
	public void lost(String indicator) {

		System.out.println(" can't reach " + indicator);
		log.info(" can't reach " + indicator);

		if (fellows.containsKey(indicator)) {
			// some broker in this group is lost

			MsgLost ml = new MsgLost();
			ml.indicator = indicator;
			ml.inside = true;

			this.spreadInLocalGroup(ml);

			// cancel the subscriptions of this broker
			ArrayList<String> topics = new ArrayList<String>();
			for (String t : brokerTable.keySet()) {

				if (brokerTable.get(t).contains(indicator)) {
					brokerTable.get(t).remove(indicator);

					if (brokerTable.get(t).isEmpty()) {
						brokerTable.remove(t);

						if (!clientTable.contains(t)) {
							// tell other groups to cancel this subscription
							topics.add(t);
						}
					}
				}
			}
			if (!topics.isEmpty()) {
				MsgSubs mss = new MsgSubs();
				mss.type = 1;
				mss.topics = topics;
				mss.originator = groupName;
				mss.sender = groupName;

				this.sendSbp(mss);
			}
			// erase it
			fellows.remove(indicator);

		} else if (neighbors.contains(indicator)) {
			// representative of some neighbor group is lost
			waitHello.add(indicator);
			LostTask lt = null;

			lt = new LostTask(indicator);

			Timer timerForLost = new Timer();
			timerForLost.schedule(lt, RtMgr.threshold / 2);
		}
	}

	public void deleteLineFromLSDB(String lostGroup, String sender) {
		// delete sender from lostGroup's LSA
		System.out.println("delete " + lostGroup + " sender :" + sender);
		if (lsdb.containsKey(sender)) {
			LSA sd = lsdb.get(sender);
			for (String s : sd.distBtnNebrs.keySet())
				if (s.equals(lostGroup)) {
					sd.distBtnNebrs.remove(s);
					break;
				}
		}
		// delete lostGroup from sender's LSA
		if (lsdb.containsKey(lostGroup)) {
			LSA sd = lsdb.get(lostGroup);
			for (String s : sd.distBtnNebrs.keySet())
				if (s.equals(sender)) {
					sd.distBtnNebrs.remove(s);
					break;
				}
			if (sd.distBtnNebrs.isEmpty()) {
				System.out.println("group " + lostGroup + " is empty in lsdb");
				// delete the group's subscriptions and recalculate these topics
				ArrayList<String> as = mgr.DeleteAllSubsOfGroup(lostGroup);
				for (String s : as) {
					mgr.route(s);
				}
				lsdb.remove(lostGroup);
				MsgLost ml = new MsgLost();
				ml.indicator = lostGroup;
				ml.inside = false;

				this.spreadInLocalGroup(ml);
				if (groupName.equals(sender) || neighbors.contains(sender)) {

					System.out.println("MsgGroupLost message of " + lostGroup
							+ " send to administrator");
					MsgGroupLost mgl = new MsgGroupLost();
					mgl.name = lostGroup;
					mgl.sender = groupName;

					this.sendObjectToAdministrator(mgl);
				}
			} // sd.distBtnNebrs.isEmpty()
		} // (RtMgr.LSDB.containsKey(lostGroup)

		if (groupMap.containsKey(lostGroup))
			groupMap.remove(lostGroup);
	}

	//group subs changes, notify mgr
	public boolean send2Mgr(MsgSubs msg){
		boolean success = false;
		Socket s = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;

//		MsgNewGroup_ mng_ = null;
		// send new group message to administrator
		try {
			s = new Socket(adminAddr, adminPort);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());

			msg.groupName = groupName;
			oos.writeObject(msg);
//			Object obj = ois.readObject();
//			mng_ = (MsgNewGroup_) obj;
			success = ois.readBoolean();
//			log.info(mng_);

			oos.close();
			ois.close();
			s.close();
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
			log.warn(e);
		}
		return success;
	}

	@Override
	public void processUdpMsg(Object msg) {

		if (msg instanceof MsgHello) {
			MsgHello mh = (MsgHello) msg;
			if (waitHello.contains(mh.indicator)) {
				waitHello.remove(mh.indicator);
				dt.addTarget(mh.indicator);
			}

			dt.onMsg(mh);
			System.out.println("hello from " + ((MsgHello) msg).indicator);
			log.info("hello from " + ((MsgHello) msg).indicator);

		} else if (msg instanceof MsgAdminChange) {

			MsgAdminChange mac = (MsgAdminChange) msg;
			adminAddr = mac.NewAdminAddr;
			System.out.println("AdminAddress change to:" + adminAddr);
			log.info("AdminAddress change to：" + adminAddr);

		} else if (msg instanceof MsgNewBroker) {// 如果本集群有新的成员

			MsgNewBroker mnm = (MsgNewBroker) msg;
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

			if (mnr.name.equals(groupName)) {
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
			}

		} else if (msg instanceof MsgSubs) {

			MsgSubs mss = (MsgSubs) msg;
			System.out.println("subs: " + mss.originator);
			log.info("subs: " + mss.originator);

			ArrayList<String> spreadTopics = new ArrayList<String>();
			if (fellows.containsKey(mss.originator)) {
				// if the msg comes from this group
				for (String t : mss.topics) {
					if (brokerTable.containsKey(t)) {// if this topic exists
						if (mss.type == 0)// add
							brokerTable.get(t).add(mss.originator);
						else {// remove
							brokerTable.get(t).remove(mss.originator);
							if (brokerTable.get(t).isEmpty()) {
								brokerTable.remove(t);

								if (!clientTable.contains(t))
									spreadTopics.add(t);
							}
						}
					} else if (mss.type == 0) {// if not exists and the type is
												// 0
						TreeSet<String> ts = new TreeSet<String>();
						ts.add(mss.originator);
						brokerTable.put(t, ts);

						if (!clientTable.contains(t)) {
							spreadTopics.add(t);
							if(!send2Mgr(mss)){
								send2Mgr(mss);
							}else{
								//send2Mgr failed twice, calc locally

							}
						}
					}
				}
				if (!spreadTopics.isEmpty()) {

					// spread it in neighbor groups
					mss.topics = spreadTopics;
					mgr.sendSbp(mss);
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
						} else {
							break;
						}
						// s.setSoTimeout(120000);
					}
				} catch (IOException e) {
					System.out.println("TCP socket with " + s.getInetAddress()
							+ " cannot be continued ");
					log.warn(e);
					try {
						oos.close();
						ois.close();
						s.close();
					} catch (IOException e1) {
						System.out.println("Failed to close the TCP Socket");
						log.warn(e1);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					log.warn(e);
					try {
						oos.close();
						ois.close();
						s.close();
					} catch (IOException e1) {
						e1.printStackTrace();
						log.warn(e1);
					}
				}
				try {
					oos.close();
					ois.close();
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
					log.warn(e);
				}
			}
		}.start();
	}

	private boolean processKindTcpMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, Object msg) throws IOException {
		if (msg instanceof MsgJoinGroup) {
			// 有代理请求加入到本集群中
			MsgJoinGroup mjg = (MsgJoinGroup) msg;
			mjg.processRepMsg(ois, oos, s, mjg);
			return false;

		} else if (msg instanceof MsgInsert) {

			MsgInsert mi = (MsgInsert) msg;
			mi.processRepMsg(ois, oos, s, mi);
			return false;

		} else if (msg instanceof GroupUnit) {
			GroupUnit mg = (GroupUnit) msg;
			System.out.println("comes group: " + mg.name);
			log.info("comes group: " + mg.name);

			if (groupMap.keySet().contains(mg.name)) {
				if (groupMap.get(mg.name).addr.equals(mg.addr)) {
					return false;
				} else {
					groupMap.remove(mg.name);
				}
			}
			groupMap.put(mg.name, mg);

			Socket s1 = null;
			ObjectOutputStream oos1 = null;
			ObjectInputStream ois1 = null;

			ArrayList<String> li = new ArrayList<String>(neighbors);
			// li.add(parent);

			if (li.isEmpty())
				return false;
			for (String n : li)
				if (!n.equals(mg.name) && groupMap.get(n) != null
						&& !waitHello.contains(n)) {
					try {
						s1 = new Socket(groupMap.get(n).addr,
								groupMap.get(n).tPort);
						oos1 = new ObjectOutputStream(s1.getOutputStream());
						ois1 = new ObjectInputStream(s1.getInputStream());
						oos1.writeObject(mg);

						ois1.close();
						oos1.close();
						s1.close();
					} catch (IOException e) {
						System.out.println("TCP message cannot reach " + n);
					}
				}
			return false;
		} else if (msg instanceof MsgNewRep) {
			MsgNewRep mnr = (MsgNewRep) msg;
			return mnr.processRepMsg(ois, oos, s, mnr);

		} else if (msg instanceof MsgSetAddr) {

			MsgSetAddr msa = (MsgSetAddr) msg;
			msa.processRepMsg(ois, oos, s, msa);
			return false;

		} else if (msg instanceof MsgAdminChange) {

			MsgAdminChange mac = (MsgAdminChange) msg;
			mac.processRepMsg(ois, oos, s, mac);
			return false;

		} else if (msg instanceof MsgLookupGroupMember) {

			MsgLookupGroupMember mlgm = (MsgLookupGroupMember) msg;
			mlgm.processRepMsg(ois, oos, s, mlgm);
			return false;

		} else if (msg instanceof MsgInfoChange) {

			MsgInfoChange mic = (MsgInfoChange) msg;
			mic.processRepMsg(ois, oos, s, mic);
			return false;

		} else if (msg instanceof MsgSetConf) {

			MsgSetConf msc = (MsgSetConf) msg;
			msc.processRepMsg(ois, oos, s, msc);
			return false;

		} else if (msg instanceof MsgAdReboot) {

			MsgAdReboot mar = (MsgAdReboot) msg;
			mar.processRepMsg(ois, oos, s, mar);
			return false;

		} else if (msg instanceof MsgLookupGroupSubscriptions) {

			MsgLookupGroupSubscriptions mlgs = (MsgLookupGroupSubscriptions) msg;
			mlgs.processRepMsg(ois, oos, s, mlgs);
			return false;

		} else if (msg instanceof MsgLookupMemberSubscriptions) {

			MsgLookupMemberSubscriptions mlms = (MsgLookupMemberSubscriptions) msg;
			mlms.processRepMsg(ois, oos, s, mlms);
			return false;

		} else if (msg instanceof MsgNotis) {
			MsgNotis mn = (MsgNotis) msg;
			if (mn.topicName.equals("priAdminLost")) {
				mn.processRepMsg(ois, oos, s, mn);
				return false;
			}
			return true;
		} else if (msg instanceof LSA) {
			LSA lsa = (LSA) msg;
			lsa.processRepMsg(ois, oos, s, lsa);
			return false;

		} else if (msg instanceof MsgAskForLSDB) {
			MsgAskForLSDB ask = (MsgAskForLSDB) msg;
			ask.processRepMsg(ois, oos, s, ask);
			return false;

		} else if (msg instanceof AskForGroupMap) {
			AskForGroupMap af = (AskForGroupMap) msg;
			af.processRepMsg(ois, oos, s, af);
			return false;
		} else if (msg instanceof PolicyDB) {
			PolicyDB pdb = (PolicyDB) msg;
			pdb.processRepMsg(ois, oos, s, pdb);
			return false;
		} else if (msg instanceof UpdateTree) {
			UpdateTree ut = (UpdateTree) msg;
			ut.processRepMsg(ois, oos, s, ut);
			return false;
		} else {
			System.out.println("No this TCP msg!");
			log.info("No this TCP msg!");

			return false;
		}
	}

	public void synLSA() {
		// send LSA to all of the neighbors and other registers in this group
		System.out.println("send syn lsa");
		LSA lsa = new LSA();
		lsa.originator = groupName;

		lsa.sendTime = System.currentTimeMillis();
		lsa.syn = 1;
		lsa.seqNum = RtMgr.lsaSeqNum++;

		for (String neighbor : neighbors) {
			lsa.distBtnNebrs.put(neighbor, new DistBtnNebr(1));
		}
		ArrayList<String> topics = new ArrayList<String>();
		topics.addAll(brokerTable.keySet());
		topics.addAll(clientTable);
		// add all the topics that are subscribed by this group to lsa

		lsa = this.addTopicsToLSA(lsa, topics, 0);

		this.sendObjectToNeighbors(lsa);
		this.spreadLSAInLocalGroup(lsa);

		this.addLSAToLSDB(lsa);
	}
}
