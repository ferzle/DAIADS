// File: scripts/glossary-tooltips.js

// ————————————————————————————————————————————————
// 1) Load glossary data from JSON
// ————————————————————————————————————————————————
let GLOSSARY = [];

function initGlossaryTooltips() {
  const glossaryUrl = new URL("glossary-data.json?cb=" + Date.now(), import.meta.url);
  fetch(glossaryUrl, { cache: "no-store" })
    .then(res => {
      if (!res.ok) throw new Error("Failed to load glossary-data.json");
      return res.json();
    })
    .then(data => {
      GLOSSARY = data;
      scheduleBuildAndWrap();
    })
    .catch(err => console.error("Error loading glossary data:", err));
}

// Kick off after DOM is ready
function scheduleBuildAndWrap() {
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", () => buildAndWrap());
  } else {
    buildAndWrap();
  }
}

// ————————————————————————————————————————————————
// 2) Tooltip positioning by mouse, classified by wrapper center
// ————————————————————————————————————————————————
function positionTooltipAt(x, y, tip) {
  tip.style.position = "fixed";
  tip.style.display  = "block";

  // 1) Measure your container/frame (for horizontal clamping)
  const frame = document.querySelector('#content') || document.querySelector('main') || document.body;
  const frameRect = frame.getBoundingClientRect();

  // 2) Reset positioning so we can measure size
  tip.style.left = tip.style.top = "0px";
  const tipRect = tip.getBoundingClientRect();
  const tipWidth  = tipRect.width;
  const tipHeight = tipRect.height;

  // 3) Horizontal placement (unchanged)
  const relX  = x - frameRect.left, third = frameRect.width/3;
  let left;
  tip.classList.remove("to-left","to-center","to-right");
  if      (relX < third)                { tip.classList.add("to-right"); left = x + 8; }
  else if (relX > 2*third)              { tip.classList.add("to-left");  left = x - tipWidth - 8; }
  else                                  { 
    tip.classList.add("to-center");
    left = Math.min(
      Math.max(x - tipWidth/2, frameRect.left + 8),
      frameRect.right - tipWidth - 8
    );
  }
  tip.style.left = `${left}px`;

  // 4) Compute the mouse’s GLOBAL Y (iframe’s top + local y)
  const margin   = 8;
  const frameTop = window.frameElement
                   ? window.frameElement.getBoundingClientRect().top
                   : 0;
  const globalY  = frameTop + y;

  // 5) Ask the **parent** window how tall it is
  const parentHeight = window.parent.innerHeight;

  // 6) Flip if there isn’t room below in the **parent** viewport
  const showAbove = (globalY + tipHeight + margin) > parentHeight;

  tip.style.top = showAbove
    ? `${y - tipHeight - margin}px`
    : `${y + margin}px`;
}

// ————————————————————————————————————————————————
// 3) Build & wrap glossary terms in text nodes
// ————————————————————————————————————————————————
function buildAndWrap() {
  if (document.body.classList.contains("no-tooltips")) return;

  const rootEl = document.querySelector("#content")
                || document.querySelector("main")
                || document.body;

  // Prepare regex patterns (longest first)
  const patterns = GLOSSARY.map(({ variants, definition }) => {
    const canonical = variants[0];

    // build a pattern for each variant, allowing spaces or hyphens between parts
    const altPatterns = variants.map(v => {
      const parts = v.split(/[\s-]+/);
      const escaped = parts
        .map(s => s.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"))
        .join("[\\s-]+");
      return escaped;
    });
    const alts = altPatterns.join("|");

    // match full words, not embedded in letters, digits, underscores or extra hyphens
    const regex = new RegExp(`(?<![\\w-])(?:${alts})(?![\\w-])`, "gi");

    return { canonical, definition, regex };
  })
  .sort((a, b) => b.canonical.length - a.canonical.length);

  const SKIP = new Set(["STYLE",
      "SCRIPT",
      "A",
      "H1",
      "H2",
      "H3",
      "H4",
	  "H5",
      "TH",
      "CODE",
      "PRE",
      "B",
      "BUTTON",
      "INPUT", 
      "OPTION",
	  "TITLE",
	  "SVG",
	  "FIGURE",
      "STRONG"]);
      function acceptNode(node) {
  let el = node.parentElement;
  while (el) {
    const tag = el.tagName.toUpperCase();

    if (
      SKIP.has(tag) ||
      el.namespaceURI === "http://www.w3.org/2000/svg" ||
      el.classList.contains("tooltip-content")
    ) {
      return NodeFilter.FILTER_REJECT;
    }

    el = el.parentElement;
  }
  return NodeFilter.FILTER_ACCEPT;
}
/*
* replaced with the previous that is more SVG-aware and skips more (supposedly)
* 5/22/26
  function acceptNode(node) {
    let el = node.parentElement;
    while (el) {
      if (SKIP.has(el.tagName.toUpperCase()) || el.classList.contains("tooltip-content"))
        return NodeFilter.FILTER_REJECT;
      el = el.parentElement;
    }
    return NodeFilter.FILTER_ACCEPT;
  }
  */

  // Collect text nodes
  const walker = document.createTreeWalker(rootEl, NodeFilter.SHOW_TEXT, { acceptNode }, false);
  const textNodes = [];
  let tooltipIdCounter = 0;
  let node;
  while ((node = walker.nextNode())) textNodes.push(node);

  // Wrap matches in each text node
  textNodes.forEach(textNode => {
    const text = textNode.textContent;
    let matches = [];

    patterns.forEach(({ canonical, definition, regex }) => {
      regex.lastIndex = 0;
      let m;
      while ((m = regex.exec(text))) {
        matches.push({
          start: m.index,
          end: regex.lastIndex,
          canonical,
          definition,
          matchText: m[0]
        });
      }
    });
    if (!matches.length) return;

    // Filter overlapping matches
    matches.sort((a,b) => a.start - b.start || b.end - a.end);
    const keep = [];
    let lastEnd = 0;
    matches.forEach(m => {
      if (m.start >= lastEnd) { keep.push(m); lastEnd = m.end; }
    });

    // Rebuild node content
    const frag = document.createDocumentFragment();
    let idx = 0;
    keep.forEach(({ start, end, canonical, definition, matchText }) => {
      if (idx < start) frag.appendChild(document.createTextNode(text.slice(idx, start)));
      const span = document.createElement("span");
      span.className = "glossary-term";
      span.setAttribute("data-term", canonical);
      span.tabIndex = 0;
      span.textContent = matchText;

      const tip = document.createElement("span");
      tip.className = "tooltip-content";
      tip.id = `glossary-tooltip-${++tooltipIdCounter}`;
      tip.setAttribute("role", "tooltip");
      span.setAttribute("aria-describedby", tip.id);
      /*tip.textContent = definition;*/
      tip.innerHTML = definition;
      tip.style.display = "none";
      tip.style.fontStyle = "normal";
      span.appendChild(tip);

      let hoverTimeout;
      const openTooltip = (x, y) => {
        span.classList.add('highlighted', 'tooltip-open');
        positionTooltipAt(x, y, tip);
      };
      const closeTooltip = () => {
        span.classList.remove('highlighted', 'tooltip-open');
        tip.style.display = 'none';
        clearTimeout(hoverTimeout);
        tip.classList.remove("to-right", "to-left", "to-center");
      };

      span.addEventListener("mouseenter", evt => {
        span.classList.add('highlighted');
        hoverTimeout = setTimeout(() => {
          openTooltip(evt.clientX, evt.clientY);
        }, 1000);
      });
      span.addEventListener("mouseleave", closeTooltip);
      span.addEventListener("focus", () => {
        const rect = span.getBoundingClientRect();
        openTooltip(rect.left + rect.width / 2, rect.bottom);
      });
      span.addEventListener("focusout", event => {
        if (!span.contains(event.relatedTarget)) closeTooltip();
      });
      span.addEventListener("keydown", event => {
        if (event.key !== 'Escape') return;
        event.preventDefault();
        closeTooltip();
      });

      frag.appendChild(span);
      idx = end;
    });
    if (idx < text.length) frag.appendChild(document.createTextNode(text.slice(idx)));

    textNode.replaceWith(frag);
  });
}

// Initialize tooltips
initGlossaryTooltips();
