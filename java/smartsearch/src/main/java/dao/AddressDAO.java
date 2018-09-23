package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import models.Address;

public class AddressDAO extends GenericDAO {
	private static final String TABLE_NAME = "addresses";

	public AddressDAO(boolean getConnection) {
		super(getConnection);
	}

	public AddressDAO(Connection conn, boolean setTransaction) {
		super(conn, setTransaction);
	}

	public Address create(Address address) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "INSERT INTO " + TABLE_NAME + " (postal_code, street, district, city, "
				+ "province_code, country_name, building_number, additional_data, person_id, created_at)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
		try {
			stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, address.getPostalCode());
			stmt.setString(2, address.getStreet());
			stmt.setString(3, address.getDistrict());
			stmt.setString(4, address.getCity());
			stmt.setString(5, address.getProvinceCode());
			stmt.setString(6, address.getCountryName());
			stmt.setInt(7, address.getBuildingNumber());
			stmt.setString(8, address.getAdditionalData());
			stmt.setInt(9, address.getPerson().getId());
			stmt.execute();

			rs = stmt.getGeneratedKeys();
			
			if (rs.next()) {
				address.setId(rs.getInt("id"));
			}
			
		} catch (SQLException e) {
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
		return address;
	}

	public void update(Address address) {
		PreparedStatement stmt = null;
		String sql = "UPDATE " + TABLE_NAME + " SET postal_code = ?, street = ?, district = ?, city = ?, "
				+ "province_code = ?, country_name = ?, building_number = ?, additional_data = ?, updated_at = NOW() "
				+ "WHERE person_id = ?";
		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setString(1, address.getPostalCode());
			stmt.setString(2, address.getStreet());
			stmt.setString(3, address.getDistrict());
			stmt.setString(4, address.getCity());
			stmt.setString(5, address.getProvinceCode());
			stmt.setString(6, address.getCountryName());
			stmt.setInt(7, address.getBuildingNumber());
			stmt.setString(8, address.getAdditionalData());
			stmt.setInt(9, address.getPerson().getId());
			stmt.executeUpdate();

		} catch (SQLException e) {
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

	public Address findByPerson(int personId) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Address address = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE person_id = ?";

		try {
			stmt = this.conn.prepareStatement(sql);
			stmt.setInt(1, personId);
			rs = stmt.executeQuery();

			if (rs.next()) {
				address = new Address();

				address.setId(rs.getInt("id"));
				address.setPostalCode(rs.getString("postal_code"));
				address.setStreet(rs.getString("street"));
				address.setDistrict(rs.getString("district"));
				address.setCity(rs.getString("city"));
				address.setProvinceCode(rs.getString("province_code"));
				address.setCountryName(rs.getString("country_name"));
				address.setBuildingNumber(rs.getInt("building_number"));
				address.setAdditionalData(rs.getString("additional_data"));
			}
		} catch (Exception error) {
			try {
				this.conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(error);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException sqlException) {
					throw new RuntimeException(sqlException);
				}
			}
		}

		return address;
	}
}
