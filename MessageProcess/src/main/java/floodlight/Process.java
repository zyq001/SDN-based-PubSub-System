package floodlight;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Process {

	public static String REST_URL = "http://10.108.166.220:8080";//"http://10.108.164.211:8080/wm/core/controller/switches/json";//"http://10.108.164.211:8080/wm/core/switch/1/flow/json";
	private static String API_KEY = "your api key";
	private static String SECRET_KEY = "your secret key";
	private static int index = 2;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		changeFLows();

	}

	public static void getStateOfAllSwitch() {

		try {

			String url = REST_URL + "/wm/core/switch/all/port/json";
			String body = doClientGet(url);
			JSONObject json = new JSONObject(body);

			System.out.println(body);


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void getOneSwitchState() {

		try {
			String url = REST_URL + "/wm/core/switch/00:00:44:37:e6:92:09:18/table/json";
			String body = doClientGet(url);
			JSONObject json = new JSONObject(body);

			System.out.println(body);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static int getPacketNum() {

		try {

			String url = REST_URL + "/wm/core/counter/StorageQuery__controller_staticflowtableentry/json";
			String body = doClientGet(url);

			String count = body.substring(body.indexOf("try") + 5, body.length() - 1);

			return Integer.parseInt(count);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;

	}

	public static void changeFLows() {

		try {

			String url = REST_URL + "/wm/staticflowentrypusher/json ";
			String body = postClientGet(url);
			System.out.println(body);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static String doClientGet(String url) {
		try {
			HttpClient httpclient = new HttpClient();
			GetMethod getMethod = new GetMethod(url);
//			 PostMethod postMethod = new PostMethod(url);
//			if (postData != null) {
//				getMethod.addParameters(postData); 
//			}
			httpclient.executeMethod(getMethod);
			String body = getMethod.getResponseBodyAsString();
			getMethod.releaseConnection();
			return body;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String postClientGet(String url) {
		try {
			HttpClient httpclient = new HttpClient();
//			 GetMethod  getMethod= new GetMethod(url);
			PostMethod postMethod = new PostMethod(url);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);

			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("switch", "00:00:44:37:e6:92:09:18");
			map.put("name", "flow1");
			map.put("priority", "100");
			map.put("networkProtocol", "6");
			map.put("transportSource", "30001");
			map.put("active", "true");
			map.put("actions", "normal");

			HttpMethodParams p = new HttpMethodParams();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				p.setParameter(entry.getKey(), entry.getValue());
			}
			postMethod.setParams(p);


//			 nameValuePairs.add(new NameValuePair("switch", "00:00:44:37:e6:92:09:18")); 
//			 nameValuePairs.add(new NameValuePair("name", "flow1")); 
//			 nameValuePairs.add(new NameValuePair("priority", "100")); 
//			 nameValuePairs.add(new NameValuePair("networkProtocol", "6"));
//			 nameValuePairs.add(new NameValuePair("transportSource", "30001"));
//			 nameValuePairs.add(new NameValuePair("active", "true"));
//			 nameValuePairs.add(new NameValuePair("actions", "normal"));
//			 
//			 httpclient.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			httpclient.executeMethod(postMethod);
			String body = postMethod.getResponseBodyAsString();
			postMethod.releaseConnection();

			//	System.out.println(body);


			//httpclient.setEntity(new UrlEncodedFormEntity(nameValuePairs));

//			if (postData != null) {
//				getMethod.addParameters(postData); 
//			}
//			httpclient.executeMethod(getMethod);
//			String body = getMethod.getResponseBodyAsString();
//			getMethod.releaseConnection();


			return body;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
