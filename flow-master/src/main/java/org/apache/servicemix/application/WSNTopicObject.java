package org.apache.servicemix.application;

import com.bupt.wangfu.ldap.TopicEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WSNTopicObject implements Serializable {

	private static final long serialVersionUID = 1L;

	//????????????
	private TopicEntry topicentry;
	//??????????????????
	private WSNTopicObject parent;
	//??????????????????
	private List<WSNTopicObject> childrens;
	//????????????????????????
	private List<String> subscribeAddress;

	//??????
	public WSNTopicObject() {
	}

	public WSNTopicObject(TopicEntry _topicentry, WSNTopicObject _parent) {
		this.topicentry = _topicentry;
		this.parent = _parent;
		this.childrens = new ArrayList<WSNTopicObject>();
		this.subscribeAddress = new ArrayList<String>();
	}

	public TopicEntry getTopicentry() {
		return topicentry;
	}

	//set??????get????
	public void setTopicentry(TopicEntry topicentry) {
		this.topicentry = topicentry;
	}

	public WSNTopicObject getParent() {
		return parent;
	}

	public void setParent(WSNTopicObject parent) {
		this.parent = parent;
	}

	public List<WSNTopicObject> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<WSNTopicObject> childrens) {
		this.childrens = childrens;
	}

	public List<String> getSubscribeAddress() {
		return subscribeAddress;
	}

	public void setSubscribeAddress(List<String> subscribeAddress) {
		this.subscribeAddress = subscribeAddress;
	}


}
