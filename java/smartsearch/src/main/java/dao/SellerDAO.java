package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import models.Buyer;
import models.Seller;

public class SellerDAO extends GenericDAO {
private final static String TABLE_NAME = "sellers";
	
	public SellerDAO(boolean getConnection) {
		super(getConnection);
	}
	
	public SellerDAO(Connection conn) {
		super(conn);
	}
	
	public void create(Seller seller) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO " + TABLE_NAME + " (person_id, quotes_expiration_date, "
				+ "created_at) VALUES (?, CAST( ? AS quotes_expiration_period), NOW())";
		
		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, seller.getId());
			stmt.setString(2, seller.getQuotesExpirationPeriod().toString());
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
