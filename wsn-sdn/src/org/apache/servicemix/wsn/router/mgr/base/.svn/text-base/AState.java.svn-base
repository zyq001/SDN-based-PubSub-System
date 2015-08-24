package org.apache.servicemix.wsn.router.mgr.base;

import java.net.Socket;

import org.apache.servicemix.wsn.router.msg.tcp.LSA;

public abstract class AState extends SysInfo {

	//加入到拓扑之中
	abstract public void join();

	//发送订阅消息
	abstract public void sendSbp(Object msg);
	
	// 增加hello试探邻居
	abstract public void addNeighbor(String target);

	//失效处理，参数为用以标识失效代理字符串
	abstract public void lost(String indicator);
	
	// 设置代表的同步和检查LSA计时器
	abstract public void setClock(boolean isRep);

	//处理收到udp消息
	abstract public void processUdpMsg(Object msg);

	//处理收到的tcp连接
	abstract public void processTcpMsg(Socket s);

	//转发给邻居
	abstract public void sendObjectToNeighbors(Object obj);
	
	// 集群内组播LSA
	abstract public void spreadLSAInLocalGroup(LSA lsa);
	
	//在本集群内组播
	abstract public void spreadInLocalGroup(Object obj);
	
	//添加LSA
	abstract public boolean addLSAToLSDB(LSA lsa);
	
	// 同步LSA
	abstract public void synLSA();
}
