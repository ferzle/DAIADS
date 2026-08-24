const fs = require('node:fs');
const path = require('node:path');
const { test, expect } = require('@playwright/test');
const reflowExceptions = require('./.ht-reflow-exceptions');

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

function contentPath(frameUrl) {
  const marker = '/Content/';
  const pathname = decodeURIComponent(new URL(frameUrl).pathname);
  const index = pathname.indexOf(marker);
  return index === -1 ? null : pathname.slice(index + marker.length);
}

async function inspectFrame(frame, exceptions) {
  return frame.evaluate(exceptionEntries => {
    const style = document.createElement('style');
    style.dataset.reflowTest = 'normalization';
    style.textContent = `
      mjx-assistive-mml { display: none !important; }
      [data-reflow-test-exempt] { display: none !important; }
    `;
    document.head.append(style);

    const missingExceptions = [];
    for (const entry of exceptionEntries) {
      const matches = [...document.querySelectorAll(entry.selector)];
      if (matches.length === 0) {
        missingExceptions.push(entry);
      } else {
        for (const element of matches) {
          element.setAttribute('data-reflow-test-exempt', '');
        }
      }
    }

    const root = document.documentElement;
    const body = document.body;
    const clientWidth = root.clientWidth;
    const scrollWidth = Math.max(root.scrollWidth, body?.scrollWidth || 0);
    const offenders = [...document.querySelectorAll('body *')]
      .filter(element => !element.closest('[data-reflow-test-exempt]'))
      .map(element => {
        const rect = element.getBoundingClientRect();
        return {
          selector: `${element.tagName.toLowerCase()}${element.id ? `#${element.id}` : ''}${
            typeof element.className === 'string' && element.className.trim()
              ? `.${element.className.trim().split(/\s+/).join('.')}`
              : ''
          }`,
          left: Math.round(rect.left),
          right: Math.round(rect.right),
          width: Math.round(rect.width),
        };
      })
      .filter(item => item.right > clientWidth + 1 || item.left < -1)
      .sort((a, b) => b.right - a.right)
      .slice(0, 8);

    style.remove();
    for (const element of document.querySelectorAll('[data-reflow-test-exempt]')) {
      element.removeAttribute('data-reflow-test-exempt');
    }

    return {
      clientWidth,
      scrollWidth,
      overflow: Math.max(0, scrollWidth - clientWidth),
      offenders,
      missingExceptions,
    };
  }, exceptions);
}

test.describe('WCAG 1.4.10 reflow at 320 CSS pixels', () => {
  test.use({ viewport: { width: 320, height: 800 } });
  test.describe.configure({ mode: 'parallel' });

  for (const route of routes) {
    test(`reflow: ${route}`, async ({ page }) => {
      await page.goto(`?path=${encodeURIComponent(route)}`);
      await waitForDAIADS(page);
      await expect(page.locator('#errorMessage')).toBeHidden();

      const failures = [];
      for (const frame of page.frames()) {
        const file = contentPath(frame.url());
        const exceptions = file ? reflowExceptions[file] || [] : [];
        const result = await inspectFrame(frame, exceptions);

        if (result.overflow > 1 || result.missingExceptions.length > 0) {
          failures.push({ frame: frame.url(), file, exceptions, ...result });
        }
      }

      expect(failures, JSON.stringify(failures, null, 2)).toEqual([]);
    });
  }
});

test('responsive table of contents follows the viewport and user navigation', async ({ page }) => {
  const menu = page.locator('#menu');
  const hamburger = page.locator('#hamburger');

  await page.setViewportSize({ width: 900, height: 800 });
  await page.goto('');
  await page.locator('#menu-content a[href]').first().waitFor({ state: 'attached' });

  await expect(menu).not.toHaveClass(/collapsed/);
  await expect(hamburger).toHaveAttribute('aria-expanded', 'true');

  await page.setViewportSize({ width: 700, height: 800 });
  await expect(menu).toHaveClass(/collapsed/);
  await expect(hamburger).toHaveAttribute('aria-expanded', 'false');

  await hamburger.click();
  await expect(menu).not.toHaveClass(/collapsed/);

  await page.locator('#menu-content a[href]').first().evaluate(link => link.click());
  await expect(menu).toHaveClass(/collapsed/);

  await page.setViewportSize({ width: 900, height: 800 });
  await expect(menu).not.toHaveClass(/collapsed/);
  await expect(hamburger).toHaveAttribute('aria-expanded', 'true');
});

test('Huffman text input scales with the available width', async ({ page }) => {
  await page.goto('?path=Demos%2FGreedy%2FHuffman%20Encoding%20Demo');
  await waitForDAIADS(page);

  const input = page.frameLocator('#content').locator('#inputText');
  const widths = [];
  for (const width of [700, 500, 320]) {
    await page.setViewportSize({ width, height: 800 });
    widths.push(await input.evaluate(element => element.getBoundingClientRect().width));
  }

  expect(widths[0]).toBeGreaterThan(widths[1]);
  expect(widths[1]).toBeGreaterThan(widths[2]);
});

test('Huffman final tree supports subtree mirroring without an opt-in control', async ({ page }) => {
  await page.goto('?path=Demos%2FGreedy%2FHuffman%20Encoding%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  await expect(demo.locator('#allowFlips')).toHaveCount(0);
  await expect(demo.locator('#treeInteractionHint')).toContainText('click any node');

  await demo.getByRole('button', { name: 'Start' }).click();
  await demo.locator('body').evaluate(() => {
    for (let attempts = 0; attempts < 100; attempts += 1) {
      if (document.querySelector('#output')?.textContent.includes('Huffman Tree Complete')) break;
      nextStep();
    }
  });

  const treeNode = demo.locator('#queue-svg .node').first();
  await expect(treeNode).toHaveCSS('cursor', 'pointer');
  await treeNode.click();
  await expect(demo.locator('#output')).toContainText('Encoding Table (flipped)');
});

test('Selection Sort pointer labels stay above the step comment', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FBrute%20Force%2FSelection%20Sort%20Demo');
  await waitForDAIADS(page);

  const layout = await page.frameLocator('#content').locator('body').evaluate(() => ({
    pointerBottom: Math.max(
      ...[...document.querySelectorAll('#boxes .arrow')]
        .map(element => element.getBoundingClientRect().bottom)
    ),
    commentTop: document.querySelector('#step-desc').getBoundingClientRect().top,
  }));

  expect(layout.pointerBottom).toBeLessThanOrEqual(layout.commentTop);
});

test('Quick Sort legend wraps complete box-and-label items', async ({ page }) => {
  for (const width of [500, 320]) {
    await page.setViewportSize({ width, height: 800 });
    await page.goto('?path=Demos%2FDivide-and-Conquer%2FQuickSort%20Demo');
    await waitForDAIADS(page);

    const layout = await page.frameLocator('#content').locator('.legend').evaluate(legend => {
      const items = [...legend.querySelectorAll('.legend-item')];
      return {
        rowTops: [...new Set(items.map(item => Math.round(item.getBoundingClientRect().top)))],
        items: items.map(item => ({
          flexShrink: getComputedStyle(item).flexShrink,
          whiteSpace: getComputedStyle(item).whiteSpace,
        })),
      };
    });

    expect(layout.rowTops.length).toBeGreaterThan(1);
    expect(layout.items.every(item => item.flexShrink === '0' && item.whiteSpace === 'nowrap')).toBe(true);
  }
});

test('Insertion Sort legend wraps complete box-and-label items', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FDecrease-and-Conquer%2FInsertion%20Sort%20Demo');
  await waitForDAIADS(page);

  const layout = await page.frameLocator('#content').locator('#legend').evaluate(legend => {
    const items = [...legend.querySelectorAll('.legend-item')];
    return {
      rowTops: [...new Set(items.map(item => Math.round(item.getBoundingClientRect().top)))],
      atomic: items.every(item => {
        const style = getComputedStyle(item);
        return style.flexShrink === '0' && style.whiteSpace === 'nowrap';
      }),
    };
  });

  expect(layout.rowTops.length).toBeGreaterThan(1);
  expect(layout.atomic).toBe(true);
});

test('Insertion Sort uses a local scrollbar for a narrow 16-element array', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FDecrease-and-Conquer%2FInsertion%20Sort%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  await demo.locator('#size').fill('16');
  await demo.locator('#generate').click();

  const layout = await demo.locator('#array-container').evaluate(array => ({
    paneClient: array.clientWidth,
    paneScroll: array.scrollWidth,
    pageClient: document.documentElement.clientWidth,
    pageScroll: Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
    cellWidths: [...array.children].map(cell => cell.getBoundingClientRect().width),
  }));

  expect(layout.paneScroll).toBeGreaterThan(layout.paneClient);
  expect(layout.pageScroll).toBeLessThanOrEqual(layout.pageClient + 1);
  expect(Math.min(...layout.cellWidths)).toBeGreaterThanOrEqual(22);
});

test('Quickselect keeps array entries closely spaced', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FDecrease-and-Conquer%2FQuickselect%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  await demo.locator('#arraySize').fill('16');
  await demo.locator('#generate').click();
  await demo.locator('#next').click();

  const spacing = await demo.locator('#boxes .subarray-row').evaluate(row => {
    const entries = [...row.querySelectorAll(':scope > .element-wrapper')];
    const first = entries[0].getBoundingClientRect();
    const second = entries[1].getBoundingClientRect();
    return second.left - first.right;
  });

  expect(spacing).toBeLessThanOrEqual(2.1);
});

test('0-1 Knapsack keeps Values and Weights in one horizontal scroll pane', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FDynamic%20Programming%2F0-1%20Knapsack%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  await demo.locator('#itemSizeInput').fill('16');
  await demo.locator('#randomItemBtn').click();

  const layout = await demo.locator('body').evaluate(() => {
    const pane = document.querySelector('.array-panes-scroll');
    const paneRect = pane.getBoundingClientRect();
    const cells = [...document.querySelectorAll('#v-array .element, #w-array .element')];
    return {
      paneClient: pane.clientWidth,
      paneScroll: pane.scrollWidth,
      overflowX: getComputedStyle(pane).overflowX,
      overflowY: getComputedStyle(pane).overflowY,
      paneHeight: pane.clientHeight,
      paneScrollHeight: pane.scrollHeight,
      cellsTop: Math.min(...cells.map(cell => cell.getBoundingClientRect().top)),
      paneTop: paneRect.top,
      pageClient: document.documentElement.clientWidth,
      pageScroll: Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
    };
  });

  expect(layout.paneScroll).toBeGreaterThan(layout.paneClient);
  expect(layout.overflowX).toBe('auto');
  expect(layout.overflowY).toBe('hidden');
  expect(layout.paneScrollHeight).toBeLessThanOrEqual(layout.paneHeight + 1);
  expect(layout.cellsTop).toBeGreaterThanOrEqual(layout.paneTop);
  expect(layout.pageScroll).toBeLessThanOrEqual(layout.pageClient + 1);
});

test('Merge Sort keeps conceptual cells tight and auxiliary cells legible', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FDivide-and-Conquer%2FMerge%20Sort%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  for (let step = 0; step < 40; step++) {
    const ready = await demo.locator('body').evaluate(() =>
      document.querySelectorAll('#aux-container > .element:not(.hidden)').length >= 2 &&
      [...document.querySelectorAll('#subarrays .array-container .array-container')]
        .some(row => row.querySelectorAll(':scope > .element').length >= 2)
    );
    if (ready) break;
    await demo.locator('#next').click();
  }

  const layout = await demo.locator('body').evaluate(() => {
    const conceptualRow = [...document.querySelectorAll('#subarrays .array-container .array-container')]
      .find(row => row.querySelectorAll(':scope > .element').length >= 2);
    const conceptualCells = conceptualRow.querySelectorAll(':scope > .element');
    const firstConceptual = conceptualCells[0].getBoundingClientRect();
    const secondConceptual = conceptualCells[1].getBoundingClientRect();
    const auxCell = document.querySelector('#aux-container > .element:not(.hidden)');
    const auxCells = document.querySelectorAll('#aux-container > .element');
    const auxStyle = getComputedStyle(auxCell);
    const auxRect = auxCell.getBoundingClientRect();
    const nextAuxRect = auxCells[1].getBoundingClientRect();
    const mainWrappers = document.querySelectorAll('#visualization > .element-wrapper');
    const mainRect = mainWrappers[0].getBoundingClientRect();
    const nextMainRect = mainWrappers[1].getBoundingClientRect();

    return {
      conceptualGap: secondConceptual.left - firstConceptual.right,
      conceptualWidth: firstConceptual.width,
      auxWidth: auxRect.width,
      auxHeight: auxRect.height,
      auxGap: nextAuxRect.left - auxCells[0].getBoundingClientRect().right,
      mainWidth: mainRect.width,
      mainGap: nextMainRect.left - mainRect.right,
      auxBorderLeft: parseFloat(auxStyle.borderLeftWidth),
      auxBorderRight: parseFloat(auxStyle.borderRightWidth),
      pageClient: document.documentElement.clientWidth,
      pageScroll: Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
    };
  });

  expect(layout.conceptualGap).toBeLessThanOrEqual(2.1);
  expect(layout.conceptualWidth).toBeGreaterThanOrEqual(28);
  expect(layout.conceptualWidth).toBeLessThan(40);
  expect(layout.auxWidth).toBeGreaterThanOrEqual(28);
  expect(layout.auxWidth).toBeLessThanOrEqual(40);
  expect(layout.auxHeight).toBeCloseTo(layout.auxWidth, 1);
  expect(layout.auxWidth).toBeCloseTo(layout.mainWidth, 1);
  expect(layout.auxGap).toBeCloseTo(layout.mainGap, 1);
  expect(layout.auxBorderLeft).toBeLessThanOrEqual(2);
  expect(layout.auxBorderRight).toBeLessThanOrEqual(2);
  expect(layout.pageScroll).toBeLessThanOrEqual(layout.pageClient + 1);

  for (let step = 0; step < 15; step++) {
    const pointersVisible = await demo.locator('body').evaluate(() =>
      ['aux-arrow', 'aux-arrow2'].every(id =>
        getComputedStyle(document.getElementById(id)).visibility === 'visible'
      )
    );
    if (pointersVisible) break;
    await demo.locator('#next').click();
  }

  const pointers = await demo.locator('#aux-container').evaluate(aux => {
    const cells = [...aux.querySelectorAll(':scope > .element:not(.hidden)')];
    const arrows = [...aux.querySelectorAll(':scope > #aux-arrow, :scope > #aux-arrow2')];
    const cellBottom = Math.max(...cells.map(cell => cell.getBoundingClientRect().bottom));
    const auxBottom = aux.getBoundingClientRect().bottom;
    return arrows.map(arrow => {
      const rect = arrow.getBoundingClientRect();
      return {
        visibility: getComputedStyle(arrow).visibility,
        top: rect.top,
        bottom: rect.bottom,
        cellBottom,
        auxBottom,
      };
    });
  });

  expect(pointers.every(pointer => pointer.visibility === 'visible')).toBe(true);
  expect(pointers.every(pointer => pointer.top >= pointer.cellBottom)).toBe(true);
  expect(pointers.every(pointer => pointer.bottom <= pointer.auxBottom + 1)).toBe(true);
});


test('Quickselect only shows horizontal scrollbars when needed', async ({ page }) => {
  await page.setViewportSize({ width: 1000, height: 800 });
  await page.goto('?path=Demos%2FDecrease-and-Conquer%2FQuickselect%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  await demo.locator('#arraySize').fill('16');
  await demo.locator('#generate').click();
  await demo.locator('#next').click();

  const readOverflow = () => demo.locator('#boxes, #subarrays').evaluateAll(panes =>
    panes.map(pane => ({
      x: getComputedStyle(pane).overflowX,
      y: getComputedStyle(pane).overflowY,
      client: pane.clientWidth,
      scroll: pane.scrollWidth,
    }))
  );

  await expect.poll(async () => (await readOverflow()).map(({ x, y }) => ({ x, y }))).toEqual([
    { x: 'hidden', y: 'hidden' },
    { x: 'hidden', y: 'hidden' },
  ]);

  const verticalFit = await demo.locator('#subarrays').evaluate(pane => {
    const arrows = [...pane.querySelectorAll('.arrow')];
    return {
      arrowCount: arrows.length,
      lowestArrow: Math.max(...arrows.map(arrow => arrow.getBoundingClientRect().bottom)),
      paneBottom: pane.getBoundingClientRect().bottom,
    };
  });
  expect(verticalFit.arrowCount).toBeGreaterThan(0);
  expect(verticalFit.lowestArrow).toBeLessThanOrEqual(verticalFit.paneBottom);

  await page.setViewportSize({ width: 320, height: 800 });
  await expect.poll(async () => (await readOverflow()).map(({ x, y }) => ({ x, y }))).toEqual([
    { x: 'auto', y: 'hidden' },
    { x: 'auto', y: 'hidden' },
  ]);
});

test('Quickselect 5th-smallest demo shrinks before scrolling', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FDecrease-and-Conquer%2FQuickselect%20Simple%20Demo');
  await waitForDAIADS(page);

  const layout = await page.frameLocator('#content').locator('#boxes').evaluate(array => ({
    paneClient: array.clientWidth,
    paneScroll: array.scrollWidth,
    widths: [...array.querySelectorAll('.element')].map(cell => cell.getBoundingClientRect().width),
    gaps: getComputedStyle(array).gap,
    paneTop: array.getBoundingClientRect().top,
    targetTop: array.querySelector('.target').getBoundingClientRect().top - 4,
  }));

  expect(layout.paneScroll).toBeGreaterThan(layout.paneClient);
  expect(layout.paneScroll).toBeLessThan(400);
  expect(Math.max(...layout.widths)).toBeLessThanOrEqual(34);
  expect(Math.min(...layout.widths)).toBeGreaterThanOrEqual(22);
  expect(layout.gaps).toBe('2px');
  expect(layout.targetTop).toBeGreaterThanOrEqual(layout.paneTop);
});

test('Divide-and-Conquer Exponentiation tree scales to the viewport with a maximum width', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FDivide-and-Conquer%2FExponentiation%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  await demo.locator('#compute').click();
  await demo.locator('#nextBtn').click();

  const readTree = () => demo.locator('#svg').evaluate(svg => ({
    width: svg.getBoundingClientRect().width,
    pageWidth: document.documentElement.clientWidth,
    viewBoxWidth: svg.viewBox.baseVal.width,
    nodes: svg.querySelectorAll('.treeNode').length,
  }));

  const narrow = await readTree();
  expect(narrow.nodes).toBeGreaterThan(0);
  expect(narrow.width).toBeLessThanOrEqual(narrow.pageWidth);
  expect(narrow.viewBoxWidth).toBeGreaterThan(0);

  await page.setViewportSize({ width: 1400, height: 900 });
  const wide = await readTree();
  expect(wide.width).toBeLessThanOrEqual(900);
  expect(wide.width).toBeGreaterThan(narrow.width);
});

for (const graphDemo of [
  { route: 'Algorithms/Greedy/Prims DRAFT', name: "Prim's" },
  { route: 'Algorithms/Greedy/Kruskals DRAFT', name: "Kruskal's" },
]) {
  test(`${graphDemo.name} embedded graph relayouts when its section opens`, async ({ page }) => {
    await page.setViewportSize({ width: 1000, height: 900 });
    await page.goto(`?path=${encodeURIComponent(graphDemo.route)}`);
    await waitForDAIADS(page);

    const lesson = page.frameLocator('#content');
    await lesson.locator('#demo .section-toggle').click();
    const demo = lesson.frameLocator('#demo iframe.embeddedDemo');
    await demo.locator('#graphSVG .node').first().waitFor();

    await expect.poll(async () => demo.locator('#graphSVG').evaluate(svg => {
      const centers = [...svg.querySelectorAll('.node')].map(node => {
        const rect = node.getBoundingClientRect();
        return { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 };
      });
      const x = Math.max(...centers.map(point => point.x)) - Math.min(...centers.map(point => point.x));
      const y = Math.max(...centers.map(point => point.y)) - Math.min(...centers.map(point => point.y));
      return Math.min(x, y);
    })).toBeGreaterThan(150);

    const spread = await demo.locator('#graphSVG').evaluate(svg => {
      const centers = [...svg.querySelectorAll('.node')].map(node => {
        const rect = node.getBoundingClientRect();
        return { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 };
      });
      return {
        x: Math.max(...centers.map(point => point.x)) - Math.min(...centers.map(point => point.x)),
        y: Math.max(...centers.map(point => point.y)) - Math.min(...centers.map(point => point.y)),
      };
    });
    expect(spread.x).toBeGreaterThan(150);
    expect(spread.y).toBeGreaterThan(150);
  });
}

test('Brute Force matrix multiplication keeps wide output in its own scroll pane', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FBrute%20Force%2FMatrix%20Multiplication%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  await demo.locator('#dim-input').fill('8');
  await demo.locator('#generate').click();

  const widths = await demo.locator('#matrix-row').evaluate(row => ({
    paneClient: row.clientWidth,
    paneScroll: row.scrollWidth,
    pageClient: document.documentElement.clientWidth,
    pageScroll: Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
  }));

  expect(widths.paneScroll).toBeGreaterThan(widths.paneClient);
  expect(widths.pageScroll).toBeLessThanOrEqual(widths.pageClient + 1);
});

for (const matrixDemo of [
  'Matrix Multiplication Demo',
  'Matrix Multiplcation (Inplace) Demo',
]) {
  test(`${matrixDemo} keeps the matrix equation in its own scroll pane`, async ({ page }) => {
    await page.setViewportSize({ width: 320, height: 800 });
    await page.goto(`?path=${encodeURIComponent(`Demos/Divide-and-Conquer/${matrixDemo}`)}`);
    await waitForDAIADS(page);

    const demo = page.frameLocator('#content');
    await demo.locator('#dim-input').selectOption('8');
    await demo.locator('#gen-btn').click();

    const widths = await demo.locator('#matrix-row').evaluate(row => ({
      paneClient: row.clientWidth,
      paneScroll: row.scrollWidth,
      overflowX: getComputedStyle(row).overflowX,
      pageClient: document.documentElement.clientWidth,
      pageScroll: Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
    }));

    expect(widths.paneScroll).toBeGreaterThan(widths.paneClient);
    expect(widths.overflowX).toBe('auto');
    expect(widths.pageScroll).toBeLessThanOrEqual(widths.pageClient + 1);
  });
}

test('Divide-and-Conquer Matrix Multiplication keeps the main Computing C11 panel in its own scroll pane', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FDivide-and-Conquer%2FMatrix%20Multiplication%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  await demo.locator('#dim-input').selectOption('8');
  await demo.locator('#gen-btn').click();
  await demo.locator('#next-btn').click();
  await demo.locator('#next-btn').click();
  await demo.locator('#next-btn').click();

  const computation = await demo.locator('#work-area .interactive-computation').evaluate(panel => ({
    paneClient: panel.clientWidth,
    paneScroll: panel.scrollWidth,
    overflowX: getComputedStyle(panel).overflowX,
    title: panel.querySelector('.step-title')?.textContent,
    pageClient: document.documentElement.clientWidth,
    pageScroll: Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
  }));

  expect(computation.title).toContain('Computing C11');
  expect(computation.paneScroll).toBeGreaterThan(computation.paneClient);
  expect(computation.overflowX).toBe('auto');
  expect(computation.pageScroll).toBeLessThanOrEqual(computation.pageClient + 1);
});

test('Divide-and-Conquer Matrix Multiplication keeps recursive-call matrices in their own scroll pane', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FDivide-and-Conquer%2FMatrix%20Multiplication%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  await demo.locator('body').evaluate(() => {
    const matrix = Array.from({ length: 4 }, (_, row) =>
      Array.from({ length: 4 }, (_, column) => row * 4 + column + 1)
    );
    SubDemoManager.showRecursiveSubDemo({
      matrixA: matrix,
      matrixB: matrix,
      description: 'recursive test call',
    }, 'C11', 'A11 × B11', () => {});
  });

  const widths = await demo.locator('#sub-matrix-row').evaluate(row => ({
    paneClient: row.clientWidth,
    paneScroll: row.scrollWidth,
    overflowX: getComputedStyle(row).overflowX,
    pageClient: document.documentElement.clientWidth,
    pageScroll: Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
  }));

  expect(widths.paneScroll).toBeGreaterThan(widths.paneClient);
  expect(widths.overflowX).toBe('auto');
  expect(widths.pageScroll).toBeLessThanOrEqual(widths.pageClient + 1);

  await demo.locator('#sub-next-btn').click();
  await demo.locator('#sub-next-btn').click();
  await demo.locator('#sub-next-btn').click();

  const computation = await demo.locator('#sub-computation-area .interactive-computation').evaluate(panel => ({
    paneClient: panel.clientWidth,
    paneScroll: panel.scrollWidth,
    overflowX: getComputedStyle(panel).overflowX,
    title: panel.querySelector('.step-title')?.textContent,
    pageClient: document.documentElement.clientWidth,
    pageScroll: Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
  }));

  expect(computation.title).toContain('Computing C11');
  expect(computation.paneScroll).toBeGreaterThan(computation.paneClient);
  expect(computation.overflowX).toBe('auto');
  expect(computation.pageScroll).toBeLessThanOrEqual(computation.pageClient + 1);
});

for (const matrixDemo of [
  'Matrix Multiplication Demo',
  'Matrix Multiplcation (Inplace) Demo',
]) {
  test(`${matrixDemo} keeps related controls together when wrapping`, async ({ page }) => {
    await page.setViewportSize({ width: 320, height: 800 });
    await page.goto(`?path=${encodeURIComponent(`Demos/Divide-and-Conquer/${matrixDemo}`)}`);
    await waitForDAIADS(page);

    const groups = await page.frameLocator('#content').locator('.input-row, .control-cluster').evaluateAll(elements =>
      elements.map(element => ({
        childCenters: [...element.children].map(child => {
          const rect = child.getBoundingClientRect();
          return Math.round(rect.top + rect.height / 2);
        }),
        flexWrap: getComputedStyle(element).flexWrap,
        right: element.getBoundingClientRect().right,
        pageRight: document.documentElement.clientWidth,
      }))
    );

    expect(groups.length).toBeGreaterThanOrEqual(2);
    expect(groups.every(group => new Set(group.childCenters).size === 1)).toBe(true);
    expect(groups.every(group => group.flexWrap === 'nowrap')).toBe(true);
    expect(groups.every(group => group.right <= group.pageRight + 1)).toBe(true);

    const spacing = await page.frameLocator('#content').locator('.controls').evaluate(controls => ({
      rowGap: parseFloat(getComputedStyle(controls).rowGap),
      marginBottom: parseFloat(getComputedStyle(controls).marginBottom),
    }));
    expect(spacing.rowGap).toBeLessThanOrEqual(8);
    expect(spacing.marginBottom).toBeLessThanOrEqual(12);
  });
}

test('Exhaustive String Matching keeps text and pattern cells aligned', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FBrute%20Force%2FString%20Matching%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  const layout = await demo.locator('#matching-visualization-scroll').evaluate(pane => {
    const textCell = pane.querySelector('#visualization .element');
    const patternCell = pane.querySelector('#pattern-row .element:not(.empty)');
    const textRect = textCell.getBoundingClientRect();
    const patternRect = patternCell.getBoundingClientRect();
    return {
      textWidth: textRect.width,
      patternWidth: patternRect.width,
      textLeft: textRect.left,
      patternLeft: patternRect.left,
      paneClient: pane.clientWidth,
      paneScroll: pane.scrollWidth,
      pageClient: document.documentElement.clientWidth,
      pageScroll: Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
    };
  });

  expect(layout.patternWidth).toBeCloseTo(layout.textWidth, 4);
  expect(layout.patternLeft).toBeCloseTo(layout.textLeft, 4);
  expect(layout.paneScroll).toBeGreaterThan(layout.paneClient);
  expect(layout.pageScroll).toBeLessThanOrEqual(layout.pageClient + 1);
});

test('demo fullscreen control uses compact directional arrows', async ({ page }) => {
  await page.goto('?path=Demos%2FBrute%20Force%2FBubble%20Sort%20Demo');
  await waitForDAIADS(page);

  const button = page.frameLocator('#content').locator('.demo-fullscreen-button');
  await expect(button).toHaveAttribute('aria-label', 'Enter fullscreen');
  await expect(button).not.toContainText('Fullscreen');
  await expect(button).not.toHaveClass(/is-fullscreen/);

  await button.click();
  await expect(button).toHaveAttribute('aria-label', 'Exit fullscreen');
  await expect(button).toHaveClass(/is-fullscreen/);
});

test('Basic Binary Tree Algorithms legend wraps complete items', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FData%20Structures%2FBasic%20Binary%20Tree%20Algorithms%20Demo');
  await waitForDAIADS(page);

  const layout = await page.frameLocator('#content').locator('#legend').evaluate(legend => {
    const items = [...legend.querySelectorAll('.legend-item')];
    return {
      rowTops: [...new Set(items.map(item => Math.round(item.getBoundingClientRect().top)))],
      atomic: items.every(item => {
        const style = getComputedStyle(item);
        return style.flexShrink === '0' && style.whiteSpace === 'nowrap';
      }),
    };
  });

  expect(layout.rowTops.length).toBeGreaterThan(1);
  expect(layout.atomic).toBe(true);
});

test('Build Heap keeps a maximum-size array in its own scroll pane', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FData%20Structures%2FHeap%2FBuild%20Heap%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  await demo.locator('#arraySize').fill('31');
  await demo.locator('#generate').click();

  const widths = await demo.locator('#arrayContainer').evaluate(array => ({
    paneClient: array.clientWidth,
    paneScroll: array.scrollWidth,
    pageClient: document.documentElement.clientWidth,
    pageScroll: Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
  }));

  expect(widths.paneScroll).toBeGreaterThan(widths.paneClient);
  expect(widths.pageScroll).toBeLessThanOrEqual(widths.pageClient + 1);

  await demo.locator('#arraySize').fill('7');
  await demo.locator('#generate').click();
  const compact = await demo.locator('#arrayContainer').evaluate(array => ({
    paneClient: array.clientWidth,
    paneScroll: array.scrollWidth,
    cellWidths: [...array.querySelectorAll('.element')].map(cell => cell.getBoundingClientRect().width),
  }));

  expect(compact.paneScroll).toBeLessThanOrEqual(compact.paneClient + 1);
  expect(Math.max(...compact.cellWidths)).toBeLessThanOrEqual(34);
  expect(Math.min(...compact.cellWidths)).toBeGreaterThanOrEqual(22);
});

for (const heapDemo of [
  'ExtractMax Demo',
  'Heap Representation Demo',
  'Insert Demo',
  'SiftDown Demo',
]) {
  test(`${heapDemo} shrinks its array before using local scrolling`, async ({ page }) => {
    await page.setViewportSize({ width: 320, height: 800 });
    await page.goto(`?path=Demos%2FData%20Structures%2FHeap%2F${encodeURIComponent(heapDemo)}`);
    await waitForDAIADS(page);

    const demo = page.frameLocator('#content');
    await demo.locator('#arraySize').fill('31');
    await demo.locator('#generate').click();

    const layout = await demo.locator('#arrayContainer').evaluate(array => ({
      paneClient: array.clientWidth,
      paneScroll: array.scrollWidth,
      pageClient: document.documentElement.clientWidth,
      pageScroll: Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
      cellWidths: [...array.querySelectorAll('.element')].map(cell => cell.getBoundingClientRect().width),
    }));

    expect(layout.paneScroll).toBeGreaterThan(layout.paneClient);
    expect(layout.pageScroll).toBeLessThanOrEqual(layout.pageClient + 1);
    expect(Math.max(...layout.cellWidths)).toBeLessThanOrEqual(34);
    expect(Math.min(...layout.cellWidths)).toBeGreaterThanOrEqual(22);
  });
}

test('Euclidean GCD commentary expands without going under the call stack', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('?path=Demos%2FDecrease-and-Conquer%2FEuclidean%20GCD%20Demo');
  await waitForDAIADS(page);

  const demo = page.frameLocator('#content');
  await demo.locator('#inputA').fill('987654321');
  await demo.locator('#inputB').fill('123456789');
  await demo.locator('#generate').click();
  await demo.locator('#next').click();

  const boxes = await demo.locator('body').evaluate(() => {
    const commentary = document.getElementById('commentary').getBoundingClientRect();
    const panels = document.getElementById('demoWrapper').getBoundingClientRect();
    return {
      commentaryBottom: commentary.bottom,
      panelsTop: panels.top,
      commentaryHeight: commentary.height,
    };
  });

  expect(boxes.commentaryHeight).toBeGreaterThan(24);
  expect(boxes.panelsTop).toBeGreaterThanOrEqual(boxes.commentaryBottom);
});

for (const route of [
  'Demos/Decrease-and-Conquer/Exponentiation By Squaring Demo',
  'Demos/Decrease-and-Conquer/Factorial Demo',
  'Demos/Divide-and-Conquer/Exponentiation Demo',
  'Demos/Data Structures/Heap/SiftDown Demo',
  'Demos/Data Structures/Heap/Insert Demo',
  'Demos/Data Structures/Heap/ExtractMax Demo',
  'Demos/Dynamic Programming/0-1 Knapsack Demo',
  'Demos/Dynamic Programming/Fibonacci Top Down Demo',
]) {
  test(`${route} commentary expands when narrow`, async ({ page }) => {
    await page.setViewportSize({ width: 320, height: 800 });
    await page.goto(`?path=${encodeURIComponent(route)}`);
    await waitForDAIADS(page);

    const dimensions = await page.frameLocator('#content').locator('#commentary').evaluate(element => {
      element.textContent = 'A deliberately long step explanation verifies that commentary wraps onto as many lines as it needs without disappearing underneath the visualization that follows it on a very narrow screen.';
      return {
        clientHeight: element.clientHeight,
        scrollHeight: element.scrollHeight,
      };
    });

    expect(dimensions.clientHeight).toBeGreaterThan(24);
    expect(dimensions.scrollHeight).toBeLessThanOrEqual(dimensions.clientHeight + 1);
  });
}
