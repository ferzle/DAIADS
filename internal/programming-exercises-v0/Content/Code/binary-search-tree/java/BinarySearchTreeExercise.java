public class BinarySearchTreeExercise {
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
      // TODO
      return null;
  }

  public boolean contains(int key) {
      return searchNode(key) != null;
  }

  public boolean insert(int key) {
      // TODO
      return false;
  }

  private Node minimumNode(Node node) {
      // TODO
      return null;
  }

  public Integer minimum() {
      Node node = minimumNode(root);

      if (node == null) {
          return null;
      }

      return node.key;
  }

  private Node successorNode(Node node) {
      // TODO
      return null;
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
      // TODO
  }

  public boolean remove(int key) {
      // TODO
      return false;
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

      check(tree.size() == 7, "the final size is 7");
      check(!tree.remove(50), "removing 50 again fails");
      check(tree.size() == 7, "an unsuccessful removal does not change size");

      BinarySearchTreeExercise oneNodeTree =
          new BinarySearchTreeExercise();

      check(oneNodeTree.insert(10), "insert into an empty tree");
      check(oneNodeTree.remove(10), "remove the only node");
      check(oneNodeTree.isEmpty(), "the one-node tree becomes empty");
      check(oneNodeTree.hasCorrectParentReferences(),
          "the empty tree has valid parent references");
  }

  public static void main(String[] args) {
      testBinarySearchTree();
  }
}
