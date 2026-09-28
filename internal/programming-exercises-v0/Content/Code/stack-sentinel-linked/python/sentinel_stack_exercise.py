import inspect


class Node:
    def __init__(self, value, next_node):
        self.value = value
        self.next = next_node


class IntStack:
    def __init__(self):
        self.sentinel = Node(-1, None)  # dummy value, not part of the stack
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

    check(stack.pop(), 19)
    check(stack.size(), 2)
    check(stack.peek(), 7)

    check(stack.pop(), 7)
    check(stack.size(), 1)
    check(stack.peek(), 12)

    check(stack.pop(), 12)
    check(stack.size(), 0)
    check(stack.is_empty(), True)

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


test_stack()
