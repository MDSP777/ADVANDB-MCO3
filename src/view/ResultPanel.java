package view;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ResultPanel extends JPanel{
	
	private JTable result;
	private Object[][] resultSet;
	
	public ResultPanel() {
		this.setBorder(BorderFactory.createTitledBorder("Result"));
	}
	
	public void buildTableModel(Object[][] resultSet) {
		this.resultSet = resultSet;
		result = new JTable(new DefaultTableModel(getRows(), getHeaders()));
		this.add(new JScrollPane(result));
	}
	
	public Object[] getHeaders() {
		Object[] headers = new Object[resultSet[0].length];
		
		for (int j = 0; j < resultSet[0].length; j++)
			headers[j] = resultSet[0][j];
		
		return headers;
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
