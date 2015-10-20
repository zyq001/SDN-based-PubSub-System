package org.apache.servicemix.wsn.router.design;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.servicemix.wsn.router.mgr.BrokerUnit;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupMember;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupMember_;

public class PSVirtual_LookUpMem {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ObjectInputStream ois;
		ObjectOutputStream oos;
		try {
			System.out.println("正在监听");
			ServerSocket PSServer=new ServerSocket(2000);
			Socket Server=PSServer.accept();
			System.out.println("连接建立");
			
			ois=new ObjectInputStream(Server.getInputStream());
			oos=new ObjectOutputStream(Server.getOutputStream());
			
			MsgLookupGroupMember require=(MsgLookupGroupMember) ois.readObject();
			System.out.println("接收了请求");
			MsgLookupGroupMember_ rtnMember = new MsgLookupGroupMember_();

			BrokerUnit member1=new BrokerUnit();
			member1.addr="192.168.0.1";
			member1.id=1;
			member1.tPort=65535;
			
			BrokerUnit member2=new BrokerUnit();
			member2.addr="127.0.0.2";
			member2.id=2;
			member2.tPort=65536;
			
		    rtnMember.members.add(member1);
		    rtnMember.members.add(member2);
			
		    oos.writeObject(rtnMember);
		    System.out.println("发送了请求");
		    oos.close();
	        ois.close();
	        Server.close();
	        PSServer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
		
	}

}
