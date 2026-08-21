// File: scripts/loadContent.js
// File: scripts/loadContent.js
// NOTE: a-CLICK a link to copy a full HTML URL. 
// E.g. <li><a href="?path=Algorithms%2FGreedy%2FMerge">Merge</a></li>
// Documented here to remind me.
history.scrollRestoration = 'manual';


let aKeyDown = false;
window.addEventListener('keydown', (e) => {
  if (e.key === 'a' || e.key === 'A') aKeyDown = true;
});
window.addEventListener('keyup', (e) => {
  if (e.key === 'a' || e.key === 'A') aKeyDown = false;
});
window.addEventListener('blur', () => aKeyDown = false);


// ─── Ordering Constants ───────────────────────────────────────────────────────
const TOP_LEVEL_ORDER = [
  'Start Here',
  'Foundations',
  'Analysis',
  'Analysis_DRAFT',
  'Data Structures',
  'Techniques',
  'Algorithms',
  'Appendix',
  'Problems',
  'Demos',
  'More'
];
const TOP_LEVEL_NUMBER_LABELS = {
  'Foundations': '1',
  'Analysis': '2',
  'Data Structures': '3',
  'Techniques': '4',
  'Algorithms': '5',
  'Appendix': 'A',
  'Problems': 'B',
  'Demos': 'C',
  'More': 'D'
};
const NUMBERED_CHILD_SECTIONS = new Set([
  'Foundations',
  'Analysis',
  'Data Structures',
  'Techniques',
  'Algorithms',
  'Appendix',
  'Problems',
  'More'
]);
const NUMBERED_PAGE_SECTION_HEADINGS = new Set([
  'Foundations',
  'Analysis',
  'Data Structures',
  'Techniques',
  'Algorithms'
]);
const UNNUMBERED_SECTION_PATHS = new Set([
  'More/Glossary'
]);
const PROBLEMS_ORDER = [
  'Problem List',
  'Foundational',
  'Optimization',
  'Geometry',
  'Graphs',
  'Other'
];
const ALGORITHMS_ORDER = [
  'Introduction',
  'Brute Force',
  'Exhaustive Search',
  'Divide-and-Conquer',
  'Decrease-and-Conquer',
  'Greedy',
  'Greedy Algorithms',
  'Dynamic Programming',
  'Transform-and-Conquer',
  'Space-Time Tradeoff',
  'Backtracking',
  'Branch-and-Bound',
  'Randomized'
];
const DECREASE_AND_CONQUER_ORDER = [
  'Introduction',
  'Decrease-by-a-Constant',
  'Decrease-by-a-Constant-Factor',
  'Variable-Size-Decrease',
  'Summary'
];
const FOUNDATIONS_ORDERS = {
  'Foundations/': [
    'Introduction',
	'ADTs',
    'Recursion',
	'Basic Analysis'
  ],
  'Foundations/Measuring Efficiency/': [
    'Cost Models and Input Size',
    'Best, Worst, Average, and Expected Cases',
    'Counting Work in Iterative Algorithms',
    'Asymptotic Notation',
    'Common Growth Rates'
  ]
};

const ANALYSIS_ORDERS = {
  'Analysis/': [
    'Overview',
    'Asymptotic Notation',
    'Asymptotic Properties',
    'Asymptotic Proofs',
    'Common Growth Rates',
    'Algorithm Analysis Fundamentals',
    'Algorithm Analysis Examples',
    'Analyzing Recursive Algorithms'
  ]
};
const DATA_STRUCTURES_ORDERS = {
  'Data Structures/': [
    'Overview',
    'Linear Structures',
    'Trees',
    'Priority Queues and Heaps',
    'Sets, Maps, and Hash Tables',
    'Graphs',
    //'Disjoint Sets',
    //'String Structures',
    //'Advanced Structures'
  ],
  'Data Structures/Linear Structures/': [
    'Representing Linear Structures',
    'Stacks',
    'Queues',
    'Lists',
    'Deques',
    //'Dynamic Arrays',
  ],
  'Data Structures/Trees/': [
    'Introduction to Trees',
    'Binary Trees',
    'Tree Traversals',
    'Binary Search Trees',
    'Balanced Search Trees',
    'AVL Trees',
    '2-3 Trees',
    //'Red-Black Trees',
    //'B-Trees'
  ],
  'Data Structures/Priority Queues and Heaps/': [
    'Priority Queues',
    'Binary Heaps',
    'Heap Construction and Heapsort'
  ],
  'Data Structures/Sets, Maps, and Hash Tables/': [
    'Introduction',
    'Sets',
    'Maps',
    'Hash Tables',
    'Separate Chaining',
    'Open Addressing'
  ]
  //,
  //'Data Structures/Graph Structures/': [
  //  'Graphs',
  //  'Edge Lists',
  //  'Adjacency Lists',
  //  'Adjacency Matrices'
  //],
  //'Data Structures/Disjoint Sets/': [
  //  'Union-Find'
  //],
  //'Data Structures/String Structures/': [
  //  'Tries',
  //  'Suffix Arrays',
  //  'Suffix Trees'
  //],
  //'Data Structures/Advanced Structures/': [
  //  'Fenwick Trees',
  //  'Segment Trees',
  //  'Sparse Tables',
  //  'k-d Trees'
  //]
};

const APPENDIX_ORDERS = {
  'Appendix/': [
    'Mathematical Background',
    'Recurrences'
  ],
  'Appendix/Recurrences/': [
    'Introduction',
    'Substitution Method',
    'Iteration Method',
    'Master Theorem',
    'Linear Recurrence Relations',
	'Mixed Practice'
  ]
};

// For "Demos", we just mirror the same order as the algorithm names:
const DEMOS_ORDER = [...ALGORITHMS_ORDER];
const TECHNIQUES_ORDER = [...ALGORITHMS_ORDER];
let chaptersData = {};
let booksData = {
  defaultBook: 'all-grouped',
  books: [{ id: 'all-grouped', label: 'All: Grouped' }]
};
let activeBookId = 'all-grouped';

let menuNumbering = {
  topLevel: new Map(),
  entries: new Map(),
  pages: new Map()
};

const getBook = (bookId = activeBookId) =>
  booksData.books.find((book) => book.id === bookId) || booksData.books[0];

const isKnownBook = (bookId) =>
  booksData.books.some((book) => book.id === bookId);

const isAllContentView = (bookId = activeBookId) =>
  bookId === 'all-grouped' || bookId === 'all-alphabetic';

const getRequestedBookId = () => {
  const fromURL = new URLSearchParams(window.location.search).get('book');
  if (fromURL && isKnownBook(fromURL)) return fromURL;

  return booksData.defaultBook || 'all-grouped';
};

const buildNavigationURL = (path, bookId = activeBookId) => {
  const params = new URLSearchParams();
  params.set('book', bookId);
  params.set('path', path);
  return `?${params.toString()}`;
};

// ─── Utility Functions ────────────────────────────────────────────────────────
// Return the current “path” either from history.state or URL param or fallback
const getCurrentPath = () =>
  history.state?.path ||
  new URLSearchParams(window.location.search).get('path') ||
  'Start Here/About';

// Ensure each segment of a path is properly URI‐encoded (but not double‐encoded)
const normalizePath = (path) =>
  path
    .split('/')
    .map((segment) => encodeURIComponent(decodeURIComponent(segment)))
    .join('/');

// Extract a “key” from either a string filename or an object entry
const getKey = (entry) =>
  typeof entry === 'string'
    ? entry.replace(/\.html$/, '')
    : Object.keys(entry)[0];

const getDisplayLabel = (entry) => {
  const key = getKey(entry);
  return key.split('/').pop().replace(/Demo$/, '').trim();
};

const getOrderListForPath = (pathPrefix) => {
  if (pathPrefix === 'Techniques/Decrease-and-Conquer/') return DECREASE_AND_CONQUER_ORDER;
  if (pathPrefix === 'Techniques/') return TECHNIQUES_ORDER;
  if (pathPrefix === 'Problems/') return PROBLEMS_ORDER;
  if (pathPrefix === 'Algorithms/') return ALGORITHMS_ORDER;
  if (pathPrefix === 'Demos/') return DEMOS_ORDER;
  if (FOUNDATIONS_ORDERS[pathPrefix]) return FOUNDATIONS_ORDERS[pathPrefix];
  if (ANALYSIS_ORDERS[pathPrefix]) return ANALYSIS_ORDERS[pathPrefix];
  if (DATA_STRUCTURES_ORDERS[pathPrefix]) return DATA_STRUCTURES_ORDERS[pathPrefix];
  if (APPENDIX_ORDERS[pathPrefix]) return APPENDIX_ORDERS[pathPrefix];
  return [];
};

const sortEntries = (items, orderList = [], sortMode = 'default') => {
  const sortedItems = [...items];

  if (sortMode === 'flat') {
    return sortedItems.sort((a, b) =>
      getDisplayLabel(a).localeCompare(getDisplayLabel(b), undefined, {
        numeric: true,
        sensitivity: 'base'
      })
    );
  }

  return sortedItems.sort((a, b) => {
    const indexA = orderList.indexOf(getKey(a));
    const indexB = orderList.indexOf(getKey(b));
    return (indexA === -1 ? 9999 : indexA) - (indexB === -1 ? 9999 : indexB);
  });
};

const prefixNumber = (number, label) =>
  number ? `${number} ${label}` : label;

const buildMenuNumbering = (chapters) => {
  const topLevel = new Map();
  const entries = new Map();
  const pages = new Map();

  const numberEntries = (items, pathPrefix, parentNumber) => {
    const orderedItems = sortEntries(
      items,
      getOrderListForPath(pathPrefix),
      'default'
    );

    orderedItems.forEach((item, index) => {
      const number = `${parentNumber}.${index + 1}`;
      const key = getKey(item);

      if (typeof item === 'string') {
        pages.set(`${pathPrefix}${key}`, number);
        return;
      }

      const childItems = item[key];
      const childPath = `${pathPrefix}${key}/`;
      entries.set(childPath, number);
      numberEntries(childItems, childPath, number);
    });
  };

  TOP_LEVEL_ORDER.forEach((sectionName) => {
    const contents = chapters[sectionName];
    const topNumber = TOP_LEVEL_NUMBER_LABELS[sectionName];
    if (!contents || !topNumber) return;

    topLevel.set(sectionName, topNumber);

    if (!NUMBERED_CHILD_SECTIONS.has(sectionName)) return;

    const sectionPath = `${sectionName}/`;
    numberEntries(contents, sectionPath, topNumber);
  });

  return { topLevel, entries, pages };
};

const getBookNodeNumber = (node, parentNumber, index) => {
  if (Object.prototype.hasOwnProperty.call(node, 'number')) {
    return String(node.number);
  }
  return parentNumber ? `${parentNumber}.${index + 1}` : '';
};

const buildBookNumbering = (book) => {
  const pages = new Map();

  const visit = (items, parentNumber) => {
    (items || []).forEach((item, index) => {
      const number = getBookNodeNumber(item, parentNumber, index);
      if (item.path) pages.set(item.path, number);
      if (item.items) visit(item.items, number);
    });
  };

  (book.sections || []).forEach((section, index) => {
    const number = getBookNodeNumber(section, '', index);
    visit(section.items, number);
  });

  return { topLevel: new Map(), entries: new Map(), pages };
};

const applyPageNumber = (doc, path) => {
  const number = menuNumbering.pages.get(path);
  if (!number) return;

  const title = doc.querySelector('h1') || doc.querySelector('h2');
  if (
    title &&
    title.dataset.menuNumber !== number &&
    !title.textContent.trim().startsWith(`${number} `)
  ) {
    title.insertAdjacentText('afterbegin', `${number} `);
    title.dataset.menuNumber = number;
  }

  if (doc.title && !doc.title.startsWith(`${number} `)) {
    doc.title = `${number} ${doc.title}`;
  }
};

const createMenuLink = (fullPath, label, level = 1, title = '') => {
  const a = document.createElement('a');
  a.textContent = label;
  a.href = buildNavigationURL(fullPath);
  a.style.fontSize = `${1 - (level - 1) * 0.1}em`;
  if (title) a.title = title;

  a.addEventListener('click', (e) => {
    if (aKeyDown) {
      e.preventDefault();
      e.stopImmediatePropagation();

      const menu = document.querySelector('#menu');
      menu.classList.add('noselect');
      const sel = window.getSelection?.();
      if (sel && !sel.isCollapsed) sel.removeAllRanges();
      requestAnimationFrame(() => {
        menu.classList.remove('noselect');
      });

      const url = new URL(window.location.href);
      url.search = buildNavigationURL(fullPath).slice(1);
      const html = `<li><a href="${url.href}">DAIA: ${a.textContent}</a></li>`;

      navigator.clipboard.writeText(html)
        .then(() => {
          a.title = 'Copied!';
          setTimeout(() => (a.title = title), 1000);
        })
        .catch(() => alert('Failed to copy link'));
    }
  });

  return a;
};

const flattenSectionItems = (items, pathPrefix, contextParts = []) => {
  const flattened = [];

  items.forEach((item) => {
    if (typeof item === 'string') {
      const raw = item.replace(/\.html$/, '');
      const fullPath = (pathPrefix.includes('More/DRAFTS') && raw.includes('/'))
        ? raw
        : pathPrefix + raw;

      flattened.push({
        fullPath,
        label: raw.split('/').pop().replace(/Demo$/, '').trim(),
        context: contextParts.join(' / ')
      });
      return;
    }

    Object.entries(item).forEach(([dir, sub]) => {
      flattened.push(
        ...flattenSectionItems(sub, `${pathPrefix}${dir}/`, [...contextParts, dir])
      );
    });
  });

  return flattened;
};

// -----------------------------------------------------------------
function highlightActiveLink(path) {
  document.querySelectorAll('#menu a').forEach(a => {
    const aPath = new URLSearchParams(a.href.split('?')[1]).get('path');
    const isActive = aPath === path;
    a.classList.toggle('active', isActive);
    if (isActive) a.setAttribute('aria-current', 'page');
    else a.removeAttribute('aria-current');
  });
  updateBookContextNotice(path);
}
// -----------------------------------------------------------------


// ─── Resize Helpers ──────────────────────────────────────────────────────────
// Get the iframe's Document object (handles both contentDocument & contentWindow)
const getIframeDocument = (frame) =>
  frame.contentDocument || frame.contentWindow.document;

// Compute the “auto + 5% up to 250px” height for any iframe‐doc combo
const computeIframeHeight = (doc) => {
  const body = doc.body;
  const html = doc.documentElement;
  const contentHeight = Math.max(
    body.scrollHeight, body.offsetHeight,
    html.clientHeight, html.scrollHeight, html.offsetHeight
  );
  const marginBottom = parseFloat(
    doc.defaultView.getComputedStyle(body).marginBottom
  ) || 0;
  const baseHeight = contentHeight + marginBottom;
  const fivePct = Math.ceil(baseHeight * 0.05);
  const buffer = Math.min(250, fivePct);
  return baseHeight + buffer;
};

// ─── Navigation / Scroll‐State / Content Loading ─────────────────────────────
let pendingScrollRestore = null;

const navigateTo = (rawPath) => {
  const currentPath = getCurrentPath();
  if (rawPath === currentPath) return;

  // 1) Save old scroll position
  const oldScrollY = window.pageYOffset;
  // Save scroll state without altering the current entry's URL
  history.replaceState(
    { path: currentPath, book: activeBookId, scrollY: oldScrollY },
    ''
  );

  // 2) Push new entry
  const safe = normalizePath(rawPath);
  history.pushState(
    { path: rawPath, book: activeBookId, scrollY: 0 },
    '',
    buildNavigationURL(rawPath)
  );

  // 3) Load
  window.scrollTo(0, 0);
  loadContent(`${safe}.html`);
  
  highlightActiveLink(rawPath);

};

const loadFromURLParams = () => {
  const rawPath = new URLSearchParams(window.location.search).get('path') || 'Start Here/About';
  highlightActiveLink(rawPath);
  loadContent(`${normalizePath(rawPath)}.html`);
};

// ─── iframe Hooking & Resizing ───────────────────────────────────────────────
function hookIframeContent(iframe) {
  const innerDoc = getIframeDocument(iframe);
  if (!innerDoc) return;

  applyPageNumber(innerDoc, getCurrentPath());

  // Inject glossary-tooltips.js (fresh each time)
  const head = innerDoc.head || innerDoc.getElementsByTagName('head')[0];
  const old = innerDoc.getElementById('glossary-tooltips-script');
  if (old) old.remove();
  const scriptTips = innerDoc.createElement('script');
  scriptTips.id = 'glossary-tooltips-script';
  scriptTips.type = 'module';
  const appRoot = new URL('.', window.location.href);
  scriptTips.src = new URL(`scripts/glossary-tooltips.js?cb=${Date.now()}`, appRoot).href;
  head.appendChild(scriptTips);

  // Insert a visible DRAFT banner in the HOST page (outside the iframe)
  // so it does NOT follow into fullscreen when the iframe enters fullscreen.
  (function manageDraftBannerHost(doc, hostDoc, hostIframe) {
    if (!doc || !hostDoc || !hostIframe) return;

    const createHostBanner = () => {
      const b = hostDoc.createElement('div');
      b.id = 'draft-banner-host';
      b.textContent = 'DRAFT';
      b.style.cssText = [
        'color:#b00000',
        'font-size:28px',
        'font-weight:700',
        'text-align:center',
        'padding:4px 0',
        'background:rgba(255,0,0,0.05)',
        'border:1px solid rgba(176,0,0,0.2)',
        'border-radius:6px',
        'margin:6px 0',
        'position:relative',
        'z-index:2',
        'box-sizing:border-box'
      ].join(';');
      return b;
    };

    const getDocName = () => {
      try {
        const loc = doc.defaultView?.location || doc.location;
        if (!loc) return '';
        const parts = loc.pathname ? loc.pathname.split('/') : [];
        return decodeURIComponent(parts.pop() || '').toUpperCase();
      } catch (e) {
        return '';
      }
    };

    const ensureContentWrapper = () => {
      // Wrap the iframe (and error message) in a right-side column so a banner
      // can sit above the content only, not above the left menu.
      const container = hostDoc.getElementById('container');
      if (!container) return null;
      let wrapper = hostDoc.getElementById('content-wrapper');
      if (!wrapper) {
        wrapper = hostDoc.createElement('div');
        wrapper.id = 'content-wrapper';
        wrapper.style.cssText = [
          'display:flex',
          'flex-direction:column',
          'flex:1 1 auto',
          'min-width:0'
        ].join(';');
        // Insert wrapper before the iframe, then move iframe (and any error box) inside it
        if (hostIframe && hostIframe.parentNode === container) {
          container.insertBefore(wrapper, hostIframe);
          wrapper.appendChild(hostIframe);
        }
        const err = hostDoc.getElementById('errorMessage');
        if (err && err.parentNode === container) {
          wrapper.appendChild(err);
        }
      }
      return wrapper;
    };

    const updateBanner = () => {
      try {
        const name = getDocName();
        const existingHost = hostDoc.getElementById('draft-banner-host');
        if (name.includes('DRAFT')) {
          if (!existingHost) {
            const banner = createHostBanner();
            const wrapper = ensureContentWrapper();
            if (wrapper) wrapper.insertBefore(banner, wrapper.firstChild);
            else hostIframe.parentNode.insertBefore(banner, hostIframe);
          }
        } else {
          if (existingHost) existingHost.remove();
        }
      } catch (e) {
        // ignore
      }
    };

    // Initial and reactive updates
    updateBanner();
    let lastName = getDocName();
    const poll = setInterval(() => {
      const current = getDocName();
      if (current !== lastName) {
        lastName = current;
        updateBanner();
      }
    }, 500);

    // Cleanup on iframe unload
    const win = doc.defaultView;
    if (win) {
      win.addEventListener('unload', () => {
        clearInterval(poll);
        // do not remove host banner here; it will be re-evaluated on next load
      }, { once: true });
    }
  })(innerDoc, document, iframe);

  // Keep the selected book in links copied or opened from content pages.
  innerDoc.querySelectorAll('a[href]').forEach((anchor) => {
    const rawHref = (anchor.getAttribute('href') || '').trim();
    if (!rawHref || rawHref.startsWith('#')) return;

    try {
      const resolved = new URL(rawHref, innerDoc.location.href);
      if (!resolved.searchParams.has('path')) return;
      resolved.searchParams.set('book', activeBookId);

      if (rawHref.startsWith('?')) {
        anchor.setAttribute('href', `${resolved.search}${resolved.hash}`);
      } else {
        anchor.setAttribute('href', resolved.href);
      }
    } catch {}
  });

  // Intercept same-site “path” links inside the iframe.
  innerDoc.addEventListener('click', (event) => {
    const anchor = event.target.closest('a[href]');
    if (!anchor) return;

    const rawHref = (anchor.getAttribute('href') || '').trim();
    let resolved;
    try {
      resolved = new URL(rawHref, innerDoc.location.href);
    } catch {
      return;
    }

    const raw = resolved.searchParams.get('path');
    if (!raw) return;
    const isSameSite = resolved.origin === window.location.origin;
    if (!rawHref.startsWith('?') && !isSameSite) return;

    event.preventDefault();
    const parentPath = getCurrentPath.call(window.parent);
    if (raw === parentPath) return;
    window.parent.postMessage({ type: 'navigate', path: raw }, '*');
  });

  /*
  // External links break out to top window
  innerDoc.querySelectorAll('a[href]:not([href^="?path="])')
    .forEach(a => a.target = '_top');
  */
 // Make non-?path= links open in top window, except hash-only anchors
 innerDoc.querySelectorAll('a[href]:not([href^="?path="])').forEach(a => {
   const href = (a.getAttribute('href') || '').trim();
   // Allow in-page hash navigation to work inside the iframe
   if (href.startsWith('#')) return;
   if (!a.hasAttribute('target')) a.target = '_top';
 });

  // Auto‐resize any embeddedDemo iframes
  innerDoc.querySelectorAll('iframe.embeddedDemo').forEach((frame) => {
    if (!frame.hasAttribute('title')) {
      let frameName = 'Interactive demonstration';
      try {
        const fileName = decodeURIComponent(new URL(frame.src, innerDoc.location.href).pathname.split('/').pop() || '');
        const cleanName = fileName.replace(/\.html$/i, '').replace(/\s+/g, ' ').trim();
        if (cleanName) frameName = cleanName;
      } catch {}
      frame.title = frameName;
    }
    if (!frame.hasAttribute('aria-label')) frame.setAttribute('aria-label', frame.title);
    const base = frame.src.split('?')[0];
    try {
      frame.contentWindow.location.replace(`${base}?cb=${Date.now()}`);
    } catch {
      frame.src = `${base}?cb=${Date.now()}`;
    }
    frame.scrolling = 'no';

    frame.addEventListener('load', () => {
      const d = getIframeDocument(frame);
      if (!d) return;
      if (d.title) {
        frame.title = d.title;
        if (!frame.hasAttribute('aria-label')) frame.setAttribute('aria-label', d.title);
      }
      const html = d.documentElement;
      const resize = () => {
        frame.style.height = 'auto';
        frame.style.height = `${computeIframeHeight(d)}px`;
      };
      resize();
      new MutationObserver(resize).observe(html, {
        subtree: true,
        childList: true,
        attributes: true
      });
      setTimeout(resize, 500);
    });
  });

  // Collapsible Sections (opt-in via `section-title` or `data-section-title`)
  (function setupCollapsibleSections(doc) {
    try {
      const selector = 'section[section-title], section[data-section-title]';
      const sections = Array.from(doc.querySelectorAll(selector))
        // avoid reprocessing if we reload/rehook
        .filter(sec => !sec.classList.contains('collapsible-ready'));
      if (sections.length === 0) return;

      // Minimal styles injected once per iframe document
      if (!doc.getElementById('collapsible-section-styles')) {
        const style = doc.createElement('style');
        style.id = 'collapsible-section-styles';
        style.textContent = `
          section.collapsible-ready { margin-top: .5rem; border-top: 1px solid #e0e0e0; padding-top: .25rem; }
          section.collapsible-ready:first-of-type { border-top: 0; margin-top: .25rem; padding-top: 0; }
          /* Depth-based border thickness: thicker top-level, thinner nested */
          section.collapsible-ready[data-depth="1"] { border-top-width: 3px; }
          section.collapsible-ready[data-depth="2"] { border-top-width: 2px; }
          section.collapsible-ready[data-depth="3"] { border-top-width: 1px; }
          section.collapsible-ready[data-depth="4"],
          section.collapsible-ready[data-depth="5"],
          section.collapsible-ready[data-depth="6"] { border-top-width: 1px; }
          /* Depth-based color: darker for higher levels */
          section.collapsible-ready[data-depth="1"] { border-top-color: #777; }
          section.collapsible-ready[data-depth="2"] { border-top-color: #999; }
          section.collapsible-ready[data-depth="3"] { border-top-color: #bfbfbf; }
          section.collapsible-ready[data-depth="4"],
          section.collapsible-ready[data-depth="5"],
          section.collapsible-ready[data-depth="6"] { border-top-color: #e0e0e0; }
          section.collapsible-ready > h1,
          section.collapsible-ready > h2,
          section.collapsible-ready > h3,
          section.collapsible-ready > h4,
          section.collapsible-ready > h5,
          section.collapsible-ready > h6 { margin: 0; }
          .section-toggle { cursor: pointer; background: none; border: none; padding: .15rem 0; font: inherit; color: inherit; display: inline-flex; align-items: center; gap: .4rem; }
          .section-toggle:focus-visible, .section-toolbar button:focus-visible { outline: 3px solid #7c3aed; outline-offset: 2px; }
          .section-toggle .chev { display: inline-block; width: 1em; }
          .section-body { margin-top: .35rem; }
          /* toolbar for expand/collapse all */
          .section-toolbar { display:flex; gap:.5rem; align-items:center; margin:.25rem 0 .5rem 0; }
          .section-toolbar button { background:none; border:1px solid #ddd; border-radius:4px; padding:.15rem .5rem; cursor:pointer; }
          .section-toolbar button:hover { background:#f7f7f7; }
          /* Optional trailing divider: when enabled, draw a bottom rule under the last section at any level */
          body.show-trailing-dividers section.collapsible-ready:last-of-type {
            border-bottom: 1px solid #e0e0e0;
            padding-bottom: .35rem;
            margin-bottom: .5rem;
          }
        `;
        (doc.head || doc.documentElement).appendChild(style);
      }

      // Parse section targets from parent URL: supports ?section=a&section=b and comma lists
      const getOpenTargets = () => {
        let search = '';
        try {
          search = window.parent?.location?.search || window.location.search || '';
        } catch (_) {
          search = window.location.search || '';
        }
        const params = new URLSearchParams(search);
        const values = [];
        params.forEach((val, key) => {
          if (key.toLowerCase() === 'section') values.push(...String(val).split(','));
        });
        const norm = (s) => String(decodeURIComponent(s || '')).trim().toLowerCase();
        return new Set(values.map(norm).filter(Boolean));
      };
      const openTargets = getOpenTargets();

      // Enable trailing divider if requested by page or parent URL param (?trailing=1)
      try {
        let trailing = false;
        try {
          const search = window.parent?.location?.search || window.location.search || '';
          const params = new URLSearchParams(search);
          const v = (params.get('trailing') || '').toLowerCase();
          trailing = ['1','true','yes','on'].includes(v);
        } catch {}
        if (trailing || doc.body.hasAttribute('data-trailing-dividers')) {
          doc.body.classList.add('show-trailing-dividers');
        }
      } catch {}

      const slugify = (txt) => String(txt || '')
        .toLowerCase()
        .normalize('NFKD')
        .replace(/[^\w\s-]/g, '')
        .trim()
        .replace(/[\s_-]+/g, '-')
        .replace(/^-+|-+$/g, '');

      const isTopLevel = (el) => el.parentElement === doc.body;

      // Build a stable list of top-level collapsibles for default-open logic
      const topLevel = Array.from(doc.body.querySelectorAll(':scope > section[section-title], :scope > section[data-section-title]'))
        .filter(sec => !sec.classList.contains('collapsible-ready'));

      const registry = [];
      const defaultOpenSlugs = new Set(['problem-solved','design-and-strategy']);
      const initiallyCollapsed = doc.body.hasAttribute('data-sections-initially-collapsed');
      const currentPath = getCurrentPath();
      const topLevelSection = currentPath.split('/')[0];
      const pageNumber = !UNNUMBERED_SECTION_PATHS.has(currentPath) && !isAllContentView()
        ? menuNumbering.pages.get(currentPath)
        : (NUMBERED_PAGE_SECTION_HEADINGS.has(topLevelSection)
          ? menuNumbering.pages.get(currentPath)
          : null);
      const sectionNumbers = new Map();
      const childCounts = new Map();

      if (pageNumber) {
        sections.forEach((section) => {
          const parentSection = section.parentElement?.closest(selector) || null;
          const childIndex = (childCounts.get(parentSection) || 0) + 1;
          childCounts.set(parentSection, childIndex);
          const parentNumber = parentSection
            ? sectionNumbers.get(parentSection)
            : pageNumber;
          if (parentNumber) {
            sectionNumbers.set(section, `${parentNumber}.${childIndex}`);
          }
        });
      }

      sections.forEach((section, index) => {
        const titleAttr = section.getAttribute('section-title') || section.getAttribute('data-section-title') || '';
        let id = (section.getAttribute('id') || '').trim();
        const slug = slugify(titleAttr);

        // Ensure the section is addressable via a stable ID for deep links
        if (!id) {
          id = slug || `section-${index + 1}`;
          try { section.setAttribute('id', id); } catch {}
        }

        // Compute nested depth (1 = top-level child of body)
        let depth = 1;
        try {
          let parent = section.parentElement;
          while (parent && parent !== doc.body) {
            if (parent.matches && parent.matches('section[section-title], section[data-section-title]')) depth++;
            parent = parent.parentElement;
          }
        } catch {}
        section.setAttribute('data-depth', String(depth));

        // Find or create a heading to host the toggle
        let header = section.querySelector(':scope > h1, :scope > h2, :scope > h3, :scope > h4, :scope > h5, :scope > h6');
        if (!header) {
          header = doc.createElement('h2');
          header.textContent = titleAttr || id || 'Section';
          section.insertBefore(header, section.firstChild);
        }

        // Wrap the rest of the content
        const body = doc.createElement('div');
        body.className = 'section-body';
        body.id = `collapsible-section-${index + 1}-content`;
        const children = Array.from(section.childNodes);
        children.forEach((node) => {
          if (node !== header) body.appendChild(node);
        });
        section.appendChild(body);

        // Create a toggle button inside the header
        const btn = doc.createElement('button');
        btn.type = 'button';
        btn.className = 'section-toggle';
        btn.setAttribute('aria-controls', body.id);
        const label = titleAttr || header.textContent || id || 'Section';
        const sectionNumber = sectionNumbers.get(section);
        btn.innerHTML = `<span class="chev" aria-hidden="true">▸</span><span class="label"></span>`;
        btn.querySelector('.label').textContent = prefixNumber(sectionNumber, label);
        if (sectionNumber) section.dataset.sectionNumber = sectionNumber;
        header.textContent = '';
        header.appendChild(btn);

        const setExpanded = (expanded) => {
          btn.setAttribute('aria-expanded', String(!!expanded));
          body.hidden = !expanded;
          const chev = btn.querySelector('.chev');
          if (chev) chev.textContent = expanded ? '▾' : '▸';
        };

        const ensureEmbeddedDemoHeights = () => {
          body.querySelectorAll('iframe.embeddedDemo').forEach((frame) => {
            try {
              const d = getIframeDocument(frame);
              if (!d) return;
              // allow layout to settle if just unhidden
              setTimeout(() => {
                try { frame.style.height = 'auto'; } catch {}
                try { frame.style.height = `${computeIframeHeight(d)}px`; } catch {}
              }, 0);
            } catch {}
          });
        };

        btn.addEventListener('click', () => {
          const next = btn.getAttribute('aria-expanded') !== 'true';
          setExpanded(next);
          if (next) ensureEmbeddedDemoHeights();

          // When expanding a section, update the parent URL hash to this section's ID
          if (next) {
            try {
              const p = window.parent;
              if (p && p.history && p.location) {
                const newUrl = `${p.location.pathname}${p.location.search}#${encodeURIComponent(id)}`;
                p.history.replaceState(p.history.state, '', newUrl);
              }
            } catch {}
          }
        });

        // Determine initial state
        let open = false;
        if (openTargets.size > 0) {
          const idKey = id.toLowerCase();
          if (idKey && openTargets.has(idKey)) open = true;
          else if (slug && openTargets.has(slug)) open = true;
        } else if (initiallyCollapsed) {
          open = false;
        } else {
          // Default: open key intro sections if present; otherwise open first top-level only
          const idKey = id.toLowerCase();
          const isPreferred = (defaultOpenSlugs.has(slug) || (idKey && defaultOpenSlugs.has(idKey)));
          if (isPreferred) {
            open = true;
          } else if (topLevel.length > 0) {
            open = (isTopLevel(section) && section === topLevel[0]);
          } else {
            open = (index === 0); // fallback
          }
        }
        setExpanded(open);
        if (open) ensureEmbeddedDemoHeights();

        section.classList.add('collapsible-ready');
        registry.push({ section, btn, body, setExpanded });
      });

      // Insert Expand/Collapse All toolbar near the page title
      if (registry.length > 0 && !doc.getElementById('section-toolbar')) {
        const toolbar = doc.createElement('div');
        toolbar.id = 'section-toolbar';
        toolbar.className = 'section-toolbar';
        toolbar.setAttribute('role', 'group');
        toolbar.setAttribute('aria-label', 'Section display controls');
        const expandBtn = doc.createElement('button');
        expandBtn.type = 'button';
        expandBtn.textContent = 'Expand All';
        expandBtn.addEventListener('click', () => {
          registry.forEach(({ setExpanded }) => setExpanded(true));
          // resize any newly visible embedded demos
          registry.forEach(({ body }) => {
            body.querySelectorAll('iframe.embeddedDemo').forEach((frame) => {
              try {
                const d = getIframeDocument(frame);
                if (d) frame.style.height = `${computeIframeHeight(d)}px`;
              } catch {}
            });
          });
        });
        const collapseBtn = doc.createElement('button');
        collapseBtn.type = 'button';
        collapseBtn.textContent = 'Collapse All';
        collapseBtn.addEventListener('click', () => {
          registry.forEach(({ setExpanded }) => setExpanded(false));
        });
        toolbar.appendChild(expandBtn);
        toolbar.appendChild(collapseBtn);

        const title = doc.querySelector('body > h1');
        const firstSection = registry[0]?.section;
        if (title && title.parentNode) {
          title.parentNode.insertBefore(toolbar, title.nextSibling);
        } else if (firstSection && firstSection.parentNode) {
          firstSection.parentNode.insertBefore(toolbar, firstSection);
        } else {
          doc.body.insertBefore(toolbar, doc.body.firstChild);
        }
      }
    } catch (e) {
      // Swallow errors to avoid affecting live content loading
      console.error('Collapsible setup error:', e);
    }
  })(innerDoc);

  // Support deep-linking: scroll to element matching parent hash
  (function handleHashScroll(doc) {
    const scrollToId = (id) => {
      if (!id) return;
      try {
        const target = doc.getElementById(id) || doc.querySelector(`[name="${CSS.escape(id)}"]`);
        if (!target) return;
        // Expand any collapsed ancestor sections to ensure visibility
        let node = target;
        while (node && node !== doc.body) {
          if (node.classList && node.classList.contains('collapsible-ready')) {
            const btn = node.querySelector(':scope > h1 .section-toggle, :scope > h2 .section-toggle, :scope > h3 .section-toggle, :scope > h4 .section-toggle, :scope > h5 .section-toggle, :scope > h6 .section-toggle');
            const body = node.querySelector(':scope > .section-body');
            if (btn) btn.setAttribute('aria-expanded', 'true');
            if (body) body.hidden = false;
          }
          node = node.parentElement;
        }
        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
      } catch {}
    };

    // Initial pass using parent's hash (preferred), else iframe's own hash
    let hash = '';
    try { hash = window.parent?.location?.hash || window.location.hash || ''; }
    catch { hash = window.location.hash || ''; }
    if (hash && hash.startsWith('#')) scrollToId(hash.slice(1));

    // Listen for parent hash changes while this doc is active
    try {
      const onHash = () => {
        const h = window.parent.location.hash;
        if (h && h.startsWith('#')) scrollToId(h.slice(1));
      };
      window.parent.addEventListener('hashchange', onHash);
      // Clean up when iframe unloads
      (doc.defaultView || window).addEventListener('unload', () => {
        try { window.parent.removeEventListener('hashchange', onHash); } catch {}
      }, { once: true });
    } catch {}
  })(innerDoc);
}

// ─── Build Sidebar Menu ───────────────────────────────────────────────────────
const buildMenu = (chapters) => {
  const activeBook = getBook();
  const isAllContent = isAllContentView();
  const allContentSortMode = activeBookId === 'all-alphabetic'
    ? 'flat'
    : 'default';
  menuNumbering = isAllContent
    ? { topLevel: new Map(), entries: new Map(), pages: new Map() }
    : buildBookNumbering(activeBook);

  const menuContainer = document.querySelector('#menu-content');
  menuContainer.innerHTML = `
    <div class="book-selector">
      <label for="bookSelect">View</label>
      <select id="bookSelect" aria-label="Choose a book or show all content"></select>
    </div>
    <div class="menu-controls">
      <div class="menu-icon-controls" role="group" aria-label="Menu display controls">
        <button id="expandAll" class="menu-icon-button" type="button" title="Expand all sections" aria-label="Expand all sections">▾▾</button>
        <button id="collapseAll" class="menu-icon-button" type="button" title="Collapse all sections" aria-label="Collapse all sections">▸▸</button>
      </div>
    </div>
    <p id="bookContextNote" class="book-context-note" hidden></p>
    <ul> </ul>
  `;
  const menuRoot = menuContainer.querySelector('ul');
  const bookSelect = menuContainer.querySelector('#bookSelect');

  booksData.books.forEach((book) => {
    const option = document.createElement('option');
    option.value = book.id;
    option.textContent = book.label;
    option.selected = book.id === activeBookId;
    bookSelect.appendChild(option);
  });

  bookSelect.addEventListener('change', () => selectBook(bookSelect.value));

  const setMenuItemExpanded = (li, expanded) => {
    li.classList.toggle('open', expanded);
    const toggle = li.querySelector(':scope > .menu-section-toggle');
    if (toggle) toggle.setAttribute('aria-expanded', String(expanded));
  };

  const addExpandableLabel = (li, label, level) => {
    const button = document.createElement('button');
    button.type = 'button';
    button.className = 'menu-section-toggle';
    button.textContent = label;
    button.setAttribute('aria-label', label);
    button.style.fontSize = `${1.1 - Math.max(0, level - 1) * 0.1}em`;
    button.setAttribute('aria-expanded', 'false');
    button.addEventListener('click', (e) => {
      if (e.altKey && e.shiftKey) return;
      setMenuItemExpanded(li, !li.classList.contains('open'));
    });
    li.appendChild(button);
    return button;
  };

  if (!isAllContent) {
    const buildBookItems = (items, container, parentNumber, level = 2) => {
      (items || []).forEach((item, index) => {
        const li = document.createElement('li');
        const number = getBookNodeNumber(item, parentNumber, index);
        const label = prefixNumber(number, item.label);

        if (item.path) {
          li.appendChild(createMenuLink(item.path, label, level));
        } else if (item.items) {
          addExpandableLabel(li, label, level);
          const ul = document.createElement('ul');
          buildBookItems(item.items, ul, number, level + 1);
          li.appendChild(ul);
        }
        container.appendChild(li);
      });
    };

    (activeBook.sections || []).forEach((section, index) => {
      const li = document.createElement('li');
      const number = getBookNodeNumber(section, '', index);
      addExpandableLabel(li, prefixNumber(number, section.label), 1);
      const ul = document.createElement('ul');
      buildBookItems(section.items, ul, number);
      li.appendChild(ul);
      menuRoot.appendChild(li);
    });
  } else {

  const buildList = (items, container, pathPrefix, level = 1) => {
   const orderList = getOrderListForPath(pathPrefix);
    sortEntries(items, orderList, allContentSortMode).forEach((item) => {
        const li = document.createElement('li');
        if (typeof item === 'string') {
          const raw = item.replace(/\.html$/, '');
          const fullPath = (pathPrefix.includes('More/DRAFTS') && raw.includes('/'))
            ? raw // already a full path
            : pathPrefix + raw;
          const label = raw.split('/').pop().replace(/Demo$/, '').trim();
          const a = createMenuLink(fullPath, prefixNumber(menuNumbering.pages.get(fullPath), label), level);
          li.appendChild(a);
        } else {
          Object.entries(item).forEach(([dir, sub]) => {
            const dirPath = `${pathPrefix}${dir}/`;
            const button = addExpandableLabel(
              li,
              prefixNumber(menuNumbering.entries.get(dirPath), dir),
              level
            );
            button.style.fontSize = `${1 - (level - 1) * 0.1}em`;

            const ul = document.createElement('ul');
            buildList(sub, ul, dirPath, level + 1);
            li.appendChild(ul);
          });
        }
        container.appendChild(li);
      });
  };

  const topLevelSections = TOP_LEVEL_ORDER;

  topLevelSections.forEach((sectionName) => {
    const contents = chapters[sectionName];
    if (!contents) return;

    const li = document.createElement('li');
    addExpandableLabel(
      li,
      prefixNumber(menuNumbering.topLevel.get(sectionName), sectionName),
      1
    );

    const ul = document.createElement('ul');

    if (allContentSortMode === 'flat') {
      const flatItems = flattenSectionItems(contents, `${sectionName}/`);
      const labelCounts = flatItems.reduce((counts, item) => {
        counts[item.label] = (counts[item.label] || 0) + 1;
        return counts;
      }, {});

      flatItems
        .sort((a, b) => a.label.localeCompare(b.label, undefined, {
          numeric: true,
          sensitivity: 'base'
        }))
        .forEach((item) => {
          const itemLi = document.createElement('li');
          const needsContext = labelCounts[item.label] > 1 && item.context;
          const visibleLabel = needsContext
            ? `${item.label} (${item.context})`
            : item.label;
          itemLi.appendChild(
            createMenuLink(item.fullPath, prefixNumber(menuNumbering.pages.get(item.fullPath), visibleLabel), 2, item.context)
          );
          ul.appendChild(itemLi);
        });
    } else {
      buildList(contents, ul, `${sectionName}/`);
    }

    li.appendChild(ul);
    menuRoot.appendChild(li);
  });
  }

  menuContainer.querySelector('#expandAll').onclick = () => {
    document.querySelectorAll('#menu li').forEach((li) => {
      if (li.querySelector(':scope > ul')) setMenuItemExpanded(li, true);
    });
  };

  menuContainer.querySelector('#collapseAll').onclick = () => {
    document.querySelectorAll('#menu li').forEach((li) => {
      if (li.querySelector(':scope > ul')) setMenuItemExpanded(li, false);
    });
  };

  updateBookContextNotice(getCurrentPath());

};

const updateBookContextNotice = (path) => {
  const note = document.querySelector('#bookContextNote');
  if (!note) return;

  const outsideBook = !isAllContentView() && !menuNumbering.pages.has(path);
  note.hidden = !outsideBook;
  note.textContent = outsideBook
    ? 'Supplementary page — not in this book’s table of contents.'
    : '';
};

const selectBook = (bookId) => {
  if (!isKnownBook(bookId) || bookId === activeBookId) return;

  const currentPath = getCurrentPath();
  activeBookId = bookId;
  history.pushState(
    { path: currentPath, book: activeBookId, scrollY: window.pageYOffset || 0 },
    '',
    buildNavigationURL(currentPath)
  );
  buildMenu(chaptersData);
  highlightActiveLink(currentPath);
  loadContent(`${normalizePath(currentPath)}.html`);
};

// ─── Load Content via Fetch + Replace‐into‐iframe ────────────────────────────
let lastTrackedPath = null;
let lastTrackedPageLocation = null;

const updateCanonicalLink = (path) => {
  const canonicalURL = new URL('.', window.location.href);
  canonicalURL.searchParams.set('path', path);

  let canonicalLink = document.querySelector('link[rel="canonical"]');
  if (!canonicalLink) {
    canonicalLink = document.createElement('link');
    canonicalLink.rel = 'canonical';
    document.head.appendChild(canonicalLink);
  }
  canonicalLink.href = canonicalURL.href;
};

const trackPageView = (pageTitle) => {
  if (typeof window.gtag !== 'function') return;

  const path = getCurrentPath();
  if (path === lastTrackedPath) return;

  const pageLocation = window.location.href;
  const eventParameters = {
    page_location: pageLocation,
    page_title: pageTitle || path.split('/').pop() || 'DAIADS'
  };

  const pageReferrer = lastTrackedPageLocation || document.referrer;
  if (pageReferrer) eventParameters.page_referrer = pageReferrer;

  window.gtag('event', 'page_view', eventParameters);
  lastTrackedPath = path;
  lastTrackedPageLocation = pageLocation;
};

async function loadContent(relativePath) {
  const iframe = document.getElementById('content');
  const err = document.getElementById('errorMessage');
  const url = `Content/${relativePath}`;
  updateCanonicalLink(getCurrentPath());

  try {
    const res = await fetch(url, { cache: 'no-cache' });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    await res.text(); // just to confirm it exists

    err.style.display = 'none';
    iframe.style.display = 'block';

    // A genuinely empty document may occasionally be the result of a transient
    // iframe navigation failure. Retry that case once, but do not let a page whose
    // source is intentionally empty enter an endless load/retry cycle.
    let emptyRetryAttempted = false;

    // Load into iframe without adding to its session history (use replace)
    const target = `${url}?_=${Date.now()}`;
    try {
      // Prefer replacing the iframe's location to avoid stacking iframe history entries
      if (iframe.contentWindow && iframe.contentWindow.location) {
        iframe.contentWindow.location.replace(target);
      } else {
        iframe.src = target;
      }
    } catch {
      iframe.src = target;
    }

    iframe.onload = () => {
      let firstResize = true;
      const resizeIframe = () => {
        const prevY = window.pageYOffset;
        iframe.style.height = 'auto';
        const d = getIframeDocument(iframe);
        if (!d) return;
        const finalHeight = computeIframeHeight(d);
        iframe.style.height = `${finalHeight}px`;

        if (firstResize && pendingScrollRestore !== null) {
          window.scrollTo(0, parseInt(pendingScrollRestore, 10));
          pendingScrollRestore = null;
          firstResize = false;
        } else {
          window.scrollTo(0, prevY);
        }
      };

      // Check before injecting page helpers. Otherwise every retry injects a fresh,
      // cache-busted glossary module and makes an empty page appear to be repeatedly
      // processing glossary terms.
      try {
        const dtest = getIframeDocument(iframe);
        const body = dtest && dtest.body;
        const hasElements = !!body && body.childElementCount > 0;
        const hasText = !!body && body.textContent.trim().length > 0;
        const isEmpty = !body || (!hasElements && !hasText);

        if (isEmpty) {
          if (!emptyRetryAttempted) {
            emptyRetryAttempted = true;
            const retry = `${url}?__retry__=${Date.now()}`;
            try {
              if (iframe.contentWindow && iframe.contentWindow.location) {
                iframe.contentWindow.location.replace(retry);
              } else {
                iframe.src = retry;
              }
            } catch {
              iframe.src = retry;
            }
            return; // wait for the one defensive retry
          }

          // The retry also produced an empty document, so the source is most likely
          // intentionally empty. Size it once and leave it alone.
          resizeIframe();
          return;
        }
      } catch {}

      hookIframeContent(iframe);
      resizeIframe();
      const d = getIframeDocument(iframe);
      const pageTitle = d && d.title;
      if (pageTitle) {
        document.title = `${pageTitle} | DAIADS`;
        iframe.title = pageTitle;
        const routeStatus = document.getElementById('route-status');
        if (routeStatus) routeStatus.textContent = `${pageTitle} loaded`;
      }
      trackPageView(pageTitle);
      if (d) {
        new MutationObserver(resizeIframe).observe(d.documentElement, {
          subtree: true,
          childList: true,
          attributes: true
        });
      }
      window.addEventListener('resize', resizeIframe);

      // After content is ready, if parent URL has a hash, scroll to it
      try {
        const h = window.location.hash;
        if (h && h.startsWith('#')) {
          const d2 = getIframeDocument(iframe);
          const id = h.slice(1);
          const el = d2 && (d2.getElementById(id) || d2.querySelector(`[name="${CSS.escape(id)}"]`));
          if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      } catch {}
    };
  } catch (e) {
    iframe.style.display = 'none';
    const rawPath = getCurrentPath();
    err.innerHTML = `
      <p>The page you are trying to load cannot be found.<br>
      It is possible it is still being created.<br>
      Please check back later.</p>
      <p><strong>Page path:</strong> ${rawPath}</p>
    `;
    err.style.display = 'block';
  }
}

// ─── Scroll Position Preservation ────────────────────────────────────────────
window.addEventListener('beforeunload', () => {
  sessionStorage.setItem('scrollPos', window.pageYOffset);
});
window.addEventListener('load', () => {
  const saved = sessionStorage.getItem('scrollPos');
  if (saved !== null) {
    pendingScrollRestore = saved;
    sessionStorage.removeItem('scrollPos');
  }
});

// ─── App Initialization ──────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  Promise.all([
    fetch('scripts/chapters.json').then((res) => res.json()),
    fetch('scripts/books.json').then((res) => res.json())
  ])
    .then(([loadedChapters, loadedBooks]) => {
      chaptersData = loadedChapters;
      booksData = loadedBooks;
      activeBookId = getRequestedBookId();

      const initialPath = new URLSearchParams(window.location.search).get('path') || 'Start Here/About';
      const requestedBook = new URLSearchParams(window.location.search).get('book');
      if (requestedBook !== activeBookId) {
        history.replaceState(
          { path: initialPath, book: activeBookId, scrollY: window.pageYOffset || 0 },
          '',
          buildNavigationURL(initialPath)
        );
      }

      buildMenu(chaptersData);

      // Load initial content from URL and ensure the initial history state is set
      loadFromURLParams();
      try {
        const currentState = history.state || {};
        if (currentState.path !== initialPath || currentState.book !== activeBookId) {
          // Preserve the current URL (including any hash) while setting state
          history.replaceState(
            { path: initialPath, book: activeBookId, scrollY: window.pageYOffset || 0 },
            '',
            window.location.href
          );
        }
      } catch {}
    })
    .catch(console.error);


  document.addEventListener('click', (event) => {
    if (event.target.closest('iframe')) return;
    const anchor = event.target.closest('a[href]');
    if (!anchor) return;
    if (aKeyDown) return;

    let resolved;
    try {
      resolved = new URL(anchor.getAttribute('href'), window.location.href);
    } catch {
      return;
    }

    const rawPath = resolved.searchParams.get('path');
    if (!rawPath || resolved.origin !== window.location.origin) return;

    event.preventDefault();
    navigateTo(rawPath);
  });

  window.addEventListener('message', (event) => {
    const msg = event.data;
    if (msg?.type === 'demo-fullscreen') {
      const frame = event.source.frameElement;
      frame?.requestFullscreen().catch(console.error);
    }
  });
});

// ─── Listen for “navigate” messages from inside iframes ───────────────────────
window.addEventListener('message', (event) => {
  const msg = event.data;
  if (msg?.type === 'navigate' && msg.path) {
    const currentPath = getCurrentPath();
    if (msg.path !== currentPath) navigateTo(msg.path);
  }
});

// ─── Handle Back/Forward buttons ─────────────────────────────────────────────
window.removeEventListener('popstate', loadFromURLParams);
window.addEventListener('popstate', (event) => {
  const state = event.state || {};
  pendingScrollRestore = typeof state.scrollY === 'number' ? state.scrollY : 0;
  // Prefer URL as source of truth for the popped entry; fallback to state
  const params = new URLSearchParams(window.location.search);
  const urlPath = params.get('path');
  const urlBook = params.get('book');
  const rawPath = urlPath || state.path || 'Start Here/About';
  const nextBook = isKnownBook(urlBook) ? urlBook : 'all-grouped';
  if (nextBook !== activeBookId) {
    activeBookId = nextBook;
    buildMenu(chaptersData);
  }
  highlightActiveLink(rawPath);
  loadContent(`${normalizePath(rawPath)}.html`);
});
