package org.apache.servicemix.wsn.router.design;

import org.apache.servicemix.wsn.router.admin.AdminMgr;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

public class SetAddress {
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private AdminMgr IF;

	SetAddress(AdminMgr IF) {
		this.IF = IF;
	}

	/**
	 * Open the window.
	 *
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
		final Shell shell = new Shell(SWT.SYSTEM_MODAL | SWT.CLOSE);

		shell.setSize(513, 341);
		shell.setText("设置地址信息");

		//-------------------------------------------------
		Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 31, 449, 79);
		group.setText("主机原来的地址和端口");

		Label label = new Label(group, SWT.NONE);
		label.setBounds(22, 38, 30, 12);
		label.setText("地址:");
		text = new Text(group, SWT.BORDER);
		text.setBounds(58, 35, 101, 18);


		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(226, 38, 42, 12);
		lblNewLabel.setText("端口号:");
		text_1 = new Text(group, SWT.BORDER);
		text_1.setBounds(271, 35, 70, 18);
		//---------------------------------

		//-----------------------------------
		Group group_1 = new Group(shell, SWT.NONE);
		group_1.setBounds(10, 132, 449, 79);
		group_1.setText("主机新配置的地址和端口");

		Label label_1 = new Label(group_1, SWT.NONE);
		label_1.setText("地址:");
		label_1.setBounds(22, 37, 30, 12);

		text_2 = new Text(group_1, SWT.BORDER);
		text_2.setBounds(58, 34, 101, 18);

		Label label_2 = new Label(group_1, SWT.NONE);
		label_2.setText("端口号:");
		label_2.setBounds(228, 37, 42, 12);

		text_3 = new Text(group_1, SWT.BORDER);
		text_3.setBounds(276, 31, 70, 18);

		//-----------------------------------------
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IF.setAddress(text.getText(), Integer.parseInt(text_2.getText()), text_2.getText(), Integer.parseInt(text_3.getText()));
			}
		});
		btnNewButton.setBounds(256, 277, 72, 22);
		btnNewButton.setText("确定");

		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		btnNewButton_1.setBounds(387, 277, 72, 22);
		btnNewButton_1.setText("取消");


		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
