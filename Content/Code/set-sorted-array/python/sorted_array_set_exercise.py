class SortedArrayIntSet:
    def __init__(self, initial_capacity):
        if initial_capacity < 1:
            raise ValueError("initial_capacity must be positive")
        self._keys = [0] * initial_capacity
        self._count = 0

    def is_empty(self):
        # TODO: Return whether the used portion of the array is empty.
        return False

    def size(self):
        # TODO: Return the number of distinct stored keys, not the capacity.
        return -1

    def clear(self):
        # TODO: Remove all logical contents. The allocated list may be reused.
        pass

    def contains(self, key):
        # TODO: Use binary search over self._keys[0:self._count].
        return False

    def add(self, key):
        # TODO: Find key or its insertion position with binary search. Reject
        # duplicates; otherwise grow, shift right, insert, and update the count.
        return False

    def remove(self, key):
        # TODO: Locate key with binary search. If present, shift later keys
        # left, decrement the count exactly once, and return True.
        return False

    def to_list(self):
        """Return the stored keys in their sorted iteration order."""
        return self._keys[:self._count]

    def _lower_bound(self, key):
        # TODO: Return the first index in the used range whose value is greater
        # than or equal to key. Return self._count if no such index exists.
        return -1

    def _ensure_capacity(self):
        # TODO: When full, double the list capacity and copy all used keys.
        pass


failures = 0


def check(actual, expected, label):
    global failures
    if actual == expected:
        print(f"pass: {label}")
    else:
        failures += 1
        print(f"FAIL: {label} (expected {expected!r}, got {actual!r})")


def test_set():
    values = SortedArrayIntSet(2)
    check(values.is_empty(), True, "new set is empty")
    check(values.size(), 0, "new set has size zero")
    check(values.contains(4), False, "missing key is not contained")
    check(values.remove(4), False, "removing a missing key changes nothing")

    check(values.add(8), True, "add first key")
    check(values.add(3), True, "insert before a larger key")
    check(values.add(11), True, "append and grow the backing array")
    check(values.add(6), True, "insert into the middle")
    check(values.add(-2), True, "insert a new minimum")
    check(values.to_list(), [-2, 3, 6, 8, 11],
          "iteration order remains sorted")

    check(values.add(6), False, "reject duplicate key")
    check(values.size(), 5, "duplicate does not change size")
    check(values.contains(-2), True, "find the first key")
    check(values.contains(8), True, "find an interior key")
    check(values.contains(12), False, "reject a key beyond the used range")

    check(values.remove(6), True, "remove an interior key")
    check(values.to_list(), [-2, 3, 8, 11], "removal shifts the suffix left")
    check(values.remove(-2), True, "remove the first key")
    check(values.remove(11), True, "remove the final key")
    check(values.remove(11), False, "cannot remove a key twice")
    check(values.to_list(), [3, 8], "remaining keys stay sorted")

    values.clear()
    check(values.is_empty(), True, "clear empties the set")
    check(values.size(), 0, "size is zero after clear")
    check(values.add(5), True, "set can be reused after clear")


if __name__ == "__main__":
    test_set()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")
