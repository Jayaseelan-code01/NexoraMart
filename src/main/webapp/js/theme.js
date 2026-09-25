
(function(){
  const themes={
    blue:{label:'Nexora Blue',cls:'nx-theme-blue',dot:'#5b8cff'},
    emerald:{label:'Emerald',cls:'nx-theme-emerald',dot:'#17c78a'},
    purple:{label:'Royal Purple',cls:'nx-theme-purple',dot:'#9b6cff'},
    orange:{label:'Sunset Orange',cls:'nx-theme-orange',dot:'#ff8a34'},
    cyan:{label:'Cyber Cyan',cls:'nx-theme-cyan',dot:'#21c7d9'}
  };
  const key='nexora-theme';
  function apply(name){
    const chosen=themes[name]?name:'blue';
    Object.values(themes).forEach(t=>document.body.classList.remove(t.cls));
    document.body.classList.add(themes[chosen].cls);
    localStorage.setItem(key,chosen);
    document.querySelectorAll('.nx-theme-option').forEach(b=>b.classList.toggle('active',b.dataset.theme===chosen));
  }
  function init(){
    const saved=localStorage.getItem(key)||'blue';
    const panel=document.createElement('div'); panel.className='nx-theme-panel';
    panel.innerHTML='<div class="nx-theme-menu" id="nx-theme-menu"><div class="nx-theme-title">Nexora Theme</div>'+
      Object.entries(themes).map(([k,t])=>`<button type="button" class="nx-theme-option" data-theme="${k}"><span class="nx-theme-dot" style="background:${t.dot}"></span>${t.label}</button>`).join('')+
      '</div><button type="button" class="nx-theme-toggle" aria-label="Change theme" title="Change theme">🎨</button>';
    document.body.appendChild(panel);
    const menu=panel.querySelector('.nx-theme-menu');
    panel.querySelector('.nx-theme-toggle').addEventListener('click',()=>menu.classList.toggle('open'));
    panel.querySelectorAll('.nx-theme-option').forEach(b=>b.addEventListener('click',()=>{apply(b.dataset.theme);menu.classList.remove('open')}));
    apply(saved);
  }
  if(document.readyState==='loading') document.addEventListener('DOMContentLoaded',init); else init();
})();
