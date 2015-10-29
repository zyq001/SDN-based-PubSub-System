package org.apache.servicemix.wsn.router.mgr.calNeighbor;

import org.apache.servicemix.wsn.router.mgr.base.SysInfo;
import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class NeigBuild extends SysInfo {
	public static String answerOrder; // socket server应答指令
	public static ConcurrentHashMap<String, Node> map;
	private static int neigsMax;// 邻居数上限
	private static int neigsMin;// 邻居数下限
	private static int neigsDefault;// 默认邻居数
	private static int neigsCount = 0; // 当前邻居数
	private static ArrayList<String> neigsIPArray;// 本地已建邻居

	public NeigBuild() {
		neigsIPArray = new ArrayList<String>();
		map = new ConcurrentHashMap<String, Node>();
	}

	// 将每个ip分割成四段int数
	public static int[] ipAddressSplit(String ipAddress) {
		String[] ipSplit = ipAddress.split("\\.");
		int[] ip = new int[ipSplit.length];
		if (ipSplit.length == 4) {
			for (int i = 0; i < ipSplit.length; i++) {
				ip[i] = Integer.parseInt(ipSplit[i]);
			}
		}
		return ip;
	}

	// 邻居计算核心算法
	private static ArrayList<String> ipSelected() {// ip地址由近及远排序

		String[] mask = localNetmask.split("\\.");
		int[] neigsIPInt = new int[mask.length]; // 邻居ip地址分段int
		int[] netmaskInt = new int[mask.length]; // 邻居子网掩码分段int
		int[] networkAddInt = new int[mask.length]; // 邻居网络地址分段int
		int[] localNetworkInt = new int[mask.length];// 本地网络地址分段int
		int[] localIPInt = new int[mask.length]; // 本地ip地址分段int
		int[] localNetworkAddrInt = new int[mask.length]; // 本地子网掩码分段int
		ArrayList<Integer> neighborsCount = new ArrayList<Integer>(); //邻居计数
		long[] networkAddrNum;// 邻居网络地址折算值
		long localNetworkAddrNum;// 本地网络地址折算值

		// 获取本机网络地址
		for (int i = 0; i < mask.length; i++) {
			localNetworkAddrInt[i] = Integer.parseInt(mask[i]);
		}

		localIPInt = ipAddressSplit(localAddr);
		for (int i = 0; i < 4; i++) {
			localNetworkInt[i] = localIPInt[i] & localNetworkAddrInt[i];
		}
		localNetworkAddrNum = localNetworkInt[0] * 255 * 255 * 255
				+ localNetworkInt[1] * 255 * 255 + localNetworkInt[2] * 255
				+ localNetworkInt[3];

		networkAddrNum = new long[map.size()];
		Iterator<Node> iterator = map.values().iterator();
		ConcurrentHashMap<Integer, String> m = new ConcurrentHashMap<Integer, String>();
		for (int k = 0; k < map.size(); k++) {
			Node node = iterator.next();
			neigsIPInt = ipAddressSplit(node.addr); // 将目的地址ip段取出
			netmaskInt = ipAddressSplit(node.netmask);
			for (int i = 0; i < 4; i++) {
				networkAddInt[i] = neigsIPInt[i] & netmaskInt[i]; // 邻居网络地址
			}

			neighborsCount.add(node.neighborCount);
			networkAddrNum[k] = networkAddInt[0] * 255 * 255 * 255
					+ networkAddInt[1] * 255 * 255 + networkAddInt[2] * 255
					+ networkAddInt[3];
			m.put(k, node.addr);
		}

		// 按准邻居网络地址总值与本地网络地址总值之差的绝对值排序
		NeigSelect localNeigsSelect = new NeigSelect();
		localNeigsSelect.buildUDN(localNetworkAddrNum, networkAddrNum,
				neighborsCount);// 构建全局带权网
		// 计算本地邻居，获取被选中邻居的下标
		ArrayList<Integer> neigsIPSubNum = localNeigsSelect.accuNeigsIPNum(
				localNetworkAddrNum, networkAddrNum, localNeigsSelect.getUDN());
		ArrayList<String> selectedNeigsIP = new ArrayList<String>();
		for (Integer i : neigsIPSubNum) {
			selectedNeigsIP.add(m.get(i));
			neigsCount++;
			// neigsIPArray.add(neigsIP[neigsIPSubNum.get(i)]);//将算法选出的邻居存入全局数组变量
			// System.out.println("本地选择的邻居第"+(i+1)+"个："+selectedNeigsIP[i]);
		}

		// //测试：打印全局带权网
		// System.out.println("全局UDN网：");
		// for(int m=0; m<=downloadIP2.size(); m++){
		// for(int n=0; n<=downloadIP2.size(); n++){
		//
		// System.out.print(localNeigsSelect.getUDN().arcs[m][n]+"  ");
		// }
		// System.out.println();
		// }
		//
		// //测试：打印全局RNG图
		// System.out.println("全局RNG图：");
		// localNeigsSelect.globalRNG(localNetworkAddrNum, networkAddrNum,
		// localNeigsSelect.getUDN());
		// for(int m=0; m<=downloadIP2.size(); m++){
		// for(int n=0; n<=downloadIP2.size(); n++){
		// System.out.print(localNeigsSelect.getRNG().arcs[m][n]+"  ");
		// }
		// System.out.println();
		// }

		return selectedNeigsIP;

	}

	// 设置邻居数上下限
	public void setValue(int max, int min, int def) {
		neigsMax = max;
		neigsMin = min;
		neigsDefault = def;
	}

	// 返回本地邻居数
	public int getNeigCount() {
		return neigsCount;
	}

	// 取得已建立的邻居ip列表
	public ArrayList<String> getBuildedNeigsIP() {
		return neigsIPArray;
	}

	// 第一次构建并返回邻居列表
	public ArrayList<String> BuildAGetNeigs() {// 获取邻居列表
		map.clear();
		for (GroupUnit g : groupMap.values()) {
			if (!lsdb.isEmpty() && lsdb.containsKey(g.name)) {
				map.put(g.addr, new Node(g.addr, g.netmask, g.name, lsdb.get(g.name).distBtnNebrs.size()));
			} else {
				map.put(g.addr, new Node(g.addr, g.netmask, g.name, 0));
			}
		}
		ArrayList<String> selectedNeigsIP = ipSelected();
		selectedNeigsIP = this.neigsCountCheck(selectedNeigsIP);
		ArrayList<String> selectedGroups = new ArrayList<String>();
		Iterator<String> iterator = selectedNeigsIP.iterator();
		for (int j = 0; j < selectedNeigsIP.size() && j < neigsDefault + 0.5; j++) {
			String addr = iterator.next();
			if (addr != null && map.containsKey(addr) && !selectedGroups.contains(map.get(addr).name)) {
				selectedGroups.add(map.get(addr).name);
			}
		}
		return selectedGroups;
	}

	// 邻居数变化的时候检查是否需要重新添加邻居，返回最终邻居列表
	public ArrayList<String> NeigsChange(ArrayList<String> out) {
		ArrayList<String> neigsAdd = new ArrayList<String>();
		neigsIPArray.clear();
		map.clear();
		for (String name : neighbors) {
			neigsIPArray.add(groupMap.get(name).addr);
		}
		neigsCount = neighbors.size();
		for (GroupUnit g : groupMap.values()) {
			if (g.addr.equals(localAddr) || neighbors.contains(g.name) || out.contains(g.name)) {
				continue;
			}
			if (!lsdb.isEmpty() && lsdb.containsKey(g.name)) {
				map.put(g.addr, new Node(g.addr, g.netmask, g.name, lsdb.get(g.name).distBtnNebrs.size()));
			} else {
				map.put(g.addr, new Node(g.addr, g.netmask, g.name, 0));
			}
		}
		neigsIPArray = neigsCountCheck(neigsIPArray);

		Iterator<String> iterator = neigsIPArray.iterator();
		for (int j = 0; j < neigsIPArray.size() && j < neigsDefault + 0.5; j++) {
			String addr = iterator.next();
			if (addr != null && map.containsKey(addr) && !neigsAdd.contains(map.get(addr).name)) {
				neigsAdd.add(map.get(addr).name);
			}
		}
		return neigsAdd;
	}

	// 邻居数检查
	public ArrayList<String> neigsCountCheck(ArrayList<String> selectedIP) {// 邻居数检查

		for (int k = 0; k < neigsMin; k++) {
			if ((map.size() - neigsCount) > 0 && neigsCount < neigsMin) {
				for (String addr : map.keySet()) {
					for (int j = 0; j < selectedIP.size(); j++)
						if (selectedIP.get(j).equals(addr)) {
							map.remove(addr);
						}
				}
				selectedIP.addAll(ipSelected());
			} else {
				break;
			}
		}
		return selectedIP;
	}

}
