package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import model.ReadTransaction;

public class ReadPanel extends JPanel{
	
	private RollupAndDrilldownPanel rollupAndDrilldownPanel;
	private SliceAndDicePanel sliceAndDicePanel;
	private TransactionsPanel transactionsPanel;
	private JButton btnAdd;
	private JComboBox cbDatabases;
	private String branchName;
	
	public ReadPanel(TransactionsPanel transactionsPanel, String branchName) {
		this.transactionsPanel = transactionsPanel;
		
		rollupAndDrilldownPanel = new RollupAndDrilldownPanel();
		sliceAndDicePanel 		= new SliceAndDicePanel();
		btnAdd 					= new JButton("Add");
		cbDatabases = new JComboBox(new String[]{"Palawan", "Marinduque", "Central"});
		this.branchName = branchName;
		
		rollupAndDrilldownPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		sliceAndDicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
		cbDatabases.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Read"));
		this.add(cbDatabases);
		this.add(sliceAndDicePanel);
		this.add(btnAdd);
		this.add(Box.createRigidArea(new Dimension(0, 150)));
		this.add(Box.createGlue());
		
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				ReadTransaction readTransaction = new ReadTransaction();
				readTransaction.setDatabase(cbDatabases.getSelectedItem().toString());
				readTransaction.setDrillDownRollUp(rollupAndDrilldownPanel.getRollUpAndDrillDown());
				readTransaction.setSliceAndDiceFish(sliceAndDicePanel.getReasonsLowFish());
				readTransaction.setSliceAndDiceHarvest(sliceAndDicePanel.getReasonsLowHarvest());
				readTransaction.setSliceAndDiceAnimal(sliceAndDicePanel.getReasonsLowLive());
				readTransaction.setBranchName(branchName);
				transactionsPanel.addTransaction(readTransaction);
			}
			
		});
	}
}
