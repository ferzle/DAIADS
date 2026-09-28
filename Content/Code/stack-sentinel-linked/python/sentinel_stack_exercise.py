import inspect


class Node:
    def __init__(self, value, next_node):
        self.value = value
        self.next = next_node


class IntStack:
    def __init__(self):
        self.sentinel = Node(-1, None)  # dummy value, not part of the stack
        self._original_sentinel = self.sentinel
        self.count = 0

    def is_empty(self):
        # TODO
        return False

    def push(self, value):
        # TODO
        pass

    def pop(self):
        # TODO
        return -1

    def peek(self):
        # TODO
        return -1

    def size(self):
        # TODO
        return -1

    def has_valid_structure_for_testing(self):
        if self.sentinel is None or self.sentinel is not self._original_sentinel:
            return False
        slow = self.sentinel.next
        fast = self.sentinel.next
        while fast is not None and fast.next is not None:
            slow = slow.next
            fast = fast.next.next
            if slow is fast:
                return False
        reachable = 0
        node = self.sentinel.next
        while node is not None:
            reachable += 1
            node = node.next
        return reachable == self.count


failures = 0


def check(actual, expected):
    global failures
    if actual == expected:
        print("pass")
    else:
        line = inspect.currentframe().f_back.f_lineno
        failures += 1
        print(f"fail at test line {line}: expected {expected!r} but got {actual!r}")


def test_stack():
    stack = IntStack()

    check(stack.is_empty(), True)
    check(stack.size(), 0)
    check(stack.pop(), -1)
    check(stack.peek(), -1)
    check(stack.size(), 0)

    stack.push(12)
    check(stack.is_empty(), False)
    check(stack.size(), 1)
    check(stack.peek(), 12)

    stack.push(7)
    check(stack.size(), 2)
    check(stack.peek(), 7)

    stack.push(19)
    check(stack.size(), 3)
    check(stack.peek(), 19)
    check(stack.has_valid_structure_for_testing(), True)

    check(stack.pop(), 19)
    check(stack.size(), 2)
    check(stack.peek(), 7)

    check(stack.pop(), 7)
    check(stack.size(), 1)
    check(stack.peek(), 12)

    check(stack.pop(), 12)
    check(stack.size(), 0)
    check(stack.is_empty(), True)
    check(stack.has_valid_structure_for_testing(), True)

    check(stack.pop(), -1)
    check(stack.peek(), -1)
    check(stack.size(), 0)

    stack.push(5)
    check(stack.is_empty(), False)
    check(stack.size(), 1)
    check(stack.peek(), 5)
    check(stack.pop(), 5)
    check(stack.size(), 0)
    check(stack.is_empty(), True)


def test_long_runs_and_repeated_reuse():
    stack = IntStack()
    for round_number in range(25):
        check(stack.is_empty(), True)
        for i in range(200):
            stack.push(round_number * 1000 + i)
            check(stack.size(), i + 1)
            check(stack.peek(), round_number * 1000 + i)
        for i in range(199, -1, -1):
            check(stack.pop(), round_number * 1000 + i)
            check(stack.size(), i)
        check(stack.pop(), -1)
        check(stack.is_empty(), True)


def test_large_aggregate_workload():
    n = 100_000
    stack = IntStack()
    for value in range(n):
        stack.push(value)
    ok = (stack.size() == n and stack.peek() == n - 1
          and stack.has_valid_structure_for_testing())
    for value in range(n - 1, -1, -1):
        ok = stack.pop() == value and ok
    ok = stack.is_empty() and stack.has_valid_structure_for_testing() and ok
    check(ok, True)


test_stack()
test_long_runs_and_repeated_reuse()
test_large_aggregate_workload()

if failures:
    raise SystemExit(1)
