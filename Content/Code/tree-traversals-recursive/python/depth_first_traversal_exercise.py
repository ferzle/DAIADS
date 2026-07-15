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


test_traversals()
