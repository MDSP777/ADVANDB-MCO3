package model;

public class Entity {
	public static final int COLUMN_COUNT = 15;
	public static final String[] HEADERS = { "id", "prov", "mun", "zone", "brgy", "purok", "bagyo", "baha", "tagtuyot",
			"lindol", "volcano", "landslide", "tsunami", "sunog", "forestfire" };
	private int id;
	private int prov;
	private int mun;
	private int zone;
	private int brgy;
	private int purok;
	private int calam1_hwmny;
	private int calam2_hwmny;
	private int calam3_hwmny;
	private int calam4_hwmny;
	private int calam5_hwmny;
	private int calam6_hwmny;
	private int calam7_hwmny;
	private int calam8_hwmny;
	private int calam9_hwmny;

	public Entity(int id, int prov, int mun, int zone, int brgy, int purok, int calam1_hwmny, int calam2_hwmny, int calam3_hwmny,
			int calam4_hwmny, int calam5_hwmny, int calam6_hwmny, int calam7_hwmny, int calam8_hwmny,
			int calam9_hwmny) {
		super();
		this.id = id;
		this.prov = prov;
		this.mun = mun;
		this.zone = zone;
		this.brgy = brgy;
		this.purok = purok;
		this.calam1_hwmny = calam1_hwmny;
		this.calam2_hwmny = calam2_hwmny;
		this.calam3_hwmny = calam3_hwmny;
		this.calam4_hwmny = calam4_hwmny;
		this.calam5_hwmny = calam5_hwmny;
		this.calam6_hwmny = calam6_hwmny;
		this.calam7_hwmny = calam7_hwmny;
		this.calam8_hwmny = calam8_hwmny;
		this.calam9_hwmny = calam9_hwmny;
	}

	public Entity(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMun() {
		return mun;
	}

	public void setMun(int mun) {
		this.mun = mun;
	}

	public int getZone() {
		return zone;
	}

	public void setZone(int zone) {
		this.zone = zone;
	}

	public int getBrgy() {
		return brgy;
	}

	public void setBrgy(int brgy) {
		this.brgy = brgy;
	}

	public int getPurok() {
		return purok;
	}

	public void setPurok(int purok) {
		this.purok = purok;
	}

	public int getCalam1_hwmny() {
		return calam1_hwmny;
	}

	public void setCalam1_hwmny(int calam1_hwmny) {
		this.calam1_hwmny = calam1_hwmny;
	}

	public int getCalam2_hwmny() {
		return calam2_hwmny;
	}

	public void setCalam2_hwmny(int calam2_hwmny) {
		this.calam2_hwmny = calam2_hwmny;
	}

	public int getCalam3_hwmny() {
		return calam3_hwmny;
	}

	public void setCalam3_hwmny(int calam3_hwmny) {
		this.calam3_hwmny = calam3_hwmny;
	}

	public int getCalam4_hwmny() {
		return calam4_hwmny;
	}

	public void setCalam4_hwmny(int calam4_hwmny) {
		this.calam4_hwmny = calam4_hwmny;
	}

	public int getCalam5_hwmny() {
		return calam5_hwmny;
	}

	public void setCalam5_hwmny(int calam5_hwmny) {
		this.calam5_hwmny = calam5_hwmny;
	}

	public int getCalam6_hwmny() {
		return calam6_hwmny;
	}

	public void setCalam6_hwmny(int calam6_hwmny) {
		this.calam6_hwmny = calam6_hwmny;
	}

	public int getCalam7_hwmny() {
		return calam7_hwmny;
	}

	public void setCalam7_hwmny(int calam7_hwmny) {
		this.calam7_hwmny = calam7_hwmny;
	}

	public int getCalam8_hwmny() {
		return calam8_hwmny;
	}

	public void setCalam8_hwmny(int calam8_hwmny) {
		this.calam8_hwmny = calam8_hwmny;
	}

	public int getCalam9_hwmny() {
		return calam9_hwmny;
	}

	public void setCalam9_hwmny(int calam9_hwmny) {
		this.calam9_hwmny = calam9_hwmny;
	}

	@Override
	public String toString() {
		return "Entity [id=" + id + ", prov=" + prov + ", mun=" + mun + ", zone=" + zone + ", brgy=" + brgy + ", purok=" + purok
				+ ", calam1_hwmny=" + calam1_hwmny + ", calam2_hwmny=" + calam2_hwmny + ", calam3_hwmny=" + calam3_hwmny
				+ ", calam4_hwmny=" + calam4_hwmny + ", calam5_hwmny=" + calam5_hwmny + ", calam6_hwmny=" + calam6_hwmny
				+ ", calam7_hwmny=" + calam7_hwmny + ", calam8_hwmny=" + calam8_hwmny + ", calam9_hwmny=" + calam9_hwmny
				+ "]";
	}

	public Object[] toArray() {
		return new Object[] { id, prov, mun, zone, brgy, purok, calam1_hwmny, calam2_hwmny, calam3_hwmny, calam4_hwmny,
				calam5_hwmny, calam6_hwmny, calam7_hwmny, calam8_hwmny, calam9_hwmny };
	}
}
