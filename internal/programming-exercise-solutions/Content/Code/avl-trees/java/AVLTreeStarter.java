import java.util.ArrayList;
import java.util.List;

public class AVLTreeStarter {
    private static int failures = 0;
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

    // Test observers: expose stored metadata without showing how to maintain it.
    public int treeHeightForTesting() { return root == null ? -1 : root.height; }
    public Integer rootKeyForTesting() { return root == null ? null : root.key; }

    private int height(Node node) {
        // Return -1 for null and the stored height otherwise.
        return node == null ? -1 : node.height;
    }

    private void updateHeight(Node node) {
        // Set node.height from its two child heights.
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int balanceFactor(Node node) {
        // Use height(left) - height(right).
        return height(node.left) - height(node.right);
    }

    private Node searchNode(int key) {
        // Follow one ordinary BST search path.
        Node current = root;
        while (current != null) {
            if (key == current.key) return current;
            current = key < current.key ? current.left : current.right;
        }
        return null;
    }

    public boolean contains(int key) {
        return searchNode(key) != null;
    }

    private void replaceSubtree(Node oldRoot, Node newRoot) {
        // Reconnect newRoot to oldRoot.parent, including the overall-root case.
        // Update newRoot.parent when newRoot is non-null.
        if (oldRoot.parent == null) root = newRoot;
        else if (oldRoot == oldRoot.parent.left) oldRoot.parent.left = newRoot;
        else oldRoot.parent.right = newRoot;
        if (newRoot != null) newRoot.parent = oldRoot.parent;
    }

    private Node rotateLeft(Node node) {
        // Preconditions: node and node.right are non-null.
        // Reconnect all links, update the downward-moving node first,
        // and return the new root of this local subtree.
        Node pivot = node.right;
        node.right = pivot.left;
        if (pivot.left != null) pivot.left.parent = node;
        replaceSubtree(node, pivot);
        pivot.left = node;
        node.parent = pivot;
        updateHeight(node);
        updateHeight(pivot);
        return pivot;
    }

    private Node rotateRight(Node node) {
        // Symmetric to rotateLeft.
        Node pivot = node.left;
        node.left = pivot.right;
        if (pivot.right != null) pivot.right.parent = node;
        replaceSubtree(node, pivot);
        pivot.right = node;
        node.parent = pivot;
        updateHeight(node);
        updateHeight(pivot);
        return pivot;
    }

    private Node rebalance(Node node) {
        // Update node.height, use the numeric balance-factor pairs,
        // perform zero, one, or two rotations, and return the local root.
        updateHeight(node);
        int balance = balanceFactor(node);
        if (balance > 1) {
            if (balanceFactor(node.left) < 0) rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1) {
            if (balanceFactor(node.right) > 0) rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    private Node bstInsert(int key) {
        // Perform only ordinary BST placement; do not change size.
        // Return the new height-0 leaf, or null if key is already present.
        Node parent = null;
        Node current = root;
        while (current != null) {
            parent = current;
            if (key == current.key) return null;
            current = key < current.key ? current.left : current.right;
        }
        Node leaf = new Node(key, parent);
        if (parent == null) root = leaf;
        else if (key < parent.key) parent.left = leaf;
        else parent.right = leaf;
        return leaf;
    }

    private void fixAfterInsert(Node newLeaf) {
        // Walk upward from newLeaf.parent. Stop at the root or immediately
        // after the first single/double repair.
        Node node = newLeaf.parent;
        while (node != null) {
            Node original = node;
            Node localRoot = rebalance(node);
            if (localRoot != original) return;
            node = localRoot.parent;
        }
    }

    public boolean insert(int key) {
        // Call bstInsert. If it succeeds, increment size exactly once,
        // then call fixAfterInsert on the returned leaf.
        Node leaf = bstInsert(key);
        if (leaf == null) return false;
        size++;
        fixAfterInsert(leaf);
        return true;
    }

    private Node minimumNode(Node node) {
        while (node != null && node.left != null) {
            node = node.left;
        }
        return node;
    }

    private Node bstRemove(Node target) {
        // target is known to be in the tree. Perform ordinary BST removal,
        // using successor-key substitution for a two-child node. Return the
        // lowest node still in the tree whose height may have changed.
        // This may be null after successfully removing the root.
        if (target.left != null && target.right != null) {
            Node successor = minimumNode(target.right);
            target.key = successor.key;
            target = successor;
        }
        Node child = target.left != null ? target.left : target.right;
        Node parent = target.parent;
        replaceSubtree(target, child);
        return parent != null ? parent : child;
    }

    private void fixAfterRemove(Node node) {
        // Rebalance upward while the repaired subtree's height decreases.
        // Stop when its height is unchanged; after a rotation that does not
        // stop the walk, continue at the repaired local root's parent.
        while (node != null) {
            int oldHeight = node.height;
            Node localRoot = rebalance(node);
            if (localRoot.height == oldHeight) return;
            node = localRoot.parent;
        }
    }

    public boolean remove(int key) {
        // Find the target first. If present, pass it to bstRemove, decrement
        // size exactly once, and repair from the returned node.
        Node target = searchNode(key);
        if (target == null) return false;
        Node repairStart = bstRemove(target);
        size--;
        fixAfterRemove(repairStart);
        return true;
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
        if (!condition) failures++;
        System.out.println((condition ? "pass: " : "fail: ") + description);
    }

    private static void testDifferentialUpdates() {
        AVLTreeStarter tree = new AVLTreeStarter();
        java.util.TreeSet<Integer> expected = new java.util.TreeSet<>();
        long state = 0x5EEDL;
        for (int operation = 0; operation < 2000; operation++) {
            state = (state * 1103515245 + 12345) & 0x7fffffffL;
            int key = (int) (state % 1000);
            boolean actual = operation % 3 == 0 ? tree.remove(key) : tree.insert(key);
            boolean oracle = operation % 3 == 0 ? expected.remove(key) : expected.add(key);
            check(actual == oracle, "operation result " + operation);
            check(tree.size() == expected.size(), "size after operation " + operation);
            check(tree.inorderValues().equals(new java.util.ArrayList<>(expected)), "contents after operation " + operation);
            check(tree.hasValidStructure(), "AVL structure after operation " + operation);
        }
        for (int key : new java.util.ArrayList<>(expected)) check(tree.remove(key), "final drain key " + key);
        check(tree.isEmpty() && tree.hasValidStructure(), "valid empty tree after complete drain");
    }

    public static void main(String[] args) {
        AVLTreeStarter tree = new AVLTreeStarter();
        check(tree.isEmpty(), "a new tree is empty");
        check(tree.size() == 0, "a new tree has size zero");
        check(tree.treeHeightForTesting() == -1, "an empty tree has height -1");
        check(tree.rootKeyForTesting() == null, "an empty tree has no root key");
        check(!tree.contains(99), "contains reports an absent key");
        check(!tree.remove(99), "removing an absent key returns false");
        int[] keys = {30, 20, 10, 40, 50, 25, 27};
        for (int key : keys) {
            check(tree.insert(key), "insert " + key);
            check(tree.hasValidStructure(), "invariants after inserting " + key);
        }
        check(!tree.insert(25), "duplicate insertion leaves the set unchanged");
        check(tree.size() == keys.length, "size counts distinct insertions");
        check(tree.contains(27), "contains finds a stored key");
        for (int key : new int[]{40, 30, 10}) {
            check(tree.remove(key), "remove " + key);
            check(tree.hasValidStructure(), "invariants after removing " + key);
        }

        int[][] rotationPatterns = {
            {30, 20, 10}, {10, 20, 30}, {30, 10, 20}, {10, 30, 20}
        };
        for (int i = 0; i < rotationPatterns.length; i++) {
            AVLTreeStarter rotationTree = new AVLTreeStarter();
            boolean ok = true;
            for (int key : rotationPatterns[i]) ok &= rotationTree.insert(key);
            check(ok && rotationTree.size() == 3 && rotationTree.hasValidStructure(),
                "rotation pattern " + (i + 1));
            check(Integer.valueOf(20).equals(rotationTree.rootKeyForTesting())
                    && rotationTree.treeHeightForTesting() == 1,
                "rotation pattern " + (i + 1) + " has root 20 and height 1");
        }

        int[][] deletionPatterns = {
            {30, 20, 40, 10, 25}, {20, 10, 30, 25, 40},
            {30, 10, 40, 20}, {20, 10, 40, 30}
        };
        int[] removedKeys = {40, 10, 40, 10};
        int[] expectedRoots = {20, 30, 20, 30};
        int[] expectedHeights = {2, 2, 1, 1};
        for (int i = 0; i < deletionPatterns.length; i++) {
            AVLTreeStarter deletionTree = new AVLTreeStarter();
            boolean ok = true;
            for (int key : deletionPatterns[i]) ok &= deletionTree.insert(key);
            ok &= deletionTree.remove(removedKeys[i]);
            check(ok && Integer.valueOf(expectedRoots[i]).equals(deletionTree.rootKeyForTesting())
                    && deletionTree.treeHeightForTesting() == expectedHeights[i]
                    && deletionTree.hasValidStructure(),
                "deletion rotation pattern " + (i + 1));
        }

        AVLTreeStarter large = new AVLTreeStarter();
        boolean largeOk = true;
        final int count = 20_000;
        for (int i = 0; i < count; i++) largeOk &= large.insert((i * 7_919) % count);
        for (int i = 0; i < count; i++) largeOk &= large.contains(i);
        largeOk &= large.size() == count && large.hasValidStructure();
        largeOk &= large.treeHeightForTesting() <= 20;
        for (int i = 0; i < count; i += 2) largeOk &= large.remove(i);
        for (int i = 0; i < count; i++) largeOk &= large.contains(i) == (i % 2 == 1);
        largeOk &= large.size() == count / 2 && large.hasValidStructure();
        check(largeOk, "20,000-key insertion, search, and 10,000-key removal stress test");
        testDifferentialUpdates();
        System.out.println("inorder: " + tree.inorderValues());
        if (failures > 0) System.exit(1);
    }
}
