import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 * Starter implementation for an ordered set backed by a 2-3 tree.
 *
 * Complete the methods marked TODO. During insertion repair, a node may
 * temporarily contain three keys and four children. During removal repair,
 * a nonroot node may temporarily contain zero keys and one child.
 */
public class TwoThreeTreeStarter {
    private static class Node {
        final List<Integer> keys = new ArrayList<>();
        final List<Node> children = new ArrayList<>();
        Node parent;

        Node() {}

        Node(int key) {
            keys.add(key);
        }

        boolean isLeaf() {
            return children.isEmpty();
        }
    }

    private static class Location {
        final Node node;
        final int keyIndex;

        Location(Node node, int keyIndex) {
            this.node = node;
            this.keyIndex = keyIndex;
        }
    }

    private Node root;
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Return the first index i for which keys[i] >= key. */
    private int findPosition(Node node, int key) {
        int i = 0;
        while (i < node.keys.size() && node.keys.get(i) < key) {
            i++;
        }
        return i;
    }

    /** Return the node and key position containing key, or null if absent. */
    private Location findLocation(int key) {
        // TODO: Follow exactly one child range at each internal node.
        return null;
    }

    public boolean contains(int key) {
        return findLocation(key) != null;
    }

    /**
     * Insert a distinct key.
     * @return true if a key was added; false for a duplicate
     */
    public boolean insert(int key) {
        // TODO:
        // 1. Search to a leaf and reject a duplicate.
        // 2. Insert the key into the leaf in sorted order.
        // 3. Split every temporary three-key node and promote its median.
        // 4. Create a new root if the old root splits.
        // 5. Update size exactly once.
        return false;
    }

    /**
     * Split an overflowing three-key node and promote its median.
     * Remember to distribute four children as [T0,T1] and [T2,T3]
     * and update every moved child's parent reference.
     */
    private void splitOverflow(Node node) {
        // TODO
    }

    public Integer minimum() {
        // TODO: Follow first-child references to the leftmost leaf.
        return null;
    }

    public Integer maximum() {
        // TODO: Follow last-child references to the rightmost leaf.
        return null;
    }

    /**
     * Remove key if present.
     * @return true if a key was removed; false if key was absent
     */
    public boolean remove(int key) {
        // TODO:
        // 1. Find the key.
        // 2. If internal, replace it with its predecessor (or successor).
        // 3. Remove from the leaf.
        // 4. Repair an underflow by borrowing or merging upward.
        // 5. Replace an empty root by its only child when necessary.
        // 6. Update size exactly once.
        return false;
    }

    /** Repair a zero-key nonroot node, possibly continuing at its parent. */
    private void repairUnderflow(Node node) {
        // TODO
    }

    private void borrowFromLeft(Node parent, int childIndex) {
        // TODO: Rotate a parent separator down and the sibling maximum up.
    }

    private void borrowFromRight(Node parent, int childIndex) {
        // TODO: Rotate a parent separator down and the sibling minimum up.
    }

    /** Merge the underfull child with its left sibling and a parent key. */
    private Node mergeWithLeft(Node parent, int childIndex) {
        // TODO: Return the parent, which may now be underfull.
        return parent;
    }

    /** Merge the underfull child with its right sibling and a parent key. */
    private Node mergeWithRight(Node parent, int childIndex) {
        // TODO: Return the parent, which may now be underfull.
        return parent;
    }

    // -----------------------------------------------------------------
    // Completed traversal and validation helpers. Do not remove these;
    // the tests use them to detect structural errors after every update.
    // -----------------------------------------------------------------

    public List<Integer> inorderValues() {
        List<Integer> result = new ArrayList<>();
        appendInorder(root, result);
        return result;
    }

    private void appendInorder(Node node, List<Integer> result) {
        if (node == null) return;

        for (int i = 0; i < node.keys.size(); i++) {
            if (!node.isLeaf()) appendInorder(node.children.get(i), result);
            result.add(node.keys.get(i));
        }
        if (!node.isLeaf()) {
            appendInorder(node.children.get(node.keys.size()), result);
        }
    }

    public int height() {
        int height = -1;
        Node node = root;
        while (node != null) {
            height++;
            node = node.isLeaf() ? null : node.children.get(0);
        }
        return height;
    }

    public boolean hasValidStructure() {
        if (root == null) return size == 0;
        if (root.parent != null) return false;

        int[] leafDepth = {-1};
        int[] keyCount = {0};
        boolean valid = validateNode(
            root, Long.MIN_VALUE, Long.MAX_VALUE, 0, leafDepth, keyCount
        );
        return valid && keyCount[0] == size;
    }

    private boolean validateNode(
        Node node,
        long lower,
        long upper,
        int depth,
        int[] leafDepth,
        int[] keyCount
    ) {
        if (node.keys.size() < 1 || node.keys.size() > 2) return false;
        if (node.keys.size() == 2 && node.keys.get(0) >= node.keys.get(1)) {
            return false;
        }
        for (int key : node.keys) {
            if (key <= lower || key >= upper) return false;
        }
        keyCount[0] += node.keys.size();

        if (node.isLeaf()) {
            if (leafDepth[0] == -1) leafDepth[0] = depth;
            return leafDepth[0] == depth;
        }
        if (node.children.size() != node.keys.size() + 1) return false;

        for (int i = 0; i < node.children.size(); i++) {
            Node child = node.children.get(i);
            if (child == null || child.parent != node) return false;
            long childLower = i == 0 ? lower : node.keys.get(i - 1);
            long childUpper = i == node.keys.size() ? upper : node.keys.get(i);
            if (!validateNode(
                child, childLower, childUpper, depth + 1, leafDepth, keyCount
            )) return false;
        }
        return true;
    }

    // ---------------------------- Tests ----------------------------

    private static void check(boolean condition, String description) {
        System.out.println((condition ? "pass: " : "FAIL: ") + description);
    }

    private static void checkContents(
        TwoThreeTreeStarter tree,
        TreeSet<Integer> expected,
        String description
    ) {
        check(tree.hasValidStructure(), description + " -- invariants");
        check(tree.size() == expected.size(), description + " -- size");
        check(
            tree.inorderValues().equals(new ArrayList<>(expected)),
            description + " -- inorder contents"
        );
    }

    private static boolean contentsMatch(
        TwoThreeTreeStarter tree,
        TreeSet<Integer> expected
    ) {
        return tree.hasValidStructure()
            && tree.size() == expected.size()
            && tree.inorderValues().equals(new ArrayList<>(expected));
    }

    private static void runTests() {
        TwoThreeTreeStarter tree = new TwoThreeTreeStarter();
        TreeSet<Integer> expected = new TreeSet<>();

        check(tree.isEmpty(), "new tree is empty");
        check(tree.size() == 0, "new tree has size zero");
        check(tree.minimum() == null, "empty tree has no minimum");
        check(tree.maximum() == null, "empty tree has no maximum");
        check(!tree.contains(10), "empty tree does not contain 10");
        check(!tree.remove(10), "cannot remove an absent key");

        int[] values = {
            40, 20, 60, 10, 30, 50, 70, 5, 15, 25,
            35, 45, 55, 65, 75, 1, 8, 12, 18
        };
        for (int value : values) {
            if (!tree.insert(value)) {
                check(false, "insert " + value);
                System.out.println("Complete insertion before running later tests.");
                return;
            }
            check(true, "insert " + value);
            expected.add(value);
            checkContents(tree, expected, "after inserting " + value);
        }

        check(!tree.insert(30), "duplicate insertion returns false");
        checkContents(tree, expected, "after duplicate insertion");
        check(Integer.valueOf(1).equals(tree.minimum()), "minimum is 1");
        check(Integer.valueOf(75).equals(tree.maximum()), "maximum is 75");

        int[] removals = {1, 8, 10, 20, 60, 75, 70, 65, 40, 5, 12};
        for (int value : removals) {
            if (!tree.remove(value)) {
                check(false, "remove " + value);
                System.out.println("Complete removal before running randomized tests.");
                return;
            }
            check(true, "remove " + value);
            expected.remove(value);
            checkContents(tree, expected, "after removing " + value);
        }
        check(!tree.remove(999), "absent removal returns false");
        checkContents(tree, expected, "after absent removal");

        // Sorted insertion forces repeated cascading splits.
        TwoThreeTreeStarter sortedTree = new TwoThreeTreeStarter();
        for (int value = 1; value <= 31; value++) {
            if (!sortedTree.insert(value) || !sortedTree.hasValidStructure()) {
                check(false, "sorted insertion through " + value);
                return;
            }
        }
        check(true, "sorted insertion through 31");

        // Deterministic randomized differential test.
        TwoThreeTreeStarter randomTree = new TwoThreeTreeStarter();
        TreeSet<Integer> oracle = new TreeSet<>();
        Random random = new Random(23023);
        for (int step = 0; step < 500; step++) {
            int value = random.nextInt(101) - 50;
            boolean doInsert = random.nextBoolean();
            boolean expectedResult = doInsert
                ? oracle.add(value)
                : oracle.remove(value);
            boolean actualResult = doInsert
                ? randomTree.insert(value)
                : randomTree.remove(value);
            if (actualResult != expectedResult || !contentsMatch(randomTree, oracle)) {
                check(false, "randomized differential test at update " + step);
                return;
            }
        }
        check(true, "500 randomized differential updates");

        // Remove everything to exercise cascading merges and root shrinkage.
        for (int value : new ArrayList<>(oracle)) {
            if (!randomTree.remove(value)) {
                check(false, "remove all remaining keys at " + value);
                return;
            }
            oracle.remove(value);
            if (!contentsMatch(randomTree, oracle)) {
                check(false, "invariants while removing all remaining keys");
                return;
            }
        }
        check(true, "remove all remaining keys");
        check(randomTree.isEmpty(), "tree is empty after removing every key");
        check(randomTree.height() == -1, "empty tree has height -1");
    }

    public static void main(String[] args) {
        runTests();
    }
}
