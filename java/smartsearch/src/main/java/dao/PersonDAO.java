package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.ConnectionFactory;
import models.LegalPerson;

public class PersonDAO {
	public void create(LegalPerson person) {
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = "INSERT INTO people (name, tel, cnpj, \"corporateName\", "
				+ "\"stateRegistration\", persontype, \"userId\", \"createdAt\" )"
				+ "VALUES (?, ?, ?, ?, ?, CAST(? AS people_type), ?, NOW())";
		try {
			conn = ConnectionFactory.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, person.getName());
			stmt.setLong(2, person.getTel());
			stmt.setLong(3, person.getCnpj());
			stmt.setString(4, person.getCorporateName());
			stmt.setLong(5, person.getStateRegistration());
			stmt.setString(6, person.getPersonType().toString());
			stmt.setInt(7, person.getUser().getId());
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
