package edu.bupt.wangfu.sdn.queue;

import edu.bupt.wangfu.sdn.info.Controller;
import org.apache.servicemix.wsn.router.router.GlobleUtil;
import org.json.JSONObject;

public class QueueManagerment {

	//
	public static boolean enQueue(Controller controller, String switchs, int inport, String srcPort, String queue) {
		if (switchs == null || switchs.equals("")) return false;
		String flow = "'{\"switch\": \"" + switchs + "\", \"name\":\"flow-mod-1\", \"cookie\":\"0\", \"priority\":\"32768\",\"ingress-port\":\"" + inport + "\"," + "src-port\":\"" + srcPort + "\",\"active\":\"true\", \"actions\":\"enqueue=" + queue + "\"}'";
		boolean result = false;

//			result = RestProcess.AddFlow(new JSONObject(flow));
		result = GlobleUtil.getInstance().downFlow(controller.url, new JSONObject(flow));

		return result;
	}

	public static void qosStart() {

		new Thread(new QueueAdjust()).start();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
