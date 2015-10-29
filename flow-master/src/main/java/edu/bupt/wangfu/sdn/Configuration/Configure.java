package edu.bupt.wangfu.sdn.Configuration;

public class Configure {


	public static final long bandWidth = 3600000;

	public static final int pckNumLimit = 20000;

	public static final double healthRate = 0.5;
	public static final double sickRate = 1.5;

	public static int sleepingTime = 50; // the unit is millisecond

	public static String sflowServer;

	public static String floodlightIP;

	public static String getSflowServer() {
		return sflowServer;
	}

	public static void setSflowServer(String sflowServer) {
		Configure.sflowServer = sflowServer;
	}

	public static String getFloodlightIP() {
		return floodlightIP;
	}

	public static void setFloodlightIP(String floodlightIP) {
		Configure.floodlightIP = floodlightIP;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
