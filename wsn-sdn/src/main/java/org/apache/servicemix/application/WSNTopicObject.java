package org.apache.servicemix.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.bupt.wangfu.ldap.TopicEntry;

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
	public WSNTopicObject(){}
	public WSNTopicObject(TopicEntry _topicentry, WSNTopicObject _parent){
		this.topicentry = _topicentry;
		this.parent = _parent;
		this.childrens = new ArrayList<WSNTopicObject>();
		this.subscribeAddress = new ArrayList<String>();
	}
	
	//set??????get????
	public void setTopicentry(TopicEntry topicentry) {
		this.topicentry = topicentry;
	}
	public TopicEntry getTopicentry() {
		return topicentry;
	}
	public void setParent(WSNTopicObject parent) {
		this.parent = parent;
	}
	public WSNTopicObject getParent() {
		return parent;
	}
	public void setChildrens(List<WSNTopicObject> childrens) {
		this.childrens = childrens;
	}
	public List<WSNTopicObject> getChildrens() {
		return childrens;
	}
	public void setSubscribeAddress(List<String> subscribeAddress) {
		this.subscribeAddress = subscribeAddress;
	}
	public List<String> getSubscribeAddress() {
		return subscribeAddress;
	}
	
	public String toString(){
		return topicentry.toString();
	}
}
