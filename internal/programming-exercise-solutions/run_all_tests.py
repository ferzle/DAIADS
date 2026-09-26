#!/usr/bin/env python3
"""Compile and run every programming-exercise solution test suite."""

from __future__ import annotations

import argparse
from dataclasses import dataclass
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import time


ROOT = Path(__file__).resolve().parent
CODE_ROOT = ROOT / "Content" / "Code"
LANGUAGE_ORDER = {"java": 0, "cpp": 1, "python": 2}
SUFFIX_LANGUAGE = {".java": "java", ".cpp": "cpp", ".py": "python"}


@dataclass(frozen=True)
class TestCase:
    source: Path
    relative_path: Path
    assignment: str
    language: str


@dataclass
class CommandResult:
    returncode: int
    elapsed: float
    timed_out: bool = False


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Recursively discover, compile, and run all self-contained "
            "solution suites under Content/Code."
        )
    )
    parser.add_argument(
        "--language",
        choices=("all", "java", "cpp", "python"),
        default="all",
        help="run only one language (default: all)",
    )
    parser.add_argument(
        "--timeout",
        type=float,
        default=600.0,
        help="maximum seconds for each compile or run command (default: 600)",
    )
    parser.add_argument(
        "--verbose",
        action="store_true",
        help="show output from passing suites as well as failing suites",
    )
    arguments = parser.parse_args()
    if arguments.timeout <= 0:
        parser.error("--timeout must be greater than zero")
    return arguments


def discover_tests(language_filter: str) -> list[TestCase]:
    tests: list[TestCase] = []
    for source in CODE_ROOT.rglob("*"):
        language = SUFFIX_LANGUAGE.get(source.suffix)
        if not source.is_file() or language is None:
            continue
        if language_filter != "all" and language != language_filter:
            continue
        relative_path = source.relative_to(CODE_ROOT)
        assignment = relative_path.parts[0]
        tests.append(TestCase(source, relative_path, assignment, language))
    tests.sort(
        key=lambda test: (
            test.assignment,
            LANGUAGE_ORDER[test.language],
            test.relative_path.as_posix(),
        )
    )
    return tests


def required_tools(tests: list[TestCase]) -> dict[str, tuple[str, ...]]:
    languages = {test.language for test in tests}
    requirements: dict[str, tuple[str, ...]] = {}
    if "java" in languages:
        requirements["java"] = ("javac", "java")
    if "cpp" in languages:
        requirements["cpp"] = ("g++",)
    if "python" in languages:
        requirements["python"] = (sys.executable,)
    return requirements


def check_tools(tests: list[TestCase]) -> bool:
    missing: list[str] = []
    for tools in required_tools(tests).values():
        for tool in tools:
            if shutil.which(tool) is None:
                missing.append(tool)
    if missing:
        print("Missing required tool(s): " + ", ".join(sorted(set(missing))))
        return False
    return True


def run_command(
    command: list[str],
    *,
    cwd: Path,
    environment: dict[str, str],
    stdout_path: Path,
    stderr_path: Path,
    timeout: float,
) -> CommandResult:
    started = time.monotonic()
    with stdout_path.open("wb") as stdout_file, stderr_path.open("wb") as stderr_file:
        try:
            completed = subprocess.run(
                command,
                cwd=cwd,
                env=environment,
                stdout=stdout_file,
                stderr=stderr_file,
                timeout=timeout,
                check=False,
            )
            return CommandResult(completed.returncode, time.monotonic() - started)
        except subprocess.TimeoutExpired:
            return CommandResult(124, time.monotonic() - started, timed_out=True)


def commands_for(
    test: TestCase, work_directory: Path
) -> tuple[list[str], list[str], dict[str, str]]:
    environment = os.environ.copy()
    if test.language == "java":
        classes = work_directory / "classes"
        classes.mkdir()
        compile_command = [
            "javac", "-Xlint:all", "-d", str(classes), str(test.source)
        ]
        run_command_line = ["java", "-cp", str(classes), test.source.stem]
    elif test.language == "cpp":
        executable = work_directory / "suite"
        compile_command = [
            "g++", "-std=c++17", "-Wall", "-Wextra", "-pedantic",
            str(test.source), "-o", str(executable),
        ]
        run_command_line = [str(executable)]
    else:
        pycache = work_directory / "pycache"
        environment["PYTHONPYCACHEPREFIX"] = str(pycache)
        compile_command = [sys.executable, "-m", "py_compile", str(test.source)]
        run_command_line = [sys.executable, "-B", str(test.source)]
    return compile_command, run_command_line, environment


def print_log(path: Path, heading: str, maximum_lines: int | None = None) -> None:
    content = path.read_text(encoding="utf-8", errors="replace")
    if not content:
        return
    lines = content.splitlines(keepends=True)
    omitted = 0
    if maximum_lines is not None and len(lines) > maximum_lines:
        omitted = len(lines) - maximum_lines
        lines = lines[-maximum_lines:]
    print(f"--- {heading} ---")
    if omitted:
        print(f"... {omitted} earlier line(s) omitted ...")
    displayed = "".join(lines)
    print(displayed, end="" if displayed.endswith("\n") else "\n")


def run_test(
    test: TestCase,
    work_directory: Path,
    *,
    timeout: float,
    verbose: bool,
) -> tuple[bool, float]:
    compile_command, run_command_line, environment = commands_for(
        test, work_directory
    )
    compile_stdout = work_directory / "compile.stdout.txt"
    compile_stderr = work_directory / "compile.stderr.txt"
    run_stdout = work_directory / "run.stdout.txt"
    run_stderr = work_directory / "run.stderr.txt"

    compile_result = run_command(
        compile_command,
        cwd=test.source.parent,
        environment=environment,
        stdout_path=compile_stdout,
        stderr_path=compile_stderr,
        timeout=timeout,
    )
    if compile_result.returncode != 0:
        reason = "compile timeout" if compile_result.timed_out else "compile failed"
        print(f"FAIL  {test.relative_path.as_posix()} ({reason})")
        print_log(compile_stdout, "compiler stdout")
        print_log(compile_stderr, "compiler stderr")
        return False, compile_result.elapsed

    run_result = run_command(
        run_command_line,
        cwd=test.source.parent,
        environment=environment,
        stdout_path=run_stdout,
        stderr_path=run_stderr,
        timeout=timeout,
    )
    elapsed = compile_result.elapsed + run_result.elapsed
    if run_result.returncode != 0:
        reason = "test timeout" if run_result.timed_out else (
            f"test exited {run_result.returncode}"
        )
        print(f"FAIL  {test.relative_path.as_posix()} ({reason}, {elapsed:.2f}s)")
        print_log(run_stdout, "test stdout", maximum_lines=200)
        print_log(run_stderr, "test stderr", maximum_lines=200)
        return False, elapsed

    print(f"PASS  {test.relative_path.as_posix()} ({elapsed:.2f}s)")
    if verbose:
        print_log(compile_stdout, "compiler stdout")
        print_log(compile_stderr, "compiler stderr")
        print_log(run_stdout, "test stdout")
        print_log(run_stderr, "test stderr")
    return True, elapsed


def main() -> int:
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(line_buffering=True)
    arguments = parse_arguments()
    tests = discover_tests(arguments.language)
    if not tests:
        print(f"No solution source files found under {CODE_ROOT}")
        return 2
    if not check_tools(tests):
        return 2

    language_counts = {
        language: sum(test.language == language for test in tests)
        for language in LANGUAGE_ORDER
    }
    counts = ", ".join(
        f"{language}={count}"
        for language, count in language_counts.items()
        if count
    )
    print(f"Discovered {len(tests)} suites ({counts}).")
    print(f"Temporary build products will be removed automatically.\n")

    passed = 0
    failed = 0
    total_elapsed = 0.0
    current_assignment: str | None = None

    with tempfile.TemporaryDirectory(prefix="daiads-solution-tests-") as temporary:
        temporary_root = Path(temporary)
        for number, test in enumerate(tests):
            if test.assignment != current_assignment:
                current_assignment = test.assignment
                print(f"[{current_assignment}]")
            test_work = temporary_root / f"suite-{number:03d}"
            test_work.mkdir()
            succeeded, elapsed = run_test(
                test,
                test_work,
                timeout=arguments.timeout,
                verbose=arguments.verbose,
            )
            total_elapsed += elapsed
            if succeeded:
                passed += 1
            else:
                failed += 1
            if number + 1 < len(tests) and tests[number + 1].assignment != current_assignment:
                print()

    print(
        f"Summary: {passed} passed, {failed} failed, "
        f"{len(tests)} total ({total_elapsed:.2f}s command time)."
    )
    return 0 if failed == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
