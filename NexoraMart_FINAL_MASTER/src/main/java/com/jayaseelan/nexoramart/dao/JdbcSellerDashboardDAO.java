package com.jayaseelan.nexoramart.dao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class JdbcSellerDashboardDAO implements SellerDashboardDAO {
    private final DataSource dataSource;

    public JdbcSellerDashboardDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Map<String,Object> loadStats(long sellerId) throws SQLException {
        Map<String,Object> out = new LinkedHashMap<>();
        String q = "SELECT " +
                "COUNT(*) AS product_count, " +
                "COALESCE(SUM(CASE WHEN active=TRUE THEN 1 ELSE 0 END),0) AS active_products, " +
                "COALESCE(SUM(CASE WHEN active=TRUE AND stock_qty BETWEEN 1 AND 5 THEN 1 ELSE 0 END),0) AS low_stock, " +
                "COALESCE(SUM(CASE WHEN active=TRUE AND stock_qty=0 THEN 1 ELSE 0 END),0) AS out_stock " +
                "FROM products WHERE seller_id=?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.put("productCount", rs.getInt("product_count"));
                    out.put("activeProducts", rs.getInt("active_products"));
                    out.put("lowStock", rs.getInt("low_stock"));
                    out.put("outOfStock", rs.getInt("out_stock"));
                }
            }
        }

        String orderQ = "SELECT " +
                "COUNT(DISTINCT o.id) AS order_count, " +
                "COALESCE(SUM(oi.quantity),0) AS units_sold, " +
                "COALESCE(SUM(oi.quantity * oi.unit_price),0) AS revenue, " +
                "COALESCE(SUM(CASE WHEN o.status='PENDING' THEN 1 ELSE 0 END),0) AS pending_lines, " +
                "COALESCE(AVG(rv.rating),0) AS avg_rating, " +
                "COUNT(rv.id) AS review_count " +
                "FROM orders o " +
                "JOIN order_items oi ON oi.order_id=o.id " +
                "JOIN products p ON p.id=oi.product_id " +
                "LEFT JOIN reviews rv ON rv.product_id=p.id " +
                "WHERE p.seller_id=? AND o.status <> 'CANCELLED'";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(orderQ)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.put("orderCount", rs.getInt("order_count"));
                    out.put("unitsSold", rs.getInt("units_sold"));
                    out.put("revenue", rs.getBigDecimal("revenue"));
                    out.put("avgRating", rs.getBigDecimal("avg_rating"));
                    out.put("reviewCount", rs.getInt("review_count"));
                }
            }
        }

        // Pending orders need distinct order counting so an order containing multiple
        // products from this seller is counted once.
        String pendingQ = "SELECT COUNT(DISTINCT o.id) FROM orders o " +
                "JOIN order_items oi ON oi.order_id=o.id " +
                "JOIN products p ON p.id=oi.product_id " +
                "WHERE p.seller_id=? AND o.status='PENDING'";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(pendingQ)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) out.put("pendingOrders", rs.getInt(1));
            }
        }
        return out;
    }

    @Override
    public List<Map<String,Object>> loadTopProducts(long sellerId) throws SQLException {
        List<Map<String,Object>> out = new ArrayList<>();
        String q = "SELECT p.id,p.name,p.category,p.price,p.stock_qty,p.image_url_1 AS image_url1," +
                "COALESCE((SELECT SUM(oi.quantity) FROM order_items oi JOIN orders o ON o.id=oi.order_id " +
                "WHERE oi.product_id=p.id AND o.status<>'CANCELLED'),0) AS units_sold," +
                "COALESCE((SELECT SUM(oi.quantity*oi.unit_price) FROM order_items oi JOIN orders o ON o.id=oi.order_id " +
                "WHERE oi.product_id=p.id AND o.status<>'CANCELLED'),0) AS revenue," +
                "COALESCE((SELECT AVG(rv.rating) FROM reviews rv WHERE rv.product_id=p.id),0) AS rating " +
                "FROM products p WHERE p.seller_id=? ORDER BY units_sold DESC, revenue DESC, p.created_at DESC LIMIT 8";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("id", rs.getLong("id"));
                    m.put("name", rs.getString("name"));
                    m.put("category", rs.getString("category"));
                    m.put("price", rs.getBigDecimal("price"));
                    m.put("stock", rs.getInt("stock_qty"));
                    m.put("imageUrl1", rs.getString("image_url1"));
                    m.put("unitsSold", rs.getInt("units_sold"));
                    m.put("revenue", rs.getBigDecimal("revenue"));
                    m.put("rating", rs.getBigDecimal("rating"));
                    out.add(m);
                }
            }
        }
        return out;
    }

    @Override
    public List<Map<String,Object>> loadRecentOrders(long sellerId) throws SQLException {
        List<Map<String,Object>> out = new ArrayList<>();
        String q = "SELECT DISTINCT o.id,o.order_ref,o.status,o.total_amount,o.created_at,u.name AS buyer_name " +
                "FROM orders o JOIN users u ON u.id=o.buyer_id " +
                "JOIN order_items oi ON oi.order_id=o.id JOIN products p ON p.id=oi.product_id " +
                "WHERE p.seller_id=? ORDER BY o.created_at DESC,o.id DESC LIMIT 8";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("id", rs.getLong("id"));
                    m.put("ref", rs.getString("order_ref"));
                    m.put("status", rs.getString("status"));
                    m.put("total", rs.getBigDecimal("total_amount"));
                    m.put("createdAt", rs.getTimestamp("created_at"));
                    m.put("buyer", rs.getString("buyer_name"));
                    out.add(m);
                }
            }
        }
        return out;
    }

    @Override
    public List<Map<String,Object>> loadCategoryBreakdown(long sellerId) throws SQLException {
        List<Map<String,Object>> out = new ArrayList<>();
        String q = "SELECT category,COUNT(*) AS product_count,COALESCE(SUM(stock_qty),0) AS total_stock " +
                "FROM products WHERE seller_id=? GROUP BY category ORDER BY product_count DESC, category";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("category", rs.getString("category"));
                    m.put("count", rs.getInt("product_count"));
                    m.put("stock", rs.getInt("total_stock"));
                    out.add(m);
                }
            }
        }
        return out;
    }
}
