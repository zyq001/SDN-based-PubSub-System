package org.oasis_open.docs.wsn.brw_2;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.3.2
 * 2012-11-13T15:21:18.390+08:00
 * Generated source version: 2.3.2
 */

@WebFault(name = "PublisherRegistrationFailedFault", targetNamespace = "http://docs.oasis-open.org/wsn/br-2")
public class PublisherRegistrationFailedFault extends Exception {
	public static final long serialVersionUID = 20121113152118L;

	private org.oasis_open.docs.wsn.br_2.PublisherRegistrationFailedFaultType publisherRegistrationFailedFault;

	public PublisherRegistrationFailedFault() {
		super();
	}

	public PublisherRegistrationFailedFault(String message) {
		super(message);
	}

	public PublisherRegistrationFailedFault(String message, Throwable cause) {
		super(message, cause);
	}

	public PublisherRegistrationFailedFault(String message, org.oasis_open.docs.wsn.br_2.PublisherRegistrationFailedFaultType publisherRegistrationFailedFault) {
		super(message);
		this.publisherRegistrationFailedFault = publisherRegistrationFailedFault;
	}

	public PublisherRegistrationFailedFault(String message, org.oasis_open.docs.wsn.br_2.PublisherRegistrationFailedFaultType publisherRegistrationFailedFault, Throwable cause) {
		super(message, cause);
		this.publisherRegistrationFailedFault = publisherRegistrationFailedFault;
	}

	public org.oasis_open.docs.wsn.br_2.PublisherRegistrationFailedFaultType getFaultInfo() {
		return this.publisherRegistrationFailedFault;
	}
}
