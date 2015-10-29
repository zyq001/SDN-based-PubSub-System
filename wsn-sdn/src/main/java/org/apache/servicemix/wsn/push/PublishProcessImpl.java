package org.apache.servicemix.wsn.push;

import org.apache.servicemix.application.WsnProcessImpl;
import org.apache.servicemix.wsn.jms.JmsSubscription;
import org.apache.servicemix.wsn.router.mgr.RtMgr;

import javax.jws.WebService;
import javax.xml.bind.JAXBException;

@WebService(endpointInterface = "org.apache.servicemix.wsn.push.IPublishProcess",
		serviceName = "IPublishProcess")
public class PublishProcessImpl implements IPublishProcess {
	private static int i = 0;
	private NotifyObserver notifyObserver = null;

	public PublishProcessImpl() {
		System.out.println("constructor Before");
		notifyObserver = new NotifyObserver();
		System.out.println("constructor After");
	}

	public void publishProcess(String publish) {
		// TODO Auto-generated method stub
		System.out.println("[notify message:] " + publish);

		doDeliver(publish);

		try {
			doOberve(publish);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doDeliver(String notification) {
		System.out.println("*************************our notification" + notification);
		int start = notification.indexOf("TopicExpression/Simple\">") + 24;
		int end = notification.indexOf("</wsnt:Topic>");
		String topicName = notification.substring(start, end);
		System.out.println("**************************our topicName  " + topicName);
		String subAddr = null;

		if (WsnProcessImpl.localtable != null) {
			for (int i = 0; i < WsnProcessImpl.localtable.size(); i++) {
				if (WsnProcessImpl.localtable.get(i).getTopicName().equals(topicName)) {
					System.out.println("**************************topicName  " + topicName);
					System.out.println("**************************notification  " + notification);
					System.out.println("**************************subAddress  " + WsnProcessImpl.localtable.get(i).getSubscriberAddress());
					if ((WsnProcessImpl.localtable.get(i).getAsyClient() == null) || ((WsnProcessImpl.localtable.get(i).getHttpPost()) == null)) {
						System.out.println("**************************is empty!!!!  ");
					}
//����shmily����					
/*					Runnable r = new PushClient(WSNComponent.localtable.get(i).getSubscriberAddress(), 
							notification, 
							WSNComponent.localtable.get(i).getAsyClient(),
							WSNComponent.localtable.get(i).getHttpPost());
            		Thread t = new Thread(r);
            		t.start();
*/
					PushClient r = new PushClient();
					r.doPush(WsnProcessImpl.localtable.get(i).getSubscriberAddress(),
							notification,
							JmsSubscription.asyClient,
							WsnProcessImpl.localtable.get(i).getHttpPost(), this);
				}
			}

		}
	}

	//new modified by shmily
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

		notifyObserver.setTopicName(topicName);
		notifyObserver.setDoc(mes);
		notifyObserver.setKind(-1);
		notifyObserver.addObserver(RtMgr.getInstance());

		notifyObserver.notifyMessage();
	}
}

