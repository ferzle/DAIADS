import java.util.ArrayList;
import java.util.List;

public class DepthFirstTraversalExercise {
    static int failures = 0;
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
        if (node == null) {
            return;
        }
        result.add(node.value);
        preorderHelper(node.left, result);
        preorderHelper(node.right, result);
    }

    static List<Integer> inorder(BinaryNode node) {
        List<Integer> result = new ArrayList<>();
        inorderHelper(node, result);
        return result;
    }

    static void inorderHelper(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        inorderHelper(node.left, result);
        result.add(node.value);
        inorderHelper(node.right, result);
    }

    static List<Integer> postorder(BinaryNode node) {
        List<Integer> result = new ArrayList<>();
        postorderHelper(node, result);
        return result;
    }

    static void postorderHelper(BinaryNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        postorderHelper(node.left, result);
        postorderHelper(node.right, result);
        result.add(node.value);
    }

    static void check(String name, List<Integer> actual, List<Integer> expected) {
        if (actual.equals(expected)) {
            System.out.println("pass: " + name);
        } else {
            failures++;
            System.out.println(
                "fail: " + name
                + "; expected " + expected
                + " but got " + actual
            );
        }
    }

    static void checkSummary(String name, boolean condition) {
        if (condition) System.out.println("pass: " + name);
        else { failures++; System.out.println("fail: " + name); }
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

    static void testIrregularTreeAndIndependentResults() {
        BinaryNode root = new BinaryNode(10);
        root.left = new BinaryNode(20); root.right = new BinaryNode(30);
        root.left.right = new BinaryNode(40); root.left.right.left = new BinaryNode(20);
        root.right.left = new BinaryNode(50); root.right.left.right = new BinaryNode(60);
        check("irregular preorder", preorder(root), List.of(10, 20, 40, 20, 30, 50, 60));
        check("irregular inorder", inorder(root), List.of(20, 20, 40, 10, 50, 60, 30));
        check("irregular postorder", postorder(root), List.of(20, 40, 20, 60, 50, 30, 10));
        List<Integer> first = preorder(root); first.clear();
        check("returned lists are independent", preorder(root), List.of(10, 20, 40, 20, 30, 50, 60));
        check("interior subtree", inorder(root.right), List.of(50, 60, 30));
    }

    static void testHundredThousandNodeCompleteTree() {
        final int count = 100_000;
        BinaryNode[] nodes = new BinaryNode[count];
        for (int i = 0; i < count; i++) nodes[i] = new BinaryNode(i);
        for (int i = 0; i < count; i++) {
            int left = 2 * i + 1, right = left + 1;
            if (left < count) nodes[i].left = nodes[left];
            if (right < count) nodes[i].right = nodes[right];
        }
        List<Integer> pre = preorder(nodes[0]);
        List<Integer> in = inorder(nodes[0]);
        List<Integer> post = postorder(nodes[0]);
        boolean ok = pre.size() == count && in.size() == count && post.size() == count
                && pre.get(0) == 0 && in.get(0) == 65_535 && post.get(count - 1) == 0;
        pre.sort(Integer::compareTo); in.sort(Integer::compareTo); post.sort(Integer::compareTo);
        if (ok) {
            for (int i = 0; i < count; i++) ok &= pre.get(i) == i && in.get(i) == i && post.get(i) == i;
        }
        checkSummary("100,000-node depth-first traversals", ok);
    }

    public static void main(String[] args) {
        testTraversals();
        testIrregularTreeAndIndependentResults();
        testHundredThousandNodeCompleteTree();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
