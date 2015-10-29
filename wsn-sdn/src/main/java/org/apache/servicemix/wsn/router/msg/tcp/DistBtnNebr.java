package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

public class DistBtnNebr implements Serializable {
	public int dist; // 与该邻居的距离

	public DistBtnNebr(int dist) {
		this.dist = dist;
	}

	public int getDist() {
		return dist;
	}
}
