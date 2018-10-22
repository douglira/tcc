package dao;

import enums.ProductSituation;
import enums.Status;
import models.Category;
import models.Product;
import models.ProductList;
import models.Seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

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

    private ProductList fetchWithProduct(ResultSet rs, ProductList productList) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("product_id"));
        product.setSeller(new Seller(rs.getInt("seller_id")));
        product.setCategory(new Category(rs.getInt("category_id")));
        product.setTitle(rs.getString("title"));
        product.setDescription(rs.getString("description"));
        product.setBasePrice(rs.getDouble("base_price"));
        product.setSoldQuantity(rs.getInt("sold_quantity"));
        product.setAvailableQuantity(rs.getInt("available_quantity"));
        product.setStatus(Status.valueOf(rs.getString("status")));
        product.setSituation(ProductSituation.valueOf(rs.getString("situation")));

        Calendar createdAt = Calendar.getInstance();
        createdAt.setTime(rs.getTimestamp("created_at"));
        product.setCreatedAt(createdAt);

        try {
            Calendar updatedAt = Calendar.getInstance();
            updatedAt.setTime(rs.getTimestamp("updated_at"));
            product.setUpdatedAt(updatedAt);
        } catch (NullPointerException | SQLException err) {

        }

        productList.setProduct(product);
        productList.setQuantity(rs.getInt("quantity"));
        return productList;
    }

    public ArrayList<ProductList> findByQuote(int quoteId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " INNER JOIN " + TABLE_RELATION_PRODUCT + " ON " +
                TABLE_NAME + ".product_id = " + TABLE_RELATION_PRODUCT + ".id WHERE " + TABLE_NAME + ".quote_id = ?";
        ArrayList<ProductList> products = new ArrayList<ProductList>();

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, quoteId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                ProductList productList = this.fetchWithProduct(rs, new ProductList());

                products.add(productList);
            }
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("QuoteProductListDAO.findByQuote [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("QuoteProductListDAO.findByQuote [ERROR](2): " + err);
                }
            }
        }

        return products;
    }
}
