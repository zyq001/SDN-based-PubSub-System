package org.apache.servicemix.application;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

//K-means�㷨ʵ��

public class KMeans {
	// �������Ŀ
	final static int ClassCount = 2;
	// ����������Ŀ�����ԣ�
	final static int FieldCount = 3;
	// �����쳣����ֵ������ÿһ���ʼ����С��ĿΪInstanceNumber/ClassCount^t��
	final static double t = -1.0;
	// ������Ŀ�����Լ���
	static int InstanceNumber = 0;
	// ������ݵľ���
	private float[][] data;

	// ÿ����ľ�ֵ����
	private float[][] classData;

	// ������������
	private ArrayList<Integer> noises;

	// ���ÿ�α任����ľ���
	private ArrayList<ArrayList<Integer>> result;

	private int state;

	private boolean isZero = true;

	// ���캯������ʼ��
	public KMeans(int INumber) {
		InstanceNumber = INumber;
		// ���һλ����������
		data = new float[InstanceNumber][FieldCount + 1];
		classData = new float[ClassCount][FieldCount];
		result = new ArrayList<ArrayList<Integer>>(ClassCount);
		noises = new ArrayList<Integer>();

	}

	/**
	 * ��������� ���Լ����ļ�����Ϊ�����Լ�.data��,������1000*57��С������ ÿһ��Ϊһ����������57������ ��Ҫ��Ϊ�������� 1.��ȡ����
	 * 2.���о��� ���ͳ������ʱ������ĵ��ڴ�
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		KMeans cluster = new KMeans(5);
		// ��ȡ����
		cluster.readData("D:/test.txt");
		// �������
		cluster.cluster();
		// ������
		cluster.printResult("D:/clusterResult.data");
		long endTime = System.currentTimeMillis();
		System.out.println("Total Time:" + (endTime - startTime) / 1000 + "s");
		System.out.println("Memory Consuming:"
				+ (float) (Runtime.getRuntime().totalMemory() - Runtime
				.getRuntime().freeMemory()) / 1000000 + "MB");
	}

	/*
	 * ��ȡ���Լ�������
	 * 
	 * @param dataMap ����
	 */
	public void readData(HashMap<String, ArrayList<Double>> dataMap) {
		int line = 0;
		for (String k : dataMap.keySet()) {
			for (int i = 0; i < dataMap.get(k).size(); i++) {
				if (dataMap.get(k).get(i).toString().startsWith("Iris-setosa")) {
					data[line][i] = (float) 1.0;
				} else if (dataMap.get(k).get(i).toString()
						.startsWith("Iris-versicolor")) {
					data[line][i] = (float) 2.0;
				} else if (dataMap.get(k).get(i).toString()
						.startsWith("Iris-virginica")) {
					data[line][i] = (float) 3.0;
				} else { // �����ݽ�ȡ֮��Ž�����
					data[line][i] = Float.parseFloat(dataMap.get(k).get(i)
							.toString());
				}
			}
			line++;
		}
	}

	/*
	 * ��ȡ���Լ�������
	 * 
	 * @param dataMap ����
	 */
	public void readData(HashMap<String, ArrayList<Double>> dataMap, ArrayList<Double> newzuobiao, String newsubscribeaddree) {
//		for (int i = 0; i < newzuobiao.size(); i++) {
//			if (newzuobiao.get(i).toString().startsWith("Iris-setosa")) {
//				data[0][i] = (float) 1.0;
//			} else if (newzuobiao.get(i).toString()
//					.startsWith("Iris-versicolor")) {
//				data[0][i] = (float) 2.0;
//			} else if (newzuobiao.get(i).toString()
//					.startsWith("Iris-virginica")) {
//				data[0][i] = (float) 3.0;
//			} else { // �����ݽ�ȡ֮��Ž�����
//				data[0][i] = Float.parseFloat(newzuobiao.get(i)
//						.toString());
//			}
//		}
		int line = 0;
		for (String k : dataMap.keySet()) {
			if (newsubscribeaddree.equals(k)) {
				for (int i = 0; i < newzuobiao.size(); i++) {
					data[line][i] = Float.parseFloat(newzuobiao.get(i)
							.toString());
					state = line;
				}
			} else {

				for (int i = 0; i < dataMap.get(k).size(); i++) {
					//
					//				if (dataMap.get(k).get(i).toString().startsWith("Iris-setosa")) {
					//					data[line][i] = (float) 1.0;
					//				} else if (dataMap.get(k).get(i).toString()
					//						.startsWith("Iris-versicolor")) {
					//					data[line][i] = (float) 2.0;
					//				} else if (dataMap.get(k).get(i).toString()
					//						.startsWith("Iris-virginica")) {
					//					data[line][i] = (float) 3.0;
					//				} else { // �����ݽ�ȡ֮��Ž�����

					data[line][i] = Float.parseFloat(dataMap.get(k).get(i)
							.toString());
				}
			}
			line++;
		}
	}

	/*
	 * ��ȡ���Լ�������
	 * 
	 * @param trainingFileName ���Լ��ļ���
	 */
	public void readData(String trainingFileName) {
		try {
			FileReader fr = new FileReader(trainingFileName);
			BufferedReader br = new BufferedReader(fr);
			// ������ݵ���ʱ����
			String lineData = null;
			String[] splitData = null;
			int line = 0;
			// ���ж�ȡ
			while (br.ready()) {
				// �õ�ԭʼ���ַ���
				lineData = br.readLine();
				splitData = lineData.split(",");
				// ת��Ϊ����
				// System.out.println("length:"+splitData.length);
				if (splitData.length > 1) {
					for (int i = 0; i < splitData.length; i++) {
						// System.out.println(splitData[i]);
						// System.out.println(splitData[i].getClass());
						if (splitData[i].startsWith("Iris-setosa")) {
							data[line][i] = (float) 1.0;
						} else if (splitData[i].startsWith("Iris-versicolor")) {
							data[line][i] = (float) 2.0;
						} else if (splitData[i].startsWith("Iris-virginica")) {
							data[line][i] = (float) 3.0;
						} else { // �����ݽ�ȡ֮��Ž�����
							data[line][i] = Float.parseFloat(splitData[i]);
						}
					}
					line++;
				}
			}
			System.out.println(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ������̣���Ҫ��Ϊ���� 1.ѭ���ҳ�ʼ�� 2.���ϵ���ֱ�����಻�ٷ����仯
	 */
	public void cluster() {
		// ���ݹ�һ��
		normalize();
		// ����Ƿ���Ҫ�����ҳ�ʼ��
		boolean needUpdataInitials = true;

		// �ҳ�ʼ��ĵ�������
		int times = 1;
		// �ҳ�ʼ��
		while (needUpdataInitials) {
			needUpdataInitials = false;
			result.clear();
			System.out.println("Find Initials Iteration" + (times++)
					+ "time(s)");

			// һ���ҳ�ʼ��ĳ��Ժ͸��ݳ�ʼ��ķ���
			findInitials();
			firstClassify();

			// ���ĳ���������ĿС���ض�����ֵ������Ϊ��������е�������������������
			// ��Ҫ�����ҳ�ʼ��
			// for(int i = 0;i < result.size();i++)
			// {
			// if(result.get(i).size() < InstanceNumber/Math.pow(ClassCount,t))
			// {
			// needUpdataInitials = true;
			// noises.addAll(result.get(i));
			// }
			// }
		}

		// �ҵ����ʵĳ�ʼ���
		// ���ϵĵ�����ֵ���ĺͷ��ֱ࣬�����ٷ����κα仯
		Adjust();
	}

	/*
	 * �����ݽ��й�һ�� 1.��ÿһ�����Ե����ֵ 2.��ĳ��������ÿ�����Գ��������ֵ
	 */
	public void normalize() {
		// �����ֵ
		float[] max = new float[FieldCount];

		for (int i = 0; i < InstanceNumber; i++) {
			for (int j = 0; j < FieldCount; j++) {
				if (data[i][j] > max[j])
					max[j] = data[i][j];
			}
		}

		// ��һ��
		for (int i = 0; i < InstanceNumber; i++) {
			for (int j = 0; j < FieldCount; j++) {
				data[i][j] = data[i][j] / max[j];
			}
		}
	}

	// ���ڳ�ʼ������һ����Ѱ����
	public void findInitials() {
		// a,bΪ��־������Զ����������������
		int i, j, a, b;
		i = j = a = b = 0;

		// ��Զ����
		float maxDis = 0;

		// �Ѿ��ҵ��ĳ�ʼ�����
		int alreadyCls = 2;

		// ����Ѿ����Ϊ��ʼ�����������
		ArrayList<Integer> initials = new ArrayList<Integer>();

		// ��������ʼ
		for (; i < InstanceNumber; i++) {
			// ������
			if (noises.contains(i))
				continue;
			// long startTime = System.currentTimeMillis();
			j = i + 1;
			for (; j < InstanceNumber; j++) {
				// ������
				if (noises.contains(j))
					continue;
				// �ҳ����ľ��벢��¼����
				float newDis = calDis(data[i], data[j]);
				if (maxDis < newDis) {
					a = i;
					b = j;
					maxDis = newDis;
				}
			}
			// long endTime = System.currentTimeMillis();
			// System.out.println(i +
			// "Vector Caculation Time:"+(endTime-startTime)+"ms");
		}

		// ��ǰ������ʼ���¼����
		initials.add(a);
		initials.add(b);
		classData[0] = data[a];
		classData[1] = data[b];

		// �ڽ�����½����ĳ���������Ķ��󣬲��ѳ�ʼ����ӽ�ȥ
		ArrayList<Integer> resultOne = new ArrayList<Integer>();
		ArrayList<Integer> resultTwo = new ArrayList<Integer>();
		resultOne.add(a);
		resultTwo.add(b);
		result.add(resultOne);
		result.add(resultTwo);

		// �ҵ�ʣ��ļ�����ʼ��
		while (alreadyCls < ClassCount) {
			i = j = 0;
			float maxMin = 0;
			int newClass = -1;

			// ����Сֵ�е����ֵ
			for (; i < InstanceNumber; i++) {
				float min = 0;
				float newMin = 0;
				// �Һ����������Сֵ
				if (initials.contains(i))
					continue;
				// ������ȥ��
				if (noises.contains(i))
					continue;
				for (j = 0; j < alreadyCls; j++) {
					newMin = calDis(data[i], classData[j]);
					if (min == 0 || newMin < min)
						min = newMin;
				}

				// ����С����ϴ�
				if (min > maxMin) {
					maxMin = min;
					newClass = i;
				}
			}
			// ��ӵ���ֵ���Ϻͽ��������
			// System.out.println("NewClass"+newClass);
			initials.add(newClass);
			classData[alreadyCls++] = data[newClass];
			ArrayList<Integer> rslt = new ArrayList<Integer>();
			rslt.add(newClass);
			result.add(rslt);
		}
	}

	// ��һ�η���
	public void firstClassify() {
		// ���ݳ�ʼ��������
		for (int i = 0; i < InstanceNumber; i++) {
			float min = 0f;
			int clsId = -1;
			for (int j = 0; j < classData.length; j++) {
				// ŷʽ����
				float newMin = calDis(classData[j], data[i]);
				if (clsId == -1 || newMin < min) {
					clsId = j;
					min = newMin;
				}

			}
			// ���������
			if (!result.get(clsId).contains(i))
				result.get(clsId).add(i);
		}
	}

	// �������ֱ࣬������������ݲ��ٱ仯
	public void Adjust() {
		// ��¼�Ƿ����仯
		boolean change = true;

		// ѭ���Ĵ���
		int times = 1;
		while (change) {
			// ��λ
			change = false;
			System.out.println("Adjust Iteration" + (times++) + "time(s)");

			// ���¼���ÿ����ľ�ֵ
			for (int i = 0; i < ClassCount; i++) {
				// ԭ�е�����
				ArrayList<Integer> cls = result.get(i);

				// �µľ�ֵ
				float[] newMean = new float[FieldCount];

				// �����ֵ
				for (Integer index : cls) {
					for (int j = 0; j < FieldCount; j++)
						newMean[j] += data[index][j];
				}
				for (int j = 0; j < FieldCount; j++)
					newMean[j] /= cls.size();
				if (!compareMean(newMean, classData[i])) {
					classData[i] = newMean;
					change = true;
				}
			}
			// ���֮ǰ������
			for (ArrayList<Integer> cls : result)
				cls.clear();

			// ���·���
			for (int i = 0; i < InstanceNumber; i++) {
				float min = 0f;
				int clsId = -1;
				for (int j = 0; j < classData.length; j++) {
					float newMin = calDis(classData[j], data[i]);
					if (clsId == -1 || newMin < min) {
						clsId = j;
						min = newMin;
					}
				}
				data[i][FieldCount] = clsId;
				result.get(clsId).add(i);
			}

			// ���Ծ���Ч��(ѵ����)
			for (int i = 0; i < ClassCount; i++) {
				int negatives = 0;
				int positives = 0;
				ArrayList<Integer> cls = result.get(i);
				for (Integer instance : cls)
					if (data[instance][FieldCount - 1] == 1f)
						negatives++;
					else
						positives++;
//				System.out.println(" " + i + " Negatives: " + negatives
//						+ " Positive: " + positives);
			}
			System.out.println();
		}

	}

	/**
	 * ����a������b������ŷʽ������Ϊ�����ƶ�
	 *
	 * @param a ����a
	 * @param b ����b
	 * @return ŷʽ���볤��
	 */
	private float calDis(float[] aVector, float[] bVector) {
		double dis = 0;
		int i = 0;
		/* ���һ��������ѵ������Ϊ��������Բ����� */
		for (; i < aVector.length; i++)
			dis += Math.pow(bVector[i] - aVector[i], 2);
		dis = Math.pow(dis, 0.5);
		return (float) dis;
	}

	/**
	 * �ж�������ֵ�����Ƿ����
	 *
	 * @param a ����a
	 * @param b ����b
	 * @return
	 */
	private boolean compareMean(float[] a, float[] b) {
		if (a.length != b.length)
			return false;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > 0 && b[i] > 0 && a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ����������һ���ļ���
	 *
	 * @param fileName
	 */
	public void printResult(String fileName) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			// д���ļ�
			for (int i = 0; i < InstanceNumber; i++) {
				bw.write(String.valueOf(data[i][FieldCount]).substring(0, 1));
				bw.newLine();
			}
			int good = 0;
			int bad = 0;
			// ͳ��ÿ�����Ŀ����ӡ������̨
			for (int i = 0; i < ClassCount; i++) {
				if (i == 0)
					good = result.get(i).size();
				else
					bad = result.get(i).size();
			}
			if (good < bad) {
				int temp = bad;
				bad = good;
				good = temp;
				isZero = false;
			}
			System.out.println("��ͨ�û���Ŀ��" + good);
			System.out.println("�����û���Ŀ��" + bad);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			// �ر���Դ
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}


	public boolean getResult() {
		printResult("E:/abc.txt");
		if ("1".equals(String.valueOf(data[state][FieldCount]).substring(0, 1)) && isZero) {
			System.out.println("�����û�����Ҫ����");
			return true;
		} else if ("0".equals(String.valueOf(data[state][FieldCount]).substring(0, 1)) && !isZero) {
			System.out.println("�����û�����Ҫ����");
			return true;
		}
		System.out.println("�����û�������Ҫ����");
		return false;
	}


}