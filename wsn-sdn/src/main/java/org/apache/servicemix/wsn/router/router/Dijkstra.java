package org.apache.servicemix.wsn.router.router;

import org.apache.servicemix.wsn.router.msg.tcp.LSA;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * open中是待计算的所有节点
 * close中是已经计算出来的节点
 * goal中是待计算的订阅本主题的目的节点
 * free中是订阅本主题并且还可以有孩子的节点
 * reach中是订阅本主题但未计算到的节点
 * pathInfo存储所有已经到达的节点
 * finalPath存储订阅节点的路径
 *
 * @author Sylvia
 */
public class Dijkstra {
	TreeMap<String, Node> open = new TreeMap<String, Node>();
	Map<String, Node> close = new HashMap<String, Node>();
	ArrayList<String> goal = new ArrayList<String>();
	ArrayList<String> free = new ArrayList<String>();
	ArrayList<String> reach = new ArrayList<String>();
	Map<String, String> pathInfo = new HashMap<String, String>();// 封装路径信息
	Map<String, String> finalPath = new HashMap<String, String>();//特定节点的路径
	String groupName;

	Map<String, Integer> path = new HashMap<String, Integer>();
	int MAX_CHILDREN = 2;
	int MAX_VALUE = 40000;

	public Node init(ConcurrentHashMap<String, LSA> lsdb, ArrayList<String> goal, String groupName) {
		// 将初始节点放入close,其他节点放入open
		Node start = new MapBuilder().build(lsdb.values(), goal, open, close, free, reach);
		this.goal = goal;
		this.groupName = groupName;
		return start;
	}

	// 计算最短路径
	public void computePath(Node start) {
		path.put(start.getName(), new Integer(0));
		pathInfo.put(start.getName(), start.getName());
		boolean cal = true;
		while (!reach.isEmpty() && cal) {
			ArrayList<Neighbor> neighbor = null;
			int shstl = MAX_VALUE;
			Node shstn = null;
			String father = "";
			for (Node reached : close.values()) {
				neighbor = reached.getNeighbors();
				if (neighbor.isEmpty()) {
					continue;
				}
				for (Neighbor nbr : neighbor) {
					if (open.containsKey(nbr.neighbor.getName())) {// 如果子节点在open中
						Integer newCompute = path.get(reached.getName())
								+ nbr.distance;
						if (shstl > newCompute) {// 之前设置的距离大于新计算出来的距离
							shstl = newCompute;
							shstn = nbr.neighbor;
							if (free.contains(reached.getName()))
								father = reached.getName();
							else
								father = pathInfo.get(reached.getName());
						}
					}
				}
			}
			if (shstn == null) {
				break;
			}
			close.put(shstn.getName(), shstn);
			open.remove(shstn.getName());
			pathInfo.put(shstn.getName(), father);
			path.put(shstn.getName(), shstl);
			if (reach.contains(shstn.getName())) {
				finalPath.put(shstn.getName(), father);
				Node fa = close.get(father);
				int sum = fa.getSum() + 1;
				fa.setSum(sum);
				reach.remove(shstn.getName());
				//若fa节点所能容纳的goal集合内的孩子已经达到上限，则在close中删除fa以及以它为上一跳的节点，重新放入open中
				if (sum >= MAX_CHILDREN) {
					//	if(fa.getName().equals(localAddr))
					//			cal = false;
					free.remove(fa.getName());
					close.remove(fa.getName());
					open.put(fa.getName(), fa);
					pathInfo.remove(fa.getName());
					path.remove(fa.getName());
					ArrayList<String> remove = new ArrayList<String>();
					for (Node n : close.values()) {
						if (pathInfo.get(n.getName()).equals(
								fa.getName()) && !free.contains(n.getName())) {
							remove.add(n.getName());
							pathInfo.remove(n.getName());
							path.remove(n.getName());
							open.put(n.getName(), n);
						}

					}
					for (String re : remove)
						close.remove(re);
					//选择距离起始节点最近的目标节点重新作为开始节点计算
					start = getShortestNode();
				}
			}
		}
	}

	public String CalFather() {
		String father = "";
		for (String n : finalPath.keySet()) {
			if (n.equals(groupName)) {
				father = finalPath.get(n);
				break;
			}
		}
		return father;
	}

	public ArrayList<String> savePath() {
		ArrayList<String> next = new ArrayList<String>();
		for (String n : finalPath.keySet()) {
			if (finalPath.get(n).equals(groupName))
				next.add(n);
		}
		return next;
	}

	public void printPathInfo() {
		Set<Map.Entry<String, String>> pathInfos = finalPath.entrySet();
		for (Map.Entry<String, String> pathInfo : pathInfos) {
			System.out.println(pathInfo.getKey() + "<-" + pathInfo.getValue());
		}
	}

	/**
	 * 获取与node最近的子节点
	 */
	private Node getShortestNode() {
		Node res = null;
		int minDis = Integer.MAX_VALUE;
		for (Node node : close.values()) {
			if (goal.contains(node.getName())
					&& minDis > path.get(node.getName())) {
				minDis = path.get(node.getName());
				res = node;
			}
		}
		return res;
	}
}