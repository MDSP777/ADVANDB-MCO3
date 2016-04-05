package view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import model.WriteTransaction;

public class WritePanel extends JPanel{

	private JTextField tfHousehold;
	private JComboBox cbCalamities;
	private JTextField tfFrequency;
	private TransactionsPanel transactionsPanel;
	private JButton btnAdd;
	private JRadioButton toCrash;
	private JRadioButton notCrash;
	private ButtonGroup bg;
	
	public WritePanel(TransactionsPanel transactionsPanel, String branchName) {
		this.transactionsPanel = transactionsPanel;
		btnAdd = new JButton("Add");
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Write"));
		
		JPanel panelHousehold = new JPanel();
		panelHousehold.setLayout(new BoxLayout(panelHousehold, BoxLayout.LINE_AXIS));
		tfHousehold = new JTextField();
		panelHousehold.add(new JLabel("Household ID: "));
		panelHousehold.add(tfHousehold);
		
		String calamities[] = {"1 Bagyo", "2 Baha", "3 Tagtuyot", "4 Lindol", "5 Volcano", "6 Landslide", "7 Tsunami", "8 Sunog", "9 Forest Fire"};
		cbCalamities = new JComboBox(calamities);
		
		JPanel panelCalamity = new JPanel();
		panelCalamity.setLayout(new BoxLayout(panelCalamity, BoxLayout.PAGE_AXIS));
		panelCalamity.setBorder(BorderFactory.createTitledBorder("Calamity Frequency"));
		panelCalamity.add(cbCalamities);
		tfFrequency = new JTextField();
		
		JPanel panelFrequency = new JPanel();
		panelFrequency.setLayout(new BoxLayout(panelFrequency, BoxLayout.LINE_AXIS));
		panelFrequency.add(new JLabel("Frequency: "));
		panelFrequency.add(tfFrequency);
		
		panelCalamity.add(panelFrequency);
		
		JPanel panelCrash = new JPanel();
		panelCrash.setLayout(new BoxLayout(panelCrash, BoxLayout.LINE_AXIS));
		toCrash = new JRadioButton("To Crash");
		notCrash = new JRadioButton("Not to Crash");
		ButtonGroup bg = new ButtonGroup();
		bg.add(toCrash);
		bg.add(notCrash);
		panelCrash.add(toCrash);
		panelCrash.add(notCrash);
		
		this.add(panelHousehold);
		this.add(panelCalamity);
		this.add(panelCrash);
		this.add(btnAdd);
		
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				WriteTransaction writeTransaction = new WriteTransaction();
				writeTransaction.setHouseholdID(tfHousehold.getText());
				writeTransaction.setCalamity(cbCalamities.getSelectedItem().toString());
				writeTransaction.setFrequency(tfFrequency.getText());
				writeTransaction.setBranchName(branchName);
				transactionsPanel.addTransaction(writeTransaction);
			}
			
		});
	}
}
