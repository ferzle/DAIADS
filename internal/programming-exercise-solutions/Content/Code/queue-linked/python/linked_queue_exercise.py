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
        return self.head is None

    def enqueue(self, value):
        node = Node(value)
        if self.is_empty():
            self.head = node
        else:
            self.tail.next = node
        self.tail = node
        self.count += 1

    def dequeue(self):
        if self.is_empty():
            return -1
        value = self.head.value
        self.head = self.head.next
        self.count -= 1
        if self.head is None:
            self.tail = None
        return value

    def front(self):
        return -1 if self.is_empty() else self.head.value

    def size(self):
        return self.count

    def has_valid_structure_for_testing(self):
        if (self.head is None) != (self.tail is None):
            return False
        if self.tail is not None and self.tail.next is not None:
            return False
        slow = self.head
        fast = self.head
        while fast is not None and fast.next is not None:
            slow = slow.next
            fast = fast.next.next
            if slow is fast:
                return False
        reachable = 0
        last = None
        node = self.head
        while node is not None:
            last = node
            reachable += 1
            node = node.next
        return reachable == self.count and last is self.tail


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
    queue = IntQueue()

    check(queue.is_empty(), True)
    check(queue.size(), 0)
    check(queue.dequeue(), -1)
    check(queue.front(), -1)

    queue.enqueue(4)
    check(queue.is_empty(), False)
    check(queue.size(), 1)
    check(queue.front(), 4)
    check(queue.has_valid_structure_for_testing(), True)

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
    check(queue.has_valid_structure_for_testing(), True)

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


def test_long_runs_and_singleton_reuse():
    queue = IntQueue()
    for round_number in range(50):
        queue.enqueue(round_number)
        check(queue.front(), round_number)
        check(queue.dequeue(), round_number)
        check(queue.is_empty(), True)
    for i in range(1000):
        queue.enqueue(i)
    for i in range(1000):
        check(queue.front(), i)
        check(queue.dequeue(), i)
        check(queue.size(), 999 - i)
    queue.enqueue(77)
    check(queue.dequeue(), 77)
    check(queue.is_empty(), True)


def test_large_aggregate_workload():
    n = 100_000
    queue = IntQueue()
    for value in range(n):
        queue.enqueue(value)
    ok = (queue.size() == n and queue.front() == 0
          and queue.has_valid_structure_for_testing())
    for value in range(n):
        ok = queue.dequeue() == value and ok
    ok = queue.is_empty() and queue.has_valid_structure_for_testing() and ok
    check(ok, True)


test_queue()
test_long_runs_and_singleton_reuse()
test_large_aggregate_workload()

if failures:
    raise SystemExit(1)
