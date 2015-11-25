package com.bupt.wangfu.ldap;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

public class Main {

	/**
	 * @param args
	 * @throws NamingException
	 */
	public static void main(String[] args) throws NamingException {
		TopicEntry all = new TopicEntry("all", "1",
				"ou=all_test,dc=wsn,dc=com", null);

		TopicEntry alarm = new TopicEntry("alarm", "2", "ou=alarm,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry graphic = new TopicEntry("graphic", "3", "ou=graphic,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry boiler = new TopicEntry("boiler", "4", "ou=boiler,ou=all_test,dc=wsn,dc=com", null);

		TopicEntry alarm1 = new TopicEntry("alarm1", "5", "ou=alarm1,ou=alarm,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry alarm2 = new TopicEntry("alarm2", "6", "ou=alarm2,ou=alarm,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry alarm3 = new TopicEntry("alarm3", "7", "ou=alarm3,ou=alarm,ou=all_test,dc=wsn,dc=com", null);

		TopicEntry graphic1 = new TopicEntry("graphic1", "8", "ou=graphic1,ou=graphic,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry graphic2 = new TopicEntry("graphic2", "9", "ou=graphic2,ou=graphic,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry graphic3 = new TopicEntry("graphic3", "10", "ou=graphic3,ou=graphic,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry graphic4 = new TopicEntry("graphic4", "11", "ou=graphic4,ou=graphic,ou=all_test,dc=wsn,dc=com", null);

		TopicEntry boiler1 = new TopicEntry("boiler1", "12", "ou=boiler1,ou=boiler,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry boiler2 = new TopicEntry("boiler2", "13", "ou=boiler2,ou=boiler,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry boiler3 = new TopicEntry("boiler3", "14", "ou=boiler3,ou=boiler,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry boiler4 = new TopicEntry("boiler4", "15", "ou=boiler4,ou=boiler,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry boiler5 = new TopicEntry("boiler5", "16", "ou=boiler5,ou=boiler,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry boiler6 = new TopicEntry("boiler6", "17", "ou=boiler6,ou=boiler,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry boiler7 = new TopicEntry("boiler7", "18", "ou=boiler7,ou=boiler,ou=all_test,dc=wsn,dc=com", null);

		TopicEntry alarm11 = new TopicEntry("alarm11", "19",
				"ou=alarm11,ou=alarm1,ou=alarm,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry alarm12 = new TopicEntry("alarm12", "20",
				"ou=alarm12,ou=alarm1,ou=alarm,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry alarm13 = new TopicEntry("alarm13", "21",
				"ou=alarm13,ou=alarm1,ou=alarm,ou=all_test,dc=wsn,dc=com", null);

		TopicEntry alarm111 = new TopicEntry("alarm111", "22",
				"ou=alarm111,ou=alarm11,ou=alarm1,ou=alarm,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry alarm112 = new TopicEntry("alarm112", "23",
				"ou=alarm112,ou=alarm11,ou=alarm1,ou=alarm,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry alarm113 = new TopicEntry("alarm113", "24",
				"ou=alarm113,ou=alarm11,ou=alarm1,ou=alarm,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry alarm114 = new TopicEntry("alarm114", "25",
				"ou=alarm114,ou=alarm11,ou=alarm1,ou=alarm,ou=all_test,dc=wsn,dc=com", null);
		TopicEntry alarm115 = new TopicEntry("alarm115", "26",
				"ou=alarm115,ou=alarm11,ou=alarm1,ou=alarm,ou=all_test,dc=wsn,dc=com", null);

		Ldap ldap = new Ldap();
		ldap.connectLdap("localhost", "cn=Manager,dc=wsn,dc=com", "123456");

		LdapContext ctx = ldap.getContext();

//	    TopicEntry xxx = ldap.getByDN("ou=sub_sub_test_topic2,ou=sub_test_topic1,ou=test_topicTree,dc=wsn,dc=com");
//	    System.out.println(xxx.getTopicName());
//	    ldap.rename(sub_sub_t2, "xxxxx");
//	    String[] attrIDs = {"description"};
//	    Attributes attrs = ctx.getAttributes("ou=sub_sub_test_topic1,ou=sub_test_topic1,ou=test_topicTree,dc=wsn,dc=com", attrIDs);
//	    System.out.println(attrs.toString());

//	    ldap.deleteWithAllChildrens(all);

		ldap.create(all);

		ldap.create(alarm);
		ldap.create(graphic);
		ldap.create(boiler);

		ldap.create(alarm1);
		ldap.create(alarm2);
		ldap.create(alarm3);

		ldap.create(graphic1);
		ldap.create(graphic2);
		ldap.create(graphic3);
		ldap.create(graphic4);

		ldap.create(boiler1);
		ldap.create(boiler2);
		ldap.create(boiler3);
		ldap.create(boiler4);
		ldap.create(boiler5);
		ldap.create(boiler6);
		ldap.create(boiler7);

		ldap.create(alarm11);
		ldap.create(alarm12);
		ldap.create(alarm13);

		ldap.create(alarm111);
		ldap.create(alarm112);
		ldap.create(alarm113);
		ldap.create(alarm114);
		ldap.create(alarm115);

//	    List<TopicEntry> list = ldap.getWithAllChildrens(t);
//	    for(TopicEntry te : list){
//	    	System.out.println(te.getTopicCode());
//	    }
//	    System.out.println(ldap.getByDN("ou=test_topicTree,dc=wsn,dc=com").getTopicCode());


		ldap.close();

//		// TODO Auto-generated method stub	
//		//????OpenLDAP????????????????LdapContext????????
//		LdapUtil lu = new LdapUtil();
//		lu.connectLdap();
//		LdapContext ctx = lu.getContext();
//		
//		TopicEntry t = new TopicEntry("test_topic", "123456789", 
//				"ou=test_topic,ou=topic1,ou=root-topicTree,dc=maxcrc,dc=com");
//		
//		ctx.bind("ou=test_topic,ou=topic1,ou=root-topicTree,dc=maxcrc,dc=com", t);
//
//		lu.closeContext();


		// Set up environment for creating initial context
//	    Hashtable<String, Object> env = new Hashtable<String, Object>(11);
//	    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
//	    env.put(Context.PROVIDER_URL, "ldap://localhost:389");
//	    //set the authentication mode
//		env.put(Context.SECURITY_AUTHENTICATION, "simple");
//		//set user of ldap server
//		env.put(Context.SECURITY_PRINCIPAL, "cn=Manager,dc=wsn,dc=com");
//		//set password of user
//		env.put(Context.SECURITY_CREDENTIALS, "123456");


//		try {
//		      // Create the initial context
////		      Context ctx = new InitialContext(env);
//			LdapContext ctx = new InitialLdapContext(env, null);
//
//		      // Create the object to be bound
//		      Fruit fruit = new Fruit("orange");
//
//		      Attributes attrs = new BasicAttributes();
//		      attrs.put("ou", "TopicEntry");
//		      attrs.put("ou", "test_topic");
//		      BasicAttribute objectclassSet = new BasicAttribute("objectclass");
//		      objectclassSet.add("top");
//		      objectclassSet.add("organizationalUnit");
//		      objectclassSet.add("javaClassName");
//		      objectclassSet.add("TopicEntry");
//		      attrs.put(objectclassSet);
//		      attrs.put("javaClassName", fruit.getClass().getName());
//		      
//		      
//		      
//		      
//		      // Perform the bind
//		      ctx.rebind("ou=test_topic,ou=root-topicTree,dc=maxcrc,dc=com", fruit, attrs);
//
//		      // Check that it is bound
//		      Object obj = ctx.lookup("ou=test_topic,ou=root-topicTree,dc=maxcrc,dc=com");
//		      System.out.println(obj);
//
//		      // Close the context when we're done
//		      ctx.close();
//		    } catch (NamingException e) {
//		      System.out.println("Operation failed: " + e);
//		    }
//		


//	    try {
//	      // Create the initial context
////	      Context ctx = new InitialContext(env);
//	      LdapContext ctx = new InitialLdapContext(env, null);
////	    	DirContext ctx = new InitialLdapContext(env, null);
//	    	

		// Create object to be bound
		//Button b = new Button("Push me");


//	      Attributes attrs = new BasicAttributes();
////	      attrs.put("ou", "TopicEntry");
////	      attrs.put("ou", "test_topic");
//	      BasicAttribute objectclassSet = new BasicAttribute("objectclass");
//	      objectclassSet.add("top");
//	      objectclassSet.add("organizationalUnit");
////	      objectclassSet.add("javaCodebase");
//	      attrs.put(objectclassSet);
//	      attrs.put("javaCodebase", "http://com.shmily.main/classes");
		// Perform bind
//	      ctx.rebind("ou=test_topic,dc=wsn,dc=com", t, attrs);

//	      Attributes attributes = ctx.getAttributes("ou=test_topic,ou=root-topicTree,dc=maxcrc,dc=com");
//	      AttributesMapper mapper = new 
//	      mapper.mapFromAttributes(attributes);
		// Check that it is bound
//	      System.out.println(ctx.lookup("ou=test_topic,ou=root-topicTree,dc=maxcrc,dc=com"));
//	      TopicEntry b2 = (TopicEntry) ctx.lookup("ou=test_topic,dc=wsn,dc=com");
//	      System.out.println(b2.getTopicPath());

		// Close the context when we're done
//	      ctx.close();
	}

}

//class Fruit implements Referenceable {
//	  String fruit;
//
//	  public Fruit(String f) {
//	    fruit = f;
//	  }
//
//	  public Reference getReference() throws NamingException {
//
//	    return new Reference(Fruit.class.getName(), new StringRefAddr("fruit",
//	        fruit), FruitFactory.class.getName(), null); // factory location
//	  }
//
//	  public String toString() {
//	    return fruit;
//	  }
//}
//
//
//class FruitFactory implements ObjectFactory {
//
//	  public FruitFactory() {
//	  }
//
//	  public Object getObjectInstance(Object obj, Name name, Context ctx,
//	      Hashtable<?, ?> env) throws Exception {
//
//	    if (obj instanceof Reference) {
//	      Reference ref = (Reference) obj;
//
//	      if (ref.getClassName().equals(Fruit.class.getName())) {
//	        RefAddr addr = ref.get("fruit");
//	        if (addr != null) {
//	          return new Fruit((String) addr.getContent());
//	        }
//	      }
//	    }
//	    return null;
//	  }
//	}
