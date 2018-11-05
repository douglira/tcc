package dao;

import enums.ShipmentMethod;
import enums.ShipmentStatus;
import models.Address;
import models.Quote;
import models.Shipment;

import java.sql.*;
import java.util.Calendar;

public class ShipmentDAO extends GenericDAO {
    private static final String TABLE_NAME = "shipments";

    public ShipmentDAO(boolean getConnection) {
        super(getConnection);
    }

    public ShipmentDAO(Connection conn) {
        super(conn);
    }

    public Shipment create(Shipment shipment) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (cost, estimated_time, status, method, quote_id, " +
                " receiver_address_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setDouble(1, shipment.getCost());

            try {
                stmt.setDate(2, new Date(shipment.getEstimatedTime().getTimeInMillis()));
            } catch (SQLException | NullPointerException err) {
                stmt.setNull(2, Types.DATE);
            }

            stmt.setString(3, shipment.getStatus().toString());
            stmt.setString(4, shipment.getMethod().toString());
            stmt.setInt(5, shipment.getQuote().getId());

            try {
                stmt.setInt(6, shipment.getReceiverAddress().getId());
            } catch (SQLException | NullPointerException err) {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setTimestamp(7, new Timestamp(shipment.getCreatedAt().getTimeInMillis()));
            stmt.execute();

            rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                shipment.setId(rs.getInt("id"));
            }
            this.conn.commit();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("ShipmentDAO.create [ERROR]: " + err);
        }

        return shipment;
    }

    private Shipment fetch(ResultSet rs, Shipment shipment) throws SQLException {
        shipment.setId(rs.getInt("id"));
        shipment.setCost(rs.getDouble("cost"));

        try {
            Calendar estimatedTime = Calendar.getInstance();
            estimatedTime.setTime(rs.getTimestamp("estimated_time"));
            shipment.setEstimatedTime(estimatedTime);
        } catch (SQLException | NullPointerException err) {

        }

        shipment.setStatus(ShipmentStatus.valueOf(rs.getString("status")));
        shipment.setMethod(ShipmentMethod.valueOf(rs.getString("method")));
        shipment.setQuote(new Quote(rs.getInt("quote_id")));

        try {
            shipment.setReceiverAddress(new Address(rs.getInt("receiver_address_id")));
        } catch (SQLException | NullPointerException err) {

        }

        Calendar createdAt = Calendar.getInstance();
        createdAt.setTime(rs.getTimestamp("created_at"));
        shipment.setCreatedAt(createdAt);

        try {
            Calendar updatedAt = Calendar.getInstance();
            updatedAt.setTime(rs.getTimestamp("updated_at"));
            shipment.setUpdatedAt(updatedAt);
        } catch (SQLException | NullPointerException err) {

        }

        return shipment;
    }
}
