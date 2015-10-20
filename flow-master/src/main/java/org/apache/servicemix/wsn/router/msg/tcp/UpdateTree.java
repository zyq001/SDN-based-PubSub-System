package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

public class UpdateTree implements Serializable {
	/**
	 * 管理员通知更新主题树
	 */
	private static final long serialVersionUID = 1L;
	public String newName;
	public String oldName;
	public int change; // 0 for change, 1 for delete
	long updateTime;
	
	public UpdateTree(long updateTime) {
		this.updateTime = updateTime;
	}
}