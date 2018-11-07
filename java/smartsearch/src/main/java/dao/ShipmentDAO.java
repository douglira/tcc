package dao;

import enums.ShipmentMethod;
import enums.ShipmentStatus;
import models.Address;
import models.Quote;
import models.Shipment;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class ShipmentDAO extends GenericDAO {
    private static final String TABLE_NAME = "shipments";
    private static final String TABLE_RELATION_QUOTE = "quotes";

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
                " receiver_address_id, created_at) VALUES (?, ?, CAST(? as shipment_status), CAST(? as shipment_method), ?, ?, ?)";

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
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setTimestamp(7, new Timestamp(shipment.getCreatedAt().getTimeInMillis()));
            stmt.execute();

            rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                shipment.setId(rs.getInt("id"));
            }
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("ShipmentDAO.create [ERROR](1): " + err);
            try {
                this.conn.rollback();
            } catch (SQLException sqlErr) {
                sqlErr.printStackTrace();
                System.out.println("ShipmentDAO.create [ERROR](2): " + sqlErr);
            }
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
            shipment.setEstimatedTime(null);
        }

        shipment.setStatus(ShipmentStatus.valueOf(rs.getString("status")));
        shipment.setMethod(ShipmentMethod.valueOf(rs.getString("method")));
        shipment.setQuote(new Quote(rs.getInt("quote_id")));

        try {
            shipment.setReceiverAddress(new Address(rs.getInt("receiver_address_id")));
        } catch (SQLException | NullPointerException err) {
            shipment.setReceiverAddress(null);
        }

        Calendar createdAt = Calendar.getInstance();
        createdAt.setTime(rs.getTimestamp("created_at"));
        shipment.setCreatedAt(createdAt);

        try {
            Calendar updatedAt = Calendar.getInstance();
            updatedAt.setTime(rs.getTimestamp("updated_at"));
            shipment.setUpdatedAt(updatedAt);
        } catch (SQLException | NullPointerException err) {
            shipment.setUpdatedAt(null);
        }

        return shipment;
    }

    public ArrayList<Shipment> findByQuoteAndSeller(int quoteId, int sellerId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME + " INNER JOIN " + TABLE_RELATION_QUOTE
                + " ON " + TABLE_NAME + ".quote_id = " + TABLE_RELATION_QUOTE + ".id "
                + " WHERE quote_id = ? AND seller_id = ?";
        ArrayList<Shipment> shipments = new ArrayList<Shipment>();

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, quoteId);
            stmt.setInt(2, sellerId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Shipment shipment = this.fetch(rs, new Shipment());

                shipments.add(shipment);
            }
        } catch(SQLException err) {
            err.printStackTrace();
            System.out.println("ShipmentDAO.findByQuote [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("ShipmentDAO.create [ERROR](2): " + err);
                }
            }
        }

        return shipments;
    }
}
