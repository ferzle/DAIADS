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
      frameDocument?.title && frameDocument?.body?.childElementCount &&
      [...frameDocument.querySelectorAll('iframe.embeddedDemo')].every(
        demo => demo.contentDocument?.readyState === 'complete' && demo.contentDocument?.body
      );
  });
}

test.describe.configure({ mode: 'parallel' });

for (const route of routes) {
  test(`accessibility tree semantics: ${route}`, async ({ page }) => {
    await page.goto(`?path=${encodeURIComponent(route)}`);
    await waitForDAIADS(page);

    const shellSnapshot = await page.locator('body').ariaSnapshot();
    expect(shellSnapshot).toContain('navigation "Table of contents"');

    const failures = [];
    for (const frame of page.frames().filter(candidate =>
      candidate.parentFrame() === page.mainFrame()
    )) {
      const snapshot = await frame.locator('body').ariaSnapshot();
      if (!snapshot) {
        failures.push({ frame: frame.url(), reason: 'empty accessibility tree' });
      }
      const unnamed = await frame.evaluate(() => {
        const selector = 'a[href], button, input, select, textarea, [role="button"]';
        return [...document.querySelectorAll(selector)]
          .filter(element => {
            const style = getComputedStyle(element);
            const rect = element.getBoundingClientRect();
            return style.display !== 'none' && style.visibility !== 'hidden' &&
              rect.width > 0 && rect.height > 0 &&
              element.getAttribute('aria-hidden') !== 'true';
          })
          .filter(element => {
            const labelledBy = element.getAttribute('aria-labelledby');
            const referenced = labelledBy
              ? labelledBy.split(/\s+/).map(id => document.getElementById(id)?.textContent || '').join(' ')
              : '';
            const label = element.labels?.length
              ? [...element.labels].map(item => item.textContent || '').join(' ')
              : '';
            const name = element.getAttribute('aria-label') || referenced || label ||
              element.getAttribute('title') || element.textContent || '';
            return !name.trim();
          })
          .map(element => element.outerHTML.slice(0, 180));
      });
      if (unnamed.length) failures.push({ frame: frame.url(), unnamed });
    }
    expect(failures, JSON.stringify(failures, null, 2)).toEqual([]);
  });
}
