/**
 * @author shoren
 * @date 2013-4-26
 */
package org.Mina.shorenMinaTest.msg.tcp;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.handlers.Start;
import org.Mina.shorenMinaTest.mgr.base.SysInfo;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.TCPForwardMsg;
import org.Mina.shorenMinaTest.router.searchRoute;
import org.apache.mina.core.session.IoSession;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * ʹ������ģʽ����ԭ�е�msg��װ��Ϊ������Ϣ
 */
@SuppressWarnings("serial")
public class EmergencyMsg extends WsnMsg implements Serializable {
	protected WsnMsg msg;

	public EmergencyMsg() {
		this.msg = new WsnMsg();
	}

	public EmergencyMsg(WsnMsg msg) {
		this.msg = msg;
	}

	public WsnMsg getMsg() {
		return msg;
	}

	public void setMsg(WsnMsg msg) {
		this.msg = msg;
	}

	public String msgToString() {
		StringBuffer content = new StringBuffer();
		Class type = this.getClass();
		String className = type.getName();
		if (type != null) {
			content.append("className=" + className + ";");
			content.append(MinaUtil.msgToString(msg));
		}
		return content.toString();
	}

	//��Stringת��Ϊmsg�࣬���ڽ��պ����л�
	public WsnMsg stringToMsg(String smsg) {

		if (smsg.contains("className=")) {
			//������Ϣʵ��
			int index1 = smsg.indexOf("=");
			int index2 = smsg.indexOf(";");
			String className = smsg.substring(index1 + 1, index2);
			smsg = smsg.substring(index2 + 1);  //��Ϣ����
			WsnMsg msg = null;
			try {
				msg = (WsnMsg) Class.forName(className).newInstance();
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (ClassNotFoundException e) {

				e.printStackTrace();
			} //�õ�ʵ��
			if (smsg != null && smsg != "" && msg != null) {
				msg.stringToMsg(smsg);  //Ϊ������ֵ
				this.setMsg(msg);
			}
		}
		//	System.out.println("there is no method defined for this class!");
		return this;
	}

	//��ȡĿ�Ľڵ��IP��ַ����ת����
	private ArrayList<String> getForwardIp() {

		return Start.forwardIP = searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);

	}

	//��ѯת���ڵ��ip��ַ�б������б��е�ip����ת��
	public void processRegMsg(IoSession session) {

		ArrayList<String> forwardIp = getForwardIp();
		//���Կ��λ�ã��ɲ��Կ�������ip

		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);

	}

	public void processRepMsg(IoSession session) {

		ArrayList<String> forwardIp = getForwardIp();
		//System.out.println(forwardIp);
		//���Կ��λ�ã��ɲ��Կ�������ip

		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);

	}
}
