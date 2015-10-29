package org.apache.servicemix.wsn.router.detection;

public interface IDt {

	//����ھӼ���Ŀ��
	public void addTarget(String indicator);

	// remove the detection of a neighbor
	public void removeTarget(String indicator);

	//����ʧЧ�ķ�ֵ
	public void setThreshold(long value);

	public void setSendPeriod(long value);

	public void onMsg(Object msg);
}
