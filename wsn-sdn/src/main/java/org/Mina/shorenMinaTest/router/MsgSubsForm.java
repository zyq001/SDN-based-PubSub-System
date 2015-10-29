package org.Mina.shorenMinaTest.router;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MsgSubsForm {
	public String topicComponent;//��������

	public ConcurrentHashMap<String, MsgSubsForm> topicChildList = new ConcurrentHashMap<String, MsgSubsForm>(); //��һ����Ϣ��keyΪ��Ϣ����

	public ArrayList<String> subs = new ArrayList<String>(); //���Ĵ���Ϣ�ļ�Ⱥ,valueΪ�䶩��ʱ��

	public ArrayList<String> routeNext = new ArrayList<String>(); //��Ը����Ƶ�ת����һ���ڵ�

	public String routeRoot; //������ת�����ĸ��ڵ�

	public MsgSubsForm() {
		topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		subs = new ArrayList<String>();
		routeNext = new ArrayList<String>();
	}

	public MsgSubsForm(String nodeName, ConcurrentHashMap<String, MsgSubsForm> topicChildList, ArrayList<String> subs, ArrayList<String> routeNext) {

		this.topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		this.subs = new ArrayList<String>();
		this.routeNext = new ArrayList<String>();

		this.topicComponent = nodeName;
		this.topicChildList = topicChildList;
		this.subs = subs;
		this.routeNext = routeNext;

	}


}
