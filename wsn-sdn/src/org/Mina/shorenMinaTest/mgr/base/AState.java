package org.Mina.shorenMinaTest.mgr.base;

import java.net.Socket;

import org.apache.mina.core.session.IoSession;

import org.Mina.shorenMinaTest.mgr.RtMgr;
//import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.Mina.shorenMinaTest.msg.WsnMsg;


public abstract class AState extends SysInfo {

	public RtMgr mgr;
//	public IDt dt;
	
/*	//加入到拓扑之中
	abstract public void join();

	//发送心跳消息
	abstract public void sendHrt();

	//发送订阅消息
	abstract public void sendSbp(Object msg);

	//失效处理，参数为用以标识失效代理字符串
	abstract public void lost(String indicator);

	//发送同步集群订阅表的消息
	abstract public void synSubs();*/

	//处理其他broker传入的通知消息
//	abstract public void processNotisMsg(Object msg);

	//处理本地提供的通知消息
//	abstract public void provideNotisMsg(Object msg);
/*
	//处理收到udp消息
	abstract public void processUdpMsg(Object msg);

	//处理收到的tcp连接
	abstract public void processTcpMsg(Socket s);*/

	//转发消息
//	public void forwardQ0Msg(Object qmn){}
/*
	//转发消息
	abstract public void forwardQ1Msg(Object qmn);

	//转发消息
	abstract public void forwardQ2Msg(Object qmn);

	//转发消息
	abstract public void forwardQ3Msg(Object qmn);

	//转发消息
	abstract public void forwardQ4Msg(Object qmn){}*/

	//转发消息
//	public void forwardOtherMsg(Object qmn){}
	
	abstract public void processMsg(IoSession session, WsnMsg msg);
}
