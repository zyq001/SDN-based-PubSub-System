package org.apache.servicemix.wsn.router.mgr.calNeighbor;

class RNG extends UDN {


	public RNG(int downVexNum, int downArcNum) {
		super(downVexNum, downArcNum);
		// TODO Auto-generated constructor stub

	}

	//	����Ӧ�ߵ�Ȩֵ�������
	public void delete(int m, int n) {
		arcs[m][n] = NeigSelect.getINFINITY();
		arcs[n][m] = arcs[m][n];
	}
}
