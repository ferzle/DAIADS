const { defineConfig } = require('@playwright/test');
const os = require('node:os');
const path = require('node:path');

const artifactsDir = path.join(os.tmpdir(), 'daiads-testing', 'artifacts');

module.exports = defineConfig({
  testDir: './tests',
  testMatch: '.ht-*.spec.js',
  outputDir: path.join(artifactsDir, 'test-results'),
  timeout: 45_000,
  workers: 4,

  use: {
    baseURL: 'http://127.0.0.1:8099/DAIADS/',
    viewport: { width: 1280, height: 900 },
  },

  webServer: {
    command: 'python3 .ht-server.py',
    cwd: __dirname,
    url: 'http://127.0.0.1:8099/DAIADS/',
    reuseExistingServer: true,
  },

  reporter: [
    ['list'],
    ['html', {
      open: 'never',
      outputFolder: path.join(artifactsDir, 'playwright-report'),
    }],
  ],
});
