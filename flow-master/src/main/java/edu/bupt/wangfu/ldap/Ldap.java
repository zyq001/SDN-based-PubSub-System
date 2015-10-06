package edu.bupt.wangfu.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.swing.JOptionPane;

import org.apache.servicemix.wsn.router.wsnPolicy.msgs.ComplexGroup;

public class Ldap {
	//store the information of connection 
	private Hashtable<String, String> env = null;
	//OpenLdap context
	private LdapContext ctx = null;
	private String[] attrIDs = {"description"};
	
	public Ldap(){
		env = new Hashtable<String, String>();
	}
	//build the connection to OpenLdap server
	public void connectLdap(String ip, String user, String password) throws NamingException{
		//set the initializing information of the context
		env.put(Context.INITIAL_CONTEXT_FACTORY,  "com.sun.jndi.ldap.LdapCtxFactory");
		//set the URL of OpenLdap server
		env.put(Context.PROVIDER_URL, "ldap://" + ip + ":389");
		//set the authentication mode
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		//set user of OpenLdap server
		env.put(Context.SECURITY_PRINCIPAL, user);
		//set password of user
		env.put(Context.SECURITY_CREDENTIALS, password);
		
		//initialize the OpenLdap context
		ctx = new InitialLdapContext(env, null);
		 
	}
	//close the connection to OpenLdap server
	public void close() throws NamingException{
		ctx.close();
	}
	//get the OpenLdap context
	public LdapContext getContext(){
		return  this.ctx;
	}
	
	//create an ou entry
	public void create(TopicEntry t){
		//build the attributes
		Attributes attrs = new BasicAttributes();
	    attrs.put("ou", "TopicEntry");
	    BasicAttribute objectclassSet = new BasicAttribute("objectclass");
	    objectclassSet.add("top");
	    objectclassSet.add("organizationalUnit");
	    attrs.put(objectclassSet);
	    attrs.put("description", "name=" + t.getTopicName() + ",code=" + t.getTopicCode());
	    
		try {
			ctx.rebind(t.getTopicPath(), t, attrs);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//create an ou entry
	public void createAndSave(ComplexGroup c){
		//build the attributes
		Attributes attrs = new BasicAttributes();
	    attrs.put("ou", "ComplexGroup");
	    BasicAttribute objectclassSet = new BasicAttribute("objectclass");
	    objectclassSet.add("top");
	    objectclassSet.add("organizationalUnit");
	    attrs.put(objectclassSet);
	    
		try {
			ctx.rebind("ou=ComplexGroup", c, attrs);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//get the single entry by dn
	public TopicEntry getByDN(String dn) throws NamingException{
		TopicEntry t = null;
		Attributes attrs = null;
		//t = (TopicEntry) ctx.lookup(dn);
		try{
		t = (TopicEntry) ctx.lookup(dn);
		}catch (NameNotFoundException e){
			JOptionPane.showMessageDialog( null, "NameNotFoundException");
		}
		attrs = ctx.getAttributes(dn, attrIDs);
		String topicName = attrs.toString().split("name=")[1].split(",")[0];
		String topicCode = attrs.toString().split("code=")[1].split("}")[0];
		t.setTopicName(topicName);
		t.setTopicCode(topicCode);
		if(t.getWsnpolicymsg() != null){
			t.getWsnpolicymsg().setTargetTopic(topicName);
		}
		return t;
	}
	//get the object in the sub level as a list
	public List<TopicEntry> getSubLevel(TopicEntry t){
		List<TopicEntry> ls = new ArrayList<TopicEntry>();
		String sub_path = null;
		
		try {
			NamingEnumeration<NameClassPair> x = ctx.list(t.getTopicPath());
			while(x.hasMore()){
				sub_path = x.next().getName() + "," + t.getTopicPath();
				TopicEntry temp = getByDN(sub_path);
				ls.add(temp);
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ls;
	}
	//get t and all its childrens as a list
	public List<TopicEntry> getWithAllChildrens(TopicEntry t){
		List<TopicEntry> childrens = new ArrayList<TopicEntry>();
		Queue<TopicEntry> queue = new LinkedList<TopicEntry>();
		
		childrens.add(t);
		queue.offer(t);
		while(!queue.isEmpty()){
			TopicEntry temp = queue.poll();
			List<TopicEntry> list = getSubLevel(temp);
			if(!list.isEmpty()){
				for(int i=0;i<list.size();i++){
					childrens.add(list.get(i));
					queue.offer(list.get(i));
				}
			}
		}		
		return childrens;
	}
	//get all childrens of t as a list
	public List<TopicEntry> getAllChildrens(TopicEntry t){
		List<TopicEntry> childrens = new ArrayList<TopicEntry>();
		Queue<TopicEntry> queue = new LinkedList<TopicEntry>();
		
		queue.offer(t);
		while(!queue.isEmpty()){
			TopicEntry temp = queue.poll();
			List<TopicEntry> list = getSubLevel(temp);
			if(!list.isEmpty()){
				for(int i=0;i<list.size();i++){
					childrens.add(list.get(i));
					queue.offer(list.get(i));
				}
			}
		}	
		return childrens;
	}
	//delete an entry(t must be a leaf)
	public void delete(TopicEntry t){
		try {
			ctx.unbind(t.getTopicPath());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//delete an entry with all of its childrens(t can be any type of node)
	public void deleteWithAllChildrens(TopicEntry t){
		List<TopicEntry> list = getWithAllChildrens(t);
		for(int i=list.size()-1;i>=0;i--){
			try {
				ctx.unbind(list.get(i).getTopicPath());
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//rename an entry
	public void rename(TopicEntry t, String newName) throws NamingException{
		TopicEntry oldEntry = new TopicEntry(t.getTopicName(), t.getTopicCode(), t.getTopicPath(), t.getWsnpolicymsg());
//		newEntry = t;
		//
		String oldName = t.getTopicName();
		String oldPath = t.getTopicPath();
		String newPath = oldPath.replaceAll(oldName, newName);
//	//	newPath.replaceAll(t.getTopicName(), new_name);//��·���е������滻
		t.setTopicPath(newPath);
		t.setTopicName(newName);
	
		create(t);
		
		Queue<TopicEntry> queue = new LinkedList<TopicEntry>();		
		queue.offer(oldEntry);
		while(!queue.isEmpty()){
			TopicEntry temp = queue.poll();
			
			List<TopicEntry> list = getSubLevel(temp);
			if(!list.isEmpty()){
				for(int i=0;i<list.size();i++){	
					TopicEntry oldChildEntry = new TopicEntry(list.get(i).getTopicName(), list.get(i).getTopicCode(), list.get(i).getTopicPath(), list.get(i).getWsnpolicymsg());
					String topicPath =list.get(i).getTopicPath().replaceAll(oldName, newName);
					list.get(i).setTopicPath(topicPath);
					create(list.get(i));
					queue.offer(oldChildEntry);
				}
			}
		}		
		deleteWithAllChildrens(oldEntry);
//			
//		ModificationItem[] mods =new ModificationItem[1];
//		Attribute attr = new BasicAttribute("description");
//		attr.add("name=" + new_name + ",code=" + t.getTopicCode());
//		mods[0] = new ModificationItem(LdapContext.REPLACE_ATTRIBUTE, attr);
//		
//		try {
//			ctx.modifyAttributes(t.getTopicPath(), mods);
//		} catch (NamingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	//get topic name string from string with "ou="
	private String getTopicName(String nameWithOU){
		return nameWithOU.substring(3, nameWithOU.length());
	}
}


















