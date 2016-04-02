package model;

public class QueryGenerator {

	public static String generate(Transaction transaction) {
		if(transaction instanceof ReadTransaction) {
			return generate((ReadTransaction) transaction);
		}else if(transaction instanceof WriteTransaction) {
			return generate((WriteTransaction) transaction);
		}
		
		return "";
	}
	
	private static String generate(ReadTransaction transaction) {
		String query = transaction.getBranchName()+"@SELECT COUNT(*) "
				+ "FROM (SELECT CP.hpq_hh_id, SUM(CP.crop_vol) AS crop_vol "
						+ "FROM db_hpq.hpq_crop CP GROUP BY CP.hpq_hh_id) Crop, "
						+ "(SELECT AQ.hpq_hh_id, SUM(AQ.aquani_vol) AS aquani_vol "
						+ "FROM db_hpq.hpq_aquani AQ GROUP BY AQ.hpq_hh_id) Fish, "
						+ "db_hpq.hpq_hh H "
				+ "WHERE Crop.hpq_hh_id = H.id "
						+ "AND Fish.hpq_hh_id = H.id ";
		
		String harvest = "AND (";
		for(String h : transaction.getSliceAndDiceHarvest().toString().split(",")) {
			harvest += "OR H.u_low_harv = " + h.split(" ")[0].trim();
		}
		harvest = harvest.replaceFirst("OR", "").trim();
		harvest += ") ";
		
		String fish = "AND (";
		for(String f : transaction.getSliceAndDiceFish().toString().split(",")) {
			fish += "OR H.u_low_fish = " + f.split(" ")[0].trim();
		}
		fish = fish.replaceFirst("OR", "").trim();
		fish += ") ";
		
		String animal = "AND (";
		for(String a : transaction.getSliceAndDiceAnimal().toString().split(",")) {
			animal += "OR H.u_low_lve = " + a.split(" ")[0].trim();
		}
		animal = animal.replaceFirst("OR", "").trim();
		animal += ") ";
		
		if(!transaction.getSliceAndDiceHarvest().toString().isEmpty()) {
			query += harvest;
		}
		if(!transaction.getSliceAndDiceFish().toString().isEmpty()) {
			query += fish;
		}
		if(!transaction.getSliceAndDiceAnimal().toString().isEmpty()) {
			query += animal;
		}
		
		return query+"@"+transaction.getDatabase();
	}
	
	private static String generate(WriteTransaction transaction) {
		String query = "UPDATE hpq_hh "
				+ "SET calam" + transaction.getCalamity().split(" ")[0] + "hwmny=" + transaction.getFrequency()
				+ " WHERE id=" + transaction.getHouseholdID() + " LIMIT 1;";
		return query;
	}
}
