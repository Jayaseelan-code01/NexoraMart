<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,java.util.Set,java.util.LinkedHashSet,com.jayaseelan.nexoramart.model.Product" %>
<% List<Product> ps=(List<Product>)request.getAttribute("products"); if(ps==null) ps=java.util.Collections.emptyList(); Set<String> cats=new LinkedHashSet<>(); for(Product px:ps){ if(px.getCategory()!=null) cats.add(px.getCategory()); } %>
<!DOCTYPE html><html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>NexoraMart · Smart Campus Marketplace</title><link rel="manifest" href="<%=request.getContextPath()%>/manifest.webmanifest"><link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"></head>
<body data-ctx="<%=request.getContextPath()%>">
<div class="nx-utility-bar"><div class="nx-utility-inner"><span>🎓 Built for campus shopping</span><span>⚡ Fast search</span><span>🔐 Secure checkout</span><span>📷 Visual discovery</span><span>🤖 Smart recommendations</span></div></div>
<header class="nav premium-nav nx-main-header"><div class="brand-mark"><b>NEXORA<span>MART</span></b><small>SMART CAMPUS COMMERCE</small></div><nav class="main-nav"><a class="active" href="<%=request.getContextPath()%>/marketplace">Marketplace</a><a href="#smart">Smart Match</a></nav><div class="nav-right">
<% if(session.getAttribute("userId")==null){%><a href="<%=request.getContextPath()%>/login">Login</a><a class="button small" href="<%=request.getContextPath()%>/register">Create Account</a><%}else{%><a href="<%=request.getContextPath()%>/dashboard">Dashboard</a><a href="<%=request.getContextPath()%>/notifications">🔔 Notifications</a><% if("BUYER".equals(session.getAttribute("userRole"))){ %><a href="<%=request.getContextPath()%>/cart">Cart 🛒</a><% } else { %><a href="<%=request.getContextPath()%>/login?next=/cart">Cart 🛒</a><% } %><a class="button small" href="<%=request.getContextPath()%>/logout">Logout</a><%}%></div></header>
<nav class="nx-category-nav"><div class="nx-category-inner">
<a class="nx-category-link" href="<%=request.getContextPath()%>/marketplace">All Categories</a>
<a class="nx-category-link" href="<%=request.getContextPath()%>/marketplace?category=Mobile">📱 Mobile</a>
<a class="nx-category-link" href="<%=request.getContextPath()%>/marketplace?category=Laptop">💻 Laptops</a>
<a class="nx-category-link" href="<%=request.getContextPath()%>/marketplace?category=Audio">🎧 Audio</a>
<a class="nx-category-link" href="<%=request.getContextPath()%>/marketplace?category=Accessories">⌨ Accessories</a>
<a class="nx-category-link" href="<%=request.getContextPath()%>/marketplace?category=Campus">🎓 Campus Essentials</a>
<button class="nx-category-link nx-category-button" type="button" data-open-visual-search>📷 Search by Photo</button>
</div></nav>

<main class="market ultimate-market">
<section class="hero-grid">
  <div class="hero-copy"><div class="eyebrow">NEXORA MART · SMART EDITION</div><h1>Campus shopping, <span>upgraded.</span></h1><p>Search, compare, save and discover products with an explainable smart-matching engine — all inside your Java capstone.</p>
  <div class="hero-actions"><button class="button" type="button" data-smart-match>✨ Find My Best Match</button><button class="button ghost" type="button" data-open-assistant>🤖 Nexora Assistant</button></div>
  <div class="hero-trust"><span>⚡ instant suggestions</span><span>📦 live stock</span><span>🖼 rich gallery</span><span>🔒 secure session</span></div></div>
  <div class="hero-orbit"><div class="orbit-ring ring-a"></div><div class="orbit-ring ring-b"></div><div class="orbit-core"><span>NX</span><small>SMART<br>MATCH</small></div><div class="orbit-card oc-1">⚡ Relevance</div><div class="orbit-card oc-2">📊 Explainable</div><div class="orbit-card oc-3">🎯 Campus-fit</div></div>
</section>

<section class="nx-shop-showcase">
  <div class="nx-showcase-heading"><div><span class="eyebrow">SHOP THE NEXORA WAY</span><h2>Built around how students actually shop.</h2></div><span class="muted">Simple choices. Clear specs. Faster decisions.</span></div>
  <div class="nx-showcase-grid">
    <a class="nx-showcase-card nx-showcase-blue" href="<%=request.getContextPath()%>/marketplace?category=Mobile"><div class="nx-showcase-icon">📱</div><div><b>Phones & Mobile</b><span>Compare RAM, camera, battery and storage</span></div><span class="nx-showcase-arrow">→</span></a>
    <a class="nx-showcase-card nx-showcase-purple" href="<%=request.getContextPath()%>/marketplace?category=Laptop"><div class="nx-showcase-icon">💻</div><div><b>Study & Coding</b><span>Find setups for classes, coding and projects</span></div><span class="nx-showcase-arrow">→</span></a>
    <a class="nx-showcase-card nx-showcase-cyan" href="<%=request.getContextPath()%>/marketplace?category=Audio"><div class="nx-showcase-icon">🎧</div><div><b>Audio & Accessories</b><span>Everyday gear for campus life</span></div><span class="nx-showcase-arrow">→</span></a>
    <a class="nx-showcase-card nx-showcase-green" href="<%=request.getContextPath()%>/marketplace?category=Campus"><div class="nx-showcase-icon">🎒</div><div><b>Campus Essentials</b><span>Practical products for hostel and college</span></div><span class="nx-showcase-arrow">→</span></a>
  </div>
</section>

<section class="stat-strip"><div><b><%=ps.size()%>+</b><span>catalog items</span></div><div><b><%=cats.size()%></b><span>categories</span></div><div><b>3</b><span>image views / item</span></div><div><b>3</b><span>compare limit</span></div></section>

<section class="tool-row" id="smart"><div class="search-wrap"><form class="search ultimate-search" method="get"><input data-live-search name="q" value="<%=request.getAttribute("searchTerm")%>" placeholder="Search phone, laptop, RAM, battery, camera..."><button class="icon-button" data-voice-search type="button" aria-label="Voice search">🎙</button><button class="button" type="submit">Search</button></form><div data-suggestions class="suggestions hidden"></div></div>
<select class="sort-select" data-sort-products><option value="relevance">Sort: Relevance</option><option value="low">Price: Low → High</option><option value="high">Price: High → Low</option><option value="stock">Stock: High → Low</option></select><button class="button ghost" data-open-visual-search type="button">📷 Search by Photo</button><button class="button ghost" data-open-compare type="button">Compare (<span data-compare-count>0</span>)</button><a class="button ghost" href="<%=request.getContextPath()%>/wishlist?ids=">Wishlist ♥ <span data-wishlist-count>0</span></a></section>

<section class="visual-search-banner">
  <div><span class="eyebrow">NEW · VISUAL DISCOVERY</span><h2>See it. Upload it. Find it.</h2><p>Take a photo or upload an image and NexoraMart finds the closest catalog products with the full specifications, price and stock.</p></div>
  <button class="button" data-open-visual-search type="button">📷 Find by Photo</button>
</section>

<section class="nx-featured-rail">
  <div class="nx-rail-head"><div><span class="eyebrow">FEATURED PICKS</span><h2>Popular in the campus catalog</h2></div><a class="details nx-rail-link" href="#catalog">See all products →</a></div>
  <div class="nx-featured-track">
  <% int featuredCount=0; for(Product fp:ps){ if(featuredCount>=6) break; String fimg=fp.getImageUrl1(); String fprice=fp.getPrice()==null?"0":fp.getPrice().toPlainString(); %>
    <a class="nx-featured-mini" href="<%=request.getContextPath()%>/product/details?id=<%=fp.getId()%>">
      <div class="nx-featured-mini-img"><img src="<%=request.getContextPath()%>/<%=fimg%>" alt="<%=fp.getName()%>"><span><%=fp.getStockQty()<=3?"Low stock":"Available"%></span></div>
      <div class="nx-featured-mini-copy"><small><%=fp.getBrand()%></small><b><%=fp.getName()%></b><strong>₹<%=fprice%></strong></div>
    </a>
  <% featuredCount++; } %>
  </div>
</section>

<section class="category-rail" id="catalog"><a class="rail-chip <%=((String)request.getAttribute("selectedCategory")).isBlank()?"active":""%>" href="<%=request.getContextPath()%>/marketplace">All</a><% for(String c:cats){%><a class="rail-chip <%=c.equalsIgnoreCase((String)request.getAttribute("selectedCategory"))?"active":""%>" href="<%=request.getContextPath()%>/marketplace?category=<%=java.net.URLEncoder.encode(c,"UTF-8")%>"><%=c%></a><%}%><button class="rail-chip" type="button" data-smart-use="budget">₹ Budget Picks</button><button class="rail-chip" type="button" data-smart-use="battery">🔋 Battery Picks</button></section>

<div class="section-bar"><div><span class="eyebrow">LIVE CATALOG</span><h2><%=request.getAttribute("searchTerm")!=null && !((String)request.getAttribute("searchTerm")).isBlank()?"Search results":"Everything is already here."%></h2></div><div class="muted"><b><%=ps.size()%></b> products</div></div>

<div class="grid ultimate-grid" data-product-grid>
<% for(Product p:ps){String[] im={p.getImageUrl1(),p.getImageUrl2(),p.getImageUrl3()}; String d= p.getDescription()==null?"":p.getDescription(); String price=p.getPrice()==null?"0":p.getPrice().toPlainString(); %>
<article class="card ultimate-card" data-product-card data-product-card-id="<%=p.getId()%>" data-product-card-idx="<%=p.getId()%>" data-product-card-id2="<%=p.getId()%>" data-name="<%=p.getName()%>" data-brand="<%=p.getBrand()%>" data-model="<%=p.getModel()==null?"":p.getModel()%>" data-display="<%=p.getDisplaySize()==null?"":p.getDisplaySize()%>" data-price="<%=price%>" data-stock="<%=p.getStockQty()%>" data-category="<%=p.getCategory()%>" data-ram="<%=p.getRam()==null?"0":p.getRam().replaceAll("[^0-9.]","")%>" data-storage="<%=p.getStorage()==null?"0":p.getStorage().replaceAll("[^0-9.]","")%>" data-battery="<%=p.getBattery()==null?"0":p.getBattery().replaceAll("[^0-9.]","")%>" data-camera="<%=p.getCamera()==null?"0":p.getCamera().replaceAll("[^0-9.]","")%>">
 <div class="gallery smart-gallery"><img src="<%=request.getContextPath()%>/<%=im[0]%>" class="active" alt="<%=p.getName()%>"><%for(int i=1;i<3;i++){ if(im[i]!=null&&!im[i].isBlank()){%><img src="<%=request.getContextPath()%>/<%=im[i]%>" alt="<%=p.getName()%> image <%=i+1%>"><%}}%><span class="chip"><%=p.getCategory()%></span><span class="gallery-badge">↻ 3-angle</span><button class="wish" data-wishlist-id="<%=p.getId()%>" aria-label="Wishlist" type="button">♡</button><span class="stock-badge <%=p.getStockQty()<=3?"danger":""%>"><%=p.getStockQty()<=3?"Low stock":"In stock"%></span></div>
 <div class="body"><div class="micro"><%=p.getBrand()%> · <%=p.getModel()%></div><h2><%=p.getName()%></h2><p><%=d%></p><div class="price-line"><strong class="price">₹<%=price%></strong><label class="compare-box"><input type="checkbox" data-compare-id="<%=p.getId()%>"> Compare</label></div>
 <div class="specs"><span>RAM <b><%=p.getRam()==null?"—":p.getRam()%></b></span><span>Storage <b><%=p.getStorage()==null?"—":p.getStorage()%></b></span><span>Display <b><%=p.getDisplaySize()==null?"—":p.getDisplaySize()%></b></span><span>Camera <b><%=p.getCamera()==null?"—":p.getCamera()%></b></span><span>Battery <b><%=p.getBattery()==null?"—":p.getBattery()%></b></span><span>Stock <b><%=p.getStockQty()%></b></span></div>
 <div class="card-actions"><a class="details" href="<%=request.getContextPath()%>/product/details?id=<%=p.getId()%>">View details →</a><button class="button tiny ghost" data-quick-view type="button">Quick view</button></div>
 <div class="cart-cta-block">
 <% if("BUYER".equals(session.getAttribute("userRole"))){ %>
 <form method="post" action="<%=request.getContextPath()%>/cart" class="add-cart-form"><input type="hidden" name="action" value="add"><input type="hidden" name="productId" value="<%=p.getId()%>"><div class="add-row"><input type="number" name="quantity" value="1" min="1" max="<%=p.getStockQty()%>"><button class="button" type="submit">🛒 Add to Cart</button></div></form>
 <% } else { %>
 <a class="button add-cart-login" href="<%=request.getContextPath()%>/login?next=/marketplace">🛒 Add to Cart</a>
 <small class="cart-login-note">Buyer login required</small>
 <% } %>
 </div>
 </div></article><% } %>
</div>

<section class="recent-section hidden" data-recent-section><div class="section-bar"><div><span class="eyebrow">MEMORY LAYER</span><h2>Recently viewed</h2></div><div class="recent-head-actions"><span class="muted">Stored in this browser</span><button class="text-action" type="button" data-clear-recent>Clear</button></div></div><div class="recent-grid" data-recent-grid></div></section>

<section class="personal-section hidden" data-personal-section><div class="section-bar"><div><span class="eyebrow">NEXORA PERSONALIZATION</span><h2>Picked for you</h2><p class="section-subtext" data-personal-subtext>Recommendations adapt to the products you explore and save.</p></div><span class="personal-signal" data-personal-signal>Learning your taste</span></div><div class="personal-grid" data-personal-grid></div></section>

<section class="feature-cards"><article><span>01</span><h3>Explainable Smart Match</h3><p>Rule-based scoring uses price, RAM, storage, battery, camera and category to explain every recommendation.</p></article><article><span>02</span><h3>Compare Engine</h3><p>Select up to three items and see the specs side-by-side instead of opening multiple tabs.</p></article><article><span>03</span><h3>Offline-ready UI</h3><p>Static CSS, JS and product imagery can be cached by the service worker for a faster repeat visit.</p></article></section>
</main>

<footer class="nx-footer">
  <div class="nx-footer-inner">
    <div><h3>NEXORA MART</h3><p>Smart campus commerce built for faster discovery, clearer comparisons and better everyday student shopping.</p></div>
    <div><h3>Explore</h3><div class="nx-footer-links"><a href="<%=request.getContextPath()%>/marketplace">Marketplace</a><a href="#smart">Smart Match</a><a href="#catalog">All Products</a></div></div>
    <div><h3>Smart Tools</h3><div class="nx-footer-links"><a href="#" data-open-visual-search>Visual Search</a><a href="<%=request.getContextPath()%>/wishlist?ids=">Wishlist</a><a href="#" data-smart-match>Smart Match</a></div></div>
    <div><h3>Account</h3><div class="nx-footer-links"><a href="<%=request.getContextPath()%>/dashboard">Dashboard</a><a href="<%=request.getContextPath()%>/notifications">Notifications</a><a href="<%=request.getContextPath()%>/cart">Cart</a></div></div>
  </div>
  <div class="nx-footer-bottom"><span>© 2026 NexoraMart · Smart Campus Marketplace</span><span>Built with Java 17 · Tomcat 9 · H2</span></div>
</footer>
<button class="nx-to-top" type="button" aria-label="Back to top">↑</button>

<div class="modal" id="visual-search-modal">
  <div class="modal-card visual-modal-card">
    <button class="modal-close" data-close-modal>×</button>
    <span class="eyebrow">NEXORA VISUAL SEARCH</span>
    <h2>Search with a picture</h2>
    <p class="muted">Upload a product photo. Your image stays in the browser while we compare it with the Nexora catalog.</p>
    <div class="visual-upload-zone" data-visual-drop>
      <div class="visual-upload-icon">📷</div>
      <b>Drop an image here</b>
      <span>or</span>
      <label class="button" for="visual-file">Choose Photo</label>
      <input id="visual-file" data-visual-file type="file" accept="image/*" capture="environment" hidden>
      <small>JPG, PNG, WEBP · phone camera supported</small>
    </div>
    <div class="visual-preview hidden" data-visual-preview-wrap>
      <img data-visual-preview alt="Selected product photo">
      <div><b data-visual-file-name>Photo selected</b><span class="muted" data-visual-status>Ready to search</span><button class="button tiny" data-run-visual-search type="button">Find Matching Products</button></div>
    </div>
    <div class="visual-progress hidden" data-visual-progress><span></span></div>
    <div class="visual-results" data-visual-results></div>
  </div>
</div>

<div class="modal" id="smart-modal"><div class="modal-card large"><button class="modal-close" data-close-modal>×</button><span class="eyebrow">NEXORA SMART MATCH</span><h2>What are you buying for?</h2><p class="muted">Pick a use case. The engine ranks the current catalog and shows why each item matched.</p><div class="smart-use-grid"><button class="use-card active" data-smart-use="study">📚<b>Study</b><small>coding + classes</small></button><button class="use-card" data-smart-use="gaming">🎮<b>Gaming</b><small>performance</small></button><button class="use-card" data-smart-use="camera">📷<b>Photography</b><small>camera-first</small></button><button class="use-card" data-smart-use="battery">🔋<b>Battery</b><small>long day</small></button><button class="use-card" data-smart-use="budget">₹<b>Budget</b><small>value-first</small></button></div><div class="smart-controls"><label>Budget <span>₹</span><input data-smart-budget type="number" min="0" step="500" placeholder="30000"></label><div class="smart-priority"><span>Priority</span><button class="smart-priority-btn active" type="button" data-smart-priority="balanced">Balanced</button><button class="smart-priority-btn" type="button" data-smart-priority="performance">Performance</button><button class="smart-priority-btn" type="button" data-smart-priority="battery">Battery</button><button class="smart-priority-btn" type="button" data-smart-priority="camera">Camera</button><button class="smart-priority-btn" type="button" data-smart-priority="value">Value</button></div></div><div class="smart-results" data-smart-results><p class="muted">Choose a use case. Add a budget for a more focused match.</p></div></div></div>

<div class="modal" id="quick-modal"><div class="modal-card"><button class="modal-close" data-close-modal>×</button><img class="qv-image" data-qv-image src="" alt=""><span class="eyebrow">QUICK VIEW</span><h2 data-qv-title></h2><strong class="price" data-qv-price></strong><div data-qv-body class="qv-body"></div><a data-qv-link class="button full" href="#">Open full product →</a></div></div>

<aside class="assistant" id="assistant"><div class="assistant-head"><div><span class="eyebrow">SMART ASSISTANT</span><h3>Nexora AI Assistant</h3><small class="assistant-status">Catalog-aware · Explainable</small></div><button class="modal-close" onclick="this.closest('.assistant').classList.remove('open')">×</button></div><div class="assistant-chips"><button type="button" data-assistant-prompt="best phone under 30000">Phone under ₹30K</button><button type="button" data-assistant-prompt="gaming phone">Gaming</button><button type="button" data-assistant-prompt="coding laptop">Coding</button><button type="button" data-assistant-prompt="long battery phone">Battery</button></div><div class="chat-log" data-chat-log><div class="chat-bubble bot"><p>Hi! I can search the Nexora catalog, explain why products match, and open Cart, Orders or Compare.</p></div></div><form class="chat-form" data-assistant-form><input name="message" placeholder="Try: best phone under 30000" autocomplete="off"><button class="button tiny">Send</button></form></aside>

<script src="<%=request.getContextPath()%>/js/nexora.js"></script><script>document.querySelectorAll('.gallery').forEach(g=>{const a=[...g.querySelectorAll('img')];if(a.length>1){let i=0;setInterval(()=>{a[i].classList.remove('active');i=(i+1)%a.length;a[i].classList.add('active')},2300)}});document.querySelectorAll('[data-wishlist-count]').forEach(x=>x.parentElement?.addEventListener('click',()=>{const ids=JSON.parse(localStorage.getItem('nexora:wishlist')||'[]');location.href='<%=request.getContextPath()%>/wishlist?ids='+encodeURIComponent(ids.join(','));}));</script>
<script src="<%=request.getContextPath()%>/js/theme.js"></script>
<script>(()=>{const b=document.querySelector('.nx-to-top');if(!b)return;window.addEventListener('scroll',()=>b.classList.toggle('show',window.scrollY>520),{passive:true});b.addEventListener('click',()=>window.scrollTo({top:0,behavior:'smooth'}));})();</script>
</body></html>