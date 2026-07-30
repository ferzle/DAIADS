def sift_down(values: list[int], root: int, heap_size: int) -> None:
    """Restore max-heap order below root within values[:heap_size].

    The left and right subtrees of root are assumed to be max-heaps.

    TODO: Repeatedly compare the root with its existing children, swap with
    the larger child when necessary, and continue from that child's former
    position. Do not inspect or modify values at an index greater than or
    equal to heap_size.
    """


def build_max_heap(values: list[int]) -> None:
    """Rearrange values into a max-heap using bottom-up construction."""
    # TODO: Starting with the final internal node, call sift_down once for
    # each internal node in decreasing index order.


def heap_sort(values: list[int]) -> None:
    """Sort values in place in nondecreasing order using Heapsort."""
    # TODO:
    # 1. Convert the entire list into a max-heap.
    # 2. Repeatedly swap the root with the final entry in the active heap,
    #    shrink the active heap by one, and sift the new root down.


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


if __name__ == "__main__":
    test_known_construction()
    test_active_prefix_boundary()
    test_heap_sort()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")
