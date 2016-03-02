package org.Mina.shorenMinaTest.mgr.base;

import org.Mina.shorenMinaTest.mgr.RtMgr;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.apache.mina.core.session.IoSession;

//import org.apache.servicemix.wsn.router.mgr.RtMgr;


public abstract class AState extends SysInfo {

	public RtMgr mgr;
//	public IDt dt;
	
/*	//���뵽����֮��
	abstract public void join();

	//����������Ϣ
	abstract public void sendHrt();

	//���Ͷ�����Ϣ
	abstract public void sendSbp(Object msg);

	//ʧЧ��������Ϊ���Ա�ʶʧЧ�����ַ���
	abstract public void lost(String indicator);

	//����ͬ����Ⱥ���ı����Ϣ
	abstract public void synSubs();*/

	//��������broker�����֪ͨ��Ϣ
//	abstract public void processNotisMsg(Object msg);

	//�������ṩ��֪ͨ��Ϣ
//	abstract public void provideNotisMsg(Object msg);
/*
	//�����յ�udp��Ϣ
	abstract public void processUdpMsg(Object msg);

	//�����յ���tcp����
	abstract public void processTcpMsg(Socket s);*/

	//ת����Ϣ
//	public void forwardQ0Msg(Object qmn){}
/*
	//ת����Ϣ
	abstract public void forwardQ1Msg(Object qmn);

	//ת����Ϣ
	abstract public void forwardQ2Msg(Object qmn);

	//ת����Ϣ
	abstract public void forwardQ3Msg(Object qmn);

	//ת����Ϣ
	abstract public void forwardQ4Msg(Object qmn){}*/

	//ת����Ϣ
//	public void forwardOtherMsg(Object qmn){}

	abstract public void processMsg(IoSession session, WsnMsg msg);

	abstract public void processMsg(WsnMsg msg);
}
