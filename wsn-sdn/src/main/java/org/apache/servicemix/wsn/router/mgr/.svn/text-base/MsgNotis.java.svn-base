package org.apache.servicemix.wsn.router.mgr;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Date;

import org.apache.servicemix.wsn.router.mgr.base.AState;
import org.apache.servicemix.wsn.router.msg.tcp.MsgAdReboot_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgAdminChange;

public class MsgNotis implements Serializable {

	private static final long serialVersionUID = 1L;

	public String sender;//转发者的信息
	
	public String originatorGroup;//提供通知的broker所在集群名字
	
	public String originatorAddr;//提供通知的broker的IP地址
	
	public String topicName;//通知主题
	
	public String doc;//通知内容
	
	public Date sendDate;//消息产生的时间

	
	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, MsgNotis mn) {
		AState state = RtMgr.getInstance().getState();
		if(mn.originatorAddr.equals(state.getAdminAddr())) {
			return;
		}
		System.out.println("Administrator has changed");
		MsgAdReboot_ m = new MsgAdReboot_();
		m.c.addAll(state.getGroupMap().values());
		m.self.addr = state.getLocalAddr();
		m.self.name = state.getGroupName();
		m.self.tPort = state.gettPort();
		m.self.uPort = state.getuPort();
		try {
			oos.writeObject(m);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MsgAdminChange mac = new MsgAdminChange();
		mac.NewAdminAddr = mn.originatorAddr;
		state.spreadInLocalGroup(mac);
		state.sendObjectToNeighbors(mac);
	}

}
