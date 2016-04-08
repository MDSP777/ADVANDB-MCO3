package view;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import model.Entity;

public class ResultPanel extends JPanel{
	
	private JTable result;
	private Object[][] resultSet;
	
	public ResultPanel() {
		this.setLayout(null);
		this.setBorder(BorderFactory.createTitledBorder("Result"));
	}
	
	public void buildTableModel(Object[][] resultSet) {
		System.out.println("ANO BA YAN");
		if (resultSet != null) {
			
			this.resultSet = resultSet;
			result = new JTable(new DefaultTableModel(getRows(), getHeaders()));
			this.removeAll();
			JScrollPane scrollPane = new JScrollPane(result);
			
			scrollPane.setSize(850, 480);
			scrollPane.setLocation(10, 20);
			
			JLabel lblRows = new JLabel("Number of rows: " + resultSet.length);
			lblRows.setSize(850, 20);
			lblRows.setLocation(10, 500);
			
			this.add(scrollPane);
			this.add(lblRows);
			System.out.println("Num of rows: " + resultSet.length);
		} else {
			this.removeAll();
			JLabel lblResult = new JLabel("Unable to retrieve data.");
			lblResult.setLocation(380, 250);
			
			lblResult.setSize(300, 30);
			this.add(lblResult);
		}
		
		this.revalidate();
		this.repaint();
	}
	
	public void showWriteMessage(String message) {
		this.removeAll();
		JLabel lblResult = new JLabel(message);
		lblResult.setLocation(380, 250);
		
		lblResult.setSize(300, 30);
		this.add(lblResult);
	}
	
	public Object[] getHeaders() {
		return Entity.HEADERS;
	}
	
	public Object[][] getRows() {
		Object[][] data = new Object[resultSet.length][Entity.COLUMN_COUNT];
		
		for (int i = 0; i < resultSet.length; i++) {
			for (int j = 0; j < resultSet[i].length; j++){
				
				data[i][j] = resultSet[i][j];
			}
		}
		
		return data;
	}

	public void hidePanel() {
		// TODO Auto-generated method stub
		this.removeAll();
	}
}
