import java.util.Arrays;

public class SortedArraySetExercise {
    static final class SortedArrayIntSet {
        private int[] keys;
        private int count;

        SortedArrayIntSet(int initialCapacity) {
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
            // TODO: Use binary search over keys[0..count).
            return false;
        }

        boolean add(int key) {
            // TODO:
            // 1. Use binary search to find key or the position where it belongs.
            // 2. Return false without changing the set if key is present.
            // 3. Grow if needed, shift the suffix right, insert key, and update count.
            return false;
        }

        boolean remove(int key) {
            // TODO: Use binary search to locate key. If present, shift later
            // keys left, decrement count exactly once, and return true.
            return false;
        }

        int[] toArray() {
            return Arrays.copyOf(keys, count);
        }

        private int lowerBound(int key) {
            // TODO: Return the first index in keys[0..count) whose value is
            // greater than or equal to key. Return count if no such index exists.
            return -1;
        }

        private void ensureCapacity() {
            // TODO: When full, double the array capacity and copy all used keys.
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
        SortedArrayIntSet set = new SortedArrayIntSet(2);
        check(set.isEmpty(), true, "new set is empty");
        check(set.size(), 0, "new set has size zero");
        check(set.contains(4), false, "missing key is not contained");
        check(set.remove(4), false, "removing a missing key changes nothing");

        check(set.add(8), true, "add first key");
        check(set.add(3), true, "insert before a larger key");
        check(set.add(11), true, "append and grow the backing array");
        check(set.add(6), true, "insert into the middle");
        check(set.add(-2), true, "insert a new minimum");
        checkArray(set.toArray(), new int[] {-2, 3, 6, 8, 11},
                "iteration order remains sorted");

        check(set.add(6), false, "reject duplicate key");
        check(set.size(), 5, "duplicate does not change size");
        check(set.contains(-2), true, "find the first key");
        check(set.contains(8), true, "find an interior key");
        check(set.contains(12), false, "reject a key beyond the used range");

        check(set.remove(6), true, "remove an interior key");
        checkArray(set.toArray(), new int[] {-2, 3, 8, 11},
                "removal shifts the suffix left");
        check(set.remove(-2), true, "remove the first key");
        check(set.remove(11), true, "remove the final key");
        check(set.remove(11), false, "cannot remove a key twice");
        checkArray(set.toArray(), new int[] {3, 8}, "remaining keys stay sorted");

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
