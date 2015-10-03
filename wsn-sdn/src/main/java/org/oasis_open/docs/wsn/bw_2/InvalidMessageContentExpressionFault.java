
package org.oasis_open.docs.wsn.bw_2;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.3.2
 * 2012-11-13T15:21:18.385+08:00
 * Generated source version: 2.3.2
 * 
 */

@WebFault(name = "InvalidMessageContentExpressionFault", targetNamespace = "http://docs.oasis-open.org/wsn/b-2")
public class InvalidMessageContentExpressionFault extends Exception {
    public static final long serialVersionUID = 20121113152118L;
    
    private org.oasis_open.docs.wsn.b_2.InvalidMessageContentExpressionFaultType invalidMessageContentExpressionFault;

    public InvalidMessageContentExpressionFault() {
        super();
    }
    
    public InvalidMessageContentExpressionFault(String message) {
        super(message);
    }
    
    public InvalidMessageContentExpressionFault(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMessageContentExpressionFault(String message, org.oasis_open.docs.wsn.b_2.InvalidMessageContentExpressionFaultType invalidMessageContentExpressionFault) {
        super(message);
        this.invalidMessageContentExpressionFault = invalidMessageContentExpressionFault;
    }

    public InvalidMessageContentExpressionFault(String message, org.oasis_open.docs.wsn.b_2.InvalidMessageContentExpressionFaultType invalidMessageContentExpressionFault, Throwable cause) {
        super(message, cause);
        this.invalidMessageContentExpressionFault = invalidMessageContentExpressionFault;
    }

    public org.oasis_open.docs.wsn.b_2.InvalidMessageContentExpressionFaultType getFaultInfo() {
        return this.invalidMessageContentExpressionFault;
    }
}
