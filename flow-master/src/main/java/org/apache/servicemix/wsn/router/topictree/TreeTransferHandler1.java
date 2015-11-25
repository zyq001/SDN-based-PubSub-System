package org.apache.servicemix.wsn.router.topictree;

import com.bupt.wangfu.ldap.Ldap;
import com.bupt.wangfu.ldap.TopicEntry;
import org.apache.servicemix.wsn.router.design.Data;
import org.apache.servicemix.wsn.router.msg.tcp.UpdateTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class TreeTransferHandler1 extends TransferHandler {

	private static final long serialVersionUID = 1L;
	Ldap lu = null;
	JTree LibTree = null;
	JTree TTTree = null;
	Data data = null;

	public TreeTransferHandler1(Ldap lu, JTree LibTree, JTree TTTree, Data data) throws ClassNotFoundException {
		this.lu = lu;
		this.LibTree = LibTree;
		this.TTTree = TTTree;
		this.data = data;
	}

	public int getSourceActions(JComponent c) {
		return MOVE;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		JTree tree = (JTree) c;
		TreePath[] paths = tree.getSelectionPaths();
		ArrayList nodes = new ArrayList();
		for (TreePath path : paths) {
			nodes.add(path.getLastPathComponent());
		}
		return new JTreeTransferable(nodes);
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
//        if (action!=MOVE) return;   
//        try {   
//            TreePath[] paths=(TreePath[])data.getTransferData(JTreeTransferable.FLAVOR);   
//            JTree tree=(JTree)source;   
//            DefaultTreeModel model=(DefaultTreeModel)tree.getModel();   
//   
//            for (TreePath path:paths){   
//   
//                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();   
////                model.nodesWereRemoved(node.getParent(),new int[]{ 2},new DefaultMutableTreeNode[]{node});   
//                model.nodeStructureChanged(node);   
//                model.removeNodeFromParent(node);   
//   
//            }   
//        } catch (Exception e) {   
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.   
//        }   
	}

	@Override
	public boolean canImport(TransferSupport support) {
		if (support.isDataFlavorSupported(JTreeTransferable.FLAVOR)) {
			if (support.getDropAction() == MOVE) return true;
		}
		return false;
	}

	@Override
	public boolean importData(TransferSupport support) {
		JTree tree = (JTree) support.getComponent();
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		Transferable transfer = support.getTransferable();
		try {
			List<DefaultMutableTreeNode> nodes = (List<DefaultMutableTreeNode>) transfer.getTransferData(JTreeTransferable.FLAVOR);
			JTree.DropLocation location = (JTree.DropLocation) support.getDropLocation();
			DefaultMutableTreeNode newParent = (DefaultMutableTreeNode) location.getPath().getLastPathComponent();
			for (DefaultMutableTreeNode node : nodes) {
				TopicEntry NodeEntry = (TopicEntry) node.getUserObject();
				lu.deleteWithAllChildrens(NodeEntry);
				model.removeNodeFromParent(node);
				model.insertNodeInto(node,
						newParent,
						newParent.getChildCount());
				LibTree.updateUI();
				TTTree.updateUI();
				DefaultMutableTreeNode rootnode = (DefaultMutableTreeNode) node.getRoot();
				TopicEntry te = (TopicEntry) rootnode.getUserObject();
				tree.setSelectionPath(new TreePath(model.getPathToRoot(node)));
				importToDatabase(newParent);
			}
			sendNotification();
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void sendNotification() {
		// TODO Auto-generated method stub
		data.sendNotification(new UpdateTree(System.currentTimeMillis()));
	}

	private void importToDatabase(DefaultMutableTreeNode Parent) {
		// TODO Auto-generated method stub
		TopicEntry parentEntry = (TopicEntry) Parent.getUserObject();
		Enumeration<DefaultMutableTreeNode> en = Parent.children();
		while (en.hasMoreElements()) {
			DefaultMutableTreeNode node = en.nextElement();
			TopicEntry nodeEntry = (TopicEntry) node.getUserObject();
			nodeEntry.setTopicPath("ou=" + nodeEntry.getTopicName() + "," + parentEntry.getTopicPath());
			lu.create(nodeEntry);
			importToDatabase(node);
		}
	}
}

class JTreeTransferable implements Transferable {
	public static DataFlavor FLAVOR = null;
	private List<DefaultMutableTreeNode> nodes;

	JTreeTransferable(ArrayList<DefaultMutableTreeNode> nodes) {
		try {
			FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType +
					";class=\"" + ArrayList.class.getName() + "\"");
			this.nodes = nodes;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return nodes;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{FLAVOR};
	}

	public boolean isDataFlavorSupported(DataFlavor flv) {
		return FLAVOR.equals(flv);
	}
}