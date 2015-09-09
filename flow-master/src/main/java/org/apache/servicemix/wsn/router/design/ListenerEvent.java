package org.apache.servicemix.wsn.router.design;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import org.apache.servicemix.wsn.router.admin.AdminMgr;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.mgr.BrokerUnit;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupMember_;

class SaveEvent extends SelectionAdapter {
	private String Fn="";
	private Shell shell;
	private Text text;
	SaveEvent(Shell shell,Text text){
		this.shell=shell;
		this.text=text;
		
	}
	public void widgetSelected(final SelectionEvent e) {
		FileDialog dlg = new FileDialog(shell, SWT.SAVE);
		dlg.setFilterExtensions(new String[] { "*.txt", "*.java",
				"*.bat", "*.*" });
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

class ExitEvent extends SelectionAdapter{
	public void widgetSelected(final SelectionEvent e){
		System.exit(0);
	}
	
}

class ReConfigEvent extends SelectionAdapter{
	private Table table;
	private Shell shell;
	private Text text;
	private Window win;
	private AdminMgr interactIF;
	
	ReConfigEvent(Shell shell,Table table,Text text,Window win,AdminMgr interactIF){
		this.shell=shell;
		this.table=table;
		this.text=text;
		this.win=win;
		this.interactIF=interactIF;
	}
	public void widgetSelected(final SelectionEvent e){
		int selection=table.getSelectionIndex();

		if(selection==-1){
			MessageDialog.openInformation(shell, "", "���ڱ����ѡ��һ���¼");
		}
		else{ 
			TableItem tmpItem=table.getItem(selection);
			String grpName=tmpItem.getText(0);
			if(grpName.equals("")){
				MessageDialog.openInformation(shell, "", "��ѡ��һ������Ʋ�Ϊ�յļ�¼");
			}
			else{
				shell.setEnabled(false);
				new Configuration(grpName, interactIF).open();
				shell.setEnabled(true);
				shell.setActive();
				
				//��ʾ״̬��
				win.label_operate.setText("���ü�Ⱥ"+grpName );
				//�л�������̨
				win.tabFolder.setSelection(win.tabItem);
				
				
				SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
				Date date = new Date();
				text.append("\n"+sdf.format(date)+":��"+grpName+"��������\n");
			}
		}
	}
}

class AboutMeEvent extends SelectionAdapter{
   private Shell shell;
	
   AboutMeEvent(Shell shell){
	   this.shell=shell;
   }	
	public void widgetSelected(final SelectionEvent e){
		MessageBox msg = new MessageBox(shell);
		msg.setText("���ڱ����±�");
		msg.setMessage("�汾:Version1.2\n������:��˼��\t\n\n2011.5.20");
		msg.open();
	}
}

class TimeDateEvent extends SelectionAdapter{
	private Text text;
	TimeDateEvent(Text text){
		this.text=text;
	}
	public void widgetSelected(final SelectionEvent e){
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd\n");
		Date date = new Date();
		text.append("\n"+sdf.format(date)+"����ѯ��ǰʱ�䣺"+sdf.format(date)+"\n");
	}
}

class CopyEvent extends SelectionAdapter{
	private Text text;
	CopyEvent(Text text){
		this.text=text;
	}
	public void widgetSelected(final SelectionEvent e) {
		text.copy();
	}
}

class SetFontsEvent extends SelectionAdapter{
	
	private Text text;
	private Shell shell;
	SetFontsEvent(Shell shell,Text text){
		this.text=text;
		this.shell=shell;
	}
	public void widgetSelected(final SelectionEvent e){
		FontDialog dlg = new FontDialog(shell);
		FontData fontData = dlg.open();
		Font f = null;
		if (fontData != null) {
			f = new Font(shell.getDisplay(), fontData);
		}
		text.setFont(f);
	}
}

class ShowGroupInfo extends SelectionAdapter{
	private Text text;
	private Shell shell;
	private Table table;
	private Data data;
	private AdminMgr Interface;
	private Window win;
	
	ShowGroupInfo(Shell shell,Text text,Table table,Data data,AdminMgr Interface,Window win){
		this.text=text;
		this.shell=shell;
		this.table=table;
		this.data=data;
		this.Interface=Interface;
		this.win=win;
	}
	public void widgetSelected(final SelectionEvent e) {
		//״̬��
		win.label_event.setText("��ѯ��Ⱥע����Ϣ");
		//ͻ��������ؿ���̨
		win.tabFolder.setSelection(win.tabItem);
		
		List<GroupInfo> GroupList=new ArrayList<GroupInfo>();
		//ɾ��ԭ�еı���¼�ͱ����
		table.removeAll();
		TableColumn[] allColumns=table.getColumns();
		
		for(int i=0;i<allColumns.length;i++){
			allColumns[i].dispose();
		}
		
		//��ʼ���µ���
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("��Ⱥ���");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("��Ⱥ����ַ");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("ע��ʱ��");
		
		
		
		text.append("�鿴��Ⱥ��Ϣ\n");
		GroupList=data.getAllGroup();
		Iterator<GroupInfo> itr=GroupList.iterator();
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd\n");
		while(itr.hasNext()){
			GroupInfo aGroup=itr.next();
			TableItem tmpTableItem=new TableItem(table,0);
			if(!(aGroup.GroupName==null)&&!(aGroup.GroupAddress==null)&&!(aGroup.date==null))
			tmpTableItem.setText(new String[]{aGroup.GroupName,aGroup.GroupAddress,sdf.format(aGroup.date)});
			else{
				if(aGroup.GroupName==null)
					System.out.println("NAME:NULL");
				if(aGroup.GroupAddress==null)
					System.out.println("ADDRESS:NULL");
				if(aGroup.date==null)
				System.out.println("DATE:NULL");
			}
		}
		
		//-----------------------------������¼�--------------------------------------------//
		table.addMouseListener(new MouseListener(){
			public void mouseDoubleClick(MouseEvent e){
				int sellndex=table.getSelectionIndex();
				TableItem item = table.getItem(sellndex);
				
				String grpName=item.getText(0);
				String grpReAddr=item.getText(1);
				
				//˫��ĳ��Ⱥ����ʾѡ�м�Ⱥ�ĳ�Ա��Ϣ
				table.removeAll();
				//ɾ��ԭ����Ϣ
				TableColumn[] allColumns=table.getColumns();
				for(int i=0;i<allColumns.length;i++){
					allColumns[i].dispose();
				}
				
				//�����µ���
				TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn.setWidth(100);
				tblclmnNewColumn.setText("��Ⱥ���");
			
				TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_1.setWidth(100);
				tblclmnNewColumn_1.setText("��Ա��ַ");
				
				TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_2.setWidth(100);
				tblclmnNewColumn_2.setText("��Ⱥid");
				
				TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_3.setWidth(100);
				tblclmnNewColumn_3.setText("tcp�˿ں�");
				
				
				//�����ô���
				/*
			  	TableItem item=new TableItem(table,0);
			  	item.setText(new String[]{grpName,"118.229.134.140"});
			  	TableItem item2=new TableItem(table,0);
			  	item2.setText(new String[]{"","118.229.134.250"});
				 */ 
			
				//���ýӿڹ��ܣ�ʵ�ּ�Ⱥ��Ա��ѯ
				MsgLookupGroupMember_ GroupMember=Interface.lookupGroupMember(grpName);
				Iterator<BrokerUnit> itr=GroupMember.members.iterator();
				
				BrokerUnit b1 = itr.next();
				TableItem ti1 = new TableItem(table, 0);
				ti1.setText(new String[]{grpName, b1.addr + "(���)", "" + b1.id, "" + b1.tPort});

				//����صĳ�Ա�б�,���±��
				while(itr.hasNext()){
					TableItem member=new TableItem(table,0);
					BrokerUnit aMember=itr.next();
					member.setText(new String[]{"",aMember.addr,""+aMember.id,""+aMember.tPort});
				}
			  //���¿���̨
				/*//���ýӿڹ��ܣ�ʵ�ּ�Ⱥ��Ա��ѯ
				MsgLookupGroupMember_ GroupMember=Interface.lookupGroupMember(grpName);
				Iterator<BrokerUnit> itr=GroupMember.members.iterator();
				int count=0;
				//����صĳ�Ա�б�,���±��
				while(itr.hasNext()){
					TableItem member=new TableItem(table,0);
					BrokerUnit aMember=itr.next();
					String addr;
					//����Ƿ��Ǵ���ַ
					if(aMember.addr.equals(grpReAddr)){
						addr=aMember.addr+"(����ַ)";
					}
					else{
						addr=aMember.addr;
					}
					
					//����Ƿ��ǵ�һ��
					if(count==0){
						
						member.setText(new String[]{grpName,addr,""+aMember.id,""+aMember.tPort});
					}
					else{
						member.setText(new String[]{"",addr,""+aMember.id,""+aMember.tPort});
					}
					count++;
				}*/
			
				
				
			
			}
			public void mouseDown(MouseEvent e){}
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//-------------------------------��Ӧ������¼�����--------------------------------------------//
		//���±��
		Date date = new Date();
		text.append("\n"+sdf.format(date)+"����ѯ�˼�Ⱥ��ע����Ϣ"+"\n");
	}
}
//---------------------------------------------------------------------//
class LookUpSub extends SelectionAdapter{
	private Text text;
	private Shell shell;
	private Table table;
	private Window win;
	private Data data;
	private String grpName;
    private AdminMgr Interface;
	LookUpSub(Shell shell, Text text, Table table,
			Window win, Data data,AdminMgr interactIF) {
		this.text=text;
		this.shell=shell;
		this.table=table;
		this.data=data;
		this.win=win;
		this.Interface=interactIF;
		// TODO Auto-generated constructor stub
	}
	public void widgetSelected(final SelectionEvent e) {
		InputDialog dialog = new InputDialog(shell,"��ѯ���Ա","�����������","",new MyValidator());
		
		if(dialog.open()==InputDialog.OK){
			grpName=dialog.getValue();
		
			if(data.getGroupIndex(grpName)==-1){
				MessageDialog.openInformation(shell, "��ʾ", "�����ڸ���");
			}
			else{
				//״̬��
				win.label_operate.setText("��ѯ��Ⱥ������Ϣ");
				//ͻ��������ؿ���̨
				win.tabFolder.setSelection(win.tabItem);
				
				List<GroupInfo> GroupList=new ArrayList<GroupInfo>();
				table.removeAll();
				TableColumn[] allColumns=table.getColumns();
				
				for(int i=0;i<allColumns.length;i++){
					allColumns[i].dispose();
				}
				
				
				TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn.setWidth(100);
				tblclmnNewColumn.setText("��Ⱥ���");
				
				TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_1.setWidth(100);
				tblclmnNewColumn_1.setText("��Ⱥ������Ϣ");
				
				/*
				//������
				  TableItem item=new TableItem(table,0);
				  item.setText(new String[]{grpName,"����Ԥ��"});
				  TableItem item2=new TableItem(table,0);
				  item2.setText(new String[]{"","����״��"});
				//����END
				*/
				
				SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
				Date date = new Date();
				text.append("\n"+sdf.format(date)+"����ѯ�˼�Ⱥ"+grpName+"�Ķ�����Ϣ\n");
				//���ýӿ�
					
//				String[] GroupMember=Interface.lookupGroupSubscriptions(grpName);
//				if(GroupMember==null){
//					data.removeGroup(grpName);
//					Interface.groups.remove(grpName);
//					Interface.GroupsChangeNtfyBkp();
//					MessageDialog.openInformation(shell, "��ʾ", "�����ڸü�Ⱥ");
//				}else{
//					//����table
//					for(int i=0;i<GroupMember.length;i++){
//						TableItem member=new TableItem(table,0);
//						if(i==0){
//							member.setText(new String[]{grpName,GroupMember[i]});
//						}
//						else{
//							member.setText(new String[]{"",GroupMember[i]});
//						}//if else
//					}//for	
//				}
			}
		}
	}
}

class ShowDefaultConfig extends SelectionAdapter{
	private Shell shell;
	ShowDefaultConfig(Shell shell) {
		// TODO Auto-generated constructor stub
		this.shell=shell;
	}
	

	public void widgetSelected(final SelectionEvent e) {
		
		shell.setEnabled(false);
		new Configuration("Default",null).open();
		shell.setEnabled(true);
		shell.setActive();
		
	}
}

class GetGroupConfig extends SelectionAdapter{
	private Shell shell;
	private Table table;
	private Text text;
	private Window win;
	private FileOperation configFile=new FileOperation();
	
	GetGroupConfig(Shell shell,Table table,Text text, Window win){
		this.shell=shell;
		this.table=table;
		this.text=text;
		this.win=win;
	}
	
	public void widgetSelected(final SelectionEvent e) {
			int selection=table.getSelectionIndex();

			if(selection==-1){
				MessageDialog.openInformation(shell, "", "���ڱ����ѡ��һ���¼");
			}
			else{ 
				TableItem tmpItem=table.getItem(selection);
				String grpName=tmpItem.getText(0);
				if(grpName.equals("")){
					MessageDialog.openInformation(shell, "", "��ѡ��һ������Ʋ�Ϊ�յļ�¼");
				}
				else{
					GroupConfiguration initInfo=new GroupConfiguration();
					if(configFile.LookForConfigFile(grpName)==null){
						initInfo=configFile.ReadGroupConfiguration("Default");
					}
					else{
						initInfo=configFile.ReadGroupConfiguration(grpName);
					}
					String configInfo = new String("��Ⱥ��ƣ�"+initInfo.GroupName+"\n"
							+"����ַ��"+initInfo.repAddr+"\n"
							+"TCP�˿ںţ�"+initInfo.tPort+"\n"
							+"�鲥�˿ںţ�"+initInfo.uPort+"\n"
							+"�ӽڵ���Ŀ��"+initInfo.childrenSize+"\n"
							+"���볬ʱʱ�䣺"+initInfo.joinTimes+"\n"
							+"�������С��"+initInfo.synPeriod+"\n"
							+"�ж�ʧЧ��ֵ��"+initInfo.lostThreshold+"\n"
							+"ɨ�����ڣ�"+initInfo.scanPeriod+"\n"
							+"�������ڣ�"+initInfo.sendPeriod+"\n");
					
					//״̬����ʾ
					win.label_operate.setText("�鿴��Ⱥ"+grpName+"��������Ϣ");
					//�л�������̨
					win.tabFolder.setSelection(win.tabItem);
					
					//text.append("��"+grpName+"���������ò�ѯ,�������£�\n"+configInfo);
					//�öԻ�����ʾ������Ϣ
					MessageDialog.openInformation(shell, "��Ⱥ"+grpName+"��������Ϣ", configInfo);
					
					SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
					Date date = new Date();
					text.append("\n"+sdf.format(date)+"����ѯ�˼�Ⱥ"+grpName+"��������Ϣ\n");
				}
			}
	}
}



class SetAddressEvent extends SelectionAdapter{
	private AdminMgr IF;
	
	
	SetAddressEvent(AdminMgr IF){
		this.IF=IF;
	}
	public void widgetSelected(final SelectionEvent e) {
		new SetAddress(IF).open();
		
	}
}

class ConvertWindowEvent extends SelectionAdapter{
	private Window win;
	
	ConvertWindowEvent(Window win){
		this.win=win;
	}
	
	public void widgetSelected(final SelectionEvent e) {
		win.tabFolder.setSelection(win.tabItem);
	}
}

//------------------------------------------------------------------------//
class LookUpMem extends SelectionAdapter{
	private Table table;
	private Text text;
	private Shell shell;
	private String grpName;
	private Data data;
	private Window win;
	private AdminMgr Interface;
	LookUpMem(Shell shell,Text text,Table table,Window win,Data data, AdminMgr interactIF){
		this.shell=shell;
		this.table=table;
		this.text=text;
		this.win=win;
		this.data=data;
		this.Interface=interactIF;
	}
	public void widgetSelected(final SelectionEvent e) {
		InputDialog dialog = new InputDialog(shell,"��ѯ���Ա","�����������","",new MyValidator());
		if(dialog.open()==InputDialog.OK){
			grpName=dialog.getValue();
		    int index=data.getGroupIndex(grpName);
			if(index==-1&&grpName!=null){
				MessageDialog.openInformation(shell, "��ʾ", "�����ڸ���");
			}
			else{
				//��ʾ״̬��
				win.label_operate.setText("��ѯ��Ⱥ��Ա");
				//���л������
				win.tabFolder.setSelection(win.tabItem);
				//���ԭ����¼
				table.removeAll();
				//ɾ��ԭ����Ϣ
				TableColumn[] allColumns=table.getColumns();
				for(int i=0;i<allColumns.length;i++){
					allColumns[i].dispose();
				}
				
				//�����µ���
				TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn.setWidth(100);
				tblclmnNewColumn.setText("��Ⱥ���");
			
				TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_1.setWidth(100);
				tblclmnNewColumn_1.setText("��Ա��ַ");
				
				TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_2.setWidth(100);
				tblclmnNewColumn_2.setText("��Ⱥid");
				
				TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_3.setWidth(100);
				tblclmnNewColumn_3.setText("tcp�˿ں�");
				
				//��ȡ���뼯Ⱥ�Ĵ���ַ
//				List<GroupInfo> GroupList=data.getAllGroup();
//				GroupInfo theGroup=GroupList.get(index);
//				String grpReAddr=theGroup.GroupAddress;
				
				//�����ô���
				/*
			  	TableItem item=new TableItem(table,0);
			  	item.setText(new String[]{grpName,"118.229.134.140"});
			  	TableItem item2=new TableItem(table,0);
			  	item2.setText(new String[]{"","118.229.134.250"});
				 */ 
			
				
				//���ýӿڹ��ܣ�ʵ�ּ�Ⱥ��Ա��ѯ
				MsgLookupGroupMember_ GroupMember=Interface.lookupGroupMember(grpName);
				if(GroupMember==null){
					data.removeGroup(grpName);
					Interface.groups.remove(grpName);
					Interface.GroupsChangeNtfyBkp();
					MessageDialog.openInformation(shell, "��ʾ", "�����ڸü�Ⱥ");
				}else{
					Iterator<BrokerUnit> itr=GroupMember.members.iterator();
					
					BrokerUnit b1 = itr.next();
					TableItem ti1 = new TableItem(table, 0);
					ti1.setText(new String[]{grpName, b1.addr + "(���)", "" + b1.id, "" + b1.tPort});

					//����صĳ�Ա�б�,���±��
					while(itr.hasNext()){
						TableItem member=new TableItem(table,0);
						BrokerUnit aMember=itr.next();
						member.setText(new String[]{"",aMember.addr,""+aMember.id,""+aMember.tPort});
					}
				  //���¿���̨
				
					SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
					Date date = new Date();
					text.append("\n"+sdf.format(date)+"����ѯ�˼�Ⱥ"+grpName+"�ĳ�Ա��Ϣ\n");
				}
				
			}
		}
	}
}
//----------------------------------------------------------------//
class LookUpMemSub extends SelectionAdapter{
	private Table table;
	private Text text;
	private Shell shell;
	private String grpName;
	private String hstAddr;;
	private Data data;
	private Window win;
	private AdminMgr Interface;
	
	LookUpMemSub(Shell shell,Text text,Table table,Window win,Data data,AdminMgr interactIF){
		this.shell=shell;
		this.table=table;
		this.text=text;
		this.win=win;
		this.data=data;
		this.Interface=interactIF;
	}
	
	public void widgetSelected(final SelectionEvent e) {
		InputDialog dialog = new InputDialog(shell,"��ѯ�ض����Ա������Ϣ","�����뼯Ⱥ���","",new MyValidator());
		
		if(dialog.open()==InputDialog.OK){
			grpName=dialog.getValue();
			
		/*
			Scanner infoScan=new Scanner(grpAndhst);
			infoScan.useDelimiter(",");
			grpName=infoScan.next();
			hstAddr=infoScan.next();
		*/
			if(data.getGroupIndex(grpName)==-1){
				MessageDialog.openInformation(shell, "��ʾ", "�����ڸ���");
			}
			else{
				
				
				InputDialog dialog1 = new InputDialog(shell,"��ѯ�ض����Ա������Ϣ","�����뼯Ⱥ�ڳ�Ա��ַ","",new MyValidator());
				
				if(dialog1.open()==InputDialog.OK){
					
					hstAddr=dialog1.getValue();
					
					//��ʾ״̬��
					win.label_operate.setText("��ѯ��Ⱥ��Ա������Ϣ");
					//ͻ��������ؿ���̨
					win.tabFolder.setSelection(win.tabItem);
					
					List<GroupInfo> GroupList=new ArrayList<GroupInfo>();
					table.removeAll();
					TableColumn[] allColumns=table.getColumns();
					
					for(int i=0;i<allColumns.length;i++){
						allColumns[i].dispose();
					}
					
					
					TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
					tblclmnNewColumn.setWidth(100);
					tblclmnNewColumn.setText("��Ⱥ���");
					
					TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
					tblclmnNewColumn_1.setWidth(100);
					tblclmnNewColumn_1.setText("��Ⱥ��Ա��ַ");
					
					TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
					tblclmnNewColumn_2.setWidth(100);
					tblclmnNewColumn_2.setText("������Ϣ");
					
					//��ʾ����̨
					//���±��
					SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
					Date date = new Date();
					text.append("\n"+sdf.format(date)+"����ѯ�˼�Ⱥ"+grpName+"�г�Ա"+hstAddr+"�Ķ�����Ϣ"+"\n");
					//���ýӿ�
					
						
//					String[] GroupMember=Interface.lookupMemberSubscriptions(grpName,hstAddr);
//					if(GroupMember==null){
//						MessageDialog.openInformation(shell, "��ʾ", "�ü�Ⱥ�ڲ����������Ա��ַ");
//					}else{
//						for(int i=0;i<GroupMember.length;i++){
//							TableItem member=new TableItem(table,0);
//							if(i==0){
//								member.setText(new String[]{grpName,hstAddr,GroupMember[i]});
//							}
//							else{
//								member.setText(new String[]{"","",GroupMember[i]});
//							}
//						}//for
						
//					}
				}
			}
		}
	}
}
//---------------------------------------------------------------//
class ShowHistory extends SelectionAdapter{
	private Text text;
	private Window win;
	private Data data;
	
	ShowHistory(Text text,Window win,Data data){
		this.text=text;
		this.win=win;
		this.data=data;
	}
	public void widgetSelected(final SelectionEvent e) {
		List<String> HistoryList=new ArrayList<String>();
		HistoryList=data.getHistory();
		
		win.tabFolder.setSelection(win.tabItem);
		
		win.label_operate.setText("�鿴��ʷ��¼");
		
		Iterator<String> itr=HistoryList.iterator();
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
		Date date = new Date();
		text.append("\n"+sdf.format(date)+":�鿴��ʷ��¼\n");
		while(itr.hasNext()){
			String history=itr.next();
			text.append("\n"+history+"\n");
		}
		
	}
}

//-----------------------------------------------------------------//
class MyValidator implements IInputValidator{

	@Override
	public String isValid(String newText) {
		// TODO Auto-generated method stub
		if (newText==null){
			return "��������ֵ";
		}
		
		return null;
		//return "���ݵ�ǰ����Ϣ���������";
	}
	
}