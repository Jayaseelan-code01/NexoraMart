<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Admin Analytics · NexoraMart</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
    <style>
        .analytics-page{max-width:1280px;margin:0 auto;padding:32px 20px 70px}
        .analytics-top{display:flex;justify-content:space-between;gap:16px;align-items:end;margin-bottom:26px}
        .analytics-top h1{margin:4px 0;font-size:clamp(30px,5vw,52px)}
        .analytics-nav{display:flex;gap:10px;flex-wrap:wrap}
        .kpi-grid{display:grid;grid-template-columns:repeat(5,minmax(0,1fr));gap:14px;margin:20px 0 24px}
        .kpi-card{padding:18px;border:1px solid var(--border,#e6e8ef);border-radius:18px;background:var(--surface,#fff);box-shadow:0 10px 30px rgba(20,26,40,.05)}
        .kpi-label{font-size:12px;text-transform:uppercase;letter-spacing:.12em;opacity:.68}.kpi-value{font-size:30px;font-weight:800;margin:8px 0 2px}.kpi-sub{font-size:12px;opacity:.68}
        .analytics-grid{display:grid;grid-template-columns:1.15fr .85fr;gap:18px;margin-bottom:18px}
        .panel{border:1px solid var(--border,#e6e8ef);border-radius:20px;background:var(--surface,#fff);padding:20px;box-shadow:0 10px 30px rgba(20,26,40,.05)}
        .panel h2{margin:0 0 4px}.panel .sub{margin:0 0 18px;opacity:.68;font-size:13px}
        .bar-row{margin:13px 0}.bar-head{display:flex;justify-content:space-between;font-size:13px;margin-bottom:6px}.bar-track{height:10px;background:rgba(127,127,127,.16);border-radius:999px;overflow:hidden}.bar-fill{height:100%;border-radius:999px;background:var(--brand,#2563eb)}
        .status-wrap{display:grid;grid-template-columns:repeat(2,1fr);gap:10px}.status-card{padding:14px;border-radius:16px;background:rgba(127,127,127,.08)}.status-name{text-transform:uppercase;font-size:11px;opacity:.7}.status-num{font-size:24px;font-weight:800;margin-top:5px}
        .stock-list{display:grid;gap:10px}.stock-row{display:flex;justify-content:space-between;align-items:center;gap:10px;padding:13px 14px;border-radius:14px;background:rgba(127,127,127,.08)}.stock-row b{display:block}.stock-row small{opacity:.7}.stock-badge{font-weight:800;padding:7px 10px;border-radius:999px;background:rgba(220,38,38,.11);color:#b42318;font-size:12px}
        .order-table{width:100%;border-collapse:collapse}.order-table th,.order-table td{padding:11px 8px;border-bottom:1px solid var(--border,#e6e8ef);text-align:left;font-size:13px}.order-table th{font-size:11px;text-transform:uppercase;letter-spacing:.09em;opacity:.66}.status-pill{display:inline-block;padding:6px 9px;border-radius:999px;background:rgba(127,127,127,.11);font-size:11px;font-weight:800}
        @media(max-width:1050px){.kpi-grid{grid-template-columns:repeat(3,1fr)}}
        @media(max-width:780px){.analytics-top{display:block}.analytics-grid{grid-template-columns:1fr}.kpi-grid{grid-template-columns:repeat(2,1fr)}.order-table{display:block;overflow-x:auto;white-space:nowrap}}
        @media(max-width:470px){.kpi-grid{grid-template-columns:1fr}.status-wrap{grid-template-columns:1fr}}
    </style>
</head>
<body>
<header class="nav">
    <b>NEXORA<span>MART</span></b>
    <a href="<%=request.getContextPath()%>/dashboard">Dashboard</a>
    <a href="<%=request.getContextPath()%>/admin">Admin Control</a>
    <a href="<%=request.getContextPath()%>/marketplace">Marketplace</a>
    <div class="nav-right"><span class="admin-badge">ANALYTICS</span><a class="button small" href="<%=request.getContextPath()%>/logout">Logout</a></div>
</header>
<main class="analytics-page">
    <div class="analytics-top">
        <div><p class="tag">NEXORA · ADMIN INTELLIGENCE</p><h1>Operations at a glance.</h1><p class="muted">Live inventory, order flow, users and revenue from the marketplace database.</p></div>
        <div class="analytics-nav"><a class="button small" href="<%=request.getContextPath()%>/admin">← Control Center</a><a class="button small" href="<%=request.getContextPath()%>/marketplace">Open Store</a></div>
    </div>

    <% Map<String,Object> k=(Map<String,Object>)request.getAttribute("kpis"); %>
    <section class="kpi-grid">
        <div class="kpi-card"><div class="kpi-label">Revenue</div><div class="kpi-value">₹<%=k.get("revenue")%></div><div class="kpi-sub">Non-cancelled orders</div></div>
        <div class="kpi-card"><div class="kpi-label">Orders</div><div class="kpi-value"><%=k.get("orders")%></div><div class="kpi-sub"><%=k.get("pendingOrders")%> pending</div></div>
        <div class="kpi-card"><div class="kpi-label">Users</div><div class="kpi-value"><%=k.get("users")%></div><div class="kpi-sub"><%=k.get("activeUsers")%> active</div></div>
        <div class="kpi-card"><div class="kpi-label">Products</div><div class="kpi-value"><%=k.get("products")%></div><div class="kpi-sub"><%=k.get("activeProducts")%> visible</div></div>
        <div class="kpi-card"><div class="kpi-label">Inventory watch</div><div class="kpi-value"><%=k.get("lowStockCount")%></div><div class="kpi-sub"><%=k.get("outOfStockCount")%> out of stock</div></div>
    </section>

    <div class="analytics-grid">
        <section class="panel"><h2>Order flow</h2><p class="sub">Current orders by operational status.</p>
            <% List<Map<String,Object>> statuses=(List<Map<String,Object>>)request.getAttribute("orderStatuses"); long max=1; for(Map<String,Object> s:statuses){max=Math.max(max,((Number)s.get("qty")).longValue());} %>
            <% if(statuses.isEmpty()){ %><p class="muted">No orders yet.</p><% } else { for(Map<String,Object> s:statuses){long qty=((Number)s.get("qty")).longValue(); int pct=(int)Math.max(4,Math.round((qty*100.0)/max));%>
                <div class="bar-row"><div class="bar-head"><span><b><%=s.get("status")%></b></span><span><%=qty%></span></div><div class="bar-track"><div class="bar-fill" style="width:<%=pct%>%"></div></div></div>
            <% }} %>
        </section>
        <section class="panel"><h2>Category footprint</h2><p class="sub">Product count and available units by category.</p>
            <% List<Map<String,Object>> cats=(List<Map<String,Object>>)request.getAttribute("categories"); long catMax=1; for(Map<String,Object> c:cats){catMax=Math.max(catMax,((Number)c.get("productCount")).longValue());} %>
            <% if(cats.isEmpty()){ %><p class="muted">No catalog data yet.</p><% } else { for(Map<String,Object> c:cats){long count=((Number)c.get("productCount")).longValue(); int pct=(int)Math.max(4,Math.round((count*100.0)/catMax));%>
                <div class="bar-row"><div class="bar-head"><span><b><%=c.get("category")%></b></span><span><%=count%> products · <%=c.get("units")%> units</span></div><div class="bar-track"><div class="bar-fill" style="width:<%=pct%>%"></div></div></div>
            <% }} %>
        </section>
    </div>

    <div class="analytics-grid">
        <section class="panel"><h2>Inventory alerts</h2><p class="sub">Products that need attention first.</p>
            <% List<Map<String,Object>> low=(List<Map<String,Object>>)request.getAttribute("lowStock"); %>
            <% if(low.isEmpty()){ %><p class="muted">Inventory is healthy right now.</p><% } else { %><div class="stock-list"><% for(Map<String,Object> p:low){ %>
                <div class="stock-row"><div><b><%=p.get("name")%></b><small><%=p.get("brand")%> · ₹<%=p.get("price")%></small></div><span class="stock-badge"><%=p.get("stock")%> left</span></div>
            <% } %></div><% } %>
        </section>
        <section class="panel"><h2>Quick health check</h2><p class="sub">Signals for admin triage.</p>
            <div class="status-wrap">
                <div class="status-card"><div class="status-name">Delivered</div><div class="status-num"><%=k.get("deliveredOrders")%></div></div>
                <div class="status-card"><div class="status-name">Pending</div><div class="status-num"><%=k.get("pendingOrders")%></div></div>
                <div class="status-card"><div class="status-name">Visible products</div><div class="status-num"><%=k.get("activeProducts")%></div></div>
                <div class="status-card"><div class="status-name">Low / out stock</div><div class="status-num"><%=((Number)k.get("lowStockCount")).longValue()+((Number)k.get("outOfStockCount")).longValue()%></div></div>
            </div>
        </section>
    </div>

    <section class="panel"><h2>Recent orders</h2><p class="sub">Latest customer activity.</p>
        <% List<Map<String,Object>> recent=(List<Map<String,Object>>)request.getAttribute("recentOrders"); %>
        <% if(recent.isEmpty()){ %><p class="muted">No recent orders.</p><% } else { %>
        <table class="order-table"><thead><tr><th>Reference</th><th>Buyer</th><th>Total</th><th>Status</th><th>Created</th></tr></thead><tbody>
        <% for(Map<String,Object> o:recent){ %><tr><td><b><%=o.get("ref")%></b></td><td><%=o.get("buyer")%></td><td>₹<%=o.get("total")%></td><td><span class="status-pill"><%=o.get("status")%></span></td><td><%=o.get("createdAt")%></td></tr><% } %>
        </tbody></table><% } %>
    </section>
</main>
<script src="<%=request.getContextPath()%>/js/theme.js"></script>
</body>
</html>
