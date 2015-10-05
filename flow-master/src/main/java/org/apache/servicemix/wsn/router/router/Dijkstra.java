package org.apache.servicemix.wsn.router.router;

import org.apache.servicemix.wsn.router.msg.tcp.LSA;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * open????????????????????
 * close??????????????????????
 * goal????????????????????????????????
 * free????????????????????????????????????
 * reach??????????????????????????????
 * pathInfo??????????????????????
 * finalPath??????????????????
 * @author Sylvia
 *
 */
public class Dijkstra {
	TreeMap<String, Node> open = new TreeMap<String, Node>();
	Map<String, Node> close = new HashMap<String, Node>();
	ArrayList<String> goal = new ArrayList<String>();
	ArrayList<String> free = new ArrayList<String>();
	ArrayList<String> reach = new ArrayList<String>();
	Map<String, String> pathInfo = new HashMap<String, String>();// ????????????
	Map<String, String> finalPath = new HashMap<String, String>();//??????????????
	String groupName;
	
	Map<String, Integer> path = new HashMap<String, Integer>();
	int MAX_CHILDREN = 2;
	int MAX_VALUE = 40000;

	public Node init(ConcurrentHashMap<String, LSA> lsdb, ArrayList<String> goal,String groupName) {
		// ??????????????close,????????????open
		Node start = new MapBuilder().build(lsdb.values(), goal, open, close,free,reach);
		this.goal = goal;
		this.groupName = groupName;
		return start;
	}

	// ????????????
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
				if(neighbor.isEmpty()) {
					continue;
				}
				for (Neighbor nbr : neighbor) {
					if (open.containsKey(nbr.neighbor.getName())) {// ????????????open??
						Integer newCompute = path.get(reached.getName())
								+ nbr.distance;
						if (shstl > newCompute) {// ??????????????????????????????????
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
			if(shstn == null) {
				break;
			}
			close.put(shstn.getName(), shstn);
			open.remove(shstn.getName());
			pathInfo.put(shstn.getName(), father);
			path.put(shstn.getName(), shstl);
			if (reach.contains(shstn.getName())) {
				finalPath.put(shstn.getName(),father);
				Node fa = close.get(father);
				int sum = fa.getSum()+1;
				fa.setSum(sum);
				reach.remove(shstn.getName());
				//??fa??????????????goal??????????????????????????????close??????fa????????????????????????????????open??
				if (sum >= MAX_CHILDREN) {
				//	if(fa.getName().equals(localAddr))
			//			cal = false;
					free.remove(fa.getName());
					close.remove(fa.getName());
					open.put(fa.getName(), fa);
					pathInfo.remove(fa.getName());
					path.remove(fa.getName());
					ArrayList<String> remove=new ArrayList<String>();
					for (Node n : close.values()) {
							if (pathInfo.get(n.getName()).equals(
									fa.getName()) && !free.contains(n.getName())) {
								remove.add(n.getName());
								pathInfo.remove(n.getName());
								path.remove(n.getName());
								open.put(n.getName(), n);
							}
							
					}
					for(String re : remove) 
						close.remove(re);
					//??????????????????????????????????????????????????
					start = getShortestNode();
				}
			}
		}
	}
	
	public String CalFather() {
		String father = "";
		for(String n : finalPath.keySet()) {
			if(n.equals(groupName)) {
				father = finalPath.get(n);
				break;
			}
		}
		return father;
	}
	
	public ArrayList<String> savePath() {
		ArrayList<String> next = new ArrayList<String>();
		for(String n : finalPath.keySet()) {
			if(finalPath.get(n).equals(groupName))
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
	 * ??????node????????????
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