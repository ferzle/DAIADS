# Programming Exercises v2 Work Status

Last updated: 2026-09-15

## Helper-method enhancement in progress

The completed v2 test expansion is receiving a second pass that adds safe test
observers and uses them for otherwise hidden representation checks. Tree,
linked-structure, hash-table, heap, and priority-queue checkpoints are complete
in all available languages. Work resumes with circular and ordinary array
queues/deques. See `helper-method-progress.md` for the exact restart point and
per-structure decisions.

## Completed

- Mirrored all 95 live starter files into `Content/Code/` under this directory.
- Expanded the tests for all 32 assignments in each available language.
- Added larger workloads, boundary cases, drain/reuse cases, and deterministic
  differential tests appropriate to each assignment.
- Removed tests for zero or negative construction sizes from the v2 suites.
- Added accumulated failure status handling to suites that use independent
  checks; dependent linked-sequence scenarios retain assertion-style failures.
- Wrote `problem-text-changes.md`. No live lesson change is required for the v2
  candidate.
- Confirmed that 32 Java files compile independently.
- Confirmed that 32 C++ files compile independently as C++17 with `-Wall`,
  `-Wextra`, and `-pedantic`.
- Confirmed that 31 Python files parse independently.
- Confirmed that the live and v2 trees contain the same 720 implementation
  `TODO` markers.

## Final audit results

- All 95 suites were run against their deliberately unfinished starters. Every
  suite returned a failure status; none accidentally passed or timed out.
- Three C++ suites initially terminated via a runtime signal when a failed
  prerequisite left later test state invalid. The tests were corrected to use
  bounds checks or scenario-level exception reporting, then recompiled and
  rerun with clean diagnostic failure statuses.
- All 95 implementation prefixes match the corresponding live starter after
  excluding only test counters and test-support includes.
- All 720 implementation `TODO` markers are preserved verbatim.
- No compiled binaries, Java class files, or Python bytecode were left in the
  v2 directory.

The v2 candidate is complete and ready for review. No files have been promoted
to the live download tree.

The live `Content/Code/` files have not been replaced. Its pre-existing
`avl-trees/cpp/avl_tree_starter.cpp` `<string>` include remains the only tracked
live starter difference visible in `git diff`.
