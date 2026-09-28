from enum import Enum, auto


class ProbingType(Enum):
    LINEAR = auto()
    QUADRATIC = auto()
    DOUBLE_HASHING = auto()


class OpenAddressingIntSet:
    class _SlotState(Enum):
        EMPTY = auto()
        OCCUPIED = auto()
        DELETED = auto()

    def __init__(self, capacity_exponent, probing_type):
        if capacity_exponent < 1 or capacity_exponent > 30:
            raise ValueError("capacity_exponent must be from 1 through 30")
        if not isinstance(probing_type, ProbingType):
            raise ValueError("probing_type must be a ProbingType")
        self.m = 1 << capacity_exponent
        self._keys = [0] * self.m
        self._states = [self._SlotState.EMPTY] * self.m
        self._probing_type = probing_type
        self._count = 0

    def size(self):
        # TODO: Return the number of distinct keys currently stored.
        return -1

    def is_empty(self):
        # TODO: Return whether the set contains no keys.
        return False

    def capacity(self):
        return self.m

    def slot_counts_for_testing(self):
        return tuple(self._states.count(state) for state in self._SlotState)

    def contains(self, key):
        # TODO: Reject a negative key. Probe at most capacity() positions,
        # stopping successfully at key or unsuccessfully at an EMPTY slot.
        return False

    def insert(self, key):
        # TODO: Reject a negative key. Remember the first DELETED slot, but
        # continue until finding key, an EMPTY slot, or the probe limit.
        # Add the key and increment _count exactly once only when it is absent.
        return False

    def remove(self, key):
        # TODO: Reject a negative key. Replace a matching OCCUPIED slot with
        # DELETED, decrement _count exactly once, and return whether key existed.
        return False

    def clear(self):
        # TODO: Restore every slot to EMPTY and reset _count to zero.
        pass

    def _probe_index(self, key, i):
        if self._probing_type is ProbingType.LINEAR:
            return self._linear_probe_index(key, i)
        if self._probing_type is ProbingType.QUADRATIC:
            return self._quadratic_probe_index(key, i)
        if self._probing_type is ProbingType.DOUBLE_HASHING:
            return self._double_hash_probe_index(key, i)
        raise AssertionError("unknown probing type")

    def _linear_probe_index(self, key, i):
        # TODO: Compute probe i using linear probing.
        return 0

    def _quadratic_probe_index(self, key, i):
        # TODO: Compute probe i using triangular quadratic offsets.
        return 0

    def _double_hash_probe_index(self, key, i):
        # TODO: Compute probe i using _primary_hash and _secondary_hash.
        return 0

    def _primary_hash(self, key):
        # TODO: Compute h1(key).
        return 0

    def _secondary_hash(self, key):
        # TODO: Compute the odd double-hashing step h2(key).
        return 0

    @staticmethod
    def _check_key(key):
        if key < 0:
            raise ValueError("key must be nonnegative")


failures = 0


def check(condition, label):
    global failures
    if condition:
        print(f"pass: {label}")
    else:
        failures += 1
        print(f"FAIL: {label}")


def check_equal(actual, expected, label):
    check(actual == expected, f"{label} (expected {expected}, got {actual})")


def check_raises(action, label):
    global failures
    try:
        action()
        failures += 1
        print(f"FAIL: {label}")
    except ValueError:
        print(f"pass: {label}")


def test_strategy(probing_type):
    name = probing_type.name
    values = OpenAddressingIntSet(3, probing_type)
    check_equal(values.capacity(), 8, f"{name}: exponent 3 gives capacity 8")
    check(values.is_empty() and values.size() == 0, f"{name}: new set is empty")
    check(values.insert(1) and values.insert(9) and values.insert(17),
          f"{name}: insert colliding keys")
    check_equal(values.slot_counts_for_testing(), (5, 3, 0),
                f"{name}: three inserts occupy three slots")
    check(values.contains(1) and values.contains(9) and values.contains(17),
          f"{name}: find colliding keys")
    check(not values.insert(17) and values.size() == 3, f"{name}: reject duplicate insertion")
    check(values.remove(9) and not values.contains(9), f"{name}: remove creates a tombstone")
    check_equal(values.slot_counts_for_testing(), (5, 2, 1),
                f"{name}: removal changes one occupied slot to deleted")
    check(values.contains(17), f"{name}: lookup continues past a tombstone")
    check(values.insert(41) and values.contains(41), f"{name}: insertion can reuse a tombstone")
    check_equal(values.slot_counts_for_testing(), (5, 3, 0),
                f"{name}: insertion reuses the tombstone")
    check(not values.remove(99), f"{name}: absent removal changes nothing")

    wraparound = OpenAddressingIntSet(3, probing_type)
    check(wraparound.insert(7) and wraparound.insert(15) and wraparound.contains(15),
          f"{name}: probe sequence wraps around")

    full = OpenAddressingIntSet(3, probing_type)
    filled = all(full.insert(key) for key in range(full.capacity()))
    check(filled and not full.insert(8), f"{name}: insertion fails when no position is available")
    check(full.remove(0) and full.insert(8) and full.contains(8),
          f"{name}: insertion reuses the only available tombstone")

    values.clear()
    check(values.is_empty() and values.size() == 0 and not values.contains(1),
          f"{name}: clear resets the set")
    check_equal(values.slot_counts_for_testing(), (8, 0, 0),
                f"{name}: clear restores every slot to empty")
    check_raises(lambda: values.contains(-1), f"{name}: reject negative lookup key")
    check_raises(lambda: values.insert(-1), f"{name}: reject negative insertion key")
    check_raises(lambda: values.remove(-1), f"{name}: reject negative removal key")


def test_tombstone_stress(probing_type):
    name = f"{probing_type.name} stress"
    values = OpenAddressingIntSet(7, probing_type)
    m = values.capacity()
    result = True

    # All 80 keys have home index 3, so every insertion must resolve collisions.
    for q in range(80):
        result = values.insert(3 + m * q) and result
    check(result and values.size() == 80, f"{name}: insert 80 colliding keys")

    result = True
    for q in range(80):
        result = values.contains(3 + m * q) and result
    check(result, f"{name}: find all initial keys")

    # Remove 27 keys. Each replacement has the same home index and, for
    # double hashing, the same step as the key it replaces.
    result = True
    for q in range(0, 80, 3):
        result = values.remove(3 + m * q) and result
    check(result and values.size() == 53, f"{name}: remove 27 keys")

    result = True
    for q in range(80):
        key = 3 + m * q
        found_as_expected = not values.contains(key) if q % 3 == 0 else values.contains(key)
        result = found_as_expected and result
    check(result, f"{name}: searches cross tombstones correctly")

    result = True
    for q in range(0, 80, 3):
        result = values.insert(3 + m * (q + 128)) and result
    check(result and values.size() == 80, f"{name}: replace all 27 removed keys")

    # Create and refill a second wave of 14 tombstones.
    result = True
    for q in range(0, 80, 6):
        result = values.remove(3 + m * (q + 128)) and result
    check(result and values.size() == 66, f"{name}: remove 14 replacement keys")

    result = True
    for q in range(0, 80, 6):
        result = values.insert(3 + m * (q + 256)) and result
    check(result and values.size() == 80, f"{name}: refill the second tombstone wave")

    result = True
    for q in range(80):
        if q % 6 == 0:
            result = not values.contains(3 + m * (q + 128)) and result
            result = values.contains(3 + m * (q + 256)) and result
        elif q % 3 == 0:
            result = values.contains(3 + m * (q + 128)) and result
        else:
            result = values.contains(3 + m * q) and result
    check(result, f"{name}: final membership is correct after 41 removals and replacements")


def test_large_delete_reinsert_cycles(probing_type):
    values = OpenAddressingIntSet(10, probing_type)
    name = probing_type.name
    for key in range(400): check(values.insert(key * 17), f"{name}: large insert {key}")
    for key in range(0, 400, 3): check(values.remove(key * 17), f"{name}: large remove {key}")
    for key in range(0, 400, 3): check(values.insert(100000 + key * 17), f"{name}: tombstone reuse {key}")
    for key in range(400): check(values.contains(key * 17) == (key % 3 != 0), f"{name}: final membership {key}")
    check(values.size() == 400, f"{name}: size after replacements")


def test_fifty_thousand_keys(probing_type):
    count = 50_000
    values = OpenAddressingIntSet(17, probing_type)
    ok = all(values.insert(key) for key in range(count))
    ok = all(values.contains(key) for key in range(count)) and ok
    ok = all(values.remove(key) for key in range(0, count, 3)) and ok
    ok = all(values.insert(200_000 + key) for key in range(0, count, 3)) and ok
    ok = all(values.contains(key) == (key % 3 != 0) for key in range(count)) and ok
    ok = all(values.contains(200_000 + key) for key in range(0, count, 3)) and ok
    check(ok and values.size() == count, f"{probing_type.name}: 50,000-key aggregate workload")


if __name__ == "__main__":
    print(
        "Note: the complete Python open-addressing test suite may take "
        "several minutes.",
        flush=True,
    )
    check_raises(lambda: OpenAddressingIntSet(3, None), "reject an invalid probing type")
    for strategy in ProbingType:
        test_strategy(strategy)
        test_tombstone_stress(strategy)
        test_large_delete_reinsert_cycles(strategy)
        test_fifty_thousand_keys(strategy)
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")

if __name__ == "__main__" and failures:
    raise SystemExit(1)
