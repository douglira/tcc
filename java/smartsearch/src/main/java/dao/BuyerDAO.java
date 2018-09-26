package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import models.Buyer;

public class BuyerDAO extends GenericDAO {
	private final static String TABLE_NAME = "buyers";
	
	public BuyerDAO(boolean getConnection) {
		super(getConnection);
	}
	
	public BuyerDAO(Connection conn) {
		super(conn);
	}
	
	public void create(Buyer buyer) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO " + TABLE_NAME + " (person_id, created_at) VALUES (?, NOW())";
		
		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, buyer.getId());
			
		} catch (SQLException e) {
			try {
				this.conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
		}
	}
}
