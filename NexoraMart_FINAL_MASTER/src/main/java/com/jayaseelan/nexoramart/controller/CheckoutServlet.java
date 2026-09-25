package com.jayaseelan.nexoramart.controller;

import com.jayaseelan.nexoramart.dao.JdbcOrderDAO;
import com.jayaseelan.nexoramart.dao.JdbcProductDAO;
import com.jayaseelan.nexoramart.model.CartItem;
import com.jayaseelan.nexoramart.model.Order;
import com.jayaseelan.nexoramart.model.Product;
import com.jayaseelan.nexoramart.service.OrderService;
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

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private ProductService productService;
    private OrderService orderService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        productService = new ProductService(new JdbcProductDAO(ds));
        orderService = new OrderService(new JdbcOrderDAO(ds));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isBuyer(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            List<CartItem> items = buildItems(request.getSession());
            if (items.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
            BigDecimal total = items.stream().map(CartItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            request.setAttribute("checkoutItems", items);
            request.setAttribute("checkoutTotal", total);
            request.getRequestDispatcher("/WEB-INF/views/checkout/checkout.jsp").forward(request, response);
        } catch (Exception e) {
            getServletContext().log("Checkout page failed", e);
            response.sendError(500, "Unable to load checkout.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isBuyer(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            HttpSession session = request.getSession();
            long buyerId = ((Number) session.getAttribute("userId")).longValue();
            Map<Long, Integer> cart = getCart(session);
            Order order = orderService.checkout(buyerId, cart);
            session.removeAttribute("cart");
            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/checkout/success.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("checkoutError", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            getServletContext().log("Checkout failed", e);
            request.setAttribute("checkoutError", "Unable to place your order right now.");
            doGet(request, response);
        }
    }

    private List<CartItem> buildItems(HttpSession session) throws Exception {
        Map<Long, Integer> cart = getCart(session);
        List<CartItem> items = new ArrayList<>();
        Map<Long, Integer> cleanCart = new LinkedHashMap<>();

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product product = productService.byId(entry.getKey());
            int requested = entry.getValue() == null ? 0 : entry.getValue();
            if (product == null || requested <= 0 || product.getStockQty() <= 0) continue;
            int safe = Math.min(requested, product.getStockQty());
            cleanCart.put(product.getId(), safe);
            items.add(new CartItem(product, safe));
        }
        session.setAttribute("cart", cleanCart);
        return items;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCart(HttpSession session) {
        Object value = session.getAttribute("cart");
        if (value instanceof Map) return (Map<Long, Integer>) value;
        Map<Long, Integer> cart = new LinkedHashMap<>();
        session.setAttribute("cart", cart);
        return cart;
    }

    private boolean isBuyer(HttpServletRequest request) {
        return "BUYER".equals(request.getSession().getAttribute("userRole"));
    }
}
