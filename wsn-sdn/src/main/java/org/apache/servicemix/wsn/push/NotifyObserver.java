package org.apache.servicemix.wsn.push;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Observable;

public class NotifyObserver extends Observable {

	private static Log log = LogFactory.getLog(NotifyObserver.class);
	private String topicName;
	private int kind;
	private String doc;
	private NotifyObserverMessage msg;

	public NotifyObserver() {
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

	public void notifyMessage() {
		msg.setTopicName(topicName);
		msg.setKind(kind);
		msg.setDoc(doc);

		super.setChanged();
		notifyObservers(msg);
	}

}