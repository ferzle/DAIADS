import inspect


class BlockNode:
    def __init__(self, block_size):
        self.values = [0] * block_size
        self.prev = None
        self.next = None


class IntDeque:
    def __init__(self, block_size):
        self.block_size = block_size
        self.first_block = None
        self.last_block = None
        self.front_index = 0
        self.back_index = 0
        self.count = 0

    def is_empty(self):
        # TODO
        return False

    def size(self):
        # TODO
        return -1

    def clear(self):
        # TODO
        pass

    def peek_front(self):
        # TODO
        return -1

    def peek_back(self):
        # TODO
        return -1

    def add_front(self, value):
        # TODO
        return False

    def add_back(self, value):
        # TODO
        return False

    def remove_front(self):
        # TODO
        return -1

    def remove_back(self):
        # TODO
        return -1

    def has_valid_structure_for_testing(self):
        if self.block_size <= 0 or self.count < 0:
            return False
        if self.count == 0:
            return self.first_block is None and self.last_block is None
        if self.first_block is None or self.last_block is None:
            return False
        if self.first_block.prev is not None or self.last_block.next is not None:
            return False
        if not (0 <= self.front_index < self.block_size
                and 0 <= self.back_index < self.block_size):
            return False
        blocks = 0
        previous = None
        node = self.first_block
        while node is not None:
            if (node.prev is not previous or len(node.values) != self.block_size
                    or blocks > self.count + 1):
                return False
            previous = node
            blocks += 1
            node = node.next
        return previous is self.last_block


failures = 0


def check(actual, expected):
    global failures
    line = inspect.currentframe().f_back.f_lineno
    if actual == expected:
        print(f"PASS at test line {line}: got {actual!r}")
    else:
        failures += 1
        print(f"FAIL at test line {line}: expected {expected!r} but got {actual!r}")


def test_deque():
    deque = IntDeque(3)
    check(deque.has_valid_structure_for_testing(), True)

    check(deque.is_empty(), True)
    check(deque.size(), 0)
    check(deque.peek_front(), -1)

    tiny_blocks = IntDeque(1)
    tiny_ok = True
    for i in range(500):
        tiny_ok = tiny_blocks.add_back(i) and tiny_ok
    for i in range(250):
        tiny_ok = tiny_blocks.remove_front() == i and tiny_ok
    for i in range(500, 750):
        tiny_ok = tiny_blocks.add_front(i) and tiny_ok
    for i in range(749, 499, -1):
        tiny_ok = tiny_blocks.remove_front() == i and tiny_ok
    for i in range(499, 249, -1):
        tiny_ok = tiny_blocks.remove_back() == i and tiny_ok
    check(tiny_ok and tiny_blocks.is_empty(), True)
    check(deque.peek_back(), -1)
    check(deque.remove_front(), -1)
    check(deque.remove_back(), -1)

    check(deque.add_back(4), True)       # [4]
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 4)
    check(deque.size(), 1)

    check(deque.add_back(7), True)       # [4, 7]
    check(deque.add_back(9), True)       # [4, 7, 9]
    check(deque.add_back(11), True)      # new back block: [4, 7, 9, 11]
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 11)
    check(deque.size(), 4)

    check(deque.add_front(2), True)      # new front block: [2, 4, 7, 9, 11]
    check(deque.add_front(1), True)      # [1, 2, 4, 7, 9, 11]
    check(deque.add_front(0), True)      # [0, 1, 2, 4, 7, 9, 11]
    check(deque.peek_front(), 0)
    check(deque.peek_back(), 11)
    check(deque.size(), 7)
    check(deque.has_valid_structure_for_testing(), True)

    check(deque.remove_front(), 0)       # [1, 2, 4, 7, 9, 11]
    check(deque.remove_front(), 1)       # [2, 4, 7, 9, 11]
    check(deque.remove_front(), 2)       # first block may be removed: [4, 7, 9, 11]
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 11)
    check(deque.size(), 4)

    check(deque.remove_back(), 11)       # [4, 7, 9]
    check(deque.remove_back(), 9)        # [4, 7]
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 7)
    check(deque.size(), 2)

    check(deque.add_front(3), True)      # [3, 4, 7]
    check(deque.add_back(8), True)       # [3, 4, 7, 8]
    check(deque.add_back(10), True)      # [3, 4, 7, 8, 10]
    check(deque.peek_front(), 3)
    check(deque.peek_back(), 10)

    check(deque.remove_back(), 10)       # [3, 4, 7, 8]
    check(deque.remove_front(), 3)       # [4, 7, 8]
    check(deque.remove_back(), 8)        # [4, 7]
    check(deque.remove_front(), 4)       # [7]
    check(deque.peek_front(), 7)
    check(deque.peek_back(), 7)
    check(deque.size(), 1)

    check(deque.remove_back(), 7)        # []
    check(deque.is_empty(), True)
    check(deque.size(), 0)
    check(deque.has_valid_structure_for_testing(), True)
    check(deque.peek_front(), -1)
    check(deque.peek_back(), -1)
    check(deque.remove_front(), -1)
    check(deque.remove_back(), -1)

    check(deque.add_front(6), True)      # [6]
    check(deque.add_back(8), True)       # [6, 8]
    deque.clear()                        # []
    check(deque.is_empty(), True)
    check(deque.size(), 0)
    check(deque.peek_front(), -1)


def test_block_boundaries_and_reuse():
    for block_size in (1, 2, 3, 4, 5, 8, 32):
        deque = IntDeque(block_size)
        count = block_size * 20 + 3
        for i in range(count):
            check(deque.add_back(i), True)
        for i in range(count):
            check(deque.remove_front(), i)
        check(deque.is_empty(), True)
        check(deque.size(), 0)
        for i in range(count):
            check(deque.add_front(i), True)
        for i in range(count):
            check(deque.remove_back(), i)
        check(deque.is_empty(), True)
        for i in range(count):
            check(deque.add_back(10000 + i), True)
            check(deque.remove_front(), 10000 + i)
        deque.add_front(7)
        deque.clear()
        check(deque.is_empty(), True)
        check(deque.peek_front(), -1)
        check(deque.peek_back(), -1)


def test_large_aggregate_workload():
    n = 100_000
    deque = IntDeque(64)
    ok = True
    for value in range(n):
        ok = deque.add_back(value) and ok
    ok = (deque.size() == n and deque.peek_front() == 0
          and deque.peek_back() == n - 1
          and deque.has_valid_structure_for_testing() and ok)
    for value in range(n):
        ok = deque.remove_front() == value and ok
    ok = deque.is_empty() and deque.has_valid_structure_for_testing() and ok
    check(ok, True)


test_deque()
test_block_boundaries_and_reuse()
test_large_aggregate_workload()

if failures:
    raise SystemExit(1)
