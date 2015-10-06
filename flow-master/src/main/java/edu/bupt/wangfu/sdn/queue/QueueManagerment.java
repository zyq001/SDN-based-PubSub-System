package edu.bupt.wangfu.sdn.queue;

import edu.bupt.wangfu.sdn.floodlight.RestProcess;
import edu.bupt.wangfu.sdn.floodlight.RestProcess;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueueManagerment {

	//
	public static boolean enQueue(String switchs, int inport, String srcPort, String queue){
		if(switchs == null || switchs.equals(""))return false;
		String flow = "'{\"switch\": \"" + switchs + "\", \"name\":\"flow-mod-1\", \"cookie\":\"0\", \"priority\":\"32768\",\"ingress-port\":\"" + inport + "\","+"src-port\":\"" + srcPort +"\",\"active\":\"true\", \"actions\":\"enqueue=" + queue + "\"}'";
		List<String> result = new ArrayList<String>();
		try {
			result = RestProcess.AddFlow(new JSONObject(flow));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.contains("pushed");
	}

	public static void qosStart(){

		new Thread(new QueueAdjust()).start();

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
