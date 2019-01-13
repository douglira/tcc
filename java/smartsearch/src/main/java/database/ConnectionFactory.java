package database;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


public class ConnectionFactory {
	private static HikariConfig config = new HikariConfig();
	private static HikariDataSource ds;
	
	static {
		config.setDriverClassName("org.postgresql.Driver");
		config.setJdbcUrl("jdbc:postgresql://127.0.0.1/smartsearch");
		config.setUsername("douglas");
		config.setPassword("docker");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("preStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.addDataSourceProperty("maximumPoolSize", "20");
		ds = new HikariDataSource(config);
	}

//	public static Connection getConnection() {
//		Connection conn= null;
//		try {
//			Class.forName("org.postgresql.Driver");
//			conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/smartsearch", "douglas", "docker");
//		} catch (ClassNotFoundException erro1) {
//			throw new RuntimeException(erro1);
//		} catch (SQLException erro2) {
//			throw new RuntimeException(erro2);
//		}
//		return conn;
//	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
}
