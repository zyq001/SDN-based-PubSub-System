package org.apache.servicemix.wsn.router.mgr.calNeighbor;

import java.util.ArrayList;
import java.util.Iterator;

public class NeigSelect {

	private static long INFINITY = Long.MAX_VALUE;// 代表正无穷
	private static int MaxVertexNum = 30;
	// int[][] arcs = new int[MaxVertexNum][MaxVertexNum];
	// int vexNum, arcNum;
	private static UDN UDN;
	private static UDN RNG;
	private static ArrayList<Integer> neigsIPNum = new ArrayList<Integer>();

	// public void NeigSelect(int localIPNUM, int[] neigNetworkNum){
	//
	// }
	public static int getMaxVertexNum() {
		return MaxVertexNum;
	}

	public int setMaxVertexNum(int num) {
		MaxVertexNum = num;
		return MaxVertexNum;
	}

	public static long getINFINITY() {
		return INFINITY;
	}

	public UDN getUDN() {
		return UDN;
	}

	public static UDN getRNG() {
		return RNG;
	}

	public static ArrayList<Integer> getNeigsIPNum() {
		return neigsIPNum;
	}

	public boolean neigBool(double uv, double uw, double vw) {// uv满足最“近”，返回true

		if (uw < uv && vw < uv) {
			return false;
		} else
			return true;
	}

	// 建UDN网

	public void buildUDN(long localNetworkAddrNum, long[] networkAddrNum,
			ArrayList<Integer> neigscount) {
		double ConLengthSum = 0;// 邻居间相对距离总和
		double VertCell = 0;// 纵坐标单位长度
		Iterator nt = neigscount.iterator();
		int minNeigCountNum;
		if(nt.hasNext()) {
			minNeigCountNum = (Integer) nt.next();
		} else {
			System.out.println("neigscount empty");
			return;
		}
		UDN = new UDN(networkAddrNum.length + 1, networkAddrNum.length + 1);

		// 计算纵坐标
		for (int i = 0; i < networkAddrNum.length; i++) {
			for (int j = i + 1; j < networkAddrNum.length; j++) {
				// System.out.println("相对距离总和:"+ConLengthSum);
				ConLengthSum = ConLengthSum
						+ Math.abs(networkAddrNum[i] - networkAddrNum[j]);
			}
		}
		VertCell = 2 * ConLengthSum
				/ (networkAddrNum.length * (networkAddrNum.length - 1) * 3);
		for (int i = 0; i < neigscount.size(); i++) {
			if (neigscount.get(i) < minNeigCountNum)
				minNeigCountNum = neigscount.get(i);
			// System.out.println("最小邻居数："+minNeigCountNum);
		}
		// 将矩阵第一行和第一列赋值
		UDN.creactUND(0, 0, 0);
		for (int k = 1; k <= networkAddrNum.length; k++) {
			UDN.creactUND(
					0,
					k,
					Math.sqrt(Math.abs(networkAddrNum[k - 1]
							- localNetworkAddrNum)
							* Math.abs(networkAddrNum[k - 1]
									- localNetworkAddrNum)
							+ (neigscount.get(k - 1) - minNeigCountNum)
							* VertCell
							* (neigscount.get(k - 1) - minNeigCountNum)
							* VertCell));
		}

		// 将邻接矩阵赋值
		for (int i = 1; i <= networkAddrNum.length; i++) {
			for (int j = i; j <= networkAddrNum.length; j++) {
				// if(i == 0 && j == 0)
				// UDN.creactUND(i, j, 0);
				// else
				UDN.creactUND(
						i,
						j,
						Math.sqrt(Math.abs(networkAddrNum[i - 1]
								- networkAddrNum[j - 1])
								* Math.abs(networkAddrNum[i - 1]
										- networkAddrNum[j - 1])
								+ Math.abs(neigscount.get(i - 1)
										- neigscount.get(j - 1))
								* VertCell
								* Math.abs(neigscount.get(i - 1)
										- neigscount.get(j - 1)) * VertCell));
			}

		}
	}

	// 计算自身邻居，并返回邻居对应ip串的下标
	public ArrayList<Integer> accuNeigsIPNum(long localNetworkAddrNum,
			long[] networkAddrNum, UDN UDN) {
		// UDN RNG = new UDN(neigNetworkNum.length+1,neigNetworkNum.length+1);
		// ArrayList <Integer> neigsIPNum = null;
		int count = 0;// 测试用
		for (int i = 1; i <= networkAddrNum.length; i++) {
			int flag = 0;
			for (int j = 1; j <= networkAddrNum.length; j++) {
				if (j != i
						&& !neigBool(UDN.arcs[0][i], UDN.arcs[0][j],
								UDN.arcs[i][j])) {
					flag = 1;
					break;
				}
			}
			if (flag == 0) {
				neigsIPNum.add(count, i - 1);
				count++;
			}
		}
		return neigsIPNum;
	}

	// 构建全局邻居图RNG
	public UDN globalRNG(long localNetworkAddrNum, long[] networkAddrNum,
			UDN UDN) {
		RNG = new UDN(networkAddrNum.length + 1, networkAddrNum.length + 1);
		System.arraycopy(UDN.arcs, 0, RNG.arcs, 0, UDN.arcs.length);
		// RNG.arcs = UDN.arcs;
		int[] count = new int[networkAddrNum.length + 1];
		for (int u = 0; u <= networkAddrNum.length; u++) {
			count[u] = networkAddrNum.length;
			for (int v = 0; v <= networkAddrNum.length; v++) {
				if (u != v)
					for (int w = 0; w <= networkAddrNum.length; w++)
						if (v != w
								&& u != w
								&& (!neigBool(UDN.arcs[u][v], UDN.arcs[u][w],
										UDN.arcs[v][w]))) {
							RNG.delete(u, v);
							count[u]--;
							break;
						}
			}
			// System.out.println("代表"+u+"选举结果：（邻居数）"+count[u]);
		}
		return RNG;
	}

}
