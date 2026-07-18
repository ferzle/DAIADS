import java.util.ArrayList;
import java.util.List;

public class AVLTreeStarter {
    private static class Node {
        int key;
        Node left;
        Node right;
        Node parent;
        int height;

        Node(int key, Node parent) {
            this.key = key;
            this.parent = parent;
            this.height = 0;
        }
    }

    private Node root;
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    private int height(Node node) {
        // Return -1 for null and the stored height otherwise.
        return 0; // TODO
    }

    private void updateHeight(Node node) {
        // Set node.height from its two child heights.
        // TODO
    }

    private int balanceFactor(Node node) {
        // Use height(left) - height(right).
        return 0; // TODO
    }

    private Node searchNode(int key) {
        // Follow one ordinary BST search path.
        return null; // TODO
    }

    public boolean contains(int key) {
        return searchNode(key) != null;
    }

    private void replaceSubtree(Node oldRoot, Node newRoot) {
        // Reconnect newRoot to oldRoot.parent, including the overall-root case.
        // Update newRoot.parent when newRoot is non-null.
        // TODO
    }

    private Node rotateLeft(Node node) {
        // Preconditions: node and node.right are non-null.
        // Reconnect all links, update the downward-moving node first,
        // and return the new root of this local subtree.
        return node; // TODO
    }

    private Node rotateRight(Node node) {
        // Symmetric to rotateLeft.
        return node; // TODO
    }

    private Node rebalance(Node node) {
        // Update node.height, use the numeric balance-factor pairs,
        // perform zero, one, or two rotations, and return the local root.
        return node; // TODO
    }

    private Node bstInsert(int key) {
        // Perform only ordinary BST placement.
        // Return the new height-0 leaf, or null if key is already present.
        return null; // TODO
    }

    private void fixAfterInsert(Node newLeaf) {
        // Walk upward from newLeaf.parent. Stop at the root or immediately
        // after the first single/double repair.
        // TODO
    }

    public boolean insert(int key) {
        // Call bstInsert, update size once, and call fixAfterInsert.
        return false; // TODO
    }

    private Node minimumNode(Node node) {
        while (node != null && node.left != null) {
            node = node.left;
        }
        return node;
    }

    private static class RemovalResult {
        boolean removed;
        Node repairStart;
        RemovalResult(boolean removed, Node repairStart) {
            this.removed = removed;
            this.repairStart = repairStart;
        }
    }

    private RemovalResult bstRemove(int key) {
        // Perform ordinary BST removal, using successor substitution for a
        // two-child node. Return whether removal occurred and the first
        // ancestor whose height may have changed.
        return new RemovalResult(false, null); // TODO
    }

    private void fixAfterRemove(Node node) {
        // Rebalance every affected ancestor through the root. After a
        // rotation, continue at the repaired local root's parent.
        // TODO
    }

    public boolean remove(int key) {
        // Handle absence, call bstRemove, update size once, then repair.
        return false; // TODO
    }

    public List<Integer> inorderValues() {
        List<Integer> values = new ArrayList<>();
        appendInorder(root, values);
        return values;
    }

    private void appendInorder(Node node, List<Integer> values) {
        if (node == null) return;
        appendInorder(node.left, values);
        values.add(node.key);
        appendInorder(node.right, values);
    }

    public boolean hasValidStructure() {
        if (root != null && root.parent != null) return false;
        Validation result = validate(root, null, Long.MIN_VALUE, Long.MAX_VALUE);
        return result.valid && result.count == size;
    }

    private static class Validation {
        boolean valid;
        int height;
        int count;
        Validation(boolean valid, int height, int count) {
            this.valid = valid;
            this.height = height;
            this.count = count;
        }
    }

    private Validation validate(Node node, Node expectedParent, long low, long high) {
        if (node == null) return new Validation(true, -1, 0);
        Validation left = validate(node.left, node, low, node.key);
        Validation right = validate(node.right, node, node.key, high);
        int computedHeight = 1 + Math.max(left.height, right.height);
        boolean valid = left.valid && right.valid
            && node.parent == expectedParent
            && low < node.key && node.key < high
            && node.height == computedHeight
            && Math.abs(left.height - right.height) <= 1;
        return new Validation(valid, computedHeight, 1 + left.count + right.count);
    }

    private static void check(boolean condition, String description) {
        System.out.println((condition ? "pass: " : "fail: ") + description);
    }

    public static void main(String[] args) {
        AVLTreeStarter tree = new AVLTreeStarter();
        check(tree.isEmpty(), "a new tree is empty");
        int[] keys = {30, 20, 10, 40, 50, 25, 27};
        for (int key : keys) {
            check(tree.insert(key), "insert " + key);
            check(tree.hasValidStructure(), "invariants after inserting " + key);
        }
        check(!tree.insert(25), "duplicate insertion leaves the set unchanged");
        check(tree.contains(27), "contains finds a stored key");
        for (int key : new int[]{40, 30, 10}) {
            check(tree.remove(key), "remove " + key);
            check(tree.hasValidStructure(), "invariants after removing " + key);
        }
        System.out.println("inorder: " + tree.inorderValues());
    }
}
