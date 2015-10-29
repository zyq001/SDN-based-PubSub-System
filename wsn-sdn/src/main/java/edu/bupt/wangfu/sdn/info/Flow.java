package edu.bupt.wangfu.sdn.info;

import org.json.JSONObject;

public class Flow {
	private String dpid;
	private String flowCount;

	private JSONObject content;

	public Flow(String dpid) {
		this.dpid = dpid;
	}

	public JSONObject getContent() {
		return content;
	}

	public void setContent(JSONObject content) {
		this.content = content;
	}


	public String getDpid() {
		return dpid;
	}

	public void setDpid(String dpid) {
		this.dpid = dpid;
	}

	public String getFlowCount() {
		return flowCount;
	}

	public void setFlowCount(String flowCount) {
		this.flowCount = flowCount;
	}


}
