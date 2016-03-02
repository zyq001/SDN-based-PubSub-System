/**
 * @author shoren
 * @date 2013-6-5
 */
package org.Mina.shorenMinaTest.msg;


import org.Mina.shorenMinaTest.MinaUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;


/**
 *
 */
public class WsnMsg {

	protected static Log log = LogFactory.getLog(WsnMsg.class);

	//��msg��ת��ΪString�����ڴ���ǰ���л�
	public String msgToString() {
		//	System.out.println("there is no method defined for this class!");
		return MinaUtil.msgToString(this);
	}

	//��Stringת��Ϊmsg�࣬���ڽ��պ����л�
	public WsnMsg stringToMsg(String smsg) {
		//	System.out.println("there is no method defined for this class!");
		return MinaUtil.stringToMsg(smsg, this);
	}

	public void processRegMsg(IoSession session) {

		//AState state = RtMgr.getInstance().getState();
		System.out.println("there is no method defined for this class!");
	}

	public void processRepMsg(IoSession session) {

		//AState state = RtMgr.getInstance().getState();
		System.out.println("there is no method defined for this class!");
	}

	//�����Ϣ������ȼ�
	public int getPriority(WsnMsg msg) {
		//	return FilterRegistry.getPriority(msg);
		return -1;
	}
}
