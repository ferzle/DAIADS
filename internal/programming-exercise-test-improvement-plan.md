# Programming Exercise Test Improvement Plan

## Scope

This plan covers all 95 student starter files currently linked from DAIADS:
32 programming assignments, with Java, C++, and Python versions except for the
intentionally Java/C++-only library BST-map activity.

The original files under `Content/Code/` remain the authoritative student
downloads. Proposed v2 work should be developed under
`internal/programming-exercises-v2/Content/Code/` with the same directory and
file names. This makes comparisons mechanical, for example:

```bash
diff -u \
  Content/Code/queue-circular-array/java/CircularQueueExercise.java \
  internal/programming-exercises-v2/Content/Code/queue-circular-array/java/CircularQueueExercise.java
```

Only test code, test helpers, and test-oriented accessors or validators should
change in v2. Student `TODO` methods must remain unimplemented.

## Test-suite standard

Every v2 suite should follow these rules unless an assignment needs a documented
exception.

1. **Match the published contract.** Do not require unsupported inputs or a
   particular internal choice unless the exercise explicitly requires it.
2. **Organize tests by named behavior.** At minimum, separate empty/singleton,
   ordinary operations, boundary transitions, and reuse after becoming empty.
3. **Check state after mutations.** Verify the operation result plus public size,
   emptiness, and observable boundary values when applicable.
4. **Exercise representation boundaries.** Use capacities/block sizes around
   `1`, `2`, and a typical larger value, and cross each boundary in both
   directions.
5. **Use deterministic differential tests where appropriate.** Compare against
   a standard-library reference model with a fixed seed and report the seed and
   operation number on failure.
6. **Check invariants when representation is part of the objective.** Prefer a
   completed validator supplied by the starter. Reflection/private-field
   inspection belongs in an instructor harness, not the introductory visible
   tests.
7. **Give diagnostic failures.** Include the scenario, operation, expected value,
   actual value, and test location or label. Continue through independent
   scenarios, then print a summary and return a failing process status if any
   check failed.
8. **Keep language versions behaviorally equivalent.** The syntax may be
   idiomatic, but Java, C++, and Python should run the same conceptual scenarios.
9. **Keep visible tests readable.** Large randomized, reflective, leak, or
   adversarial suites should be separate instructor tests. Student files should
   still demonstrate how to write focused tests.
10. **Do not expose solutions.** Test helpers may compute expected public results,
    but must not reveal the implementation of a student `TODO` method.

## Planned improvements by assignment

Priorities mean:

- **High:** important public behavior or a difficult structural transition is
  weakly covered.
- **Medium:** current tests are useful but omit meaningful edge combinations.
- **Low:** current tests are already broad; improve diagnostics or add targeted
  regression cases.

### Linear structures

#### Fixed-size array stack — Medium

- Run the same contract against capacities 1, 2, and 5.
- Separate underflow, fill-to-capacity, overflow-without-mutation, LIFO drain,
  and reuse-after-drain scenarios.
- Check `size`, `isEmpty`, `isFull`, and `peek` after every boundary transition.
- Add a fixed-seed model test using a simple reference sequence, while keeping
  values nonnegative because `-1` is the published underflow sentinel.

#### Linked stack — Medium

- Add repeated singleton push/pop cycles to expose stale-head bugs.
- Grow well beyond the current three-node example, drain completely, and reuse.
- Interleave pushes, peeks, and pops against a reference sequence.
- In an instructor harness, verify that the node count equals public size and
  that the chain is acyclic.

#### Sentinel linked stack — Medium

- Add the linked-stack scenarios above plus explicit repeated empty/nonempty
  transitions.
- In an instructor harness, verify the sentinel is never replaced, removed, or
  counted and that `sentinel.next` is null exactly when empty.

#### Ordinary-array queue — High

- Test capacities 1, 2, and 5 and document that removed prefix space is not
  reused in this representation.
- Distinguish logical emptiness from exhausted backing-array capacity.
- Test failed enqueue without mutation, FIFO draining, and clear/reuse if clear
  is part of the published contract.
- Add sequences that alternate enqueue/dequeue so students see the limitation
  relative to a circular queue.

#### Circular-array queue — High

- Force front and back indices to wrap multiple times for capacities 1, 2, and 5.
- Test full-to-not-full-to-full transitions and failed enqueue without mutation.
- Compare long deterministic mixed sequences with a bounded reference queue.
- Check front, size, empty, and full state after every operation.

#### Linked queue — Medium

- Exercise first insertion, removal of the only node, and immediate reuse many
  times to catch stale-tail errors.
- Interleave enqueue/dequeue operations and compare with a reference FIFO.
- In an instructor harness, verify head/tail nullness, tail termination, chain
  acyclicity, and node count.

#### Doubly linked deque — Medium

- Run all four add/remove direction pairings on singleton and multi-node states.
- Add repeated transitions across sizes 0, 1, and 2, clear/reuse, duplicates,
  and a deterministic reference-deque sequence.
- In an instructor harness, check both traversal directions, endpoint nullness,
  reciprocal links, acyclicity, and node count.

#### Fixed circular-array deque — Medium

- Test capacities 1, 2, 3, and 8 with wraparound beginning from both ends.
- Cover every add/remove direction pairing, overflow without mutation,
  clear/reuse after wrapped state, and repeated full/empty cycles.
- Add a bounded reference-deque differential test with a fixed seed.

#### Linked-block deque — High

- Use the useful structure of `internal/DequeTest.java` and
  `internal/deque_test.py`: multiple block sizes, all direction pairings, exact
  block boundaries, ping-pong operations, interleaved growth, clear/reuse, and a
  seeded reference-deque test.
- Keep generated values nonnegative unless the page contract is changed.
- Enforce the published empty invariant: both end-block references must be null.
- Do not grade block reclamation or impose a strict block-count bound. The page
  currently says an empty end block *may* be unlinked. If reclamation later
  becomes a learning objective, change the page to say *must* before adding a
  corresponding test.
- Treat absent required representation fields as failures rather than silently
  disabling structural tests.
- Fix the Python harness so a student-code crash aborts one scenario, not the
  rest of that test section, and verify add-operation return values in randomized
  replay.
- Create a matching C++ instructor harness after the contract is settled.

#### Fixed-array list — Low

- Retain the broad current scripted coverage.
- Add capacities 0/1 only if their constructor behavior is explicitly defined.
- Add a short deterministic model test mixing indexed insert/remove, value
  deletion, set/get, and failed operations at both invalid-index boundaries.
- Confirm every failed mutator leaves size and contents unchanged.

#### Singly linked list with tail — Low

- Retain the broad current operation script and add repeated singleton
  insert/remove cycles targeting tail reset.
- Add duplicate values to distinguish `indexOf`, `contains`, and deletion of the
  first occurrence.
- Add a deterministic model sequence and instructor checks for acyclicity,
  tail termination, tail reachability, and stored count.

#### Basic singly linked list — High

- Split the current single fail-fast script into named independent scenarios.
- Cover deletion from empty, insertion after head/middle/tail, deletion after
  head/middle/tail, null-node errors, absent searches, duplicates, and complete
  drain/reuse.
- Improve C++ failure diagnostics with call-site labels/lines and give all three
  versions the same scenario summary.

#### Doubly linked list — Medium

- Add independent empty, singleton, two-node, and longer-list scenarios.
- Exercise insert-before/after and delete-node at head, tail, and interior;
  repeat with duplicate values returned by search.
- Check forward traversal is the reverse of backward traversal after every
  mutation.
- Add instructor invariants for reciprocal links, endpoint nullness, count,
  acyclicity, and head/tail reachability.

### Sets, maps, and hash tables

#### Unsorted-array set — Medium

- Add capacity-growth boundaries, duplicates before and after resizing, removal
  of first/middle/last entries, absent removal, clear/reuse, and zero.
- Because order is not part of the ADT, compare contents as sets rather than
  imposing an incidental array order except where gap filling is explicitly
  required.
- Add a fixed-seed differential test against a library set.

#### Sorted-array set — Medium

- Add descending insertion, insertion at both ends and the middle, duplicates,
  resize boundaries, and every removal position.
- Verify sorted unique output after every mutation.
- Add a fixed-seed differential test against an ordered library set.

#### Bit-vector set — Medium

- Retain the existing word-boundary cases and add universe sizes 1, 31, 32, 33,
  63, 64, and 65.
- Test the final partially used word, repeated add/remove, clear/reuse, and both
  invalid-key boundaries.
- Differentially test every valid key in several small universes after a fixed
  sequence of mutations.

#### Separate-chaining set — Medium

- Add deliberate collisions, removal at the head/middle/tail of a bucket chain,
  duplicate insertion, absent removal, and clear/reuse if supported.
- Cross each resize threshold in both sparse and collision-heavy distributions.
- After resizing, verify all old keys and bucket-index legality.
- Add a fixed-seed differential test against a library set.

#### Open-addressing set — Low

- Retain the existing per-strategy and tombstone stress tests.
- Add wraparound clusters, insertion when no truly empty slot remains but a
  tombstone exists, duplicate discovery beyond a tombstone, and repeated
  delete/reinsert cycles.
- Differentially test all probing strategies with fixed seeds and report probe
  strategy in every failure.

#### Deliberately incomplete hash table — High

- Test the assignment's intended limitation explicitly: a collision that is
  placed away from home cannot be found/removed by the incomplete methods.
- Add no-collision success cases, absent keys, repeated removal, invalid keys,
  and keys at universe boundaries.
- Label expected failures as demonstrations of incompleteness rather than making
  the starter appear accidentally broken.

#### Unsorted-array map — High

- Test absent versus stored zero, replacement return values, size stability on
  replacement, resizing, removal at every physical position, gap filling of both
  aligned arrays, clear/reuse, and duplicate-prevention.
- Compare entries as mappings rather than relying on incidental order unless the
  page requires an order.
- Add a fixed-seed differential test against a library map.

#### Direct-address map — High

- Exercise universes 1 and a larger non-word-sized value; test first/last valid
  keys and both invalid boundaries.
- Test stored zero, replacement, repeated removal, clear/reuse, and size changes
  after successful versus unsuccessful mutations.
- Exhaustively compare a small universe with a reference map after each update.

#### Library BST map — High

- Add the two tests the page currently asks students to write: updating a key and
  querying an empty range.
- Add inclusive endpoint ranges, reversed ranges, negative keys, stored zero,
  removals, clear state if supported, and range queries returning none/one/many.
- Differentially compare operations and ordered ranges with the underlying
  language's ordered-map oracle without testing library internals.

### Priority queues and heaps

#### Unsorted-array priority queue — Medium

- Expand stability tests to three or more equal-priority entries separated by
  other priorities and across a resize.
- Exercise removal when the best entry is first, middle, and last, followed by
  complete drain and reuse.
- Compare a fixed mixed sequence against an oracle ordered by
  `(priority, insertionSequence)`.

#### Sorted-array priority queue — Medium

- Verify physical sorted order indirectly by draining after ascending,
  descending, and mixed insertions.
- Test stable ties across resizing, repeated peeks, empty removals, complete
  drain/reuse, and several initial capacities.
- Differentially compare with a stable `(priority, sequence)` oracle.

#### Fixed-capacity binary max-heap — High

- Drain the heap completely and verify nonincreasing output, size changes, and
  heap order after every extraction.
- Add capacities 1 and 2, repeated maximum values, left-only and right-wins
  sift-down cases, overflow without mutation, and reuse after draining.
- Add a fixed-seed differential test against a max-priority-queue oracle.

#### Heap construction and heapsort — Medium

- Add `siftDown` no-op cases for empty/singleton active prefixes and leaf roots,
  plus cases where the right child is larger and where descendants outside the
  active prefix must remain untouched.
- For `buildMaxHeap`, test empty, singleton, sorted, reverse-sorted, duplicate,
  and randomized arrays while checking both heap order and value preservation.
- Compare fixed-seed randomized `heapSort` results with the language's trusted
  sort implementation.

### Trees

#### Binary-tree recursive algorithms — High

- Add asymmetric trees containing right-only and mixed one-child nodes; the
  current complete and one-direction degenerate trees do not expose all
  left/right mistakes.
- Test each function on every subtree of a representative irregular tree.
- Add a moderately deep chain and a fixed-seed generated tree with independently
  computed expected counts/heights.

#### Recursive depth-first traversals — Medium

- Add an irregular tree with both left-only and right-only nodes and repeated
  values.
- Test traversal from interior subtrees, repeated calls on the same tree, and
  returned-list independence (one call must not contaminate the next).
- Add a fixed-seed generated tree checked by a small independent oracle.

#### Level-order traversal — High

- Add wide, sparse, right-only, and mixed-shape trees; the current five checks
  cover too few shapes.
- Test duplicates, interior-subtree traversal, repeated calls, and returned-list
  independence.
- Add a fixed-seed generated tree checked against a simple reference BFS.

#### Binary search tree — Medium

- Add deletion of root/leaf/one-child/two-child nodes on both left and right
  orientations, including immediate-successor and deeper-successor cases.
- Verify inorder contents, size, membership, minimum, successors, and parent
  references after every mutation.
- Add sorted and reverse-sorted insertion, complete drain/root replacement, and
  a fixed-seed differential test against an ordered set.

#### AVL tree — High

- Add targeted deletion repairs for all rotation directions, the child-balance
  zero case, and repairs that continue through multiple ancestors.
- Check inorder contents and exact size—not only structural validity—after every
  insertion and deletion.
- Add ascending/descending insertion, duplicate and absent updates, complete
  drain/root shrinkage, and fixed-seed differential testing against an ordered
  set after every operation.

#### 2–3 tree — Low

- Retain the current validator, sorted split stress, randomized differential
  updates, and complete drain; this is already the strongest visible suite.
- Add targeted deletion scenarios for borrowing from each sibling direction,
  merging in each parent position, cascading merge, and root contraction so
  failures identify the missing repair rather than only a random update number.
- During randomized testing, also sample minimum, maximum, predecessor,
  successor, and inclusive ranges against the ordered-set oracle.

## Rollout order

1. **Pilot:** linked-block deque in all three languages, preserving optional
   block reclamation and correcting the two sample harnesses.
2. **High-priority small suites:** maps, ordinary/circular queues, basic singly
   linked list, binary-tree algorithms, level order, incomplete hash table.
3. **High-priority advanced suites:** binary heap and AVL tree.
4. **Medium-priority families:** remaining linear structures, sets, priority
   queues, heapsort, BST, and recursive traversals.
5. **Low-priority suites:** fixed-array/singly-linked lists, open addressing, and
   2–3 trees.
6. Compile or parse all 95 v2 files, run contract/static checks, compare language
   scenario inventories, and only then decide which v2 files replace live
   downloads.

## Implementation decisions

1. Follow the published contract for linked-block reclamation. Require the stated
   fully empty representation, but permit retention of an empty end block while
   values remain. Do not add a strict space-bound test unless the problem text is
   first changed to require reclamation.
2. Do not test construction with zero or negative capacities. This is outside the
   intended learning objectives unless a particular problem explicitly specifies
   that behavior.
3. Visible tests should continue through independent scenarios, print specific
   diagnostic messages that help students locate failures, print a final summary,
   and return a failing process status when any check fails.
4. Student-facing suites should provide strong evidence of correctness rather
   than serve only as small examples. Include larger inputs, boundary transitions,
   complete drain and reuse, mixed-operation sequences, and fixed-seed differential
   tests. Use separate instructor harnesses for reflection-based or otherwise
   specialized checks when those would obscure the student-facing suite.
