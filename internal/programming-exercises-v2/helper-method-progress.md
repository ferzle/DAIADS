# Test Helper Enhancement Progress

Last updated: 2026-09-15

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

## Remaining order

1. Circular and ordinary array queues/deques.
2. Smaller list, map, and set helpers where they add meaningful coverage.
3. Consolidated compile/parse/run audit and final per-structure summary.
