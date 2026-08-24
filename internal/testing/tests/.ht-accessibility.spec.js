const fs = require('node:fs');
const path = require('node:path');
const { test, expect } = require('@playwright/test');
const axeSource = require('axe-core').source;

const wcagTags = [
  'wcag2a',
  'wcag2aa',
  'wcag21a',
  'wcag21aa',
];

const chapters = JSON.parse(
  fs.readFileSync(
    path.join(__dirname, '../../../scripts/chapters.json'),
    'utf8'
  )
);

function collectRoutes(node, parts = [], routes = []) {
  if (typeof node === 'string') {
    const route = node.replace(/\.html$/i, '');

    // Draft entries are grouped under More/DRAFTS for display, but their
    // stored values are already paths relative to Content/.
    routes.push(
      parts.join('/') === 'More/DRAFTS' && route.includes('/')
        ? route
        : [...parts, route].join('/')
    );
  } else if (Array.isArray(node)) {
    for (const child of node) {
      collectRoutes(child, parts, routes);
    }
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
    ) {
      return false;
    }

    const demos = [...frameDocument.querySelectorAll('iframe.embeddedDemo')];

    return demos.every(
      demo =>
        demo.contentDocument?.body &&
        demo.contentDocument.readyState === 'complete'
    );
  });
}

async function scanAllFrames(page) {
  const violations = [];

  for (const frame of page.frames()) {
    await frame.addScriptTag({ content: axeSource });

    const results = await frame.evaluate(async tags => {
      return window.axe.run(document, {
        runOnly: {
          type: 'tag',
          values: tags,
        },
      });
    }, wcagTags);

    for (const violation of results.violations) {
      violations.push({
        frame: frame.url(),
        id: violation.id,
        impact: violation.impact,
        help: violation.help,
        targets: violation.nodes.map(node => node.target),
      });
    }
  }

  return violations;
}

test.describe.configure({ mode: 'parallel' });

for (const route of routes) {
  test(`WCAG 2.1 AA: ${route}`, async ({ page }) => {
    await page.goto(`?path=${encodeURIComponent(route)}`);
    await waitForDAIADS(page);

    await expect(page.locator('#errorMessage')).toBeHidden();

    const violations = await scanAllFrames(page);

    expect(
      violations,
      JSON.stringify(violations, null, 2)
    ).toEqual([]);
  });
}
