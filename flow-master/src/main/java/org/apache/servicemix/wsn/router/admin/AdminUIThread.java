package org.apache.servicemix.wsn.router.admin;

import org.apache.servicemix.wsn.router.design.PSManagerUI;

import java.util.Iterator;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.eclipse.swt.widgets.Display;

public class AdminUIThread implements Runnable {

	private AdminMgr Amgr;

	public AdminUIThread(AdminMgr Amgr) {
		this.Amgr = Amgr;
	}

	public void run() {
		Amgr.ui = new PSManagerUI(Amgr);

		Iterator<GroupUnit> Itr = Amgr.groups.values().iterator();
		while (Itr.hasNext()) {
			GroupUnit g = Itr.next();
			Amgr.ui.recoverGroup(g);// 恢复集群信息，便于管理员查询
		}
		// Display.getDefault().asyncExec(new Runnable(){

		//	@Override
		//	public void run() {
				// TODO Auto-generated method stub
				Amgr.ui.open();
		//	}		
		// });
	}

}
