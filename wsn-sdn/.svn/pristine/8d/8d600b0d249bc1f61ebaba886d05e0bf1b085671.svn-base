package org.apache.servicemix.wsn;

import org.oasis_open.docs.wsn.b_2.Notify;
import org.apache.servicemix.wsn.jms.JmsSubscription;

public class HandleNotifyThread extends AbstractNotificationBroker implements Runnable{

	private Notify notify = null;
	public HandleNotifyThread(String name, Notify notify) {
		super(name);
		this.notify = notify;
		// TODO Auto-generated constructor stub
	}

	public void run() {
		// TODO Auto-generated method stub
		handleNotify(notify);
	}

	@Override
	protected AbstractPublisher createPublisher(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JmsSubscription createSubcription(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
