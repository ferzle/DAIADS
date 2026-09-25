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
        return self.count == 0

    def size(self):
        return self.count

    def clear(self):
        self.front = None
        self.back = None
        self.count = 0

    def peek_front(self):
        return -1 if self.is_empty() else self.front.value

    def peek_back(self):
        return -1 if self.is_empty() else self.back.value

    def add_front(self, value):
        node = Node(value)
        node.next = self.front
        if self.is_empty():
            self.back = node
        else:
            self.front.prev = node
        self.front = node
        self.count += 1
        return True

    def add_back(self, value):
        node = Node(value)
        node.prev = self.back
        if self.is_empty():
            self.front = node
        else:
            self.back.next = node
        self.back = node
        self.count += 1
        return True

    def remove_front(self):
        if self.is_empty():
            return -1
        value = self.front.value
        self.front = self.front.next
        self.count -= 1
        if self.front is None:
            self.back = None
        else:
            self.front.prev = None
        return value

    def remove_back(self):
        if self.is_empty():
            return -1
        value = self.back.value
        self.back = self.back.prev
        self.count -= 1
        if self.back is None:
            self.front = None
        else:
            self.back.next = None
        return value

    def has_valid_structure_for_testing(self):
        if (self.front is None) != (self.back is None):
            return False
        if self.front is not None and self.front.prev is not None:
            return False
        if self.back is not None and self.back.next is not None:
            return False
        reachable = 0
        previous = None
        node = self.front
        while node is not None:
            if node.prev is not previous or reachable > self.count:
                return False
            previous = node
            reachable += 1
            node = node.next
        if previous is not self.back or reachable != self.count:
            return False
        backward = 0
        next_node = None
        node = self.back
        while node is not None:
            if node.next is not next_node or backward > self.count:
                return False
            next_node = node
            backward += 1
            node = node.prev
        return next_node is self.front and backward == self.count


failures = 0


def check(actual, expected):
    global failures
    line = inspect.currentframe().f_back.f_lineno
    if actual == expected:
        print(f"PASS at test line {line}: got {actual!r}")
    else:
        failures += 1
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
    check(deque.has_valid_structure_for_testing(), True)

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
    check(deque.has_valid_structure_for_testing(), True)
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


def test_large_mixed_runs():
    deque = IntDeque()
    for round_number in range(20):
        for i in range(100):
            check(deque.add_front(round_number * 1000 + i), True)
            check(deque.add_back(round_number * 1000 + 500 + i), True)
        check(deque.size(), 200)
        for i in range(99, -1, -1):
            check(deque.remove_front(), round_number * 1000 + i)
        for i in range(99, -1, -1):
            check(deque.remove_back(), round_number * 1000 + 500 + i)
        check(deque.is_empty(), True)
    deque.add_front(7)
    deque.clear()
    check(deque.is_empty(), True)
    check(deque.peek_front(), -1)
    check(deque.peek_back(), -1)
    check(deque.add_back(8), True)
    check(deque.remove_front(), 8)


def test_large_aggregate_workload():
    n = 100_000
    deque = IntDeque()
    ok = True
    for value in range(n):
        ok = deque.add_back(value) and ok
    ok = (deque.size() == n and deque.peek_front() == 0
          and deque.peek_back() == n - 1
          and deque.has_valid_structure_for_testing() and ok)
    for value in range(n):
        ok = deque.remove_front() == value and ok
    ok = deque.is_empty() and deque.has_valid_structure_for_testing() and ok
    check(ok, True)


test_deque()
test_large_mixed_runs()
test_large_aggregate_workload()

if failures:
    raise SystemExit(1)
