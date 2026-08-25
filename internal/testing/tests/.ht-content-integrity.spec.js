const fs = require('node:fs');
const path = require('node:path');
const { test, expect } = require('@playwright/test');

const repositoryRoot = path.join(__dirname, '../../..');
const contentRoot = path.join(repositoryRoot, 'Content');
const chapters = JSON.parse(
  fs.readFileSync(path.join(repositoryRoot, 'scripts/chapters.json'), 'utf8')
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

function walkHtml(directory, files = []) {
  for (const entry of fs.readdirSync(directory, { withFileTypes: true })) {
    const entryPath = path.join(directory, entry.name);
    if (entry.isDirectory()) {
      if (!['old', 'images', 'figures', 'code'].includes(entry.name.toLowerCase())) {
        walkHtml(entryPath, files);
      }
    } else if (entry.isFile() && entry.name.toLowerCase().endsWith('.html')) {
      files.push(entryPath);
    }
  }
  return files;
}

function idsIn(html) {
  return new Set(
    [...html.matchAll(/\bid\s*=\s*(["'])(.*?)\1/gi)].map(match => match[2])
  );
}

function decodeReference(value) {
  try {
    return decodeURIComponent(value);
  } catch {
    return value;
  }
}

const routes = new Set(collectRoutes(chapters));
const htmlFiles = walkHtml(contentRoot);

test('every local route, fragment, asset, iframe, stylesheet, script, and download resolves', () => {
  const failures = [];

  for (const file of htmlFiles) {
    const html = fs.readFileSync(file, 'utf8');
    const sourceIds = idsIn(html);
    const references = [...html.matchAll(
      /\b(?:href|src)\s*=\s*(["'])(.*?)\1/gi
    )].map(match => match[2].trim());

    for (const reference of references) {
      if (
        !reference ||
        /^(?:https?:|data:|mailto:|tel:|javascript:|\/\/)/i.test(reference)
      ) continue;

      if (reference.startsWith('#')) {
        const fragment = decodeReference(reference.slice(1));
        if (fragment && !sourceIds.has(fragment)) {
          failures.push({ file, reference, reason: 'missing same-document fragment' });
        }
        continue;
      }

      const queryIndex = reference.indexOf('?');
      if (reference.startsWith('?') || queryIndex !== -1) {
        const query = reference.slice(queryIndex === -1 ? 1 : queryIndex + 1).split('#')[0];
        const route = new URLSearchParams(query).get('path');
        if (route && !routes.has(route.replace(/\.html$/i, ''))) {
          failures.push({ file, reference, reason: 'unknown DAIADS route' });
        }
        if (reference.startsWith('?')) continue;
      }

      const withoutQuery = reference.split('?')[0];
      const [rawPath, rawFragment = ''] = withoutQuery.split('#');
      if (!rawPath) continue;

      const decodedPath = decodeReference(rawPath);
      let target;
      if (decodedPath.startsWith('/DAIADS/')) {
        target = path.join(repositoryRoot, decodedPath.slice('/DAIADS/'.length));
      } else if (decodedPath === '/DAIADS') {
        target = repositoryRoot;
      } else if (decodedPath.startsWith('/')) {
        continue;
      } else {
        target = path.resolve(path.dirname(file), decodedPath);
      }

      if (!target.startsWith(repositoryRoot + path.sep) && target !== repositoryRoot) {
        failures.push({ file, reference, reason: 'reference escapes repository' });
        continue;
      }
      if (!fs.existsSync(target)) {
        failures.push({ file, reference, target, reason: 'missing local target' });
        continue;
      }

      const fragment = decodeReference(rawFragment);
      if (fragment && fs.statSync(target).isFile() && target.toLowerCase().endsWith('.html')) {
        const targetIds = idsIn(fs.readFileSync(target, 'utf8'));
        if (!targetIds.has(fragment)) {
          failures.push({ file, reference, target, reason: 'missing target fragment' });
        }
      }
    }
  }

  expect(failures, JSON.stringify(failures, null, 2)).toEqual([]);
});

test('chapters.json and live Content HTML remain in one-to-one correspondence', () => {
  const fileRoutes = new Set(
    htmlFiles.map(file => path.relative(contentRoot, file).replace(/\.html$/i, '').split(path.sep).join('/'))
  );
  expect(
    {
      filesMissingFromMenu: [...fileRoutes].filter(route => !routes.has(route)).sort(),
      menuRoutesMissingFiles: [...routes].filter(route => !fileRoutes.has(route)).sort(),
    }
  ).toEqual({ filesMissingFromMenu: [], menuRoutesMissingFiles: [] });
});
