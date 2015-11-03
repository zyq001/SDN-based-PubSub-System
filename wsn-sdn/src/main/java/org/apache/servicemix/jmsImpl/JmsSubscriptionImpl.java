package org.apache.servicemix.jmsImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.wsn.jms.JmsSubscription;
import org.apache.servicemix.wsn.push.NotifyObserver;
import org.oasis_open.docs.wsn.b_2.Subscribe;
import org.oasis_open.docs.wsn.b_2.SubscribeCreationFailedFaultType;
import org.oasis_open.docs.wsn.bw_2.*;
import org.w3c.dom.Element;

import javax.jms.Message;

public class JmsSubscriptionImpl extends JmsSubscription {
	private static Log log = LogFactory.getLog(JmsSubscriptionImpl.class);

	private NotifyObserver notifyObserver;

	public JmsSubscriptionImpl(String name) {
		super(name);
		notifyObserver = new NotifyObserver();
	}


	@Override
	protected void start() throws SubscribeCreationFailedFault {
		super.start();
	}

	@Override
	protected void validateSubscription(Subscribe subscribeRequest) throws InvalidFilterFault,
			InvalidMessageContentExpressionFault, InvalidProducerPropertiesExpressionFault,
			InvalidTopicExpressionFault, SubscribeCreationFailedFault, TopicExpressionDialectUnknownFault,
			TopicNotSupportedFault, UnacceptableInitialTerminationTimeFault,
			UnsupportedPolicyRequestFault, UnrecognizedPolicyRequestFault {
		super.validateSubscription(subscribeRequest);
		System.out.println("validateSubscription1");
		try {
			//endpoint = resolveConsumer(subscribeRequest);
		} catch (Exception e) {
			SubscribeCreationFailedFaultType fault = new SubscribeCreationFailedFaultType();
			throw new SubscribeCreationFailedFault("Unable to resolve consumer reference endpoint", fault, e);
		}
//        if (endpoint == null) {
//            SubscribeCreationFailedFaultType fault = new SubscribeCreationFailedFaultType();
//            throw new SubscribeCreationFailedFault("Unable to resolve consumer reference endpoint", fault);
//        }
                       
        /*if(super.subscriberAddress != null){
        	String topicName = convertTopic(super.topic);
        	System.out.println("*****************************convert topicName" + topicName);

        	*//**========================================================================================
		 * topic tree subscribe
		 * new added at 2013/12/1
		 *//*
			String[] topicPath = topicName.split(":");
        	WSNTopicObject current = WsnProcessImpl.topicTree;
        	int flag = 0;
        	for(int i=0;i<topicPath.length-1;i++){
        		if(current.getTopicentry().getTopicName().equals(topicPath[i])){
        			for( int counter=0;counter<current.getChildrens().size();counter++ ){
        				if(current.getChildrens().get(counter).getTopicentry().getTopicName().equals(topicPath[i+1])){
        					current = current.getChildrens().get(counter);
        					flag++;
        					break;
        				}
        			}
        		}
        		else{
        			
        			log.error("subscribe faild! there is not this topic in the topic tree!");
        			break;
        		}
        	}
        	System.out.println("match time is: " + flag + "path.length is: " + topicPath.length);
        	if(flag == topicPath.length-1){
        		//�ж϶����Ƿ��Ѿ�����
        		int i;
        		for(i=0;i<current.getSubscribeAddress().size();i++){
        			if(current.getSubscribeAddress().get(i).equals(subscriberAddress)){
        				System.out.println("subscribe exists in the sysytem already,there is no need to do it again!");
        				log.error("subscribe exists in the sysytem already,there is no need to do it again!");
        				break;
        			}
        		}
        		//�����Ĳ����ڣ�����Ӷ���
        		if(i == current.getSubscribeAddress().size())
        			current.getSubscribeAddress().add(subscriberAddress);
        	}
        	else
        		System.out.println("subscribe faild! there is not this topic in the topic tree!");
        	//==========================================================================================
        	
        	if(WsnProcessImpl.localtable == null){
        		System.out.println("validateSubscription2");
        		WsnProcessImpl.localtable = new LinkedList<ListItem>();
        		ListItem newItem = new ListItem();
        		newItem.setSubscriberAddress(subscriberAddress);
        		newItem.setTopicName(topicName);
        		WsnProcessImpl.localtable.add(newItem);
        		
        		
        		
            	//NotifyObserver notify = new NotifyObserver(topicName,1);
            	notifyObserver.setTopicName(topicName);
            	notifyObserver.setKind(1);
        		notifyObserver.addObserver(RtMgr.getInstance());
            	log.debug("The new topic name is "+topicName);
            	notifyObserver.notifyMessage();
        	}
        	else{
        	
        		int nameCounter = 0;
        		int addressCounter = 0;

        		for(ListItem listItem : WsnProcessImpl.localtable){
        			if((listItem.getTopicName()==topicName))
        				nameCounter++;
        			if((listItem.getTopicName()==topicName)&&
        				(listItem.getSubscriberAddress()==subscriberAddress))
        				addressCounter++;
        			}
        		if(nameCounter==0){
        			//NotifyObserver notify = new NotifyObserver(topicName,1);
        			notifyObserver.setTopicName(topicName);
        			notifyObserver.setKind(1);
        			notifyObserver.addObserver(RtMgr.getInstance());
        			notifyObserver.notifyMessage();
        		}
        		if(addressCounter==0){
        			ListItem item = new ListItem();
        			item.setSubscriberAddress(subscriberAddress);
        			item.setTopicName(topicName);
        			WsnProcessImpl.localtable.add(item);
        		}
        	}
        }*/

	}


//    protected ServiceEndpoint resolveConsumer(Subscribe subscribeRequest) throws Exception {
//        // Try to resolve the WSA endpoint
//        JAXBContext ctx = JAXBContext.newInstance(Subscribe.class);
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        dbf.setNamespaceAware(true);
//        DocumentBuilder db = dbf.newDocumentBuilder();
//        Document doc = db.newDocument();
//        ctx.createMarshaller().marshal(subscribeRequest, doc);
//        NodeList nl = doc.getDocumentElement().getElementsByTagNameNS("http://docs.oasis-open.org/wsn/b-2",
//                "ConsumerReference");
//        if (nl.getLength() != 1) {
//            throw new Exception("Subscribe request must have exactly one ConsumerReference node");
//        }
//        Element el = (Element) nl.item(0);
//        DocumentFragment epr = doc.createDocumentFragment();
//        epr.appendChild(el);
//        ServiceEndpoint ep = getContext().resolveEndpointReference(epr);
//        if (ep == null) {
//            String[] parts = split(AbstractWSAClient.getWSAAddress(subscribeRequest.getConsumerReference()));
//            ep = getContext().getEndpoint(new QName(parts[0], parts[1]), parts[2]);
//        }
//        return ep;
//    }

	protected String[] split(String uri) {
		char sep;
		if (uri.indexOf('/') > 0) {
			sep = '/';
		} else {
			sep = ':';
		}
		int idx1 = uri.lastIndexOf(sep);
		int idx2 = uri.lastIndexOf(sep, idx1 - 1);
		String epName = uri.substring(idx1 + 1);
		String svcName = uri.substring(idx2 + 1, idx1);
		String nsUri = uri.substring(0, idx2);
		return new String[]{nsUri, svcName, epName};
	}

	@Override
	protected void doNotify(final Element content) {
//        try {
//        	
//        	MessageExchangeFactory factory = context.getDeliveryChannel().createExchangeFactory(endpoint);
//            InOnly inonly = factory.createInOnlyExchange();
//            NormalizedMessage msg = inonly.createMessage();
//            inonly.setInMessage(msg);
//            msg.setContent(new DOMSource(content));
//            context.getDeliveryChannel().send(inonly);
//        } catch (JBIException e) {
//            log.warn("Could not deliver notification", e);
//        }
	}


	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub

	}

//    public ComponentContext getContext() {
//        return context;
//    }
//
//    public void setContext(ComponentContext context) {
//        this.context = context;
//        this.contextInstance = context;
//    }
}
