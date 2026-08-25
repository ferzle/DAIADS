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
    const documentInFrame = iframe?.contentDocument;
    return documentInFrame?.readyState === 'complete' &&
      documentInFrame?.title && documentInFrame?.body?.childElementCount;
  });
}

test.describe.configure({ mode: 'parallel' });

for (const route of routes) {
  test(`runtime internal navigation: ${route}`, async ({ page }) => {
    const pageErrors = [];
    page.on('pageerror', error => pageErrors.push(error.message));

    await page.goto(`?path=${encodeURIComponent(route)}`);
    await waitForDAIADS(page);

    const contentFrame = page.frames().find(frame =>
      frame.parentFrame() === page.mainFrame() && frame !== page.mainFrame()
    );
    expect(contentFrame, `No content iframe for ${route}`).toBeTruthy();

    const link = await contentFrame.locator('a').evaluateAll((anchors, currentRoute) => {
      for (let index = 0; index < anchors.length; index += 1) {
        const anchor = anchors[index];
        const href = (anchor.getAttribute('href') || '').trim();
        const style = getComputedStyle(anchor);
        const rect = anchor.getBoundingClientRect();
        if (!href.startsWith('?path=') || style.display === 'none' ||
            style.visibility === 'hidden' || rect.width === 0 || rect.height === 0) continue;
        const target = new URLSearchParams(href.slice(1)).get('path');
        if (target && target !== currentRoute) {
          return { index, target };
        }
      }
      return null;
    }, route);

    if (link) {
      await contentFrame.locator('a').nth(link.index).click();
      await page.waitForFunction(target =>
        new URLSearchParams(window.location.search).get('path') === target,
      link.target);
      await waitForDAIADS(page);
      expect(new URLSearchParams(page.url().split('?')[1] || '').get('path')).toBe(link.target);
    }

    expect(pageErrors, JSON.stringify({ route, link, pageErrors }, null, 2)).toEqual([]);
  });
}
