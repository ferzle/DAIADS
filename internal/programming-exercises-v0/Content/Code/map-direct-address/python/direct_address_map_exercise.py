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
        # TODO: Return whether no keys are present.
        return False

    def size(self) -> int:
        # TODO: Return the number of present keys.
        return -1

    def contains_key(self, key: int) -> bool:
        self._check_key(key)
        # TODO: Inspect the presence entry, not the stored value.
        return False

    def get(self, key: int) -> Optional[int]:
        self._check_key(key)
        # TODO: Return the value only when its presence entry is True.
        return None

    def put(self, key: int, value: int) -> Optional[int]:
        self._check_key(key)
        # TODO: Replace and return the old value if present. Otherwise mark
        # present, store the value, increment the count, and return None.
        return None

    def remove(self, key: int) -> Optional[int]:
        self._check_key(key)
        # TODO: If present, clear the flag, decrement count, and return the value.
        return None

    def clear(self) -> None:
        # TODO: Clear every presence entry and reset the count. Values at absent
        # positions do not need to be erased.
        pass

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
    try:
        DirectAddressIntMap(0)
        check(False, "reject nonpositive universe size")
    except ValueError:
        check(True, "reject nonpositive universe size")
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


if __name__ == "__main__":
    test_map()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")
