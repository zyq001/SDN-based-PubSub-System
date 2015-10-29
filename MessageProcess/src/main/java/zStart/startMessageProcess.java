package zStart;

import Configuration.Configure;
import OvsInitModule.OvsInit;
import threadFolder.RateAdjust;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class startMessageProcess {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		OvsInit init = new OvsInit();
		init.initOvs();
		System.out.println("init finished !");

		RateAdjust process = new RateAdjust();
		//process.start();

		ThreadPoolExecutor deliverWorker = new ThreadPoolExecutor(1, 1, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.CallerRunsPolicy());
		while (true) {
			try {
				Thread.sleep(Configure.sleepingTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			deliverWorker.execute(process);
		}

//		System.out.println(oi.getAllBridge());
//		System.out.println(oi.getAllBridgeInfo());
//		System.out.println(oi.getBridgeDetailedInfo("br0"));
//		
//		System.out.println(oi.getBridgeIfaces("br0"));
//		System.out.println(oi.getBridgePorts("br0"));
//		System.out.println(oi.getBridgePortDetailedInfo("br0"));
//		System.out.println(oi.getBridgeTablesInfo("br0"));
	}

}
