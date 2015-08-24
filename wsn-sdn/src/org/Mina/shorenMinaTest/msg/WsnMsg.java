/**
 * @author shoren
 * @date 2013-6-5
 */
package org.Mina.shorenMinaTest.msg;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.mgr.RtMgr;


/**
 *
 */
public class WsnMsg {
	
	protected static Log log = LogFactory.getLog(WsnMsg.class);
	
	//将msg类转化为String，用于传输前序列化
	public String msgToString() {
	//	System.out.println("there is no method defined for this class!");
		return MinaUtil.msgToString(this);
	}

	//将String转化为msg类，用于接收后反序列化
	public WsnMsg stringToMsg(String smsg) {
	//	System.out.println("there is no method defined for this class!");
		return MinaUtil.stringToMsg(smsg, this);
	}
	
	public void processRegMsg(IoSession session){
		
		//AState state = RtMgr.getInstance().getState();
		System.out.println("there is no method defined for this class!");
	}
	
	public void processRepMsg(IoSession session){
		
		//AState state = RtMgr.getInstance().getState();
		System.out.println("there is no method defined for this class!");
	}
	
	//获得信息类的优先级
	public int getPriority(WsnMsg msg)
	{
	//	return FilterRegistry.getPriority(msg);
		return -1;
	}
}
