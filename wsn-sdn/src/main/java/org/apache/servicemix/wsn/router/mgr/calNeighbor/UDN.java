package org.apache.servicemix.wsn.router.mgr.calNeighbor;

class UDN {

	double[][] arcs;
	int vexNum, arcNum;

	public UDN(int downVexNum, int downArcNum) {
		vexNum = downVexNum;
		arcNum = downArcNum;
		arcs = new double[vexNum][arcNum];
		initUND();
	}

	//��ʼ��UDN
	private void initUND() {
		for (int i = 0; i < vexNum; i++)
			for (int j = 0; j < vexNum; j++) {
				arcs[i][j] = NeigSelect.getINFINITY();
			}
	}

	//�����ڽӾ���	
	public void creactUND(int m, int n, double d) {

		arcs[m][n] = d;
		arcs[n][m] = arcs[m][n];

	}


	//��RNG��Ӧ�ߵ�Ȩֵ�������
	public void delete(int m, int n) {
		arcs[m][n] = NeigSelect.getINFINITY();
		arcs[n][m] = arcs[m][n];
	}

}
