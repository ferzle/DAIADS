# Programming Exercises v2 Work Status

Last updated: 2026-09-28

## Helper-method and large-input enhancements complete

The completed v2 test expansion received a second pass that adds safe test
observers and uses them for otherwise hidden representation checks. Tree,
linked-structure, hash-table, heap, priority-queue, array queue/deque, list,
map, and set checkpoints are complete in all available languages. The
consolidated audit also passes. See `helper-method-progress.md` for the
per-structure additions and deliberate omissions, and
`large-input-coverage.md` for the assignment-by-assignment scale review.

## Completed

- Mirrored all 95 live starter files into `Content/Code/` under this directory.
- Expanded the tests for all 32 assignments in each available language.
- Added larger workloads, boundary cases, drain/reuse cases, and deterministic
  differential tests appropriate to each assignment.
- Re-audited all 32 assignments for scale. Constant-time, linear, and
  O(n log n) work now reaches 20,000 to 1,000,000 elements as appropriate;
  intentionally quadratic assignments retain 500- or 1,000-item adversarial
  workloads.
- Removed tests for zero or negative construction sizes from the v2 suites.
- Added accumulated failure status handling to suites that use independent
  checks; dependent linked-sequence scenarios retain assertion-style failures.
- Wrote `problem-text-changes.md`. No live lesson change is required for the v2
  candidate.
- Confirmed that 32 Java files compile independently.
- Confirmed that 32 C++ files compile independently as C++17 with `-Wall`,
  `-Wextra`, and `-pedantic`.
- Confirmed that 31 Python files parse independently.
- Confirmed that the pre-promotion live tree (now v0) and v2 contain the same
  716 implementation `TODO` markers.

## Final audit results

- All 95 suites were run against their deliberately unfinished starters. Every
  suite returned a failure status; none accidentally passed or timed out.
- Three C++ suites initially terminated via a runtime signal when a failed
  prerequisite left later test state invalid. The tests were corrected to use
  bounds checks or scenario-level exception reporting, then recompiled and
  rerun with clean diagnostic failure statuses.
- All 95 implementation prefixes match the corresponding pre-promotion starter
  now preserved in v0, after excluding only test counters and test-support
  includes.
- All 716 current implementation `TODO` markers are preserved verbatim.
- No compiled binaries, Java class files, or Python bytecode were left in the
  v2 directory.
- After the final large-input pass, all 95 suites again compiled or parsed and
  all 95 unfinished starters exited with ordinary failure status under a
  30-second per-suite timeout. There were no accidental passes, signals, or
  timeouts.
- Corrected the recursive-tree chain test's expected height from 200 to 199,
  consistent with the exercise's existing convention that an empty tree has
  height -1 and a leaf has height 0.
- Corrected the recursive-tree irregular-tree height expectations from 4/3/3
  to 3/2/2, also matching the edge-based height convention.
- Corrected the recursive-traversal irregular-tree inorder expectation so its
  left child is visited before its parent.
- Corrected the Python 2-3-tree large workload to call `size()` instead of
  comparing the `size` method object with the expected integer.

The v2 candidate was promoted to the live `Content/Code/` download tree on
2026-09-28. Immediately before promotion, a fresh audit compiled or parsed all
95 files and confirmed that all 95 deliberately unfinished suites exited with
ordinary failure status within 30 seconds, with no accidental passes, signals,
timeouts, or build failures. Post-copy comparison confirmed that production
matches v2 byte-for-byte. The former production tree is archived unchanged at
`internal/programming-exercises-v0/Content/Code/`.
