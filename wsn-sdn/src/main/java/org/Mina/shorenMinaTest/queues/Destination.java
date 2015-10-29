package org.Mina.shorenMinaTest.queues;

/**
 * Created by root on 15-10-13.
 */
public class Destination {
	protected String addr;
	protected int port;


	public Destination(String addr, int port) {
		this.addr = addr;
		this.port = port;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String toString() {
		return addr + "#" + port;
	}
}
