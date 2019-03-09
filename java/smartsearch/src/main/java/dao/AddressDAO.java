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

	public AddressDAO(Connection conn) {
		super(conn);
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
			this.conn.commit();
		} catch (SQLException e) {
			try {
				this.conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("AddressDAO.create [ERROR](1): " + e1);
			}
			e.printStackTrace();
			System.out.println("AddressDAO.create [ERROR](2): " + e);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
					System.out.println("AddressDAO.create [ERROR](3): " + sqlException);
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

			this.conn.commit();
		} catch (SQLException e) {
			try {
				this.conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("AddressDAO.update [ERROR](1): " + e1);
			}
			e.printStackTrace();
			System.out.println("AddressDAO.update [ERROR](2): " + e);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
					System.out.println("AddressDAO.update [ERROR](3): " + sqlException);
				}
			}
		}
	}

	private Address fetch(ResultSet rs, Address address) throws SQLException {
		address.setId(rs.getInt("id"));
		address.setPostalCode(rs.getString("postal_code"));
		address.setStreet(rs.getString("street"));
		address.setDistrict(rs.getString("district"));
		address.setCity(rs.getString("city"));
		address.setProvinceCode(rs.getString("province_code"));
		address.setCountryName(rs.getString("country_name"));
		address.setBuildingNumber(rs.getInt("building_number"));
		address.setAdditionalData(rs.getString("additional_data"));

		return address;
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
				address = this.fetch(rs, new Address());
			}
		} catch (Exception error) {
			try {
				this.conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("AddressDAO.findByPerson [ERROR](1): " + e1);
			}
				error.printStackTrace();
				System.out.println("AddressDAO.findByPerson [ERROR](2): " + error);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
					System.out.println("AddressDAO.findByPerson [ERROR](1): " + sqlException);
				}
			}
		}

		return address;
	}

    public Address findById(Address address) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder()
				.append("SELECT * FROM ")
				.append(TABLE_NAME)
				.append(" WHERE id = ? ");

		try {
			stmt = this.conn.prepareStatement(sql.toString());
			stmt.setInt(1, address.getId());
			rs = stmt.executeQuery();

			if (rs.next()) {
				address = this.fetch(rs, new Address());
			} else {
				address = null;
			}
		} catch (Exception error) {
			error.printStackTrace();
			System.out.println("AddressDAO.findById [ERROR](1): " + error);
		} finally {
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
					System.out.println("AddressDAO.findById [ERROR](2): " + sqlException);
				}
			}
		}

		return address;
    }
}
