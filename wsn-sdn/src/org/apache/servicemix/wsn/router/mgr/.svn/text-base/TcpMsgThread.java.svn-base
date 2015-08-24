package org.apache.servicemix.wsn.router.mgr;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.wsn.router.mgr.base.AState;
import org.apache.servicemix.wsn.router.mgr.base.SysInfo;

public class TcpMsgThread extends SysInfo implements Runnable {
	private static Log log = LogFactory.getLog(TcpMsgThread.class);
	private RtMgr rt;

	private ServerSocket ss;

	public TcpMsgThread(RtMgr rt) {
		this.rt = rt;

		try {
			ss = new ServerSocket(tPort, 0, InetAddress.getByName(localAddr));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.warn(e);
		}
	}

	public void run() {
		// TODO Auto-generated method stub
		while (tcpMsgThreadSwitch) {
			try {
				Socket s = ss.accept();
				s.setReceiveBufferSize(1024*1024);
				rt.getState().processTcpMsg(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.warn(e);
			}
		}
	}
}
