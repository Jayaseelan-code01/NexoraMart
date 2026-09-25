<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,java.math.BigDecimal,com.jayaseelan.nexoramart.model.CartItem" %>
<%
    List<CartItem> items = (List<CartItem>) request.getAttribute("cartItems");
    BigDecimal total = (BigDecimal) request.getAttribute("cartTotal");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Your Cart · NexoraMart</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
<header class="nav">
    <b>NEXORA<span>MART</span></b>
    <a href="<%=request.getContextPath()%>/marketplace">Marketplace</a>
    <div class="nav-right">
        <a href="<%=request.getContextPath()%>/dashboard">Dashboard</a>
        <a class="button small" href="<%=request.getContextPath()%>/logout">Logout</a>
    </div>
</header>
<main class="cart-page">
    <p class="tag">BUYER CART</p>
    <h1>Your Cart</h1>
    <p class="muted">Review quantities, remove products, and check your current total before checkout.</p>

    <% if (request.getParameter("added") != null) { %>
        <div class="success">Product added to your cart.</div>
    <% } else if (request.getParameter("updated") != null) { %>
        <div class="success">Cart quantity updated.</div>
    <% } else if (request.getParameter("removed") != null) { %>
        <div class="success">Product removed from cart.</div>
    <% } %>
    <% if (error != null) { %>
        <div class="alert"><%=error%></div>
    <% } %>

    <% if (items == null || items.isEmpty()) { %>
        <section class="empty-cart">
            <div class="empty-icon">🛒</div>
            <h2>Your cart is empty</h2>
            <p class="muted">Add a product from the marketplace to see it here.</p>
            <a class="button" href="<%=request.getContextPath()%>/marketplace">Browse Products →</a>
        </section>
    <% } else { %>
        <section class="cart-layout">
            <div class="cart-list">
                <% for (CartItem item : items) { %>
                    <article class="cart-item">
                        <img src="<%=request.getContextPath()%>/<%=item.getProduct().getImageUrl1()%>" alt="<%=item.getProduct().getName()%>">
                        <div class="cart-info">
                            <small><%=item.getProduct().getBrand()%> · <%=item.getProduct().getModel()%></small>
                            <h2><%=item.getProduct().getName()%></h2>
                            <div class="cart-meta">₹<%=item.getProduct().getPrice()%> each · Stock <%=item.getProduct().getStockQty()%></div>
                            <strong>₹<%=item.getSubtotal()%></strong>
                        </div>
                        <div class="cart-actions">
                            <form method="post" action="<%=request.getContextPath()%>/cart" class="qty-form">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="productId" value="<%=item.getProduct().getId()%>">
                                <input type="number" name="quantity" value="<%=item.getQuantity()%>" min="1" max="<%=item.getProduct().getStockQty()%>">
                                <button type="submit">Update</button>
                            </form>
                            <form method="post" action="<%=request.getContextPath()%>/cart">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="productId" value="<%=item.getProduct().getId()%>">
                                <button class="remove-btn" type="submit">Remove</button>
                            </form>
                        </div>
                    </article>
                <% } %>
            </div>
            <aside class="cart-summary">
                <p class="tag">ORDER SUMMARY</p>
                <div class="summary-row"><span>Items</span><b><%=request.getAttribute("cartCount")%></b></div>
                <div class="summary-row"><span>Subtotal</span><b>₹<%=total%></b></div>
                <div class="summary-row total"><span>Total</span><b>₹<%=total%></b></div>
                <a class="button" href="<%=request.getContextPath()%>/checkout">Proceed to Checkout →</a>
                <p class="muted small-note">Review your order before placing it.</p>
            </aside>
        </section>
    <% } %>
</main>
<script src="<%=request.getContextPath()%>/js/theme.js"></script>
</body>
</html>
