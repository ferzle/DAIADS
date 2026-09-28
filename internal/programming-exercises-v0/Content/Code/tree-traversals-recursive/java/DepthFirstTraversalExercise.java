import java.util.ArrayList;
import java.util.List;

public class DepthFirstTraversalExercise {
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

    static List<Integer> preorder(BinaryNode node) {
        List<Integer> result = new ArrayList<>();
        preorderHelper(node, result);
        return result;
    }

    
    // To visit a node, append its value to the result list:
    // result.add(node.value);

    static void preorderHelper(BinaryNode node, List<Integer> result) {
        // TODO
    }

    static List<Integer> inorder(BinaryNode node) {
        List<Integer> result = new ArrayList<>();
        inorderHelper(node, result);
        return result;
    }

    static void inorderHelper(BinaryNode node, List<Integer> result) {
        // TODO
    }

    static List<Integer> postorder(BinaryNode node) {
        List<Integer> result = new ArrayList<>();
        postorderHelper(node, result);
        return result;
    }

    static void postorderHelper(BinaryNode node, List<Integer> result) {
        // TODO
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

    static void testTraversals() {
        BinaryNode empty = null;

        check("preorder empty", preorder(empty), List.of());
        check("inorder empty", inorder(empty), List.of());
        check("postorder empty", postorder(empty), List.of());

        BinaryNode single = new BinaryNode(11);

        check("preorder single", preorder(single), List.of(11));
        check("inorder single", inorder(single), List.of(11));
        check("postorder single", postorder(single), List.of(11));

        BinaryNode complete = buildCompleteTree();

        check(
            "preorder complete",
            preorder(complete),
            List.of(1, 2, 4, 5, 3, 6)
        );
        check(
            "inorder complete",
            inorder(complete),
            List.of(4, 2, 5, 1, 6, 3)
        );
        check(
            "postorder complete",
            postorder(complete),
            List.of(4, 5, 2, 6, 3, 1)
        );

        check(
            "preorder left subtree",
            preorder(complete.left),
            List.of(2, 4, 5)
        );
        check(
            "inorder left subtree",
            inorder(complete.left),
            List.of(4, 2, 5)
        );
        check(
            "postorder left subtree",
            postorder(complete.left),
            List.of(4, 5, 2)
        );

        BinaryNode degenerate = buildDegenerateTree();

        check(
            "preorder degenerate",
            preorder(degenerate),
            List.of(7, 8, 9, 10)
        );
        check(
            "inorder degenerate",
            inorder(degenerate),
            List.of(7, 8, 9, 10)
        );
        check(
            "postorder degenerate",
            postorder(degenerate),
            List.of(10, 9, 8, 7)
        );
    }

    public static void main(String[] args) {
        testTraversals();
    }
}
