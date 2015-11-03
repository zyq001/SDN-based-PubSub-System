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
package org.apache.servicemix.wsn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.application.AbstractWSAClient;
import org.apache.servicemix.application.IdGenerator;
import org.apache.servicemix.application.WsnProcessImpl;
import org.apache.servicemix.wsn.jms.JmsSubscription;
import org.apache.servicemix.wsn.push.NotifyObserver;
import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.oasis_open.docs.wsn.b_2.*;
import org.oasis_open.docs.wsn.br_2.PublisherRegistrationFailedFaultType;
import org.oasis_open.docs.wsn.br_2.RegisterPublisher;
import org.oasis_open.docs.wsn.br_2.RegisterPublisherResponse;
import org.oasis_open.docs.wsn.brw_2.NotificationBroker;
import org.oasis_open.docs.wsn.brw_2.PublisherRegistrationFailedFault;
import org.oasis_open.docs.wsn.brw_2.PublisherRegistrationRejectedFault;
import org.oasis_open.docs.wsn.brw_2.ResourceNotDestroyedFault;
import org.oasis_open.docs.wsn.bw_2.*;
import org.oasis_open.docs.wsrf.rp_2.GetResourcePropertyResponse;
import org.oasis_open.docs.wsrf.rp_2.InvalidResourcePropertyQNameFaultType;
import org.oasis_open.docs.wsrf.rpw_2.GetResourceProperty;
import org.oasis_open.docs.wsrf.rpw_2.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnavailableFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;

import javax.jws.*;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebService(endpointInterface = "org.oasis_open.docs.wsn.brw_2.NotificationBroker")
public abstract class AbstractNotificationBroker extends AbstractEndpoint implements NotificationBroker, GetResourceProperty {

	public static final String NAMESPACE_URI = "http://docs.oasis-open.org/wsn/b-2";
	public static final String PREFIX = "wsnt";
	public static final QName TOPIC_EXPRESSION_QNAME = new QName(NAMESPACE_URI, "TopicExpression", PREFIX);
	public static final QName FIXED_TOPIC_SET_QNAME = new QName(NAMESPACE_URI, "FixedTopicSet", PREFIX);
	public static final QName TOPIC_EXPRESSION_DIALECT_QNAME = new QName(NAMESPACE_URI, "TopicExpressionDialect", PREFIX);
	public static final QName TOPIC_SET_QNAME = new QName(NAMESPACE_URI, "TopicSet", PREFIX);
	public static String BROKER_ADDR;
	public static Map<String, JmsSubscription> subscriptions;
	private static Log log = LogFactory.getLog(AbstractNotificationBroker.class);
	private IdGenerator idGenerator;
	private AbstractPublisher anonymousPublisher;
	private Map<String, AbstractPublisher> publishers;
	private NotifyObserver notifyObserver;

	private ExecutorService pool;//�̳߳�

	public AbstractNotificationBroker(String name) {
		super(name);
		idGenerator = new IdGenerator();
		subscriptions = new ConcurrentHashMap<String, JmsSubscription>();
		publishers = new ConcurrentHashMap<String, AbstractPublisher>();
		notifyObserver = new NotifyObserver();

		pool = Executors.newFixedThreadPool(5); //��ʼ���̳߳�
	}

	public void init() throws Exception {
		//register();
		anonymousPublisher = createPublisher("Anonymous");
		//anonymousPublisher.register();


	}

	public void destroy() throws Exception {
		anonymousPublisher.destroy();
		unregister();
	}

	protected String createAddress() {
		BROKER_ADDR = "http://servicemix.org/wsnotification/NotificationBroker/" + getName();
		return BROKER_ADDR;
	}

	/**
	 * @param notify
	 */
	@WebMethod(operationName = "Notify")
	@Oneway
	public void notify(
			@WebParam(name = "Notify",
					targetNamespace = "http://docs.oasis-open.org/wsn/b-1",
					partName = "Notify")
			Notify notify) {
		log.debug("Notify");
		handleNotify(notify);
		try {
			Thread.sleep(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//new modified by wp
	protected void handleNotify(Notify notify) {
		if (WsnProcessImpl.mes.isEmpty()) {
			return;
		}

		handleClient x = new handleClient(this, notify);
		x.run();


//    	pool.execute(new Thread((Runnable)new handleClient(this, notify)));

//        Runnable x = new handleClient(this, notify);
//        Thread t = new Thread(x);
//		t.start();
	}

	//new modified by wp
	protected void doOberve(String mes) throws JAXBException {
//    	System.out.println("****************************Router message" + mes);
		int start = mes.indexOf("TopicExpression/Simple\">") + 24;
		int end = mes.indexOf("</wsnt:Topic>");
		if ((start < 0) || (end < 0)) {
			return;
		}
		String topicName = mes.substring(start, end);

//    	System.out.println("****************************Router TopicName" + topicName);
//    	System.out.println("****************************Router message" + mes);

		NotifyObserver notifyObserver = new NotifyObserver();
		notifyObserver.setTopicName(topicName);
		notifyObserver.setDoc(mes);
		notifyObserver.setKind(-1);
		notifyObserver.addObserver(RtMgr.getInstance());

		notifyObserver.notifyMessage();
	}

	//��TopicExpressionType���͵�topicת����String���͵�topic
	protected String convertTopic(TopicExpressionType topic) {
		String answer = null;
		for (Iterator iter = topic.getContent().iterator(); iter.hasNext(); ) {
			Object contentItem = iter.next();
			if (contentItem instanceof String)
				answer = ((String) contentItem).trim();
			if (contentItem instanceof QName)
				answer = ((QName) contentItem).toString();
			if (answer != null) {
				return answer;
			}
		}
		return answer;
	}

	protected AbstractPublisher getPublisher(W3CEndpointReference producerReference) {
		AbstractPublisher publisher = null;
		if (producerReference != null) {
			String address = AbstractWSAClient.getWSAAddress(producerReference);
			publisher = publishers.get(address);
		}
		if (publisher == null) {
			publisher = anonymousPublisher;
		}
		return publisher;
	}

	/**
	 * @param subscribeRequest
	 * @return returns org.oasis_open.docs.wsn.b_1.SubscribeResponse
	 * @throws SubscribeCreationFailedFault
	 * @throws InvalidTopicExpressionFault
	 * @throws TopicNotSupportedFault
	 * @throws InvalidFilterFault
	 * @throws InvalidProducerPropertiesExpressionFault
	 * @throws ResourceUnknownFault
	 * @throws InvalidUseRawValueFault
	 * @throws InvalidMessageContentExpressionFault
	 * @throws TopicExpressionDialectUnknownFault
	 * @throws UnacceptableInitialTerminationTimeFault
	 */
	@WebMethod(operationName = "Subscribe")
	@WebResult(name = "SubscribeResponse",
			targetNamespace = "http://docs.oasis-open.org/wsn/b-1",
			partName = "SubscribeResponse")
	public SubscribeResponse subscribe(
			@WebParam(name = "Subscribe",
					targetNamespace = "http://docs.oasis-open.org/wsn/b-1",
					partName = "SubscribeRequest")
			Subscribe subscribeRequest) throws InvalidFilterFault, InvalidMessageContentExpressionFault,
			InvalidProducerPropertiesExpressionFault, InvalidTopicExpressionFault, ResourceUnknownFault,
			SubscribeCreationFailedFault, TopicExpressionDialectUnknownFault, TopicNotSupportedFault,
			UnacceptableInitialTerminationTimeFault, UnsupportedPolicyRequestFault, UnrecognizedPolicyRequestFault {

		log.debug("Subscribe");
//
//        if(i == 0){
//        System.out.println("webService starting...");
//        PublishProcessImpl implementor = new PublishProcessImpl();
//        javax.xml.ws.Endpoint.publish("http://10.108.166.237:8199/IPublishProcess",
//        		implementor);
//        System.out.println("webService started");
//        i++;
//        }


		return handleSubscribe(subscribeRequest, null);
	}

	public SubscribeResponse handleSubscribe(
			Subscribe subscribeRequest,
			EndpointManager manager) throws InvalidFilterFault, InvalidMessageContentExpressionFault,
			InvalidProducerPropertiesExpressionFault, InvalidTopicExpressionFault,
			SubscribeCreationFailedFault, TopicExpressionDialectUnknownFault,
			TopicNotSupportedFault, UnacceptableInitialTerminationTimeFault,
			UnsupportedPolicyRequestFault, UnrecognizedPolicyRequestFault {
		/*
		 * �ж϶����Ƿ����
    	 */
		System.out.println("handleSubscribe1");
		Set<String> key = subscriptions.keySet();
		for (Iterator it = key.iterator(); it.hasNext(); ) {
			String addres = (String) it.next();
			if (addres.equals(WsnProcessImpl.newsubscribeaddree + "/" + WsnProcessImpl.newtopic)) {
				return null;
			}
		}

		JmsSubscription subscription = null;
		boolean success = false;
		try {
			System.out.println("handleSubscribe2");
			subscription = createSubcription(idGenerator.generateSanitizedId());
			subscription.setBroker(this);
			System.out.println("WsnProcessImpl.newsubscribeaddree----" + WsnProcessImpl.newsubscribeaddree + "/" + WsnProcessImpl.newtopic);
			//subscriptions.put(subscription.getAddress(), subscription);
			//���ȡ�����ı��
			subscriptions.put(WsnProcessImpl.newsubscribeaddree + "/" + WsnProcessImpl.newtopic, subscription);
//            subscription.create(subscribeRequest);
			if (manager != null) {
				subscription.setManager(manager);
			}
			//subscription.register();
			SubscribeResponse response = new SubscribeResponse();
			response.setSubscriptionReference(AbstractWSAClient.createWSA(subscription.getAddress()));
			success = true;
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("Unable to register new endpoint", e);
			SubscribeCreationFailedFaultType fault = new SubscribeCreationFailedFaultType();
			throw new SubscribeCreationFailedFault("Unable to register new endpoint", fault, e);
		} finally {
			if (!success && subscription != null) {
				subscriptions.remove(subscription);
				try {
					subscription.unsubscribe();
				} catch (UnableToDestroySubscriptionFault e) {
					log.info("Error destroying subscription", e);
				}
			}
		}
	}

	public void unsubscribe(String address) throws UnableToDestroySubscriptionFault {
		AbstractSubscription subscription = (AbstractSubscription) subscriptions.remove(address);
		if (subscription != null) {
			subscription.unsubscribe();
		}
	}

	/**
	 * @param getCurrentMessageRequest
	 * @return returns org.oasis_open.docs.wsn.b_1.GetCurrentMessageResponse
	 * @throws MultipleTopicsSpecifiedFault
	 * @throws TopicNotSupportedFault
	 * @throws InvalidTopicExpressionFault
	 * @throws ResourceUnknownFault
	 * @throws TopicExpressionDialectUnknownFault
	 * @throws NoCurrentMessageOnTopicFault
	 */
	@WebMethod(operationName = "GetCurrentMessage")
	@WebResult(name = "GetCurrentMessageResponse",
			targetNamespace = "http://docs.oasis-open.org/wsn/b-1",
			partName = "GetCurrentMessageResponse")
	public GetCurrentMessageResponse getCurrentMessage(
			@WebParam(name = "GetCurrentMessage",
					targetNamespace = "http://docs.oasis-open.org/wsn/b-1",
					partName = "GetCurrentMessageRequest")
			GetCurrentMessage getCurrentMessageRequest) throws InvalidTopicExpressionFault,
			MultipleTopicsSpecifiedFault, NoCurrentMessageOnTopicFault, ResourceUnknownFault,
			TopicExpressionDialectUnknownFault, TopicNotSupportedFault {

		log.debug("GetCurrentMessage");
		NoCurrentMessageOnTopicFaultType fault = new NoCurrentMessageOnTopicFaultType();
		throw new NoCurrentMessageOnTopicFault("There is no current message on this topic.", fault);
	}

	/**
	 * @param registerPublisherRequest
	 * @return returns org.oasis_open.docs.wsn.br_1.RegisterPublisherResponse
	 * @throws PublisherRegistrationRejectedFault
	 * @throws InvalidTopicExpressionFault
	 * @throws TopicNotSupportedFault
	 * @throws ResourceUnknownFault
	 * @throws PublisherRegistrationFailedFault
	 */
	@WebMethod(operationName = "RegisterPublisher")
	@WebResult(name = "RegisterPublisherResponse",
			targetNamespace = "http://docs.oasis-open.org/wsn/br-1",
			partName = "RegisterPublisherResponse")
	public RegisterPublisherResponse registerPublisher(
			@WebParam(name = "RegisterPublisher",
					targetNamespace = "http://docs.oasis-open.org/wsn/br-1",
					partName = "RegisterPublisherRequest")
			RegisterPublisher registerPublisherRequest) throws InvalidTopicExpressionFault,
			PublisherRegistrationFailedFault, PublisherRegistrationRejectedFault, ResourceUnknownFault,
			TopicNotSupportedFault {

		log.debug("RegisterPublisher");
		return handleRegisterPublisher(registerPublisherRequest, null);
	}

	public RegisterPublisherResponse handleRegisterPublisher(RegisterPublisher registerPublisherRequest,
	                                                         EndpointManager manager) throws InvalidTopicExpressionFault, PublisherRegistrationFailedFault,
			PublisherRegistrationRejectedFault, ResourceUnknownFault, TopicNotSupportedFault {
		AbstractPublisher publisher = null;
		boolean success = false;
		try {
			publisher = createPublisher(idGenerator.generateSanitizedId());
			publishers.put(publisher.getAddress(), publisher);
			if (manager != null) {
				publisher.setManager(manager);
			}
			publisher.register();
			publisher.create(registerPublisherRequest);
			RegisterPublisherResponse response = new RegisterPublisherResponse();
			response.setPublisherRegistrationReference(AbstractWSAClient.createWSA(publisher.getAddress()));
			success = true;
			return response;
		} catch (EndpointRegistrationException e) {
			log.warn("Unable to register new endpoint", e);
			PublisherRegistrationFailedFaultType fault = new PublisherRegistrationFailedFaultType();
			throw new PublisherRegistrationFailedFault("Unable to register new endpoint", fault, e);
		} finally {
			if (!success && publisher != null) {
				publishers.remove(publisher.getAddress());
				try {
					publisher.destroy();
				} catch (ResourceNotDestroyedFault e) {
					log.info("Error destroying publisher", e);
				}
			}
		}
	}

	protected abstract AbstractPublisher createPublisher(String name);

	protected abstract JmsSubscription createSubcription(String name);

	@WebResult(name = "GetResourcePropertyResponse", targetNamespace = "http://docs.oasis-open.org/wsrf/rp-2", partName = "GetResourcePropertyResponse")
	@WebMethod(operationName = "GetResourceProperty")
	public GetResourcePropertyResponse getResourceProperty(
			@WebParam(partName = "GetResourcePropertyRequest", name = "GetResourceProperty", targetNamespace = "http://docs.oasis-open.org/wsrf/rp-2")
			javax.xml.namespace.QName getResourcePropertyRequest
	) throws ResourceUnavailableFault, ResourceUnknownFault, InvalidResourcePropertyQNameFault {

		log.debug("GetResourceProperty");
		return handleGetResourceProperty(getResourcePropertyRequest);
	}

	protected GetResourcePropertyResponse handleGetResourceProperty(QName property)
			throws ResourceUnavailableFault, ResourceUnknownFault, InvalidResourcePropertyQNameFault {
		InvalidResourcePropertyQNameFaultType fault = new InvalidResourcePropertyQNameFaultType();
		throw new InvalidResourcePropertyQNameFault("Invalid resource property QName: " + property, fault);
	}

	//new added by wp
	public class diliverRouter implements Runnable {
		private AbstractNotificationBroker broker = null;
		private String mes;

		public diliverRouter(AbstractNotificationBroker broker, String mes) {
			this.broker = broker;
			this.mes = mes;
		}

		public void run() {
			// TODO Auto-generated method stub
			try {
				broker.doOberve(mes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//new added by wp
	public class handleClient implements Runnable {
		private Notify notify = null;
		private AbstractNotificationBroker broker = null;
		private String mes = null;

		public handleClient(AbstractNotificationBroker broker, Notify notify) {
			this.notify = notify;
			this.broker = broker;
			this.mes = WsnProcessImpl.mes.element();
		}

		public void run() {
   /*       ������������
    		Runnable y = new diliverRouter(broker, mes);
    		Thread t = new Thread(y);
    		t.start();
   */
			diliverRouter y = new diliverRouter(broker, mes);
			y.run();

			NotificationMessageHolderType messageHolder = notify.getNotificationMessage().get(0);
			W3CEndpointReference producerReference = messageHolder.getProducerReference();
			AbstractPublisher publisher = broker.getPublisher(producerReference);

			if (publisher != null) {
				publisher.notify(messageHolder, mes);
			}
		}
	}
}
