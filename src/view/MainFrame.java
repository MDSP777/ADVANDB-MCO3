package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import socket.PalawanClient;

public class MainFrame extends JFrame{

	private JPanel mainPanel;
	private IsolationLevelPanel isolationLevelPanel;
	private ReadPanel readPanel;
	private WritePanel writePanel;
	private TransactionsPanel transactionsPanel;
	private TransactionListPanel transactionListPanel;
	private ResultPanel resultPanel;
	private String IPAddress;
	private String branchName;
	private PalawanClient client;
	
	public MainFrame() throws IOException {
		JPanel serverPanel = new JPanel();
		serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.PAGE_AXIS));
		JLabel lblAddress = new JLabel("IP Address: ");
		JTextField tfAddress = new JTextField();
		JComboBox cbDatabases = new JComboBox(new String[]{"Palawan", "Marinduque", "Central"});
		
		serverPanel.add(lblAddress);
		serverPanel.add(tfAddress);
		serverPanel.add(cbDatabases);
		
		int opt = JOptionPane.showConfirmDialog(null, serverPanel, "Input IP Address: ", JOptionPane.OK_CANCEL_OPTION);
		if (opt == JOptionPane.OK_OPTION) {
			IPAddress = tfAddress.getText();
			branchName = cbDatabases.getSelectedItem().toString();

			this.setExtendedState( this.getExtendedState()|JFrame.MAXIMIZED_BOTH );
			mainPanel = new JPanel();
			mainPanel.setLayout(null);
			
			transactionsPanel = new TransactionsPanel(this);
			
			isolationLevelPanel = new IsolationLevelPanel();
			isolationLevelPanel.setLocation(0, 0);
			isolationLevelPanel.setSize(250, 50);
			readPanel = new ReadPanel(transactionsPanel, branchName);
			readPanel.setLocation(0, 75);
			readPanel.setSize(250, 525);
			writePanel = new WritePanel(transactionsPanel, branchName);
			writePanel.setLocation(250, 0);
			writePanel.setSize(250, 200);
			transactionsPanel.setLocation(250, 200);
			transactionsPanel.setSize(250, 400);
			client = new PalawanClient(IPAddress);
			transactionsPanel.setClient(client);
			transactionListPanel = new TransactionListPanel(this);
			transactionListPanel.setLocation(500, 0);
			transactionListPanel.setSize(865, 50);
			resultPanel = new ResultPanel();
			resultPanel.setLocation(500, 50);
			resultPanel.setSize(865, 550);
			mainPanel.add(isolationLevelPanel);
			mainPanel.add(Box.createRigidArea(new Dimension(250, 25)));
			mainPanel.add(readPanel);
			mainPanel.add(writePanel);
			mainPanel.add(transactionsPanel);
			mainPanel.add(transactionListPanel);
			mainPanel.add(resultPanel);
			
			this.add(mainPanel);
			this.setTitle("ADVANDB MC03");
			this.setLocationRelativeTo(null);
			this.setVisible(true);
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.addWindowListener(new WindowListener() {

				@Override
				public void windowActivated(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowClosed(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowClosing(WindowEvent arg0) {
					// TODO Auto-generated method stub
					try {
						client.sendCrashMessage();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void windowDeactivated(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowDeiconified(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowIconified(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowOpened(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
		}
	}
	
	public void updateTransactionList(ArrayList<String> transactions) {
		transactionListPanel.update(transactions);
	}
	
	public void updateTableById(String id) {
		resultPanel.buildTableModel(transactionsPanel.getById(id));
	}
}
