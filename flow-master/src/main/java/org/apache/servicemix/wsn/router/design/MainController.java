package org.apache.servicemix.wsn.router.design;

public class MainController {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		PSManagerUI ui=new PSManagerUI();
//		ui.open();
//		
		FileOperation a = new FileOperation();

		GroupConfiguration grpConfig = new GroupConfiguration();
		grpConfig.synPeriod = 16;
		grpConfig.childrenSize = 20;
		grpConfig.GroupName = "G1";
		grpConfig.joinTimes = 20;
		grpConfig.lostThreshold = 100;
		grpConfig.mutltiAddr = "112.229.336.126";
		grpConfig.tPort = 65535;
		grpConfig.uPort = 65589;
		grpConfig.repAddr = "127.0.0.1";
		grpConfig.scanPeriod = 10;
		grpConfig.sendPeriod = 20;

		a.WriteGroupConfiguration(grpConfig);
		//grpConfig=a.ReadGroupConfiguration("G1");
		System.exit(0);

	}

}
