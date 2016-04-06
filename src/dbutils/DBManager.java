package dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
	private String driverName;
	private String url;
	private String dbName;
	private String username;
	private String password;
	
	public DBManager(String dbName, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		this.driverName = "com.mysql.jdbc.DriverManager"; //com.mysql.jdbc.DriverManager
		this.url = "jdbc:mysql://127.0.0.1:3306/"; //jdbc.mysql://127.0.0.1:3306/
		this.dbName = dbName; //db_hpq
		this.username = "root"; //root
		this.password = password;
	}
	
	public DBManager(String dbName) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		this.driverName = "com.mysql.jdbc.DriverManager"; //com.mysql.jdbc.DriverManager
		this.url = "jdbc:mysql://127.0.0.1:3306/"; //jdbc.mysql://127.0.0.1:3306/
		this.dbName = dbName; //db_hpq
		this.username = "root"; //root
		this.password = "IforgoT197!!";
	}

	public Connection getConnection() {
		try {
			return DriverManager.getConnection(url + dbName,username,password);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getDriverName() {
		return driverName;
	}

	public String getUrl() {
		return url;
	}

	public String getDbName() {
		return dbName;
	}

	public String getUsername() {
		return username;
	}
}
