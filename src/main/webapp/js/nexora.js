(() => {
  const ctx = document.body?.dataset?.ctx || '';
  const key = (name) => `nexora:${name}`;
  const read = (name) => { try { return JSON.parse(localStorage.getItem(key(name)) || '[]'); } catch { return []; } };
  const write = (name, value) => localStorage.setItem(key(name), JSON.stringify(value));

  // Wishlist
  const wishlist = read('wishlist');
  const wlButtons = [...document.querySelectorAll('[data-wishlist-id]')];
  const syncWishlist = () => {
    wlButtons.forEach(b => {
      const id = String(b.dataset.wishlistId);
      const active = wishlist.includes(id);
      b.classList.toggle('is-active', active);
      b.setAttribute('aria-pressed', active ? 'true' : 'false');
      b.textContent = active ? '♥' : '♡';
      b.title = active ? 'Remove from wishlist' : 'Add to wishlist';
    });
    document.querySelectorAll('[data-wishlist-count]').forEach(el => el.textContent = wishlist.length);
  };
  wlButtons.forEach(b => b.addEventListener('click', e => {
    e.preventDefault(); e.stopPropagation();
    const id = String(b.dataset.wishlistId);
    const i = wishlist.indexOf(id);
    if (i >= 0) wishlist.splice(i, 1); else wishlist.unshift(id);
    write('wishlist', wishlist); syncWishlist();
  }));
  syncWishlist();

  // Wishlist Pro page controls.
  const wishlistGrid = document.querySelector('[data-wishlist-grid]');
  const wishlistCards = [...document.querySelectorAll('[data-wishlist-card]')];
  const wishlistFilters = [...document.querySelectorAll('[data-wishlist-filter]')];
  const wishlistSort = document.querySelector('[data-wishlist-sort]');
  const removeWishlistButtons = [...document.querySelectorAll('[data-remove-wishlist]')];

  const refreshWishlistPage = () => {
    const ids = wishlist.join(',');
    if (wishlistGrid && !wishlist.length) { window.location.href = `${ctx}/wishlist?ids=`; return; }
    if (wishlistGrid) {
      wishlistCards.forEach(card => {
        const id = String(card.dataset.wishlistCardId);
        const keep = wishlist.includes(id);
        card.classList.toggle('is-hidden', !keep);
      });
    }
  };

  removeWishlistButtons.forEach(btn => btn.addEventListener('click', e => {
    e.preventDefault();
    const id = String(btn.dataset.removeWishlist || '');
    const i = wishlist.indexOf(id);
    if (i >= 0) { wishlist.splice(i, 1); write('wishlist', wishlist); toast('Removed from wishlist.'); }
    syncWishlist();
    refreshWishlistPage();
  }));

  const applyWishlistSort = () => {
    if (!wishlistGrid || !wishlistSort) return;
    const mode = wishlistSort.value;
    const cards = [...wishlistGrid.querySelectorAll('[data-wishlist-card]')];
    cards.sort((a,b) => {
      if (mode === 'low') return Number(a.dataset.price||0) - Number(b.dataset.price||0);
      if (mode === 'high') return Number(b.dataset.price||0) - Number(a.dataset.price||0);
      if (mode === 'name') return String(a.dataset.name||'').localeCompare(String(b.dataset.name||''));
      return Number(a.dataset.wishlistOrder||0) - Number(b.dataset.wishlistOrder||0);
    }).forEach(card => wishlistGrid.appendChild(card));
  };
  wishlistSort?.addEventListener('change', applyWishlistSort);

  wishlistFilters.forEach(btn => btn.addEventListener('click', () => {
    if (!wishlistGrid) return;
    wishlistFilters.forEach(x => x.classList.remove('active')); btn.classList.add('active');
    const filter = btn.dataset.wishlistFilter || 'all';
    wishlistCards.forEach(card => {
      const stock = Number(card.dataset.stock || 0);
      const keep = filter === 'all' || (filter === 'stock' && stock > 0) || (filter === 'out' && stock <= 0);
      const saved = wishlist.includes(String(card.dataset.wishlistCardId));
      card.classList.toggle('is-hidden', !(keep && saved));
    });
  }));

  document.querySelector('[data-clear-wishlist]')?.addEventListener('click', () => {
    if (!wishlist.length) return;
    wishlist.splice(0, wishlist.length); write('wishlist', wishlist); syncWishlist(); toast('Wishlist cleared.');
    setTimeout(() => refreshWishlistPage(), 160);
  });

  document.querySelector('[data-share-wishlist]')?.addEventListener('click', async () => {
    if (!wishlist.length) { toast('Your wishlist is empty.'); return; }
    const url = `${location.origin}${ctx}/wishlist?ids=${encodeURIComponent(wishlist.join(','))}`;
    try {
      if (navigator.share) await navigator.share({title:'My NexoraMart Wishlist',text:'My saved NexoraMart products',url});
      else if (navigator.clipboard) { await navigator.clipboard.writeText(url); toast('Wishlist link copied.'); }
      else toast('Wishlist link: ' + url);
    } catch (_) {}
  });
  applyWishlistSort();
  refreshWishlistPage();

  // Compare up to three products.
  const compare = read('compare');
  const syncCompare = () => {
    document.querySelectorAll('[data-compare-id]').forEach(input => input.checked = compare.includes(String(input.dataset.compareId)));
    document.querySelectorAll('[data-compare-count]').forEach(el => el.textContent = compare.length);
  };
  document.querySelectorAll('[data-compare-id]').forEach(input => input.addEventListener('change', () => {
    const id = String(input.dataset.compareId);
    if (input.checked) {
      if (compare.length >= 3) { input.checked = false; toast('Compare supports up to 3 products.'); return; }
      if (!compare.includes(id)) compare.push(id);
    } else {
      const i = compare.indexOf(id); if (i >= 0) compare.splice(i, 1);
    }
    write('compare', compare); syncCompare();
  }));
  const compareBtn = document.querySelector('[data-open-compare]');
  if (compareBtn) compareBtn.addEventListener('click', () => {
    if (!compare.length) { toast('Select at least one product to compare.'); return; }
    location.href = `${ctx}/compare?ids=${encodeURIComponent(compare.join(','))}`;
  });
  syncCompare();

  // Recently viewed + explainable personalization.
  const recent = read('recent');
  const recentMeta = (() => { try { return JSON.parse(localStorage.getItem(key('recentMeta')) || '{}'); } catch { return {}; } })();
  const rememberRecent = (id, meta={}) => {
    const x = String(id);
    const i = recent.indexOf(x);
    if (i >= 0) recent.splice(i, 1);
    recent.unshift(x);
    recent.splice(10);
    recentMeta[x] = { ...recentMeta[x], ...meta, viewedAt: Date.now() };
    // Trim metadata so the browser does not accumulate unbounded history.
    Object.keys(recentMeta).forEach(k => { if (!recent.includes(k)) delete recentMeta[k]; });
    write('recent', recent);
    write('recentMeta', recentMeta);
  };

  const currentId = document.body.dataset.productId;
  if (currentId) {
    rememberRecent(currentId, {
      name: document.body.dataset.productName || '',
      category: document.body.dataset.productCategory || '',
      brand: document.body.dataset.productBrand || '',
      price: document.body.dataset.productPrice || '0',
      image: document.body.dataset.productImage || ''
    });
  }

  const recentWrap = document.querySelector('[data-recent-grid]');
  const recentSection = document.querySelector('[data-recent-section]');
  if (recentWrap) {
    const catalogCards = [...document.querySelectorAll('[data-product-card]')];
    const cardById = new Map(catalogCards.map(card => [String(card.dataset.productCardId || ''), card]));
    recent.forEach(id => {
      const source = cardById.get(String(id));
      if (!source) return;
      const clone = source.cloneNode(true);
      clone.classList.add('recent-mini-card');
      clone.querySelectorAll('form').forEach(f => f.remove());
      clone.querySelectorAll('[data-compare-id]').forEach(x => x.remove());
      recentWrap.appendChild(clone);
    });
    if (recentWrap.children.length) recentSection?.classList.remove('hidden');
  }

  document.querySelector('[data-clear-recent]')?.addEventListener('click', () => {
    recent.splice(0, recent.length);
    Object.keys(recentMeta).forEach(k => delete recentMeta[k]);
    write('recent', recent); write('recentMeta', recentMeta);
    document.querySelector('[data-recent-grid]')?.replaceChildren();
    document.querySelector('[data-recent-section]')?.classList.add('hidden');
    toast('Recently viewed cleared.');
  });

  // Smart personalization: transparent, local ranking from recent views, wishlist and searches.
  const personalGrid = document.querySelector('[data-personal-grid]');
  const personalSection = document.querySelector('[data-personal-section]');
  if (personalGrid) {
    const cards = [...document.querySelectorAll('[data-product-card]')];
    const wishlistIds = new Set(read('wishlist').map(String));
    const searches = read('searches').map(String);
    const recentIds = recent.map(String);
    const recentCategory = recentIds.map(id => recentMeta[id]?.category).filter(Boolean)[0] || '';
    const recentBrand = recentIds.map(id => recentMeta[id]?.brand).filter(Boolean)[0] || '';

    const searchText = searches.join(' ').toLowerCase();
    const candidate = cards.map(card => {
      const category = String(card.dataset.category || '');
      const brand = String(card.dataset.brand || '');
      const name = String(card.dataset.name || '');
      const model = String(card.dataset.model || '');
      const text = `${category} ${brand} ${name} ${model}`.toLowerCase();
      let score = Number(card.dataset.stock || 0) > 0 ? 4 : -20;
      const reasons = [];
      if (recentIds.includes(String(card.dataset.productCardId))) score -= 35;
      if (recentCategory && category.toLowerCase() === recentCategory.toLowerCase()) { score += 42; reasons.push(`because you viewed ${recentCategory}`); }
      if (recentBrand && brand.toLowerCase() === recentBrand.toLowerCase()) { score += 22; reasons.push(`from ${recentBrand}`); }
      if ([...wishlistIds].length && wishlistIds.has(String(card.dataset.productCardId))) score -= 18;
      const searchTokens = searchText.split(/\s+/).filter(Boolean).slice(0, 10);
      const matchingSearch = searchTokens.some(t => t.length > 2 && text.includes(t));
      if (matchingSearch) { score += 28; reasons.push('matches your recent searches'); }
      if (!reasons.length && Number(card.dataset.stock || 0) > 0) reasons.push('in-stock campus pick');
      return { card, score, reason: reasons[0] };
    }).filter(x => x.score > 5).sort((a,b) => b.score - a.score).slice(0, 6);

    if (candidate.length) {
      candidate.forEach(({card, reason}) => {
        const clone = card.cloneNode(true);
        clone.classList.add('personal-card');
        clone.querySelectorAll('.compare-box').forEach(x => x.remove());
        clone.querySelectorAll('[data-wishlist-id]').forEach(x => x.setAttribute('data-personal-wish', 'true'));
        const body = clone.querySelector('.body');
        if (body) {
          const badge = document.createElement('div');
          badge.className = 'personal-reason';
          badge.textContent = `✨ ${reason || 'Recommended for you'}`;
          body.prepend(badge);
        }
        personalGrid.appendChild(clone);
      });
      const signal = document.querySelector('[data-personal-signal]');
      if (signal) signal.textContent = recentCategory ? `Based on ${recentCategory}` : (searches.length ? 'Based on your searches' : 'Smart campus picks');
      const sub = document.querySelector('[data-personal-subtext]');
      if (sub) sub.textContent = recentCategory ? `You recently explored ${recentCategory}. Here are related products worth a look.` : 'Recommendations update locally from your recent views and search activity.';
      personalSection?.classList.remove('hidden');
    }
  }

  // Smart Search 2.0: fuzzy, field-aware, image-rich live suggestions.
  const searchInput = document.querySelector('[data-live-search]');
  const searchForm = searchInput?.form;
  const suggestionBox = document.querySelector('[data-suggestions]');
  if (searchInput && suggestionBox) {
    const searchItems = [...document.querySelectorAll('[data-product-card]')].map(card => ({
      id: card.dataset.productCardId || '',
      name: card.dataset.name || '',
      brand: card.dataset.brand || '',
      model: card.dataset.model || '',
      category: card.dataset.category || '',
      ram: card.dataset.ram || '',
      storage: card.dataset.storage || '',
      display: card.dataset.display || '',
      camera: card.dataset.camera || '',
      battery: card.dataset.battery || '',
      price: card.dataset.price || '0',
      stock: card.dataset.stock || '0',
      image: card.querySelector('img')?.getAttribute('src') || ''
    }));

    const recentSearches = read('searches');
    let activeSuggestion = -1;
    let currentSuggestions = [];

    const normalize = value => String(value || '').toLowerCase().replace(/[^a-z0-9]+/g, ' ').trim();
    const tokens = value => normalize(value).split(/\s+/).filter(Boolean);
    const levenshtein = (a,b) => {
      a=normalize(a); b=normalize(b);
      if (a===b) return 0;
      if (!a || !b) return Math.max(a.length,b.length);
      if (a.length > b.length) [a,b]=[b,a];
      let prev=Array.from({length:a.length+1},(_,i)=>i);
      for(let j=1;j<=b.length;j++){
        const cur=[j];
        for(let i=1;i<=a.length;i++) cur[i]=Math.min(cur[i-1]+1,prev[i]+1,prev[i-1]+(a[i-1]===b[j-1]?0:1));
        prev=cur;
      }
      return prev[a.length];
    };
    const bestTokenScore = (qToken, field) => {
      const parts=tokens(field); if(!parts.length) return 0;
      let best=0;
      for(const part of parts){
        if(part===qToken) best=Math.max(best,64);
        else if(part.startsWith(qToken)) best=Math.max(best,52);
        else if(part.includes(qToken)) best=Math.max(best,42);
        else {
          const d=levenshtein(qToken,part);
          const limit=Math.max(1,Math.floor(Math.max(qToken.length,part.length)*0.45));
          if(d<=limit) best=Math.max(best,Math.max(0,34-d*7));
        }
      }
      return best;
    };

    function scoreItem(item, query){
      const q=normalize(query), qt=tokens(q); if(!qt.length) return 0;
      const fields=[item.name,item.brand,item.model,item.category,item.ram,item.storage,item.display,item.camera,item.battery];
      const name=normalize(item.name), brand=normalize(item.brand), model=normalize(item.model), category=normalize(item.category);
      let score=0;
      if(name===q) score+=180;
      if(name.startsWith(q)) score+=110;
      if(brand===q) score+=95;
      if(model===q) score+=90;
      if(category===q) score+=72;
      if(fields.some(f=>normalize(f).includes(q))) score+=55;
      qt.forEach(t=>{
        score += bestTokenScore(t,item.name)*1.45;
        score += bestTokenScore(t,item.brand)*1.05;
        score += bestTokenScore(t,item.model);
        score += Math.max(...fields.slice(3).map(f=>bestTokenScore(t,f)),0)*0.7;
      });
      if(Number(item.stock)>0) score+=4;
      return score;
    }

    const escape = value => String(value ?? '').replace(/[&<>'"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]));
    const money = value => { const n=Number(value||0); return Number.isFinite(n) ? `₹${n.toLocaleString('en-IN',{maximumFractionDigits:2})}` : '₹0'; };

    function saveSearch(q){
      const clean=String(q||'').trim(); if(clean.length<2) return;
      const next=[clean,...recentSearches.filter(x=>String(x).toLowerCase()!==clean.toLowerCase())].slice(0,6);
      recentSearches.splice(0,recentSearches.length,...next); write('searches',recentSearches);
    }

    function showRecent(){
      currentSuggestions=[]; activeSuggestion=-1; suggestionBox.innerHTML='';
      const head=document.createElement('div'); head.className='suggestion-head'; head.textContent='Recent searches'; suggestionBox.appendChild(head);
      if(!recentSearches.length){
        const empty=document.createElement('div'); empty.className='suggestion-empty'; empty.textContent='Start typing a product, brand, category or specification.'; suggestionBox.appendChild(empty);
      } else {
        recentSearches.slice(0,5).forEach(q=>{
          const a=document.createElement('a'); a.className='suggestion recent-search'; a.href=`${ctx}/marketplace?q=${encodeURIComponent(q)}`;
          a.innerHTML=`<span>↻ ${escape(q)}</span><small>Search again</small>`;
          a.addEventListener('click',()=>saveSearch(q)); suggestionBox.appendChild(a);
        });
      }
      suggestionBox.classList.remove('hidden');
    }

    function renderSuggestions(query){
      const scored=searchItems.map(item=>({item,score:scoreItem(item,query)})).filter(x=>x.score>=18).sort((a,b)=>b.score-a.score).slice(0,8);
      currentSuggestions=scored.map(x=>x.item); activeSuggestion=-1; suggestionBox.innerHTML='';
      const head=document.createElement('div'); head.className='suggestion-head';
      head.innerHTML=`<span>Smart matches</span><small>${scored.length ? `${scored.length} products found` : 'Try a brand, model or spec'}</small>`;
      suggestionBox.appendChild(head);
      if(!scored.length){
        const empty=document.createElement('div'); empty.className='suggestion-empty';
        empty.innerHTML=`No exact match. <b>Try simpler words</b> like “phone”, “gaming”, “8gb” or “battery”.`;
        suggestionBox.appendChild(empty); suggestionBox.classList.remove('hidden'); return;
      }
      scored.forEach(({item})=>{
        const a=document.createElement('a'); a.className='suggestion rich-suggestion'; a.href=`${ctx}/product/details?id=${encodeURIComponent(item.id)}`;
        const stock=Number(item.stock)>0 ? 'In stock' : 'Out of stock';
        const specs=[item.ram&&`RAM ${item.ram}`,item.storage&&`Storage ${item.storage}`,item.battery&&`Battery ${item.battery}`].filter(Boolean).slice(0,3).join(' · ');
        a.innerHTML=`<img src="${escape(item.image)}" alt=""><span class="suggestion-main"><b>${escape(item.name)}</b><small>${escape(item.brand)}${item.model?' · '+escape(item.model):''}</small><em>${escape(specs||item.category||'Product')}</em></span><span class="suggestion-side"><strong>${money(item.price)}</strong><small>${stock}</small></span>`;
        suggestionBox.appendChild(a);
      });
      const all=document.createElement('a'); all.className='suggestion-search-all'; all.href=`${ctx}/marketplace?q=${encodeURIComponent(query)}`; all.textContent=`View all results for “${query.trim()}” →`;
      all.addEventListener('click',()=>saveSearch(query)); suggestionBox.appendChild(all);
      suggestionBox.classList.remove('hidden');
    }

    function moveSelection(delta){
      const entries=[...suggestionBox.querySelectorAll('.rich-suggestion')]; if(!entries.length) return;
      activeSuggestion=(activeSuggestion+delta+entries.length)%entries.length;
      entries.forEach((el,i)=>el.classList.toggle('is-selected',i===activeSuggestion));
      entries[activeSuggestion].scrollIntoView({block:'nearest'});
    }

    searchInput.addEventListener('focus',()=>{ if(searchInput.value.trim().length<2) showRecent(); });
    searchInput.addEventListener('input',()=>{
      const q=searchInput.value.trim();
      if(q.length<2) { showRecent(); return; }
      renderSuggestions(q);
    });
    searchInput.addEventListener('keydown',e=>{
      if(suggestionBox.classList.contains('hidden')) return;
      if(e.key==='ArrowDown'){e.preventDefault();moveSelection(1);}
      else if(e.key==='ArrowUp'){e.preventDefault();moveSelection(-1);}
      else if(e.key==='Escape'){suggestionBox.classList.add('hidden'); activeSuggestion=-1;}
      else if(e.key==='Enter' && activeSuggestion>=0 && currentSuggestions[activeSuggestion]){
        e.preventDefault(); saveSearch(searchInput.value); location.href=`${ctx}/product/details?id=${encodeURIComponent(currentSuggestions[activeSuggestion].id)}`;
      }
    });
    searchForm?.addEventListener('submit',()=>saveSearch(searchInput.value));
    document.addEventListener('click',e=>{ if(!e.target.closest('.search-wrap')) suggestionBox.classList.add('hidden'); });
  }

  // Sort client-side to preserve the server-side search contract.
  const sort = document.querySelector('[data-sort-products]');
  const grid = document.querySelector('[data-product-grid]');
  if (sort && grid) sort.addEventListener('change', () => {
    const cards = [...grid.querySelectorAll('[data-product-card]')];
    cards.sort((a,b) => {
      const pa=Number(a.dataset.price||0), pb=Number(b.dataset.price||0), sa=Number(a.dataset.stock||0), sb=Number(b.dataset.stock||0);
      if (sort.value==='low') return pa-pb; if (sort.value==='high') return pb-pa; if (sort.value==='stock') return sb-sa;
      return 0;
    }); cards.forEach(c => grid.appendChild(c));
  });

  // Smart Match 2.0: explainable, local scoring with budget + priorities.
  const smartBtn = document.querySelector('[data-smart-match]');
  const smartModal = document.querySelector('#smart-modal');
  const smartResults = document.querySelector('[data-smart-results]');
  const smartBudget = document.querySelector('[data-smart-budget]');
  const smartPriority = [...document.querySelectorAll('[data-smart-priority]')];
  let smartUse = 'study';
  let smartPriorityValue = 'balanced';

  smartBtn?.addEventListener('click', () => smartModal?.classList.add('open'));
  smartPriority.forEach(btn => btn.addEventListener('click', () => {
    smartPriority.forEach(x => x.classList.remove('active'));
    btn.classList.add('active');
    smartPriorityValue = btn.dataset.smartPriority || 'balanced';
    runSmartMatch();
  }));

  const num = value => {
    const match = String(value || '').replace(/,/g,'').match(/[0-9]+(?:\.[0-9]+)?/);
    return match ? Number(match[0]) : 0;
  };
  const scorePercent = value => Math.max(0, Math.min(100, Math.round(value)));

  function runSmartMatch() {
    const cards = [...document.querySelectorAll('[data-product-card]')];
    const budget = Number(smartBudget?.value || 0);
    const use = smartUse;
    const priority = smartPriorityValue;

    const scored = cards.map(c => {
      const ram = num(c.dataset.ram);
      const storage = num(c.dataset.storage);
      const battery = num(c.dataset.battery);
      const camera = num(c.dataset.camera);
      const display = num(c.dataset.display);
      const price = Number(c.dataset.price || 0);
      const stock = Number(c.dataset.stock || 0);
      const category = String(c.dataset.category || '').toLowerCase();
      let score = stock > 0 ? 6 : -30;
      const reasons = [];

      if (budget > 0) {
        if (price <= budget) { score += 28; reasons.push('within your budget'); }
        else {
          const over = (price - budget) / budget;
          score += Math.max(-24, 8 - over * 35);
          if (over <= 0.15) reasons.push('slightly above budget');
        }
      }

      const add = (condition, points, reason) => { if (condition) { score += points; reasons.push(reason); } };
      if (use === 'study') {
        add(category.includes('laptop'), 24, 'study & coding friendly');
        add(ram >= 8, 13, '8GB+ RAM');
        add(storage >= 256, 11, '256GB+ storage');
        add(display >= 14, 5, 'comfortable display');
      } else if (use === 'gaming') {
        add(ram >= 8, 18, '8GB+ RAM');
        add(storage >= 256, 14, 'large storage');
        add(category.includes('laptop') || category.includes('mobile'), 12, 'gaming-capable category');
        add(battery >= 5000, 7, 'strong battery');
      } else if (use === 'camera') {
        add(camera >= 100, 28, 'high-resolution camera');
        add(camera >= 50, 12, '50MP-class camera');
        add(category.includes('mobile'), 15, 'mobile photography fit');
      } else if (use === 'battery') {
        add(battery >= 6000, 32, '6000mAh-class battery');
        add(battery >= 5000, 15, '5000mAh-class battery');
      } else if (use === 'budget') {
        add(price <= 5000, 28, 'budget-first price');
        add(price > 5000 && price <= 15000, 18, 'value-range price');
      }

      if (priority === 'performance') {
        add(ram >= 8, 9, 'performance priority');
        add(storage >= 256, 7, 'faster multitasking/storage fit');
      } else if (priority === 'battery') {
        add(battery >= 5000, 12, 'battery priority');
      } else if (priority === 'camera') {
        add(camera >= 50, 12, 'camera priority');
      } else if (priority === 'value') {
        if (price > 0 && budget > 0) {
          const ratio = Math.max(0, 1 - price / budget);
          score += ratio * 12;
        } else if (price <= 15000) score += 8;
        reasons.push('value priority');
      } else {
        add(stock > 0, 3, 'currently in stock');
      }

      // Normalize to a user-facing 0–100 score without pretending this is an ML probability.
      const match = scorePercent(48 + score * 1.18);
      return { c, score: match, reasons: [...new Set(reasons)].slice(0, 4) };
    })
      .filter(x => Number(x.c.dataset.stock || 0) > 0)
      .sort((a,b) => b.score - a.score)
      .slice(0, 5);

    if (!smartResults) return;
    smartResults.innerHTML = scored.length ? scored.map((x, i) => `
      <div class="smart-result pro-result">
        <div class="smart-rank">#${i + 1}</div>
        <img src="${x.c.querySelector('img')?.src || ''}" alt="">
        <div class="smart-result-copy">
          <b>${escapeHtml(x.c.dataset.name || 'Product')}</b>
          <span class="smart-score">${x.score}% match</span>
          <small>${escapeHtml(x.reasons.join(' · ') || 'Catalog match')}</small>
        </div>
        <a class="button tiny" href="${ctx}/product/details?id=${encodeURIComponent(x.c.dataset.productCardId)}">View</a>
      </div>`).join('') : '<p class="muted">No in-stock products matched. Try a wider budget or another use case.</p>';
  }

  document.querySelectorAll('[data-smart-use]').forEach(btn => btn.addEventListener('click', () => {
    if (!btn.dataset.smartUse || btn.dataset.smartUse === 'budget' || btn.dataset.smartUse === 'battery' || btn.dataset.smartUse === 'study' || btn.dataset.smartUse === 'gaming' || btn.dataset.smartUse === 'camera') {
      smartUse = btn.dataset.smartUse || 'study';
      document.querySelectorAll('#smart-modal [data-smart-use]').forEach(x => x.classList.toggle('active', x === btn));
      runSmartMatch();
    }
  }));

  // Quick view modal from the existing card content.
  document.querySelectorAll('[data-quick-view]').forEach(btn => btn.addEventListener('click', () => {
    const card = btn.closest('[data-product-card]'); if(!card) return;
    const modal = document.querySelector('#quick-modal'); if(!modal) return;
    modal.querySelector('[data-qv-title]').textContent = card.dataset.name || 'Product';
    modal.querySelector('[data-qv-price]').textContent = `₹${card.dataset.price}`;
    modal.querySelector('[data-qv-body]').innerHTML = card.querySelector('.specs')?.outerHTML || card.querySelector('.body')?.innerHTML || '';
    modal.querySelector('[data-qv-link]').href = `${ctx}/product/details?id=${encodeURIComponent(card.dataset.productCardId)}`;
    modal.querySelector('[data-qv-image]').src = card.querySelector('img')?.src || '';
    modal.classList.add('open');
  }));
  document.querySelectorAll('[data-close-modal]').forEach(x => x.addEventListener('click', () => x.closest('.modal')?.classList.remove('open')));
  document.querySelectorAll('.modal').forEach(m => m.addEventListener('click', e => { if(e.target===m) m.classList.remove('open'); }));

  // Voice search using browser Web Speech API when available.
  const voice = document.querySelector('[data-voice-search]');
  if (voice && ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window)) {
    voice.addEventListener('click', () => {
      const Rec = window.SpeechRecognition || window.webkitSpeechRecognition; const rec = new Rec(); rec.lang='en-IN';
      voice.classList.add('listening'); rec.start(); rec.onresult = e => { searchInput.value=e.results[0][0].transcript; searchInput.form?.submit(); }; rec.onend=()=>voice.classList.remove('listening');
    });
  } else if (voice) voice.title='Voice search requires a supported browser';

  // Visual Search: browser-local image matching against the Nexora catalog.
  const visualModal = document.querySelector('#visual-search-modal');
  const visualFile = document.querySelector('[data-visual-file]');
  const visualDrop = document.querySelector('[data-visual-drop]');
  const visualPreviewWrap = document.querySelector('[data-visual-preview-wrap]');
  const visualPreview = document.querySelector('[data-visual-preview]');
  const visualFileName = document.querySelector('[data-visual-file-name]');
  const visualStatus = document.querySelector('[data-visual-status]');
  const visualProgress = document.querySelector('[data-visual-progress]');
  const visualResults = document.querySelector('[data-visual-results]');
  const visualOpeners = document.querySelectorAll('[data-open-visual-search]');
  let visualSelectedFile = null;
  let visualCatalogPromise = null;

  visualOpeners.forEach(btn => btn.addEventListener('click', () => visualModal?.classList.add('open')));
  if (visualModal) visualModal.addEventListener('click', e => { if (e.target === visualModal) visualModal.classList.remove('open'); });

  function setVisualFile(file) {
    if (!file || !file.type.startsWith('image/')) { toast('Please select an image file.'); return; }
    visualSelectedFile = file;
    const url = URL.createObjectURL(file);
    if (visualPreview) visualPreview.src = url;
    if (visualFileName) visualFileName.textContent = file.name;
    if (visualStatus) visualStatus.textContent = 'Ready to compare with the catalog';
    visualPreviewWrap?.classList.remove('hidden');
    visualResults?.replaceChildren();
  }

  visualFile?.addEventListener('change', e => setVisualFile(e.target.files?.[0]));
  if (visualDrop) {
    ['dragenter','dragover'].forEach(type => visualDrop.addEventListener(type, e => { e.preventDefault(); visualDrop.classList.add('dragging'); }));
    ['dragleave','drop'].forEach(type => visualDrop.addEventListener(type, e => { e.preventDefault(); visualDrop.classList.remove('dragging'); }));
    visualDrop.addEventListener('drop', e => setVisualFile(e.dataTransfer?.files?.[0]));
    visualDrop.addEventListener('click', e => { if (!e.target.closest('label')) visualFile?.click(); });
  }

  document.querySelector('[data-run-visual-search]')?.addEventListener('click', async () => {
    if (!visualSelectedFile) { toast('Choose a photo first.'); return; }
    try {
      visualProgress?.classList.remove('hidden');
      if (visualStatus) visualStatus.textContent = 'Loading product catalog…';
      const catalog = await loadVisualCatalog();
      if (visualStatus) visualStatus.textContent = `Comparing against ${catalog.length} products…`;
      const matches = await findVisualMatches(visualSelectedFile, catalog);
      renderVisualResults(matches);
    } catch (e) {
      console.error(e);
      if (visualResults) visualResults.innerHTML = '<div class="visual-error">Could not compare this image right now. Please try another photo.</div>';
      if (visualStatus) visualStatus.textContent = 'Search failed';
    } finally {
      visualProgress?.classList.add('hidden');
    }
  });

  async function loadVisualCatalog() {
    if (!visualCatalogPromise) {
      visualCatalogPromise = fetch(`${ctx}/api/visual-catalog`, {headers:{'Accept':'application/json'}}).then(r => {
        if (!r.ok) throw new Error('Catalog request failed');
        return r.json();
      });
    }
    return visualCatalogPromise;
  }

  async function findVisualMatches(file, catalog) {
    const query = await imageDescriptor(URL.createObjectURL(file));
    const scored = [];
    for (const product of catalog) {
      let best = Infinity;
      for (const imagePath of (product.images || []).slice(0, 3)) {
        try {
          const d = await imageDescriptor(`${ctx}/${imagePath}`);
          best = Math.min(best, descriptorDistance(query, d));
        } catch (_) { /* skip a broken catalog image */ }
      }
      if (Number.isFinite(best)) scored.push({...product, distance: best});
    }
    return scored.sort((a,b)=>a.distance-b.distance).slice(0,6).map(x => ({...x, similarity: distanceToSimilarity(x.distance)}));
  }

  // Compact visual fingerprint: normalized low-resolution grayscale + RGB histogram.
  async function imageDescriptor(src) {
    const img = await loadImage(src);
    const size = 20;
    const canvas = document.createElement('canvas'); canvas.width=size; canvas.height=size;
    const c = canvas.getContext('2d', {willReadFrequently:true});
    const crop = Math.min(img.naturalWidth || img.width, img.naturalHeight || img.height);
    const sx = ((img.naturalWidth || img.width) - crop) / 2;
    const sy = ((img.naturalHeight || img.height) - crop) / 2;
    c.drawImage(img, sx, sy, crop, crop, 0, 0, size, size);
    const px = c.getImageData(0,0,size,size).data;
    const gray=[]; const hist=new Array(27).fill(0);
    let sum=0, count=size*size;
    for(let i=0;i<px.length;i+=4){
      const r=px[i], g=px[i+1], b=px[i+2];
      const y=(0.299*r+0.587*g+0.114*b)/255; gray.push(y); sum+=y;
      const bin=Math.min(2,Math.floor(r/86))*9+Math.min(2,Math.floor(g/86))*3+Math.min(2,Math.floor(b/86)); hist[bin]++;
    }
    const mean=sum/count; let variance=0;
    for(const v of gray) variance+=(v-mean)*(v-mean);
    const std=Math.sqrt(variance/count)||0.2;
    return {gray:gray.map(v=>Math.max(-2,Math.min(2,(v-mean)/std))), hist:hist.map(v=>v/count)};
  }

  function descriptorDistance(a,b) {
    let mse=0;
    for(let i=0;i<a.gray.length;i++){ const d=a.gray[i]-b.gray[i]; mse+=d*d; }
    mse/=a.gray.length;
    let hist=0;
    for(let i=0;i<a.hist.length;i++) hist+=Math.abs(a.hist[i]-b.hist[i]);
    return (0.78*mse)+(0.22*hist);
  }

  function distanceToSimilarity(d) {
    return Math.max(1, Math.min(99, Math.round(100*Math.exp(-2.3*d))));
  }

  function loadImage(src) {
    return new Promise((resolve,reject)=>{
      const img=new Image(); img.onload=()=>resolve(img); img.onerror=reject; img.src=src;
    });
  }

  function renderVisualResults(matches) {
    if (!visualResults) return;
    if (!matches.length) { visualResults.innerHTML='<div class="visual-error">No catalog image could be compared.</div>'; return; }
    visualResults.innerHTML = `<div class="visual-result-heading"><span class="eyebrow">VISUAL MATCHES</span><h3>Closest products in NexoraMart</h3><small>Your photo stays in this browser. Results are based on the closest catalog image fingerprint.</small></div>` +
      matches.map((p, i) => {
        const img = `${ctx}/${(p.images||[])[0] || ''}`;
        return `<article class="visual-result-card"><div class="visual-result-rank">${i+1}</div><img src="${img}" alt="${escapeHtml(p.name||'Product')}"><div class="visual-result-copy"><div class="micro">${escapeHtml(p.brand||'Nexora')} · ${escapeHtml(p.model||'')}</div><h4>${escapeHtml(p.name||'Product')}</h4><div class="visual-match-pill">${p.similarity}% visual match</div><strong class="price">₹${escapeHtml(String(p.price??'0'))}</strong><div class="visual-mini-specs"><span>RAM <b>${escapeHtml(p.ram||'—')}</b></span><span>Storage <b>${escapeHtml(p.storage||'—')}</b></span><span>Display <b>${escapeHtml(p.display||'—')}</b></span><span>Camera <b>${escapeHtml(p.camera||'—')}</b></span><span>Battery <b>${escapeHtml(p.battery||'—')}</b></span><span>Stock <b>${escapeHtml(String(p.stock??0))}</b></span></div><a class="button tiny" href="${ctx}/product/details?id=${encodeURIComponent(p.id)}">View full details →</a></div></article>`;
      }).join('');
  }

  // PWA + service worker.
  if ('serviceWorker' in navigator) navigator.serviceWorker.register(`${ctx}/sw.js`).catch(()=>{});

  // Smart assistant drawer.
  const chatOpen = document.querySelector('[data-open-assistant]');
  const chat = document.querySelector('#assistant');
  const chatForm = document.querySelector('[data-assistant-form]');
  const chatLog = document.querySelector('[data-chat-log]');
  if(chatOpen && chat) chatOpen.addEventListener('click',()=>chat.classList.add('open'));

  document.querySelectorAll('[data-assistant-prompt]').forEach(btn => btn.addEventListener('click', () => {
    if(!chatForm) return;
    const input=chatForm.querySelector('input[name="message"]');
    input.value=btn.dataset.assistantPrompt || '';
    input.focus();
  }));

  if(chatForm) chatForm.addEventListener('submit', async (e)=>{
    e.preventDefault();
    const input=chatForm.querySelector('input[name="message"]');
    const msg=input.value.trim();
    if(!msg) return;
    addChat('you',msg); input.value='';
    const typing = addChat('bot','Thinking…');
    try {
      const body=new URLSearchParams({message:msg});
      const res=await fetch(`${ctx}/assistant`,{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body});
      if(!res.ok) throw new Error('assistant request failed');
      const data=await res.json();
      typing?.remove();
      addChat('bot',data.reply,data.products,data.link);
    } catch {
      typing?.remove();
      addChat('bot','I could not reach the assistant right now. Try Search or Categories.');
    }
  });

  function addChat(who,text,products,link){
    if(!chatLog) return null;
    const el=document.createElement('div');
    el.className=`chat-bubble ${who}`;
    el.innerHTML=`<p>${escapeHtml(text)}</p>`;
    if(link) el.innerHTML+=`<a class="button tiny" href="${link}">Open</a>`;
    if(products?.length) el.innerHTML+=products.map(p=>`<a class="chat-product" href="${ctx}/product/details?id=${encodeURIComponent(p.id)}"><img src="${p.image}" alt=""><span><b>${escapeHtml(p.name)}</b><small>₹${escapeHtml(String(p.price))} · ${escapeHtml(p.reason)} · ${escapeHtml(String(p.score||''))}% match</small></span></a>`).join('');
    chatLog.appendChild(el); chatLog.scrollTop=chatLog.scrollHeight; return el;
  }

  function toast(msg){ const t=document.createElement('div'); t.className='toast'; t.textContent=msg; document.body.appendChild(t); setTimeout(()=>t.classList.add('show'),20); setTimeout(()=>{t.classList.remove('show');setTimeout(()=>t.remove(),220)},2200); }
  function escapeHtml(s){ return String(s).replace(/[&<>'"]/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c])); }
})();
