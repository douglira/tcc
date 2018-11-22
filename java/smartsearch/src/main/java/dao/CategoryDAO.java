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
			sqlException.printStackTrace();
			System.out.println("CategoryDAO.create [ERROR](1): " + sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					errClose.printStackTrace();
					System.out.println("CategoryDAO.create [ERROR](2): " + errClose);
				}
			}
		}
	}

	private ArrayList<Category> fetch(ResultSet rs) throws SQLException {
		ArrayList<Category> categories = new ArrayList<Category>();

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

			categories.add(subcategory);
		}

		return categories;
	}

	public ArrayList<Category> restrictGenerals() {
		ArrayList<Category> categories = new ArrayList<Category>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE layer = 1";

		try {
			stmt = this.conn.prepareStatement(sql);
			rs = stmt.executeQuery();

			categories = this.fetch(rs);
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			System.out.println("CategoryDAO.restrictGenerals [ERROR](1): " + sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					errClose.printStackTrace();
					System.out.println("CategoryDAO.restrictGenerals [ERROR](2): " + errClose);
				}
			}
		}
		return categories;
	}

	public ArrayList<Category> publicGenerals() {
		ArrayList<Category> categories = new ArrayList<Category>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE layer = 1 AND status = CAST('ACTIVE' AS status_entity)";

		try {
			stmt = this.conn.prepareStatement(sql);
			rs = stmt.executeQuery();

			categories = this.fetch(rs);
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			System.out.println("CategoryDAO.publicGenerals [ERROR](1): " + sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					errClose.printStackTrace();
					System.out.println("CategoryDAO.publicGenerals [ERROR](2): " + errClose);
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
			sqlException.printStackTrace();
			System.out.println("CategoryDAO.findByTitle [ERROR](1): " + sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					errClose.printStackTrace();
					System.out.println("CategoryDAO.findByTitle [ERROR](2): " + errClose);
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
			sqlException.printStackTrace();
			System.out.println("CategoryDAO.findParentByChildId [ERROR](1): " + sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					errClose.printStackTrace();
					System.out.println("CategoryDAO.findParentByChildId [ERROR](2): " + errClose);
				}
			}
		}

		return parentCategory;
	}

	public void saveDetails(Category category) {
		PreparedStatement stmt = null;
		String sql = "UPDATE " + TABLE_NAME + " SET title = ?, description = ?, updated_at = NOW() WHERE id = ?";

		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setString(1, category.getTitle());
			stmt.setString(2, category.getDescription());
			stmt.setInt(3, category.getId());
			stmt.executeUpdate();
		} catch (Exception sqlException) {
			sqlException.printStackTrace();
			System.out.println("CategoryDAO.saveDetails [ERROR](1): " + sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					errClose.printStackTrace();
					System.out.println("CategoryDAO.saveDetails [ERROR](2): " + errClose);
				}
			}
		}
	}

	public ArrayList<Category> restrictSubcategoriesByParent(Integer parentId) {
		ArrayList<Category> subcategories = new ArrayList<Category>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE parent_id = ?";

		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, parentId);
			rs = stmt.executeQuery();

			subcategories = this.fetch(rs);
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			System.out.println("CategoryDAO.restrictSubcategoriesByParent [ERROR](1): " + sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					errClose.printStackTrace();
					System.out.println("CategoryDAO.restrictSubcategoriesByParent [ERROR](2): " +  errClose);
				}
			}
		}

		return subcategories;
	}

	public ArrayList<Category> publicSubcategoriesByParent(Integer parentId) {
		ArrayList<Category> subcategories = new ArrayList<Category>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE parent_id = ? AND status = CAST('ACTIVE' AS status_entity)";

		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, parentId);
			rs = stmt.executeQuery();

			subcategories = this.fetch(rs);
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			System.out.println("CategoryDAO.publicSubcategoriesByParent [ERROR](1): " + sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					errClose.printStackTrace();
					System.out.println("CategoryDAO.publicSubcategoriesByParent [ERROR](2): " +  errClose);
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
			sqlException.printStackTrace();
			System.out.println("CategoryDAO.updateStatus [ERROR](1): " +  sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					errClose.printStackTrace();
					System.out.println("CategoryDAO.updateStatus [ERROR](2): " +  errClose);
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
			sqlException.printStackTrace();
			System.out.println("CategoryDAO.destroyCreation [ERROR](1): " +  sqlException);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException errClose) {
					errClose.printStackTrace();
					System.out.println("CategoryDAO.destroyCreation [ERROR](2): " +  errClose);
				}
			}
		}
	}
}
