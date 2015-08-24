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
//策略信息中附带的代理信息，主要用来存储客户信息
public class TargetRep extends TargetMsg
{	

	private static final long serialVersionUID = 1L;
	protected List<TargetHost> targetClients;
	protected String repIp;
	protected boolean allMsg = false;  //是否包含内部所有成员，若是包括，其列表可为空。


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

	public TargetRep()
	{
		this(null);
	}
	
	public TargetRep(String repIp)
	{
		targetClients = new ArrayList<TargetHost>();
		this.repIp = repIp;
	}
	
	public TargetRep(String repIp, List<TargetHost> targetClients)
	{
		this.targetClients = new ArrayList<TargetHost>();
		this.repIp = repIp;
		for(int i=0; i<targetClients.size(); i++)
		{
			this.targetClients.add(targetClients.get(i));
		}
	}
	
	public void deleteMsg(TargetRep msg)
	{
		if(!msg.getTargetClients().isEmpty())
		{
			List<TargetHost> ths = msg.getTargetClients();
			for(int i=0; i<ths.size(); i++){
				TargetHost th = ths.get(i);
				if(targetClients.contains(th)){
					int index = targetClients.indexOf(th);
					targetClients.remove(index);
				}
					
			}
		}
	}
	
	public void mergeMsg(TargetRep msg)
	{
		if(!this.equals(msg))
			return;
		//如果msg所有主机均受限
		if(msg.isAllMsg()){
			this.setAllMsg(true);
			targetClients.clear();
			return;
		}
		//如果当前所有主机均受限
		if(this.allMsg)
			return;
		
		if(!msg.getTargetClients().isEmpty())
		{
			List<TargetHost> ths = msg.getTargetClients();
			for(int i=0; i<ths.size(); i++){
				TargetHost th = ths.get(i);
				if(!targetClients.contains(th))
					targetClients.add(th);
			}
		}
	}
}