package org.apache.servicemix.wsn.router.wsnPolicy.msgs;

/**
 * @author shoren
 * @date 2013-3-29
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class ComplexGroup extends TargetMsg
{	
	private static final long serialVersionUID = 1L;
	protected List<ComplexGroup> complexGroups;
	protected List<TargetGroup> targetGroups;
	protected boolean allMsg = false;  //是否包含内部所有成员，若是包括，其列表可为空。


	public boolean isAllMsg() {
		return allMsg;
	}

	public void setAllMsg(boolean allMsg) {
		this.allMsg = allMsg;
	}

	public ComplexGroup()
	{
		this(null,null,null);
	}
	
	public ComplexGroup(String complexName)
	{
		this(complexName,null,null);
	}
	
	public ComplexGroup(String complexName, List<ComplexGroup> complexGroups)
	{
		this(complexName,complexGroups,null);
	}	
	
	public ComplexGroup(String complexName, List<ComplexGroup> complexGroups, List<TargetGroup> targetGroups)
	{
		this.name = complexName;
		this.complexGroups = new ArrayList<ComplexGroup>();
		this.targetGroups = new ArrayList<TargetGroup>();
		
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

	public List<ComplexGroup> getComplexGroups() {
		return complexGroups;
	}
	
	public void setComplexGroups(List<ComplexGroup> complexGroups) {
		this.complexGroups = complexGroups;
	}
	
	public List<TargetGroup> getTargetGroups() {
		return targetGroups;
	}

	public void setTargetGroups(List<TargetGroup> targetGroups) {
		this.targetGroups = targetGroups;
	}
	
	protected Set<TargetGroup> getGroups()
	{
		Set<TargetGroup> tgs = new HashSet<TargetGroup>();
		if(!complexGroups.isEmpty())
		{
			for(int i=0; i<complexGroups.size();i++)
			{
				tgs.addAll(complexGroups.get(i).getGroups());
			}
		}
		if(!targetGroups.isEmpty())
		{
			tgs.addAll(targetGroups);
		}
		return tgs;
	}
}
