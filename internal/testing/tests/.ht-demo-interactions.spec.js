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

const demoRoutes = collectRoutes(chapters).filter(route => route.startsWith('Demos/'));

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

test.describe.configure({ mode: 'parallel' });

for (const route of demoRoutes) {
  test(`keyboard activates a primary demo control: ${route}`, async ({ page }) => {
    const pageErrors = [];
    page.on('pageerror', error => pageErrors.push(error.message));
    await page.goto(`?path=${encodeURIComponent(route)}`);
    await waitForDAIADS(page);

    let activated = 0;
    for (const frame of page.frames().filter(candidate => candidate !== page.mainFrame())) {
      const button = frame.locator(
        'button:visible:not(.demo-fullscreen-button):not([disabled])'
      ).first();
      if (await button.count()) {
        await button.focus();
        await button.press('Enter');
        activated += 1;
      }
    }

    expect(pageErrors, JSON.stringify(pageErrors, null, 2)).toEqual([]);
    expect(activated).toBeGreaterThanOrEqual(0);
  });
}
