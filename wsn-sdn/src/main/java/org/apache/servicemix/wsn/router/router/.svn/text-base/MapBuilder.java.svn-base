package org.apache.servicemix.wsn.router.router;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.servicemix.wsn.router.msg.tcp.LSA;

public class MapBuilder {
	/**
	 * 初始化
	 * @param pi为全网拓扑
	 * @param open为为遍历的节点
	 * @param close为已经遍历的节点，设定open排序后的第一个首先放入close中
	 * @return起始节点，即close中的元素
	 */
	public Node build(Collection<LSA> lsdb,ArrayList<String> goal,TreeMap<String,Node> open, Map<String,Node> close,ArrayList<String>free, ArrayList<String> reach){
		if(lsdb.isEmpty() || goal.isEmpty())
			return null;
		for(LSA lsa : lsdb) {
			Node node = new Node(lsa.originator);
			open.put(node.getName()	, node);
		}
		for(LSA lsa : lsdb) {
			Node node = open.get(lsa.originator);
			for(String neighbor : lsa.distBtnNebrs.keySet()) {
				Node ne = null;
				if(open.containsKey(neighbor)) {
					ne = open.get(neighbor);
				} else {
					ne = new Node(neighbor);
					open.put(neighbor, ne);
				}
				boolean include = false;
				for(Neighbor n : node.getNeighbors()) {
					if(n.neighbor.equals(ne)) {
						include = true;
						break;
					}
				}
				if(!include) {
					node.addNeighbor(new Neighbor(ne, lsa.distBtnNebrs.get(neighbor).getDist()));
				}
				include = false;
				for(Neighbor n : ne.getNeighbors()) {
					if(n.neighbor.equals(node)) {
						include = true;
						break;
					}
				}
				if(!include) {
					ne.addNeighbor(new Neighbor(node, lsa.distBtnNebrs.get(neighbor).getDist()));
				}
			}
		}
		for(String s : goal) {
			free.add(s);
			reach.add(s);
		}
		
		Node start = null;
		boolean st = true;
		for(Node  n: open.values()) {
			//选择起始节点
			if(st && goal.contains(n.getName()) ) {
				start = n;
				reach.remove(n.getName());
				st = false;
			}
			
			//对所有节点的邻居节点按名称进行排序
			ComparatorNeighbor comparator = new ComparatorNeighbor();
			Collections.sort(n.getNeighbors(), comparator);
			}
		close.put(start.getName(), start);
		open.remove(start.getName());
		return start;
	}
	
	public class ComparatorNeighbor implements Comparator<Object> {

		public int compare(Object arg0, Object arg1) {
			Neighbor ne1 = (Neighbor) arg0;
			Neighbor ne2 = (Neighbor) arg1;
			return ne1.neighbor.getName().compareTo(ne2.neighbor.getName());
		}
		
	}
}