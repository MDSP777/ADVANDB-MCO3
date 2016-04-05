package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class TransactionListPanel extends JPanel implements ActionListener{
	private JComboBox transactionList;
	private MainFrame mainFrame;
	
	public TransactionListPanel(MainFrame mainFrame) {
		this.setBorder(BorderFactory.createTitledBorder("Transaction List"));
		transactionList = new JComboBox();
		transactionList.addActionListener(this);
		this.mainFrame = mainFrame;
	}
	
	public void update(ArrayList<String> transactions) {
		transactionList.removeAllItems();
		for(String s : transactions) {
			transactionList.addItem(s);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == transactionList) {
			mainFrame.updateTableById(transactionList.getSelectedItem().toString().split("@")[3]);
		}
	}
}
