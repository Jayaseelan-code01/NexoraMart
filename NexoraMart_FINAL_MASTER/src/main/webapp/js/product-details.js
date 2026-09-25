(() => {
  const photos=[...document.querySelectorAll('[data-photo]')];
  const thumbs=[...document.querySelectorAll('[data-thumb]')];
  if(!photos.length) return;
  let index=0, timer;
  const setImage=(i,autoplay=true)=>{
    index=(i+photos.length)%photos.length;
    photos.forEach(p=>p.classList.toggle('active',Number(p.dataset.photo)===index));
    thumbs.forEach(t=>t.classList.toggle('selected',Number(t.dataset.thumb)===index));
    if(autoplay) restart();
  };
  const restart=()=>{clearInterval(timer); if(photos.length>1) timer=setInterval(()=>setImage(index+1,false),4200);};
  thumbs.forEach(t=>t.addEventListener('click',()=>setImage(Number(t.dataset.thumb))));
  document.querySelector('[data-gallery-prev]')?.addEventListener('click',()=>setImage(index-1));
  document.querySelector('[data-gallery-next]')?.addEventListener('click',()=>setImage(index+1));
  const openZoom=()=>{
    const modal=document.querySelector('#image-modal'), target=document.querySelector('[data-zoom-image]');
    const current=photos[index]; if(!modal||!target||!current) return;
    target.src=current.src; target.alt=current.alt; modal.classList.add('open');
  };
  document.querySelector('[data-open-zoom]')?.addEventListener('click',openZoom);
  photos.forEach(p=>p.addEventListener('click',openZoom));
  document.querySelector('[data-close-modal]')?.addEventListener('click',()=>document.querySelector('#image-modal')?.classList.remove('open'));
  document.querySelector('#image-modal')?.addEventListener('click',e=>{if(e.target.id==='image-modal') e.currentTarget.classList.remove('open');});

  const qty=document.querySelector('[data-detail-qty]');
  const max=qty?Number(qty.max):1;
  document.querySelector('[data-qty-minus]')?.addEventListener('click',()=>{if(qty) qty.value=Math.max(1,Number(qty.value||1)-1);});
  document.querySelector('[data-qty-plus]')?.addEventListener('click',()=>{if(qty) qty.value=Math.min(max,Number(qty.value||1)+1);});

  document.querySelector('[data-share-product]')?.addEventListener('click',async()=>{
    try{await navigator.clipboard.writeText(location.href); const b=document.querySelector('[data-share-product]'); const old=b.textContent; b.textContent='✓ Link copied'; setTimeout(()=>b.textContent=old,1400);}catch{location.href=`mailto:?subject=NexoraMart product&body=${encodeURIComponent(location.href)}`;}
  });
  restart();
})();
