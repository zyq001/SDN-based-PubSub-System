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

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.servicemix.application.WSNTopicObject;
import org.apache.servicemix.application.WsnProcessImpl;
import org.apache.servicemix.wsn.AbstractNotificationBroker;
import org.apache.servicemix.wsn.AbstractSubscription;
import org.oasis_open.docs.wsn.b_2.InvalidTopicExpressionFaultType;
import org.oasis_open.docs.wsn.b_2.NotificationMessageHolderType;
import org.oasis_open.docs.wsn.b_2.Notify;
import org.oasis_open.docs.wsn.b_2.PauseFailedFaultType;
import org.oasis_open.docs.wsn.b_2.ResumeFailedFaultType;
import org.oasis_open.docs.wsn.b_2.Subscribe;
import org.oasis_open.docs.wsn.b_2.SubscribeCreationFailedFaultType;
import org.oasis_open.docs.wsn.b_2.UnableToDestroySubscriptionFaultType;
import org.oasis_open.docs.wsn.b_2.UnacceptableTerminationTimeFaultType;
import org.oasis_open.docs.wsn.bw_2.InvalidFilterFault;
import org.oasis_open.docs.wsn.bw_2.InvalidMessageContentExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidProducerPropertiesExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidTopicExpressionFault;
import org.oasis_open.docs.wsn.bw_2.PauseFailedFault;
import org.oasis_open.docs.wsn.bw_2.ResumeFailedFault;
import org.oasis_open.docs.wsn.bw_2.SubscribeCreationFailedFault;
import org.oasis_open.docs.wsn.bw_2.TopicExpressionDialectUnknownFault;
import org.oasis_open.docs.wsn.bw_2.TopicNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.UnableToDestroySubscriptionFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableInitialTerminationTimeFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableTerminationTimeFault;
import org.oasis_open.docs.wsn.bw_2.UnsupportedPolicyRequestFault;
import org.oasis_open.docs.wsn.bw_2.UnrecognizedPolicyRequestFault;

//import org.apache.servicemix.wsn.push.INotificationProcess;
import org.apache.servicemix.wsn.push.ListItem;
import org.apache.servicemix.wsn.push.NotifyObserver;
import org.apache.servicemix.wsn.push.PushClient;
//import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.servicemix.wsn.router.mgr.RegState;
import org.apache.servicemix.wsn.router.mgr.RtMgr;

public abstract class JmsSubscription extends AbstractSubscription implements MessageListener {

    private static Log log = LogFactory.getLog(JmsSubscription.class);
    
    private static int subpackagelen = 3000;

    private Connection connection;

    public static Session session;

    private JmsTopicExpressionConverter topicConverter;

    public Topic jmsTopic;
    
	public static HttpAsyncClient asyClient = null;
	
	public static ArrayList<HttpAsyncClient> asyClientList = new ArrayList<HttpAsyncClient>();
	
	private HttpPost httpPost = null;
	
//	private NotifyObserver notifyObserver = new NotifyObserver();
//	private  NotifyObserver  notifyObserver = null;
	
//	public ArrayList<String> infoTemp;
	
//    public static ExecutorService pushpool = new ;
//    private ExecutorService pool2;//线程池
	public static ThreadPool pushpool = new ThreadPool(10);
//	public ThreadPool pool2;
//	private boolean flag = true;
	public static PushClient diliverToWebservice = new PushClient();
	private String dopushResponse = "initial";
	
	private boolean successfulFlag = true;
//    
//    private JaxWsProxyFactoryBean factory ;
//    
//    private INotificationProcess service;

    public JmsSubscription(String name) {
        super(name);
      //  factory = new JaxWsProxyFactoryBean();
        topicConverter = new JmsTopicExpressionConverter();
		 if(asyClient==null){  
		    try {
				asyClient = new DefaultHttpAsyncClient();
			} catch (IOReactorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			asyClient.start();
		 }
		 for(int i=asyClientList.size();i<5;i++){
			 try {
				 HttpAsyncClient asyCli = new DefaultHttpAsyncClient();
				 asyCli.start();
				 asyClientList.add(asyCli);
				} catch (IOReactorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 }
//		notifyObserver = new NotifyObserver();
//		notifyObserver.addObserver(RtMgr.getInstance());
//		pool1 = Executors.newFixedThreadPool(5); //初始化线程池
//		pool2 = Executors.newFixedThreadPool(5); //初始化线程池
//		pool1 = new ThreadPool(2);
//		pool2 = new ThreadPool(30);
//		r = new PushClient();
    }

    public void setSuccessfulFlag(boolean _successfulFlag){
    	successfulFlag = _successfulFlag;
    	log.error("%%%%%JmsSubscription: set the successfulFlag " + _successfulFlag);
    }
    public boolean getSuccessfulFlag(){
    	return successfulFlag;
    }
    
    protected void start() throws SubscribeCreationFailedFault {
        try {
        	
        	session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        	if(session==null){
        		System.out.println("jmsTopic:"+jmsTopic);
                MessageConsumer consumer = session.createConsumer(jmsTopic);
                consumer.setMessageListener(this);
        	}
        } catch (JMSException e) {
            SubscribeCreationFailedFaultType fault = new SubscribeCreationFailedFaultType();
            throw new SubscribeCreationFailedFault("Error starting subscription", fault, e);
        }
    }

    @Override
    protected void validateSubscription(Subscribe subscribeRequest) throws InvalidFilterFault,
            InvalidMessageContentExpressionFault, InvalidProducerPropertiesExpressionFault,
            InvalidTopicExpressionFault, SubscribeCreationFailedFault, TopicExpressionDialectUnknownFault,
            TopicNotSupportedFault, UnacceptableInitialTerminationTimeFault,
            UnsupportedPolicyRequestFault, UnrecognizedPolicyRequestFault {
    	super.validateSubscription(subscribeRequest);
        try {
            jmsTopic = topicConverter.toActiveMQTopic(topic);
        } catch (InvalidTopicException e) {
            InvalidTopicExpressionFaultType fault = new InvalidTopicExpressionFaultType();
            throw new InvalidTopicExpressionFault(e.getMessage(), fault);
        }
    }

    @Override
    protected void pause() throws PauseFailedFault {
        if (session == null) {
            PauseFailedFaultType fault = new PauseFailedFaultType();
            throw new PauseFailedFault("Subscription is already paused", fault);
        } else {
            try {
                session.close();
            } catch (JMSException e) {
                PauseFailedFaultType fault = new PauseFailedFaultType();
                throw new PauseFailedFault("Error pausing subscription", fault, e);
            } finally {
                session = null;
            }
        }
    }

    @Override
    protected void resume() throws ResumeFailedFault {
        if (session != null) {
            ResumeFailedFaultType fault = new ResumeFailedFaultType();
            throw new ResumeFailedFault("Subscription is already running", fault);
        } else {
            try {
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageConsumer consumer = session.createConsumer(jmsTopic);
                consumer.setMessageListener(this);
            } catch (JMSException e) {
                ResumeFailedFaultType fault = new ResumeFailedFaultType();
                throw new ResumeFailedFault("Error resuming subscription", fault, e);
            }
        }
    }

    @Override
    protected void renew(XMLGregorianCalendar terminationTime) throws UnacceptableTerminationTimeFault {
        UnacceptableTerminationTimeFaultType fault = new UnacceptableTerminationTimeFaultType();
        throw new UnacceptableTerminationTimeFault("TerminationTime is not supported", fault);
    }

    @Override
	public void unsubscribe() throws UnableToDestroySubscriptionFault {
        super.unsubscribe();
        if (session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                UnableToDestroySubscriptionFaultType fault = new UnableToDestroySubscriptionFaultType();
                throw new UnableToDestroySubscriptionFault("Unable to unsubscribe", fault, e);
            } finally {
                session = null;
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void onMessage(String message) {
//   System.out.println("onMessaged1");
        String text = message;
        //(super.subscriberAddress != null) && (!super.subscriberAddress.equals("http://localhost:12345/only/register/use"))
        if(true){         	
        	try{
//        		System.out.println("onMessaged2");
//         		if( httpPost==null )
//         			httpPost = new HttpPost(super.subscriberAddress);
//         		System.out.println("TextMessage:"+text);
         		int start = text.indexOf("TopicExpression/Simple\">") + 24;
    			int end = text.indexOf("</wsnt:Topic>");
    			String topicName = text.substring(start, end);
         		
    			//flag == 
    			if(true){
//    				System.out.println("onMessaged3");
    				//匹配主题   				
        			String[] topicPath = topicName.split(":");
        			WSNTopicObject current = WsnProcessImpl.topicTree;
        	    	int flag = 0;
        	    	for(int i=0;i<topicPath.length-1;i++){
//        	    		System.out.println("current.getTopicentry().getTopicName():"+current.getTopicentry().getTopicName()+"      topicPath[i]"+topicPath[i]);
        	    		if(current.getTopicentry().getTopicName().equals(topicPath[i])){
        	    			
        	    			for( int counter=0;counter<current.getChildrens().size();counter++ ){
//        	    				System.out.println("---:"+current.getChildrens().get(counter).getTopicentry().getTopicName());
        	    				if(current.getChildrens().get(counter).getTopicentry().getTopicName().equals(topicPath[i+1])){
        	    					current = current.getChildrens().get(counter);
        	    					flag++;
        	    					break;
        	    				}
        	    			} 
        	    		}
        	    		else{
        	    			System.out.println("notify faild! there is not this topic in the topic tree!");
        	    		}
        	    	}
        	    	//若匹配成功，则向当前主题以及当前主题的祖先主题的订阅者推送消息
        	    	if(flag == topicPath.length-1){
        	    		System.out.println("match success!");
        	    		ArrayList<String> sendedaddr = new ArrayList<String>();
        	    		while(current != null){
        	    			for(int i=0;i<current.getSubscribeAddress().size();i++){
        	    				if(sendedaddr.contains(current.getSubscribeAddress().get(i)))
        	    					continue;
        	    				sendedaddr.add(current.getSubscribeAddress().get(i));
        	    				JmsSubscription.diliverToWebservice.doPush(current.getSubscribeAddress().get(i),
        	    						text,
        	    						JmsSubscription.asyClient,
        	    						new HttpPost(current.getSubscribeAddress().get(i)), this);
        	    			}
        	    			current = current.getParent();
        	    		}	
        	    	}
        	    	else{
        	    		System.out.println("notify faild! there is not this topic");
        	    	}
    			}
    			
//    			infoTemp.clear();
    			ArrayList<String> infoTemp = new ArrayList<String>();
    			/**
        		 * 消息在递交给路由前，判断是否要进行拆包，
        		 * 是，则调用拆包方法完成拆包
        		 * 否，则直接向路由递交消息
        		 */
        		if(text.length() > 3000){
        			breakPackage(text,infoTemp);
//        			Thread.sleep(0);
        		}else{
        			infoTemp.add(text);
        		}
  System.out.println("infoTemp:"+infoTemp.size());
        		for(int q=0;q<infoTemp.size();q++){
//        			System.out.println("JMS-message: " + infoTemp.get(q));
        			NotifyObserver notifyObserver = new NotifyObserver();
        			notifyObserver.addObserver(RtMgr.getInstance());
            		ObserveMutiThread om = new ObserveMutiThread(infoTemp.get(q), notifyObserver);
            		om.run();
            		
            		if(infoTemp.size()>1)
            			Thread.sleep(5);
        		}
     
     		}catch (Exception e) {
     			e.printStackTrace();
                 log.warn("Error notifying consumer", e);
             }
         }
         
    }
    
    
    
    
    public class ObserveMutiThread implements Runnable{
      	private String text = null;
      	private NotifyObserver notifyObserver = null;
      	
      	
      	public ObserveMutiThread(String text, NotifyObserver notifyObserver){
      		this.text = text;
      		this.notifyObserver = notifyObserver;
      	}
		public void run() {
			// TODO Auto-generated method stub
//			if(text.contains("<EventType=1></EventType=1>")){
//				String tempTopic = text.substring(text.indexOf("TopicExpression/Simple\">") + 24,
//						text.indexOf("</wsnt:Topic>"));
//				text = text.replaceAll("<EventType=1></EventType=1>", "");
//				text = text.replaceAll(tempTopic, tempTopic + "EventType=1"); 
//				System.out.println("%%%%%%%%%%etxt in JmsSubscription:" + text);
//			}
//			synchronized (this) {
				try {
//					System.out.println("text:"+text);
		    			doOberve(text);
	    		} catch (JAXBException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
//			}
		}
	    public void doOberve(String mes) throws JAXBException{
	    	//	System.out.println("****************************Router message" + mes);
	    	int start = mes.indexOf("TopicExpression/Simple\">") + 24;
	    	int end = mes.indexOf("</wsnt:Topic>");	 	
	    	if((start <0) || (end <0)){
	    		return;
	    	}
	    	String topicName = mes.substring(start, end);
	
	    	//	System.out.println("****************************Router TopicName" + topicName);
	    	//	System.out.println("****************************Router message" + mes);
String before = mes.split("<Fragment>")[0].split("<wsnt:Package>")[1];
System.out.println("befor:"+before);
			
	    	notifyObserver.setTopicName(topicName);
	    	notifyObserver.setDoc(mes);
	    	notifyObserver.setKind(-1);
	    	notifyObserver.addObserver(RtMgr.getInstance());
	    	notifyObserver.notifyMessage();
	    	}
    	} 
    
    protected boolean doFilter(Element content) {
        if (contentFilter != null) {
            if (!contentFilter.getDialect().equals(XPATH1_URI)) {
                throw new IllegalStateException("Unsupported dialect: " + contentFilter.getDialect());
            }
            try {
                XPathFactory xpfactory = XPathFactory.newInstance();
                XPath xpath = xpfactory.newXPath();
                XPathExpression exp = xpath.compile(contentFilter.getContent().get(0).toString());
                Boolean ret = (Boolean) exp.evaluate(content, XPathConstants.BOOLEAN);
                return ret.booleanValue();
            } catch (XPathExpressionException e) {
                log.warn("Could not filter notification", e);
            }
            return false;
        }
        return true;
    }

    protected abstract void doNotify(Element content);
    
    public  void breakPackage(String message,ArrayList<String> infoTemp){
    	//解析出外围包裹消息体
    	String beforeFragment = message.split("<Fragment>")[0] + "<Fragment>";

    	String betweenFrangmentAndNotification = "</Fragment>" +"<EventType>"+message.split("<EventType>")[1].split("</EventType>")[0]+"</EventType>"+ "</wsnt:Package>" + "<wsnt:Message>";
    	String afterNotification = "</wsnt:Message>" + message.split("</wsnt:Message>")[1];
    	//解析出消息内容和打拆包信息
    	String notification = message.split("<wsnt:Message>")[1].split("</wsnt:Message>")[0];
    	String fragment = message.split("<Fragment>")[1].split("</Fragment>")[0];
    	String[] splitFragment = fragment.split("-");
   System.out.println("fragment:"+fragment);
    	
    	int num = message.length() / 3000;
    	//分包长度
    	int length = 3000 - beforeFragment.length() - betweenFrangmentAndNotification.length() - 
    			afterNotification.length();
    	//构建拆包后的消息体，并将之存储在一个ArrayList中
    	int i;
    	for(i=0;i<=num;i++){
    		String partOfNotification = null;
    		String partOfFragment = null;
    		if(i<num){
    			partOfNotification = notification.substring(i*length, (i+1)*length);
        		partOfFragment = "1-1-" + splitFragment[2] + "-" + i;
    		}else{
    			partOfNotification = notification.substring(i*length, notification.length());
    			partOfFragment = "1-0-" + splitFragment[2] + "-" + i;
    		}
    		StringBuilder breakedMessage = new StringBuilder();
    		breakedMessage.append(beforeFragment);
    		breakedMessage.append(partOfFragment);
    		breakedMessage.append(betweenFrangmentAndNotification);
    		breakedMessage.append(partOfNotification);
    		breakedMessage.append(afterNotification);
System.out.println("infoTemp:"+infoTemp+" beforeFragment:"+beforeFragment);
    		infoTemp.add(breakedMessage.toString());
//    System.out.println("infoTemp:"+infoTemp);
    	}
    }
//    protected void doOberve(String mes) throws JAXBException{
////    	System.out.println("****************************Router message" + mes);
//    	int start = mes.indexOf("TopicExpression/Simple\">") + 24;
//    	int end = mes.indexOf("</wsnt:Topic>");	 	
//    	if((start <0) || (end <0)){
//    		return;
//    	}
//    	String topicName = mes.substring(start, end);
//    	
////    	System.out.println("****************************Router TopicName" + topicName);
////    	System.out.println("****************************Router message" + mes);
//
//    	notifyObserver.setTopicName(topicName);
//    	notifyObserver.setDoc(mes);
//    	notifyObserver.setKind(-1);
////    	notifyObserver.addObserver(RtMgr.getInstance());
//    	
//    	notifyObserver.notifyMessage();
//    }

}
