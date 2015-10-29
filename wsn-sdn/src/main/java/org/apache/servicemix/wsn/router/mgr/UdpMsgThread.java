package org.apache.servicemix.wsn.router.mgr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.wsn.router.mgr.base.SysInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

public class UdpMsgThread extends SysInfo implements Runnable {
	private static Log log = LogFactory.getLog(UdpMsgThread.class);
	private RtMgr rt;
	private MulticastSocket s;
	private byte[] buf = new byte[4096];
	private DatagramPacket p;
	private ObjectInputStream ois;
	private ByteArrayInputStream bais;

	public UdpMsgThread(RtMgr rt) {
		this.rt = rt;

		try {

			s = new MulticastSocket(new InetSocketAddress(localAddr, uPort));
//			System.out.println("local address: " + localAddr + "	port: " + uPort);
			log.info("local address: " + localAddr + "	port: " + uPort);
			s.joinGroup(InetAddress.getByName(multiAddr));
//			System.out.println("multicast address: " + multiAddr);
			log.info("multicast address: " + multiAddr);
			s.setLoopbackMode(true);
			s.setReceiveBufferSize(1024 * 1024);

			p = new DatagramPacket(buf, buf.length);

		} catch (IOException e) {
			e.printStackTrace();
			log.warn(e);
		}

	}

	public void run() {
		while (udpMsgThreadSwitch) {
			try {
				bais = new ByteArrayInputStream(buf);
				s.receive(p);
				ois = new ObjectInputStream(bais);

				Object msg = ois.readObject();

				rt.getState().processUdpMsg(msg);

			} catch (IOException e) {
				log.warn(e);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				log.warn(e);
			}
		}
	}
}
