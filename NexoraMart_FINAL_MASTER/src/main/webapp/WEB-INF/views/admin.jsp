<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,java.util.Map" %>
<!DOCTYPE html><html><head><title>Admin Control Center · NexoraMart</title><link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"></head><body>
<header class="nav"><b>NEXORA<span>MART</span></b><a href="<%=request.getContextPath()%>/dashboard">Dashboard</a><a href="<%=request.getContextPath()%>/marketplace">Marketplace</a><div class="nav-right"><span class="admin-badge">ADMIN CONTROL</span><a class="button small" href="<%=request.getContextPath()%>/admin/analytics">Analytics</a><a class="button small" href="<%=request.getContextPath()%>/logout">Logout</a></div></header>
<main class="admin-page">
<p class="tag">F7 · ADMIN MODERATION</p><h1>Control Center.</h1><p class="muted">Manage users, moderate products, update orders, and review security/audit activity.</p>
<% if(request.getParameter("updated")!=null){ %><div class="success">Admin action completed.</div><% } %>

<section class="admin-section"><div class="section-title"><div><p class="tag">USERS</p><h2>User moderation</h2></div><span class="count"><%=((List<?>)request.getAttribute("users")).size()%> accounts</span></div>
<div class="admin-table"><table><thead><tr><th>ID</th><th>User</th><th>Email</th><th>Role</th><th>Status</th><th>Action</th></tr></thead><tbody>
<% for(Map<String,Object> u:(List<Map<String,Object>>)request.getAttribute("users")){ %><tr><td>#<%=u.get("id")%></td><td><b><%=u.get("name")%></b></td><td><%=u.get("email")%></td><td><span class="status-pill"><%=u.get("role")%></span></td><td><span class="status-pill <%=Boolean.TRUE.equals(u.get("active"))?"status-delivered":"status-cancelled"%>"><%=Boolean.TRUE.equals(u.get("active"))?"ACTIVE":"BLOCKED"%></span></td><td><form method="post"><input type="hidden" name="action" value="toggle-user"><input type="hidden" name="id" value="<%=u.get("id")%>"><button class="button small" <%= "ADMIN".equals(u.get("role")) ? "disabled" : "" %>><%=Boolean.TRUE.equals(u.get("active"))?"Block":"Activate"%></button></form></td></tr><% } %>
</tbody></table></div></section>

<section class="admin-section"><div class="section-title"><div><p class="tag">PRODUCTS</p><h2>Product moderation</h2></div><span class="count"><%=((List<?>)request.getAttribute("products")).size()%> products</span></div>
<div class="admin-table"><table><thead><tr><th>Product</th><th>Seller</th><th>Category</th><th>Price</th><th>Stock</th><th>Status</th><th>Action</th></tr></thead><tbody>
<% for(Map<String,Object> p:(List<Map<String,Object>>)request.getAttribute("products")){ %><tr><td><b><%=p.get("name")%></b><small><%=p.get("brand")%></small></td><td><%=p.get("seller")%></td><td><%=p.get("category")%></td><td>₹<%=p.get("price")%></td><td><%=p.get("stock")%></td><td><span class="status-pill <%=Boolean.TRUE.equals(p.get("active"))?"status-delivered":"status-cancelled"%>"><%=Boolean.TRUE.equals(p.get("active"))?"VISIBLE":"HIDDEN"%></span></td><td><div class="admin-actions"><form method="post"><input type="hidden" name="action" value="toggle-product"><input type="hidden" name="id" value="<%=p.get("id")%>"><button class="button small"><%=Boolean.TRUE.equals(p.get("active"))?"Hide":"Publish"%></button></form><form method="post"><input type="hidden" name="action" value="delete-product"><input type="hidden" name="id" value="<%=p.get("id")%>"><button class="remove-btn">Deactivate</button></form></div></td></tr><% } %>
</tbody></table></div></section>

<section class="admin-section"><div class="section-title"><div><p class="tag">ORDERS</p><h2>Order moderation</h2></div><span class="count"><%=((List<?>)request.getAttribute("orders")).size()%> orders</span></div>
<div class="admin-table"><table><thead><tr><th>Order</th><th>Buyer</th><th>Total</th><th>Status</th><th>Change</th></tr></thead><tbody>
<% for(Map<String,Object> o:(List<Map<String,Object>>)request.getAttribute("orders")){ String st=String.valueOf(o.get("status")); %><tr><td><b><%=o.get("ref")%></b><small><%=o.get("createdAt")%></small></td><td><%=o.get("buyer")%><small><%=o.get("email")%></small></td><td>₹<%=o.get("total")%></td><td><span class="status-pill"><%=st%></span></td><td><form method="post" class="status-form"><input type="hidden" name="action" value="order-status"><input type="hidden" name="id" value="<%=o.get("id")%>"><select name="status"><option>PENDING</option><option>CONFIRMED</option><option>SHIPPED</option><option>DELIVERED</option><option>CANCELLED</option></select><button class="button small">Save</button></form></td></tr><% } %>
</tbody></table></div></section>

<section class="admin-section"><div class="section-title"><div><p class="tag">AUDIT LOGS</p><h2>Recent activity</h2></div><span class="count">Last 50 events</span></div>
<div class="audit-list"><% for(Map<String,Object> a:(List<Map<String,Object>>)request.getAttribute("audits")){ %><div class="audit-row"><div><b><%=a.get("action")%></b> · <span><%=a.get("entityType")%> #<%=a.get("entityId")%></span><p><%=a.get("details")%></p></div><small><%=a.get("actor")%><br><%=a.get("createdAt")%></small></div><% } if(((List<?>)request.getAttribute("audits")).isEmpty()){%><div class="empty-orders">No admin activity yet.</div><%}%></div></section>
</main><script src="<%=request.getContextPath()%>/js/theme.js"></script>
</body></html>
