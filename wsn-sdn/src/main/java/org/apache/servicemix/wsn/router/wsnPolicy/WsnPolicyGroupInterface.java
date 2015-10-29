package org.apache.servicemix.wsn.router.wsnPolicy;

/**
 * @author shoren
 * @date 2013-3-4
 */

import org.apache.servicemix.wsn.router.wsnPolicy.msgs.ComplexGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetMsg;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
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

	private JButton okayBtn = createBtn("ȷ��");
	private JButton cancelBtn = createBtn("ȡ��");
	private JButton addBtn = createBtn("���");
	private JButton deleteBtn = createBtn("ɾ��");
	private JButton newGroupBtn = createBtn("�½����ϼ�Ⱥ");
	private JButton deleGroupBtn = createBtn("ɾ�����ϼ�Ⱥ");
	private HashMap<String, JTree> name_Tree = new HashMap<String, JTree>();
	private JTree chooseTree;
	private JTree resultTree;
	private WsnPolicyInterface parentFrame;

	public WsnPolicyGroupInterface(String targetTopic) {
		super("���Լ�Ⱥ����");
		//��Ӹ�Panel
		add(createMsgPanel(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);

		iniTrees(targetTopic);

		//������½�������Ϣ���õ�������������ļ�Ⱥ��Ϣ�����ǲſ����½���ɾ������Ⱥ��
		if (!ShorenUtils.isWholeMsg()) {
			newGroupBtn.setEnabled(false);
			deleGroupBtn.setEnabled(false);
		}

		//frame conf
		setBounds(screenSize.width / 4, screenSize.height / 8,
				screenSize.width / 2, 5 * screenSize.height / 8);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args) {

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
		ShorenUtils.setWholeMsg(false);
		new WsnPolicyGroupInterface("test");

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
		WsnPolicyMsg wpm = null;
		if (ShorenUtils.isWholeMsg()) {
			//��complexGroupsMsgs.xml��ȡ��Ϣ
			wpm = ShorenUtils.decodeAllComplexGroups();   //.decodePolicyMsg();  ;

		} else {
			//��policyMsg.xml��ȡ��Ӧ��Ϣ
			wpm = ShorenUtils.decodePolicyMsg(targetTopic);
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
				createGroupTree(CHOOSE, "ѡ��Χ"), createMidPanel());
		JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				inner, createGroupTree(RESULT, "��ѡ���"));
		msgPanel.add(outer, BorderLayout.CENTER);
		Border border = BorderFactory.createTitledBorder("��Ⱥ��Ϣ");
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
		//�ǵ�Ҫ����root��
		JTree groupTree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
		ShorenNodeRenderer ren = new ShorenNodeRenderer();
		groupTree.setCellRenderer(ren);
		groupTree.setShowsRootHandles(true);  //��ʾǰ��ķ�֧��
		groupTree.setRootVisible(false);         //����ʾ���ڵ�
		name_Tree.put(name, groupTree);

		JScrollPane listScrollPane = new JScrollPane(groupTree);
		listScrollPane.setPreferredSize(new Dimension(250, 380));
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
				//��Ҫ��㸳ֵ������ֱ�Ӹ�ֵ������.
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

					//�����Ҫ����һ�£����֮ǰ�Ѿ�����ˣ��Ͳ�Ҫ�ټӽ�����
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
			//��ȡѡȡ�Ľڵ㣬����ӵ�resultTree�С�
			TreePath[] tp = chooseTree.getSelectionPaths();
			if (tp == null) return;

			int len = tp.length;
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) resultTree.getModel().getRoot();
			for (int i = 0; i < len; i++) {
				//����ѡȡ�ļ�Ⱥ���������resultTree
				TargetMsg msg = (TargetMsg) ((ShorenTreeNode) tp[i].getLastPathComponent()).getUserObject();

				//�����Ҫ����һ�£����֮ǰ�Ѿ�����ˣ��Ͳ�Ҫ�ټӽ�����
				if (!ShorenUtils.isInNode(root, msg))
					root.add(ShorenUtils.showTargetMsg(msg));
			}

			TreePath rp = new TreePath(root);
			resultTree.expandPath(rp);
			resultTree.updateUI();

			System.out.println("test");
		} else if (e.getSource() == deleteBtn) {
			TreePath[] tp = resultTree.getSelectionPaths();
			if (tp == null) return;

			int len = tp.length;
			for (int i = 0; i < len; i++) {
				DefaultMutableTreeNode node = (ShorenTreeNode) tp[i].getLastPathComponent();
				node.removeFromParent();
			}
			resultTree.updateUI();
			System.out.println("test");
		} else if (e.getSource() == newGroupBtn) {

			TreePath[] tp = chooseTree.getSelectionPaths();
			if (tp != null) {
				int len = tp.length;
				String name = JOptionPane.showInputDialog("�����븴�ϼ�Ⱥ�����֣�");
				if ((name == null) || name.equals(""))
					return;
				//�������Ӧ��Ψһ
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) chooseTree.getModel().getRoot();
				if (ShorenUtils.hasNameExisted(root, name)) {
					JOptionPane.showMessageDialog(this, "��������Ѿ������ˣ�^_^");
					return;
				}

				List<ComplexGroup> complexGroups = new ArrayList<ComplexGroup>();
				List<TargetGroup> targetGroups = new ArrayList<TargetGroup>();
				for (int i = 0; i < len; i++) {
					//��ѡȡ�ļ�Ⱥ��ɸ��ϼ�Ⱥ
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
				//�½����ϼ�Ⱥʱ������չʾϵͳ���и��ϼ�Ⱥ�����档
				ShorenUtils.encodeAllComplexGroups(policy);

				//����chooseTree����
				ShorenUtils.showPolicyMsg(croot, policy);
				chooseTree.updateUI();

				System.out.println("test");

			} else {
				JOptionPane.showMessageDialog(null, "Ҫѡ��Ⱥ������^_^");
			}

		} else if (e.getSource() == deleGroupBtn) {
			TreePath[] tp = chooseTree.getSelectionPaths();
			if (tp != null) {
				int len = tp.length;
				DefaultMutableTreeNode croot = (DefaultMutableTreeNode) chooseTree.getModel().getRoot();
				WsnPolicyMsg policy = (WsnPolicyMsg) croot.getUserObject();

				for (int i = 0; i < len; i++) {
					//deleteѡȡ�ĸ��ϼ�Ⱥ��ֻ��ɾ�����ϼ�Ⱥ
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

				//delete���ϼ�Ⱥʱ������չʾϵͳ���и��ϼ�Ⱥ�����档
				ShorenUtils.encodeAllComplexGroups(policy);

				//����chooseTree����
				ShorenUtils.showPolicyMsg(croot, policy);
				chooseTree.updateUI();

				System.out.println("test");

			} else {
				JOptionPane.showMessageDialog(null, "Ҫѡ��Ⱥ������^_^");
			}
		}

	}
}
