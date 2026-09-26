public class BinarySearchTreeExercise {
    static int failures = 0;
  private static class Node {
      int key;
      Node left;
      Node right;
      Node parent;

      Node(int key) {
          this.key = key;
          this.left = null;
          this.right = null;
          this.parent = null;
      }
  }

  private Node root;
  private int size;

  public BinarySearchTreeExercise() {
      root = null;
      size = 0;
  }

  public int size() {
      return size;
  }

  public boolean isEmpty() {
      return size == 0;
  }

  private Node searchNode(int key) {
      Node current = root;
      while (current != null) {
          if (key == current.key) {
              return current;
          }
          current = key < current.key ? current.left : current.right;
      }
      return null;
  }

  public boolean contains(int key) {
      return searchNode(key) != null;
  }

  public boolean insert(int key) {
      Node parent = null;
      Node current = root;
      while (current != null) {
          parent = current;
          if (key == current.key) {
              return false;
          }
          current = key < current.key ? current.left : current.right;
      }

      Node node = new Node(key);
      node.parent = parent;
      if (parent == null) {
          root = node;
      } else if (key < parent.key) {
          parent.left = node;
      } else {
          parent.right = node;
      }
      size++;
      return true;
  }

  private Node minimumNode(Node node) {
      if (node == null) {
          return null;
      }
      while (node.left != null) {
          node = node.left;
      }
      return node;
  }

  public Integer minimum() {
      Node node = minimumNode(root);

      if (node == null) {
          return null;
      }

      return node.key;
  }

  private Node successorNode(Node node) {
      if (node.right != null) {
          return minimumNode(node.right);
      }
      Node parent = node.parent;
      while (parent != null && node == parent.right) {
          node = parent;
          parent = parent.parent;
      }
      return parent;
  }

  public Integer successor(int key) {
      Node node = searchNode(key);

      if (node == null) {
          return null;
      }

      Node result = successorNode(node);

      if (result == null) {
          return null;
      }

      return result.key;
  }

  private void replaceSubtree(Node oldRoot, Node newRoot) {
      if (oldRoot.parent == null) {
          root = newRoot;
      } else if (oldRoot == oldRoot.parent.left) {
          oldRoot.parent.left = newRoot;
      } else {
          oldRoot.parent.right = newRoot;
      }
      if (newRoot != null) {
          newRoot.parent = oldRoot.parent;
      }
  }

  public boolean remove(int key) {
      Node node = searchNode(key);
      if (node == null) {
          return false;
      }

      if (node.left == null) {
          replaceSubtree(node, node.right);
      } else if (node.right == null) {
          replaceSubtree(node, node.left);
      } else {
          Node successor = minimumNode(node.right);
          if (successor.parent != node) {
              replaceSubtree(successor, successor.right);
              successor.right = node.right;
              successor.right.parent = successor;
          }
          replaceSubtree(node, successor);
          successor.left = node.left;
          successor.left.parent = successor;
      }
      size--;
      return true;
  }

  //--------------------------------------------------
  // The rest of the methods are helpers for the tests
  //--------------------------------------------------

  public String inorderString() {
      StringBuilder result = new StringBuilder();
      appendInorder(root, result);
      return result.toString().trim();
  }

  private void appendInorder(Node node, StringBuilder result) {
      if (node == null) {
          return;
      }

      appendInorder(node.left, result);
      result.append(node.key).append(" ");
      appendInorder(node.right, result);
  }

  public boolean hasCorrectParentReferences() {
      if (root != null && root.parent != null) {
          return false;
      }

      return hasCorrectParentReferences(root);
  }

  public Integer rootKeyForTesting() {
      return root == null ? null : root.key;
  }

  private boolean hasCorrectParentReferences(Node node) {
      if (node == null) {
          return true;
      }

      if (node.left != null && node.left.parent != node) {
          return false;
      }

      if (node.right != null && node.right.parent != node) {
          return false;
      }

      return hasCorrectParentReferences(node.left)
          && hasCorrectParentReferences(node.right);
  }

  private static void check(boolean condition, String description) {
      if (condition) {
          System.out.println("pass: " + description);
      } else {
          failures++;
          System.out.println("fail: " + description);
      }
  }

  private static void testBinarySearchTree() {
      BinarySearchTreeExercise tree = new BinarySearchTreeExercise();

      check(tree.isEmpty(), "a new tree is empty");
      check(tree.size() == 0, "a new tree has size 0");
      check(tree.minimum() == null, "an empty tree has no minimum");
      check(!tree.contains(50), "an empty tree does not contain 50");
      check(!tree.remove(50), "an absent key cannot be removed");

      int[] keys = {50, 30, 70, 20, 40, 60, 80, 55, 65, 57};

      for (int key : keys) {
          check(tree.insert(key), "insert " + key);
          check(
              tree.hasCorrectParentReferences(),
              "parent references after inserting " + key
          );
      }

      check(tree.size() == 10, "the tree has size 10");
      check(!tree.isEmpty(), "the tree is not empty");
      check(tree.contains(57), "the tree contains 57");
      check(!tree.contains(58), "the tree does not contain 58");

      check(tree.inorderString().equals("20 30 40 50 55 57 60 65 70 80"),
          "inorder traversal after insertion");
      check(Integer.valueOf(50).equals(tree.rootKeyForTesting()),
          "50 remains the root after insertion");

      check(!tree.insert(60), "duplicate insertion fails");
      check(tree.size() == 10, "duplicate insertion does not change size");

      check(Integer.valueOf(20).equals(tree.minimum()),
          "the minimum is 20");
      check(Integer.valueOf(50).equals(tree.successor(40)),
          "the successor of 40 is 50");
      check(Integer.valueOf(57).equals(tree.successor(55)),
          "the successor of 55 is 57");
      check(Integer.valueOf(70).equals(tree.successor(65)),
          "the successor of 65 is 70");
      check(tree.successor(80) == null, "80 has no successor");
      check(tree.successor(58) == null, "an absent key has no successor");

      check(tree.remove(20), "remove the leaf 20");
      check(tree.inorderString().equals("30 40 50 55 57 60 65 70 80"),
          "inorder traversal after removing 20");
      check(tree.hasCorrectParentReferences(),
          "parent references after removing 20");

      check(tree.remove(55), "remove the one-child node 55");
      check(tree.inorderString().equals("30 40 50 57 60 65 70 80"),
          "inorder traversal after removing 55");
      check(tree.hasCorrectParentReferences(),
          "parent references after removing 55");

      check(tree.remove(50), "remove the two-child root 50");
      check(tree.inorderString().equals("30 40 57 60 65 70 80"),
          "inorder traversal after removing 50");
      check(tree.hasCorrectParentReferences(),
          "parent references after removing 50");
      check(Integer.valueOf(57).equals(tree.rootKeyForTesting()),
          "successor substitution changes the root key to 57");

      check(tree.size() == 7, "the final size is 7");
      check(!tree.remove(50), "removing 50 again fails");
      check(tree.size() == 7, "an unsuccessful removal does not change size");

      BinarySearchTreeExercise oneNodeTree =
          new BinarySearchTreeExercise();

      check(oneNodeTree.insert(10), "insert into an empty tree");
      check(oneNodeTree.remove(10), "remove the only node");
      check(oneNodeTree.isEmpty(), "the one-node tree becomes empty");
      check(oneNodeTree.rootKeyForTesting() == null,
          "the drained one-node tree has no root");
      check(oneNodeTree.hasCorrectParentReferences(),
          "the empty tree has valid parent references");
  }

  private static void testLargeOrderedUpdates() {
    BinarySearchTreeExercise tree = new BinarySearchTreeExercise();
    for (int key = 0; key < 500; key++) check(tree.insert(key), "insert ascending key " + key);
    check(tree.size() == 500 && tree.hasCorrectParentReferences(), "ascending tree size and parent links");
    for (int key = 0; key < 500; key++) check(tree.contains(key), "contains ascending key " + key);
    for (int key = 0; key < 500; key += 2) { check(tree.remove(key), "remove even key " + key); check(tree.hasCorrectParentReferences(), "parent links after removing " + key); }
    check(tree.size() == 250 && tree.minimum() == 1, "size and minimum after even removals");
    for (int key = 1; key < 499; key += 2) check(tree.successor(key) == key + 2, "successor among odd keys " + key);
    for (int key = 1; key < 500; key += 2) check(tree.remove(key), "drain odd key " + key);
    check(tree.isEmpty() && tree.minimum() == null && tree.hasCorrectParentReferences(), "empty after complete drain");
  }

  private static void testLargeBalancedOrderWorkload() {
    final int count = 20_000;
    BinarySearchTreeExercise tree = new BinarySearchTreeExercise();
    int[] lows = new int[count], highs = new int[count];
    int top = 0;
    lows[top] = 0; highs[top++] = count - 1;
    boolean ok = true;
    while (top > 0) {
      int low = lows[--top], high = highs[top];
      if (low > high) continue;
      int middle = low + (high - low) / 2;
      ok &= tree.insert(middle);
      if (middle + 1 <= high) { lows[top] = middle + 1; highs[top++] = high; }
      if (low <= middle - 1) { lows[top] = low; highs[top++] = middle - 1; }
    }
    for (int key = 0; key < count; key++) ok &= tree.contains(key);
    for (int key = 0; key < count; key += 2) ok &= tree.remove(key);
    for (int key = 0; key < count; key++) ok &= tree.contains(key) == (key % 2 == 1);
    check(ok && tree.size() == count / 2 && tree.hasCorrectParentReferences(),
        "20,000-key balanced-order aggregate workload");
  }

  public static void main(String[] args) {
    testBinarySearchTree();
    testLargeOrderedUpdates();
    testLargeBalancedOrderWorkload();
      System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
      if (failures > 0) System.exit(1);
  }
}
