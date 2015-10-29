package org.apache.servicemix.wsn.router.admin;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class TcpMsgThread implements Runnable {
	private AdminMgr Amgr;

	private ServerSocket ss;

	public TcpMsgThread(AdminMgr Amgr) {
		this.Amgr = Amgr;

		try {
			ss = new ServerSocket(Amgr.tPort, 0, InetAddress.getByName(Amgr.localAddr));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {

				Socket s = ss.accept();
				if (Amgr.getState() != null) {
					Amgr.getState().processTcpMsg(s);
				}
				try {
					s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}