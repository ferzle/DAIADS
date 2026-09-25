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
            return count == 0;
        }

        int size() {
            return count;
        }

        void clear() {
            count = 0;
        }

        boolean contains(int key) {
            for (int i = 0; i < count; i++) {
                if (keys[i] == key) return true;
            }
            return false;
        }

        boolean add(int key) {
            if (contains(key)) return false;
            ensureCapacity();
            keys[count++] = key;
            return true;
        }

        boolean remove(int key) {
            for (int i = 0; i < count; i++) {
                if (keys[i] == key) {
                    keys[i] = keys[count - 1];
                    count--;
                    return true;
                }
            }
            return false;
        }

        int[] toArray() {
            // This snapshot exposes the current iteration order for testing.
            return Arrays.copyOf(keys, count);
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

    private static void testLargeDifferentialWorkload() {
        UnsortedArrayIntSet set = new UnsortedArrayIntSet(1);
        for (int i = 0; i < 1000; i++) check(set.add(i), true, "add unique key " + i);
        check(set.size(), 1000, "size after 1000 unique additions");
        for (int i = 0; i < 1000; i++) {
            check(set.contains(i), true, "contains key " + i);
            check(set.add(i), false, "reject duplicate key " + i);
        }
        for (int i = 0; i < 1000; i += 2) check(set.remove(i), true, "remove key " + i);
        check(set.size(), 500, "size after removing even keys");
        for (int i = 0; i < 1000; i++)
            check(set.contains(i), i % 2 == 1, "membership after removals for " + i);
        set.clear(); check(set.isEmpty(), true, "empty after large clear");
        check(set.add(0), true, "reuse after large clear");
    }

    public static void main(String[] args) {
        testSet();
        testLargeDifferentialWorkload();
        System.out.println(failures == 0
                ? "All tests passed."
                : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
