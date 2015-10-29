package org.Mina.shorenMinaTest.msg.tcp;

import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.TCPForwardMsg;
import org.apache.mina.core.session.IoSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


@SuppressWarnings("serial")
public class MsgNotis extends WsnMsg implements Serializable {

	public String sender;//ת���ߵ���Ϣ

	public String originatorGroup;//�ṩ֪ͨ��broker���ڼ�Ⱥ����

	public String originatorAddr;//�ṩ֪ͨ��broker��IP��ַ

	public String topicName;//֪ͨ����

	public String doc;//֪ͨ����


	public String sendDate;//��Ϣ������ʱ��
//
//	public int Ccount;//������Ϣ
	
/*	private ArrayList<String> getForwardIp(){
		return Start.forwardIP=searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);
	}*/

	private ArrayList<String> getForwardIp() {

		ArrayList<String> ret = org.apache.servicemix.wsn.router.mgr.RtMgr.calForwardGroups(this.topicName,
				this.originatorGroup);
		Iterator<String> it = ret.iterator();
		ArrayList<String> forwardIP = new ArrayList<String>();
		while (it.hasNext()) {
			String itNext = it.next();
			//System.out.println("@@@@@@@@@@@@@@@@@@@@@:"+org.apache.servicemix.wsn.router.mgr.base.SysInfo.groupMap.get(itNext).addr);
			String addr = org.apache.servicemix.wsn.router.mgr.base.SysInfo.groupMap.get(itNext).addr;
			forwardIP.add(addr);
		}

		return forwardIP;//Start.forwardIP=searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);
	}

	public void processRegMsg(IoSession session) {
		ArrayList<String> forwardIp = getForwardIp();
		//���Կ��λ�ã��ɲ��Կ�������ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, 30008, this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
	}

	public void processRepMsg(IoSession session) {
		ArrayList<String> forwardIp = getForwardIp();
		//���Կ��λ�ã��ɲ��Կ�������ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, 30008, this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
	}

}
