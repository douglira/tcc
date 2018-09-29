package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import enums.ProductSituation;
import enums.Status;
import models.Category;
import models.Product;
import models.Seller;

public class ProductDAO extends GenericDAO {
	private static final String TABLE_NAME = "products";

	public ProductDAO(boolean getConnection) {
		super(getConnection);
	}

	public ProductDAO(Connection conn) {
		super(conn);
	}

	public void create(Product product) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO " + TABLE_NAME + " (seller_id, product_item_id, category_id, title, description, "
				+ "price, available_quantity, situation, status, sold_quantity, created_at) VALUES (?, ?, ?, ?, "
				+ "?, ?, ?, CAST( ? as product_situation), CAST( ? as status_entity), "
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
			stmt.setString(8, product.getSituation().toString());
			stmt.setString(9, product.getStatus().toString());
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

	public ArrayList<Product> findProductsByProductItem(int productItemId) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM " + TABLE_NAME
				+ " WHERE product_item_id = ? AND status = CAST( ? AS status_entity )";
		ArrayList<Product> products = new ArrayList<Product>();

		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, productItemId);
			stmt.setString(2, Status.ACTIVE.toString());
			rs = stmt.executeQuery();

			while (rs.next()) {
				Seller seller = new Seller();
				seller.setId(rs.getInt("seller_id"));

				Category category = new Category();
				category.setId(rs.getInt("category_id"));

				Product product = new Product();
				product.setId(rs.getInt("id"));

				product.setSeller(seller);
				product.setCategory(category);
				product.setTitle(rs.getString("title"));
				product.setDescription(rs.getString("description"));
				product.setPrice(rs.getDouble("price"));
				product.setSoldQuantity(rs.getInt("sold_quantity"));
				product.setAvailableQuantity(rs.getInt("available_quantity"));
				product.setSituation(ProductSituation.valueOf(rs.getString("situation")));
				product.setStatus(Status.ACTIVE);

				products.add(product);
			}
		} catch (SQLException e) {
			System.out.println("ProductDAO.findProductsByProductItem -> " + e);
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

		return products;
	}
	
	public ArrayList<Product> findBySeller(int sellerId) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<Product> products = new ArrayList<Product>();
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE seller_id = ?";
		
		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, sellerId);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				Product product = new Product();
				
				product.setId(rs.getInt("id"));
				product.setTitle(rs.getString("title"));
				product.setDescription(rs.getString("description"));
				product.setPrice(rs.getDouble("price"));
				product.setSoldQuantity(rs.getInt("sold_quantity"));
				product.setAvailableQuantity(rs.getInt("available_quantity"));
				product.setStatus(Status.valueOf(rs.getString("status")));
				
				Seller seller = new Seller();
				seller.setId(rs.getInt("seller_id"));
				
				Category category = new Category();
				category.setId(rs.getInt("category_id"));
				
				product.setSeller(seller);
				product.setCategory(category);
				
				products.add(product);
			}
		} catch(SQLException sqlError) {
			sqlError.printStackTrace();
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException sqlException) {
					throw new RuntimeException(sqlException);
				}
			}
		}
		
		return products;
	}
}
