def sift_down(values: list[int], root: int, heap_size: int) -> None:
    """Restore max-heap order below root within values[:heap_size].

    The left and right subtrees of root are assumed to be max-heaps.

    Repeatedly swap the root with its larger child until heap order is
    restored. Entries outside the active heap are left untouched.
    """
    while 2 * root + 1 < heap_size:
        larger_child = 2 * root + 1
        right_child = larger_child + 1
        if (right_child < heap_size
                and values[right_child] > values[larger_child]):
            larger_child = right_child
        if values[root] >= values[larger_child]:
            return
        values[root], values[larger_child] = values[larger_child], values[root]
        root = larger_child


def build_max_heap(values: list[int]) -> None:
    """Rearrange values into a max-heap using bottom-up construction."""
    for root in range(len(values) // 2 - 1, -1, -1):
        sift_down(values, root, len(values))


def heap_sort(values: list[int]) -> None:
    """Sort values in place in nondecreasing order using Heapsort."""
    build_max_heap(values)
    for heap_size in range(len(values) - 1, 0, -1):
        values[0], values[heap_size] = values[heap_size], values[0]
        sift_down(values, 0, heap_size)


def is_max_heap(values: list[int], heap_size: int) -> bool:
    """Return whether values[:heap_size] satisfies max-heap order."""
    for child in range(1, heap_size):
        parent = (child - 1) // 2
        if values[parent] < values[child]:
            return False
    return True


failures = 0


def check(actual, expected, label: str) -> None:
    global failures
    if actual == expected:
        print("pass:", label)
    else:
        failures += 1
        print(f"FAIL: {label} (expected {expected!r}, got {actual!r})")


def test_known_construction() -> None:
    values = [7, 2, 9, 1, 6, 8, 3, 5, 4]
    build_max_heap(values)
    check(
        values,
        [9, 6, 8, 5, 2, 7, 3, 1, 4],
        "known bottom-up construction",
    )
    check(
        is_max_heap(values, len(values)),
        True,
        "constructed array has max-heap order",
    )

    duplicates_and_negatives = [-4, 7, 7, -9, 0, 7, -4]
    build_max_heap(duplicates_and_negatives)
    check(
        is_max_heap(duplicates_and_negatives, len(duplicates_and_negatives)),
        True,
        "construction handles duplicates and negative keys",
    )


def test_active_prefix_boundary() -> None:
    values = [2, 9, 8, 7, 6, 5, 1000, 2000]
    sift_down(values, 0, 6)
    check(
        values,
        [9, 7, 8, 2, 6, 5, 1000, 2000],
        "sift_down stays inside the active prefix",
    )
    check(
        is_max_heap(values, 6),
        True,
        "active prefix has max-heap order after sift_down",
    )


def test_heap_sort() -> None:
    cases = [
        (
            [7, 2, 9, 1, 6, 8, 3, 5, 4],
            [1, 2, 3, 4, 5, 6, 7, 8, 9],
            "sorts a typical input",
        ),
        (
            [-5, 3, -5, 0, 12, 3, -1],
            [-5, -5, -1, 0, 3, 3, 12],
            "sorts duplicate and negative keys",
        ),
        (
            [1, 2, 3, 4, 5, 6],
            [1, 2, 3, 4, 5, 6],
            "sorts an already sorted input",
        ),
        (
            [6, 5, 4, 3, 2, 1],
            [1, 2, 3, 4, 5, 6],
            "sorts a reverse-sorted input",
        ),
        ([], [], "sorts an empty input"),
        ([42], [42], "sorts a one-element input"),
    ]

    for values, expected, label in cases:
        heap_sort(values)
        check(values, expected, label)


def test_deterministic_large_arrays():
    for length in (0, 1, 2, 3, 31, 32, 33, 1000, 100_000):
        state = 0x5EED
        values = []
        for _ in range(length):
            state = (state * 1103515245 + 12345) & 0x7FFFFFFF
            values.append(state % 101 - 50)
        expected = sorted(values)
        heap_sort(values)
        check(values, expected, f"deterministic heap_sort length {length}")
        heap = expected.copy()
        build_max_heap(heap)
        check(is_max_heap(heap, len(heap)), True, f"build_max_heap length {length}")


if __name__ == "__main__":
    test_known_construction()
    test_active_prefix_boundary()
    test_heap_sort()
    test_deterministic_large_arrays()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")

if __name__ == "__main__" and failures:
    raise SystemExit(1)
