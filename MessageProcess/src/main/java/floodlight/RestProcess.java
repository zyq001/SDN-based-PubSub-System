package floodlight;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RestProcess {///wm/device/
	public static String REST_URL = "http://10.108.166.220:8080";//"http://10.108.164.211:8080/wm/core/controller/switches/json";//"http://10.108.164.211:8080/wm/core/switch/1/flow/json";
	private static String API_KEY = "your api key";
	private static String SECRET_KEY = "your secret key";
	private static int index = 2;


	public static void main(String args[]) {

		ArrayList<String> result = getMemory();
		System.out.println(result);

		ArrayList<ArrayList<String>> result2 = getDevInfo();
		System.out.println(result2);

	}


	public static ArrayList<String> getMemory() {
		ArrayList<String> list = new ArrayList<String>();
		try {
			String url = REST_URL + "/wm/core/memory/json";
			String body = doClientGet(url);
			JSONObject json = new JSONObject(body);

			list.add(REST_URL.split("//")[1].split(":")[0]);
			list.add(json.getString("total"));
			list.add(json.getString("free"));
			//		JSONArray jsonArray = null;
			//		jsonArray = new JSONArray(body);
			//		JSONArray jsonArray2 = new JSONArray(""+jsonArray.getJSONObject(1).get("attachmentPoint"));
			//	    System.out.println(jsonArray2.getJSONObject(0).get("switchDPID"));
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		list.add("null");
		list.add("null");
		list.add("null");
		return list;
	}

	public static ArrayList<ArrayList<String>> getDevInfo() {
		ArrayList<ArrayList<String>> all = new ArrayList<ArrayList<String>>();
		try {
			String url = REST_URL + "/wm/device/";
			String body = doClientGet(url);
			JSONArray json = new JSONArray(body);
			System.out.println(body);
			System.out.println(json.length());
			for (int i = 0; i < json.length(); i++) {
				ArrayList<String> list = new ArrayList<String>();
				list.add("10.108.167." + index);//json.getJSONObject(i).getJSONArray("ipv4").getString(0)
				index++;
				list.add(json.getJSONObject(i).getJSONArray("mac").getString(0));
				list.add("" + json.getJSONObject(i).getJSONArray("attachmentPoint").getJSONObject(0).getInt("port"));
				list.add("" + json.getJSONObject(i).getJSONArray("attachmentPoint").getJSONObject(0).get("errorStatus"));
				list.add("" + json.getJSONObject(i).getLong("lastSeen"));
				all.add(list);
			}
			//		JSONArray jsonArray = null;
			//		jsonArray = new JSONArray(body);
			//		JSONArray jsonArray2 = new JSONArray(""+jsonArray.getJSONObject(1).get("attachmentPoint"));
			//	    System.out.println(jsonArray2.getJSONObject(0).get("switchDPID"));
			return all;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return all;
	}

	public static ArrayList<ArrayList<String>> getFlowInfo() {
		ArrayList<ArrayList<String>> all = new ArrayList<ArrayList<String>>();
		try {
			String devurl = REST_URL + "/wm/device/";
			String devbody = doClientGet(devurl);
			JSONArray devjson = new JSONArray(devbody);
			String DPID = devjson.getJSONObject(0).getJSONArray("attachmentPoint").getJSONObject(0).getString("switchDPID");

			String url = REST_URL + "/wm/core/counter/all/json";
			String body = doClientGet(url);
			JSONObject json = new JSONObject(body);

			ArrayList<String> dpidlist = new ArrayList<String>();
			dpidlist.add(DPID);
			dpidlist.add("" + json.getInt("controller__OFPacketIn"));
			all.add(dpidlist);

			for (int i = 0; i < devjson.length(); i++) {
				ArrayList<String> list = new ArrayList<String>();
				list.add(DPID + "__" + devjson.getJSONObject(i).getJSONArray("attachmentPoint").getJSONObject(0).getInt("port"));
				list.add("" + json.getInt(DPID + "__" + devjson.getJSONObject(i).getJSONArray("attachmentPoint").getJSONObject(0).getInt("port") + "__OFPacketIn"));
				all.add(list);
			}
			System.out.println("all:" + all);
			return all;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return all;
	}

	private static String doClientGet(String url) {
		try {
			HttpClient httpclient = new HttpClient();
			GetMethod getMethod = new GetMethod(url);
//				 PostMethod postMethod = new PostMethod(url);
//				if (postData != null) {
//					getMethod.addParameters(postData); 
//				}
			httpclient.executeMethod(getMethod);
			String body = getMethod.getResponseBodyAsString();
			getMethod.releaseConnection();
			return body;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}