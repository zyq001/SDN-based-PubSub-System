package org.Mina.shorenMinaTest.mgr.base;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.msg.tcp.BrokerUnit;
import org.Mina.shorenMinaTest.msg.tcp.GroupUnit;


public abstract class SysInfo {

	//本地配置项
	public static String adminAddr;//管理者的地址
	public static int adminPort;//管理者得TCP端口号
	public static String groupName;//本集群的名字
	public static int tPort;//本地TCP端口号
	public static String localAddr;//本系统的地址
	public static int connectCount;//连接池容量
	public int EnQueueTime;//入队时间
//	private static long thresholdInitialize;//初始化——判断心跳超时的阀值
//	private static long sendPeriodInitialize;//初始化——发送心跳的时间间隔
//	private static long scanPeriodInitialize;//初始化——扫描心跳的时间间隔
//	private static long synPeriodInitialize;//初始化——发送更新订阅的时间间隔

	//远程配置项
	public static BrokerUnit rep;//本集群代表的地址
	public static int childrenSize;//允许的孩子节点的数量
	public static String multiAddr;//组播地址
	public static int uPort;//UDP端口号，同时也是组播端口号
	public static int queueSize;//路由模块队列长度
	public static int poolCount;//线程池容量
	public static int joinTimes;//尝试加入的次数
	public static long threshold;//判断心跳超时的阀值
	public static long sendPeriod;//发送心跳的时间间隔
	public static long scanPeriod;//扫描心跳的时间间隔
	public static long synPeriod;//发送更新订阅的时间间隔
	//others,集合改为protected
	public static ConcurrentHashMap<String, GroupUnit> groupMap;//保存当前拓扑内出了本集群外所有集群的信息，key为集群名
	public static String parent;//父节点集群的名字
	public static ArrayList<String> children;//孩子结点集群的名字的集合
	public static ConcurrentHashMap<String, BrokerUnit> neighbors;//本机所在集群的其他代理，key为代理地址
	public static int nextInsertChild;//当孩子节点数量等于childrenSize时，用于标识新集群该插入的孩子结点的下标
	public static boolean joinOK;//标识加入是否成功
	public static ArrayList<String> wait4Hrt;//集群心跳超时时，用以标记该集群正在重选代表
	public static long id;//代理的id值，加入集群时由代表分配
	//订阅表,集合改为protected
	public static ArrayList<String> clientTable;//本地的订阅信息,本地broker订阅主题的集合
	public static ConcurrentHashMap<String, TreeSet<String>> brokerTable;//本集群里其他代理的订阅信息，key为主题，value为订阅代理的地址
	public static ConcurrentHashMap<String, TreeSet<String>> groupTable;//其他集群的订阅信息，key为主题，value为订阅集群的名字
	public static boolean udpMsgThreadSwitch;//用于控制接收udp消息的线程重启的开关
	public static boolean tcpMsgThreadSwitch;//用于控制接收tcp连接的线程重启的开关
	
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
	
	public static int getQueueSize() {
		return queueSize;
	}
	public static void setQueueSize(int queueSize) {
		SysInfo.queueSize = queueSize;
	}
	
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
	public static int getChildrenSize() {
		return childrenSize;
	}
	public static void setChildrenSize(int childrenSize) {
		SysInfo.childrenSize = childrenSize;
	}
	public static int getJoinTimes() {
		return joinTimes;
	}
	public static void setJoinTimes(int joinTimes) {
		SysInfo.joinTimes = joinTimes;
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
	
	public static String getParent() {
		return parent;
	}
	public static void setParent(String parent) {
		SysInfo.parent = parent;
	}
	
	public static ArrayList<String> getChildren() {
		return children;
	}
	public static void setChildren(ArrayList<String> children) {
		SysInfo.children = children;
	}
	
	public static ConcurrentHashMap<String, BrokerUnit> getNeighbors() {
		return neighbors;
	}
	public static void setNeighbors(ConcurrentHashMap<String, BrokerUnit> neighbors) {
		SysInfo.neighbors = neighbors;
	}
	
	public static int getNextInsertChild() {
		return nextInsertChild;
	}
	public static void setNextInsertChild(int nextInsertChild) {
		SysInfo.nextInsertChild = nextInsertChild;
	}
	
	public static boolean isJoinOK() {
		return joinOK;
	}
	public static void setJoinOK(boolean joinOK) {
		SysInfo.joinOK = joinOK;
	}
	
	public static ArrayList<String> getWait4Hrt() {
		return wait4Hrt;
	}
	public static void setWait4Hrt(ArrayList<String> wait4Hrt) {
		SysInfo.wait4Hrt = wait4Hrt;
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
	public static void setBrokerTable(ConcurrentHashMap<String, TreeSet<String>> brokerTable) {
		SysInfo.brokerTable = brokerTable;
	}
	

	public static ConcurrentHashMap<String, TreeSet<String>> getGroupTable() {
		return groupTable;
	}
	public static void setGroupTable(ConcurrentHashMap<String, TreeSet<String>> groupTable) {
		SysInfo.groupTable = groupTable;
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

}
