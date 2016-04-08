package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import socket.Client;

public class MainFrame extends JFrame{

	private JPanel mainPanel;
	private IsolationLevelPanel isolationLevelPanel;
	private ReadPanel readPanel;
	private WritePanel writePanel;
	private TransactionsPanel transactionsPanel;
	private TransactionListPanel transactionListPanel;
	private ResultPanel resultPanel;
	private PasswordPanel passwordPanel;
	private String IPAddress;
	private String branchName;
	private Client client;
	private JPanel panelLoader;
	
	public MainFrame() throws IOException {
		
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
		JPanel serverPanel = new JPanel();
		serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.PAGE_AXIS));
		JTextField tfAddress = new JTextField();
		JComboBox cbDatabases = new JComboBox(new String[]{Client.PALAWAN, Client.MARINDUQUE, Client.CENTRAL});
		
		serverPanel.add(tfAddress);
		serverPanel.add(cbDatabases);
		
		int opt = JOptionPane.showConfirmDialog(null, serverPanel, "Input IP Address: ", JOptionPane.OK_CANCEL_OPTION);
		if (opt == JOptionPane.OK_OPTION) {
			IPAddress = tfAddress.getText();
			branchName = cbDatabases.getSelectedItem().toString();

			this.setExtendedState( this.getExtendedState()|JFrame.MAXIMIZED_BOTH );
			mainPanel = new JPanel();
			mainPanel.setLayout(null);
			isolationLevelPanel = new IsolationLevelPanel();
			isolationLevelPanel.setLocation(0, 0);
			isolationLevelPanel.setSize(250, 70);
			
			writePanel = new WritePanel(transactionsPanel, branchName);
			writePanel.setLocation(250, 0);
			writePanel.setSize(250, 200);
			client = new Client(this, IPAddress, branchName);
			transactionListPanel = new TransactionListPanel(this);
			transactionListPanel.setLocation(500, 0);
			transactionListPanel.setSize(865, 70);
			resultPanel = new ResultPanel();
			resultPanel.setLocation(500, 70);
			resultPanel.setSize(865, 530);
			transactionsPanel = new TransactionsPanel(this, resultPanel);
			transactionsPanel.setLocation(250, 200);
			transactionsPanel.setSize(250, 400);
			transactionsPanel.setClient(client);
			readPanel = new ReadPanel(transactionsPanel, branchName);
			readPanel.setLocation(0, 75);
			readPanel.setSize(250, 425);
			passwordPanel = new PasswordPanel(transactionsPanel);
			passwordPanel.setLocation(0, 500);
			passwordPanel.setSize(250, 100);
			panelLoader = new JPanel();
			panelLoader.setLocation(580, 620);
			panelLoader.setSize(200, 50);
			panelLoader.setVisible(false);
			JLabel lblLoader = new JLabel(new ImageIcon("ajax-loader.gif"));
			lblLoader.setText("Executing");
			panelLoader.add(lblLoader);
			mainPanel.add(isolationLevelPanel);
			mainPanel.add(Box.createRigidArea(new Dimension(250, 25)));
			mainPanel.add(readPanel);
			mainPanel.add(writePanel);
			mainPanel.add(transactionsPanel);
			mainPanel.add(transactionListPanel);
			mainPanel.add(resultPanel);
			mainPanel.add(passwordPanel);
			mainPanel.add(panelLoader);
			
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
		if(id.startsWith("Read")) {
			resultPanel.buildTableModel(transactionsPanel.getById(id.split("@")[1]));
		} else if(id.startsWith("Write")) {
			resultPanel.showWriteMessage(transactionsPanel.getWriteStatusById(id.split("@")[1]));
		}
	}
	
	public void enableComboBox() {
		transactionListPanel.enableComboBox();
	}
	
	public void setLoading(boolean bool) {
		panelLoader.setVisible(bool);
	}
}
