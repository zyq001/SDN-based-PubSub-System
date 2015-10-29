/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicemix.wsn.jms;

import org.apache.servicemix.application.WsnProcessImpl;
import org.apache.servicemix.wsn.AbstractNotificationBroker;
import org.apache.servicemix.wsn.AbstractPublisher;
import org.oasis_open.docs.wsrf.rp_2.GetResourcePropertyResponse;
import org.oasis_open.docs.wsrf.rpw_2.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnavailableFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;

import javax.jms.*;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.net.URI;

public abstract class JmsNotificationBroker extends AbstractNotificationBroker {

	private static ConnectionFactory connectionFactoryWp;
	private ConnectionFactory connectionFactory;
	private Connection connection;

	public JmsNotificationBroker(String name) {
		super(name);
	}

	public static ConnectionFactory getConnectionFactoryWp() {
		return connectionFactoryWp;
	}

	public void init() throws Exception {
		if (connection == null) {
			connection = connectionFactory.createConnection();
			connection.start();
		}
		super.init();
	}

	public void destroy() throws Exception {
		if (connection != null) {
			connection.close();
		}
		super.destroy();
	}

	@Override
	protected AbstractPublisher createPublisher(String name) {
		JmsPublisher publisher = createJmsPublisher(name);
		publisher.setManager(getManager());
		publisher.setConnection(connection);
		return publisher;
	}

	@Override
	protected JmsSubscription createSubcription(String name) {
		JmsSubscription subscription = createJmsSubscription(name);
		subscription.setManager(getManager());
		subscription.setConnection(connection);
		return subscription;
	}

	protected abstract JmsSubscription createJmsSubscription(String name);

	protected abstract JmsPublisher createJmsPublisher(String name);

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
//        this.connectionFactoryWp = connectionFactory;
		//��ʼ��JMS��Ϣ���ն�
		try {
			JmsMessage(connectionFactory);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected GetResourcePropertyResponse handleGetResourceProperty(QName property)
			throws ResourceUnavailableFault, ResourceUnknownFault, InvalidResourcePropertyQNameFault {
		if (TOPIC_EXPRESSION_QNAME.equals(property)) {
			// TODO
		} else if (FIXED_TOPIC_SET_QNAME.equals(property)) {
			// TODO
		} else if (TOPIC_EXPRESSION_DIALECT_QNAME.equals(property)) {
			GetResourcePropertyResponse r = new GetResourcePropertyResponse();
			r.getAny().add(new JAXBElement(TOPIC_EXPRESSION_DIALECT_QNAME, URI.class, JmsTopicExpressionConverter.SIMPLE_DIALECT));
			return r;
		} else if (TOPIC_SET_QNAME.equals(property)) {
			// TODO
		}
		return super.handleGetResourceProperty(property);
	}


	public void JmsMessage(ConnectionFactory connectionFactory) throws JMSException {
		TextMessage message = null;
//        ConnectionFactory connectionFactory = JmsNotificationBroker.getConnectionFactoryWp();
		//JMS �ͻ��˵�JMS Provider ������
		Connection connection = connectionFactory.createConnection();
		if (connection != null)
			System.out.println("connection is not empty!!!");
		connection.start();
		// Session�� һ�����ͻ������Ϣ���߳�
		Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
		// Destination ����Ϣ��Ŀ�ĵ�;��Ϣ���͸�˭.
		// ��ȡsessionע�����ֵxingbo.xu-queue��һ����������queue��������ActiveMq��console����
		Destination destination = session.createQueue("Notify-Queue");
		if (destination != null)
			System.out.println("destination is not empty!!!");
		// �����ߣ���Ϣ������
		MessageConsumer consumer = session.createConsumer(destination);
		consumer.setMessageListener(new MessageListener() {

			public void onMessage(Message arg0) {
				// TODO Auto-generated method stub
				TextMessage message = (TextMessage) arg0;
				if (message != null) {
					String str = null;
					try {
						System.out.println("�յ���Ϣ��" + message.getText());
						str = message.getText();
					} catch (JMSException e) {
						e.printStackTrace();
					}
					if (str.indexOf("NotificationMessage") > 0) {
						WsnProcessImpl.mes.offer(str);
					}
				}
			}
		});
		session.close();
		connection.close();
	}

}
