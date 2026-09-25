package com.jayaseelan.nexoramart.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String role = String.valueOf(session.getAttribute("userRole"));
        if ("SELLER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/seller/dashboard");
            return;
        }
        if ("ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/admin");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }
}
