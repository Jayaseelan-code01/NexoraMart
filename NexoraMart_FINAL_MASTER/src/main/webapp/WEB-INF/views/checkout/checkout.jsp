<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,java.math.BigDecimal,com.jayaseelan.nexoramart.model.CartItem" %>
<%
    List<CartItem> items = (List<CartItem>) request.getAttribute("checkoutItems");
    BigDecimal total = (BigDecimal) request.getAttribute("checkoutTotal");
    String error = (String) request.getAttribute("checkoutError");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Checkout · NexoraMart</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
<header class="nav">
    <b>NEXORA<span>MART</span></b>
    <a href="<%=request.getContextPath()%>/marketplace">Marketplace</a>
    <div class="nav-right">
        <a href="<%=request.getContextPath()%>/cart">Cart 🛒</a>
        <a href="<%=request.getContextPath()%>/dashboard">Dashboard</a>
        <a class="button small" href="<%=request.getContextPath()%>/logout">Logout</a>
    </div>
</header>
<main class="checkout-page">
    <p class="tag">SECURE CHECKOUT</p>
    <h1>Review & Place Order</h1>
    <p class="muted">Confirm your cart total and place the order. Stock is checked again during order creation.</p>

    <% if (error != null) { %>
        <div class="alert"><%=error%></div>
    <% } %>

    <section class="checkout-layout">
        <div class="checkout-list">
            <% for (CartItem item : items) { %>
                <article class="checkout-item">
                    <img src="<%=request.getContextPath()%>/<%=item.getProduct().getImageUrl1()%>" alt="<%=item.getProduct().getName()%>">
                    <div>
                        <small><%=item.getProduct().getBrand()%> · <%=item.getProduct().getModel()%></small>
                        <h2><%=item.getProduct().getName()%></h2>
                        <p class="muted">₹<%=item.getProduct().getPrice()%> × <%=item.getQuantity()%></p>
                    </div>
                    <strong>₹<%=item.getSubtotal()%></strong>
                </article>
            <% } %>
        </div>
        <aside class="checkout-summary">
            <p class="tag">ORDER SUMMARY</p>
            <div class="summary-row"><span>Items</span><b><%=items.stream().mapToInt(CartItem::getQuantity).sum()%></b></div>
            <div class="summary-row total"><span>Total</span><b>₹<%=total%></b></div>
            <form method="post" action="<%=request.getContextPath()%>/checkout">
                <button class="button full" type="submit">Place Order →</button>
            </form>
            <a class="details full-link" href="<%=request.getContextPath()%>/cart">← Back to Cart</a>
        </aside>
    </section>
</main>
<script src="<%=request.getContextPath()%>/js/theme.js"></script>
</body>
</html>
