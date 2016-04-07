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
		this.add(transactionList);
		update(new ArrayList<String>());
		this.mainFrame = mainFrame;
	}
	
	public void update(ArrayList<String> transactions) {
		if(transactions.isEmpty()){
			transactionList.setVisible(false);
		} else {
			transactionList.setVisible(true);
		}
		transactionList.removeAllItems();
		for(String s : transactions) {
			transactionList.addItem(s);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == transactionList) {
			if(transactionList.getSelectedItem() != null){
				String tString = transactionList.getSelectedItem().toString();
				if(tString.startsWith("Read")) {
					mainFrame.updateTableById("Read@" + tString.split("@")[1]);
				} else if(tString.startsWith("Write")) {
					mainFrame.updateTableById("Write@" + tString.split("@")[1]);
				}
			}
		}
	}
}
