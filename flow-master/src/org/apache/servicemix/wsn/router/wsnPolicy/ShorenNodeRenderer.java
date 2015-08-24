/**
 * @author shoren
 * @date 2013-3-5
 */
package org.apache.servicemix.wsn.router.wsnPolicy;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetMsg;
/**
 *
 */
public class ShorenNodeRenderer extends DefaultTreeCellRenderer{
	
	private static final long serialVersionUID = 1L;

	public Component getTreeCellRendererComponent(JTree tree, Object value,boolean sel, boolean expanded, boolean leaf, int row,boolean hasFocus) {  
		  
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,row, hasFocus);    
		  
		DefaultMutableTreeNode node=(DefaultMutableTreeNode)value;
		//StringValue是value调用toString()后的转来的描述性文字，所以在节点中包装的
		//对象应自己重写toString方法来实现自己想要的结果  	  
	//	String stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus); 
	//	setText(stringValue);
		//与上述方法等价
		if(!node.isRoot())
		{
			TargetMsg msg = (TargetMsg)node.getUserObject();
			setText(msg.getName());
			setIcon(null);
		}
		
		return this;  
		  
		}//end of getTreeCellRendererComponent  
}
