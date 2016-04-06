package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PasswordPanel extends JPanel implements ActionListener{

	private JButton btnChange;
	private JTextField tfPassword;
	
	private TransactionsPanel transactionsPanel;
	
	public PasswordPanel(TransactionsPanel transactionsPanel) {
		this.setBorder(BorderFactory.createTitledBorder("Change Password"));
		this.transactionsPanel = transactionsPanel;
		tfPassword = new JTextField(15);
		btnChange = new JButton("Change");
		btnChange.addActionListener(this);
		btnChange.setActionCommand("CHANGE");
		this.add(tfPassword);
		this.add(btnChange);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		transactionsPanel.setClientPassword(tfPassword.getText());
	}
}
