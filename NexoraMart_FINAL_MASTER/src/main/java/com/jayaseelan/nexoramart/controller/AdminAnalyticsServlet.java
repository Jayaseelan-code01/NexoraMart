package com.jayaseelan.nexoramart.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

@WebServlet("/admin/analytics")
public class AdminAnalyticsServlet extends HttpServlet {
    private DataSource ds;

    @Override
    public void init() {
        ds = (DataSource) getServletContext().getAttribute("dataSource");
    }

    private boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("userRole"))) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!requireAdmin(req, resp)) return;
        try (Connection c = ds.getConnection()) {
            Map<String, Object> kpis = loadKpis(c);
            List<Map<String, Object>> orderStatuses = loadOrderStatuses(c);
            List<Map<String, Object>> categories = loadCategories(c);
            List<Map<String, Object>> lowStock = loadLowStock(c);
            List<Map<String, Object>> recentOrders = loadRecentOrders(c);

            req.setAttribute("kpis", kpis);
            req.setAttribute("orderStatuses", orderStatuses);
            req.setAttribute("categories", categories);
            req.setAttribute("lowStock", lowStock);
            req.setAttribute("recentOrders", recentOrders);
            req.getRequestDispatcher("/WEB-INF/views/admin-analytics.jsp").forward(req, resp);
        } catch (Exception e) {
            getServletContext().log("Admin analytics failed", e);
            resp.sendError(500, "Unable to load analytics.");
        }
    }

    private Map<String, Object> loadKpis(Connection c) throws SQLException {
        Map<String, Object> k = new LinkedHashMap<>();
        k.put("users", scalarLong(c, "SELECT COUNT(*) FROM users"));
        k.put("activeUsers", scalarLong(c, "SELECT COUNT(*) FROM users WHERE active=TRUE"));
        k.put("products", scalarLong(c, "SELECT COUNT(*) FROM products"));
        k.put("activeProducts", scalarLong(c, "SELECT COUNT(*) FROM products WHERE active=TRUE"));
        k.put("orders", scalarLong(c, "SELECT COUNT(*) FROM orders"));
        k.put("pendingOrders", scalarLong(c, "SELECT COUNT(*) FROM orders WHERE status='PENDING'"));
        k.put("deliveredOrders", scalarLong(c, "SELECT COUNT(*) FROM orders WHERE status='DELIVERED'"));
        k.put("revenue", scalarDecimal(c, "SELECT COALESCE(SUM(total_amount),0) FROM orders WHERE status <> 'CANCELLED'"));
        k.put("lowStockCount", scalarLong(c, "SELECT COUNT(*) FROM products WHERE active=TRUE AND stock_qty BETWEEN 1 AND 5"));
        k.put("outOfStockCount", scalarLong(c, "SELECT COUNT(*) FROM products WHERE active=TRUE AND stock_qty=0"));
        return k;
    }

    private List<Map<String, Object>> loadOrderStatuses(Connection c) throws SQLException {
        List<Map<String, Object>> out = new ArrayList<>();
        String sql = "SELECT status, COUNT(*) qty FROM orders GROUP BY status " +
                     "ORDER BY CASE status WHEN 'PENDING' THEN 1 WHEN 'CONFIRMED' THEN 2 WHEN 'SHIPPED' THEN 3 WHEN 'DELIVERED' THEN 4 WHEN 'CANCELLED' THEN 5 ELSE 6 END";
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("status", rs.getString("status"));
                m.put("qty", rs.getLong("qty"));
                out.add(m);
            }
        }
        return out;
    }

    private List<Map<String, Object>> loadCategories(Connection c) throws SQLException {
        List<Map<String, Object>> out = new ArrayList<>();
        String sql = "SELECT category, COUNT(*) product_count, COALESCE(SUM(stock_qty),0) units " +
                     "FROM products GROUP BY category ORDER BY product_count DESC, category ASC";
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("category", rs.getString("category"));
                m.put("productCount", rs.getLong("product_count"));
                m.put("units", rs.getLong("units"));
                out.add(m);
            }
        }
        return out;
    }

    private List<Map<String, Object>> loadLowStock(Connection c) throws SQLException {
        List<Map<String, Object>> out = new ArrayList<>();
        String sql = "SELECT id,name,brand,stock_qty,price FROM products " +
                     "WHERE active=TRUE AND stock_qty <= 5 ORDER BY stock_qty ASC, name ASC LIMIT 10";
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", rs.getLong("id"));
                m.put("name", rs.getString("name"));
                m.put("brand", rs.getString("brand"));
                m.put("stock", rs.getInt("stock_qty"));
                m.put("price", rs.getBigDecimal("price"));
                out.add(m);
            }
        }
        return out;
    }

    private List<Map<String, Object>> loadRecentOrders(Connection c) throws SQLException {
        List<Map<String, Object>> out = new ArrayList<>();
        String sql = "SELECT o.order_ref, o.total_amount, o.status, o.created_at, u.name buyer " +
                     "FROM orders o JOIN users u ON u.id=o.buyer_id ORDER BY o.created_at DESC, o.id DESC LIMIT 8";
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("ref", rs.getString("order_ref"));
                m.put("total", rs.getBigDecimal("total_amount"));
                m.put("status", rs.getString("status"));
                m.put("createdAt", rs.getTimestamp("created_at"));
                m.put("buyer", rs.getString("buyer"));
                out.add(m);
            }
        }
        return out;
    }

    private long scalarLong(Connection c, String sql) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }

    private BigDecimal scalarDecimal(Connection c, String sql) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            rs.next();
            BigDecimal v = rs.getBigDecimal(1);
            return v == null ? BigDecimal.ZERO : v;
        }
    }
}
