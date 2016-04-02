package model;

import java.io.Serializable;
import java.sql.ResultSet;

public class ResultSetWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	private ResultSet rs;

	public ResultSetWrapper(ResultSet rs) {
		super();
		this.rs = rs;
	}

	public ResultSet getRs() {
		return rs;
	}
}
