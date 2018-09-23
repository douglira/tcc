package dao;

import java.sql.Connection;
import java.sql.SQLException;

import database.ConnectionFactory;

public abstract class GenericDAO {
	protected Connection conn;
	
	protected GenericDAO(boolean getConnection) {
		if (getConnection) {
			this.conn = ConnectionFactory.getConnection();
		}
	}

	protected GenericDAO(Connection conn, boolean setTransaction) {
		this.conn = conn;

		if (setTransaction) {
			this.initTransaction();
		}
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	public Connection getConnection() {
		return this.conn;
	}

	public void initTransaction() {
		try {
			this.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void closeTransaction() {
		try {
			this.conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
