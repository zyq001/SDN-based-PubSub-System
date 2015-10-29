package org.apache.servicemix.wsn.router.mgr;

import org.Mina.shorenMinaTest.handlers.Start;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.TCPForwardMsg;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.wsn.push.NotifyObserverMessage;
import org.apache.servicemix.wsn.router.detection.DtMgr;
import org.apache.servicemix.wsn.router.detection.IDt;
import org.apache.servicemix.wsn.router.mgr.base.AConfiguration;
import org.apache.servicemix.wsn.router.mgr.base.AState;
import org.apache.servicemix.wsn.router.mgr.base.MsgSubsForm;
import org.apache.servicemix.wsn.router.mgr.base.SysInfo;
import org.apache.servicemix.wsn.router.mgr.calNeighbor.NeigBuild;
import org.apache.servicemix.wsn.router.msg.udp.MsgSubs;
import org.apache.servicemix.wsn.router.router.IRouter;
import org.apache.servicemix.wsn.router.router.Router;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

public class RtMgr extends SysInfo implements IRouter, Observer {
	public static ExecutorService pool;// 处理wsn的线程池
	private static Log log = LogFactory.getLog(RtMgr.class);
	private static int count;// 允许处理“上交WSN”的并发线程数目
	private static int poolLimit;// 线程池容量//多次声明的问题//注意编码层次
	private static RtMgr INSTANCE = new RtMgr();
	private static Object routeObject;
	private AConfiguration configuration;// 配置系统
	private AState regState;// 普通代理状态
	private AState repState;// 代表状态
	private AState state;// 当前状态
	// private int Ncount;//计数器，log用，以免多线程后，线程混淆
	private IDt dt;// 集群内检测模块
	private IRouter ir;// 路由算法模块
	private ArrayBlockingQueue<MsgNotis> mq;// 通知消息缓存队列
	private Thread tmt;// 监听tcp连接的线程
	private Thread umt;// 监听udp消息的线程
	private Object synObject;

	private RtMgr() {

		synObject = new Object();

		routeObject = new Object();

		dt = new DtMgr(this);

		ir = new Router();

		nb = new NeigBuild();

		configuration = new Configuration(this);

		regState = new RegState(this, dt);

		repState = new RepState(this, dt);

		boolean ManagerOn = configuration.configure();

		dt.setThreshold(threshold);
		dt.setSendPeriod(sendPeriod);
		nb.setValue(neighborSize, neighborSize / 3, neighborSize / 2);

		count = 0;
		// Ncount = 0;

		// poolLimit = poolCount;
		// pool = Executors.newFixedThreadPool(poolCount); //初始化线程池
		//
		// mq = new ArrayBlockingQueue<MsgNotis>(queueSize);
		//
		// // sendpool = new ConnectPoolS(connectCount);
		// connectpool = new ConnectPool();
		// connectqueuepool = new ConnectQueuePool();

		if (!ManagerOn) {
			return;
		} else {
			tmt = new Thread(new TcpMsgThread(this));
			umt = new Thread(new UdpMsgThread(this));

			tmt.start();
			umt.start();

			id = System.currentTimeMillis();
			while (!joinOK) {
				state.join();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("join finished!");
			log.info("join finished!");

			// start other threads

		}
	}

	public static void main(String[] args) {
		RtMgr.getInstance();
	}

	public static RtMgr getInstance() {
		if (INSTANCE == null)
			INSTANCE = new RtMgr();
		return INSTANCE;

	}

	public synchronized static void add() {
		count++;
	}

	public synchronized static void subtract() {
		count--;
	}

	public synchronized static boolean limitbool() {
		if (count < poolLimit)
			return true;
		else
			return false;
	}

	// 根据标题名称和转发源查找要转发的节点
	public static ArrayList<String> calForwardGroups(String topic,
	                                                 String originator) {
		synchronized (routeObject) {
			ArrayList<String> forwardGroups = new ArrayList<String>();
			MsgSubsForm msf = groupTableRoot;
			boolean thisGroup = false;
			if (originator.equals(groupName) || originator.equals(rep.addr)
					|| fellows.containsKey(originator))
				thisGroup = true;
			String[] splited = topic.split(":");
			String temp = "";
			for (int i = 0; i < splited.length; i++) {
				if (msf.topicChildList.containsKey(splited[i]))
					msf = msf.topicChildList.get(splited[i]);
				else
					break;
				if (thisGroup && msf.routeRoot != null
						&& msf.routeRoot.length() > 0) {
					forwardGroups.add(msf.routeRoot);
				}
				if (i > 0)
					temp += ":";
				temp += splited[i];
				if (brokerTable.containsKey(temp) || clientTable.contains(temp)) {
					if (!msf.routeNext.isEmpty())
						forwardGroups.addAll(msf.routeNext);
					if (!thisGroup)
						break;
				}
			}
			if (forwardGroups.contains(originator)) {
				forwardGroups.remove(originator);
			}
			if (forwardGroups.contains(groupName)) {
				forwardGroups.remove(groupName);
			}
			return forwardGroups;
		}
	}

	// 修改主题树名称
	public static void changeTopicName(String oldName, String newName) {
		String[] old = oldName.split(":");
		String[] nw = newName.split(":");
		MsgSubsForm msf = groupTableRoot;
		MsgSubsForm pre = msf;
		int i;
		for (i = 0; i < old.length; i++) {
			if (msf.topicChildList.containsKey(old[i])) {
				pre = msf;
				msf = msf.topicChildList.get(old[i]);
			} else {
				break;
			}
		}
		if (i < oldName.length()) {
			return;
		}

		MsgSubsForm nmsf = pre.topicChildList.get(old[i]);
		pre.topicChildList.remove(old[i]);
		pre.topicChildList.put(nw[i], nmsf);

		boolean send = false;
		for (String sub : clientTable) {
			if (sub.startsWith(oldName)) {
				clientTable.remove(sub);
				sub.replace(newName, oldName);
				clientTable.add(sub);
				send = true;
			}
		}
		for (String key : brokerTable.keySet()) {
			if (key.startsWith(oldName)) {
				TreeSet<String> brokers = brokerTable.get(key);
				brokerTable.remove(key);
				key.replace(newName, oldName);
				brokerTable.put(key, brokers);
				send = true;
			}
		}

		if (send && RtMgr.rep.addr.equals(localAddr)) {
			RtMgr.getInstance().getState().synLSA();
		}
	}

	public AState getRegState() {
		return regState;
	}

	public AState getRepState() {
		rep.addr = localAddr;
		return repState;
	}

	public AState getState() {
		return state;
	}

	public void setState(AState state) {
		this.state = state;
	}

	public int getUPort() {
		return uPort;
	}

	public String getRepAddr() {
		return rep.addr;
	}

	public void addTarget(String target) {
		dt.addTarget(target);
	}

	public void updateUdpSkt() {
		udpMsgThreadSwitch = false;
		try {
			umt.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
			log.warn(e);
		}
		umt = new Thread(new UdpMsgThread(this));
		udpMsgThreadSwitch = true;
		umt.start();
	}

	public void updateTcpSkt() {
		tcpMsgThreadSwitch = false;
		try {
			tmt.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.warn(e);
		}
		tmt = new Thread(new TcpMsgThread(this));
		tcpMsgThreadSwitch = true;
		tmt.start();
	}

	public void destroy() {
		tmt.interrupt();
		umt.interrupt();
	}

	public void lost(String indicator) {
		state.lost(indicator);
	}

	public void setClock(boolean isRep) {
		state.setClock(isRep);
	}

	public void sendSbp(Object obj) {
		state.sendSbp(obj);
	}

	public void route(String topic) {
		synchronized (routeObject) {
			ir.route(topic);
		}
	}

	public void update(Observable o, Object arg) {// 被上层wsn调用的接口
		NotifyObserverMessage nom = (NotifyObserverMessage) arg;
		int msgKind = nom.getKind();
		log.debug("Msg kind is " + msgKind);
		if (msgKind == 1) {

			if (clientTable.contains(nom.getTopicName()))
				return;
			else {
				synchronized (synObject) {
					clientTable.add(nom.getTopicName());
				}

				// spread it
				MsgSubs mss = new MsgSubs();
				mss.topics.add(nom.getTopicName());
				mss.type = 0;
				mss.originator = localAddr;
				log.debug("A new SubMsg**** Topic:" + nom.getTopicName()
						+ "Content:" + nom.getDoc());

				state.sendSbp(mss);
			}

		} else if (msgKind == 0) {// 取消订阅

			if (clientTable.contains(nom.getTopicName())) {

				synchronized (synObject) {
					clientTable.remove(nom.getTopicName());
				}

				MsgSubs mss = new MsgSubs();
				mss.topics.add(nom.getTopicName());
				mss.type = 1;
				mss.originator = localAddr;
				log.debug("Cancel a SubMsg**** Topic:" + nom.getTopicName());
				state.sendSbp(mss);
			}

		} else {
			// MsgNotis mns = new MsgNotis();
			// Ncount++;
			// mns.sender = localAddr;
			// mns.topicName = nom.getTopicName();
			// mns.doc = nom.getDoc();
			// mns.originatorGroup = groupName;
			// mns.originatorAddr = localAddr;
			// mns.sendDate = new Date();
			// mns.Ccount = Ncount;
			// log.debug("A new NotifyMsg**** Topic:" + nom.getTopicName() +
			// "Content:" + nom.getDoc());
			// state.provideNotisMsg(mns);

			//System.out.println("当前状态是:" + state.toString());

			String doc = nom.getDoc();
			int start = doc.indexOf("<EventType>") + 11;
			int end = doc.indexOf("</EventType>");
			String eventType = doc.substring(start, end);
			//System.out.println("eventType:" + eventType);

			if (eventType.equals("A")) {

				org.Mina.shorenMinaTest.msg.tcp.highPriority mns = new org.Mina.shorenMinaTest.msg.tcp.highPriority();
				mns.sender = localAddr;
				mns.topicName = nom.getTopicName();
				mns.doc = nom.getDoc();
				mns.originatorGroup = groupName;
				mns.originatorAddr = localAddr;
				mns.sendDate = new Date().toString();

				String str = state.toString();

				if (str.contains("RegState")) {

					mns.sender = localAddr;
					ArrayList<String> forwardIp = new ArrayList<String>();
					forwardIp.add(RtMgr.rep.addr);//reg send to rep
					ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, 30008,
							(WsnMsg) mns);
					MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
				} else {
					//	System.out.println("eventType:A:sent" + eventType);
					Start.generateHighPrioriyMsg(mns);//rep send to other rep

				}

			} else if (eventType.equals("B")) {

				org.Mina.shorenMinaTest.msg.tcp.MsgNotis mns = new org.Mina.shorenMinaTest.msg.tcp.MsgNotis();
				mns.sender = localAddr;
				mns.topicName = nom.getTopicName();
				mns.doc = nom.getDoc();
				mns.originatorGroup = groupName;
				mns.originatorAddr = localAddr;
				mns.sendDate = new Date().toString();

				String str = state.toString();

				if (str.contains("RegState")) {

					mns.sender = localAddr;
					ArrayList<String> forwardIp = new ArrayList<String>();
					forwardIp.add(RtMgr.rep.addr);
					ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, 30008,
							(WsnMsg) mns);
					MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
				} else {

					Start.generateMsgNoticeMsg(mns);

				}

			} else if (eventType.equals("C")) {
				org.Mina.shorenMinaTest.msg.tcp.lowPriority mns = new org.Mina.shorenMinaTest.msg.tcp.lowPriority();
				mns.sender = localAddr;
				mns.topicName = nom.getTopicName();
				mns.doc = nom.getDoc();
				mns.originatorGroup = groupName;
				mns.originatorAddr = localAddr;
				mns.sendDate = new Date().toString();

				String str = state.toString();

				if (str.contains("RegState")) {

					mns.sender = localAddr;
					ArrayList<String> forwardIp = new ArrayList<String>();
					forwardIp.add(RtMgr.rep.addr);
					ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, 30008,
							(WsnMsg) mns);
					MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
				} else {

					Start.generateLowPrioriyMsg(mns);

				}

			}

		}
	}

	public int getMqSize() {
		return mq.size();
	}

	// 向队列中添加消息，都放在队尾
	public void addMqLast(MsgNotis msg) {
		try {
			mq.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// 获取排在队列首位的进程任务，并将其移除队列
	public MsgNotis getMqFirst() {
		try {
			return mq.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	// protected synchronized void substractLimit(){
	// poolLimit--;
	// }

	protected synchronized boolean countboolten() {
		if (count < 1)
			return true;
		else
			return false;
	}

	// protected synchronized void limtToHalf() {
	// poolLimit=poolLimit/2;
	// }

	protected synchronized void addLimit() {
		if (poolLimit < poolCount)
			poolLimit++;
	}

	protected synchronized void limtTo1() {
		poolLimit = 1;
	}

	// 添加或删除订阅时总返回ArrayList<String>形式的被改变订阅的标题
	// 以标题带一堆集群的形式加订阅
	public ArrayList<String> AddSubsByTopic(String topic, ArrayList<String> subs) {
		log.debug("new subscription:" + topic + ",groups:" + subs);
		String[] classify = topic.split(":");
		MsgSubsForm msf = groupTableRoot;
		ArrayList<String> changedTopics = new ArrayList<String>();
		for (int i = 0; i < classify.length; i++) {
			if (msf.topicChildList.containsKey(classify[i])) {
				// if the group appears in the parent's subs, it won't appear in
				// the children's
				for (int j = 0; j < subs.size(); j++) {
					String sub = subs.get(j);
					if (msf.topicChildList.get(classify[i]).subs.contains(sub)) {
						subs.remove(sub);
						j--;
					}
				}
			} else {
				MsgSubsForm temp = new MsgSubsForm();
				temp.topicComponent = classify[i];
				msf.topicChildList.put(classify[i], temp);
			}
			if (subs.isEmpty())
				break;
			msf = msf.topicChildList.get(classify[i]);
		}
		boolean changed = false;
		for (int j = 0; j < subs.size(); j++) {
			String sub = subs.get(j);
			if (!msf.subs.contains(sub)) {
				msf.subs.add(sub);
				// 向下删除子节点中此订阅集群
				changed = true;
				for (String change : DeleteRepeatedSub(topic, msf, sub))
					if (!changedTopics.contains(change))
						changedTopics.add(change);
			}
		}
		if (changed) {
			changedTopics.add(topic);
		}
		return changedTopics;
	}

	// 删除孩子节点中此订阅集群
	protected ArrayList<String> DeleteRepeatedSub(String topic,
	                                              MsgSubsForm msf, String group) {
		ArrayList<String> changedTopics = new ArrayList<String>();
		if (!msf.topicChildList.isEmpty())
			for (MsgSubsForm m : msf.topicChildList.values()) {
				if (m.subs.contains(group)) {
					m.subs.remove(group);
					changedTopics.add(topic + ":" + m.topicComponent);
					continue;
				}
				changedTopics.addAll(DeleteRepeatedSub(topic + ":"
						+ m.topicComponent, m, group));
			}

		return changedTopics;
	}

	// 以集群带一堆标题的方式加订阅
	public ArrayList<String> AddSubsByGroup(String group, ArrayList<String> sub1) {
		if (sub1 == null || sub1.isEmpty()) {
			return null;
		}
		ArrayList<String> ts = new ArrayList<String>();
		ts.add(group);
		ArrayList<String> changedTopics = new ArrayList<String>();
		for (String topic : sub1)
			changedTopics.addAll(AddSubsByTopic(topic, ts));
		return changedTopics;
	}

	// 删除一个集群的指定订阅
	public ArrayList<String> DeleteSubsByGroup(String group,
	                                           ArrayList<String> topics) {
		ArrayList<String> changedTopics = new ArrayList<String>();
		for (String topic : topics) {
			String[] classify = topic.split(":");
			MsgSubsForm msf = groupTableRoot;
			int i;
			for (i = 0; i < classify.length; i++) {
				if (!msf.topicChildList.containsKey(classify[i]))
					break;
				msf = msf.topicChildList.get(classify[i]);
			}
			if (i == classify.length) {
				if (msf.subs.contains(group)) {
					msf.subs.remove(group);
					changedTopics.add(topic);
				} else
					changedTopics.addAll(DeleteRepeatedSub(topic, msf, group));
			}
		}
		return changedTopics;
	}

	// 删除一个集群的所有订阅
	public ArrayList<String> DeleteAllSubsOfGroup(String group) {
		return this.DeleteSubsByGroup(group, groupTableRoot, "");
	}

	// 删除一个集群的订阅
	public ArrayList<String> DeleteSubsByGroup(String group, MsgSubsForm root,
	                                           String topic) {
		ArrayList<String> changedTopics = new ArrayList<String>();
		if (root.subs.contains(group)) {
			root.subs.remove(group);
			changedTopics.add(topic);
			return changedTopics;
		}

		if (!root.topicChildList.isEmpty())
			for (MsgSubsForm child : root.topicChildList.values()) {
				if (topic.length() == 0) {
					changedTopics.addAll(DeleteSubsByGroup(group, child,
							child.topicComponent));
				} else
					changedTopics.addAll(DeleteSubsByGroup(group, child, topic
							+ ":" + child.topicComponent));
			}
		return changedTopics;
	}

	// 计算所有订阅节点的路由
	public void CalAllTopicRoute() {
		System.out.println("before calAll " + System.currentTimeMillis());
		this.CalTopicRoute("", groupTableRoot);
		System.out.println("after calAll " + System.currentTimeMillis());
	}

	public void CalPolicyChildrenRoute(String topic) {
		String[] splited = topic.split(":");
		MsgSubsForm msf = groupTableRoot;
		int i;
		for (i = 0; i < splited.length; i++) {
			if (!msf.topicChildList.containsKey(splited[i])) {
				break;
			}
			msf = msf.topicChildList.get(splited[i]);
		}
		if (i == splited.length) {
			CalRoute(topic, msf);
		}
	}

	protected void CalRoute(String topic, MsgSubsForm msf) {
		route(topic);
		if (!msf.topicChildList.isEmpty()) {
			for (MsgSubsForm m : msf.topicChildList.values()) {
				CalRoute(topic + ":" + m.topicComponent, m);
			}
		}
	}

	// 计算订阅节点的路由
	protected void CalTopicRoute(String ex, MsgSubsForm topicRoot) {
		String topic = ex;
		if (topic.length() > 0) {
			topic += ":";
		}
		if (topicRoot.topicComponent != null
				&& topicRoot.topicComponent.length() > 0)
			topic += topicRoot.topicComponent;
		route(topic);

		if (!topicRoot.topicChildList.isEmpty())
			for (MsgSubsForm msf : topicRoot.topicChildList.values()) {
				CalTopicRoute(topic, msf);
			}
	}
}
