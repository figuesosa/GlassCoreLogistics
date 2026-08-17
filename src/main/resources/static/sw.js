const CACHE = 'glasscore-shell-v1';
const ASSETS = [
  '/css/app.css',
  '/js/app.js',
  '/img/logo.svg',
  '/offline.html',
  '/manifest.json'
];

self.addEventListener('install', function (event) {
  event.waitUntil(
    caches.open(CACHE).then(function (cache) {
      return cache.addAll(ASSETS);
    })
  );
  self.skipWaiting();
});

self.addEventListener('activate', function (event) {
  event.waitUntil(
    caches.keys().then(function (keys) {
      return Promise.all(keys.filter(function (k) { return k !== CACHE; }).map(function (k) {
        return caches.delete(k);
      }));
    })
  );
  self.clients.claim();
});

self.addEventListener('fetch', function (event) {
  const req = event.request;
  if (req.method !== 'GET') {
    return;
  }
  const url = new URL(req.url);
  if (url.origin !== location.origin) {
    return;
  }
  const isShell = ASSETS.indexOf(url.pathname) !== -1;
  if (!isShell) {
    return;
  }
  event.respondWith(
    caches.match(req).then(function (cached) {
      return cached || fetch(req).catch(function () {
        if (req.mode === 'navigate') {
          return caches.match('/offline.html');
        }
      });
    })
  );
});
