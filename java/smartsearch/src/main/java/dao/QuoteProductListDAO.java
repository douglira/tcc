package dao;

import models.ProductList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QuoteProductListDAO extends GenericDAO {
    private static final String TABLE_NAME = "quote_products";
    private static final String TABLE_RELATION_PRODUCT = "products";

    public QuoteProductListDAO(boolean getConnection) {
        super(getConnection);
    }

    public QuoteProductListDAO(Connection conn) {
        super(conn);
    }

    public void attachQuote(int quoteId, ProductList productList) {
        PreparedStatement stmt = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (quote_id, product_id, quantity) VALUES (?, ?, ?)";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, quoteId);
            stmt.setInt(2, productList.getProduct().getId());
            stmt.setInt(3, productList.getQuantity());
            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("QuoteProductListDAO.attachQuote [ERROR](1): " + err);
            try {
                this.conn.rollback();
            } catch (SQLException sqlErr) {
                sqlErr.printStackTrace();
                System.out.println("QuoteProductListDAO.attachQuote [ERROR](2): " + sqlErr);
            }
        }
    }
}
