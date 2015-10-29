/**
 * @author shoren
 * @date 2013-4-28
 */
package org.Mina.shorenMinaTest.queues;

import org.Mina.shorenMinaTest.msg.WsnMsg;

import java.util.ArrayList;


/**
 *
 */
public class UDPForwardMsg extends ForwardMsg {

	public UDPForwardMsg(Destination dest, WsnMsg msg) {
		super(dest, msg);
	}

	public UDPForwardMsg(String addr, int port, WsnMsg msg) {
		super(addr, port, msg);
	}

	public UDPForwardMsg(ArrayList<String> forwardip, int port, WsnMsg msg) {
		super(forwardip, port, msg);
	}

	//����ͬһ��ַ���͵�ͨ������TCP��UDP
	public String getKeyDest() {
		return super.getKeyDest() + "UDP";
	}
}
