package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.ConnectionFactory;
import models.Person;

public class PersonDAO {
	private Connection conn;

	public PersonDAO(boolean getConnection) {
		if (getConnection) {
			this.conn = ConnectionFactory.getConnection();
		}
	}

	public PersonDAO(Connection conn, boolean setTransaction) {
		this.conn = conn;

		if (setTransaction) {
			this.setTransaction();			
		}
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	public Connection getConnection() {
		return this.conn;
	}

	public void setTransaction() {
		try {
			this.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void create(Person person) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO people (account_owner, tel, cnpj, corporate_name, "
				+ "state_registration, user_id, created_at)" + "VALUES (?, ?, ?, ?, ?, ?, NOW())";
		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setString(1, person.getAccountOwner());
			stmt.setLong(2, person.getTel());
			stmt.setLong(3, person.getCnpj());
			stmt.setString(4, person.getCorporateName());
			stmt.setLong(5, person.getStateRegistration());
			stmt.setInt(6, person.getUser().getId());
			stmt.execute();
			this.conn.commit();

		} catch (SQLException e) {
			try {
				this.conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException sqlException) {
					throw new RuntimeException(sqlException);
				}
			}
		}
	}
}
