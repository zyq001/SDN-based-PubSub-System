package org.apache.servicemix.wsn.push;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.servicemix.wsn.AbstractSubscription;
import org.apache.servicemix.wsn.jms.JmsSubscription;
import org.oasis_open.docs.wsn.b_2.Notify;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.methods.RequestEntity;

//import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

public class PushClient {

	private final static int threshold = 200;
	private final static int limit = 2000;
	public static int pushCounter = 0;
	public static int sendCounter = 0;
	public static int callBackCounter = 0;
	public static int callBackCompletedCounter = 0;
	public static int counterAfterBeyondLimit = 0;
	private static Log log = LogFactory.getLog(PushClient.class);
	private static String sendResponse = "normal";
	private static String failedURL = null;
	private static int dopushCounter = 0;
	private Notify notify;
	private String addr;
	private HttpAsyncClient asyClient = null;
	//	private HttpPost httpPost = null;
	private String text = null;
	private CountDownLatch latch = null;
	private boolean flag = false;

//	public PushClient(String addr,String text, HttpAsyncClient asyClient, HttpPost httpPost){
//	
//	}
//	public void run() {
//		doPush();	
//	}

	public void doPush(String addr, String text, HttpAsyncClient asyClient, HttpPost httpPost, Object callPushClient) {
		this.addr = addr;
		if (pushCounter > 100000)
			pushCounter = 0;
		this.asyClient = JmsSubscription.asyClientList.get(pushCounter % JmsSubscription.asyClientList.size());
		pushCounter++;
		System.out.println("pushCounter%JmsSubscription.asyClientList.size():" + pushCounter % JmsSubscription.asyClientList.size());
		this.text = text;
		if ((PushClient.sendCounter % 100000) == 0) {
			System.gc();
		}

		Date date = new Date();
		dopushCounter++;
		//System.out.println("ThreadPool 1-PushClient Counter:" + dopushCounter + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
//		try{
//			JAXBContext jaxbContext = JAXBContext.newInstance(Notify.class);
//		
//			for (NotificationMessageHolderType messageHolder : notify.getNotificationMessage()){
//				Notify notifyHolder = new Notify();
//				notifyHolder.getNotificationMessage().add(messageHolder);
//				StringWriter writer = new StringWriter();
//				jaxbContext.createMarshaller().marshal(notifyHolder, writer);
//				String notifyContent = writer.toString();
//				String content = getContent(notifyContent);
		int start = text.indexOf("<wsnt:Message>") + 14;
		int end = text.indexOf("</wsnt:Message>");
		String content = text.substring(start, end).trim();

		content = content.replaceAll("<", "&lt;");
		content = content.replaceAll(">", "&gt;");

		//转锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷要转锟斤拷锟斤拷址锟�
//				content = escapeCharacterMethod(content);

		String request = requestGenerate(content);
		//added by shmily(6)
//				try {
//					request = new String(request.getBytes("UTF-8"), "ISO-8859-1");
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if((sendResult.equals("CANCELLED")) || (sendResult.equals("CREATED")) || 
//						(sendResult.equals("ACCEPTED")) || (sendResult.equals("NO_RESPONSE")) || 
//						(sendResult.equals("OK")) || (sendResult.equals("normal"))){
//					log.info("**********PushClient:" + sendResult);
//					sendResult = send(request);
//				}else if(sendResult.equals("initial")){
//					sendResult = send(request);
//					log.info("**********enter the initial branch");
//				}else if((sendResult.equals("FAILED")) || (sendResult.equals("BAD_REQUEST")) || 
//						(sendResult.equals("UNAUTHORIZED")) || (sendResult.equals("FORBIDDEN")) || 
//						(sendResult.equals("NOT_FOUND")) || (sendResult.equals("INTERNAL_ERROR"))){
//					log.info("**********PushClient:" + sendResult);
//				}
//				sendThread sends = new sendThread();
//				sends.setagrs(content, httpPost, callPushClient);
//				JmsSubscription.pushpool.execute(sends);
		send(request, httpPost, callPushClient);
	}

	private String escapeCharacterMethod(String string) {
		if (string.contains("<")) {
			string = string.replaceAll("<", "&lt;");
		}
		if (string.contains(">")) {
			string = string.replaceAll(">", "&gt;");
		}
		if (string.contains("&")) {
			string = string.replaceAll("&", "&amp;");
		}
		if (string.contains("'")) {
			string = string.replaceAll("'", "&apos;");
		}
		if (string.contains("\"")) {
			string = string.replaceAll("'", "&quot;");
		}
		return string;
	}

	protected String getContent(String source) throws SAXException, IOException, ParserConfigurationException, TransformerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(source)));
		Element root = doc.getDocumentElement();
		Element holder = (Element) root.getElementsByTagNameNS(AbstractSubscription.WSN_URI, "NotificationMessage").item(0);
		Element message = (Element) holder.getElementsByTagNameNS(AbstractSubscription.WSN_URI, "Message").item(0);
		Element content = null;
		for (int i = 0; i < message.getChildNodes().getLength(); i++) {
			if (message.getChildNodes().item(i) instanceof Element) {
				content = (Element) message.getChildNodes().item(i);
				break;
			}
		}
		String messageContent = content.getTextContent();
		return messageContent;
	}

	protected String convertElement(Element element) throws TransformerException {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(element), new StreamResult(writer));

		return writer.toString().trim();
	}

	protected String requestGenerate(String content) {
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:org=\"http://org.apache.servicemix.wsn.push\">");
		contentBuilder.append("<soapenv:Header/>");
		contentBuilder.append("<soapenv:Body>");
		contentBuilder.append("<org:notificationProcess>");
		contentBuilder.append(content);
		contentBuilder.append("</org:notificationProcess>");
		contentBuilder.append("</soapenv:Body>");
		contentBuilder.append("</soapenv:Envelope>");
		return contentBuilder.toString();
	}

	public void send(String content, HttpPost _httpPost, final Object _callPushClient) {
		sendCounter++;
		Date dateSendCounter = new Date();
		log.error("PushClient: sendCounter: " + sendCounter + " " +
				"callBackCompletedCounter:" + callBackCompletedCounter + " " +
				"callBackCounter:" + callBackCounter + " " +
				dateSendCounter.getHours() + ":" + dateSendCounter.getMinutes() + ":" + dateSendCounter.getSeconds());
		try {
			StringEntity entity = null;
			try {
				entity = new StringEntity(content);
//				entity.setContentEncoding("UTF-8");
//				entity.setContentType("text/xml");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			_httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
			_httpPost.setEntity(entity);

			int CountDownValue = PushClient.sendCounter - PushClient.callBackCompletedCounter;
			if (CountDownValue > limit) {
				creatLatch(CountDownValue);
			} else {
				flag = false;
				counterAfterBeyondLimit = 0;
			}

//			Future<HttpResponse> future = asyClient.execute(_httpPost, new FutureCallbackImpl(_callPushClient, flag, latch));
//			
//			if(flag){
//				System.out.println("%%%%%PushClient: latch.getCount():" + latch.getCount());
//				Date dateBefore = new Date();
//				try {
//					latch.await(3, TimeUnit.SECONDS);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				Date dateAfter = new Date();
//				if(((dateAfter.getTime() - dateBefore.getTime()) > 2500)){
//					System.out.println("**********PushClient: restart asyClient!!!");
//					log.info("**********PushClient: restart asyClient!!!");
//					try {
//						JmsSubscription.asyClient.shutdown();
//						JmsSubscription.asyClient = new DefaultHttpAsyncClient();
//						JmsSubscription.asyClient.start();
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOReactorException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
			log.error("PushClient-flag:" + flag);
			sendThread sends = new sendThread(_httpPost, _callPushClient);
			JmsSubscription.pushpool.execute(sends);
			/*Future<HttpResponse> future = asyClient.execute(_httpPost, new FutureCallback<HttpResponse>() {
				
				public void failed(Exception ex) {
					// TODO Auto-generated method stub
					System.out.println("Exception:" + ex.getLocalizedMessage());
					ex.printStackTrace();
					
					callBackCounter ++;
					if(_callPushClient instanceof JmsSubscription){
						((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
					}else if(_callPushClient instanceof SendNotification){
						((SendNotification) _callPushClient).setSuccessfulFlag(false);
					}
					System.out.println("enter the failed function!!!");
					log.error("enter the failed function!!!");
//					log.info("enter the failed function!!!");
				}
				
				public void completed(HttpResponse result) {
					// TODO Auto-generated method stub
					if((flag) && (latch != null)){
						latch.countDown();
					}
					callBackCounter ++;
					callBackCompletedCounter ++;
					int statusCode = result.getStatusLine().getStatusCode();
					switch(statusCode){
					case 200 :
						if(flag){
							counterAfterBeyondLimit ++;
						}
						if(_callPushClient instanceof JmsSubscription){
							((JmsSubscription) _callPushClient).setSuccessfulFlag(true);
						}else if(_callPushClient instanceof SendNotification){
							((SendNotification) _callPushClient).setSuccessfulFlag(true);
						}
//						System.out.println("%%%%%PushClient:status:200");
						log.error("%%%%%PushClient:status:200 number:" + counterAfterBeyondLimit);
						break;
					case 201 : 
						if(_callPushClient instanceof JmsSubscription){
							((JmsSubscription) _callPushClient).setSuccessfulFlag(true);
						}else if(_callPushClient instanceof SendNotification){
							((SendNotification) _callPushClient).setSuccessfulFlag(true);
						}
						System.out.println("%%%%%PushClient:status:201");
						log.error("%%%%%PushClient:status:201");
						break;
					case 202 : 
						if(_callPushClient instanceof JmsSubscription){
							((JmsSubscription) _callPushClient).setSuccessfulFlag(true);
						}else if(_callPushClient instanceof SendNotification){
							((SendNotification) _callPushClient).setSuccessfulFlag(true);
						}
						System.out.println("%%%%%PushClient:status:202");
						log.error("%%%%%PushClient:status:202");
						break;
					case 204 : 
						if(_callPushClient instanceof JmsSubscription){
							((JmsSubscription) _callPushClient).setSuccessfulFlag(true);
						}else if(_callPushClient instanceof SendNotification){
							((SendNotification) _callPushClient).setSuccessfulFlag(true);
						}
						System.out.println("%%%%%PushClient:status:204");
						log.error("%%%%%PushClient:status:204");
						break;
					case 400 : 
						if(_callPushClient instanceof JmsSubscription){
							((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
						}else if(_callPushClient instanceof SendNotification){
							((SendNotification) _callPushClient).setSuccessfulFlag(false);
						}
						System.out.println("%%%%%PushClient:status:400");
						log.error("%%%%%PushClient:status:400");
						break;
					case 401 : 
						if(_callPushClient instanceof JmsSubscription){
							((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
						}else if(_callPushClient instanceof SendNotification){
							((SendNotification) _callPushClient).setSuccessfulFlag(false);
						}
						System.out.println("%%%%%PushClient:status:401");
						log.error("%%%%%PushClient:status:401");
						break;
					case 403 : 
						if(_callPushClient instanceof JmsSubscription){
							((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
						}else if(_callPushClient instanceof SendNotification){
							((SendNotification) _callPushClient).setSuccessfulFlag(false);
						}
						System.out.println("%%%%%PushClient:status:403");
						log.error("%%%%%PushClient:status:403");
						break;
					case 404 :
						if(_callPushClient instanceof JmsSubscription){
							((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
						}else if(_callPushClient instanceof SendNotification){
							((SendNotification) _callPushClient).setSuccessfulFlag(false);
						}
						System.out.println("%%%%%PushClient:status:404");
						log.error("%%%%%PushClient:status:404");
						break;
					case 500 :
						if(_callPushClient instanceof JmsSubscription){
							((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
						}else if(_callPushClient instanceof SendNotification){
							((SendNotification) _callPushClient).setSuccessfulFlag(false);
						}
						System.out.println("%%%%%PushClient:status:500");
						log.error("%%%%%PushClient:status:500");
						break;
					default :
						if(_callPushClient instanceof JmsSubscription){
							((JmsSubscription) _callPushClient).setSuccessfulFlag(true);
						}else if(_callPushClient instanceof SendNotification){
							((SendNotification) _callPushClient).setSuccessfulFlag(true);
						}
						System.out.println("%%%%%PushClient:status:default");
						log.error("%%%%%PushClient:status:default");
					}
				}
				
				public void cancelled() {
					// TODO Auto-generated method stub
					callBackCounter ++;
					if(_callPushClient instanceof JmsSubscription){
						((JmsSubscription) _callPushClient).setSuccessfulFlag(true);
					}else if(_callPushClient instanceof SendNotification){
						((SendNotification) _callPushClient).setSuccessfulFlag(true);
					}
					System.out.println("enter the cancelled function!!!");
					log.error("enter the cancelled function!!!");
				}
			});			
*///			if(flag){
//				Date dateBefore = new Date();
//				log.error("dateBefore: " + dateBefore.getMinutes() + ":" + dateBefore.getSeconds());
//				latch.await(80, TimeUnit.SECONDS);
//				Date dateAfter = new Date();
//				log.error("dateAfter: " + dateAfter.getMinutes() + ":" + dateAfter.getSeconds());
//				log.error("Time-Diff:" + (dateAfter.getTime() - dateBefore.getTime()));
//				if(((dateAfter.getTime() - dateBefore.getTime()) > 75000)){
//					System.out.println("%%%%%PushClient: there is " + (sendCounter - callBackCompletedCounter) + " callback left!!");
//					log.error("%%%%%PushClient: there is " + (sendCounter - callBackCompletedCounter) + " callback left!!");
//					System.out.println("**********PushClient: restart asyClient!!!");
//					log.error("**********PushClient: restart asyClient!!!");
//					JmsSubscription.asyClient.shutdown();
//					JmsSubscription.asyClient = null;
//					Thread.sleep(3000);
//					JmsSubscription.asyClient = new DefaultHttpAsyncClient();
//					JmsSubscription.asyClient.start();
//				}
//				dateBefore = null;
//				dateAfter = null;
//			}
//	        HttpResponse response = future.get();
//	        System.out.println("Response: " + response.getStatusLine());
//	        System.out.println("Shutting down")
			_httpPost.releaseConnection();
		} catch (Exception e) {
			log.error("%%%%%PushClient: enter the outException in PushClient!!!" + e.getLocalizedMessage());
			e.printStackTrace();
//	        System.out.println("Exception: " + e.getLocalizedMessage());
//	        if(_callPushClient instanceof JmsSubscription){
//	        	((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
//	        }else if(_callPushClient instanceof SendNotification){
//	        	((SendNotification) _callPushClient).setSuccessfulFlag(false);
//	        }
			try {
				JmsSubscription.asyClient.shutdown();
				PushClient.sendCounter = 0;
				PushClient.callBackCompletedCounter = 0;
				callBackCounter = 0;

				JmsSubscription.asyClient = null;
				Thread.sleep(3000);
				JmsSubscription.asyClient = new DefaultHttpAsyncClient();
				JmsSubscription.asyClient.start();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOReactorException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		} finally {
			if (_httpPost != null) {
				_httpPost.releaseConnection();
			}
		}
		if (flag) {
			restart();
		}
	}

	public synchronized void restart() {
		if (flag) {
			Date dateBefore = new Date();
			log.error("dateBefore: " + dateBefore.getMinutes() + ":" + dateBefore.getSeconds());
			try {
				latch.await(80, TimeUnit.SECONDS);
				latch = null;
				PushClient.sendCounter = 0;
				PushClient.callBackCompletedCounter = 0;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Date dateAfter = new Date();
			log.error("dateAfter: " + dateAfter.getMinutes() + ":" + dateAfter.getSeconds());
			log.error("Time-Diff:" + (dateAfter.getTime() - dateBefore.getTime()));
			if (((dateAfter.getTime() - dateBefore.getTime()) > 75000) && (PushClient.counterAfterBeyondLimit == 0)) {
				System.out.println("**********PushClient: restart asyClient!!!");
				log.error("**********PushClient: restart asyClient!!!");
				try {
					JmsSubscription.asyClient.shutdown();
//					PushClient.sendCounter = 0;
//					PushClient.callBackCompletedCounter = 0;

					JmsSubscription.asyClient = null;
					Thread.sleep(3000);
					JmsSubscription.asyClient = new DefaultHttpAsyncClient();
					JmsSubscription.asyClient.start();
					flag = false;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void creatLatch(int _CountDownValue) {
		if ((latch == null) && ((sendCounter - callBackCompletedCounter) > limit)) {
			latch = new CountDownLatch(_CountDownValue - threshold);
			flag = true;
			System.out.println("PushClient Beyond the limit!!!");
			log.error("PushClient beyond the limit!!!");
		}
	}

	public class sendThread implements Runnable {
		HttpPost _httpPost;
		Object _callPushClient;

		public sendThread(HttpPost _httpPost, Object _callPushClient) {
			this._httpPost = _httpPost;
			this._callPushClient = _callPushClient;
		}

		public void sendToSub() {
			Future<HttpResponse> future = asyClient.execute(_httpPost, new FutureCallback<HttpResponse>() {

				public void failed(Exception ex) {
					// TODO Auto-generated method stub
					System.out.println("Exception:" + ex.getLocalizedMessage());
					ex.printStackTrace();

					callBackCounter++;
					if (_callPushClient instanceof JmsSubscription) {
						((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
					} else if (_callPushClient instanceof SendNotification) {
						((SendNotification) _callPushClient).setSuccessfulFlag(false);
					}
					System.out.println("enter the failed function!!!");
					log.error("enter the failed function!!!");
//					log.info("enter the failed function!!!");
				}

				public void completed(HttpResponse result) {
					// TODO Auto-generated method stub
					if ((flag) && (latch != null)) {
						latch.countDown();
					}
					callBackCounter++;
					callBackCompletedCounter++;
					int statusCode = result.getStatusLine().getStatusCode();
					switch (statusCode) {
						case 200:
							if (flag) {
								counterAfterBeyondLimit++;
							}
							if (_callPushClient instanceof JmsSubscription) {
								((JmsSubscription) _callPushClient).setSuccessfulFlag(true);
							} else if (_callPushClient instanceof SendNotification) {
								((SendNotification) _callPushClient).setSuccessfulFlag(true);
							}
							System.out.println("%%%%%PushClient:status:200");
							log.error("%%%%%PushClient:status:200 number:" + counterAfterBeyondLimit);
							break;
						case 201:
							if (_callPushClient instanceof JmsSubscription) {
								((JmsSubscription) _callPushClient).setSuccessfulFlag(true);
							} else if (_callPushClient instanceof SendNotification) {
								((SendNotification) _callPushClient).setSuccessfulFlag(true);
							}
							System.out.println("%%%%%PushClient:status:201");
							log.error("%%%%%PushClient:status:201");
							break;
						case 202:
							if (_callPushClient instanceof JmsSubscription) {
								((JmsSubscription) _callPushClient).setSuccessfulFlag(true);
							} else if (_callPushClient instanceof SendNotification) {
								((SendNotification) _callPushClient).setSuccessfulFlag(true);
							}
							System.out.println("%%%%%PushClient:status:202");
							log.error("%%%%%PushClient:status:202");
							break;
						case 204:
							if (_callPushClient instanceof JmsSubscription) {
								((JmsSubscription) _callPushClient).setSuccessfulFlag(true);
							} else if (_callPushClient instanceof SendNotification) {
								((SendNotification) _callPushClient).setSuccessfulFlag(true);
							}
							System.out.println("%%%%%PushClient:status:204");
							log.error("%%%%%PushClient:status:204");
							break;
						case 400:
							if (_callPushClient instanceof JmsSubscription) {
								((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
							} else if (_callPushClient instanceof SendNotification) {
								((SendNotification) _callPushClient).setSuccessfulFlag(false);
							}
							System.out.println("%%%%%PushClient:status:400");
							log.error("%%%%%PushClient:status:400");
							break;
						case 401:
							if (_callPushClient instanceof JmsSubscription) {
								((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
							} else if (_callPushClient instanceof SendNotification) {
								((SendNotification) _callPushClient).setSuccessfulFlag(false);
							}
							System.out.println("%%%%%PushClient:status:401");
							log.error("%%%%%PushClient:status:401");
							break;
						case 403:
							if (_callPushClient instanceof JmsSubscription) {
								((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
							} else if (_callPushClient instanceof SendNotification) {
								((SendNotification) _callPushClient).setSuccessfulFlag(false);
							}
							System.out.println("%%%%%PushClient:status:403");
							log.error("%%%%%PushClient:status:403");
							break;
						case 404:
							if (_callPushClient instanceof JmsSubscription) {
								((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
							} else if (_callPushClient instanceof SendNotification) {
								((SendNotification) _callPushClient).setSuccessfulFlag(false);
							}
							System.out.println("%%%%%PushClient:status:404");
							log.error("%%%%%PushClient:status:404");
							break;
						case 500:
							if (_callPushClient instanceof JmsSubscription) {
								((JmsSubscription) _callPushClient).setSuccessfulFlag(false);
							} else if (_callPushClient instanceof SendNotification) {
								((SendNotification) _callPushClient).setSuccessfulFlag(false);
							}
							System.out.println("%%%%%PushClient:status:500");
							log.error("%%%%%PushClient:status:500");
							break;
						default:
							if (_callPushClient instanceof JmsSubscription) {
								((JmsSubscription) _callPushClient).setSuccessfulFlag(true);
							} else if (_callPushClient instanceof SendNotification) {
								((SendNotification) _callPushClient).setSuccessfulFlag(true);
							}
							System.out.println("%%%%%PushClient:status:default");
							log.error("%%%%%PushClient:status:default");
					}
				}

				public void cancelled() {
					// TODO Auto-generated method stub
					callBackCounter++;
					if (_callPushClient instanceof JmsSubscription) {
						((JmsSubscription) _callPushClient).setSuccessfulFlag(true);
					} else if (_callPushClient instanceof SendNotification) {
						((SendNotification) _callPushClient).setSuccessfulFlag(true);
					}
					System.out.println("enter the cancelled function!!!");
					log.error("enter the cancelled function!!!");
				}
			});


		}

		@Override
		public void run() {
			sendToSub();
		}
	}
}
