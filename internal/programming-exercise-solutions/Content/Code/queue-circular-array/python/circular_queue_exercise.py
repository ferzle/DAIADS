import inspect


class IntQueue:
    def __init__(self, capacity):
        self.A = [None] * capacity
        self.front_index = 0
        self.count = 0

    def is_empty(self):
        return self.count == 0

    def is_full(self):
        return self.count == len(self.A)

    def enqueue(self, value):
        if self.is_full():
            return False
        back_index = (self.front_index + self.count) % len(self.A)
        self.A[back_index] = value
        self.count += 1
        return True

    def dequeue(self):
        if self.is_empty():
            return -1
        value = self.A[self.front_index]
        self.front_index = (self.front_index + 1) % len(self.A)
        self.count -= 1
        return value

    def front(self):
        return -1 if self.is_empty() else self.A[self.front_index]

    def size(self):
        return self.count

    def storage_state_for_testing(self):
        return self.front_index, self.count, len(self.A)


failures = 0


def check(actual, expected):
    global failures
    line = inspect.currentframe().f_back.f_lineno
    if actual == expected:
        print(f"PASS at test line {line}: got {actual!r}")
    else:
        failures += 1
        print(f"FAIL at test line {line}: expected {expected!r} but got {actual!r}")


def test_queue():
    queue = IntQueue(4)

    check(queue.is_empty(), True)
    check(queue.is_full(), False)
    check(queue.dequeue(), -1)
    check(queue.front(), -1)

    check(queue.enqueue(4), True)
    check(queue.enqueue(7), True)
    check(queue.enqueue(9), True)
    check(queue.storage_state_for_testing(), (0, 3, 4))

    check(queue.dequeue(), 4)
    check(queue.dequeue(), 7)
    check(queue.storage_state_for_testing(), (2, 1, 4))

    check(queue.enqueue(2), True)
    check(queue.enqueue(5), True)
    check(queue.enqueue(8), True)
    check(queue.storage_state_for_testing(), (2, 4, 4))
    check(queue.is_full(), True)
    check(queue.enqueue(10), False)

    check(queue.front(), 9)
    check(queue.dequeue(), 9)
    check(queue.dequeue(), 2)
    check(queue.dequeue(), 5)
    check(queue.dequeue(), 8)

    check(queue.is_empty(), True)
    check(queue.size(), 0)
    check(queue.dequeue(), -1)

    check(queue.enqueue(11), True)
    check(queue.front(), 11)

    wrapped = IntQueue(257)
    wrapped_ok = True
    for round_number in range(20):
        for i in range(257):
            wrapped_ok = wrapped.enqueue(round_number * 257 + i) and wrapped_ok
        for i in range(257):
            wrapped_ok = wrapped.dequeue() == round_number * 257 + i and wrapped_ok
    check(wrapped_ok and wrapped.is_empty(), True)


def test_repeated_wraparound():
    for capacity in (1, 2, 5, 64):
        queue = IntQueue(capacity)
        next_expected = 0
        for round_number in range(20):
            for i in range(capacity):
                check(queue.enqueue(round_number * capacity + i), True)
            check(queue.is_full(), True)
            check(queue.enqueue(999999), False)
            for i in range(capacity):
                check(queue.front(), next_expected)
                check(queue.dequeue(), next_expected)
                next_expected += 1
                check(queue.size(), capacity - i - 1)
            check(queue.is_empty(), True)


def test_large_aggregate_wraparound():
    capacity = 10_000
    queue = IntQueue(capacity)
    ok = True
    for round_number in range(10):
        for i in range(capacity):
            ok = queue.enqueue(round_number * capacity + i) and ok
        ok = queue.is_full() and not queue.enqueue(-1) and ok
        for i in range(capacity):
            ok = queue.dequeue() == round_number * capacity + i and ok
    ok = queue.is_empty() and queue.size() == 0 and ok
    check(ok, True)


test_queue()
test_repeated_wraparound()
test_large_aggregate_wraparound()

if failures:
    raise SystemExit(1)
