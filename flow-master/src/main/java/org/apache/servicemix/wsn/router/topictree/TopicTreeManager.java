/*	
 * author:goucanran date:2013-6-20
 * ????????????
 *
 * */
package org.apache.servicemix.wsn.router.topictree;

import com.bupt.wangfu.ldap.Ldap;
import com.bupt.wangfu.ldap.TopicEntry;
import org.apache.servicemix.application.WSNTopicObject;
import org.apache.servicemix.wsn.router.admin.AdminMgr;
import org.apache.servicemix.wsn.router.design.Data;
import org.apache.servicemix.wsn.router.design.PSManagerUI;
import org.apache.servicemix.wsn.router.msg.tcp.UpdateTree;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;
import org.apache.servicemix.wsn.router.wsnPolicy.WsnPolicyInterface;
import org.w3c.dom.Document;

import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.Queue;

//import jaxe.Jaxe;

public class TopicTreeManager {
	public static JTree LibTreeForSchema = null;
	public static WSNTopicObject topicTree;//???wsn??????????? ?????
	private static Toolkit kit;
	private static Dimension screenSize;

	static {
		kit = Toolkit.getDefaultToolkit();
		screenSize = kit.getScreenSize();

	}

	public WsnPolicyInterface wpi = null;
	public Data data = null;
	public Ldap lu = null;
	public DefaultMutableTreeNode lib_root = new DefaultMutableTreeNode("root");
	protected File schemaFile;
	DefaultTreeModel libmodel = new DefaultTreeModel(lib_root);
	JFrame editingJFrame = new JFrame("重命名");
	JFrame addJFrame = new JFrame("添加主题");
	JFrame newTreeFrame = new JFrame("新建主题树");
	ImageIcon addimage = new ImageIcon("icon/add.jpg");
	ImageIcon deleteimage = new ImageIcon("./icon/delete.jpg");
	ImageIcon modifyimage = new ImageIcon("./icon/modify.jpg");
	ImageIcon newtreeimage = new ImageIcon("./icon/newtree.png");
	ImageIcon saveimage = new ImageIcon("./icon/save.jpg");
	ImageIcon reflashimage = new ImageIcon("./icon/reflash.png");
	ImageIcon treeopenimage = new ImageIcon("./icon/图标2.jpg");
	ImageIcon treecloseimage = new ImageIcon("./icon/图标.jpg");
	ImageIcon leafimage = new ImageIcon("./icon/composite.jpg");
	ImageIcon strategyImage = new ImageIcon("./icon/strategy.jpg");
	ImageIcon schemaimage = new ImageIcon("./icon/schema.png");
	private JPanel TTFrame = null;
	private JMenuBar TTMenuBar = null;
	private JToolBar TTToolBar = null;
	private JTree TTTree = null;
	private JTree LibTree = null;
	private DefaultMutableTreeNode root = null;
	private DefaultTreeModel model = null;
	private JPopupMenu popMenu = null;
	private HashMap<String, Integer> topicmap = new HashMap<String, Integer>();
	private int topic_counter;
	private JPanel kuAndTree;
	private int counter = 0;
	private PSManagerUI ui;
	private TransformerFactory tFactory;
	private Transformer transformer;
	private DOMSource source;
	private StreamResult result;

	public TopicTreeManager(Data data, PSManagerUI ui) {
		this.data = data;
		this.ui = ui;
		TTTree = new JTree(model);
		LibTree = new JTree(libmodel);
		LibTreeForSchema = new JTree(libmodel);
		LibTreeForSchema.setAutoscrolls(true);
		LibTree.setAutoscrolls(true);
	}

	public TopicTreeManager(Data data) {
		this.data = data;
		TTTree = new JTree(model);
		LibTree = new JTree(libmodel);
		LibTreeForSchema = new JTree(libmodel);
		LibTreeForSchema.setAutoscrolls(true);

		LibTree.setAutoscrolls(true);
	}

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, NamingException {
		// TODO Auto-generated method stub
		TopicTreeManager tt = new TopicTreeManager(null);
//		JFrame frame = tt.getTreeInstance();
//		frame.setVisible(true);
//		frame.show();


		TopicEntry all = new TopicEntry("all", "1",
				"ou=all_test,dc=wsn,dc=com", null);
		DefaultMutableTreeNode newtree = new DefaultMutableTreeNode(all);
		tt.lu = new Ldap();
		System.out.println("????2");
		tt.lu.connectLdap("10.109.253.6", "cn=Manager,dc=wsn,dc=com", "123456");
		System.out.println("????1");
		tt.setTTTree(newtree);
		System.out.println("????");
		System.out.println(newtree);
//		try {
//			Socket s=new Socket();
//			s.connect(new InetSocketAddress("10.108.166.237",4001),5000);
//			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
//			oos.writeObject(new String("lookup the database!"));//??
//			s.close();
//
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	//???????
	private void frame_init() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, NamingException {
		TTFrame.setLayout(new BorderLayout());
		TTFrame.setSize(new Dimension(600, 520));
		TTFrame.setPreferredSize(new Dimension(600, 520));
		TTFrame.setOpaque(false);
		TTFrame.setBounds(screenSize.width / 4, screenSize.height / 8,
				1100, 630);
		//	TTFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//		TTFrame.setResizable(false);
		//String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		//UIManager.setLookAndFeel(lookAndFeel);

		tFactory = TransformerFactory.newInstance();
		try {
			transformer = tFactory.newTransformer();
			source = new DOMSource();
//		    result = new StreamResult();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//??LDAP???
		lu = new Ldap();
		lu.connectLdap(AdminMgr.ldapAddr, "cn=Manager,dc=wsn,dc=com", "123456");

		OPFrame_init();//??????????????
		MenuBar_init();//??????
		ToolBar_init();//??????
		PopMenu_init();//???????
		LibTree_init();//????????
		Database_init();//LDAP?????
		Tree_init();//????????
		policy_init();//??????????
	}

	public void Database_init() throws NamingException {
		// TODO Auto-generated method stub
		reload_LibTrees();
	}

	public void reload_LibTrees() throws NamingException {
		// TODO Auto-generated method stub

		TopicEntry libentry = new TopicEntry();
		libentry.setTopicName("all");
		libentry.setTopicCode("1");
		libentry.setTopicPath("ou=all_test,dc=wsn,dc=com");
		lib_root.setUserObject(libentry);

		lib_root.removeAllChildren();
		setTTTree(lib_root);
		TTTreeToWSNObject();
		libmodel.reload(lib_root);
		LibTree.setModel(libmodel);
		LibTreeForSchema.setModel(libmodel);
		LibTreeForSchema.updateUI();

		LibTree.updateUI();
	}

	private void OPFrame_init() {
		//??????????
		editingJFrame.setBounds(screenSize.width / 2, screenSize.height / 2,
				300, 150);
		editingJFrame.setLayout(null);
		final JTextField editArea = new JTextField();
		editArea.setBounds(30, 20, 220, 30);
		editingJFrame.add(editArea);
		JButton editconfirm = new JButton("??");
		editconfirm.setBounds(30, 70, 100, 30);
		editconfirm.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent arg0) {
				// ??????????????
				JTree temptree = TTTree;
				DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) TTTree.getLastSelectedPathComponent();
				if (treenode == null) {
					treenode = (DefaultMutableTreeNode) LibTree.getLastSelectedPathComponent();
					temptree = LibTree;
				}
				String newname = editArea.getText().trim();

				if (treenode != null && !newname.equals(new String(""))) {
					TopicEntry tempentry = (TopicEntry) treenode.getUserObject();
					//	tempentry.setTopicName(newname);
					if (topicmap.containsKey(newname))
						tempentry.setTopicCode(topicmap.get(newname).toString());
					else {
						tempentry.setTopicCode("" + (++topic_counter));
						topicmap.put(newname, topic_counter);
					}

					String oldname = tempentry.getTopicName();
					UpdateTree ut = new UpdateTree(System.currentTimeMillis());
					ut.oldName = ShorenUtils.getFullName(tempentry);
					ut.newName = ut.oldName.replace(tempentry.getTopicName(), newname);
					ut.change = 0;
					//   data.sendNotification(ut);
					try {
						lu.rename(tempentry, newname);
					} catch (NamingException e1) {
						e1.printStackTrace();
					}

					treenode.setUserObject(tempentry);//????????treenode

					//????treenode???????
					Queue<DefaultMutableTreeNode> queue = new LinkedList<DefaultMutableTreeNode>();
					queue.offer(treenode);
					while (!queue.isEmpty()) {
						DefaultMutableTreeNode temptreenode = queue.poll();
						Enumeration list = temptreenode.children();
						while (list.hasMoreElements()) {
							DefaultMutableTreeNode treenodeChild = (DefaultMutableTreeNode) list.nextElement();
							TopicEntry childUO = (TopicEntry) treenodeChild.getUserObject();
							childUO.setTopicPath(childUO.getTopicPath().replaceAll(oldname, newname));
							treenodeChild.setUserObject(childUO);
							queue.offer(treenodeChild);
						}
					}


					if (temptree == LibTree) {
						libmodel.reload(lib_root);
						LibTree.setModel(libmodel);
						reload_TTTree(treenode);
						TTTreeToWSNObject();
					} else {
						model.reload(root);
						TTTree.setModel(model);
						try {
							reload_LibTrees();
						} catch (NamingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						search_LibTree(treenode, lib_root);
					}
					DefaultTreeModel tempmodel = (DefaultTreeModel) temptree.getModel();
					TreePath temp_path = new TreePath(tempmodel.getPathToRoot(treenode));
					temptree.setSelectionPath(temp_path);
					//TTTree.setSelectionPath(new TreePath(model.getPathToRoot(treenode)));


					data.sendNotification(ut);
				}
				editingJFrame.setVisible(false);
			}
		});
		JButton editcancel = new JButton("??");
		editcancel.setBounds(150, 70, 100, 30);
		editcancel.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				editingJFrame.setVisible(false);
			}
		});
		editingJFrame.add(editconfirm);
		editingJFrame.add(editcancel);
		editingJFrame.setVisible(false);

		//??????????
		addJFrame.setBounds(screenSize.width / 2, screenSize.height / 2,
				300, 150);
		addJFrame.setLayout(null);
		final JTextField addArea = new JTextField();
		addArea.setBounds(30, 20, 220, 30);
		addJFrame.add(addArea);
		JButton addconfirm = new JButton("??");
		addconfirm.setBounds(30, 70, 100, 30);
		addconfirm.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent arg0) {
				// ???????????????????
				JTree temptree = TTTree;
				DefaultMutableTreeNode temp_node = (DefaultMutableTreeNode) TTTree.getLastSelectedPathComponent();
				if (temp_node == null) {
					temptree = LibTree;
					temp_node = (DefaultMutableTreeNode) LibTree.getLastSelectedPathComponent();
				}
				TopicEntry temptopic = (TopicEntry) temp_node.getUserObject();
				TopicEntry newtopic = new TopicEntry();
				newtopic.setTopicName(addArea.getText());
				newtopic.setTopicCode("" + getTopicCode(newtopic.getTopicName()));
				newtopic.setTopicPath("ou=" + newtopic.getTopicName() + "," + temptopic.getTopicPath());
				lu.create(newtopic);

				DefaultMutableTreeNode new_node = new DefaultMutableTreeNode();
				new_node.setUserObject(newtopic);
				temp_node.add(new_node);

				//?????????????????
				if (temptree == LibTree) {
					libmodel.reload(lib_root);
					LibTree.setModel(libmodel);
					reload_TTTree(new_node);
					TTTreeToWSNObject();
				} else {
					model.reload(root);
					TTTree.setModel(model);
					try {
						reload_LibTrees();
					} catch (NamingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					search_LibTree(temp_node, lib_root);
				}
				DefaultTreeModel tempmodel = (DefaultTreeModel) temptree.getModel();
				TreePath temp_path = new TreePath(tempmodel.getPathToRoot(new_node));
				temptree.setSelectionPath(temp_path);
				addJFrame.setVisible(false);
				data.sendNotification(new UpdateTree(System.currentTimeMillis()));

				temptree.requestFocusInWindow();

				ui.reflashJtreeRoot();
			}
		});
		JButton addcancel = new JButton("??");
		addcancel.setBounds(150, 70, 100, 30);
		addcancel.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				addJFrame.setVisible(false);
			}
		});
		addJFrame.add(addconfirm);
		addJFrame.add(addcancel);
		addJFrame.setVisible(false);

		//??????????????????????
		newTreeFrame.setBounds(screenSize.width / 2, screenSize.height / 2,
				300, 150);
		newTreeFrame.setLayout(null);
		final JTextField newtreeArea = new JTextField();
		newtreeArea.setBounds(30, 20, 220, 30);
		newTreeFrame.add(newtreeArea);
		JButton newconfirm = new JButton("??");
		newconfirm.setBounds(30, 70, 100, 30);
		newconfirm.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent arg0) {
				// ?????????????????
				if (!newtreeArea.getText().trim().equals(new String(""))) {
					try {
						new_tree(newtreeArea.getText().trim());
					} catch (NamingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					newTreeFrame.setVisible(false);
					//	TTTree.requestFocusInWindow();
				}


			}
		});
		JButton newcancel = new JButton("??");
		newcancel.setBounds(150, 70, 100, 30);
		newcancel.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				newTreeFrame.setVisible(false);
			}
		});
		newTreeFrame.add(newconfirm);
		newTreeFrame.add(newcancel);
		newTreeFrame.setVisible(false);
	}

	protected void search_LibTree(DefaultMutableTreeNode node, DefaultMutableTreeNode cur_node) {
		// ????????????
		TopicEntry tempentry = (TopicEntry) cur_node.getUserObject();
		TopicEntry targetentry = (TopicEntry) node.getUserObject();
		if (tempentry.getTopicPath().equals(targetentry.getTopicPath()))
			LibTree.setSelectionPath(new TreePath(libmodel.getPathToRoot(cur_node)));
		else {
			Enumeration<DefaultMutableTreeNode> children = cur_node.children();
			while (children.hasMoreElements()) {
				DefaultMutableTreeNode child = children.nextElement();
				search_LibTree(node, child);
			}
		}
	}

	protected int getTopicCode(String topicName) {
		// ??????????
		int index;
		if (!topicmap.containsKey(topicName)) {
			topicmap.put(topicName, ++topic_counter);
			index = topic_counter;
		} else
			index = topicmap.get(topicName);
		return index;
	}

//	 public JTree getLibTree(){
//		 JTree libTreeCopy = LibTree;
//		return libTreeCopy;
//	}

	private void LibTree_init() {
		// ???????????
		LibTree.setSize(250, 500);

		LibTree.setRootVisible(false);
		LibTree.setEditable(false);
		LibTree.setDragEnabled(true);
		LibTree.setDropMode(DropMode.ON_OR_INSERT);
		LibTree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		try {
			LibTree.setTransferHandler(new TreeTransferHandler1(lu, LibTree, TTTree, data));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LibTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.getButton() == 1) {
					//??????????????????
					TreePath path = LibTree.getPathForLocation(evt.getX(), evt.getY());
					if (path != null) {
						LibTree.setSelectionPath(path);
						DefaultMutableTreeNode chosen_node = (DefaultMutableTreeNode) LibTree.getLastSelectedPathComponent();
						topicmap.clear();
						topic_counter = 0;
						System.out.println("chosen_node" + chosen_node);
						TopicEntry tempentry = (TopicEntry) chosen_node.getUserObject();
						if (tempentry != null && !tempentry.getTopicCode().equals("null")) {
							topicmap.put(tempentry.getTopicName(), Integer.parseInt(tempentry.getTopicCode()));
							root.removeAllChildren();
							root.setUserObject(tempentry);
							TTTree.setRootVisible(false);
							reload_TTTree(root);
							LibTree.setSelectionPath(path);
							new Thread() {

								void TTTreeToWSNObject() {
									TopicEntry rootNode = (TopicEntry) lib_root.getUserObject();
									topicTree = new WSNTopicObject(rootNode, null);
									Queue<WSNTopicObject> queue = new LinkedList<WSNTopicObject>();
									queue.offer(topicTree);
									while (!queue.isEmpty()) {
										WSNTopicObject to = queue.poll();
										List<TopicEntry> ls = lu.getSubLevel(to.getTopicentry());
										if (!ls.isEmpty()) {
											List<WSNTopicObject> temp = new ArrayList<WSNTopicObject>();
											for (TopicEntry t : ls) {
												WSNTopicObject wto = new WSNTopicObject(t, to);
												temp.add(wto);
												queue.offer(wto);
											}
											to.setChildrens(temp);
										}
									}
								}
							}.start();
						} else {
							JOptionPane.showMessageDialog(null, "?????????????????!");

						}
					}
				}
			}
		});
		LibTreeForSchema.setRootVisible(false);
		LibTreeForSchema.setEditable(false);
		LibTreeForSchema.setDragEnabled(false);
		LibTreeForSchema.setDropMode(DropMode.ON_OR_INSERT);
		LibTreeForSchema.setToggleClickCount(2);
//		LibTreeForSchema.e
		LibTreeForSchema.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		try {
			LibTreeForSchema.setTransferHandler(new TreeTransferHandler1(lu, LibTree, TTTree, data));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LibTreeForSchema.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
//				if(e.getClickCount()==2){//????????????
//
//					TreePath path = LibTreeForSchema.getPathForLocation(e.getX(), e.getY());
//					LibTreeForSchema.setSelectionPath(path);
//					DefaultMutableTreeNode chosenSchema = (DefaultMutableTreeNode)LibTreeForSchema.getLastSelectedPathComponent();
//					if(path!=null&&chosenSchema.isLeaf())
//					{
//						//??????????????????);
//						File clickSchemaFile = new File("schema/"+chosenSchema.toString()+".xsd");
//						if(clickSchemaFile.exists()){
//							Jaxe.schemaFrame.ouvrir(clickSchemaFile);
//							ui.lblNewLabel_5.setText("        ???" + chosenSchema.toString());
//							ui.lblNewLabel_5.setName(chosenSchema.toString());
//						}else
//							JOptionPane.showMessageDialog(null, "??????schema? "+chosenSchema.toString());
//					}
//
//				}

			}

			public void mousePressed(MouseEvent evt) {
				if (evt.getButton() == 1) {
//					TreePath path = LibTreeForSchema.getPathForLocation(evt.getX(), evt.getY());
//					if(path!=null)
//					{
//						LibTreeForSchema.setSelectionPath(path);
//					DefaultMutableTreeNode chosenSchema = (DefaultMutableTreeNode)LibTreeForSchema.getLastSelectedPathComponent();
//					//??????????????????);
//					File clickSchemaFile = new File("schema/"+chosenSchema.toString()+".xsd");
//					if(clickSchemaFile.exists()){
//					Jaxe.schemaFrame.ouvrir(clickSchemaFile);
//					ui.lblNewLabel_5.setText("        ???" + chosenSchema.toString());
//					ui.lblNewLabel_5.setName(chosenSchema.toString());
//					}else
//						JOptionPane.showMessageDialog(null, "??????schema?");
//					}
				}
			}
		});


		LibTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.getButton() == 3) {
					//???????????????????????????????????????????????????
					TreePath path = LibTree.getPathForLocation(evt.getX(), evt.getY());
					if (path != null) {
						LibTree.setSelectionPath(path);
						DefaultMutableTreeNode chosen_node = (DefaultMutableTreeNode) LibTree.getLastSelectedPathComponent();
						topicmap.clear();
						topic_counter = 0;
						System.out.println("chosen_node" + chosen_node);
						TopicEntry tempentry = (TopicEntry) chosen_node.getUserObject();
						if (tempentry != null && !tempentry.getTopicCode().equals("null")) {
							topicmap.put(tempentry.getTopicName(), Integer.parseInt(tempentry.getTopicCode()));
							root.removeAllChildren();
							root.setUserObject(tempentry);
							TTTree.setRootVisible(false);
							reload_TTTree(root);
							LibTree.setSelectionPath(path);
							new Thread() {

								void TTTreeToWSNObject() {
									TopicEntry rootNode = (TopicEntry) lib_root.getUserObject();
									topicTree = new WSNTopicObject(rootNode, null);
									Queue<WSNTopicObject> queue = new LinkedList<WSNTopicObject>();
									queue.offer(topicTree);
									while (!queue.isEmpty()) {
										WSNTopicObject to = queue.poll();
										List<TopicEntry> ls = lu.getSubLevel(to.getTopicentry());
										if (!ls.isEmpty()) {
											List<WSNTopicObject> temp = new ArrayList<WSNTopicObject>();
											for (TopicEntry t : ls) {
												WSNTopicObject wto = new WSNTopicObject(t, to);
												temp.add(wto);
												queue.offer(wto);
											}
											to.setChildrens(temp);
										}
									}
								}
							}.start();
						} else {
							JOptionPane.showMessageDialog(null, "?????????????????!");

						}
					}
					//?????????
					TreePath path2 = LibTree.getPathForLocation(evt.getX(), evt.getY());
					LibTree.setSelectionPath(path2);
					popMenu.show(LibTree, evt.getX(), evt.getY());
				}
			}
		});
		JScrollPane TreePane = new JScrollPane(LibTree);
		TreePane.setBounds(30, 30, 300, 500);
		TreePane.setPreferredSize(new Dimension(150, 500));
		Border title1 = BorderFactory.createTitledBorder("?");
		TreePane.setBorder(title1);
		TreePane.setOpaque(false);
		TTFrame.add(BorderLayout.WEST, TreePane);
	}

	//?Jtree???WSNTopicIObject
	public void TTTreeToWSNObject() {
		TopicEntry rootNode = (TopicEntry) this.lib_root.getUserObject();
		topicTree = new WSNTopicObject(rootNode, null);
		Queue<WSNTopicObject> queue = new LinkedList<WSNTopicObject>();
		queue.offer(topicTree);
		while (!queue.isEmpty()) {
			WSNTopicObject to = queue.poll();
			List<TopicEntry> ls = lu.getSubLevel(to.getTopicentry());
			if (!ls.isEmpty()) {
				List<WSNTopicObject> temp = new ArrayList<WSNTopicObject>();
				for (TopicEntry t : ls) {
					WSNTopicObject wto = new WSNTopicObject(t, to);
					temp.add(wto);
					queue.offer(wto);
					//??schema??????

					Document schemaT = t.getSchema();
					if (schemaT != null) {
						try {
							source.setNode(schemaT);
							result = new StreamResult(new File("schema/" + t.getTopicName() + ".xsd"));
//							result.setSystemId(new File("schema/"+t.getTopicName()+".xsd"));;
							transformer.transform(source, result);

						} catch (Exception ex) {
							ex.printStackTrace();
						}
						//	System.out.println(t.getTopicName());
					}
				}
				to.setChildrens(temp);
			}
		}
	}

	protected void setTTTree(DefaultMutableTreeNode tempnode) {
		// ?????????

		List<TopicEntry> topics = null;
		TopicEntry tempentry = (TopicEntry) tempnode.getUserObject();

		//if(tempentry.getTopicCode()!=null){
		topics = lu.getSubLevel(tempentry);
//			List<WSNTopicObject> temp = new ArrayList<WSNTopicObject>();
		for (TopicEntry topic : topics) {
			DefaultMutableTreeNode newnode = new DefaultMutableTreeNode();
			int tempTopicCode = 0;
			if (!tempentry.getTopicCode().equals("null")) {
				tempTopicCode = Integer.parseInt(tempentry.getTopicCode());
			}
			topicmap.put(topic.getTopicName(), tempTopicCode);
			if (tempTopicCode > topic_counter)
				topic_counter = tempTopicCode;
			newnode.setUserObject(topic);
			setTTTree(newnode);

			tempnode.add(newnode);


		}
		//	}
	}

	private void policy_init() {
		// ???????
		JPanel wpiPanel = new JPanel();
		wpi = new WsnPolicyInterface(wpiPanel, lu);
		wpiPanel.setBounds(690, 30, 400, 520);
		//TTFrame.add(BorderLayout.EAST, wpiPanel);
	}

	private void PopMenu_init() {
		// ???????
		popMenu = new JPopupMenu();
		JMenuItem addItem = new JMenuItem("??", addimage);
		addItem.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				add_node_to_tree();
			}
		});
		JMenuItem delItem = new JMenuItem("??", deleteimage);
		delItem.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				delete_node_from_tree();
			}
		});
		JMenuItem editItem = new JMenuItem("???", modifyimage);
		editItem.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				rename_node();
			}
		});

		JMenuItem strategyItem = new JMenuItem("????", strategyImage);
		strategyItem.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode temp = (DefaultMutableTreeNode) TTTree.getLastSelectedPathComponent();
				JComboBox[] topicComboBox = {ui.comboBox, ui.comboBox_1, ui.comboBox_2, ui.comboBox_3, ui.comboBox_4, ui.comboBox_5};
				int rightLevel = 0;
				if (temp != null) rightLevel = temp.getLevel();
				int leftLevel = ((DefaultMutableTreeNode) LibTree.getLastSelectedPathComponent()).getLevel();
				int currentlevel = leftLevel + rightLevel;
				int[] indexs = new int[currentlevel];

				if (temp == null) {//??????
					temp = (DefaultMutableTreeNode) LibTree.getLastSelectedPathComponent();
					for (int j = 0; j < leftLevel; j++) {

						if (!temp.equals(lib_root)) {
							int tempIndex = temp.getParent().getIndex(temp);
							indexs[leftLevel - 1 - j] = tempIndex + 1;
							temp = (DefaultMutableTreeNode) temp.getParent();
						}
					}
				} else {    //?????

					for (int i = 0; i < rightLevel; i++) {//??????????

						if (temp.getParent() != null) {
							int tempIndex = temp.getParent().getIndex(temp);
							indexs[currentlevel - 1 - i] = tempIndex + 1;
							temp = (DefaultMutableTreeNode) temp.getParent();


						}
					}
					temp = (DefaultMutableTreeNode) LibTree.getLastSelectedPathComponent();
					for (int j = 0; j < leftLevel; j++) {    //?????????

						if (!temp.equals(lib_root)) {
							int tempIndex = temp.getParent().getIndex(temp);
							indexs[leftLevel - 1 - j] = tempIndex + 1;
							temp = (DefaultMutableTreeNode) temp.getParent();
						}
					}
				}


				System.out.println("indexs:" + indexs + indexs[0]);
				for (int k = 0; k < indexs.length; k++) {

					topicComboBox[k].setSelectedIndex(indexs[k]);
				}


				Hashtable<Integer, DefaultMutableTreeNode> selectedTopicPath = new Hashtable<Integer, DefaultMutableTreeNode>();


				do {
					selectedTopicPath.put(new Integer(temp.getLevel()), temp);
					temp = (DefaultMutableTreeNode) temp.getParent();
					//	System.out.println("temp.getParent().toString()"+temp.getParent());
				} while (temp != null && !((DefaultMutableTreeNode) temp).equals(lib_root));//???????????? ??????
				if (temp != null)
					System.out.println("???????????temp.toString()" + temp.toString());
				else {
					System.out.println("selectedTopicPath" + selectedTopicPath);//selectedTopicPath.add(lib_root);
					//???????????
					temp = (DefaultMutableTreeNode) LibTree.getLastSelectedPathComponent();
					temp = (DefaultMutableTreeNode) temp.getParent();
					do {
						if (!selectedTopicPath.contains(temp) && !temp.toString().equals("all"))
							selectedTopicPath.put(new Integer(temp.getLevel()), temp);
						temp = (DefaultMutableTreeNode) temp.getParent();
						//	System.out.println("temp.getParent().toString()"+temp.getParent());
					} while (temp != null && !((DefaultMutableTreeNode) temp).equals(lib_root));//???????????? ??????
					if (temp != null)
						System.out.println("??????????????temp.toString()" + temp.toString());
					System.out.println("??????????selectedTopicPath" + selectedTopicPath);
				}
//					for(int i=0; i<selectedTopicPath.size();i++){
//						if(selectedTopicPath.get(i).getParent()==null){
//							topicComboBox[i].setSelectedIndex(lib_root.getIndex(selectedTopicPath.get(i)));
//						}else
//						topicComboBox[i].setSelectedIndex((selectedTopicPath.get(i).getParent().getIndex((selectedTopicPath.get(i)))));
//					//	topicComboBox[i].setSelectedItem(selectedTopicPath.get(i));
//						topicComboBox[i].repaint();
//					}
				ui.reflashFbdnGroups();
				ui.visualManagement.setSelectedIndex(2);//?????????
//					TopicEntry selectedTopic;
//					selectedTopic = (TopicEntry)temp.getUserObject();
//					String topicPath = selectedTopic.getTopicPath();


				//			wpi.updateTopics(null);
			}

		});

//		JMenuItem schema = new JMenuItem("schema",schemaimage);
//		schema.addActionListener(new ActionListener(){
////			private File schemaFile;
//
////			@Override
//			public void actionPerformed(ActionEvent e){
//
////				TreePath path = LibTreeForSchema.getPathForLocation(evt.getX(), evt.getY());
////				if(path!=null)
////				{
////					LibTreeForSchema.setSelectionPath(path);
//				DefaultMutableTreeNode chosenSchema = (DefaultMutableTreeNode)LibTree.getLastSelectedPathComponent();
//				//??????????????????);
//				File popSchemaFile = new File("schema/"+chosenSchema.toString()+".xsd");
//				if(popSchemaFile.exists()){
//					Jaxe.schemaFrame.ouvrir(popSchemaFile);
//					LibTreeForSchema.setSelectionPath(LibTree.getLeadSelectionPath());
//
//					ui.visualManagement.setSelectedIndex(3);//?????????
//					//????????
//					ui.lblNewLabel_5.setText("        ???" + chosenSchema.toString());
//					ui.lblNewLabel_5.setName(chosenSchema.toString());
//				}else if(!chosenSchema.isLeaf()){
//					LibTreeForSchema.setSelectionPath(LibTree.getLeadSelectionPath());
//					ui.visualManagement.setSelectedIndex(3);//?????????
//					//????????
//					ui.lblNewLabel_5.setText("        ???" + chosenSchema.toString());
//					ui.lblNewLabel_5.setName(chosenSchema.toString());
//				}else
//					JOptionPane.showMessageDialog( null, "??????schema? "+chosenSchema.toString());
//			}
//		});

		popMenu.add(addItem);
		popMenu.add(delItem);
		popMenu.add(editItem);
		popMenu.add(strategyItem);
//		popMenu.add(schema);
	}

	private void Tree_init() {
		// ??????????
		root = new DefaultMutableTreeNode("root");
		model = new DefaultTreeModel(root);
		TTTree.setModel(model);
		TTTree.setSize(440, 500);
		TTTree.setRootVisible(false);
		TTTree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		//TTTree.putClientProperty("JTree.lineStyle", "Horizontal");
		//TTTree.setToggleClickCount(1);
		TTTree.setEditable(false);
		TTTree.setDragEnabled(true);
		TTTree.setDropMode(DropMode.ON_OR_INSERT);
		TTTree.setShowsRootHandles(false);

		DefaultTreeCellRenderer renderer =
				new DefaultTreeCellRenderer();
		renderer.setClosedIcon(treecloseimage);
		renderer.setOpenIcon(treeopenimage);
		renderer.setLeafIcon(leafimage);
		TTTree.setCellRenderer(renderer);

		try {
			TTTree.setTransferHandler(new TreeTransferHandler1(lu, LibTree, TTTree, data));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TTTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.getButton() == 3) {
					TreePath path = TTTree.getPathForLocation(evt.getX(), evt.getY());
					TTTree.setSelectionPath(path);
					popMenu.show(TTTree, evt.getX(), evt.getY());
				}
			}
		});

		JScrollPane TreePane = new JScrollPane(TTTree);
		TreePane.setBounds(250, 30, 440, 500);
		Border title1 = BorderFactory.createTitledBorder("???");
		TreePane.setBorder(title1);
		TreePane.setOpaque(false);
		TTFrame.add(BorderLayout.CENTER, TreePane);
	}

	private void MenuBar_init() {
		// ??????
		TTMenuBar = new JMenuBar();

		JMenu file = new JMenu("??");

		JMenuItem open_menu = new JMenuItem("??", newtreeimage);
		file.add(open_menu);

		JMenuItem save_menu = new JMenuItem("??", saveimage);
		file.add(save_menu);

//		JMenuItem exit_menu = new JMenuItem("??",closeimage);
//		exit_menu.addActionListener(new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent e){
//				TTFrame.setVisible(false);
//			}
//		});
//		file.add(exit_menu);

		JMenu tree = new JMenu("???");

		JMenuItem add_menu = new JMenuItem("??", addimage);
		add_menu.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				add_node_to_tree();
			}
		});
		tree.add(add_menu);

		JMenuItem delete_menu = new JMenuItem("??", deleteimage);
		delete_menu.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				delete_node_from_tree();
			}
		});
		tree.add(delete_menu);

		JMenuItem modify_menu = new JMenuItem("???", modifyimage);
		modify_menu.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				rename_node();
			}
		});
		tree.add(modify_menu);

		TTMenuBar.add(file);
		TTMenuBar.add(tree);
		//	TTFrame.add(TTMenuBar);
		//TTFrame.add(TTMenuBar,BorderLayout.NORTH);
	}

	protected void rename_node() {
		// TODO Auto-generated method stub
		if (TTTree.getSelectionPath() != null | LibTree.getSelectionPath() != null) {
			//TTTree.startEditingAtPath(TTTree.getSelectionPath());
			editingJFrame.setVisible(true);
		}
		ui.reflashJtreeRoot();
	}

	protected void delete_node_from_tree() {
		// ??????
		JTree temptree = TTTree;
		DefaultMutableTreeNode temp_node = (DefaultMutableTreeNode) TTTree.getLastSelectedPathComponent();
		if (temp_node == null) {
			temp_node = (DefaultMutableTreeNode) LibTree.getLastSelectedPathComponent();
			temptree = LibTree;
		}
		if (temp_node == null)
			return;
		DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode) temp_node.getParent();
		TopicEntry tempEntry = (TopicEntry) temp_node.getUserObject();
		lu.deleteWithAllChildrens(tempEntry);
		temp_node.removeFromParent();
		if (temptree == LibTree) {
			libmodel.reload(lib_root);
			LibTree.setModel(libmodel);
			if (parent_node != lib_root)
				reload_TTTree(parent_node);
			else {
				root.removeAllChildren();
				model.reload();
				TTTree.setModel(model);
				TTTree.updateUI();
			}
		} else {
			model.reload(root);
			TTTree.setModel(model);
			try {
				reload_LibTrees();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			search_LibTree(parent_node, lib_root);
		}

		if (parent_node != null) {
			DefaultTreeModel tempmodel = (DefaultTreeModel) temptree.getModel();
			temptree.setSelectionPath(new TreePath(tempmodel.getPathToRoot(parent_node)));
		}
		UpdateTree ut = new UpdateTree(System.currentTimeMillis());
		ut.oldName = ShorenUtils.getFullName(tempEntry);
		ut.change = 1;
		data.sendNotification(ut);

		ui.reflashJtreeRoot();
	}

	private void reload_TTTree(DefaultMutableTreeNode rootnode) {
		// ???????
		TopicEntry rootentry = (TopicEntry) rootnode.getUserObject();

		root.setUserObject(rootentry);

		root.removeAllChildren();
		setTTTree(root);
		//TTTreeToWSNObject();

		model.reload(root);
		TTTree.setModel(model);
		TTTree.updateUI();
	}

	protected void add_node_to_tree() {
		// TODO Auto-generated method stub
		addJFrame.setVisible(true);
		//TTTree.setSelectionPath(null);
		//TTTree.updateUI();
	}

	private void ToolBar_init() {
		// ??????
		TTToolBar = new JToolBar(SwingConstants.HORIZONTAL);
		TTToolBar.setSize(1200, 30);

		JButton open_button = new JButton(newtreeimage);
		open_button.setToolTipText("?????");
		open_button.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				newTreeFrame.setVisible(true);
			}
		});
		TTToolBar.add(open_button);

		JButton save_button = new JButton(saveimage);
		save_button.setToolTipText("??");
		save_button.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				topic_counter = 0;
				Enumeration<DefaultMutableTreeNode> children = root.children();
				while (children.hasMoreElements()) {
					DefaultMutableTreeNode child = children.nextElement();
					save_tree(child, "");
				}
			}
		});
		TTToolBar.add(save_button);

		JButton add_button = new JButton(addimage);
		add_button.setToolTipText("????");
		add_button.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				add_node_to_tree();
			}
		});
		TTToolBar.add(add_button);

		JButton delete_button = new JButton(deleteimage);
		delete_button.setToolTipText("????");
		delete_button.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				delete_node_from_tree();
			}
		});
		TTToolBar.add(delete_button);

		JButton modify_button = new JButton(modifyimage);
		modify_button.setToolTipText("???");
		modify_button.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
				rename_node();
			}
		});
		TTToolBar.add(modify_button);

		JButton close_button = new JButton(reflashimage);
		close_button.setToolTipText("??");
		close_button.addActionListener(new ActionListener() {
			//			@Override
			public void actionPerformed(ActionEvent e) {
//				TTFrame.setVisible(false);
				//?ldap?????????
				try {
					PSManagerUI.topicTreeManager.lu = new Ldap();
					PSManagerUI.topicTreeManager.lu.connectLdap(AdminMgr.ldapAddr, "cn=Manager,dc=wsn,dc=com", "123456");
					PSManagerUI.topicTreeManager.reload_LibTrees();
					PSManagerUI.topicTreeM.invalidate();
				} catch (NamingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		TTToolBar.add(close_button);
		TTFrame.add(BorderLayout.NORTH, TTToolBar);
	}

	protected void new_tree(String newtree_name) throws NamingException {
		// ????

		TopicEntry libentry = (TopicEntry) lib_root.getUserObject();
		TopicEntry treeentry = new TopicEntry();
		treeentry.setTopicName(newtree_name);
		treeentry.setTopicPath("ou=" + newtree_name + "," + libentry.getTopicPath());
		treeentry.setTopicCode("" + getTopicCode(newtree_name));
		DefaultMutableTreeNode newtree = new DefaultMutableTreeNode(treeentry);
		DefaultMutableTreeNode newroot = new DefaultMutableTreeNode(treeentry);

		lu.create(treeentry);

		lib_root.add(newroot);
		libmodel.reload(lib_root);
		LibTree.updateUI();
		reload_TTTree(newtree);
		TTTreeToWSNObject();
		data.sendNotification(new UpdateTree(System.currentTimeMillis()));

		newtree.setParent(lib_root);
		TreePath visiblePath = new TreePath(libmodel.getPathToRoot(newtree));
		LibTree.setSelectionPath(visiblePath);
		LibTree.scrollPathToVisible(visiblePath);
		LibTree.makeVisible(visiblePath);
		ui.reflashJtreeRoot();
	}

	protected void save_tree(DefaultMutableTreeNode current_node, String current_code) {
		// TODO Auto-generated method stub
	}

	public JPanel getTreeInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, NamingException {
		// ?????????
		if (TTFrame == null) {
			TTFrame = new JPanel();
			frame_init();
		}
		return TTFrame;
	}
}