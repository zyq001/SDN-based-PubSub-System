package org.Mina.shorenMinaTest.router;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class searchRoute {

	static ArrayList<MsgSubsForm> nodeList = new ArrayList<MsgSubsForm>();
	static MsgSubsForm groupTableRoot = null;
	static generateNode gN = new generateNode();

	static ConcurrentHashMap<String, MsgSubsForm> testMap = new ConcurrentHashMap<String, MsgSubsForm>();

	public static ArrayList<String> calForwardIP(String topic, String originator, ConcurrentHashMap<String, MsgSubsForm> testMap) {
		/*
		 * ������
		 * topic:���⡣
		 * originator:ת��Դ��
		 */


		ArrayList<String> forwardIP = new ArrayList<String>();
		//forwardIP:�����洢Ŀ��ת��ip��ַ

		//MsgSubsForm msf = groupTableRoot;
		//msf-->groupTableRoot�Ǹ���ģ���

		boolean thisGroup = false;

			/*
			 * String-->groupName:����Ⱥ������
			 * BrokerUnit-->rep������Ⱥ����ĵ�ַ
			 * ConcurrentHashMap-->neighbors���������ڼ�Ⱥ����������keyΪ�����ַ
			 */

		String[] splited = topic.split(":");

		MsgSubsForm msf = testMap.get(splited[0]);


		String temp = "";
		//temp�������뿪�������ٽ����һ��

		//System.out.println(groupTableRoot.topicChildList.size());

		if (msf.topicComponent.equals(splited[0]))
			for (int i = 1; i < splited.length; i++) {
				if (msf.topicChildList.containsKey(splited[i])) {
					//System.out.println(msf.topicComponent+":"+msf.routeNext);
					msf = msf.topicChildList.get(splited[i]);
				} else {
					//System.out.println(splited[i]);
					break;
				}
				if (thisGroup)
					forwardIP.add(msf.routeRoot);
				if (i > 0)
					temp += ":";
				temp += splited[i];
				/*if (brokerTable.containsKey(temp) || clientTable.contains(temp)) {
					
					 * ConcurrentHashMap-->brokerTable:����Ⱥ����������Ķ�����Ϣ��keyΪ���⣬valueΪ���Ĵ���ĵ�ַ
					 * ArrayList<String-->clientTable:���صĶ�����Ϣ,����broker��������ļ���
					 
					
					forwardIP.addAll(msf.routeNext);
					if (!thisGroup)
						break;
				}*/
			}

		forwardIP.addAll(msf.routeNext);
		if (forwardIP.contains(originator))
			forwardIP.remove(originator);
		return forwardIP;

	}
}
