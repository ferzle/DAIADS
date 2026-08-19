(() => {
  const contentMarker = '/Content/';
  const pathname = window.location.pathname;
  const markerIndex = pathname.indexOf(contentMarker);

  if (markerIndex === -1 || !pathname.toLowerCase().endsWith('.html')) return;

  const encodedPath = pathname
    .slice(markerIndex + contentMarker.length)
    .replace(/\.html$/i, '');

  let path;
  try {
    path = encodedPath
      .split('/')
      .map((segment) => decodeURIComponent(segment))
      .join('/');
  } catch {
    return;
  }

  const siteRoot = pathname.slice(0, markerIndex + 1);
  const canonicalURL = new URL(siteRoot, window.location.origin);
  canonicalURL.searchParams.set('path', path);

  let canonicalLink = document.querySelector('link[rel="canonical"]');
  if (!canonicalLink) {
    canonicalLink = document.createElement('link');
    canonicalLink.rel = 'canonical';
    document.head.appendChild(canonicalLink);
  }
  canonicalLink.href = canonicalURL.href;

  // Content documents stay put in DAIADS iframes. Standalone visits, including
  // old search-result URLs, move into the complete site interface.
  if (window.top === window.self) {
    const redirectURL = new URL(canonicalURL.href);
    redirectURL.hash = window.location.hash;
    window.location.replace(redirectURL.href);
  }
})();
