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

import model.Entity;
import model.QueryGenerator;
import model.ReadTransaction;
import model.Transaction;

public class TransactionsPanel extends JPanel{

	private ArrayList<Transaction> transactions;
	private JTextArea taTransactions;
	private JButton btnExecute;
	private Client client;
	private ArrayList<String> transactionsList;
	private int id = 0;
	
	public TransactionsPanel(MainFrame mainFrame) {
		taTransactions = new JTextArea();
		taTransactions.setAlignmentX(CENTER_ALIGNMENT);
		
		btnExecute = new JButton("Execute");
		btnExecute.setAlignmentX(CENTER_ALIGNMENT);
		
		btnExecute.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					client.case1(transactionsList);
					System.out.println("Finished executing SQL statements");
					ArrayList<String> readTransactions = new ArrayList<String>();
					for(int i = 0; i < transactionsList.size(); i++) {
						Transaction t = transactions.get(i);
						if(t instanceof ReadTransaction) {
							readTransactions.add(t.toString()+"@"+transactionsList.get(i).split("@")[3]);
						}
					}
					mainFrame.updateTransactionList(readTransactions);
					transactions.removeAll(transactions);
					transactionsList.removeAll(transactionsList);
					taTransactions.setText("");
					id = 0;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
		id++;
		transactions.add(transaction);
		taTransactions.append(transaction.toString()+"\n");
		transactionsList.add(QueryGenerator.generate(transaction)+"@"+id);
		System.out.println("Generated: "+QueryGenerator.generate(transaction)+"@"+id);
	}
	
	public void setClient(Client c){
		this.client = c;
	}
	
	public Client getClient() {
		return client;
	}
	
	public Object[][] getById(String id) {
		ArrayList<Entity> entities = client.getById(id);
		// TODO handle if entities is null (unable to read from other branches)

		if (entities == null) {
			return null;
		}
		Object data[][] = new Object[entities.size()][Entity.COLUMN_COUNT];
		for(int i = 0; i < entities.size(); i++) {
			data[i] = entities.get(i).toArray();
		}
		return data;
	}
}

