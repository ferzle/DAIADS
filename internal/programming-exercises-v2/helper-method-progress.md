# Test Helper Enhancement Progress

Last updated: 2026-09-24

This file is the restart point for the helper-method review. Changes are made
only in the v2 candidate; the live `Content/Code/` starters remain untouched.

## Safety rule

Test observers may expose state but must not show students how to implement the
assigned operations. Completed validators are acceptable only when their logic
is not itself a learning objective in that assignment. Helpers return values or
copies, never mutable internal nodes or arrays.

## Completed: AVL tree

Languages: Java, C++, Python.

- Added a test-only root-height observer. It returns `-1` for an empty tree and
  otherwise reads the height already stored at the root. It does not compute or
  maintain heights, so it does not reveal the AVL implementation.
- Added a test-only root-key observer. It exposes one key but no node or link.
- Added tests for empty-tree observations, the root and height after all four
  insertion rotation patterns, the root and height after four deletion rotation
  patterns, and a generous height bound after 1,000 insertions.
- Retained the existing completed structural validator for ordering, parents,
  stored heights, AVL balance, and node count.
- Verified that all three files compile or parse.

## Completed: binary search tree

Languages: Java, C++, Python.

- Added a test-only root-key observer; it exposes no node or link.
- Added tests that the initial root remains 50, successor substitution changes
  the deleted two-child root from 50 to 57, and draining a one-node tree clears
  the root.
- Deliberately did **not** add a completed height method. Its recursive
  implementation would disclose part of the separate binary-tree recursive
  algorithms exercise. Existing inorder and parent-reference helpers remain the
  structural checks for this assignment.
- Verified that all three files compile or parse.

## Completed: linked stacks, queue, and deques

Languages: Java, C++, Python.

- Linked stack: added `hasValidStructureForTesting` to detect cycles and compare
  reachable nodes with the stored count. Tests use it on a nonempty stack and
  after draining.
- Sentinel linked stack: retained the original sentinel identity and added a
  validator for sentinel preservation, cycles, and reachable-node count. Tests
  use it on a nonempty stack and after draining.
- Linked queue: added a validator for matching empty endpoints, a terminating
  tail, cycles, tail reachability, and node count. Tests use it after growth and
  after draining.
- Doubly linked deque: added a validator that walks both directions, checking
  endpoints, reciprocal links, reachability, cycles/bounds, and node count.
  Tests use it in nonempty and empty states.
- Linked-block deque: added a deliberately limited validator for the published
  invariants: empty endpoints, endpoint links, index ranges, uniform block
  sizes, reciprocal block links, and last-block reachability. It does not reveal
  element placement and does not require optional empty-block reclamation.
  Tests use it initially, across multiple blocks, and after draining.
- Basic singly linked list and doubly linked list: no helper added because
  traversal is an explicit student task and a completed traversal validator
  would disclose that implementation.
- Singly linked list with tail: no helper added because a validator would be
  essentially the assigned `nodeAt` traversal. Its existing indexed operations
  and repeated singleton tests remain the safer black-box checks.
- Verified that the 15 changed linked-structure files compile or parse.

## Completed: hash tables

Languages: Java, C++, Python.

- Open-addressing set: added a slot-state count snapshot returning counts of
  empty, occupied, and deleted slots without exposing keys or probe formulas.
  Tests now check allocation after collisions, creation and reuse of a
  tombstone, and restoration to all-empty state after `clear`.
- Separate-chaining set: added a structural validator for cycles, duplicate
  keys, wrong-bucket keys, and disagreement between reachable nodes and the
  stored count. This builds on the already supplied bucket snapshot and does
  not implement search, insertion, removal, or resizing. Tests use it after a
  collision chain, rehashing, and the 1,000-key workload.
- Deliberately incomplete hash table: added a copy of the backing table for
  tests. Tests now verify exact home-position placement, collision
  non-mutation, and reuse of a removed home position. The helper does not
  compute a home position or resolve a collision.
- Verified that all nine changed hash-table files compile or parse.

## Completed: binary heap and array priority queues

Languages: Java, C++, Python.

- Fixed-capacity binary max-heap: no new method was necessary. All versions
  already expose the used-array state to tests. The existing
  `hasValidHeapOrder` remains a student TODO; supplying a second completed
  heap-order validator would give away that assigned implementation.
- Sorted-array priority queue: added a read-only string summary of the used
  entries. Tests now verify the required worst-to-best physical order and the
  physical order that produces stable extraction for tied priorities.
- Unsorted-array priority queue: added a read-only string summary containing
  each used entry's value, priority, and sequence number. Tests verify append
  order, increasing sequence numbers, and that extracting an interior best
  entry fills its gap with the former last entry.
- The summaries disclose representation state already required by the problem
  text but contain no insertion, comparison, search, or extraction algorithm.
- Verified that all nine heap/PQ files compile or parse.

## Completed: array queues and circular deque

Languages: Java, C++, Python.

- Circular-array queue: added a read-only `(front, count, capacity)` state
  observer. Tests check the state before wraparound, after advancing the front,
  and when the wrapped queue becomes full. The observer exposes stored fields
  but does not supply the modular-index formula.
- Ordinary-array queue: added a copy of the used prefix. Tests check that
  dequeue shifts the remaining logical values left and that enqueue appends to
  that used prefix.
- Corrected an erroneous v2 test which claimed an ordinary array queue could
  not reuse capacity after draining. The lesson explicitly specifies shifting
  values left on dequeue, and the starter has only a count field, so a drained
  queue can be filled again. Tests now enqueue and dequeue a value after drain.
- Fixed circular-array deque: added a read-only `(front, count, capacity)` state
  observer. Tests check state after front wraparound and after refilling a
  wrapped deque to capacity; no back-index formula is provided.
- Verified that all nine changed files compile or parse.

## Completed: final list, stack, map, and set review

Languages: Java, C++, Python where available.

- Fixed-array list: added a copy of the used prefix. Tests check the complete
  logical order after mixed insertions and confirm failed full-capacity
  insertion leaves contents unchanged.
- Fixed-array stack: added a copy of the used prefix from bottom through top.
  Tests confirm failed overflow is non-mutating and pop/reuse produces the
  expected physical contents.
- Unsorted-array map already supplies an aligned entry snapshot; no additional
  helper is needed.
- Unsorted-array, sorted-array, and bit-vector sets already supply complete
  logical snapshots; no additional helpers are needed.
- Direct-address map can be exhaustively checked through its finite public key
  universe, so exposing its presence/value arrays would add coupling without
  meaningful new confidence.
- Library BST map intentionally delegates representation to a standard-library
  map, so internal structural helpers would test the library rather than the
  student activity.
- The 2-3 tree already has inorder values, height, and a comprehensive
  structural validator. Recursive algorithms, traversals, heap construction,
  and heapsort return complete results that tests can inspect directly.
- Verified that the six changed fixed-list/stack files compile or parse.

## Completed: consolidated audit

- Compiled all 32 Java files independently.
- Compiled all 32 C++ files independently as C++17 with `-Wall`, `-Wextra`,
  and `-pedantic`.
- Parsed all 31 Python files independently without creating bytecode artifacts.
- Ran all 95 deliberately unfinished suites with a five-second timeout. All 95
  returned ordinary failure statuses; none passed accidentally, timed out, or
  ended via a runtime signal.
- Compared every TODO line in each v2 starter with its corresponding live
  starter. All 716 current TODO markers match. The older status count of 720
  had become stale and has been corrected.
- Confirmed that no class files, Python bytecode, or executable artifacts were
  left in the v2 directory.

The helper-method enhancement is complete. Live starter files remain unchanged.
