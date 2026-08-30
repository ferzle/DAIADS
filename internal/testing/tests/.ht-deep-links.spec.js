const { test, expect } = require('@playwright/test');

async function waitForContent(page) {
  await page.locator('#content').waitFor();
  await page.waitForFunction(() => {
    const frame = document.querySelector('#content');
    return frame?.contentDocument?.querySelector('section.collapsible-ready');
  });
}

async function targetPosition(page, id) {
  return page.evaluate(targetId => {
    const frame = document.querySelector('#content');
    const target = frame.contentDocument.getElementById(targetId);
    if (!target) return { exists: false };
    const frameRect = frame.getBoundingClientRect();
    const targetRect = target.getBoundingClientRect();
    return {
      exists: true,
      top: frameRect.top + targetRect.top,
      bottom: frameRect.top + targetRect.bottom,
      scrollY: window.scrollY,
      viewportHeight: window.innerHeight,
      expanded: target.closest('.collapsible-ready')
        ?.querySelector('.section-toggle')?.getAttribute('aria-expanded'),
    };
  }, id);
}

test('an initial section fragment expands and scrolls the dynamic content', async ({ page }) => {
  await page.goto('?path=Foundations%2FRecursion#problems');
  await waitForContent(page);
  await expect.poll(async () => (await targetPosition(page, 'problems')).scrollY).toBeGreaterThan(100);

  const position = await targetPosition(page, 'problems');
  expect(position.expanded).toBe('true');
  expect(position.top).toBeGreaterThanOrEqual(-2);
  expect(position.top).toBeLessThan(80);
});

test('top-level section list items receive linkable numbered fragments', async ({ page }) => {
  await page.goto('?book=algorithms&path=Foundations%2FRecursion#problems-10');
  await waitForContent(page);
  await expect.poll(async () => (await targetPosition(page, 'problems-10')).scrollY).toBeGreaterThan(1000);

  const position = await targetPosition(page, 'problems-10');
  expect(position.expanded).toBe('true');
  expect(position.bottom).toBeGreaterThan(0);
  expect(position.top).toBeLessThan(position.viewportHeight);
});

test('hash-only links in content update and scroll the parent route', async ({ page }) => {
  await page.goto('?book=algorithms&path=Foundations%2FRecursion#problems');
  await waitForContent(page);

  await page.locator('#content').contentFrame().locator('body').evaluate(body => {
    const link = document.createElement('a');
    link.href = '#overview';
    link.id = 'deep-link-test';
    link.textContent = 'Overview';
    body.appendChild(link);
    link.click();
  });

  await expect.poll(() => new URL(page.url()).hash).toBe('#overview');
  await expect.poll(async () => (await targetPosition(page, 'overview')).top).toBeLessThan(80);
});

test('cross-page content links preserve their fragment target', async ({ page }) => {
  await page.goto('?book=algorithms&path=Foundations%2FIntroduction');
  await waitForContent(page);

  await page.locator('#content').contentFrame().locator('body').evaluate(body => {
    const link = document.createElement('a');
    link.href = '?path=Foundations%2FRecursion#problems-10';
    link.textContent = 'Recursion homework 10';
    body.appendChild(link);
    link.click();
  });

  await expect.poll(() => new URL(page.url()).hash).toBe('#problems-10');
  await expect.poll(async () => (await targetPosition(page, 'problems-10')).expanded).toBe('true');
  const position = await targetPosition(page, 'problems-10');
  expect(position.bottom).toBeGreaterThan(0);
  expect(position.top).toBeLessThan(position.viewportHeight);
});
