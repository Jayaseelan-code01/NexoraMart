package com.jayaseelan.nexoramart.controller;

import com.jayaseelan.nexoramart.dao.JdbcProductDAO;
import com.jayaseelan.nexoramart.model.CartItem;
import com.jayaseelan.nexoramart.model.Product;
import com.jayaseelan.nexoramart.service.ProductService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private ProductService productService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        productService = new ProductService(new JdbcProductDAO(ds));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isBuyer(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        showCart(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isBuyer(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        try {
            if ("add".equalsIgnoreCase(action)) {
                addToCart(request);
                response.sendRedirect(request.getContextPath() + "/cart?added=1");
                return;
            }
            if ("update".equalsIgnoreCase(action)) {
                updateCart(request);
                response.sendRedirect(request.getContextPath() + "/cart?updated=1");
                return;
            }
            if ("remove".equalsIgnoreCase(action)) {
                removeFromCart(request);
                response.sendRedirect(request.getContextPath() + "/cart?removed=1");
                return;
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown cart action.");
        } catch (IllegalArgumentException e) {
            showCart(request, response, e.getMessage());
        } catch (Exception e) {
            getServletContext().log("Cart operation failed", e);
            showCart(request, response, "Unable to update cart right now.");
        }
    }

    private void addToCart(HttpServletRequest request) throws Exception {
        long productId = parseId(request.getParameter("productId"));
        int quantity = parsePositiveQuantity(request.getParameter("quantity"), 1);
        Product product = requireProduct(productId);
        Map<Long, Integer> cart = cart(request.getSession(true));
        int current = cart.getOrDefault(productId, 0);
        int requested = current + quantity;
        validateStock(product, requested);
        cart.put(productId, requested);
    }

    private void updateCart(HttpServletRequest request) throws Exception {
        long productId = parseId(request.getParameter("productId"));
        int quantity = parseNonNegativeQuantity(request.getParameter("quantity"));
        Product product = requireProduct(productId);
        Map<Long, Integer> cart = cart(request.getSession(true));
        if (quantity == 0) {
            cart.remove(productId);
            return;
        }
        validateStock(product, quantity);
        cart.put(productId, quantity);
    }

    private void removeFromCart(HttpServletRequest request) {
        long productId = parseId(request.getParameter("productId"));
        cart(request.getSession(true)).remove(productId);
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> cart(HttpSession session) {
        Object existing = session.getAttribute("cart");
        if (existing instanceof Map) {
            return (Map<Long, Integer>) existing;
        }
        Map<Long, Integer> created = new LinkedHashMap<>();
        session.setAttribute("cart", created);
        return created;
    }

    private void showCart(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        try {
            Map<Long, Integer> rawCart = cart(request.getSession(true));
            List<CartItem> items = new ArrayList<>();
            List<Long> staleIds = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            for (Map.Entry<Long, Integer> entry : rawCart.entrySet()) {
                Product product = productService.byId(entry.getKey());
                if (product == null || entry.getValue() == null || entry.getValue() <= 0) {
                    staleIds.add(entry.getKey());
                    continue;
                }
                int safeQuantity = Math.min(entry.getValue(), Math.max(product.getStockQty(), 0));
                if (safeQuantity <= 0) {
                    staleIds.add(entry.getKey());
                    continue;
                }
                if (safeQuantity != entry.getValue()) {
                    rawCart.put(entry.getKey(), safeQuantity);
                }
                CartItem item = new CartItem(product, safeQuantity);
                items.add(item);
                total = total.add(item.getSubtotal());
            }
            for (Long staleId : staleIds) {
                rawCart.remove(staleId);
            }

            request.setAttribute("cartItems", items);
            request.setAttribute("cartTotal", total);
            request.setAttribute("cartCount", rawCart.values().stream().mapToInt(Integer::intValue).sum());
            request.setAttribute("error", error);
            request.getRequestDispatcher("/WEB-INF/views/cart/cart.jsp").forward(request, response);
        } catch (Exception e) {
            getServletContext().log("Cart display failed", e);
            response.sendError(500, "Unable to load cart.");
        }
    }

    private Product requireProduct(long id) throws Exception {
        Product p = productService.byId(id);
        if (p == null) {
            throw new IllegalArgumentException("Product is no longer available.");
        }
        if (p.getStockQty() <= 0) {
            throw new IllegalArgumentException("This product is out of stock.");
        }
        return p;
    }

    private void validateStock(Product product, int quantity) {
        if (quantity > product.getStockQty()) {
            throw new IllegalArgumentException(
                    "Only " + product.getStockQty() + " unit(s) available for " + product.getName() + ".");
        }
    }

    private long parseId(String value) {
        try {
            long id = Long.parseLong(value);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid product.");
        }
    }

    private int parsePositiveQuantity(String value, int fallback) {
        if (value == null || value.isBlank()) return fallback;
        try {
            int q = Integer.parseInt(value);
            if (q <= 0) throw new NumberFormatException();
            return q;
        } catch (Exception e) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
    }

    private int parseNonNegativeQuantity(String value) {
        try {
            int q = Integer.parseInt(value);
            if (q < 0) throw new NumberFormatException();
            return q;
        } catch (Exception e) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
    }

    private boolean isBuyer(HttpServletRequest request) {
        return "BUYER".equals(request.getSession().getAttribute("userRole"));
    }
}
