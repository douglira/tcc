package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	public static Connection getConnection() {
		Connection conn= null;
		try {
			Class.forName("org.postgresql.Driver");
			
			conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/smartsearch", "douglas", "docker");
		} catch (ClassNotFoundException erro1) {
			throw new RuntimeException(erro1);
		} catch (SQLException erro2) {
			throw new RuntimeException(erro2);
		}
		return conn;
	}
}
