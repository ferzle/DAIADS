import java.util.Arrays;

public class SeparateChainingSetExercise {
    static final class SeparateChainingIntSet {
        private static final double MAX_LOAD_FACTOR = 0.75;

        private static final class Node {
            final int key;
            Node next;
            Node(int key) { this.key = key; }
        }

        private Node[] buckets;
        private int count;

        SeparateChainingIntSet(int initialCapacity) {
            if (initialCapacity < 1) {
                throw new IllegalArgumentException("initialCapacity must be positive");
            }
            buckets = new Node[initialCapacity];
        }

        int size() {
            // TODO: Return the number of distinct keys, not the bucket count.
            return -1;
        }

        int capacity() { return buckets.length; }

        boolean contains(int key) {
            // TODO: Reject a negative key, then search only its linked bucket.
            return false;
        }

        boolean add(int key) {
            // TODO:
            // 1. Reject a negative key and return false if it is already present.
            // 2. If the projected load factor exceeds 0.75, double the bucket
            //    array and rehash every existing key.
            // 3. Recompute key's index, append it to that bucket's tail,
            //    increment count exactly once, and return true.
            return false;
        }

        boolean remove(int key) {
            // TODO: Reject a negative key. Unlink key from its bucket if present,
            // handling the first node separately. Update count exactly once.
            return false;
        }

        private int bucketIndex(int key) { return key % buckets.length; }

        private static void checkKey(int key) {
            if (key < 0) throw new IllegalArgumentException("key must be nonnegative");
        }

        private void resize(int newCapacity) {
            // TODO: Allocate a new bucket array and move every existing node to
            // its new bucket. Preserve tail-append order and do not change count.
        }

        int[] bucketSnapshot(int index) {
            if (index < 0 || index >= buckets.length) {
                throw new IndexOutOfBoundsException("invalid bucket index");
            }
            int length = 0;
            for (Node node = buckets[index]; node != null; node = node.next) length++;
            int[] keys = new int[length];
            int i = 0;
            for (Node node = buckets[index]; node != null; node = node.next) keys[i++] = node.key;
            return keys;
        }
    }

    private static int failures;

    private static void check(boolean condition, String label) {
        if (condition) System.out.println("pass: " + label);
        else { failures++; System.out.println("FAIL: " + label); }
    }

    private static void check(int actual, int expected, String label) {
        check(actual == expected, label + " (expected " + expected + ", got " + actual + ")");
    }

    private static void checkArray(int[] actual, int[] expected, String label) {
        check(Arrays.equals(actual, expected), label + " (expected "
                + Arrays.toString(expected) + ", got " + Arrays.toString(actual) + ")");
    }

    private static void checkThrows(Runnable action, String label) {
        try { action.run(); failures++; System.out.println("FAIL: " + label); }
        catch (IllegalArgumentException expected) { System.out.println("pass: " + label); }
    }

    private static void testSet() {
        checkThrows(() -> new SeparateChainingIntSet(0), "reject nonpositive capacity");
        SeparateChainingIntSet set = new SeparateChainingIntSet(8);
        check(set.size(), 0, "new set has size zero");
        check(!set.contains(6), "lookup in an empty bucket");
        check(!set.remove(6), "remove from an empty bucket");

        check(set.add(1), "add first key");
        check(set.add(9) && set.add(17) && set.add(25), "add colliding keys");
        checkArray(set.bucketSnapshot(1), new int[] {1, 9, 17, 25},
                "colliding keys append at the tail");
        check(!set.add(17), "reject duplicate key");
        check(set.size(), 4, "duplicate does not change size");
        check(set.contains(1) && set.contains(17) && set.contains(25),
                "contains traverses a chain");

        check(set.remove(1), "remove first node");
        check(set.remove(17), "remove middle node");
        check(set.remove(25), "remove final node");
        checkArray(set.bucketSnapshot(1), new int[] {9}, "remaining chain is intact");
        check(!set.remove(17), "absent removal changes nothing");
        check(set.size(), 1, "removals update size");

        SeparateChainingIntSet growing = new SeparateChainingIntSet(4);
        check(growing.add(2) && growing.add(6) && growing.add(10),
                "fill table to load factor 0.75");
        check(growing.capacity(), 4, "capacity unchanged at threshold");
        check(!growing.add(6) && growing.capacity() == 4,
                "duplicate does not trigger resize");
        check(growing.add(14), "next distinct key triggers resize");
        check(growing.capacity(), 8, "resize doubles capacity");
        check(growing.contains(2) && growing.contains(6)
                && growing.contains(10) && growing.contains(14),
                "all keys remain findable after rehashing");
        checkArray(growing.bucketSnapshot(2), new int[] {2, 10},
                "rehashing preserves tail order in bucket 2");
        checkArray(growing.bucketSnapshot(6), new int[] {6, 14},
                "new key appends after rehashing");

        checkThrows(() -> set.contains(-1), "reject negative lookup key");
        checkThrows(() -> set.add(-1), "reject negative insertion key");
        checkThrows(() -> set.remove(-1), "reject negative removal key");
    }

    public static void main(String[] args) {
        testSet();
        System.out.println(failures == 0 ? "All tests passed."
                : failures + " test(s) failed.");
    }
}
