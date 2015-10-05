/**
 * @author shoren
 * @date 2013-3-5
 * 读写LDAP数据库的功能函数
 * 
 */
package org.apache.servicemix.wsn.router.wsnPolicy;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.naming.NamingException;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.servicemix.application.WSNTopicObject;
import org.apache.servicemix.wsn.router.admin.AdminBase;
import org.apache.servicemix.wsn.router.admin.AdminMgr;
import org.apache.servicemix.wsn.router.msg.tcp.PolicyDB;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.ComplexGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetHost;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetMsg;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetRep;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.bupt.wangfu.ldap.*;

import org.apache.servicemix.wsn.router.topictree.*;

/**
 *
 */
public class ShorenUtils{
	
	private static final String POLICYMSG = "policyMsg.xml";
	private static boolean WholeMsg = true;
	private static List<String> topics = new ArrayList<String>();
	public static Ldap ldap = null;
	
//	public static WSNTopicObject topicTree = new WSNTopicObject();
	

	public static List<String> getTopics() {
		return topics;
	}

	public static void setTopics(List<String> topics) {
		ShorenUtils.topics = topics;
	}

//	public static String getUSERDIR() {
//		return USERDIR;
//	}

	public static boolean isWholeMsg() {
		return WholeMsg;
	}

	public static void setWholeMsg(boolean wholeMsg) {
		WholeMsg = wholeMsg;
	}

	//得到所有的topic
/*	public static List getWholeTopics()
	{
		List<String> topics = new ArrayList<String>();
		
		return topics;
	}*/
	
//	//获取已经定义过的topic
//	public static List getPolicyTopics()
//	{
//		List<String> topics = new ArrayList<String>();
//		Document doc = null;
//		String fileName = USERDIR + POLICYMSG;
//		try {
//			doc = parse(readFile(fileName));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		if(doc == null)						
//		{
//			JOptionPane.showMessageDialog(null, "读取"+fileName+"文件出错");
//			return null;
//		}		
//		Node root = doc.getDocumentElement();
//		NodeList nodes = doc.getElementsByTagName("policyMsg");
//		if(nodes == null)
//			return null;
//		
//		topics.clear();   //clear 
//		for(int i=0; i<nodes.getLength(); i++)
//		{
//			Node node = nodes.item(i);
//			if(!node.hasAttributes())
//				break;
//			NamedNodeMap  attrs = node.getAttributes();
//			for(int j=0; j<attrs.getLength(); j++)
//			{
//				Node attr = attrs.item(j);
//				if(attr.getNodeName().equals("targetTopic"))
//				{
//					topics.add(attr.getNodeValue());
//					break;
//				}
//			}
//		}
//		
//		return topics;
//	}
	
	
	//value没有topic属性的处理，主要用来在树中显示集群信息。
	@SuppressWarnings("rawtypes")
	public static DefaultMutableTreeNode showPolicyMsg(DefaultMutableTreeNode root, WsnPolicyMsg value)
	{
	//	DefaultMutableTreeNode root = new DefaultMutableTreeNode();			
		if(value != null)
		{
			//clean first
			if(!root.isLeaf())
				root.removeAllChildren();
			if(value.getComplexGroups() != null)
			{
				List<ComplexGroup> complexGroups = value.getComplexGroups();
				Iterator it = complexGroups.iterator();
				while(it.hasNext())
				{
					ComplexGroup group = (ComplexGroup) it.next();
					root.add(showComplexGroup(group));					
				}
			}
			
			if(value.getTargetGroups() != null)
			{
				List<TargetGroup> targetGroups = value.getTargetGroups();
				Iterator itt = targetGroups.iterator();
				while(itt.hasNext())
				{
					TargetGroup tg = (TargetGroup) itt.next();
					//System.out.println(tg.getName());
					DefaultMutableTreeNode tgNode = new ShorenTreeNode(tg);
					root.add(tgNode);
				}
			}
		}		
		return root;
	}
	
	protected static DefaultMutableTreeNode showTargetMsg(TargetMsg msg)
	{
		DefaultMutableTreeNode parent = null;
		if(msg instanceof ComplexGroup)
		{
			parent = showComplexGroup((ComplexGroup)msg);
		}else if(msg instanceof TargetGroup)
		{
			parent = showTargetGroup((TargetGroup)msg);
		}
		return parent;
	}
	
	@SuppressWarnings("rawtypes")
	protected static DefaultMutableTreeNode showComplexGroup(ComplexGroup value)
	{
		DefaultMutableTreeNode parent = null;
		if(value != null)
		{
			parent = new ShorenTreeNode(value);
			if(value.getComplexGroups() != null)
			{
				List<ComplexGroup> complexGroups = value.getComplexGroups();
				Iterator it = complexGroups.iterator();
				while(it.hasNext())
				{
					ComplexGroup group = (ComplexGroup) it.next();					
					parent.add(showComplexGroup(group));					
				}
			}
			
			if(value.getTargetGroups() != null)
			{
				List<TargetGroup> targetGroups = value.getTargetGroups();
				Iterator itt = targetGroups.iterator();
				while(itt.hasNext())
				{
					TargetGroup tg = (TargetGroup) itt.next();
					DefaultMutableTreeNode tgNode = new ShorenTreeNode(tg);
					parent.add(tgNode);
				}
			}
		}
		return parent;
	}
	

	@SuppressWarnings("null")
	protected static DefaultMutableTreeNode showTargetGroup(TargetGroup group)
	{
		DefaultMutableTreeNode parent = null;
		if(group != null)
		{
			parent = new ShorenTreeNode(group);
		}		
		return parent;
	}

	
	public static boolean isInNode(DefaultMutableTreeNode node, TargetMsg msg)
	{		
		if(node.getChildCount() != 0)
		{
			for(int i=0; i<node.getChildCount(); i++)
			{
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
				if(child.getUserObject() == msg)
					return true;
				//只检验一级孩子节点即可。
	/*			if(isInNode(child, msg))
					return true;*/
			}
		}
		
		return false;
	}
	
	public static boolean isInPath(DefaultMutableTreeNode node, TargetMsg msg)
	{		
		if(node.getChildCount() == 0)
			return false;
		
		for(int i=0; i<node.getChildCount(); i++)
		{
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
			if(child.getUserObject() == msg)
				return true;
			//只检验一级孩子节点即可。
/*			if(isInNode(child, msg))
				return true;*/
		}
		
		return false;
	}
	
	public static boolean hasNameExisted(DefaultMutableTreeNode node, String name)
	{
		if(node.getChildCount() != 0)
		{
			for(int i=0; i<node.getChildCount(); i++)
			{
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
				
				if(((TargetMsg)child.getUserObject()).getName().equals(name))
					return true;
				
				if(hasNameExisted(child, name))
					return true;
			}
		}
		return false;
	}
	
	public static String getXml(Node node)
	{
		try
		{
			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer = tranFactory.newTransformer();
			//chen
			aTransformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8"); 
			aTransformer.setOutputProperty(OutputKeys.INDENT,"yes");			
			aTransformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			//
			Source src = new DOMSource(node);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Result dest = new StreamResult(stream);
			aTransformer.transform(src, dest);

			return stream.toString("UTF-8");//原来UTF-8
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}
	
	public static void writeFile(String contents, String filename)
	throws IOException
	{
		/*	FileWriter fw = new FileWriter(filename);		
		fw.write(contents);
		fw.flush();
		fw.close();
		*/
		
		OutputStreamWriter o = new OutputStreamWriter(new FileOutputStream(
				new File(filename)),
				"UTF-8");
		o.write(contents);
		o.flush();
		o.close();
	}
	
	public static Document createDocument()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();

			return parser.newDocument();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		return null;
	}
	
	//修改：参数为一个topicentry，一次写进所有的集群信息
	public static void encodeAllComplexGroups(TopicEntry policyEntry)
	{
		try{
			ldap.getByDN(policyEntry.getTopicPath());
		}
		catch (Exception e) {
			// TODO: handle exception
			ldap.create(policyEntry);
		}
		ldap.delete(policyEntry);
		ldap.create(policyEntry);
	}
	
	//编码的时候查看是否有此策略的信息，若有则删除，再添加，若没有，则直接添加。写入ldap
	public static void encodePolicyMsg(TopicEntry topic)
	{
		List<TopicEntry> topicList = ldap.getAllChildrens(topic);
		ldap.deleteWithAllChildrens(topic);
		ldap.create(topic);
		for(int i = 0 ; i < topicList.size(); i++)
			{
				TopicEntry tpentry = topicList.get(i);
				ldap.create(tpentry);
			}
		
		// send this policy message to groups
		PolicyDB policys = new PolicyDB();
		policys.clearAll = false;
		policys.time = System.currentTimeMillis();
		topic.getWsnpolicymsg().setTargetTopic(getFullName(topic));
		policys.pdb.add(topic.getWsnpolicymsg());
		encodePolicyMsg(topic.getWsnpolicymsg());//写入xml文件
		
		spreadPolicyMsg(policys);
	}
	
	// 删除策略xml文件内容
	public static void deleteAllPolicyMsg() {
		Document doc = null;
		Node root = null;
		String fileName = POLICYMSG;
		try {
			doc = parse(readFile(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(doc == null)						
		{
			return;
		}
		
	   //获取root节点
		WsnPolicyMsgCodec codec = new WsnPolicyMsgCodec();
		codec.setDocument(doc);		
		root = doc.getElementsByTagName("WsnPolicyMsgs").item(0);	
		while(root.hasChildNodes()) {
			root.removeChild(root.getFirstChild());
		}
		
		try {
			writeFile(getXml(doc.getDocumentElement()), fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//encode WsnPolicyMsg写入xml文件
	public static void encodePolicyMsg(WsnPolicyMsg msg)
	{
		Document doc = null;
		Node root = null;
		String fileName = POLICYMSG;
		try {
			doc = parse(readFile(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(doc == null)						
		{
			//若没有文件，则生成一个。
			doc = ShorenUtils.createDocument();
			root = doc.createElement("WsnPolicyMsgs");
			doc.appendChild(root);
		}
		
	   //获取root节点
		WsnPolicyMsgCodec codec = new WsnPolicyMsgCodec();
		codec.setDocument(doc);		
		root = doc.getElementsByTagName("WsnPolicyMsgs").item(0);	
		
		//判断有没有当前策略的信息，有则删除
		Node node = getPolicyMsg(root, msg.getTargetTopic());
		if(node != null)
			root.removeChild(node);
		
		//写入当前策略信息
		root.appendChild(codec.encodePolicyMsg(msg));
		try {
			writeFile(getXml(doc.getDocumentElement()), fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//public static void getPolicy(String topic){
//	Document doc = null;
//	Node root = null;
//	String fileName = POLICYMSG;
//	try {
//		doc = parse(readFile(fileName));
//	} catch (IOException e) {
//		e.printStackTrace();
//	}
//	if(doc == null)						
//	{
//		//若没有文件，则生成一个。
//		System.out.println("不存在策略xml文件");
//	}
//	
//	WsnPolicyMsgCodec codec = new WsnPolicyMsgCodec();
//	codec.setDocument(doc);		
//	root = doc.getElementsByTagName("WsnPolicyMsgs").item(0);	
//	
//	//判断有没有当前策略的信息，有则删除
//	NodeList nodes = root.getChildNodes();
//	if(nodes != null)
//	{
//		for(int i=0; i<nodes.getLength(); i++)
//		{
//			Node node = nodes.item(i);
//			String name = node.getNodeName();
//			//需要判断topic！！！！！！！！！！！！！！！！！！！！
//			if(name.equals("policyMsg") && node.hasAttributes())
//			{
//				NamedNodeMap  attrs = node.getAttributes();
//				for(int j=0; j<attrs.getLength(); j++)
//				{
//					Node attr = attrs.item(j);
//					if(attr.getNodeName().equals("TargetGroup"))
//					{
//						String value = attr.getNodeValue();
//						if(value.equals(topic))
//						{
//						//	return node;
//							//System.out
//						}else
//							break;
//					}
//				}
//			}
//		}
//	}
//		
//	}

	
	
	public static void spreadPolicyMsg(Object obj) {
		Socket s = null;
		ObjectOutputStream oos = null;
		
		int times = 3;
		for(GroupUnit g: AdminBase.groups.values()) {
			try {
				s = new Socket(g.addr, g.tPort);
				oos = new ObjectOutputStream(s.getOutputStream());
				oos.writeObject(obj);
				if(--times == 0) {
					break;
				}
			} catch (UnknownHostException e) {
				System.out.println("Unkonwn host "+g.name);
			} catch (IOException e) {
				System.out.println("can't connect host "+g.name);
			}
		}
	}
	
	//从文件中查找topic策略信息，并返回node.
	protected static Node getPolicyMsg(Node root, String topic)
	{
		NodeList nodes = root.getChildNodes();
		if(nodes != null)
		{
			for(int i=0; i<nodes.getLength(); i++)
			{
				Node node = nodes.item(i);
				String name = node.getNodeName();
				//需要判断topic！！！！！！！！！！！！！！！！！！！！
				if(name.equals("policyMsg") && node.hasAttributes())
				{
					NamedNodeMap  attrs = node.getAttributes();
					for(int j=0; j<attrs.getLength(); j++)
					{
						Node attr = attrs.item(j);
						if(attr.getNodeName().equals("targetTopic"))
						{
							String value = attr.getNodeValue();
							if(value.equals(topic))
							{
								return node;
							}else
								break;
						}
					}
				}
			}
		}
		return null;
	}
	public static WsnPolicyMsg decodePolicyMsg(String topic)
	{
		WsnPolicyMsg policy = null;
		Document doc = null;
		String fileName = POLICYMSG;
		try {
			doc = parse(readFile(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(doc == null)
		{
			JOptionPane.showMessageDialog(null, "��ȡ"+fileName+"�ļ�����");
			return null;
		}

		Node root = doc.getDocumentElement();
		Node node = getPolicyMsg(root, topic);
		if(node != null)
		{
			WsnPolicyMsgCodec codec = new WsnPolicyMsgCodec(doc);
			policy = new WsnPolicyMsg();
			codec.decodePolicyMsg(node, policy);
		}
		return policy;
	}
	//
	public static WsnPolicyMsg decodePolicyMsg(TopicEntry topic)
	{	
		TopicEntry tempentry = null;
		try {
			tempentry = ldap.getByDN(topic.getTopicPath());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tempentry.getWsnpolicymsg();
	}
	
	//此处的policy只是用来方便存放解析的集群。与showPolicyMsg一起使用。
	public static WsnPolicyMsg decodeAllComplexGroups()
	{
		TopicEntry tempentry = null;
		try {
			tempentry = ldap.getByDN("ou=complexGroup,dc=wsn,dc=com");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return tempentry.getWsnpolicymsg();		
	}
	/**
	 * Returns a new document for the given XML string.
	 * 
	 * @param xml String that represents the XML data.
	 * @return Returns a new XML document.
	 */
	public static Document parse(String xml)
	{
		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			return docBuilder.parse(new InputSource(new StringReader(xml)));
		}
		catch (Exception e)
		{
		
			e.printStackTrace();
			
		}

		return null;
	}
	
	
	/**
	 * Reads the given filename into a string.
	 * 
	 * @param filename Name of the file to be read.
	 * @return Returns a string representing the file contents.
	 * @throws IOException
	 */
	public static String readFile(String filename) throws IOException
	{
	
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename),"UTF-8"));
		StringBuffer result = new StringBuffer();
		String tmp = reader.readLine();

		while (tmp != null)
		{
			result.append(tmp + "\n");
			tmp = reader.readLine();
		}

		reader.close();

		return result.toString();
	}
	


	public static WsnPolicyMsg createTestMsg()
	{
		List<TargetGroup> targetGroups = new ArrayList<TargetGroup>();
		for(int i=0; i<3; i++)
		{
			TargetGroup tg = new TargetGroup("test" + i);
			targetGroups.add(tg);
		}
		TargetGroup ttg = targetGroups.get(0);
		for(int i=0; i<3; i++)
		{
			TargetRep tr = new TargetRep("rep" + i);			
			ttg.getTargetList().add(tr);
			if(i == 1){
				TargetHost th = new TargetHost("HOST");
				tr.getTargetClients().add(th);
			}
		}
		
		ComplexGroup cg = new ComplexGroup("complexGroup", null, targetGroups);
		List<ComplexGroup> complexGroups = new ArrayList<ComplexGroup>();
		complexGroups.add(cg);
		WsnPolicyMsg wpm = new WsnPolicyMsg("test", complexGroups, targetGroups);
		return wpm;
	}
	
	public static ComplexGroup createTestComplex()
	{
		List<TargetGroup> targetGroups = new ArrayList<TargetGroup>();
		for(int i=0; i<3; i++)
		{
			TargetGroup tg = new TargetGroup("test" + i);
			targetGroups.add(tg);
		}
		ComplexGroup cg1 = new ComplexGroup("shorenCG", null, targetGroups);
		List<ComplexGroup> cgs = new ArrayList<ComplexGroup>();
		cgs.add(cg1);
		ComplexGroup cg = new ComplexGroup("MyCG", cgs, targetGroups);
		return cg;
	}
public static ArrayList<WsnPolicyMsg> encodeAllPolicy() throws NamingException{
		
		deleteAllPolicyMsg();
		
		ArrayList<WsnPolicyMsg> allPolicy = new ArrayList<WsnPolicyMsg>();
		//List<TopicEntry> list = new ArrayList<TopicEntry>();
		ldap = new Ldap();
		ldap.connectLdap(AdminMgr.ldapAddr,"cn=Manager,dc=wsn,dc=com","123456");
		
		TopicEntry libentry = new TopicEntry();
		libentry.setTopicName("all_test");
		//libentry.setTopicCode("1");
		libentry.setTopicPath("ou=all_test,dc=wsn,dc=com");
		
	    //创建一个队列，用于从数据库中读取一整棵树
	    Queue<WSNTopicObject> queue = new LinkedList<WSNTopicObject>();
	    //以根节点为入口读取一棵树
	    TopicTreeManager.topicTree = new WSNTopicObject(libentry, null); 
		queue.offer(TopicTreeManager.topicTree);
		//list.add(libentry);
		if (libentry.getWsnpolicymsg()!= null && !libentry.getWsnpolicymsg().getTargetGroups().isEmpty()){
			libentry.getWsnpolicymsg().setTargetTopic(getFullName(libentry));
	    	allPolicy.add(libentry.getWsnpolicymsg());
	    	encodePolicyMsg(libentry.getWsnpolicymsg());
	    }
		while(!queue.isEmpty()){
			WSNTopicObject to = queue.poll();
			List<TopicEntry> ls = ldap.getSubLevel(to.getTopicentry());
			if(!ls.isEmpty()){
				List<WSNTopicObject> temp = new ArrayList<WSNTopicObject>();
				for(TopicEntry t : ls){
					WSNTopicObject wto = new WSNTopicObject(t, to);
					temp.add(wto);
					queue.offer(wto);
					if (t.getWsnpolicymsg()!= null && !t.getWsnpolicymsg().getTargetGroups().isEmpty()){
						t.getWsnpolicymsg().setTargetTopic(getFullName(t));
				    	allPolicy.add(t.getWsnpolicymsg());
				    	encodePolicyMsg(t.getWsnpolicymsg());
				    }
					
				}
				to.setChildrens(temp);
			}
		}
		
		
		
//		//List<TopicEntry> list = ldap.getWithAllChildrens(libentry);
//		Iterator itr = list.iterator();
//		while (itr.hasNext()) {
//		    TopicEntry tem = (TopicEntry)itr.next();  
//
//		    if (tem.getWsnpolicymsg()!= null && !tem.getWsnpolicymsg().getTargetGroups().isEmpty()){
//		    	tem.getWsnpolicymsg().setTargetTopic(getFullName(tem));
//		    	allPolicy.add(tem.getWsnpolicymsg());
//		    	encodePolicyMsg(tem.getWsnpolicymsg());
//		    }
//		}
		return allPolicy;
	}

public static ArrayList<WsnPolicyMsg> getAllPolicy() {
	WsnPolicyMsg policy = null;
	Document doc = null;
	String fileName = POLICYMSG;
	try {
		doc = parse(readFile(fileName));
	} catch (IOException e) {
		e.printStackTrace();
	}
	if(doc == null)						
	{
		JOptionPane.showMessageDialog(null, "读取"+fileName+"文件出错");
		return null;
	}

	Node root = doc.getDocumentElement();	
	
	ArrayList<WsnPolicyMsg> all = new ArrayList<WsnPolicyMsg>();
	
	WsnPolicyMsgCodec codec = new WsnPolicyMsgCodec(doc);
	
	for(int i = 0; i < root.getChildNodes().getLength(); i++) {
		Node node = root.getChildNodes().item(i);
		policy = new WsnPolicyMsg();
		codec.decodePolicyMsg(node, policy);
		if(policy.getTargetTopic() != null && policy.getAllGroups() != null&&policy.getTargetGroups().size()>0) {
			all.add(policy);
		}
	}	
	return all;
}


	public static String getFullName(TopicEntry t) {
		String []prefix = t.getTopicPath().split(",");
    	StringBuffer topic = new StringBuffer();
    	for(int i = prefix.length-1; i>=0; i--) {
    		if(prefix[i].startsWith("ou=")) {
    			if(topic.length()>0) {
	    			topic.append(":");
	    		}
    			if(prefix[i].split("=")[1].equals("all_test")) {
    				topic.append("all");
    			} else {
    				topic.append(prefix[i].split("=")[1]);
    			}
    		}
    	}
    	return topic.toString();
	}
	
	public static void main(String[] args) throws NamingException
	{
		encodeAllPolicy();
	//	System.out.println(TopicTreeManager.topicTree.toString().length());
	//	getPolicy("all:alarm:alarm1");
		ArrayList<WsnPolicyMsg> testPrint = getAllPolicy();
		@SuppressWarnings("rawtypes")
		Iterator itr = testPrint.iterator();
		WsnPolicyMsg tem = (WsnPolicyMsg)itr.next();
		System.out.println(tem);
		System.out.println(itr);
		while (itr.hasNext()) {
			
			
			if (tem != null){
			System.out.println("策略信息TargetTopic："+tem.getTargetTopic());
			System.out.println("策略信息AllGroups："+tem.getAllGroups());
			System.out.println("策略信息ComplexGroups："+tem.getComplexGroups());
			System.out.println("策略信息TargetGroups："+tem.getTargetGroups());
			}else
				System.out.println(tem);
		}
//		encodePolicyMsg(wpm);
		
/*		ComplexGroup cg = createTestComplex();
		encodeAllComplexGroups(cg);*/
		
	//	WsnPolicyMsg wpm = decodePolicyMsg("test");
//		encodePolicyMsg(wpm);
	}
}
