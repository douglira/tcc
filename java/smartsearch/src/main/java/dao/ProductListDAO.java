package dao;

import enums.Status;
import models.ProductItem;
import models.ProductList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductListDAO extends GenericDAO {
    private static final String TABLE_NAME = "pr_products";
    private static final String TABLE_RELATION_PI = "product_items";

    public ProductListDAO(boolean getConnection) {
        super(getConnection);
    }

    public ProductListDAO(Connection conn) {
        super(conn);
    }

    public void attachPurchaseRequest(int purchaseRequestId, ProductList productList) {
        PreparedStatement stmt = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (purchase_request_id, product_item_id, " +
                "quantity, additional_spec) VALUES (?, ?, ?, ?)";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequestId);
            stmt.setInt(2, productList.getProduct().getId());
            stmt.setInt(3, productList.getQuantity());
            stmt.setString(4, productList.getAdditionalSpec());
            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("ProductListDAO.attachPurchaseRequest [ERROR]: " + err);
        }
    }

    private ProductList fetchWithProductItem(ResultSet rs, ProductList productList) throws SQLException {

        ProductItem productItem = new ProductItem();
        productItem.setId(rs.getInt("product_item_id"));
        productItem.setTitle(rs.getString("title"));
        productItem.setViewsCount(rs.getInt("views_count"));
        productItem.setRelevance(rs.getInt("relevance"));
        productItem.setBasePrice(rs.getDouble("base_price"));
        productItem.setMaxPrice(rs.getDouble("max_price"));
        productItem.setMinPrice(rs.getDouble("min_price"));
        productItem.setStatus(Status.valueOf(rs.getString("status")));

        productList.setProduct(productItem);
        productList.setQuantity(rs.getInt("quantity"));
        productList.setAdditionalSpec(rs.getString("additional_spec"));
        productList.calculateAmount();

        return productList;
    }

    public boolean validateProductInsertion(int purchaseRequestId, ProductList productList) {
        PreparedStatement stmt = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE purchase_request_id = ? AND product_item_id = ?";
        boolean isValid = false;

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequestId);
            stmt.setInt(2, productList.getProduct().getId());
            isValid = !stmt.executeQuery().next();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("ProductListDAO.validateProductInsertion [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("ProductListDAO.validateProductInsertion [ERROR](2): " + err);
                }
            }
        }

        return isValid;
    }

    public ArrayList<ProductList> findByPurchaseRequest(int purchaseRequestId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " INNER JOIN " + TABLE_RELATION_PI +
                " ON " + TABLE_NAME + ".product_item_id = " + TABLE_RELATION_PI + ".id" +
                " WHERE purchase_request_id = ?";
        ArrayList<ProductList> products = new ArrayList<ProductList>();

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, purchaseRequestId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                ProductList productList = new ProductList();
                productList = this.fetchWithProductItem(rs, productList);

                products.add(productList);
            }
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("ProductListDAO.findByPurchaseRequest [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("ProductListDAO.findByPurchaseRequest [ERROR](2): " + err);
                }
            }
        }

        return products;
    }

    public void updateQuantityAndSpec(int purchaseRequestId, ProductList productList) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET quantity = ?, additional_spec = ? " +
                "WHERE purchase_request_id = ? AND product_item_id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, productList.getQuantity());
            stmt.setString(2, productList.getAdditionalSpec());
            stmt.setInt(3, purchaseRequestId);
            stmt.setInt(4, productList.getProduct().getId());
            stmt.execute();
            this.conn.commit();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("ProductListDAO.updateQuantityAndSpec [ERROR](1): " + err);
        }
    }
}
