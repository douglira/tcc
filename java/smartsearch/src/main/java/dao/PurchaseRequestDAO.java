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
        String sql = "INSERT INTO " + TABLE_NAME + " (id, buyer_id, stage, additional_data, due_date, " +
                "total_amount, views_count, propagation_count, quotes_visibility, created_at) VALUES (nextval('pr_sequence'), ?, CAST(? AS pr_stage), ?, ?, ?, ?, ?, ?, ?)";
        try {
            stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, purchaseRequest.getBuyer().getId());
            stmt.setString(2, purchaseRequest.getStage().toString());
            stmt.setString(3, purchaseRequest.getAdditionalData());
            stmt.setTimestamp(4, new Timestamp(purchaseRequest.getDueDate().getTimeInMillis()));
            stmt.setDouble(5, purchaseRequest.getTotalAmount());
            stmt.setInt(6, purchaseRequest.getViewsCount());
            stmt.setInt(7, purchaseRequest.getPropagationCount());
            stmt.setBoolean(8, purchaseRequest.getQuotesVisibility());

            purchaseRequest.setCreatedAt(Calendar.getInstance());
            stmt.setTimestamp(9, new Timestamp(purchaseRequest.getCreatedAt().getTimeInMillis()));

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
        purchaseRequest.setQuotesVisibility(rs.getBoolean("quotes_visibility"));

        Calendar dueDateAverage = Calendar.getInstance();
        dueDateAverage.setTime(rs.getTimestamp("due_date"));
        purchaseRequest.setDueDate(dueDateAverage);

        Calendar createdAt = Calendar.getInstance();
        createdAt.setTime(rs.getTimestamp("created_at"));
        purchaseRequest.setCreatedAt(createdAt);

        try {
            Calendar updatedAt = Calendar.getInstance();
            updatedAt.setTime(rs.getTimestamp("updated_at"));
            purchaseRequest.setUpdatedAt(updatedAt);
        } catch (SQLException | NullPointerException error) {
            purchaseRequest.setUpdatedAt(null);
        }

        try {

            Calendar closedAt = Calendar.getInstance();
            closedAt.setTime(rs.getTimestamp("closed_at"));
            purchaseRequest.setClosedAt(closedAt);
        } catch (SQLException | NullPointerException error) {
            purchaseRequest.setClosedAt(null);
        }

        Buyer buyer = new Buyer();
        buyer.setId(rs.getInt("buyer_id"));
        purchaseRequest.setBuyer(buyer);

        return purchaseRequest;
    }

    public PurchaseRequest findById(PurchaseRequest purchaseRequest) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequest.getId());
            rs = stmt.executeQuery();

            if (rs.next()) {
                purchaseRequest = this.fetch(rs, purchaseRequest);
            } else {
                purchaseRequest = null;
            }
        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            System.out.println("PurchaseRequestDAO.findById [ERROR](1): " + sqlError);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlError) {
                    sqlError.printStackTrace();
                    System.out.println("PurchaseRequestDAO.findById [ERROR](2): " + sqlError);
                }
            }
        }

        return purchaseRequest;
    }

    public ArrayList<PurchaseRequest> findByBuyer(int buyerId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE buyer_id = ?";
        ArrayList<PurchaseRequest> purchaseRequests = new ArrayList<PurchaseRequest>();

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, buyerId);
            rs = stmt.executeQuery();

            while(rs.next()) {
                PurchaseRequest purchaseRequest = this.fetch(rs, new PurchaseRequest());

                purchaseRequests.add(purchaseRequest);
            }
        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            System.out.println("PurchaseRequestDAO.findByBuyer [ERROR](1): " + sqlError);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlError) {
                    sqlError.printStackTrace();
                    System.out.println("PurchaseRequestDAO.findByBuyer [ERROR](2): " + sqlError);
                }
            }
        }

        return purchaseRequests;
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
                PurchaseRequest pr = this.fetch(rs, new PurchaseRequest());

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

    public void updateViewsCount(PurchaseRequest purchaseRequest) {
        CallableStatement stmt = null;
        String sql = "{CALL pr_update_views(?)}";

        try {
            stmt = this.conn.prepareCall(sql);
            stmt.setInt(1, purchaseRequest.getId());
            stmt.execute();
        } catch (SQLException error) {
            error.printStackTrace();
            System.out.println("SellerDAO.updateViewsCount [ERROR](1): " + error);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlError) {
                    sqlError.printStackTrace();
                    System.out.println("PurchaseRequestDAO.updateViewsCount [ERROR](2): " + sqlError);
                }
            }
        }
    }

    public void updatePropagation(PurchaseRequest purchaseRequest) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET propagation_count = ?, updated_at = ? WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequest.getPropagationCount());

            purchaseRequest.setUpdatedAt(Calendar.getInstance());
            stmt.setTimestamp(2, new Timestamp(purchaseRequest.getUpdatedAt().getTimeInMillis()));

            stmt.setInt(3, purchaseRequest.getId());
            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseRequestDAO.updatePropagation [ERROR]: " + err);
        }
    }

    public void updateDueDate(PurchaseRequest purchaseRequest) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET due_date = ?, updated_at = ? WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(purchaseRequest.getDueDate().getTimeInMillis()));

            purchaseRequest.setUpdatedAt(Calendar.getInstance());
            stmt.setTimestamp(2, new Timestamp(purchaseRequest.getUpdatedAt().getTimeInMillis()));

            stmt.setInt(3, purchaseRequest.getId());
            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseRequestDAO.updateDueDate [ERROR]: " + err);
        }
    }

    public void updateTotalAmount(PurchaseRequest purchaseRequest) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET total_amount = ?, updated_at = ? WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setDouble(1, purchaseRequest.getTotalAmount());

            purchaseRequest.setUpdatedAt(Calendar.getInstance());
            stmt.setTimestamp(2, new Timestamp(purchaseRequest.getUpdatedAt().getTimeInMillis()));

            stmt.setInt(3, purchaseRequest.getId());
            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseRequestDAO.updateDueDate [ERROR](1): " + err);
            try {
                this.conn.rollback();
            } catch (SQLException error) {
                error.printStackTrace();
                System.out.println("PurchaseRequestDAO.updateDueDate [ERROR](2): " + error);
            }
        }
    }

    public void updatePublish(PurchaseRequest purchaseRequest) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET stage = CAST(? as pr_stage), additional_data = ?, quotes_visibility = ?, due_date = ?, updated_at = ? WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, purchaseRequest.getStage().toString());
            stmt.setString(2, purchaseRequest.getAdditionalData());
            stmt.setBoolean(3, purchaseRequest.getQuotesVisibility());
            stmt.setTimestamp(4, new Timestamp(purchaseRequest.getDueDate().getTimeInMillis()));

            purchaseRequest.setUpdatedAt(Calendar.getInstance());
            stmt.setTimestamp(5, new Timestamp(purchaseRequest.getUpdatedAt().getTimeInMillis()));

            stmt.setInt(6, purchaseRequest.getId());
            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseRequest.updatePublish [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("PurchaseRequest.updatePublish [ERROR](2): " + err);
                }
            }
        }
    }

    public void destroyCreation(int purchaseRequestId, int buyerId) {
        PreparedStatement stmt = null;
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ? AND buyer_id = ? AND stage = CAST('CREATION' as pr_stage)";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(2, buyerId);
            stmt.setInt(1, purchaseRequestId);
            stmt.execute();
            this.conn.commit();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseRequestDAO.destroyCreation [ERROR](1): " + err);
            try {
                this.conn.rollback();
            } catch (SQLException error) {
                error.printStackTrace();
                System.out.println("PurchaseRequestDAO.destroyCreation [ERROR](2): " + error);
            }
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("PurchaseRequestDAO.destroyCreation [ERROR](3): " + err);
                }
            }
        }
    }

    public void updateStage(PurchaseRequest purchaseRequest) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET stage = CAST(? as pr_stage), closed_at = ?, updated_at = ? WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, purchaseRequest.getStage().toString());
            stmt.setTimestamp(2, new Timestamp(purchaseRequest.getClosedAt().getTimeInMillis()));
            stmt.setTimestamp(3, new Timestamp(purchaseRequest.getUpdatedAt().getTimeInMillis()));
            stmt.setInt(4, purchaseRequest.getId());
            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseRequest.updateStage [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("PurchaseRequest.updateStage [ERROR](2): " + e);
                }
            }
        }
    }

    public void updateClosedStage(PurchaseRequest purchaseRequest) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET stage = CAST(? as pr_stage), closed_at = NOW(), updated_at = NOW() WHERE id = ?";
        purchaseRequest.setStage(PRStage.CLOSED);

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, purchaseRequest.getStage().toString());
            stmt.setInt(2, purchaseRequest.getId());
            stmt.execute();
        } catch (SQLException err) {
            try {
                this.conn.rollback();
            } catch (SQLException error) {
                error.printStackTrace();
                System.out.println("PurchaseRequest.updateClosedStage [ERROR](1): " + err);
            }
            err.printStackTrace();
            System.out.println("PurchaseRequest.updateClosedStage [ERROR](2): " + err);
        }
    }
}
