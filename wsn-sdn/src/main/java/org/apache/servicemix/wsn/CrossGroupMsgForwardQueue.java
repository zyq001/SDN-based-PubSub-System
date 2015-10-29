package org.apache.servicemix.wsn;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;

import java.net.InetSocketAddress;
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
		NioDatagramConnector minaConnector = MinaUtil.createDatagramConnector();
		ConnectFuture cf = minaConnector.connect(new InetSocketAddress(dstIP, port));
		cf.awaitUninterruptibly();
		IoSession session = cf.getSession();
		WriteFuture future = session.write(wsnMsg.getMsg());
		return future.isWritten();
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
