package org.apache.servicemix.wsn.router.detection;

public interface IDt{

	//添加邻居检测的目标
	public void addTarget(String indicator);
	
	// remove the detection of a neighbor
	public void removeTarget(String indicator);

	//设置失效的阀值
	public void setThreshold(long value);
	
	public void setSendPeriod(long value);
	
	public void onMsg(Object msg);
}
