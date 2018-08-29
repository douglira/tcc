package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import database.ConnectionFactory;
import enums.Status;
import enums.UserRoles;
import models.User;

public class UserDAO {
	public User create(User user) {
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = "INSERT INTO users (email, username, \"displayName\", "
		+ "password, role, status, \"createdAt\") VALUES (?, ?, ?, "
		+ "?, CAST(? AS users_role), CAST(? AS users_status), NOW())";
				
		try {
			conn = ConnectionFactory.getConnection();
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, user.getEmail());
			stmt.setString(2, user.getUsername());
			stmt.setString(3, user.getDisplayName());
			stmt.setString(4, user.getPassword());
			stmt.setString(5, user.getRole().toString());
			stmt.setString(6, user.getStatus().toString());
			stmt.execute();
			
			ResultSet rs = stmt.getGeneratedKeys();
			
			if (rs.next()) {
				user.setId(rs.getInt(1));
			}
			
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
		
		return user;
	}
	
	public User authenticate(User user) {
		User loggedUser = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM users WHERE users.email = ? AND password = ?";
		
		try {
			
			conn = ConnectionFactory.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, user.getEmail());
			stmt.setString(2, user.getPassword());
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				loggedUser = new User();
				loggedUser.setUsername(rs.getString("username"));
				loggedUser.setEmail(rs.getString("email"));
				loggedUser.setDisplayName(rs.getString("displayName"));
				loggedUser.setRole(UserRoles.valueOf(rs.getString("role")));
				loggedUser.setStatus(Status.valueOf(rs.getString("status")));
			}
			
			
		} catch (SQLException sqlException) {
			throw new RuntimeException(sqlException);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException errClose) {
					throw new RuntimeException(errClose);
				}
			}
		}
		
		return loggedUser;
	}
}
