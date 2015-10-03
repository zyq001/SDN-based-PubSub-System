package threadFolder;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import Configuration.Configure;
import InfoCollect.OvsInfo;
import OvsOperation.OvsOperate;
import floodlight.Process;

public class RateAdjust2 extends Thread {

	public ConcurrentHashMap<Double,Integer> currentRate = new ConcurrentHashMap<Double,Integer>();
	OvsInfo oi = new OvsInfo();
	OvsOperate oo = new OvsOperate();
	Process pro = new Process();
	int count = 0;
	int totalCount = 0;
	int lastNum = 0;
	
	public void run(){

			
//			try {
//				
//				
//				currentRate.putAll(oi.getPacktNumber());
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			//System.out.println(totalCount);
			
			int currentNum = pro.getPacketNum();
			totalCount = currentNum - lastNum;
			
			double currentState = (double) totalCount*100 / Configure.pckNumLimit;
			
			//System.out.println(totalCount*250 + "     " + count);
			
			lastNum = currentNum;
			
			if(currentState <= Configure.healthRate){
				
				try {
					System.out.println("...... state is changed to healthy ......");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30001,actions=normal");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30002,actions=normal");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30003,actions=normal");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else if(currentState <= Configure.sickRate){

				try {
					System.out.println("###### state is changed to slightly blocked ######");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30001,actions=normal");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30002,actions=normal");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30003,actions=drop");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else if(currentState > Configure.sickRate){
				
				try {
					System.out.println("------ state is changed to heavily blocked ------");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30001,actions=normal");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30002,actions=drop");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30003,actions=drop");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

	}
}
