package org.Mina.shorenMinaTest.mgr;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.mgr.base.AConfiguration;
import org.Mina.shorenMinaTest.mgr.base.AState;
import org.Mina.shorenMinaTest.mgr.base.SysInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


//·�ɹ���ģ��
public class RtMgr extends SysInfo {

	private static Log log = LogFactory.getLog(RtMgr.class);
	private static RtMgr INSTANCE = null;
	//mina related
	private static NioSocketAcceptor socketAcceptor;
	private static NioDatagramAcceptor datagramAcceptor;
	private AConfiguration configuration;//����ϵͳ
	private AState regState;//��ͨ����״̬
	private AState repState;//����״̬
	private AState state;//��ǰ״̬
	private Thread tdt;//�����߳�


	private RtMgr() {
		PropertyConfigurator.configure("log4j.properties");

		configuration = new Configuration(this);

		regState = new RegState(this);

		repState = new RepState(this);

		boolean ManagerOn = configuration.configure();

		System.out.println("configuration.localAddr:" + configuration.localAddr +
				";  tport=" + tPort + ";  port=" + uPort);
		//create server
		socketAcceptor = MinaUtil.createSocketAcceptor(localAddr, 30008);
		datagramAcceptor = MinaUtil.createDatagramAcceptor(localAddr, uPort);
	}

	public static NioSocketAcceptor getSocketAcceptor() {
		return socketAcceptor;
	}

	public static void setSocketAcceptor(NioSocketAcceptor acceptor) {
		socketAcceptor = acceptor;
	}

	public static NioDatagramAcceptor getDatagramAcceptor() {
		return datagramAcceptor;
	}

	public static void setDatagramAcceptor(NioDatagramAcceptor acceptor) {
		datagramAcceptor = acceptor;
	}

	public static RtMgr getInstance() {
		if (INSTANCE == null)
			INSTANCE = new RtMgr();
		return INSTANCE;

	}

	public AState getRegState() {
		return regState;
	}

	public AState getRepState() {
		getRep().addr = getLocalAddr();
		return repState;
	}

	public AState getState() {
		return state;
	}

	public void setState(AState state) {
		this.state = state;
	}

	public void updateUdpSkt() {
		try {
			datagramAcceptor.bind(new InetSocketAddress(InetAddress.getByName(localAddr), tPort));
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void updateTcpSkt() {
		try {
			socketAcceptor.bind(new InetSocketAddress(InetAddress.getByName(localAddr), tPort));
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void destroy() {
		tdt.interrupt();
		socketAcceptor.dispose();
		datagramAcceptor.dispose();
	}

	public void add() {
		// TODO Auto-generated method stub

	}
}
