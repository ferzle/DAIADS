import inspect


class Node:
    def __init__(self, value):
        self.value = value
        self.next = None
        self.prev = None


class IntDeque:
    def __init__(self):
        self.front = None
        self.back = None
        self.count = 0

    def is_empty(self):
        # TODO
        return False

    def size(self):
        # TODO
        return -1

    def clear(self):
        # TODO
        pass

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
    deque = IntDeque()

    check(deque.is_empty(), True)
    check(deque.size(), 0)

    transitions = IntDeque()
    transition_ok = True
    for i in range(500):
        transition_ok = transitions.add_front(i) and transition_ok
        transition_ok = transitions.add_back(-i) and transition_ok
        transition_ok = transitions.remove_front() == i and transition_ok
        transition_ok = transitions.remove_back() == -i and transition_ok
        transition_ok = transitions.is_empty() and transition_ok
    check(transition_ok, True)
    check(deque.peek_front(), -1)
    check(deque.peek_back(), -1)
    check(deque.remove_front(), -1)
    check(deque.remove_back(), -1)

    check(deque.add_back(4), True)       # [4]
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 4)
    check(deque.size(), 1)
    check(deque.is_empty(), False)

    check(deque.add_back(7), True)       # [4, 7]
    check(deque.add_front(2), True)      # [2, 4, 7]
    check(deque.add_back(9), True)       # [2, 4, 7, 9]

    check(deque.size(), 4)
    check(deque.peek_front(), 2)
    check(deque.peek_back(), 9)

    check(deque.remove_front(), 2)       # [4, 7, 9]
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 9)
    check(deque.size(), 3)

    check(deque.remove_back(), 9)        # [4, 7]
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 7)
    check(deque.size(), 2)

    check(deque.add_front(1), True)      # [1, 4, 7]
    check(deque.add_back(11), True)      # [1, 4, 7, 11]
    check(deque.add_front(0), True)      # [0, 1, 4, 7, 11]

    check(deque.size(), 5)
    check(deque.peek_front(), 0)
    check(deque.peek_back(), 11)

    check(deque.remove_back(), 11)       # [0, 1, 4, 7]
    check(deque.remove_front(), 0)       # [1, 4, 7]
    check(deque.remove_back(), 7)        # [1, 4]
    check(deque.remove_front(), 1)       # [4]

    check(deque.size(), 1)
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 4)

    check(deque.remove_back(), 4)        # []
    check(deque.is_empty(), True)
    check(deque.size(), 0)
    check(deque.peek_front(), -1)
    check(deque.peek_back(), -1)
    check(deque.remove_front(), -1)
    check(deque.remove_back(), -1)

    check(deque.add_front(6), True)      # [6]
    check(deque.peek_front(), 6)
    check(deque.peek_back(), 6)

    check(deque.add_back(8), True)       # [6, 8]
    check(deque.peek_front(), 6)
    check(deque.peek_back(), 8)

    deque.clear()                        # []
    check(deque.is_empty(), True)
    check(deque.size(), 0)
    check(deque.peek_front(), -1)
    check(deque.peek_back(), -1)

    check(deque.add_back(10), True)      # [10]
    check(deque.remove_front(), 10)      # []
    check(deque.is_empty(), True)
    check(deque.size(), 0)


test_deque()
