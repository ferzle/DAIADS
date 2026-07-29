import inspect


class BinaryNode:
    def __init__(self, value):
        self.value = value
        self.left = None
        self.right = None

def size(node):
    # TODO
    return -1


def height(node):
    # TODO
    return 0


def count_leaves(node):
    # TODO
    return -1


def count_two_child_nodes(node):
    # TODO
    return -1


def check(actual, expected):
    if actual == expected:
        print("pass")
    else:
        line = inspect.currentframe().f_back.f_lineno
        print(f"fail at test line {line}: expected {expected!r} but got {actual!r}")


def build_complete_tree():
    root = BinaryNode(1)

    root.left = BinaryNode(2)
    root.right = BinaryNode(3)

    root.left.left = BinaryNode(4)
    root.left.right = BinaryNode(5)
    root.right.left = BinaryNode(6)

    return root


def build_degenerate_tree():
    root = BinaryNode(7)

    root.right = BinaryNode(8)
    root.right.right = BinaryNode(9)
    root.right.right.right = BinaryNode(10)

    return root


def test_algorithms():
    empty = None

    check(size(empty), 0)
    check(height(empty), -1)
    check(count_leaves(empty), 0)
    check(count_two_child_nodes(empty), 0)

    single = BinaryNode(11)

    check(size(single), 1)
    check(height(single), 0)
    check(count_leaves(single), 1)
    check(count_two_child_nodes(single), 0)

    complete = build_complete_tree()

    check(size(complete), 6)
    check(height(complete), 2)
    check(count_leaves(complete), 3)
    check(count_two_child_nodes(complete), 2)

    check(size(complete.left), 3)
    check(height(complete.left), 1)
    check(count_leaves(complete.left), 2)
    check(count_two_child_nodes(complete.left), 1)

    degenerate = build_degenerate_tree()

    check(size(degenerate), 4)
    check(height(degenerate), 3)
    check(count_leaves(degenerate), 1)
    check(count_two_child_nodes(degenerate), 0)

test_algorithms()
