package org.apache.servicemix.wsn.router.admin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

public class UdpMsgThread implements Runnable {
	private AdminMgr Amgr;
	private MulticastSocket s;
	private byte[] buf = new byte[4096];
	private DatagramPacket p;
	private ObjectInputStream ois;
	private ByteArrayInputStream bais;

	public UdpMsgThread(AdminMgr Amgr) {
		this.Amgr = Amgr;

		try {

			s = new MulticastSocket(new InetSocketAddress(Amgr.localAddr, Amgr.uPort));
			System.out.println("local address: " + Amgr.localAddr + "	uport: " + Amgr.uPort);
			s.setLoopbackMode(true);

			p = new DatagramPacket(buf, buf.length);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {

				bais = new ByteArrayInputStream(buf);
				s.receive(p);
				ois = new ObjectInputStream(bais);

				Object msg = (Object) ois.readObject();
				if (msg != null) {
					Amgr.getState().processUdpMsg(msg);

				}


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
