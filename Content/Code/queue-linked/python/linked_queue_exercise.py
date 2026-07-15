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
    if actual == expected:
        print("pass")
    else:
        print("fail: expected", expected, "but got", actual)


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


test_queue()
