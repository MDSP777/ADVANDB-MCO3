package view;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class TransactionListPanel extends JPanel{
	private JComboBox transactionList;
	
	public TransactionListPanel() {
		this.setBorder(BorderFactory.createTitledBorder("Transaction List"));
	}
}
