package org.apache.servicemix.wsn.router.wsnPolicy;

/**
 * @author shoren
 * @date 2013-3-5
 */

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.Mina.shorenMinaTest.mgr.RtMgr;
import org.Mina.shorenMinaTest.mgr.base.SysInfo;
import org.apache.servicemix.wsn.router.router.IRouter;
import org.apache.servicemix.wsn.router.router.Router;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.ComplexGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetHost;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetMsg;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetRep;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;


/**
 *
 */
public class ShorenUtils{
	
	private static boolean WholeMsg = true;
	private static final String CGROUPFILE = "complexGroupsMsgs.xml";
	private static final String POLICYMSG = "policyMsg.xml";
	private static List<String> topics = new ArrayList<String>();
	private static ReadWriteLock lock = new ReentrantReadWriteLock();

	public static List<String> getTopics() {
		return topics;
	}

	public static void setTopics(List<String> topics) {
		ShorenUtils.topics = topics;
	}

	public static boolean isWholeMsg() {
		return WholeMsg;
	}

	public static void setWholeMsg(boolean wholeMsg) {
		WholeMsg = wholeMsg;
	}

	//�õ����е�topic
/*	public static List getWholeTopics()
	{
		List<String> topics = new ArrayList<String>();
		
		return topics;
	}*/
	
	//��ȡ�Ѿ�������topic
	public static List getPolicyTopics()
	{
		List<String> topics = new ArrayList<String>();
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
		NodeList nodes = doc.getElementsByTagName("policyMsg");
		if(nodes == null)
			return null;
		
		topics.clear();   //clear 
		for(int i=0; i<nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			if(!node.hasAttributes())
				break;
			NamedNodeMap  attrs = node.getAttributes();
			for(int j=0; j<attrs.getLength(); j++)
			{
				Node attr = attrs.item(j);
				if(attr.getNodeName().equals("targetTopic"))
				{
					topics.add(attr.getNodeValue());
					break;
				}
			}
		}
		
		return topics;
	}
	
	
	//valueû��topic���ԵĴ��?��Ҫ������������ʾ��Ⱥ��Ϣ��
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
				//ֻ����һ�����ӽڵ㼴�ɡ�
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
			//ֻ����һ�����ӽڵ㼴�ɡ�
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

			return stream.toString("UTF-8");//ԭ��UTF-8
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
		Lock writeLock = lock.writeLock();
		writeLock.lock();
		OutputStreamWriter o = new OutputStreamWriter(new FileOutputStream(
				new File(filename)),
				"UTF-8");	
		try {
			o.write(contents);
			o.flush();
			o.close();
		} finally {
			writeLock.unlock();
		}
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
	
	//�޸ģ�����Ϊһ��set��һ��д�����еļ�Ⱥ��Ϣ
	public static void encodeAllComplexGroups(WsnPolicyMsg policy)
	{
		Document doc = null;		
		String fileName = CGROUPFILE;
		WsnPolicyMsgCodec codec = new WsnPolicyMsgCodec();
		doc = codec.getDocument();	
		Node root = doc.createElement("GroupMsgs");
		doc.appendChild(root);
		
		List<ComplexGroup> complexGroups = policy.getComplexGroups();
		List<TargetGroup> targetGroups = policy.getTargetGroups();
		Iterator itc = complexGroups.iterator();
		while(itc.hasNext())
		{
			ComplexGroup cg = (ComplexGroup) itc.next();
			root.appendChild(codec.encodeTargetMsg(cg));
		}
		
		Iterator itg = targetGroups.iterator();
		while(itg.hasNext())
		{
			TargetGroup tg = (TargetGroup)itg.next();
			root.appendChild(codec.encodeTargetMsg(tg));
		}
		
		try {
			writeFile(getXml(doc.getDocumentElement()), fileName);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	// ɾ�����xml�ļ�����
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
		
	   //��ȡroot�ڵ�
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
	
	//�����ʱ��鿴�Ƿ��д˲��Ե���Ϣ��������ɾ������ӣ���û�У���ֱ����ӡ�
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
			//��û���ļ��������һ����
			doc = ShorenUtils.createDocument();
			root = doc.createElement("WsnPolicyMsgs");
			doc.appendChild(root);
		}
		
	   //��ȡroot�ڵ�
		WsnPolicyMsgCodec codec = new WsnPolicyMsgCodec();
		codec.setDocument(doc);		
		root = doc.getElementsByTagName("WsnPolicyMsgs").item(0);	
		
		//�ж���û�е�ǰ���Ե���Ϣ������ɾ��
		Node node = getPolicyMsg(root, msg.getTargetTopic());
		if(node != null)
			root.removeChild(node);
		
		//д�뵱ǰ������Ϣ
		if(codec.encodePolicyMsg(msg) != null) {
			root.appendChild(codec.encodePolicyMsg(msg));
		}
		try {
			writeFile(getXml(doc.getDocumentElement()), fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//���ļ��в���topic������Ϣ��������node.
	protected static Node getPolicyMsg(Node root, String topic)
	{
		NodeList nodes = root.getChildNodes();
		if(nodes != null)
		{
			for(int i=0; i<nodes.getLength(); i++)
			{
				Node node = nodes.item(i);
				String name = node.getNodeName();
				//��Ҫ�ж�topic����������������������������������������
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
	
	public static void deleteName(String name) {
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
			//��û���ļ��������һ����
			doc = ShorenUtils.createDocument();
			root = doc.createElement("WsnPolicyMsgs");
			doc.appendChild(root);
		}
		
	   //��ȡroot�ڵ�
		WsnPolicyMsgCodec codec = new WsnPolicyMsgCodec();
		codec.setDocument(doc);		
		root = doc.getElementsByTagName("WsnPolicyMsgs").item(0);	
		
		NodeList nodes = root.getChildNodes();
		//ɾ����Եı���
		ArrayList<String> topics = new ArrayList<String>();	
		if(nodes != null)
		{
			for(int i=0; i<nodes.getLength(); i++)
			{
				Node node = nodes.item(i);
				String nm = node.getNodeName();
				//��Ҫ�ж�topic����������������������������������������
				if(nm.equals("policyMsg") && node.hasAttributes())
				{
					NamedNodeMap  attrs = node.getAttributes();
					for(int j=0; j<attrs.getLength(); j++)
					{
						Node attr = attrs.item(j);
						if(attr.getNodeName().equals("targetTopic"))
						{
							String value = attr.getNodeValue();
							if(value.startsWith(name))
							{
								topics.add(value);
								root.removeChild(node);
								}
						}
					}
				}
			}
		}

		try {
			writeFile(getXml(doc.getDocumentElement()), fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		RtMgr rm = RtMgr.getInstance();
		
		if(!topics.isEmpty() && rm.getRep().addr.equals(rm.getLocalAddr())) {
			IRouter ir = new Router();
			for(String topic : topics) {
				ir.route(topic);
			}
		}
	}
	
	public static void changeName(String oldName, String newName) {
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
			//��û���ļ��������һ����
			doc = ShorenUtils.createDocument();
			root = doc.createElement("WsnPolicyMsgs");
			doc.appendChild(root);
		}
		
	   //��ȡroot�ڵ�
		WsnPolicyMsgCodec codec = new WsnPolicyMsgCodec();
		codec.setDocument(doc);		
		root = doc.getElementsByTagName("WsnPolicyMsgs").item(0);	
		
		NodeList nodes = root.getChildNodes();
		ArrayList<Node> add = new ArrayList<Node>();
		if(nodes != null)
		{
			for(int i=0; i<nodes.getLength(); i++)
			{
				Node node = nodes.item(i);
				String name = node.getNodeName();
				//��Ҫ�ж�topic����������������������������������������
				if(name.equals("policyMsg") && node.hasAttributes())
				{
					NamedNodeMap  attrs = node.getAttributes();
					for(int j=0; j<attrs.getLength(); j++)
					{
						Node attr = attrs.item(j);
						if(attr.getNodeName().equals("targetTopic"))
						{
							String value = attr.getNodeValue();
							if(value.startsWith(oldName))
							{
								Node n = node.cloneNode(true);
								value = value.replace(oldName, newName);
								n.getAttributes().item(j).setNodeValue(value);
								add.add(n);
								root.removeChild(node);
								}
						}
					}
				}
			}
		}
		
		for(Node n : add) {
			root.appendChild(n);
		}

		try {
			writeFile(getXml(doc.getDocumentElement()), fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//������еĲ�����Ϣ
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
			JOptionPane.showMessageDialog(null, "��ȡ"+fileName+"�ļ�����");
			return null;
		}

		Node root = doc.getDocumentElement();	
		
		ArrayList<WsnPolicyMsg> all = new ArrayList<WsnPolicyMsg>();
		
		WsnPolicyMsgCodec codec = new WsnPolicyMsgCodec(doc);
		
		for(int i = 0; i < root.getChildNodes().getLength(); i++) {
			Node node = root.getChildNodes().item(i);
			policy = new WsnPolicyMsg();
			codec.decodePolicyMsg(node, policy);
			if(policy.getTargetTopic() != null && policy.getAllGroups() != null) {
				all.add(policy);
			}
		}	
		return all;
	}
	
	//
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
	
	//�˴���policyֻ�����������Ž����ļ�Ⱥ����showPolicyMsgһ��ʹ�á�
	public static WsnPolicyMsg decodeAllComplexGroups()
	{
		WsnPolicyMsg policy = new WsnPolicyMsg();
		Document doc = null;
		String fileName = CGROUPFILE;
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
		NodeList nodes = root.getChildNodes();
		if(nodes != null)
		{
			WsnPolicyMsgCodec codec = new WsnPolicyMsgCodec(doc);
			for(int i=0; i<nodes.getLength(); i++)
			{
				Node node = nodes.item(i);
				String name = node.getNodeName();
				if(name.equals("ComplexGroup"))
				{
					policy.getComplexGroups().add(codec.decodeComplexGroup(node));
				}else if(name.equals("TargetGroup")){
					policy.getTargetGroups().add(codec.decodeTargetGroup(node));
				}
			}
		}
		
		return policy;		
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
	
		Lock readLock = lock.readLock();
		readLock.lock();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename),"UTF-8"));
		StringBuffer result = new StringBuffer();
		try {
			String tmp = reader.readLine();

			while (tmp != null)
			{
				result.append(tmp + "\n");
				tmp = reader.readLine();
			}
		} finally {
			readLock.unlock();
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
	
	public static void main(String[] args)
	{
		ArrayList<WsnPolicyMsg> all = getAllPolicy();
		for(WsnPolicyMsg msg : all) {
			System.out.println(msg.getTargetTopic());
			System.out.println(msg.getAllGroups());
		}
//		WsnPolicyMsg wpm = createTestMsg();
	//	encodePolicyMsg(wpm);
		
/*		ComplexGroup cg = createTestComplex();
		encodeAllComplexGroups(cg);*/
//		WsnPolicyMsg wpm = ShorenUtils.decodePolicyMsg("all:alarm");
	//	System.out.println(wpm.getTargetTopic()+"  "+wpm.getAllGroups().toString());
	}
}
