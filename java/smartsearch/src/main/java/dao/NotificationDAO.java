package dao;

import enums.NotificationStatus;
import enums.NotificationResource;
import models.User;
import models.socket.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class NotificationDAO extends GenericDAO {
    private final static String TABLE_NAME = "notifications";

    public NotificationDAO(boolean getConnection) {
        super(getConnection);
    }

    public NotificationDAO(Connection conn) {
        super(conn);
    }

    public void create(Notification notification) {
        PreparedStatement stmt = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (from_user_id, to_user_id, resource_id, resource_type, " +
                "status, content, created_at, updated_at) VALUES (?, ?, ?, CAST(? AS notification_resource), " +
                "CAST(? AS notification_status), ?, ?, ?)";

        try {
            stmt = this.conn.prepareStatement(sql);

            try {
                stmt.setInt(1, notification.getFrom().getId());
            } catch (NullPointerException err) {
                stmt.setNull(1, Types.INTEGER);
            }

            stmt.setInt(2, notification.getTo().getId());

            try {
                stmt.setInt(3, notification.getResourceId());
            } catch (NullPointerException err) {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, notification.getResourceType().toString());
            stmt.setString(5, notification.getStatus().toString());
            stmt.setString(6, notification.getContent());

            Calendar createdAt = Calendar.getInstance();
            notification.setCreatedAt(createdAt);
            stmt.setTimestamp(7, new Timestamp(notification.getCreatedAt().getTimeInMillis()));

            Calendar updatedAt = Calendar.getInstance();
            notification.setUpdatedAt(updatedAt);
            stmt.setTimestamp(8, new Timestamp(notification.getUpdatedAt().getTimeInMillis()));

            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("NotificationDAO.create [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("NotificationDAO.create [ERROR](2): " + err);
                }
            }
        }
    }

    private Notification fetch(ResultSet rs, Notification notification) throws SQLException {
        notification.setId(rs.getInt("id"));

        try {
            User from = new User();
            from.setId(rs.getInt("from_user_id"));
            notification.setFrom(from);
        } catch (NullPointerException err) {
            notification.setFrom(null);
        }

        User to = new User();
        to.setId(rs.getInt("to_user_id"));
        notification.setTo(to);

        try {
            notification.setResourceId(rs.getInt("resource_id"));
        } catch (NullPointerException err) {
            notification.setResourceId(null);
        }

        notification.setResourceType(NotificationResource.valueOf(rs.getString("resource_type")));
        notification.setStatus(NotificationStatus.valueOf(rs.getString("status")));
        notification.setContent(rs.getString("content"));

        Calendar createdAt = Calendar.getInstance();
        createdAt.setTime(rs.getTimestamp("created_at"));
        notification.setCreatedAt(createdAt);

        try {
            Calendar updatedAt = Calendar.getInstance();
            updatedAt.setTime(rs.getTimestamp("updated_at"));
            notification.setUpdatedAt(updatedAt);
        } catch (NullPointerException err) {

        }

        return notification;
    }

    public ArrayList<Notification> findLastOnes(int userId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE to_user_id = ? ORDER BY created_at DESC LIMIT 15";
        ArrayList<Notification> notifications = new ArrayList<Notification>();

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Notification notification = this.fetch(rs, new Notification());

                notifications.add(notification);
            }
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("NotificationDAO.findLastOnes [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("NotificationDAO.findLastOnes [ERROR](2): " + err);
                }
            }
        }
        return notifications;
    }

    public void updateStatus(Notification notification) {
        PreparedStatement stmt = null;
        String sql = "UPDATE " + TABLE_NAME + " SET status = CAST(? as notification_status) WHERE id = ?";

        try {
            stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, notification.getStatus().toString());
            stmt.setInt(2, notification.getId());
            stmt.execute();
        } catch (SQLException err) {
            err.printStackTrace();
            System.out.println("NotificationDAO.updateStatus [ERROR](1): " + err);
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                    System.out.println("NotificationDAO.updateStatus [ERROR](2): " + err);
                }
            }
        }
    }
}
