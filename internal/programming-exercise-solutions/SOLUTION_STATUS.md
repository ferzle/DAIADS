# Programming Exercise Solution Status

Last updated: 2026-09-24

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

## Next assignment

4. Ordinary-array queue.

## Verification policy

For each assignment:

- start from the corresponding enhanced v2 exercise so the solution runs the
  exact candidate tests;
- complete only the student implementation and retain test-only helpers;
- compile Java and C++ with warnings enabled and parse Python;
- execute every available language suite and require a successful exit;
- record the result here before beginning the next assignment.
