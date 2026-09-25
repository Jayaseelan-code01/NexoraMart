package com.jayaseelan.nexoramart.dao;

import com.jayaseelan.nexoramart.model.Notification;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Read-model DAO for a notification center. Notifications are derived from existing domain data. */
public class JdbcNotificationDAO implements NotificationDAO {
    private final DataSource dataSource;
    public JdbcNotificationDAO(DataSource dataSource) { this.dataSource = dataSource; }

    @Override
    public List<Notification> forBuyer(long buyerId) throws SQLException {
        List<Notification> out = new ArrayList<>();
        String orders = "SELECT id,order_ref,status,created_at FROM orders WHERE buyer_id=? ORDER BY created_at DESC,id DESC LIMIT 20";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(orders)) {
            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String ref = rs.getString("order_ref");
                    String status = rs.getString("status");
                    String title = "Order update";
                    String msg;
                    if ("PENDING".equals(status)) {
                        title = "Order placed";
                        msg = "Your order " + ref + " is waiting for confirmation.";
                    } else if ("CANCELLED".equals(status)) {
                        title = "Order cancelled";
                        msg = "Your order " + ref + " was cancelled.";
                    } else {
                        msg = "Your order " + ref + " is now " + status + ".";
                    }
                    out.add(new Notification("ORDER-" + id + "-" + status, "ORDER", title, msg, "orders", rs.getTimestamp("created_at")));
                }
            }
        }

        String replies = "SELECT r.id,r.product_id,p.name AS product_name,r.replied_at FROM reviews r JOIN products p ON p.id=r.product_id " +
                "WHERE r.buyer_id=? AND r.seller_reply IS NOT NULL ORDER BY r.replied_at DESC,r.id DESC LIMIT 20";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(replies)) {
            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Notification("REVIEW-REPLY-" + rs.getLong("id"), "REVIEW", "Seller replied to your review",
                            "A seller replied to your review on " + rs.getString("product_name") + ".",
                            "product/details?id=" + rs.getLong("product_id"), rs.getTimestamp("replied_at")));
                }
            }
        }
        return sort(out);
    }

    @Override
    public List<Notification> forSeller(long sellerId) throws SQLException {
        List<Notification> out = new ArrayList<>();
        String orders = "SELECT DISTINCT o.id,o.order_ref,o.status,o.created_at FROM orders o " +
                "JOIN order_items oi ON oi.order_id=o.id JOIN products p ON p.id=oi.product_id " +
                "WHERE p.seller_id=? ORDER BY o.created_at DESC,o.id DESC LIMIT 20";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(orders)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String ref = rs.getString("order_ref");
                    out.add(new Notification("SELLER-ORDER-" + rs.getLong("id") + "-" + rs.getString("status"), "ORDER",
                            "Customer order " + rs.getString("status"),
                            "Order " + ref + " currently needs your attention.", "seller/orders", rs.getTimestamp("created_at")));
                }
            }
        }

        String lowStock = "SELECT id,name,stock_qty,created_at FROM products WHERE seller_id=? AND active=TRUE AND stock_qty<=3 ORDER BY stock_qty ASC,id DESC LIMIT 20";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(lowStock)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int stock = rs.getInt("stock_qty");
                    out.add(new Notification("LOW-STOCK-" + rs.getLong("id") + "-" + stock, "INVENTORY", "Low stock alert",
                            rs.getString("name") + " has only " + stock + " unit(s) left.", "seller/products", rs.getTimestamp("created_at")));
                }
            }
        }

        String reviews = "SELECT r.id,r.product_id,p.name AS product_name,r.created_at FROM reviews r JOIN products p ON p.id=r.product_id " +
                "WHERE p.seller_id=? AND (r.seller_reply IS NULL OR TRIM(r.seller_reply)='') ORDER BY r.created_at DESC,r.id DESC LIMIT 20";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(reviews)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Notification("REVIEW-" + rs.getLong("id"), "REVIEW", "New customer review",
                            "A customer reviewed " + rs.getString("product_name") + ". Reply from your seller dashboard.",
                            "product/details?id=" + rs.getLong("product_id"), rs.getTimestamp("created_at")));
                }
            }
        }
        return sort(out);
    }

    @Override
    public List<Notification> forAdmin() throws SQLException {
        List<Notification> out = new ArrayList<>();
        String q = "SELECT a.id,a.action,a.entity_type,a.entity_id,a.details,a.created_at,u.name AS actor_name " +
                "FROM audit_logs a LEFT JOIN users u ON u.id=a.actor_user_id ORDER BY a.created_at DESC,a.id DESC LIMIT 30";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String actor = rs.getString("actor_name");
                String details = rs.getString("details");
                String msg = (actor == null ? "System" : actor) + " · " + (details == null ? rs.getString("action") : details);
                out.add(new Notification("AUDIT-" + rs.getLong("id"), "ADMIN", "Admin activity", msg,
                        "admin", rs.getTimestamp("created_at")));
            }
        }
        return out;
    }

    private List<Notification> sort(List<Notification> notifications) {
        notifications.sort((a,b) -> {
            Timestamp at = a.getCreatedAt(), bt = b.getCreatedAt();
            if (at == null && bt == null) return 0;
            if (at == null) return 1;
            if (bt == null) return -1;
            int c = bt.compareTo(at);
            return c != 0 ? c : b.getId().compareTo(a.getId());
        });
        return notifications;
    }
}
