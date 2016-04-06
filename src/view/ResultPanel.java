package view;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import model.Entity;

public class ResultPanel extends JPanel{
	
	private JTable result;
	private JPanel tablePane;
	private JScrollPane scrollPane;
	private Object[][] resultSet;
	
	public ResultPanel() {
		tablePane = new JPanel();
		tablePane.setBorder(BorderFactory.createTitledBorder("Result"));
		this.add(tablePane);
	}
	
	public void buildTableModel(Object[][] resultSet) {
		this.resultSet = resultSet;
		this.remove(tablePane);
		result = new JTable(new DefaultTableModel(getRows(), getHeaders()));
		scrollPane = new JScrollPane(result);
		tablePane.add(scrollPane);
		this.revalidate();
		this.repaint();
	}
	
	public Object[] getHeaders() {
		return Entity.HEADERS;
	}
	
	public Object[][] getRows() {
		Object[][] data = new Object[resultSet.length-1][resultSet[0].length];
		
		for (int i = 1; i < resultSet.length; i++) {
			for (int j = 0; j < resultSet[i].length; j++){
				data[i-1][j] = resultSet[i][j];
			}
		}
		
		return data;
	}
}
