<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.jayaseelan.nexoramart.model.Product" %>
<% List<Product> ps=(List<Product>)request.getAttribute("compareProducts"); %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Compare · NexoraMart</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body data-ctx="<%=request.getContextPath()%>">
<header class="nav premium-nav">
  <div class="brand-mark"><b>NEXORA<span>MART</span></b><small>SMART CAMPUS COMMERCE</small></div>
  <nav class="main-nav">
    <a href="<%=request.getContextPath()%>/marketplace">Marketplace</a>
    <a class="active" href="<%=request.getContextPath()%>/compare">Compare</a>
    <a href="<%=request.getContextPath()%>/wishlist">Wishlist</a>
    <a href="<%=request.getContextPath()%>/cart">Cart 🛒</a>
  </nav>
</header>

<main class="compare-page compare-pro">
  <section class="compare-hero">
    <div>
      <span class="eyebrow">NEXORA DECISION LAB</span>
      <h1>Compare before you buy.</h1>
      <p class="muted">See up to three products together, spot differences instantly and add the one you choose straight to your cart.</p>
    </div>
    <div class="compare-hero-actions">
      <div class="compare-count-card"><b><%=ps==null?0:ps.size()%></b><span>selected</span></div>
      <a class="button ghost" href="<%=request.getContextPath()%>/marketplace">+ Add more products</a>
      <button class="button ghost" type="button" data-clear-compare>Clear compare</button>
    </div>
  </section>

<% if(ps==null||ps.isEmpty()){ %>
  <div class="empty-panel compare-empty">
    <div>⚖️</div>
    <h2>Your comparison tray is empty</h2>
    <p class="muted">Choose products from the marketplace and tick <b>Compare</b>. You can compare up to three at once.</p>
    <a class="button" href="<%=request.getContextPath()%>/marketplace">Explore products</a>
  </div>
<% } else { %>
  <section class="compare-toolbar">
    <div><strong><%=ps.size()%> product<%=ps.size()==1?"":"s"%></strong><span class="muted"> · differences are highlighted</span></div>
    <div class="compare-legend"><span class="legend-dot different"></span> Different <span class="legend-dot same"></span> Same</div>
  </section>

  <div class="compare-table-wrap compare-pro-wrap">
    <table class="compare-table compare-pro-table" data-compare-table>
      <thead>
        <tr>
          <th class="feature-head">PRODUCT</th>
          <% for(Product p:ps){
              String[] imgs={p.getImageUrl1(),p.getImageUrl2(),p.getImageUrl3()};
          %>
          <th class="compare-product-head" data-compare-column data-product-id="<%=p.getId()%>">
            <div class="compare-photo-strip">
              <img src="<%=request.getContextPath()%>/<%=imgs[0]%>" class="compare-photo active" alt="<%=p.getName()%>">
              <% for(int i=1;i<3;i++){ if(imgs[i]!=null&&!imgs[i].isBlank()) { %>
              <img src="<%=request.getContextPath()%>/<%=imgs[i]%>" class="compare-photo" alt="<%=p.getName()%> image <%=i+1%>">
              <% }} %>
            </div>
            <div class="compare-product-meta">
              <span class="micro"><%=p.getBrand()%> · <%=p.getModel()%></span>
              <h2><%=p.getName()%></h2>
              <div class="compare-price">₹<%=p.getPrice()%></div>
              <span class="stock-badge <%=p.getStockQty()<=3?"danger":""%>"><%=p.getStockQty()<=3?"Low stock":"In stock"%> · <%=p.getStockQty()%></span>
            </div>
            <div class="compare-head-actions">
              <% if("BUYER".equals(session.getAttribute("userRole"))){ %>
              <form method="post" action="<%=request.getContextPath()%>/cart" class="compare-add-form">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="productId" value="<%=p.getId()%>">
                <input type="hidden" name="quantity" value="1">
                <button class="button tiny" type="submit">🛒 Add to cart</button>
              </form>
              <% } else { %>
              <a class="button tiny" href="<%=request.getContextPath()%>/login?next=/compare">🛒 Add to cart</a>
              <% } %>
              <a class="button tiny ghost" href="<%=request.getContextPath()%>/product/details?id=<%=p.getId()%>">View details</a>
              <button class="compare-remove" type="button" data-compare-remove="<%=p.getId()%>">Remove ×</button>
            </div>
          </th>
          <% } %>
        </tr>
      </thead>
      <tbody>
        <tr class="group-row"><td colspan="<%=ps.size()+1%>">Identity</td></tr>
        <tr><td>Brand / Model</td><%for(Product p:ps){%><td data-compare-value="<%=p.getBrand()%> <%=p.getModel()%>"><%=p.getBrand()%> · <%=p.getModel()%></td><%}%></tr>
        <tr><td>Category</td><%for(Product p:ps){%><td data-compare-value="<%=p.getCategory()%>"><%=p.getCategory()%></td><%}%></tr>
        <tr><td>Description</td><%for(Product p:ps){String d=p.getDescription()==null?"—":p.getDescription();%><td><%=d%></td><%}%></tr>

        <tr class="group-row"><td colspan="<%=ps.size()+1%>">Core specifications</td></tr>
        <tr><td>RAM</td><%for(Product p:ps){%><td data-compare-value="<%=p.getRam()==null?"—":p.getRam()%>"><%=p.getRam()==null?"—":p.getRam()%></td><%}%></tr>
        <tr><td>Storage</td><%for(Product p:ps){%><td data-compare-value="<%=p.getStorage()==null?"—":p.getStorage()%>"><%=p.getStorage()==null?"—":p.getStorage()%></td><%}%></tr>
        <tr><td>Display</td><%for(Product p:ps){%><td data-compare-value="<%=p.getDisplaySize()==null?"—":p.getDisplaySize()%>"><%=p.getDisplaySize()==null?"—":p.getDisplaySize()%></td><%}%></tr>
        <tr><td>Camera</td><%for(Product p:ps){%><td data-compare-value="<%=p.getCamera()==null?"—":p.getCamera()%>"><%=p.getCamera()==null?"—":p.getCamera()%></td><%}%></tr>
        <tr><td>Battery</td><%for(Product p:ps){%><td data-compare-value="<%=p.getBattery()==null?"—":p.getBattery()%>"><%=p.getBattery()==null?"—":p.getBattery()%></td><%}%></tr>

        <tr class="group-row"><td colspan="<%=ps.size()+1%>">Buying view</td></tr>
        <tr><td>Price</td><%for(Product p:ps){%><td class="price-cell" data-compare-price="<%=p.getPrice()%>">₹<%=p.getPrice()%></td><%}%></tr>
        <tr><td>Stock</td><%for(Product p:ps){%><td data-compare-stock="<%=p.getStockQty()%>"><%=p.getStockQty()%> units</td><%}%></tr>
        <tr><td>Seller</td><%for(Product p:ps){%><td><%=p.getSellerName()==null?"Nexora Verified Seller":p.getSellerName()%></td><%}%></tr>
        <tr class="final-action-row"><td>Quick action</td><%for(Product p:ps){%><td><a class="button tiny" href="<%=request.getContextPath()%>/product/details?id=<%=p.getId()%>">Open product →</a></td><%}%></tr>
      </tbody>
    </table>
  </div>
<% } %>
</main>

<script src="<%=request.getContextPath()%>/js/nexora.js"></script>
<script src="<%=request.getContextPath()%>/js/theme.js"></script>
<script>
(() => {
  const compareKey = 'nexora:compare';
  const read = () => { try { return JSON.parse(localStorage.getItem(compareKey) || '[]'); } catch { return []; } };
  const write = v => localStorage.setItem(compareKey, JSON.stringify(v));
  document.querySelectorAll('[data-compare-remove]').forEach(btn => btn.addEventListener('click', () => {
    const id = String(btn.dataset.compareRemove);
    const next = read().filter(x => String(x) !== id);
    write(next);
    const ctx = document.body.dataset.ctx || '';
    location.href = `${ctx}/compare?ids=${encodeURIComponent(next.join(','))}`;
  }));
  document.querySelector('[data-clear-compare]')?.addEventListener('click', () => {
    write([]);
    const ctx = document.body.dataset.ctx || '';
    location.href = `${ctx}/compare`;
  });

  // Mark identical vs different specs so a quick scan is possible.
  document.querySelectorAll('[data-compare-table] tbody tr').forEach(row => {
    const cells = [...row.querySelectorAll('td[data-compare-value]')];
    if (cells.length < 2) return;
    const values = cells.map(c => (c.dataset.compareValue || '').trim().toLowerCase());
    const same = values.every(v => v === values[0]);
    cells.forEach(c => c.classList.add(same ? 'spec-same' : 'spec-different'));
    const first = row.querySelector('td:first-child');
    first?.classList.toggle('spec-label-same', same);
  });

  // Lightly surface the lowest listed price without changing the data.
  const priceCells = [...document.querySelectorAll('[data-compare-price]')];
  if (priceCells.length > 1) {
    const nums = priceCells.map(c => Number(c.dataset.comparePrice || 0));
    const min = Math.min(...nums);
    priceCells.forEach(c => { if (Number(c.dataset.comparePrice || 0) === min) c.classList.add('price-best'); });
  }

  // Reuse the three local product images as a slow comparison carousel.
  document.querySelectorAll('[data-compare-column]').forEach(col => {
    const imgs = [...col.querySelectorAll('.compare-photo')];
    if (imgs.length < 2) return;
    let idx = 0;
    setInterval(() => {
      imgs[idx].classList.remove('active'); idx = (idx + 1) % imgs.length; imgs[idx].classList.add('active');
    }, 2400);
  });
})();
</script>
</body>
</html>
