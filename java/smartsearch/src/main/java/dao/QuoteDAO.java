package dao;

import enums.QuoteStatus;
import models.PurchaseRequest;
import models.Quote;
import models.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class QuoteDAO extends GenericDAO {
    private static final String TABLE_NAME = "quotes";

    public QuoteDAO(boolean getConnection) {
        super(getConnection);
    }

    public QuoteDAO(Connection conn) {
        super(conn);
    }

    public Quote create(Quote quote) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (purchase_request_id, seller_id, additional_data, status, " +
                " discount, total_amount, expiration_date, created_at) VALUES (?, ?, ?, CAST( ? as quote_status), ?, ?, ?, ?)";

        try {
            stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, quote.getPurchaseRequest().getId());
            stmt.setInt(2, quote.getSeller().getId());
            stmt.setString(3, quote.getAdditionalData());
            stmt.setString(4, quote.getStatus().toString());
            stmt.setDouble(5, quote.getDiscount());
            stmt.setDouble(6, quote.getTotalAmount());
            stmt.setTimestamp(7, new Timestamp(quote.getExpirationDate().getTimeInMillis()));
            stmt.setTimestamp(8, new Timestamp(quote.getCreatedAt().getTimeInMillis()));
            stmt.execute();

            rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                quote.setId(rs.getInt("id"));
            }
            this.conn.commit();
        } catch (SQLException sqlErr) {
            sqlErr.printStackTrace();
            System.out.println("QuoteDAO.create [ERROR]: " + sqlErr);
        }

        return quote;
    }

    private Quote fetch(ResultSet rs, Quote quote) throws SQLException {
        quote.setId(rs.getInt("id"));
        quote.setPurchaseRequest(new PurchaseRequest(rs.getInt("purchase_request_id")));
        quote.setSeller(new Seller(rs.getInt("seller_id")));
        quote.setAdditionalData(rs.getString("additional_data"));
        quote.setStatus(QuoteStatus.valueOf(rs.getString("status")));
        quote.setReason(rs.getString("reason"));
        quote.setDiscount(rs.getDouble("discount"));
        quote.setTotalAmount(rs.getDouble("total_amount"));

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTime(rs.getTimestamp("expiration_date"));
        quote.setExpirationDate(expirationDate);

        Calendar createdAt = Calendar.getInstance();
        createdAt.setTime(rs.getTimestamp("created_at"));
        quote.setCreatedAt(createdAt);

        try {
            Calendar updatedAt = Calendar.getInstance();
            updatedAt.setTime(rs.getTimestamp("updated_at"));
            quote.setUpdatedAt(updatedAt);
        } catch (SQLException | NullPointerException err) {
            quote.setUpdatedAt(null);
        }

        return quote;
    }

    public Quote findById(Quote quote) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder()
                .append("SELECT * FROM ")
                .append(TABLE_NAME)
                .append(" WHERE ")
                .append(" id = ? ");
        try {
            stmt = this.conn.prepareStatement(sql.toString());
            stmt.setInt(1, quote.getId());
            rs = stmt.executeQuery();

            if (rs.next()) {
                quote = this.fetch(rs, new Quote());
            } else {
                quote = null;
            }
        } catch (SQLException err) {
            quote = null;
            err.printStackTrace();
            System.out.println("QuoteDAO.findById [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("QuoteDAO.findById [ERROR](2): " + err);
                }
            }
        }
        return quote;
    }

    public ArrayList<Quote> findByPurchaseRequest(int purchaseRequestId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE purchase_request_id = ?";
        ArrayList<Quote> quotes = new ArrayList<Quote>();

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequestId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Quote quote = this.fetch(rs, new Quote());
                quotes.add(quote);
            }
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("QuoteDAO.findByPurchaseRequest [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("QuoteDAO.findByPurchaseRequest [ERROR](2): " + err);
                }
            }
        }
        return quotes;
    }

    public ArrayList<Quote> findRestrictQuotes(int purchaseRequestId, int sellerId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE purchase_request_id = ? AND seller_id = ?";
        ArrayList<Quote> quotes = new ArrayList<Quote>();

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequestId);
            stmt.setInt(2, sellerId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Quote quote = this.fetch(rs, new Quote());
                quotes.add(quote);
            }
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("QuoteDAO.findRestrictQuotes [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("QuoteDAO.findRestrictQuotes [ERROR](2): " + err);
                }
            }
        }
        return quotes;
    }

    public void updateStatus(Quote quote) {
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder()
                .append("UPDATE ")
                .append(TABLE_NAME)
                .append(" SET ")
                .append(" status = CAST (? AS quote_status), ")
                .append(" updated_at = NOW() ")
                .append("WHERE ")
                .append("id = ?");
        try {
            stmt = this.conn.prepareStatement(sql.toString());
            stmt.setString(1, quote.getStatus().toString());
            stmt.setInt(2, quote.getId());
            stmt.executeUpdate();

        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("QuoteDAO.updateStatus [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("QuoteDAO.updateStatus [ERROR](2): " + err);
                }
            }
        }
    }

    public void updateStatusAndReason(Quote quote) {
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder()
                .append("UPDATE ")
                .append(TABLE_NAME)
                .append(" SET ")
                .append(" status = CAST (? AS quote_status), ")
                .append(" updated_at = NOW(), ")
                .append(" reason = ? ")
                .append(" WHERE ")
                .append(" id = ? ");
        try {
            stmt = this.conn.prepareStatement(sql.toString());
            stmt.setString(1, quote.getStatus().toString());
            stmt.setString(2, quote.getReason());
            stmt.setInt(3, quote.getId());
            stmt.executeUpdate();

        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("QuoteDAO.updateStatusAndReason [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("QuoteDAO.updateStatusAndReason [ERROR](2): " + err);
                }
            }
        }
    }

    public void updateAcceptedStatus(Quote quote) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET status = CAST (? as quote_status), updated_at = NOW() WHERE id = ?";
        quote.setStatus(QuoteStatus.ACCEPTED);

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, quote.getStatus().toString());
            stmt.setInt(2, quote.getId());
            stmt.executeUpdate();
        } catch (SQLException err) {
            try {
                this.conn.rollback();
            } catch (SQLException error) {
                error.printStackTrace();
                System.out.println("QuoteDAO.updateAcceptedStatus [ERROR](1): " + err);
            }
            err.printStackTrace();
            System.out.println("QuoteDAO.updateAcceptedStatus [ERROR](2): " + err);
        }
    }
}
