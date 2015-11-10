package org.apache.servicemix.wsn;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;

import java.net.DatagramPacket;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
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
			Inet6Address inetAddress = (Inet6Address) Inet6Address.getByName(wsnMsg.getDest().getAddr());//根据主机名返回主机的IP地址
			byte[] msg = wsnMsg.getMsg().msgToString().getBytes();
			DatagramPacket datagramPacket = new DatagramPacket(msg, msg.length,inetAddress, 7777);//数据包包含消息内容，消息长度，多播IP和端口
			MulticastSocket multicastSocket = new MulticastSocket();
			multicastSocket.send(datagramPacket);//发送数据包
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
