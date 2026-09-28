from typing import Optional


class UnsortedArrayIntMap:
    def __init__(self, initial_capacity: int) -> None:
        if initial_capacity < 1:
            raise ValueError("initial_capacity must be positive")
        self._keys = [0] * initial_capacity
        self._values = [0] * initial_capacity
        self._count = 0

    def is_empty(self) -> bool:
        # TODO: Return whether there are no used entries.
        return False

    def size(self) -> int:
        # TODO: Return the number of entries, not the capacity.
        return -1

    def clear(self) -> None:
        # TODO: Remove all logical entries while retaining the lists.
        pass

    def contains_key(self, key: int) -> bool:
        # TODO: Search only the used prefix.
        return False

    def get(self, key: int) -> Optional[int]:
        # TODO: Return the aligned value, or None when key is absent.
        return None

    def put(self, key: int, value: int) -> Optional[int]:
        # TODO: Replace and return an old value when key is present.
        # Otherwise grow if needed, append one aligned entry, and return None.
        return None

    def remove(self, key: int) -> Optional[int]:
        # TODO: If present, save its value, copy BOTH parts of the final entry
        # into its gap, decrement the count, and return the saved value.
        return None

    def entries(self) -> list[tuple[int, int]]:
        return [(self._keys[i], self._values[i]) for i in range(self._count)]

    def _index_of(self, key: int) -> int:
        # TODO: Return key's used index, or -1 if absent.
        return -1

    def _ensure_capacity(self) -> None:
        # TODO: When full, double BOTH lists and preserve all entries.
        pass


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


if __name__ == "__main__":
    test_map()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")
