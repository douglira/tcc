package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import models.ProductPicture;

public class ProductPictureDAO extends GenericDAO {
	private static final String TABLE_NAME = "product_pictures";

	public ProductPictureDAO(boolean getConnection) {
		super(getConnection);
	}

	public ProductPictureDAO(Connection conn) {
		super(conn);
	}
	
	public ProductPicture create(ProductPicture productPicture) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "INSERT INTO " + TABLE_NAME + " (filename, name, size, "
				+ "type, url_path, product_item_id, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";

		try {
			stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, productPicture.getFilename());
			stmt.setString(2, productPicture.getName());
			stmt.setDouble(3, productPicture.getSize());
			stmt.setString(4, productPicture.getType());
			stmt.setString(5, productPicture.getUrlPath());
			stmt.setInt(6, productPicture.getProductItem().getId());
			stmt.execute();

			rs = stmt.getGeneratedKeys();

			if (rs.next()) {
				productPicture.setId(rs.getInt("id"));
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return productPicture;
	}
	
	public ArrayList<ProductPicture> findByProductItem(int productItemId){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE product_item_id = ?";
		ArrayList<ProductPicture> pictures = new ArrayList<ProductPicture>();
		
		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, productItemId);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				ProductPicture picture = new ProductPicture();
				picture.setId(rs.getInt("id"));
				picture.setName(rs.getString("name"));
				picture.setFilename(rs.getString("filename"));
				picture.setSize(rs.getDouble("size"));
				picture.setType(rs.getString("type"));
				picture.setUrlPath(rs.getString("url_path"));
				
				pictures.add(picture);
			}
			
		} catch(SQLException sqlError) {
			sqlError.printStackTrace();
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return pictures;
	}
}
