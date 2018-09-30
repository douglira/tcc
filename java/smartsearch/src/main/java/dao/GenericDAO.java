package dao;

import java.sql.Connection;
import java.sql.SQLException;

import database.ConnectionFactory;

public abstract class GenericDAO {
	protected Connection conn;
	
	protected GenericDAO(boolean getConnection) {
		if (getConnection) {
			try {
				this.conn = ConnectionFactory.getConnection();
			} catch(SQLException errorSql) {
				throw new RuntimeException(errorSql);
			}
		}
	}

	protected GenericDAO(Connection conn) {
		this.conn = conn;
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	public Connection getConnection() {
		return this.conn;
	}

	public void initTransaction() throws SQLException{
		this.conn.setAutoCommit(false);
	}

	public void closeTransaction() throws SQLException{
		this.conn.setAutoCommit(true);
	}

	public void closeConnection() throws SQLException {
		this.conn.close();
	}
}
