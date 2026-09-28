import inspect


class Node:
    def __init__(self, value, next_node):
        self.value = value
        self.next = next_node


class IntStack:
    def __init__(self):
        self.head = None
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


def check(actual, expected):
    if actual == expected:
        print("pass")
    else:
        line = inspect.currentframe().f_back.f_lineno
        print(f"fail at test line {line}: expected {expected!r} but got {actual!r}")


def test_stack():
    stack = IntStack()

    check(stack.is_empty(), True)
    check(stack.size(), 0)
    check(stack.pop(), -1)
    check(stack.peek(), -1)
    check(stack.size(), 0)

    stack.push(4)
    check(stack.is_empty(), False)
    check(stack.size(), 1)
    check(stack.peek(), 4)
    check(stack.size(), 1)

    stack.push(7)
    check(stack.size(), 2)
    check(stack.peek(), 7)

    stack.push(9)
    check(stack.size(), 3)
    check(stack.peek(), 9)

    check(stack.pop(), 9)
    check(stack.size(), 2)
    check(stack.peek(), 7)

    stack.push(2)
    check(stack.size(), 3)
    check(stack.peek(), 2)

    check(stack.pop(), 2)
    check(stack.pop(), 7)
    check(stack.pop(), 4)
    check(stack.size(), 0)
    check(stack.is_empty(), True)

    check(stack.pop(), -1)
    check(stack.peek(), -1)
    check(stack.size(), 0)

    stack.push(6)
    check(stack.is_empty(), False)
    check(stack.size(), 1)
    check(stack.peek(), 6)
    check(stack.pop(), 6)
    check(stack.size(), 0)
    check(stack.is_empty(), True)


test_stack()
