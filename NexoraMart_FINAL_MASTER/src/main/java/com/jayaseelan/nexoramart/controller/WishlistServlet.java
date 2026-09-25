package com.jayaseelan.nexoramart.controller;

import com.jayaseelan.nexoramart.dao.JdbcProductDAO;
import com.jayaseelan.nexoramart.model.Product;
import com.jayaseelan.nexoramart.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/wishlist")
public class WishlistServlet extends HttpServlet {
    private ProductService service;

    @Override
    public void init() {
        service = new ProductService(new JdbcProductDAO((DataSource) getServletContext().getAttribute("dataSource")));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Product> products = new ArrayList<>();
        String raw = request.getParameter("ids");
        if (raw != null && !raw.isBlank()) {
            for (String part : raw.split(",")) {
                try {
                    Product p = service.byId(Long.parseLong(part.trim()));
                    if (p != null && products.stream().noneMatch(x -> x.getId() == p.getId())) products.add(p);
                } catch (NumberFormatException ignored) { }
                catch (Exception ignored) { }
                if (products.size() >= 30) break;
            }
        }
        request.setAttribute("wishlistProducts", products);
        request.getRequestDispatcher("/WEB-INF/views/wishlist.jsp").forward(request, response);
    }
}
