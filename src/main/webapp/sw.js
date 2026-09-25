const CACHE='nexora-smart-v1';
self.addEventListener('install',e=>{e.waitUntil(caches.open(CACHE).then(c=>c.addAll(['./','./marketplace','./css/style.css','./js/nexora.js','./manifest.webmanifest'])));self.skipWaiting();});
self.addEventListener('activate',e=>e.waitUntil(self.clients.claim()));
self.addEventListener('fetch',e=>{e.respondWith(caches.match(e.request).then(c=>c||fetch(e.request).then(r=>{if(e.request.method==='GET'&&r.ok&&new URL(e.request.url).pathname.match(/\.(css|js|svg|webmanifest)$/)){const copy=r.clone();caches.open(CACHE).then(cache=>cache.put(e.request,copy));}return r;}).catch(()=>c)));});
