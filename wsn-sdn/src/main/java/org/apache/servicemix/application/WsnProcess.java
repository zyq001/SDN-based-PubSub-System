package org.apache.servicemix.application;

import javax.xml.bind.JAXBException;
import javax.xml.ws.Endpoint;

public class WsnProcess {
	/**
	 * @param args
	 * @throws JAXBException
	 */

	public static void main(String[] args) throws JAXBException {
		// TODO Auto-generated method stub
		// debug-----------
		for (int i = 0; i < args.length; ++i) {
			System.out.println(args[i]);
		}
		// ----------------
		WsnProcessImpl wsnprocess = new WsnProcessImpl();
		wsnprocess.init();
		Endpoint.publish(args[0], wsnprocess);
	}

}
