/**
 * @author shoren
 * @date 2013-3-4
 */
package org.apache.servicemix.wsn.router.wsnPolicy;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.bupt.wangfu.ldap.*;

import org.apache.servicemix.wsn.router.admin.AdminBase;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.ComplexGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetHost;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetMsg;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetRep;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;
/**
 *
 */
public class WsnPolicyGroupInterface extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private static Toolkit kit;
	private static Dimension screenSize;
	private static String RESULT = "result";
	private static String CHOOSE = "choose";
	private JButton okayBtn = createBtn("确定");
	private JButton cancelBtn = createBtn("取消");
	private JButton addBtn = createBtn("添加");
	private JButton deleteBtn = createBtn("删除");
	private JButton newGroupBtn = createBtn("新建复合集群");
	private JButton deleGroupBtn = createBtn("删除复合集群");
	public TopicEntry currentTopic = null;
	private TopicEntry complexGroupEntry = new TopicEntry();
	
	private HashMap<String,JTree>name_Tree = new HashMap<String,JTree>();
	private boolean isAdd;
	private JTree chooseTree;
	private JTree resultTree;	

	private WsnPolicyInterface parentFrame;

	public WsnPolicyInterface getParentFrame() {
		return parentFrame;
	}

	public void setParentFrame(WsnPolicyInterface parentFrame) {
		this.parentFrame = parentFrame;
	}

	static
	{
		kit = Toolkit.getDefaultToolkit();  
        screenSize = kit.getScreenSize();
	}
	
	public WsnPolicyGroupInterface(TopicEntry targetTopic, boolean isAdd)
	{
		super("策略集群操作");
		this.isAdd = isAdd;
		if(targetTopic == null || targetTopic.getTopicName() == null || targetTopic.getTopicName().length() == 1) {
			return;
		}
		//添加各Panel
		add(createMsgPanel(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
		
		currentTopic = targetTopic;
		iniTrees(targetTopic.getTopicName());
		
		complexGroupEntry.setTopicName("complexGroup");
		complexGroupEntry.setTopicPath("ou=complexGroup,dc=wsn,dc=com");
		
		//如果是新建策略信息，得到的是整个网络的集群信息，这是才可以新建、删除复合群。
		if(!ShorenUtils.isWholeMsg())
		{
			newGroupBtn.setEnabled(false);
			deleGroupBtn.setEnabled(false);
		}
		
		//frame conf
		setBounds(screenSize.width/4 , screenSize.height/8,
				screenSize.width/2 ,5*screenSize.height/8);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(false);
		setResizable(false);
		setVisible(true);
	}
	
	protected void iniTrees(String targetTopic)
	{
		//test	
		chooseTree = name_Tree.get(CHOOSE);
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) chooseTree.getModel().getRoot();			
		WsnPolicyMsg wpm = new WsnPolicyMsg();
//		if(ShorenUtils.isWholeMsg())
//		{
//			//从complexGroupsMsgs.xml读取信息
//			wpm = ShorenUtils.decodeAllComplexGroups();   //.decodePolicyMsg();  ;
//		}else
//		{
//			//从policyMsg.xml读取相应信息
//			wpm = ShorenUtils.decodePolicyMsg(currentTopic);
//		}
		
		//遍历当前连入管理员的集群信息，其集群名
		if(isAdd) {
			Set groupSet = AdminBase.groups.keySet();
			Iterator iterator=groupSet.iterator();
			while (iterator.hasNext()) {
				String groupName=(String)iterator.next();
				TargetGroup t = new TargetGroup(groupName);
				wpm.getTargetGroups().add(t);
			}
		} else {
			wpm = ShorenUtils.decodePolicyMsg(currentTopic);
		}
		
		ShorenUtils.showPolicyMsg(root,wpm);
		root.setUserObject(wpm);
		TreePath rp = new TreePath(root);
		chooseTree.expandPath(rp);
		
		
		resultTree = name_Tree.get(RESULT);
		DefaultMutableTreeNode root1 = (DefaultMutableTreeNode) resultTree.getModel().getRoot();
		TreePath rp1 = new TreePath(root1);
		resultTree.expandPath(rp1);
	}
	
	protected JPanel createMsgPanel()
	{
		JPanel msgPanel = new JPanel();
		JSplitPane inner = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				createGroupTree(CHOOSE,"选择范围"),createMidPanel());
		JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				inner,createGroupTree(RESULT,"已选结果"));
		msgPanel.add(outer, BorderLayout.CENTER);
		Border border = BorderFactory.createTitledBorder("集群信息");
		msgPanel.setBorder(border);
		return msgPanel;
	}
	
	protected JPanel createMidPanel()
	{
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
	
	protected JScrollPane createGroupTree(String name, String title)
	{
		//记得要设置root哈
		JTree groupTree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));		
		ShorenNodeRenderer ren = new ShorenNodeRenderer();
		groupTree.setCellRenderer(ren);
		groupTree.setShowsRootHandles(true);  //显示前面的分支线
		groupTree.setRootVisible(false);		 //不显示根节点
		name_Tree.put(name, groupTree);		
		
		JScrollPane listScrollPane = new JScrollPane(groupTree);
        listScrollPane.setPreferredSize(new Dimension(200, 380));
        listScrollPane.setMinimumSize(new Dimension(150, 300));
        listScrollPane.setBorder(BorderFactory.createTitledBorder(title));
        return listScrollPane;
	}
	
	protected JPanel createButtonPanel()
	{
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
	
	protected JButton createBtn(String btnName)
	{
		JButton btn = new JButton(btnName);
		btn.setSize(80, 30);	
		btn.setPreferredSize(new Dimension(80,30));
		return btn;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == okayBtn)
		{
			//return the result
			if(resultTree != null && getParentFrame() != null)
			{
				JTree gtree = getParentFrame().getGroupTree();
				//需要逐点赋值，不能直接赋值整棵树.
				DefaultMutableTreeNode root1 = (DefaultMutableTreeNode) resultTree.getModel().getRoot();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) gtree.getModel().getRoot();
				if(root1.getChildCount() == 0) {
					this.dispose();
					return;
				}
				root.removeAllChildren();
				int count = root1.getChildCount();
				for(int i=0; i<count; i++)
				{
					DefaultMutableTreeNode child = (DefaultMutableTreeNode)root1.getChildAt(i);
					TargetMsg msg = (TargetMsg)child.getUserObject();
					
					//这边需要过滤一下，如果之前已经添加了，就不要再加进来。
					if(!ShorenUtils.isInNode(root, msg))
						root.add(ShorenUtils.showTargetMsg(msg));
				}
				TreePath rp = new TreePath(root);
				gtree.expandPath(rp);
				gtree.updateUI();
				
				getParentFrame().updateBtns();	
			}			   
			
			this.dispose();
			
		}else if(e.getSource() == cancelBtn)
		{
			this.dispose();
		}else if(e.getSource() == addBtn)
		{
			//获取选取的节点，并添加到resultTree中。
			TreePath[] tp = chooseTree.getSelectionPaths();
			if(tp == null) return;
			
			int len = tp.length;			
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) resultTree.getModel().getRoot();		
			for(int i =0; i<len; i++)
			{
				//遍历选取的集群，并添加在resultTree
				TargetMsg msg = (TargetMsg)((ShorenTreeNode)tp[i].getLastPathComponent()).getUserObject();
				
				//这边需要过滤一下，如果之前已经添加了，就不要再加进来。
				if(!ShorenUtils.isInNode(root, msg))
					root.add(ShorenUtils.showTargetMsg(msg));
			}
			
			TreePath rp = new TreePath(root);
			resultTree.expandPath(rp);
			resultTree.updateUI();
			
		}else if(e.getSource() == deleteBtn)
		{
			TreePath[] tp = resultTree.getSelectionPaths();
			if(tp == null) return;
			
			int len = tp.length;
			for(int i =0; i<len; i++)
			{
				DefaultMutableTreeNode node = (ShorenTreeNode)tp[i].getLastPathComponent();
				node.removeFromParent();
			}
			resultTree.updateUI();
		}else if(e.getSource() == newGroupBtn)
		{	
			TreePath[] tp = chooseTree.getSelectionPaths();			
			if(tp != null)
			{
				int len = tp.length;
				String name = JOptionPane.showInputDialog("请输入复合集群的名字：");
				if( (name == null) || name.equals(""))
					return;
				//这个名字应该唯一
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) chooseTree.getModel().getRoot();
				if(ShorenUtils.hasNameExisted(root, name))
				{
					JOptionPane.showMessageDialog(this , "这个名字已经存在了！^_^");
					return;
				}
				
				List<ComplexGroup> complexGroups = new ArrayList<ComplexGroup>();
				List<TargetGroup> targetGroups = new ArrayList<TargetGroup>();
				for(int i =0; i<len; i++)
				{
					//将选取的集群组成复合集群
					TargetMsg msg = (TargetMsg)((ShorenTreeNode)tp[i].getLastPathComponent()).getUserObject();
					if(msg instanceof ComplexGroup)
						complexGroups.add((ComplexGroup)msg);
					else if(msg instanceof TargetGroup)
						targetGroups.add((TargetGroup)msg);
				}
				ComplexGroup cgroup = new ComplexGroup(name,complexGroups,targetGroups);
				DefaultMutableTreeNode croot = (DefaultMutableTreeNode) chooseTree.getModel().getRoot();	
				WsnPolicyMsg policy = (WsnPolicyMsg)croot.getUserObject();
				policy.getComplexGroups().add(cgroup);
				complexGroupEntry.setWsnpolicymsg(policy);
				//新建复合集群时，必是展示系统所有复合集群，保存。
				ShorenUtils.encodeAllComplexGroups(complexGroupEntry);
				
				//更新chooseTree内容
				ShorenUtils.showPolicyMsg(croot,policy);
				chooseTree.updateUI();
				
				
			}else{
				JOptionPane.showMessageDialog(null , "要选择集群的撒！^_^");
			}
	
		}else if(e.getSource() == deleGroupBtn)
		{
			TreePath[] tp = chooseTree.getSelectionPaths();			
			if(tp != null)
			{
				int len = tp.length;
				DefaultMutableTreeNode croot = (DefaultMutableTreeNode) chooseTree.getModel().getRoot();	
				WsnPolicyMsg policy = (WsnPolicyMsg)croot.getUserObject();

				for(int i =0; i<len; i++)
				{
					//delete选取的复合集群，只能删除复合集群
					ShorenTreeNode node = (ShorenTreeNode)tp[i].getLastPathComponent();
					TargetMsg group = (TargetMsg)node.getUserObject();
					if(group instanceof ComplexGroup)
					{
						Object parent = ((DefaultMutableTreeNode)node.getParent()).getUserObject();
						if(parent instanceof WsnPolicyMsg)
							((WsnPolicyMsg)parent).getComplexGroups().remove(group);
						if(parent instanceof ComplexGroup)
							((ComplexGroup)parent).getComplexGroups().remove(group);
					}
				}				
				
				//delete复合集群时，必是展示系统所有复合集群，保存。
				complexGroupEntry.setWsnpolicymsg(policy);
				ShorenUtils.encodeAllComplexGroups(complexGroupEntry);
				
				//更新chooseTree内容
				ShorenUtils.showPolicyMsg(croot,policy);
				chooseTree.updateUI();
				
			}else{
				JOptionPane.showMessageDialog(null , "要选择集群的撒！^_^");
			}
		}
		
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
		
		List <TargetHost> targethostlist = new ArrayList<TargetHost>();
		TargetHost targethost1 = new TargetHost();
		targethost1.setHostIp("127.0.0.1");
		targethost1.setName("host1");
		targethostlist.add(targethost1);
		
		List <TargetRep> targetreplist = new ArrayList<TargetRep>();
		TargetRep targetrep1 = new TargetRep();
		targetrep1.setName("rep1");
		targetrep1.setRepIp("10.108.166.236");
		targetrep1.setTargetClients(targethostlist);
		targetreplist.add(targetrep1);
		
		List <TargetGroup> targetgrouplist = new ArrayList<TargetGroup>();
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
}
