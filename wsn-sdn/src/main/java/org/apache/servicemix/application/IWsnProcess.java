package org.apache.servicemix.application;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://org.apache.servicemix.application", name = "IWsnProcess")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface IWsnProcess {
	public String WsnProcess(@WebParam(partName = "Wsn", name = "WsnProcess", targetNamespace = "http://org.apache.servicemix.application") String message);
}
