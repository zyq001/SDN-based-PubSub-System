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

import java.net.URI;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.namespace.QName;
import javax.xml.bind.JAXBElement;
import org.apache.servicemix.wsn.AbstractNotificationBroker;
import org.apache.servicemix.wsn.AbstractPublisher;
import org.apache.servicemix.wsn.AbstractSubscription;
import org.oasis_open.docs.wsrf.rp_2.GetResourcePropertyResponse;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnavailableFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;
import org.oasis_open.docs.wsrf.rpw_2.InvalidResourcePropertyQNameFault;

import org.apache.servicemix.application.WsnProcessImpl;

public abstract class JmsNotificationBroker extends AbstractNotificationBroker {

    private ConnectionFactory connectionFactory;

    private Connection connection;
    
    private static ConnectionFactory connectionFactoryWp;

    public JmsNotificationBroker(String name) {
        super(name);
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
		//初始化JMS消息接收端
		try {
			JmsMessage(connectionFactory);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static ConnectionFactory getConnectionFactoryWp(){
    	return connectionFactoryWp;
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
    
    
    
    public void JmsMessage(ConnectionFactory connectionFactory) throws JMSException{
    	TextMessage message = null;
//        ConnectionFactory connectionFactory = JmsNotificationBroker.getConnectionFactoryWp();
        //JMS 客户端到JMS Provider 的连接 
        Connection connection = connectionFactory.createConnection(); 
        if(connection != null)
        	System.out.println("connection is not empty!!!");
        connection.start(); 
        // Session： 一个发送或接收消息的线程 
        Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE); 
        // Destination ：消息的目的地;消息发送给谁. 
        // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置 
        Destination destination = session.createQueue("Notify-Queue"); 
        if(destination != null)
        	System.out.println("destination is not empty!!!");
        // 消费者，消息接收者 
        MessageConsumer consumer = session.createConsumer(destination); 
        consumer.setMessageListener(new MessageListener() {
				
			public void onMessage(Message arg0) {
				// TODO Auto-generated method stub
				TextMessage message = (TextMessage) arg0;
				if(message != null){
					String str = null;
					try{
						System.out.println("收到消息：" + message.getText());
						str = message.getText();
					}catch(JMSException e){
						e.printStackTrace();
					}				
					if(str.indexOf("NotificationMessage") > 0){
						WsnProcessImpl.mes.offer(str);
					}
				}
			}
		});
        session.close(); 
        connection.close(); 
    } 

}
