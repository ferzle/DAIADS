# DAIADS Content Audit TODO

Audit date: 2026-09-28

## Scope and approach

This audit covers all 219 active HTML files under `Content/` that are included by
the site's content scanner. It excludes `Content/Code/`, image/figure folders,
and archived `old/` material. The reviewed set contains:

| Page type | Files |
|---|---:|
| Algorithms | 39 |
| Analysis | 8 |
| Appendix | 7 |
| Data Structures | 23 |
| Demos | 85 |
| Foundations | 4 |
| More | 5 |
| Problems | 32 |
| Start Here | 2 |
| Techniques | 14 |
| **Total** | **219** |

The review treated the files as several different page types rather than applying
one template to everything:

- Foundations, Analysis, and Appendix pages are textbook chapters or prerequisite
  references.
- Data Structure pages emphasize an ADT/model, state changes, implementation
  choices, operation costs, edge cases, and practice.
- Technique pages explain a reusable design pattern and connect multiple examples.
- Algorithm pages are expected to stay comparatively close to the algorithm
  template: problem, strategy, worked example/demo, implementation or pseudocode,
  justified analysis, and practice.
- Problem pages are short reference/contract pages. They do not need to become
  full algorithm chapters, but their input/output assumptions and links to actual
  solution pages should be precise.
- Demo pages are interaction-first. The important criteria are a clear state,
  meaningful step narration, robust controls, input validation, and accessible
  alternatives to visual-only information.
- Start Here, More, and overview pages are navigation or project-reference pages.

Priorities below mean:

- **P0**: incomplete content, incorrect or ambiguous core material, invalid sample
  code, or malformed HTML that can alter the rendered structure.
- **P1**: a meaningful teaching, navigation, or accessibility gap.
- **P2**: polish, consistency, metadata, naming, or maintainability.

This is an editorial/content audit, not a formal proof of every mathematical claim
or a compilation test of every displayed program. All local `href`/`src` targets,
fragments, menu routes, and files were checked separately; none are currently
missing. External links were not tested for availability.

## Cross-cutting work

### Repair malformed document structure

These files have unmatched opening/closing tags in their current source. Some are
extra closing tags; others unintentionally nest later sections inside an earlier
section. Correct these before doing substantial copy edits because the browser's
error recovery can hide the source problem while changing collapsible-section
behavior.

- [x] **P0** `Content/Algorithms/Brute Force/Matrix Multiplication.html` — remove
  the extra `</section>` after Homework Problems.
- [x] **P0** `Content/Algorithms/Decrease-and-Conquer/Hoare Partition.html` —
  remove the duplicate closing `</body>` and `</html>` pair.
- [x] **P0** `Content/Algorithms/Divide-and-Conquer/Matrix Multiplication.html` —
  close Design and Strategy at the intended boundary; it currently has one more
  opening than closing section tag.
- [x] **P0** `Content/Algorithms/Exhaustive Search/Depth-First Search.html` — close
  the outer Design and Strategy section at the intended boundary.
- [x] **P0** `Content/Algorithms/Exhaustive Search/Topological Sort (DFS) DRAFT.html`
  — remove the duplicate closing `</html>`.
- [x] **P0** `Content/Algorithms/Greedy/Prims.html` — remove the stray section close
  between the design illustration and Interactive Demo.
- [x] **P0** `Content/Data Structures/Linear Structures/Stacks.html` — close the
  unmatched `div` and verify that the remainder of the page is not swallowed by it.
- [x] **P0** `Content/Problems/Graphs/All-Pairs Shortest Path.html` — close the
  Examples section.
- [x] **P0** `Content/Problems/Graphs/Maximum Flow.html` — add the missing opening
  section around Problem Description.
- [x] **P0** `Content/Problems/Optimization/Minimum Coin Change.html` — add the
  missing `<body>` and remove the extra `</section>` after Examples.
- [x] **P0** `Content/Techniques/Dynamic Programming.html` — repair the extra
  section close around the nested Fibonacci examples/summary and then verify the
  top-level collapse structure.
- [x] **P0** `Content/Techniques/Exhaustive Search.html` — close the unmatched `div`.
- [x] **P0** `Content/Demos/Brute Force/Matrix Multiplication Demo.html` — remove
  the duplicate closing body/html pair.
- [x] **P0** `Content/Demos/Divide-and-Conquer/Matrix Multiplcation (Inplace) Demo.html`
  — remove the duplicate closing body/html pair.
### Normalize code tabs

Several older pages mark every tab and/or every panel active on initial load. The
scripts partly mask this visually, but the source exposes contradictory tab semantics
to assistive technology. In each tab group, only the initial tab should have
`aria-selected="true"` and only its panel should be active/visible.

- [x] **P1** `Content/Algorithms/Brute Force/Bubble Sort.html`
- [x] **P1** `Content/Algorithms/Brute Force/Selection Sort.html`
- [x] **P1** `Content/Algorithms/Brute Force/Sequential Search.html`
- [x] **P1** `Content/Algorithms/Decrease-and-Conquer/Hoare Partition.html`
- [x] **P1** `Content/Algorithms/Decrease-and-Conquer/Insertion Sort.html`
- [x] **P1** `Content/Algorithms/Divide-and-Conquer/Merge Sort.html`
- [x] **P1** `Content/Algorithms/Divide-and-Conquer/Quicksort.html`
- [x] **P1** `Content/Algorithms/Greedy/Merge.html` — normalize both independent tab
  groups.
- [x] **P1** `Content/Algorithms/Space-Time Tradeoff/Bucket Sort.html`
- [x] **P1** `Content/Algorithms/Space-Time Tradeoff/Counting Sort.html`
- [x] **P1** `Content/Algorithms/Space-Time Tradeoff/Radix Sort.html`
- [x] **P1** `Content/Algorithms/Transform-and-Conquer/Horner's Rule.html`
- [x] **P1** `Content/Data Structures/Linear Structures/Representing Linear Structures.html`
  — keep one selected tab per language group, not one for the whole page.

### Make the reference pages connect back into the textbook

All 31 individual Problem pages name relevant algorithms and techniques as plain
text, but almost none link back to the DAIADS pages that teach them. Add internal
links in each page's Common Algorithms/Techniques and Variants sections. This is the
largest navigation/content-discovery gap in an otherwise connected site.

Do the same selectively in Data Structure and Appendix chapters: many “Related
Links” sections link only to external sites even when a directly relevant DAIADS page
exists. Internal links should come first; external sources should remain supplemental.

### Establish a demo accessibility/content baseline

Apply this baseline to all 85 demo pages, then use the page-specific demo items below
for exceptions and integration work:

- Every visible input has a persistent `<label>` or explicit accessible name in the
  source, rather than depending only on the routing script's fallback label.
- The current operation/result is exposed through a concise `aria-live` status.
- SVG/canvas/tree/graph/matrix state has a text equivalent or a sufficiently complete
  narrated status; color is not the only carrier of meaning.
- Invalid custom input explains the accepted range/format and does not silently
  replace the user's data.
- Previous, Next, Play/Pause, speed, Generate, and custom-input controls have the same
  lifecycle and disabled-state behavior wherever those concepts apply.
- Keyboard focus remains visible and does not jump unexpectedly when a step rerenders.

Thirteen demos also omit the project convention `body class="no-tooltips"`; add it to:

- [x] **P2** `Content/Demos/Brute Force/Matrix Multiplication Demo.html`
- [x] **P2** `Content/Demos/Brute Force/String Matching Demo.html`
- [x] **P2** `Content/Demos/Decrease-and-Conquer/Quickselect Demo.html`
- [x] **P2** `Content/Demos/Divide-and-Conquer/Matrix Multiplcation (Inplace) Demo.html`
- [x] **P2** `Content/Demos/Divide-and-Conquer/Matrix Multiplication Demo.html`
- [x] **P2** `Content/Demos/Divide-and-Conquer/Strassens Demo.html`
- [x] **P2** `Content/Demos/Dynamic Programming/Fibonacci Best Demo.html`
- [x] **P2** `Content/Demos/Dynamic Programming/Floyd's Demo.html`
- [x] **P2** `Content/Demos/Dynamic Programming/Warshall's Demo.html`
- [x] **P2** `Content/Demos/Space-Time Tradeoff/Boyer-Moore Demo.html`
- [x] **P2** `Content/Demos/Space-Time Tradeoff/Horspool Precomputation Demo.html`
- [x] **P2** `Content/Demos/Transform-and-Conquer/Fibonacci Number (Matrix) Demo.html`
- [x] **P2** `Content/Demos/Transform-and-Conquer/Horners Rule Demo.html`

### Mechanical consistency pass

- [ ] **P2** Add a viewport meta tag to the 146 legacy pages that omit it. This is
  most visible when a content file or standalone demo is opened outside the shell.
- [ ] **P2** Standardize algorithm section names (`Problem Solved`, `Design and
  Strategy`, `Implementation in Java, C++, Python`, `Time/Space Analysis`, and
  `Homework Problems`) where a different name does not convey a real distinction.
- [x] **P2** Add `rel="noopener"` consistently to external `target="_blank"` links.
- [ ] **P2** Run a final spelling/name pass, especially for “Quickselect,” “Quicksort,”
  “Strassen's,” “Horner's,” and “matrix multiplication.”

### Strengthen the weakest learning-task sets

The task sections vary more in quality than their presence/length initially suggests.
Use pages such as `Content/Foundations/Recursion.html`,
`Content/Analysis/Algorithm Analysis Examples.html`,
`Content/Algorithms/Decrease-and-Conquer/Quickselect.html`, and the newer data-structure chapters
as the benchmark. A strong set should include:

- reading questions that move from comprehension to prediction, tracing, invariant or
  state reasoning, analysis, and edge cases;
- complete answers that model the expected reasoning instead of saying to check a demo;
- in-class activities with a supplied input, roles or grouping when relevant, an
  observable deliverable, and a realistic scope for class time;
- homework that is specific enough to grade, progresses from trace/analysis to
  modification or implementation, and does not merely duplicate the activities; and
- explicit alignment with the exact algorithm variant and terminology taught on the page.

The following pages need the most attention in this dimension.

#### `Content/Algorithms/Brute Force/Bubble Sort.html`

- [ ] **P1** Replace some of the true/false and multiple-choice reading questions with
  a concrete pass trace, an invariant/prediction question, an exact comparison/swap
  count, and a stability question.
- [ ] **P1** Remove or carefully justify the answer that classifies Bubble Sort as both
  brute force and decrease-and-conquer. It introduces taxonomy ambiguity without the
  prose first establishing that interpretation.
- [ ] **P1** Give the card/comparison activities fixed inputs and recording tables so
  groups compare the same evidence rather than only “discussing performance.”

#### `Content/Algorithms/Brute Force/Selection Sort.html`

- [ ] **P0** Align the questions and answers with the algorithm actually taught. The
  implementation selects the maximum and grows a sorted suffix, but the supplied trace
  selects minima and grows a sorted prefix while the preceding answer says the right
  portion is sorted.
- [ ] **P1** Replace “go over,” “which is better,” and “discuss optimizations” with
  specified arrays, required comparison/swap counts, a stability experiment using
  tagged duplicate keys, and a comparison table against Bubble and Insertion Sort.
- [ ] **P1** State the expected deliverable and cost model for the linked-list and
  dual-ended homework; otherwise the questions admit several incomparable answers.

#### `Content/Algorithms/Brute Force/Sequential Search.html`

- [ ] **P1** Give the in-class tasks concrete arrays/targets and require a comparison
  log, first-match/all-match result, or short decision rule for choosing sequential
  versus binary search. Four of the five current prompts are broad discussion prompts.
- [ ] **P1** Add homework on boundary tests, generic equality/comparator behavior, and
  an evidence-based cost comparison; the current set mostly asks for closely related
  reimplementations of the same scan.

#### `Content/Algorithms/Brute Force/Matrix Multiplication.html`

- [ ] **P1** Expand the reading questions beyond definition recall: ask students to
  trace one output cell, derive the exact scalar-operation counts for rectangular
  dimensions, identify the loop invariant, and predict the effect of loop ordering.
- [ ] **P1** Replace or move homework that is only general matrix programming (addition,
  identity testing, column norms). Use that space for multiplication-specific testing,
  dimension validation, operation counting, sparse/diagonal cases, and comparison with
  the divide-and-conquer page.

#### `Content/Algorithms/Brute Force/Polynomial Evaluation.html`

- [ ] **P1** Strengthen the reading set with a complete coefficient/value trace and an
  exact multiplication count. The activities and homework are substantially stronger
  than the mostly definition/complexity recall questions.
- [ ] **P1** Remove duplicate derivative exercises or distinguish their learning goals;
  use the recovered space for a direct brute-force/repeated-power/Horner comparison.

#### `Content/Algorithms/Decrease-and-Conquer/Hoare Partition.html`

- [ ] **P0** Rebuild all three task sections only after choosing the exact partition
  contract. The current answers repeat the disputed claims that the pivot necessarily
  reaches its final position and that each element is examined exactly once.
- [ ] **P1** Replace “go over a few examples” with one shared trace table containing
  `i`, `j`, pivot, comparison, swap, and array-state columns; include duplicates and
  all-equal input as required cases.
- [ ] **P1** Supply an explicit Lomuto implementation/contract before asking students to
  compare the schemes, and define what they should measure.

#### `Content/Algorithms/Decrease-and-Conquer/Insertion Sort.html`

- [ ] **P1** Expand the three short activities into a prefix-invariant trace, a tagged-
  duplicate stability test, and a measured comparison of sorted, reverse, and nearly
  sorted inputs.
- [ ] **P1** Add scaffolding and test requirements to the four homework prompts. Include
  boundary cases, exact shift/comparison counting, and a question explaining why binary
  search reduces comparisons but not the quadratic number of movements.
- [ ] **P1** Change “Does insertion sort swap elements?” to refer explicitly to the shown
  shift-based implementation; swap-based insertion-sort variants exist.

#### `Content/Algorithms/Divide-and-Conquer/Merge Sort.html`

- [ ] **P1** Add an intermediate homework tier. The current set moves quickly from a
  basic trace to genuinely difficult in-place and parallel implementations. Include a
  recurrence derivation, merge invariant, tagged-duplicate stability test, and tests for
  empty/odd-sized inputs before those advanced options.
- [ ] **P2** Specify what counts as a comparison and a move in the counting exercise so
  student results are comparable.

#### `Content/Algorithms/Divide-and-Conquer/Quicksort.html`

- [ ] **P0** Replace “Check your answer by comparing with the demo” with the complete
  trace for the exact partition rule used on the page.
- [ ] **P1** Replace the first six vague activities with two or three specified tasks:
  compare pivot rules on the same inputs, trace duplicate-heavy data, derive best/worst
  recurrences, and record maximum recursion depth.
- [ ] **P1** Qualify the reading answer that sorted input causes the worst case: that is
  true for particular deterministic pivot rules, not for Quicksort unconditionally.
- [ ] **P1** Add smaller correctness and edge-case homework before the million-element
  benchmark and label Nuts and Bolts/performance engineering as advanced.

#### `Content/Algorithms/Greedy/Merge.html`

- [ ] **P1** Rebuild the very small task set (three reading questions, three activities,
  and two homework prompts). Add a pointer/state trace, merge invariant, exact best/worst
  comparison counts, tagged-duplicate stability, exhaustion edge cases, and tests for
  empty inputs.
- [ ] **P1** Do not make a difficult `O(1)`-extra-space merge design one of only two
  homework options; add scaffolded core exercises and label that problem advanced.

#### `Content/Algorithms/Space-Time Tradeoff/Boyer-Moore_DRAFT.html`

- [ ] **P0** Replace the placeholder-like five-question/five-activity/five-problem lists
  with tasks tied to one fully specified pattern/text example. Students should construct
  both preprocessing tables, predict every shift, compare with Horspool, and explain
  which rule wins at each mismatch.

#### `Content/Algorithms/Space-Time Tradeoff/Bucket Sort.html`

- [ ] **P1** Replace the non-answer “Look at the demo or perform this by hand” with the
  actual bucket assignments and final arrays, or change the prompt to a self-check task
  without a Show Answers entry.
- [ ] **P2** Give the exploratory activities a shared dataset and results table for
  `k`, bucket occupancy, internal comparisons, and space. This will turn several broad
  “discuss/compare” prompts into an evidence-based comparison.

#### `Content/Algorithms/Transform-and-Conquer/Binary Exponentiation.html`

- [ ] **P1** Edit the unusually long reading section for progression and remove repeated
  binary-product exercises. Retain one derivation, one LTR trace, one RTL trace, one
  operation-count question, and edge cases; move additional numerical practice to
  homework.

#### `Content/Techniques/Greedy Algorithms.html`

- [ ] **P1** Rewrite the first activity so students test a named candidate greedy rule
  on a supplied instance and produce either a counterexample or an exchange argument;
  “convince yourself” is not an assessable outcome.
- [ ] **P1** Reduce duplication between activities and homework (minimum stabbing points
  and rope merging currently appear in both) and use the space for a greedy-choice
  identification exercise and a compare-with-DP exercise.
- [ ] **P1** Replace the claim that greedy algorithms “typically” run in `O(n)` or
  `O(n log n)` with the more accurate point that runtime depends on candidate generation,
  ordering, feasibility checks, and the supporting data structure.

#### `Content/Techniques/Transform-and-Conquer.html`

- [ ] **P1** Give the comparison/brainstorm activities explicit outputs: a cost table,
  transformed representation, pseudocode, and a break-even inequality where applicable.
- [ ] **P1** Either provide prerequisites/scaffolding for Gaussian elimination with
  partial pivoting or replace it; it is a large numerical-analysis jump from the page's
  taught examples.
- [x] **P2** Correct `heapfify`, `comparision`, `comparisions`, and `multipliation` in the
  question/activity/homework text.

#### `Content/Data Structures/Trees/2-3 Trees.html`

- [ ] **P1** Curate the exceptionally large activity/homework bank into Core, Additional
  Practice, and Advanced/Implementation groups. Remove near-duplicate traces and make a
  short required path visible; abundance currently makes assignment selection harder.

#### `Content/Data Structures/Priority Queues and Heaps/Priority Queues.html`

- [ ] **P1** Similarly identify a core subset of homework before the multiple
  implementation projects. Ensure a student can practice ADT behavior, compare
  representations, and analyze costs without completing every starter-code exercise.

## Other improvement areas

The exercise-quality pass exposed several broader curriculum and presentation issues.
These should be addressed selectively: the goal is to make dependencies, contracts,
and page roles clear, not to force every chapter into identical headings.

### Make the learning sequence and page roles explicit

- [ ] **P1** Add a short “Before you begin” or prerequisite line to advanced pages
  whose explanations assume material elsewhere in the book. The highest-value cases
  are BFS/DFS/topological sorting (graph representations), Prim/Kruskal (graphs plus
  priority queues or disjoint sets), Floyd/Warshall (graph matrices and dynamic
  programming), AVL/2-3 Trees (BST invariants), and the divide-and-conquer algorithms
  that immediately use recurrence analysis. Link the prerequisite rather than
  re-teaching it.
- [ ] **P1** Give each unit overview a usable route through the material: what is core,
  what may be read independently, and what should come next. Only six non-demo pages
  currently signal prerequisites explicitly, so menu order is doing too much hidden
  curricular work.
- [ ] **P1** Define the different jobs of `Content/Foundations/Basic Analysis.html` and
  `Content/Analysis/Algorithm Analysis Fundamentals.html`. They currently cover much
  of the same ground at substantial length. Make the former an explicitly concise
  on-ramp and the latter the deeper course chapter, cross-link them, and remove or
  consolidate duplicated explanations and exercises.
- [ ] **P1** Add “this page's role” and continuation links where one topic is spread
  across several page types: the Fibonacci problem/brute-force/DP/matrix materials;
  brute-force, divide-and-conquer, and Strassen matrix multiplication; Merge versus
  Merge Sort; the exponentiation problem, technique examples, and Binary
  Exponentiation; and the Heapsort gateway versus the heap-construction chapter.
  Readers should be able to tell whether a page is a problem contract, a technique
  example, a full algorithm lesson, or an optional extension.
- [ ] **P1** Add internal previous/next or prerequisite links to the data-structure
  chapters, not just external Related Links. Fifteen of the 23 data-structure pages
  currently contain no route to another DAIADS page, including most of the linear,
  tree, and set/map chapters, despite forming deliberate sequences in the menu.

### Standardize mathematical and algorithm contracts

- [ ] **P0** `Content/Algorithms/Decrease-and-Conquer/Quickselect.html` — replace the
  statement that its supplied code can use “Hoare partition, Lomuto partition, or any
  other” partition. The code requires a routine that returns a pivot's final index;
  classic Hoare partition does not provide that contract. Also rewrite the precondition
  in terms of the zero-based target `k - 1`, not the currently confusing
  `lo <= k <= hi`, and keep rank versus index explicit at the API boundary.
- [ ] **P1** Establish a small site-wide notation/conventions reference and link it
  where needed: 0-based indices versus 1-based ranks, inclusive versus half-open
  subarray bounds, `n`/`m`/`k` meanings, graph symbols `V`/`E`, and whether a space
  bound includes output storage or means auxiliary space. Individual pages may choose
  different conventions, but each algorithm/demo/problem trio must declare and share
  one contract.
- [ ] **P1** Audit complexity statements for bound strength and assumptions. Use
  `Theta` for tight bounds when established, reserve `O` for upper bounds, state the
  input distribution behind average-case claims, and distinguish expected time from
  average time. Pay particular attention to Quicksort/Quickselect, hashing, Bucket
  Sort, and graph algorithms whose bound depends on the representation.
- [ ] **P2** Adopt canonical display names and aliases across titles, menu labels,
  glossary variants, demos, and prose: `Quickselect`, `Quicksort`, `QuickHull`, and a
  consistent possessive policy for Prim, Kruskal, Floyd, Warshall, Strassen, Hoare,
  and Horner. Preserve common aliases for search, but stop presenting spelling
  variation as if it identifies a different algorithm.

### Treat prose, demos, figures, and code as one lesson

- [ ] **P1** For every embedded demo, state the exact variant it implements, the
  initial/default input, and one or two things the student should observe. Then verify
  that its step vocabulary, tie/duplicate behavior, indexing, and output match the
  surrounding worked example and code. Start with the partition/Quickselect/Quicksort
  cluster, both matrix divide-and-conquer demos, directed versus undirected DFS, and
  the left-to-right versus right-to-left Binary Exponentiation demos.
- [ ] **P1** Give the 18 currently unnamed instructional SVGs an accessible purpose:
  use `aria-labelledby` with a concise title/description when the graphic teaches
  something, or mark it hidden when nearby text completely duplicates it. The affected
  pages are Hoare Partition, both draft Topological Sort pages, QuickHull, BFS, DFS,
  N-Queens, and the Divide-and-Conquer technique chapter. Use the newer named
  data-structure SVGs as the implementation model.
- [ ] **P1** Do not make color the only vocabulary for graph state. In particular,
  the DFS lesson explicitly maps white/gray/black states to blue/orange/green and calls
  those colors central to understanding the traversal. Add persistent textual state,
  patterns/shapes, or labels to the static diagrams and demos and describe the
  semantic states first.
- [ ] **P2** Add “what to notice” captions to dense traces and diagrams. A label such
  as “Dataset B” identifies an input but does not explain the instructional point;
  captions should call out the invariant, boundary, repeated subproblem, or state
  transition the figure is meant to reveal.

### Make code examples teachable and verifiable

- [ ] **P1** Add language-neutral pseudocode or a precise numbered algorithm before
  the language tabs on code-first pages that currently provide neither. The clearest
  cases are brute-force Fibonacci, Selection Sort, Hoare Partition, Insertion Sort,
  both Topological Sort drafts, Merge Sort, Quicksort, Subset Sum, Prim's algorithm,
  Bucket Sort, and the Boyer-Moore draft. Students should be able to reason about the
  algorithm without first translating Java, C++, or Python syntax.
- [ ] **P1** Build a repeatable compile/syntax-check pass for extractable Java, C++,
  and Python samples. The invalid C++ Insertion Sort declaration and stray character
  in Merge show that visual review is insufficient. Mark intentionally partial
  fragments as fragments so the check does not encourage fake scaffolding.
- [ ] **P1** Check semantic parity across each language tab: same preconditions,
  mutation behavior, return value, duplicate/tie handling, and asymptotic strategy.
  Add a tiny shared example and boundary-case test set (empty, singleton, duplicate,
  and invalid input where relevant) rather than allowing three implementations to
  drift independently.
- [ ] **P1** Put input contracts next to code, not only in prose several sections
  earlier. State whether arrays may be empty, matrices may be ragged, graphs may be
  disconnected, keys may repeat, arithmetic may overflow, and whether invalid input
  is rejected or assumed absent.

### Improve assessment usability, not just assessment quantity

- [ ] **P1** Apply `Core`, `Additional Practice`, and `Advanced/Project` labels to
  unusually large activity/homework banks. Use the core set to cover trace,
  correctness/invariant, complexity, and one edge case; optional breadth should not
  obscure what every reader is expected to learn.
- [ ] **P1** Distinguish formative self-checks from assignable work. Reading-question
  answers should be complete and explanatory; prompts intended for grading should
  state the deliverable, assumptions, and cost model and should not reveal their full
  solution through a nearby demo.
- [ ] **P2** Add rough scope markers where assignments range from a five-minute trace
  to a multi-class implementation project. A simple `Short`, `Standard`, or `Project`
  label is more useful than making instructors infer scale from prose.

### Audit the glossary as instructional content

- [ ] **P0** Repair `scripts/glossary-data.json` before relying on its definitions in
  page-wide tooltips. `Parent` is incorrectly listed as a variant of `Pointer`, and
  Approximation Algorithm, Bellman-Ford Algorithm, Hamiltonian Path, and Weighted Graph
  have duplicate canonical/variant entries. The Bubble Sort definition also contains
  malformed complexity markup (`O(n^2>)`) and copy errors. Merge genuine duplicates
  and add a uniqueness check for normalized variants.
- [ ] **P1** Review all 321 glossary definitions against the corresponding chapters,
  especially terms with multiple valid conventions such as partition, height, path,
  average case, in-place, stability, and space complexity. A tooltip must not silently
  contradict the page it appears on.
- [ ] **P1** Make the Glossary page navigable at its current size: add an alphabet jump
  list and term/alias search, suppress empty letter sections, and let definitions link
  to the primary DAIADS lesson where one exists.
- [ ] **P1** Replace or verify time-sensitive factual examples in otherwise durable
  chapters. For example, `Algorithm Analysis Fundamentals.html` names a supposedly
  current fastest supercomputer “as of June 2026” (and says “in the words”). A timeless
  machine-speed comparison would teach the same point without becoming stale.

## Page-specific TODOs

### Start Here and More

#### `Content/Start Here/About.html`

- [ ] **P1** Make the Status section name the known incomplete areas explicitly:
  the Graphs data-structure chapter, three draft algorithm pages, and the intentionally
  uneven problem-to-algorithm coverage. The current wording makes it difficult for a
  student to know which readings are course-ready.

#### `Content/Start Here/How To Use.html`

- [ ] **P1** Qualify the statement that the Intro Data Structures path “covers ...
  graphs” until the graph chapter is written, or point students to the discrete-math
  PDF as the temporary graph prerequisite.

#### `Content/More/AI Resources.html`

- [ ] **P1** Treat this as a dated, curated bibliography: add a “last reviewed” date
  and review the time-sensitive descriptions in Latest News and Platforms.
- [ ] **P2** Change the document title from `Links` to `AI Resources`; correct
  “Safetey,” “Stepehn Wolfram,” “YoutTube,” and the grammar in the LLM-video entry.
- [ ] **P2** Add a short selection/scope note so this page reads as an intentional
  resource list rather than miscellaneous bookmarks.

#### `Content/More/Books.html`

- [ ] **P2** Correct “A revision if Version 4.0” to “A revision of Version 4.0” and
  tighten the version descriptions so the recommended/current edition is immediately
  obvious.

#### `Content/More/Links.html`

- [ ] **P2** Add one-sentence annotations and a last-checked date for external links;
  this will make the page useful as a guided resource rather than a link dump.

### Foundations, Analysis, and Appendix

#### `Content/Analysis/Overview.html`

- [ ] **P1** Turn What This Chapter Covers into an actual chapter map with internal
  links to notation, properties, proofs, growth rates, iterative examples, recursive
  analysis, and recurrence prerequisites.

#### `Content/Appendix/Mathematical Background.html`

- [ ] **P1** Add the prerequisites promised by the Analysis overview but absent here:
  common summations (arithmetic/geometric), floor and ceiling facts, inequalities, and
  a short explanation of the proof/induction tools used later.
- [ ] **P1** Add at least one worked algorithm-analysis example using each major tool,
  rather than leaving the page mostly as a formula sheet.
- [ ] **P2** State the hypotheses for l'Hopital's Rule more carefully or clearly label
  the displayed version as an informal course-use rule.

Apart from the cross-cutting role, notation, and freshness items above, the remaining
Foundations, Analysis, and recurrence pages are among the strongest material in the
repository: they contain sustained explanations, worked examples, practice, and clear
audience scaffolding. No broad page-specific rewrite is recommended for them in this
pass.

### Data Structures

#### `Content/Data Structures/Graphs/Graphs.html`

- [ ] **P0** Replace the 30-word placeholder with a complete graph data-structure
  chapter: directed/undirected and weighted/unweighted models; vertices, edges,
  degree, paths and connectivity; adjacency list/matrix/edge-list representations;
  operation costs; mutation edge cases; a visual example; and exercises.
- [ ] **P1** Connect the chapter to BFS, DFS, topological sort, shortest paths, spanning
  trees, and the graph Problem pages. Keep the PDF link as optional background, not as
  a replacement for the site's primary reading.

#### `Content/Data Structures/Overview.html`

- [ ] **P1** Link every row to the corresponding chapter. Hash Table, Balanced BST,
  Priority Queue, and Graph are currently plain text even though pages exist (or, for
  Graphs, are planned).
- [ ] **P1** Qualify operation costs that depend on representation details: linked-list
  tail insertion/deletion needs the appropriate tail/predecessor support, dynamic-array
  end insertion is amortized, and queue/priority-queue costs depend on implementation.
- [ ] **P2** Add the missing doctype, character encoding, and viewport metadata; move
  the page-specific table CSS into an appropriate shared class when convenient.

#### `Content/Data Structures/Sets, Maps, and Hash Tables/Introduction.html`

- [ ] **P1** End the unit overview with a linked reading path to Sets, Maps, Hash
  Tables, Separate Chaining, and Open Addressing. The prose explains the sequence well,
  but does not let the reader follow it directly.

#### `Content/Data Structures/Trees/2-3 Trees.html`

- [ ] **P2** Do an editorial compression pass. At roughly 20,000 words it is much
  longer than neighboring chapters; keep the detailed split/merge cases, but move
  repeated setup, optional advanced material, or very long implementation scaffolds
  behind clearly labeled advanced subsections.

The newer linear-structure, heap, hashing, and tree chapters otherwise set the best
current model for future data-structure work: they distinguish ADT from representation,
show state changes, discuss costs and pitfalls, and provide structured exercises.

### Techniques

#### `Content/Techniques/Introduction.html`

- [ ] **P1** Link every named technique to its DAIADS page and distinguish techniques
  actually covered in this edition from advanced topics that are only mentioned.
- [ ] **P1** Rework the markup so the page title is outside the first top-level section;
  the current outer section contains all later sections and does not match the site's
  normal collapse structure.
- [x] **P2** Correct “but be too prohbitive,” and standardize “QuickHull.”

#### `Content/Techniques/Decrease-and-Conquer/Introduction.html`

- [x] **P0** Fix the broken MathJax delimiter in `\(c)` and the sentence “doing a bit
  work.”
- [ ] **P1** Remove Binary Search from Variable-Size-Decrease examples (it is already,
  correctly, listed under decrease-by-a-constant-factor) or explain a genuinely
  different variant; the current classification contradicts itself on the same page.
- [ ] **P1** Add one small recurrence/size-sequence example for each of the three forms
  so this functions as an introduction rather than only an index.

#### `Content/Techniques/Backtracking.html`

- [x] **P2** Correct “Exhuastive Search” and call exhaustive search a technique rather
  than an algorithm in the comparison text.

#### `Content/Techniques/Dynamic Programming.html`

- [ ] **P1** After repairing the section nesting, distinguish the early example summary
  from the final Summary & Key Takeaways section; two generic Summary headings obscure
  the page hierarchy.

#### `Content/Techniques/Exhaustive Search.html`

- [ ] **P1** After closing the unmatched `div`, verify the N-Queens example, demos, and
  later sections at narrow widths; malformed containment can currently make that content
  appear correctly only because of browser repair.

### Algorithms

#### `Content/Algorithms/Transform-and-Conquer/Heapsort.html`

- [ ] **P0** Decide whether this is a real algorithm chapter or a redirect/overview.
  At 56 words it is the clearest non-graph content stub. Prefer a full algorithm-shaped
  page, or make it an explicit concise gateway to the already strong Heap Construction
  and Heapsort chapter without pretending to be a complete lesson.
- [ ] **P2** Correct `unfamilair` to `unfamiliar` and add the viewport
  metadata. Verify the cross-page `#heapsort-example` deep link after consolidation.

#### `Content/Algorithms/Space-Time Tradeoff/Boyer-Moore_DRAFT.html`

- [ ] **P0** Finish or keep out of student-facing course paths. Remove UNDER
  CONSTRUCTION only after expanding the 18-word analysis, validating the good-suffix
  preprocessing/code, and adding a complete worked search trace.
- [ ] **P1** Explain the relationship among full Boyer-Moore, the existing Horspool
  chapter, and the two demos so students do not conflate the heuristics.
- [ ] **P1** Add Variations/Improvements and Related Links sections or document why this
  page intentionally departs from the algorithm template.

#### `Content/Algorithms/Decrease-and-Conquer/Topological Sort DRAFT.html`

- [ ] **P1** Perform a publication pass: confirm deterministic tie-handling language,
  make cycle detection/output behavior explicit, link directly to the DFS alternative,
  and then either remove `DRAFT` or list the remaining blocker.

#### `Content/Algorithms/Exhaustive Search/Topological Sort (DFS) DRAFT.html`

- [x] **P0** Remove the duplicate closing HTML tag.
- [ ] **P1** Perform the same publication pass as the source-removal page and add a
  direct side-by-side comparison: state maintained, cycle detection, output order, and
  shared `O(V+E)` bound.

#### `Content/Algorithms/Decrease-and-Conquer/Hoare Partition.html`

- [ ] **P0** Name the exact partition contract. The described code swaps the first
  pivot into its final index; classic Hoare partition normally returns a boundary and
  does not promise that contract. Either rename this as the chosen pivot-placing
  Hoare-style variant or teach the classic scheme and show how its return value is used.
- [ ] **P0** Recheck duplicate-key scans, bounds/sentinels, loop termination, and the
  claim that every element is examined “exactly once.” Align the problem page, prose,
  code, static SVG, demo, and answers around the same variant.
- [ ] **P1** Replace “kind of exemplifies” and the placement-based decrease-and-conquer
  rationale with a precise explanation: partition itself is a linear subroutine; the
  surrounding Quickselect/Quicksort recurrence determines the broader design technique.

#### `Content/Problems/Foundational/Array Partition.html`

- [ ] **P0** Coordinate this contract with the Hoare page. It currently requires the
  pivot to finish in its sorted position, then lists Hoare's scheme as if every standard
  Hoare implementation provides that result.
- [ ] **P1** State one consistent duplicate policy (`<`/`>=`, `<=`/`>`, or three-way)
  for the primary problem and move other valid contracts into Variants.

#### `Content/Algorithms/Decrease-and-Conquer/Insertion Sort.html`

- [x] **P0** Fix the displayed C++ declaration `void insertionSort(int []A, int n)`;
  use valid C++ array/pointer or container syntax.
- [ ] **P1** Add visible headings for Design and Strategy and Implementation, plus a
  complete static trace that states the sorted-prefix invariant.
- [ ] **P1** Discuss stability, adaptiveness, and comparison versus movement counts;
  qualify the blanket claim that it outperforms Bubble Sort on nearly sorted data.

#### `Content/Algorithms/Greedy/Merge.html`

- [x] **P0** Remove the stray `S` after `C[k++] = A[i++];` in the displayed C++ code.
- [ ] **P1** Reconsider the Greedy classification. If Merge remains here, explicitly
  explain the local-choice interpretation and that Merge is normally taught as the
  combine subroutine of divide-and-conquer Merge Sort, not as a standalone greedy
  optimization algorithm.
- [ ] **P1** Do not call the auxiliary-array implementation “in-place”; it allocates
  `L` and `R`. Rename it to “single-array interface” or provide a genuinely in-place
  variant and explain its tradeoffs.

#### `Content/Algorithms/Brute Force/Sequential Search.html`

- [ ] **P1** Expand the thinnest non-stub algorithm lesson with a short complete trace,
  an explicit duplicate policy (“return first match”), empty-input behavior, and a
  comparison table against binary search.
- [ ] **P2** Add useful internal/external related links; the current resources section
  is only five words.

#### `Content/Algorithms/Brute Force/Selection Sort.html`

- [ ] **P1** Add a complete worked pass-by-pass example and explicitly discuss
  in-place behavior, non-adaptiveness, and when the shown variant is or is not stable.
- [x] **P2** Correct “the number of comparisons the algorithms does.”

#### `Content/Algorithms/Brute Force/Bubble Sort.html`

- [ ] **P1** Add a compact static trace and distinguish the baseline algorithm from the
  early-exit optimization when giving best-case complexity.
- [ ] **P1** State stability and clarify what becomes fixed after each pass for the
  exact left-to-right variant shown.

#### `Content/Algorithms/Brute Force/Fibonacci Numbers.html`

- [ ] **P1** Add an explicit small recursion tree with repeated subproblems highlighted;
  the demo helps, but the central reason for exponential work should remain visible in
  the prose page.
- [ ] **P2** Note numeric overflow/large-integer limits separately from algorithmic
  complexity in the three language examples.

#### `Content/Algorithms/Brute Force/Matrix Multiplication.html`

- [ ] **P1** Add one fully worked small matrix product and expand the very short analysis
  to distinguish rectangular `m x n` by `n x p` cost from square-matrix shorthand.
- [ ] **P1** State how implementations handle empty, ragged, or incompatible matrices,
  or clearly document that those are preconditions.

#### `Content/Algorithms/Brute Force/Polynomial Evaluation.html`

- [ ] **P1** Show the quadratic method's repeated power work in a small trace, then
  contrast it explicitly with linear repeated-power accumulation and Horner's Rule.
- [ ] **P1** Embed or more prominently introduce the separate Linear Demo; it is
  currently discoverable only through an activity link.

#### `Content/Algorithms/Divide-and-Conquer/Matrix Multiplication.html`

- [ ] **P1** After fixing the unclosed Design section, add a concise roadmap at the top.
  The 5,000+ word page mixes derivation, pseudocode, two demos, and implementation
  details; students need to know which parts are core versus optional implementation
  depth.

#### `Content/Algorithms/Exhaustive Search/Depth-First Search.html`

- [ ] **P1** After repairing section nesting, separate the core DFS lesson from the
  advanced timestamp/edge-classification material so a CS2 reader can stop at a clear
  core endpoint.

#### `Content/Algorithms/Greedy/Prims.html`

- [ ] **P1** After removing the stray section close, label the long graph traces and
  exercises as core versus extended practice. Preserve the good implementation and
  cumulative complexity explanation.

#### `Content/Algorithms/Space-Time Tradeoff/Counting Sort.html`

- [ ] **P1** Add a Variations/Improvements section that explicitly compares stable
  output-array counting sort, the simpler count-and-rewrite form, negative-key support,
  and the failure mode when the key range is much larger than `n`.

### Problem reference pages

For every entry below, the common task is: link each named DAIADS algorithm/technique
to its lesson and add a “Learn this solution” path back into the textbook. Keep these
pages concise; the goal is a precise contract and useful routing, not duplicating the
algorithm chapters.

#### Foundational problems

- [ ] **P1** `Content/Problems/Foundational/Exponentiation.html` — define `0^0`,
  overflow/number-domain expectations, and link the naïve and binary-exponentiation
  lessons/demos.
- [ ] **P1** `Content/Problems/Foundational/GCD.html` — define the `gcd(0,0)` convention
  and link the Euclidean demo/technique example.
- [ ] **P1** `Content/Problems/Foundational/K-th Order Statistic.html` — specify how
  duplicate values affect rank, keep 1-based `k` consistent, and link Quickselect.
- [ ] **P1** `Content/Problems/Foundational/Matrix Multiplication.html` — make dimension
  compatibility a named precondition and link all three matrix-multiplication chapters.
- [ ] **P1** `Content/Problems/Foundational/Merge.html` — state stability/tie behavior
  and link Merge and Merge Sort.
- [ ] **P1** `Content/Problems/Foundational/Polynomial Evaluation.html` — link the brute
  force and Horner pages and keep coefficient ordering prominent.
- [ ] **P1** `Content/Problems/Foundational/Searching.html` — specify first match versus
  any match when duplicates occur and link Sequential and Binary Search.
- [ ] **P1** `Content/Problems/Foundational/Sorting.html` — define whether stability and
  in-place behavior are requirements or comparison dimensions, then link the many
  existing sorting lessons.

`Array Partition.html` has its own P0 item in the Algorithms section above.

#### Geometry problems

- [ ] **P1** `Content/Problems/Geometry/Closest Pair.html` — define duplicate-point
  behavior and link from the named divide-and-conquer technique even though a dedicated
  algorithm page does not yet exist.
- [ ] **P1** `Content/Problems/Geometry/Convex Hull.html` — state the policy for collinear
  boundary points and duplicate points; link QuickHull and the otherwise isolated
  brute-force Convex Hull demo.

#### Graph problems

- [ ] **P1** `Content/Problems/Graphs/All-Pairs Shortest Path.html` — after closing the
  Examples section, define diagonal/unreachable/negative-cycle output conventions and
  link Floyd's.
- [ ] **P1** `Content/Problems/Graphs/Graph Traversal.html` — say whether the task covers
  only the start vertex's reachable component or an entire disconnected graph; link BFS
  and DFS.
- [ ] **P1** `Content/Problems/Graphs/Maximum Flow.html` — after restoring the missing
  opening section, define parallel edges and reverse/residual capacity conventions.
- [ ] **P1** `Content/Problems/Graphs/Minimum Spanning Tree.html` — state behavior for a
  disconnected graph (minimum spanning forest versus invalid input) and link Prim/Kruskal.
- [ ] **P1** `Content/Problems/Graphs/Single-Source Shortest Path.html` — make the
  negative-edge/negative-cycle contract explicit and distinguish distance output from
  path reconstruction.
- [ ] **P1** `Content/Problems/Graphs/Spanning Tree.html` — correct the Problem List's
  “Greedy” classification; ordinary spanning trees can be produced by DFS/BFS, while
  greedy methods are relevant to minimum spanning trees.
- [ ] **P1** `Content/Problems/Graphs/Topological Sort.html` — state the cyclic-graph
  result and link both source-removal and DFS draft lessons.
- [ ] **P1** `Content/Problems/Graphs/Transitive Closure.html` — define whether closure
  is reflexive by default and link Warshall's.

#### Optimization problems

- [ ] **P1** `Content/Problems/Optimization/0-1 Knapsack.html` — state integer/nonnegative
  assumptions behind the pseudopolynomial `O(nW)` bound and link both exhaustive-search
  and dynamic-programming lessons.
- [ ] **P1** `Content/Problems/Optimization/Chain Matrix Multiplication.html` — clarify
  that the matrices are compatible in the given order and distinguish scalar-operation
  cost from actually multiplying them.
- [ ] **P1** `Content/Problems/Optimization/Edit Distance.html` — state operation costs
  (currently implicitly unit cost) and whether a script or only the distance is output.
- [ ] **P1** `Content/Problems/Optimization/Fractional Knapsack.html` — state assumptions
  about positive weights/fractions and link the Greedy lesson.
- [ ] **P1** `Content/Problems/Optimization/Interval Scheduling.html` — define endpoint
  compatibility (`finish <= start` versus strict separation) and link the Greedy lesson.
- [ ] **P1** `Content/Problems/Optimization/Minimum Coin Change.html` — after repairing
  the body/section markup, explicitly state unlimited coin multiplicity and nonnegative
  target assumptions; link the Dynamic Programming technique example if no dedicated
  algorithm page is planned.

#### Other problems

- [ ] **P1** `Content/Problems/Other/Fibonacci.html` — link each substantial method to
  its page/demo and distinguish mathematical integer output from fixed-width machine
  arithmetic.
- [ ] **P1** `Content/Problems/Other/N-Queens.html` — correct “Exhuastive,” specify whether
  one solution, all solutions, or the count is requested, and link exhaustive/backtracking
  lessons.
- [ ] **P1** `Content/Problems/Other/Optimal Character Encoding.html` — state tie-breaking
  expectations and whether codewords or only cost are output; link Huffman.
- [ ] **P1** `Content/Problems/Other/String Matching.html` — define empty-pattern and
  overlapping-match behavior; link brute force, Horspool, and Boyer-Moore.
- [ ] **P1** `Content/Problems/Other/Subset Sum.html` — reconcile “set” with an array that
  may contain duplicates, retain the nonnegative-integer restriction prominently, and
  link exhaustive/backtracking/DP material.
- [ ] **P1** `Content/Problems/Other/Travelling Salesman.html` — state directed versus
  undirected, complete versus missing-edge, and exact versus approximate output
  assumptions.

#### `Content/Problems/Problem List.html`

- [ ] **P0** Correct the overview taxonomy: “Nth Order Statistic” should match the
  K-th Order Statistic page; ordinary Spanning Tree is not inherently Greedy; Minimum
  Coin Change is not generally solved optimally by greedy choice; and the Exponentiation
  row should match the site's transform/decrease/divide terminology.
- [ ] **P1** Add an availability/status column or visual distinction between problems
  with complete DAIADS algorithm lessons, problems with demos/examples only, and topics
  not yet covered. This would turn the table into an honest coverage map.

### Standalone demos needing page-specific decisions

#### `Content/Demos/Brute Force/Convex Hull.html`

- [ ] **P1** Embed or link this from the Convex Hull problem page and explain the
  brute-force criterion it visualizes. It is currently a menu-level demo without a
  surrounding lesson.

#### `Content/Demos/Brute Force/Polynomial Evaluation (Linear) Demo.html`

- [ ] **P1** Integrate this next to the quadratic demo on the brute-force Polynomial
  Evaluation page so students can compare operation counts directly.

#### `Content/Demos/Data Structures/Red-Black Tree Operations Demo.html`

- [ ] **P1** Either embed it in Balanced Search Trees with enough red-black invariants
  to interpret the colors/rotations, create a red-black-tree chapter, or mark it as an
  optional preview. At present it has no teaching page.

#### `Content/Demos/Randomized/BogoSort Demo.html`

- [ ] **P2** Give this novelty demo a short surrounding explanation of randomized
  algorithms, expected runtime, and strict input-size limits, or move it out of the
  main educational navigation.

#### `Content/Demos/Transform-and-Conquer/Fibonacci Number (Matrix) Demo.html`

- [ ] **P1** Link/embed it from the Fibonacci problem page or Binary Exponentiation
  variations and explain why matrix exponentiation is transform-and-conquer.
- [x] **P2** Correct “Matrix-Multiplcation” in the visible heading.

#### Demo filename/title cleanup

- [ ] **P2** Rename `Matrix Multiplcation (Inplace) Demo.html`, `Strassens Demo.html`,
  and `Horners Rule Demo.html` to correctly spelled titles. Update all iframe references
  and regenerate `scripts/chapters.json`/the sitemap in the same change.
- [x] **P2** `Content/Demos/Divide-and-Conquer/Exponentiation Demo.html` — correct
  “multiplcation” in the step narration.

## Coverage gaps exposed by the page audit

These are not defects in a single existing file, but they explain why several Problem
pages and standalone demos currently feel disconnected. Decide deliberately whether to
add these lessons or mark the topics as reference-only; do not leave their status
implicit.

- [ ] **P1** Add a real graph-data-structures prerequisite before expanding graph
  algorithms. This is the dependency for BFS/DFS, topological sorting, MST, shortest
  paths, and maximum flow.
- [ ] **P1** Add dedicated algorithm lessons (or clearly scoped technique examples) for
  Euclid's GCD algorithm, closest pair, single-source shortest path, and maximum flow.
  Their Problem/demo coverage currently has no matching algorithm chapter.
- [ ] **P1** Decide the Dynamic Programming sequence beyond 0-1 Knapsack, Floyd, and
  Warshall. Chain matrix multiplication, edit distance, minimum coin change, and the DP
  form of subset sum all have Problem pages but no dedicated algorithm lesson.
- [ ] **P2** Decide whether Travelling Salesman belongs only in the problem catalog or
  should receive exact, dynamic-programming, and approximation coverage.
- [ ] **P2** Decide whether Red-Black Trees warrant a chapter. Until then, present the
  existing operations demo as an explicitly optional preview rather than peer material
  beside the fully developed AVL and 2-3 tree chapters.

## Suggested implementation order

1. Fix malformed HTML and the two invalid displayed code samples.
2. Resolve the Hoare/partition/Quickselect contracts, glossary correctness issues, and
   Problem List taxonomy issues.
3. Replace the Graphs placeholder and decide the overlapping-page roles, beginning
   with Basic Analysis versus Analysis Fundamentals and the Heapsort consolidation.
4. Finish or explicitly quarantine the three draft algorithm pages.
5. Add prerequisite/continuation links and internal textbook links to every Problem
   page and the currently isolated data-structure chapters.
6. Expand the thin legacy algorithm pages with worked examples, pseudocode, and edge
   contracts; then establish repeatable sample-code checks.
7. Apply the demo/figure accessibility baseline, verify lesson-demo variant alignment,
   and integrate the five isolated demos.
8. Complete the notation, metadata, spelling, naming, glossary, and external-resource
   freshness passes.

## What is already working well

The audit should not be read as a request to homogenize every page. The Foundations
sequence, the newer Analysis chapters, and most of the newer linear-structure, heap,
hashing, and tree chapters already have the depth expected of a textbook replacement.
They use examples, diagrams, explicit operation contracts, tradeoffs, pitfalls, and
focused exercises effectively. Many mature algorithm pages also follow the intended
problem/strategy/implementation/analysis/practice arc. Their differences in length and
shape are often appropriate to the topic and do not need “fixing” merely for uniformity.
