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

- [ ] **P0** `Content/Algorithms/Brute Force/Matrix Multiplication.html` — remove
  the extra `</section>` after Homework Problems.
- [ ] **P0** `Content/Algorithms/Decrease-and-Conquer/Hoare Partition.html` —
  remove the duplicate closing `</body>` and `</html>` pair.
- [ ] **P0** `Content/Algorithms/Divide-and-Conquer/Matrix Multiplication.html` —
  close Design and Strategy at the intended boundary; it currently has one more
  opening than closing section tag.
- [ ] **P0** `Content/Algorithms/Exhaustive Search/Depth-First Search.html` — close
  the outer Design and Strategy section at the intended boundary.
- [ ] **P0** `Content/Algorithms/Exhaustive Search/Topological Sort (DFS) DRAFT.html`
  — remove the duplicate closing `</html>`.
- [ ] **P0** `Content/Algorithms/Greedy/Prims.html` — remove the stray section close
  between the design illustration and Interactive Demo.
- [ ] **P0** `Content/Data Structures/Linear Structures/Stacks.html` — close the
  unmatched `div` and verify that the remainder of the page is not swallowed by it.
- [ ] **P0** `Content/Problems/Graphs/All-Pairs Shortest Path.html` — close the
  Examples section.
- [ ] **P0** `Content/Problems/Graphs/Maximum Flow.html` — add the missing opening
  section around Problem Description.
- [ ] **P0** `Content/Problems/Optimization/Minimum Coin Change.html` — add the
  missing `<body>` and remove the extra `</section>` after Examples.
- [ ] **P0** `Content/Techniques/Dynamic Programming.html` — repair the extra
  section close around the nested Fibonacci examples/summary and then verify the
  top-level collapse structure.
- [ ] **P0** `Content/Techniques/Exhaustive Search.html` — close the unmatched `div`.
- [ ] **P0** `Content/Demos/Brute Force/Matrix Multiplication Demo.html` — remove
  the duplicate closing body/html pair.
- [ ] **P0** `Content/Demos/Divide-and-Conquer/Matrix Multiplcation (Inplace) Demo.html`
  — remove the duplicate closing body/html pair.
- [ ] **P1** `Content/Demos/Data Structures/Open Addressing Demo.html` — inspect the
  dynamically generated table markup; the source contains an unmatched table opener.

### Normalize code tabs

Several older pages mark every tab and/or every panel active on initial load. The
scripts partly mask this visually, but the source exposes contradictory tab semantics
to assistive technology. In each tab group, only the initial tab should have
`aria-selected="true"` and only its panel should be active/visible.

- [ ] **P1** `Content/Algorithms/Brute Force/Bubble Sort.html`
- [ ] **P1** `Content/Algorithms/Brute Force/Selection Sort.html`
- [ ] **P1** `Content/Algorithms/Brute Force/Sequential Search.html`
- [ ] **P1** `Content/Algorithms/Decrease-and-Conquer/Hoare Partition.html`
- [ ] **P1** `Content/Algorithms/Decrease-and-Conquer/Insertion Sort.html`
- [ ] **P1** `Content/Algorithms/Divide-and-Conquer/Merge Sort.html`
- [ ] **P1** `Content/Algorithms/Divide-and-Conquer/Quicksort.html`
- [ ] **P1** `Content/Algorithms/Greedy/Merge.html` — normalize both independent tab
  groups.
- [ ] **P1** `Content/Algorithms/Space-Time Tradeoff/Bucket Sort.html`
- [ ] **P1** `Content/Algorithms/Space-Time Tradeoff/Counting Sort.html`
- [ ] **P1** `Content/Algorithms/Space-Time Tradeoff/Radix Sort.html`
- [ ] **P1** `Content/Algorithms/Transform-and-Conquer/Horner's Rule.html`
- [ ] **P1** `Content/Data Structures/Linear Structures/Representing Linear Structures.html`
  — keep one selected tab per language group, not one for the whole page.

### Make the reference pages connect back into the textbook

All 30 individual Problem pages name relevant algorithms and techniques as plain
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

- [ ] **P2** `Content/Demos/Brute Force/Matrix Multiplication Demo.html`
- [ ] **P2** `Content/Demos/Brute Force/String Matching Demo.html`
- [ ] **P2** `Content/Demos/Decrease-and-Conquer/Quickselect Demo.html`
- [ ] **P2** `Content/Demos/Divide-and-Conquer/Matrix Multiplcation (Inplace) Demo.html`
- [ ] **P2** `Content/Demos/Divide-and-Conquer/Matrix Multiplication Demo.html`
- [ ] **P2** `Content/Demos/Divide-and-Conquer/Strassens Demo.html`
- [ ] **P2** `Content/Demos/Dynamic Programming/Fibonacci Best Demo.html`
- [ ] **P2** `Content/Demos/Dynamic Programming/Floyd's Demo.html`
- [ ] **P2** `Content/Demos/Dynamic Programming/Warshall's Demo.html`
- [ ] **P2** `Content/Demos/Space-Time Tradeoff/Boyer-Moore Demo.html`
- [ ] **P2** `Content/Demos/Space-Time Tradeoff/Horspool Precomputation Demo.html`
- [ ] **P2** `Content/Demos/Transform-and-Conquer/Fibonacci Number (Matrix) Demo.html`
- [ ] **P2** `Content/Demos/Transform-and-Conquer/Horners Rule Demo.html`

### Mechanical consistency pass

- [ ] **P2** Add a viewport meta tag to the 146 legacy pages that omit it. This is
  most visible when a content file or standalone demo is opened outside the shell.
- [ ] **P2** Standardize algorithm section names (`Problem Solved`, `Design and
  Strategy`, `Implementation in Java, C++, Python`, `Time/Space Analysis`, and
  `Homework Problems`) where a different name does not convey a real distinction.
- [ ] **P2** Add `rel="noopener"` consistently to external `target="_blank"` links.
- [ ] **P2** Run a final spelling/name pass, especially for “Quickselect,” “Quicksort,”
  “Strassen's,” “Horner's,” and “matrix multiplication.”

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

The remaining Foundations, Analysis, and recurrence pages are among the strongest
material in the repository: they contain sustained explanations, worked examples,
practice, and clear audience scaffolding. No page-specific rewrite is recommended for
them in this pass.

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
- [ ] **P2** Correct “but be too prohbitive,” and standardize “QuickHull.”

#### `Content/Techniques/Decrease-and-Conquer/Introduction.html`

- [ ] **P0** Fix the broken MathJax delimiter in `\(c)` and the sentence “doing a bit
  work.”
- [ ] **P1** Remove Binary Search from Variable-Size-Decrease examples (it is already,
  correctly, listed under decrease-by-a-constant-factor) or explain a genuinely
  different variant; the current classification contradicts itself on the same page.
- [ ] **P1** Add one small recurrence/size-sequence example for each of the three forms
  so this functions as an introduction rather than only an index.

#### `Content/Techniques/Backtracking.html`

- [ ] **P2** Correct “Exhuastive Search” and call exhaustive search a technique rather
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
- [ ] **P2** Correct “unfamili ar” (`unfamili ar` is currently written as
  `unfamili ar`/`unfamili ar` without the second “i” in source) and add the viewport
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

- [ ] **P0** Remove the duplicate closing HTML tag.
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

- [ ] **P0** Fix the displayed C++ declaration `void insertionSort(int []A, int n)`;
  use valid C++ array/pointer or container syntax.
- [ ] **P1** Add visible headings for Design and Strategy and Implementation, plus a
  complete static trace that states the sorted-prefix invariant.
- [ ] **P1** Discuss stability, adaptiveness, and comparison versus movement counts;
  qualify the blanket claim that it outperforms Bubble Sort on nearly sorted data.

#### `Content/Algorithms/Greedy/Merge.html`

- [ ] **P0** Remove the stray `S` after `C[k++] = A[i++];` in the displayed C++ code.
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
- [ ] **P2** Correct “the number of comparisons the algorithms does.”

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
- [ ] **P2** Correct “Matrix-Multiplcation” in the visible heading.

#### Demo filename/title cleanup

- [ ] **P2** Rename `Matrix Multiplcation (Inplace) Demo.html`, `Strassens Demo.html`,
  and `Horners Rule Demo.html` to correctly spelled titles. Update all iframe references
  and regenerate `scripts/chapters.json`/the sitemap in the same change.
- [ ] **P2** `Content/Demos/Divide-and-Conquer/Exponentiation Demo.html` — correct
  “multiplcation” in the step narration.

## Suggested implementation order

1. Fix malformed HTML and the two invalid displayed code samples.
2. Resolve the Hoare/partition contract and Problem List taxonomy issues.
3. Replace the Graphs placeholder and decide the Heapsort consolidation strategy.
4. Finish or explicitly quarantine the three draft algorithm pages.
5. Add internal textbook links to every Problem page.
6. Expand the thin legacy algorithm pages with worked examples and edge contracts.
7. Apply the demo accessibility baseline and integrate the five isolated demos.
8. Complete the metadata, spelling, naming, and external-resource freshness pass.

## What is already working well

The audit should not be read as a request to homogenize every page. The Foundations
sequence, the newer Analysis chapters, and most of the newer linear-structure, heap,
hashing, and tree chapters already have the depth expected of a textbook replacement.
They use examples, diagrams, explicit operation contracts, tradeoffs, pitfalls, and
focused exercises effectively. Many mature algorithm pages also follow the intended
problem/strategy/implementation/analysis/practice arc. Their differences in length and
shape are often appropriate to the topic and do not need “fixing” merely for uniformity.
