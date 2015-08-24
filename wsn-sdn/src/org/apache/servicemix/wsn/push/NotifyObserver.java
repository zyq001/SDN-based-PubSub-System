package org.apache.servicemix.wsn.push;

import java.util.Observable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.wsn.jms.JmsSubscription;
import org.apache.servicemix.wsn.push.NotifyObserverMessage;

public class NotifyObserver extends Observable{
	
	private String topicName;	

	private int kind;

	private String doc;

	private NotifyObserverMessage msg;
	
	private static Log log = LogFactory.getLog(NotifyObserver.class);
	
	public NotifyObserver(){
		msg = new NotifyObserverMessage();
	}
//		
//	public NotifyObserver(String topicName,int kind){
//		this.topicName = topicName;
//		this.kind=kind;
//	}
//	public NotifyObserver(String topicName,String doc){
//		this.doc = doc;
//		this.topicName = topicName;
//		this.kind = -1;
//	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
	public void setKind(int kind) {
		this.kind = kind;
	}
	
	public void setDoc(String doc) {
		this.doc = doc;
	}
	
	public void notifyMessage(){
		msg.setTopicName(topicName);
		msg.setKind(kind);
		msg.setDoc(doc);
		
		super.setChanged();
		notifyObservers(msg);
	}
	
}