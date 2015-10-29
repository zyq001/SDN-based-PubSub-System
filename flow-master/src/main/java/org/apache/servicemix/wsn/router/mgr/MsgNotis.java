package org.apache.servicemix.wsn.router.mgr;

import java.io.Serializable;
import java.util.Date;

public class MsgNotis implements Serializable {

	private static final long serialVersionUID = 1L;

	public String sender;// 转发者的信息

	public String originatorGroup;// 提供通知的broker所在集群名字

	public String originatorAddr;// 提供通知的broker的IP地址

	public String topicName;// 通知主题

	public String doc;// 通知内容

	public Date sendDate;// 消息产生的时间

}
