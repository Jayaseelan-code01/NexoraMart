package com.jayaseelan.nexoramart.dao;

import com.jayaseelan.nexoramart.model.Order;
import com.jayaseelan.nexoramart.model.OrderItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JdbcOrderDAO implements OrderDAO {
    private final DataSource dataSource;
    private static final DateTimeFormatter REF_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public JdbcOrderDAO(DataSource dataSource) { this.dataSource = dataSource; }

    @Override
    public Order createOrder(long buyerId, Map<Long, Integer> cart) throws SQLException {
        if (cart == null || cart.isEmpty()) throw new IllegalArgumentException("Your cart is empty.");

        try (Connection connection = dataSource.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                BigDecimal total = BigDecimal.ZERO;
                String select = "SELECT id,name,price,stock_qty FROM products WHERE id=? FOR UPDATE";
                try (PreparedStatement ps = connection.prepareStatement(select)) {
                    for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                        int quantity = entry.getValue() == null ? 0 : entry.getValue();
                        if (quantity <= 0) throw new IllegalArgumentException("Invalid cart quantity.");
                        ps.setLong(1, entry.getKey());
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) throw new IllegalArgumentException("A cart product is no longer available.");
                            String name = rs.getString("name");
                            BigDecimal price = rs.getBigDecimal("price");
                            int stock = rs.getInt("stock_qty");
                            if (quantity > stock) throw new IllegalArgumentException("Only " + stock + " unit(s) available for " + name + ".");
                            total = total.add(price.multiply(BigDecimal.valueOf(quantity)));
                        }
                    }
                }

                String orderRef = generateOrderRef();
                long orderId;
                String insertOrder = "INSERT INTO orders(order_ref,buyer_id,total_amount,status) VALUES(?,?,?,'PENDING')";
                try (PreparedStatement ps = connection.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, orderRef);
                    ps.setLong(2, buyerId);
                    ps.setBigDecimal(3, total);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Unable to create order.");
                        orderId = keys.getLong(1);
                    }
                }

                String selectProduct = "SELECT id,name,price,stock_qty FROM products WHERE id=? FOR UPDATE";
                String insertItem = "INSERT INTO order_items(order_id,product_id,product_name,unit_price,quantity) VALUES(?,?,?,?,?)";
                String updateStock = "UPDATE products SET stock_qty=stock_qty-? WHERE id=?";
                try (PreparedStatement productPs = connection.prepareStatement(selectProduct);
                     PreparedStatement itemPs = connection.prepareStatement(insertItem);
                     PreparedStatement stockPs = connection.prepareStatement(updateStock)) {
                    for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                        int quantity = entry.getValue();
                        productPs.setLong(1, entry.getKey());
                        try (ResultSet rs = productPs.executeQuery()) {
                            if (!rs.next()) throw new IllegalArgumentException("A cart product is no longer available.");
                            String name = rs.getString("name");
                            BigDecimal price = rs.getBigDecimal("price");
                            int stock = rs.getInt("stock_qty");
                            if (quantity > stock) throw new IllegalArgumentException("Only " + stock + " unit(s) available for " + name + ".");
                            itemPs.setLong(1, orderId);
                            itemPs.setLong(2, entry.getKey());
                            itemPs.setString(3, name);
                            itemPs.setBigDecimal(4, price);
                            itemPs.setInt(5, quantity);
                            itemPs.executeUpdate();
                            stockPs.setInt(1, quantity);
                            stockPs.setLong(2, entry.getKey());
                            if (stockPs.executeUpdate() != 1) throw new SQLException("Unable to update product stock.");
                        }
                    }
                }
                connection.commit();

                Order order = new Order();
                order.setId(orderId);
                order.setOrderRef(orderRef);
                order.setBuyerId(buyerId);
                order.setTotalAmount(total);
                order.setStatus("PENDING");
                order.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                return order;
            } catch (Exception ex) {
                connection.rollback();
                if (ex instanceof IllegalArgumentException) throw (IllegalArgumentException) ex;
                if (ex instanceof SQLException) throw (SQLException) ex;
                throw new SQLException("Order creation failed.", ex);
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }

    @Override
    public Order findById(long orderId, long buyerId) throws SQLException {
        String q = "SELECT o.id,o.order_ref,o.buyer_id,u.name AS buyer_name,o.total_amount,o.status,o.created_at " +
                "FROM orders o JOIN users u ON u.id=o.buyer_id WHERE o.id=? AND o.buyer_id=?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, orderId);
            ps.setLong(2, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Order order = mapOrder(rs);
                loadItems(c, order);
                return order;
            }
        }
    }

    @Override
    public List<Order> findByBuyer(long buyerId) throws SQLException {
        String q = "SELECT o.id,o.order_ref,o.buyer_id,u.name AS buyer_name,o.total_amount,o.status,o.created_at " +
                "FROM orders o JOIN users u ON u.id=o.buyer_id WHERE o.buyer_id=? ORDER BY o.created_at DESC,o.id DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) orders.add(mapOrder(rs));
            }
            for (Order order : orders) loadItems(c, order);
        }
        return orders;
    }

    @Override
    public List<Order> findBySeller(long sellerId) throws SQLException {
        String q = "SELECT DISTINCT o.id,o.order_ref,o.buyer_id,u.name AS buyer_name,o.total_amount,o.status,o.created_at " +
                "FROM orders o JOIN users u ON u.id=o.buyer_id " +
                "JOIN order_items oi ON oi.order_id=o.id JOIN products p ON p.id=oi.product_id " +
                "WHERE p.seller_id=? ORDER BY o.created_at DESC,o.id DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) orders.add(mapOrder(rs));
            }
            for (Order order : orders) loadItems(c, order);
        }
        return orders;
    }

    @Override
    public boolean cancelPending(long orderId, long buyerId) throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            boolean previousAutoCommit = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                int changed;
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE orders SET status='CANCELLED' WHERE id=? AND buyer_id=? AND status='PENDING'")) {
                    ps.setLong(1, orderId);
                    ps.setLong(2, buyerId);
                    changed = ps.executeUpdate();
                }
                if (changed != 1) {
                    c.rollback();
                    return false;
                }

                String restore = "UPDATE products p SET stock_qty=p.stock_qty + " +
                        "(SELECT oi.quantity FROM order_items oi WHERE oi.order_id=? AND oi.product_id=p.id) " +
                        "WHERE p.id IN (SELECT product_id FROM order_items WHERE order_id=?)";
                try (PreparedStatement ps = c.prepareStatement(restore)) {
                    ps.setLong(1, orderId);
                    ps.setLong(2, orderId);
                    ps.executeUpdate();
                }
                c.commit();
                return true;
            } catch (Exception ex) {
                c.rollback();
                if (ex instanceof SQLException) throw (SQLException) ex;
                throw new SQLException("Unable to cancel order.", ex);
            } finally {
                c.setAutoCommit(previousAutoCommit);
            }
        }
    }

    @Override
    public boolean updateSellerStatus(long orderId, long sellerId, String newStatus) throws SQLException {
        if (!isSupportedStatus(newStatus)) return false;
        try (Connection c = dataSource.getConnection()) {
            String current;
            try (PreparedStatement ps = c.prepareStatement("SELECT status FROM orders WHERE id=? AND EXISTS (SELECT 1 FROM order_items oi JOIN products p ON p.id=oi.product_id WHERE oi.order_id=orders.id AND p.seller_id=?)")) {
                ps.setLong(1, orderId);
                ps.setLong(2, sellerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;
                    current = rs.getString("status");
                }
            }
            if (!isAllowedTransition(current, newStatus)) return false;
            try (PreparedStatement ps = c.prepareStatement("UPDATE orders SET status=? WHERE id=? AND status=?")) {
                ps.setString(1, newStatus);
                ps.setLong(2, orderId);
                ps.setString(3, current);
                return ps.executeUpdate() == 1;
            }
        }
    }

    private void loadItems(Connection c, Order order) throws SQLException {
        String q = "SELECT id,product_id,product_name,unit_price,quantity FROM order_items WHERE order_id=? ORDER BY id";
        try (PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, order.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    order.getItems().add(item);
                }
            }
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setOrderRef(rs.getString("order_ref"));
        order.setBuyerId(rs.getLong("buyer_id"));
        order.setBuyerName(rs.getString("buyer_name"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        return order;
    }

    private boolean isSupportedStatus(String status) {
        return "CONFIRMED".equals(status) || "SHIPPED".equals(status) || "DELIVERED".equals(status);
    }

    private boolean isAllowedTransition(String current, String next) {
        return ("PENDING".equals(current) && "CONFIRMED".equals(next)) ||
                ("CONFIRMED".equals(current) && "SHIPPED".equals(next)) ||
                ("SHIPPED".equals(current) && "DELIVERED".equals(next));
    }

    private String generateOrderRef() {
        return "NXM-" + REF_TIME.format(LocalDateTime.now()) + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
