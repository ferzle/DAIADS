class SeparateChainingIntSet:
    MAX_LOAD_FACTOR = 0.75

    class _Node:
        def __init__(self, key):
            self.key = key
            self.next = None

    def __init__(self, initial_capacity):
        if initial_capacity < 1:
            raise ValueError("initial_capacity must be positive")
        self._buckets = [None] * initial_capacity
        self._count = 0

    def size(self):
        # TODO: Return the number of distinct keys, not the bucket count.
        return -1

    def capacity(self):
        return len(self._buckets)

    def contains(self, key):
        # TODO: Reject a negative key, then search only its linked bucket.
        return False

    def add(self, key):
        # TODO:
        # 1. Reject a negative key and return False if it is already present.
        # 2. If the projected load factor exceeds 0.75, double the bucket
        #    array and rehash every existing key.
        # 3. Recompute key's index, append it to that bucket's tail,
        #    increment _count exactly once, and return True.
        return False

    def remove(self, key):
        # TODO: Reject a negative key. Unlink key from its bucket if present,
        # handling the first node separately. Update _count exactly once.
        return False

    def _bucket_index(self, key):
        return key % len(self._buckets)

    @staticmethod
    def _check_key(key):
        if key < 0:
            raise ValueError("key must be nonnegative")

    def _resize(self, new_capacity):
        # TODO: Allocate new buckets and move every existing node to the tail
        # of its new bucket. Preserve tail-append order and do not change _count.
        pass

    def bucket_snapshot(self, index):
        if index < 0 or index >= len(self._buckets):
            raise IndexError("invalid bucket index")
        keys = []
        node = self._buckets[index]
        while node is not None:
            keys.append(node.key)
            node = node.next
        return keys


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


def test_set():
    check_raises(lambda: SeparateChainingIntSet(0), "reject nonpositive capacity")
    values = SeparateChainingIntSet(8)
    check_equal(values.size(), 0, "new set has size zero")
    check(not values.contains(6), "lookup in an empty bucket")
    check(not values.remove(6), "remove from an empty bucket")

    check(values.add(1), "add first key")
    check(values.add(9) and values.add(17) and values.add(25), "add colliding keys")
    check_equal(values.bucket_snapshot(1), [1, 9, 17, 25],
                "colliding keys append at the tail")
    check(not values.add(17), "reject duplicate key")
    check_equal(values.size(), 4, "duplicate does not change size")
    check(values.contains(1) and values.contains(17) and values.contains(25),
          "contains traverses a chain")

    check(values.remove(1), "remove first node")
    check(values.remove(17), "remove middle node")
    check(values.remove(25), "remove final node")
    check_equal(values.bucket_snapshot(1), [9], "remaining chain is intact")
    check(not values.remove(17), "absent removal changes nothing")
    check_equal(values.size(), 1, "removals update size")

    growing = SeparateChainingIntSet(4)
    check(growing.add(2) and growing.add(6) and growing.add(10),
          "fill table to load factor 0.75")
    check_equal(growing.capacity(), 4, "capacity unchanged at threshold")
    check(not growing.add(6) and growing.capacity() == 4,
          "duplicate does not trigger resize")
    check(growing.add(14), "next distinct key triggers resize")
    check_equal(growing.capacity(), 8, "resize doubles capacity")
    check(all(growing.contains(key) for key in (2, 6, 10, 14)),
          "all keys remain findable after rehashing")
    check_equal(growing.bucket_snapshot(2), [2, 10],
                "rehashing preserves tail order in bucket 2")
    check_equal(growing.bucket_snapshot(6), [6, 14],
                "new key appends after rehashing")

    check_raises(lambda: values.contains(-1), "reject negative lookup key")
    check_raises(lambda: values.add(-1), "reject negative insertion key")
    check_raises(lambda: values.remove(-1), "reject negative removal key")


if __name__ == "__main__":
    test_set()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")
