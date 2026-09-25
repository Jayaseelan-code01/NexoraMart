package com.jayaseelan.nexoramart.controller;

import com.jayaseelan.nexoramart.dao.JdbcOrderDAO;
import com.jayaseelan.nexoramart.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {
    private OrderService orderService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        orderService = new OrderService(new JdbcOrderDAO(ds));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"BUYER".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            long buyerId = ((Number) session.getAttribute("userId")).longValue();
            request.setAttribute("orders", orderService.buyerHistory(buyerId));
            request.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(request, response);
        } catch (Exception e) {
            getServletContext().log("Order history failed", e);
            response.sendError(500, "Unable to load order history.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"BUYER".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String action = request.getParameter("action");
        if (!"cancel".equals(action)) {
            response.sendError(400, "Invalid order action.");
            return;
        }
        try {
            long buyerId = ((Number) session.getAttribute("userId")).longValue();
            long orderId = Long.parseLong(request.getParameter("orderId"));
            boolean cancelled = orderService.cancelPending(orderId, buyerId);
            response.sendRedirect(request.getContextPath() + "/orders?cancelled=" + cancelled);
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid order ID.");
        } catch (Exception e) {
            getServletContext().log("Order cancellation failed", e);
            response.sendError(500, "Unable to cancel order.");
        }
    }
}
