from typing import Optional


class FixedCapacityMaxHeap:
    def __init__(self, capacity: int):
        if capacity < 1:
            raise ValueError("capacity must be positive")
        self._values: list[Optional[int]] = [None] * capacity
        self._size = 0

    def is_empty(self) -> bool:
        """Return whether the logical heap contains no keys."""
        # TODO
        return False

    def is_full(self) -> bool:
        """Return whether every position in the backing array is used."""
        # TODO
        return False

    def size(self) -> int:
        """Return the number of keys in the logical heap."""
        # TODO
        return -1

    @staticmethod
    def _parent(index: int) -> int:
        """Return the zero-based parent index. Precondition: index > 0."""
        # TODO
        return -1

    @staticmethod
    def _left_child(index: int) -> int:
        """Return the zero-based left-child index."""
        # TODO
        return -1

    @staticmethod
    def _right_child(index: int) -> int:
        """Return the zero-based right-child index."""
        # TODO
        return -1

    def insert(self, key: int) -> bool:
        """Insert key, or return False without changing a full heap."""
        # TODO:
        # 1. If the heap is full, return False without changing it.
        # 2. Place key at the end of the used range and increment _size.
        # 3. Restore max-heap order with _sift_up.
        # 4. Return True.
        return False

    def peek_max(self) -> Optional[int]:
        """Return the maximum, or None when the heap is empty."""
        # TODO: Otherwise return the root without changing the heap.
        return None

    def extract_max(self) -> Optional[int]:
        """Remove and return the maximum, or None when empty."""
        # TODO:
        # 1. Save the root.
        # 2. Move the final used key to the root, clear its old cell, and
        #    decrement _size.
        # 3. Restore max-heap order with _sift_down when nonempty.
        # 4. Return the saved maximum.
        return None

    def _sift_up(self, index: int) -> None:
        """Move one key toward the root until max-heap order is restored."""
        # TODO

    def _sift_down(self, index: int) -> None:
        """Move one key toward the leaves until max-heap order is restored."""
        # TODO: When both children exist, compare them and select the larger
        # one. Also handle the case in which only a left child exists.

    def has_valid_heap_order(self) -> bool:
        """Check every used parent-child pair; intended only for tests."""
        # TODO: This O(n) validation helper must not be called by the public
        # heap operations.
        return False

    def used_values_for_testing(self) -> list[int]:
        """Return a copy of the logical heap for the supplied tests."""
        return [
            value
            for value in self._values[: self._size]
            if value is not None
        ]


failures = 0


def check(actual, expected, label: str) -> None:
    global failures
    if actual == expected:
        print("pass:", label)
    else:
        failures += 1
        print(f"FAIL: {label} (expected {expected!r}, got {actual!r})")


def check_array(
    heap: FixedCapacityMaxHeap, expected: list[int], label: str
) -> None:
    check(heap.used_values_for_testing(), expected, label)


def test_core_operations() -> None:
    heap = FixedCapacityMaxHeap(7)

    check(heap.is_empty(), True, "new heap is empty")
    check(heap.is_full(), False, "new heap is not full")
    check(heap.size(), 0, "new heap has size zero")
    check(heap.peek_max(), None, "peek on empty heap")
    check(heap.extract_max(), None, "extract on empty heap")

    for key in [40, 70, 30, 90, 60, 80, 80]:
        check(heap.insert(key), True, f"insert {key}")
        check(
            heap.has_valid_heap_order(),
            True,
            f"heap order after inserting {key}",
        )

    check_array(
        heap, [90, 70, 80, 40, 60, 30, 80], "array after insertions"
    )
    check(heap.is_full(), True, "heap reports full")
    check(heap.insert(100), False, "insertion fails when full")
    check_array(
        heap,
        [90, 70, 80, 40, 60, 30, 80],
        "failed insertion leaves heap unchanged",
    )

    check(heap.peek_max(), 90, "peek returns the maximum")
    check(heap.size(), 7, "peek leaves size unchanged")
    check(heap.extract_max(), 90, "extract returns the maximum")
    check_array(
        heap, [80, 70, 80, 40, 60, 30], "array after extraction"
    )
    check(heap.has_valid_heap_order(), True, "heap order after extraction")


def test_only_left_child_and_negative_keys() -> None:
    heap = FixedCapacityMaxHeap(5)
    for key in [100, 90, 80, 70, 60]:
        heap.insert(key)

    check(heap.extract_max(), 100, "extract before left-only repair")
    check_array(
        heap,
        [90, 70, 80, 60],
        "siftDown handles an only-left-child step",
    )
    check(heap.has_valid_heap_order(), True, "left-only result is a heap")

    negatives = FixedCapacityMaxHeap(3)
    negatives.insert(-8)
    negatives.insert(-3)
    negatives.insert(-12)
    check(
        negatives.extract_max(),
        -3,
        "maximum is correct for negative keys",
    )


if __name__ == "__main__":
    test_core_operations()
    test_only_left_child_and_negative_keys()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")
