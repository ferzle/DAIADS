# DAIADS

Design, Analysis, and Implementation of Algorithms and Data Structures.

This is a static educational site with textbook-style algorithm/data structure content and interactive demos. The site shell is `index.html`; content pages live under `Content/` and are loaded into an iframe based on the `?path=` URL parameter.

The goal is to replace a traditional data structures and algorithms textbook with complete explanations, demonstrations, implementations where useful, and structured activities/exercises.

## Run Locally

```bash
python3 -m http.server 8080
```

Then open:

```text
http://localhost:8080/
```

## Regenerate Navigation

After adding, removing, or renaming HTML files under `Content/`, regenerate the menu data and sitemap:

```bash
python3 scripts/create_JSON.py
```

## Context For Agents

- `AGENTS.md`: working instructions and conventions for AI/coding agents.
- `PROJECT_CONTEXT.md`: project architecture, content types, and validation notes.
- `CONTENT_GUIDE.md`: pedagogical guidance for writing and revising course content.
- `agent.md`: lowercase pointer for tools or people looking for that filename.
