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

		if(!node.isRoot())
		{
			TargetMsg msg = (TargetMsg)node.getUserObject();
			setText(msg.getName());
			setIcon(null);
		}
		
		return this;  
		  
		}//end of getTreeCellRendererComponent  
}
