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
			MessageDialog.openInformation(shell, "", "锟斤拷锟节憋拷锟斤拷锟窖★拷锟揭伙拷锟斤拷录");
		}
		else{ 
			TableItem tmpItem=table.getItem(selection);
			String grpName=tmpItem.getText(0);
			if(grpName.equals("")){
				MessageDialog.openInformation(shell, "", "锟斤拷选锟斤拷一锟斤拷锟斤拷锟斤拷撇锟轿拷盏募锟铰�");
			}
			else{
				shell.setEnabled(false);
				new Configuration(grpName, interactIF).open();
				shell.setEnabled(true);
				shell.setActive();
				
				//锟斤拷示状态锟斤拷
				win.label_operate.setText("锟斤拷锟矫硷拷群"+grpName );
				//锟叫伙拷锟斤拷锟斤拷锟斤拷台
				win.tabFolder.setSelection(win.tabItem);
				
				
				SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
				Date date = new Date();
				text.append("\n"+sdf.format(date)+":锟斤拷"+grpName+"锟斤拷锟斤拷锟斤拷锟斤拷\n");
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
		msg.setText("锟斤拷锟节憋拷锟斤拷锟铰憋拷");
		msg.setMessage("锟芥本:Version1.2\n锟斤拷锟斤拷锟斤拷:锟斤拷思锟斤拷\t\n\n2011.5.20");
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
		text.append("\n"+sdf.format(date)+"锟斤拷锟斤拷询锟斤拷前时锟戒："+sdf.format(date)+"\n");
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
		//状态锟斤拷
		win.label_event.setText("锟斤拷询锟斤拷群注锟斤拷锟斤拷息");
		//突锟斤拷锟斤拷锟斤拷锟斤拷乜锟斤拷锟教�
		win.tabFolder.setSelection(win.tabItem);
		
		List<GroupInfo> GroupList=new ArrayList<GroupInfo>();
		//删锟斤拷原锟叫的憋拷锟斤拷录锟酵憋拷锟斤拷锟�
		table.removeAll();
		TableColumn[] allColumns=table.getColumns();
		
		for(int i=0;i<allColumns.length;i++){
			allColumns[i].dispose();
		}
		
		//锟斤拷始锟斤拷锟铰碉拷锟斤拷
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("锟斤拷群锟斤拷锟�");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("锟斤拷群锟斤拷锟斤拷址");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("注锟斤拷时锟斤拷");
		
		
		
		text.append("锟介看锟斤拷群锟斤拷息\n");
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
		
		//-----------------------------锟斤拷锟斤拷锟斤拷录锟�--------------------------------------------//
		table.addMouseListener(new MouseListener(){
			public void mouseDoubleClick(MouseEvent e){
				int sellndex=table.getSelectionIndex();
				TableItem item = table.getItem(sellndex);
				
				String grpName=item.getText(0);
				String grpReAddr=item.getText(1);
				
				//双锟斤拷某锟斤拷群锟斤拷锟斤拷示选锟叫硷拷群锟侥筹拷员锟斤拷息
				table.removeAll();
				//删锟斤拷原锟斤拷锟斤拷息
				TableColumn[] allColumns=table.getColumns();
				for(int i=0;i<allColumns.length;i++){
					allColumns[i].dispose();
				}
				
				//锟斤拷锟斤拷锟铰碉拷锟斤拷
				TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn.setWidth(100);
				tblclmnNewColumn.setText("锟斤拷群锟斤拷锟�");
			
				TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_1.setWidth(100);
				tblclmnNewColumn_1.setText("锟斤拷员锟斤拷址");
				
				TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_2.setWidth(100);
				tblclmnNewColumn_2.setText("锟斤拷群id");
				
				TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_3.setWidth(100);
				tblclmnNewColumn_3.setText("tcp锟剿口猴拷");
				
				
				//锟斤拷锟斤拷锟矫达拷锟斤拷
				/*
			  	TableItem item=new TableItem(table,0);
			  	item.setText(new String[]{grpName,"118.229.134.140"});
			  	TableItem item2=new TableItem(table,0);
			  	item2.setText(new String[]{"","118.229.134.250"});
				 */ 
			
				//锟斤拷锟矫接口癸拷锟杰ｏ拷实锟街硷拷群锟斤拷员锟斤拷询
				MsgLookupGroupMember_ GroupMember=Interface.lookupGroupMember(grpName);
				Iterator<BrokerUnit> itr=GroupMember.members.iterator();
				
				BrokerUnit b1 = itr.next();
				TableItem ti1 = new TableItem(table, 0);
				ti1.setText(new String[]{grpName, b1.addr + "(锟斤拷锟�)", "" + b1.id, "" + b1.tPort});

				//锟斤拷锟斤拷氐某锟皆憋拷斜锟�,锟斤拷锟铰憋拷锟�
				while(itr.hasNext()){
					TableItem member=new TableItem(table,0);
					BrokerUnit aMember=itr.next();
					member.setText(new String[]{"",aMember.addr,""+aMember.id,""+aMember.tPort});
				}
			  //锟斤拷锟铰匡拷锟斤拷台
				/*//锟斤拷锟矫接口癸拷锟杰ｏ拷实锟街硷拷群锟斤拷员锟斤拷询
				MsgLookupGroupMember_ GroupMember=Interface.lookupGroupMember(grpName);
				Iterator<BrokerUnit> itr=GroupMember.members.iterator();
				int count=0;
				//锟斤拷锟斤拷氐某锟皆憋拷斜锟�,锟斤拷锟铰憋拷锟�
				while(itr.hasNext()){
					TableItem member=new TableItem(table,0);
					BrokerUnit aMember=itr.next();
					String addr;
					//锟斤拷锟斤拷欠锟斤拷谴锟斤拷锟街�
					if(aMember.addr.equals(grpReAddr)){
						addr=aMember.addr+"(锟斤拷锟斤拷址)";
					}
					else{
						addr=aMember.addr;
					}
					
					//锟斤拷锟斤拷欠锟斤拷堑锟揭伙拷锟�
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
		
		//-------------------------------锟斤拷应锟斤拷锟斤拷锟斤拷录锟斤拷锟斤拷锟�--------------------------------------------//
		//锟斤拷锟铰憋拷锟�
		Date date = new Date();
		text.append("\n"+sdf.format(date)+"锟斤拷锟斤拷询锟剿硷拷群锟斤拷注锟斤拷锟斤拷息"+"\n");
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
		InputDialog dialog = new InputDialog(shell,"锟斤拷询锟斤拷锟皆�","锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�","",new MyValidator());
		
		if(dialog.open()==InputDialog.OK){
			grpName=dialog.getValue();
		
			if(data.getGroupIndex(grpName)==-1){
				MessageDialog.openInformation(shell, "锟斤拷示", "锟斤拷锟斤拷锟节革拷锟斤拷");
			}
			else{
				//状态锟斤拷
				win.label_operate.setText("锟斤拷询锟斤拷群锟斤拷锟斤拷锟斤拷息");
				//突锟斤拷锟斤拷锟斤拷锟斤拷乜锟斤拷锟教�
				win.tabFolder.setSelection(win.tabItem);
				
				List<GroupInfo> GroupList=new ArrayList<GroupInfo>();
				table.removeAll();
				TableColumn[] allColumns=table.getColumns();
				
				for(int i=0;i<allColumns.length;i++){
					allColumns[i].dispose();
				}
				
				
				TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn.setWidth(100);
				tblclmnNewColumn.setText("锟斤拷群锟斤拷锟�");
				
				TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_1.setWidth(100);
				tblclmnNewColumn_1.setText("锟斤拷群锟斤拷锟斤拷锟斤拷息");
				
				/*
				//锟斤拷锟斤拷锟斤拷
				  TableItem item=new TableItem(table,0);
				  item.setText(new String[]{grpName,"锟斤拷锟斤拷预锟斤拷"});
				  TableItem item2=new TableItem(table,0);
				  item2.setText(new String[]{"","锟斤拷锟斤拷状锟斤拷"});
				//锟斤拷锟斤拷END
				*/
				
				SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
				Date date = new Date();
				text.append("\n"+sdf.format(date)+"锟斤拷锟斤拷询锟剿硷拷群"+grpName+"锟侥讹拷锟斤拷锟斤拷息\n");
				//锟斤拷锟矫接匡拷
					
//				String[] GroupMember=Interface.lookupGroupSubscriptions(grpName);
//				if(GroupMember==null){
//					data.removeGroup(grpName);
//					Interface.groups.remove(grpName);
//					Interface.GroupsChangeNtfyBkp();
//					MessageDialog.openInformation(shell, "锟斤拷示", "锟斤拷锟斤拷锟节该硷拷群");
//				}else{
//					//锟斤拷锟斤拷table
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
				MessageDialog.openInformation(shell, "", "锟斤拷锟节憋拷锟斤拷锟窖★拷锟揭伙拷锟斤拷录");
			}
			else{ 
				TableItem tmpItem=table.getItem(selection);
				String grpName=tmpItem.getText(0);
				if(grpName.equals("")){
					MessageDialog.openInformation(shell, "", "锟斤拷选锟斤拷一锟斤拷锟斤拷锟斤拷撇锟轿拷盏募锟铰�");
				}
				else{
					GroupConfiguration initInfo=new GroupConfiguration();
					if(configFile.LookForConfigFile(grpName)==null){
						initInfo=configFile.ReadGroupConfiguration("Default");
					}
					else{
						initInfo=configFile.ReadGroupConfiguration(grpName);
					}
					String configInfo = new String("锟斤拷群锟斤拷疲锟�"+initInfo.GroupName+"\n"
							+"锟斤拷锟斤拷址锟斤拷"+initInfo.repAddr+"\n"
							+"TCP锟剿口号ｏ拷"+initInfo.tPort+"\n"
							+"锟介播锟剿口号ｏ拷"+initInfo.uPort+"\n"
							+"锟接节碉拷锟斤拷目锟斤拷"+initInfo.childrenSize+"\n"
							+"锟斤拷锟诫超时时锟戒："+initInfo.joinTimes+"\n"
							+"锟斤拷锟斤拷锟斤拷锟叫★拷锟�"+initInfo.synPeriod+"\n"
							+"锟叫讹拷失效锟斤拷值锟斤拷"+initInfo.lostThreshold+"\n"
							+"扫锟斤拷锟斤拷锟节ｏ拷"+initInfo.scanPeriod+"\n"
							+"锟斤拷锟斤拷锟斤拷锟节ｏ拷"+initInfo.sendPeriod+"\n");
					
					//状态锟斤拷锟斤拷示
					win.label_operate.setText("锟介看锟斤拷群"+grpName+"锟斤拷锟斤拷锟斤拷锟斤拷息");
					//锟叫伙拷锟斤拷锟斤拷锟斤拷台
					win.tabFolder.setSelection(win.tabItem);
					
					//text.append("锟斤拷"+grpName+"锟斤拷锟斤拷锟斤拷锟斤拷锟矫诧拷询,锟斤拷锟斤拷锟斤拷锟铰ｏ拷\n"+configInfo);
					//锟矫对伙拷锟斤拷锟斤拷示锟斤拷锟斤拷锟斤拷息
					MessageDialog.openInformation(shell, "锟斤拷群"+grpName+"锟斤拷锟斤拷锟斤拷锟斤拷息", configInfo);
					
					SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
					Date date = new Date();
					text.append("\n"+sdf.format(date)+"锟斤拷锟斤拷询锟剿硷拷群"+grpName+"锟斤拷锟斤拷锟斤拷锟斤拷息\n");
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
		InputDialog dialog = new InputDialog(shell,"锟斤拷询锟斤拷锟皆�","锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�","",new MyValidator());
		if(dialog.open()==InputDialog.OK){
			grpName=dialog.getValue();
		    int index=data.getGroupIndex(grpName);
			if(index==-1&&grpName!=null){
				MessageDialog.openInformation(shell, "锟斤拷示", "锟斤拷锟斤拷锟节革拷锟斤拷");
			}
			else{
				//锟斤拷示状态锟斤拷
				win.label_operate.setText("锟斤拷询锟斤拷群锟斤拷员");
				//锟斤拷锟叫伙拷锟斤拷锟斤拷锟�
				win.tabFolder.setSelection(win.tabItem);
				//锟斤拷锟皆拷锟斤拷锟铰�
				table.removeAll();
				//删锟斤拷原锟斤拷锟斤拷息
				TableColumn[] allColumns=table.getColumns();
				for(int i=0;i<allColumns.length;i++){
					allColumns[i].dispose();
				}
				
				//锟斤拷锟斤拷锟铰碉拷锟斤拷
				TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn.setWidth(100);
				tblclmnNewColumn.setText("锟斤拷群锟斤拷锟�");
			
				TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_1.setWidth(100);
				tblclmnNewColumn_1.setText("锟斤拷员锟斤拷址");
				
				TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_2.setWidth(100);
				tblclmnNewColumn_2.setText("锟斤拷群id");
				
				TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
				tblclmnNewColumn_3.setWidth(100);
				tblclmnNewColumn_3.setText("tcp锟剿口猴拷");
				
				//锟斤拷取锟斤拷锟诫集群锟侥达拷锟斤拷址
//				List<GroupInfo> GroupList=data.getAllGroup();
//				GroupInfo theGroup=GroupList.get(index);
//				String grpReAddr=theGroup.GroupAddress;
				
				//锟斤拷锟斤拷锟矫达拷锟斤拷
				/*
			  	TableItem item=new TableItem(table,0);
			  	item.setText(new String[]{grpName,"118.229.134.140"});
			  	TableItem item2=new TableItem(table,0);
			  	item2.setText(new String[]{"","118.229.134.250"});
				 */ 
			
				
				//锟斤拷锟矫接口癸拷锟杰ｏ拷实锟街硷拷群锟斤拷员锟斤拷询
				MsgLookupGroupMember_ GroupMember=Interface.lookupGroupMember(grpName);
				if(GroupMember==null){
					data.removeGroup(grpName);
					Interface.groups.remove(grpName);
					Interface.GroupsChangeNtfyBkp();
					MessageDialog.openInformation(shell, "锟斤拷示", "锟斤拷锟斤拷锟节该硷拷群");
				}else{
					Iterator<BrokerUnit> itr=GroupMember.members.iterator();
					
					BrokerUnit b1 = itr.next();
					TableItem ti1 = new TableItem(table, 0);
					ti1.setText(new String[]{grpName, b1.addr + "(锟斤拷锟�)", "" + b1.id, "" + b1.tPort});

					//锟斤拷锟斤拷氐某锟皆憋拷斜锟�,锟斤拷锟铰憋拷锟�
					while(itr.hasNext()){
						TableItem member=new TableItem(table,0);
						BrokerUnit aMember=itr.next();
						member.setText(new String[]{"",aMember.addr,""+aMember.id,""+aMember.tPort});
					}
				  //锟斤拷锟铰匡拷锟斤拷台
				
					SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
					Date date = new Date();
					text.append("\n"+sdf.format(date)+"锟斤拷锟斤拷询锟剿硷拷群"+grpName+"锟侥筹拷员锟斤拷息\n");
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
		InputDialog dialog = new InputDialog(shell,"锟斤拷询锟截讹拷锟斤拷锟皆憋拷锟斤拷锟斤拷锟较�","锟斤拷锟斤拷锟诫集群锟斤拷锟�","",new MyValidator());
		
		if(dialog.open()==InputDialog.OK){
			grpName=dialog.getValue();
			
		/*
			Scanner infoScan=new Scanner(grpAndhst);
			infoScan.useDelimiter(",");
			grpName=infoScan.next();
			hstAddr=infoScan.next();
		*/
			if(data.getGroupIndex(grpName)==-1){
				MessageDialog.openInformation(shell, "锟斤拷示", "锟斤拷锟斤拷锟节革拷锟斤拷");
			}
			else{
				
				
				InputDialog dialog1 = new InputDialog(shell,"锟斤拷询锟截讹拷锟斤拷锟皆憋拷锟斤拷锟斤拷锟较�","锟斤拷锟斤拷锟诫集群锟节筹拷员锟斤拷址","",new MyValidator());
				
				if(dialog1.open()==InputDialog.OK){
					
					hstAddr=dialog1.getValue();
					
					//锟斤拷示状态锟斤拷
					win.label_operate.setText("锟斤拷询锟斤拷群锟斤拷员锟斤拷锟斤拷锟斤拷息");
					//突锟斤拷锟斤拷锟斤拷锟斤拷乜锟斤拷锟教�
					win.tabFolder.setSelection(win.tabItem);
					
					List<GroupInfo> GroupList=new ArrayList<GroupInfo>();
					table.removeAll();
					TableColumn[] allColumns=table.getColumns();
					
					for(int i=0;i<allColumns.length;i++){
						allColumns[i].dispose();
					}
					
					
					TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
					tblclmnNewColumn.setWidth(100);
					tblclmnNewColumn.setText("锟斤拷群锟斤拷锟�");
					
					TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
					tblclmnNewColumn_1.setWidth(100);
					tblclmnNewColumn_1.setText("锟斤拷群锟斤拷员锟斤拷址");
					
					TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
					tblclmnNewColumn_2.setWidth(100);
					tblclmnNewColumn_2.setText("锟斤拷锟斤拷锟斤拷息");
					
					//锟斤拷示锟斤拷锟斤拷台
					//锟斤拷锟铰憋拷锟�
					SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
					Date date = new Date();
					text.append("\n"+sdf.format(date)+"锟斤拷锟斤拷询锟剿硷拷群"+grpName+"锟叫筹拷员"+hstAddr+"锟侥讹拷锟斤拷锟斤拷息"+"\n");
					//锟斤拷锟矫接匡拷
					
						
//					String[] GroupMember=Interface.lookupMemberSubscriptions(grpName,hstAddr);
//					if(GroupMember==null){
//						MessageDialog.openInformation(shell, "锟斤拷示", "锟矫硷拷群锟节诧拷锟斤拷锟斤拷锟斤拷锟斤拷锟皆憋拷锟街�");
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
		
		win.label_operate.setText("锟介看锟斤拷史锟斤拷录");
		
		Iterator<String> itr=HistoryList.iterator();
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm yyyy-M-dd");
		Date date = new Date();
		text.append("\n"+sdf.format(date)+":锟介看锟斤拷史锟斤拷录\n");
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
			return "锟斤拷锟斤拷锟斤拷锟斤拷值";
		}
		
		return null;
		//return "锟斤拷锟捷碉拷前锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟斤拷锟�";
	}
	
}