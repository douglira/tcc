package dao;

import enums.Status;
import models.ProductItem;

import java.sql.*;
import java.util.Calendar;

public class ProductItemDAO extends GenericDAO {
    private static final String TABLE_NAME = "product_items";

    public ProductItemDAO(boolean getConnection) {
        super(getConnection);
    }

    public ProductItemDAO(Connection conn) {
        super(conn);
    }

    public ProductItem create(ProductItem productItem) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (title, views_count, relevance, "
                + "base_price, max_price, min_price, status, created_at) VALUES (?, 0, 1, ?, ?, ?, CAST(? AS status_entity), ?)";

        try {
            stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, productItem.getTitle());
            stmt.setDouble(2, productItem.getBasePrice());
            stmt.setDouble(3, productItem.getMaxPrice());
            stmt.setDouble(4, productItem.getMinPrice());
            stmt.setString(5, productItem.getStatus().toString());
            stmt.setTimestamp(6, new Timestamp(productItem.getCreatedAt().getTimeInMillis()));
            stmt.execute();

            rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                productItem.setId(rs.getInt("id"));
                productItem.setRelevance(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ProductItemDAO.create [ERROR]: " + e);
        }

        return productItem;
    }

    private ProductItem fetch(ResultSet rs, ProductItem productItem) throws SQLException {
        productItem.setId(rs.getInt("id"));
        productItem.setTitle(rs.getString("title"));
        productItem.setViewsCount(rs.getInt("views_count"));
        productItem.setRelevance(rs.getInt("relevance"));
        productItem.setBasePrice(rs.getDouble("base_price"));
        productItem.setMaxPrice(rs.getDouble("max_price"));
        productItem.setMinPrice(rs.getDouble("min_price"));
        productItem.setStatus(Status.valueOf(rs.getString("status")));

        try {
            Calendar createdAt = Calendar.getInstance();
            createdAt.setTime(rs.getTimestamp("created_at"));
            productItem.setCreatedAt(createdAt);

            Calendar updatedAt = Calendar.getInstance();
            updatedAt.setTime(rs.getTimestamp("updated_at"));
            productItem.setUpdatedAt(updatedAt);
        } catch(NullPointerException err) {

        }

        return productItem;
    }

    public ProductItem findById(ProductItem productItem) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, productItem.getId());
            rs = stmt.executeQuery();

            if (rs.next()) {
                productItem = this.fetch(rs, productItem);
            } else {
                productItem = null;
            }
        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            System.out.println("ProductItemDAO.findById [ERROR](1): " + sqlError);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    System.out.println("ProductItemDAO.findById [ERROR](2): " + sqlException);
                }
            }
        }

        return productItem;
    }

    public void updatePricesAndRelevance(ProductItem productItem) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET base_price = ?, max_price = ?, min_price = ?, "
        		+ "relevance = ?, updated_at = ?, status = CAST('ACTIVE' as status_entity) "
                + "WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setDouble(1, productItem.getBasePrice());
            stmt.setDouble(2, productItem.getMaxPrice());
            stmt.setDouble(3, productItem.getMinPrice());
            stmt.setInt(4, productItem.getRelevance());

            productItem.setUpdatedAt(Calendar.getInstance());
            stmt.setTimestamp(5, new Timestamp(productItem.getUpdatedAt().getTimeInMillis()));

            stmt.setInt(6, productItem.getId());
            stmt.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("ProductItemDAO.updatePricesAndRelevance: [ERROR]: " + sqlException);
        }
    }

    public void updateViewsCount(ProductItem productItem) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET views_count = ?, updated_at = ? WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, productItem.getViewsCount());

            productItem.setUpdatedAt(Calendar.getInstance());
            stmt.setTimestamp(2, new Timestamp(productItem.getUpdatedAt().getTimeInMillis()));

            stmt.setInt(3, productItem.getId());
            stmt.executeUpdate();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("ProductItemDAO.updateViewsCount [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("ProductItemDAO.updateViewsCount [ERROR](2): " + err);
                }
            }
        }
    }
}
