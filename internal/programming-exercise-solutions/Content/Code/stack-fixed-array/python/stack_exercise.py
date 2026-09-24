import inspect


class IntStack:
    def __init__(self, capacity):
        self.A = [0] * capacity
        self.top = -1

    def is_empty(self):
        return self.top == -1

    def is_full(self):
        return self.top == len(self.A) - 1

    def push(self, value):
        if self.is_full():
            return False
        self.top += 1
        self.A[self.top] = value
        return True

    def pop(self):
        if self.is_empty():
            return -1
        value = self.A[self.top]
        self.top -= 1
        return value

    def peek(self):
        if self.is_empty():
            return -1
        return self.A[self.top]

    def size(self):
        return self.top + 1

    def used_values_for_testing(self):
        return self.A[:self.top + 1]


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
    stack = IntStack(4)

    check(stack.size(), 0)
    check(stack.pop(), -1)
    check(stack.peek(), -1)
    check(stack.size(), 0)

    check(stack.push(4), True)
    check(stack.size(), 1)
    check(stack.peek(), 4)
    check(stack.size(), 1)

    check(stack.push(7), True)
    check(stack.size(), 2)
    check(stack.peek(), 7)

    check(stack.push(9), True)
    check(stack.size(), 3)
    check(stack.peek(), 9)

    check(stack.push(2), True)
    check(stack.size(), 4)
    check(stack.peek(), 2)

    check(stack.push(5), False)
    check(stack.used_values_for_testing(), [4, 7, 9, 2])
    check(stack.size(), 4)
    check(stack.peek(), 2)

    check(stack.pop(), 2)
    check(stack.size(), 3)

    check(stack.pop(), 9)
    check(stack.size(), 2)

    check(stack.push(6), True)
    check(stack.used_values_for_testing(), [4, 7, 6])
    check(stack.size(), 3)
    check(stack.peek(), 6)

    check(stack.pop(), 6)
    check(stack.size(), 2)

    check(stack.pop(), 7)
    check(stack.size(), 1)

    check(stack.pop(), 4)
    check(stack.size(), 0)

    check(stack.pop(), -1)
    check(stack.peek(), -1)
    check(stack.size(), 0)

def test_boundary_sizes_and_reuse():
    for capacity in (1, 2, 5, 32):
        stack = IntStack(capacity)
        check(stack.is_empty(), True)
        check(stack.is_full(), False)
        for i in range(capacity):
            check(stack.push(1000 + i), True)
            check(stack.size(), i + 1)
            check(stack.peek(), 1000 + i)
        check(stack.is_full(), True)
        check(stack.push(9999), False)
        check(stack.size(), capacity)
        for i in range(capacity - 1, -1, -1):
            check(stack.pop(), 1000 + i)
            check(stack.size(), i)
        check(stack.is_empty(), True)
        check(stack.push(77), True)
        check(stack.pop(), 77)
        check(stack.is_empty(), True)


def test_large_aggregate_workload():
    n = 100_000
    stack = IntStack(n)
    ok = True
    for value in range(n):
        ok = stack.push(value) and ok
    ok = stack.size() == n and stack.is_full() and stack.peek() == n - 1 and ok
    for value in range(n - 1, -1, -1):
        ok = stack.pop() == value and ok
    ok = stack.is_empty() and stack.size() == 0 and ok
    check(ok, True)


test_stack()
test_boundary_sizes_and_reuse()
test_large_aggregate_workload()

if failures:
    raise SystemExit(1)
