import inspect


class Node:
    def __init__(self, value):
        self.value = value
        self.next = None


class IntQueue:
    def __init__(self):
        self.head = None
        self.tail = None
        self.count = 0

    def is_empty(self):
        # TODO
        return False

    def enqueue(self, value):
        # TODO
        pass

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
    queue = IntQueue()

    check(queue.is_empty(), True)
    check(queue.size(), 0)
    check(queue.dequeue(), -1)
    check(queue.front(), -1)

    queue.enqueue(4)
    check(queue.is_empty(), False)
    check(queue.size(), 1)
    check(queue.front(), 4)

    queue.enqueue(7)
    queue.enqueue(9)
    check(queue.size(), 3)
    check(queue.front(), 4)

    check(queue.dequeue(), 4)
    check(queue.front(), 7)
    check(queue.size(), 2)

    queue.enqueue(2)
    check(queue.dequeue(), 7)
    check(queue.dequeue(), 9)
    check(queue.dequeue(), 2)
    check(queue.is_empty(), True)
    check(queue.size(), 0)

    check(queue.dequeue(), -1)
    check(queue.front(), -1)

    queue.enqueue(6)
    check(queue.is_empty(), False)
    check(queue.front(), 6)
    check(queue.dequeue(), 6)
    check(queue.is_empty(), True)

    large = IntQueue()
    for i in range(5000):
        large.enqueue(i)
    large_ok = large.size() == 5000 and large.front() == 0
    for i in range(5000):
        large_ok = large.dequeue() == i and large_ok
    check(large_ok and large.is_empty(), True)


test_queue()
