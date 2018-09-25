package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import models.Product;

public class ProductDAO extends GenericDAO {
	private static final String TABLE_NAME = "products";
	
	public ProductDAO(boolean getConnection) {
		super(getConnection);
	}
	
	public ProductDAO(Connection conn, boolean setTransaction) {
		super(conn, setTransaction);
	}
	
	public void create(Product product) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO " + TABLE_NAME + " (seller_id, product_item_id, category_id, title, description, "
				+ "price, available_quantity, status, sold_quantity, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, CAST( ? as status_entity), "
				+ "0, NOW())";
		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, product.getSeller().getId());
			stmt.setInt(2, product.getProductItem().getId());
			stmt.setInt(3, product.getCategory().getId());
			stmt.setString(4, product.getTitle());
			stmt.setString(5, product.getDescription());
			stmt.setDouble(6, product.getPrice());
			stmt.setInt(7, product.getAvailableQuantity());
			stmt.setString(8, product.getStatus().toString());
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
}
