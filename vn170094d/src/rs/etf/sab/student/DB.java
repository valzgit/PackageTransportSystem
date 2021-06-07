package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
	private static final String username = "sa";
	private static final String password = "123";
	private static final String database = "vn170094";
	private static final int port = 1433;
	private static final String server = "localhost";

	private static final String connectionUrl = "jdbc:sqlserver://" + server + ":" + port + ";databaseName=" + database;
	
	private Connection connection;
	private DB() {
		try {
			connection = DriverManager.getConnection(connectionUrl,username,password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return connection;
	}

	private static DB db = null;

	public static DB getInstance() {
		if (db == null)
			db = new DB();
		return db;
	}
}
