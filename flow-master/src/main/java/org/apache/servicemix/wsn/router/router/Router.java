package org.apache.servicemix.wsn.router.router;

import edu.bupt.wangfu.sdn.info.Controller;
import edu.bupt.wangfu.sdn.info.DevInfo;
import edu.bupt.wangfu.sdn.info.Flow;
import edu.bupt.wangfu.sdn.info.Switch;
import org.apache.servicemix.application.WSNTopicObject;
import org.apache.servicemix.wsn.router.mgr.base.SysInfo;
import org.apache.servicemix.wsn.router.router.LCW.Dijkstra;
import org.apache.servicemix.wsn.router.topictree.TopicTreeManager;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.servicemix.wsn.router.router.LCW.Dijkstra.getEachStop;

/**
 * 计算指定名称的转发路由
 * 针对该名称的所有订阅节点，由集群名比较选取根节点
 * 根据迪杰斯特拉算法由根节点开始计算转发树
 * 将转发树的根节点及本节点的吓一跳转发记录在名称路由结构中
 *
 * @author Sylvia
 */

public class Router extends SysInfo implements IRouter {

	public static HashMap<String, String> topicCodes = new HashMap<>();//key是topicName，value是topicCode
	static long flowcount = 0;
	static int M = 10000;

	public static Switch weight2Switch(Controller controller, Integer curSwitchWeight) {
		String curSwitchDpid = GlobleUtil.switchMap.get(curSwitchWeight);
		Switch curSwitch = GlobleUtil.findSwitch(curSwitchDpid, controller);
		return curSwitch;
	}

	public static Integer getRoutePort(Switch src, Switch dst) {
		for (Map.Entry<Integer, DevInfo> entry : src.getWsnDevMap().entrySet()) {
			if (entry.getValue() instanceof Switch) {
				Switch maybeDst = (Switch) entry.getValue();
				if (maybeDst.equals(dst)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

	public static void downRouteFlow(Controller controller, int[] route) {
		for (int i = 0; i < route.length; i++) {
			if (route[i] == M) break;
			else {
				Switch curSwitch = weight2Switch(controller, route[i]);

				Map<Integer, DevInfo> devMap = curSwitch.getWsnDevMap();
				//这里需要算两个端口：之前、之后两个交换机连接的端口
				if (i - 1 >= 0) {
					Switch pre = weight2Switch(controller, route[i - 1]);
					Integer p1 = getRoutePort(curSwitch, pre);
					Flow f = generateFlow(curSwitch, "abc", p1 != null ? p1.toString() : null);
					GlobleUtil.downFlow(controller, f);
				}
				if (route[i + 1] != M) {
					Switch next = weight2Switch(controller, route[i + 1]);
					Integer p2 = getRoutePort(curSwitch, next);
					Flow f = generateFlow(curSwitch, "abc", p2 != null ? p2.toString() : null);
					GlobleUtil.downFlow(controller, f);
				}
			}
		}
	}

	public static boolean adjustRoute() {
		boolean success = false;
		List<Flow> flows = cacluate(GlobleUtil.getInstance().controllers);
		success = FlowDownwardQueue.grtInstance().enqueue(flows);

		return success;
	}

	public static boolean adjustRoute(Controller controller) {
		boolean success = false;
		List<Flow> flows = cacluate(GlobleUtil.getInstance().controllers);
		success = FlowDownwardQueue.grtInstance().enqueue(flows);

		return success;
	}

//	public static String

	public static String getTopicLength(String topicCode) {
		char[] finalTC = new char[7];
		for (int i = 0; i < finalTC.length; i++) {
			finalTC[i] = '0';
		}

		int len = topicCode.length();
		String topicCodeLength = Integer.toBinaryString(len);
		char[] shortTC = topicCodeLength.toCharArray();
		int fc = 6;
		for (int i = shortTC.length - 1; i >= 0; i--) {
			finalTC[fc] = shortTC[i];
			fc--;
		}
		String res = String.valueOf(finalTC);
		return res;
	}

	public static String topicCode2mutiv6Addr(String topicCode) {
		topicCode = Integer.toBinaryString(Integer.valueOf(topicCode).byteValue());
		String newTopicCode = "11111111" + "0000" + "1110" + "10" + topicCode.length() + topicCode;

		String addr = getNewTopicCode(newTopicCode);
		return addr;
	}

	public static String topicName2mutiv6Addr(String topicName) {

		String[] topicPath = topicName.split(":");
		WSNTopicObject current = TopicTreeManager.topicTree;
		for (int i = 0; i < topicPath.length - 1; i++) {
			if (current.getTopicentry().getTopicName().equals(topicPath[i])) {
				for (int counter = 0; counter < current.getChildrens()
						.size(); counter++) {
					if (current.getChildrens().get(counter).getTopicentry()
							.getTopicName().equals(topicPath[i + 1])) {
						current = current.getChildrens().get(counter);
						if (i == topicPath.length - 2) {
							return topicCode2mutiv6Addr(current.getChildrens().get(counter).getTopicentry().getTopicCode());
						}
						break;
					}
				}
			} else {
//				log.error("subscribe faild! there is not this topic in the topic tree!");
				return "faild";
			}
		}
		return "";
	}

	public static String getNewTopicCode(String newTopicCode) {
		char[] tmp = new char[128];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = '0';
		}

		char[] parm = newTopicCode.toCharArray();
		for (int i = 0; i < parm.length; i++) {
			tmp[i] = parm[i];
		}

		String res = String.valueOf(tmp);
		return res;
	}

	public static List<Flow> cacluate(Map<String, Controller> controllers) {
		//这里是实现算法的地方，controllers提供了本集群所有的订阅主题和交换机
		//交换机的DPID是其唯一标识，假定每个集群有一个repSwitch，其余的
		//所有集群都是发布者，topics不为空的集群是订阅者
		//以集群为节点，针对每个topic进行计算

		List<Flow> flows = new ArrayList<>();
		String curTopic;
		ArrayList<Integer> sub = new ArrayList<>();//订阅节点集合
		ArrayList<Integer> pub = new ArrayList<>();//发布节点集合

		//保存着连接关系的邻接表
		int[][] weight = new int[1000][1000];
		for (int i = 0; i < 1000; i++)
			for (int j = 0; j < 1000; j++)
				weight[i][j] = M;

		ArrayList<Controller> controllers_1 = new ArrayList<>();

//      这里有个问题，如果是按照主题匹配的话，那么应该返回的就是主题和流表的Map
//      HashMap<String,List<Flow>>


		//获取LDAP中存储的一级主题名称
		List<WSNTopicObject> topics = TopicTreeManager.topicTree.getChildrens();
		for (WSNTopicObject to : topics) {
			String topicName = to.getTopicentry().getTopicName();
			String topicCode = to.getTopicentry().getTopicCode();

			String topicCodeLength = getTopicLength(topicCode);

			topicCode = Integer.toBinaryString(Integer.valueOf(topicCode).byteValue());//topicCode从10进制字符串转为2进制字符串
			String newTopicCode = "11111111" + "0000" + "1110" + "10" + topicCodeLength + topicCode;

			String finalNewTopicCode = getNewTopicCode(newTopicCode);//结尾补0

			topicCodes.put(topicName, finalNewTopicCode);
		}

		ArrayList<Switch> weightIdList = new ArrayList<>();
		List<String> subTopics = new ArrayList<>();//subTopics存的是这个Controller上面订阅的主题
		//这里是标记每个Switch的编号
		for (Controller controller : controllers.values()) {
			controllers_1.add(controller);
			for (String s : controller.getTopics()) {
				//？这里要按照已有的主题树进行计算，就是父主题下每层子主题都要写进去
				// 从WSNTopicObject.topicTree.childrens.getName这样，获取每个一级子主题的名字
				subTopics.add(s);
			}
			Switch rep = controller.getRepSwitch();
			weightIdList.add(rep);
		}

//      这里假定它是有订阅主题的，先只匹配第一个主题
		curTopic = subTopics.get(0);

		//遍历所有控制器，找到所有连通路，构建邻接表
		for (Controller controller : controllers.values()) {
			if (controller.getTopics().contains(curTopic)) {//订阅了当前匹配的主题，那么它的repSwitch的weightId就加到sub中
				sub.add(weightIdList.indexOf(controller.getRepSwitch()));
			}
			Switch rep = controller.getRepSwitch();

			Map<String, Switch> switchMap = controller.getSwitchMap();
			Map<Integer, DevInfo> hostMap = rep.getWsnDevMap();
			for (Object device : hostMap.values()) {
				if (device instanceof Switch && !switchMap.containsValue(device)) {
					//当前控制器的头交换机连接的设备是交换机，且不是组内的
					Switch target = (Switch) device;
					int i = weightIdList.indexOf(rep);
					int j = weightIdList.indexOf(target);
					weight[i][j] = 1;
					weight[j][i] = 1;//这里是无向图
					//！在这里保存
				}
			}
			pub.add(weightIdList.indexOf(controller.getRepSwitch()));
		}

		int[][] finalDecision = new int[1000][1000];//第一个下标：第几个订阅点；第二个下标：中间的跳转路径

		for (int i = 0; i < sub.size(); i++) {
			int[][] path = getEachStop(weight, sub.get(i));/*算出来第i个订阅点到其他各个发布点的路径，
			path的第一个下标：第几个发布点；第二个下标：中间的跳转路径*/
			int shortestPubNo = -1;
			int shortestDistance = 0;
			for (int j = 0; j < path.length; j++) {//对着每个发布点看，哪个是最短的
				if (shortestPubNo == -1) {
					shortestPubNo = j;//这里假定每条路的流量都是“1”，这样distance数组的长度就是这条路的长度
					shortestDistance = path[j].length;
				} else if (shortestDistance > path[j].length) {
					shortestPubNo = j;
					shortestDistance = path[j].length;
				}
			}
			finalDecision[i] = path[shortestPubNo];
		}
		//到这里，就算出来了每个订阅节点应该是从哪个发布者接收信息
		//流表格式如下：
		// "switch": "00:00:00:00:00:00:00:01", "name":"flow-mod-1", "cookie":"0", "priority":"32768",
		// "ipv6_dst":"XXXX...","active":"true", "actions":"output=2"
		//里面的ipv6_dst字段是IPv6组播地址，一个主题对应一个地址
		for (int i = 0; i < finalDecision.length; i++) {

			int[] path = finalDecision[i];

			Switch suber = weightIdList.get(path[0]);


			//一个订阅点也要对应多条流表
			for (int j = path.length - 1; j > 0; j--) {
				Switch curSwitch = weightIdList.get(path[path.length - 1]);//从发布点倒着往回找

//				这个Calculate函数暂时不用了，所以这里先注掉了
//				Flow f = generateFlow(curSwitch, topicCodes, "Flood");

//				flows.add(f);
			}
		}
		return flows;
	}

	public static Flow generateFlow(Switch curSwitch, String curTopic, String targetPort) {
		String dpid = curSwitch.getDPID();

		HashMap<String, String> parms = new HashMap<>();
		parms.put("switch", dpid);
		parms.put("name", "flow-mod-" + flowcount++);//！不是写死的，最好每个流表不一样
		parms.put("cookie", "0");
		parms.put("priority", "32768");
//		parms.put("ipv6_dst", topicCodes.get(curTopic));
//		parms.put("ipv6_dst", topicName2mutiv6Addr(curTopic));//测试v6地址转化函数
		parms.put("ipv6_dst", "FF01:0000:0000:0000:0001:2345:6789:ABCD");//测试openflow协议
		parms.put("active", "true");

		parms.put("actions", "output=" + targetPort);
		parms.put("eth_type", "0x86dd");

		Flow f = new Flow(dpid);
		JSONObject content = new JSONObject(parms);
		f.setContent(content);

		return f;
	}

	public static ArrayList<int[]> getEachRoute_wrong(int[][] connected, ArrayList<Integer> subers) {

		int[][] weight = new int[connected.length][connected[0].length];
		for (int i = 0; i < connected.length; i++) {
			for (int j = 0; j < connected[0].length; j++) {
				weight[i][j] = connected[i][j];// 会更改connected的值，所以需要预先存一份
			}
		}

		ArrayList<int[]> res = new ArrayList<>();

		for (int k = 0; k < subers.size(); k++) {
			int start = subers.get(k);

			int[][] path = Dijkstra.getEachStop(connected, start);// path[i][j]到第i个节点路上的第j跳
			connected = weight;
			int[] shortPath = Dijkstra.dijkstra(connected, start);// shortPath[i]到第i个节点的总长度

			int shortestPath = M;
			int shortestPathNum = 0;
			for (int i = 0; i < shortPath.length; i++) {
				if (shortPath[i] > 0 && shortPath[i] < shortestPath) {
					shortestPath = shortPath[i];
					shortestPathNum = i;
				}
			}

			// ！这里需要确保有至少一条联通路
			int[] eachStop = path[shortestPathNum];
			res.add(eachStop);
		}

		return res;
	}

	//发布者到每一个订阅者的路径
	public static ArrayList<int[]> getEachRoute(int[][] connected, ArrayList<Integer> pubers) {

		ArrayList<int[]> res = new ArrayList<>();

		for (int k = 0; k < pubers.size(); k++) {
			int start = pubers.get(k);

			int[][] path = Dijkstra.getEachStop(connected, start);// path[i][j]到第i个节点路上的第j跳

			for (int i = 0; i < path.length; i++) {
				res.add(path[i]);
			}
		}

		return res;
	}

	/*public static Integer getNextJumpPort(Switch curSwitch, ArrayList<Switch> weightIdList, int[] path, int j) {
		Integer nextJumpWeightId = path[j - 1];
		Switch s = weightIdList.get(nextJumpWeightId);

		Map<Integer, DevInfo> map = s.getWsnDevMap();
		for (Map.Entry<Integer, DevInfo> entry : map.entrySet()) {
			if (entry.getValue().equals(curSwitch)) {
				return entry.getKey();
			}
		}

		return 0;
	}
*/
	public static void main(String args[]) throws InterruptedException {

//		int[][] weight = new int[20][20];
		int[][] weight;
		ArrayList<Integer> subers = new ArrayList<>();//存的是数字，也即Switch在邻接表中的序号
		ArrayList<Integer> pubers = new ArrayList<>();

		String[] subers_dpid = {"00:00:16:18:67:3a:db:47",
				"00:00:9e:50:72:0c:5c:4e",
				"00:00:f2:8d:9c:69:51:43",
				"00:00:8a:54:d0:dc:26:44",
				"00:00:5c:f3:fc:db:63:a2"};

		Controller G2 = new Controller("http://10.109.253.2:8080");

		long t1 = System.currentTimeMillis();
		//获取拓扑邻接表
		Map<String, Switch> pubersMap = GlobleUtil.getAllSwitch(G2);
		GlobleUtil.initFunc(G2);
		weight = GlobleUtil.getTopology(G2);

		String[] pubers_dpid = new String[pubersMap.size()];
		int x = 0;//把所有Switch的dpid存起来
		for (Map.Entry<String, Switch> entry : pubersMap.entrySet()) {
			pubers_dpid[x] = entry.getKey();
			x++;
		}

		long t2 = System.currentTimeMillis();
		//用dpid在邻接表中查找相应交换机的序号
		for (int i = 0; i < subers_dpid.length; i++) {
			for (Map.Entry<Integer, String> entry : GlobleUtil.switchMap.entrySet()) {
				if (subers_dpid[i].equals(entry.getValue())) {
					subers.add(entry.getKey());
				}
			}
		}
		for (int i = 0; i < pubers_dpid.length; i++) {
			for (Map.Entry<Integer, String> entry : GlobleUtil.switchMap.entrySet()) {
				if (pubers_dpid[i].equals(entry.getValue())) {
					pubers.add(entry.getKey());
				}
			}
		}

		long t3 = System.currentTimeMillis();
		//计算路由跳转
//		ArrayList<int[]> res = getEachRoute_wrong(weight, subers);
		ArrayList<int[]> res = getEachRoute(weight, pubers);

		long t4 = System.currentTimeMillis();
		//下发流表
		for (int[] route : res) {
			downRouteFlow(G2, route);
		}
		long t5 = System.currentTimeMillis();

		long tmp = t5 - t1;
		System.out.println("t5-t1==" + String.valueOf(tmp));
		tmp = t4 - t3;
		System.out.println("t4-t3==" + String.valueOf(tmp));


		/*Controller G2 = new Controller("10.109.253.2");
		Switch br0 = new Switch();
		G2.getSwitchMap().put("dpid", br0);

		Controller G21 = new Controller("10.109.253.2");
		Switch br1 = new Switch();
		G21.getSwitchMap().put("dpidbr1", br1);


		Controller G22 = new Controller("10.109.253.2");
		Switch br2 = new Switch();
		G21.getSwitchMap().put("dpidbr2", br2);


		Controller G23 = new Controller("10.109.253.2");
		Switch br3 = new Switch();
		G21.getSwitchMap().put("dpidbr3", br3);

		Controller G3 = new Controller("10.109.253.3");
		Switch br0G3 = new Switch();
		G21.getSwitchMap().put("dpidbr0G3", br0G3);

		Controller G4 = new Controller("10.109.253.4");
		Switch br0G4 = new Switch();
		G21.getSwitchMap().put("dpidbr0G4", br0G4);

		GlobleUtil.getInstance().controllers.put("G2", G2);
		GlobleUtil.getInstance().controllers.put("G3", G3);
		GlobleUtil.getInstance().controllers.put("G4", G4);*/
	}

	public void route(String topic) {

	}
}