# Content Guide

## Purpose

DAIADS is intended to replace a traditional textbook for data structures and algorithms courses. Pages should teach the concept completely enough that students can use the site as their main reading source, with interactive demonstrations, code where it helps, and activities/exercises where students should do the implementation work themselves.

The site should not read like brief reference documentation. It should explain what problem is being solved, why the idea works, how the operation or algorithm behaves, what tradeoffs matter, and what students should be able to do after reading.

## Pedagogical Priorities

When writing or revising content, prioritize:

1. Conceptual clarity before implementation detail.
2. Worked examples and demos before dense formalism.
3. Precise operation/algorithm behavior before language-specific syntax.
4. Student activities and exercises where implementation practice is the learning goal.
5. Honest treatment of complexity and tradeoffs without burying the main idea in low-level machinery.

Implementation details should be included when they clarify the concept or are central to the course objective. They can be abbreviated, deferred to exercises, or omitted when they distract from the intended learning goal.

## Audience And Depth

Most data structures content targets a CS2 audience. Many algorithm pages target sophomore/junior algorithms courses, but some algorithm topics may also be used in CS2 contexts. When a topic plausibly serves both audiences, write the main explanation for the lower prerequisite level and move deeper analysis, implementation detail, or mathematical treatment into optional advanced sections.

Formal invariants and proofs are not a primary emphasis for most current content. Include them only when they substantially improve understanding or when the page is explicitly about correctness. They can be added later as advanced material.

## Algorithms Content

Algorithm pages should conform closely to the algorithm page template unless there is a strong reason to vary. A predictable structure helps students compare algorithms across techniques.

Algorithm pages should usually explain:

- The problem being solved.
- The design strategy or technique.
- The main invariant, choice, recurrence, or state representation.
- A small worked example.
- An interactive demo when available.
- Pseudocode or high-level steps.
- Time and space analysis, with enough justification for students to understand where the bounds come from.
- Implementation notes in Java, C++, and Python when implementation is a course objective.
- Activities/exercises that ask students to trace, modify, analyze, or implement the algorithm.

For complicated algorithms, it is acceptable to focus on the general idea and the algorithmic strategy instead of every implementation detail. For example, pages on Prim's and Kruskal's algorithms may discuss the role of priority queues, edge sorting, and disjoint sets without fully implementing every supporting data structure unless the page's goal is implementation practice.

## Data Structures Content

The data structure template is a good starting point, but data structures are less uniform than algorithms. Vary the page format when the structure, operations, or course goals call for it.

Data structures pages should usually emphasize:

- The abstract data type or conceptual model.
- The data stored and the relationships between elements.
- The core operations and what each operation is supposed to accomplish.
- Visual examples of state before and after operations.
- High-level implementation options and their tradeoffs.
- Complexity of core operations.
- Common edge cases and failure modes.
- Activities/exercises where students implement the details.

For current data structures work, prefer less finished implementation in the explanatory page and more student-facing implementation exercises. The page should make the operations and intended behavior clear, then give students structured opportunities to implement methods themselves.

## Implementation Code

When full implementations are included, they should be readable teaching code rather than overly clever production code. Prefer direct control flow and clear names.

Use idiomatic code for each language rather than forcing the Java, C++, and Python versions to be line-by-line translations. For C++, avoid relying on standard library data structures when the point is for students to understand or implement the structure themselves. Basic language and utility features are fine, but do not hide the target data structure inside `std::stack`, `std::queue`, `std::priority_queue`, `std::map`, or similar containers.

When implementation is intended as practice, provide downloadable starter/template code in Java, C++, and Python. Starter code should include:

- The overall data structure or algorithm class/function structure.
- Mostly empty methods for students to implement.
- Clear method contracts in comments.
- A small test method, test class, or driver.
- Enough scaffolding that students can run the file in a normal IDE.

Do not fill in the key logic when the point of the exercise is for students to implement it. The page may discuss high-level strategy and edge cases without giving away the full solution.

Do not include solutions for these implementation exercises unless the site policy changes. Pages may include hidden answer toggles for reading questions, but starter-code exercises should remain unsolved for now.

## Starter Code Organization

Starter code should live under `Content/Code/`. This directory is intentionally not part of the generated sidebar or sitemap.

Use a topic directory, then one directory per language:

```text
Content/Code/<topic-slug>/
  java/
  cpp/
  python/
  downloads/
```

Prefer URL-friendly lowercase slugs for code directories, such as `stacks`, `queues`, `linked-lists`, or `binary-search-trees`, even when the content page title contains spaces.

For a one-file starter, link directly to the source file:

```text
Content/Code/stacks/java/StackStarter.java
Content/Code/stacks/cpp/stack_starter.cpp
Content/Code/stacks/python/stack_starter.py
```

For multi-file starters, keep the source files in the language directory and provide a zip file in `downloads/`, preferably one zip per language:

```text
Content/Code/binary-search-trees/downloads/binary-search-trees-java-starter.zip
Content/Code/binary-search-trees/downloads/binary-search-trees-cpp-starter.zip
Content/Code/binary-search-trees/downloads/binary-search-trees-python-starter.zip
```

On the content page, use Java/C++/Python tabs similar to algorithm implementation sections. For short starters, it is fine to show the complete starter code inline and include a download link. For longer or multi-file starters, show the primary file or a compact excerpt inline, then link to the zip file.

## Activities And Exercises

Activities and exercises should be specific and action-oriented. Good prompts ask students to:

- Trace an algorithm or operation step by step.
- Predict the next state of a structure.
- Identify invariants or edge cases.
- Complete missing implementation methods.
- Compare two implementation choices.
- Analyze time or space complexity.
- Modify an algorithm for a variant problem.
- Design tests that expose boundary cases.

Prefer several focused exercises over one vague prompt. Include in-class activities for discussion, tracing, peer work, or whiteboard reasoning. Use homework problems for longer implementation and analysis tasks.

## Writing Style

Use clear, direct prose for students who are learning the material for the first time. Define terms before relying on them. Keep paragraphs short enough to scan, but do not reduce explanations to bullet-only notes.

Avoid assuming a detail is obvious just because it is standard in textbooks. If an operation has important edge cases, name them explicitly.

Use mathematical notation when it improves precision, but pair it with prose. Complexity claims should be justified, not only stated.

## Demo Guidance

Interactive demos should reinforce the page's learning objective. A good demo makes the current state, next operation, and reason for change visible.

For algorithm demos, highlight the active elements, decisions, swaps/updates, or subproblems. For data structure demos, show the state before and after operations and make edge cases easy to observe.

Demo text should explain the current step without overwhelming the visualization.

## Open Decisions To Preserve

Some course-policy details may vary by instructor or semester. When they are unknown, make conservative choices and leave room for adjustment:

- The exact operation set required for each data structure topic.
- How much starter code versus completed code to provide in non-exercise contexts.
- Whether non-implementation exercise solutions should be visible on-page, hidden behind toggles, or omitted.
- How formal proofs should be for each topic.
- Which implementation details are required for a particular offering of the course.
