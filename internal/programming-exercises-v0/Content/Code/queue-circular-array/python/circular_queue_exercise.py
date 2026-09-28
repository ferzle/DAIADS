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


def check(actual, expected):
    line = inspect.currentframe().f_back.f_lineno
    if actual == expected:
        print(f"PASS at test line {line}: got {actual!r}")
    else:
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


test_queue()
