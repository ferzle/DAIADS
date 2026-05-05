# Project Context

## What This Site Is

DAIADS is a static algorithms and data structures learning site intended to replace a traditional course textbook. It combines textbook-style explanations, problem pages, technique overviews, implementation tabs, activities/exercises, downloadable starter code, and interactive demos.

The first screen is `index.html`. The actual content page is selected by a query parameter:

```text
/?path=Home%2FAbout
/?path=Algorithms%2FBrute%20Force%2FSelection%20Sort
/?path=Demos%2FGreedy%2FHuffman%20Encoding%20Demo
```

The route maps directly to an HTML file under `Content/` with `.html` appended.

## Runtime Architecture

1. `index.html` loads the site shell.
2. `scripts/loadContent.js` fetches `scripts/chapters.json`.
3. The sidebar menu is built from that JSON.
4. The selected page is loaded into the `#content` iframe from `Content/<path>.html`.
5. The parent page injects glossary tooltip behavior into the iframe.
6. The parent page rewrites internal `?path=` clicks to keep navigation in the shell.
7. Embedded demo iframes are cache-busted, resized, and observed for height changes.

Because of this flow, test through a local HTTP server rather than `file://`.

## Major JavaScript Responsibilities

`scripts/loadContent.js` handles:

- URL path normalization.
- history state and back/forward behavior.
- sidebar ordering and grouped/alphabetical menu modes.
- iframe loading and error display.
- glossary script injection.
- DRAFT banner display for pages with `DRAFT` in the filename.
- internal navigation from iframe pages.
- external link targeting.
- embedded demo cache-busting and resizing.
- collapsible sections based on `section-title`.
- section deep links via hash and `?section=...`.

`scripts/chapterScripts.js` handles:

- language tabs for Java, C++, and Python code blocks.
- show/hide answer blocks with `#toggleAnswers` and `#answers`.
- highlight.js initialization.

`scripts/demoScripts.js` handles:

- generic previous/next/play/speed demo controls.
- initial random/custom input generation hooks.
- step rendering through demo-provided functions.

## Content Types

For authoring style and pedagogy, use `CONTENT_GUIDE.md`.

Algorithm pages usually include:

- Problem solved.
- Design and strategy.
- Embedded demo.
- Java/C++/Python implementation tabs when full implementation is a learning objective.
- Time/space analysis.
- Variations or improvements.
- Links and resources.
- Reading questions and answers.
- In-class activities.
- Homework problems.

Problem pages usually include:

- Problem description.
- Input and output.
- Motivation/applications.
- Examples.
- Common algorithms/techniques.
- Variants/related problems.

Technique pages usually include:

- Introduction.
- High-level pattern or pseudocode when useful.
- Worked examples.
- Algorithms using the technique.
- When to use.
- Limitations.
- Implementation tips.
- Pitfalls.
- Applications.
- Summary and exercises.

Demo pages should be self-contained and should also work when embedded in a lesson page.

Data structure pages should currently lean toward conceptual explanation and guided practice. They should make the structure, operations, expected behavior, edge cases, and complexity clear, then use starter code and exercises for much of the implementation work.

Algorithm pages should stay relatively close to the algorithm template. Data structure pages may vary more because the structures and operation sets are less uniform.

## Adding A New Page

1. Choose the correct folder under `Content/`.
2. Start from a matching file in `templates/` or a nearby existing page.
3. Fix relative paths to scripts and CSS based on folder depth.
4. Use stable section IDs and `section-title` attributes for collapsible content.
5. Use `?path=` links for internal site links.
6. Run `python3 scripts/create_JSON.py` if the page should appear in the menu.
7. Test the route through `index.html`.

When adding or revising data structures content, decide whether the page should include full implementation code or starter code for student completion. If the learning goal is implementation practice, prefer starter files in Java, C++, and Python with empty methods and a runnable test/driver.

Starter code belongs under `Content/Code/<topic-slug>/<language>/`. `Content/Code` is excluded from generated navigation and sitemap output, so pages must link to starter files or zip downloads explicitly.

## Adding A New Demo

1. Put the demo under `Content/Demos/<Technique or Category>/`.
2. Include `scripts/demoScripts.js` only if the generic playback controls fit.
3. Use `body class="no-tooltips"`.
4. Implement deterministic step generation separate from rendering where practical.
5. Keep controls and visualization dimensions stable so iframe resizing is predictable.
6. Embed it from the relevant lesson page with `class="embeddedDemo"`.
7. Test standalone and embedded behavior.

## Known Project Quirks

- The worktree may already contain many modified and deleted files. Treat them as user-owned unless you made them.
- `scripts/create_JSON.py` has hardcoded absolute paths and a placeholder `SITE_ROOT`.
- Many files use spaces and punctuation in names; quote paths.
- Some templates have relative paths that are only placeholders for their expected final depth.
- The `OLD/` tree is ignored by menu generation and may be deleted or absent in the working tree.
- External dependencies are loaded from CDNs, including MathJax, highlight.js, Google Fonts, and Google Analytics.
- The `.vscode/launch.json` target assumes `http://localhost:8080`.

## Useful Routes For Smoke Testing

```text
http://localhost:8080/
http://localhost:8080/?path=Home%2FAbout
http://localhost:8080/?path=Algorithms%2FBrute%20Force%2FSelection%20Sort
http://localhost:8080/?path=Demos%2FBrute%20Force%2FSelection%20Sort%20Demo
http://localhost:8080/?path=More%2FGlossary
```
