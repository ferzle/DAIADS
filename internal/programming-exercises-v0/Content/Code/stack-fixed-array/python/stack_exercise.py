import inspect


class IntStack:
    def __init__(self, capacity):
        self.A = [0] * capacity
        self.top = -1

    def is_empty(self):
        # TODO
        return False

    def is_full(self):
        # TODO
        return False

    def push(self, value):
        # TODO
        return False

    def pop(self):
        # TODO
        return -1

    def peek(self):
        # TODO
        return -1

    def size(self):
        # TODO
        return -1


def check(actual, expected):
    if actual == expected:
        print("pass")
    else:
        line = inspect.currentframe().f_back.f_lineno
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
    check(stack.size(), 4)
    check(stack.peek(), 2)

    check(stack.pop(), 2)
    check(stack.size(), 3)

    check(stack.pop(), 9)
    check(stack.size(), 2)

    check(stack.push(6), True)
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


test_stack()
