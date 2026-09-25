class UnsortedArrayIntSet:
    def __init__(self, initial_capacity):
        if initial_capacity < 1:
            raise ValueError("initial_capacity must be positive")
        self._keys = [0] * initial_capacity
        self._count = 0

    def is_empty(self):
        return self._count == 0

    def size(self):
        return self._count

    def clear(self):
        self._count = 0

    def contains(self, key):
        for i in range(self._count):
            if self._keys[i] == key:
                return True
        return False

    def add(self, key):
        if self.contains(key):
            return False
        self._ensure_capacity()
        self._keys[self._count] = key
        self._count += 1
        return True

    def remove(self, key):
        for i in range(self._count):
            if self._keys[i] == key:
                self._keys[i] = self._keys[self._count - 1]
                self._count -= 1
                return True
        return False

    def to_list(self):
        """Return the current iteration order for testing."""
        return self._keys[:self._count]

    def _ensure_capacity(self):
        if self._count == len(self._keys):
            larger = [0] * (len(self._keys) * 2)
            larger[:self._count] = self._keys[:self._count]
            self._keys = larger


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


def test_large_differential_workload():
    values = UnsortedArrayIntSet(1)
    for key in range(1000):
        check(values.add(key), True, f"add unique key {key}")
    check(values.size(), 1000, "size after 1000 unique additions")
    for key in range(1000):
        check(values.contains(key), True, f"contains key {key}")
        check(values.add(key), False, f"reject duplicate key {key}")
    for key in range(0, 1000, 2):
        check(values.remove(key), True, f"remove key {key}")
    check(values.size(), 500, "size after removing even keys")
    for key in range(1000):
        check(values.contains(key), key % 2 == 1, f"membership after removals for {key}")
    values.clear()
    check(values.is_empty(), True, "empty after large clear")
    check(values.add(0), True, "reuse after clear")


if __name__ == "__main__":
    test_set()
    test_large_differential_workload()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")

if __name__ == "__main__" and failures:
    raise SystemExit(1)
