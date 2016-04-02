package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import socket.Client;
import model.QueryGenerator;
import model.Transaction;

public class TransactionsPanel extends JPanel{

	private ArrayList<Transaction> transactions;
	private JTextArea taTransactions;
	private JButton btnExecute;
	private Client client;
	private ArrayList<String> transactionsList;
	
	public TransactionsPanel() {
		taTransactions = new JTextArea();
		taTransactions.setAlignmentX(CENTER_ALIGNMENT);
		
		btnExecute = new JButton("Execute");
		btnExecute.setAlignmentX(CENTER_ALIGNMENT);
		
		btnExecute.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		transactions = new ArrayList<Transaction>();
		transactionsList = new ArrayList<>();
	
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Transactions"));
		
		JScrollPane scrollPane = new JScrollPane(taTransactions, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(scrollPane);
		this.add(btnExecute);
	}
	
	public void addTransaction(Transaction transaction) {
		transactions.add(transaction);
		taTransactions.append(transaction.toString()+"\n");
		transactionsList.add(transaction.toString());
		System.out.println(QueryGenerator.generate(transaction));
	}
	
	public void setClient(Client c){
		this.client = c;
	}
}

