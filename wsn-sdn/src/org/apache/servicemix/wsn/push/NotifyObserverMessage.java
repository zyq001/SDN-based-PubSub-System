package org.apache.servicemix.wsn.push;


public class NotifyObserverMessage{
	
	private String topicName;
	
	private int kind = -1;
	
	private String doc;
	
	public String getDoc(){
		return this.doc;
	}
	
	public void setDoc(String value){
		this.doc = value;
	}
	
	
	
	public String getTopicName(){
		return topicName;
	}
	
	public void setTopicName(String value){
		this.topicName = value;
	}
	
	public int getKind(){
		return this.kind;
	}
	
	public void setKind(int value){
		this.kind = value;
	}
	
	public String toString(){
		
		if(doc!=null){
			return "[Notificationg Message]\n"
					+doc.toString();
		}
		else{
			if(kind==1)
				return "[New Subscription]"
						+ "The topic name: "
						+ this.getTopicName();
			if(kind==0)
				return "[Unsubscribe Subscirption]"
						+ "The topic name: "
						+ this.getTopicName();
		}
		
		return null;
	}
}