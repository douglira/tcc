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
        } catch (NullPointerException err) {

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
            System.out.println("QuoteDAO.create [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("QuoteDAO.create [ERROR](2): " + err);
                }
            }
        }
        return quotes;
    }
}