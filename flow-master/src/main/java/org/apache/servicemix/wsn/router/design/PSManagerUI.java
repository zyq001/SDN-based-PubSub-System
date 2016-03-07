package org.apache.servicemix.wsn.router.design;

import com.bupt.wangfu.ldap.TopicEntry;
import edu.bupt.wangfu.sdn.floodlight.RestProcess;
import edu.bupt.wangfu.sdn.info.DevInfo;
import edu.bupt.wangfu.sdn.info.Flow;
import edu.bupt.wangfu.sdn.info.MemoryInfo;
import org.apache.servicemix.wsn.router.admin.AdminMgr;
import org.apache.servicemix.wsn.router.mgr.BrokerUnit;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupMember_;
import org.apache.servicemix.wsn.router.topictree.TopicTreeManager;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;

import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//import demo.network.miscellaneous.office.*;
//import demo.network.miscellaneous.office.OfficeDemo;
//import jaxe.Jaxe;
//import jaxe.JaxeFrame;
//import jaxe.JaxeResourceBundle;

public class PSManagerUI implements IAdminUI {

	public static TopicTreeManager topicTreeManager = null;// 主题树
	public static JPanel topicTreeM;// 主题树管理
	public static String currentGroupName = null;
	public JTabbedPane visualManagement;// "可视化管理"
	public JTextArea text;// 控制台文本
	public JComboBox comboBox;// 根主题
	public JComboBox comboBox_1;
	public JComboBox comboBox_2;
	public JComboBox comboBox_3;
	public JComboBox comboBox_4;
	public JComboBox comboBox_5;
	public JLabel lblNewLabel_5;
	public JTable suber;
	protected TopicEntry currentTopicEntry = new TopicEntry();
	ArrayList<WsnPolicyMsg> policyList;
	// private Text text;
	private Data data = new Data();// 数据类
	private FileOperation configFile = new FileOperation();// 配置文件管理
	private AdminMgr interactIF;// 系统管理员
	private JFrame frame;// 主窗口
	private JTabbedPane tabbedPane_1;// 顶层tab窗体，分为图形化管理，控制台，系统设置等
	private JPanel groupsMgmt;// 集群信息管理
	private JScrollPane allGroupsScrollPane;// 所有集群 可滚动窗口容器
	private JPanel allGroupsPane;// 所有集群
	private JTabbedPane groupsInfoTabbedPane;// 包括“集群成员”、“订阅信息”、“配置新”
	private JPanel groupMember;// 集群成员
	private JPanel subscbs;// 订阅信息面板
	private JTable subsTable;// 订阅信息表
	private DefaultTableModel groupSubsModel;
	private JTextField groupsNameOrIPInput;// “按集群或ip地址查询”输入框
	private JPanel groupConf;// 集群配置信息
	private JPanel policyM;// 策略管理
	private JPanel devConf;// 设备信息
	private JPanel flowConf;// 流量信息
	private JPanel consol;// 控制台
	private JPanel sys;// 系统
	private JPanel currentConf;
	private JPanel defaultConf;
	private JTabbedPane Conf;
	private JLabel defultLabel;
	private JButton applyChangeButton;
	private JPanel basicConf;
	private JPanel panel_1;
	private JPanel basicConf_1;
	private JPanel heartConf;
	private JLabel repAddr;
	private JLabel currentGroupLabel;
	private JTabbedPane currentGroupConf;
	private JButton confirmCurrentG;
	private JTextField repAddrInput;
	private String currentGroup;
	private JPanel basicConfC;
	private JTextField repAddrInputC;
	private JTextField tcpPortInputC;
	private JTextField childrenSizeInputC;
	private JTextField mutltiAddrInputC;
	private JTextField uPortInputC;
	private JTextField joinTimesInputC;
	private JTextField synPeriodInputC;
	private JTextField lostThresholdInputC;
	private JTextField scanPeriodInputC;
	private JTextField sendPeriodInputC;
	private JPanel newPolicy;
	// private JPanel currentPolicy;
	private JLabel newPolicylabel;
	private JLabel currentPolicyLabel;
	private JPanel editPolicy;
	private JScrollPane scrollPane_1;
	// private String selectedTopic;
	private JCheckBox chckbxNewCheckBox;
	private JPanel fbdnGroupsPanel;
	private JButton confirmFbdnGroups;
	private JPanel schemaPanel_2;
	private JLabel lblNewLabel_1;
	private JTable table;
	;
	private JButton currentPolicyReflash;
	private JPanel panel_4;
	private DefaultTableModel currentPolicyTableModel;
	private List<TargetGroup> currentTargetGroups = new ArrayList<TargetGroup>();// 受限集群框内
	// 被选中的集群名称和
	private JPanel fbdnGroups;
	private JPanel topicsPanel;
	private JPanel currentPolicyPanel;
	private JScrollPane subsScrollPane;
	private JLabel sendPeriod;
	private JTextField scanPeriodInput;
	private JLabel lostThreshold;
	private JLabel synPeriod;
	private JTextField synPeriodInput;
	private JTextField lostThresholdInput;
	private JTextField sendPeriodInput;
	private JTextArea sysInfo;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_4;
	private JPanel allGroupsPanel;
	private JScrollPane fbdnGroupsScrollPane;
	private JPanel chooseTopic;
	//	private Jaxe jaxe;
//	private JaxeFrame schemaFrame;
	private JPanel panel;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	private JButton btnNewButton_3;

	// private JList<? extends E> list;

	/**
	 * Create the application.
	 */
	public PSManagerUI(AdminMgr interactIF1) {
		interactIF = interactIF1;
		topicTreeManager = new TopicTreeManager(data, this);
		policyList = ShorenUtils.getAllPolicy();
		JFrame.setDefaultLookAndFeelDecorated(true);
		/**
		 * com.jtattoo.plaf.aluminium.AluminiumLookAndFeel 椭圆按钮+翠绿色按钮背景+金属质感
		 *
		 */
		//
//		try {
//			UIManager
//					.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedLookAndFeelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		// open();
	}

	public PSManagerUI() {

		topicTreeManager = new TopicTreeManager(data, this);
		policyList = ShorenUtils.getAllPolicy();
		open();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					JFrame.setDefaultLookAndFeelDecorated(true);
					/**
					 * com.jtattoo.plaf.aluminium.AluminiumLookAndFeel
					 * 椭圆按钮+翠绿色按钮背景+金属质感
					 *
					 */
					//
					UIManager
							.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
					PSManagerUI window = new PSManagerUI();

					window.open();
					/*
					 * while(true){ //new Thread.sleep(10);
					 * window.text.setText("集群"+"代表地址为"+"端口号"+"注册成功\r\n"); }
					 */
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void open() {
		frame = new JFrame();// 主窗口
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"./res/INT25.png"));
		frame.setResizable(false);
		frame.setTitle("发布订阅管理器");
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);

		tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);// 顶层tab窗体，分为图形化管理，控制台，系统设置等
		tabbedPane_1.setBounds(0, 0, 792, 570);
		tabbedPane_1.setPreferredSize(frame.getSize());
		frame.getContentPane().add(tabbedPane_1);

		visualManagement = new JTabbedPane(JTabbedPane.LEFT);// 图形化管理分页
		visualManagement.setToolTipText("图形化管理");
		tabbedPane_1.addTab("图形化管理", null, visualManagement, null);

		groupsMgmt = new JPanel();// 集群成员窗口
		visualManagement.addTab(
				"",
//				new ImageIcon(PSManagerUI.class
//						.getResource("./res/GroupM.png")),
				new ImageIcon("./res/GroupM.png"),
				groupsMgmt, null);
		groupsMgmt.setLayout(new GridLayout(0, 1, 0, 0));

		allGroupsPanel = new JPanel();
		allGroupsPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		groupsMgmt.add(allGroupsPanel);

		// JPanel panel = new JPanel();
		// panel.setPreferredSize(new Dimension());
		// panel.setSize(new Dimension());
		// panel.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

		allGroupsPane = new JPanel();// 所有集群显示窗口
		FlowLayout flowLayout = (FlowLayout) allGroupsPane.getLayout();
		flowLayout.setAlignment(FlowLayout.LEADING);
		allGroupsPane.setSize(new Dimension(350, 1000));
		allGroupsPane.setPreferredSize(new Dimension(350, 250));// **********************************显示所有集群信息的窗口的大小要根据集群数量动态调整

		// 根据当前当前的集群树，构造集群button

		// JButton btnNewButton = new JButton("100.109.122.122");
		//
		// btnNewButton.setHorizontalTextPosition(SwingConstants.CENTER);
		// btnNewButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		// btnNewButton.setSelected(true);
		// btnNewButton.setToolTipText("G0");
		// btnNewButton.setSelectedIcon(new
		// ImageIcon(PSManagerUI.class.getResource("/com/bupt/wangfu/Swing/./res/01_sys_cskin_btn.png")));
		//
		// btnNewButton.setPreferredSize(new Dimension(122, 60));
		// btnNewButton.setSize(new Dimension(122, 60));
		// btnNewButton.setIcon(new
		// ImageIcon(PSManagerUI.class.getResource("/com/bupt/wangfu/Swing/./res/01_sys_cskin_btn.png")));
		// allGroupsPane.add(btnNewButton);
		// btnNewButton.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		//
		// currentGroup = "G0";
		//
		// //加载集群成员
		// // MsgLookupGroupMember_ groupMem =
		// interactIF.lookupGroupMember(groupName);
		// // for(BrokerUnit temMem: groupMem.members){
		// // String memName = temMem.addr;
		// JButton buttonName1 = new JButton("G0");
		// buttonName1.setToolTipText("G0");
		// buttonName1.setPreferredSize(new Dimension(122, 55));
		// buttonName1.setSize(new Dimension(122, 55));
		// buttonName1.setHorizontalTextPosition(SwingConstants.CENTER);
		// buttonName1.setVerticalTextPosition(SwingConstants.BOTTOM);
		// buttonName1.setSelectedIcon(new
		// ImageIcon(PSManagerUI.class.getResource("/com/bupt/wangfu/Swing/./res/01_sys_cskin_btn.png")));
		// buttonName1.setVerticalTextPosition(SwingConstants.BOTTOM );
		// groupMember.removeAll();
		// groupMember.add(buttonName1);
		// buttonName1.setPreferredSize(new Dimension(95, 55));
		// buttonName1.setSize(new Dimension(80, 50));
		// buttonName1.setIcon(new
		// ImageIcon(PSManagerUI.class.getResource("/com/bupt/wangfu/Swing/./res/01_sys_cskin_btn.png")));
		//
		// //加载集群订阅信息
		// //String []groupSubs =
		// interactIF.lookupGroupSubscriptions(groupName);
		// String []groupSubs =
		// {"alarm:amarm1","test:test1","test:test1","test:test1","test:test1","test:test1"};
		// String[][] groupSubData = new String[groupSubs.length][1];
		//
		// for(int i = 0; i<groupSubs.length; i++){
		// groupSubData[i][0]=groupSubs[i];
		// }
		//
		// String[] columnNames = {"集群" + "G1" +"的订阅"};
		// groupSubsModel = new DefaultTableModel(groupSubData,columnNames);
		// subsTable.setModel(groupSubsModel);
		// //
		//
		// currentGroupLabel.setText("集群"+ currentGroup+ "配置信息");
		//
		// }
		//
		// });
		allGroupsPanel.setLayout(new BorderLayout(0, 0));

		lblNewLabel_4 = new JLabel(
				"\u5F53\u524D\u6240\u6709\u96C6\u7FA4\u4FE1\u606F\uFF08\u70B9\u51FB\u67E5\u8BE2\uFF09");
		allGroupsPanel.add(lblNewLabel_4, BorderLayout.NORTH);
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);

		allGroupsScrollPane = new JScrollPane(allGroupsPane);
		allGroupsPanel.add(allGroupsScrollPane);

		groupsInfoTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		groupsMgmt.add(groupsInfoTabbedPane);

		groupMember = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) groupMember.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEADING);
		groupsInfoTabbedPane.addTab("集群成员", null, groupMember, null);

		subscbs = new JPanel();
		groupsInfoTabbedPane.addTab("订阅信息", null, subscbs, null);
		subscbs.setLayout(null);


		suber = new JTable();
		suber.setVerifyInputWhenFocusTarget(false);
		suber.setOpaque(false);
		suber.setEnabled(false);
		suber.setBounds(0, 0, 428, 1);
//		suber.addMouseListener(new MouseListener() {
////			   private Object groupSubs;
//
//
//			/** *//**
//		      * 鼠标单击事件
//		      * @param e 事件源参数
//		      */
//			 public void mouseClicked(MouseEvent e) {
//			     
//			       if(e.getClickCount()==1||e.getClickCount()==2){//点击几次，这里是双击事件
//			        //加载该该用户订阅
//			    	   int row=suber.rowAtPoint(e.getPoint());
//			    	   String[][] SubData = null;
//			    	   SubData = new String[globalSubInfo.get(currentGroupName).get((String)suber.getValueAt(row,0)).size()][1];
//			    	   for(int k=0; k<globalSubInfo.get(currentGroupName).get((String)suber.getValueAt(row,0)).size();k++){										    		   
//			    		  
//							
//									SubData[k][0] = globalSubInfo.get(currentGroupName).get((String)suber.getValueAt(row,0)).get(k);														
//			    		  
//			    	   }
//			    	   String[] columnNames = {"集群" +currentGroup+"用户"+ suber.getValueAt(row,0) +"的订阅"};
//						groupSubsModel = new DefaultTableModel(SubData,columnNames);
//						subsTable.setModel(groupSubsModel);
//			       }
//			    }
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//			   });


		subsTable = new JTable();
		subsTable.setVerifyInputWhenFocusTarget(false);
		// subsTable.setSize(new Dimension(200, 230));
		// subsTable.setBounds(0, 0, 433, 229);
		// subsTable.setPreferredSize(new Dimension(200, 1000));
		subsTable.setEnabled(false);
		subsTable.setOpaque(false);
		subsScrollPane = new JScrollPane(subsTable);
		subsScrollPane.setSize(new Dimension(430, 240));
		subsScrollPane.setViewportView(subsTable);
		subsScrollPane.setOpaque(false);
		subscbs.add(subsScrollPane);

		JLabel lblNewLabel = new JLabel("集群成员订阅查询\r\n(按ip)");
		lblNewLabel.setBounds(441, 17, 150, 15);
		subscbs.add(lblNewLabel);

		groupsNameOrIPInput = new JTextField();
		groupsNameOrIPInput.setBounds(443, 42, 150, 21);
		groupsNameOrIPInput.setHorizontalAlignment(SwingConstants.LEFT);
		subscbs.add(groupsNameOrIPInput);
		groupsNameOrIPInput.setColumns(10);

		JButton checkButton = new JButton("查询");
		checkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String searchInput = groupsNameOrIPInput.getText();
				if (searchInput.equals("")) {
					JOptionPane.showMessageDialog(null, "输入为空！请输入要查询的集群名称或代表ip");
				} else {

					Map<String, ArrayList<String>> groupSubs = interactIF.lookupMemberSubscriptions(currentGroup, searchInput);
					System.out.println("查询" + "currentGroup:" + currentGroup + "成员" + searchInput + "订阅");
					if (groupSubs != null) {
						int count = 0;
						Set<String> groupsubers = groupSubs.keySet();
						for (String temp : groupsubers) {
							count += groupSubs.get(temp).size();
						}
						String[][] groupSubData = new String[count][1];
//							String[][] groupSubData = new String[groupSubs.length][1];

						for (String temp : groupsubers) {
							ArrayList<String> temptopics = groupSubs.get(temp);
							for (int j = 0; j < temptopics.size(); j++) {
								groupSubData[j][0] = temptopics.get(j);
							}

						}
//						for(int i=0;i<groupSubs.length;i++){
//							
//							groupSubData[i][0] = groupSubs[i];
//							
//						}
//						
//						System.out.println(groupSubs.length);
//						if(groupSubs!=null){
//							for(int i=0;i<groupSubs.length;i++){
//								
//								groupSubData[i][0] = groupSubs[i];
//								
//							}
						//System.arraycopy(groupSubs, 0, groupSubData[0],0, groupSubs.length);
						//	System.arraycopy(src, srcPos, dest, destPos, length)
//						}
						String[] columnNames = {"集群" + currentGroup + "成员" + searchInput + "的订阅"};
						groupSubsModel = new DefaultTableModel(groupSubData, columnNames);
						subsTable.setModel(groupSubsModel);

						//显示查询成员的订阅用户						


					} else {
						JOptionPane.showMessageDialog(null, "该集群无此成员，请先选中集群");
					}
				}
			}
		});
		checkButton.setBounds(498, 73, 93, 23);
		subscbs.add(checkButton);

		groupConf = new JPanel();
		groupsInfoTabbedPane.addTab("配置信息", null, groupConf, null);
		groupConf.setLayout(new GridLayout(1, 0, 5, 0));

		// guocheng
//		this.refreshInfo();
		groupsInfoTabbedPane.addTab("设备信息", null, devConf, "设备信息");
		groupsInfoTabbedPane.addTab("流量信息", null, flowConf, "流量信息");

		// ArrayList<String> list = RestProcess.getMemory();
		// JLabel swilb = new JLabel("交换机：");
		// JLabel swilbtext = new JLabel("00:00:00:00:00:00:00:01");
		// JLabel transmitPackets = new JLabel("发送包数：");
		// JLabel transmitPacketstext = new JLabel("10");
		//
		// flowConf.add(swilb);
		// flowConf.add(swilbtext);
		// flowConf.add(transmitPackets);
		// flowConf.add(transmitPacketstext);

		// 集群配置信息
		currentConf = new JPanel();
		currentConf.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		currentConf.setBounds(new Rectangle(1, 1, 1, 1));

		groupConf.add(currentConf);
		currentConf.setLayout(new BorderLayout(0, 0));

		currentGroupLabel = new JLabel("集群配置");
		currentGroupLabel.setHorizontalAlignment(SwingConstants.CENTER);
		currentGroupLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		currentConf.add(BorderLayout.NORTH, currentGroupLabel);

		currentGroupConf = new JTabbedPane();
		currentGroupConf.setTabPlacement(JTabbedPane.RIGHT);
		currentConf.add(BorderLayout.CENTER, currentGroupConf);

		basicConfC = new JPanel();
		currentGroupConf.addTab("\u57FA\u672C", null, basicConfC, null);
		basicConfC.setLayout(new GridLayout(7, 2, 0, 0));

		JLabel repAddrC = new JLabel("代表地址");
		basicConfC.add(repAddrC);

		repAddrInputC = new JTextField();
		repAddrInputC.setEditable(false);
		repAddrInputC.setEnabled(false);
		repAddrInputC.setOpaque(false);
		repAddrInputC.setToolTipText("系统运行时不得更改");
		repAddrInputC.setHorizontalAlignment(SwingConstants.CENTER);
		basicConfC.add(repAddrInputC);
		repAddrInputC.setColumns(10);

		JLabel tcpPortC = new JLabel("Tcp端口号");
		basicConfC.add(tcpPortC);

		tcpPortInputC = new JTextField();
		tcpPortInputC.setEditable(false);
		tcpPortInputC.setOpaque(false);
		tcpPortInputC.setToolTipText("系统运行时不得更改");
		tcpPortInputC.setHorizontalAlignment(SwingConstants.CENTER);
		basicConfC.add(tcpPortInputC);
		tcpPortInputC.setColumns(10);

		JLabel childrenSizeC = new JLabel("子节点数目");
		basicConfC.add(childrenSizeC);

		childrenSizeInputC = new JTextField();
		childrenSizeInputC.setHorizontalAlignment(SwingConstants.CENTER);
		basicConfC.add(childrenSizeInputC);
		childrenSizeInputC.setColumns(10);

		JLabel mutltiAddrC = new JLabel("组播地址");
		basicConfC.add(mutltiAddrC);

		mutltiAddrInputC = new JTextField();
		mutltiAddrInputC.setHorizontalAlignment(SwingConstants.CENTER);
		basicConfC.add(mutltiAddrInputC);
		mutltiAddrInputC.setColumns(10);

		JLabel uPortC = new JLabel("组播端口号");
		basicConfC.add(uPortC);

		uPortInputC = new JTextField();
		uPortInputC.setHorizontalAlignment(SwingConstants.CENTER);
		basicConfC.add(uPortInputC);
		uPortInputC.setColumns(10);

		JLabel joinTimesC = new JLabel("加入重试次数");
		basicConfC.add(joinTimesC);

		joinTimesInputC = new JTextField();
		joinTimesInputC.setHorizontalAlignment(SwingConstants.CENTER);

		basicConfC.add(joinTimesInputC);
		joinTimesInputC.setColumns(10);

		JLabel synPeriodC = new JLabel("订阅同步周期（min）");
		basicConfC.add(synPeriodC);

		synPeriodInputC = new JTextField();
		synPeriodInputC.setEditable(false);
		synPeriodInputC.setOpaque(false);
		synPeriodInputC.setToolTipText("系统运行时不得更改");
		synPeriodInputC.setHorizontalAlignment(SwingConstants.CENTER);
		basicConfC.add(synPeriodInputC);
		synPeriodInputC.setColumns(10);

		heartConf = new JPanel();
		currentGroupConf.addTab("\u5FC3\u8DF3", null, heartConf, null);
		heartConf.setLayout(new GridLayout(7, 2, 0, 0));

		JLabel lostThresholdC = new JLabel("判断失效阀值:");
		heartConf.add(lostThresholdC);

		lostThresholdInputC = new JTextField();
		lostThresholdInputC.setEditable(false);
		lostThresholdInputC.setOpaque(false);
		lostThresholdInputC.setToolTipText("系统运行时不得更改");
		lostThresholdInputC.setSize(new Dimension(20, 20));
		lostThresholdInputC.setHorizontalAlignment(SwingConstants.CENTER);
		heartConf.add(lostThresholdInputC);
		lostThresholdInputC.setColumns(10);

		JLabel scanPeriodC = new JLabel("扫描周期:");
		heartConf.add(scanPeriodC);

		scanPeriodInputC = new JTextField();
		scanPeriodInputC.setEditable(false);
		scanPeriodInputC.setOpaque(false);
		scanPeriodInputC.setToolTipText("系统运行时不得更改");
		scanPeriodInputC.setHorizontalAlignment(SwingConstants.CENTER);
		heartConf.add(scanPeriodInputC);
		scanPeriodInputC.setColumns(10);

		JLabel sendPeriodC = new JLabel("发送周期:");
		heartConf.add(sendPeriodC);

		sendPeriodInputC = new JTextField();
		sendPeriodInputC.setEditable(false);
		sendPeriodInputC.setOpaque(false);
		sendPeriodInputC.setToolTipText("系统运行时不得更改");
		sendPeriodInputC.setHorizontalAlignment(SwingConstants.CENTER);
		heartConf.add(sendPeriodInputC);
		sendPeriodInputC.setColumns(10);

		confirmCurrentG = new JButton("应用更改");
		confirmCurrentG.setAlignmentX(Component.CENTER_ALIGNMENT);
		currentConf.add(BorderLayout.SOUTH, confirmCurrentG);
		confirmCurrentG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				GroupConfiguration svGroup = new GroupConfiguration();
				MsgConf_ conf = new MsgConf_();

				svGroup.GroupName = currentGroup;
				svGroup.repAddr = repAddrInputC.getText();
				svGroup.tPort = Integer.parseInt(tcpPortInputC.getText());
				svGroup.childrenSize = Integer.parseInt(childrenSizeInputC
						.getText());
				svGroup.mutltiAddr = mutltiAddrInputC.getText();
				svGroup.uPort = Integer.parseInt(uPortInputC.getText());
				svGroup.joinTimes = Integer.parseInt(joinTimesInputC.getText());
				svGroup.synPeriod = Integer.parseInt(synPeriodInputC.getText());
				svGroup.lostThreshold = Long.parseLong(lostThresholdInputC
						.getText());
				svGroup.scanPeriod = Long.parseLong(scanPeriodInputC.getText());
				svGroup.sendPeriod = Long.parseLong(sendPeriodInputC.getText());

				conf.repAddr = repAddrInputC.getText();
				conf.tPort = Integer.parseInt(tcpPortInputC.getText());
				conf.neighborSize = Integer.parseInt(childrenSizeInputC
						.getText());
				conf.multiAddr = mutltiAddrInputC.getText();
				conf.uPort = Integer.parseInt(uPortInputC.getText());
				conf.joinTimes = Integer.parseInt(joinTimesInputC.getText());
				conf.synPeriod = Long.parseLong(synPeriodInputC.getText());
				conf.lostThreshold = Long.parseLong(lostThresholdInputC
						.getText());
				conf.scanPeriod = Long.parseLong(scanPeriodInputC.getText());
				conf.sendPeriod = Long.parseLong(sendPeriodInputC.getText());

				configFile.WriteGroupConfiguration(svGroup);
				interactIF.setConfiguration(currentGroup, conf);
				JOptionPane.showMessageDialog(null, "保存成功");
				// MessageDialog.openInformation(shell, "", "保存成功");

			}
		});

		// 默认配置信息
		defaultConf = new JPanel();
		defaultConf.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		defaultConf.setOpaque(false);
		groupConf.add(defaultConf);
		defaultConf.setLayout(new BorderLayout(0, 0));

		defultLabel = new JLabel("默认配置信息");//
		defultLabel.setHorizontalAlignment(SwingConstants.CENTER);
		defultLabel.setToolTipText("默认配置信息");
		defultLabel.setVerticalAlignment(SwingConstants.TOP);
		defaultConf.add(BorderLayout.NORTH, defultLabel);

		Conf = new JTabbedPane();
		Conf.setTabPlacement(JTabbedPane.RIGHT);
		defaultConf.add(BorderLayout.CENTER, Conf);

		basicConf_1 = new JPanel();
		Conf.addTab("\u57FA\u672C", null, basicConf_1, null);
		basicConf_1.setLayout(new GridLayout(7, 2, 0, 0));

		repAddr = new JLabel("代表地址");
		basicConf_1.add(repAddr);

		repAddrInput = new JTextField();
		repAddrInput.setHorizontalAlignment(SwingConstants.CENTER);
		basicConf_1.add(repAddrInput);
		repAddrInput.setColumns(10);

		JLabel tcpPort = new JLabel("Tcp端口号");
		basicConf_1.add(tcpPort);

		final JTextField tcpPortInput = new JTextField();
		tcpPortInput.setHorizontalAlignment(SwingConstants.CENTER);
		basicConf_1.add(tcpPortInput);
		tcpPortInput.setColumns(10);

		JLabel childrenSize = new JLabel("子节点数目");
		basicConf_1.add(childrenSize);

		final JTextField childrenSizeInput = new JTextField();
		childrenSizeInput.setHorizontalAlignment(SwingConstants.CENTER);
		basicConf_1.add(childrenSizeInput);
		childrenSizeInput.setColumns(10);

		JLabel mutltiAddr = new JLabel("组播地址");
		basicConf_1.add(mutltiAddr);

		final JTextField mutltiAddrInput = new JTextField();
		mutltiAddrInput.setHorizontalAlignment(SwingConstants.CENTER);
		basicConf_1.add(mutltiAddrInput);
		mutltiAddrInput.setColumns(10);

		JLabel uPort = new JLabel("组播端口号");
		basicConf_1.add(uPort);

		final JTextField uPortInput = new JTextField();
		uPortInput.setHorizontalAlignment(SwingConstants.CENTER);
		basicConf_1.add(uPortInput);
		uPortInput.setColumns(10);

		JLabel joinTimes = new JLabel("加入重试次数");
		basicConf_1.add(joinTimes);

		final JTextField joinTimesInput = new JTextField();
		joinTimesInput.setHorizontalAlignment(SwingConstants.CENTER);
		basicConf_1.add(joinTimesInput);
		joinTimesInput.setColumns(10);

		synPeriod = new JLabel("订阅同步周期（min）");
		basicConf_1.add(synPeriod);

		synPeriodInput = new JTextField();
		synPeriodInput.setHorizontalAlignment(SwingConstants.CENTER);
		basicConf_1.add(synPeriodInput);
		synPeriodInput.setColumns(10);

		heartConf = new JPanel();
		Conf.addTab("\u5FC3\u8DF3", null, heartConf, null);
		heartConf.setLayout(new GridLayout(7, 2, 0, 0));

		lostThreshold = new JLabel("判断失效阀值:");
		heartConf.add(lostThreshold);

		lostThresholdInput = new JTextField();
		lostThresholdInput.setEditable(true);
		lostThresholdInput.setOpaque(true);
		lostThresholdInput.setSize(new Dimension(20, 20));
		lostThresholdInput.setHorizontalAlignment(SwingConstants.CENTER);
		heartConf.add(lostThresholdInput);
		lostThresholdInput.setColumns(10);

		JLabel scanPeriod = new JLabel("扫描周期:");
		heartConf.add(scanPeriod);

		scanPeriodInput = new JTextField();
		scanPeriodInput.setEditable(true);
		scanPeriodInput.setOpaque(true);
		scanPeriodInput.setHorizontalAlignment(SwingConstants.CENTER);
		heartConf.add(scanPeriodInput);
		scanPeriodInput.setColumns(10);

		sendPeriod = new JLabel("发送周期:");
		heartConf.add(sendPeriod);

		sendPeriodInput = new JTextField();

		sendPeriodInput.setHorizontalAlignment(SwingConstants.CENTER);
		heartConf.add(sendPeriodInput);
		sendPeriodInput.setColumns(10);

		// Conf.setLayout(new GridLayout(0, 2, 0, 0));
		// 初始化的时候即显示默认值
		GroupConfiguration initInfo = configFile
				.ReadGroupConfiguration("Default");
		repAddrInput.setText(initInfo.repAddr);
		tcpPortInput.setText("" + initInfo.tPort);
		childrenSizeInput.setText("" + initInfo.childrenSize);
		mutltiAddrInput.setText(initInfo.mutltiAddr);
		uPortInput.setText("" + initInfo.uPort);
		joinTimesInput.setText("" + initInfo.joinTimes);
		synPeriodInput.setText("" + initInfo.synPeriod);
		lostThresholdInput.setText("" + initInfo.lostThreshold);
		scanPeriodInput.setText("" + initInfo.scanPeriod);
		sendPeriodInput.setText("" + initInfo.sendPeriod);

		applyChangeButton = new JButton("应用更改");
		applyChangeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				GroupConfiguration svGroup = new GroupConfiguration();
				MsgConf_ conf = new MsgConf_();

				svGroup.GroupName = "Default";
				svGroup.repAddr = repAddrInput.getText();
				svGroup.tPort = Integer.parseInt(tcpPortInput.getText());
				svGroup.childrenSize = Integer.parseInt(childrenSizeInput
						.getText());
				svGroup.mutltiAddr = mutltiAddrInput.getText();
				svGroup.uPort = Integer.parseInt(uPortInput.getText());
				svGroup.joinTimes = Integer.parseInt(joinTimesInput.getText());
				svGroup.synPeriod = Integer.parseInt(synPeriodInput.getText());
				svGroup.lostThreshold = Long.parseLong(lostThresholdInput
						.getText());
				svGroup.scanPeriod = Long.parseLong(scanPeriodInput.getText());
				svGroup.sendPeriod = Long.parseLong(sendPeriodInput.getText());

				configFile.WriteGroupConfiguration(svGroup);
				JOptionPane.showMessageDialog(null, "保存成功");
				// MessageDialog.openInformation(shell, "", "保存成功");

			}
		});
		defaultConf.add(applyChangeButton, BorderLayout.SOUTH);

		topicTreeM = new JPanel();
		visualManagement
				.addTab("",
						new ImageIcon("./res/TopicTree.png"),
						topicTreeM, null);

		try {
			topicTreeM.add(topicTreeManager.getTreeInstance());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		policyM = new JPanel();
		visualManagement.addTab(
				"",
				new ImageIcon("./res/policy.png"),
				policyM, null);
		policyM.setLayout(new GridLayout(0, 1, 0, 5));

		newPolicy = new JPanel();
		newPolicy.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		newPolicy.setName("\u65B0\u5EFA\u7B56\u7565");
		newPolicy.setAutoscrolls(true);
		policyM.add(newPolicy);
		newPolicy.setLayout(new BorderLayout(0, 0));

		newPolicylabel = new JLabel("\u65B0\u5EFA\u7B56\u7565");
		newPolicylabel.setHorizontalAlignment(SwingConstants.CENTER);
		newPolicy.add(newPolicylabel, BorderLayout.NORTH);

		editPolicy = new JPanel();
		newPolicy.add(editPolicy, BorderLayout.CENTER);
		editPolicy.setLayout(null);

		Enumeration trees = topicTreeManager.lib_root.children();
		Vector<DefaultMutableTreeNode> treesNames = new Vector<DefaultMutableTreeNode>();
		DefaultMutableTreeNode defaultChose = new DefaultMutableTreeNode("All");

		treesNames.add(defaultChose);

		while (trees.hasMoreElements()) {
			// DefaultMutableTreeNode tempTree =
			// (DefaultMutableTreeNode)trees.nextElement();
			treesNames.add((DefaultMutableTreeNode) trees.nextElement());
		}

		chooseTopic = new JPanel();
		chooseTopic.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		chooseTopic.setBounds(10, 10, 602, 65);
		editPolicy.add(chooseTopic);
		chooseTopic.setLayout(null);
		comboBox = new JComboBox(treesNames);
		comboBox.setBounds(10, 5, 124, 45);
		comboBox.setSelectedIndex(0);
		comboBox.setOpaque(false);
		chooseTopic.add(comboBox);
		// comboBox.addItemListener(new ItemListener() {
		// public void itemStateChanged(final ItemEvent e) {
		// if(comboBox_1.getItemCount()>0){
		// comboBox_1.removeAllItems();//先清除
		// }
		// DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode)
		// comboBox.getSelectedItem();
		//
		// if(tempNode!=null){
		// Enumeration tempNodeEnum = tempNode.children();
		// System.out.print("*********************************"+tempNodeEnum.nextElement());
		// Vector<DefaultMutableTreeNode> treesNames = new
		// Vector<DefaultMutableTreeNode>() ;
		// while(tempNodeEnum.hasMoreElements()){
		// DefaultMutableTreeNode tempTree =
		// (DefaultMutableTreeNode)tempNodeEnum.nextElement();
		// comboBox_1.addItem(tempTree);
		//
		// }
		//
		// }
		// }
		// });
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (comboBox_1.getItemCount() > 0) {
					comboBox_1.removeAllItems();// 先清除
				}
				if (comboBox.getSelectedIndex() != 0) {
					// JOptionPane.showMessageDialog( null, "请确认所选的主题是否正确!");
					// }else{
					DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) comboBox
							.getSelectedItem();
					DefaultMutableTreeNode defaultChose = new DefaultMutableTreeNode(
							"All");

					comboBox_1.addItem(defaultChose);
					comboBox_1.setSelectedIndex(0);
					if (tempNode != null) {
						Enumeration tempNodeEnum = tempNode.children();
						System.out.print("*********************************"
								+ comboBox.getSelectedItem().toString());
						// Vector<DefaultMutableTreeNode> treesNames = new
						// Vector<DefaultMutableTreeNode>() ;
						while (tempNodeEnum.hasMoreElements()) {
							DefaultMutableTreeNode tempTree = (DefaultMutableTreeNode) tempNodeEnum
									.nextElement();
							comboBox_1.addItem(tempTree);

						}
					}
					reflashFbdnGroups();
				}
			}
		});

		comboBox.setName("选择主题树");
		comboBox.setToolTipText("选择主题树");
		comboBox.setBorder(BorderFactory.createTitledBorder("选择主题树"));
		comboBox.setEditable(true);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(144, 0, 458, 63);
		chooseTopic.add(scrollPane_1);
		scrollPane_1
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		topicsPanel = new JPanel();
		topicsPanel.setBorder(null);
		scrollPane_1.setViewportView(topicsPanel);
		FlowLayout flowLayout_2 = (FlowLayout) topicsPanel.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEADING);

		// JTree tree = topicTreeManager.getLibTree();
		// tree.setBounds(0, 0, 78, 64);
		// editPolicy.add(tree);

		comboBox_1 = new JComboBox();
		comboBox_1.setPreferredSize(new Dimension(95, 45));
		comboBox_1.setEditable(true);
		comboBox_1.setOpaque(false);
		topicsPanel.add(comboBox_1);
		// comboBox_1.setBounds(31, 10, 138, 50);
		// comboBox.addItem("中国");//利用JComboBox类所提供的addItem()方法，加入一个项目到此JComboBox中。
		comboBox_1.setBorder(BorderFactory.createTitledBorder("选择主题"));
		comboBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboBox_2.getItemCount() > 0) {
					comboBox_2.removeAllItems();// 先清除
				}

				DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) comboBox_1
						.getSelectedItem();
				DefaultMutableTreeNode defaultChose = new DefaultMutableTreeNode(
						"All");

				comboBox_2.addItem(defaultChose);
				comboBox_2.setSelectedIndex(0);

				if (comboBox_1.getSelectedIndex() != 0) {

					if (tempNode != null) {
						Enumeration tempNodeEnum = tempNode.children();

						Vector<DefaultMutableTreeNode> treesNames = new Vector<DefaultMutableTreeNode>();
						while (tempNodeEnum.hasMoreElements()) {
							DefaultMutableTreeNode tempTree = (DefaultMutableTreeNode) tempNodeEnum
									.nextElement();

							comboBox_2.addItem(tempTree);
						}
					}

					// 显示受限集群
					reflashFbdnGroups();
				}
				// if(comboBox_2.getSelectedIndex()==0
				// &&comboBox_1.getSelectedIndex()!=0&&comboBox_1.getSelectedItem()!=
				// null){
				// // selectedTopic =
				// comboBox.getSelectedItem().toString()+comboBox_1.getSelectedItem().toString();
				//
				// currentTopicEntry.setTopicName(comboBox_1.getSelectedItem().toString());
				// currentTopicEntry.setTopicPath("ou="+comboBox_1.getSelectedItem().toString()+",ou="+comboBox.getSelectedItem().toString()+",ou=all_test,dc=wsn,dc=com");
				// WsnPolicyMsg selectedPolicy =
				// ShorenUtils.decodePolicyMsg(currentTopicEntry);
				// if(selectedPolicy != null){
				// List<TargetGroup> targetGroups=
				// selectedPolicy.getTargetGroups();
				// //刷新受限集群面板 并把当前选中主题的受限集群选中
				//
				// for(TargetGroup temp: targetGroups){
				// for(int i =0; i<fbdnGroupsPanel.getComponentCount();i++){
				// JCheckBox group = (JCheckBox)
				// fbdnGroupsPanel.getComponent(i);
				// if(group.getText().equals( temp.getName()))
				// { group.setSelected(true);}
				//
				// fbdnGroupsPanel.repaint();
				// break;
				// }
				//
				// // fbdnGroupsPanel.add(groups);
				// }
				// }else if(comboBox_1.getSelectedIndex()==0){
				//
				//
				// }
				// else{
				// for(int i =0; i<fbdnGroupsPanel.getComponentCount();i++){
				// JCheckBox group = (JCheckBox)
				// fbdnGroupsPanel.getComponent(i);
				// group.setSelected(false);
				// }
				// fbdnGroupsPanel.repaint();
				// }
				// }else if(comboBox_1.getSelectedIndex()==0){
				//
				// // selectedTopic = comboBox.getSelectedItem().toString();
				//
				// currentTopicEntry.setTopicName(comboBox.getSelectedItem().toString());
				// currentTopicEntry.setTopicPath("ou="+comboBox.getSelectedItem().toString()+",ou=all_test,dc=wsn,dc=com");
				// WsnPolicyMsg selectedPolicy =
				// ShorenUtils.decodePolicyMsg(currentTopicEntry);
				// if(selectedPolicy != null){
				// List<TargetGroup> targetGroups=
				// selectedPolicy.getTargetGroups();
				// for(TargetGroup temp: targetGroups){
				// for(int i =0; i<fbdnGroupsPanel.getComponentCount();i++){
				// JCheckBox group = (JCheckBox)
				// fbdnGroupsPanel.getComponent(i);
				// if(group.getText().equals( temp.getName()))
				// group.setSelected(true);
				//
				// fbdnGroupsPanel.repaint();
				// break;
				// }
				// }
				// }
				// else{
				// for(int i =0; i<fbdnGroupsPanel.getComponentCount();i++){
				// JCheckBox group = (JCheckBox)
				// fbdnGroupsPanel.getComponent(i);
				// group.setSelected(false);
				// }
				// fbdnGroupsPanel.repaint();
				// }
				//
				// }
			}
		});

		comboBox_2 = new JComboBox();
		comboBox_2.setEditable(true);
		comboBox_2.setPreferredSize(new Dimension(95, 45));
		comboBox_2.setBorder(BorderFactory.createTitledBorder("选择主题"));
		comboBox_2.setOpaque(false);
		topicsPanel.add(comboBox_2);

		comboBox_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (comboBox_3.getItemCount() > 0) {
					comboBox_3.removeAllItems();// 先清除
				}
				DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) comboBox_2
						.getSelectedItem();
				DefaultMutableTreeNode defaultChose = new DefaultMutableTreeNode(
						"All");

				comboBox_3.addItem(defaultChose);
				comboBox_3.setSelectedIndex(0);
				if (comboBox_2.getSelectedIndex() != 0) {

					if (tempNode != null) {
						Enumeration tempNodeEnum = tempNode.children();

						Vector<DefaultMutableTreeNode> treesNames = new Vector<DefaultMutableTreeNode>();
						while (tempNodeEnum.hasMoreElements()) {
							DefaultMutableTreeNode tempTree = (DefaultMutableTreeNode) tempNodeEnum
									.nextElement();

							comboBox_3.addItem(tempTree);
						}
					}
					reflashFbdnGroups();
				}
				//
			}
		});

		comboBox_3 = new JComboBox();
		comboBox_3.setPreferredSize(new Dimension(95, 45));
		comboBox_3.setBorder(BorderFactory.createTitledBorder("选择主题"));
		comboBox_3.setEditable(true);
		comboBox_3.setOpaque(false);
		topicsPanel.add(comboBox_3);

		// comboBox_4 = new JComboBox();
		// comboBox_4.setPreferredSize(new Dimension(95, 45));
		// comboBox_4.setBorder(BorderFactory.createTitledBorder("选择主题"));
		// comboBox_4.setEditable(true);
		// comboBox_4.setOpaque(false);
		//
		// comboBox_5 = new JComboBox();
		// comboBox_5.setPreferredSize(new Dimension(95, 45));
		// comboBox_5.setBorder(BorderFactory.createTitledBorder("选择主题"));
		// comboBox_5.setEditable(true);
		// comboBox_5.setOpaque(false);

		comboBox_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboBox_4 != null && comboBox_4.getItemCount() > 0) {
					comboBox_4.removeAllItems();// 先清除
				}
				System.out.println(topicsPanel.getComponentCount());

				DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) comboBox_3
						.getSelectedItem();
				if (topicsPanel.getComponentCount() > 4) {

					topicsPanel.remove(4);
				} else if (topicsPanel.getComponentCount() > 3) {
					topicsPanel.remove(3);

				}

				if (tempNode != null && tempNode.children() != null) {// 当选择某个主题，并且该主题有子主题时，创建第四个选框
					Enumeration tempNodeEnum = tempNode.children();
					comboBox_4 = new JComboBox();
					comboBox_4.setPreferredSize(new Dimension(95, 45));
					comboBox_4.setBorder(BorderFactory
							.createTitledBorder("选择主题"));
					comboBox_4.setEditable(true);
					comboBox_4.setOpaque(false);
					topicsPanel.add(comboBox_4);

					DefaultMutableTreeNode defaultChose = new DefaultMutableTreeNode(
							"All");
					comboBox_4.addItem(defaultChose);
					comboBox_4.setSelectedIndex(0);

					if (comboBox_3.getSelectedIndex() != 0) {

						Vector<DefaultMutableTreeNode> treesNames = new Vector<DefaultMutableTreeNode>();
						while (tempNodeEnum.hasMoreElements()) {
							DefaultMutableTreeNode tempTree = (DefaultMutableTreeNode) tempNodeEnum
									.nextElement();

							comboBox_4.addItem(tempTree);
						}
						comboBox_4.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent e) {
								if (comboBox_5 != null
										&& comboBox_5.getItemCount() > 0) {
									comboBox_5.removeAllItems();// 先清除
								}
								DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) comboBox_4
										.getSelectedItem();
								if (topicsPanel.getComponentCount() > 4) {
									topicsPanel.remove(4);
								}
								if (tempNode != null
										&& tempNode.children() != null) {// 当选择某个主题，并且该主题有子主题时，创建第四个选框
									Enumeration tempNodeEnum = tempNode
											.children();

									if (comboBox_4.getSelectedIndex() != 0) {
										comboBox_5 = new JComboBox();
										comboBox_5
												.setPreferredSize(new Dimension(
														95, 45));
										comboBox_5.setBorder(BorderFactory
												.createTitledBorder("选择主题"));
										comboBox_5.setEditable(true);
										comboBox_5.setOpaque(false);
										topicsPanel.add(comboBox_5);
										DefaultMutableTreeNode defaultChose = new DefaultMutableTreeNode(
												"All");
										comboBox_5.addItem(defaultChose);
										comboBox_5.setSelectedIndex(0);

										Vector<DefaultMutableTreeNode> treesNames = new Vector<DefaultMutableTreeNode>();
										while (tempNodeEnum.hasMoreElements()) {
											DefaultMutableTreeNode tempTree = (DefaultMutableTreeNode) tempNodeEnum
													.nextElement();
											comboBox_5.addItem(tempTree);
										}
										comboBox_5
												.addActionListener(new ActionListener() {

													public void actionPerformed(
															ActionEvent e) {
														System.out
																.println("已达5级主题");

													}
												});
									}
								}

							}
						});
					}
					reflashFbdnGroups();

				}
				// if(comboBox_4.getSelectedIndex()==0&&comboBox_3.getSelectedIndex()!=0&&comboBox_3.getSelectedItem()!=
				// null){
				// // selectedTopic =
				// comboBox.getSelectedItem().toString()+comboBox_1.getSelectedItem().toString()+comboBox_2.getSelectedItem().toString()+comboBox_3.getSelectedItem().toString();
				//
				// currentTopicEntry.setTopicName(comboBox_3.getSelectedItem().toString());
				// currentTopicEntry.setTopicPath("ou="+comboBox_3.getSelectedItem().toString()+",ou="+comboBox_2.getSelectedItem().toString()+",ou="+comboBox_1.getSelectedItem().toString()+",ou="+comboBox.getSelectedItem().toString()+",ou=all_test,dc=wsn,dc=com");
				// WsnPolicyMsg selectedPolicy =
				// ShorenUtils.decodePolicyMsg(currentTopicEntry);
				// if(selectedPolicy != null){
				// List<TargetGroup> targetGroups=
				// selectedPolicy.getTargetGroups();
				// for(TargetGroup temp: targetGroups){
				// for(int i =0; i<fbdnGroupsPanel.getComponentCount();i++){
				// JCheckBox group = (JCheckBox)
				// fbdnGroupsPanel.getComponent(i);
				// if(group.getText().equals( temp.getName()))
				// group.setSelected(true);
				//
				// fbdnGroupsPanel.repaint();
				// break;
				// }
				//
				// // fbdnGroupsPanel.add(groups);
				// }
				// }else{
				// for(int i =0; i<fbdnGroupsPanel.getComponentCount();i++){
				// JCheckBox group = (JCheckBox)
				// fbdnGroupsPanel.getComponent(i);
				// group.setSelected(false);
				// }
				// fbdnGroupsPanel.repaint();
				// }
				// }
			}
		});

		// comboBox_4 = new JComboBox();
		// comboBox_4.setPreferredSize(new Dimension(95, 45));
		// comboBox_4.setBorder(BorderFactory.createTitledBorder("选择主题"));
		//

		// schemaPanel_2 = new JPanel();
		// schemaPanel_2.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		// schemaPanel_2.setBounds(308, 87, 256, 151);
		// editPolicy.add(schemaPanel_2);
		// schemaPanel_2.setLayout(new BorderLayout(0, 0));
		//
		// lblNewLabel_1 = new JLabel("SCHEMA");
		// lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		// schemaPanel_2.add(lblNewLabel_1, BorderLayout.NORTH);

		// 受限集群总面板
		fbdnGroups = new JPanel();
		fbdnGroups.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		fbdnGroups.setBounds(10, 87, 239, 151);

		editPolicy.add(fbdnGroups);
		fbdnGroups.setLayout(new BorderLayout(0, 0));

		JLabel label = new JLabel("受限集群（仅在线）");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		fbdnGroups.add(label, BorderLayout.NORTH);

		fbdnGroupsPanel = new JPanel();
		fbdnGroupsScrollPane = new JScrollPane(fbdnGroupsPanel);
		fbdnGroupsPanel.setPreferredSize(new Dimension(230, 80));
		fbdnGroupsScrollPane.setPreferredSize(new Dimension(230, 80));
		fbdnGroupsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		fbdnGroups.add(fbdnGroupsScrollPane, BorderLayout.CENTER);
		// 默认加载所有集群 且处于未选中状态
		// ConcurrentHashMap<String,GroupUnit> tem=AdminBase.groups;
		if (AdminMgr.groups != null) {
			Iterator itr = AdminMgr.groups.keySet().iterator();
			while (itr.hasNext()) {
				final String groupName = (String) itr.next();
				final JCheckBox groups = new JCheckBox(groupName);
				groups.setSelected(false);
				groups.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						if (groups.isSelected()) {
							TargetGroup tem = new TargetGroup(groupName);
							currentTargetGroups.add(tem);
						} else {
							for (TargetGroup temTG : currentTargetGroups) {
								if (temTG.getName() == groupName) {

									currentTargetGroups.remove(temTG);
									break;
								}
							}
						}
						System.out
								.print("currentGroups:" + currentTargetGroups);

					}
				});
				fbdnGroupsPanel.add(groups);
				// groups.addItemListener(l);
			}
		}

		confirmFbdnGroups = new JButton("应用更改");
		confirmFbdnGroups.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// 使策略变更生效

				// 获得受限集群面板中选中的集群
				WsnPolicyMsg currentPolicy = new WsnPolicyMsg();
				// currentPolicy.setTargetGroups(currentTargetGroups);
				List<TargetGroup> currentChosenTargetGroups = new ArrayList<TargetGroup>();

				for (int i = 0; i < fbdnGroupsPanel.getComponentCount(); i++) {
					JCheckBox topicsChoosed = (JCheckBox) fbdnGroupsPanel
							.getComponent(i);
					if (topicsChoosed.isSelected()) {
						TargetGroup currentChosenTargetGroup = new TargetGroup(
								topicsChoosed.getText());
						currentChosenTargetGroups.add(currentChosenTargetGroup);
					}
				}

				currentPolicy.setTargetGroups(currentChosenTargetGroups);

				// 获得主题树中选中主题
				String selectedTopic = comboBox.getSelectedItem().toString();

				TopicEntry currentChosenTopicEntry = new TopicEntry();

				currentChosenTopicEntry.setTopicName(selectedTopic);


				String chosenTopicPath = "ou=" + selectedTopic
						+ ",ou=all_test,dc=wsn,dc=com";
				try {
					currentChosenTopicEntry.setTopicCode(ShorenUtils.ldap
							.getByDN(chosenTopicPath).getTopicCode());
				} catch (NamingException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				for (int i = 0; i < topicsPanel.getComponentCount(); i++) {
					JComboBox topicsChoosed = (JComboBox) topicsPanel
							.getComponent(i);
					if (topicsChoosed.getSelectedIndex() != 0
							&& topicsChoosed.getSelectedItem() != null) {
						selectedTopic = selectedTopic + ":"
								+ topicsChoosed.getSelectedItem().toString();

						currentChosenTopicEntry.setTopicName(topicsChoosed
								.getSelectedItem().toString());
						chosenTopicPath = "ou="
								+ topicsChoosed.getSelectedItem().toString()
								+ "," + chosenTopicPath;

						try {
							currentChosenTopicEntry
									.setTopicCode(ShorenUtils.ldap.getByDN(
											chosenTopicPath).getTopicCode());
						} catch (NamingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else
						break;
				}
				currentPolicy.setTargetTopic(selectedTopic);
				currentChosenTopicEntry.setWsnpolicymsg(currentPolicy);
				currentChosenTopicEntry.setTopicPath(chosenTopicPath);

				// 使策略生效
				if (selectedTopic == null) {
					JOptionPane.showMessageDialog(null, "请选择主题!");
				} else if (currentChosenTopicEntry != null
						&& currentChosenTopicEntry.getTopicPath() != null) {

					ShorenUtils.encodePolicyMsg(currentChosenTopicEntry);
					JOptionPane.showMessageDialog(null, "策略设置成功!");
				} else
					JOptionPane.showMessageDialog(null, "请确认所选的主题是否正确!");
				// selectedTopic = null;
			}
		});
		fbdnGroups.add(confirmFbdnGroups, BorderLayout.SOUTH);

		currentPolicyPanel = new JPanel();
		currentPolicyPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		currentPolicyPanel.setOpaque(false);
		policyM.add(currentPolicyPanel);
		currentPolicyPanel.setLayout(new BorderLayout(0, 0));

		panel_4 = new JPanel();
		currentPolicyPanel.add(panel_4, BorderLayout.NORTH);
		panel_4.setLayout(new FlowLayout(FlowLayout.TRAILING, 5, 5));

		currentPolicyLabel = new JLabel("\u5F53\u524D\u7B56\u7565");
		currentPolicyLabel.setPreferredSize(new Dimension(500, 15));
		panel_4.add(currentPolicyLabel);
		currentPolicyLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		currentPolicyLabel.setHorizontalAlignment(SwingConstants.CENTER);

		currentPolicyReflash = new JButton("刷新");
		currentPolicyReflash.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				reflashCurrentPolicy();
			}
		});
		panel_4.add(currentPolicyReflash);

		// 初始化当前所有策略信息
		String[][] tPolicy = new String[policyList.size()][2];
		String[] columnNames = {"主题", "对应策略"};

		for (int i = 0; i < policyList.size(); i++) {
			tPolicy[i][0] = policyList.get(i).getTargetTopic();
			tPolicy[i][1] = policyList.get(i).getTargetGroups().toString();

		}
		System.out.println(tPolicy);
		currentPolicyTableModel = new DefaultTableModel(tPolicy, columnNames) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable();
		// currentPolicy.add(table, BorderLayout.SOUTH);
		table.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		table.setModel(currentPolicyTableModel);
		table.setOpaque(false);
		JScrollPane currentPolicyScrollPane = new JScrollPane(table);
		currentPolicyScrollPane.setOpaque(false);

		table.setPreferredScrollableViewportSize(new Dimension(600, 500));
		currentPolicyPanel.add(currentPolicyScrollPane, BorderLayout.CENTER);

		// reflashCurrentPolicy();
		consol = new JPanel();
		tabbedPane_1.addTab("控制台", null, consol, null);
		consol.setLayout(new GridLayout(0, 1, 0, 0));

		text = new JTextArea();
		text.setOpaque(false);
		text.setLineWrap(true);
		text.setEditable(false);
		consol.add(text);

		sys = new JPanel();
		sys.setLayout(null);
		sysInfo = new JTextArea();
		sysInfo.setBounds(214, 87, 351, 227);
		sys.add(sysInfo);
		sysInfo.setOpaque(false);
		sysInfo.setLineWrap(true);
		sysInfo.setEditable(false);
		sysInfo.setText("                \u7F51\u670D\u4E2D\u5FC3\u51FA\u54C1\uFF01\r\n\r\n\u8DEF\u7531\u62D3\u6251\uFF1A\u738B\u53CC\u9526  \u670D\u52A1\u63A5\u53E3\uFF1A\u90ED\u6210  \u6570\u636E\u8F6C\u53D1\uFF1A\u9648\u5929\u5B87\r\n\r\n   \u7BA1\u7406\u5458\uFF1A\u5434\u601D\u9F50\uFF08\u7B2C\u4E00\u7248\uFF09\uFF0C\u81E7\u4E9A\u5F3A\uFF08\u7B2C\u4E8C\u7248\uFF09\r\n\r\n          \u7F51\u7EDC\u6280\u672F\u7814\u7A76\u9662  \u5317\u4EAC\u90AE\u7535\u5927\u5B66");
		tabbedPane_1.addTab("系统Info", null, sys, null);

//		JPanel graph = new OfficeDemo();
//		tabbedPane_1.addTab("拓扑图", null, graph, null);
//		graph.setLayout(new GridLayout(0, 1, 0, 0));

		// text = new JTextArea();
		// text.setOpaque(false);
		// text.setLineWrap(true);
		// text.setEditable(false);
		// consol.add(text);
		//
		lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setIcon(new ImageIcon("./res/INT100.png"));
		lblNewLabel_2.setBounds(162, 250, 106, 105);
		sys.add(lblNewLabel_2);

		lblNewLabel_3 = new JLabel("");
		lblNewLabel_3.setIcon(new ImageIcon("./res/bupt.png"));
		lblNewLabel_3.setBounds(290, 246, 532, 110);
		sys.add(lblNewLabel_3);

		// 刷新显示当前集群（第一次）
		reloadAllGroup();

		// schema模块
//
//		final File dir = new File("config");
//		if (!dir.exists()) {
//			JOptionPane.showMessageDialog(null, JaxeResourceBundle.getRB()
//					.getString("erreur.DossierConfig"), JaxeResourceBundle
//					.getRB().getString("config.ErreurLancement"),
//					JOptionPane.ERROR_MESSAGE);
//			System.exit(1);
//		}
//		int nbconf = 0;
//		final File[] liste = dir.listFiles();
//		for (final File element : liste)
//			if (element.getName().endsWith("cfg.xml")
//					|| element.getName().endsWith("config.xml")) {
//				if (nbconf == 0)
//					new Jaxe(element.getPath());
//				nbconf++;
//			}
//		if (nbconf == 0) {
//			JOptionPane.showMessageDialog(null, JaxeResourceBundle.getRB()
//					.getString("config.AucunFichier"), JaxeResourceBundle
//					.getRB().getString("config.ErreurLancement"),
//					JOptionPane.ERROR_MESSAGE);
//			System.exit(1);
//		}
//		Jaxe.ouvrir(new File("schema/test.xsd"), null);
//
//		JPanel schemaPanel = new JPanel();
//		schemaPanel.setLayout(new BorderLayout(0, 0));
//
//		panel = new JPanel();
//		FlowLayout flowLayout_3 = (FlowLayout) panel.getLayout();
//		flowLayout_3.setAlignment(FlowLayout.LEADING);
//		schemaPanel.add(panel, BorderLayout.NORTH);
//
//		btnNewButton = new JButton("打开");
//		btnNewButton.addActionListener(new ActionListener() {
//			private JFileChooser jChooser;
//
//			public void actionPerformed(ActionEvent e) {
//
//				jChooser = new JFileChooser();
//				// 设置默认的打开目录,如果不设的话按照window的默认目录(我的文档)
//				jChooser.setCurrentDirectory(new File("schema/"));
//				// 设置打开文件类型,此处设置成只能选择文件夹，不能选择文件
//				jChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 只能打开文件夹
//				// 打开一个对话框
//				int index = jChooser.showDialog(null, "打开文件");
//				if (index == JFileChooser.APPROVE_OPTION) {
//
//					Jaxe.schemaFrame.ouvrir(jChooser.getSelectedFile());
//
//					// 把获取到的文件的绝对路径显示在文本编辑框中
//					lblNewLabel_5.setText("        当前："
//							+ jChooser.getSelectedFile().getName());
//
//				}
//			}
//		});
//		panel.add(btnNewButton);
//
//		btnNewButton_1 = new JButton("保存");
//		btnNewButton_1.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				Jaxe.schemaFrame.enregistrer();
//
//				// 写入ldap
//
//				// 获得当前xsd的DN
//
//				// String chosenTopicPath
//				// ="ou="+selectedTopic+",ou=all_test,dc=wsn,dc=com";
//				// TopicEntry newSchema =
//				// ShorenUtils.ldap.getByDN(chosenTopicPath);
//			}
//		});
//		panel.add(btnNewButton_1);
//
//		btnNewButton_2 = new JButton("刷新");
//		panel.add(btnNewButton_2);
//		btnNewButton_2.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//
//				// 读取ldap并刷新显示
//
//			}
//		});
//
//		btnNewButton_3 = new JButton("重置");
//		panel.add(btnNewButton_3);
//
//		lblNewLabel_5 = new JLabel("        当前：");
//		lblNewLabel_5.setAutoscrolls(true);
//		lblNewLabel_5.setToolTipText("当前选择");
//		panel.add(lblNewLabel_5);
//		btnNewButton_3.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//
//				// 将当前schema恢复成默认
//				Jaxe.schemaFrame.ouvrir(new File("schema/test.xsd"));
//
//			}
//		});
//		schemaPanel.add(Jaxe.schemaFrame.getContentPane(), BorderLayout.CENTER);
//		visualManagement
//				.addTab("",
//						new ImageIcon(
//								PSManagerUI.class
//										.getResource("/edu/bupt/wangfu/Swing/./res/shcemaM.png")),
//						schemaPanel, null);

	}

	// 根据当前所选的主题，显示受限集群

	public void reflashFbdnGroups() {
		if (comboBox.getSelectedIndex() != 0
				&& comboBox.getSelectedItem() != null
				&& !comboBox.getSelectedItem().toString().equals("null")) {

			String selectedTopic = comboBox.getSelectedItem().toString();
			TopicEntry currentChosenTopicEntry = new TopicEntry();
			String chosenTopicPath = "ou=" + selectedTopic
					+ ",ou=all_test,dc=wsn,dc=com";
			currentChosenTopicEntry.setTopicName(selectedTopic);
			currentChosenTopicEntry.setTopicPath(chosenTopicPath);
			for (int i = 0; i < topicsPanel.getComponentCount(); i++) {
				JComboBox topicsChoosed = (JComboBox) topicsPanel
						.getComponent(i);
				if (topicsChoosed.getSelectedIndex() != 0
						&& topicsChoosed.getSelectedItem() != null) {
					selectedTopic = selectedTopic + ":"
							+ topicsChoosed.getSelectedItem().toString();

					currentChosenTopicEntry.setTopicName(topicsChoosed
							.getSelectedItem().toString());
					chosenTopicPath = "ou="
							+ topicsChoosed.getSelectedItem().toString() + ","
							+ chosenTopicPath;
					currentChosenTopicEntry.setTopicPath(chosenTopicPath);

				} else
					break;
			}

			if (currentChosenTopicEntry != null
					&& currentChosenTopicEntry.getWsnpolicymsg() != null) {
				WsnPolicyMsg selectedPolicy = ShorenUtils
						.decodePolicyMsg(currentChosenTopicEntry);

				List<TargetGroup> targetGroups = selectedPolicy
						.getTargetGroups();
				for (TargetGroup temp : targetGroups) {
					for (int i = 0; i < fbdnGroupsPanel.getComponentCount(); i++) {
						JCheckBox group = (JCheckBox) fbdnGroupsPanel
								.getComponent(i);
						if (group.getText().equals(temp.getName()))
							group.setSelected(true);

						fbdnGroupsPanel.repaint();
						break;
					}
				}
			} else {
				for (int i = 0; i < fbdnGroupsPanel.getComponentCount(); i++) {
					JCheckBox group = (JCheckBox) fbdnGroupsPanel
							.getComponent(i);
					group.setSelected(false);
				}
				fbdnGroupsPanel.repaint();
			}
		}
	}

	// 刷新显示当前策略信息
	public void reflashCurrentPolicy() {

		// currentPolicyPanel.removeAll();

		policyList = ShorenUtils.getAllPolicy();
		// String [][] tPolicy = new String[policyList.size()][2];
		// //String[] columnNames = {"主题","对应策略"};
		// table.removeAll();
		// table.invalidate();
		// // String [][] cPolicy = new String[policyList.size()][2];
		// String[] columnNames = {"主题","对应策略"};
		//
		// for(int i =0; i<policyList.size(); i++){
		// tPolicy[i][0] = policyList.get(i).getTargetTopic();
		// tPolicy[i][1] = policyList.get(i).getTargetGroups().toString();
		//
		//
		// }
		// System.out.println(tPolicy);
		// currentPolicyTableModel = new DefaultTableModel(tPolicy,columnNames);
		//

		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		while (tableModel.getRowCount() > 0) {
			tableModel.removeRow(0);
		}

		// currentPolicyTableModel = new ;
		for (int i = 0; i < policyList.size(); i++) {
			// tPolicy[i][0] = policyList.get(i).getTargetTopic();
			// tPolicy[i][1] = policyList.get(i).getTargetGroups().toString();

			tableModel.addRow(new Object[]{
					policyList.get(i).getTargetTopic(),
					policyList.get(i).getTargetGroups().toString()});

		}

		table.invalidate();
		// currentPolicyPanel.setLayout(null);
		// table = new JTable(tPolicy,columnNames);
		// table.setBounds(10, 5, 483, 225);
		// table.
		// currentPolicyPanel.add(table);
	}

	//	@Override
	public void newGroup(final GroupUnit newGroup) {
		// TODO Auto-generated method stub
		GroupInfo aGroupItem = new GroupInfo();
		/*
		 * aGroupItem.GroupAddress=address; aGroupItem.GroupName=name;
		 * aGroupItem.date=new Date();
		 */
		aGroupItem.GroupAddress = newGroup.addr;
		aGroupItem.GroupName = newGroup.name;
		aGroupItem.date = newGroup.date;
		aGroupItem.port = newGroup.tPort;
		int stat = data.addGroup(aGroupItem);

		if (stat == -1) {
			text.append(new Date() + "已经存在名为" + newGroup.name + "的集群，无法注册"
					+ "\r\n");
			text.paintImmediately(text.getBounds());
			// lblEvent.setText("集群"+newGroup.name+"注册失败");
		} else {
			if (stat == 1) {
				// Display.getDefault().asyncExec(new Runnable() {
				//
				// @Override
				// public void run() {
				// TODO Auto-generated method stub
				text.append(new Date() + "集群" + newGroup.name + "代表地址为"
						+ newGroup.addr + "端口号" + newGroup.tPort + "注册成功"
						+ "\r\n");
				text.paintImmediately(text.getBounds());
				// lblEvent.setText("集群"+newGroup.name+":"+newGroup.addr+"注册成功");
				// }
				// });

				if (configFile.LookForConfigFile(aGroupItem.GroupName) == null) {

					GroupConfiguration defaultConfig = new GroupConfiguration();
					defaultConfig = configFile
							.ReadGroupConfiguration("Default");
					defaultConfig.GroupName = aGroupItem.GroupName;
					defaultConfig.repAddr = newGroup.addr;
					defaultConfig.tPort = newGroup.tPort;
					configFile.WriteGroupConfiguration(defaultConfig);
				} else {

				}
			} else {
				System.out.println("$$$$$$$$$$$$$$$$");
				// Display.getDefault().asyncExec(new Runnable() {
				//
				// @Override
				// public void run() {
				// TODO Auto-generated method stub
				text.append(new Date() + "注册失败，未知错误!" + "\r\n");
				text.paintImmediately(text.getBounds());
				// lblEvent.setText("注册失败");
				// }
				// });
			}
		}

	}

	public void reflashJtreeRoot() {

		// comboBox.removeAllItems();
		// Enumeration trees = topicTreeManager.lib_root.children();
		// // Vector<DefaultMutableTreeNode> treesNames = new
		// Vector<DefaultMutableTreeNode>() ;
		// DefaultMutableTreeNode defaultChose = new
		// DefaultMutableTreeNode("All");
		//
		// comboBox.addItem(defaultChose);
		//
		//
		// while(trees.hasMoreElements()){
		// //DefaultMutableTreeNode tempTree =
		// (DefaultMutableTreeNode)trees.nextElement();
		// DefaultMutableTreeNode temp =
		// (DefaultMutableTreeNode)trees.nextElement();
		//
		// comboBox.addItem(temp);
		// }
		// comboBox.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		//
		// if(comboBox_1.getItemCount()>0){
		// comboBox_1.removeAllItems();//先清除
		// }
		// if(comboBox.getSelectedItem() instanceof String){
		// JOptionPane.showMessageDialog( null, "请确认所选的主题是否正确!");
		// }else{
		// DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode)
		// comboBox.getSelectedItem();
		// DefaultMutableTreeNode defaultChose = new
		// DefaultMutableTreeNode("All");
		//
		// comboBox_1.addItem(defaultChose);
		// comboBox_1.setSelectedIndex(0);
		// if(tempNode!=null){
		// Enumeration tempNodeEnum = tempNode.children();
		// System.out.print("*********************************"+comboBox.getSelectedItem().toString());
		// //Vector<DefaultMutableTreeNode> treesNames = new
		// Vector<DefaultMutableTreeNode>() ;
		// while(tempNodeEnum.hasMoreElements()){
		// DefaultMutableTreeNode tempTree =
		// (DefaultMutableTreeNode)tempNodeEnum.nextElement();
		// comboBox_1.addItem(tempTree);
		//
		// }
		// }
		// reflashFbdnGroups();
		// }
		// }
		// });
		// comboBox.repaint();
		// comboBox.setSelectedIndex(0);
		// comboBox_1.setSelectedIndex(0);
		// comboBox_2.setSelectedIndex(0);
		// comboBox_3.setSelectedIndex(0);
		// comboBox_4.setSelectedIndex(0);
		// comboBox.
		// JPanel chooseTopic = new JPanel();
		// chooseTopic.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		// chooseTopic.setBounds(10, 10, 602, 65);
		// editPolicy.add(chooseTopic);
		// chooseTopic.setLayout(null);
		// comboBox.removeAll();
		// comboBox.add(treesNames);
		Enumeration trees = topicTreeManager.lib_root.children();
		Vector<DefaultMutableTreeNode> treesNames = new Vector<DefaultMutableTreeNode>();
		DefaultMutableTreeNode defaultChose = new DefaultMutableTreeNode("All");

		treesNames.add(defaultChose);

		while (trees.hasMoreElements()) {
			// DefaultMutableTreeNode tempTree =
			// (DefaultMutableTreeNode)trees.nextElement();
			treesNames.add((DefaultMutableTreeNode) trees.nextElement());
		}

		// chooseTopic = new JPanel();
		// chooseTopic.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		// chooseTopic.setBounds(10, 10, 602, 65);
		// editPolicy.add(chooseTopic);
		// chooseTopic.setLayout(null);
		chooseTopic.remove(comboBox);
		comboBox = new JComboBox(treesNames);
		comboBox.setBounds(10, 5, 124, 45);
		comboBox.setSelectedIndex(0);
		comboBox.setOpaque(false);
		chooseTopic.add(comboBox);

		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (comboBox_1.getItemCount() > 0) {
					comboBox_1.removeAllItems();// 先清除
				}
				if (comboBox.getSelectedItem() instanceof String) {
					JOptionPane.showMessageDialog(null, "请确认所选的主题是否正确!");
				} else {
					DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) comboBox
							.getSelectedItem();
					DefaultMutableTreeNode defaultChose = new DefaultMutableTreeNode(
							"All");

					comboBox_1.addItem(defaultChose);
					comboBox_1.setSelectedIndex(0);
					if (tempNode != null) {
						Enumeration tempNodeEnum = tempNode.children();
						System.out.print("*********************************"
								+ comboBox.getSelectedItem().toString());
						// Vector<DefaultMutableTreeNode> treesNames = new
						// Vector<DefaultMutableTreeNode>() ;
						while (tempNodeEnum.hasMoreElements()) {
							DefaultMutableTreeNode tempTree = (DefaultMutableTreeNode) tempNodeEnum
									.nextElement();
							comboBox_1.addItem(tempTree);

						}
					}
					reflashFbdnGroups();
				}
			}
		});

		comboBox.setName("选择主题树");
		comboBox.setToolTipText("选择主题树");
		comboBox.setBorder(BorderFactory.createTitledBorder("选择主题树"));
		comboBox.setEditable(true);
		chooseTopic.repaint();

	}

	public void recoverGroup(final GroupUnit recoverGroup) {
		// TODO Auto-generated method stub
		GroupInfo aGroupItem = new GroupInfo();

		aGroupItem.GroupAddress = recoverGroup.addr;
		aGroupItem.GroupName = recoverGroup.name;
		aGroupItem.date = recoverGroup.date;
		aGroupItem.port = recoverGroup.tPort;
		int stat = data.addGroup(aGroupItem);

		if (stat == -1) {
			text.append(new Date() + "已经存在名为" + recoverGroup.name + "的集群，无法注册"
					+ "\r\n");
			text.paintImmediately(text.getBounds());
			// lblEvent.setText("集群"+recoverGroup.name+"注册失败");
		} else {
			if (stat == 1) {
				// Display.getDefault().asyncExec(new Runnable() {
				//
				// @Override
				// public void run() {
				// TODO Auto-generated method stub
				text.append(new Date() + "集群" + recoverGroup.name + "代表地址为"
						+ recoverGroup.addr + "端口号" + recoverGroup.tPort
						+ "恢复成功" + "\r\n");
				text.paintImmediately(text.getBounds());
				// lblEvent.setText("集群"+recoverGroup.name+":"+recoverGroup.addr+"恢复成功");
				// }
				// });

				if (configFile.LookForConfigFile(aGroupItem.GroupName) == null) {

					GroupConfiguration defaultConfig = new GroupConfiguration();
					defaultConfig = configFile
							.ReadGroupConfiguration("Default");
					defaultConfig.GroupName = aGroupItem.GroupName;
					defaultConfig.repAddr = recoverGroup.addr;
					defaultConfig.tPort = recoverGroup.tPort;
					configFile.WriteGroupConfiguration(defaultConfig);
				} else {

				}

			} else {
				// Display.getDefault().asyncExec(new Runnable() {
				//
				// @Override
				// public void run() {
				// TODO Auto-generated method stub
				text.append(new Date() + "恢复失败，未知错误!" + "\r\n");
				text.paintImmediately(text.getBounds());
				// lblEvent.setText("恢复失败");
				// }
				// });
			}
		}

		reloadAllGroup();

	}

	//	@Override
	public void removeGroup(final String name, String address) {
		// TODO Auto-generated method stub
		int stat = data.removeGroup(name);
		if (stat == -1) {
			// Display.getDefault().asyncExec(new Runnable() {
			//
			// @Override
			// public void run() {
			// TODO Auto-generated method stub
			text.append(new Date() + "不存在名为" + name + "的集群，无法完成注销" + "\r\n");
			text.paintImmediately(text.getBounds());
			// lblEvent.setText("集群"+name+"注销失败");
			// }
			// });
			//
		} else {
			if (stat == 1) {
				// Display.getDefault().asyncExec(new Runnable() {
				//
				// @Override
				// public void run() {
				// TODO Auto-generated method stub
				text.append(new Date() + "集群" + name + "已经被成功删除" + "\r\n");
				text.paintImmediately(text.getBounds());
				// allGroupsPane.get

				// lblEvent.setText("集群"+name+"注销成功");
				// }
				// });
				//
			} else {
				// Display.getDefault().asyncExec(new Runnable() {
				//
				// @Override
				// public void run() {
				// TODO Auto-generated method stub
				text.append(new Date() + "删除失败,未知错误" + "\r\n");
				text.paintImmediately(text.getBounds());
				// lblEvent.setText("注销失败");
				// }
				// });

			}
		}
		reloadAllGroup();

	}

	public void printAllGroup() {
		List<GroupInfo> curGroup = new ArrayList<GroupInfo>();
		curGroup = data.getAllGroup();
		Iterator<GroupInfo> itr = curGroup.iterator();
		text.append(new Date() + "当前所有集群的信息如下：" + "\r\n");
		while (itr.hasNext()) {
			GroupInfo tmpItem = itr.next();

			text.append("组名称:" + tmpItem.GroupName + "组代表地址："
					+ tmpItem.GroupAddress + "\r\n");
			text.paintImmediately(text.getBounds());
		}
	}

	public void reloadAllGroup() {

		//清空所有集群面板和受限集群面板

		if (allGroupsPane != null) allGroupsPane.removeAll();
		if (fbdnGroupsPanel != null) fbdnGroupsPanel.removeAll();

		if (interactIF.groups != null && interactIF.groups.size() > 0) {

			Iterator itr = AdminMgr.groups.keySet().iterator();
			while (itr.hasNext()) {//遍历当前所有集群
				final String groupName = (String) itr.next();

				final JButton buttonName1 = new JButton(groupName);

				buttonName1.setToolTipText(groupName);
				buttonName1.setSelectedIcon(new ImageIcon("./res/01_sys_cskin_btn.png"));

				buttonName1.setPreferredSize(new Dimension(80, 80));
				buttonName1.setSize(new Dimension(80, 80));
				buttonName1.setHorizontalTextPosition(SwingConstants.CENTER);
				buttonName1.setVerticalTextPosition(SwingConstants.BOTTOM);
				allGroupsPane.add(buttonName1);
				buttonName1.setIcon(new ImageIcon("./res/01_sys_cskin_btn.png"));
				buttonName1.addActionListener(new ActionListener() {
					private DefaultTableModel memSubsModel;
					private String[][] memsubs;
					private ConcurrentHashMap<String, ArrayList<String>> groupSubs;

					public void actionPerformed(ActionEvent e) {

						currentGroupName = groupName;

						currentGroup = buttonName1.getText();
						groupsInfoTabbedPane.setTitleAt(0, ("集群" + currentGroup + "成员"));
						groupsInfoTabbedPane.setTitleAt(1, ("集群" + currentGroup + "的订阅"));
						groupsInfoTabbedPane.setTitleAt(2, ("集群" + currentGroup + "配置"));
//						scrollPane_2.setName("集群"+currentGroup+"的所有用户");

						//加载集群成员
						MsgLookupGroupMember_ groupMem = interactIF.lookupGroupMember(groupName);
						//添加非空判断
						if (groupMem == null) {

							int n = 0;
							Object[] possibilities = {"重试", "稍后重试", "删除"};
							//Icon icon=new ImageIcon("/com/bupt/wangfu/Swing/./res/01_sys_cskin_btn.png");
							while (n == 0) {

								n = JOptionPane.showOptionDialog(null, "查询集群失败,网络问题或该集群已丢失!", "查询失败", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, possibilities, possibilities[1]);
								groupMem = interactIF.lookupGroupMember(groupName);
								if (groupMem != null) {
									break;
								}
							}
							if (n == 2) {
								interactIF.groups.remove(groupName);
								interactIF.GroupsChangeNtfyBkp();
								reloadAllGroup();

							}
							//	JOptionPane.showConfirmDialog(null, "查询集群失败,网络问题或该集群已丢失!");
							//	JOptionPane.showMessageDialog( null, "查询集群失败,该集群可能已丢失但还未受到报告!");
							//	interactIF.groups.remove(groupName);

							//	reloadAllGroup();
						}

						if (groupMem == null) {
							groupMember.removeAll();
							groupsInfoTabbedPane.setTitleAt(0, ("集群成员"));
							groupsInfoTabbedPane.setTitleAt(1, ("集群的订阅"));
							groupsInfoTabbedPane.setTitleAt(2, ("集群配置"));
						} else {

							groupMember.removeAll();
							for (BrokerUnit temMem : groupMem.members) {
								String memName = temMem.addr;
								final JButton buttonName1 = new JButton(memName);
								buttonName1.setToolTipText(memName);
								if (memName.equals(interactIF.groups.get(groupName).addr)) {
									buttonName1.setSelectedIcon(new ImageIcon("./res/repLd.png"));
									buttonName1.setIcon(new ImageIcon("./res/repLd.png"));
								} else {
									buttonName1.setSelectedIcon(new ImageIcon("./res/rep.png"));
									buttonName1.setIcon(new ImageIcon("./res/rep.png"));
								}
								//	groupMember.removeAll();


								buttonName1.setPreferredSize(new Dimension(122, 80));
								buttonName1.setSize(new Dimension(122, 80));
								buttonName1.setHorizontalTextPosition(SwingConstants.CENTER);
								buttonName1.setVerticalTextPosition(SwingConstants.BOTTOM);
								buttonName1.setToolTipText("点击查询该成员订阅");
								buttonName1.addActionListener(new ActionListener() {

									private DefaultTableModel groupSubersModel;

									public void actionPerformed(ActionEvent e) {
										currentGroupName = groupName;
										String regAddr = buttonName1.getText();

										Map<String, ArrayList<String>> groupSubs = interactIF.lookupMemberSubscriptions(currentGroup, regAddr);
										System.out.println("查询" + "currentGroup:" + currentGroup + "成员" + regAddr + "订阅");
										if (groupSubs != null) {

											int count = 0;
											Set<String> groupsubers = groupSubs.keySet();
											for (String temp : groupsubers) {
												count += groupSubs.get(temp).size();
											}
											String[][] groupSubData = new String[count][1];
											for (String temp : groupsubers) {
												ArrayList<String> temptopics = groupSubs.get(temp);
												for (int j = 0; j < temptopics.size(); j++) {
													groupSubData[j][0] = temptopics.get(j);
												}
											}//
//									System.out.println(groupSubs.length);
//									if(groupSubs!=null){
//										for(int i=0;i<groupSubs.length;i++){
//											
//											groupSubData[i][0] = groupSubs[i];
//											
//										}
											//System.arraycopy(groupSubs, 0, groupSubData[0],0, groupSubs.length);
											//	System.arraycopy(src, srcPos, dest, destPos, length)
//									}
											String[] columnNames = {"集群" + currentGroup + "成员" + regAddr + "的订阅"};
											groupSubsModel = new DefaultTableModel(groupSubData, columnNames);
											subsTable.setModel(groupSubsModel);

//								String[][] groupSubData = new String[1][groupSubs.length];
//								String[] columnNames = { currentGroup+ ":" +searchInput +"的订阅"};
//								groupSubsModel = new DefaultTableModel(groupSubData,columnNames);
//								subsTable.setModel(groupSubsModel);

											//加载成员上的订阅者
											Object[] subersaddrO = groupsubers.toArray();
											String[] subersaddr = new String[subersaddrO.length];
											for (int m = 0; m < subersaddrO.length; m++) {
												subersaddr[m] = subersaddrO[m].toString();
											}
											final String[][] memsubers = new String[subersaddr.length][1];
											for (int i = 0; i < subersaddr.length; i++) {
												memsubers[i][0] = subersaddr[i];
											}

											String[] subers = {"集群" + currentGroup + "成员" + regAddr + "的订阅"};
											groupSubersModel = new DefaultTableModel(memsubers, subers);
//									final JTable suber = null;
											suber.setModel(groupSubersModel);
//									suber.addMouseListener(new MouseListener() {
////										   private Object groupSubs;
//
//										/** *//**
//									      * 鼠标单击事件
//									      * @param e 事件源参数
//									      */
//										 public void mouseClicked(MouseEvent e) {
//										     
//										       if(e.getClickCount()==1||e.getClickCount()==2){//点击几次，这里是双击事件
//										        //加载该该用户订阅
//										    	   int row=suber.rowAtPoint(e.getPoint());
//										    	   String[][] SubData = null;
//										    	   SubData = new String[globalSubInfo.get(groupName).get((String)suber.getValueAt(row,0)).size()][1];
//										    	   for(int k=0; k<globalSubInfo.get(groupName).get((String)suber.getValueAt(row,0)).size();k++){										    		   
//										    		  
//														
//																SubData[k][0] = globalSubInfo.get(groupName).get((String)suber.getValueAt(row,0)).get(k);														
//										    		  
//										    	   }
//										    	   String[] columnNames = {"集群" +currentGroup+"用户"+ suber.getValueAt(row,0) +"的订阅"};
//													groupSubsModel = new DefaultTableModel(SubData,columnNames);
//													subsTable.setModel(groupSubsModel);
//										       }
//										    }
//
//										@Override
//										public void mousePressed(MouseEvent e) {
//											// TODO Auto-generated method stub
//											
//										}
//
//										@Override
//										public void mouseReleased(MouseEvent e) {
//											// TODO Auto-generated method stub
//											
//										}
//
//										@Override
//										public void mouseEntered(MouseEvent e) {
//											// TODO Auto-generated method stub
//											
//										}
//
//										@Override
//										public void mouseExited(MouseEvent e) {
//											// TODO Auto-generated method stub
//											
//										}
//										   });

										} else {
											JOptionPane.showMessageDialog(null, "该成员已丢");
										}
									}


								});
								groupMember.add(buttonName1);
							}
							//	groupMember.repaint();
							//加载集群订阅信息
							ArrayList<String> subers = new ArrayList<String>();//所有用户地址
							groupSubs = new ConcurrentHashMap<String, ArrayList<String>>();
							for (BrokerUnit temMem : groupMem.members) {
								String memName = temMem.addr;
								Map<String, ArrayList<String>> memsuber = interactIF.lookupMemberSubscriptions(currentGroup, memName);
								if (memsuber != null) {
									for (String temp : memsuber.keySet()) {
										subers.add(temp);
										groupSubs.put(temp, memsuber.get(temp));
									}
								}
							}

							if (groupSubs != null) {
//							globalSubInfo.put(groupName, groupSubs);
								int count = 0;
								Set<String> groupsubers = groupSubs.keySet();
								for (String temp : groupsubers) {
									count += groupSubs.get(temp).size();
								}
								String[][] groupSubData = new String[count][1];
//						System.out.println(groupSubs.length);

								ArrayList<String> groupAllTopics = new ArrayList<String>();
								for (String temp : groupsubers) {
									groupAllTopics.addAll(groupSubs.get(temp));
//								ArrayList<String> temptopics =groupSubs.get(temp);
//								for(int j=0; j<temptopics.size(); j++){
//									
//									groupSubData[j][0] = temptopics.get(j);
//								}
//								
								}
								for (int j = 0; j < groupAllTopics.size(); j++) {
									groupSubData[j][0] = groupAllTopics.get(j);
								}

								String[] columnNames = {"集群" + groupName + "的所有订阅"};
								groupSubsModel = new DefaultTableModel(groupSubData, columnNames);
								subsTable.setModel(groupSubsModel);
								//subsTable.repaint();
							}
							//集群的所有用户

							memsubs = new String[subers.size()][1];
							for (int s = 0; s < subers.size(); s++) {
								memsubs[s][0] = subers.get(s);
							}
							String[] columnNames = {"集群" + groupName + "的所有订阅用户"};
							memSubsModel = new DefaultTableModel(memsubs, columnNames);
//						final JTable suber = null;
							suber.setModel(memSubsModel);
//						suber.addMouseListener(new MouseListener() {
//							   /** *//**
//						      * 鼠标单击事件
//						      * @param e 事件源参数
//						      */
//							 public void mouseClicked(MouseEvent e) {
//							     
//							       if(e.getClickCount()==1||e.getClickCount()==2){//点击几次，这里是双击事件
//							        //加载该该用户订阅
//							    	   int row=suber.rowAtPoint(e.getPoint());
//							    	   String[][] SubData = null;
//							    	   SubData = new String[globalSubInfo.get(groupName).get((String)suber.getValueAt(row,0)).size()][1];
//							    	   for(int k=0; k<globalSubInfo.get(groupName).get((String)suber.getValueAt(row,0)).size();k++){										    		   
//							    		  
//											
//													SubData[k][0] = globalSubInfo.get(groupName).get((String)suber.getValueAt(row,0)).get(k);														
//							    		  
//							    	   }
//							    	   String[] columnNames = {"集群" +currentGroup+"用户"+ suber.getValueAt(row,0) +"的订阅"};
//										groupSubsModel = new DefaultTableModel(SubData,columnNames);
//										subsTable.setModel(groupSubsModel);
//							       }
//							    }
//
//							@Override
//							public void mousePressed(MouseEvent e) {
//								// TODO Auto-generated method stub
//								
//							}
//
//							@Override
//							public void mouseReleased(MouseEvent e) {
//								// TODO Auto-generated method stub
//								
//							}
//
//							@Override
//							public void mouseEntered(MouseEvent e) {
//								// TODO Auto-generated method stub
//								
//							}
//
//							@Override
//							public void mouseExited(MouseEvent e) {
//								// TODO Auto-generated method stub
//								
//							}
//							   });
//						groupSubsModel = new DefaultTableModel(groupSubData,columnNames);
//						subsTable.setModel(groupSubsModel);


							//加载集群配置信息
							currentGroupLabel.setText("集群 " + currentGroup + " 配置信息");
							GroupConfiguration initInfo = configFile.ReadGroupConfiguration(groupName);
							repAddrInputC.setText(initInfo.repAddr);
							tcpPortInputC.setText("" + initInfo.tPort);
							childrenSizeInputC.setText("" + initInfo.childrenSize);
							mutltiAddrInputC.setText(initInfo.mutltiAddr);
							uPortInputC.setText("" + initInfo.uPort);
							joinTimesInputC.setText("" + initInfo.joinTimes);
							synPeriodInputC.setText("" + initInfo.synPeriod);
							lostThresholdInputC.setText("" + initInfo.lostThreshold);
							scanPeriodInputC.setText("" + initInfo.scanPeriod);
							sendPeriodInputC.setText("" + initInfo.sendPeriod);


						}
					}
				});

				//向受限集群面板中添加
				//final String groupName = (String)itr.next();
				final JCheckBox groups = new JCheckBox(groupName);
				groups.setSelected(false);
				groups.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						if (groups.isSelected()) {
							TargetGroup tem = new TargetGroup(groupName);
							currentTargetGroups.add(tem);
						} else {
							for (TargetGroup temTG : currentTargetGroups) {
								if (temTG.getName() == groupName) {

									currentTargetGroups.remove(temTG);
									break;
								}
							}
						}
						System.out.print("currentGroups:" + currentTargetGroups);

					}
				});
				if (fbdnGroupsPanel != null && groups != null && !groups.equals(null)) {
					fbdnGroupsPanel.add(groups);

				}
			}
			//fbdnGroupsPanel.repaint();
			//	fbdnGroupsPanel.invalidate();
			sendPeriodInput.setToolTipText("系统运行时不得更改");
			sendPeriodInput.setEditable(false);
			sendPeriodInput.setOpaque(false);
			scanPeriodInput.setToolTipText("系统运行时不得更改");
			scanPeriodInput.setEditable(false);
			scanPeriodInput.setOpaque(false);
			lostThresholdInput.setToolTipText("系统运行时不得更改");
			lostThresholdInput.setEditable(false);
			lostThresholdInput.setOpaque(false);
			synPeriodInput.setToolTipText("系统运行时不得更改");
			synPeriodInput.setEditable(false);
			synPeriodInput.setOpaque(false);
		} else {

			sendPeriodInput.setToolTipText("");
			sendPeriodInput.setEditable(true);
			sendPeriodInput.setOpaque(true);
			scanPeriodInput.setToolTipText("");
			scanPeriodInput.setEditable(true);
			scanPeriodInput.setOpaque(true);
			lostThresholdInput.setToolTipText("");
			lostThresholdInput.setEditable(true);
			lostThresholdInput.setOpaque(true);
			synPeriodInput.setToolTipText("");
			synPeriodInput.setEditable(true);
			synPeriodInput.setOpaque(true);


		}
	}

	//	@Override
	public MsgConf_ getConfiguration(final String name) {
		// TODO Auto-generated method stub
		GroupConfiguration config = new GroupConfiguration();
		// 检查是否存在这个组
		// if(data.getGroupIndex(name)==-1)
		// return null;
		// 获取文件
		if (configFile.LookForConfigFile(name) == null) {
			config = configFile.ReadGroupConfiguration("Default");
		} else {
			config = configFile.ReadGroupConfiguration(name);
		}

		MsgConf_ conf_ = new MsgConf_();
		conf_.neighborSize = config.childrenSize;
		conf_.joinTimes = config.joinTimes;
		conf_.lostThreshold = config.lostThreshold;
		conf_.multiAddr = config.mutltiAddr;
		conf_.scanPeriod = config.scanPeriod;
		conf_.sendPeriod = config.sendPeriod;
		conf_.synPeriod = config.synPeriod;
		conf_.uPort = config.uPort;
		conf_.repAddr = config.repAddr;

		// Display.getDefault().asyncExec(new Runnable() {
		//
		// @Override
		// public void run() {
		// TODO Auto-generated method stub
		text.append(new Date() + "获取集群" + name + "的配置信息" + "\r\n");
		text.paintImmediately(text.getBounds());
		// lblEvent.setText("获取集群"+name+"的配置信息");
		// }
		// });

		return conf_;

	}

	public void updateConfigureFile(String groupName, String addr) {
		GroupConfiguration grpConfig = new GroupConfiguration();
		MsgConf_ concurentconf_ = this.getConfiguration(groupName);
		grpConfig.GroupName = groupName;
		grpConfig.repAddr = addr;
		grpConfig.tPort = concurentconf_.tPort;
		grpConfig.childrenSize = concurentconf_.neighborSize;
		grpConfig.mutltiAddr = concurentconf_.multiAddr;
		grpConfig.uPort = concurentconf_.uPort;
		grpConfig.joinTimes = concurentconf_.joinTimes;
		grpConfig.synPeriod = concurentconf_.synPeriod;
		grpConfig.lostThreshold = concurentconf_.lostThreshold;
		grpConfig.scanPeriod = concurentconf_.scanPeriod;
		grpConfig.sendPeriod = concurentconf_.sendPeriod;
		configFile.WriteGroupConfiguration(grpConfig);
	}

	public void updateConfigureFile(String groupName, MsgConf_ groupconfs) {// 从当前的主管理员处获得的配置信息写入配置文件

		GroupConfiguration grpConfig = new GroupConfiguration();
		// MsgConf_ concurentconf_=this.getConfiguration(groupName);
		grpConfig.GroupName = groupName;
		grpConfig.repAddr = groupconfs.repAddr;
		grpConfig.tPort = groupconfs.tPort;
		grpConfig.childrenSize = groupconfs.neighborSize;
		grpConfig.mutltiAddr = groupconfs.multiAddr;
		grpConfig.uPort = groupconfs.uPort;
		grpConfig.joinTimes = groupconfs.joinTimes;
		grpConfig.synPeriod = groupconfs.synPeriod;
		grpConfig.lostThreshold = groupconfs.lostThreshold;
		grpConfig.scanPeriod = groupconfs.scanPeriod;
		grpConfig.sendPeriod = groupconfs.sendPeriod;
		configFile.WriteGroupConfiguration(grpConfig);

	}

	//	@Override
	public void updateGroup(final String name, final String newAddress) {
		// TODO Auto-generated method stub
		int stat;
		final GroupInfo priorGroup = data.getGroup(name);
		stat = data.updateGroup(name, newAddress);

		if (stat == -1) {
			// Display.getDefault().syncExec(new Runnable() {
			//
			// @Override
			// public void run() {
			// TODO Auto-generated method stub
			text.append(new Date() + "名称为" + name + "的集群未注册,无法修改信息" + "\r\n");
			text.paintImmediately(text.getBounds());
			// lblEvent.setText("集群信息修改失败");
			// }
			// });

		} else {
			if (stat == 0) {

				// Display.getDefault().syncExec(new Runnable() {
				//
				// @Override
				// public void run() {
				// TODO Auto-generated method stub
				text.append(new Date() + name + "新的代表地址与原来的一样，无需修改" + "\r\n");
				text.paintImmediately(text.getBounds());
				// lblEvent.setText("集群"+name+"代表地址无需修改");
				// }
				// });

			} else {
				GroupConfiguration grpConfig = new GroupConfiguration();
				grpConfig = configFile.ReadGroupConfiguration(name);
				grpConfig.repAddr = newAddress;
				configFile.WriteGroupConfiguration(grpConfig);

				// Display.getDefault().syncExec(new Runnable() {
				//
				// @Override
				// public void run() {
				// TODO Auto-generated method stub

				text.append(new Date() + "修改成功:将集群" + name + "的代表地址由"
						+ priorGroup.GroupAddress + "变更为" + newAddress + "\r\n");
				text.paintImmediately(text.getBounds());
				// lblEvent.setText("集群"+name+":"+priorGroup.GroupAddress+"-->"+newAddress);
				// }
				// });
			}
		}

	}

	private void refreshInfo() {
		devConf = new JPanel();

		devConf.setLayout(new GridLayout(1, 0, 5, 0));
		devConf.addMouseListener(new MouseListener() {


			//			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("devConf Mouse Clicked fffffffffffff");
				refreshInfo();
			}

			//			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("devConf Mouse mouseEntered fffffffffffff");

			}

			//			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("devConf Mouse mouseExited fffffffffffff");

			}

			//			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("devConf Mouse mousePressed fffffffffffff");

			}

			//			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		String url = "http://" + interactIF.globalControllerAddr + ":8080";
		MemoryInfo memInfo = RestProcess.getMemory(url);
		ArrayList<DevInfo> devInfo = RestProcess.getDevInfo();
		JTable jDevTab = new JTable(devInfo.size() + 2, 6);
		devConf.add(jDevTab);
		jDevTab.setValueAt("控制器：", 0, 0);
		jDevTab.setValueAt(memInfo.getUrl(), 0, 1);
		jDevTab.setValueAt("总内存：", 0, 2);
		jDevTab.setValueAt(memInfo.getTotalMem(), 0, 3);
		jDevTab.setValueAt("空闲内存：", 0, 4);
		jDevTab.setValueAt(memInfo.getFreeMem(), 0, 5);

		jDevTab.setValueAt("交换机IP", 1, 0);

		jDevTab.setValueAt("MAC地址", 1, 1);

		jDevTab.setValueAt("端口", 1, 2);

		jDevTab.setValueAt("错误状态", 1, 3);

		jDevTab.setValueAt("最后通信时间", 1, 4);

		jDevTab.setValueAt("备注", 1, 5);

		try {
			DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable table,
				                                               Object value, boolean isSelected, boolean hasFocus,
				                                               int row, int column) {
					if (row == 0) {
						setBackground(Color.yellow); // 设置奇数行底色
					} else {
						setBackground(new Color(206, 231, 255)); // 设置偶数行底色
					}
					return super.getTableCellRendererComponent(table, value,
							isSelected, hasFocus, row, column);
				}
			};
			tcr.setHorizontalAlignment(SwingConstants.CENTER);
			for (int i = 0; i < jDevTab.getColumnCount(); i++) {
				jDevTab.getColumn(jDevTab.getColumnName(i)).setCellRenderer(tcr);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (int i = 0; i < devInfo.size(); i++) {
			jDevTab.setValueAt(devInfo.get(i).getUrl(), i + 2, 0);
			jDevTab.setValueAt(devInfo.get(i).getMac(), i + 2, 1);
			jDevTab.setValueAt(devInfo.get(i).getPort(), i + 2, 2);
			jDevTab.setValueAt(devInfo.get(i).getErrorStatus(), i + 2, 3);
			jDevTab.setValueAt(devInfo.get(i).getLastSeen(), i + 2, 4);
			jDevTab.setValueAt(devInfo.get(i).getRemark(), i + 2, 5);
		}

		flowConf = new JPanel();

		flowConf.setLayout(new GridLayout(1, 0, 5, 0));

//		ArrayList<Flow> flow = RestProcess.getFlowInfo();
		ArrayList<Flow> flow = new ArrayList<Flow>();
		JTable jFlowTab = new JTable(flow.size(), 4);
		TableColumn firsetColumn = jFlowTab.getColumnModel().getColumn(0);
		firsetColumn.setPreferredWidth(80);
		firsetColumn.setMaxWidth(80);
		firsetColumn.setMinWidth(80);
		TableColumn threadColumn = jFlowTab.getColumnModel().getColumn(2);
		threadColumn.setPreferredWidth(80);
		threadColumn.setMaxWidth(80);
		threadColumn.setMinWidth(80);

		flowConf.add(jFlowTab);
//		jFlowTab.setValueAt("控制器：", 0, 0);
		jFlowTab.setValueAt(flow.get(0).getDpid(), 0, 1);
		jFlowTab.setValueAt("总流量：", 0, 2);
		jFlowTab.setValueAt(flow.get(0).getFlowCount(), 0, 3);

		try {
			DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable table,
				                                               Object value, boolean isSelected, boolean hasFocus,
				                                               int row, int column) {
					if (row == 0) {
						setBackground(Color.yellow); // 设置奇数行底色
					} else {
						setBackground(new Color(206, 231, 255)); // 设置偶数行底色
					}
					return super.getTableCellRendererComponent(table, value,
							isSelected, hasFocus, row, column);
				}
			};
			tcr.setHorizontalAlignment(SwingConstants.CENTER);
			for (int i = 0; i < jFlowTab.getColumnCount(); i++) {
				jFlowTab.getColumn(jFlowTab.getColumnName(i)).setCellRenderer(tcr);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (int i = 1; i < flow.size(); i++) {
			jFlowTab.setValueAt("交换机：", i, 0);
			jFlowTab.setValueAt(flow.get(i).getDpid(), i, 1);
			jFlowTab.setValueAt("流量：", i, 2);
			jFlowTab.setValueAt(flow.get(i).getFlowCount(), i, 3);
		}


	}
}
