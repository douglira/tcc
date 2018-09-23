package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.ConnectionFactory;
import models.Person;

public class PersonDAO extends GenericDAO {
	private static final String TABLE_NAME = "people";

	public PersonDAO(boolean getConnection) {
		super(getConnection);
	}

	public PersonDAO(Connection conn, boolean setTransaction) {
		super(conn, setTransaction);
	}

	public void create(Person person) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO " + TABLE_NAME + " (account_owner, tel, cnpj, corporate_name, "
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

	public void update(Person person) {
		PreparedStatement stmt = null;
		String sql = "UPDATE " + TABLE_NAME + " SET account_owner = ?, tel = ?, cnpj = ?, "
				+ "corporate_name = ?, state_registration = ?, updated_at = NOW() WHERE id = ?";
		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setString(1, person.getAccountOwner());
			stmt.setLong(2, person.getTel());
			stmt.setLong(3, person.getCnpj());
			stmt.setString(4, person.getCorporateName());
			stmt.setLong(5, person.getStateRegistration());
			stmt.setInt(6, person.getId());
			stmt.execute();

		} catch (SQLException e) {
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

	public Person findByUser(Person person) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE user_id = ?";

		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, person.getUser().getId());
			rs = stmt.executeQuery();

			if (rs.next()) {
				person.setId(rs.getInt("id"));
				person.setAccountOwner(rs.getString("account_owner"));
				person.setTel(rs.getLong("tel"));
				person.setCnpj(rs.getLong("cnpj"));
				person.setCorporateName(rs.getString("corporate_name"));
				person.setStateRegistration(rs.getLong("state_registration"));
			}
		} catch (Exception error) {
			try {
				this.conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(error);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException sqlException) {
					throw new RuntimeException(sqlException);
				}
			}
		}

		return person;
	}
}
