package dao;

import models.File;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class FileDAO extends GenericDAO {
    private static final String TABLE_NAME = "files";
    private static final String PIVOT_PRODUCT = "product_galleries";
    private static final String PIVOT_PRODUCT_ITEM = "product_item_galleries";

    public FileDAO(boolean getConnection) {
        super(getConnection);
    }

    public FileDAO(Connection conn) {
        super(conn);
    }

    public File create(File file) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (file_path, name, size, "
                + "type, subtype, url_path, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, file.getFilePath());
            stmt.setString(2, file.getName());
            stmt.setDouble(3, file.getSize());
            stmt.setString(4, file.getType());
            stmt.setString(5, file.getSubtype());
            stmt.setString(6, file.getUrlPath());

            file.setCreatedAt(Calendar.getInstance());
            stmt.setTimestamp(7, new Timestamp(file.getCreatedAt().getTimeInMillis()));

            stmt.execute();

            rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                file.setId(rs.getInt("id"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    private ArrayList<File> fetch(ResultSet rs) throws SQLException {
        ArrayList<File> files = new ArrayList<File>();

        while (rs.next()) {
            File file = new File();
            file.setId(rs.getInt("id"));
            file.setFilePath(rs.getString("file_path"));
            file.setName(rs.getString("name"));
            file.setSize(rs.getDouble("size"));
            file.setType(rs.getString("type"));
            file.setSubtype(rs.getString("subtype"));
            file.setUrlPath(rs.getString("url_path"));

            Calendar createdAt = Calendar.getInstance();
            createdAt.setTime(rs.getTimestamp("created_at"));
            file.setCreatedAt(createdAt);

            files.add(file);
        }

        return files;
    }

    public void attachProduct(int productId, int pictureId) {
        PreparedStatement stmt = null;
        String sql = "INSERT INTO " + PIVOT_PRODUCT + " (product_id, picture_id) " +
                "VALUES (?, ?)";
        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            stmt.setInt(2, pictureId);
            stmt.execute();
        } catch (SQLException sqlError) {
            System.out.println("FileDAO.attachProduct [ERROR]: " + sqlError);
        }
    }

    public ArrayList<File> getProductPictures(int productId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " INNER JOIN " + PIVOT_PRODUCT +
                " ON " + TABLE_NAME + ".id = " + PIVOT_PRODUCT + ".picture_id WHERE " +
                PIVOT_PRODUCT + ".product_id = ?";
        ArrayList<File> files = null;

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            rs = stmt.executeQuery();

            files = this.fetch(rs);

        } catch (SQLException sqlError) {
            System.out.println("FileDAO.getProductPictures [ERROR](1): " + sqlError);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException e) {
                    System.out.println("FileDAO.getProductPictures [ERROR](2): " + e);
                }
            }
        }
        return files;
    }

    public void attachProductItem(int productItemId, int pictureId) {
        PreparedStatement stmt = null;
        String sql = "INSERT INTO " + PIVOT_PRODUCT_ITEM + " (product_item_id, picture_id) " +
                "VALUES (?, ?)";
        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, productItemId);
            stmt.setInt(2, pictureId);
            stmt.execute();
        } catch (SQLException sqlError) {
            System.out.println("FileDAO.attachProductItem [ERROR]: " + sqlError);
        }
    }

    public ArrayList<File> getProductItemPictures(int productItemId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " INNER JOIN " + PIVOT_PRODUCT_ITEM +
                " ON " + TABLE_NAME + ".id = " + PIVOT_PRODUCT_ITEM + ".picture_id WHERE " +
                PIVOT_PRODUCT_ITEM + ".product_item_id = ?";
        ArrayList<File> files = null;

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, productItemId);
            rs = stmt.executeQuery();

            files = this.fetch(rs);

        } catch (SQLException sqlError) {
            System.out.println("FileDAO.getProductItemPictures [ERROR](1): " + sqlError);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException e) {
                    System.out.println("FileDAO.getProductItemPictures [ERROR](2): " + e);
                }
            }
        }
        return files;
    }
}
