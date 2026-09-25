import inspect


class IntDeque:
    def __init__(self, capacity):
        self.A = [0] * capacity
        self.front = 0
        self.count = 0

    def is_empty(self):
        return self.count == 0

    def is_full(self):
        return self.count == len(self.A)

    def size(self):
        return self.count

    def clear(self):
        self.front = 0
        self.count = 0

    def back_index(self):
        return (self.front + self.count - 1) % len(self.A)

    def peek_front(self):
        return -1 if self.is_empty() else self.A[self.front]

    def peek_back(self):
        return -1 if self.is_empty() else self.A[self.back_index()]

    def add_front(self, value):
        if self.is_full():
            return False
        self.front = (self.front - 1) % len(self.A)
        self.A[self.front] = value
        self.count += 1
        return True

    def add_back(self, value):
        if self.is_full():
            return False
        insertion_index = (self.front + self.count) % len(self.A)
        self.A[insertion_index] = value
        self.count += 1
        return True

    def remove_front(self):
        if self.is_empty():
            return -1
        value = self.A[self.front]
        self.front = (self.front + 1) % len(self.A)
        self.count -= 1
        return value

    def remove_back(self):
        if self.is_empty():
            return -1
        value = self.A[self.back_index()]
        self.count -= 1
        return value

    def storage_state_for_testing(self):
        return self.front, self.count, len(self.A)


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
    deque = IntDeque(5)

    check(deque.is_empty(), True)
    check(deque.is_full(), False)
    check(deque.size(), 0)
    check(deque.peek_front(), -1)

    large = IntDeque(257)
    large_ok = True
    for round_number in range(20):
        for i in range(257):
            large_ok = large.add_back(round_number * 257 + i) and large_ok
        large_ok = large.is_full() and not large.add_front(-1) and large_ok
        for i in range(257):
            large_ok = large.remove_front() == round_number * 257 + i and large_ok
    check(large_ok and large.is_empty(), True)
    check(deque.peek_back(), -1)
    check(deque.remove_front(), -1)
    check(deque.remove_back(), -1)

    check(deque.add_back(4), True)       # [4]
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 4)
    check(deque.size(), 1)

    check(deque.add_back(7), True)       # [4, 7]
    check(deque.add_front(2), True)      # [2, 4, 7]
    check(deque.add_back(9), True)       # [2, 4, 7, 9]
    check(deque.storage_state_for_testing(), (4, 4, 5))

    check(deque.size(), 4)
    check(deque.is_empty(), False)
    check(deque.peek_front(), 2)
    check(deque.peek_back(), 9)

    check(deque.remove_front(), 2)       # [4, 7, 9]
    check(deque.peek_front(), 4)
    check(deque.remove_back(), 9)        # [4, 7]
    check(deque.peek_back(), 7)
    check(deque.size(), 2)

    check(deque.add_front(1), True)      # [1, 4, 7]
    check(deque.add_front(0), True)      # [0, 1, 4, 7]
    check(deque.add_back(11), True)      # [0, 1, 4, 7, 11]
    check(deque.is_full(), True)
    check(deque.size(), 5)
    check(deque.peek_front(), 0)
    check(deque.peek_back(), 11)

    check(deque.add_back(13), False)     # full
    check(deque.add_front(13), False)    # full
    check(deque.size(), 5)
    check(deque.peek_front(), 0)
    check(deque.peek_back(), 11)

    check(deque.remove_front(), 0)       # [1, 4, 7, 11]
    check(deque.remove_front(), 1)       # [4, 7, 11]
    check(deque.add_back(13), True)      # [4, 7, 11, 13]
    check(deque.add_back(15), True)      # [4, 7, 11, 13, 15]
    check(deque.storage_state_for_testing(), (0, 5, 5))
    check(deque.is_full(), True)
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 15)

    check(deque.remove_back(), 15)       # [4, 7, 11, 13]
    check(deque.remove_back(), 13)       # [4, 7, 11]
    check(deque.add_front(3), True)      # [3, 4, 7, 11]
    check(deque.add_front(2), True)      # [2, 3, 4, 7, 11]
    check(deque.peek_front(), 2)
    check(deque.peek_back(), 11)

    check(deque.remove_front(), 2)       # [3, 4, 7, 11]
    check(deque.remove_back(), 11)       # [3, 4, 7]
    check(deque.remove_front(), 3)       # [4, 7]
    check(deque.remove_back(), 7)        # [4]
    check(deque.remove_back(), 4)        # []

    check(deque.is_empty(), True)
    check(deque.size(), 0)
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


def test_capacities_wraparound_and_reuse():
    for capacity in (1, 2, 3, 8, 64):
        deque = IntDeque(capacity)
        for round_number in range(20):
            for i in range(capacity):
                check(deque.add_back(round_number * capacity + i), True)
            check(deque.is_full(), True)
            check(deque.add_front(999999), False)
            for i in range(capacity):
                check(deque.remove_front(), round_number * capacity + i)
            check(deque.is_empty(), True)
            for i in range(capacity):
                check(deque.add_front(round_number * capacity + i), True)
            for i in range(capacity):
                check(deque.remove_back(), round_number * capacity + i)
            check(deque.is_empty(), True)
        deque.add_back(7)
        deque.clear()
        check(deque.is_empty(), True)
        check(deque.add_front(8), True)
        check(deque.remove_back(), 8)


def test_large_aggregate_wraparound():
    capacity = 10_000
    deque = IntDeque(capacity)
    ok = True
    for round_number in range(10):
        for i in range(capacity):
            ok = deque.add_back(round_number * capacity + i) and ok
        for i in range(capacity):
            ok = deque.remove_front() == round_number * capacity + i and ok
    ok = deque.is_empty() and deque.size() == 0 and ok
    check(ok, True)


test_deque()
test_capacities_wraparound_and_reuse()
test_large_aggregate_wraparound()

if failures:
    raise SystemExit(1)
