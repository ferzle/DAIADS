# Agent Instructions

## Project Summary

DAIADS is a static educational website for "Design, Analysis, and Implementation of Algorithms and Data Structures." It is intended to replace a traditional textbook for data structures and algorithms courses, with complete concept explanations, problem descriptions, data structure pages, implementations where useful, student activities/exercises, and interactive demonstrations.

There is no package manager or build step. The app is loaded by `index.html`, which fetches `scripts/chapters.json`, builds the sidebar menu, and loads files from `Content/` into an iframe based on the `?path=` URL parameter.

For content-writing decisions, read `CONTENT_GUIDE.md` before drafting or revising pages.

## How To Run Locally

Run a static server from the repository root:

```bash
python3 -m http.server 8080
```

Then open:

```text
http://localhost:8080/
```

Do not rely on opening `index.html` directly from the filesystem; the site depends on `fetch()` and iframe loading.

## Important Commands

Regenerate the menu data and sitemap after adding, removing, or renaming HTML files under `Content/`:

```bash
python3 scripts/create_JSON.py
```

The generator currently uses hardcoded paths for `/home/cusack/public_html/DAIADS` and a `SITE_ROOT` placeholder in `scripts/create_JSON.py`. Verify those before treating the generated sitemap as production-ready.

## Repository Map

- `index.html`: top-level shell, header, sidebar container, iframe, scroll controls.
- `Content/`: all site content loaded by menu paths.
- `Content/Algorithms/`: algorithm lesson pages.
- `Content/Demos/`: interactive demo pages.
- `Content/Problems/`: problem definition pages.
- `Content/Data Structures/`: data structure lesson pages.
- `Content/Techniques/`: technique overview pages.
- `Content/Code/`: downloadable starter/template code; intentionally excluded from menu and sitemap generation.
- `Content/Home/` and `Content/More/`: static informational pages.
- `scripts/loadContent.js`: routing, menu construction, iframe loading/resizing, collapsible sections, deep links, draft banner, embedded demo handling.
- `scripts/chapterScripts.js`: code tabs, answer toggles, highlight.js initialization for lesson pages.
- `scripts/demoScripts.js`: generic playback controls used by many demos.
- `scripts/create_JSON.py`: scans `Content/` to write `scripts/chapters.json` and `scripts/sitemap.xml`.
- `scripts/glossary-tooltips.js` and `scripts/glossary-data.json`: glossary tooltip system injected into iframe content.
- `css/style.css`: global shell and shared styles.
- `css/chapter.css`: lesson/content page styles.
- `css/demo.css`: demo page styles.
- `templates/`: starting points for new algorithm, demo, problem, technique, and data structure pages.
- `CONTENT_GUIDE.md`: pedagogical and authoring guidance for writing textbook-replacement content.
- `PDFS/`: course/textbook PDF assets.

## Content And Routing Conventions

Menu links use URL paths like:

```html
<a href="?path=Algorithms%2FBrute%20Force%2FSelection%20Sort">Selection Sort</a>
```

That maps to:

```text
Content/Algorithms/Brute Force/Selection Sort.html
```

Inside content pages, prefer `href="?path=..."` links for internal navigation. `scripts/loadContent.js` intercepts those links in the iframe and routes through the parent page.

File and directory names often contain spaces, apostrophes, and parentheses. Quote paths in shell commands.

## Page Authoring Conventions

Pages should teach, not merely summarize. Prefer conceptual clarity, worked examples, visible operations, and specific student tasks over terse reference-style descriptions.

For complicated algorithms, include enough implementation discussion to support the learning goal, but do not force every low-level data-structure detail into the main explanation if the intended point is the high-level strategy. For example, Prim's and Kruskal's pages may mention priority queues, edge sorting, and disjoint sets without fully developing every support structure unless that page is explicitly about implementation.

For data structures content, currently favor the idea of the structure, its operations, behavior, high-level implementation options, complexity, and exercises over full finished implementations in the prose page. Provide starter/template code when implementation practice is the goal.

Data structure pages may vary more than algorithm pages. Algorithm pages should stay closer to the algorithm template unless a topic genuinely needs a different structure.

Algorithm and technique pages should use top-level sections with stable `id` values and `section-title` attributes:

```html
<section id="analysis" section-title="Time/Space Analysis">
  <h2>Time/Space Analysis</h2>
  ...
</section>
```

The loader turns those sections into collapsible panels. The default-open sections are `problem-solved` and `design-and-strategy`; otherwise the first top-level section opens by default.

Most lesson pages include:

- highlight.js CSS/JS from CDN.
- MathJax from CDN when math notation is needed.
- `scripts/chapterScripts.js`.
- `css/style.css` and `css/chapter.css`.
- The Google Analytics tag.

Use the existing templates as the starting point, but adjust relative paths based on nesting depth.

## Demo Conventions

Demo pages normally include:

- `scripts/demoScripts.js`.
- `css/style.css` and `css/demo.css`.
- `body class="no-tooltips"` to disable glossary wrapping.
- Optional controls with IDs `prev`, `next`, `play`, `speed`, `generate`, and `useCustom`.

When using the generic demo playback script, expose these functions on `window`:

- `window.onGenerate(isCustom)`: returns the input data for a run.
- `window.genSteps(arr)`: returns the recorded step list.
- `window.renderStep(steps, idx, original)`: renders the selected step.
- `window.setupAux(original, length)`: optional setup hook.

Embedded demos in lesson pages should use:

```html
<iframe class="embeddedDemo" src="../../../Content/Demos/.../Name Demo.html" allow="fullscreen"></iframe>
```

The parent loader cache-busts and auto-resizes embedded demos.

## Exercise And Starter Code Conventions

When implementation is intended as student practice, provide downloadable starter/template code in Java, C++, and Python rather than only completed snippets. Starter code should include:

- A runnable class/function scaffold.
- Mostly empty methods for students to implement.
- Clear method contracts and edge-case expectations.
- A small test method, test class, or driver.

Keep the explanatory page focused on what operations should do, how the structure changes, and what cases students must handle.

Place starter code under `Content/Code/<topic-slug>/<language>/`, using lowercase URL-friendly topic slugs. Link directly to one-file starters. For multi-file starters, keep source files in the language directory and provide per-language zip files under `Content/Code/<topic-slug>/downloads/`.

Use idiomatic Java, C++, and Python. For C++, avoid standard library data structures that hide the structure being taught. Do not provide solutions for implementation exercises unless the site policy changes.

## Menu Generation Rules

`scripts/create_JSON.py` scans `Content/` for `.html` files.

- Directories named `old`, `images`, `figures`, or `code` are ignored case-insensitively.
- Files with `DRAFT` in the filename are collected under `More/DRAFTS`.
- The live sidebar order is controlled by constants in `scripts/loadContent.js`.
- `scripts/chapters.json` is generated data; regenerate it after content structure changes instead of hand-editing when possible.

## Glossary Tooltips

The parent loader injects `scripts/glossary-tooltips.js` into loaded content pages. Terms come from `scripts/glossary-data.json`. Demos opt out with `body class="no-tooltips"`.

The tooltip script skips code, preformatted text, headings, links, buttons, inputs, SVG, and other sensitive elements. Be careful when adding markup around glossary-heavy prose.

## Working Safely

This repository may have a large dirty worktree. Do not revert or clean up existing changes unless explicitly asked. Keep edits scoped to the requested files.

Before changing generated navigation files, inspect the current diff if needed and avoid overwriting unrelated user work. If a content addition requires regenerating `scripts/chapters.json`, mention that in the final response.

Do not remove analytics tags, CDN includes, or relative path prefixes as incidental cleanup.

## Validation Checklist

For content-only edits:

- Load `http://localhost:8080/?path=...` for the changed page.
- Check internal `?path=` links.
- Check collapsible section behavior if sections changed.
- Check MathJax/code tabs if math or code blocks changed.

For demo edits:

- Load the standalone demo page.
- Load the lesson page that embeds it.
- Check random input, custom input, previous/next, play/pause, speed, and fullscreen when present.
- Check iframe height after expanding/collapsing the demo section.

For navigation changes:

- Run `python3 scripts/create_JSON.py`.
- Confirm the new route appears in `scripts/chapters.json`.
- Load the route through `index.html` using `?path=...`.
