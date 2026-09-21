# Programming Exercise Tests v2

This directory mirrors all student starter code under `Content/Code/` while the
live downloads remain unchanged.

## Scope

- 32 programming assignments
- 95 self-contained starter files
- 32 Java, 32 C++, and 31 Python versions
- The library BST-map activity intentionally has no Python version

Every v2 starter retains its implementation `TODO`s and adds broader tests for
the existing contract. Depending on the assignment, the additions cover larger
inputs, capacity or word boundaries, wraparound, block boundaries, duplicate
values, stable priority ties, repeated empty/nonempty transitions, complete
drain and reuse, ordered range endpoints, parent/link validation, and
deterministic differential workloads.

Tests report the expected and actual behavior or a labeled failed condition.
Suites with independent checks continue collecting failures and return a
nonzero process status at the end. The linked-sequence activity retains
exception-style dependent scenarios because later node operations cannot safely
continue when an earlier node creation fails.

## Validation performed

- All 32 Java files compile independently.
- All 32 C++ files compile independently with C++17, warnings enabled, and
  pedantic checking.
- All 31 Python files parse independently.
- The number and text of implementation `TODO` markers match the live starters.
- Running the suites against the deliberately unfinished starters produces a
  failure status rather than an accidental pass, and the expanded workloads do
  not hang by relying on a student-reported size or emptiness value.

See `problem-text-changes.md` for the contract review and
`../programming-exercise-test-improvement-plan.md` for the assignment-by-
assignment rationale.
