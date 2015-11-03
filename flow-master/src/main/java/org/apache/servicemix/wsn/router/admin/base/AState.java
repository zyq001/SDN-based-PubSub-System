package org.apache.servicemix.wsn.router.admin.base;

import java.net.Socket;

public abstract class AState {

	//发送心跳消息
	abstract public void sendHrt();

	//失效处理，参数为用以标识失效代理字符串
	abstract public void lost(String indicator);

	//发送同步集群订阅表的消息
	abstract public void synTopoInfo();

	//处理收到udp消息
	abstract public void processUdpMsg(Object msg);

	//处理收到的tcp连接
	abstract public void processTcpMsg(Socket s);

}
