# Problem-Text Changes for Programming Exercise Tests v2

## Purpose

The v2 tests must enforce only behavior promised by the corresponding DAIADS
exercise. This document records every place where stronger testing would require
a change to the published problem text. The live lesson pages have not been
changed.

## Current decisions

- Do not add general tests for zero or negative constructor capacities. Those
  cases are outside the intended learning objectives unless an exercise already
  defines them explicitly.
- Larger inputs, repeated operations, boundary transitions, deterministic model
  tests, and reuse after becoming empty do not require text changes; they test
  the existing operation contracts more thoroughly.
- A representation invariant is tested only when the exercise specifies that
  representation. Otherwise, tests use public observable behavior.

## Linked-block deque

No text change is required for the v2 behavioral tests.

The exercise currently requires `firstBlock == null`, `lastBlock == null`, and
`count == 0` when the deque is completely empty. The tests may enforce that
explicit invariant. The exercise also says that an empty end block *may* be
unlinked while other values remain. Therefore, the v2 tests must not require
immediate reclamation or impose a strict maximum block count.

If block reclamation is later intended to be part of the assignment, replace
the optional statement with a requirement such as:

> When a removal leaves an end block with no stored values, unlink that block
> immediately. When the final value is removed, set both end-block references
> to null.

Only after making that contract change should a strict reclamation or space-bound
test be added.

## Review log

The complete 32-assignment test pass did not identify any required live-page
changes. The v2 suites use the contracts already stated either on the lesson
page or directly beside the starter method. In particular:

- The ordinary-array queue tests its intentionally noncircular behavior; they do
  not require removed prefix positions to be reused.
- The library BST-map starter already specifies inclusive ranges and an empty
  result when `low > high`.
- The set, map, hash-table, and bit-vector suites test invalid *keys* only where
  the starter explicitly specifies an exception. They do not test zero or
  negative construction sizes.
- Heap and balanced-tree structural validators are starter-provided testing
  operations, so using them does not add a hidden representation requirement.

Accordingly, no edits to live problem text are necessary for this v2 candidate.
The linked-block wording above remains a documented optional future change, not
a prerequisite for adopting these tests.
