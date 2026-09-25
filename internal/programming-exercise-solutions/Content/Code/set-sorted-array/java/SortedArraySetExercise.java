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
            return count == 0;
        }

        int size() {
            return count;
        }

        void clear() {
            count = 0;
        }

        boolean contains(int key) {
            int index = lowerBound(key);
            return index < count && keys[index] == key;
        }

        boolean add(int key) {
            int index = lowerBound(key);
            if (index < count && keys[index] == key) return false;
            ensureCapacity();
            for (int i = count; i > index; i--) keys[i] = keys[i - 1];
            keys[index] = key;
            count++;
            return true;
        }

        boolean remove(int key) {
            int index = lowerBound(key);
            if (index == count || keys[index] != key) return false;
            for (int i = index; i < count - 1; i++) keys[i] = keys[i + 1];
            count--;
            return true;
        }

        int[] toArray() {
            return Arrays.copyOf(keys, count);
        }

        private int lowerBound(int key) {
            int low = 0, high = count;
            while (low < high) {
                int middle = low + (high - low) / 2;
                if (keys[middle] < key) low = middle + 1;
                else high = middle;
            }
            return low;
        }

        private void ensureCapacity() {
            if (count == keys.length) keys = Arrays.copyOf(keys, keys.length * 2);
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

    private static void testLargeOrderedWorkload() {
        SortedArrayIntSet set = new SortedArrayIntSet(1);
        for (int i = 999; i >= 0; i--) check(set.add(i), true, "descending add " + i);
        check(set.size(), 1000, "size after descending additions");
        int[] ordered = set.toArray();
        for (int i = 0; i < 1000; i++) check(ordered[i], i, "sorted position " + i);
        for (int i = 0; i < 1000; i += 2) check(set.remove(i), true, "remove key " + i);
        check(set.size(), 500, "size after removing even keys");
        for (int i = 0; i < 1000; i++)
            check(set.contains(i), i % 2 == 1, "membership after removals for " + i);
        set.clear(); check(set.add(42), true, "reuse after clear");
    }

    public static void main(String[] args) {
        testSet();
        testLargeOrderedWorkload();
        System.out.println(failures == 0
                ? "All tests passed."
                : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
