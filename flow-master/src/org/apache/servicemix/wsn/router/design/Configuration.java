package org.apache.servicemix.wsn.router.design;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.apache.servicemix.wsn.router.admin.AdminMgr;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;

public class Configuration {
	private String Title;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private FileOperation configFile=new FileOperation();
	private AdminMgr interactIF;
	private Text text_5;
	private Text text_6;
	private Text text_7;
	private Text text_8;
	private Text text_9;
	
	Configuration(String Title,AdminMgr interactIF){
		this.Title=Title;
		this.interactIF=interactIF;
	}
	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		final Display display = Display.getDefault();
		final Shell shell = new Shell(SWT.CLOSE|SWT.SYSTEM_MODAL);
		shell.setToolTipText("");
		shell.setSize(341, 643);
		shell.setDragDetect(false);
		shell.setFullScreen(false);

		shell.setText(Title+"配置信息");
		
		//读取文件
		GroupConfiguration initInfo=new GroupConfiguration();
		if(Title.equals("Default")){
			initInfo=configFile.ReadGroupConfiguration("Default");
		//	System.out.println("读取完默认配置文件到initInfo中");
		}
		else{
			if(configFile.LookForConfigFile(Title)==null){
				initInfo=configFile.ReadGroupConfiguration("Default");
			}
			else{
				initInfo=configFile.ReadGroupConfiguration(Title);
			}
		}
		//读取文件结束
		
		//初始化界面信息
		//setInitInfo(initInfo);
		//初始化结束
		
		//保存按钮
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GroupConfiguration svGroup=new GroupConfiguration();
				MsgConf_ conf=new MsgConf_();
				
				svGroup.GroupName=Title;
				svGroup.repAddr=text.getText();
				svGroup.tPort=Integer.parseInt(text_1.getText());
				svGroup.childrenSize=Integer.parseInt(text_2.getText());
				svGroup.mutltiAddr=text_3.getText();
				svGroup.uPort=Integer.parseInt(text_4.getText());
				svGroup.joinTimes=Integer.parseInt(text_5.getText());
				svGroup.synPeriod=Integer.parseInt(text_6.getText());
				svGroup.lostThreshold=Long.parseLong(text_7.getText());
				svGroup.scanPeriod=Long.parseLong(text_8.getText());
				svGroup.sendPeriod=Long.parseLong(text_9.getText());
				
				
				
				configFile.WriteGroupConfiguration(svGroup);
				MessageDialog.openInformation(shell, "", "保存成功");
				
				if(!Title.equalsIgnoreCase("Default")){
					conf.repAddr=text.getText();
					conf.tPort=Integer.parseInt(text_1.getText());
					conf.neighborSize=Integer.parseInt(text_2.getText());
					conf.multiAddr=text_3.getText();
					conf.uPort=Integer.parseInt(text_4.getText());
					conf.joinTimes=Integer.parseInt(text_5.getText());
					conf.synPeriod=Long.parseLong(text_6.getText());
					conf.lostThreshold=Long.parseLong(text_7.getText());
					conf.scanPeriod=Long.parseLong(text_8.getText());
					conf.sendPeriod=Long.parseLong(text_9.getText());
				
					interactIF.setConfiguration(Title, conf);
				}
			}
		});
		btnNewButton.setBounds(156, 557, 72, 22);
		btnNewButton.setText("保存");
		
		//退出按钮
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			
				//shell.dispose();
				//display.dispose();
			}
		});
		btnNewButton_1.setBounds(253, 557, 72, 22);
		btnNewButton_1.setText("退出");
		//配置项第一组
		Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 10, 302, 357);
		group.setText("基本配置项");
	
		//代表地址配置项
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(10, 51, 85, 18);
		lblNewLabel.setText("代表地址");
		
		text = new Text(group, SWT.BORDER);
		text.setBounds(140, 48, 101, 18);
		//TCP端口号配置项
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setBounds(10, 98, 79, 18);
		lblNewLabel_1.setText("TCP端口");
		
		text_1 = new Text(group, SWT.BORDER);
		text_1.setBounds(140, 95, 101, 18);
		
		//子节点数目配置项
		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setBounds(10, 137, 85, 18);
		lblNewLabel_2.setText("子节点数目");
		
		text_2 = new Text(group, SWT.BORDER);
		text_2.setBounds(140, 134, 101, 18);
		
		//组播地址配置项
		Label lblNewLabel_3 = new Label(group, SWT.NONE);
		lblNewLabel_3.setBounds(10, 179, 72, 15);
		lblNewLabel_3.setText("组播地址");
		
		text_3 = new Text(group, SWT.BORDER);
		text_3.setBounds(140, 176, 101, 18);
		
		//组播端口号
		Label lblNewLabel_4 = new Label(group, SWT.NONE);
		lblNewLabel_4.setBounds(10, 218, 79, 15);
		lblNewLabel_4.setText("组播端口号");
		
		text_4 = new Text(group, SWT.BORDER);
		text_4.setBounds(140, 215, 101, 18);
		
		//加入超时时间配置项
		Label lblNewLabel_5 = new Label(group, SWT.NONE);
		lblNewLabel_5.setBounds(10, 265, 108, 16);
		lblNewLabel_5.setText("加入重试次数");
		
		text_5 = new Text(group, SWT.BORDER);
		text_5.setBounds(140, 262, 97, 22);
		
		//缓冲区大小
	
		Label lblNewLabel_6 = new Label(group, SWT.NONE);
		lblNewLabel_6.setBounds(10, 302, 85, 16);
		lblNewLabel_6.setText("订阅同步周期");
		
		text_6 = new Text(group, SWT.BORDER);
		text_6.setBounds(140, 302, 97, 22);
		
		//配置项第二组
		Group group_1 = new Group(shell, SWT.NONE);
		group_1.setBounds(10, 386, 302, 151);
		group_1.setText("心跳检测配置项");
		
		Label lblNewLabel_7 = new Label(group_1, SWT.NONE);
		lblNewLabel_7.setBounds(10, 28, 104, 16);
		lblNewLabel_7.setText("判定失效阀值");
		text_7 = new Text(group_1, SWT.BORDER);
		text_7.setBounds(141, 22, 71, 22);
		
		Label lblNewLabel_8 = new Label(group_1, SWT.NONE);
		lblNewLabel_8.setBounds(10, 72, 72, 16);
		lblNewLabel_8.setText("扫描周期");
		text_8 = new Text(group_1, SWT.BORDER);
		text_8.setBounds(141, 72, 71, 22);
		
		Label lblNewLabel_9 = new Label(group_1, SWT.NONE);
		lblNewLabel_9.setBounds(10, 114, 72, 16);
		lblNewLabel_9.setText("发送周期");
		text_9 = new Text(group_1, SWT.BORDER);
		text_9.setBounds(141, 111, 71, 22);
		
		setInitInfo(initInfo);

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public void setInitInfo(GroupConfiguration initInfo){
	//	System.out.println("代表地址为："+initInfo.repAddr);
	//	System.out.println("端口号为："+initInfo.portT);
	
		text.setText(initInfo.repAddr);
		text_1.setText(""+initInfo.tPort);
		text_2.setText(""+initInfo.childrenSize);
		text_3.setText(initInfo.mutltiAddr);
		text_4.setText(""+initInfo.uPort);
		text_5.setText(""+initInfo.joinTimes);
		text_6.setText(""+initInfo.synPeriod);
		text_7.setText(""+initInfo.lostThreshold);
		text_8.setText(""+initInfo.scanPeriod);
		text_9.setText(""+initInfo.sendPeriod);
		
		text.setText(initInfo.repAddr);
	}
}
