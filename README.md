# DAIADS

Design, Analysis, and Implementation of Algorithms and Data Structures.

This is a static educational site with textbook-style algorithm/data structure content and interactive demos. The site shell is `index.html`; content pages live under `Content/` and are loaded into an iframe based on the `?path=` URL parameter.

The goal is to replace a traditional data structures and algorithms textbook with complete explanations, demonstrations, implementations where useful, and structured activities/exercises.

## Run Locally

```bash
python3 internal/testing/.ht-server.py
```

Then open:

```text
http://127.0.0.1:8099/DAIADS/
```

## Regenerate Navigation

After adding, removing, or renaming HTML files under `Content/`, regenerate the menu data and sitemap:

```bash
python3 scripts/create_JSON.py
```

## Context For Agents

- `AGENTS.md`: a discovery bootstrap for AI/coding agents.
- `internal/agent-docs/`: protected `.ht-*.md` working instructions,
  architecture context, content guidance, and internal handoff notes.

## Accessibility Tests

The tracked Playwright test project is isolated under `internal/testing/`.
Its source files use Apache's protected `.ht-*` naming convention. Dependencies
and generated artifacts are placed under the operating system's temporary
directory, outside the web root.

```bash
python3 internal/testing/.ht-run-tests.py
```
