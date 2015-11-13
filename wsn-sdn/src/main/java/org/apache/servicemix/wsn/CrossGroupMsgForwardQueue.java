package org.apache.servicemix.wsn;

import org.Mina.shorenMinaTest.queues.ForwardMsg;

import java.net.DatagramPacket;
import java.net.Inet6Address;
import java.net.MulticastSocket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by root on 15-10-8.
 */
public class CrossGroupMsgForwardQueue extends Thread {

	private static CrossGroupMsgForwardQueue INSTANCE;
	public BlockingQueue<ForwardMsg> queue;

	private CrossGroupMsgForwardQueue() {
		queue = new LinkedBlockingDeque<ForwardMsg>();
	}

	public static CrossGroupMsgForwardQueue grtInstance() {
		if (INSTANCE == null) INSTANCE = new CrossGroupMsgForwardQueue();
		return INSTANCE;
	}

	public boolean enqueque(ForwardMsg wsnmsg) {
		boolean success = false;
		success = queue.offer(wsnmsg);
		return success;
	}

	public boolean enqueque(List<ForwardMsg> wsnmsgs) {
		boolean success = false;
		for (ForwardMsg wsnMsg : wsnmsgs) {
			//atmost try twice;
			if (!enqueque(wsnMsg)) success = enqueque(wsnMsg);
		}
		return success;
	}

	private boolean processMsg(ForwardMsg wsnMsg) {
		if (wsnMsg == null || wsnMsg.getDest() == null) return false;
		String dstIP = wsnMsg.getDest().getAddr();
		int port = wsnMsg.getDest().getPort();
//		NioDatagramConnector minaConnector = MinaUtil.createDatagramConnector();
//		ConnectFuture cf = minaConnector.connect(new InetSocketAddress(dstIP, port));
//		cf.awaitUninterruptibly();
//		IoSession session = cf.getSession();
//		WriteFuture future = session.write(wsnMsg.getMsg());
//		return future.isWritten();
		try {
//			byte[] msg = new byte[] { 'h', 'e', 'l', 'l', 'o' };
			String add = "";
//			if(topic2Addr.containsKey(wsnMsg.getKeyDest().))
			Inet6Address inetAddress = (Inet6Address) Inet6Address.getByName(wsnMsg.getDest().getAddr());//��������������������IP��ַ
			byte[] msg = wsnMsg.getMsg().msgToString().getBytes();
			DatagramPacket datagramPacket = new DatagramPacket(msg, msg.length, inetAddress, 7777);//���ݰ�������Ϣ���ݣ���Ϣ���ȣ��ಥIP�Ͷ˿�
			MulticastSocket multicastSocket = new MulticastSocket();
			multicastSocket.send(datagramPacket);//�������ݰ�
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	public void run() {
		while (true) {
			try {
				ForwardMsg msg = queue.take();
				if (!processMsg(msg)) {
					queue.put(msg);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
