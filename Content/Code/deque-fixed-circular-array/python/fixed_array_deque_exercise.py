import inspect


class IntDeque:
    def __init__(self, capacity):
        self.A = [0] * capacity
        self.front = 0
        self.count = 0

    def is_empty(self):
        # TODO
        return False

    def is_full(self):
        # TODO
        return False

    def size(self):
        # TODO
        return -1

    def clear(self):
        # TODO
        pass

    def back_index(self):
        # TODO
        return -1

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


def check(actual, expected):
    line = inspect.currentframe().f_back.f_lineno
    if actual == expected:
        print(f"PASS at test line {line}: got {actual!r}")
    else:
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


test_deque()
