package org.apache.servicemix.application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.Endpoint;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.servicemix.jmsImpl.JmsNotificationBrokerImpl;
import org.apache.servicemix.jmsImpl.JmsSubscriptionImpl;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpPost;
import org.apache.servicemix.application.IWsnProcess;
import org.apache.servicemix.wsn.EndpointRegistrationException;
import org.apache.servicemix.wsn.push.ListItem;
import org.apache.servicemix.wsn.push.NotifyObserver;
//import org.apache.servicemix.wsn.router.admin.GroupAllInfo;
import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;
import org.apache.servicemix.wsn.AbstractCreatePullPoint;
import org.apache.servicemix.wsn.AbstractNotificationBroker;
import org.apache.servicemix.wsn.AbstractSubscription;
import org.apache.servicemix.wsn.jms.JmsCreatePullPoint;
import org.apache.servicemix.wsn.jms.JmsSubscription;
import org.apache.servicemix.wsn.jms.JmsNotificationBroker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import javax.jms.Message;
import org.oasis_open.docs.wsn.b_2.CreatePullPointResponse;
import org.oasis_open.docs.wsn.b_2.SubscribeResponse;
import org.oasis_open.docs.wsn.bw_2.UnableToDestroySubscriptionFault;

import com.bupt.wangfu.ldap.Ldap;
import com.bupt.wangfu.ldap.TopicEntry;

@WebService(endpointInterface = "org.apache.servicemix.application.IWsnProcess", serviceName = "IWsnProcess")
public class WsnProcessImpl implements IWsnProcess {
	private static int counter = 0;
	private JmsCreatePullPoint createpullpoint = null;
	private JmsNotificationBroker notificationbroker = null;
	private ConnectionFactory connectionFactory = null;
	private Connection connection = null;
	private Session session = null;
	private Topic topic = null;
	public static List<ListItem> localtable;
	public static Queue<String> mes = new LinkedList<String>();
	private MessageProducer producer = null;
	private static Map<String, MessageProducer> topicMap = new LinkedHashMap<String, MessageProducer>();
	private static Log log = LogFactory.getLog(WsnProcessImpl.class);
	protected JAXBContext jaxbContext;
	protected Set<Class> endpointInterfaces;
	private JmsSubscriptionImpl allVal;

	public static WSNTopicObject topicTree;

	public static String newsubscribeaddree;

	public static String newtopic;

	public static int countsubscr = 0;

	public static HashMap<String, ArrayList<subscribeTime>> subscribeMap = new HashMap<String, ArrayList<subscribeTime>>();

	public static HashMap<String, ArrayList<Double>> subzuobiao = new HashMap<String, ArrayList<Double>>();

	public void init() throws JAXBException {
		String url = "tcp://localhost:61616";
		/*
		 * if(connectionFactory == null){ // ConnectionFactory ：连接工厂，JMS 用它创建连接
		 * connectionFactory = new ActiveMQConnectionFactory(
		 * ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD,
		 * url); }
		 */
		createpullpoint = new JmsCreatePullPoint("Brokername");
		// createpullpoint.setConnectionFactory(connectionFactory);
		notificationbroker = new JmsNotificationBrokerImpl("Brokername");
		// notificationbroker.setConnectionFactory(connectionFactory);
		// try {
		// createpullpoint.init();
		// notificationbroker.init();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		endpointInterfaces = createEndpointInterfaces();
		jaxbContext = createJAXBContext(endpointInterfaces);

		// 向管理员注册
		mgrInstance mgrinst = new mgrInstance();
		Thread mgrThread = new Thread(mgrinst);
		mgrThread.start();
		// 从openldap数据库加载主题树
		try {
			System.out.println("read ldap starting!");
			WsnProcessImpl.readTopicTree("ou=all_test,dc=wsn,dc=com");
			WsnProcessImpl.printTopicTree();
			System.out.println("read ldap successed!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		allVal = new JmsSubscriptionImpl("all");

		// RtMgr.getInstance();
		// System.out.println("init finished!");
	}

	public Set<Class> createEndpointInterfaces() {
		Set<Class> Interfaces = new HashSet<Class>();
		// Check additional interfaces
		for (Class pojoClass = createpullpoint.getClass(); pojoClass != Object.class; pojoClass = pojoClass
				.getSuperclass()) {
			for (Class cl : pojoClass.getInterfaces()) {
				if (getWebServiceAnnotation(cl) != null) {
					Interfaces.add(cl);
				}
			}
		}
		for (Class pojoClass = notificationbroker.getClass(); pojoClass != Object.class; pojoClass = pojoClass
				.getSuperclass()) {
			for (Class cl : pojoClass.getInterfaces()) {
				if (getWebServiceAnnotation(cl) != null) {
					Interfaces.add(cl);
				}
			}
		}
		return Interfaces;
	}

	@SuppressWarnings("unchecked")
	protected WebService getWebServiceAnnotation(Class clazz) {
		for (Class cl = clazz; cl != null; cl = cl.getSuperclass()) {
			WebService ws = (WebService) cl.getAnnotation(WebService.class);
			if (ws != null) {
				return ws;
			}
		}
		return null;
	}

	public String WsnProcess(String message) {

		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

		Method webMethod = null;
		Object input;
		Object output = "null";
		/*
		 * 取消订阅消息判断 取消订阅处理
		 */
		if (message.indexOf("wsnt:Unsubscribe") > 0) {
			String topicname = splitstring(
					"<wsnt:TopicExpression Dialect=\"http://docs.oasis-open.org/wsn/t-1/TopicExpression/Simple\">",
					"</wsnt:TopicExpression>", message).trim();
			String subscriberAddress = splitstring("<wsnt:SubscriberAddress>",
					"</wsnt:SubscriberAddress>", message).trim();
			System.out.println("======================== topicName:"
					+ topicname);
			System.out.println("======================== subscriberAddress:"
					+ subscriberAddress);

			/**
			 * ================================================================
			 * ======================== topic tree subscribe new added at
			 * 2013/12/1
			 */
			String[] topicPath = topicname.split(":");
			WSNTopicObject current = WsnProcessImpl.topicTree;
			int flag = 0;
			for (int i = 0; i < topicPath.length - 1; i++) {
				if (current.getTopicentry().getTopicName().equals(topicPath[i])) {
					for (int counter = 0; counter < current.getChildrens()
							.size(); counter++) {
						if (current.getChildrens().get(counter).getTopicentry()
								.getTopicName().equals(topicPath[i + 1])) {
							current = current.getChildrens().get(counter);
							flag++;
							break;
						}
					}
				} else {

					log.error("subscribe faild! there is not this topic in the topic tree!");
					return "faild";
				}
			}
			System.out.println("match time is: " + flag + "path.length is: "
					+ topicPath.length);
			if (flag == topicPath.length - 1) {
				// 判断订阅是否已经存在
				int i;
				boolean isdele = false;
				for (i = 0; i < current.getSubscribeAddress().size(); i++) {
					if (current.getSubscribeAddress().get(i)
							.equals(subscriberAddress)) {
						current.getSubscribeAddress().remove(i);
						System.out.println("unsubscribe success");
						isdele = true;
						break;
					}
				}
				// 若订阅不存在
				if (isdele == false) {
					// current.getSubscribeAddress().add(subscriberAddress);
					System.out
							.println("subscribe is not exists in the system,there is no need to do it!");
					log.error("subscribe is not exists in the system,there is no need to do it!");
					return "faild";
				}
			} else
				System.out
						.println("unsubscribe faild! there is not this topic in the topic tree!");
			/*
			 * 释放空间
			 */
			System.out.println("unsub befor----------------------"
					+ AbstractNotificationBroker.subscriptions);
			JmsSubscription subscription = AbstractNotificationBroker.subscriptions
					.get(subscriberAddress + "/" + topicname);
			try {
				subscription.unsubscribe();
			} catch (UnableToDestroySubscriptionFault e) {
				e.printStackTrace();
			}
			AbstractNotificationBroker.subscriptions.remove(subscriberAddress
					+ "/" + topicname);
			System.out.println("unsub after----------------------"
					+ AbstractNotificationBroker.subscriptions);

			return "success";
		}

		if (message.indexOf("wsnt:Notify") > 0
				|| message.indexOf("wsnt:NotificationMessage") > 0) {
			try {
				// System.out.println("notify----------------------"+message);
				fast_Notify(message);
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "success!";
		} else {
			// 保存订阅地址
			newsubscribeaddree = splitstring("<wsnt:SubscriberAddress>",
					"</wsnt:SubscriberAddress>", message).trim();
			if (message.indexOf("wsnt:TopicExpression") > 0){
				newtopic = splitstring(
						"<wsnt:TopicExpression Dialect=\"http://docs.oasis-open.org/wsn/t-1/TopicExpression/Simple\">",
						"</wsnt:TopicExpression>", message).trim();
			}
			// 增加变量
//			postMethod.addParameter("lad", "09/24/2011");

			// 防御订阅攻击
			
			if (limitSubscribe(newtopic, newsubscribeaddree)) {
				System.out.println("已防御");
				
				HttpClient client = new HttpClient();
				String url = newsubscribeaddree;
				PostMethod postMethod = new PostMethod(url);
				String str = "";
				try {
					client.executeMethod(postMethod);
					str = new String(postMethod.getResponseBodyAsString().getBytes(
							"utf-8"));
					postMethod.releaseConnection();
					Thread.sleep(10000);
				} catch (HttpException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(isAllow(str)!=true){
					return "faild";
				}
			}

			Source source = new StreamSource(new java.io.StringReader(message));
			try {
				input = jaxbContext.createUnmarshaller().unmarshal(source);
				Class inputClass = input.getClass();
				if (input instanceof JAXBElement) {
					inputClass = ((JAXBElement) input).getDeclaredType();
					input = ((JAXBElement) input).getValue();
				}
				for (Class clazz : endpointInterfaces) {
					for (Method mth : clazz.getMethods()) {
						Class[] params = mth.getParameterTypes();
						if (params.length == 1
								&& params[0].isAssignableFrom(inputClass)) {
							if (webMethod == null) {
								webMethod = mth;
							} else if (!mth.getName().equals(
									webMethod.getName())) {
								throw new IllegalStateException(
										"Multiple methods matching parameters");
							}
						}
					}
				}
				if (webMethod == null) {
					throw new IllegalStateException(
							"Could not determine invoked web method");
				}
				// System.out.println("************************invoke BEFORE" +
				// new Date().getTime());

				boolean oneWay = webMethod.getAnnotation(Oneway.class) != null;

				/**
				 * 
				 * 区分createPullPoint消息、subscribe消息并进行不同的调用；
				 * 添加取消订阅功能时要在此处区分unsubscribe消息并调用相应的处理逻辑。
				 */
				if (webMethod.getName().equals("createPullPoint"))
					output = webMethod.invoke(createpullpoint,
							new Object[] { input });
				else {
					output = webMethod.invoke(notificationbroker,
							new Object[] { input });
					startSubscription(newsubscribeaddree, newtopic);
					// for(int i=0;i<WsnProcessImpl.localtable.size();i++){
					// System.out.println(i+" "+WsnProcessImpl.localtable.get(i).getTopicName()+" "+WsnProcessImpl.localtable.get(i).getSubscriberAddress());
					// }
				}
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			countsubscr++;
			System.out.println("topic:" + newtopic + "-----sum:" + countsubscr);
			// System.out.println("sub1----------------------"+AbstractNotificationBroker.subscriptions);
			return convertResponse(output, webMethod);
		}
	}

	private boolean isAllow(String str) {
		// TODO Auto-generated method stub
		return true;
	}

	private boolean limitSubscribe(String newtopic, String newsubscribeaddree) {
		subscribeTime st = new subscribeTime();
		st.setTopic(newtopic);
		st.setDate(new Date());
		if (!subscribeMap.containsKey(newsubscribeaddree)) {
			subscribeMap
					.put(newsubscribeaddree, new ArrayList<subscribeTime>());
		}
		
		if (subscribeMap.get(newsubscribeaddree).size() < 10) {
			subscribeMap.get(newsubscribeaddree).add(st);
		} else {
			ArrayList<Double> newzuobiao = getZuobiao(subscribeMap
					.get(newsubscribeaddree));
			
			KMeans cluster = new KMeans(subscribeMap.size());
			// 读取数据
			cluster.readData(subzuobiao, newzuobiao, newsubscribeaddree);
			// 聚类过程
			cluster.cluster();
			// 输出结果
			subzuobiao.put(newsubscribeaddree, newzuobiao);
			return cluster.getResult();
		}
		return false;
	}

	public String convertResponse(Object message, Method webmethod) {
		try {
			String ans = "";
			if (webmethod.getName().equals("createPullPoint")) {
				CreatePullPointResponse temp = (CreatePullPointResponse) message;
				ans = temp.getPullPoint().toString();
			}
			if (webmethod.getName().equals("subscribe")) {
				SubscribeResponse temp = (SubscribeResponse) message;
				ans = temp.getSubscriptionReference().toString();
			}
			return ans;
		} catch (Exception e) {
			return "Repeated subscriptions";
		}

	}

	public void fast_Notify(String message) throws JMSException {
		// System.out.println("Notificationmessage:  "+message);
		String topicname = splitstring(
				"<wsnt:Topic Dialect=\"http://docs.oasis-open.org/wsn/t-1/TopicExpression/Simple\">",
				"</wsnt:Topic>", message).trim();
		String address = "";

		Iterator iter = notificationbroker.subscriptions.entrySet().iterator();

		// System.out.println("1-------------------------");

		int fast_counter = 0;
		if(notificationbroker.subscriptions.size()>0){
//			System.out.println();
		while (iter.hasNext() && fast_counter <1) {
			fast_counter ++;
//			System.out.println("2-------------------------");
			Map.Entry entry = (Map.Entry) iter.next();
			JmsSubscription val = (JmsSubscription)entry.getValue();
//			
//			TextMessage jmsMessage = new Text ;//= val.session.createTextMessage()
//			jmsMessage.setText(message);
//			System.out.println(System.currentTimeMillis() + "val.onMessage前");
			val.onMessage(message);
//			System.out.println("4-------------------------");
//			System.out.println(System.currentTimeMillis() + "val.onMessage后");

		}
		}else{
			this.allVal.onMessage(message);
		}
//		while (iter.hasNext() && fast_counter < 1) {
//			fast_counter++;
//			// System.out.println("2-------------------------");
//			Map.Entry entry = (Map.Entry) iter.next();
//			JmsSubscription val = (JmsSubscription) entry.getValue();
//			//
//			// TextMessage jmsMessage = new Text ;//=
//			// val.session.createTextMessage()
//			// jmsMessage.setText(message);
//			// System.out.println("3-------------------------");
//			val.onMessage(message);
//			// System.out.println("4-------------------------");
//		}
		// System.out.println("topicname:  "+topicname);
		// for(int i=0;i<WsnProcessImpl.localtable.size();i++){
		// //
		// System.out.println("WsnProcessImpl.localtable.get(i).getTopicName():"
		// + WsnProcessImpl.localtable.get(i).getTopicName());
		// if(WsnProcessImpl.localtable.get(i).getTopicName().equals(topicname))
		// {
		// address = WsnProcessImpl.localtable.get(i).getSubscriberAddress();
		// Iterator iter =
		// notificationbroker.subscriptions.entrySet().iterator();
		// while (iter.hasNext()) {
		// Map.Entry entry = (Map.Entry) iter.next();
		// JmsSubscription val = (JmsSubscription)entry.getValue();
		// //System.out.println("subscribeaddress: "+val.subscriberAddress);
		// //System.out.println("address: "+address);
		// if(val.subscriberAddress.equals(address)&&topicname.equals(val.jmsTopic.getTopicName()))
		// {
		// TextMessage jmsMessage = val.session.createTextMessage();
		// jmsMessage.setText(message);
		// val.onMessage(jmsMessage);
		// }
		// }
		// }
		// }
	}

	public static JAXBContext createJAXBContext(Iterable<Class> interfaceClasses)
			throws JAXBException {
		List<Class> classes = new ArrayList<Class>();
		classes.add(XmlException.class);
		for (Class interfaceClass : interfaceClasses) {
			for (Method mth : interfaceClass.getMethods()) {
				WebMethod wm = (WebMethod) mth.getAnnotation(WebMethod.class);
				if (wm != null) {
					classes.add(mth.getReturnType());
					classes.addAll(Arrays.asList(mth.getParameterTypes()));
				}
			}
		}
		return JAXBContext
				.newInstance(classes.toArray(new Class[classes.size()]));
	}

	@XmlRootElement(name = "Exception")
	public static class XmlException {
		private String stackTrace;

		public XmlException() {
		}

		public XmlException(Throwable e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			stackTrace = sw.toString();
		}

		public String getStackTrace() {
			return stackTrace;
		}

		public void setStackTrace(String stackTrace) {
			this.stackTrace = stackTrace;
		}

		@XmlMixed
		public List getContent() {
			return Collections.singletonList(stackTrace);
		}
	}

	public String splitstring(String s, String e, String string) {
		int start = string.indexOf(s) + s.length();
		int end = string.indexOf(e);
		return string.substring(start, end);
	}

	public void startSubscription(String subscriberAddress, String topic) {
		if (subscriberAddress != null && !subscriberAddress.equals("")) {
			NotifyObserver notifyObserver = new NotifyObserver();
			String topicName = topic;
			System.out.println("*****************************convert topicName"
					+ topicName);

			/**
			 * ================================================================
			 * ======================== topic tree subscribe new added at
			 * 2013/12/1
			 */
			String[] topicPath = topicName.split(":");
			WSNTopicObject current = WsnProcessImpl.topicTree;
			int flag = 0;
			for (int i = 0; i < topicPath.length - 1; i++) {
				if (current.getTopicentry().getTopicName().equals(topicPath[i])) {
					for (int counter = 0; counter < current.getChildrens()
							.size(); counter++) {
						if (current.getChildrens().get(counter).getTopicentry()
								.getTopicName().equals(topicPath[i + 1])) {
							current = current.getChildrens().get(counter);
							flag++;
							break;
						}
					}
				} else {

					log.error("subscribe faild! there is not this topic in the topic tree!");
					break;
				}
			}
			System.out.println("match time is: " + flag + "path.length is: "
					+ topicPath.length);
			if (flag == topicPath.length - 1) {
				// 判断订阅是否已经存在
				int i;
				for (i = 0; i < current.getSubscribeAddress().size(); i++) {
					if (current.getSubscribeAddress().get(i)
							.equals(subscriberAddress)) {
						System.out
								.println("subscribe exists in the sysytem already,there is no need to do it again!");
						log.error("subscribe exists in the sysytem already,there is no need to do it again!");
						break;
					}
				}
				// 若订阅不存在，则添加订阅
				if (i == current.getSubscribeAddress().size())
					current.getSubscribeAddress().add(subscriberAddress);
			} else
				System.out
						.println("subscribe faild! there is not this topic in the topic tree!");
			// ==========================================================================================

			if (WsnProcessImpl.localtable == null) {
				System.out.println("validateSubscription2");
				WsnProcessImpl.localtable = new LinkedList<ListItem>();
				ListItem newItem = new ListItem();
				newItem.setSubscriberAddress(subscriberAddress);
				newItem.setTopicName(topicName);
				WsnProcessImpl.localtable.add(newItem);

				// NotifyObserver notify = new NotifyObserver(topicName,1);
				notifyObserver.setTopicName(topicName);
				notifyObserver.setKind(1);
				notifyObserver.addObserver(RtMgr.getInstance());
				log.debug("The new topic name is " + topicName);
				notifyObserver.notifyMessage();
			} else {

				int nameCounter = 0;
				int addressCounter = 0;

				for (ListItem listItem : WsnProcessImpl.localtable) {
					if ((listItem.getTopicName() == topicName))
						nameCounter++;
					if ((listItem.getTopicName() == topicName)
							&& (listItem.getSubscriberAddress() == subscriberAddress))
						addressCounter++;
				}
				if (nameCounter == 0) {
					// NotifyObserver notify = new NotifyObserver(topicName,1);
					notifyObserver.setTopicName(topicName);
					notifyObserver.setKind(1);
					notifyObserver.addObserver(RtMgr.getInstance());
					notifyObserver.notifyMessage();
				}
				if (addressCounter == 0) {
					ListItem item = new ListItem();
					item.setSubscriberAddress(subscriberAddress);
					item.setTopicName(topicName);
					WsnProcessImpl.localtable.add(item);
				}
			}
		}
	}

	public ArrayList<Double> getZuobiao(ArrayList<subscribeTime> arr) {
		ArrayList<Double> arrDou = new ArrayList<Double>();
		int allSize = arr.size();
		int dupSize = 0;
		double X, Y, Z;
		HashSet set = new HashSet<String>();
		ArrayList<Date> list = new ArrayList<Date>();
		for (subscribeTime st : arr) {
			list.add(st.getDate());
			set.add(st.getTopic());
		}
		dupSize = allSize - set.size();
		Date firstTime = new Date();
		Date lastTime = new Date();
		if(list.size() > 1){
			firstTime = list.get(1);
			lastTime = list.get(list.size() - 1);
		}
		X = 10.0 * ((double) dupSize / (double) set.size());
		Z = lastTime.getTime() - firstTime.getTime();
		ArrayList<Double> spacelist = new ArrayList<Double>();
		for (int i = 1; i < list.size() - 1; i++) {
			spacelist.add((double) (list.get(i + 1).getTime() - list.get(i)
					.getTime()));
			System.out.println("时间间隔："+(double) (list.get(i + 1).getTime() - list.get(i)
					.getTime()));
		}
		double fangCha = getFangCha(spacelist);
		Y = fangCha;
		arrDou.add(X);
		arrDou.add(Y);
		arrDou.add(Z);
		System.out.println("X="+X+",Y="+Y+",Z="+Z);
		return arrDou;
	}

	public static double getFangCha(List<Double> num) {

		double avg = 0.0;
		double count = num.size();
		
		for (int i = 0; i < num.size(); i++) {
			avg = avg + num.get(i);
		}
		avg = avg / count;
		return getFangCha(num, avg);
	}
	public static double getFangCha(List<Double> num, double avg) {

		double count = num.size();
		double ff = 0.0;
		
		for (int i = 0; i < num.size(); i++) {
			ff = ff + (num.get(i) - avg) * (num.get(i) - avg);
		}
		
		ff = ff / count;
		return ff;
	}

	// 从OpenLdap数据库中读取一个给定跟节点的主题树
	public static void readTopicTree(String root_path) throws Exception {

		Socket s = null;
		ObjectInputStream ois = null;// 对象输入流

		ObjectOutputStream oos = null;// 对象输出流

		Object recieveTopicTee = null;
		try {
			s = new Socket();
			AState state = RtMgr.getInstance().getState();
			s.connect(
					new InetSocketAddress(state.getAdminAddr(), state
							.getAdminPort()), 5000);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());

			// 发送请求空object
			WSNTopicObject requestTopicTree = new WSNTopicObject();
			oos.writeObject(requestTopicTree);
			// 接收TopicTree
			while (true) {
				recieveTopicTee = ois.readObject();
				if (recieveTopicTee instanceof WSNTopicObject) {
					topicTree = (WSNTopicObject) recieveTopicTee;
					System.out.println("从管理员处下载主题树完成");
					break;

				} else
					System.out.println("没有下载到主题树，将重新下载");

			}
		} catch (IOException e) {
			System.out.println("从管理员处下载主题数出错，将重新下载");
			readTopicTree(root_path);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (ois != null) {
					ois.close();
				}
				if (s != null) {
					s.close();
				}
			} catch (IOException e) {
				System.out.println("关闭下载主题树的socket出错");
			}
		}

		/*
		 * Ldap ldap = new Ldap(); ldap.connectLdap("10.109.253.6",
		 * "cn=Manager,dc=wsn,dc=com", "123456"); //创建一个队列，用于从数据库中读取一整棵树
		 * Queue<WSNTopicObject> queue = new LinkedList<WSNTopicObject>();
		 * //以根节点为入口读取一棵树 TopicEntry rootNode = ldap.getByDN(root_path);
		 * topicTree = new WSNTopicObject(rootNode, null);
		 * queue.offer(topicTree); while(!queue.isEmpty()){ WSNTopicObject to =
		 * queue.poll(); List<TopicEntry> ls =
		 * ldap.getSubLevel(to.getTopicentry()); if(!ls.isEmpty()){
		 * List<WSNTopicObject> temp = new ArrayList<WSNTopicObject>();
		 * for(TopicEntry t : ls){ WSNTopicObject wto = new WSNTopicObject(t,
		 * to); temp.add(wto); queue.offer(wto); } to.setChildrens(temp); } }
		 */
	}

	// 打印当前内存中存储的topic tree
	public static void printTopicTree() {
		Queue<WSNTopicObject> printQueue = new LinkedList<WSNTopicObject>();
		printQueue.offer(topicTree);
		while (!printQueue.isEmpty()) {
			WSNTopicObject x = printQueue.poll();
			if (x == topicTree)
				System.out.println("root: " + x);
			else
				System.out.println(x + " " + x.getParent());
			List<WSNTopicObject> y = x.getChildrens();
			if (!y.isEmpty()) {
				for (WSNTopicObject w : y) {
					printQueue.offer(w);
					System.out.print(w + " " + w.getParent() + "   ");
				}
				System.out.println();
			}
		}
	}

	public static void updateTopicTree(String root_path) throws Exception {
		WSNTopicObject newtopicTree;

		Socket s = null;
		ObjectInputStream ois = null;// 对象输入流

		ObjectOutputStream oos = null;// 对象输出流

		Object recieveTopicTee = null;
		try {
			s = new Socket();
			AState state = RtMgr.getInstance().getState();
			s.connect(
					new InetSocketAddress(state.getAdminAddr(), state
							.getAdminPort()), 5000);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());

			// 发送请求空object
			WSNTopicObject requestTopicTree = new WSNTopicObject();
			oos.writeObject(requestTopicTree);
			// 接收TopicTree
			while (true) {
				recieveTopicTee = ois.readObject();
				if (recieveTopicTee instanceof WSNTopicObject) {
					newtopicTree = (WSNTopicObject) recieveTopicTee;
					System.out.println("从管理员处下载主题树完成");
					break;

				} else
					System.out.println("没有下载到主题树，将重新下载");

			}

			WSNTopicObject newcurrent = newtopicTree;
			HashMap<String, WSNTopicObject> topicmap = new HashMap<String, WSNTopicObject>();
			Queue<WSNTopicObject> topicQueue = new LinkedList<WSNTopicObject>();
			topicQueue.offer(topicTree);
			while (!topicQueue.isEmpty()) {
				WSNTopicObject x = topicQueue.poll();
				if (x == topicTree)
					System.out.println("root: " + x);
				else
					System.out.println(x + " " + x.getParent());
				topicmap.put(x.getTopicentry().getTopicName(), x);
				List<WSNTopicObject> y = x.getChildrens();
				if (!y.isEmpty()) {
					for (WSNTopicObject w : y) {
						topicQueue.offer(w);
						// System.out.print(w + " " + w.getParent() + "   ");
					}
					System.out.println();
				}
			}
			Queue<WSNTopicObject> newtopicQueue = new LinkedList<WSNTopicObject>();
			newtopicQueue.offer(newtopicTree);
			while (!newtopicQueue.isEmpty()) {
				WSNTopicObject x2 = newtopicQueue.poll();
				if (x2 == newtopicTree)
					System.out.println("root: " + x2);
				else
					System.out.println(x2 + " " + x2.getParent());
				if (topicmap.containsKey(x2.getTopicentry().getTopicName())) {
					x2.setSubscribeAddress(topicmap.get(
							x2.getTopicentry().getTopicName())
							.getSubscribeAddress());
				}
				List<WSNTopicObject> y2 = x2.getChildrens();
				if (!y2.isEmpty()) {
					for (WSNTopicObject w : y2) {
						newtopicQueue.offer(w);
						// System.out.print(w + " " + w.getParent() + "   ");
					}
					System.out.println();
				}
			}
		} catch (IOException e) {
			System.out.println("从管理员处下载主题数出错，将重新下载");
			updateTopicTree(root_path);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (ois != null) {
					ois.close();
				}
				if (s != null) {
					s.close();
				}
			} catch (IOException e) {
				System.out.println("关闭下载主题树的socket出错");
			}
		}
	}

	// 向管理员注册
	public class mgrInstance implements Runnable {

		@Override
		public void run() {
			RtMgr.getInstance();
			System.out.println("init finished!");
		}

	}

	public static void main(String argv[]) throws Exception {

		readTopicTree("ou=all_test,dc=wsn,dc=com");
		printTopicTree();

	}

	public class subscribeTime {

		String topic;
		Date date;

		public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

	}

}
