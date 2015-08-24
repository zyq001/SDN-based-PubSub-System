package org.apache.servicemix.wsn.router.router;

import java.util.ArrayList;

public class Node {
	private String name;
	private ArrayList<Neighbor> neighbors;
	private int length;
	private int sum;
	
	public Node(String name){
		this.name = name;
		neighbors=new ArrayList<Neighbor>();
	}
	
	public Node() {
		
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public ArrayList<Neighbor> getNeighbors() {
		return neighbors;
	}
	
	public void setNeighbors(ArrayList<Neighbor> neighbors) {
		this.neighbors = neighbors;
	}
	
	public void addNeighbor(Neighbor neighbor) {
		this.neighbors.add(neighbor);
	}
	
	public int getSum() {
		return this.sum;
	}
	
	public void setSum(int sum) {
		this.sum = sum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
} 