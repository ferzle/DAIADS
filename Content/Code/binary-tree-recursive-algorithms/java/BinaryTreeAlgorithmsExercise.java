public class BinaryTreeAlgorithmsExercise {
  static class BinaryNode {
      int value;
      BinaryNode left;
      BinaryNode right;

      BinaryNode(int value) {
          this.value = value;
          this.left = null;
          this.right = null;
      }
  }

  static int size(BinaryNode node) {
      // TODO
      return -1;
  }

  static int height(BinaryNode node) {
      // TODO
      return 0;
  }

  static int countLeaves(BinaryNode node) {
      // TODO
      return -1;
  }

  static int countTwoChildNodes(BinaryNode node) {
      // TODO
      return -1;
  }

  static String checkLocation() {
      StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
      return caller.getMethodName() + "(), line " + caller.getLineNumber();
  }

  static void check(int actual, int expected) {
      if (actual == expected) {
          System.out.println("pass");
      } else {
          System.out.println(
              "fail at " + checkLocation() + ": expected " + expected + " but got " + actual
          );
      }
  }

  static BinaryNode buildCompleteTree() {
      BinaryNode root = new BinaryNode(1);

      root.left = new BinaryNode(2);
      root.right = new BinaryNode(3);

      root.left.left = new BinaryNode(4);
      root.left.right = new BinaryNode(5);

      root.right.left = new BinaryNode(6);

      return root;
  }

  static BinaryNode buildDegenerateTree() {
      BinaryNode root = new BinaryNode(7);

      root.right = new BinaryNode(8);
      root.right.right = new BinaryNode(9);
      root.right.right.right = new BinaryNode(10);

      return root;
  }

  static void testAlgorithms() {
      BinaryNode empty = null;

      check(size(empty), 0);
      check(height(empty), -1);
      check(countLeaves(empty), 0);
      check(countTwoChildNodes(empty), 0);

      BinaryNode single = new BinaryNode(11);

      check(size(single), 1);
      check(height(single), 0);
      check(countLeaves(single), 1);
      check(countTwoChildNodes(single), 0);

      BinaryNode complete = buildCompleteTree();

      check(size(complete), 6);
      check(height(complete), 2);
      check(countLeaves(complete), 3);
      check(countTwoChildNodes(complete), 2);

      check(size(complete.left), 3);
      check(height(complete.left), 1);
      check(countLeaves(complete.left), 2);
      check(countTwoChildNodes(complete.left), 1);

      BinaryNode degenerate = buildDegenerateTree();

      check(size(degenerate), 4);
      check(height(degenerate), 3);
      check(countLeaves(degenerate), 1);
      check(countTwoChildNodes(degenerate), 0);
  }

  public static void main(String[] args) {
      testAlgorithms();
  }

}
