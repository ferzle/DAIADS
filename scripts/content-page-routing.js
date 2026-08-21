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

  // Give unlabeled demo controls a useful accessible name. Most controls are
  // already wrapped by a <label>; this covers the older demos that relied on
  // placeholders or nearby visual text instead. The fallback is intentionally
  // conservative and only adds an ARIA name when no native label exists.
  const addFallbackControlNames = () => {
    const controls = document.querySelectorAll('input, select, textarea');
    controls.forEach((control) => {
      if (control.hasAttribute('aria-label') || control.hasAttribute('aria-labelledby')) return;
      if (control.labels && control.labels.length) return;

      const id = control.id || '';
      const placeholder = control.getAttribute('placeholder') || '';
      const type = (control.getAttribute('type') || '').toLowerCase();
      let name = placeholder;

      if (!name && id) {
        name = id
          .replace(/[-_]+/g, ' ')
          .replace(/([a-z])([A-Z])/g, '$1 $2')
          .replace(/\b(input|select|range)\b/gi, '')
          .trim();
      }

      if (!name) {
        if (type === 'checkbox') name = 'Option';
        else if (type === 'radio') name = 'Choice';
        else name = 'Value';
      }

      name = name.charAt(0).toUpperCase() + name.slice(1);
      if (type === 'checkbox' && /theme/i.test(id)) name = 'Toggle theme';
      control.setAttribute('aria-label', name);
    });
  };

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', addFallbackControlNames, { once: true });
  } else {
    addFallbackControlNames();
  }

  // Content documents stay put in DAIADS iframes. Standalone visits, including
  // old search-result URLs, move into the complete site interface.
  if (window.top === window.self) {
    const redirectURL = new URL(canonicalURL.href);
    redirectURL.hash = window.location.hash;
    window.location.replace(redirectURL.href);
  }
})();
