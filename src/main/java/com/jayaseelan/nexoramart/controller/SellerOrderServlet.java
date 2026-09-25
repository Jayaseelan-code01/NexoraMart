package com.jayaseelan.nexoramart.controller;

import com.jayaseelan.nexoramart.dao.JdbcOrderDAO;
import com.jayaseelan.nexoramart.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/seller/orders")
public class SellerOrderServlet extends HttpServlet {
    private OrderService orderService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        orderService = new OrderService(new JdbcOrderDAO(ds));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"SELLER".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            long sellerId = ((Number) session.getAttribute("userId")).longValue();
            request.setAttribute("orders", orderService.sellerOrders(sellerId));
            request.getRequestDispatcher("/WEB-INF/views/seller/orders.jsp").forward(request, response);
        } catch (Exception e) {
            getServletContext().log("Seller orders failed", e);
            response.sendError(500, "Unable to load seller orders.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"SELLER".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            long sellerId = ((Number) session.getAttribute("userId")).longValue();
            long orderId = Long.parseLong(request.getParameter("orderId"));
            String status = request.getParameter("status");
            boolean updated = orderService.updateSellerStatus(orderId, sellerId, status);
            response.sendRedirect(request.getContextPath() + "/seller/orders?updated=" + updated);
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid order ID.");
        } catch (Exception e) {
            getServletContext().log("Seller order status update failed", e);
            response.sendError(500, "Unable to update order status.");
        }
    }
}
