<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,java.util.HashSet,java.util.Set,com.jayaseelan.nexoramart.model.Product" %>
<% List<Product> ps=(List<Product>)request.getAttribute("wishlistProducts"); if(ps==null) ps=java.util.Collections.emptyList(); Set<String> categories=new HashSet<>(); int inStock=0; for(Product p:ps){ if(p.getCategory()!=null) categories.add(p.getCategory()); if(p.getStockQty()>0) inStock++; } %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Wishlist · NexoraMart</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body data-ctx="<%=request.getContextPath()%>">
<header class="nav premium-nav">
  <div class="brand-mark"><b>NEXORA<span>MART</span></b><small>SMART CAMPUS COMMERCE</small></div>
  <nav class="main-nav"><a href="<%=request.getContextPath()%>/marketplace">Marketplace</a><a class="active" href="#">Wishlist</a><a href="<%=request.getContextPath()%>/cart">Cart 🛒</a></nav>
</header>

<main class="wishlist-page wishlist-pro-page">
  <section class="wishlist-hero">
    <div>
      <span class="eyebrow">PERSONAL SHOPPING SPACE</span>
      <h1>My wishlist <span>♥</span></h1>
      <p class="muted">Keep the products you care about in one place. Compare them later, add them to cart, or open the full product experience.</p>
    </div>
    <div class="wishlist-hero-card"><div class="wishlist-heart-orbit">♥</div><div><b>Nexora Saved List</b><small>Browser-saved favourites</small></div></div>
  </section>

  <section class="wishlist-stat-strip">
    <div><span>Saved items</span><b><%=ps.size()%></b></div>
    <div><span>In stock</span><b><%=inStock%></b></div>
    <div><span>Categories</span><b><%=categories.size()%></b></div>
    <div><span>Compare limit</span><b>3</b></div>
  </section>

  <% if(ps.isEmpty()){ %>
    <section class="empty-panel wishlist-empty-pro">
      <div class="empty-icon">♡</div>
      <span class="eyebrow">NOTHING SAVED YET</span>
      <h2>Build your shortlist</h2>
      <p class="muted">Tap the heart on any product card and it will appear here. Your saved list stays in this browser.</p>
      <a class="button" href="<%=request.getContextPath()%>/marketplace">Explore the marketplace →</a>
    </section>
  <% } else { %>
    <section class="wishlist-toolbar-pro">
      <div class="wishlist-toolbar-left">
        <div class="wishlist-filter-group">
          <button class="wishlist-filter active" data-wishlist-filter="all" type="button">All <span><%=ps.size()%></span></button>
          <button class="wishlist-filter" data-wishlist-filter="stock" type="button">In stock <span><%=inStock%></span></button>
          <button class="wishlist-filter" data-wishlist-filter="out" type="button">Out of stock <span><%=ps.size()-inStock%></span></button>
        </div>
        <select class="wishlist-sort" data-wishlist-sort>
          <option value="saved">Sort: Recently saved</option>
          <option value="low">Price: Low → High</option>
          <option value="high">Price: High → Low</option>
          <option value="name">Name: A → Z</option>
        </select>
      </div>
      <div class="wishlist-toolbar-right">
        <button class="button ghost small" data-share-wishlist type="button">↗ Share list</button>
        <button class="button ghost small" data-clear-wishlist type="button">Clear all</button>
      </div>
    </section>

    <section class="wishlist-grid-pro" data-wishlist-grid>
      <% int order=0; for(Product p:ps){ String[] ims={p.getImageUrl1(),p.getImageUrl2(),p.getImageUrl3()}; String imageCount="1"; int count=0; for(String im:ims){if(im!=null&&!im.isBlank())count++;} imageCount=String.valueOf(Math.max(1,count)); %>
      <article class="wishlist-pro-card" data-wishlist-card data-wishlist-card-id="<%=p.getId()%>" data-wishlist-order="<%=order++%>" data-price="<%=p.getPrice()==null?0:p.getPrice()%>" data-name="<%=p.getName()%>" data-stock="<%=p.getStockQty()%>">
        <div class="wishlist-pro-media">
          <a href="<%=request.getContextPath()%>/product/details?id=<%=p.getId()%>" class="wishlist-main-link">
            <div class="wishlist-card-gallery">
              <% for(int i=0;i<ims.length;i++){ if(ims[i]!=null&&!ims[i].isBlank()) { %>
                <img src="<%=request.getContextPath()%>/<%=ims[i]%>" class="<%=i==0?"active":""%>" alt="<%=p.getName()%> view <%=i+1%>">
              <% }} %>
            </div>
          </a>
          <span class="wishlist-view-count">↻ <%=imageCount%> views</span>
          <button class="wishlist-remove-top" data-remove-wishlist="<%=p.getId()%>" type="button" aria-label="Remove from wishlist">×</button>
          <span class="wishlist-stock-chip <%=p.getStockQty()<=0?"out":""%>"><%=p.getStockQty()>0?(p.getStockQty()<=3?"Low stock":"In stock"):"Out of stock"%></span>
        </div>
        <div class="wishlist-card-body">
          <div class="micro"><%=p.getBrand()%> · <%=p.getModel()%></div>
          <h2><%=p.getName()%></h2>
          <p><%=p.getDescription()==null?"":p.getDescription()%></p>
          <div class="wishlist-price-row"><strong class="price">₹<%=p.getPrice()%></strong><span><%=p.getCategory()%></span></div>
          <div class="wishlist-mini-specs">
            <span>RAM <b><%=p.getRam()==null?"—":p.getRam()%></b></span>
            <span>Storage <b><%=p.getStorage()==null?"—":p.getStorage()%></b></span>
            <span>Battery <b><%=p.getBattery()==null?"—":p.getBattery()%></b></span>
            <span>Stock <b><%=p.getStockQty()%></b></span>
          </div>
          <div class="wishlist-card-actions">
            <a class="details" href="<%=request.getContextPath()%>/product/details?id=<%=p.getId()%>">View details →</a>
            <% if("BUYER".equals(session.getAttribute("userRole")) && p.getStockQty()>0){ %>
              <form method="post" action="<%=request.getContextPath()%>/cart" class="wishlist-cart-form"><input type="hidden" name="action" value="add"><input type="hidden" name="productId" value="<%=p.getId()%>"><input type="hidden" name="quantity" value="1"><button class="button" type="submit">🛒 Add</button></form>
            <% } else if(p.getStockQty()>0) { %>
              <a class="button" href="<%=request.getContextPath()%>/login?next=/wishlist">🛒 Add</a>
            <% } else { %>
              <button class="button disabled-button" type="button" disabled>Out of stock</button>
            <% } %>
          </div>
        </div>
      </article>
      <% } %>
    </section>

    <section class="wishlist-bottom-panel">
      <div><span class="eyebrow">SMART SHORTLIST</span><h2>Ready for the next decision?</h2><p class="muted">Use Compare for a tighter side-by-side decision, or head back to the marketplace to discover more products.</p></div>
      <div class="wishlist-bottom-actions"><a class="button" href="<%=request.getContextPath()%>/marketplace">Continue shopping →</a><a class="button ghost" href="<%=request.getContextPath()%>/compare?ids=">Open Compare</a></div>
    </section>
  <% } %>
</main>

<script src="<%=request.getContextPath()%>/js/nexora.js"></script>
<script src="<%=request.getContextPath()%>/js/theme.js"></script>
</body>
</html>
