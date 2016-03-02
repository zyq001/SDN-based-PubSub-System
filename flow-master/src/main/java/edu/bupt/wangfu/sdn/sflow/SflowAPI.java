package edu.bupt.wangfu.sdn.sflow;

import edu.bupt.wangfu.sdn.Configuration.Configure;
import edu.bupt.wangfu.sdn.floodlight.RestProcess;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 15-7-14.
 */
public class SflowAPI {

	public static double getSpeed(String agent, int port, String metric) {
		double speed = 0;
		String url = "http://" + Configure.sflowServer + ":8008/metric/" + agent + "/" + port
				+ "." +metric + "/json";
		String result = RestProcess.doClientGet(url);
		try {
			speed = Double.valueOf(new JSONObject(result).get("metricValue").toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return speed;
	}

}
