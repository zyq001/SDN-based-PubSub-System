package org.apache.servicemix.wsn.router.wsnPolicy;

/**
 * @author shoren
 * @date 2013-3-5
 */

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetMsg;

/**
 *
 */
public class ShorenTreeNode extends DefaultMutableTreeNode{

	private static final long serialVersionUID = 1L;
	private TargetMsg msg;

	public ShorenTreeNode(TargetMsg msg, boolean allowsChildren)
	{
		super(msg, allowsChildren);
		this.msg = msg;
	}
	
	public ShorenTreeNode(TargetMsg msg)
	{
		this(msg, true);
	}
	
	
	public TargetMsg getMsg() {
		return msg;
	}

	public void setMsg(TargetMsg msg) {
		this.msg = msg;
	}
}
