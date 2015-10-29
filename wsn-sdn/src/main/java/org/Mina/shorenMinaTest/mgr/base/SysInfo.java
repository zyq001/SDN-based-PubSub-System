package org.Mina.shorenMinaTest.mgr.base;

import org.Mina.shorenMinaTest.msg.tcp.BrokerUnit;
import org.Mina.shorenMinaTest.msg.tcp.GroupUnit;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


public abstract class SysInfo {

	//����������
	public static String adminAddr;//�����ߵĵ�ַ
	public static int adminPort;//�����ߵ�TCP�˿ں�
	public static String groupName;//����Ⱥ������
	public static int tPort;//����TCP�˿ں�
	public static String localAddr;//��ϵͳ�ĵ�ַ
	public static int connectCount;//���ӳ�����
	//Զ��������
	public static BrokerUnit rep;//����Ⱥ����ĵ�ַ
//	private static long thresholdInitialize;//��ʼ�������ж�������ʱ�ķ�ֵ
//	private static long sendPeriodInitialize;//��ʼ����������������ʱ����
//	private static long scanPeriodInitialize;//��ʼ������ɨ��������ʱ����
//	private static long synPeriodInitialize;//��ʼ���������͸��¶��ĵ�ʱ����
	public static int childrenSize;//����ĺ��ӽڵ������
	public static String multiAddr;//�鲥��ַ
	public static int uPort;//UDP�˿ںţ�ͬʱҲ���鲥�˿ں�
	public static int queueSize;//·��ģ����г���
	public static int poolCount;//�̳߳�����
	public static int joinTimes;//���Լ���Ĵ���
	public static long threshold;//�ж�������ʱ�ķ�ֵ
	public static long sendPeriod;//����������ʱ����
	public static long scanPeriod;//ɨ��������ʱ����
	public static long synPeriod;//���͸��¶��ĵ�ʱ����
	//others,���ϸ�Ϊprotected
	public static ConcurrentHashMap<String, GroupUnit> groupMap;//���浱ǰ�����ڳ��˱���Ⱥ�����м�Ⱥ����Ϣ��keyΪ��Ⱥ��
	public static String parent;//���ڵ㼯Ⱥ������
	public static ArrayList<String> children;//���ӽ�㼯Ⱥ�����ֵļ���
	public static ConcurrentHashMap<String, BrokerUnit> neighbors;//�������ڼ�Ⱥ����������keyΪ�����ַ
	public static int nextInsertChild;//�����ӽڵ���������childrenSizeʱ�����ڱ�ʶ�¼�Ⱥ�ò���ĺ��ӽ����±�
	public static boolean joinOK;//��ʶ�����Ƿ�ɹ�
	public static ArrayList<String> wait4Hrt;//��Ⱥ������ʱʱ�����Ա�Ǹü�Ⱥ������ѡ����
	public static long id;//�����idֵ�����뼯Ⱥʱ�ɴ������
	//���ı�,���ϸ�Ϊprotected
	public static ArrayList<String> clientTable;//���صĶ�����Ϣ,����broker��������ļ���
	public static ConcurrentHashMap<String, TreeSet<String>> brokerTable;//����Ⱥ����������Ķ�����Ϣ��keyΪ���⣬valueΪ���Ĵ���ĵ�ַ
	public static ConcurrentHashMap<String, TreeSet<String>> groupTable;//������Ⱥ�Ķ�����Ϣ��keyΪ���⣬valueΪ���ļ�Ⱥ������
	public static boolean udpMsgThreadSwitch;//���ڿ��ƽ���udp��Ϣ���߳������Ŀ���
	public static boolean tcpMsgThreadSwitch;//���ڿ��ƽ���tcp���ӵ��߳������Ŀ���
	public int EnQueueTime;//���ʱ��

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
