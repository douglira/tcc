package dao;

import models.Seller;

import java.sql.*;
import java.util.ArrayList;

public class SellerDAO extends GenericDAO {
    private final static String TABLE_NAME = "sellers";

    public SellerDAO(boolean getConnection) {
        super(getConnection);
    }

    public SellerDAO(Connection conn) {
        super(conn);
    }

    public void create(Seller seller) {
        PreparedStatement stmt = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (person_id, quotes_expiration_period, "
                + "created_at) VALUES (?, ?, NOW())";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, seller.getId());
            stmt.setInt(2, seller.getQuotesExpirationPeriod());
            stmt.execute();

            this.conn.commit();
        } catch (SQLException e) {
            try {
                this.conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                System.out.println("SellerDAO.create [ERROR](1): " + e1);
            }
            e.printStackTrace();
            System.out.println("SellerDAO.create [ERROR](2): " + e);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    System.out.println("SellerDAO.create [ERROR](3): " + sqlException);
                }
            }
        }
    }

    public Seller findById(Seller seller) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE person_id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, seller.getId());
            rs = stmt.executeQuery();

            if (rs.next()) {
                seller.setQuotesExpirationPeriod(rs.getInt("quotes_expiration_period"));
            }
        } catch (SQLException error) {
            error.printStackTrace();
            System.out.println("SellerDAO.findById [ERROR](1): " + error);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("SellerDAO.findById [ERROR](2): " + err);
                }
            }
        }

        return seller;
    }

    public ArrayList<Seller> findByPurchaseRequest(int purchaseRequestId) {
        CallableStatement stmt = null;
        ResultSet rs = null;
        String sql = "{CALL get_pr_sellers(?)}";
        ArrayList<Seller> sellers = new ArrayList<Seller>();

        try {
            stmt = this.conn.prepareCall(sql);
            stmt.setInt(1, purchaseRequestId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Seller seller = new Seller();
                seller.setId(rs.getInt("person_id"));
                seller.setQuotesExpirationPeriod(rs.getInt("quotes_expiration_period"));

                sellers.add(seller);
            }
        } catch (SQLException error) {
            error.printStackTrace();
            System.out.println("SellerDAO.findByPurchaseRequest [ERROR]: " + error);
        }

        return sellers;
    }
}
