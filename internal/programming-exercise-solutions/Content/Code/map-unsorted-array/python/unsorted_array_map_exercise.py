from typing import Optional


class UnsortedArrayIntMap:
    def __init__(self, initial_capacity: int) -> None:
        if initial_capacity < 1:
            raise ValueError("initial_capacity must be positive")
        self._keys = [0] * initial_capacity
        self._values = [0] * initial_capacity
        self._count = 0

    def is_empty(self) -> bool:
        return self._count == 0

    def size(self) -> int:
        return self._count

    def clear(self) -> None:
        self._count = 0

    def contains_key(self, key: int) -> bool:
        return self._index_of(key) != -1

    def get(self, key: int) -> Optional[int]:
        index = self._index_of(key)
        return None if index == -1 else self._values[index]

    def put(self, key: int, value: int) -> Optional[int]:
        index = self._index_of(key)
        if index != -1:
            old_value = self._values[index]
            self._values[index] = value
            return old_value
        self._ensure_capacity()
        self._keys[self._count] = key
        self._values[self._count] = value
        self._count += 1
        return None

    def remove(self, key: int) -> Optional[int]:
        index = self._index_of(key)
        if index == -1:
            return None
        old_value = self._values[index]
        self._keys[index] = self._keys[self._count - 1]
        self._values[index] = self._values[self._count - 1]
        self._count -= 1
        return old_value

    def entries(self) -> list[tuple[int, int]]:
        return [(self._keys[i], self._values[i]) for i in range(self._count)]

    def _index_of(self, key: int) -> int:
        for i in range(self._count):
            if self._keys[i] == key:
                return i
        return -1

    def _ensure_capacity(self) -> None:
        if self._count == len(self._keys):
            self._keys.extend([0] * len(self._keys))
            self._values.extend([0] * len(self._values))


failures = 0


def check(condition: bool, label: str) -> None:
    global failures
    if condition:
        print(f"pass: {label}")
    else:
        failures += 1
        print(f"FAIL: {label}")


def test_map() -> None:
    map_ = UnsortedArrayIntMap(2)
    check(map_.is_empty() and map_.size() == 0, "new map is empty")
    check(map_.get(4) is None and map_.remove(4) is None, "missing operations")
    check(map_.put(8, 80) is None and map_.put(3, 0) is None, "insert entries")
    check(map_.contains_key(3) and map_.get(3) == 0, "stored zero is present")
    check(map_.put(8, 81) == 80 and map_.size() == 2, "replace value")
    check(map_.put(11, 110) is None and map_.put(-2, -20) is None, "resize together")
    check(map_.remove(3) == 0, "remove interior entry")
    check(map_.entries() == [(8, 81), (-2, -20), (11, 110)], "copy aligned final entry")
    check(map_.remove(11) == 110 and map_.remove(11) is None, "remove once")
    map_.clear()
    check(map_.is_empty() and map_.put(5, 50) is None, "clear and reuse")


def test_large_mixed_workload():
    map_ = UnsortedArrayIntMap(1)
    for key in range(1000):
        check(map_.put(key, key * 3) is None, f"insert key {key}")
    check(map_.size() == 1000, "size after 1000 inserts")
    for key in range(1000):
        check(map_.get(key) == key * 3, f"get key {key}")
    for key in range(0, 1000, 3):
        check(map_.put(key, -key) == key * 3, f"replace key {key}")
    check(map_.size() == 1000, "replacement preserves size")
    for key in range(0, 1000, 2):
        check(map_.remove(key) is not None, f"remove key {key}")
    for key in range(1000):
        check(map_.contains_key(key) == (key % 2 == 1), f"membership for key {key}")


if __name__ == "__main__":
    test_map()
    test_large_mixed_workload()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")

if __name__ == "__main__" and failures:
    raise SystemExit(1)
