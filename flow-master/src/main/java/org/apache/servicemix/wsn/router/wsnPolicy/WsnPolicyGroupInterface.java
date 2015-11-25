/**
 * @author shoren
 * @date 2013-3-4
 */
package org.apache.servicemix.wsn.router.wsnPolicy;

import com.bupt.wangfu.ldap.Ldap;
import com.bupt.wangfu.ldap.TopicEntry;
import org.apache.servicemix.wsn.router.admin.AdminBase;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.*;

import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 *
 */
public class WsnPolicyGroupInterface extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static Toolkit kit;
	private static Dimension screenSize;
	private static String RESULT = "result";
	private static String CHOOSE = "choose";

	static {
		kit = Toolkit.getDefaultToolkit();
		screenSize = kit.getScreenSize();
	}

	public TopicEntry currentTopic = null;
	private JButton okayBtn = createBtn("????");
	private JButton cancelBtn = createBtn("????");
	private JButton addBtn = createBtn("????");
	private JButton deleteBtn = createBtn("????");
	private JButton newGroupBtn = createBtn("????????????");
	private JButton deleGroupBtn = createBtn("????????????");
	private TopicEntry complexGroupEntry = new TopicEntry();
	private HashMap<String, JTree> name_Tree = new HashMap<String, JTree>();
	private boolean isAdd;
	private JTree chooseTree;
	private JTree resultTree;
	private WsnPolicyInterface parentFrame;

	public WsnPolicyGroupInterface(TopicEntry targetTopic, boolean isAdd) {
		super("????????????");
		this.isAdd = isAdd;
		if (targetTopic == null || targetTopic.getTopicName() == null || targetTopic.getTopicName().length() == 1) {
			return;
		}
		//??????Panel
		add(createMsgPanel(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);

		currentTopic = targetTopic;
		iniTrees(targetTopic.getTopicName());

		complexGroupEntry.setTopicName("complexGroup");
		complexGroupEntry.setTopicPath("ou=complexGroup,dc=wsn,dc=com");

		//????????????????????????????????????????????????????????????????????????????
		if (!ShorenUtils.isWholeMsg()) {
			newGroupBtn.setEnabled(false);
			deleGroupBtn.setEnabled(false);
		}

		//frame conf
		setBounds(screenSize.width / 4, screenSize.height / 8,
				screenSize.width / 2, 5 * screenSize.height / 8);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(false);
		setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args) throws NamingException {

		Ldap lu = new Ldap();
		try {
			lu.connectLdap("10.109.253.6", "cn=Manager,dc=wsn,dc=com", "123456");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		//ShorenUtils.setWholeMsg(false);
		TopicEntry newtopic = new TopicEntry();
		newtopic.setTopicName("complexGroup");
		newtopic.setTopicPath("ou=complexGroup,dc=wsn,dc=com");
		WsnPolicyMsg newWsnPolicyMsg = new WsnPolicyMsg();

		List<TargetHost> targethostlist = new ArrayList<TargetHost>();
		TargetHost targethost1 = new TargetHost();
		targethost1.setHostIp("127.0.0.1");
		targethost1.setName("host1");
		targethostlist.add(targethost1);

		List<TargetRep> targetreplist = new ArrayList<TargetRep>();
		TargetRep targetrep1 = new TargetRep();
		targetrep1.setName("rep1");
		targetrep1.setRepIp("10.108.166.236");
		targetrep1.setTargetClients(targethostlist);
		targetreplist.add(targetrep1);

		List<TargetGroup> targetgrouplist = new ArrayList<TargetGroup>();
		TargetGroup targetgroup1 = new TargetGroup();
		targetgroup1.setName("G1");
		targetgroup1.setTargetList(targetreplist);
		targetgrouplist.add(targetgroup1);
		newWsnPolicyMsg.setTargetGroups(targetgrouplist);

		newtopic.setWsnpolicymsg(newWsnPolicyMsg);


		lu.create(newtopic);

//		TopicEntry temp = lu.getByDN("ou=complexGroup,dc=wsn,dc=com");
//		TargetGroup tempGroup = temp.getWsnpolicymsg().getTargetGroups().get(0);
//		System.out.println(tempGroup.getName());
		//new WsnPolicyGroupInterface(newtopic);

	}

	public WsnPolicyInterface getParentFrame() {
		return parentFrame;
	}

	public void setParentFrame(WsnPolicyInterface parentFrame) {
		this.parentFrame = parentFrame;
	}

	protected void iniTrees(String targetTopic) {
		//test
		chooseTree = name_Tree.get(CHOOSE);
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) chooseTree.getModel().getRoot();
		WsnPolicyMsg wpm = new WsnPolicyMsg();
//		if(ShorenUtils.isWholeMsg())
//		{
//			//??complexGroupsMsgs.xml????????
//			wpm = ShorenUtils.decodeAllComplexGroups();   //.decodePolicyMsg();  ;
//		}else
//		{
//			//??policyMsg.xml????????????
//			wpm = ShorenUtils.decodePolicyMsg(currentTopic);
//		}

		//??????????????????????????????????????
		if (isAdd) {
			Set groupSet = AdminBase.groups.keySet();
			Iterator iterator = groupSet.iterator();
			while (iterator.hasNext()) {
				String groupName = (String) iterator.next();
				TargetGroup t = new TargetGroup(groupName);
				wpm.getTargetGroups().add(t);
			}
		} else {
			wpm = ShorenUtils.decodePolicyMsg(currentTopic);
		}

		ShorenUtils.showPolicyMsg(root, wpm);
		root.setUserObject(wpm);
		TreePath rp = new TreePath(root);
		chooseTree.expandPath(rp);


		resultTree = name_Tree.get(RESULT);
		DefaultMutableTreeNode root1 = (DefaultMutableTreeNode) resultTree.getModel().getRoot();
		TreePath rp1 = new TreePath(root1);
		resultTree.expandPath(rp1);
	}

	protected JPanel createMsgPanel() {
		JPanel msgPanel = new JPanel();
		JSplitPane inner = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				createGroupTree(CHOOSE, "????????"), createMidPanel());
		JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				inner, createGroupTree(RESULT, "????????"));
		msgPanel.add(outer, BorderLayout.CENTER);
		Border border = BorderFactory.createTitledBorder("????????");
		msgPanel.setBorder(border);
		return msgPanel;
	}

	protected JPanel createMidPanel() {
		JPanel panel = new JPanel();
		Box box = Box.createVerticalBox();
		box.add(Box.createHorizontalStrut(30));
		box.add(addBtn);
		box.add(Box.createHorizontalStrut(10));
		box.add(deleteBtn);
		box.add(Box.createHorizontalStrut(30));
		panel.setLayout(new BorderLayout());
		panel.add(box, BorderLayout.CENTER);
		addBtn.addActionListener(this);
		deleteBtn.addActionListener(this);
		return panel;
	}

	protected JScrollPane createGroupTree(String name, String title) {
		//??????????root??
		JTree groupTree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
		ShorenNodeRenderer ren = new ShorenNodeRenderer();
		groupTree.setCellRenderer(ren);
		groupTree.setShowsRootHandles(true);  //????????????????
		groupTree.setRootVisible(false);         //????????????
		name_Tree.put(name, groupTree);

		JScrollPane listScrollPane = new JScrollPane(groupTree);
		listScrollPane.setPreferredSize(new Dimension(200, 380));
		listScrollPane.setMinimumSize(new Dimension(150, 300));
		listScrollPane.setBorder(BorderFactory.createTitledBorder(title));
		return listScrollPane;
	}

	protected JPanel createButtonPanel() {
		JPanel btnPanel = new JPanel();
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(100));
		box.add(newGroupBtn);
		box.add(Box.createHorizontalStrut(20));
		box.add(deleGroupBtn);
		box.add(Box.createHorizontalStrut(20));
		box.add(okayBtn);
		box.add(Box.createHorizontalStrut(20));
		box.add(cancelBtn);
		btnPanel.setLayout(new BorderLayout());
		btnPanel.add(box, BorderLayout.CENTER);
		okayBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		newGroupBtn.addActionListener(this);
		deleGroupBtn.addActionListener(this);
		return btnPanel;
	}

	protected JButton createBtn(String btnName) {
		JButton btn = new JButton(btnName);
		btn.setSize(80, 30);
		btn.setPreferredSize(new Dimension(80, 30));
		return btn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okayBtn) {
			//return the result
			if (resultTree != null && getParentFrame() != null) {
				JTree gtree = getParentFrame().getGroupTree();
				//????????????????????????????????.
				DefaultMutableTreeNode root1 = (DefaultMutableTreeNode) resultTree.getModel().getRoot();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) gtree.getModel().getRoot();
				if (root1.getChildCount() == 0) {
					this.dispose();
					return;
				}
				root.removeAllChildren();
				int count = root1.getChildCount();
				for (int i = 0; i < count; i++) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) root1.getChildAt(i);
					TargetMsg msg = (TargetMsg) child.getUserObject();

					//??????????????????????????????????????????????????????
					if (!ShorenUtils.isInNode(root, msg))
						root.add(ShorenUtils.showTargetMsg(msg));
				}
				TreePath rp = new TreePath(root);
				gtree.expandPath(rp);
				gtree.updateUI();

				getParentFrame().updateBtns();
			}

			this.dispose();

		} else if (e.getSource() == cancelBtn) {
			this.dispose();
		} else if (e.getSource() == addBtn) {
			//????????????????????????resultTree????
			TreePath[] tp = chooseTree.getSelectionPaths();
			if (tp == null) return;

			int len = tp.length;
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) resultTree.getModel().getRoot();
			for (int i = 0; i < len; i++) {
				//????????????????????????resultTree
				TargetMsg msg = (TargetMsg) ((ShorenTreeNode) tp[i].getLastPathComponent()).getUserObject();

				//??????????????????????????????????????????????????????
				if (!ShorenUtils.isInNode(root, msg))
					root.add(ShorenUtils.showTargetMsg(msg));
			}

			TreePath rp = new TreePath(root);
			resultTree.expandPath(rp);
			resultTree.updateUI();

		} else if (e.getSource() == deleteBtn) {
			TreePath[] tp = resultTree.getSelectionPaths();
			if (tp == null) return;

			int len = tp.length;
			for (int i = 0; i < len; i++) {
				DefaultMutableTreeNode node = (ShorenTreeNode) tp[i].getLastPathComponent();
				node.removeFromParent();
			}
			resultTree.updateUI();
		} else if (e.getSource() == newGroupBtn) {
			TreePath[] tp = chooseTree.getSelectionPaths();
			if (tp != null) {
				int len = tp.length;
				String name = JOptionPane.showInputDialog("??????????????????????");
				if ((name == null) || name.equals(""))
					return;
				//????????????????
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) chooseTree.getModel().getRoot();
				if (ShorenUtils.hasNameExisted(root, name)) {
					JOptionPane.showMessageDialog(this, "????????????????????^_^");
					return;
				}

				List<ComplexGroup> complexGroups = new ArrayList<ComplexGroup>();
				List<TargetGroup> targetGroups = new ArrayList<TargetGroup>();
				for (int i = 0; i < len; i++) {
					//????????????????????????
					TargetMsg msg = (TargetMsg) ((ShorenTreeNode) tp[i].getLastPathComponent()).getUserObject();
					if (msg instanceof ComplexGroup)
						complexGroups.add((ComplexGroup) msg);
					else if (msg instanceof TargetGroup)
						targetGroups.add((TargetGroup) msg);
				}
				ComplexGroup cgroup = new ComplexGroup(name, complexGroups, targetGroups);
				DefaultMutableTreeNode croot = (DefaultMutableTreeNode) chooseTree.getModel().getRoot();
				WsnPolicyMsg policy = (WsnPolicyMsg) croot.getUserObject();
				policy.getComplexGroups().add(cgroup);
				complexGroupEntry.setWsnpolicymsg(policy);
				//????????????????????????????????????????????????
				ShorenUtils.encodeAllComplexGroups(complexGroupEntry);

				//????chooseTree????
				ShorenUtils.showPolicyMsg(croot, policy);
				chooseTree.updateUI();


			} else {
				JOptionPane.showMessageDialog(null, "????????????????^_^");
			}

		} else if (e.getSource() == deleGroupBtn) {
			TreePath[] tp = chooseTree.getSelectionPaths();
			if (tp != null) {
				int len = tp.length;
				DefaultMutableTreeNode croot = (DefaultMutableTreeNode) chooseTree.getModel().getRoot();
				WsnPolicyMsg policy = (WsnPolicyMsg) croot.getUserObject();

				for (int i = 0; i < len; i++) {
					//delete????????????????????????????????
					ShorenTreeNode node = (ShorenTreeNode) tp[i].getLastPathComponent();
					TargetMsg group = (TargetMsg) node.getUserObject();
					if (group instanceof ComplexGroup) {
						Object parent = ((DefaultMutableTreeNode) node.getParent()).getUserObject();
						if (parent instanceof WsnPolicyMsg)
							((WsnPolicyMsg) parent).getComplexGroups().remove(group);
						if (parent instanceof ComplexGroup)
							((ComplexGroup) parent).getComplexGroups().remove(group);
					}
				}

				//delete????????????????????????????????????????????
				complexGroupEntry.setWsnpolicymsg(policy);
				ShorenUtils.encodeAllComplexGroups(complexGroupEntry);

				//????chooseTree????
				ShorenUtils.showPolicyMsg(croot, policy);
				chooseTree.updateUI();

			} else {
				JOptionPane.showMessageDialog(null, "????????????????^_^");
			}
		}

	}
}
