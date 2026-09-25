<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.jayaseelan.nexoramart.model.Order" %>
<%
    Order order = (Order) request.getAttribute("order");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Order Confirmed · NexoraMart</title>
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
<main class="success-page">
    <div class="success-card">
        <div class="success-icon">✓</div>
        <p class="tag">ORDER CONFIRMED</p>
        <h1>Thank you for your order!</h1>
        <p class="muted">Your order has been created successfully and your cart has been cleared.</p>
        <div class="order-ref"><span>Order Reference</span><b><%=order.getOrderRef()%></b></div>
        <div class="summary-row"><span>Status</span><b><%=order.getStatus()%></b></div>
        <div class="summary-row total"><span>Total Paid</span><b>₹<%=order.getTotalAmount()%></b></div>
        <div class="success-actions">
            <a class="button" href="<%=request.getContextPath()%>/marketplace">Continue Shopping →</a>
            <a class="details" href="<%=request.getContextPath()%>/orders">View My Orders</a> <a class="details" href="<%=request.getContextPath()%>/dashboard">Go to Dashboard</a>
        </div>
    </div>
</main>
<script src="<%=request.getContextPath()%>/js/theme.js"></script>
</body>
</html>
