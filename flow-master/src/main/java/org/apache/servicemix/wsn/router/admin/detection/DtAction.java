package org.apache.servicemix.wsn.router.admin.detection;

public interface DtAction {

	int SEND = 0;

	int SCAN = 1;

	int SYN = 2;

	public void action(int type);

}
