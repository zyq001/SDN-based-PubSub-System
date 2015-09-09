package org.apache.servicemix.wsn.router.wsnPolicy.msgs;

/**
 * @author shoren
 * @date 2013-3-29
 */

public //显示集群信息时，按顺序排列，所以实现Comparable接口
class TargetMsg implements java.io.Serializable
{	
	private static final long serialVersionUID = 1L;
	
	protected String name;

	public TargetMsg(String name)
	{
		this.name = name;
	}
	
	public TargetMsg()
	{
		this(null);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString()
	{
		return getName();
	}
 
	
	public boolean equals(Object anObject)
	{
		if(!(anObject instanceof TargetMsg))
			return false;
		TargetMsg msg = (TargetMsg)anObject;
		if(this.getName().equals(msg.getName()))
			return true;
		return false;
	}
	
	public void mergeMsg(TargetMsg msg)
	{
		//ignore
	}
	
	public void deleteMsg(TargetMsg msg)
	{
		//ignore
	}
	
	public static void main(String[] args) {
		TargetMsg t1 = new TargetMsg("a");
		TargetMsg t2 = new TargetMsg("a");

		System.out.println(t1.equals(t2));
	}
}
