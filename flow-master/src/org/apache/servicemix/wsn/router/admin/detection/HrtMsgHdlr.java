package org.apache.servicemix.wsn.router.admin.detection;


public interface HrtMsgHdlr {

	void sendHrtMsg();
	
	void synTopoInfo();
	
	void lost(String indicator);
	
	int BackupIsPrimary();
	
}
