package view;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SliceAndDicePanel extends JPanel{
	
	private JPanel panelHarvest;
	private JPanel panelFish;
	private JPanel panelLive;
	
	private JCheckBox cbReasonsLowHarvest[];
	private JCheckBox cbReasonsLowFish[];
	private JCheckBox cbReasonsLowLive[];
	
	public SliceAndDicePanel() {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Slice and Dice"));
		
		/*******************************HARVEST PANEL************************************/
		panelHarvest	= new JPanel();
		panelHarvest.setLayout(new BoxLayout(panelHarvest, BoxLayout.PAGE_AXIS));
		panelHarvest.add(new JLabel("Reasons for low harvest"));
		String[] reasonsLowHarvest = {"2 Affected by drought", "3 Affected by typhoon", "4 Affected by flood"};
		cbReasonsLowHarvest	= new JCheckBox[reasonsLowHarvest.length];
		for(int i = 0; i < cbReasonsLowHarvest.length; i++) {
			cbReasonsLowHarvest[i]	= new JCheckBox(reasonsLowHarvest[i]);
			panelHarvest.add(cbReasonsLowHarvest[i]);
		}
		/********************************************************************************/
		
		/*********************************FISH PANEL*************************************/
		panelFish	= new JPanel();
		panelFish.setLayout(new BoxLayout(panelFish, BoxLayout.PAGE_AXIS));
		panelFish.add(new JLabel("Reasons for low fish"));
		String[] reasonsLowFish = {"3 Fewer fishes", "8 Frequent occurrence of typhoons"};
		cbReasonsLowFish		= new JCheckBox[reasonsLowFish.length];
		for(int i = 0; i < cbReasonsLowFish.length; i++) {
			cbReasonsLowFish[i]	= new JCheckBox(reasonsLowFish[i]);
			panelFish.add(cbReasonsLowFish[i]);
		}
		/******************************************************************************/
		
		/********************************LIVE PANEL************************************/
		panelLive	= new JPanel();
		panelLive.setLayout(new BoxLayout(panelLive, BoxLayout.PAGE_AXIS));
		panelLive.add(new JLabel("Reasons for few animals"));
		String[] reasonsLowLive = {"4 Affected by typhoon", "5 Affected by flood", "6 Affected by extreme hot weather condition"};
		cbReasonsLowLive		= new JCheckBox[reasonsLowLive.length];
		for(int i = 0; i < cbReasonsLowLive.length; i++) {
			cbReasonsLowLive[i]	= new JCheckBox(reasonsLowLive[i]);
			panelLive.add(cbReasonsLowLive[i]);
		}
		/******************************************************************************/
		this.add(panelFish);
		this.add(panelHarvest);
		this.add(panelLive);
	}
	
	@Override
	public Dimension getMaximumSize() {
		Dimension d = super.getMaximumSize();
        d.width = Integer.MAX_VALUE;
        return d;
	}
	
	private String[] getReasons(JCheckBox[] cbReasons) {
		ArrayList<String> reasonList = new ArrayList<String>();
		for(JCheckBox cb : cbReasons) {
			if(cb.isSelected()) {
				reasonList.add(cb.getText());
				System.out.println(cb.getText());
			}
		}
		String reasons[] = new String[reasonList.size()];
		for(int i = 0; i < reasonList.size(); i++) {
			reasons[i] = reasonList.get(i);
		}
		return reasons;
	}
	
	public String getReasonsLowHarvest() {
		String reasonsLowHarvetString = "";
		String[] reasons = getReasons(cbReasonsLowHarvest);
		
		for (int i = 0; i < reasons.length; i++)
			reasonsLowHarvetString += "," + reasons[i];
		
		reasonsLowHarvetString = reasonsLowHarvetString.replaceFirst(",", "");
		
		return reasonsLowHarvetString;
	}
	
	public String getReasonsLowFish() {
		String reasonsLowFishString = "";
		String[] reasons = getReasons(cbReasonsLowFish);
		
		for (int i = 0; i < reasons.length; i++)
			reasonsLowFishString += "," + reasons[i];
		
		reasonsLowFishString = reasonsLowFishString.replaceFirst(",", "");
		
		return reasonsLowFishString;
	}
	
	public String getReasonsLowLive(){
		String reasonsLowLiveString = "";
		String[] reasons = getReasons(cbReasonsLowLive);
		
		for (int i = 0; i < reasons.length; i++)
			reasonsLowLiveString += "," + reasons[i];
		
		reasonsLowLiveString = reasonsLowLiveString.replaceFirst(",", "");
		
		return reasonsLowLiveString;
	}
 }
