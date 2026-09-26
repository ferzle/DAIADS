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
        index = self._home_position(key)
        if self._table[index] == key:
            return InsertResult.ALREADY_PRESENT
        if self._table[index] is not None:
            return InsertResult.COLLISION
        self._table[index] = key
        return InsertResult.INSERTED

    def contains(self, key):
        index = self._home_position(key)
        return self._table[index] == key

    def remove(self, key):
        index = self._home_position(key)
        if self._table[index] != key:
            return False
        self._table[index] = None
        return True

    def table_snapshot_for_testing(self):
        return self._table.copy()


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
    values = IncompleteHashTable(7)
    check(not values.contains(8), "new table does not contain 8")
    check(values.insert(8) is InsertResult.INSERTED, "insert 8 at index 1")
    check(values.insert(10) is InsertResult.INSERTED, "insert 10 at index 3")
    check(values.insert(19) is InsertResult.INSERTED, "insert 19 at index 5")
    check(values.table_snapshot_for_testing() == [None, 8, None, 10, None, 19, None],
          "keys occupy only their home positions")
    check(values.contains(8) and values.contains(10) and values.contains(19),
          "contains finds inserted keys")
    check(values.insert(8) is InsertResult.ALREADY_PRESENT,
          "report a duplicate separately")
    check(values.insert(24) is InsertResult.COLLISION, "24 collides with 10 at index 3")
    check(not values.contains(24) and values.contains(10),
          "a collision does not overwrite 10")
    check(values.table_snapshot_for_testing()[3] == 10,
          "collision leaves the occupied home slot unchanged")
    check(not values.remove(24) and values.contains(10),
          "removing colliding absent key preserves 10")
    check(values.remove(10) and not values.contains(10), "remove stored key")
    check(not values.remove(10), "cannot remove a key twice")
    check(values.insert(24) is InsertResult.INSERTED, "removed position can be reused")
    check(values.table_snapshot_for_testing()[3] == 24,
          "reused home position stores the new key")
    check_raises(lambda: values.insert(-1), "reject negative insert key")
    check_raises(lambda: values.contains(-1), "reject negative lookup key")
    check_raises(lambda: values.remove(-1), "reject negative removal key")


def test_all_home_positions_and_collisions():
    capacity = 100_000
    table = IncompleteHashTable(capacity)
    ok = all(table.insert(key) is InsertResult.INSERTED for key in range(capacity))
    ok = all(table.contains(key) for key in range(capacity)) and ok
    ok = all(table.insert(key + capacity) is InsertResult.COLLISION for key in range(capacity)) and ok
    ok = all(table.remove(key) for key in range(0, capacity, 2)) and ok
    ok = all(table.insert(key + capacity) is InsertResult.INSERTED for key in range(0, capacity, 2)) and ok
    check(ok, "100,000 home positions, collisions, removals, and slot reuses")


if __name__ == "__main__":
    test_table()
    test_all_home_positions_and_collisions()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")

if __name__ == "__main__" and failures:
    raise SystemExit(1)
