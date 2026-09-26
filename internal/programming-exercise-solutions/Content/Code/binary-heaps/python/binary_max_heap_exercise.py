from typing import Optional


class FixedCapacityMaxHeap:
    def __init__(self, capacity: int):
        if capacity < 1:
            raise ValueError("capacity must be positive")
        self._values: list[Optional[int]] = [None] * capacity
        self._size = 0

    def is_empty(self) -> bool:
        """Return whether the logical heap contains no keys."""
        return self._size == 0

    def is_full(self) -> bool:
        """Return whether every position in the backing array is used."""
        return self._size == len(self._values)

    def size(self) -> int:
        """Return the number of keys in the logical heap."""
        return self._size

    @staticmethod
    def _parent(index: int) -> int:
        """Return the zero-based parent index. Precondition: index > 0."""
        return (index - 1) // 2

    @staticmethod
    def _left_child(index: int) -> int:
        """Return the zero-based left-child index."""
        return 2 * index + 1

    @staticmethod
    def _right_child(index: int) -> int:
        """Return the zero-based right-child index."""
        return 2 * index + 2

    def insert(self, key: int) -> bool:
        """Insert key, or return False without changing a full heap."""
        if self.is_full():
            return False
        self._values[self._size] = key
        self._sift_up(self._size)
        self._size += 1
        return True

    def peek_max(self) -> Optional[int]:
        """Return the maximum, or None when the heap is empty."""
        return None if self.is_empty() else self._values[0]

    def extract_max(self) -> Optional[int]:
        """Remove and return the maximum, or None when empty."""
        if self.is_empty():
            return None
        maximum = self._values[0]
        self._size -= 1
        self._values[0] = self._values[self._size]
        self._values[self._size] = None
        if not self.is_empty():
            self._sift_down(0)
        return maximum

    def _sift_up(self, index: int) -> None:
        """Move one key toward the root until max-heap order is restored."""
        while index > 0:
            parent = self._parent(index)
            current_value = self._values[index]
            parent_value = self._values[parent]
            assert current_value is not None and parent_value is not None
            if current_value <= parent_value:
                return
            self._values[index], self._values[parent] = parent_value, current_value
            index = parent

    def _sift_down(self, index: int) -> None:
        """Move one key toward the leaves until max-heap order is restored."""
        while self._left_child(index) < self._size:
            larger_child = self._left_child(index)
            right = self._right_child(index)
            if right < self._size:
                left_value = self._values[larger_child]
                right_value = self._values[right]
                assert left_value is not None and right_value is not None
                if right_value > left_value:
                    larger_child = right
            child_value = self._values[larger_child]
            current_value = self._values[index]
            assert child_value is not None and current_value is not None
            if child_value <= current_value:
                return
            self._values[index], self._values[larger_child] = child_value, current_value
            index = larger_child

    def has_valid_heap_order(self) -> bool:
        """Check every used parent-child pair; intended only for tests."""
        for child in range(1, self._size):
            child_value = self._values[child]
            parent_value = self._values[self._parent(child)]
            if child_value is None or parent_value is None or child_value > parent_value:
                return False
        return True

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


def test_large_deterministic_drain():
    expected = []
    state = 0x5EED
    heap = FixedCapacityMaxHeap(1000)
    for operation in range(1000):
        state = (state * 1103515245 + 12345) & 0x7FFFFFFF
        value = state % 2001 - 1000
        expected.append(value)
        check(heap.insert(value), True, f"large insert {operation}")
        check(heap.has_valid_heap_order(), True, f"heap order after large insert {operation}")
    expected.sort(reverse=True)
    for operation, value in enumerate(expected):
        check(heap.extract_max(), value, f"large extraction {operation}")
        check(heap.has_valid_heap_order(), True, f"heap order after large extraction {operation}")
    check(heap.is_empty(), True, "empty after large drain")
    check(heap.insert(42), True, "reuse after large drain")
    check(heap.extract_max(), 42, "extract reused value")


def test_hundred_thousand_aggregate_operations():
    count = 100_000
    expected = []
    state = 0xC0FFEE
    heap = FixedCapacityMaxHeap(count)
    ok = True
    for _ in range(count):
        state = (state * 1103515245 + 12345) & 0x7FFFFFFF
        expected.append(state)
        ok = heap.insert(state) and ok
    expected.sort(reverse=True)
    ok = all(heap.extract_max() == value for value in expected) and ok
    check(ok and heap.is_empty() and heap.has_valid_heap_order(), True,
          "100,000 aggregate insertions and extractions")


if __name__ == "__main__":
    test_core_operations()
    test_only_left_child_and_negative_keys()
    test_large_deterministic_drain()
    test_hundred_thousand_aggregate_operations()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")

if __name__ == "__main__" and failures:
    raise SystemExit(1)
