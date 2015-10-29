package org.apache.servicemix.wsn.router.mgr;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SendSocket {
	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;

	public SendSocket() {
		this.socket = null;
		this.input = null;
		this.output = null;
	}

	public void setSocket(Socket socket, ObjectOutputStream output, ObjectInputStream input) {
		this.socket = socket;
		this.input = input;
		this.output = output;

	}

	public Socket getSocket() {
		return socket;

	}

	public ObjectOutputStream getOutputStream() {
		return output;

	}

	public ObjectInputStream getInputStream() {
		return input;

	}

}
