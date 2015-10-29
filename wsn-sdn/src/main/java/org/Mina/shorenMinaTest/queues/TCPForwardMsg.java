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
public class TCPForwardMsg extends ForwardMsg {

	public TCPForwardMsg(Destination dest, WsnMsg msg) {
		super(dest, msg);

	}

	public TCPForwardMsg(String addr, int port, WsnMsg msg) {
		super(addr, port, msg);
	}

	public TCPForwardMsg(ArrayList<String> forwardip, int port, WsnMsg msg) {
		super(forwardip, port, msg);
	}

	//����ͬһ��ַ���͵�ͨ������TCP��UDP
	public String getKeyDest() {
		return super.getKeyDest() + "TCP";
	}

}
