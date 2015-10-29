package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

public class MsgTreeChange implements Serializable {

	/**
	 * ����Ա֪ͨ�������ı�
	 */
	private static final long serialVersionUID = 1L;

	public String topicName;

	public int change; // 0 for change, 1 for delete
}