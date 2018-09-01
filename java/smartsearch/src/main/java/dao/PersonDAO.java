package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.ConnectionFactory;
import models.Person;

public class PersonDAO {
	public void create(Person person) {
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = "INSERT INTO people (account_owner, tel, cnpj, corporate_name, "
				+ "state_registration, user_id, created_at)"
				+ "VALUES (?, ?, ?, ?, ?, ?, NOW())";
		try {
			conn = ConnectionFactory.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, person.getAccountOwner());
			stmt.setLong(2, person.getTel());
			stmt.setLong(3, person.getCnpj());
			stmt.setString(4, person.getCorporateName());
			stmt.setLong(5, person.getStateRegistration());
			stmt.setInt(6, person.getUser().getId());
			stmt.execute();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqlException) {
					throw new RuntimeException(sqlException);
				}
			}
		}
	}
}
