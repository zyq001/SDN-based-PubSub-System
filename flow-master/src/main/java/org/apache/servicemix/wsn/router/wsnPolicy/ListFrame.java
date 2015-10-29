/**
 * @author shoren
 * @date 2013-3-26
 */
package org.apache.servicemix.wsn.router.wsnPolicy;

import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetHost;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetMsg;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetRep;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;


/**
 *
 */
public class ListFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static Toolkit kit;
	private static Dimension screenSize;

	static {
		kit = Toolkit.getDefaultToolkit();
		screenSize = kit.getScreenSize();
	}

	private JButton okayBtn = createBtn("确定");
	private JButton cancelBtn = createBtn("取消");
	private JList nameList;
	private WsnPolicyInterface parentFrame;
	private HashMap<String, TargetMsg> name_msg = new HashMap<String, TargetMsg>();
	private boolean isRegMsg = true;

	public ListFrame(List<Object> msgs, boolean isRegMsg) {
		super("信息列表");
		add(createList(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);

		this.isRegMsg = isRegMsg;
		initMsg(msgs);

		//frame conf
		setBounds(screenSize.width / 4, screenSize.height / 8,
				3 * screenSize.width / 16, 4 * screenSize.height / 8);
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
		new ListFrame(null, true);

	}

	public WsnPolicyInterface getParentFrame() {
		return parentFrame;
	}

	public void setParentFrame(WsnPolicyInterface parentFrame) {
		this.parentFrame = parentFrame;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okayBtn) {
			Object[] selects = nameList.getSelectedValues();
			if (selects.length == 0) {
				this.dispose();
				return;
			}
			JList result = null;
			List relist = null;
			if (this.isRegMsg) {
				result = getParentFrame().name_List.get("regs");
				relist = getParentFrame().name_Array.get("regs");
			} else {
				result = getParentFrame().name_List.get("hosts");
				relist = getParentFrame().name_Array.get("hosts");
			}
			DefaultListModel model = (DefaultListModel) result.getModel();
			model.clear();
			relist.clear();
			for (int i = 0; i < selects.length; i++) {
				String select = (String) selects[i];
				model.addElement(select);
				relist.add(name_msg.get(select));
			}
			if (isRegMsg && selects.length > 1) {
				getParentFrame().updateBtns();
			}
			this.dispose();
		} else if (e.getSource() == cancelBtn) {
			this.dispose();
		}
	}

	protected void initMsg(List<Object> msgs) {
		if (msgs == null) return;
		DefaultListModel listModel = (DefaultListModel) nameList.getModel();
		for (int i = 0; i < msgs.size(); i++) {
			TargetMsg msg = (TargetMsg) msgs.get(i);
			String name = null;
			if (isRegMsg) {
				name = ((TargetRep) msg).getRepIp();
			} else {
				name = ((TargetHost) msg).getHostIp();
			}
			listModel.addElement(name);
			name_msg.put(name, msg);

		}

	}

	protected JScrollPane createList() {
		DefaultListModel listModel = new DefaultListModel();
		//Create the list and put it in a scroll pane.
		nameList = new JList(listModel);
		nameList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION); //多选
		JScrollPane listScrollPane = new JScrollPane(nameList);
		listScrollPane.setPreferredSize(new Dimension(250, 100));
		listScrollPane.setMinimumSize(new Dimension(200, 100));
		listScrollPane.setBorder(BorderFactory.createTitledBorder("选择范围"));
		return listScrollPane;
	}

	protected JButton createBtn(String btnName) {
		JButton btn = new JButton(btnName);
		btn.setSize(80, 30);
		btn.setPreferredSize(new Dimension(80, 30));
		return btn;
	}

	protected JPanel createButtonPanel() {
		JPanel btnPanel = new JPanel();
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(53));
		box.add(okayBtn);
		box.add(Box.createHorizontalStrut(20));
		box.add(cancelBtn);
		btnPanel.setLayout(new BorderLayout());
		btnPanel.add(box, BorderLayout.CENTER);
		okayBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		return btnPanel;
	}
}
