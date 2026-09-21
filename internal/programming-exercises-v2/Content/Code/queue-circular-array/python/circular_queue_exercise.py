import inspect


class IntQueue:
    def __init__(self, capacity):
        self.A = [None] * capacity
        self.front_index = 0
        self.count = 0

    def is_empty(self):
        # TODO
        return False

    def is_full(self):
        # TODO
        return False

    def enqueue(self, value):
        # TODO
        return False

    def dequeue(self):
        # TODO
        return -1

    def front(self):
        # TODO
        return -1

    def size(self):
        # TODO
        return -1


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

    check(queue.dequeue(), 4)
    check(queue.dequeue(), 7)

    check(queue.enqueue(2), True)
    check(queue.enqueue(5), True)
    check(queue.enqueue(8), True)
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


test_queue()
test_repeated_wraparound()

if failures:
    raise SystemExit(1)
