package view;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RollupAndDrilldownPanel extends JPanel{

	private JCheckBox cbMun;
	private JCheckBox cbZone;
	private JCheckBox cbBrgy;
	private JCheckBox cbPurok;
	
	public RollupAndDrilldownPanel(){
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Rollup and Drilldown"));
		
		cbMun 		= new JCheckBox("Municipality");
		cbZone 		= new JCheckBox("Zone");
		cbBrgy 		= new JCheckBox("Barangay");
		cbPurok 	= new JCheckBox("Purok");
		
		this.add(cbMun);
		this.add(cbZone);
		this.add(cbBrgy);
		this.add(cbPurok);
	}
	
	public String getRollUpAndDrillDown() {
		String checkBoxes = "";
		
		if (cbMun.isSelected())
			checkBoxes += ",mun";
		if (cbZone.isSelected())
			checkBoxes += ",zone";
		if (cbBrgy.isSelected())
			checkBoxes += ",brgy";
		if (cbPurok.isSelected())
			checkBoxes += ",purok";
		
		checkBoxes = checkBoxes.replaceFirst(",", "");
		
		return checkBoxes;
	}
	
	@Override
	public Dimension getMaximumSize() {
		Dimension d = super.getMaximumSize();
        d.width = Integer.MAX_VALUE;
        return d;
	}
}
