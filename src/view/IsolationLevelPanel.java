package view;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class IsolationLevelPanel extends JPanel {

	private final String[] isolationLevels = { "Read Uncommitted", "Read Committed", "Read Repeatable",
			"Serializable" };
	private JComboBox cbIsolationLevel;
	
	public IsolationLevelPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Isolation Level"));
		cbIsolationLevel = new JComboBox(isolationLevels);
		this.add(cbIsolationLevel);
		this.setPreferredSize(new Dimension(200, 100));
	}
	
	@Override
	public Dimension getMaximumSize() {
		Dimension d = super.getMaximumSize();
        d.width = Integer.MAX_VALUE;
        return d;
	}
}
