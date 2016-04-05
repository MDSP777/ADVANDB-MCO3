package model;

public class WriteTransaction extends Transaction{
	private String householdID;
	private String calamity;
	private String frequency;
	private String branchName;
	
	public String getHouseholdID() {
		return householdID;
	}
	
	public void setHouseholdID(String householdID) {
		this.householdID = householdID;
	}
	
	public String getCalamity() {
		return calamity;
	}
	
	public void setCalamity(String calamity) {
		this.calamity = calamity;
	}
	
	public String getFrequency() {
		return frequency;
	}
	
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String toString() {
		return "Write: Household ID = " + householdID + ", Calamity = " + calamity + ", Frequency = " + frequency;
	}
	
}
