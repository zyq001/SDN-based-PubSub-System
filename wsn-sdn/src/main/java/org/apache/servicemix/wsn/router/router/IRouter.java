package org.apache.servicemix.wsn.router.router;

public interface IRouter {
	
	//input topic, calculate the children of this representative and save them
	public void route(String topic);

}
