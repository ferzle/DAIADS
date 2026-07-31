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
    # Valid nodes use only part of this fixed capacity. The extra key and
    # child slots hold the temporary overflow created during insertion.
    keys: list[int] = field(default_factory=lambda: [0, 0, 0])
    children: list[Optional["Node"]] = field(
        default_factory=lambda: [None, None, None, None]
    )
    key_count: int = 0
    child_count: int = 0
    parent: Optional["Node"] = None

    @property
    def is_leaf(self) -> bool:
        return self.child_count == 0


@dataclass
class Location:
    """Describe where a root-to-leaf search ended.

    If ``found`` is true, ``node.keys[key_index]`` is the requested key. If
    it is false and ``node`` is not None, the node is the leaf where the key
    belongs and ``key_index`` is its insertion position. ``node is None``
    represents an empty tree. This lets contains, insert, and remove share
    one search.
    """

    node: Optional[Node]
    key_index: int
    found: bool


@dataclass
class SplitResult:
    """Values returned when an overfull node is split.

    The original node becomes the left half. ``promoted_key`` moves into its
    parent and ``right_node`` is attached immediately after the left half.
    """

    promoted_key: int
    right_node: Node


class TwoThreeTree:
    def __init__(self) -> None:
        self.root: Optional[Node] = None
        self._size = 0

    def __len__(self) -> int:
        return self._size

    def size(self) -> int:
        """Return the number of stored keys, not the number of nodes."""
        return self._size

    def is_empty(self) -> bool:
        return self._size == 0

    @staticmethod
    def _find_position(node: Node, key: int) -> int:
        """Return the first index i for which node.keys[i] >= key."""
        i = 0
        while i < node.key_count and node.keys[i] < key:
            i += 1
        return i

    def _locate(self, key: int) -> Location:
        """Locate key, or the leaf position where it would be inserted."""
        node = self.root
        while node is not None:
            index = self._find_position(node, key)
            if index < node.key_count and node.keys[index] == key:
                return Location(node, index, True)
            if node.is_leaf:
                return Location(node, index, False)
            node = node.children[index]
        return Location(None, 0, False)

    def contains(self, key: int) -> bool:
        return self._locate(key).found

    def __contains__(self, key: int) -> bool:
        return self.contains(key)

    @staticmethod
    def _child_index(parent: Node, child: Node) -> int:
        """Return child's active index, or -1 for inconsistent references."""
        for index in range(parent.child_count):
            if parent.children[index] is child:
                return index
        return -1

    @staticmethod
    def _insert_key_at(node: Node, index: int, key: int) -> None:
        """Insert into the active key prefix; the caller preserves order."""
        for i in range(node.key_count, index, -1):
            node.keys[i] = node.keys[i - 1]
        node.keys[index] = key
        node.key_count += 1

    @staticmethod
    def _remove_key_at(node: Node, index: int) -> int:
        """Remove and return an active key, closing the array gap."""
        removed = node.keys[index]
        for i in range(index, node.key_count - 1):
            node.keys[i] = node.keys[i + 1]
        node.key_count -= 1
        return removed

    @staticmethod
    def _insert_child_at(
        node: Node, index: int, child: Optional[Node]
    ) -> None:
        """Insert a child and maintain its upward parent reference."""
        for i in range(node.child_count, index, -1):
            node.children[i] = node.children[i - 1]
        node.children[index] = child
        node.child_count += 1
        if child is not None:
            child.parent = node

    @staticmethod
    def _remove_child_at(node: Node, index: int) -> Optional[Node]:
        """Remove a child, clear the stale slot, and detach its parent."""
        removed = node.children[index]
        for i in range(index, node.child_count - 1):
            node.children[i] = node.children[i + 1]
        node.child_count -= 1
        node.children[node.child_count] = None
        if removed is not None:
            removed.parent = None
        return removed

    def insert(self, key: int) -> bool:
        """Insert a distinct key; return False for a duplicate."""
        # TODO:
        # 1. Search to a leaf and reject a duplicate.
        # 2. Insert the key into the leaf in sorted order.
        # 3. Split every temporary three-key node and promote its median.
        # 4. Create a new root if the old root splits.
        # 5. Update _size exactly once.
        return False

    def _split_overfull_node(self, node: Node) -> SplitResult:
        """Split node into a left half, promoted key, and new right half."""
        # TODO: Distribute four children as [T0,T1] and [T2,T3] and
        # update every moved child's parent reference.
        raise NotImplementedError

    def _promote_to_parent(
        self,
        parent: Node,
        left_index: int,
        promoted_key: int,
        right_child: Node,
    ) -> None:
        """Insert a child split; the left child is already at left_index."""
        self._insert_key_at(parent, left_index, promoted_key)
        self._insert_child_at(parent, left_index + 1, right_child)

    @staticmethod
    def _minimum_location(node: Node) -> Location:
        """Return the leftmost leaf and its first key position."""
        while not node.is_leaf:
            child = node.children[0]
            assert child is not None
            node = child
        return Location(node, 0, True)

    @staticmethod
    def _maximum_location(node: Node) -> Location:
        """Return the rightmost leaf and its last key position."""
        while not node.is_leaf:
            child = node.children[node.key_count]
            assert child is not None
            node = child
        return Location(node, node.key_count - 1, True)

    def minimum(self) -> Optional[int]:
        """Return the smallest key, or None when empty."""
        if self.root is None:
            return None
        location = self._minimum_location(self.root)
        assert location.node is not None
        return location.node.keys[location.key_index]

    def maximum(self) -> Optional[int]:
        """Return the largest key, or None when empty."""
        # TODO: Use _maximum_location symmetrically to minimum().
        return None

    def predecessor(self, key: int) -> Optional[int]:
        """Return the preceding key, or None if absent or first."""
        # TODO
        return None

    def successor(self, key: int) -> Optional[int]:
        """Return the following key, or None if absent or last."""
        # TODO
        return None

    def values_in_range(self, low: int, high: int) -> list[int]:
        """Return keys in the inclusive interval [low, high]."""
        # TODO: Prune child ranges that cannot intersect the interval.
        return []

    def remove(self, key: int) -> bool:
        """Remove key if present; return False if it is absent."""
        # TODO:
        # 1. If internal, replace the key with a predecessor/successor.
        # 2. Remove the key from its leaf.
        # 3. Repair deficiency by redistribution or merging upward.
        # 4. Replace an empty root by its only child when needed.
        # 5. Update _size exactly once.
        return False

    def _repair_deficiency(self, node: Node) -> None:
        """Repair a zero-key nonroot node, possibly continuing upward."""
        # TODO
        pass

    def _redistribute_from_left(self, parent: Node, child_index: int) -> None:
        # TODO: Rotate a parent separator down and sibling maximum up.
        pass

    def _redistribute_from_right(self, parent: Node, child_index: int) -> None:
        # TODO: Rotate a parent separator down and sibling minimum up.
        pass

    def _merge_children(self, parent: Node, separator_index: int) -> None:
        """Merge adjacent children and their separator into the left child."""
        # TODO: Removing the separator may leave parent deficient.
        pass

    # -----------------------------------------------------------------
    # Completed traversal and validation helpers. Do not remove these;
    # the tests use them to detect structural errors after every update.
    # -----------------------------------------------------------------

    def inorder_values(self) -> list[int]:
        result: list[int] = []

        def visit(node: Optional[Node]) -> None:
            if node is None:
                return
            for i in range(node.key_count):
                if not node.is_leaf:
                    visit(node.children[i])
                result.append(node.keys[i])
            if not node.is_leaf:
                visit(node.children[node.key_count])

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

            if node.key_count not in (1, 2):
                return False
            if node.key_count == 2 and node.keys[0] >= node.keys[1]:
                return False
            for i in range(node.key_count):
                if node.keys[i] <= lower or node.keys[i] >= upper:
                    return False
            counted_keys += node.key_count

            if node.is_leaf:
                if leaf_depth is None:
                    leaf_depth = depth
                return leaf_depth == depth

            if node.child_count != node.key_count + 1:
                return False
            if any(
                child is not None
                for child in node.children[node.child_count:]
            ):
                return False
            for i in range(node.child_count):
                child = node.children[i]
                if child is None or child.parent is not node:
                    return False
                child_lower = lower if i == 0 else node.keys[i - 1]
                child_upper = upper if i == node.key_count else node.keys[i]
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
    check(tree.size() == 0, "named size operation agrees with len")
    check(tree.minimum() is None, "empty tree has no minimum")
    check(tree.maximum() is None, "empty tree has no maximum")
    check(not tree.contains(10), "named contains operation reports absence")
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
    check(tree.predecessor(1) is None, "minimum has no predecessor")
    check(tree.predecessor(40) == 35, "predecessor of 40 is 35")
    check(tree.successor(40) == 45, "successor of 40 is 45")
    check(tree.successor(75) is None, "maximum has no successor")
    check(tree.predecessor(999) is None, "absent key has no predecessor")
    check(
        tree.values_in_range(18, 45) == [18, 20, 25, 30, 35, 40, 45],
        "inclusive range from 18 through 45",
    )
    check(tree.values_in_range(45, 18) == [], "reversed range is empty")

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
