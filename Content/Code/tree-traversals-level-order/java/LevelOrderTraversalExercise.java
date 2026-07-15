import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class LevelOrderTraversalExercise {
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

    static List<Integer> levelOrder(BinaryNode root) {
        List<Integer> result = new ArrayList<>();

        if (root == null) {
            return result;
        }

        Queue<BinaryNode> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            BinaryNode node = queue.remove();

            // To visit node:
            // result.add(node.value);

            // TODO: Visit node and add its existing children
            // to the back of the queue in left-to-right order.
        }

        return result;
    }

    static void check(String name, List<Integer> actual, List<Integer> expected) {
        if (actual.equals(expected)) {
            System.out.println("pass: " + name);
        } else {
            System.out.println(
                "fail: " + name
                + "; expected " + expected
                + " but got " + actual
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

    static void testLevelOrder() {
        BinaryNode empty = null;
        BinaryNode single = new BinaryNode(11);
        BinaryNode complete = buildCompleteTree();
        BinaryNode degenerate = buildDegenerateTree();

        check("level order empty", levelOrder(empty), List.of());
        check("level order single", levelOrder(single), List.of(11));
        check(
            "level order complete",
            levelOrder(complete),
            List.of(1, 2, 3, 4, 5, 6)
        );
        check(
            "level order left subtree",
            levelOrder(complete.left),
            List.of(2, 4, 5)
        );
        check(
            "level order degenerate",
            levelOrder(degenerate),
            List.of(7, 8, 9, 10)
        );
    }

    public static void main(String[] args) {
        testLevelOrder();
    }
}
