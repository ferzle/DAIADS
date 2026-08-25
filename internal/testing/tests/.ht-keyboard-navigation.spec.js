const fs = require('node:fs');
const path = require('node:path');
const { test, expect } = require('@playwright/test');

const chapters = JSON.parse(
  fs.readFileSync(path.join(__dirname, '../../../scripts/chapters.json'), 'utf8')
);

function collectRoutes(node, parts = [], routes = []) {
  if (typeof node === 'string') {
    const route = node.replace(/\.html$/i, '');
    routes.push(
      parts.join('/') === 'More/DRAFTS' && route.includes('/')
        ? route
        : [...parts, route].join('/')
    );
  } else if (Array.isArray(node)) {
    for (const child of node) collectRoutes(child, parts, routes);
  } else if (node && typeof node === 'object') {
    for (const [name, child] of Object.entries(node)) {
      collectRoutes(child, [...parts, name], routes);
    }
  }
  return routes;
}

const routes = collectRoutes(chapters);

async function waitForDAIADS(page) {
  await page.locator('#content').waitFor();
  await page.waitForFunction(() => {
    const iframe = document.querySelector('#content');
    const frameDocument = iframe?.contentDocument;
    return frameDocument?.readyState === 'complete' &&
      frameDocument?.title &&
      frameDocument?.body?.childElementCount &&
      [...frameDocument.querySelectorAll('iframe.embeddedDemo')].every(
        demo => demo.contentDocument?.readyState === 'complete' && demo.contentDocument?.body
      );
  });
}

async function auditFrameKeyboardNavigation(frame) {
  return frame.evaluate(async () => {
    const selector = [
      'a[href]', 'button', 'input', 'select', 'textarea',
      '[contenteditable="true"]', '[tabindex]:not([tabindex="-1"])',
    ].join(',');
    const visible = element => {
      const style = getComputedStyle(element);
      const rect = element.getBoundingClientRect();
      return !element.disabled &&
        element.getAttribute('aria-hidden') !== 'true' &&
        style.display !== 'none' && style.visibility !== 'hidden' &&
        rect.width > 0 && rect.height > 0;
    };
    const failures = [];
    for (const element of [...document.querySelectorAll(selector)].filter(visible)) {
      const tabindex = element.getAttribute('tabindex');
      if (tabindex !== null && Number(tabindex) > 0) {
        failures.push({ type: 'positive-tabindex', html: element.outerHTML.slice(0, 180) });
        continue;
      }
      element.focus({ preventScroll: true });
      element.scrollIntoView({ block: 'center', inline: 'nearest' });
      const rects = [...element.getClientRects()].filter(rect =>
        rect.bottom > 0 && rect.top < innerHeight && rect.right > 0 && rect.left < innerWidth
      );
      const unobscured = rects.some(rect => {
        const points = [
          [rect.left + rect.width / 2, rect.top + rect.height / 2],
          [rect.left + Math.min(4, rect.width / 2), rect.top + rect.height / 2],
          [rect.right - Math.min(4, rect.width / 2), rect.top + rect.height / 2],
          [rect.left + rect.width / 2, rect.top + Math.min(4, rect.height / 2)],
          [rect.left + rect.width / 2, rect.bottom - Math.min(4, rect.height / 2)],
        ];
        return points.some(([x, y]) =>
          document.elementsFromPoint(
            Math.min(innerWidth - 1, Math.max(0, x)),
            Math.min(innerHeight - 1, Math.max(0, y))
          ).some(candidate => candidate === element || element.contains(candidate))
        );
      });
      const rect = element.getBoundingClientRect();
      if (document.activeElement !== element || !rects.length || !unobscured) {
        failures.push({
          type: 'focus-unavailable-or-obscured',
          html: element.outerHTML.slice(0, 180),
          active: document.activeElement === element,
          rect: { top: Math.round(rect.top), bottom: Math.round(rect.bottom) },
        });
      }
    }
    return failures;
  });
}

test.describe.configure({ mode: 'parallel' });

for (const route of routes) {
  test(`keyboard navigation: ${route}`, async ({ page }) => {
    const pageErrors = [];
    page.on('pageerror', error => pageErrors.push(error.message));
    await page.goto(`?path=${encodeURIComponent(route)}`);
    await waitForDAIADS(page);

    const failures = [];
    for (const frame of page.frames().filter(candidate => candidate !== page.mainFrame())) {
      const frameFailures = await auditFrameKeyboardNavigation(frame);
      if (frameFailures.length) failures.push({ frame: frame.url(), failures: frameFailures });
    }
    expect({ pageErrors, failures }, JSON.stringify({ pageErrors, failures }, null, 2))
      .toEqual({ pageErrors: [], failures: [] });
  });
}
