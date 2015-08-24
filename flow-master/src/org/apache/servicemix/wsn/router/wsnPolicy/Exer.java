/**
 * @author shoren
 * @date 2013-3-5
 */
package org.apache.servicemix.wsn.router.wsnPolicy;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.servicemix.wsn.router.wsnPolicy.msgs.ComplexGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;


/**
 *
 */
public class Exer extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Toolkit kit;
	private static Dimension screenSize;
	
	
	static
	{
		kit = Toolkit.getDefaultToolkit();  
        screenSize = kit.getScreenSize();
	}
	
	public Exer()
	{
		super("策略集群操作");
		
		List<TargetGroup> targetGroups = new ArrayList<TargetGroup>();
		for(int i=0; i<5; i++)
		{
			targetGroups.add(new TargetGroup("test" + i));
		}
		
		ComplexGroup cg = new ComplexGroup("complexGroup", null, targetGroups);
		List<ComplexGroup> complexGroups = new ArrayList<ComplexGroup>();
		complexGroups.add(cg);
		WsnPolicyMsg wpm = new WsnPolicyMsg("test", complexGroups, targetGroups);
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		
		ShorenUtils.showPolicyMsg(root, wpm);
		JTree tree = new JTree(new DefaultTreeModel(root));  //注意这两句的次序
		ShorenNodeRenderer ren = new ShorenNodeRenderer();
		tree.setCellRenderer(ren);
		tree.setShowsRootHandles(true);  //显示前面的分支线
		tree.setRootVisible(false);		 //不显示根节点
		add(tree);
		
		
	//	add(createList("信息列表"), BorderLayout.CENTER);
		//frame conf
		setBounds(screenSize.width/4 , screenSize.height/8, 
				screenSize.width/4 ,screenSize.height/2);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		setVisible(true);
	}	
	
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());			
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new Exer();
	}

}
