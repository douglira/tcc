package dao;

import enums.ProductSituation;
import enums.Status;
import models.Category;
import models.Product;
import models.ProductItem;
import models.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class ProductDAO extends GenericDAO {
    private static final String TABLE_NAME = "products";

    public ProductDAO(boolean getConnection) {
        super(getConnection);
    }

    public ProductDAO(Connection conn) {
        super(conn);
    }

    public Product create(Product product) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (seller_id, product_item_id, category_id, title, description, "
                + "base_price, available_quantity, situation, status, sold_quantity, created_at) VALUES (?, ?, ?, ?, "
                + "?, ?, ?, CAST( ? as product_situation), CAST( ? as status_entity), "
                + "0, ?)";
        try {
            stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, product.getSeller().getId());
            stmt.setInt(2, product.getProductItem().getId());
            stmt.setInt(3, product.getCategory().getId());
            stmt.setString(4, product.getTitle());
            stmt.setString(5, product.getDescription());
            stmt.setDouble(6, product.getBasePrice());
            stmt.setInt(7, product.getAvailableQuantity());
            stmt.setString(8, product.getSituation().toString());
            stmt.setString(9, product.getStatus().toString());
            stmt.setTimestamp(10, new Timestamp(product.getCreatedAt().getTimeInMillis()));
            stmt.execute();

            rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                product.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ProductDAO.create [ERROR]: " + e);
        }
        return product;
    }

    private Product fetch(ResultSet rs, Product product) throws SQLException {
        product.setId(rs.getInt("id"));
        product.setTitle(rs.getString("title"));
        product.setDescription(rs.getString("description"));
        product.setBasePrice(rs.getDouble("base_price"));
        product.setSoldQuantity(rs.getInt("sold_quantity"));
        product.setAvailableQuantity(rs.getInt("available_quantity"));
        product.setStatus(Status.valueOf(rs.getString("status")));
        product.setSituation(ProductSituation.valueOf(rs.getString("situation")));

        Calendar create_at = Calendar.getInstance();
        create_at.setTime(rs.getTimestamp("created_at"));
        product.setCreatedAt(create_at);

        try {
            Calendar updated_at = Calendar.getInstance();
            updated_at.setTime(rs.getTimestamp("updated_at"));
            product.setUpdatedAt(updated_at);
        } catch (NullPointerException err) {

        }

        Seller seller = new Seller();
        seller.setId(rs.getInt("seller_id"));

        Category category = new Category();
        category.setId(rs.getInt("category_id"));

        ProductItem productItem = new ProductItem();
        productItem.setId(rs.getInt("product_item_id"));

        product.setSeller(seller);
        product.setCategory(category);
        product.setProductItem(productItem);

        return product;
    }

    public Product findById(Product product) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, product.getId());
            rs = stmt.executeQuery();

            if (rs.next()) {
                this.fetch(rs, product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ProductDAO.findById [ERROR](1): " + e);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    System.out.println("ProductDAO.findById [ERROR](2): " + sqlException);
                }
            }
        }

        return product;
    }

    public ArrayList<Product> findByProductItem(int productItemId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME
                + " WHERE product_item_id = ? AND status = CAST( ? AS status_entity )";
        ArrayList<Product> products = new ArrayList<Product>();

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, productItemId);
            stmt.setString(2, Status.ACTIVE.toString());
            rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = this.fetch(rs, new Product());

                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ProductDAO.findProductsByProductItem [ERROR](1): " + e);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    System.out.println("ProductDAO.findProductsByProductItem [ERROR](2): " + sqlException);
                }
            }
        }

        return products;
    }

    public ArrayList<Product> findBySeller(int sellerId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Product> products = new ArrayList<Product>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE seller_id = ? ORDER BY created_at DESC";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, sellerId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = this.fetch(rs, new Product());

                products.add(product);
            }
        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            System.out.println("ProductDAO.findBySeller [ERROR](1): " + sqlError);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    System.out.println("ProductDAO.findBySeller [ERROR](2): " + sqlException);
                }
            }
        }

        return products;
    }

    public Product findByProductItemAndSeller(int productItemId, int sellerId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME +
                " WHERE product_item_id = ? AND seller_id = ? AND status = CAST('ACTIVE' as status_entity)";
        Product product = null;

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, productItemId);
            stmt.setInt(2, sellerId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                product = this.fetch(rs, new Product());
            }
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("ProductDAO.findByProductItemAndSeller [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    System.out.println("ProductDAO.findByProductItemAndSeller [ERROR](2): " + sqlException);
                }
            }
        }

        return product;
    }
    
    public void update(Product product) {
    	PreparedStatement stmt = null;

    	String sql = new StringBuilder()
                .append("UPDATE ")
                .append(TABLE_NAME)
                .append(" SET ")
                .append("description = ?, available_quantity = ?, ")
                .append("category_id = ?, base_price = ?")
                .append("WHERE id = ?")
                .toString();

    	try {
    		stmt = this.conn.prepareStatement(sql);
    		stmt.setString(1, product.getDescription());
    		stmt.setInt(2, product.getAvailableQuantity());
    		stmt.setInt(3, product.getCategory().getId());
    		stmt.setDouble(4, product.getBasePrice());
    		stmt.setInt(5, product.getId());
    		stmt.executeUpdate();
    		
    		this.conn.commit();
    	} catch (SQLException err) {
            err.printStackTrace();
            System.out.println("ProductDAO.update [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    System.out.println("ProductDAO.update [ERROR](2): " + sqlException);
                }
            }
        }
    }

    public ArrayList<Product> pagination(int page, int perPage, int sellerId) {
        int offset = (page - 1) * perPage;
        ArrayList<Product> products = new ArrayList<Product>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE status = CAST('ACTIVE' as status_entity) AND seller_id = ? OFFSET ? LIMIT ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, sellerId);
            stmt.setInt(2, offset);
            stmt.setInt(3, perPage);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = this.fetch(rs, new Product());
                products.add(product);
            }
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("ProductDAO.pagination [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    System.out.println("ProductDAO.pagination [ERROR](2): " + sqlException);
                }
            }
        }

        return products;
    }

    public ArrayList<Product> searchByTitle(String title, int sellerId) {
        ArrayList<Product> products = new ArrayList<Product>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE (status = CAST('ACTIVE' as status_entity) AND seller_id = ?) AND title ilike ?";
        title = "%" + title + "%";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, sellerId);
            stmt.setString(2, title);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = this.fetch(rs, new Product());
                products.add(product);
            }
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("ProductDAO.searchByTitle [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    System.out.println("ProductDAO.searchByTitle [ERROR](2): " + sqlException);
                }
            }
        }

        return products;
    }

    public void updateSale(Product product) {
        PreparedStatement stmt = null;
        String sql = new StringBuilder()
                .append("UPDATE ")
                .append(TABLE_NAME)
                .append(" SET ")
                .append("available_quantity = ?, sold_quantity = ? ")
                .append("WHERE id = ?")
                .toString();

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, product.getAvailableQuantity());
            stmt.setInt(2, product.getSoldQuantity());
            stmt.setInt(3, product.getId());
            stmt.executeUpdate();


        } catch (SQLException err) {
            try {
                this.conn.rollback();
            } catch (Exception error) {
                error.printStackTrace();
                System.out.println("ProductDAO.updateAvailableQuantity [ERROR](1): " + err);
            }
            err.printStackTrace();
            System.out.println("ProductDAO.updateAvailableQuantity [ERROR](2): " + err);
        }
    }
}
