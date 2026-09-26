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


failures = 0


def check(actual, expected):
    global failures
    line = inspect.currentframe().f_back.f_lineno
    if actual == expected:
        print(f"PASS at test line {line}: got {actual!r}")
    else:
        failures += 1
        print(f"FAIL at test line {line}: expected {expected!r} but got {actual!r}")


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

def test_irregular_tree_and_every_subtree():
    root = BinaryNode(10)
    root.left = BinaryNode(20); root.right = BinaryNode(30)
    root.left.right = BinaryNode(40); root.left.right.left = BinaryNode(50)
    root.right.left = BinaryNode(60); root.right.left.right = BinaryNode(70)
    root.right.right = BinaryNode(80); root.right.right.right = BinaryNode(90)
    check(size(root), 9); check(height(root), 3); check(count_leaves(root), 3); check(count_two_child_nodes(root), 2)
    check(size(root.left), 3); check(height(root.left), 2); check(count_leaves(root.left), 1)
    check(size(root.right), 5); check(height(root.right), 2); check(count_leaves(root.right), 2)
    chain = BinaryNode(0)
    cursor = chain
    for i in range(1, 200):
        cursor.right = BinaryNode(i)
        cursor = cursor.right
    check(size(chain), 200); check(height(chain), 199); check(count_leaves(chain), 1); check(count_two_child_nodes(chain), 0)


def test_hundred_thousand_node_complete_tree():
    count = 100_000
    nodes = [BinaryNode(i) for i in range(count)]
    for i, node in enumerate(nodes):
        left = 2 * i + 1
        right = left + 1
        if left < count:
            node.left = nodes[left]
        if right < count:
            node.right = nodes[right]
    check(size(nodes[0]), count)
    check(height(nodes[0]), 16)
    check(count_leaves(nodes[0]), 50_000)
    check(count_two_child_nodes(nodes[0]), 49_999)


test_algorithms()
test_irregular_tree_and_every_subtree()
test_hundred_thousand_node_complete_tree()

if failures:
    raise SystemExit(1)
