package org.apache.servicemix.wsn.router.mgr;

public class Subscription {
	
	private String subscriberURI;//订阅者的地址
	private String topicName;//主题
	private String brokerAddress;//代理地址
	private int type;//0为订阅，1为取消订阅

	public Subscription(String topicName, String subscriberURI, String brokerAddress, int type) {
		this.subscriberURI = subscriberURI;
		this.topicName = topicName;
		this.brokerAddress = brokerAddress;
		this.type = type;
	}
	
	public String getSubscriberURI() {
		return subscriberURI;
	}
	
	public String getTopicName() {
		return topicName;
	}
	
	public String getBrokerAddress() {
		return brokerAddress;
	}
	
	public int getType() {
		return type;
	}

}
