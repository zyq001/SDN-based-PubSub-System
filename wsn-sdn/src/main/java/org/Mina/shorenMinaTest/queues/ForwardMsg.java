/**
 * @author shoren
 * @date 2013-4-26
 */
package org.Mina.shorenMinaTest.queues;

import java.util.ArrayList;

import org.Mina.shorenMinaTest.msg.WsnMsg;



/**
 *
 */
public class ForwardMsg {

	private ArrayList<Destination> destination = new ArrayList<Destination>();
	
	private Destination dest;
	private WsnMsg msg;
	private int priority;
	
	public ForwardMsg(Destination dest, WsnMsg msg){
		this.dest = dest;
		this.msg = msg;
	}
	
	public ForwardMsg(String addr, int port, WsnMsg msg){
		this.dest = new Destination(addr, port);
		this.msg = msg;
	}
	
	public ForwardMsg(ArrayList<String> forwardip, int port, WsnMsg msg){
		for(int i=0;i<forwardip.size();i++){
			this.dest = new Destination(forwardip.get(i), port);
			destination.add(dest);
		}
		this.msg = msg;
	}
	
	public ArrayList<Destination> getDestination(){
		return destination;
	}
	
	public Destination getDest() {
		return dest;
	}

	public void setDest(Destination dest) {
		this.dest = dest;
	}

	public WsnMsg getMsg() {
		return msg;
	}

	public void setMsg(WsnMsg msg) {
		this.msg = msg;
	}
	
	public String getKeyDest(){
		return getDest().toString();
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	
}


