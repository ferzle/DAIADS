from collections import deque


class BinaryNode:
    def __init__(self, value):
        self.value = value
        self.left = None
        self.right = None


def level_order(root):
    result = []

    if root is None:
        return result

    queue = deque([root])

    while queue:
        node = queue.popleft()

        # To visit node:
        # result.append(node.value)

        # TODO: Visit node and add its existing children
        # to the back of the queue in left-to-right order.

    return result


def check(name, actual, expected):
    if actual == expected:
        print("pass:", name)
    else:
        print(
            "fail:", name,
            "; expected", expected,
            "but got", actual
        )


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


def test_level_order():
    empty = None
    single = BinaryNode(11)
    complete = build_complete_tree()
    degenerate = build_degenerate_tree()

    check("level order empty", level_order(empty), [])
    check("level order single", level_order(single), [11])
    check(
        "level order complete",
        level_order(complete),
        [1, 2, 3, 4, 5, 6]
    )
    check(
        "level order left subtree",
        level_order(complete.left),
        [2, 4, 5]
    )
    check(
        "level order degenerate",
        level_order(degenerate),
        [7, 8, 9, 10]
    )


test_level_order()
