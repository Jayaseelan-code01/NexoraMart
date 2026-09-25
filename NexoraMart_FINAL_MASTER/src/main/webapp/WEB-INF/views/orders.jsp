<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,java.text.SimpleDateFormat,com.jayaseelan.nexoramart.model.Order,com.jayaseelan.nexoramart.model.OrderItem" %>
<% List<Order> orders=(List<Order>)request.getAttribute("orders"); SimpleDateFormat fmt=new SimpleDateFormat("dd MMM yyyy, hh:mm a"); %>
<!DOCTYPE html><html><head><title>My Orders · NexoraMart</title><link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"></head>
<body><header class="nav"><b>NEXORA<span>MART</span></b><a href="<%=request.getContextPath()%>/marketplace">Marketplace</a><a href="<%=request.getContextPath()%>/cart">Cart 🛒</a><div class="nav-right"><a href="<%=request.getContextPath()%>/dashboard">Dashboard</a><a class="button small" href="<%=request.getContextPath()%>/logout">Logout</a></div></header>
<main class="orders-page"><p class="tag">BUYER AREA</p><h1>My Orders</h1><p class="muted">Track your orders and cancel a pending order when needed.</p>
<% if("true".equals(request.getParameter("cancelled"))){ %><div class="success">Order cancelled successfully. Stock has been restored.</div><% } else if("false".equals(request.getParameter("cancelled"))){ %><div class="alert">Only a pending order can be cancelled.</div><% } %>
<% if(orders==null||orders.isEmpty()) { %><section class="empty-orders"><div class="empty-icon">📦</div><h2>No orders yet</h2><p class="muted">Complete a checkout and your orders will appear here.</p><a class="button" href="<%=request.getContextPath()%>/marketplace">Start Shopping →</a></section>
<% } else { for(Order order:orders){ %>
<section class="order-card"><div class="order-head"><div><p class="tag">ORDER REFERENCE</p><h2><%=order.getOrderRef()%></h2><span class="muted"><%=fmt.format(order.getCreatedAt())%></span></div><span class="status-pill status-<%=order.getStatus().toLowerCase()%>"><%=order.getStatus()%></span></div>
<div class="order-items"><% for(OrderItem item:order.getItems()){ %><div class="order-item-row"><span><%=item.getProductName()%> × <%=item.getQuantity()%></span><b>₹<%=item.getSubtotal()%></b></div><% } %></div>
<div class="order-foot"><div><span class="muted">Total</span><strong>₹<%=order.getTotalAmount()%></strong></div><% if("PENDING".equals(order.getStatus())){ %><form method="post" action="<%=request.getContextPath()%>/orders" onsubmit="return confirm('Cancel this pending order?');"><input type="hidden" name="action" value="cancel"><input type="hidden" name="orderId" value="<%=order.getId()%>"><button class="remove-btn" type="submit">Cancel Order</button></form><% } %></div>
</section><% }} %></main><script src="<%=request.getContextPath()%>/js/theme.js"></script>
</body></html>
