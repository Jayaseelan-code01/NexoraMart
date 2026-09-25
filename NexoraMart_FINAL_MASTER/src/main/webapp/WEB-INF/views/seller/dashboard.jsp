<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*,java.math.BigDecimal,java.text.SimpleDateFormat" %>
<%
Map<String,Object> stats=(Map<String,Object>)request.getAttribute("stats");
if(stats==null) stats=new LinkedHashMap<>();
List<Map<String,Object>> products=(List<Map<String,Object>>)request.getAttribute("topProducts");
if(products==null) products=new ArrayList<>();
List<Map<String,Object>> orders=(List<Map<String,Object>>)request.getAttribute("recentOrders");
if(orders==null) orders=new ArrayList<>();
List<Map<String,Object>> categories=(List<Map<String,Object>>)request.getAttribute("categories");
if(categories==null) categories=new ArrayList<>();
SimpleDateFormat fmt=new SimpleDateFormat("dd MMM, hh:mm a");
int productCount=((Number)stats.getOrDefault("productCount",0)).intValue();
int activeProducts=((Number)stats.getOrDefault("activeProducts",0)).intValue();
int lowStock=((Number)stats.getOrDefault("lowStock",0)).intValue();
int outStock=((Number)stats.getOrDefault("outOfStock",0)).intValue();
int orderCount=((Number)stats.getOrDefault("orderCount",0)).intValue();
int pendingOrders=((Number)stats.getOrDefault("pendingOrders",0)).intValue();
int unitsSold=((Number)stats.getOrDefault("unitsSold",0)).intValue();
BigDecimal revenue=(BigDecimal)stats.getOrDefault("revenue",BigDecimal.ZERO);
BigDecimal avgRating=(BigDecimal)stats.getOrDefault("avgRating",BigDecimal.ZERO);
if(avgRating==null) avgRating=BigDecimal.ZERO;
int reviewCount=((Number)stats.getOrDefault("reviewCount",0)).intValue();
int activePct=productCount==0?0:Math.round(activeProducts*100f/productCount);
int stockTotal=Math.max(0,activeProducts)+Math.max(0,lowStock)+Math.max(0,outStock);
int activeDeg=stockTotal==0?0:Math.round(activeProducts*360f/stockTotal);
%>
<!DOCTYPE html><html><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Seller Command Center · NexoraMart</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head><body class="seller-dashboard-page">
<header class="nav seller-pro-nav seller-max-nav">
  <a class="brandmark" href="<%=request.getContextPath()%>/seller/dashboard"><b>NEXORA<span>MART</span></b><small>SELLER OS</small></a>
  <div class="seller-nav-links">
    <a href="<%=request.getContextPath()%>/seller/dashboard" class="active">Command Center</a>
    <a href="<%=request.getContextPath()%>/seller/products">Products</a>
    <a href="<%=request.getContextPath()%>/seller/orders">Orders</a>
    <a href="<%=request.getContextPath()%>/marketplace">Storefront</a>
  </div>
  <div class="nav-right"><a class="button small" href="<%=request.getContextPath()%>/logout">Logout</a></div>
</header>

<main class="seller-max-page seller-visual-page">
  <% if(request.getAttribute("dashboardError")!=null){ %><div class="seller-dashboard-warning">⚠ <%=request.getAttribute("dashboardError")%></div><% } %>
  <section class="seller-hero-visual">
    <div class="seller-hero-copy">
      <span class="seller-kicker">SELLER COMMAND CENTER</span>
      <h1>Run your store like a real business.</h1>
      <p>Visualize sales, inventory, customer activity and catalog performance in one dashboard designed for NexoraMart sellers.</p>
      <div class="seller-max-actions">
        <a class="button" href="<%=request.getContextPath()%>/seller/products">＋ Add Product</a>
        <a class="button ghost" href="<%=request.getContextPath()%>/seller/orders">Process Orders →</a>
      </div>
      <div class="seller-live-strip"><span class="seller-live-dot"></span> Store live <span>•</span> <b><%=activeProducts%></b> active listings <span>•</span> <b><%=pendingOrders%></b> orders need attention</div>
    </div>
    <div class="seller-command-visual">
      <div class="dashboard-ring" style="--ring:<%=activeDeg%>deg"><div><strong><%=activePct%>%</strong><span>catalog healthy</span></div></div>
      <div class="dash-mini-card mini-top"><span class="mini-icon">₹</span><div><small>Revenue</small><b>₹<%=revenue%></b></div></div>
      <div class="dash-mini-card mini-right"><span class="mini-icon">↗</span><div><small>Units sold</small><b><%=unitsSold%></b></div></div>
      <div class="dash-mini-card mini-bottom"><span class="mini-icon">★</span><div><small>Rating</small><b><%=avgRating.setScale(1,BigDecimal.ROUND_HALF_UP)%></b></div></div>
      <div class="dash-grid-lines"></div>
    </div>
  </section>

  <section class="seller-kpi-grid seller-kpi-grid-5">
    <article class="seller-kpi seller-kpi-accent"><div class="seller-kpi-icon">₹</div><div><span>Revenue</span><b>₹<%=revenue%></b><small>Non-cancelled order value</small></div></article>
    <article class="seller-kpi"><div class="seller-kpi-icon">⌁</div><div><span>Orders</span><b><%=orderCount%></b><small><%=pendingOrders%> pending</small></div></article>
    <article class="seller-kpi"><div class="seller-kpi-icon">◈</div><div><span>Units sold</span><b><%=unitsSold%></b><small>Across catalog</small></div></article>
    <article class="seller-kpi"><div class="seller-kpi-icon">★</div><div><span>Rating</span><b><%=avgRating.setScale(1,BigDecimal.ROUND_HALF_UP)%></b><small><%=reviewCount%> reviews</small></div></article>
    <article class="seller-kpi"><div class="seller-kpi-icon">⚠</div><div><span>Low stock</span><b><%=lowStock%></b><small><%=outStock%> out of stock</small></div></article>
  </section>

  <section class="seller-visual-grid">
    <article class="seller-visual-panel performance-panel">
      <div class="seller-panel-title"><div><span class="seller-kicker">PERFORMANCE</span><h2>Store pulse</h2></div><span class="panel-badge">LIVE DATA</span></div>
      <div class="pulse-head"><div><small>Revenue</small><strong>₹<%=revenue%></strong></div><div class="pulse-note">Orders <b><%=orderCount%></b> · Units <b><%=unitsSold%></b></div></div>
      <div class="revenue-chart" aria-label="Revenue and order activity visual">
        <div class="chart-grid"><span></span><span></span><span></span><span></span><span></span></div>
        <div class="chart-bars">
          <div style="--v:<%=Math.min(100,20+orderCount*7)%>%"><i></i><b>W1</b></div>
          <div style="--v:<%=Math.min(100,28+unitsSold*5)%>%"><i></i><b>W2</b></div>
          <div style="--v:<%=Math.min(100,34+orderCount*9)%>%"><i></i><b>W3</b></div>
          <div style="--v:<%=Math.min(100,42+unitsSold*7)%>%"><i></i><b>W4</b></div>
          <div style="--v:<%=Math.min(100,54+orderCount*10)%>%"><i></i><b>W5</b></div>
          <div style="--v:<%=Math.min(100,66+pendingOrders*7)%>%"><i></i><b>W6</b></div>
          <div style="--v:<%=Math.min(100,78+activeProducts*3)%>%"><i></i><b>W7</b></div>
        </div>
      </div>
      <div class="chart-legend"><span><i class="legend-dot"></i>Store activity</span><span><i class="legend-line"></i>Orders / revenue pulse</span></div>
    </article>

    <article class="seller-visual-panel inventory-panel-large">
      <div class="seller-panel-title"><div><span class="seller-kicker">INVENTORY</span><h2>Stock health</h2></div><a href="<%=request.getContextPath()%>/seller/products">Manage →</a></div>
      <div class="inventory-layout">
        <div class="donut-stack"><div class="donut-ring" style="--good:<%=productCount==0?0:Math.min(100,activePct)%>%"><div><strong><%=activePct%>%</strong><span>active</span></div></div><small><%=productCount%> total listings</small></div>
        <div class="inventory-legend">
          <div><span><i class="inventory-dot good"></i>Active</span><b><%=activeProducts%></b></div>
          <div><span><i class="inventory-dot warn"></i>Low stock</span><b><%=lowStock%></b></div>
          <div><span><i class="inventory-dot danger"></i>Out of stock</span><b><%=outStock%></b></div>
          <div class="inventory-callout"><strong><%=lowStock+outStock%></strong><span>items need attention</span></div>
        </div>
      </div>
    </article>

    <article class="seller-visual-panel category-visual-panel">
      <div class="seller-panel-title"><div><span class="seller-kicker">CATALOG MIX</span><h2>Category footprint</h2></div></div>
      <% if(categories.isEmpty()) { %>
        <div class="seller-empty-block"><span>＋</span><b>No categories yet</b><small>Add products to unlock this visual.</small></div>
      <% } else { int max=1; for(Map<String,Object> c:categories) max=Math.max(max,((Number)c.get("count")).intValue());
        for(Map<String,Object> c:categories){ int count=((Number)c.get("count")).intValue(); int width=Math.max(12,Math.round(count*100f/max)); %>
        <div class="category-visual-row"><div class="category-label"><b><%=c.get("category")%></b><small><%=c.get("stock")%> units</small></div><div class="category-track"><span style="width:<%=width%>%"></span></div><strong><%=count%></strong></div>
      <% }} %>
    </article>

    <article class="seller-visual-panel quick-actions-panel">
      <div class="seller-panel-title"><div><span class="seller-kicker">SHORTCUTS</span><h2>Fast actions</h2></div></div>
      <div class="seller-quick-grid">
        <a href="<%=request.getContextPath()%>/seller/products"><span>📦</span><b>Manage products</b><small>Catalog & inventory</small></a>
        <a href="<%=request.getContextPath()%>/seller/orders"><span>🧾</span><b>Process orders</b><small>Confirm & ship</small></a>
        <a href="<%=request.getContextPath()%>/notifications"><span>🔔</span><b>Notifications</b><small>Store activity</small></a>
        <a href="<%=request.getContextPath()%>/marketplace"><span>🛍️</span><b>View storefront</b><small>Customer view</small></a>
      </div>
    </article>
  </section>

  <section class="seller-max-two-col seller-content-bottom">
    <article class="seller-max-panel">
      <div class="seller-panel-title"><div><span class="seller-kicker">TOP LISTINGS</span><h2>Products driving your store</h2></div><a href="<%=request.getContextPath()%>/seller/products">All products →</a></div>
      <% if(products.isEmpty()){ %><div class="seller-empty-block"><span>📦</span><b>Your catalog is empty</b><small>Create your first listing.</small><a class="button small" href="<%=request.getContextPath()%>/seller/products">Add product</a></div><% } else { %>
      <div class="seller-product-board seller-product-board-rich">
        <% for(Map<String,Object> p:products){ BigDecimal pr=(BigDecimal)p.get("revenue"); BigDecimal rating=(BigDecimal)p.get("rating"); if(pr==null) pr=BigDecimal.ZERO; if(rating==null) rating=BigDecimal.ZERO; String image=String.valueOf(p.getOrDefault("imageUrl1","")); int stock=((Number)p.get("stock")).intValue(); %>
        <a class="seller-product-card seller-product-card-rich" href="<%=request.getContextPath()%>/product/details?id=<%=p.get("id")%>">
          <div class="seller-product-thumb"><% if(image!=null && !"null".equals(image) && !image.isBlank()) { %><img src="<%=image%>" alt="<%=p.get("name")%>"><% } else { %><span>📦</span><% } %></div>
          <div class="seller-product-copy"><b><%=p.get("name")%></b><small><%=p.get("category")%> · <%=rating.setScale(1,BigDecimal.ROUND_HALF_UP)%> ★</small><div class="seller-product-meta"><span><strong><%=p.get("unitsSold")%></strong> sold</span><span><strong>₹<%=pr%></strong> revenue</span></div></div>
          <span class="seller-stock-chip <%=stock==0?"danger":stock<=5?"warn":"good"%>"><%=stock==0?"Out":stock+" left"%></span>
        </a>
        <% } %>
      </div><% } %>
    </article>

    <article class="seller-max-panel">
      <div class="seller-panel-title"><div><span class="seller-kicker">CUSTOMER ACTIVITY</span><h2>Latest orders</h2></div><a href="<%=request.getContextPath()%>/seller/orders">Manage →</a></div>
      <% if(orders.isEmpty()){ %><div class="seller-empty-block"><span>🧾</span><b>No customer orders yet</b><small>New orders will appear here.</small></div><% } else { for(Map<String,Object> o:orders){ %>
      <div class="seller-order-line seller-order-line-visual"><div class="seller-order-icon"><span>📦</span></div><div class="seller-order-main"><b><%=o.get("ref")%></b><small><%=o.get("buyer")%> · <%=fmt.format((java.util.Date)o.get("createdAt"))%></small></div><div class="seller-order-side"><strong>₹<%=o.get("total")%></strong><span class="status-pill status-<%=String.valueOf(o.get("status")).toLowerCase()%>"><%=o.get("status")%></span></div></div>
      <% }} %>
    </article>
  </section>
</main>
<script src="<%=request.getContextPath()%>/js/theme.js"></script>
</body></html>
