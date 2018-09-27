package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import models.ProductItem;

public class ProductItemDAO extends GenericDAO {
	private static final String TABLE_NAME = "product_items";

	public ProductItemDAO(boolean getConnection) {
		super(getConnection);
	}

	public ProductItemDAO(Connection conn) {
		super(conn);
	}

	public ProductItem create(ProductItem productItem) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "INSERT INTO " + TABLE_NAME + " (title, views_count, relevance, "
				+ "market_price, max_price, min_price) VALUES (?, 0, 1, ?, ?, ?)";

		try {
			stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, productItem.getTitle());
			stmt.setDouble(2, productItem.getMarketPrice());
			stmt.setDouble(3, productItem.getMaxPrice());
			stmt.setDouble(4, productItem.getMinPrice());
			stmt.execute();

			rs = stmt.getGeneratedKeys();

			if (rs.next()) {
				productItem.setId(rs.getInt("id"));
				productItem.setRelevance(1);
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return productItem;
	}

	public ProductItem findById(ProductItem productItem) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, productItem.getId());
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				productItem.setTitle(rs.getString("title"));
				productItem.setThumbnailPath(rs.getString("thumbnail_path"));
				productItem.setViewsCount(rs.getInt("views_count"));
				productItem.setRelevance(rs.getInt("relevance"));
				productItem.setMarketPrice(rs.getDouble("market_price"));
				productItem.setMaxPrice(rs.getDouble("max_price"));
				productItem.setMinPrice(rs.getDouble("min_price"));
			}
		} catch (SQLException sqlError) {
			System.out.println("ProductItem.findById" + sqlError);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException sqlException) {
					throw new RuntimeException(sqlException);
				}
			}
		}
		
		return productItem;
	}
	
	public void updatePricesAndRelevance(ProductItem productItem) {
		PreparedStatement stmt = null;
		String sql = "UPDATE " + TABLE_NAME + " SET market_price = ?, max_price = ?, min_price = ?, relevance = ? "
				+ "WHERE id = ?";
		
		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setDouble(1,  productItem.getMarketPrice());
			stmt.setDouble(2, productItem.getMaxPrice());
			stmt.setDouble(3, productItem.getMinPrice());
			stmt.setInt(4, productItem.getRelevance());
			stmt.setInt(5, productItem.getId());
			stmt.executeUpdate();
		} catch(SQLException sqlException) {
			System.out.println("ProductItemDAO.updateSimple: ERROR -> " + sqlException);
		}
	}
}
