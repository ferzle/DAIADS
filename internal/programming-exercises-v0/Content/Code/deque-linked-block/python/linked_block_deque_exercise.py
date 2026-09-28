import inspect


class BlockNode:
    def __init__(self, block_size):
        self.values = [0] * block_size
        self.prev = None
        self.next = None


class IntDeque:
    def __init__(self, block_size):
        self.block_size = block_size
        self.first_block = None
        self.last_block = None
        self.front_index = 0
        self.back_index = 0
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
    deque = IntDeque(3)

    check(deque.is_empty(), True)
    check(deque.size(), 0)
    check(deque.peek_front(), -1)

    tiny_blocks = IntDeque(1)
    tiny_ok = True
    for i in range(500):
        tiny_ok = tiny_blocks.add_back(i) and tiny_ok
    for i in range(250):
        tiny_ok = tiny_blocks.remove_front() == i and tiny_ok
    for i in range(500, 750):
        tiny_ok = tiny_blocks.add_front(i) and tiny_ok
    for i in range(749, 499, -1):
        tiny_ok = tiny_blocks.remove_front() == i and tiny_ok
    for i in range(499, 249, -1):
        tiny_ok = tiny_blocks.remove_back() == i and tiny_ok
    check(tiny_ok and tiny_blocks.is_empty(), True)
    check(deque.peek_back(), -1)
    check(deque.remove_front(), -1)
    check(deque.remove_back(), -1)

    check(deque.add_back(4), True)       # [4]
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 4)
    check(deque.size(), 1)

    check(deque.add_back(7), True)       # [4, 7]
    check(deque.add_back(9), True)       # [4, 7, 9]
    check(deque.add_back(11), True)      # new back block: [4, 7, 9, 11]
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 11)
    check(deque.size(), 4)

    check(deque.add_front(2), True)      # new front block: [2, 4, 7, 9, 11]
    check(deque.add_front(1), True)      # [1, 2, 4, 7, 9, 11]
    check(deque.add_front(0), True)      # [0, 1, 2, 4, 7, 9, 11]
    check(deque.peek_front(), 0)
    check(deque.peek_back(), 11)
    check(deque.size(), 7)

    check(deque.remove_front(), 0)       # [1, 2, 4, 7, 9, 11]
    check(deque.remove_front(), 1)       # [2, 4, 7, 9, 11]
    check(deque.remove_front(), 2)       # first block may be removed: [4, 7, 9, 11]
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 11)
    check(deque.size(), 4)

    check(deque.remove_back(), 11)       # [4, 7, 9]
    check(deque.remove_back(), 9)        # [4, 7]
    check(deque.peek_front(), 4)
    check(deque.peek_back(), 7)
    check(deque.size(), 2)

    check(deque.add_front(3), True)      # [3, 4, 7]
    check(deque.add_back(8), True)       # [3, 4, 7, 8]
    check(deque.add_back(10), True)      # [3, 4, 7, 8, 10]
    check(deque.peek_front(), 3)
    check(deque.peek_back(), 10)

    check(deque.remove_back(), 10)       # [3, 4, 7, 8]
    check(deque.remove_front(), 3)       # [4, 7, 8]
    check(deque.remove_back(), 8)        # [4, 7]
    check(deque.remove_front(), 4)       # [7]
    check(deque.peek_front(), 7)
    check(deque.peek_back(), 7)
    check(deque.size(), 1)

    check(deque.remove_back(), 7)        # []
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
