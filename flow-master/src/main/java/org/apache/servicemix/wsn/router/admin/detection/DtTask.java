package org.apache.servicemix.wsn.router.admin.detection;

import java.util.TimerTask;

public class DtTask extends TimerTask {

	private DtAction action;
	private int type;

	public DtTask(DtAction action, int type) {
		this.action = action;
		this.type = type;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		action.action(type);

	}

}
