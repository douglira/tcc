package dao;

import models.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class OrderDAO extends GenericDAO {
    private static final String TABLE_NAME = "orders";

    public OrderDAO(boolean getConnection) {
        super(getConnection);
    }

    public OrderDAO(Connection conn) {
        super(conn);
    }

    public Order create(Order order) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = new StringBuilder()
                .append("INSERT INTO ")
                .append(TABLE_NAME)
                .append(" (quote_id, shipment_id, status, total_amount, created_at) ")
                .append(" VALUES (?, ?, CAST (? as order_status), ?, NOW())")
                .toString();

        try {
            stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, order.getQuote().getId());
            stmt.setInt(2, order.getShipment().getId());
            stmt.setString(3, order.getStatus().toString());
            stmt.setDouble(4, order.getTotalAmount());
            stmt.execute();

            rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                order.setId(rs.getInt("id"));
            }
        } catch(Exception err) {
            try {
                this.conn.rollback();
            } catch (Exception sqlErr) {
                err.printStackTrace();
                System.out.println("OrderDAO.create [ERROR](1): " + err);
            }
            err.printStackTrace();
            System.out.println("OrderDAO.create [ERROR](2): " + err);
        }
        return order;
    }
}
