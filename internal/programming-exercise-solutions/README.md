# Programming Exercise Solutions

This directory mirrors the enhanced exercises in
`internal/programming-exercises-v2/Content/Code/`. Each completed solution
retains the full v2 test suite in the same self-contained source file, so the
implementation and its verification remain together.

All 32 assignments have verified solutions in Java, C++, and Python where
those versions exist. See `SOLUTION_STATUS.md` for the per-assignment results.

## Run every solution suite

From the repository root, run:

```bash
python3 internal/programming-exercise-solutions/run_all_tests.py
```

The runner recursively discovers every `.java`, `.cpp`, and `.py` file below
`Content/Code/`, so a later exercise is included automatically when its source
file is added there. Results are grouped by assignment directory and then by
language, which keeps the implementations of the same exercise together.

Useful options:

```bash
# Run one language only.
python3 internal/programming-exercise-solutions/run_all_tests.py --language python

# Show the output from passing test programs too.
python3 internal/programming-exercise-solutions/run_all_tests.py --verbose

# Change the per-command timeout from its 600-second default.
python3 internal/programming-exercise-solutions/run_all_tests.py --timeout 900
```

The required tools are `javac` and `java` for Java, a C++17-capable `g++` for
C++, and Python 3 for Python. Missing tools are reported before any suite runs.
The complete run takes several minutes on the current repository. In
particular, the Python open-addressing suite takes about four minutes on the
development machine, which is why the per-command default is ten minutes.

The script does not write build products into an assignment directory. Java
class files, C++ executables, Python bytecode, and captured stdout/stderr all go
into one operating-system temporary directory, which is removed automatically
when the run finishes or is interrupted normally. Copying the source tree is
therefore unnecessary.

Each source file is compiled or parsed first and then run as a self-contained
test program. The individual programs do not use one uniform output format:
most print passing checks, failures, or both, and some print a final summary.
The reliable interface is their process exit status. Exit status zero means a
suite passed; a compilation error, timeout, or nonzero test exit means it
failed. The runner reports one `PASS` or `FAIL` line per source file, prints
up to the last 200 captured output lines for failures, gives a final count,
and itself exits nonzero if any suite fails. `--verbose` prints complete output
for passing suites. This makes the runner suitable for both manual checks and
automation.

These files are instructor material. Production Apache is configured to deny
HTTP access to the containing `internal/` directory.
