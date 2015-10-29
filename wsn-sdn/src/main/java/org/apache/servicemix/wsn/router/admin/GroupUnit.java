package org.apache.servicemix.wsn.router.admin;

import java.io.Serializable;
import java.util.Date;

public class GroupUnit implements Serializable {

	//����group�Ļ�����Ϣ

	public String addr;//group�ĵ�ַ

	public String name;//group������

	public int port;//group��TCP�˿ں�

	public Date date;//����ʱ��

	public GroupUnit(String addr, int port, String name) {
		this.addr = addr;
		this.name = name;
		this.port = port;
		date = new Date();
	}

}
