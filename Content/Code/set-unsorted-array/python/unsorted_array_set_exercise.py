class UnsortedArrayIntSet:
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
        # TODO: Search self._keys[0:self._count] sequentially.
        return False

    def add(self, key):
        # TODO: Reject duplicates, grow if necessary, append the new key,
        # increment self._count, and report whether the set changed.
        return False

    def remove(self, key):
        # TODO: Find the key, fill its gap with the final used key, decrement
        # self._count exactly once, and report whether the set changed.
        return False

    def to_list(self):
        """Return the current iteration order for testing."""
        return self._keys[:self._count]

    def _ensure_capacity(self):
        # TODO: If the array is full, replace it with a list having twice the
        # capacity and copy all used keys into the new list.
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
    values = UnsortedArrayIntSet(2)
    check(values.is_empty(), True, "new set is empty")
    check(values.size(), 0, "new set has size zero")
    check(values.contains(4), False, "missing key is not contained")
    check(values.remove(4), False, "removing a missing key changes nothing")

    check(values.add(8), True, "add first key")
    check(values.add(3), True, "add second key")
    check(values.add(8), False, "reject duplicate key")
    check(values.size(), 2, "duplicate does not change size")
    check(values.to_list(), [8, 3], "keys append while space remains")

    check(values.add(11), True, "add grows the backing array")
    check(values.add(-2), True, "negative keys are supported")
    check(values.contains(11), True, "contains finds a stored key")
    check(values.size(), 4, "size after resizing")

    check(values.remove(3), True, "remove an interior key")
    check(values.to_list(), [8, -2, 11],
          "interior gap is filled with the final key")
    check(values.remove(11), True, "remove the final used key")
    check(values.contains(11), False, "removed key is absent")
    check(values.remove(11), False, "cannot remove a key twice")

    values.clear()
    check(values.is_empty(), True, "clear empties the set")
    check(values.size(), 0, "size is zero after clear")
    check(values.add(5), True, "set can be reused after clear")


if __name__ == "__main__":
    test_set()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")
