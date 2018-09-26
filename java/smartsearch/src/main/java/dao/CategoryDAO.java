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

public class CategoryDAO extends GenericDAO {
	private static final String TABLE_NAME = "categories";

	public CategoryDAO(boolean getConnection) {
		super(getConnection);
	}

	public CategoryDAO(Connection conn) {
		super(conn);
	}

	public void create(Category category) {
		PreparedStatement stmt = null;
		String sql = "SELECT * FROM func_insert_new_category(?, ?, ?, NULL)";
		Integer parentId = null;
		boolean isSubCategory = category.getParent() != null && category.getParent().getId() > 0;

		if (isSubCategory) {
			parentId = category.getParent().getId();
			sql = "SELECT * FROM func_insert_new_category(?, ? , ?, ?)";
		}

		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setString(1, category.getTitle());
			stmt.setString(2, category.getDescription());
			stmt.setInt(3, category.getLayer());

			if (isSubCategory) {
				stmt.setInt(4, parentId);
			}

			stmt.execute();

		} catch (SQLException sqlException) {
			throw new RuntimeException(sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					throw new RuntimeException(errClose);
				}
			}
		}
	}

	public ArrayList<Category> generals() {
		ArrayList<Category> categories = new ArrayList<Category>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE layer = 1";

		try {
			stmt = this.conn.prepareStatement(sql);
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
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					throw new RuntimeException(errClose);
				}
			}
		}
		return categories;
	}

	public Category findByTitle(Category category) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE title = ?";

		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setString(1, category.getTitle());
			rs = stmt.executeQuery();

			if (rs.next()) {

				category.setId(rs.getInt("id"));
				category.setTitle(rs.getString("title"));
				category.setDescription(rs.getString("description"));
				category.setLayer(rs.getInt("layer"));
				category.setLastChild(rs.getBoolean("is_last_child"));
				category.setStatus(Status.valueOf(rs.getString("status")));

				Calendar createdAt = Calendar.getInstance();
				createdAt.setTime(rs.getTimestamp("created_at"));
				category.setCreatedAt(createdAt);
			} else {
				category = null;
			}

		} catch (Exception sqlException) {
			throw new RuntimeException(sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					throw new RuntimeException(errClose);
				}
			}
		}

		return category;
	}
	
	public Category findParentByChildId(Category category) {
		Category parentCategory = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM categories WHERE id = (SELECT parent_id FROM categories WHERE id = ?)";
		
		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, category.getId());
			rs = stmt.executeQuery();

			if (rs.next()) {
				parentCategory = new Category();
				
				parentCategory.setId(rs.getInt("id"));
				parentCategory.setTitle(rs.getString("title"));
				parentCategory.setDescription(rs.getString("description"));
				parentCategory.setLayer(rs.getInt("layer"));
				parentCategory.setLastChild(rs.getBoolean("is_last_child"));
				parentCategory.setStatus(Status.valueOf(rs.getString("status")));

				Calendar createdAt = Calendar.getInstance();
				createdAt.setTime(rs.getTimestamp("created_at"));
				parentCategory.setCreatedAt(createdAt);
			}

		} catch (Exception sqlException) {
			throw new RuntimeException(sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					throw new RuntimeException(errClose);
				}
			}
		}

		return parentCategory;
	}

	public void saveDetails(Category category) {
		PreparedStatement stmt = null;
		String sql = "UPDATE " + TABLE_NAME + " SET title = ?, description = ? WHERE id = ?";

		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setString(1, category.getTitle());
			stmt.setString(2, category.getDescription());
			stmt.setInt(3, category.getId());
			stmt.executeUpdate();
		} catch (Exception sqlException) {
			throw new RuntimeException(sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					throw new RuntimeException(errClose);
				}
			}
		}
	}

	public ArrayList<Category> subcategoriesByParent(Integer parentId) {
		ArrayList<Category> subcategories = new ArrayList<Category>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE parent_id = ?";

		try {
			stmt = this.conn.prepareStatement(sql);
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
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					throw new RuntimeException(errClose);
				}
			}
		}

		return subcategories;
	}

	public void updateStatus(Category category) {
		PreparedStatement stmt = null;
		String sql = "SELECT * FROM func_toggle_status_categories(?, ?::status_entity)";

		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, category.getId());
			stmt.setString(2, category.getStatus().toString());
			stmt.execute();
		} catch (Exception sqlException) {
			throw new RuntimeException(sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					throw new RuntimeException(errClose);
				}
			}
		}
	}

	public void destroy(Category category) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, category.getId());
			stmt.execute();
		} catch (Exception sqlException) {
			throw new RuntimeException(sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					throw new RuntimeException(errClose);
				}
			}
		}
	}
}
