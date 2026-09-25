# Programming Exercise Solution Status

Last updated: 2026-09-25

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

## Next assignment

18. Open-addressing hash set.

## Verification policy

For each assignment:

- start from the corresponding enhanced v2 exercise so the solution runs the
  exact candidate tests;
- complete only the student implementation and retain test-only helpers;
- compile Java and C++ with warnings enabled and parse Python;
- execute every available language suite and require a successful exit;
- record the result here before beginning the next assignment.
