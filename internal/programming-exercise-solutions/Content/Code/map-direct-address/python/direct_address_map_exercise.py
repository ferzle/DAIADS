from typing import Optional


class DirectAddressIntMap:
    def __init__(self, universe_size: int) -> None:
        if universe_size < 1:
            raise ValueError("universe_size must be positive")
        self._present = [False] * universe_size
        self._values = [0] * universe_size
        self._count = 0

    def universe_size(self) -> int:
        return len(self._present)

    def is_empty(self) -> bool:
        return self._count == 0

    def size(self) -> int:
        return self._count

    def contains_key(self, key: int) -> bool:
        self._check_key(key)
        return self._present[key]

    def get(self, key: int) -> Optional[int]:
        self._check_key(key)
        return self._values[key] if self._present[key] else None

    def put(self, key: int, value: int) -> Optional[int]:
        self._check_key(key)
        if self._present[key]:
            old_value = self._values[key]
            self._values[key] = value
            return old_value
        self._present[key] = True
        self._values[key] = value
        self._count += 1
        return None

    def remove(self, key: int) -> Optional[int]:
        self._check_key(key)
        if not self._present[key]:
            return None
        self._present[key] = False
        self._count -= 1
        return self._values[key]

    def clear(self) -> None:
        for i in range(len(self._present)):
            self._present[i] = False
        self._count = 0

    def _check_key(self, key: int) -> None:
        if key < 0 or key >= len(self._present):
            raise IndexError("key outside the universe")


failures = 0


def check(condition: bool, label: str) -> None:
    global failures
    if condition:
        print(f"pass: {label}")
    else:
        failures += 1
        print(f"FAIL: {label}")


def check_raises(action, label: str) -> None:
    global failures
    try:
        action()
        failures += 1
        print(f"FAIL: {label}")
    except IndexError:
        print(f"pass: {label}")


def test_map() -> None:
    map_ = DirectAddressIntMap(10)
    check(map_.universe_size() == 10 and map_.is_empty(), "new map records universe")
    check(map_.put(0, 0) is None and map_.put(9, -4) is None, "insert boundary keys")
    check(map_.contains_key(0) and map_.get(0) == 0, "zero value is present")
    check(map_.put(9, 12) == -4 and map_.size() == 2, "replace without growing")
    check(map_.remove(9) == 12 and map_.remove(9) is None, "remove once")
    check_raises(lambda: map_.get(-1), "reject negative key")
    check_raises(lambda: map_.put(10, 1), "reject key equal to universe size")
    map_.clear()
    check(map_.is_empty() and not map_.contains_key(0), "clear presence flags")
    check(map_.put(5, 50) is None, "reuse after clear")


def test_exhaustive_universe():
    universe = 100_000
    map_ = DirectAddressIntMap(universe)
    ok = all(map_.put(key, key - 50_000) is None for key in range(universe))
    ok = map_.size() == universe and ok
    ok = all(map_.get(key) == key - 50_000 for key in range(universe)) and ok
    ok = all(map_.remove(key) is not None for key in range(0, universe, 2)) and ok
    ok = all(map_.contains_key(key) == (key % 2 == 1) for key in range(universe)) and ok
    map_.clear()
    ok = map_.is_empty() and map_.put(universe - 1, 0) is None and ok
    check(ok, "100,000-key exhaustive universe workload")


if __name__ == "__main__":
    test_map()
    test_exhaustive_universe()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")

if __name__ == "__main__" and failures:
    raise SystemExit(1)
