# Programming Exercise Solution Status

Last updated: 2026-09-26

## Progress

1. Fixed-array stack — complete and verified in Java, C++, and Python.
   Java compiled with `-Xlint:all`; C++ compiled as C++17 with `-Wall`,
   `-Wextra`, and `-pedantic`; Python parsed with `py_compile`. All three full
   suites passed, including the 100,000-operation workload.
2. Linked stack — complete and verified in Java, C++, and Python. All three
   full suites passed, including repeated drain/reuse, link/count validation,
   and the 100,000-operation workload.
3. Sentinel linked stack — complete and verified in Java, C++, and Python.
   All three suites passed repeated drain/reuse, sentinel identity and link
   validation, and the 100,000-operation workload.
4. Ordinary-array queue — complete and verified in Java, C++, and Python. All
   three suites passed capacity boundaries, shifting after dequeue, complete
   drain/reuse, representation snapshots, and the 1,024-item quadratic
   workload.
5. Circular-array queue — complete and verified in Java, C++, and Python. All
   three suites passed capacity boundaries, repeated wraparound at capacities
   1, 2, 5, and 64, representation-state checks, and the 100,000-operation
   aggregate wraparound workload.
6. Linked queue — complete and verified in Java, C++, and Python. All three
   suites passed FIFO behavior, singleton drain/reuse, head/tail/count and
   acyclic-link validation, and the 100,000-operation workload.
7. Doubly linked deque — complete and verified in Java, C++, and Python. All
   three suites passed mixed operations at both ends, clear and singleton
   reuse, bidirectional-link/count validation, and the 100,000-operation
   workload.
8. Fixed circular-array deque — complete and verified in Java, C++, and
   Python. All three suites passed full/empty boundaries, mixed operations at
   both ends, repeated wraparound and reuse across capacities 1, 2, 3, 8, and
   64, representation-state checks, and the 100,000-operation workload.
9. Linked-block deque — complete and verified in Java, C++, and Python. All
   three suites passed mixed operations across block boundaries, block sizes
   1, 2, 3, 4, 5, 8, and 32, clear and reuse, linked-block validation, and the
   100,000-operation workload.
10. Fixed-array list — complete and verified in Java, C++, and Python. All
    three suites passed indexed access and mutation, insertion/removal shifts,
    capacity and invalid-index boundaries, duplicate deletion semantics, and
    the 100,000-append/read workload.
11. Singly linked list with tail — complete and verified in Java, C++, and
    Python. All three suites passed indexed and endpoint operations, invalid
    indices, deletion at the head/tail/interior, clear and singleton reuse,
    and the 100,000-endpoint-operation workload.
12. Singly linked list — complete and verified in Java, C++, and Python. All
    three suites passed head insertion/deletion, insertion/deletion after a
    supplied node, exception contracts, search/traversal, and the 100,000-node
    mixed workload.
13. Doubly linked list — complete and verified in Java, C++, and Python. All
    three suites passed head/tail insertion and deletion, forward/backward
    traversal, search, insert-before/after, node deletion, and the 100,000
    endpoint-operation workload.
14. Unsorted-array set — complete and verified in Java, C++, and Python. All
    three suites passed duplicate rejection, growth, sequential membership,
    constant-time gap filling on removal, clear/reuse, negative keys, and the
    1,000-key differential workload.
15. Sorted-array set — complete and verified in Java, C++, and Python. All
    three suites passed binary-search lookup/insertion points, duplicate
    rejection, growth, ordered shifting, clear/reuse, and the 1,000-key
    descending-insertion and removal workload.
16. Bit-vector set — complete and verified in Java, C++, and Python. All three
    suites passed word/bit mapping, boundary bits, duplicate and removal count
    handling, invalid-key exceptions, clear/reuse, and the one-million-bit
    aggregate workload.
17. Separate-chaining hash set — complete and verified in Java, C++, and
    Python. All three suites passed collision-chain ordering, duplicate
    rejection, head/interior/tail removal, load-factor resizing with stable
    rehash order, structural validation, and the 100,000-key workload.
18. Open-addressing hash set — complete and verified in Java, C++, and Python.
    All three suites passed linear, triangular-quadratic, and double-hashing
    probe rules; tombstone search/reuse; full-table and wraparound cases;
    invalid-key handling; and all three 50,000-key aggregate workloads.
19. Incomplete hash table — complete and verified in Java, C++, and Python.
    All three suites passed home-position insertion, explicit duplicate versus
    collision results, non-overwriting collision behavior, removal/reuse,
    invalid-key handling, and the 100,000-slot workload.
20. Unsorted-array map — complete and verified in Java, C++, and Python. All
    three suites passed aligned key/value growth, stored-zero handling,
    replacement return values, constant-time gap filling on removal,
    clear/reuse, negative keys, and the 1,000-key mixed workload.
21. Direct-address map — complete and verified in Java, C++, and Python. All
    three suites passed boundary keys, presence tracking independent of stored
    values, replacement/removal return values, invalid-key handling,
    clear/reuse, and the exhaustive 100,000-key universe workload.
22. Library BST map — complete and verified in Java and C++; no Python version
    exists by design. Both suites passed stored-zero handling, replacement and
    removal return values, inclusive/reversed/out-of-range queries, sorted
    iteration, and the 100,000-entry map with a 10,000-entry range query.
23. Unsorted-array priority queue — complete and verified in Java, C++, and
    Python. All three suites passed stable priority/sequence ordering, growth,
    peek, interior and final-slot gap filling, complete drain, and the 500-entry
    intentionally quadratic workload. The missing Python `check_empty` test
    helper was added identically to v2 and the solution copy.
24. Sorted-array priority queue — complete and verified in Java, C++, and
    Python. All three suites passed worst-to-best insertion order, stable ties,
    growth, constant-time peek/extract at the used right end, and the 500-entry
    intentionally quadratic workload. Its missing Python `check_empty` helper
    was added identically to v2 and the solution copy.
25. Binary heaps — complete and verified in Java, C++, and Python. All three
    suites passed fixed-capacity boundaries, parent/child indexing, sift-up and
    sift-down (including an only-left-child case), duplicates, negative keys,
    heap-order validation, reuse, and 100,000 insert/extract operations.
26. Heap construction and heapsort — complete and verified in Java, C++, and
    Python. All three suites passed exact bottom-up heap construction,
    active-prefix sift-down boundaries, empty and singleton inputs,
    duplicates and negative values, and deterministic heapsort and heap
    construction workloads through 100,000 elements.
27. Recursive binary-tree algorithms — complete and verified in Java, C++,
    and Python. All three suites passed size, edge-based height, leaf count,
    and two-child-node count checks for empty, singleton, complete,
    degenerate, irregular, and 100,000-node trees. The irregular-tree height
    expectations were corrected identically in v2 and the solution copy from
    4/3/3 to 3/2/2 to match the documented empty-height -1 convention.
28. Recursive tree traversals — complete and verified in Java, C++, and
    Python. All three suites passed preorder, inorder, and postorder traversal
    for empty, singleton, complete, degenerate, irregular, interior-subtree,
    and 100,000-node trees, including independent result containers. The
    irregular-tree inorder expectation was corrected identically in v2 and
    the solution copy to visit a left child before its parent.
29. Level-order tree traversal — complete and verified in Java, C++, and
    Python. All three suites passed empty, singleton, complete, degenerate,
    sparse, interior-subtree, and long right-only trees, independent result
    containers, and a 100,000-node breadth-first traversal.
30. Binary search tree — complete and verified in Java, C++, and Python. All
    three suites passed search, duplicate-safe insertion, minimum and
    successor queries, leaf/one-child/two-child/root removal, parent-link and
    size maintenance, complete drain, 500-key ordered updates, and the
    20,000-key balanced-order aggregate workload.
31. AVL trees — complete and verified in Java, C++, and Python. All three
    suites passed stored-height maintenance, all four insertion rotations,
    deletion rotations, search, duplicate handling, removal and complete
    drain, 2,000 randomized differential updates, and the 20,000-key insertion,
    search, and 10,000-key removal stress workload.
32. 2–3 trees — complete and verified in Java, C++, and Python. All three
    suites passed cascading splits, minimum/maximum and neighbor queries,
    inclusive ranges, deletion redistribution and merging, root shrinkage,
    randomized differential updates and complete drains, and the 20,000-key
    ordered insertion/search and 10,000-key removal workload. The Python large
    test was corrected identically in v2 and the solution copy to call
    `size()` instead of comparing the method object with an integer.

## Completion

All 32 assignments are complete and verified in every available language.
The solution tree contains all 95 expected source paths, contains no remaining
implementation `TODO` markers, and contains no generated build artifacts.
A self-discovering runner, `run_all_tests.py`, compiles and executes every
suite using an automatically cleaned temporary build directory. A fresh audit
passed 94 suites through the runner; its initial 60-second policy timed out the
Python open-addressing suite, which then completed separately with all tests
passing in 4m7s. The runner's documented default is now 600 seconds.

## Verification policy

For each assignment:

- start from the corresponding enhanced v2 exercise so the solution runs the
  exact candidate tests;
- complete only the student implementation and retain test-only helpers;
- compile Java and C++ with warnings enabled and parse Python;
- execute every available language suite and require a successful exit;
- record the result here before beginning the next assignment.
