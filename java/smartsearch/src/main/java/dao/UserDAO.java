package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import database.ConnectionFactory;
import enums.Status;
import enums.UserRoles;
import models.User;

public class UserDAO {
	public User create(User user) {
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = "INSERT INTO users (email, username, display_name, "
		+ "password, role, status, created_at) VALUES (?, ?, ?, "
		+ "?, CAST(? AS users_role), CAST(? AS status_entity), NOW())";
				
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
	
	public User checkIfExists(User user) {
		User loggedUser = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM users WHERE users.email = ?";
		
		try {
			
			conn = ConnectionFactory.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, user.getEmail());
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				loggedUser = new User();
				loggedUser.setUsername(rs.getString("username"));
				loggedUser.setEmail(rs.getString("email"));
				loggedUser.setPassword(rs.getString("password"));
				loggedUser.setDisplayName(rs.getString("display_name"));
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
	
	public ArrayList<User> report(int page, int perPage) {
		User user;
		ArrayList<User> users = new ArrayList<User>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT display_name, email, created_at FROM users OFFSET ? LIMIT ?";
		
		int offset = (page - 1) * perPage;
		try {
			
			conn = ConnectionFactory.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, offset);
			stmt.setInt(2, perPage);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				user = new User();
				user.setDisplayName(rs.getString("display_name"));
				user.setEmail(rs.getString("email"));
				user.setCreatedAt(rs.getTimestamp("created_at"));
				
				users.add(user);
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

		return users;
	}
	
	
}
