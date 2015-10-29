package edu.bupt.wangfu.ldap;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;


public class UserAuthenticate {
	private String URL = "ldap://localhost:389";
	private String SEARCHDN = "CN=alimailfad,OU=service,DC=hz,DC=ali,DC=com";
	private String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private String BASEDN = "DC=hz,DC=ali,DC=com";
	private LdapContext ctx = null;
	private Hashtable env = null;
	private Control[] connCtls = null;


	private void LDAP_connect() {
		env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
		env.put(Context.PROVIDER_URL, URL);// LDAP server
		env.put(Context.SECURITY_PRINCIPAL, SEARCHDN);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_CREDENTIALS, "password");
// ????????????????????????,???????????????????? 


		try {
			ctx = new InitialLdapContext(env, connCtls);
		} catch (NamingException e) {
// TODO Auto-generated catch block 
			e.printStackTrace();
		}
	}


	private String getUserDN(String email) {
		String userDN = "";


		LDAP_connect();


		try {
			String filters = "(&(&(objectCategory=person)(objectClass=user))(sAMAccountName=elbert.chenh))";
			String[] returnedAtts = {"distinguishedName",
					"userAccountControl", "displayName", "employeeID"};
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);


			if (returnedAtts != null && returnedAtts.length > 0) {
				constraints.setReturningAttributes(returnedAtts);
			}
			NamingEnumeration en = ctx.search(BASEDN, filters, constraints);
			if (en == null) {
				System.out.println("Have no NamingEnumeration.");
			}
			if (!en.hasMoreElements()) {
				System.out.println("Have no element.");
			} else {
				while (en != null && en.hasMoreElements()) {
					Object obj = en.nextElement();


					if (obj instanceof SearchResult) {
						SearchResult si = (SearchResult) obj;
						Attributes userInfo = si.getAttributes();
						userDN += userInfo.toString();
						userDN += "," + BASEDN;
					} else {
						System.out.println(obj.toString());
					}
					System.out.println(userDN);
				}
			}
		} catch (Exception e) {
			System.out.println("Exception in search():" + e);
		}


		return userDN;
	}


	public boolean authenricate(String ID, String password) {
		boolean valide = false;
		String userDN = getUserDN(ID);
		try {
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			ctx.reconnect(connCtls);
			System.out.println(userDN + " is authenticated");
			valide = true;
		} catch (AuthenticationException e) {
			System.out.println(userDN + " is not authenticated");
			System.out.println(e.toString());
			valide = false;
		} catch (NamingException e) {
			System.out.println(userDN + " is not authenticated");
			valide = false;
		}


		return valide;
	}
}