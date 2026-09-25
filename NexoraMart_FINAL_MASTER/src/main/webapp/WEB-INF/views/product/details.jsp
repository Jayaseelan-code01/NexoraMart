<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.jayaseelan.nexoramart.model.Product,com.jayaseelan.nexoramart.model.Review,java.util.List" %>
<% Product p=(Product)request.getAttribute("product"); List<Review> reviews=(List<Review>)request.getAttribute("reviews"); List<Product> similarProducts=(List<Product>)request.getAttribute("similarProducts"); Double avg=(Double)request.getAttribute("reviewAverage"); boolean canReview=Boolean.TRUE.equals(request.getAttribute("canReview")); boolean canReply=Boolean.TRUE.equals(request.getAttribute("canReply")); Review myReview=(Review)request.getAttribute("myReview"); String reviewStatus=request.getParameter("review"); String ctx=request.getContextPath(); String stockLabel=p.getStockQty()>10?"In stock":p.getStockQty()>0?"Only "+p.getStockQty()+" left":"Out of stock"; %>
<!DOCTYPE html><html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><meta name="description" content="<%=p.getName()%> details, specifications, ratings and shopping options on NexoraMart."><title><%=p.getName()%> · NexoraMart</title><link rel="manifest" href="<%=ctx%>/manifest.webmanifest"><link rel="stylesheet" href="<%=ctx%>/css/style.css"></head>
<body data-ctx="<%=ctx%>" data-product-id="<%=p.getId()%>" data-product-name="<%=p.getName()%>" data-product-category="<%=p.getCategory()%>" data-product-brand="<%=p.getBrand()==null?"":p.getBrand()%>" data-product-price="<%=p.getPrice()%>" data-product-image="<%=p.getImageUrl1()%>">
<header class="nav detail-nav"><div class="brand-mark"><b>NEXORA<span>MART</span></b><small>SMART CAMPUS COMMERCE</small></div><a href="<%=ctx%>/marketplace">← Back to Marketplace</a><div class="nav-right"><a href="<%=ctx%>/marketplace">Shop</a><%if("BUYER".equals(session.getAttribute("userRole"))){%><a href="<%=ctx%>/cart">Cart 🛒</a><a href="<%=ctx%>/orders">Orders</a><%}%></div></header>

<main class="detail-page pro-detail">
  <div class="breadcrumb">Home <span>›</span> <%=p.getCategory()%> <span>›</span> <b><%=p.getName()%></b></div>

  <section class="detail-pro-layout">
    <div class="media-column">
      <div class="product-media-card">
        <div class="media-badge">NEXORA VERIFIED</div>
        <div class="thumb-rail">
          <%String[] ims={p.getImageUrl1(),p.getImageUrl2(),p.getImageUrl3()}; for(int i=0;i<ims.length;i++){if(ims[i]!=null&&!ims[i].isBlank()){%><button type="button" class="thumb-button <%=i==0?"selected":""%>" data-thumb="<%=i%>"><img src="<%=ctx%>/<%=ims[i]%>" alt="<%=p.getName()%> view <%=i+1%>"></button><%}}%>
        </div>
        <div class="detail-main-image pro-main-image">
          <%for(int i=0;i<ims.length;i++){if(ims[i]!=null&&!ims[i].isBlank()){%><img src="<%=ctx%>/<%=ims[i]%>" class="detail-photo <%=i==0?"active":""%>" data-photo="<%=i%>" alt="<%=p.getName()%> product image"><%}}%>
          <button class="image-control prev" type="button" data-gallery-prev aria-label="Previous image">‹</button>
          <button class="image-control next" type="button" data-gallery-next aria-label="Next image">›</button>
          <button class="zoom-note" type="button" data-open-zoom>Click image to zoom</button>
        </div>
        <div class="media-footer"><span>📷 <%=Math.max(1,(p.getImageUrl2()==null||p.getImageUrl2().isBlank()?1:2)+(p.getImageUrl3()==null||p.getImageUrl3().isBlank()?0:1))%> product views</span><span>✨ High-detail gallery</span></div>
      </div>
    </div>

    <div class="detail-info pro-info">
      <div class="micro-row"><span class="chip"><%=p.getCategory()%></span><span><%=p.getBrand()%> · <%=p.getModel()%></span></div>
      <h1><%=p.getName()%></h1>
      <div class="rating-line"><span class="stars-inline"><%int rounded=(int)Math.round(avg==null?0.0:avg);for(int i=1;i<=5;i++){%><span class="<%=i<=rounded?"filled":"empty"%>">★</span><%}%></span><b><%=String.format("%.1f",avg==null?0.0:avg)%></b><span><%=reviews==null?0:reviews.size()%> ratings</span><span class="dot-sep">•</span><span>Campus buyer reviews</span></div>
      <div class="price-row"><div class="price">₹<%=p.getPrice()%></div><span class="price-note">Inclusive of listed product price</span></div>
      <div class="campus-fit-card"><span class="fit-icon">✨</span><div><b>Campus Smart Pick</b><p>Balanced specifications for everyday student use, study and personal productivity.</p></div></div>

      <div class="quick-spec-strip">
        <div><span>RAM</span><b><%=p.getRam()==null?"—":p.getRam()%></b></div><div><span>Storage</span><b><%=p.getStorage()==null?"—":p.getStorage()%></b></div><div><span>Display</span><b><%=p.getDisplaySize()==null?"—":p.getDisplaySize()%></b></div><div><span>Battery</span><b><%=p.getBattery()==null?"—":p.getBattery()%></b></div>
      </div>

      <p class="pro-description"><%=p.getDescription()%></p>

      <div class="stock-box <%=p.getStockQty()>0?"in-stock":"out-stock"%>"><span class="stock-dot"></span><div><b><%=stockLabel%></b><small><%=p.getStockQty()>0?"Ready for checkout":"This product is currently unavailable"%></small></div></div>

      <div class="pro-actions">
        <%if("BUYER".equals(session.getAttribute("userRole"))&&p.getStockQty()>0){%>
          <form method="post" action="<%=ctx%>/cart" class="pro-cart-form"><input type="hidden" name="action" value="add"><input type="hidden" name="productId" value="<%=p.getId()%>"><div class="quantity-box"><button type="button" data-qty-minus>−</button><input type="number" name="quantity" value="1" min="1" max="<%=p.getStockQty()%>" data-detail-qty><button type="button" data-qty-plus>+</button></div><button class="button pro-add-cart" type="submit">🛒 Add to Cart</button></form>
        <%}else if(p.getStockQty()<=0){%><div class="alert">Out of stock</div><%}else{%><a class="button pro-login-cta" href="<%=ctx%>/login">Login to Add to Cart</a><%}%>
        <div class="secondary-actions"><button class="wish large-wish" data-wishlist-id="<%=p.getId()%>" type="button">♡</button><label class="compare-box"><input type="checkbox" data-compare-id="<%=p.getId()%>"> Compare</label><button class="text-action" type="button" data-share-product>↗ Share</button></div>
      </div>

      <div class="trust-grid"><div><b>🔐 Secure checkout</b><span>Protected account flow</span></div><div><b>📦 Stock verified</b><span>Live inventory status</span></div><div><b>💬 Buyer voice</b><span>Ratings & seller replies</span></div></div>
    </div>
  </div>

  <section class="description-specs-grid">
    <div class="spec-card"><div class="section-kicker">PRODUCT DETAILS</div><h2>Built for your campus routine</h2><p><%=p.getDescription()%></p><div class="spec-table"><div><span>Brand</span><b><%=p.getBrand()==null?"—":p.getBrand()%></b></div><div><span>Model</span><b><%=p.getModel()==null?"—":p.getModel()%></b></div><div><span>Category</span><b><%=p.getCategory()%></b></div><div><span>RAM</span><b><%=p.getRam()==null?"—":p.getRam()%></b></div><div><span>Storage</span><b><%=p.getStorage()==null?"—":p.getStorage()%></b></div><div><span>Display</span><b><%=p.getDisplaySize()==null?"—":p.getDisplaySize()%></b></div><div><span>Camera</span><b><%=p.getCamera()==null?"—":p.getCamera()%></b></div><div><span>Battery</span><b><%=p.getBattery()==null?"—":p.getBattery()%></b></div><div><span>Seller</span><b><%=p.getSellerName()%></b></div><div><span>Available Stock</span><b><%=p.getStockQty()%></b></div></div></div>
    <div class="buy-box"><div class="section-kicker">AT A GLANCE</div><h3>Why students may like it</h3><div class="glance-list"><div><span>⚡</span><p><b>Instant product context</b><small>Specs, price and stock together.</small></p></div><div><span>🖼️</span><p><b>Multi-angle gallery</b><small>Switch views before buying.</small></p></div><div><span>⭐</span><p><b>Community signal</b><small>See ratings and verified review flow.</small></p></div><div><span>🛍️</span><p><b>Fast cart access</b><small>Choose quantity and add directly.</small></p></div></div></div>
  </section>

  <%if(similarProducts!=null&&!similarProducts.isEmpty()){%>
  <section class="similar-section"><div class="similar-heading"><div><span class="section-kicker">YOU MAY ALSO LIKE</span><h2>More from <%=p.getCategory()%></h2></div><a href="<%=ctx%>/marketplace?category=<%=java.net.URLEncoder.encode(p.getCategory(),"UTF-8")%>">View category →</a></div><div class="similar-rail">
    <%for(Product q:similarProducts){%><article class="similar-card" data-product-card data-product-card-id="<%=q.getId()%>" data-price="<%=q.getPrice()%>" data-stock="<%=q.getStockQty()%>" data-name="<%=q.getName()%>" data-brand="<%=q.getBrand()%>"><a href="<%=ctx%>/product/details?id=<%=q.getId()%>" class="similar-image"><img src="<%=ctx%>/<%=q.getImageUrl1()%>" alt="<%=q.getName()%>"></a><span class="similar-category"><%=q.getCategory()%></span><h3><%=q.getName()%></h3><p><%=q.getBrand()%> · <%=q.getModel()%></p><div class="similar-meta"><span>₹<%=q.getPrice()%></span><small><%=q.getStockQty()%> in stock</small></div></article><%}%>
  </div></section>
  <%}%>

  <section class="reviews-section pro-reviews">
    <div class="review-heading"><div><span class="eyebrow">CUSTOMER VOICE</span><h2>Reviews & Ratings</h2><p>Verified buyer feedback with seller replies.</p></div><div class="rating-summary"><div class="rating-number"><%=String.format("%.1f",avg==null?0.0:avg)%></div><div><div class="stars"><%for(int i=1;i<=5;i++){%><span class="<%=i<=rounded?"filled":"empty"%>">★</span><%}%></div><small><%=reviews==null?0:reviews.size()%> review<%=reviews!=null&&reviews.size()==1?"":"s"%></small></div></div></div>
    <%if(reviewStatus!=null){%><div class="success"><%="success".equals(reviewStatus)?"Your review was posted successfully.":"replied".equals(reviewStatus)?"Seller reply posted successfully.":""%></div><%}%>
    <%if(canReview){%><div class="review-form-card"><h3>Share your experience</h3><form method="post" action="<%=ctx%>/reviews"><input type="hidden" name="action" value="add"><input type="hidden" name="productId" value="<%=p.getId()%>"><label>Rating</label><select name="rating" required><option value="5">★★★★★ — Excellent</option><option value="4">★★★★☆ — Very good</option><option value="3">★★★☆☆ — Good</option><option value="2">★★☆☆☆ — Fair</option><option value="1">★☆☆☆☆ — Poor</option></select><label>Review</label><textarea name="comment" maxlength="1000" required placeholder="Tell other campus buyers what you liked..."></textarea><button class="button" type="submit">Post Review</button></form></div><%}else if(myReview!=null){%><div class="review-form-card"><h3>You already reviewed this product</h3><p class="muted">Your rating: <strong><%=myReview.getRating()%>/5</strong>.</p></div><%}else if("BUYER".equals(session.getAttribute("userRole"))){%><div class="review-form-card"><h3>Review after purchase</h3><p class="muted">Complete a purchase first, then you can leave one review.</p></div><%}%>
    <div class="review-list"><%if(reviews==null||reviews.isEmpty()){%><div class="empty-review"><span>💬</span><h3>No reviews yet</h3><p>Be the first buyer to review this product.</p></div><%}else{for(Review rv:reviews){%><article class="review-card"><div class="review-top"><div><strong><%=rv.getBuyerName()%></strong><small><%=rv.getCreatedAt()%></small></div><div class="stars"><%for(int i=1;i<=5;i++){%><span class="<%=i<=rv.getRating()?"filled":"empty"%>">★</span><%}%></div></div><p class="review-text"><%=rv.getComment()%></p><%if(rv.getSellerReply()!=null&&!rv.getSellerReply().isBlank()){%><div class="seller-reply"><strong>Seller reply</strong><p><%=rv.getSellerReply()%></p><small><%=rv.getRepliedAt()%></small></div><%}else if(canReply){%><form method="post" action="<%=ctx%>/reviews" class="reply-form"><input type="hidden" name="action" value="reply"><input type="hidden" name="reviewId" value="<%=rv.getId()%>"><input type="hidden" name="productId" value="<%=p.getId()%>"><textarea name="reply" maxlength="1000" required placeholder="Reply to this buyer..."></textarea><button class="button small" type="submit">Reply</button></form><%}%></article><%}}%></div>
  </section>
</main>
<div class="modal" id="image-modal"><div class="image-modal-card"><button class="modal-close" data-close-modal>×</button><img data-zoom-image src="" alt=""></div></div>
<script src="<%=ctx%>/js/nexora.js"></script><script src="<%=ctx%>/js/product-details.js"></script><script src="<%=ctx%>/js/theme.js"></script>
</body></html>
