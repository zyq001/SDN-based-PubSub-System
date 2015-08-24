package org.apache.servicemix.wsn.router.wsnPolicy;

/**
 * @author shoren
 * @date 2013-2-25
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.servicemix.wsn.router.wsnPolicy.msgs.ComplexGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetHost;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetMsg;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetRep;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;

/**
 *
 */
public class WsnPolicyInterface extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private static Toolkit kit;
	private static Dimension screenSize;
	
	private static String fileName = "wsnPolicy";
	//若需要全局（整个网络）的信息，即从rtMsg处获得，则为true；
	//若需要本地存储的策略文件信息，即局部信息，则为false。默认是从全局获得。


	private JButton okayBtn = createBtn("确定");
	private JButton cancelBtn = createBtn("取消");
	
	private JButton bt1 = createBtn1();   //受限群组
	private JButton bt2 = createBtn1();   //受限代理
	private JButton bt3 = createBtn1();   //受限主机

	private List<String> msgStyle;  //信息类型,第一个是空格
	private List<String> topics;   //主题，第一个是空格
	private JComboBox msgStyleList = new JComboBox();
	private JComboBox topicList = new JComboBox();

	public HashMap<String,JList>name_List = new HashMap<String,JList>();
	public HashMap<String,List>name_Array = new HashMap<String,List>();
	
	private JTree groupTree;
	private ButtonGroup bg;
	private JRadioButton[] radioButtons;

	public JTree getGroupTree() {
		return groupTree;
	}

	public void setGroupTree(JTree groupTree) {
		this.groupTree = groupTree;
	}
	
	static
	{
		kit = Toolkit.getDefaultToolkit();  
        screenSize = kit.getScreenSize();
	}

	public WsnPolicyInterface()
	{
		super("WSN策略操作");
		Border title1 = BorderFactory.createTitledBorder("策略信息");
		Border title2 = BorderFactory.createTitledBorder("操作选择");
		JPanel msgPanel = createMsgPanel();
		msgPanel.setBorder(title1);
		
		JPanel operaPanel = createRadioPanel();
		operaPanel.setBorder(title2);
		
	/*	JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				msgPanel,createButtonPanel());
		JSplitPane outer = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				operaPanel,splitPane);
		add(outer, BorderLayout.CENTER);*/
		add(operaPanel, BorderLayout.NORTH);
		add(msgPanel, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
		
		//frame conf
		setBounds(screenSize.width/4 , screenSize.height/8, 
				screenSize.width/3 ,3*screenSize.height/4);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		setVisible(true);
	}
	
	protected void initialMsg()
	{
		//查询当前保存的信息类型及其主题
		msgStyle = new ArrayList<String>();
		//将所有的信息类插入msgStyle
		updateMsgStyle();

		topics = new ArrayList<String>();
		updateTopics((String)msgStyleList.getSelectedItem());
	}
	
	
	protected JPanel createMsgPanel()
	{
		JPanel msgPanel = new JPanel();		
		initialMsg();
		
		JPanel msgStylePanel = new JPanel();
		msgStylePanel.add(new JLabel("信息类型"), BorderLayout.WEST);		
		msgStyleList.setPreferredSize(new Dimension(200, 30));
		msgStylePanel.add(msgStyleList,BorderLayout.EAST);
		
		JPanel topicPanel = new JPanel();
		topicPanel.add(new JLabel("目标主题"), BorderLayout.WEST);		
		topicList.setPreferredSize(new Dimension(200, 30));
		topicPanel.add(topicList,BorderLayout.EAST);		
		
		JPanel limitedPanel1 = new JPanel();
		limitedPanel1.add(new JLabel("受限群组"));
		limitedPanel1.add(createGroupTree());
		limitedPanel1.add(bt1);		
		
		JPanel limitedPanel2 = new JPanel();
		limitedPanel2.add(new JLabel("受限代理"), BorderLayout.WEST);
		limitedPanel2.add(createList("regs"), BorderLayout.CENTER);   //key = "regs"
		limitedPanel2.add(bt2, BorderLayout.EAST);
		
		JPanel limitedPanel3 = new JPanel();
		limitedPanel3.add(new JLabel("受限主机"), BorderLayout.WEST);
		limitedPanel3.add(createList("hosts"), BorderLayout.CENTER);   //key = hosts 
		limitedPanel3.add(bt3, BorderLayout.EAST);
		
		bt1.addActionListener(this);
		bt2.addActionListener(this);
		bt3.addActionListener(this);
		
		
		Box box = Box.createVerticalBox();
  		box.add(msgStylePanel);
		box.add(topicPanel);
		box.add(limitedPanel1);
		box.add(limitedPanel2);
		box.add(limitedPanel3);
		msgPanel.setLayout(new BorderLayout());
		msgPanel.add(box, BorderLayout.CENTER);
/*		msgPanel.add(msgStylePanel);
		msgPanel.add(topicPanel);
		msgPanel.add(limitedPanel1);
		msgPanel.add(limitedPanel2);
		msgPanel.add(limitedPanel3);*/		

		return msgPanel;
	}
	
	protected JScrollPane createGroupTree()
	{
		//记得要设置root哈
		groupTree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
		ShorenNodeRenderer ren = new ShorenNodeRenderer();
		groupTree.setCellRenderer(ren);
		groupTree.setShowsRootHandles(true);  //显示前面的分支线
		groupTree.setRootVisible(false);		 //不显示根节点
		
		JScrollPane listScrollPane = new JScrollPane(groupTree);
        listScrollPane.setPreferredSize(new Dimension(250, 100));
        listScrollPane.setMinimumSize(new Dimension(200, 100));
        
        return listScrollPane;
	}
	protected JScrollPane createList(String name)
	{
		DefaultListModel listModel = new DefaultListModel();			
        
        //Create the list and put it in a scroll pane.
		JList nameList = new JList(listModel);
/*		for(int i = 0; i<10;i++)
		{
			listModel.addElement("aaaa" + i);
			listModel.addElement("aaaa" + i);
		}*/
		
		nameList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION); //多选
		name_List.put(name, nameList);
		name_Array.put(name, new ArrayList<TargetMsg>());
        JScrollPane listScrollPane = new JScrollPane(nameList);
        listScrollPane.setPreferredSize(new Dimension(250, 100));
        listScrollPane.setMinimumSize(new Dimension(200, 100));
        
        return listScrollPane;
	}
	
	protected JPanel createRadioPanel()
	{
		JPanel radioPanel = new JPanel();		
		radioButtons = new JRadioButton[3];
        bg = new ButtonGroup();
		radioButtons[0]=new JRadioButton("添加",true);
		bg.add(radioButtons[0]);
		radioPanel.add(radioButtons[0]);
		radioButtons[1]=new JRadioButton("修改",false);
		bg.add(radioButtons[1]);
		radioPanel.add(radioButtons[1]);		  
		radioButtons[2]=new JRadioButton("删除",false);
		bg.add(radioButtons[2]);
		radioPanel.add(radioButtons[2]);  
		
		radioButtons[0].addActionListener(this);
		radioButtons[1].addActionListener(this);
		radioButtons[2].addActionListener(this);
		
		return radioPanel;
	}
	
	protected WsnPolicyMsg getCreatedPolicyMsg()
	{
//		String mStyle = msgStyleList.getSelectedItem().toString();
		String topic = topicList.getSelectedItem().toString();
		WsnPolicyMsg policy = new WsnPolicyMsg(topic);
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) groupTree.getModel().getRoot();
		if(!root.isLeaf()){
			int count = root.getChildCount();
			for(int i=0; i<count; i++)
			{
				DefaultMutableTreeNode child = (DefaultMutableTreeNode)root.getChildAt(i);
				TargetMsg msg = (TargetMsg)child.getUserObject();
				if(msg instanceof ComplexGroup)
					policy.getComplexGroups().add((ComplexGroup)msg);
				else if(msg instanceof TargetGroup)
					policy.getTargetGroups().add((TargetGroup)msg);
			}			
		}
		if(bt2.isEnabled() && name_List.get("regs").getModel().getSize() > 0){			
			ListModel listModel = name_List.get("regs").getModel();
			List<TargetRep> trs = policy.getTargetGroups().get(0).getTargetList();
			trs.clear();
			for(int i=0; i<listModel.getSize(); i++){
				trs.add(new TargetRep(listModel.getElementAt(i).toString()));//??????????
			}
		}
		if(bt2.isEnabled() && name_List.get("hosts").getModel().getSize() > 0){
			ListModel listModel = name_List.get("hosts").getModel();
			TargetRep rep = policy.getTargetGroups().get(0).getTargetList().get(0);
			rep.getTargetClients().clear();
			for(int i=0; i<listModel.getSize(); i++){
				//加入host
				rep.getTargetClients().add(new TargetHost(listModel.getElementAt(i).toString()));//??????????
			}
		}
		if(policy.getComplexGroups().isEmpty() && !policy.getTargetGroups().isEmpty()
				 && (policy.getTargetGroups().size() == 1)){
			TargetGroup tg = policy.getTargetGroups().get(0);
			//如果没有选择代理，说明整个集群受限
			if(tg.getTargetList().isEmpty()){
				tg.setAllMsg(true);
			}
				
		}
		
		//如果选择多个集群，则默认所有集群内部主机均受限
		Set<TargetGroup> allGroups = policy.getAllGroups();
		if(!allGroups.isEmpty() && (allGroups.size()>1)){
			Iterator it = allGroups.iterator();
			while(it.hasNext()){
				((TargetGroup)it.next()).setAllMsg(true);
			}
		}
		
		return policy;
	}
	
	//添加策略信息，并保存在文件中
	public void addPolicyMsg()
	{
		if(!ShorenUtils.isWholeMsg()){
			ShorenUtils.setWholeMsg(true); //新建策略时，从全局获得信息
		}
		String topic = topicList.getSelectedItem().toString();
		WsnPolicyMsg newPolicy = getCreatedPolicyMsg();
		WsnPolicyMsg policy = ShorenUtils.decodePolicyMsg(topic);
		policy.mergeMsg(newPolicy);            //add
		
		ShorenUtils.encodePolicyMsg(policy);  //write		
	}
	
	public void updateMsg()
	{
		if(ShorenUtils.isWholeMsg()){
			ShorenUtils.setWholeMsg(false);  //修改策略信息时，从策略文件获得信息
		}
		//delete the same topic nodes before add new ones
		WsnPolicyMsg policy = getCreatedPolicyMsg();
		ShorenUtils.encodePolicyMsg(policy);  //write
	}
	
	public void deleteMsg()
	{
		if(ShorenUtils.isWholeMsg()){
			ShorenUtils.setWholeMsg(false);  //修改策略信息时，从策略文件获得信息
		}
		String topic = topicList.getSelectedItem().toString();
		WsnPolicyMsg newPolicy = getCreatedPolicyMsg();
		WsnPolicyMsg policy = ShorenUtils.decodePolicyMsg(topic);
		policy.deleteMsg(newPolicy);            //delete
		
		ShorenUtils.encodePolicyMsg(policy);  //write
	}

	//将五个信息框都置为空
	public void setMsgNull()
	{
		msgStyleList.setSelectedIndex(0);
		topicList.setSelectedIndex(0);
		
		((DefaultMutableTreeNode)groupTree.getModel().getRoot()).removeAllChildren();
		groupTree.updateUI();
		
		Iterator it = name_List.keySet().iterator();
		while(it.hasNext())
		{
			String key = (String) it.next();
			name_Array.get(key).clear();
			DefaultListModel listModel = (DefaultListModel) name_List.get(key).getModel();
			listModel.clear();
		}
	}
	
	protected void updateMsgStyle()
	{
		msgStyle.clear();
	//	msgStyle.add("        ");  //第一个是8个空格
		msgStyle.add("MsgNotis");
/*		
		if(ShorenUtils.isWholeMsg())
		{
			//从全局RtMgr处得到信息,存入msgStyle中
			msgStyle.add("aaaa");
			msgStyle.add("cccca");
			msgStyle.add("bbbb");
		}
		else{
			//从策略文件处得到信息，存入msgStyle中
			
		}*/
		for(int i=0; i<msgStyle.size(); i++)
		{
			msgStyleList.addItem(msgStyle.get(i));
		}
	}
	
	//以后要扩展的，根据msgStyle读取相应的topic,或者弃用.
	@SuppressWarnings("unchecked")
	protected void updateTopics(String msgStyle)
	{
		topics.clear();
		topicList.removeAllItems();
		
//		topics.add("        "); //第一个是8个空格
		if(ShorenUtils.isWholeMsg())
		{
			//从全局RtMgr处得到信息,存入topics中
			
		}
		else{
			//从策略文件处得到信息，存入topics中
			topics = ShorenUtils.getPolicyTopics();
		}
		topicList.addItem("        ");
		for(int i=0; i<topics.size(); i++)
		{
			topicList.addItem(topics.get(i));
		}
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == okayBtn)
		{
		//	System.out.println((String)msgStyleList.getSelectedItem());
			//添加
			if(radioButtons[0].isSelected())
			{
				addPolicyMsg();
			}else if(radioButtons[1].isSelected())
			{
				//修改
				updateMsg();
			}else if(radioButtons[2].isSelected())
			{
				//删除
				deleteMsg();
			}
			this.dispose();
		}else if(e.getSource() == cancelBtn)  
		{
			this.dispose();
		}else if(e.getSource() == radioButtons[0] || e.getSource() == radioButtons[1] 
		          ||  e.getSource() == radioButtons[2])  //更换模式，所有选项置空
		{
			setMsgNull();
			//添加的时候，使用
			if(e.getSource() == radioButtons[0])
				ShorenUtils.setWholeMsg(true);
			else
				ShorenUtils.setWholeMsg(false);
			
			updateMsgStyle();
			updateTopics(null);
		}else if(e.getSource() == bt1)
		{
			//传入所选择的topic 
			String topic = topicList.getSelectedItem().toString();
			WsnPolicyGroupInterface inter = new WsnPolicyGroupInterface(topic);
			inter.setParentFrame(this);
			System.out.println("DONE!");
		}else if(e.getSource() == bt2)
		{
			//根据bt1的选择作响应,若root只有一个节点且封装TargetGroup，则继续
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) groupTree.getModel().getRoot();
			if(root.isLeaf()){
				JOptionPane.showMessageDialog(this , "请选择一个且仅一个集群！^_^");
				return;
			}
			//弹出列表供选择
			Object group = ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject();
			if(group instanceof TargetGroup){
				getRegMsg((TargetGroup)group);
			}
			updateBtns();			
		}else if(e.getSource() == bt3)
		{
			if(name_List.get("regs").getModel().getSize() < 1){
				JOptionPane.showMessageDialog(this , "请选择一个且仅一个代理！^_^");
				return;
			}
			//弹出列表供选择
			Object reg = name_Array.get("regs").get(0);
			if(reg instanceof TargetRep){
				getHostMsg((TargetRep)reg);
			}
			System.out.println("DONE!");
		}
		
	}
	
	//get messages from ListFrame
	@SuppressWarnings("rawtypes")
	protected void getRegMsg(TargetGroup group)
	{
		if(group != null && !group.getTargetList().isEmpty()){
			List<TargetRep> regList = group.getTargetList();
			List<Object> lists = Arrays.asList(regList.toArray());
			ListFrame lf = new ListFrame(lists, true);
			lf.setParentFrame(this);
		}
	}
	
	protected void getHostMsg(TargetRep reg)
	{
		if(reg!=null && !reg.getTargetClients().isEmpty()){
			List<TargetHost> regList = reg.getTargetClients();
			List<Object> lists = Arrays.asList(regList.toArray());
			ListFrame lf = new ListFrame(lists, false);
			lf.setParentFrame(this);
		}
	}
	
	protected void updateBtns()
	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) groupTree.getModel().getRoot();
		if(root.getChildCount() > 1 || !root.getChildAt(0).isLeaf())
		{
			bt2.setEnabled(false);
			bt3.setEnabled(false);
		}else
		{
			bt2.setEnabled(true);
			bt3.setEnabled(true);
		}
		if(!bt2.isEnabled() || name_List.get("regs").getModel().getSize() > 1)
		{
			bt3.setEnabled(false);
		}else{
			bt3.setEnabled(true);
		}
	}
	
	protected JPanel createButtonPanel()
	{
		JPanel btnPanel = new JPanel();
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(150));
		box.add(okayBtn);
		box.add(Box.createHorizontalStrut(20));
		box.add(cancelBtn);
		btnPanel.setLayout(new BorderLayout());
		btnPanel.add(box, BorderLayout.CENTER);
		okayBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		return btnPanel;	
	}
	
	protected JButton createBtn(String btnName)
	{
		JButton btn = new JButton(btnName);
		btn.setSize(80, 30);	
		btn.setPreferredSize(new Dimension(80,30));
		return btn;
	}
	
	//方形按钮
	protected JButton createBtn1()
	{
		JButton btn = new JButton();
		btn.setText("...");
		btn.setSize(30, 30);
		btn.setPreferredSize(new Dimension(30, 30));
		return btn;
	}
	

	public static void main(String[] args) {
		try {
		//	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());			
			String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
			UIManager.setLookAndFeel(lookAndFeel);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		new WsnPolicyInterface();

	}

}
