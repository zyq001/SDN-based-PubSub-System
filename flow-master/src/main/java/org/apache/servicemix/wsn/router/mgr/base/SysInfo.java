package org.apache.servicemix.wsn.router.mgr.base;

import org.apache.servicemix.wsn.router.mgr.BrokerUnit;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

//import org.apache.servicemix.wsn.router.mgr.calNeighbor.NeigBuild;
//import org.apache.servicemix.wsn.router.msg.tcp.LSA;

public abstract class SysInfo {

	public static String groupName;//本集群的名字
	public static String localAddr;//本系统的地址
	public static String multiAddr;//组播地址
	public static int uPort;//UDP端口号，同时也是组播端口号
	public static long threshold;//判断心跳超时的阀值
	public static long sendPeriod;//发送心跳的时间间隔
	public static long scanPeriod;//扫描心跳的时间间隔
	public static long synPeriod;//发送更新订阅的时间间隔
	//others
	public static ConcurrentHashMap<String, GroupUnit> groupMap;//保存当前拓扑内出了本集群外所有集群的信息，key为集群名
	//	protected static long thresholdInitialize;//初始化——判断心跳超时的阀值
//	protected static long sendPeriodInitialize;//初始化——发送心跳的时间间隔
//	protected static long scanPeriodInitialize;//初始化——扫描心跳的时间间隔
//	protected static long synPeriodInitialize;//初始化——发送更新订阅的时间间隔
	public static ConcurrentHashMap<String, BrokerUnit> fellows;//本机所在集群的其他代理，key为代理地址
	//订阅表
	public static ArrayList<String> clientTable;//本地的订阅信息,本地broker订阅主题的集合
	public static ConcurrentHashMap<String, TreeSet<String>> brokerTable;//本集群里其他代理的订阅信息，key为主题，value为订阅代理的地址
	public static String groupController;
	public static String globalController;
	//本地配置项
	protected static String adminAddr;//管理者的地址
	protected static int adminPort;//管理者得TCP端口号
	protected static int tPort;//本地TCP端口号
	protected static String localNetmask; //本地子网掩码
	protected static int queueSize;//路由模块队列长度
	protected static int poolCount;//线程池容量
	protected static int connectCount;//连接池容量
	//远程配置项
	protected static BrokerUnit rep;//本集群代表的地址
	protected static int neighborSize;//允许的孩子节点的数量
	protected static int joinTimes;//尝试加入的次数
	protected static ArrayList<String> neighbors; //邻居集群
	protected static int lsaSeqNum; //LSA的序列号
	protected static long policyTime; // 策略消息的更新时间
	protected static long treeTime; // 树更新时间
	//	protected static ConcurrentHashMap<String, LSA> lsdb; //LSA数据库，以集群名称标示该集群发出的LSA消息
//	protected static LSA cacheLSA; //缓存LSA，缓存需要发送的LSA数据
	protected static String askMsg; // 请求LSDB和策略消息发送的字符串
	protected static boolean joinOK;//标识加入是否成功
	protected static ArrayList<String> waitHello;//集群hello超时时，用以标记该集群正在重选代表
	protected static long id;//代理的id值，加入集群时由代表分配
	protected static MsgSubsForm groupTableRoot; //其他集群订阅树的根

	//	protected static NeigBuild nb; // 邻居选择模块
	protected static boolean udpMsgThreadSwitch;//用于控制接收udp消息的线程重启的开关
	protected static boolean tcpMsgThreadSwitch;//用于控制接收tcp连接的线程重启的开关

	public static String getAdminAddr() {
		return adminAddr;
	}

	public static void setAdminAddr(String adminAddr) {
		SysInfo.adminAddr = adminAddr;
	}

	public static int getAdminPort() {
		return adminPort;
	}

	public static void setAdminPort(int adminPort) {
		SysInfo.adminPort = adminPort;
	}

	public static String getGroupName() {
		return groupName;
	}

	public static void setGroupName(String groupName) {
		SysInfo.groupName = groupName;
	}

	public static int gettPort() {
		return tPort;
	}

	public static void settPort(int tPort) {
		SysInfo.tPort = tPort;
	}

	public static String getLocalAddr() {
		return localAddr;
	}

	public static void setLocalAddr(String localAddr) {
		SysInfo.localAddr = localAddr;
	}

	public static String getLocalNetmask() {
		return localNetmask;
	}

	public static void setLocalNetmask(String localNetmask) {
		SysInfo.localNetmask = localNetmask;
	}

	public static int getQueueSize() {
		return queueSize;
	}

	public static void setQueueSize(int queueSize) {
		SysInfo.queueSize = queueSize;
	}

	public static int getPoolCount() {
		return poolCount;
	}

	public static void setPoolCount(int poolCount) {
		SysInfo.poolCount = poolCount;
	}

	public static int getConnectCount() {
		return connectCount;
	}

	public static void setConnectCount(int connectCount) {
		SysInfo.connectCount = connectCount;
	}

	public static BrokerUnit getRep() {
		return rep;
	}

	public static void setRep(BrokerUnit rep) {
		SysInfo.rep = rep;
	}

	public static int getNeighborSize() {
		return neighborSize;
	}

	public static void setNeighborSize(int neighborSize) {
		SysInfo.neighborSize = neighborSize;
	}

	public static String getMultiAddr() {
		return multiAddr;
	}

	public static void setMultiAddr(String multiAddr) {
		SysInfo.multiAddr = multiAddr;
	}

	public static int getuPort() {
		return uPort;
	}

	public static void setuPort(int uPort) {
		SysInfo.uPort = uPort;
	}

	public static int getJoinTimes() {
		return joinTimes;
	}

	public static void setJoinTimes(int joinTimes) {
		SysInfo.joinTimes = joinTimes;
	}

	public static long getThreshold() {
		return threshold;
	}

	public static void setThreshold(long threshold) {
		SysInfo.threshold = threshold;
	}

	public static long getSendPeriod() {
		return sendPeriod;
	}

	public static void setSendPeriod(long sendPeriod) {
		SysInfo.sendPeriod = sendPeriod;
	}

	public static long getScanPeriod() {
		return scanPeriod;
	}

	public static void setScanPeriod(long scanPeriod) {
		SysInfo.scanPeriod = scanPeriod;
	}

	public static long getSynPeriod() {
		return synPeriod;
	}

	public static void setSynPeriod(long synPeriod) {
		SysInfo.synPeriod = synPeriod;
	}

	public static ConcurrentHashMap<String, GroupUnit> getGroupMap() {
		return groupMap;
	}

	public static void setGroupMap(ConcurrentHashMap<String, GroupUnit> groupMap) {
		SysInfo.groupMap = groupMap;
	}

	public static ArrayList<String> getNeighbors() {
		return neighbors;
	}

	public static void setNeighbors(ArrayList<String> neighbors) {
		SysInfo.neighbors = neighbors;
	}

	public static ConcurrentHashMap<String, BrokerUnit> getFellows() {
		return fellows;
	}

	public static void setFellows(ConcurrentHashMap<String, BrokerUnit> fellows) {
		SysInfo.fellows = fellows;
	}

	public static int getLsaSeqNum() {
		return lsaSeqNum;
	}

	public static void setLsaSeqNum(int lsaSeqNum) {
		SysInfo.lsaSeqNum = lsaSeqNum;
	}

//	public static ConcurrentHashMap<String, LSA> getLsdb() {
//		return lsdb;
//	}
//
//	public static void setLsdb(ConcurrentHashMap<String, LSA> lsdb) {
//		SysInfo.lsdb = lsdb;
//	}

//	public static LSA getCacheLSA() {
//		return cacheLSA;
//	}
//
//	public static void setCacheLSA(LSA cacheLSA) {
//		SysInfo.cacheLSA = cacheLSA;
//	}

	public static String getAskMsg() {
		return askMsg;
	}

	public static void setAskMsg(String askMsg) {
		SysInfo.askMsg = askMsg;
	}

	public static boolean isJoinOK() {
		return joinOK;
	}

	public static void setJoinOK(boolean joinOK) {
		SysInfo.joinOK = joinOK;
	}

	public static ArrayList<String> getWaitHello() {
		return waitHello;
	}

	public static void setWaitHello(ArrayList<String> waitHello) {
		SysInfo.waitHello = waitHello;
	}

	public static long getId() {
		return id;
	}

	public static void setId(long id) {
		SysInfo.id = id;
	}

	public static ArrayList<String> getClientTable() {
		return clientTable;
	}

	public static void setClientTable(ArrayList<String> clientTable) {
		SysInfo.clientTable = clientTable;
	}

	public static ConcurrentHashMap<String, TreeSet<String>> getBrokerTable() {
		return brokerTable;
	}

	public static void setBrokerTable(
			ConcurrentHashMap<String, TreeSet<String>> brokerTable) {
		SysInfo.brokerTable = brokerTable;
	}

	public static MsgSubsForm getGroupTableRoot() {
		return groupTableRoot;
	}

	public static void setGroupTableRoot(MsgSubsForm groupTableRoot) {
		SysInfo.groupTableRoot = groupTableRoot;
	}

	public static boolean isUdpMsgThreadSwitch() {
		return udpMsgThreadSwitch;
	}

	public static void setUdpMsgThreadSwitch(boolean udpMsgThreadSwitch) {
		SysInfo.udpMsgThreadSwitch = udpMsgThreadSwitch;
	}

	public static boolean isTcpMsgThreadSwitch() {
		return tcpMsgThreadSwitch;
	}

	public static void setTcpMsgThreadSwitch(boolean tcpMsgThreadSwitch) {
		SysInfo.tcpMsgThreadSwitch = tcpMsgThreadSwitch;
	}

//	public static NeigBuild getNb() {
//		return nb;
//	}
//
//	public static void setNb(NeigBuild nb) {
//		SysInfo.nb = nb;
//	}

	public static long getPolicyTime() {
		return policyTime;
	}

	public static void setPolicyTime(long policyTime) {
		SysInfo.policyTime = policyTime;
	}

	public static long getTreeTime() {
		return treeTime;
	}

	public static void setTreeTime(long treeTime) {
		SysInfo.treeTime = treeTime;
	}

}
