package dao;

import enums.ProductSituation;
import enums.Status;
import models.Category;
import models.Item;
import models.Product;
import models.Seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class QuotationItemDAO extends GenericDAO {
    private static final String TABLE_NAME = "quote_products";
    private static final String TABLE_RELATION_PRODUCT = "products";

    public QuotationItemDAO(boolean getConnection) {
        super(getConnection);
    }

    public QuotationItemDAO(Connection conn) {
        super(conn);
    }

    public void attachQuote(int quoteId, Item item) {
        PreparedStatement stmt = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (quote_id, product_id, quantity, sale_price) VALUES (?, ?, ?, ?)";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, quoteId);
            stmt.setInt(2, item.getProduct().getId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getProduct().getBasePrice());
            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("QuotationItemDAO.attachQuote [ERROR](1): " + err);
            try {
                this.conn.rollback();
            } catch (SQLException sqlErr) {
                sqlErr.printStackTrace();
                System.out.println("QuotationItemDAO.attachQuote [ERROR](2): " + sqlErr);
            }
        }
    }

    private Item fetchWithProduct(ResultSet rs, Item item) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("product_id"));
        product.setSeller(new Seller(rs.getInt("seller_id")));
        product.setCategory(new Category(rs.getInt("category_id")));
        product.setTitle(rs.getString("title"));
        product.setDescription(rs.getString("description"));
        product.setBasePrice(rs.getDouble("sale_price"));
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
            product.setUpdatedAt(null);
        }

        item.setProduct(product);
        item.setQuantity(rs.getInt("quantity"));
        return item;
    }

    public ArrayList<Item> findByQuote(int quoteId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " INNER JOIN " + TABLE_RELATION_PRODUCT + " ON " +
                TABLE_NAME + ".product_id = " + TABLE_RELATION_PRODUCT + ".id WHERE " + TABLE_NAME + ".quote_id = ?";
        ArrayList<Item> products = new ArrayList<Item>();

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, quoteId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = this.fetchWithProduct(rs, new Item());

                products.add(item);
            }
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("QuotationItemDAO.findByQuote [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("QuotationItemDAO.findByQuote [ERROR](2): " + err);
                }
            }
        }

        return products;
    }
}
