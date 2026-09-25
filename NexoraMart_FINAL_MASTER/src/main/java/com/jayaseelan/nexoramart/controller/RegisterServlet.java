package com.jayaseelan.nexoramart.controller;

import com.jayaseelan.nexoramart.dao.JdbcUserDAO;
import com.jayaseelan.nexoramart.model.User;
import com.jayaseelan.nexoramart.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private AuthService service;

    @Override
    public void init() {
        service = new AuthService(
                new JdbcUserDAO((DataSource) getServletContext().getAttribute("dataSource"))
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            service.register(
                    request.getParameter("name"),
                    email,
                    password,
                    request.getParameter("role")
            );

            // Auto-login the newly created account so the user does not have
            // to enter the email/password again on the login page.
            User user = service.login(email, password);
            if (user == null) {
                throw new IllegalStateException("Account created, but automatic login failed.");
            }

            HttpSession session = request.getSession(true);
            request.changeSessionId();
            session.setMaxInactiveInterval(1800);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole());

            response.sendRedirect(request.getContextPath() + ("ADMIN".equals(user.getRole()) ? "/admin" : "SELLER".equals(user.getRole()) ? "/seller/dashboard" : "/dashboard"));
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        }
    }
}
