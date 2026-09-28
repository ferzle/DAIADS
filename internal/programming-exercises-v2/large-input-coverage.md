# Large-Input Test Coverage Audit

Last updated: 2026-09-24

This is the completed scale audit of all 32 programming assignments. Large
tests are selected according to the complexity students are
asked to implement. A test is not made larger when doing so would require a
correct quadratic implementation to perform billions of operations.

## Scale policy

- Expected O(1) operations and linear traversals: target 100,000 aggregate
  operations or elements.
- O(n log n) structures and sorting: target at least 20,000 elements, and
  100,000 where the language-independent runtime remains reasonable.
- Deliberately O(n^2) workloads: 500-1,000 adversarial elements are sufficient
  to execute hundreds of thousands to about a million primitive comparisons or
  moves.
- Recursive tree algorithms: use a large balanced or wide tree so the test
  measures total work without failing a correct solution merely because the
  language has a limited call stack. Keep a separate moderate-depth chain test.
- Large workloads aggregate their result into one or a few checks. They must not
  print 100,000 success lines.

## Assignment-by-assignment decisions

| Assignment | Existing largest useful workload | Scale decision |
|---|---:|---|
| Fixed-array stack | capacity 32 | Raise to 100,000 push/pop operations. |
| Linked stack | 5,000 push/pop operations | Raise to 100,000. |
| Sentinel linked stack | 5,000 push/pop operations | Raise to 100,000. |
| Ordinary-array queue | 1,024 fill/drain | Keep: shifting dequeue makes the drain quadratic. |
| Circular-array queue | about 5,140 wraparound items | Raise to 100,000 operations. |
| Linked queue | 5,000-item drain | Raise to 100,000. |
| Doubly linked deque | 4,000 mixed items | Raise to 100,000. |
| Fixed circular-array deque | about 5,140 items | Raise to 100,000 operations. |
| Linked-block deque | 750-item mixed run | Raise to 100,000 across many blocks. |
| Fixed-array list | 256-item drain and 200 indexed items | Add a 100,000-item append/read test; retain smaller quadratic shifting tests. |
| Singly linked list with tail | 1,000 indexed/removal workload | Add 100,000 append/remove-first operations; retain smaller indexed tests. |
| Basic singly linked sequence | 200 nodes | Raise head operations and tail search to 100,000. |
| Doubly linked list | 200 nodes | Raise endpoint operations to 100,000. |
| Unsorted-array set | 1,000 adversarial keys | Keep: duplicate/search/removal workload is quadratic. |
| Sorted-array set | 1,000 descending inserts | Keep: ordered insertion and removal shift quadratically. |
| Bit-vector set | universe 257 | Raise universe coverage to 1,000,000 bits with sampled/full-word operations. |
| Separate-chaining set | 1,000 collision-heavy keys | Add 100,000 well-distributed operations; retain collision test. |
| Open-addressing set | 400 replacement cycles | Add a large table with 50,000 mixed operations per probing strategy. |
| Incomplete hash table | 257 positions | Raise exhaustive home-position/collision test to 100,000 positions. |
| Unsorted-array map | 1,000 keys | Keep: lookups, replacements, and removals are quadratic. |
| Direct-address map | universe 257 | Raise exhaustive universe test to 100,000 keys. |
| Library BST map | 1,001 keys | Raise to 100,000 keys and a large range result. |
| Unsorted-array priority queue | 500-entry stable drain | Keep: repeated best-entry scans are quadratic. |
| Sorted-array priority queue | 500-entry insertion/drain | Keep: ordered insertion shifts quadratically. |
| Binary max-heap | 1,000 with validation after every update | Add 100,000 insert/extract operations with aggregate ordering checks. |
| Heap construction and heapsort | 1,000 elements | Raise deterministic sort test to 100,000. |
| Binary-tree recursive algorithms | 200-node chain | Add a 100,000-node broad/balanced tree; retain chain edge case. |
| Recursive depth-first traversals | irregular small tree | Add a large balanced tree of at least 100,000 nodes. |
| Level-order traversal | 201-node chain | Add a 100,000-node wide/balanced tree. |
| Binary search tree | 500 ascending keys | Keep adversarial 500-key quadratic case and add a larger balanced-order workload. |
| AVL tree | 1,000 keys plus 2,000 differential updates | Add a 20,000-key aggregate insert/search/remove test. |
| 2-3 tree | 3,000 differential updates | Add a 20,000-key aggregate insert/search/remove test. |

## Work status

- Inventory and scale decisions: complete.
- Test updates in every available language: complete.
- Consolidated compile/parse audit: complete (32 Java, 32 C++, 31 Python).
- Unfinished-starter execution audit: complete. All 95 suites exited with the
  expected ordinary failure status; none passed accidentally, crashed, or
  timed out under a 30-second per-suite limit.
- Preservation audit: complete. The pre-promotion live tree (now v0) and v2
  each contain 95 starter
  files and 716 `TODO` markers, and the v2 directory contains no generated
  binaries, class files, or bytecode.

## Coverage conclusion

Every programming assignment was reviewed, including the assignments whose
existing 500- or 1,000-item workload was deliberately retained. The final
sizes are complexity-aware rather than uniform:

- One million positions/bits are exercised where storage and operations are
  constant-time (the bit-vector set).
- One hundred thousand elements or aggregate operations are exercised for
  linear structures, direct addressing, heaps/heapsort, library-backed maps,
  and broad tree traversals.
- Fifty thousand keys are exercised under each open-addressing strategy.
- Twenty thousand keys are exercised for the ordinary BST's balanced-order
  case, AVL trees, and 2–3 trees.
- Correctly quadratic student implementations retain 500- or 1,000-item
  adversarial tests, which already cause hundreds of thousands to about a
  million comparisons or shifts without imposing billion-operation runs.

The large tests use aggregate checks, so a successful implementation does not
print tens or hundreds of thousands of individual pass messages. Smaller
tests remain in place to provide precise failure localization.
