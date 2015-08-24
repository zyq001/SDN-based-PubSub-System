package org.apache.servicemix.wsn.push;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * <b>function</b>: WSN调用处理通知消息服务的接口
 * @author 柴兆航
 * @version 2.0
 *
 */
@WebService(targetNamespace = "http://org.apache.servicemix.wsn.push",name = "IPublishProcess")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface IPublishProcess{
	public void publishProcess(@WebParam(partName = "Publish",name = "publishProcess",targetNamespace = "http://org.apache.servicemix.wsn.push")String publish);
}