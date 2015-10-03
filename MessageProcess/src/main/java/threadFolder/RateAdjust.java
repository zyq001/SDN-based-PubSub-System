package threadFolder;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import Configuration.Configure;
import InfoCollect.OvsInfo;
import OvsOperation.OvsOperate;

public class RateAdjust extends Thread {

	public ConcurrentHashMap<Double,Integer> currentRate = new ConcurrentHashMap<Double,Integer>();
	OvsInfo oi = new OvsInfo();
	OvsOperate oo = new OvsOperate();
	int count = 0;
	int totalCount = 0;
	int lastNum = 0;
	
	long count2 = 0;
	long totalCount2 = 0;
	long lastNum2 = 0;
	long currentNum = 0;
	
	public void run2(){

			
			try {
				currentRate.putAll(oi.getPacktNumber());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println(totalCount);
			
			int currentNum = currentRate.get(1.0) + currentRate.get(2.0) +
					currentRate.get(3.0) + currentRate.get(4.0);
			totalCount = currentNum - lastNum;
			
			double currentState = (double) totalCount*100 / Configure.pckNumLimit;
			
			//System.out.println(totalCount*250 + "     " + count);
			
			lastNum = currentNum;
			
			if(currentState <= Configure.healthRate){
				
				try {
					//System.out.println("...... state is changed to healthy ......");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30001,actions=normal");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30002,actions=normal");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30003,actions=normal");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else if(currentState <= Configure.sickRate){

				try {
					//System.out.println("###### state is changed to slightly blocked ######");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30001,actions=normal");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30002,actions=normal");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30003,actions=drop");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else if(currentState > Configure.sickRate){
				
				try {
					//System.out.println("------ state is changed to heavily blocked ------");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30001,actions=normal");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30002,actions=drop");
					oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30003,actions=drop");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

	}
	
	public void run(){
		
		try{
			currentNum = oi.getByte();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		totalCount2 = currentNum - lastNum2;
		
		//System.out.println(totalCount2);
		
		double currentState = totalCount2*20 / Configure.bandWidth;
		
		lastNum2 = currentNum;
		
		if(currentState <= Configure.healthRate){
			
			try {
				//System.out.println("...... state is changed to healthy ......");
				oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30001,actions=normal");
				oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30002,actions=normal");
				oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30003,actions=normal");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else if(currentState <= Configure.sickRate){

			try {
				//System.out.println("###### state is changed to slightly blocked ######");
				oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30001,actions=normal");
				oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30002,actions=normal");
				oo.changeFlow("ovs-ofctl add-flow br0 tcp,tcp_src=30003,actions=drop");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else if(currentState > Configure.sickRate){
			
			try {
				//System.out.println("------ state is changed to heavily blocked ------");
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
