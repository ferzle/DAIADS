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
        return self._count

    def capacity(self):
        return len(self._buckets)

    def contains(self, key):
        self._check_key(key)
        node = self._buckets[self._bucket_index(key)]
        while node is not None:
            if node.key == key:
                return True
            node = node.next
        return False

    def add(self, key):
        self._check_key(key)
        if self.contains(key):
            return False
        if (self._count + 1) / len(self._buckets) > self.MAX_LOAD_FACTOR:
            self._resize(len(self._buckets) * 2)
        index = self._bucket_index(key)
        new_node = self._Node(key)
        if self._buckets[index] is None:
            self._buckets[index] = new_node
        else:
            tail = self._buckets[index]
            while tail.next is not None:
                tail = tail.next
            tail.next = new_node
        self._count += 1
        return True

    def remove(self, key):
        self._check_key(key)
        index = self._bucket_index(key)
        previous = None
        node = self._buckets[index]
        while node is not None and node.key != key:
            previous = node
            node = node.next
        if node is None:
            return False
        if previous is None:
            self._buckets[index] = node.next
        else:
            previous.next = node.next
        self._count -= 1
        return True

    def _bucket_index(self, key):
        return key % len(self._buckets)

    @staticmethod
    def _check_key(key):
        if key < 0:
            raise ValueError("key must be nonnegative")

    def _resize(self, new_capacity):
        old_buckets = self._buckets
        self._buckets = [None] * new_capacity
        tails = [None] * new_capacity
        for head in old_buckets:
            node = head
            while node is not None:
                next_node = node.next
                node.next = None
                index = self._bucket_index(node.key)
                if self._buckets[index] is None:
                    self._buckets[index] = node
                else:
                    tails[index].next = node
                tails[index] = node
                node = next_node

    def bucket_snapshot(self, index):
        if index < 0 or index >= len(self._buckets):
            raise IndexError("invalid bucket index")
        keys = []
        node = self._buckets[index]
        while node is not None:
            keys.append(node.key)
            node = node.next
        return keys

    def has_valid_structure_for_testing(self):
        seen = set()
        reachable = 0
        for index, head in enumerate(self._buckets):
            slow = head
            fast = head
            while fast is not None and fast.next is not None:
                slow = slow.next
                fast = fast.next.next
                if slow is fast:
                    return False
            node = head
            while node is not None:
                if (node.key < 0 or self._bucket_index(node.key) != index
                        or node.key in seen):
                    return False
                seen.add(node.key)
                reachable += 1
                node = node.next
        return reachable == self._count


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
    values = SeparateChainingIntSet(8)
    check_equal(values.size(), 0, "new set has size zero")
    check(not values.contains(6), "lookup in an empty bucket")
    check(not values.remove(6), "remove from an empty bucket")

    check(values.add(1), "add first key")
    check(values.add(9) and values.add(17) and values.add(25), "add colliding keys")
    check_equal(values.bucket_snapshot(1), [1, 9, 17, 25],
                "colliding keys append at the tail")
    check(values.has_valid_structure_for_testing(), "collision chain has valid structure")
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
    check(growing.has_valid_structure_for_testing(), "valid structure after rehashing")

    check_raises(lambda: values.contains(-1), "reject negative lookup key")
    check_raises(lambda: values.add(-1), "reject negative insertion key")
    check_raises(lambda: values.remove(-1), "reject negative removal key")


def test_large_resize_and_collision_workload():
    values = SeparateChainingIntSet(2)
    for key in range(1000): check(values.add(key * 16), f"large add {key}")
    check(values.size() == 1000, "size after collision-heavy growth")
    for key in range(1000): check(values.contains(key * 16), f"large contains {key}")
    for key in range(0, 1000, 2): check(values.remove(key * 16), f"large remove {key}")
    for key in range(1000): check(values.contains(key * 16) == (key % 2 == 1), f"large membership {key}")
    check(values.has_valid_structure_for_testing(), "valid structure after collision-heavy workload")


def test_hundred_thousand_distributed_keys():
    count = 100_000
    values = SeparateChainingIntSet(4)
    ok = all(values.add(key) for key in range(count))
    ok = all(values.contains(key) for key in range(count)) and ok
    ok = all(values.remove(key) for key in range(0, count, 2)) and ok
    ok = all(values.contains(key) == (key % 2 == 1) for key in range(count)) and ok
    check(ok and values.size() == count // 2 and values.has_valid_structure_for_testing(),
          "100,000-key distributed aggregate workload")


if __name__ == "__main__":
    test_set()
    test_large_resize_and_collision_workload()
    test_hundred_thousand_distributed_keys()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")

if __name__ == "__main__" and failures:
    raise SystemExit(1)
