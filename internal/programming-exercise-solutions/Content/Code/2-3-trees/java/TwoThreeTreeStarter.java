import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.TreeSet;

/**
 * Starter implementation for an ordered set backed by a 2-3 tree.
 *
 * Completed implementation of an ordered set backed by a 2-3 tree. During
 * insertion repair, a node may
 * temporarily contain three keys and four children. During removal repair,
 * a nonroot node may temporarily contain zero keys and one child.
 */
public class TwoThreeTreeStarter {
    private static int failures = 0;
    private static class Node {
        // A valid node uses one or two keys and zero, two, or three
        // children. The extra slots hold the temporary overflow created
        // during insertion.
        final int[] keys = new int[3];
        final Node[] children = new Node[4];
        int keyCount;
        int childCount;
        Node parent;

        Node() {}

        Node(int key) {
            keys[0] = key;
            keyCount = 1;
        }

        boolean isLeaf() {
            return childCount == 0;
        }
    }

    /**
     * Describes where a search ended.
     *
     * If found is true, node.keys[keyIndex] is the requested key. If found
     * is false and node is non-null, node is the leaf where the key belongs
     * and keyIndex is its insertion position. An empty tree is represented
     * by (null, 0, false). Keeping all three pieces together lets contains,
     * insert, and remove share one root-to-leaf search.
     */
    private static class Location {
        final Node node;
        final int keyIndex;
        final boolean found;

        Location(Node node, int keyIndex, boolean found) {
            this.node = node;
            this.keyIndex = keyIndex;
            this.found = found;
        }
    }

    /**
     * Carries the two values produced by splitting an overfull node.
     * The original node becomes the left half; promotedKey moves into the
     * parent, and rightNode is attached immediately after the left half.
     */
    private static class SplitResult {
        final int promotedKey;
        final Node rightNode;

        SplitResult(int promotedKey, Node rightNode) {
            this.promotedKey = promotedKey;
            this.rightNode = rightNode;
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

    /** Return the first active index i for which keys[i] >= key. */
    private int findPosition(Node node, int key) {
        int i = 0;
        while (i < node.keyCount && node.keys[i] < key) {
            i++;
        }
        return i;
    }

    /**
     * Locate key, or the leaf position where an absent key would be inserted.
     */
    private Location locate(int key) {
        Node node = root;
        while (node != null) {
            int index = findPosition(node, key);
            if (index < node.keyCount && node.keys[index] == key) {
                return new Location(node, index, true);
            }
            if (node.isLeaf()) {
                return new Location(node, index, false);
            }
            node = node.children[index];
        }
        return new Location(null, 0, false);
    }

    public boolean contains(int key) {
        return locate(key).found;
    }

    /** Return child in parent's active child array, or -1 if inconsistent. */
    private int childIndex(Node parent, Node child) {
        for (int i = 0; i < parent.childCount; i++) {
            if (parent.children[i] == child) return i;
        }
        return -1;
    }

    /** Insert into the active key prefix; the caller preserves sorted order. */
    private void insertKeyAt(Node node, int index, int key) {
        for (int i = node.keyCount; i > index; i--) {
            node.keys[i] = node.keys[i - 1];
        }
        node.keys[index] = key;
        node.keyCount++;
    }

    /** Remove and return an active key, closing the gap in the array. */
    private int removeKeyAt(Node node, int index) {
        int removed = node.keys[index];
        for (int i = index; i < node.keyCount - 1; i++) {
            node.keys[i] = node.keys[i + 1];
        }
        node.keyCount--;
        return removed;
    }

    /**
     * Insert a child and maintain its upward parent reference.
     * A null child is permitted only as a temporary implementation detail.
     */
    private void insertChildAt(Node node, int index, Node child) {
        for (int i = node.childCount; i > index; i--) {
            node.children[i] = node.children[i - 1];
        }
        node.children[index] = child;
        node.childCount++;
        if (child != null) child.parent = node;
    }

    /**
     * Remove and return a child, closing the gap and clearing the stale slot.
     * The returned child's parent is cleared until a caller reattaches it.
     */
    private Node removeChildAt(Node node, int index) {
        Node removed = node.children[index];
        for (int i = index; i < node.childCount - 1; i++) {
            node.children[i] = node.children[i + 1];
        }
        node.childCount--;
        node.children[node.childCount] = null;
        if (removed != null) removed.parent = null;
        return removed;
    }

    /**
     * Insert a distinct key.
     * @return true if a key was added; false for a duplicate
     */
    public boolean insert(int key) {
        // Steps:
        // 1. Search to a leaf and reject a duplicate.
        // 2. Insert the key into the leaf in sorted order.
        // 3. Split every temporary three-key node and promote its median.
        // 4. Create a new root if the old root splits.
        // 5. Update size exactly once.
        Location location = locate(key);
        if (location.found) return false;
        if (location.node == null) {
            root = new Node(key);
            size++;
            return true;
        }

        Node node = location.node;
        insertKeyAt(node, location.keyIndex, key);
        while (node.keyCount == 3) {
            Node parent = node.parent;
            SplitResult split = splitOverfullNode(node);
            if (parent == null) {
                Node newRoot = new Node(split.promotedKey);
                insertChildAt(newRoot, 0, node);
                insertChildAt(newRoot, 1, split.rightNode);
                root = newRoot;
                break;
            }
            int index = childIndex(parent, node);
            promoteToParent(parent, index, split.promotedKey, split.rightNode);
            node = parent;
        }
        size++;
        return true;
    }

    /**
     * Split an overflowing three-key node and promote its median.
     * Remember to distribute four children as [T0,T1] and [T2,T3]
     * and update every moved child's parent reference.
     */
    private SplitResult splitOverfullNode(Node node) {
        // Modify node into the left half and return the promoted key
        // and newly created right half.
        int promotedKey = node.keys[1];
        Node right = new Node(node.keys[2]);
        right.parent = node.parent;
        node.keyCount = 1;
        if (node.childCount == 4) {
            right.children[0] = node.children[2];
            right.children[1] = node.children[3];
            right.childCount = 2;
            right.children[0].parent = right;
            right.children[1].parent = right;
            node.children[2] = null;
            node.children[3] = null;
            node.childCount = 2;
        }
        return new SplitResult(promotedKey, right);
    }

    /**
     * Insert the products of a child split into its existing parent.
     * The left child is already stored at leftIndex.
     */
    private void promoteToParent(
        Node parent, int leftIndex, int promotedKey, Node rightChild
    ) {
        insertKeyAt(parent, leftIndex, promotedKey);
        insertChildAt(parent, leftIndex + 1, rightChild);
    }

    /** Return the leftmost leaf and its first key position. */
    private Location minimumLocation(Node node) {
        while (!node.isLeaf()) node = node.children[0];
        return new Location(node, 0, true);
    }

    /** Return the rightmost leaf and its last key position. */
    private Location maximumLocation(Node node) {
        while (!node.isLeaf()) node = node.children[node.keyCount];
        return new Location(node, node.keyCount - 1, true);
    }

    public OptionalInt minimum() {
        if (root == null) return OptionalInt.empty();
        Location location = minimumLocation(root);
        return OptionalInt.of(location.node.keys[location.keyIndex]);
    }

    public OptionalInt maximum() {
        if (root == null) return OptionalInt.empty();
        Location location = maximumLocation(root);
        return OptionalInt.of(location.node.keys[location.keyIndex]);
    }

    public OptionalInt predecessor(int key) {
        Location location = locate(key);
        if (!location.found) return OptionalInt.empty();
        Node node = location.node;
        int index = location.keyIndex;
        if (!node.isLeaf()) {
            Location result = maximumLocation(node.children[index]);
            return OptionalInt.of(result.node.keys[result.keyIndex]);
        }
        if (index > 0) return OptionalInt.of(node.keys[index - 1]);
        while (node.parent != null) {
            Node parent = node.parent;
            int child = childIndex(parent, node);
            if (child > 0) return OptionalInt.of(parent.keys[child - 1]);
            node = parent;
        }
        return OptionalInt.empty();
    }

    public OptionalInt successor(int key) {
        Location location = locate(key);
        if (!location.found) return OptionalInt.empty();
        Node node = location.node;
        int index = location.keyIndex;
        if (!node.isLeaf()) {
            Location result = minimumLocation(node.children[index + 1]);
            return OptionalInt.of(result.node.keys[result.keyIndex]);
        }
        if (index + 1 < node.keyCount) return OptionalInt.of(node.keys[index + 1]);
        while (node.parent != null) {
            Node parent = node.parent;
            int child = childIndex(parent, node);
            if (child < parent.keyCount) return OptionalInt.of(parent.keys[child]);
            node = parent;
        }
        return OptionalInt.empty();
    }

    /** Return all keys in the inclusive interval [low, high]. */
    public List<Integer> valuesInRange(int low, int high) {
        List<Integer> result = new ArrayList<>();
        if (low <= high) appendRange(root, low, high, result);
        return result;
    }

    private void appendRange(Node node, int low, int high, List<Integer> result) {
        if (node == null) return;
        for (int i = 0; i < node.keyCount; i++) {
            if (!node.isLeaf() && low < node.keys[i]) {
                appendRange(node.children[i], low, high, result);
            }
            if (node.keys[i] > high) return;
            if (node.keys[i] >= low) result.add(node.keys[i]);
        }
        if (!node.isLeaf() && high > node.keys[node.keyCount - 1]) {
            appendRange(node.children[node.keyCount], low, high, result);
        }
    }

    /**
     * Remove key if present.
     * @return true if a key was removed; false if key was absent
     */
    public boolean remove(int key) {
        // Steps:
        // 1. Find the key.
        // 2. If internal, replace it with its predecessor (or successor).
        // 3. Remove from the leaf.
        // 4. Repair a deficiency by redistribution or merging upward.
        // 5. Replace an empty root by its only child when necessary.
        // 6. Update size exactly once.
        Location location = locate(key);
        if (!location.found) return false;
        Node node = location.node;
        int index = location.keyIndex;
        if (!node.isLeaf()) {
            Location predecessor = maximumLocation(node.children[index]);
            node.keys[index] = predecessor.node.keys[predecessor.keyIndex];
            node = predecessor.node;
            index = predecessor.keyIndex;
        }
        removeKeyAt(node, index);
        size--;
        if (node.keyCount == 0) repairDeficiency(node);
        return true;
    }

    /** Repair a zero-key nonroot node, possibly continuing at its parent. */
    private void repairDeficiency(Node node) {
        while (node != root && node.keyCount == 0) {
            Node parent = node.parent;
            int index = childIndex(parent, node);
            Node left = index > 0 ? parent.children[index - 1] : null;
            Node right = index + 1 < parent.childCount
                ? parent.children[index + 1] : null;
            if (left != null && left.keyCount == 2) {
                redistributeFromLeft(parent, index);
                return;
            }
            if (right != null && right.keyCount == 2) {
                redistributeFromRight(parent, index);
                return;
            }
            if (left != null) mergeChildren(parent, index - 1);
            else mergeChildren(parent, index);
            node = parent;
        }
        if (root != null && root.keyCount == 0) {
            root = root.childCount == 0 ? null : removeChildAt(root, 0);
            if (root != null) root.parent = null;
        }
    }

    private void redistributeFromLeft(Node parent, int childIndex) {
        Node node = parent.children[childIndex];
        Node sibling = parent.children[childIndex - 1];
        insertKeyAt(node, 0, parent.keys[childIndex - 1]);
        parent.keys[childIndex - 1] = removeKeyAt(sibling, sibling.keyCount - 1);
        if (!sibling.isLeaf()) {
            Node child = removeChildAt(sibling, sibling.childCount - 1);
            insertChildAt(node, 0, child);
        }
    }

    private void redistributeFromRight(Node parent, int childIndex) {
        Node node = parent.children[childIndex];
        Node sibling = parent.children[childIndex + 1];
        insertKeyAt(node, node.keyCount, parent.keys[childIndex]);
        parent.keys[childIndex] = removeKeyAt(sibling, 0);
        if (!sibling.isLeaf()) {
            Node child = removeChildAt(sibling, 0);
            insertChildAt(node, node.childCount, child);
        }
    }

    /**
     * Merge children[separatorIndex], the separator key, and the child to
     * its right into the left child. The parent may become deficient.
     */
    private void mergeChildren(Node parent, int separatorIndex) {
        Node left = parent.children[separatorIndex];
        Node right = parent.children[separatorIndex + 1];
        insertKeyAt(left, left.keyCount, removeKeyAt(parent, separatorIndex));
        int rightKeys = right.keyCount;
        for (int i = 0; i < rightKeys; i++) {
            insertKeyAt(left, left.keyCount, right.keys[i]);
        }
        while (right.childCount > 0) {
            insertChildAt(left, left.childCount, removeChildAt(right, 0));
        }
        removeChildAt(parent, separatorIndex + 1);
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

        for (int i = 0; i < node.keyCount; i++) {
            if (!node.isLeaf()) appendInorder(node.children[i], result);
            result.add(node.keys[i]);
        }
        if (!node.isLeaf()) {
            appendInorder(node.children[node.keyCount], result);
        }
    }

    public int height() {
        int height = -1;
        Node node = root;
        while (node != null) {
            height++;
            node = node.isLeaf() ? null : node.children[0];
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
        if (node.keyCount < 1 || node.keyCount > 2) return false;
        if (node.keyCount == 2 && node.keys[0] >= node.keys[1]) {
            return false;
        }
        for (int i = 0; i < node.keyCount; i++) {
            int key = node.keys[i];
            if (key <= lower || key >= upper) return false;
        }
        keyCount[0] += node.keyCount;

        if (node.isLeaf()) {
            if (leafDepth[0] == -1) leafDepth[0] = depth;
            return leafDepth[0] == depth;
        }
        if (node.childCount != node.keyCount + 1) return false;
        for (int i = node.childCount; i < node.children.length; i++) {
            if (node.children[i] != null) return false;
        }

        for (int i = 0; i < node.childCount; i++) {
            Node child = node.children[i];
            if (child == null || child.parent != node) return false;
            long childLower = i == 0 ? lower : node.keys[i - 1];
            long childUpper = i == node.keyCount ? upper : node.keys[i];
            if (!validateNode(
                child, childLower, childUpper, depth + 1, leafDepth, keyCount
            )) return false;
        }
        return true;
    }

    // ---------------------------- Tests ----------------------------

    private static void check(boolean condition, String description) {
        if (!condition) failures++;
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
        check(tree.minimum().isEmpty(), "empty tree has no minimum");
        check(tree.maximum().isEmpty(), "empty tree has no maximum");
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
        check(tree.minimum().orElse(Integer.MIN_VALUE) == 1, "minimum is 1");
        check(tree.maximum().orElse(Integer.MIN_VALUE) == 75, "maximum is 75");
        check(tree.predecessor(1).isEmpty(), "minimum has no predecessor");
        check(
            tree.predecessor(40).orElse(Integer.MIN_VALUE) == 35,
            "predecessor of 40 is 35"
        );
        check(
            tree.successor(40).orElse(Integer.MIN_VALUE) == 45,
            "successor of 40 is 45"
        );
        check(tree.successor(75).isEmpty(), "maximum has no successor");
        check(tree.predecessor(999).isEmpty(), "absent key has no predecessor");
        check(
            tree.valuesInRange(18, 45).equals(
                List.of(18, 20, 25, 30, 35, 40, 45)
            ),
            "inclusive range from 18 through 45"
        );
        check(tree.valuesInRange(45, 18).isEmpty(), "reversed range is empty");

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

    private static void testLargeDifferentialUpdates() {
        TwoThreeTreeStarter tree = new TwoThreeTreeStarter();
        TreeSet<Integer> expected = new TreeSet<>();
        long state = 0x5EEDL;
        for (int operation = 0; operation < 3000; operation++) {
            state = (state * 1103515245 + 12345) & 0x7fffffffL;
            int key = (int) (state % 1500);
            boolean actual = operation % 3 == 0 ? tree.remove(key) : tree.insert(key);
            boolean oracle = operation % 3 == 0 ? expected.remove(key) : expected.add(key);
            check(actual == oracle, "large operation result " + operation);
            check(tree.inorderValues().equals(new ArrayList<>(expected)), "large contents after operation " + operation);
            check(tree.hasValidStructure(), "large structure after operation " + operation);
        }
        for (int key : new ArrayList<>(expected)) check(tree.remove(key), "large final drain key " + key);
        check(tree.isEmpty() && tree.hasValidStructure(), "valid empty tree after large drain");
    }

    private static void testTwentyThousandOrderedKeys() {
        final int count = 20_000;
        TwoThreeTreeStarter tree = new TwoThreeTreeStarter();
        boolean ok = true;
        for (int key = 0; key < count; key++) ok &= tree.insert(key);
        for (int key = 0; key < count; key++) ok &= tree.contains(key);
        ok &= tree.size() == count && tree.hasValidStructure();
        for (int key = 0; key < count; key += 2) ok &= tree.remove(key);
        for (int key = 0; key < count; key++) ok &= tree.contains(key) == (key % 2 == 1);
        ok &= tree.size() == count / 2 && tree.hasValidStructure();
        check(ok, "20,000-key ordered insert/search and 10,000-key removal workload");
    }

    public static void main(String[] args) {
        runTests();
        testLargeDifferentialUpdates();
        testTwentyThousandOrderedKeys();
        if (failures > 0) System.exit(1);
    }
}
