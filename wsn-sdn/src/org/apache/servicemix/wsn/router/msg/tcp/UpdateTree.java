package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.apache.servicemix.wsn.push.SendNotification;
import org.apache.servicemix.wsn.router.mgr.RtMgr;
import org.apache.servicemix.wsn.router.mgr.base.AState;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;

public class UpdateTree implements Serializable {
	/**
	 * 管理员通知更新主题树
	 */
	private static final long serialVersionUID = 1L;
	String newName;
	String oldName;
	int change; // 0 for change, 1 for delete
	long updateTime;
	
	@SuppressWarnings("static-access")
	public void processRepMsg(ObjectInputStream ois,
			ObjectOutputStream oos, Socket s, UpdateTree ut) {
		System.out.println("Topic tree is updated");
		AState state = RtMgr.getInstance().getState();
		if(ut.updateTime <= state.getTreeTime()) {
			return;
		}
		state.setTreeTime(ut.updateTime);
		SendNotification SN = new SendNotification();
		try {
			SN.update(ut.newName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if((ut.newName != null &&ut.newName.length()>0) || (ut.oldName != null && ut.oldName.length()>0)) {
			if(ut.change == 0) {
				ShorenUtils.changeName(ut.oldName, ut.newName);
				RtMgr.changeTopicName(ut.oldName, ut.newName);
			} else {
				ShorenUtils.deleteName(ut.oldName);
			}
		}

		state.sendObjectToNeighbors(ut);
		state.spreadInLocalGroup(ut);
	}
	
	@SuppressWarnings("static-access")
	public void processRegMsg(UpdateTree ut) {
		AState state = RtMgr.getInstance().getState();
		if(ut.updateTime <= state.getTreeTime()) {
			return;
		}
		state.setTreeTime(ut.updateTime);
		SendNotification SN = new SendNotification();
		try {
			SN.update(ut.newName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if((ut.newName != null &&ut.newName.length()>0) || (ut.oldName != null && ut.oldName.length()>0)) {
			if(ut.change == 0) {
				ShorenUtils.changeName(ut.oldName, ut.newName);
			} else {
				ShorenUtils.deleteName(ut.oldName);
			}
		}
	}
}