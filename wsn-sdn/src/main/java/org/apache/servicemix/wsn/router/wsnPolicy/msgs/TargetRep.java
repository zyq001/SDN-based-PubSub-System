package org.apache.servicemix.wsn.router.wsnPolicy.msgs;

/**
 * @author shoren
 * @date 2013-3-29
 */

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
//������Ϣ�и����Ĵ�����Ϣ����Ҫ�����洢�ͻ���Ϣ
public class TargetRep extends TargetMsg {

	private static final long serialVersionUID = 1L;
	protected List<TargetHost> targetClients;
	protected String repIp;
	protected boolean allMsg = false;  //�Ƿ�����ڲ����г�Ա�����ǰ��������б��Ϊ�ա�


	public TargetRep() {
		this(null);
	}

	public TargetRep(String repIp) {
		targetClients = new ArrayList<TargetHost>();
		this.repIp = repIp;
	}

	public TargetRep(String repIp, List<TargetHost> targetClients) {
		this.targetClients = new ArrayList<TargetHost>();
		this.repIp = repIp;
		for (int i = 0; i < targetClients.size(); i++) {
			this.targetClients.add(targetClients.get(i));
		}
	}

	public boolean isAllMsg() {
		return allMsg;
	}

	public void setAllMsg(boolean allMsg) {
		this.allMsg = allMsg;
	}

	public String getRepIp() {
		return repIp;
	}

	public void setRepIp(String repIp) {
		this.repIp = repIp;
	}

	public List<TargetHost> getTargetClients() {
		return targetClients;
	}

	public void setTargetClients(List<TargetHost> targetClients) {
		this.targetClients = targetClients;
	}

	public void deleteMsg(TargetRep msg) {
		if (!msg.getTargetClients().isEmpty()) {
			List<TargetHost> ths = msg.getTargetClients();
			for (int i = 0; i < ths.size(); i++) {
				TargetHost th = ths.get(i);
				if (targetClients.contains(th)) {
					int index = targetClients.indexOf(th);
					targetClients.remove(index);
				}

			}
		}
	}

	public void mergeMsg(TargetRep msg) {
		if (!this.equals(msg))
			return;
		//���msg��������������
		if (msg.isAllMsg()) {
			this.setAllMsg(true);
			targetClients.clear();
			return;
		}
		//�����ǰ��������������
		if (this.allMsg)
			return;

		if (!msg.getTargetClients().isEmpty()) {
			List<TargetHost> ths = msg.getTargetClients();
			for (int i = 0; i < ths.size(); i++) {
				TargetHost th = ths.get(i);
				if (!targetClients.contains(th))
					targetClients.add(th);
			}
		}
	}
}