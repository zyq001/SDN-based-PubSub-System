package org.apache.servicemix.wsn.router.design;

import org.apache.servicemix.wsn.router.admin.AdminMgr;
import org.apache.servicemix.wsn.router.mgr.BrokerUnit;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupMember_;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

class SaveEvent extends SelectionAdapter {
	private String Fn = "";
	private Shell shell;
	private Text text;

	SaveEvent(Shell shell, Text text) {
		this.shell = shell;
		this.text = text;

	}

	public void widgetSelected(final SelectionEvent e) {
		FileDialog dlg = new FileDialog(shell, SWT.SAVE);
		dlg.setFilterExtensions(new String[]{"*.txt", "*.java",
				"*.bat", "*.*"});
		String fileName = dlg.open();
		if (fileName == null)
			return;
		Fn = fileName;
		File file = new File(Fn);

		String str = text.getText();
		byte byteBuf[] = new byte[100000];
		byteBuf = str.getBytes();
		try {
			FileOutputStream out = new FileOutputStream(fileName);
			out.write(byteBuf);
			out.close();
		} catch (IOException ioe) {
		}
	}
}

class ExitEvent extends SelectionAdapter {
	public void widgetSelected(final SelectionEvent e) {
		System.exit(0);
	}

}

class ReConfigEvent extends SelectionAdapter {
	private Table table;
	private Shell shell;
	private Text text;
	private Window win;
	private AdminMgr interactIF;

	ReConfigEvent(Shell shell, Table table, Text text, Window win, AdminMgr interactIF) {
		this.shell = shell;
		this.table = table;
		this.text = text;
		this.win = win;
		this.interactIF = interactIF;
	}

	public void widgetSelected(final SelectionEvent e) {
		int selection = table.getSelectionIndex();

		if (selection == -1) {
			MessageDialog.openInformation(shell, "", "请在表格中选择一项纪录");
		} else {
			TableItem tmpItem = table.getItem(selection);
			String grpName = tmpItem.getText(0);
			if (grpName.equals("")) {
				MessageDialog.openInformation(shell, "", "请选择一条组名称不为空的记录");
			} else {
				shell.setEnabled(false);
				new Configuration(grpName, interactIF).open();
				shell.setEnabled(true);
				shell.setActive();

				//显示状态栏
				win.label_operate.setText("配置集群" + grpName);
				//切换到控制台
				win.tabFolder.setSelection(win.tabItem);


				SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
				Date date = new Date();
				text.append("\n" + sdf.format(date) + ":对" + grpName + "进行配置\n");
			}
		}
	}
}

class AboutMeEvent extends SelectionAdapter {
	private Shell shell;

	AboutMeEvent(Shell shell) {
		this.shell = shell;
	}

	public void widgetSelected(final SelectionEvent e) {
		MessageBox msg = new MessageBox(shell);
		msg.setText("关于本记事本");
		msg.setMessage("版本:Version1.2\n制作人:吴思齐\t\n\n2011.5.20");
		msg.open();
	}
}

class TimeDateEvent extends SelectionAdapter {
	private Text text;

	TimeDateEvent(Text text) {
		this.text = text;
	}

	public void widgetSelected(final SelectionEvent e) {
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd\n");
		Date date = new Date();
		text.append("\n" + sdf.format(date) + "：查询当前时间：" + sdf.format(date) + "\n");
	}
}

class CopyEvent extends SelectionAdapter {
	private Text text;

	CopyEvent(Text text) {
		this.text = text;
	}

	public void widgetSelected(final SelectionEvent e) {
		text.copy();
	}
}

class SetFontsEvent extends SelectionAdapter {

	private Text text;
	private Shell shell;

	SetFontsEvent(Shell shell, Text text) {
		this.text = text;
		this.shell = shell;
	}

	public void widgetSelected(final SelectionEvent e) {
		FontDialog dlg = new FontDialog(shell);
		FontData fontData = dlg.open();
		Font f = null;
		if (fontData != null) {
			f = new Font(shell.getDisplay(), fontData);
		}
		text.setFont(f);
	}
}

class ShowGroupInfo extends SelectionAdapter {
	private Text text;
	private Shell shell;
	private Table table;
	private Data data;
	private AdminMgr Interface;
	private Window win;

	ShowGroupInfo(Shell shell, Text text, Table table, Data data, AdminMgr Interface, Window win) {
		this.text = text;
		this.shell = shell;
		this.table = table;
		this.data = data;
		this.Interface = Interface;
		this.win = win;
	}

	public void widgetSelected(final SelectionEvent e) {
		//状态栏
		win.label_event.setText("查询集群注册信息");
		//突出表格，隐藏控制台
		win.tabFolder.setSelection(win.tabItem);

		List<GroupInfo> GroupList = new ArrayList<GroupInfo>();
		//删除原有的表格记录和表格列
		table.removeAll();
		TableColumn[] allColumns = table.getColumns();

		for (int i = 0; i < allColumns.length; i++) {
			allColumns[i].dispose();
		}

		//初始化新的列
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("集群名称");

		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("集群代表地址");

		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("注册时间");


		text.append("查看集群信息\n");
		GroupList = data.getAllGroup();
		Iterator<GroupInfo> itr = GroupList.iterator();
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd\n");
		while (itr.hasNext()) {
			GroupInfo aGroup = itr.next();
			TableItem tmpTableItem = new TableItem(table, 0);
			if (!(aGroup.GroupName == null) && !(aGroup.GroupAddress == null) && !(aGroup.date == null))
				tmpTableItem.setText(new String[]{aGroup.GroupName, aGroup.GroupAddress, sdf.format(aGroup.date)});
			else {
				if (aGroup.GroupName == null)
					System.out.println("NAME:NULL");
				if (aGroup.GroupAddress == null)
					System.out.println("ADDRESS:NULL");
				if (aGroup.date == null)
					System.out.println("DATE:NULL");
			}
		}

		//-----------------------------鼠标点击事件--------------------------------------------//
		table.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				int sellndex = table.getSelectionIndex();
				TableItem item = table.getItem(sellndex);

				String grpName = item.getText(0);
				String grpReAddr = item.getText(1);

				//双击某集群后，显示选中集群的成员信息
				table.removeAll();
				//删除原列信息
				TableColumn[] allColumns = table.getColumns();
				for (int i = 0; i < allColumns.length; i++) {
					allColumns[i].dispose();
				}

				//创建新的列
				TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn.setWidth(100);
				tblclmnNewColumn.setText("集群名称");

				TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_1.setWidth(100);
				tblclmnNewColumn_1.setText("成员地址");

				TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_2.setWidth(100);
				tblclmnNewColumn_2.setText("集群id");

				TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_3.setWidth(100);
				tblclmnNewColumn_3.setText("tcp端口号");


				//测试用代码
				/*
			  	TableItem item=new TableItem(table,0);
			  	item.setText(new String[]{grpName,"118.229.134.140"});
			  	TableItem item2=new TableItem(table,0);
			  	item2.setText(new String[]{"","118.229.134.250"});
				 */

				//调用接口功能，实现集群成员查询
				MsgLookupGroupMember_ GroupMember = Interface.lookupGroupMember(grpName);
				Iterator<BrokerUnit> itr = GroupMember.members.iterator();

				BrokerUnit b1 = itr.next();
				TableItem ti1 = new TableItem(table, 0);
				ti1.setText(new String[]{grpName, b1.addr + "(代表)", "" + b1.id, "" + b1.tPort});

				//遍历返回的成员列表,更新表格
				while (itr.hasNext()) {
					TableItem member = new TableItem(table, 0);
					BrokerUnit aMember = itr.next();
					member.setText(new String[]{"", aMember.addr, "" + aMember.id, "" + aMember.tPort});
				}
				//更新控制台
				/*//调用接口功能，实现集群成员查询
				MsgLookupGroupMember_ GroupMember=Interface.lookupGroupMember(grpName);
				Iterator<BrokerUnit> itr=GroupMember.members.iterator();
				int count=0;
				//遍历返回的成员列表,更新表格
				while(itr.hasNext()){
					TableItem member=new TableItem(table,0);
					BrokerUnit aMember=itr.next();
					String addr;
					//检查是否是代表地址
					if(aMember.addr.equals(grpReAddr)){
						addr=aMember.addr+"(代表地址)";
					}
					else{
						addr=aMember.addr;
					}
					
					//检查是否是第一项
					if(count==0){
						
						member.setText(new String[]{grpName,addr,""+aMember.id,""+aMember.tPort});
					}
					else{
						member.setText(new String[]{"",addr,""+aMember.id,""+aMember.tPort});
					}
					count++;
				}*/


			}

			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		//-------------------------------响应鼠标点击事件结束--------------------------------------------//
		//更新表格
		Date date = new Date();
		text.append("\n" + sdf.format(date) + "：查询了集群的注册信息" + "\n");
	}
}

//---------------------------------------------------------------------//
class LookUpSub extends SelectionAdapter {
	private Text text;
	private Shell shell;
	private Table table;
	private Window win;
	private Data data;
	private String grpName;
	private AdminMgr Interface;

	LookUpSub(Shell shell, Text text, Table table,
	          Window win, Data data, AdminMgr interactIF) {
		this.text = text;
		this.shell = shell;
		this.table = table;
		this.data = data;
		this.win = win;
		this.Interface = interactIF;
		// TODO Auto-generated constructor stub
	}

	public void widgetSelected(final SelectionEvent e) {
		InputDialog dialog = new InputDialog(shell, "查询组成员", "请输入组名称", "", new MyValidator());

		if (dialog.open() == InputDialog.OK) {
			grpName = dialog.getValue();

			if (data.getGroupIndex(grpName) == -1) {
				MessageDialog.openInformation(shell, "提示", "不存在该组");
			} else {
				//状态栏
				win.label_operate.setText("查询集群订阅信息");
				//突出表格，隐藏控制台
				win.tabFolder.setSelection(win.tabItem);

				List<GroupInfo> GroupList = new ArrayList<GroupInfo>();
				table.removeAll();
				TableColumn[] allColumns = table.getColumns();

				for (int i = 0; i < allColumns.length; i++) {
					allColumns[i].dispose();
				}


				TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn.setWidth(100);
				tblclmnNewColumn.setText("集群名称");

				TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_1.setWidth(100);
				tblclmnNewColumn_1.setText("集群订阅信息");
				
				/*
				//测试用
				  TableItem item=new TableItem(table,0);
				  item.setText(new String[]{grpName,"天气预报"});
				  TableItem item2=new TableItem(table,0);
				  item2.setText(new String[]{"","网络状况"});
				//测试END
				*/

				SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
				Date date = new Date();
				text.append("\n" + sdf.format(date) + "：查询了集群" + grpName + "的订阅信息\n");
				//调用接口

//				String[] GroupMember=Interface.lookupGroupSubscriptions(grpName);
				/*if(GroupMember==null){
					data.removeGroup(grpName);
					Interface.groups.remove(grpName);
					Interface.GroupsChangeNtfyBkp();
					MessageDialog.openInformation(shell, "提示", "不存在该集群");
				}else{
					//更新table
					for(int i=0;i<GroupMember.length;i++){
						TableItem member=new TableItem(table,0);
						if(i==0){
							member.setText(new String[]{grpName,GroupMember[i]});
						}
						else{
							member.setText(new String[]{"",GroupMember[i]});
						}//if else
					}//for	
				}*/
			}
		}
	}
}

class ShowDefaultConfig extends SelectionAdapter {
	private Shell shell;

	ShowDefaultConfig(Shell shell) {
		// TODO Auto-generated constructor stub
		this.shell = shell;
	}


	public void widgetSelected(final SelectionEvent e) {

		shell.setEnabled(false);
		new Configuration("Default", null).open();
		shell.setEnabled(true);
		shell.setActive();

	}
}

class GetGroupConfig extends SelectionAdapter {
	private Shell shell;
	private Table table;
	private Text text;
	private Window win;
	private FileOperation configFile = new FileOperation();

	GetGroupConfig(Shell shell, Table table, Text text, Window win) {
		this.shell = shell;
		this.table = table;
		this.text = text;
		this.win = win;
	}

	public void widgetSelected(final SelectionEvent e) {
		int selection = table.getSelectionIndex();

		if (selection == -1) {
			MessageDialog.openInformation(shell, "", "请在表格中选择一项纪录");
		} else {
			TableItem tmpItem = table.getItem(selection);
			String grpName = tmpItem.getText(0);
			if (grpName.equals("")) {
				MessageDialog.openInformation(shell, "", "请选择一条组名称不为空的记录");
			} else {
				GroupConfiguration initInfo = new GroupConfiguration();
				if (configFile.LookForConfigFile(grpName) == null) {
					initInfo = configFile.ReadGroupConfiguration("Default");
				} else {
					initInfo = configFile.ReadGroupConfiguration(grpName);
				}
				String configInfo = new String("集群名称：" + initInfo.GroupName + "\n"
						+ "代表地址：" + initInfo.repAddr + "\n"
						+ "TCP端口号：" + initInfo.tPort + "\n"
						+ "组播端口号：" + initInfo.uPort + "\n"
						+ "子节点数目：" + initInfo.childrenSize + "\n"
						+ "加入超时时间：" + initInfo.joinTimes + "\n"
						+ "缓冲区大小：" + initInfo.synPeriod + "\n"
						+ "判定失效阀值：" + initInfo.lostThreshold + "\n"
						+ "扫描周期：" + initInfo.scanPeriod + "\n"
						+ "发送周期：" + initInfo.sendPeriod + "\n");

				//状态栏显示
				win.label_operate.setText("查看集群" + grpName + "的配置信息");
				//切换到控制台
				win.tabFolder.setSelection(win.tabItem);

				//text.append("对"+grpName+"进行了配置查询,内容如下：\n"+configInfo);
				//用对话框显示配置信息
				MessageDialog.openInformation(shell, "集群" + grpName + "的配置信息", configInfo);

				SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
				Date date = new Date();
				text.append("\n" + sdf.format(date) + "：查询了集群" + grpName + "的配置信息\n");
			}
		}
	}
}


class SetAddressEvent extends SelectionAdapter {
	private AdminMgr IF;


	SetAddressEvent(AdminMgr IF) {
		this.IF = IF;
	}

	public void widgetSelected(final SelectionEvent e) {
		new SetAddress(IF).open();

	}
}

class ConvertWindowEvent extends SelectionAdapter {
	private Window win;

	ConvertWindowEvent(Window win) {
		this.win = win;
	}

	public void widgetSelected(final SelectionEvent e) {
		win.tabFolder.setSelection(win.tabItem);
	}
}

//------------------------------------------------------------------------//
class LookUpMem extends SelectionAdapter {
	private Table table;
	private Text text;
	private Shell shell;
	private String grpName;
	private Data data;
	private Window win;
	private AdminMgr Interface;

	LookUpMem(Shell shell, Text text, Table table, Window win, Data data, AdminMgr interactIF) {
		this.shell = shell;
		this.table = table;
		this.text = text;
		this.win = win;
		this.data = data;
		this.Interface = interactIF;
	}

	public void widgetSelected(final SelectionEvent e) {
		InputDialog dialog = new InputDialog(shell, "查询组成员", "请输入组名称", "", new MyValidator());
		if (dialog.open() == InputDialog.OK) {
			grpName = dialog.getValue();
			int index = data.getGroupIndex(grpName);
			if (index == -1 && grpName != null) {
				MessageDialog.openInformation(shell, "提示", "不存在该组");
			} else {
				//显示状态栏
				win.label_operate.setText("查询集群成员");
				//切切换到表格
				win.tabFolder.setSelection(win.tabItem);
				//清除原表格记录
				table.removeAll();
				//删除原列信息
				TableColumn[] allColumns = table.getColumns();
				for (int i = 0; i < allColumns.length; i++) {
					allColumns[i].dispose();
				}

				//创建新的列
				TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn.setWidth(100);
				tblclmnNewColumn.setText("集群名称");

				TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_1.setWidth(100);
				tblclmnNewColumn_1.setText("成员地址");

				TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_2.setWidth(100);
				tblclmnNewColumn_2.setText("集群id");

				TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_3.setWidth(100);
				tblclmnNewColumn_3.setText("tcp端口号");

				//获取输入集群的代表地址
//				List<GroupInfo> GroupList=data.getAllGroup();
//				GroupInfo theGroup=GroupList.get(index);
//				String grpReAddr=theGroup.GroupAddress;

				//测试用代码
				/*
			  	TableItem item=new TableItem(table,0);
			  	item.setText(new String[]{grpName,"118.229.134.140"});
			  	TableItem item2=new TableItem(table,0);
			  	item2.setText(new String[]{"","118.229.134.250"});
				 */


				//调用接口功能，实现集群成员查询
				MsgLookupGroupMember_ GroupMember = Interface.lookupGroupMember(grpName);
				if (GroupMember == null) {
					data.removeGroup(grpName);
					Interface.groups.remove(grpName);
					Interface.GroupsChangeNtfyBkp();
					MessageDialog.openInformation(shell, "提示", "不存在该集群");
				} else {
					Iterator<BrokerUnit> itr = GroupMember.members.iterator();

					BrokerUnit b1 = itr.next();
					TableItem ti1 = new TableItem(table, 0);
					ti1.setText(new String[]{grpName, b1.addr + "(代表)", "" + b1.id, "" + b1.tPort});

					//遍历返回的成员列表,更新表格
					while (itr.hasNext()) {
						TableItem member = new TableItem(table, 0);
						BrokerUnit aMember = itr.next();
						member.setText(new String[]{"", aMember.addr, "" + aMember.id, "" + aMember.tPort});
					}
					//更新控制台

					SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
					Date date = new Date();
					text.append("\n" + sdf.format(date) + "：查询了集群" + grpName + "的成员信息\n");
				}

			}
		}
	}
}

//----------------------------------------------------------------//
class LookUpMemSub extends SelectionAdapter {
	private Table table;
	private Text text;
	private Shell shell;
	private String grpName;
	private String hstAddr;
	;
	private Data data;
	private Window win;
	private AdminMgr Interface;

	LookUpMemSub(Shell shell, Text text, Table table, Window win, Data data, AdminMgr interactIF) {
		this.shell = shell;
		this.table = table;
		this.text = text;
		this.win = win;
		this.data = data;
		this.Interface = interactIF;
	}

	public void widgetSelected(final SelectionEvent e) {
		InputDialog dialog = new InputDialog(shell, "查询特定组成员订阅信息", "请输入集群名称", "", new MyValidator());

		if (dialog.open() == InputDialog.OK) {
			grpName = dialog.getValue();
			
		/*
			Scanner infoScan=new Scanner(grpAndhst);
			infoScan.useDelimiter(",");
			grpName=infoScan.next();
			hstAddr=infoScan.next();
		*/
			if (data.getGroupIndex(grpName) == -1) {
				MessageDialog.openInformation(shell, "提示", "不存在该组");
			} else {


				InputDialog dialog1 = new InputDialog(shell, "查询特定组成员订阅信息", "请输入集群内成员地址", "", new MyValidator());

				if (dialog1.open() == InputDialog.OK) {

					hstAddr = dialog1.getValue();

					//显示状态栏
					win.label_operate.setText("查询集群成员订阅信息");
					//突出表格，隐藏控制台
					win.tabFolder.setSelection(win.tabItem);

					List<GroupInfo> GroupList = new ArrayList<GroupInfo>();
					table.removeAll();
					TableColumn[] allColumns = table.getColumns();

					for (int i = 0; i < allColumns.length; i++) {
						allColumns[i].dispose();
					}


					TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
					tblclmnNewColumn.setWidth(100);
					tblclmnNewColumn.setText("集群名称");

					TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
					tblclmnNewColumn_1.setWidth(100);
					tblclmnNewColumn_1.setText("集群成员地址");

					TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
					tblclmnNewColumn_2.setWidth(100);
					tblclmnNewColumn_2.setText("订阅信息");

					//显示控制台
					//更新表格
					SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
					Date date = new Date();
					text.append("\n" + sdf.format(date) + "：查询了集群" + grpName + "中成员" + hstAddr + "的订阅信息" + "\n");
					//调用接口


					/*String[] GroupMember=Interface.lookupMemberSubscriptions(grpName,hstAddr);
					if(GroupMember==null){
						MessageDialog.openInformation(shell, "提示", "该集群内不存在这个成员地址");
					}else{
						for(int i=0;i<GroupMember.length;i++){
							TableItem member=new TableItem(table,0);
							if(i==0){
								member.setText(new String[]{grpName,hstAddr,GroupMember[i]});
							}
							else{
								member.setText(new String[]{"","",GroupMember[i]});
							}
						}//for

					}*/
				}
			}
		}
	}
}

//---------------------------------------------------------------//
class ShowHistory extends SelectionAdapter {
	private Text text;
	private Window win;
	private Data data;

	ShowHistory(Text text, Window win, Data data) {
		this.text = text;
		this.win = win;
		this.data = data;
	}

	public void widgetSelected(final SelectionEvent e) {
		List<String> HistoryList = new ArrayList<String>();
		HistoryList = data.getHistory();

		win.tabFolder.setSelection(win.tabItem);

		win.label_operate.setText("查看历史记录");

		Iterator<String> itr = HistoryList.iterator();
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
		Date date = new Date();
		text.append("\n" + sdf.format(date) + ":查看历史记录\n");
		while (itr.hasNext()) {
			String history = itr.next();
			text.append("\n" + history + "\n");
		}

	}
}

//-----------------------------------------------------------------//
class MyValidator implements IInputValidator {

	@Override
	public String isValid(String newText) {
		// TODO Auto-generated method stub
		if (newText == null) {
			return "请输入数值";
		}

		return null;
		//return "请根据当前组信息输入组名称";
	}

}