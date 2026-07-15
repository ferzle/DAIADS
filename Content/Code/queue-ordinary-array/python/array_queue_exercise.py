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
    if actual == expected:
        print("pass")
    else:
        print("fail: expected", expected, "but got", actual)


def test_queue():
    queue = IntQueue(3)

    check(queue.is_empty(), True)
    check(queue.is_full(), False)
    check(queue.size(), 0)
    check(queue.dequeue(), -1)
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
