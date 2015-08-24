package org.apache.servicemix.wsn.router.wsnPolicy.msgs;

/**
 * @author shoren
 * @date 2013-1-16
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *策略信息类
 *策略信息保存在磁盘文件中，更新后就直接替换或修改文件。
 *所有信息放在一个xml文件中，因为策略信息不是很多哈。
 */
public class WsnPolicyMsg implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	//这个暂时用不上
//	private String targetMsgStyle;
	protected String targetTopic;
	protected List<TargetGroup> targetGroups;
	protected List<ComplexGroup> complexGroups;
	//这个是将所有的ComplexGroup里面和
	private Set<TargetGroup> allGroups;  
/*	public String getTargetMsgStyle() {
		return targetMsgStyle;
	}

	public void setTargetMsgStyle(String targetMsgStyle) {
		this.targetMsgStyle = targetMsgStyle;
	}*/
	
	public WsnPolicyMsg()
	{
		this(null, null, null);
	}
	
	public WsnPolicyMsg(String targetTopic)
	{
		this(targetTopic, null, null);
	}
	
	public WsnPolicyMsg(String targetTopic, List<ComplexGroup> complexGroups)
	{
		this(targetTopic, complexGroups, null);
	} 
	
	//生成一条完整策略信息
	public WsnPolicyMsg(String targetTopic, List<ComplexGroup> complexGroups, List<TargetGroup> targetGroups)
	{
		this.targetTopic = targetTopic;
		this.complexGroups = new ArrayList<ComplexGroup>();
		this.targetGroups = new ArrayList<TargetGroup>();
		this.allGroups = new HashSet<TargetGroup>();
		
		if(complexGroups != null)
		{			
			for(int i=0; i<complexGroups.size(); i++)
			{
				this.complexGroups.add(complexGroups.get(i));
			}
		}
		
		if(targetGroups != null)
		{			
			for(int i=0; i<targetGroups.size(); i++)
			{
				this.targetGroups.add(targetGroups.get(i));
			}
		}
	}
	
	public Set<TargetGroup> getAllGroups()
	{
		if(!complexGroups.isEmpty())
		{
			for(int i=0; i<complexGroups.size(); i++)
			{
				allGroups.addAll(complexGroups.get(i).getGroups());
			}
		}
		if(!targetGroups.isEmpty())
		{
			allGroups.addAll(targetGroups);
		}
		return allGroups;
	}
	

	
	//
	@SuppressWarnings("rawtypes")
	public void mergeMsg(WsnPolicyMsg msg)
	{
		if(!getTargetTopic().equals(msg.getTargetTopic()))
			return;
		List<ComplexGroup> cgs = msg.getComplexGroups();
		List<TargetGroup> tgs = msg.getTargetGroups();
		if(cgs.isEmpty() && tgs.isEmpty())
			return;
		
		if(cgs.isEmpty() && !tgs.isEmpty()
				 && (msg.getTargetGroups().size() == 1))
		{
			getAllGroups();	  //update allGroups.	
			TargetGroup ttg = tgs.get(0);
			//如果没有此集群，则加入，若有，则深入merge。
			if(!allGroups.contains(ttg))
				targetGroups.add(ttg);
			else{
				//深入merge
				Iterator it = allGroups.iterator();
				while(it.hasNext())
				{
					TargetGroup tg = (TargetGroup) it.next();
					if(tg.equals(ttg)){
						//merge group
						tg.mergeMsg(ttg);
						break;
					}
				}
			}
		}else{
			//add groups simply
			//如果包含新加入的groups，就把之前的删除，添加新的，因为新的默认是
			//包含内部所有成员。
			if(!cgs.isEmpty())
			{
				for(int i=0; i<cgs.size(); i++){
					ComplexGroup cg = cgs.get(i);
					if(complexGroups.contains(cg)){
						int index = complexGroups.indexOf(cg);
						complexGroups.remove(index);
					}
					cg.setAllMsg(true);
					complexGroups.add(cg);
				}
			}
			if(!tgs.isEmpty()){
				for(int i=0; i<tgs.size(); i++){
					TargetGroup tg = tgs.get(i);
					if(targetGroups.contains(tg)){
						int index = targetGroups.indexOf(tg);
						targetGroups.remove(index);
					}
					tg.setAllMsg(true);
					targetGroups.add(tg);
				}
			}
		}
	}
	
	protected int isContainGroup(TargetGroup group)
	{
		if(getTargetGroups().isEmpty())
			return -1;
		for(int i=0; i<targetGroups.size(); i++)
		{
			if(group.equals(targetGroups.get(i)))
				return i;
		}
		return -1;
	}
	
	public void deleteMsg(WsnPolicyMsg msg)
	{
		if(!getTargetTopic().equals(msg.getTargetTopic()))
			return;
		List<ComplexGroup> cgs = msg.getComplexGroups();
		List<TargetGroup> tgs = msg.getTargetGroups();
		if(cgs.isEmpty() && tgs.isEmpty())
			return;
		
		if(cgs.isEmpty() && !tgs.isEmpty()
				 && (tgs.size() == 1))
		{	
			TargetGroup ttg = tgs.get(0);
			if(ttg.isAllMsg()){
				int index = targetGroups.indexOf(ttg);
				targetGroups.remove(index);
			}
				
			getAllGroups();	  //update allGroups.	
			
			//如果包含此集群，则delete
			if(allGroups.contains(ttg))
			{
				//深入
				Iterator it = allGroups.iterator();
				while(it.hasNext())
				{
					TargetGroup tg = (TargetGroup) it.next();
					if(tg.equals(ttg)){
						//merge group
						tg.deleteMsg(ttg);
						break;
					}
				}
			}
		}else{
			//delete groups simply
			if(!cgs.isEmpty())
			{
				for(int i=0; i<cgs.size(); i++){
					ComplexGroup cg = cgs.get(i);
					if(complexGroups.contains(cg)){
						int index = complexGroups.indexOf(cg);
						complexGroups.remove(index);
					}
				}
			}
			if(!tgs.isEmpty()){
				for(int i=0; i<tgs.size(); i++){
					TargetGroup tg = tgs.get(i);
					if(targetGroups.contains(tg)){
						int index = targetGroups.indexOf(tg);
						targetGroups.remove(index);
					}
				}
			}
		}
	}
	
	public List<ComplexGroup> getComplexGroups() {
		return complexGroups;
	}

	public void setComplexGroups(List<ComplexGroup> complexGroups) {
		this.complexGroups = complexGroups;
	}

	public String getTargetTopic() {
		return targetTopic;
	}

	public void setTargetTopic(String targetTopic) {
		this.targetTopic = targetTopic;
	}

	public List<TargetGroup> getTargetGroups() {
		return targetGroups;
	}

	public void setTargetGroups(List<TargetGroup> targetGroups) {
		this.targetGroups = targetGroups;
	}
}









