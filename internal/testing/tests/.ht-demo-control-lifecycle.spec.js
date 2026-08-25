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
      frameDocument?.body?.childElementCount;
  });
}

test.describe.configure({ mode: 'parallel' });

for (const route of demoRoutes) {
  test(`demo control lifecycle: ${route}`, async ({ page }) => {
    const pageErrors = [];
    const dialogs = [];
    page.on('pageerror', error => pageErrors.push(error.message));
    page.on('dialog', async dialog => {
      dialogs.push(dialog.message());
      await dialog.accept();
    });

    await page.goto(`?path=${encodeURIComponent(route)}`);
    await waitForDAIADS(page);

    const actions = [];
    for (const frame of page.frames().filter(candidate => candidate !== page.mainFrame())) {
      const frameRoot = frame.locator('body');
      if (!(await frameRoot.count())) continue;

      const clickIfUsable = async (selector, action) => {
        const control = frame.locator(selector).first();
        if (!(await control.count()) || !(await control.isVisible()) || !(await control.isEnabled())) {
          return false;
        }
        await control.click();
        actions.push(`${action}:${selector}`);
        return true;
      };

      // Start/reset the demo, then exercise custom-input mode when exposed.
      await clickIfUsable('#generate, #generateBtn, #gen-btn, #randomBtn', 'generate');
      await clickIfUsable('#useCustom', 'custom-input');
      await clickIfUsable('#generate, #generateBtn, #gen-btn, #randomBtn', 'generate-after-custom');

      for (const selector of ['#prev, #prevBtn, #prev-btn', '#next, #nextBtn, #next-btn']) {
        await clickIfUsable(selector, selector.includes('prev') ? 'previous' : 'next');
      }

      const play = frame.locator('#play, #playBtn, #play-btn').first();
      if (await play.count() && await play.isVisible() && await play.isEnabled()) {
        await play.click();
        actions.push('play');
        await page.waitForTimeout(80);
        if (await play.isVisible() && await play.isEnabled()) {
          await play.click();
          actions.push('pause');
        }
      }

      for (const selector of ['#speed, #speedSelect, #speed-select']) {
        const speed = frame.locator(selector).first();
        if (await speed.count() && await speed.isVisible() && await speed.isEnabled()) {
          const options = await speed.locator('option').evaluateAll(items => items.map(item => item.value));
          if (options.length > 1) {
            await speed.selectOption(options.at(-1));
            actions.push(`speed:${selector}`);
          }
        }
      }

      // Some demos use bespoke control names (for example, Build Table).
      // Exercise one such primary action when no shared control was found.
      if (!actions.length) {
        await clickIfUsable('button:visible:not(.demo-fullscreen-button):not([disabled])', 'primary');
      }

      const fullscreen = frame.locator('.demo-fullscreen-button').first();
      if (await fullscreen.count() && await fullscreen.isVisible() && await fullscreen.isEnabled()) {
        await fullscreen.click();
        actions.push('fullscreen-enter');
        if (await fullscreen.isVisible() && await fullscreen.isEnabled()) {
          await fullscreen.click();
          actions.push('fullscreen-exit');
        }
      }
    }

    expect(actions.length, `No usable demo controls found for ${route}`).toBeGreaterThan(0);
    expect(pageErrors, JSON.stringify({ route, pageErrors, dialogs, actions }, null, 2)).toEqual([]);
    for (const message of dialogs) {
      expect(message.trim(), JSON.stringify({ route, dialogs, actions }, null, 2)).not.toBe('');
    }
  });
}
