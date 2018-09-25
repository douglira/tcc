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
	
	public ProductItemDAO(Connection conn, boolean setTransaction) {
		super(conn, setTransaction);
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
}
