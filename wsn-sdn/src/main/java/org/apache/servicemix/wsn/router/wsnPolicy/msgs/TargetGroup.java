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
//策略信息中附带的代表信息，主要用来存储代理信息
public class TargetGroup extends TargetMsg {
	private static final long serialVersionUID = 1L;

	protected List<TargetRep> targetList;
	protected boolean allMsg = false;


	public TargetGroup() {
		this(null, null);
	}

	public TargetGroup(String groupName) {
		this(groupName, null);
	}

	public TargetGroup(String groupName, List<TargetRep> targetList) {
		this.name = groupName;
		this.targetList = new ArrayList<TargetRep>();

		if (targetList != null) {
			for (int i = 0; i < targetList.size(); i++) {
				this.targetList.add(targetList.get(i));
			}
		}
	}

	public static void main(String[] args) {
		TargetGroup t1 = new TargetGroup("ab");
		TargetGroup t2 = new TargetGroup("ab");

		System.out.println(t1.equals(t2));
	}

	public boolean isAllMsg() {
		return allMsg;
	}

	public void setAllMsg(boolean allMsg) {
		this.allMsg = allMsg;
	}

	public List<TargetRep> getTargetList() {
		return targetList;
	}

	public void setTargetList(List<TargetRep> targetList) {
		this.targetList = targetList;
	}

	public void mergeMsg(TargetGroup tg) {
		if (!this.equals(tg))
			return;
		//如果tg所有信息均受限
		if (tg.isAllMsg()) {
			this.setAllMsg(true);
			targetList.clear();
			return;
		}
		//如果当前集群所有主机均受限
		if (this.allMsg)
			return;
		List<TargetRep> trs = tg.getTargetList();
		if (trs.isEmpty()) {
			return;
		}

		if (trs.size() == 1) {
			TargetRep tr = trs.get(0);
			if (!targetList.contains(tr))
				targetList.add(tr);
			else {
				int index = targetList.indexOf(tr);
				TargetRep ttr = targetList.get(index);
				ttr.mergeMsg(tr);
			}
		} else {
			//若含有tg下的reps，则删除原来的，添加当前的
			for (int i = 0; i < trs.size(); i++) {
				TargetRep tr = trs.get(i);
				if (targetList.contains(tr)) {
					int index = targetList.indexOf(tr);
					targetList.remove(index);
				}
				tr.setAllMsg(true);
				targetList.add(tr);
			}
		}
	}

	public void deleteMsg(TargetGroup tg) {
		if (!this.equals(tg))
			return;
		List<TargetRep> trs = tg.getTargetList();
		if (trs.isEmpty())
			return;
		if (trs.size() == 1) {
			TargetRep tr = trs.get(0);
			if (targetList.contains(tr)) {
				int index = targetList.indexOf(tr);
				TargetRep ttr = targetList.get(index);
				ttr.deleteMsg(tr);
			}
		} else {
			//若含有tg下的reps，则删除
			for (int i = 0; i < trs.size(); i++) {
				TargetRep tr = trs.get(i);
				if (targetList.contains(tr)) {
					int index = targetList.indexOf(tr);
					targetList.remove(index);
				}
			}
		}
	}
}
