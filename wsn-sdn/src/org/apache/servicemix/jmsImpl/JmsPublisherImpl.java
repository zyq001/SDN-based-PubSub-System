package org.apache.servicemix.jmsImpl;

import javax.xml.namespace.QName;

import org.apache.servicemix.wsn.jms.JmsPublisher;
import org.oasis_open.docs.wsn.br_2.PublisherRegistrationFailedFaultType;
import org.oasis_open.docs.wsn.br_2.RegisterPublisher;
import org.oasis_open.docs.wsn.brw_2.PublisherRegistrationFailedFault;
import org.oasis_open.docs.wsn.brw_2.PublisherRegistrationRejectedFault;
import org.oasis_open.docs.wsn.bw_2.InvalidTopicExpressionFault;
import org.oasis_open.docs.wsn.bw_2.TopicNotSupportedFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;

public class JmsPublisherImpl extends JmsPublisher{

    private String notificationBrokerAddress;

    public JmsPublisherImpl(String name) {
        super(name);
    }

    public String getNotificationBrokerAddress() {
        return notificationBrokerAddress;
    }

    public void setNotificationBrokerAddress(String notificationBrokerAddress) {
        this.notificationBrokerAddress = notificationBrokerAddress;
    }

    @Override
    protected Object startSubscription() {
        //Subscription subscription = null;
        try {
            //NotificationBroker broker = new NotificationBroker(getContext(), publisherReference);
            //subscription = broker.subscribe(AbstractWSAClient.createWSA(notificationBrokerAddress), "noTopic", null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        return subscription;
        return new Object();
    }

    @Override
    protected void destroySubscription(Object subscription) {
        try {
            //((Subscription) subscription).unsubscribe();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void validatePublisher(RegisterPublisher registerPublisherRequest) throws InvalidTopicExpressionFault,
            PublisherRegistrationFailedFault, PublisherRegistrationRejectedFault, ResourceUnknownFault,
            TopicNotSupportedFault {
        super.validatePublisher(registerPublisherRequest);
//        String[] parts = split(AbstractWSAClient.getWSAAddress(publisherReference));
//        endpoint = getContext().getEndpoint(new QName(parts[0], parts[1]), parts[2]);
//        if (endpoint == null) {
//            PublisherRegistrationFailedFaultType fault = new PublisherRegistrationFailedFaultType();
//            throw new PublisherRegistrationFailedFault("Unable to resolve consumer reference endpoint", fault);
//        }
    }

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
        return new String[] {nsUri, svcName, epName };
    }

}
