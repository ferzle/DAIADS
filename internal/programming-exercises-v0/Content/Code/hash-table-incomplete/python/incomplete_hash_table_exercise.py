from enum import Enum, auto


class InsertResult(Enum):
    INSERTED = auto()
    ALREADY_PRESENT = auto()
    COLLISION = auto()


class IncompleteHashTable:
    def __init__(self, capacity):
        if capacity < 1:
            raise ValueError("capacity must be positive")
        self._table = [None] * capacity

    def _home_position(self, key):
        self._check_key(key)
        return key % len(self._table)

    @staticmethod
    def _check_key(key):
        if key < 0:
            raise ValueError("key must be nonnegative")

    def insert(self, key):
        # TODO: Inspect only key's home position. Store the key if that
        # position is empty, and return the appropriate InsertResult.
        return InsertResult.COLLISION

    def contains(self, key):
        # TODO: Return whether key is stored at its home position.
        return False

    def remove(self, key):
        # TODO: If key is stored at its home position, mark that position
        # empty and return True. Otherwise make no change and return False.
        return False


failures = 0


def check(condition, label):
    global failures
    if condition:
        print(f"pass: {label}")
    else:
        failures += 1
        print(f"FAIL: {label}")


def check_raises(action, label):
    global failures
    try:
        action()
        failures += 1
        print(f"FAIL: {label}")
    except ValueError:
        print(f"pass: {label}")


def test_table():
    check_raises(lambda: IncompleteHashTable(0), "reject nonpositive capacity")
    values = IncompleteHashTable(7)
    check(not values.contains(8), "new table does not contain 8")
    check(values.insert(8) is InsertResult.INSERTED, "insert 8 at index 1")
    check(values.insert(10) is InsertResult.INSERTED, "insert 10 at index 3")
    check(values.insert(19) is InsertResult.INSERTED, "insert 19 at index 5")
    check(values.contains(8) and values.contains(10) and values.contains(19),
          "contains finds inserted keys")
    check(values.insert(8) is InsertResult.ALREADY_PRESENT,
          "report a duplicate separately")
    check(values.insert(24) is InsertResult.COLLISION, "24 collides with 10 at index 3")
    check(not values.contains(24) and values.contains(10),
          "a collision does not overwrite 10")
    check(not values.remove(24) and values.contains(10),
          "removing colliding absent key preserves 10")
    check(values.remove(10) and not values.contains(10), "remove stored key")
    check(not values.remove(10), "cannot remove a key twice")
    check(values.insert(24) is InsertResult.INSERTED, "removed position can be reused")
    check_raises(lambda: values.insert(-1), "reject negative insert key")
    check_raises(lambda: values.contains(-1), "reject negative lookup key")
    check_raises(lambda: values.remove(-1), "reject negative removal key")


if __name__ == "__main__":
    test_table()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")
