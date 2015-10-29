/*
 * 
 */

package org.apache.servicemix.wsn.jaxws;

import org.oasis_open.docs.wsn.bw_2.PausableSubscriptionManager;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class was generated by Apache CXF 2.3.2
 * 2012-11-13T15:21:18.459+08:00
 * Generated source version: 2.3.2
 */


@WebServiceClient(name = "PausableSubscriptionManagerService",
		wsdlLocation = "file:/E:/wenpeng/compile/compile/servicemix-wsn2005-2011.01-MutiThread-publish/src/main/resources/org/apache/servicemix/wsn/wsn.wsdl",
		targetNamespace = "http://servicemix.apache.org/wsn/jaxws")
public class PausableSubscriptionManagerService extends Service {

	public final static URL WSDL_LOCATION;

	public final static QName SERVICE = new QName("http://servicemix.apache.org/wsn/jaxws", "PausableSubscriptionManagerService");
	public final static QName JBI = new QName("http://servicemix.apache.org/wsn/jaxws", "JBI");

	static {
		URL url = null;
		try {
			url = new URL("file:/E:/wenpeng/compile/compile/servicemix-wsn2005-2011.01-MutiThread-publish/src/main/resources/org/apache/servicemix/wsn/wsn.wsdl");
		} catch (MalformedURLException e) {
			System.err.println("Can not initialize the default wsdl from file:/E:/wenpeng/compile/compile/servicemix-wsn2005-2011.01-MutiThread-publish/src/main/resources/org/apache/servicemix/wsn/wsn.wsdl");
			// e.printStackTrace();
		}
		WSDL_LOCATION = url;
	}

	public PausableSubscriptionManagerService(URL wsdlLocation) {
		super(wsdlLocation, SERVICE);
	}

	public PausableSubscriptionManagerService(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public PausableSubscriptionManagerService() {
		super(WSDL_LOCATION, SERVICE);
	}


	/**
	 * @return returns PausableSubscriptionManager
	 */
	@WebEndpoint(name = "JBI")
	public PausableSubscriptionManager getJBI() {
		return super.getPort(JBI, PausableSubscriptionManager.class);
	}

	/**
	 * @param features A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
	 * @return returns PausableSubscriptionManager
	 */
	@WebEndpoint(name = "JBI")
	public PausableSubscriptionManager getJBI(WebServiceFeature... features) {
		return super.getPort(JBI, PausableSubscriptionManager.class, features);
	}

}
