import inspect


class IntQueue:
    def __init__(self, capacity):
        self.A = [None] * capacity
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

    def used_values_for_testing(self):
        return self.A[:self.count]


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
    queue = IntQueue(3)

    check(queue.is_empty(), True)
    check(queue.is_full(), False)
    check(queue.size(), 0)
    check(queue.dequeue(), -1)

    large = IntQueue(1024)
    large_ok = True
    for i in range(1024):
        large_ok = large.enqueue(i) and large_ok
    large_ok = large.is_full() and not large.enqueue(1024) and large_ok
    for i in range(1024):
        large_ok = large.dequeue() == i and large_ok
    check(large_ok and large.is_empty(), True)
    check(queue.front(), -1)

    check(queue.enqueue(4), True)
    check(queue.enqueue(7), True)
    check(queue.enqueue(9), True)
    check(queue.is_full(), True)
    check(queue.enqueue(2), False)

    check(queue.front(), 4)
    check(queue.dequeue(), 4)
    check(queue.used_values_for_testing(), [7, 9])
    check(queue.front(), 7)
    check(queue.size(), 2)

    check(queue.enqueue(2), True)
    check(queue.used_values_for_testing(), [7, 9, 2])
    check(queue.dequeue(), 7)
    check(queue.dequeue(), 9)
    check(queue.dequeue(), 2)

    check(queue.is_empty(), True)
    check(queue.size(), 0)
    check(queue.dequeue(), -1)


def test_capacity_boundaries_and_exhaustion():
    for capacity in (1, 2, 5, 64):
        queue = IntQueue(capacity)
        for i in range(capacity):
            check(queue.enqueue(1000 + i), True)
            check(queue.front(), 1000)
            check(queue.size(), i + 1)
        check(queue.is_full(), True)
        check(queue.enqueue(9999), False)
        for i in range(capacity):
            check(queue.dequeue(), 1000 + i)
            check(queue.size(), capacity - i - 1)
        check(queue.is_empty(), True)
        check(queue.enqueue(77), True)
        check(queue.dequeue(), 77)


test_queue()
test_capacity_boundaries_and_exhaustion()

if failures:
    raise SystemExit(1)
