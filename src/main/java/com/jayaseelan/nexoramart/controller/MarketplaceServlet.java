package com.jayaseelan.nexoramart.controller;

import com.jayaseelan.nexoramart.dao.JdbcProductDAO;
import com.jayaseelan.nexoramart.model.Product;
import com.jayaseelan.nexoramart.service.ProductService;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet("/marketplace")
public class MarketplaceServlet extends HttpServlet {
    private ProductService service;

    @Override
    public void init() {
        service = new ProductService(new JdbcProductDAO((DataSource) getServletContext().getAttribute("dataSource")));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String q = request.getParameter("q");
            String category = request.getParameter("category");
            List<Product> products = service.browse(q, category);
            request.setAttribute("products", products);
            request.setAttribute("searchTerm", q == null ? "" : q);
            request.setAttribute("selectedCategory", category == null ? "" : category);
            request.getRequestDispatcher("/WEB-INF/views/marketplace.jsp").forward(request, response);
        } catch (Exception e) {
            getServletContext().log("Marketplace failed", e);
            response.sendError(500, "Unable to load marketplace.");
        }
    }
}
