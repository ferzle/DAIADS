import java.util.Arrays;

public class UnsortedArraySetExercise {
    static final class UnsortedArrayIntSet {
        private int[] keys;
        private int count;

        UnsortedArrayIntSet(int initialCapacity) {
            if (initialCapacity < 1) {
                throw new IllegalArgumentException("initialCapacity must be positive");
            }
            keys = new int[initialCapacity];
            count = 0;
        }

        boolean isEmpty() {
            // TODO: Return whether the used portion of the array is empty.
            return false;
        }

        int size() {
            // TODO: Return the number of distinct stored keys, not the capacity.
            return -1;
        }

        void clear() {
            // TODO: Remove all logical contents. The allocated array may be reused.
        }

        boolean contains(int key) {
            // TODO: Search keys[0..count) sequentially.
            return false;
        }

        boolean add(int key) {
            // TODO:
            // 1. Return false without changing the set if key is already present.
            // 2. Grow the array when count equals keys.length.
            // 3. Append key to the used portion, increment count, and return true.
            return false;
        }

        boolean remove(int key) {
            // TODO:
            // 1. Find key in keys[0..count). Return false if it is absent.
            // 2. Fill its gap with the final used key; do not shift a suffix.
            // 3. Decrement count exactly once and return true.
            return false;
        }

        int[] toArray() {
            // This snapshot exposes the current iteration order for testing.
            return Arrays.copyOf(keys, count);
        }

        private void ensureCapacity() {
            // TODO: If the array is full, replace it with an array having twice
            // the capacity and copy all count used keys into the new array.
        }
    }

    private static int failures = 0;

    private static void check(boolean actual, boolean expected, String label) {
        if (actual == expected) {
            System.out.println("pass: " + label);
        } else {
            failures++;
            System.out.println("FAIL: " + label + " (expected " + expected
                    + ", got " + actual + ")");
        }
    }

    private static void check(int actual, int expected, String label) {
        if (actual == expected) {
            System.out.println("pass: " + label);
        } else {
            failures++;
            System.out.println("FAIL: " + label + " (expected " + expected
                    + ", got " + actual + ")");
        }
    }

    private static void checkArray(int[] actual, int[] expected, String label) {
        if (Arrays.equals(actual, expected)) {
            System.out.println("pass: " + label);
        } else {
            failures++;
            System.out.println("FAIL: " + label + " (expected "
                    + Arrays.toString(expected) + ", got "
                    + Arrays.toString(actual) + ")");
        }
    }

    private static void testSet() {
        UnsortedArrayIntSet set = new UnsortedArrayIntSet(2);

        check(set.isEmpty(), true, "new set is empty");
        check(set.size(), 0, "new set has size zero");
        check(set.contains(4), false, "missing key is not contained");
        check(set.remove(4), false, "removing a missing key changes nothing");

        check(set.add(8), true, "add first key");
        check(set.add(3), true, "add second key");
        check(set.add(8), false, "reject duplicate key");
        check(set.size(), 2, "duplicate does not change size");
        checkArray(set.toArray(), new int[] {8, 3}, "keys append while space remains");

        check(set.add(11), true, "add grows the backing array");
        check(set.add(-2), true, "negative keys are supported");
        check(set.contains(11), true, "contains finds a stored key");
        check(set.size(), 4, "size after resizing");

        check(set.remove(3), true, "remove an interior key");
        checkArray(set.toArray(), new int[] {8, -2, 11},
                "interior gap is filled with the final key");
        check(set.remove(11), true, "remove the final used key");
        check(set.contains(11), false, "removed key is absent");
        check(set.remove(11), false, "cannot remove a key twice");

        set.clear();
        check(set.isEmpty(), true, "clear empties the set");
        check(set.size(), 0, "size is zero after clear");
        check(set.add(5), true, "set can be reused after clear");
    }

    public static void main(String[] args) {
        testSet();
        System.out.println(failures == 0
                ? "All tests passed."
                : failures + " test(s) failed.");
    }
}
