class BinaryNode:
    def __init__(self, value):
        self.value = value
        self.left = None
        self.right = None


def preorder(node):
    result = []
    preorder_helper(node, result)
    return result


# To visit node, append its value to the result list:
# result.append(node.value)  

def preorder_helper(node, result):
    # TODO
    pass


def inorder(node):
    result = []
    inorder_helper(node, result)
    return result


def inorder_helper(node, result):
    # TODO
    pass


def postorder(node):
    result = []
    postorder_helper(node, result)
    return result


def postorder_helper(node, result):
    # TODO
    pass


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


def check_summary(name, condition):
    global failures
    if condition:
        print("pass:", name)
    else:
        failures += 1
        print("fail:", name)


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


def test_traversals():
    empty = None

    check("preorder empty", preorder(empty), [])
    check("inorder empty", inorder(empty), [])
    check("postorder empty", postorder(empty), [])

    single = BinaryNode(11)

    check("preorder single", preorder(single), [11])
    check("inorder single", inorder(single), [11])
    check("postorder single", postorder(single), [11])

    complete = build_complete_tree()

    check(
        "preorder complete",
        preorder(complete),
        [1, 2, 4, 5, 3, 6]
    )
    check(
        "inorder complete",
        inorder(complete),
        [4, 2, 5, 1, 6, 3]
    )
    check(
        "postorder complete",
        postorder(complete),
        [4, 5, 2, 6, 3, 1]
    )

    check(
        "preorder left subtree",
        preorder(complete.left),
        [2, 4, 5]
    )
    check(
        "inorder left subtree",
        inorder(complete.left),
        [4, 2, 5]
    )
    check(
        "postorder left subtree",
        postorder(complete.left),
        [4, 5, 2]
    )

    degenerate = build_degenerate_tree()

    check(
        "preorder degenerate",
        preorder(degenerate),
        [7, 8, 9, 10]
    )
    check(
        "inorder degenerate",
        inorder(degenerate),
        [7, 8, 9, 10]
    )
    check(
        "postorder degenerate",
        postorder(degenerate),
        [10, 9, 8, 7]
    )


def test_irregular_tree_and_independent_results():
    root = BinaryNode(10)
    root.left = BinaryNode(20); root.right = BinaryNode(30)
    root.left.right = BinaryNode(40); root.left.right.left = BinaryNode(20)
    root.right.left = BinaryNode(50); root.right.left.right = BinaryNode(60)
    check("irregular preorder", preorder(root), [10, 20, 40, 20, 30, 50, 60])
    check("irregular inorder", inorder(root), [20, 20, 40, 10, 50, 60, 30])
    check("irregular postorder", postorder(root), [20, 40, 20, 60, 50, 30, 10])
    first = preorder(root)
    first.clear()
    check("returned lists are independent", preorder(root), [10, 20, 40, 20, 30, 50, 60])
    check("interior subtree", inorder(root.right), [50, 60, 30])


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
    pre, ino, post = preorder(nodes[0]), inorder(nodes[0]), postorder(nodes[0])
    ok = (len(pre) == count and len(ino) == count and len(post) == count
          and pre[0] == 0 and ino[0] == 65_535 and post[-1] == 0)
    ordered = list(range(count))
    ok = sorted(pre) == ordered and sorted(ino) == ordered and sorted(post) == ordered and ok
    check_summary("100,000-node depth-first traversals", ok)


test_traversals()
test_irregular_tree_and_independent_results()
test_hundred_thousand_node_complete_tree()
if failures:
    raise SystemExit(1)
