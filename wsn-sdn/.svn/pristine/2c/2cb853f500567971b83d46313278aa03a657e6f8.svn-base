package org.apache.servicemix.jmsImpl;


import org.apache.servicemix.wsn.jms.JmsNotificationBroker;
import org.apache.servicemix.wsn.jms.JmsPublisher;
import org.apache.servicemix.wsn.jms.JmsSubscription;

public class JmsNotificationBrokerImpl extends JmsNotificationBroker {

    public JmsNotificationBrokerImpl(String name) {
        super(name);
    }

    @Override
    protected JmsSubscription createJmsSubscription(String name) {
        JmsSubscriptionImpl subscription = new JmsSubscriptionImpl(name);
        // The context here should be overriden by the EndpointManager with the endpoint's context
        return subscription;
    }

    @Override
    protected JmsPublisher createJmsPublisher(String name) {
        JmsPublisherImpl publisher = new JmsPublisherImpl(name);
        // The context here should be overriden by the EndpointManager with the endpoint's context
        publisher.setNotificationBrokerAddress(address);
        return publisher;
    }

}
