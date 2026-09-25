<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.jayaseelan.nexoramart.model.Notification" %>
<%
  List<Notification> ns = (List<Notification>) request.getAttribute("notifications");
  if (ns == null) ns = java.util.Collections.emptyList();
%>
<!DOCTYPE html><html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Notifications · NexoraMart</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"></head>
<body data-ctx="<%=request.getContextPath()%>">
<div class="nx-utility-bar"><div class="nx-utility-inner"><span>🔔 Smart notifications</span><span>🎓 Campus shopping</span><span>🔐 Secure account</span></div></div>
<header class="nav premium-nav"><div class="brand-mark"><b>NEXORA<span>MART</span></b><small>SMART CAMPUS COMMERCE</small></div><div class="nav-right"><a href="<%=request.getContextPath()%>/marketplace">Marketplace</a><a href="<%=request.getContextPath()%>/dashboard">Dashboard</a><a class="button small" href="<%=request.getContextPath()%>/logout">Logout</a></div></header>
<main class="market" style="max-width:1100px;margin:0 auto;padding:28px 20px 60px">
<section class="section-bar"><div><span class="eyebrow">INBOX</span><h1>Notifications</h1><p class="muted">Order updates, inventory alerts and marketplace activity — all in one place.</p></div><button class="button ghost" type="button" data-mark-all-read>Mark all read</button></section>
<div class="notification-toolbar"><span><b data-notification-visible-count><%=ns.size()%></b> recent notifications</span><button class="details" type="button" data-clear-read>Clear read</button></div>
<section class="notification-list" data-notification-list>
<% if(ns.isEmpty()) { %><div class="empty-state"><div class="success-icon">✓</div><h2>You're all caught up</h2><p class="muted">New order, review and inventory activity will appear here.</p><a class="button" href="<%=request.getContextPath()%>/marketplace">Continue Shopping</a></div><% }
for(Notification n:ns){ %>
<article class="notification-card" data-notification-id="<%=n.getId()%>">
  <div class="notification-icon"><%= "ORDER".equals(n.getType()) ? "📦" : "INVENTORY".equals(n.getType()) ? "⚠️" : "REVIEW".equals(n.getType()) ? "⭐" : "🛡️" %></div>
  <div class="notification-copy"><div class="notification-head"><div><b><%=n.getTitle()%></b><span class="notification-type"><%=n.getType()%></span></div><time><%=n.getCreatedAt()==null?"":n.getCreatedAt()%></time></div><p><%=n.getMessage()%></p><a class="details" href="<%=request.getContextPath()%>/<%=n.getLink()%>">Open →</a></div>
  <button class="notification-read" type="button" title="Mark as read" data-mark-read="<%=n.getId()%>">✓</button>
</article>
<% } %></section>
</main>
<script>
(function(){
  const KEY='nexora.notification.read.v1';
  const readSet=new Set(JSON.parse(localStorage.getItem(KEY)||'[]'));
  const cards=[...document.querySelectorAll('[data-notification-id]')];
  function save(){localStorage.setItem(KEY,JSON.stringify([...readSet].slice(-300)));}
  function apply(){cards.forEach(c=>{if(readSet.has(c.dataset.notificationId))c.classList.add('is-read');}); updateCount();}
  function updateCount(){const unread=cards.filter(c=>!c.classList.contains('is-read')).length; const el=document.querySelector('[data-notification-visible-count]'); if(el)el.textContent=unread+' unread / '+cards.length;}
  document.querySelectorAll('[data-mark-read]').forEach(b=>b.addEventListener('click',()=>{readSet.add(b.dataset.markRead);save();apply();}));
  document.querySelector('[data-mark-all-read]')?.addEventListener('click',()=>{cards.forEach(c=>readSet.add(c.dataset.notificationId));save();apply();});
  document.querySelector('[data-clear-read]')?.addEventListener('click',()=>{cards.filter(c=>c.classList.contains('is-read')).forEach(c=>c.remove());apply();});
  apply();
})();
</script>
<script src="<%=request.getContextPath()%>/js/theme.js"></script>
</body></html>
