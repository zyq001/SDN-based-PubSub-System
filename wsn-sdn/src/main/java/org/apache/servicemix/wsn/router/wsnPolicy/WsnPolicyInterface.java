package org.apache.servicemix.wsn.router.wsnPolicy;

/**
 * @author shoren
 * @date 2013-2-25
 */

import org.apache.servicemix.wsn.router.wsnPolicy.msgs.*;

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
	//����Ҫȫ�֣��������磩����Ϣ������rtMsg����ã���Ϊtrue��
	//����Ҫ���ش洢�Ĳ����ļ���Ϣ�����ֲ���Ϣ����Ϊfalse��Ĭ���Ǵ�ȫ�ֻ�á�

	static {
		kit = Toolkit.getDefaultToolkit();
		screenSize = kit.getScreenSize();
	}

	public HashMap<String, JList> name_List = new HashMap<String, JList>();
	public HashMap<String, List> name_Array = new HashMap<String, List>();
	private JButton okayBtn = createBtn("ȷ��");
	private JButton cancelBtn = createBtn("ȡ��");
	private JButton bt1 = createBtn1();   //����Ⱥ��
	private JButton bt2 = createBtn1();   //���޴���
	private JButton bt3 = createBtn1();   //��������
	private List<String> msgStyle;  //��Ϣ����,��һ���ǿո�
	private List<String> topics;   //���⣬��һ���ǿո�
	private JComboBox msgStyleList = new JComboBox();
	private JComboBox topicList = new JComboBox();
	private JTree groupTree;
	private ButtonGroup bg;
	private JRadioButton[] radioButtons;

	public WsnPolicyInterface() {
		super("WSN���Բ���");
		Border title1 = BorderFactory.createTitledBorder("������Ϣ");
		Border title2 = BorderFactory.createTitledBorder("����ѡ��");
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
		setBounds(screenSize.width / 4, screenSize.height / 8,
				screenSize.width / 3, 3 * screenSize.height / 4);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		setVisible(true);
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

	public JTree getGroupTree() {
		return groupTree;
	}

	public void setGroupTree(JTree groupTree) {
		this.groupTree = groupTree;
	}

	protected void initialMsg() {
		//��ѯ��ǰ�������Ϣ���ͼ�������
		msgStyle = new ArrayList<String>();
		//�����е���Ϣ�����msgStyle
		updateMsgStyle();

		topics = new ArrayList<String>();
		updateTopics((String) msgStyleList.getSelectedItem());
	}

	protected JPanel createMsgPanel() {
		JPanel msgPanel = new JPanel();
		initialMsg();

		JPanel msgStylePanel = new JPanel();
		msgStylePanel.add(new JLabel("��Ϣ����"), BorderLayout.WEST);
		msgStyleList.setPreferredSize(new Dimension(200, 30));
		msgStylePanel.add(msgStyleList, BorderLayout.EAST);

		JPanel topicPanel = new JPanel();
		topicPanel.add(new JLabel("Ŀ������"), BorderLayout.WEST);
		topicList.setPreferredSize(new Dimension(200, 30));
		topicPanel.add(topicList, BorderLayout.EAST);

		JPanel limitedPanel1 = new JPanel();
		limitedPanel1.add(new JLabel("����Ⱥ��"));
		limitedPanel1.add(createGroupTree());
		limitedPanel1.add(bt1);

		JPanel limitedPanel2 = new JPanel();
		limitedPanel2.add(new JLabel("���޴���"), BorderLayout.WEST);
		limitedPanel2.add(createList("regs"), BorderLayout.CENTER);   //key = "regs"
		limitedPanel2.add(bt2, BorderLayout.EAST);

		JPanel limitedPanel3 = new JPanel();
		limitedPanel3.add(new JLabel("��������"), BorderLayout.WEST);
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

	protected JScrollPane createGroupTree() {
		//�ǵ�Ҫ����root��
		groupTree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
		ShorenNodeRenderer ren = new ShorenNodeRenderer();
		groupTree.setCellRenderer(ren);
		groupTree.setShowsRootHandles(true);  //��ʾǰ��ķ�֧��
		groupTree.setRootVisible(false);         //����ʾ���ڵ�

		JScrollPane listScrollPane = new JScrollPane(groupTree);
		listScrollPane.setPreferredSize(new Dimension(250, 100));
		listScrollPane.setMinimumSize(new Dimension(200, 100));

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

		nameList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION); //��ѡ
		name_List.put(name, nameList);
		name_Array.put(name, new ArrayList<TargetMsg>());
		JScrollPane listScrollPane = new JScrollPane(nameList);
		listScrollPane.setPreferredSize(new Dimension(250, 100));
		listScrollPane.setMinimumSize(new Dimension(200, 100));

		return listScrollPane;
	}

	protected JPanel createRadioPanel() {
		JPanel radioPanel = new JPanel();
		radioButtons = new JRadioButton[3];
		bg = new ButtonGroup();
		radioButtons[0] = new JRadioButton("���", true);
		bg.add(radioButtons[0]);
		radioPanel.add(radioButtons[0]);
		radioButtons[1] = new JRadioButton("�޸�", false);
		bg.add(radioButtons[1]);
		radioPanel.add(radioButtons[1]);
		radioButtons[2] = new JRadioButton("ɾ��", false);
		bg.add(radioButtons[2]);
		radioPanel.add(radioButtons[2]);

		radioButtons[0].addActionListener(this);
		radioButtons[1].addActionListener(this);
		radioButtons[2].addActionListener(this);

		return radioPanel;
	}

	protected WsnPolicyMsg getCreatedPolicyMsg() {
//		String mStyle = msgStyleList.getSelectedItem().toString();
		String topic = topicList.getSelectedItem().toString();
		WsnPolicyMsg policy = new WsnPolicyMsg(topic);
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
				//����host
				rep.getTargetClients().add(new TargetHost(listModel.getElementAt(i).toString()));//??????????
			}
		}
		if (policy.getComplexGroups().isEmpty() && !policy.getTargetGroups().isEmpty()
				&& (policy.getTargetGroups().size() == 1)) {
			TargetGroup tg = policy.getTargetGroups().get(0);
			//���û��ѡ�����˵��������Ⱥ����
			if (tg.getTargetList().isEmpty()) {
				tg.setAllMsg(true);
			}

		}

		//���ѡ������Ⱥ����Ĭ�����м�Ⱥ�ڲ�����������
		Set<TargetGroup> allGroups = policy.getAllGroups();
		if (!allGroups.isEmpty() && (allGroups.size() > 1)) {
			Iterator it = allGroups.iterator();
			while (it.hasNext()) {
				((TargetGroup) it.next()).setAllMsg(true);
			}
		}

		return policy;
	}

	//��Ӳ�����Ϣ�����������ļ���
	public void addPolicyMsg() {
		if (!ShorenUtils.isWholeMsg()) {
			ShorenUtils.setWholeMsg(true); //�½�����ʱ����ȫ�ֻ����Ϣ
		}
		String topic = topicList.getSelectedItem().toString();
		WsnPolicyMsg newPolicy = getCreatedPolicyMsg();
		WsnPolicyMsg policy = ShorenUtils.decodePolicyMsg(topic);
		policy.mergeMsg(newPolicy);            //add

		ShorenUtils.encodePolicyMsg(policy);  //write
	}

	public void updateMsg() {
		if (ShorenUtils.isWholeMsg()) {
			ShorenUtils.setWholeMsg(false);  //�޸Ĳ�����Ϣʱ���Ӳ����ļ������Ϣ
		}
		//delete the same topic nodes before add new ones
		WsnPolicyMsg policy = getCreatedPolicyMsg();
		ShorenUtils.encodePolicyMsg(policy);  //write
	}

	public void deleteMsg() {
		if (ShorenUtils.isWholeMsg()) {
			ShorenUtils.setWholeMsg(false);  //�޸Ĳ�����Ϣʱ���Ӳ����ļ������Ϣ
		}
		String topic = topicList.getSelectedItem().toString();
		WsnPolicyMsg newPolicy = getCreatedPolicyMsg();
		WsnPolicyMsg policy = ShorenUtils.decodePolicyMsg(topic);
		policy.deleteMsg(newPolicy);            //delete

		ShorenUtils.encodePolicyMsg(policy);  //write
	}

	//�������Ϣ����Ϊ��
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
		//	msgStyle.add("        ");  //��һ����8���ո�
		msgStyle.add("MsgNotis");
/*
		if(ShorenUtils.isWholeMsg())
		{
			//��ȫ��RtMgr���õ���Ϣ,����msgStyle��
			msgStyle.add("aaaa");
			msgStyle.add("cccca");
			msgStyle.add("bbbb");
		}
		else{
			//�Ӳ����ļ����õ���Ϣ������msgStyle��

		}*/
		for (int i = 0; i < msgStyle.size(); i++) {
			msgStyleList.addItem(msgStyle.get(i));
		}
	}

	//�Ժ�Ҫ��չ�ģ�����msgStyle��ȡ��Ӧ��topic,��������.
	@SuppressWarnings("unchecked")
	protected void updateTopics(String msgStyle) {
		topics.clear();
		topicList.removeAllItems();

//		topics.add("        "); //��һ����8���ո�
		if (ShorenUtils.isWholeMsg()) {
			//��ȫ��RtMgr���õ���Ϣ,����topics��

		} else {
			//�Ӳ����ļ����õ���Ϣ������topics��
			topics = ShorenUtils.getPolicyTopics();
		}
		topicList.addItem("        ");
		for (int i = 0; i < topics.size(); i++) {
			topicList.addItem(topics.get(i));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okayBtn) {
			//	System.out.println((String)msgStyleList.getSelectedItem());
			//���
			if (radioButtons[0].isSelected()) {
				addPolicyMsg();
			} else if (radioButtons[1].isSelected()) {
				//�޸�
				updateMsg();
			} else if (radioButtons[2].isSelected()) {
				//ɾ��
				deleteMsg();
			}
			this.dispose();
		} else if (e.getSource() == cancelBtn) {
			this.dispose();
		} else if (e.getSource() == radioButtons[0] || e.getSource() == radioButtons[1]
				|| e.getSource() == radioButtons[2])  //����ģʽ������ѡ���ÿ�
		{
			setMsgNull();
			//��ӵ�ʱ��ʹ��
			if (e.getSource() == radioButtons[0])
				ShorenUtils.setWholeMsg(true);
			else
				ShorenUtils.setWholeMsg(false);

			updateMsgStyle();
			updateTopics(null);
		} else if (e.getSource() == bt1) {
			//������ѡ���topic
			String topic = topicList.getSelectedItem().toString();
			WsnPolicyGroupInterface inter = new WsnPolicyGroupInterface(topic);
			inter.setParentFrame(this);
			System.out.println("DONE!");
		} else if (e.getSource() == bt2) {
			//����bt1��ѡ������Ӧ,��rootֻ��һ���ڵ��ҷ�װTargetGroup�������
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) groupTree.getModel().getRoot();
			if (root.isLeaf()) {
				JOptionPane.showMessageDialog(this, "��ѡ��һ���ҽ�һ����Ⱥ��^_^");
				return;
			}
			//�����б�ѡ��
			Object group = ((DefaultMutableTreeNode) root.getChildAt(0)).getUserObject();
			if (group instanceof TargetGroup) {
				getRegMsg((TargetGroup) group);
			}
			updateBtns();
		} else if (e.getSource() == bt3) {
			if (name_List.get("regs").getModel().getSize() < 1) {
				JOptionPane.showMessageDialog(this, "��ѡ��һ���ҽ�һ������^_^");
				return;
			}
			//�����б�ѡ��
			Object reg = name_Array.get("regs").get(0);
			if (reg instanceof TargetRep) {
				getHostMsg((TargetRep) reg);
			}
			System.out.println("DONE!");
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

	//���ΰ�ť
	protected JButton createBtn1() {
		JButton btn = new JButton();
		btn.setText("...");
		btn.setSize(30, 30);
		btn.setPreferredSize(new Dimension(30, 30));
		return btn;
	}

}
