package org.apache.servicemix.wsn.router.admin.detection;
import org.apache.servicemix.wsn.router.admin.MsgLsnr;
public interface IDt extends MsgLsnr {

	//添加心跳检测的目标
	public void addTarget(String indicator);
	
	//删除心跳检测的目标
	public void removeTarget(String indicator);
	
	//设置失效的阀值
	public void setThreshold(long value);
	
	//设置心跳消息的发送频率并重启该计时器任务
	public void setSendPeriod(long value);
	
	//设置扫描心跳信息表的频率并重启该计时器任务
	public void setScanPeriod(long value);
	
	//设置发送同步消息的频率并重启该计时器任务
	public void setSynPeriod(long value);


}
