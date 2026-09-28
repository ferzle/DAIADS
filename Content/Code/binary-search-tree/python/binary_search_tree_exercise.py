class BinarySearchTree:
  class Node:
    def __init__(self, key):
        self.key = key
        self.left = None
        self.right = None
        self.parent = None

  def __init__(self):
      self.root = None
      self.size = 0

  def is_empty(self):
      return self.size == 0

  def search_node(self, key):
      # TODO
      return None

  def contains(self, key):
      return self.search_node(key) is not None

  def insert(self, key):
      # TODO
      return False

  def minimum_node(self, node):
      # TODO
      return None

  def minimum(self):
      node = self.minimum_node(self.root)

      if node is None:
          return None

      return node.key

  def successor_node(self, node):
      # TODO
      return None

  def successor(self, key):
      node = self.search_node(key)

      if node is None:
          return None

      result = self.successor_node(node)

      if result is None:
          return None

      return result.key

  def replace_subtree(self, old_root, new_root):
      # TODO
      pass

  def remove(self, key):
      # TODO
      return False

  ###################################
  # The rest is testing-related code
  ###################################
  def inorder_list(self):
      result = []
      self.append_inorder(self.root, result)
      return result

  def append_inorder(self, node, result):
      if node is None:
          return

      self.append_inorder(node.left, result)
      result.append(node.key)
      self.append_inorder(node.right, result)

  def has_correct_parent_references(self):
      if self.root is not None and self.root.parent is not None:
          return False

      return self.check_parent_references(self.root)

  def root_key_for_testing(self):
      return None if self.root is None else self.root.key

  def check_parent_references(self, node):
      if node is None:
          return True

      if node.left is not None and node.left.parent is not node:
          return False

      if node.right is not None and node.right.parent is not node:
          return False

      return (self.check_parent_references(node.left)
          and self.check_parent_references(node.right))

failures = 0


def check(condition, description):
  global failures
  if condition:
    print("pass:", description)
  else:
    failures += 1
    print("fail:", description)

def test_binary_search_tree():
  tree = BinarySearchTree()

  check(tree.is_empty(), "a new tree is empty")
  check(tree.size == 0, "a new tree has size 0")
  check(tree.minimum() is None, "an empty tree has no minimum")
  check(not tree.contains(50), "an empty tree does not contain 50")
  check(not tree.remove(50), "an absent key cannot be removed")

  keys = [50, 30, 70, 20, 40, 60, 80, 55, 65, 57]

  for key in keys:
      check(tree.insert(key), "insert " + str(key))
      check(tree.has_correct_parent_references(),
          "parent references after inserting " + str(key))

  check(tree.size == 10, "the tree has size 10")
  check(not tree.is_empty(), "the tree is not empty")
  check(tree.contains(57), "the tree contains 57")
  check(not tree.contains(58), "the tree does not contain 58")

  check(tree.inorder_list() == [20, 30, 40, 50, 55, 57, 60, 65, 70, 80],
      "inorder traversal after insertion")
  check(tree.root_key_for_testing() == 50, "50 remains the root after insertion")

  check(not tree.insert(60), "duplicate insertion fails")
  check(tree.size == 10, "duplicate insertion does not change size")

  check(tree.minimum() == 20, "the minimum is 20")
  check(tree.successor(40) == 50, "the successor of 40 is 50")
  check(tree.successor(55) == 57, "the successor of 55 is 57")
  check(tree.successor(65) == 70, "the successor of 65 is 70")
  check(tree.successor(80) is None, "80 has no successor")
  check(tree.successor(58) is None, "an absent key has no successor")

  check(tree.remove(20), "remove the leaf 20")
  check(tree.inorder_list() == [30, 40, 50, 55, 57, 60, 65, 70, 80],
      "inorder traversal after removing 20")
  check( tree.has_correct_parent_references(),
      "parent references after removing 20")

  check(tree.remove(55), "remove the one-child node 55")
  check( tree.inorder_list() == [30, 40, 50, 57, 60, 65, 70, 80],
      "inorder traversal after removing 55")
  check( tree.has_correct_parent_references(),
      "parent references after removing 55")

  check(tree.remove(50), "remove the two-child root 50")
  check(tree.inorder_list() == [30, 40, 57, 60, 65, 70, 80],
      "inorder traversal after removing 50")
  check(tree.has_correct_parent_references(),
      "parent references after removing 50")
  check(tree.root_key_for_testing() == 57,
      "successor substitution changes the root key to 57")

  check(tree.size == 7, "the final size is 7")
  check(not tree.remove(50), "removing 50 again fails")
  check(tree.size == 7, "an unsuccessful removal does not change size")

  one_node_tree = BinarySearchTree()

  check(one_node_tree.insert(10), "insert into an empty tree")
  check(one_node_tree.remove(10), "remove the only node")
  check(one_node_tree.is_empty(), "the one-node tree becomes empty")
  check(one_node_tree.root_key_for_testing() is None,
      "the drained one-node tree has no root")
  check(one_node_tree.has_correct_parent_references(),
      "the empty tree has valid parent references")

def test_large_ordered_updates():
    tree = BinarySearchTree()
    for key in range(500): check(tree.insert(key), f"insert ascending key {key}")
    check(tree.size == 500 and tree.has_correct_parent_references(), "ascending tree size and parent links")
    for key in range(500): check(tree.contains(key), f"contains ascending key {key}")
    for key in range(0, 500, 2):
        check(tree.remove(key), f"remove even key {key}")
        check(tree.has_correct_parent_references(), f"parent links after removing {key}")
    check(tree.size == 250 and tree.minimum() == 1, "size and minimum after even removals")
    for key in range(1, 499, 2): check(tree.successor(key) == key + 2, f"successor among odd keys {key}")
    for key in range(1, 500, 2): check(tree.remove(key), f"drain odd key {key}")
    check(tree.is_empty() and tree.has_correct_parent_references(), "empty after complete drain")


def test_large_balanced_order_workload():
    count = 20_000
    tree = BinarySearchTree()
    ranges = [(0, count - 1)]
    ok = True
    while ranges:
        low, high = ranges.pop()
        if low > high:
            continue
        middle = low + (high - low) // 2
        ok = tree.insert(middle) and ok
        if middle + 1 <= high:
            ranges.append((middle + 1, high))
        if low <= middle - 1:
            ranges.append((low, middle - 1))
    ok = all(tree.contains(key) for key in range(count)) and ok
    ok = all(tree.remove(key) for key in range(0, count, 2)) and ok
    ok = all(tree.contains(key) == (key % 2 == 1) for key in range(count)) and ok
    check(ok and tree.size == count // 2 and tree.has_correct_parent_references(),
          "20,000-key balanced-order aggregate workload")


test_binary_search_tree()
test_large_ordered_updates()
test_large_balanced_order_workload()

if failures:
    raise SystemExit(1)
