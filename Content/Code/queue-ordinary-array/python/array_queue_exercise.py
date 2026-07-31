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


def check(actual, expected):
    line = inspect.currentframe().f_back.f_lineno
    if actual == expected:
        print(f"PASS at test line {line}: got {actual!r}")
    else:
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
    check(queue.front(), 7)
    check(queue.size(), 2)

    check(queue.enqueue(2), True)
    check(queue.dequeue(), 7)
    check(queue.dequeue(), 9)
    check(queue.dequeue(), 2)

    check(queue.is_empty(), True)
    check(queue.size(), 0)
    check(queue.dequeue(), -1)


test_queue()
