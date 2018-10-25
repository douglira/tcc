package dao;

import enums.Status;
import models.Item;
import models.ProductItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PurchaseItemDAO extends GenericDAO {
    private static final String TABLE_NAME = "pr_products";
    private static final String TABLE_RELATION_PI = "product_items";

    public PurchaseItemDAO(boolean getConnection) {
        super(getConnection);
    }

    public PurchaseItemDAO(Connection conn) {
        super(conn);
    }

    public void attachPurchaseRequest(int purchaseRequestId, Item item) {
        PreparedStatement stmt = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (purchase_request_id, product_item_id, " +
                "quantity, additional_spec) VALUES (?, ?, ?, ?)";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequestId);
            stmt.setInt(2, item.getProduct().getId());
            stmt.setInt(3, item.getQuantity());
            stmt.setString(4, item.getAdditionalSpec());
            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseItemDAO.attachPurchaseRequest [ERROR]: " + err);
        }
    }

    private Item fetchWithProductItem(ResultSet rs, Item item) throws SQLException {

        ProductItem productItem = new ProductItem();
        productItem.setId(rs.getInt("product_item_id"));
        productItem.setTitle(rs.getString("title"));
        productItem.setViewsCount(rs.getInt("views_count"));
        productItem.setRelevance(rs.getInt("relevance"));
        productItem.setBasePrice(rs.getDouble("base_price"));
        productItem.setMaxPrice(rs.getDouble("max_price"));
        productItem.setMinPrice(rs.getDouble("min_price"));
        productItem.setStatus(Status.valueOf(rs.getString("status")));

        item.setProduct(productItem);
        item.setQuantity(rs.getInt("quantity"));
        item.setAdditionalSpec(rs.getString("additional_spec"));
        item.calculateAmount();

        return item;
    }

    public boolean validateProductInsertion(int purchaseRequestId, Item item) {
        PreparedStatement stmt = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE purchase_request_id = ? AND product_item_id = ?";
        boolean isValid = false;

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequestId);
            stmt.setInt(2, item.getProduct().getId());
            isValid = !stmt.executeQuery().next();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseItemDAO.validateProductInsertion [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("PurchaseItemDAO.validateProductInsertion [ERROR](2): " + err);
                }
            }
        }

        return isValid;
    }

    public ArrayList<Item> findByPurchaseRequest(int purchaseRequestId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " INNER JOIN " + TABLE_RELATION_PI +
                " ON " + TABLE_NAME + ".product_item_id = " + TABLE_RELATION_PI + ".id" +
                " WHERE purchase_request_id = ?";
        ArrayList<Item> products = new ArrayList<Item>();

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequestId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item();
                item = this.fetchWithProductItem(rs, item);

                products.add(item);
            }
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseItemDAO.findByPurchaseRequest [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("PurchaseItemDAO.findByPurchaseRequest [ERROR](2): " + err);
                }
            }
        }

        return products;
    }

    public void updateQuantityAndSpec(int purchaseRequestId, Item item) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET quantity = ?, additional_spec = ? " +
                "WHERE purchase_request_id = ? AND product_item_id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, item.getQuantity());
            stmt.setString(2, item.getAdditionalSpec());
            stmt.setInt(3, purchaseRequestId);
            stmt.setInt(4, item.getProduct().getId());
            stmt.execute();
            this.conn.commit();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseItemDAO.updateQuantityAndSpec [ERROR](1): " + err);
        }
    }

    public void remove(int purchaseRequestId, int productItemId) {
        PreparedStatement stmt = null;
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE purchase_request_id = ? AND product_item_id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequestId);
            stmt.setInt(2, productItemId);
            stmt.execute();
            this.conn.commit();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseItemDAO.remove [ERROR]: " + err);
        }
    }

    public void removeAll(int purchaseRequestId) {
        PreparedStatement stmt = null;
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE purchase_request_id = ? ";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequestId);
            stmt.execute();
        } catch(SQLException err) {
            err.printStackTrace();
            System.out.println("PurchaseItemDAO.removeAll [ERROR]: " + err);
        }
    }
}
