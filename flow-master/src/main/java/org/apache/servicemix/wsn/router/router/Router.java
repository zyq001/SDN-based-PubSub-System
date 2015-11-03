package org.apache.servicemix.wsn.router.router;

import edu.bupt.wangfu.sdn.info.Controller;
import edu.bupt.wangfu.sdn.info.DevInfo;
import edu.bupt.wangfu.sdn.info.Flow;
import edu.bupt.wangfu.sdn.info.Switch;
import org.apache.servicemix.application.WSNTopicObject;
import org.apache.servicemix.wsn.router.mgr.base.SysInfo;
import org.apache.servicemix.wsn.router.topictree.TopicTreeManager;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.servicemix.wsn.router.router.LCW.Dijkstra.Dijsktra;

/**
 * 计算指定名称的转发路由
 * 针对该名称的所有订阅节点，由集群名比较选取根节点
 * 根据迪杰斯特拉算法由根节点开始计算转发树
 * 将转发树的根节点及本节点的吓一跳转发记录在名称路由结构中
 *
 * @author Sylvia
 */

public class Router extends SysInfo implements IRouter {

	static int M = 10000;

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
				i = M;

		ArrayList<Controller> controllers_1 = new ArrayList<>();

//      这里有个问题，如果是按照主题匹配的话，那么应该返回的就是主题和流表的Map
//      HashMap<String,List<Flow>>

		HashMap<String, String> topicCodes = new HashMap<>();//key是topicName，value是topicCode

		//获取LDAP中存储的一级主题名称
		List<WSNTopicObject> topics = TopicTreeManager.topicTree.getChildrens();
		for (WSNTopicObject to : topics) {
			String topicName = to.getTopicentry().getTopicName();
			String topicCode = to.getTopicentry().getTopicCode();

			String topicCodeLength = getTopicLength(topicCode);

			String newTopicCode = "11111111" + "0000" + "1110" + "10" + topicCodeLength + topicCode;

			String finalNewTopicCode = getNewTopicCode(newTopicCode);

			topicCodes.put(topicName, finalNewTopicCode);
		}

//      先只匹配第一个主题，这里假定它是有订阅主题的
		//？这里要按照已有的主题树进行计算，从WSNTopicObject.topicTree.childrens.getName这样，获取每个一级子主题的名字
		List<String> subTopics = controllers.get(0).getTopics();
		curTopic = subTopics.get(0);
//            for (String topic : subTopics) {
//                curTopic = topic;
//            }

		ArrayList<Switch> weightIdList = new ArrayList<>();
		//这里是标记每个Switch的编号
		for (Controller controller : controllers.values()) {
			controllers_1.add(controller);
			Switch rep = controller.getRepSwitch();
			weightIdList.add(rep);
		}

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
			int[][] path = Dijsktra(weight, sub.get(i));/*算出来第i个订阅点到其他各个发布点的路径，
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

				String dpid = curSwitch.getDPID();

				HashMap<String, String> parms = new HashMap<>();
				parms.put("switch", curSwitch.getMac());
				parms.put("name", "flow-mod-1");//这个怎么获得？牛琳琳说是写死的
				parms.put("cookie", "0");
				parms.put("priority", "32768");
				parms.put("ipv6_dst", topicCodes.get(curTopic));
				parms.put("active", "true");

				Integer nextJumpPort = getNextJumpPort(suber, weightIdList, path, j);
				parms.put("actions", "output=" + nextJumpPort.toString());//应当是path[j+1]这个Switch对应的端口

				Flow f = new Flow(dpid);
				JSONObject content = new JSONObject(parms);
				f.setContent(content);

				flows.add(f);
			}
		}
		return flows;
	}

	public static Integer getNextJumpPort(Switch curSwitch, ArrayList<Switch> weightIdList, int[] path, int j) {
		Integer nextJumpWeightId = path[j - 1];
		Switch s = weightIdList.get(nextJumpWeightId);

		Map<Integer, DevInfo> map = s.getWsnDevMap();
		for (Map.Entry<Integer, DevInfo> entry : map.entrySet()) {
			if (entry.getValue() == curSwitch) {
				return entry.getKey();
			}
		}

		return 0;
	}

	public void route(String topic) {

	}
}