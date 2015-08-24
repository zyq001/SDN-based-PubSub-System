package org.apache.servicemix.wsn.push;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.servicemix.wsn.jms.JmsSubscription;

public class FutureCallbackImpl implements FutureCallback<HttpResponse>{
	private Object callPushClient = null;
	private boolean flag = false;
	private CountDownLatch latch = null;
	private static Log log = LogFactory.getLog(FutureCallbackImpl.class);
	
	public FutureCallbackImpl(Object _callPushClient, boolean _flag, CountDownLatch _latch){
		this.callPushClient = _callPushClient;
		this.flag = _flag;
		this.latch = _latch;
	}
	public void completed(HttpResponse result) {
		// TODO Auto-generated method stub
		PushClient.callBackCompletedCounter ++;
		if(latch == null){
			System.out.println("log must be loooooooooooooooooooooooong%%%%%FutureCallbackImpl: now, the latch is null~~");
		}else{
			System.out.println("log must be loooooooooooooooooooooooong%%%%%FutureCallbackImpl: now, the latch is not null!!!!");
		}
		System.out.println("log must be longggggggggggggggggggggggggg*****FutureCallbackImpl: FLAG:" + flag);
		if(flag){
			latch.countDown();
		}
		int statusCode = result.getStatusLine().getStatusCode();
		
		//debug information added by shmily
		String logTemp = "%%%%%FutureCallbackImpl: sendCounter:" + PushClient.sendCounter + 
			" callBackCompletedCounter:" + PushClient.callBackCompletedCounter + 
			" status:" + statusCode;
		log.info(logTemp);
		System.out.println(logTemp);
		
		switch(statusCode){
		case 200 : 
			if(callPushClient instanceof JmsSubscription){
				((JmsSubscription) callPushClient).setSuccessfulFlag(true);
			}
			System.out.println("%%%%%FutureCallbackImpl:status:200");
			log.info("%%%%%FutureCallbackImpl:status:200");
			break;
		case 201 : 
			if(callPushClient instanceof JmsSubscription){
				((JmsSubscription) callPushClient).setSuccessfulFlag(true);
			}
			log.info("%%%%%FutureCallbackImpl:status:201");
			break;
		case 202 : 
			if(callPushClient instanceof JmsSubscription){
				((JmsSubscription) callPushClient).setSuccessfulFlag(true);
			}
			log.info("%%%%%FutureCallbackImpl:status:202");
			break;
		case 204 : 
			if(callPushClient instanceof JmsSubscription){
				((JmsSubscription) callPushClient).setSuccessfulFlag(true);
			}
			log.info("%%%%%FutureCallbackImpl:status:204");
			break;
		case 400 : 
			if(callPushClient instanceof JmsSubscription){
				((JmsSubscription) callPushClient).setSuccessfulFlag(false);
			}
			log.info("%%%%%FutureCallbackImpl:status:400");
			break;
		case 401 : 
			if(callPushClient instanceof JmsSubscription){
				((JmsSubscription) callPushClient).setSuccessfulFlag(false);
			}
			log.info("%%%%%FutureCallbackImpl:status:401");
			break;
		case 403 : 
			if(callPushClient instanceof JmsSubscription){
				((JmsSubscription) callPushClient).setSuccessfulFlag(false);
			}
			log.info("%%%%%FutureCallbackImpl:status:403");
			break;
		case 404 :
			if(callPushClient instanceof JmsSubscription){
				((JmsSubscription) callPushClient).setSuccessfulFlag(false);
			}
			System.out.println("404 : not found!!!");
			log.info("%%%%%FutureCallbackImpl:status:404");
			break;
		case 500 :
			if(callPushClient instanceof JmsSubscription){
				((JmsSubscription) callPushClient).setSuccessfulFlag(false);
			}
			log.info("%%%%%FutureCallbackImpl:status:500");
			break;
		default :
			if(callPushClient instanceof JmsSubscription){
				((JmsSubscription) callPushClient).setSuccessfulFlag(true);
			}
			log.info("%%%%%FutureCallbackImpl:status:others");
		}
	}

	public void failed(Exception ex) {
		// TODO Auto-generated method stub
		if(callPushClient instanceof JmsSubscription){
			((JmsSubscription) callPushClient).setSuccessfulFlag(false);
		}
		log.info("%%%%%FutureCallbackImpl:status:Failed!!!");
	}

	public void cancelled() {
		// TODO Auto-generated method stub
		if(callPushClient instanceof JmsSubscription){
			((JmsSubscription) callPushClient).setSuccessfulFlag(true);
		}
		log.info("%%%%%FutureCallbackImpl:status:Cancelled!!!");
	}
}
