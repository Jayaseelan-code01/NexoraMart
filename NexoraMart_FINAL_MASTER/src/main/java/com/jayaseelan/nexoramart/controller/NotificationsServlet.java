package com.jayaseelan.nexoramart.controller;

import com.google.gson.Gson;
import com.jayaseelan.nexoramart.dao.JdbcNotificationDAO;
import com.jayaseelan.nexoramart.model.Notification;
import com.jayaseelan.nexoramart.service.NotificationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@WebServlet("/notifications")
public class NotificationsServlet extends HttpServlet {
    private NotificationService service;
    private final Gson gson = new Gson();

    @Override public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        service = new NotificationService(new JdbcNotificationDAO(ds));
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Object idObj = session == null ? null : session.getAttribute("userId");
        String role = session == null ? null : (String) session.getAttribute("userRole");
        if (!(idObj instanceof Number) || role == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            List<Notification> notifications = service.forRole(((Number) idObj).longValue(), role);
            if ("json".equalsIgnoreCase(req.getParameter("format"))) {
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write(gson.toJson(Map.of("notifications", notifications, "count", notifications.size())));
                return;
            }
            req.setAttribute("notifications", notifications == null ? Collections.emptyList() : notifications);
            req.getRequestDispatcher("/WEB-INF/views/notifications.jsp").forward(req, resp);
        } catch (Exception e) {
            getServletContext().log("Notifications failed", e);
            resp.sendError(500, "Unable to load notifications.");
        }
    }
}
