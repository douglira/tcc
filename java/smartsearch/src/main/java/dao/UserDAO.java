package dao;

import enums.Status;
import enums.UserRoles;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class UserDAO extends GenericDAO {
    private static final String TABLE_NAME = "users";

    public UserDAO(boolean getConnection) {
        super(getConnection);
    }

    public UserDAO(Connection conn) {
        super(conn);
    }

    public User create(User user) {
        PreparedStatement stmt = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (email, username, display_name, "
                + "password, role, status, created_at) VALUES (?, ?, ?, "
                + "?, CAST(? AS users_role), CAST(? AS status_entity), NOW())";

        try {
            stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getDisplayName());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRole().toString());
            stmt.setString(6, user.getStatus().toString());
            stmt.execute();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                user.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("UserDAO.create [ERROR]: " + e);
        }

        return user;
    }

    public User checkIfExists(User user) {
        User loggedUser = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE users.email = ?";

        try {

            stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, user.getEmail());
            rs = stmt.executeQuery();

            if (rs.next()) {
                loggedUser = new User();
                loggedUser.setId(rs.getInt("id"));
                loggedUser.setUsername(rs.getString("username"));
                loggedUser.setEmail(rs.getString("email"));
                loggedUser.setPassword(rs.getString("password"));
                loggedUser.setDisplayName(rs.getString("display_name"));
                loggedUser.setRole(UserRoles.valueOf(rs.getString("role")));
                loggedUser.setStatus(Status.valueOf(rs.getString("status")));
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("UserDAO.checkIfExists [ERROR](1): " + sqlException);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException errClose) {
                    errClose.printStackTrace();
                    System.out.println("UserDAO.checkIfExists [ERROR](2): " + errClose);
                }
            }
        }

        return loggedUser;
    }

    public ArrayList<User> report(int page, int perPage) {
        User user;
        ArrayList<User> users = new ArrayList<User>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT display_name, email, created_at FROM " + TABLE_NAME + " OFFSET ? LIMIT ?";

        int offset = (page - 1) * perPage;
        try {

            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, offset);
            stmt.setInt(2, perPage);
            rs = stmt.executeQuery();

            while (rs.next()) {
                user = new User();
                user.setDisplayName(rs.getString("display_name"));
                user.setEmail(rs.getString("email"));

                Calendar createdAt = Calendar.getInstance();
                createdAt.setTime(rs.getTimestamp("created_at"));
                user.setCreatedAt(createdAt);

                users.add(user);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("UserDAO.report [ERROR](1): " + sqlException);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException errClose) {
                    errClose.printStackTrace();
                    System.out.println("UserDAO.report [ERROR](2): " + errClose);
                }
            }
        }

        return users;
    }

    public void setPasswordResetToken(User user) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET password_reset_token = ?, password_expires_in = ? WHERE email = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, user.getPasswordResetToken());
            stmt.setTimestamp(2, new Timestamp(user.getPasswordExpiresIn().getTimeInMillis()));
            stmt.setString(3, user.getEmail());
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("UserDAO.setPasswordResetToken [ERROR](1): " + e);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    System.out.println("UserDAO.setPasswordResetToken [ERROR](2): " + sqlException);
                }
            }
        }
    }

    public User findByPassResetToken(User user) {
        User userQuery = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT id, email, password_reset_token, password_expires_in FROM " + TABLE_NAME
                + " WHERE password_reset_token = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, user.getPasswordResetToken());
            rs = stmt.executeQuery();

            if (rs.next()) {
                userQuery = new User();
                userQuery.setId(rs.getInt("id"));
                userQuery.setEmail(rs.getString("email"));
                userQuery.setPasswordResetToken("password_reset_token");

                Calendar passwordExpiresIn = Calendar.getInstance();
                passwordExpiresIn.setTime(rs.getTimestamp("password_expires_in"));
                userQuery.setPasswordExpiresIn(passwordExpiresIn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("UserDAO.findPassResetToken [ERROR](1): " + e);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    System.out.println("UserDAO.findPassResetToken [ERROR](2): " + sqlException);
                }
            }
        }

        return userQuery;
    }

    public void updateResetPassword(User user) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME
                + " SET password_reset_token = NULL, password_expires_in = NULL, password = ? WHERE id = ?";

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getPassword());
            stmt.setInt(2, user.getId());
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("UserDAO.updateResetToken [ERROR](1): " + e);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    System.out.println("UserDAO.updateResetToken [ERROR](2): " + sqlException);
                }
            }
        }
    }

    public void updateDisplayName(User user) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET display_name = ? WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, user.getDisplayName());
            stmt.setInt(2, user.getId());
            stmt.executeUpdate();
        } catch (SQLException sqlError) {
            try {
                this.conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("UserDAO.updateDisplayName [ERROR](1): " + e);
            }
            sqlError.printStackTrace();
            System.out.println("UserDAO.updateDisplayName [ERROR](2): " + sqlError);
        }
    }
}
