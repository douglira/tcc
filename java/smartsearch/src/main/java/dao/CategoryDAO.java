package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import database.ConnectionFactory;
import enums.Status;
import models.Category;

public class CategoryDAO {

	public ArrayList<Category> generals() {
		ArrayList<Category> categories = new ArrayList<Category>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM categories WHERE layer = 1";
		
		
		try {
			conn = ConnectionFactory.getConnection();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				Category category = new Category();
				
				category.setId(rs.getInt("id"));
				category.setTitle(rs.getString("title"));
				category.setDescription(rs.getString("description"));
				category.setLayer(rs.getInt("layer"));
				category.setLastChild(rs.getBoolean("is_last_child"));
				category.setStatus(Status.valueOf(rs.getString("status")));
				
				Calendar createdAt = Calendar.getInstance();
				createdAt.setTime(rs.getTimestamp("created_at"));
				category.setCreatedAt(createdAt);
				
				categories.add(category);
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
		return categories;
	}
	
	public ArrayList<Category> subcategoriesByParent(Integer parentId) {
		ArrayList<Category> subcategories = new ArrayList<Category>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM categories WHERE parent_id = ?";
		
		try {
			conn = ConnectionFactory.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, parentId);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				Category subcategory = new Category();
				
				subcategory.setId(rs.getInt("id"));
				subcategory.setTitle(rs.getString("title"));
				subcategory.setDescription(rs.getString("description"));
				subcategory.setLayer(rs.getInt("layer"));
				subcategory.setLastChild(rs.getBoolean("is_last_child"));
				subcategory.setStatus(Status.valueOf(rs.getString("status")));
				
				Calendar createdAt = Calendar.getInstance();
				createdAt.setTime(rs.getTimestamp("created_at"));
				subcategory.setCreatedAt(createdAt);
				
				subcategories.add(subcategory);
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
		
		return subcategories;
	}
	
	public ArrayList<Category> subcategoriesByLayer(Integer layer) {
		ArrayList<Category> categories = new ArrayList<Category>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM categories WHERE layer = ?";
		
		try {
			conn = ConnectionFactory.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, layer);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				Category category = new Category();
				
				category.setId(rs.getInt("id"));
				category.setTitle(rs.getString("title"));
				category.setDescription(rs.getString("description"));
				category.setLayer(rs.getInt("layer"));
				category.setLastChild(rs.getBoolean("is_last_child"));
				category.setStatus(Status.valueOf(rs.getString("status")));
				
				Calendar createdAt = Calendar.getInstance();
				createdAt.setTime(rs.getTimestamp("created_at"));
				category.setCreatedAt(createdAt);
				
				categories.add(category);
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
		
		return categories;
	}
}
