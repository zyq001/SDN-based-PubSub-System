package org.apache.servicemix.wsn.router.router;

import edu.bupt.wangfu.sdn.floodlight.RestProcess;
import edu.bupt.wangfu.sdn.info.*;
import edu.bupt.wangfu.sdn.queue.QueueManagerment;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.servicemix.wsn.router.admin.AdminMgr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-6.
 */
public class GlobleUtil {
	public static List<Flow> initFlows = new ArrayList<>();
//	public static String REST_URL = "http://10.109.253.2:8080";//nll
	public static Map<Integer, String> switchMap = new HashMap<>();//用于保存邻接矩阵的下标与交换机的对应关系
	public static int[][] weight = new int[200][200];//用于保存每次更新时当前交换机之间的连接关系
	public static int[][] weight_first = new int[20][20];//用于保存本次更新时上一次存储的交换机之间的连接关系
	private static int M = 10000; // 此路不通
	private static GlobleUtil INSTANCE;
	private static int index = 2;//nll
	private static Timer timer = new Timer();
	private static Map<Integer, DevInfo> wsnDevMap = new ConcurrentHashMap<>();//交换机所连接的所设备
	public Map<String, Controller> controllers = new ConcurrentHashMap<>();
	public Map<String, String> group2controller = new ConcurrentHashMap<String, String>();
	public Controller centerController;

	private GlobleUtil() {
		group2controller.put("G2", "10.109.253.2");
		group2controller.put("G3", "10.109.253.3");
		group2controller.put("G4", "10.109.253.4");
		//init static initFlows{queueFlow, topics}
		centerController = new Controller(AdminMgr.globalControllerAddr);
//		centerController.setSwitchMap(getAllSwitch(centerController));
		centerController.setSwitchMap(getRealtimeSwitchs2(centerController));
		controllers.put(AdminMgr.globalControllerAddr, centerController);
		Flow flow = new Flow("queue");
		initFlows.add(flow);
		// start timer to recaclateRoute
		timer.schedule(new GlobalTimerTask(), 2000, 5 * 60 * 1000);
	}


	public static Map<String, Switch> getAllSwitch(Controller controller) {//获取当前controller下所有交换机的dpid
		Map<String, Switch> switches = new HashMap<>();
		String url = controller.getUrl() + ":8080/wm/core/controller/switches/json";
		String body = doClientGet(url);
		JSONArray json = new JSONArray(body);
		for (int i = 0; i < json.length(); i++) {
			Switch swc = new Switch();
			String DPID = json.getJSONObject(i).getString("switchDPID");

			String mac = "";
			String[] tmp = DPID.split(":");
			for (int j = 2; j < tmp.length; j++) {
				if (j != tmp.length - 1)
					mac = mac + tmp[j] + ":";
				else
					mac = mac + tmp[j];
			}

			swc.setDPID(DPID);
			swc.setMac(mac);
			controller.getSwitchMap().put(DPID, swc);
		}
		return controller.getSwitchMap();
	}

	public static Map<String, Switch> getRealtimeHosts(Controller controller) {
		String url = controller.getUrl() + "/wm/device/";
		String body = doClientGet(url);
		JSONArray json = new JSONArray(body);
		int flag = 0;
		for (int i = 0; i < json.length(); i++) {
			JSONArray mac = json.getJSONObject(i).getJSONArray("mac");
			String macAdd = mac.get(0).toString();
//			System.out.println("&&&&" + macAdd);
			String DPID = "00:00:" + macAdd;
			for (Map.Entry<String, Switch> entry : controller.getSwitchMap().entrySet()) {
				Switch swt = entry.getValue();
				if (swt.getDPID().equals(DPID))
					flag = 1;
			}
			if (flag == 0) {//不存在这个DPID的交换机,则该设备是一个主机，需加入邻居中
				JSONArray array = json.getJSONObject(i).getJSONArray("attachmentPoint");
				for (int j = 0; j < array.length(); j++) {
//						System.out.println("^^^^"+array.length());
					String switchDPID = array.getJSONObject(j).getString("switchDPID");
//					System.out.println("DPID" + switchDPID);
					WSNHost host = new WSNHost();
					host.setMac(macAdd);
					int port = array.getJSONObject(j).getInt("port");
					Switch swtch = findSwitch(switchDPID, controller);
					swtch.put(port, host);

					System.out.println("****" + port + "   " + swtch.getDPID());

				}
			}
			flag = 0;
		}
		return controller.getSwitchMap();
	}

	public static Map<String, Switch> getRealtimeSwitchs2(Controller controller) {//获取网络的拓扑结构

		Map<String, Switch> switches = new HashMap<String, Switch>();
//		try {
			String url = controller.getUrl() + ":8080/wm/core/controller/switches/json";
			String body = doClientGet(url);
			JSONArray json = new JSONArray(body);
//			System.out.println(body);
//			System.out.println(json.length());
			for (int i = 0; i < json.length(); i++) {//获取交换机对象
				Switch swc = new Switch();
				JSONObject jbo = json.getJSONObject(i);
				String DPID = jbo.getString("switchDPID");
				String[] ipAport = jbo.getString("inetAddress").substring(1).split(":");
				swc.setIpAddr(ipAport[0]);
				swc.setPort(ipAport[1]);
				swc.setConnectedSince(Long.valueOf(jbo.getLong("connectedSince")));
				swc.setDPID(DPID);
				String mac = null;
				Map<Integer, DevInfo> wsnDevMap = new ConcurrentHashMap<>();//交换机所连接的所有设备
				String url_af = controller.getUrl() + ":8080/wm/core/switch/all/features/json";
				String body_af = doClientGet(url_af);
				JSONObject json_af = new JSONObject(body_af);
				System.out.println("***" + body_af);
				JSONObject json_each = json_af.getJSONObject(DPID);//根据当前的DPID获取到这个交换机的相关信息
				JSONArray json_port;
				try {
					json_port = json_each.getJSONArray("portDesc");
				}catch (Exception e){
					continue;
				}
				for (int j = 0; j < json_port.length(); j++) {
					if (json_port.getJSONObject(j).getString("portNumber").equals("local")) {
						mac = json_port.getJSONObject(j).getString("hardwareAddress");
						swc.setMac(mac);
						System.out.println("****" + mac);
					} else {
						if (!json_port.getJSONObject(j).getString("portNumber").equals("1")) {//如果端口号不是local与1则为所连接的设备
							int portNum = json_port.getJSONObject(j).getInt("portNumber");
							String macAddr = json_port.getJSONObject(j).getString("hardwareAddress");
							boolean flag = false;//用来记录是否在map中找到该交换机
							Switch swth = null;
							for (Map.Entry<String, Switch> entry : switches.entrySet()) {
								Switch swt = entry.getValue();
								if (swt.getMac().equals(macAddr)) {
									flag = true;
									swth = swt;
								}
							}
							if (flag) {
								System.out.println("a switch is added...");
								System.out.println("the DPID of this switch is :" + swth.getDPID());
								wsnDevMap.put(portNum, swth);
							} else {
								System.out.println("a host is added...");
								WSNHost host = new WSNHost();
								host.setMac(macAddr);
								System.out.println("the macAddr of this host is : " + macAddr);
								wsnDevMap.put(portNum, host);
							}
							swc.setWsnDevMap(wsnDevMap);
						}
					}
				}
				switches.put(DPID, swc);
			}
			for (Map.Entry<String, Switch> entry : switches.entrySet()) {
				System.out.println("the switch is : " + entry.getValue().getDPID());
			}
//			return switches;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		for (Map.Entry<String, Switch> entry : switches.entrySet()) {
////			System.out.println("the switch is : " + entry.getValue().getDPID());
//		}
		return switches;
	}

	private static void refreshSwitchInfo(Switch swt, Controller controller) {//nll更新switch所连接的设备map
		Map<Integer, DevInfo> wsnDevMap = swt.getWsnDevMap();
		Map<String, Switch> switchMap = controller.getSwitchMap();
		for (Map.Entry<Integer, DevInfo> entry : wsnDevMap.entrySet()) {
			Integer port = entry.getKey();
			DevInfo dev = entry.getValue();
			if (dev instanceof WSNHost) {// 如果设备当前被设置成一个主机，那么要判断这个主机是否是一个交换机
				for (Map.Entry<String, Switch> entry2 : switchMap.entrySet()) {
					if (dev.getMac().equals(entry2.getValue().getMac())) {//如果是一个交换机，那么更新Map
						dev = entry2.getValue();
						wsnDevMap.put(port, dev);
					}
				}
			}
		}
	}

	public static void getRealtimeSwitchs(Map<String, Controller> controllers) {
		for (Map.Entry<String, Controller> entry : controllers.entrySet()) {
			Controller controller = entry.getValue();
			Map<String, Switch> map = getRealtimeSwitchs2(controller);
			controller.setSwitchMap(map);
		}

	}

	public static void initFunc(Controller controller) {//初始化数组与对应的Map
		int flag = 0;
		for (int i = 0; i < weight.length; i++)
			for (int j = 0; j < weight[i].length; j++)
				weight[i][j] = M;//初始化二维数组，M值表示不连接，初始为全部未连接
		for (Map.Entry<String, Switch> entry : controller.getSwitchMap().entrySet()) {
			Switch swt = entry.getValue();
			switchMap.put(flag, swt.getDPID());
			flag++;
		}
	}

	public static int[][] getTopology(Controller controller) {//获取交换机之间的连接关系，将结果更新到邻接矩阵中
		String url = controller.getUrl() + "/wm/topology/links/json";
		String body = doClientGet(url);
		JSONArray json = new JSONArray(body);
		for (int i = 0; i < json.length(); i++) {
			//当连接是双向的时候才会进行记录，单向的忽略
			if (json.getJSONObject(i).getString("direction").equals("bidirectional")) {
				int port = json.getJSONObject(i).getInt("src-port");
				String dpid = json.getJSONObject(i).getString("src-switch");
				Switch swt = findSwitch(dpid, controller);
				String dst_dpid = json.getJSONObject(i).getString("dst-switch");
				int dst_port = json.getJSONObject(i).getInt("dst-port");
				Switch dest_swt = findSwitch(dst_dpid, controller);
				swt.put(port, dest_swt);//将邻居交换机加入
				dest_swt.put(dst_port, swt);//由于是双向的，所以都要加入
				int row = 0, column = 0;
				for (Map.Entry<Integer, String> entry : switchMap.entrySet()) {
					if (entry.getValue().equals(dpid))
						row = entry.getKey();
					if (entry.getValue().equals(dst_dpid))
						column = entry.getKey();
				}
				weight[row][column] = 1;
				weight[column][row] = 1;
			}
		}
		return weight;
	}

	public static Switch findSwitch(String dpid, Controller controller) {//用来判断所给的DPID的交换机是否已存在
		boolean flag = false;
		Switch swtch = new Switch();
		for (Map.Entry<String, Switch> entry : controller.getSwitchMap().entrySet()) {
			Switch swt = entry.getValue();
			if (swt.getDPID().equals(dpid)) {//存在则直接返回
				flag = true;
				return swt;
			} else
				flag = false;
		}
		if (!flag) {//不存在就新建，并加入
//			Switch swtch = new Switch();
			swtch.setDPID(dpid);
			controller.getSwitchMap().put(dpid, swtch);
			return swtch;
		}
		return swtch;
	}

	//    @Override
//	public void run(){
//		Controller controller = new Controller("http://10.109.253.2:8080");
//		getAllSwitch(controller);
//		for (Map.Entry<String, Switch> entry : controller.getSwitchMap().entrySet()) {
//			System.out.println("*" + entry.getValue().getDPID());
//		}
//		initFunc(weight, controller);
//		getTopology(controller);
//		for (int i = 0; i < weight.length; i++) {
//			for (int j = 0; j < weight[i].length; j++) {
//				if(weight_first[i][j]!= weight[i][j]){
//					//这里调用lcw的函数；
//				}
//					System.out.print(weight[i][j] + " ");
//			}
//			System.out.println();
//		}
//		weight_first = weight;
//
//
//		getRealtimeHosts(controller);
//		int j =0;
//		for (Map.Entry<String, Switch> entry : controller.getSwitchMap().entrySet()) {
////			System.out.println("*" + entry.getValue().getDPID());
//			if(entry.getValue().getDPID().equals("00:00:ee:2d:00:5a:16:45")){
//				Map<Integer , DevInfo> map = entry.getValue().getWsnDevMap();
//				for(Map.Entry<Integer , DevInfo> entry1: map.entrySet()){
//					j++;
//					if(entry1.getValue() instanceof Switch)
//						System.out.println("Switch :" +entry1.getKey()+"  "+((Switch) entry1.getValue()).getDPID());
//					else if(entry1.getValue() instanceof WSNHost){
//						System.out.println("++++++");
//						System.out.println("Host :" + entry1.getKey() + "  " + ((WSNHost) entry1.getValue()).getMac());
//					}
//
//				}
//				System.out.println("j:"+j);
//			}
//		}
//
//	}
	public static void main(String args[]) {
		Controller controller = new Controller("http://10.109.253.2:8080");
		getAllSwitch(controller);
		for (Map.Entry<String, Switch> entry : controller.getSwitchMap().entrySet()) {
			System.out.println("*" + entry.getValue().getDPID());
		}
		initFunc(controller);
		getTopology(controller);
		for (int i = 0; i < weight.length; i++) {
			for (int j = 0; j < weight[i].length; j++) {
				System.out.print(weight[i][j] + " ");
			}
			System.out.println();
		}
		getRealtimeHosts(controller);
		int j = 0;
		for (Map.Entry<String, Switch> entry : controller.getSwitchMap().entrySet()) {
//			System.out.println("*" + entry.getValue().getDPID());
			if (entry.getValue().getDPID().equals("00:00:ee:2d:00:5a:16:45")) {
				Map<Integer, DevInfo> map = entry.getValue().getWsnDevMap();
				for (Map.Entry<Integer, DevInfo> entry1 : map.entrySet()) {
					j++;
					if (entry1.getValue() instanceof Switch)
						System.out.println("Switch :" + entry1.getKey() + "  " + ((Switch) entry1.getValue()).getDPID());
					else if (entry1.getValue() instanceof WSNHost) {
						System.out.println("++++++");
						System.out.println("Host :" + entry1.getKey() + "  " + ((WSNHost) entry1.getValue()).getMac());
					}

				}
				System.out.println("j:" + j);
			}
		}

	}

	//	public static void detectChange(final Controller controller){//设定计时器，定时检查维护拓扑结构
//		Timer timer = new Timer();
//		timer.schedule(new refeshTask(){
//			GlobleUtil globleUtil = new GlobleUtil();
//			Map<String, Switch> map =globleUtil.getAllSwitch(controller);
//			for (Map.Entry<String, Switch> entry : controller.getSwitchMap().entrySet()) {
//				System.out.println("*" + entry.getValue().getDPID());
//			}
//			globleUtil.initFunc(globleweight, controller);
//			getTopology(controller);
//			for (int i = 0; i < weight.length; i++) {
//				for (int j = 0; j < weight[i].length; j++) {
//					System.out.print(weight[i][j] + " ");
//				}
//				System.out.println();
//			}
//			getRealtimeHosts(controller);
//			int j =0;
//			for (Map.Entry<String, Switch> entry2 : controller.getSwitchMap().entrySet()) {
////			System.out.println("*" + entry.getValue().getDPID());
//				if(entry.getValue().getDPID().equals("00:00:ee:2d:00:5a:16:45")){
//					Map<Integer , DevInfo> map = entry2.getValue().getWsnDevMap();
//					for(Map.Entry<Integer , DevInfo> entry1: map.entrySet()){
//						j++;
//						if(entry1.getValue() instanceof Switch)
//							System.out.println("Switch :" +entry1.getKey()+"  "+((Switch) entry1.getValue()).getDPID());
//						else if(entry1.getValue() instanceof WSNHost){
//							System.out.println("++++++");
//							System.out.println("Host :" + entry1.getKey() + "  " + ((WSNHost) entry1.getValue()).getMac());
//						}
//
//					}
//					System.out.println("j:"+j);
//				}
//			}
//		}, 1000, 2000);
//
//	}
	private static String doClientGet(String url) {//nll
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

	public static boolean downFlow(String url, JSONObject content) {
		boolean success = false;
		return RestProcess.doClientPost(url, content).get(0).equals("200");

	}

	public static boolean downFlow(Controller controller, List<Flow> flows) {
		boolean success = false;
		for (Flow flow : flows) {
			if (downFlow(controller, flow)) success = true;
		}
		return true;
	}

	public static boolean downFlow(Controller controller, Flow flow) {
		boolean success = false;
		String staticFlowPushUri = controller.url + "/wm/staticflowpusher/json";
		List<String> result = RestProcess.doClientPost(staticFlowPushUri, flow.getContent());
		return result.size() < 1? true: result.get(0).equals("200");

	}

	public static GlobleUtil getInstance() {
		if (INSTANCE == null) INSTANCE = new GlobleUtil();
		return INSTANCE;
	}

	/*
	public static void main(String args[]) {
//		GlobleUtil globleUtil = new GlobleUtil();
		Controller controller = new Controller("http://10.109.253.2:8080");
		Map<String, Switch> map = getRealtimeSwitchs(controller);
		controller.setSwitchMap(map);
		for (Map.Entry<String, Switch> entry : map.entrySet()){
			Switch swt = entry.getValue();
			System.out.println("the DPID is : "+swt.getDPID());
			refreshSwitchInfo(swt, controller);
			Map<Integer, DevInfo> map1 = new ConcurrentHashMap<Integer, DevInfo>();
			for(Map.Entry<Integer ,DevInfo> entry1 : map1.entrySet()){
				DevInfo dev = entry1.getValue();
				if(dev instanceof Switch){
					System.out.println("this is a Switch and the DPID is :" + ((Switch) dev).getDPID());
				}else if(dev instanceof WSNHost){
					System.out.println("this is a host and the macAdd is : "+((WSNHost)dev).getMac());
				}
			}
		}
		return;
	}
	*/
	public void init() {
		//get realtime global info
		reflashGlobleInfo();

		//init all switchs
		for (Map.Entry<String, Controller> entry : controllers.entrySet()) {
			Controller controller = entry.getValue();
			initSwitchs(controller);
		}


	}

	public boolean initSwitchs(Controller controller) {
		boolean success = false;

		//down init flows
		downFlow(controller, initFlows);

		return success;
	}

	public boolean reflashGlobleInfo() {

		//Traversal controllers, GET global realtime status
		for (Map.Entry<String, Controller> entry : controllers.entrySet()) {
			Controller controller = entry.getValue();
			if (!controller.isAlive()) {
				controllers.remove(entry.getKey());
				continue;
			}
			controller.reflashSwitchMap();
		}
		return true;
	}

	public synchronized void addController(String controllerAddr) {

		Controller newController = new Controller(controllerAddr);

		newController.reflashSwitchMap();

		controllers.put(controllerAddr, newController);

	}

	class GlobalTimerTask extends TimerTask {

		/**
		 * The action to be performed by this timer task.
		 */
		@Override
		public void run() {

			//whether to adjust queue
			QueueManagerment.qosStart();

			//whether to adjust route

		}
	}
}
