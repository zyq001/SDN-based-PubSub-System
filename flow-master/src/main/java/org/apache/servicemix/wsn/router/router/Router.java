package org.apache.servicemix.wsn.router.router;

import edu.bupt.wangfu.sdn.info.Controller;
import edu.bupt.wangfu.sdn.info.Flow;
import org.apache.servicemix.wsn.router.mgr.base.MsgSubsForm;
import org.apache.servicemix.wsn.router.mgr.base.SysInfo;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 计算指定名称的转发路由
 * 针对该名称的所有订阅节点，由集群名比较选取根节点
 * 根据迪杰斯特拉算法由根节点开始计算转发树
 * 将转发树的根节点及本节点的吓一跳转发记录在名称路由结构中
 * @author Sylvia
 *
 */

public class Router extends SysInfo implements IRouter {

	public List<Flow> cacluate(Map<String, Controller> controllers){
		List<Flow> flows = new ArrayList<Flow>();

		return flows;
	}


	public void route(String topic) {

	}
}