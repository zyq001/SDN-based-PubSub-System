package com.sdn.sflow;

import com.sdn.Configuration.Configure;
import com.sdn.floodlight.RestProcess;
import jaxe.Config;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by root on 15-7-14.
 */
public class SflowAPI {

    public static double getSpeed(String agent, String metric){
        double speed = 0;
        String url = "http://" + Configure.sflowServer + ":8008/metric/" + agent + "/" + metric + "/json";
        String result = RestProcess.doClientGet(url);
        try {
            speed = Double.valueOf(new JSONObject(result).get("metricValue").toString());
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        return speed;
    }

}
