package view;

import java.awt.Component;
import java.io.IOException;

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
	private ReadPanel readPanel;
	private WritePanel writePanel;
	private TransactionsPanel transactionsPanel;
	private String IPAddress;
	private String branchName;
	
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
			
			transactionsPanel = new TransactionsPanel();
			
			readPanel = new ReadPanel(transactionsPanel, branchName);
			readPanel.setLocation(0, 0);
			readPanel.setSize(250, 550);
			writePanel = new WritePanel(transactionsPanel);
			writePanel.setLocation(250, 0);
			writePanel.setSize(250, 150);
			transactionsPanel.setLocation(250,  150);
			transactionsPanel.setSize(250, 400);
			switch(branchName){
				case "Palawan":
					transactionsPanel.setClient(new PalawanClient(IPAddress));
					break;
			}
			mainPanel.add(readPanel);
			mainPanel.add(writePanel);
			mainPanel.add(transactionsPanel);
			
			
			this.add(mainPanel);
			this.setTitle("ADVANDB MC03");
			this.setLocationRelativeTo(null);
			this.setVisible(true);
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
	}
}
