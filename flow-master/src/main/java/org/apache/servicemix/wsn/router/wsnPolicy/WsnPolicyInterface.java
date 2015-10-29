/**
 * @author shoren
 * @date 2013-2-25
 */
package org.apache.servicemix.wsn.router.wsnPolicy;

import edu.bupt.wangfu.ldap.Ldap;
import edu.bupt.wangfu.ldap.TopicEntry;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.*;

import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 *
 */
public class WsnPolicyInterface extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static Toolkit kit;
	private static Dimension screenSize;

	private static String fileName = "wsnPolicy";
	//??????????????????????????????????rtMsg????????????true??
	//??????????????????????????????????????????????false????????????????????

	static {
		kit = Toolkit.getDefaultToolkit();
		screenSize = kit.getScreenSize();
	}

	public TopicEntry currentTopic = null;
	public HashMap<String, JList> name_List = new HashMap<String, JList>();
	public HashMap<String, List> name_Array = new HashMap<String, List>();
	private JButton okayBtn = createBtn("????");
	private JButton cancelBtn = createBtn("????");
	private JButton bt1 = createBtn1();   //????????
	private JButton bt2 = createBtn1();   //????????
	private JButton bt3 = createBtn1();   //????????
	private List<String> msgStyle;  //????????,????????????
	private List<String> topics;   //??????????????????
	private JComboBox msgStyleList = new JComboBox();
	private JComboBox topicList = new JComboBox();
	private Ldap lu = null;
	private JTree groupTree;
	private ButtonGroup bg;
	private JRadioButton[] radioButtons;

	public WsnPolicyInterface(JPanel panel, Ldap lu) {
		super("WSN????????");
		Border title1 = BorderFactory.createTitledBorder("????????");
		Border title2 = BorderFactory.createTitledBorder("????????");
		JPanel msgPanel = createMsgPanel();
		msgPanel.setBorder(title1);

		JPanel operaPanel = createRadioPanel();
		operaPanel.setBorder(title2);

	/*	JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				msgPanel,createButtonPanel());
		JSplitPane outer = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				operaPanel,splitPane);
		add(outer, BorderLayout.CENTER);*/
		panel.setLayout(new BorderLayout());
		panel.add(operaPanel, BorderLayout.NORTH);
		panel.add(msgPanel, BorderLayout.CENTER);
		panel.add(createButtonPanel(), BorderLayout.SOUTH);

		this.lu = lu;
		ShorenUtils.ldap = lu;
		//frame conf
//		setBounds(screenSize.width/4 , screenSize.height/8,
//				screenSize.width/3 ,3*screenSize.height/4);
//		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		setAlwaysOnTop(true);
//		setResizable(false);
//		setVisible(true);
	}

	public static void main(String[] args) throws NamingException {
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
		Ldap ldap = new Ldap();
		ldap.connectLdap("10.109.253.6", "cn=Manager,dc=wsn,dc=com", "123456");

		TopicEntry libentry = new TopicEntry();
		libentry.setTopicName("all_test");
		//libentry.setTopicCode("1");
		libentry.setTopicPath("ou=all_test,dc=wsn,dc=com");
		List<TopicEntry> list = ldap.getWithAllChildrens(libentry);
		Iterator itr = list.iterator();
		while (itr.hasNext()) {
			TopicEntry tem = (TopicEntry) itr.next();
			System.out.println(tem);
			System.out.println(tem.getTopicPath());
			System.out.println(tem.getWsnpolicymsg());
			if (tem.getWsnpolicymsg() != null) {
				System.out.println("????????TargetTopic??" + tem.getWsnpolicymsg().getTargetTopic());
				System.out.println("????????AllGroups??" + tem.getWsnpolicymsg().getAllGroups());
				System.out.println("????????ComplexGroups??" + tem.getWsnpolicymsg().getComplexGroups());
				System.out.println("????????TargetGroups??" + tem.getWsnpolicymsg().getTargetGroups());
			}
		}

//		WsnPolicyInterface wpi = new WsnPolicyInterface(new JPanel(),ldap);
//		wpi.getCreatedPolicyMsg();

	}

	public JTree getGroupTree() {
		return groupTree;
	}

	public void setGroupTree(JTree groupTree) {
		this.groupTree = groupTree;
	}

	protected void initialMsg() {
		//??????????????????????????????
		msgStyle = new ArrayList<String>();
		//??????????????????msgStyle
		updateMsgStyle();

		topics = new ArrayList<String>();
		updateTopics((String) msgStyleList.getSelectedItem());
	}

	protected JPanel createMsgPanel() {
		JPanel msgPanel = new JPanel();
		initialMsg();

		JPanel msgStylePanel = new JPanel();
		msgStylePanel.add(new JLabel("????????"), BorderLayout.WEST);
		msgStyleList.setPreferredSize(new Dimension(150, 30));
		msgStylePanel.add(msgStyleList, BorderLayout.EAST);

		JPanel topicPanel = new JPanel();
		topicPanel.add(new JLabel("????????"), BorderLayout.WEST);
		topicList.setPreferredSize(new Dimension(150, 30));
		topicPanel.add(topicList, BorderLayout.EAST);

		JPanel limitedPanel1 = new JPanel();
		limitedPanel1.add(new JLabel("????????"));
		limitedPanel1.add(createGroupTree());
		limitedPanel1.add(bt1);

		JPanel limitedPanel2 = new JPanel();
		limitedPanel2.add(new JLabel("????????"), BorderLayout.WEST);
		limitedPanel2.add(createList("regs"), BorderLayout.CENTER);   //key = "regs"
		limitedPanel2.add(bt2, BorderLayout.EAST);

		JPanel limitedPanel3 = new JPanel();
		limitedPanel3.add(new JLabel("????????"), BorderLayout.WEST);
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
//		msgPanel.add(msgStylePanel);
//		msgPanel.add(topicPanel);
//		msgPanel.add(limitedPanel1);
//		msgPanel.add(limitedPanel2);
//		msgPanel.add(limitedPanel3);

		return msgPanel;
	}

	protected JScrollPane createGroupTree() {
		//??????????root??
		groupTree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
		ShorenNodeRenderer ren = new ShorenNodeRenderer();
		groupTree.setCellRenderer(ren);
		groupTree.setShowsRootHandles(true);  //????????????????
		groupTree.setRootVisible(false);         //????????????

		JScrollPane listScrollPane = new JScrollPane(groupTree);
		listScrollPane.setPreferredSize(new Dimension(200, 100));
		listScrollPane.setMinimumSize(new Dimension(150, 100));

		return listScrollPane;
	}

	protected JScrollPane createList(String name) {
		DefaultListModel listModel = new DefaultListModel();

		//Create the list and put it in a scroll pane.
		JList nameList = new JList(listModel);
/*		for(int i = 0; i<10;i++)
		{
			listModel.addElement("aaaa" + i);
			listModel.addElement("aaaa" + i);
		}*/

		nameList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION); //????
		name_List.put(name, nameList);
		name_Array.put(name, new ArrayList<TargetMsg>());
		JScrollPane listScrollPane = new JScrollPane(nameList);
		listScrollPane.setPreferredSize(new Dimension(200, 100));
		listScrollPane.setMinimumSize(new Dimension(150, 100));

		return listScrollPane;
	}

	protected JPanel createRadioPanel() {
		JPanel radioPanel = new JPanel();
		radioButtons = new JRadioButton[3];
		bg = new ButtonGroup();
		radioButtons[0] = new JRadioButton("????", true);
		bg.add(radioButtons[0]);
		radioPanel.add(radioButtons[0]);
		radioButtons[1] = new JRadioButton("????", false);
		bg.add(radioButtons[1]);
		radioPanel.add(radioButtons[1]);
		radioButtons[2] = new JRadioButton("????", false);
		bg.add(radioButtons[2]);
		radioPanel.add(radioButtons[2]);

		radioButtons[0].addActionListener(this);
		radioButtons[1].addActionListener(this);
		radioButtons[2].addActionListener(this);

		return radioPanel;
	}

	public WsnPolicyMsg getCreatedPolicyMsg() {
//		String mStyle = msgStyleList.getSelectedItem().toString();
		WsnPolicyMsg policy = new WsnPolicyMsg(currentTopic.getTopicName());
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) groupTree.getModel().getRoot();
		if (!root.isLeaf()) {
			int count = root.getChildCount();
			for (int i = 0; i < count; i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
				TargetMsg msg = (TargetMsg) child.getUserObject();
				if (msg instanceof ComplexGroup)
					policy.getComplexGroups().add((ComplexGroup) msg);
				else if (msg instanceof TargetGroup)
					policy.getTargetGroups().add((TargetGroup) msg);
			}
		}
		if (bt2.isEnabled() && name_List.get("regs").getModel().getSize() > 0) {
			ListModel listModel = name_List.get("regs").getModel();
			List<TargetRep> trs = policy.getTargetGroups().get(0).getTargetList();
			trs.clear();
			for (int i = 0; i < listModel.getSize(); i++) {
				trs.add(new TargetRep(listModel.getElementAt(i).toString()));//??????????
			}
		}
		if (bt2.isEnabled() && name_List.get("hosts").getModel().getSize() > 0) {
			ListModel listModel = name_List.get("hosts").getModel();
			TargetRep rep = policy.getTargetGroups().get(0).getTargetList().get(0);
			rep.getTargetClients().clear();
			for (int i = 0; i < listModel.getSize(); i++) {
				//????host
				rep.getTargetClients().add(new TargetHost(listModel.getElementAt(i).toString()));//??????????
			}
		}
		if (policy.getComplexGroups().isEmpty() && !policy.getTargetGroups().isEmpty()
				&& (policy.getTargetGroups().size() == 1)) {
			TargetGroup tg = policy.getTargetGroups().get(0);
			//??????????????????????????????????
			if (tg.getTargetList().isEmpty()) {
				tg.setAllMsg(true);
			}

		}

		//??????????????????????????????????????????????
		Set<TargetGroup> allGroups = policy.getAllGroups();
		if (!allGroups.isEmpty() && (allGroups.size() > 1)) {
			Iterator it = allGroups.iterator();
			while (it.hasNext()) {
				((TargetGroup) it.next()).setAllMsg(true);
			}
		}

		return policy;
	}

	//????????????????????????????
	public void addPolicyMsg() {
		if (!ShorenUtils.isWholeMsg()) {
			ShorenUtils.setWholeMsg(true); //??????????????????????????
		}
		WsnPolicyMsg newPolicy = getCreatedPolicyMsg();
		WsnPolicyMsg policy = ShorenUtils.decodePolicyMsg(currentTopic);
		if (policy != null)
			policy.mergeMsg(newPolicy);//add
		else
			policy = newPolicy;
		currentTopic.setWsnpolicymsg(policy);

		ShorenUtils.encodePolicyMsg(currentTopic);  //write
	}

	public void updateMsg() {
		if (ShorenUtils.isWholeMsg()) {
			ShorenUtils.setWholeMsg(false);  //??????????????????????????????????
		}
		//delete the same topic nodes before add new ones
		WsnPolicyMsg policy = getCreatedPolicyMsg();
		currentTopic.setWsnpolicymsg(policy);
		ShorenUtils.encodePolicyMsg(currentTopic);  //write
	}

	public void deleteMsg() {
		if (ShorenUtils.isWholeMsg()) {
			ShorenUtils.setWholeMsg(false);  //??????????????????????????????????
		}
		WsnPolicyMsg newPolicy = getCreatedPolicyMsg();
		WsnPolicyMsg policy = ShorenUtils.decodePolicyMsg(currentTopic);
		policy.deleteMsg(newPolicy);            //delete
		currentTopic.setWsnpolicymsg(policy);
		ShorenUtils.encodePolicyMsg(currentTopic);  //write
	}

	//????????????????????
	public void setMsgNull() {
		msgStyleList.setSelectedIndex(0);
		topicList.setSelectedIndex(0);

		((DefaultMutableTreeNode) groupTree.getModel().getRoot()).removeAllChildren();
		groupTree.updateUI();

		Iterator it = name_List.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			name_Array.get(key).clear();
			DefaultListModel listModel = (DefaultListModel) name_List.get(key).getModel();
			listModel.clear();
		}
	}

	protected void updateMsgStyle() {
		msgStyle.clear();
		msgStyleList.removeAllItems();
		//	msgStyle.add("        ");  //????????8??????
		msgStyle.add("MsgNotis");
/*
		if(ShorenUtils.isWholeMsg())
		{
			//??????RtMgr??????????,????msgStyle??
			msgStyle.add("aaaa");
			msgStyle.add("cccca");
			msgStyle.add("bbbb");
		}
		else{
			//??????????????????????????msgStyle??

		}*/
		for (int i = 0; i < msgStyle.size(); i++) {
			msgStyleList.addItem(msgStyle.get(i));
		}
	}

	//??????????????????msgStyle??????????topic,????????.
	@SuppressWarnings("unchecked")
	public void updateTopics(String msgStyle) {
		topics.clear();
		topicList.removeAllItems();

//		topics.add("        "); //????????8??????
		if (currentTopic != null) {
			topicList.addItem(currentTopic.getTopicName());
			topics.add(currentTopic.getTopicName());
			topicList.setSelectedIndex(0);
		} else
			topicList.addItem("        ");

//		if(ShorenUtils.isWholeMsg())
//		{
//			//??????RtMgr??????????,????topics??
//
//		}
//		else{
//			//??????????????????????????topics??
//			topics = ShorenUtils.getPolicyTopics();
//			for(int i=0; i<topics.size(); i++)
//			{
//				topicList.addItem(topics.get(i));
//			}
//		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okayBtn) {
			//	System.out.println((String)msgStyleList.getSelectedItem());
			//????
			if (radioButtons[0].isSelected()) {
				addPolicyMsg();
			} else if (radioButtons[1].isSelected()) {
				//????
				updateMsg();
			} else if (radioButtons[2].isSelected()) {
				//????
				deleteMsg();
			}
			this.dispose();
		} else if (e.getSource() == cancelBtn) {
			this.dispose();
		} else if (e.getSource() == radioButtons[0] || e.getSource() == radioButtons[1]
				|| e.getSource() == radioButtons[2])  //??????????????????????
		{
			setMsgNull();
			//????????????????
			if (e.getSource() == radioButtons[0])
				ShorenUtils.setWholeMsg(true);
			else
				ShorenUtils.setWholeMsg(false);

			updateMsgStyle();
			updateTopics(null);
		} else if (e.getSource() == bt1) {
			//????????????topic
			WsnPolicyGroupInterface inter = null;
			if (radioButtons[0].isSelected() || radioButtons[1].isSelected()) {
				inter = new WsnPolicyGroupInterface(currentTopic, true);
			} else {
				inter = new WsnPolicyGroupInterface(currentTopic, false);
			}
			inter.setParentFrame(this);

		} else if (e.getSource() == bt2) {
			//????bt1????????????,??root??????????????????TargetGroup????????
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) groupTree.getModel().getRoot();
			if (root.isLeaf()) {
				JOptionPane.showMessageDialog(this, "????????????????????????^_^");
				return;
			}
			//??????????????
			Object group = ((DefaultMutableTreeNode) root.getChildAt(0)).getUserObject();
			if (group instanceof TargetGroup) {
				getRegMsg((TargetGroup) group);
			}
			updateBtns();
		} else if (e.getSource() == bt3) {
			if (name_List.get("regs").getModel().getSize() < 1) {
				JOptionPane.showMessageDialog(this, "????????????????????????^_^");
				return;
			}
			//??????????????
			Object reg = name_Array.get("regs").get(0);
			if (reg instanceof TargetRep) {
				getHostMsg((TargetRep) reg);
			}
		}

	}

	//get messages from ListFrame
	@SuppressWarnings("rawtypes")
	protected void getRegMsg(TargetGroup group) {
		if (group != null && !group.getTargetList().isEmpty()) {
			List<TargetRep> regList = group.getTargetList();
			List<Object> lists = Arrays.asList(regList.toArray());
			ListFrame lf = new ListFrame(lists, true);
			lf.setParentFrame(this);
		}
	}

	protected void getHostMsg(TargetRep reg) {
		if (reg != null && !reg.getTargetClients().isEmpty()) {
			List<TargetHost> regList = reg.getTargetClients();
			List<Object> lists = Arrays.asList(regList.toArray());
			ListFrame lf = new ListFrame(lists, false);
			lf.setParentFrame(this);
		}
	}

	protected void updateBtns() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) groupTree.getModel().getRoot();
		if (root.getChildCount() > 1 || !root.getChildAt(0).isLeaf()) {
			bt2.setEnabled(false);
			bt3.setEnabled(false);
		} else {
			bt2.setEnabled(true);
			bt3.setEnabled(true);
		}
		if (!bt2.isEnabled() || name_List.get("regs").getModel().getSize() > 1) {
			bt3.setEnabled(false);
		} else {
			bt3.setEnabled(true);
		}
	}

	protected JPanel createButtonPanel() {
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

	protected JButton createBtn(String btnName) {
		JButton btn = new JButton(btnName);
		btn.setSize(80, 30);
		btn.setPreferredSize(new Dimension(80, 30));
		return btn;
	}

	//????????
	protected JButton createBtn1() {
		JButton btn = new JButton();
		btn.setText("...");
		btn.setSize(30, 30);
		btn.setPreferredSize(new Dimension(30, 30));
		return btn;
	}

}
