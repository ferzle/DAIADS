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


failures = 0


def check(name, actual, expected):
    global failures
    if actual == expected:
        print("pass:", name)
    else:
        failures += 1
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


def test_sparse_wide_and_independent_results():
    root = BinaryNode(10)
    root.left = BinaryNode(20); root.right = BinaryNode(30)
    root.left.right = BinaryNode(40); root.right.left = BinaryNode(50)
    root.left.right.left = BinaryNode(60); root.right.left.right = BinaryNode(70)
    check("sparse mixed tree", level_order(root), [10, 20, 30, 40, 50, 60, 70])
    check("interior subtree", level_order(root.left), [20, 40, 60])
    first = level_order(root)
    first.clear()
    check("returned lists are independent", level_order(root), [10, 20, 30, 40, 50, 60, 70])
    chain = BinaryNode(0)
    cursor = chain
    for i in range(1, 201):
        cursor.right = BinaryNode(i)
        cursor = cursor.right
    check("long right-only tree", level_order(chain), list(range(201)))


test_level_order()
test_sparse_wide_and_independent_results()
if failures:
    raise SystemExit(1)
