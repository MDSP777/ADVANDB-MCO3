package model;

public class ReadTransaction extends Transaction{
	private String sliceAndDiceFish;
	private String sliceAndDiceHarvest;
	private String sliceAndDiceAnimal;
	private String drillDownRollUp;
	private String database;
	private String branchName;

	public String getSliceAndDiceFish() {
		return sliceAndDiceFish;
	}

	public void setSliceAndDiceFish(String sliceAndDiceFish) {
		this.sliceAndDiceFish = sliceAndDiceFish;
	}

	public String getSliceAndDiceHarvest() {
		return sliceAndDiceHarvest;
	}

	public void setSliceAndDiceHarvest(String sliceAndDiceHarvest) {
		this.sliceAndDiceHarvest = sliceAndDiceHarvest;
	}

	public String getSliceAndDiceAnimal() {
		return sliceAndDiceAnimal;
	}

	public void setSliceAndDiceAnimal(String sliceAndDiceAnimal) {
		this.sliceAndDiceAnimal = sliceAndDiceAnimal;
	}

	public String getDrillDownRollUp() {
		return drillDownRollUp;
	}

	public void setDrillDownRollUp(String drillDownRollUp) {
		this.drillDownRollUp = drillDownRollUp;
	}
	
	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
	
	public String getStrings(String[] array) {
		String string = "";
		for (int i = 0; i < array.length; i++) {
			string += ", " + array[0];
		}
		
		string = string.replaceFirst(", ", "");
		
		return string;
	}
	
	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String toString() {
		return "Read from " + database + " : " +
				((!drillDownRollUp.isEmpty()) ? "Group By: " + drillDownRollUp : "")+ 
				((!sliceAndDiceFish.isEmpty()) ? ((!drillDownRollUp.isEmpty()) ? ", Reasons for Low Fish = " + sliceAndDiceFish : " Reasons for Low Fish = " + sliceAndDiceFish) : "") +
				((!sliceAndDiceHarvest.isEmpty()) ? ((!sliceAndDiceFish.isEmpty() || !drillDownRollUp.isEmpty()) ? ", Reasons for Low Harvest = " + sliceAndDiceHarvest : " Reasons for Low Harvest = " + sliceAndDiceHarvest) : "")+
				((!sliceAndDiceAnimal.isEmpty()) ? ((!sliceAndDiceHarvest.isEmpty() || !sliceAndDiceFish.isEmpty() || !drillDownRollUp.isEmpty()) ? ", Reasons for Few Animals = " + sliceAndDiceAnimal : " Reasons for Few Animals = " + sliceAndDiceAnimal) : "");
	}
}
