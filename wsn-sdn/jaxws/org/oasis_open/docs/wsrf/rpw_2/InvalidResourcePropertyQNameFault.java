package org.oasis_open.docs.wsrf.rpw_2;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.3.2
 * 2012-11-13T15:21:18.348+08:00
 * Generated source version: 2.3.2
 */

@WebFault(name = "InvalidResourcePropertyQNameFault", targetNamespace = "http://docs.oasis-open.org/wsrf/rp-2")
public class InvalidResourcePropertyQNameFault extends Exception {
	public static final long serialVersionUID = 20121113152118L;

	private org.oasis_open.docs.wsrf.rp_2.InvalidResourcePropertyQNameFaultType invalidResourcePropertyQNameFault;

	public InvalidResourcePropertyQNameFault() {
		super();
	}

	public InvalidResourcePropertyQNameFault(String message) {
		super(message);
	}

	public InvalidResourcePropertyQNameFault(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidResourcePropertyQNameFault(String message, org.oasis_open.docs.wsrf.rp_2.InvalidResourcePropertyQNameFaultType invalidResourcePropertyQNameFault) {
		super(message);
		this.invalidResourcePropertyQNameFault = invalidResourcePropertyQNameFault;
	}

	public InvalidResourcePropertyQNameFault(String message, org.oasis_open.docs.wsrf.rp_2.InvalidResourcePropertyQNameFaultType invalidResourcePropertyQNameFault, Throwable cause) {
		super(message, cause);
		this.invalidResourcePropertyQNameFault = invalidResourcePropertyQNameFault;
	}

	public org.oasis_open.docs.wsrf.rp_2.InvalidResourcePropertyQNameFaultType getFaultInfo() {
		return this.invalidResourcePropertyQNameFault;
	}
}
