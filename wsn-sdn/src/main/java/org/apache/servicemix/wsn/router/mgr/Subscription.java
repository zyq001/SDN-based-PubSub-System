package org.apache.servicemix.wsn.router.mgr;

public class Subscription {

	private String subscriberURI;//�����ߵĵ�ַ
	private String topicName;//����
	private String brokerAddress;//�����ַ
	private int type;//0Ϊ���ģ�1Ϊȡ������

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
