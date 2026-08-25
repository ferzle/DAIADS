const { test, expect } = require('@playwright/test');

async function waitForDemo(page) {
  await page.locator('#content').waitFor();
  await page.waitForFunction(() => {
    const iframe = document.querySelector('#content');
    const frameDocument = iframe?.contentDocument;
    return frameDocument?.readyState === 'complete' && frameDocument?.body?.childElementCount;
  });
  return page.frameLocator('#content');
}

async function expectDialogAfter(page, action, expectedMessage) {
  const dialog = page.waitForEvent('dialog').then(async event => {
    const message = event.message();
    await event.accept();
    return message;
  });
  await action();
  await expect(dialog).resolves.toBe(expectedMessage);
}

test('BST announces an invalid empty value', async ({ page }) => {
  await page.goto('?path=Demos/Data%20Structures/BST%20Operations%20Demo');
  const demo = await waitForDemo(page);
  await demo.locator('#valueInput').fill('');
  await demo.locator('#searchBtn').press('Enter');
  await expect(demo.locator('#status')).toHaveText('Enter an integer value before choosing Search.');
  await expect(demo.locator('#status')).toHaveAttribute('role', 'status');
  await expect(demo.locator('#status')).toHaveAttribute('aria-live', 'polite');
});

test('Huffman reports an empty input through a dialog', async ({ page }) => {
  await page.goto('?path=Demos/Greedy/Huffman%20Encoding%20Demo');
  const demo = await waitForDemo(page);
  await demo.locator('#inputText').fill('');
  const dialog = page.waitForEvent('dialog').then(async event => {
    const message = event.message();
    await event.accept();
    return message;
  });
  await demo.getByRole('button', { name: 'Start', exact: true }).press('Enter');
  const message = await dialog;
  expect(message).toBe('Please enter a string.');
});

test('0-1 Knapsack rejects an invalid item count', async ({ page }) => {
  await page.goto('?path=Demos/Dynamic%20Programming/0-1%20Knapsack%20Demo');
  const demo = await waitForDemo(page);
  await demo.locator('#itemSizeInput').fill('0');
  const dialog = page.waitForEvent('dialog').then(async event => {
    const message = event.message();
    await event.accept();
    return message;
  });
  await demo.locator('#randomItemBtn').press('Enter');
  const message = await dialog;
  expect(message).toBe('Enter valid n (1–20)');
});

test('Binary Search rejects a custom array below its minimum size', async ({ page }) => {
  await page.goto('?path=Demos/Decrease-and-Conquer/Binary%20Search%20Demo');
  const demo = await waitForDemo(page);
  await demo.locator('#customArray').fill('1,2,3');
  await expectDialogAfter(page, () => demo.locator('#useCustom').press('Enter'), 'Custom array must have 8–20 numbers.');
});

test('Insertion Sort rejects a custom array below its minimum size', async ({ page }) => {
  await page.goto('?path=Demos/Decrease-and-Conquer/Insertion%20Sort%20Demo');
  const demo = await waitForDemo(page);
  await demo.locator('#customArray').fill('1,2,3');
  await expectDialogAfter(page, () => demo.locator('#useCustom').press('Enter'), 'Custom array must have 8-20 numbers, comma-separated.');
});

test('Hoare Partition rejects malformed custom input', async ({ page }) => {
  await page.goto('?path=Demos/Decrease-and-Conquer/Hoare%20Partition%20Demo');
  const demo = await waitForDemo(page);
  await demo.locator('#customArray').fill('not numbers');
  await expectDialogAfter(page, () => demo.locator('#useCustom').press('Enter'), 'Please enter at least 2 valid integers separated by commas.');
});

test('Subset Sum rejects an empty set and target', async ({ page }) => {
  await page.goto('?path=Demos/Exhaustive%20Search/Subset%20Sum%20Demo');
  const demo = await waitForDemo(page);
  await demo.locator('#inputSet').fill('');
  await demo.locator('#inputTarget').fill('');
  await expectDialogAfter(page, () => demo.locator('#startBtn').press('Enter'), 'Enter valid set & target');
});

test('Fibonacci rejects a value outside its allowed range', async ({ page }) => {
  await page.goto('?path=Demos/Brute%20Force/Fibonacci%20Naive%20Demo');
  const demo = await waitForDemo(page);
  await demo.locator('#inputN').fill('1');
  await expectDialogAfter(page, () => demo.locator('#startBtn').press('Enter'), 'Please enter an integer n with 2 ≤ n ≤ 8.');
});
