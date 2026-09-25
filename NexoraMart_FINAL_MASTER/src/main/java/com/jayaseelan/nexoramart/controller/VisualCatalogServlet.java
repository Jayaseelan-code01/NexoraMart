package com.jayaseelan.nexoramart.controller;

import com.google.gson.Gson;
import com.jayaseelan.nexoramart.dao.JdbcProductDAO;
import com.jayaseelan.nexoramart.model.Product;
import com.jayaseelan.nexoramart.service.ProductService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supplies the active product catalog to the browser for Visual Search.
 * No uploaded image is sent to this endpoint; the browser performs the
 * visual comparison locally against catalog images.
 */
@WebServlet("/api/visual-catalog")
public class VisualCatalogServlet extends HttpServlet {
    private ProductService service;
    private final Gson gson = new Gson();

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        service = new ProductService(new JdbcProductDAO(ds));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            List<Product> products = service.browse(null, null);
            List<Map<String, Object>> out = new ArrayList<>();
            for (Product p : products) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", p.getId());
                item.put("name", p.getName());
                item.put("brand", p.getBrand());
                item.put("model", p.getModel());
                item.put("category", p.getCategory());
                item.put("price", p.getPrice());
                item.put("stock", p.getStockQty());
                item.put("ram", p.getRam());
                item.put("storage", p.getStorage());
                item.put("display", p.getDisplaySize());
                item.put("camera", p.getCamera());
                item.put("battery", p.getBattery());
                List<String> images = new ArrayList<>();
                if (p.getImageUrl1() != null && !p.getImageUrl1().isBlank()) images.add(p.getImageUrl1());
                if (p.getImageUrl2() != null && !p.getImageUrl2().isBlank()) images.add(p.getImageUrl2());
                if (p.getImageUrl3() != null && !p.getImageUrl3().isBlank()) images.add(p.getImageUrl3());
                item.put("images", images);
                out.add(item);
            }
            response.getWriter().write(gson.toJson(out));
        } catch (Exception e) {
            getServletContext().log("Visual catalog failed", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unable to load visual catalog.\"}");
        }
    }
}
