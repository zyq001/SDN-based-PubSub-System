package org.apache.servicemix.wsn.router.mgr.calNeighbor;

import java.util.ArrayList;
import java.util.Iterator;

public class NeigSelect {

	private static long INFINITY = Long.MAX_VALUE;// ����������
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

	public static long getINFINITY() {
		return INFINITY;
	}

	public static UDN getRNG() {
		return RNG;
	}

	public static ArrayList<Integer> getNeigsIPNum() {
		return neigsIPNum;
	}

	public int setMaxVertexNum(int num) {
		MaxVertexNum = num;
		return MaxVertexNum;
	}

	public UDN getUDN() {
		return UDN;
	}

	public boolean neigBool(double uv, double uw, double vw) {// uv���������������true

		if (uw < uv && vw < uv) {
			return false;
		} else
			return true;
	}

	// ��UDN��

	public void buildUDN(long localNetworkAddrNum, long[] networkAddrNum,
	                     ArrayList<Integer> neigscount) {
		double ConLengthSum = 0;// �ھӼ���Ծ����ܺ�
		double VertCell = 0;// �����굥λ����
		Iterator nt = neigscount.iterator();
		int minNeigCountNum;
		if (nt.hasNext()) {
			minNeigCountNum = (Integer) nt.next();
		} else {
			System.out.println("neigscount empty");
			return;
		}
		UDN = new UDN(networkAddrNum.length + 1, networkAddrNum.length + 1);

		// ����������
		for (int i = 0; i < networkAddrNum.length; i++) {
			for (int j = i + 1; j < networkAddrNum.length; j++) {
				// System.out.println("��Ծ����ܺ�:"+ConLengthSum);
				ConLengthSum = ConLengthSum
						+ Math.abs(networkAddrNum[i] - networkAddrNum[j]);
			}
		}
		VertCell = 2 * ConLengthSum
				/ (networkAddrNum.length * (networkAddrNum.length - 1) * 3);
		for (int i = 0; i < neigscount.size(); i++) {
			if (neigscount.get(i) < minNeigCountNum)
				minNeigCountNum = neigscount.get(i);
			// System.out.println("��С�ھ�����"+minNeigCountNum);
		}
		// �������һ�к͵�һ�и�ֵ
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

		// ���ڽӾ���ֵ
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

	// ���������ھӣ��������ھӶ�Ӧip�����±�
	public ArrayList<Integer> accuNeigsIPNum(long localNetworkAddrNum,
	                                         long[] networkAddrNum, UDN UDN) {
		// UDN RNG = new UDN(neigNetworkNum.length+1,neigNetworkNum.length+1);
		// ArrayList <Integer> neigsIPNum = null;
		int count = 0;// ������
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

	// ����ȫ���ھ�ͼRNG
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
			// System.out.println("����"+u+"ѡ�ٽ�������ھ�����"+count[u]);
		}
		return RNG;
	}

}
