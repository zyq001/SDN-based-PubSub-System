package org.apache.servicemix.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bupt.wangfu.ldap.TopicEntry;

public class WSNTopicObject implements Serializable {
	
	private static final long serialVersionUID = 1L;

	
	//存储当前节点
	private TopicEntry topicentry;
	//存储当前节点的父亲
	private WSNTopicObject parent;
	//存储当前节点的孩子
	private List<WSNTopicObject> childrens;
	//存储订阅该主题的地址列表
	private List<String> subscribeAddress;
	
	//构造器
	public WSNTopicObject(){}
	public WSNTopicObject(TopicEntry _topicentry, WSNTopicObject _parent){
		this.topicentry = _topicentry;
		this.parent = _parent;
		this.childrens = new ArrayList<WSNTopicObject>();
		this.subscribeAddress = new ArrayList<String>();
	}
	
	//set方法和get方法
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
