/**
 * @author shoren
 * @date 2013-4-26
 */
package org.Mina.shorenMinaTest.msg.tcp;

import java.io.Serializable;
import java.util.ArrayList;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.apache.mina.core.session.IoSession;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.handlers.Start;
import org.Mina.shorenMinaTest.mgr.base.SysInfo;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.TCPForwardMsg;
import org.Mina.shorenMinaTest.queues.UDPForwardMsg;
import org.Mina.shorenMinaTest.router.searchRoute;


/**
 *使用修饰模式，将原有的msg包装称为紧急信息
 */
@SuppressWarnings("serial")
public class EmergencyMsg extends WsnMsg implements Serializable {
	protected WsnMsg msg;
	
	public WsnMsg getMsg() {
		return msg;
	}

	public void setMsg(WsnMsg msg) {
		this.msg = msg;
	}

	public EmergencyMsg(){
		this.msg = new WsnMsg();
	}
	
	public EmergencyMsg(WsnMsg msg){
		this.msg = msg;
	}
	
	public String msgToString() {
		StringBuffer content = new StringBuffer();
		Class type = this.getClass();
		String className = type.getName();
		if(type != null)
		{
			content.append("className=" + className + ";");
			content.append(MinaUtil.msgToString(msg));
		}
		return content.toString();
	}
	
	//将String转化为msg类，用于接收后反序列化
	public WsnMsg stringToMsg(String smsg) {
		
		if(smsg.contains("className=")){
        	//生成消息实例
            int index1 = smsg.indexOf("=");
            int index2 = smsg.indexOf(";");
            String className = smsg.substring(index1+1, index2);
            smsg = smsg.substring(index2+1);  //消息类容
            WsnMsg msg = null;
			try {
				msg = (WsnMsg) Class.forName(className).newInstance();
			} catch (InstantiationException e) {
				
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
			
				e.printStackTrace();
			} //得到实例
            if(smsg!=null && smsg != "" && msg != null){
            	msg.stringToMsg(smsg);  //为各个域赋值
            	this.setMsg(msg);
            }
		}
	//	System.out.println("there is no method defined for this class!");
		return this;
	}
	
	//获取目的节点的IP地址，做转发用
	private ArrayList<String> getForwardIp(){
		
		return Start.forwardIP=searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);

	}
	
	//查询转发节点的ip地址列表，根据列表中的ip来做转发
	public void processRegMsg(IoSession session){
		
		ArrayList<String> forwardIp = getForwardIp();
		//策略库的位置，由策略库来过滤ip

		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);

	}
	
	public void processRepMsg(IoSession session){
		
		ArrayList<String> forwardIp = getForwardIp();
		//System.out.println(forwardIp);
		//策略库的位置，由策略库来过滤ip

		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);

	}
}
