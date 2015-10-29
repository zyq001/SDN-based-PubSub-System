package org.oasis_open.docs.wsn.bw_2;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.3.2
 * 2012-11-13T15:21:18.377+08:00
 * Generated source version: 2.3.2
 */

@WebFault(name = "InvalidFilterFault", targetNamespace = "http://docs.oasis-open.org/wsn/b-2")
public class InvalidFilterFault extends Exception {
	public static final long serialVersionUID = 20121113152118L;

	private org.oasis_open.docs.wsn.b_2.InvalidFilterFaultType invalidFilterFault;

	public InvalidFilterFault() {
		super();
	}

	public InvalidFilterFault(String message) {
		super(message);
	}

	public InvalidFilterFault(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidFilterFault(String message, org.oasis_open.docs.wsn.b_2.InvalidFilterFaultType invalidFilterFault) {
		super(message);
		this.invalidFilterFault = invalidFilterFault;
	}

	public InvalidFilterFault(String message, org.oasis_open.docs.wsn.b_2.InvalidFilterFaultType invalidFilterFault, Throwable cause) {
		super(message, cause);
		this.invalidFilterFault = invalidFilterFault;
	}

	public org.oasis_open.docs.wsn.b_2.InvalidFilterFaultType getFaultInfo() {
		return this.invalidFilterFault;
	}
}
