"""Starter implementation for an ordered set backed by a 2-3 tree.

Complete the methods marked TODO. During insertion repair, a node may
temporarily contain three keys and four children. During removal repair,
a nonroot node may temporarily contain zero keys and one child.
"""

from __future__ import annotations

from dataclasses import dataclass, field
import random
from typing import Optional


@dataclass
class Node:
    keys: list[int] = field(default_factory=list)
    children: list["Node"] = field(default_factory=list)
    parent: Optional["Node"] = None

    @property
    def is_leaf(self) -> bool:
        return not self.children


@dataclass
class Location:
    node: Node
    key_index: int


class TwoThreeTree:
    def __init__(self) -> None:
        self.root: Optional[Node] = None
        self._size = 0

    def __len__(self) -> int:
        return self._size

    def is_empty(self) -> bool:
        return self._size == 0

    @staticmethod
    def _find_position(node: Node, key: int) -> int:
        """Return the first index i for which node.keys[i] >= key."""
        i = 0
        while i < len(node.keys) and node.keys[i] < key:
            i += 1
        return i

    def _find_location(self, key: int) -> Optional[Location]:
        """Return the node and position containing key, or None."""
        # TODO: Follow exactly one child range at each internal node.
        return None

    def __contains__(self, key: int) -> bool:
        return self._find_location(key) is not None

    def insert(self, key: int) -> bool:
        """Insert a distinct key; return False for a duplicate."""
        # TODO:
        # 1. Search to a leaf and reject a duplicate.
        # 2. Insert the key into the leaf in sorted order.
        # 3. Split every temporary three-key node and promote its median.
        # 4. Create a new root if the old root splits.
        # 5. Update _size exactly once.
        return False

    def _split_overflow(self, node: Node) -> None:
        """Split a temporary three-key node and promote its median."""
        # TODO: Distribute four children as [T0,T1] and [T2,T3]
        # and update every moved child's parent reference.
        pass

    def minimum(self) -> Optional[int]:
        """Return the smallest key, or None when empty."""
        # TODO: Follow first-child references to the leftmost leaf.
        return None

    def maximum(self) -> Optional[int]:
        """Return the largest key, or None when empty."""
        # TODO: Follow last-child references to the rightmost leaf.
        return None

    def remove(self, key: int) -> bool:
        """Remove key if present; return False if it is absent."""
        # TODO:
        # 1. If internal, replace the key with a predecessor/successor.
        # 2. Remove the key from its leaf.
        # 3. Repair underflow by borrowing or merging upward.
        # 4. Replace an empty root by its only child when needed.
        # 5. Update _size exactly once.
        return False

    def _repair_underflow(self, node: Node) -> None:
        """Repair a zero-key nonroot node, possibly continuing upward."""
        # TODO
        pass

    def _borrow_from_left(self, parent: Node, child_index: int) -> None:
        # TODO: Rotate a parent separator down and sibling maximum up.
        pass

    def _borrow_from_right(self, parent: Node, child_index: int) -> None:
        # TODO: Rotate a parent separator down and sibling minimum up.
        pass

    def _merge_with_left(self, parent: Node, child_index: int) -> Node:
        # TODO: Return parent, which may now be underfull.
        return parent

    def _merge_with_right(self, parent: Node, child_index: int) -> Node:
        # TODO: Return parent, which may now be underfull.
        return parent

    # -----------------------------------------------------------------
    # Completed traversal and validation helpers. Do not remove these;
    # the tests use them to detect structural errors after every update.
    # -----------------------------------------------------------------

    def inorder_values(self) -> list[int]:
        result: list[int] = []

        def visit(node: Optional[Node]) -> None:
            if node is None:
                return
            for i, key in enumerate(node.keys):
                if not node.is_leaf:
                    visit(node.children[i])
                result.append(key)
            if not node.is_leaf:
                visit(node.children[len(node.keys)])

        visit(self.root)
        return result

    def height(self) -> int:
        result = -1
        node = self.root
        while node is not None:
            result += 1
            node = None if node.is_leaf else node.children[0]
        return result

    def has_valid_structure(self) -> bool:
        if self.root is None:
            return self._size == 0
        if self.root.parent is not None:
            return False

        leaf_depth: Optional[int] = None
        counted_keys = 0

        def validate(
            node: Node,
            lower: float,
            upper: float,
            depth: int,
        ) -> bool:
            nonlocal leaf_depth, counted_keys

            if len(node.keys) not in (1, 2):
                return False
            if len(node.keys) == 2 and node.keys[0] >= node.keys[1]:
                return False
            if any(key <= lower or key >= upper for key in node.keys):
                return False
            counted_keys += len(node.keys)

            if node.is_leaf:
                if leaf_depth is None:
                    leaf_depth = depth
                return leaf_depth == depth

            if len(node.children) != len(node.keys) + 1:
                return False
            for i, child in enumerate(node.children):
                if child is None or child.parent is not node:
                    return False
                child_lower = lower if i == 0 else node.keys[i - 1]
                child_upper = upper if i == len(node.keys) else node.keys[i]
                if not validate(child, child_lower, child_upper, depth + 1):
                    return False
            return True

        valid = validate(self.root, float("-inf"), float("inf"), 0)
        return valid and counted_keys == self._size


# ---------------------------- Tests ----------------------------

def check(condition: bool, description: str) -> None:
    print(("pass: " if condition else "FAIL: ") + description)


def check_contents(
    tree: TwoThreeTree,
    expected: set[int],
    description: str,
) -> None:
    check(tree.has_valid_structure(), description + " -- invariants")
    check(len(tree) == len(expected), description + " -- size")
    check(tree.inorder_values() == sorted(expected), description + " -- inorder")


def contents_match(tree: TwoThreeTree, expected: set[int]) -> bool:
    return (
        tree.has_valid_structure()
        and len(tree) == len(expected)
        and tree.inorder_values() == sorted(expected)
    )


def run_tests() -> None:
    tree = TwoThreeTree()
    expected: set[int] = set()

    check(tree.is_empty(), "new tree is empty")
    check(len(tree) == 0, "new tree has size zero")
    check(tree.minimum() is None, "empty tree has no minimum")
    check(tree.maximum() is None, "empty tree has no maximum")
    check(10 not in tree, "empty tree does not contain 10")
    check(not tree.remove(10), "cannot remove an absent key")

    values = [
        40, 20, 60, 10, 30, 50, 70, 5, 15, 25,
        35, 45, 55, 65, 75, 1, 8, 12, 18,
    ]
    for value in values:
        if not tree.insert(value):
            check(False, f"insert {value}")
            print("Complete insertion before running later tests.")
            return
        check(True, f"insert {value}")
        expected.add(value)
        check_contents(tree, expected, f"after inserting {value}")

    check(not tree.insert(30), "duplicate insertion returns False")
    check_contents(tree, expected, "after duplicate insertion")
    check(tree.minimum() == 1, "minimum is 1")
    check(tree.maximum() == 75, "maximum is 75")

    removals = [1, 8, 10, 20, 60, 75, 70, 65, 40, 5, 12]
    for value in removals:
        if not tree.remove(value):
            check(False, f"remove {value}")
            print("Complete removal before running randomized tests.")
            return
        check(True, f"remove {value}")
        expected.remove(value)
        check_contents(tree, expected, f"after removing {value}")
    check(not tree.remove(999), "absent removal returns False")
    check_contents(tree, expected, "after absent removal")

    # Sorted insertion forces repeated cascading splits.
    sorted_tree = TwoThreeTree()
    for value in range(1, 32):
        if not sorted_tree.insert(value) or not sorted_tree.has_valid_structure():
            check(False, f"sorted insertion through {value}")
            return
    check(True, "sorted insertion through 31")

    # Deterministic randomized differential test.
    random_tree = TwoThreeTree()
    oracle: set[int] = set()
    generator = random.Random(23023)
    for step in range(500):
        value = generator.randint(-50, 50)
        do_insert = bool(generator.getrandbits(1))
        if do_insert:
            expected_result = value not in oracle
            oracle.add(value)
            actual_result = random_tree.insert(value)
        else:
            expected_result = value in oracle
            oracle.discard(value)
            actual_result = random_tree.remove(value)
        if actual_result != expected_result or not contents_match(random_tree, oracle):
            check(False, f"randomized differential test at update {step}")
            return
    check(True, "500 randomized differential updates")

    # Remove everything to exercise cascading merges and root shrinkage.
    for value in sorted(oracle):
        if not random_tree.remove(value):
            check(False, f"remove all remaining keys at {value}")
            return
        oracle.remove(value)
        if not contents_match(random_tree, oracle):
            check(False, "invariants while removing all remaining keys")
            return
    check(True, "remove all remaining keys")
    check(random_tree.is_empty(), "tree is empty after removing every key")
    check(random_tree.height() == -1, "empty tree has height -1")


if __name__ == "__main__":
    run_tests()
