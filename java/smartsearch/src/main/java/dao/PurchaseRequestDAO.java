package dao;

import enums.PRStage;
import models.Buyer;
import models.PurchaseRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class PurchaseRequestDAO extends GenericDAO {
    private static final String TABLE_NAME = "purchase_requests";

    public PurchaseRequestDAO(boolean getConnection) {
        super(getConnection);
    }

    public PurchaseRequestDAO(Connection conn) {
        super(conn);
    }

    public PurchaseRequest create(PurchaseRequest purchaseRequest) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (buyer_id, stage, additional_data, due_date_average, " +
                "total_amount, views_count, propagation_count, created_at) VALUES (?, CAST(? AS pr_stage), ?, ?, ?, ?, ?, NOW())";
        try {
            stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, purchaseRequest.getBuyer().getId());
            stmt.setString(2, purchaseRequest.getStage().toString());
            stmt.setString(3, purchaseRequest.getAdditionalData());
            stmt.setTimestamp(4, new Timestamp(purchaseRequest.getDueDateAverage().getTimeInMillis()));
            stmt.setDouble(5, purchaseRequest.getTotalAmount());
            stmt.setInt(6, purchaseRequest.getViewsCount());
            stmt.setInt(7, purchaseRequest.getPropagationCount());
            stmt.execute();

            rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                purchaseRequest.setId(rs.getInt("id"));
            }
        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            System.out.println("PurchaseRequestDAO.create [ERROR]: " + sqlError);
        }

        return purchaseRequest;
    }

    private PurchaseRequest fetch(ResultSet rs, PurchaseRequest purchaseRequest) throws SQLException {
        purchaseRequest.setId(rs.getInt("id"));
        purchaseRequest.setAdditionalData(rs.getString("additional_data"));
        purchaseRequest.setStage(PRStage.valueOf(rs.getString("stage")));
        purchaseRequest.setTotalAmount(rs.getDouble("total_amount"));
        purchaseRequest.setViewsCount(rs.getInt("views_count"));
        purchaseRequest.setPropagationCount(rs.getInt("propagation_count"));

        Calendar createdAt = Calendar.getInstance();
        createdAt.setTime(rs.getTimestamp("created_at"));
        purchaseRequest.setCreatedAt(createdAt);

        try {
            Calendar updatedAt = Calendar.getInstance();
            updatedAt.setTime(rs.getTimestamp("updated_at"));
            purchaseRequest.setCreatedAt(updatedAt);

            Calendar closedAt = Calendar.getInstance();
            closedAt.setTime(rs.getTimestamp("closed_at"));
            purchaseRequest.setCreatedAt(closedAt);
        } catch (NullPointerException error) {
            // error.printStackTrace();
        }

        Buyer buyer = new Buyer();
        buyer.setId(rs.getInt("buyer_id"));
        purchaseRequest.setBuyer(buyer);

        return purchaseRequest;
    }

    public ArrayList<PurchaseRequest> findByStageAndBuyer(PurchaseRequest purchaseRequest) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE buyer_id = ? and stage = CAST(? AS pr_stage)";
        ArrayList<PurchaseRequest> purchaseRequests = new ArrayList<PurchaseRequest>();

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequest.getBuyer().getId());
            stmt.setString(2, purchaseRequest.getStage().toString());
            rs = stmt.executeQuery();

            while (rs.next()) {
                PurchaseRequest pr = new PurchaseRequest();
                pr = this.fetch(rs, pr);

                purchaseRequests.add(pr);
            }
        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            System.out.println("PurchaseRequestDAO.findByStageAndBuyer [ERROR](1): " + sqlError);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlError) {
                    sqlError.printStackTrace();
                    System.out.println("PurchaseRequestDAO.findByStageAndBuyer [ERROR](2): " + sqlError);
                }
            }
        }

        return purchaseRequests;
    }

    public void updatePropagation(PurchaseRequest purchaseRequest) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET propagation_count = ? WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequest.getPropagationCount());
            stmt.setInt(2, purchaseRequest.getId());
            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseRequestDAO.updatePropagation [ERROR]: " + err);
        }
    }

    public void updateDueDate(PurchaseRequest purchaseRequest) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET due_date_average = ? WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(purchaseRequest.getDueDateAverage().getTimeInMillis()));
            stmt.setInt(2, purchaseRequest.getId());
            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseRequestDAO.updateDueDate [ERROR]: " + err);
        }
    }
}
