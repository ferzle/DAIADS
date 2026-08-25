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
    return frameDocument?.readyState === 'complete' && frameDocument?.body?.childElementCount;
  });
}

test.describe.configure({ mode: 'parallel' });

for (const route of demoRoutes) {
  test(`invalid-input probe: ${route}`, async ({ page }) => {
    const pageErrors = [];
    const dialogs = [];
    page.on('pageerror', error => pageErrors.push(error.message));
    page.on('dialog', async dialog => {
      dialogs.push(dialog.message());
      await dialog.accept();
    });

    await page.goto(`?path=${encodeURIComponent(route)}`);
    await waitForDAIADS(page);

    const probes = [];
    for (const frame of page.frames().filter(candidate => candidate !== page.mainFrame())) {
      const probe = await frame.evaluate(() => {
        const input = [...document.querySelectorAll('input:not([type="hidden"]):not([type="checkbox"]):not([type="radio"]), textarea')]
          .find(element => {
            const style = getComputedStyle(element);
            const rect = element.getBoundingClientRect();
            return style.display !== 'none' && style.visibility !== 'hidden' && rect.width > 0 && rect.height > 0;
          });
        if (!input) return { input: false, activated: false };

        if (input.tagName === 'TEXTAREA' || input.type === 'text') {
          input.value = '';
        } else {
          const min = Number(input.min);
          input.value = Number.isFinite(min) ? String(min - 1) : '';
        }
        input.dispatchEvent(new Event('input', { bubbles: true }));
        input.dispatchEvent(new Event('change', { bubbles: true }));

        const priority = [
          '#useCustom', '#createItemsBtn', '#create-coeffs-btn', '#startBtn', '#start',
          '#generate', '#generateBtn', '#randomItemBtn', '#setWeightBtn', '#search',
        ];
        const button = priority.map(selector => document.querySelector(selector))
          .find(candidate => candidate && !candidate.disabled && getComputedStyle(candidate).display !== 'none')
          || [...document.querySelectorAll('button:not(.demo-fullscreen-button)')]
            .find(candidate => !candidate.disabled && getComputedStyle(candidate).display !== 'none');
        if (!button) return { input: true, activated: false };
        button.focus();
        button.click();
        return { input: true, activated: true, control: button.id || button.textContent.trim().slice(0, 40) };
      });
      probes.push({ frame: frame.url(), probe });
    }

    await page.waitForTimeout(100);
    expect(pageErrors, JSON.stringify({ route, pageErrors, probes }, null, 2)).toEqual([]);
    for (const message of dialogs) {
      expect(message.trim(), JSON.stringify({ route, dialogs }, null, 2)).not.toBe('');
    }
  });
}
