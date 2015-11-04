package edu.bupt.wangfu.sdn.floodlight;

import edu.bupt.wangfu.sdn.info.DevInfo;
import edu.bupt.wangfu.sdn.info.Flow;
import edu.bupt.wangfu.sdn.info.MemoryInfo;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.servicemix.wsn.router.admin.AdminMgr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

//import org.apache.http.client.HttpClient;

public class RestProcess {///wm/device/
	public static String REST_URL = "http://10.109.253.2:8080";//"http://10.108.164.211:8080/wm/core/controller/switches/json";//"http://10.108.164.211:8080/wm/core/switch/1/flow/json";
	private static String API_KEY = "your api key";
	private static String SECRET_KEY = "your secret key";
	private static int index = 2;
	private static CloseableHttpClient client = HttpClients.createDefault();


	public static void main(String args[]) {

		MemoryInfo result = getMemory();
		System.out.println(result);

//		ArrayList<ArrayList<String>> result2 = getDevInfo();
//		System.out.println(result2);

	}

	public static MemoryInfo getMemory(String url) {
		MemoryInfo info = new MemoryInfo();
		try {
//			url = REST_URL + "/wm/core/memory/json";
			url += "/wm/core/memory/json";
			String body = doClientGet(url);
			JSONObject json = new JSONObject(body);

			info.setUrl(REST_URL.split("//")[1].split(":")[0]);
			info.setTotalMem(json.getString("total"));
			info.setFreeMem(json.getString("free"));

			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	public static MemoryInfo getMemory() {
		MemoryInfo info = new MemoryInfo();
		try {
			String url = REST_URL + "/wm/core/memory/json";
			String body = doClientGet(url);
			JSONObject json = new JSONObject(body);

			info.setUrl(REST_URL.split("//")[1].split(":")[0]);
			info.setTotalMem(json.getString("total"));
			info.setFreeMem(json.getString("free"));

			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	public static ArrayList<DevInfo> getDevInfo() {
		ArrayList<DevInfo> all = new ArrayList<DevInfo>();
		try {
			String url = REST_URL + "/wm/device/";
			String body = doClientGet(url);
			JSONArray json = new JSONArray(body);
			System.out.println(body);
			System.out.println(json.length());
			for (int i = 0; i < json.length(); i++) {
				DevInfo info = DevInfo.getINSTANCE();
				JSONArray ipv4 = json.getJSONObject(i).getJSONArray("ipv4");
				if (!ipv4.isNull(0)) info.setUrl(ipv4.getString(0));
				info.setMac(json.getJSONObject(i).getJSONArray("mac").getString(0));
				info.setPort("" + (json.getJSONObject(i).getJSONArray("attachmentPoint").length() > 0
						? json.getJSONObject(i).getJSONArray("attachmentPoint").getJSONObject(0).getInt("port") : ""));
				info.setErrorStatus("" + (json.getJSONObject(i).getJSONArray("attachmentPoint").length() > 0
						? json.getJSONObject(i).getJSONArray("attachmentPoint").getJSONObject(0).get("errorStatus") : ""));
				info.setLastSeen("" + json.getJSONObject(i).getLong("lastSeen"));
				all.add(info);
			}
			return all;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return all;
	}

	public static void downRuntimeTopology() {

		String topoString = doClientGet(REST_URL + "/wm/topology/links/json");
		JSONArray topoArray = null;
		topoArray = new JSONArray(topoString);

		for (int i = 0; i < topoArray.length(); i = i + 2) {
			JSONObject link = new JSONObject(topoArray.get(i).toString());
			String srcSW = link.getString("src-switch");
			String srcport = link.getString("src-port");
			String dstSW = link.getString("dst-switch");
			String dstPort = link.getString("dst-port");
			DevInfo.getINSTANCE().getTopology().get(srcSW).add(DevInfo.getINSTANCE().getSwitchs().get(dstSW));
		}

	}

	public static ArrayList<Flow> getFlowInfo() {
		ArrayList<Flow> all = new ArrayList<Flow>();
		try {
			String devurl = REST_URL + "/wm/device/";
			String devbody = doClientGet(devurl);
			JSONArray devjson = new JSONArray(devbody);
			System.out.println(devjson);

			String DPID = devjson.getJSONObject(0).getJSONArray("attachmentPoint")
					.length() > 0 ? devjson.getJSONObject(0).getJSONArray("attachmentPoint")
					.getJSONObject(0).getString("switchDPID") : "";
			String url = REST_URL + "/wm/core/counter/all/json";
			String body = doClientGet(url);
			JSONObject json = new JSONObject(body);
			System.out.println(json);
			Flow dpidlist = new Flow(json.getString("dpid"));
			dpidlist.setDpid(REST_URL);
			dpidlist.setFlowCount("" + json.getInt("controller__OFPacketIn"));
			all.add(dpidlist);

			String switchurl = REST_URL + "/wm/core/controller/switches/json";
			String switchbody = doClientGet(switchurl);
			JSONArray switchjson = new JSONArray(switchbody);
			System.out.println(switchjson);

			for (int i = 0; i < switchjson.length(); i++) {//test
				String dpid = switchjson.getJSONObject(i).getString("dpid");

				Flow list = new Flow(dpid);
				list.setDpid(dpid);
//				list.setDpid(DPID+"__"+(devjson.getJSONObject(i)
//						.getJSONArray("attachmentPoint").length() > 0 ? devjson.getJSONObject(i)
//								.getJSONArray("attachmentPoint").getJSONObject(0).getInt("port") : ""));
				list.setFlowCount("" + json.getInt(dpid
//						+"__"+(devjson.getJSONObject(i).getJSONArray("attachmentPoint").length() > 0 ? devjson.getJSONObject(i)
//								.getJSONArray("attachmentPoint").getJSONObject(0).getInt("port") : "")
						+ "__OFPacketIn"));
				all.add(list);
			}
			return all;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return all;
	}

	public static String doClientGet(String url) {
		try {
			HttpClient httpclient = new HttpClient();
			GetMethod getMethod = new GetMethod(url);
			httpclient.executeMethod(getMethod);
			String body = getMethod.getResponseBodyAsString();
			getMethod.releaseConnection();
			return body;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> doClientPost(String url, JSONObject jo) {
		List<String> result = new ArrayList<String>();

		HttpPost method = new HttpPost(url);
		StringEntity entity = new StringEntity(jo.toString(), "utf-8");
		method.setHeader("Content-Type", "application/json; charset=UTF-8");
		method.setEntity(entity);

		try {
			CloseableHttpResponse statusCode = client.execute(method);
			result.add(statusCode.toString());
			BufferedReader reader = new BufferedReader(new InputStreamReader(statusCode.getEntity().getContent(), "utf-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				result.add(line);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static List<String> doClientDelete(String url, JSONObject jo) {
		List<String> result = new ArrayList<String>();
		HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
		// json ??

		httpDelete.setHeader("Content-Type", "application/json; charset=UTF-8");//or addHeader();

		httpDelete.setHeader("X-Requested-With", "XMLHttpRequest");

		//??HttpDelete?????

		try {
			httpDelete.setEntity(new StringEntity(jo.toString()));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

//		httpDelete.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
//
//		httpdelete.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);

//		HttpDelete method = new HttpDelete(url);
//		StringEntity entity = new StringEntity(jo.toString(),"utf-8");
//		method.setHeader("Content-Type","application/json; charset=UTF-8");
//		method.setEntity(entity);

		try {
			CloseableHttpResponse statusCode = client.execute(httpDelete);

			BufferedReader reader = new BufferedReader(new InputStreamReader(statusCode.getEntity().getContent(), "utf-8"));
			String line;
			while ((line = reader.readLine()) != null) {
//	        	 System.out.println(line);
				result.add(line);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

//	public static List<String> add()

	public static List<String> AddFlow(JSONObject flow) throws IOException {

		String staticFlowUrl = REST_URL + "wm/staticflowentrypusher/json";

		return doClientPost(staticFlowUrl, flow);
	}

	public ArrayList<String> changeFlow(String cmd) throws IOException {

		ArrayList<String> result = new ArrayList<String>();
		java.lang.Process process = null;

		//process = Runtime.getRuntime().exec("ovs-vsctl add-br " + bridge);
		process = Runtime.getRuntime().exec(cmd);
		//System.out.println("ovs-vsctl add-br "+bridge+" port");
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = input.readLine()) != null) {
			result.add(line);
		}
		input.close();

		return result;
	}


}