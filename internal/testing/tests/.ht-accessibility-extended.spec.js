const fs = require('node:fs');
const path = require('node:path');
const { test, expect } = require('@playwright/test');
const axeSource = require('axe-core').source;

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
    const frameLocation = iframe?.contentWindow?.location.href;

    if (
      !frameDocument?.body ||
      frameDocument.readyState !== 'complete' ||
      frameLocation === 'about:blank' ||
      !frameDocument.title ||
      frameDocument.body.childElementCount === 0
    ) return false;

    return [...frameDocument.querySelectorAll('iframe.embeddedDemo')].every(
      demo => demo.contentDocument?.body && demo.contentDocument.readyState === 'complete'
    );
  });
}

async function scanFrames(page, options = {}, frames = page.frames()) {
  const violations = [];
  for (const frame of frames) {
    await frame.addScriptTag({ content: axeSource });
    const results = await frame.evaluate(async axeOptions => {
      return window.axe.run(document, axeOptions);
    }, options);

    for (const violation of results.violations) {
      violations.push({
        frame: frame.url(),
        id: violation.id,
        impact: violation.impact,
        targets: violation.nodes.map(node => node.target),
      });
    }
  }
  return violations;
}

async function clippedTextAfterSpacing(frame) {
  return frame.evaluate(() => {
    const style = document.createElement('style');
    style.dataset.accessibilityTest = 'text-spacing';
    style.textContent = `
      body * {
        line-height: 1.5 !important;
        letter-spacing: 0.12em !important;
        word-spacing: 0.16em !important;
      }
      p { margin-bottom: 2em !important; }
    `;
    document.head.append(style);

    const ignored = element =>
      element.closest(
        '.visually-hidden, .sr-only, [hidden], [aria-hidden="true"], script, style, svg, canvas'
      );

    const failures = [...document.querySelectorAll('body *')]
      .filter(element => !ignored(element))
      .filter(element => [...element.childNodes].some(node =>
        node.nodeType === Node.TEXT_NODE && node.textContent.trim()
      ))
      .map(element => {
        const computed = getComputedStyle(element);
        const clipsX = ['hidden', 'clip'].includes(computed.overflowX);
        const clipsY = ['hidden', 'clip'].includes(computed.overflowY);
        return {
          element,
          clipsX,
          clipsY,
          clippedX: clipsX && element.scrollWidth > element.clientWidth + 1,
          clippedY: clipsY && element.scrollHeight > element.clientHeight + 1,
        };
      })
      .filter(item => item.clippedX || item.clippedY)
      .map(({ element, clippedX, clippedY }) => ({
        selector: `${element.tagName.toLowerCase()}${element.id ? `#${element.id}` : ''}${
          typeof element.className === 'string' && element.className.trim()
            ? `.${element.className.trim().split(/\s+/).join('.')}`
            : ''
        }`,
        clippedX,
        clippedY,
        clientWidth: element.clientWidth,
        scrollWidth: element.scrollWidth,
        clientHeight: element.clientHeight,
        scrollHeight: element.scrollHeight,
        text: element.textContent.trim().slice(0, 100),
      }))
      .slice(0, 12);

    style.remove();
    return failures;
  });
}

async function auditFocusableElements(frame) {
  return frame.evaluate(async () => {
    const selector = [
      'a[href]',
      'button',
      'input',
      'select',
      'textarea',
      '[tabindex]:not([tabindex="-1"])',
    ].join(',');

    const visible = element => {
      const style = getComputedStyle(element);
      const rect = element.getBoundingClientRect();
      return !element.disabled &&
        element.getAttribute('aria-hidden') !== 'true' &&
        style.display !== 'none' &&
        style.visibility !== 'hidden' &&
        rect.width > 0 && rect.height > 0;
    };

    const failures = [];
    for (const element of [...document.querySelectorAll(selector)].filter(visible)) {
      const tabIndexAttribute = element.getAttribute('tabindex');
      if (tabIndexAttribute !== null && Number(tabIndexAttribute) > 0) {
        failures.push({ type: 'positive-tabindex', html: element.outerHTML.slice(0, 180) });
        continue;
      }

      element.focus({ preventScroll: true });
      element.scrollIntoView({ block: 'center', inline: 'nearest' });
      if (element.matches('.skip-link')) {
        await new Promise(resolve => setTimeout(resolve, 180));
      }
      const rect = element.getBoundingClientRect();
      const visibleRects = [...element.getClientRects()].filter(candidate =>
        candidate.bottom > 0 && candidate.top < innerHeight &&
        candidate.right > 0 && candidate.left < innerWidth
      );
      const hits = visibleRects.map(candidate => {
        const x = Math.min(innerWidth - 1, Math.max(0, candidate.left + candidate.width / 2));
        const y = Math.min(innerHeight - 1, Math.max(0, candidate.top + candidate.height / 2));
        return document.elementsFromPoint(x, y);
      });
      const unobscured = hits.some(hit =>
        hit.some(candidate => candidate === element || element.contains(candidate))
      );

      if (
        document.activeElement !== element ||
        rect.bottom <= 0 || rect.top >= innerHeight ||
        rect.right <= 0 || rect.left >= innerWidth ||
        !unobscured
      ) {
        failures.push({
          type: 'focus-obscured-or-unavailable',
          html: element.outerHTML.slice(0, 180),
          rect: {
            top: Math.round(rect.top),
            right: Math.round(rect.right),
            bottom: Math.round(rect.bottom),
            left: Math.round(rect.left),
          },
          active: document.activeElement === element,
          hit: (hits[0] || []).slice(0, 3).map(candidate => candidate.tagName.toLowerCase()),
        });
      }
    }
    return failures;
  });
}

test.describe('all-route supplemental accessibility checks', () => {
  test.describe.configure({ mode: 'parallel' });

  for (const route of routes) {
    test(`text spacing and runtime: ${route}`, async ({ page }) => {
      const pageErrors = [];
      const failedResponses = [];

      page.on('pageerror', error => pageErrors.push(error.message));
      page.on('response', response => {
        const url = new URL(response.url());
        if (
          url.origin === 'http://127.0.0.1:8099' &&
          url.pathname.startsWith('/DAIADS/') &&
          response.status() >= 400
        ) {
          failedResponses.push({ status: response.status(), url: response.url() });
        }
      });

      await page.goto(`?path=${encodeURIComponent(route)}`);
      await waitForDAIADS(page);
      await expect(page.locator('#errorMessage')).toBeHidden();

      const clippedText = [];
      for (const frame of page.frames()) {
        const failures = await clippedTextAfterSpacing(frame);
        if (failures.length) clippedText.push({ frame: frame.url(), failures });
      }

      expect({ pageErrors, failedResponses, clippedText }).toEqual({
        pageErrors: [],
        failedResponses: [],
        clippedText: [],
      });
    });
  }
});

test('skip link and responsive menu work from the keyboard', async ({ page }) => {
  await page.setViewportSize({ width: 700, height: 800 });
  await page.goto('');
  await page.locator('#menu-content a[href]').first().waitFor({ state: 'attached' });

  await page.keyboard.press('Tab');
  await expect(page.locator('.skip-link')).toBeFocused();
  await expect(page.locator('.skip-link')).toBeInViewport();

  await page.keyboard.press('Enter');
  await expect(page.locator('#content-wrapper')).toBeFocused();

  await page.locator('#hamburger').focus();
  await page.keyboard.press('Enter');
  await expect(page.locator('#hamburger')).toHaveAttribute('aria-expanded', 'true');
  await page.keyboard.press('Space');
  await expect(page.locator('#hamburger')).toHaveAttribute('aria-expanded', 'false');
});

test('lesson sections and demo controls support keyboard operation', async ({ page }) => {
  await page.goto('?path=Algorithms%2FDecrease-and-Conquer%2FBinary%20Search');
  await waitForDAIADS(page);

  const lesson = page.frameLocator('#content');
  const section = lesson.locator('.section-toggle').first();
  const before = await section.getAttribute('aria-expanded');
  await section.focus();
  await section.press('Enter');
  await expect(section).toHaveAttribute('aria-expanded', before === 'true' ? 'false' : 'true');

  await page.goto('?path=Demos%2FDecrease-and-Conquer%2FBinary%20Search%20Demo');
  await waitForDAIADS(page);
  const demo = page.frameLocator('#content');
  const previous = demo.locator('#prev');
  const next = demo.locator('#next');

  await expect(previous).toBeDisabled();
  await demo.locator('#search').focus();
  await demo.locator('#search').press('Enter');
  await expect(next).toBeEnabled();
  await next.focus();
  await next.press('Enter');
  await expect(previous).toBeEnabled();
});

const statefulDemos = [
  {
    route: 'Demos/Decrease-and-Conquer/Binary Search Demo',
    status: '#status',
    setupButtons: ['Search'],
    actionButton: 'Next',
  },
  {
    route: 'Demos/Brute Force/Bubble Sort Demo',
    status: '#stepInfo',
    setupButtons: [],
    actionButton: 'Next',
  },
  {
    route: 'Demos/Divide-and-Conquer/QuickSort Demo',
    status: '#step-desc',
    setupButtons: [],
    actionButton: 'Next',
  },
  {
    route: 'Demos/Greedy/Huffman Encoding Demo',
    status: '#output',
    setupButtons: ['Start'],
    actionButton: 'Next',
  },
  {
    route: 'Demos/Data Structures/BST Operations Demo',
    status: '#status',
    setupButtons: ['Search'],
    actionButton: 'Next Step',
  },
  {
    route: 'Demos/Space-Time Tradeoff/Counting Sort Demo',
    status: '#description',
    setupButtons: [],
    actionButton: 'Next',
  },
];

for (const entry of statefulDemos) {
  test(`stateful axe and live status: ${entry.route}`, async ({ page }) => {
    await page.goto(`?path=${encodeURIComponent(entry.route)}`);
    await waitForDAIADS(page);
    const demo = page.frameLocator('#content');
    const status = demo.locator(entry.status);

    await expect(status).toHaveAttribute('role', 'status');
    await expect(status).toHaveAttribute('aria-live', 'polite');
    for (const name of entry.setupButtons) {
      await demo.getByRole('button', { name, exact: true }).click();
    }

    const initialStatus = await status.textContent();
    const action = demo.getByRole('button', { name: entry.actionButton, exact: true });
    await expect(action).toBeEnabled();
    await action.click();
    await expect(status).not.toHaveText(initialStatus);

    const violations = await scanFrames(page, {
      runOnly: {
        type: 'tag',
        values: ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'],
      },
    });
    expect(violations, JSON.stringify(violations, null, 2)).toEqual([]);
  });
}

test.describe('all-route WCAG 2.2 target-size checks', () => {
  test.describe.configure({ mode: 'parallel' });

  test('WCAG 2.2 target size: application shell', async ({ page }) => {
    await page.goto('');
    await waitForDAIADS(page);
    const options = {
      runOnly: { type: 'rule', values: ['target-size'] },
      rules: { 'target-size': { enabled: true } },
    };
    const violations = await scanFrames(page, options, [page.mainFrame()]);
    expect(violations, JSON.stringify(violations, null, 2)).toEqual([]);
  });

  for (const route of routes) {
    test(`WCAG 2.2 target size: ${route}`, async ({ page }) => {
      await page.goto(`?path=${encodeURIComponent(route)}`);
      await waitForDAIADS(page);
      const options = {
        runOnly: { type: 'rule', values: ['target-size'] },
        rules: { 'target-size': { enabled: true } },
      };
      const contentFrames = page.frames().filter(frame => frame !== page.mainFrame());
      const violations = await scanFrames(page, options, contentFrames);
      expect(violations, JSON.stringify(violations, null, 2)).toEqual([]);
    });
  }
});

const representativeRoutes = [
  'Start Here/About',
  'Algorithms/Decrease-and-Conquer/Binary Search',
  'Demos/Brute Force/Bubble Sort Demo',
  'Demos/Data Structures/BST Operations Demo',
  'Demos/Greedy/Huffman Encoding Demo',
  'Demos/Space-Time Tradeoff/Counting Sort Demo',
];

for (const route of representativeRoutes) {
  test(`focus is reachable and unobscured: ${route}`, async ({ page }) => {
    await page.goto(`?path=${encodeURIComponent(route)}`);
    await waitForDAIADS(page);
    const failures = [];
    for (const frame of page.frames()) {
      const frameFailures = await auditFocusableElements(frame);
      if (frameFailures.length) failures.push({ frame: frame.url(), failures: frameFailures });
    }
    expect(failures, JSON.stringify(failures, null, 2)).toEqual([]);
  });

  test(`reduced motion suppresses active CSS motion: ${route}`, async ({ page }) => {
    await page.emulateMedia({ reducedMotion: 'reduce' });
    await page.goto(`?path=${encodeURIComponent(route)}`);
    await waitForDAIADS(page);

    const failures = [];
    for (const frame of page.frames()) {
      const result = await frame.evaluate(() => {
        const toMilliseconds = value => value.endsWith('ms')
          ? Number.parseFloat(value)
          : Number.parseFloat(value) * 1000;
        const offenders = [...document.querySelectorAll('body *')]
          .filter(element => {
            const style = getComputedStyle(element);
            const animation = style.animationDuration.split(',').some(value =>
              toMilliseconds(value.trim()) > 1
            );
            const transition = style.transitionDuration.split(',').some(value =>
              toMilliseconds(value.trim()) > 1
            );
            return animation || transition;
          })
          .map(element => element.outerHTML.slice(0, 180))
          .slice(0, 10);
        return {
          reduced: matchMedia('(prefers-reduced-motion: reduce)').matches,
          offenders,
        };
      });
      if (!result.reduced || result.offenders.length) {
        failures.push({ frame: frame.url(), ...result });
      }
    }
    expect(failures, JSON.stringify(failures, null, 2)).toEqual([]);
  });

  test(`forced colors preserve focus visibility: ${route}`, async ({ page }) => {
    await page.emulateMedia({ forcedColors: 'active' });
    await page.goto(`?path=${encodeURIComponent(route)}`);
    await waitForDAIADS(page);

    const failures = [];
    for (const frame of page.frames()) {
      const result = await frame.evaluate(() => {
        const candidate = [...document.querySelectorAll(
          'button:not(:disabled), a[href], input:not(:disabled), select:not(:disabled), textarea:not(:disabled)'
        )].find(element => {
          const rect = element.getBoundingClientRect();
          const style = getComputedStyle(element);
          return rect.width > 0 && rect.height > 0 &&
            style.display !== 'none' && style.visibility !== 'hidden';
        });
        if (!candidate) return null;
        candidate.focus();
        const style = getComputedStyle(candidate);
        return {
          html: candidate.outerHTML.slice(0, 180),
          focused: document.activeElement === candidate,
          outlineStyle: style.outlineStyle,
          outlineWidth: style.outlineWidth,
        };
      });
      if (result && (!result.focused || result.outlineStyle === 'none' || result.outlineWidth === '0px')) {
        failures.push({ frame: frame.url(), ...result });
      }
    }
    expect(failures, JSON.stringify(failures, null, 2)).toEqual([]);
  });
}

test('shell and demo expose useful accessibility trees', async ({ page }) => {
  await page.goto('?path=Demos%2FDecrease-and-Conquer%2FBinary%20Search%20Demo');
  await waitForDAIADS(page);

  const shellSnapshot = await page.locator('body').ariaSnapshot();
  expect(shellSnapshot).toContain('link "Skip to main content"');
  expect(shellSnapshot).toContain('navigation "Table of contents"');
  expect(shellSnapshot).toContain('button "Hide table of contents"');

  const demoSnapshot = await page.frameLocator('#content').locator('body').ariaSnapshot();
  expect(demoSnapshot).toContain('heading "Binary Search Demo"');
  expect(demoSnapshot).toContain('button "Search"');
  expect(demoSnapshot).toContain('status');
});
