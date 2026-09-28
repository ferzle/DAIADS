class AVLTree:
    class Node:
        def __init__(self, key, parent=None):
            self.key = key
            self.left = None
            self.right = None
            self.parent = parent
            self.height = 0

    def __init__(self):
        self.root = None
        self.size = 0

    def is_empty(self):
        return self.size == 0

    def height_of(self, node):
        """Return -1 for None and the stored height otherwise."""
        return 0  # TODO

    def update_height(self, node):
        """Recompute node.height from its two children."""
        pass  # TODO

    def balance_factor(self, node):
        """Return height(left) - height(right)."""
        return 0  # TODO

    def search_node(self, key):
        """Follow one ordinary BST path."""
        return None  # TODO

    def contains(self, key):
        return self.search_node(key) is not None

    def replace_subtree(self, old_root, new_root):
        """Reconnect new_root in old_root's former position."""
        pass  # TODO

    def rotate_left(self, node):
        """Rotate, update downward then upward, and return the local root."""
        return node  # TODO

    def rotate_right(self, node):
        """Symmetric to rotate_left."""
        return node  # TODO

    def rebalance(self, node):
        """Update height and handle all four numeric cases."""
        return node  # TODO

    def bst_insert(self, key):
        """Place a BST leaf without changing size; return None for duplicate."""
        return None  # TODO

    def fix_after_insert(self, new_leaf):
        """Walk upward and stop immediately after the first repair."""
        pass  # TODO

    def insert(self, key):
        """Insert, increment size exactly once on success, and perform fix-up."""
        return False  # TODO

    def minimum_node(self, node):
        while node is not None and node.left is not None:
            node = node.left
        return node

    def bst_remove(self, target):
        """
        Remove target, which is known to be in the tree, using successor-key
        substitution when it has two children. Return the lowest node still
        in the tree whose height may have changed. A successful root removal
        may return None.
        """
        return None  # TODO

    def fix_after_remove(self, node):
        """Rebalance upward until the repaired subtree height is unchanged."""
        pass  # TODO

    def remove(self, key):
        """Find the target, remove it, change size once, and perform fix-up."""
        return False  # TODO

    def inorder_values(self):
        values = []
        self._append_inorder(self.root, values)
        return values

    def _append_inorder(self, node, values):
        if node is None:
            return
        self._append_inorder(node.left, values)
        values.append(node.key)
        self._append_inorder(node.right, values)

    def has_valid_structure(self):
        if self.root is not None and self.root.parent is not None:
            return False
        valid, _, count = self._validate(
            self.root, None, float("-inf"), float("inf")
        )
        return valid and count == self.size

    def _validate(self, node, expected_parent, low, high):
        if node is None:
            return True, -1, 0
        left_ok, left_height, left_count = self._validate(
            node.left, node, low, node.key
        )
        right_ok, right_height, right_count = self._validate(
            node.right, node, node.key, high
        )
        computed_height = 1 + max(left_height, right_height)
        valid = (
            left_ok
            and right_ok
            and node.parent is expected_parent
            and low < node.key < high
            and node.height == computed_height
            and abs(left_height - right_height) <= 1
        )
        return valid, computed_height, 1 + left_count + right_count


def check(condition, description):
    print(("pass: " if condition else "fail: ") + description)


def main():
    tree = AVLTree()
    check(tree.is_empty(), "a new tree is empty")
    check(tree.size == 0, "a new tree has size zero")
    check(not tree.contains(99), "contains reports an absent key")
    check(not tree.remove(99), "removing an absent key returns False")
    for key in [30, 20, 10, 40, 50, 25, 27]:
        check(tree.insert(key), "insert " + str(key))
        check(tree.has_valid_structure(), "invariants after inserting " + str(key))
    check(not tree.insert(25), "duplicate insertion leaves the set unchanged")
    check(tree.size == 7, "size counts seven distinct insertions")
    check(tree.contains(27), "contains finds a stored key")
    for key in [40, 30, 10]:
        check(tree.remove(key), "remove " + str(key))
        check(tree.has_valid_structure(), "invariants after removing " + str(key))
    rotation_patterns = ([30, 20, 10], [10, 20, 30], [30, 10, 20], [10, 30, 20])
    for number, pattern in enumerate(rotation_patterns, 1):
        rotation_tree = AVLTree()
        ok = all(rotation_tree.insert(key) for key in pattern)
        check(ok and rotation_tree.size == 3 and rotation_tree.has_valid_structure(),
              "rotation pattern " + str(number))
    large = AVLTree()
    large_ok = all(large.insert((i * 641) % 1000) for i in range(1000))
    large_ok = large.size == 1000 and large.has_valid_structure() and large_ok
    large_ok = all(large.remove(i) for i in range(0, 1000, 2)) and large_ok
    large_ok = large.size == 500 and large.has_valid_structure() and large_ok
    check(large_ok, "1000-key insertion and 500-key removal stress test")
    print("inorder:", tree.inorder_values())


if __name__ == "__main__":
    main()
