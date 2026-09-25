package com.jayaseelan.nexoramart.controller;

import com.jayaseelan.nexoramart.dao.JdbcSellerDashboardDAO;
import com.jayaseelan.nexoramart.service.SellerDashboardService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/seller/dashboard")
public class SellerDashboardServlet extends HttpServlet {
    private SellerDashboardService service;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        service = new SellerDashboardService(new JdbcSellerDashboardDAO(ds));
    }

    private Long seller(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Object id = session == null ? null : session.getAttribute("userId");
        Object role = session == null ? null : session.getAttribute("userRole");
        if (id == null || !"SELLER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        return ((Number) id).longValue();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long sellerId = seller(request, response);
        if (sellerId == null) return;
        try {
            request.setAttribute("stats", service.stats(sellerId));
            request.setAttribute("topProducts", service.topProducts(sellerId));
            request.setAttribute("recentOrders", service.recentOrders(sellerId));
            request.setAttribute("categories", service.categoryBreakdown(sellerId));
            request.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            getServletContext().log("Seller dashboard failed", e);
            request.setAttribute("stats", new java.util.LinkedHashMap<String,Object>());
            request.setAttribute("topProducts", new java.util.ArrayList<java.util.Map<String,Object>>());
            request.setAttribute("recentOrders", new java.util.ArrayList<java.util.Map<String,Object>>());
            request.setAttribute("categories", new java.util.ArrayList<java.util.Map<String,Object>>());
            request.setAttribute("dashboardError", "Some live metrics are temporarily unavailable. Your store tools remain available.");
            request.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(request, response);
        }
    }
}
