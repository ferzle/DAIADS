# Content Guide

## Purpose

DAIADS is intended to replace a traditional textbook for data structures and algorithms courses. Pages should teach the concept completely enough that students can use the site as their main reading source, with interactive demonstrations, code where it helps, and activities/exercises where students should do the implementation work themselves.

The site should not read like brief reference documentation. It should explain what problem is being solved, why the idea works, how the operation or algorithm behaves, what tradeoffs matter, and what students should be able to do after reading.

Write pages as teaching material for students, not as outlines for instructors. A student who reads a page carefully should have enough explanation, examples, diagrams, and practice prompts to understand the topic without needing a separate textbook reading, except when the page intentionally links to another DAIADS page for prerequisite material.

## Source Material

When writing or revising pages, check `TrainingData/AIDMA-V3.5/` for relevant source material before drafting from scratch when the topic overlaps with discrete mathematics. This folder contains the Discrete Mathematics book and may be useful for topics such as logic, sets, functions, relations, proof techniques, induction, counting, recurrence relations, graphs, trees, and algorithm analysis foundations.

Use that material as course-aligned context rather than as text to paste wholesale. Adapt explanations, examples, terminology, and exercises to the DAIADS audience, page templates, and interactive-site style.

## Pedagogical Priorities

When writing or revising content, prioritize:

1. Conceptual clarity before implementation detail.
2. Worked examples and demos before dense formalism.
3. Precise operation/algorithm behavior before language-specific syntax.
4. Student activities and exercises where implementation practice is the learning goal.
5. Honest treatment of complexity and tradeoffs without burying the main idea in low-level machinery.

Implementation details should be included when they clarify the concept or are central to the course objective. They can be abbreviated, deferred to exercises, or omitted when they distract from the intended learning goal.

## Audience And Depth

Most data structures content targets undergraduate students in an introductory data structures course, often called CS2. Many algorithm pages target undergraduate students in an introduction to algorithms course, but some algorithm topics may also be used in CS2 contexts. When a topic plausibly serves both audiences, write the main explanation for the lower prerequisite level and move deeper analysis, implementation detail, or mathematical treatment into optional advanced sections.

Assume students are capable and motivated, but are seeing many of these ideas for the first time. Do not skip the connective tissue between definitions, examples, and conclusions. When a standard result depends on a subtle point, explain the point instead of relying on "it is clear" or a terse formula.

Formal invariants and proofs are not a primary emphasis for most current content. Include them only when they substantially improve understanding or when the page is explicitly about correctness. They can be added later as advanced material.

## Page Shape And Uniformity

Templates provide useful starting points, but page structure should serve the topic. Do not force every page into the same number of sections, the same number of examples, or lists with the same number of items. Uniformity is useful when it helps students compare related algorithms or data structures; it is harmful when it makes the writing feel mechanical or leaves important explanation out.

Prefer a natural teaching sequence:

1. Motivate the problem or idea.
2. Build intuition with a concrete example or visual model.
3. Introduce the formal vocabulary or notation once students have something to attach it to.
4. Work through at least one complete example carefully.
5. Summarize the key takeaways and give students something specific to practice.

This sequence can be adjusted. Some topics need more examples, some need more implementation discussion, and some need an early definition before examples make sense.

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

## Visual Explanations And Illustrations

Use visuals when they make an idea easier to understand. A good illustration should show structure, state change, comparison, or flow that would be awkward to communicate with prose alone.

SVG is often the best format for static instructional diagrams because it is crisp at any size, easy to edit, works well with labels and arrows, and can be styled consistently with the page. Use SVG for diagrams such as trees, graphs, arrays with highlighted ranges, recursion trees, stack frames, pointer structures, state transitions, and geometric layouts.

Tables are useful for exact comparisons and step-by-step traces, but do not use a table as a substitute for a diagram when spatial relationships matter. ASCII art should be used sparingly; it is rarely the clearest final presentation for students.

Every visual should have a clear instructional purpose. Avoid decorative illustrations that do not teach the concept. When a figure introduces notation or a state change, explain what students should notice before or after the figure.

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

The preferred style is polished textbook prose, not a terse checklist. Lists are useful for procedures, comparisons, summaries, and exercises, but they should not replace explanation. If a list item contains an idea students must understand deeply, consider expanding it into prose, a worked example, or a diagram.

Do not pad pages merely to make them longer. Add detail when it removes a likely point of confusion, connects two ideas, explains why a step is valid, or gives students a concrete example they can reason through.

Avoid formulaic uniformity in the prose. Repeated three-item lists, repeated section patterns, and identical sentence rhythms make the content feel generated rather than taught. Let the topic determine how much structure it needs.

Good explanations often include:

- A concrete example before or immediately after an abstract definition.
- A sentence explaining why the next step follows, not just what the next step is.
- A warning about a common misconception or edge case when students are likely to make that mistake.
- A short interpretation of a formula or complexity result in ordinary language.
- A transition that explains why the next section matters.

## Demo Guidance

Interactive demos should reinforce the page's learning objective. A good demo makes the current state, next operation, and reason for change visible.

For algorithm demos, highlight the active elements, decisions, swaps/updates, or subproblems. For data structure demos, show the state before and after operations and make edge cases easy to observe.

Demo text should explain the current step without overwhelming the visualization.

Demo pages have different writing and design needs than prose lessons. For demos, prioritize direct interaction, stable controls, readable state, and concise step text. The surrounding lesson page can carry the longer explanation.

## Open Decisions To Preserve

Some course-policy details may vary by instructor or semester. When they are unknown, make conservative choices and leave room for adjustment:

- The exact operation set required for each data structure topic.
- How much starter code versus completed code to provide in non-exercise contexts.
- Whether non-implementation exercise solutions should be visible on-page, hidden behind toggles, or omitted.
- How formal proofs should be for each topic.
- Which implementation details are required for a particular offering of the course.
