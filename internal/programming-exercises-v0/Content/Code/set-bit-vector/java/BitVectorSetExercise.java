import java.util.Arrays;

public class BitVectorSetExercise {
    static final class BitVectorIntSet {
        private static final int BITS_PER_WORD = 32;

        private final int universeSize;
        private final int[] words;
        private int count;

        BitVectorIntSet(int universeSize) {
            if (universeSize < 1) {
                throw new IllegalArgumentException("universeSize must be positive");
            }
            this.universeSize = universeSize;
            this.words = new int[(universeSize + BITS_PER_WORD - 1)
                    / BITS_PER_WORD];
            this.count = 0;
        }

        // Helpers

        private int wordIndex(int key) {
            // TODO: Return the index of the 32-bit word that stores key.
            return -1;
        }

        private int bitIndex(int key) {
            // TODO: Return key's bit position within its word (0 through 31).
            return -1;
        }

        private int wordMask(int bitIndex) {
            // TODO: Return a 32-bit word with only bitIndex set to 1.
            // bitIndex must be between 0 and 31. Java's signed int still
            // provides exactly 32 usable bit positions.
            return 0;
        }

        private void checkKey(int key) {
            if (key < 0 || key >= universeSize) {
                throw new IndexOutOfBoundsException(
                        "key must be in [0, " + universeSize + ")");
            }
        }

        int universeSize() {
            return universeSize;
        }

        boolean isEmpty() {
            // TODO: Return whether the set contains no keys.
            return false;
        }

        int size() {
            // TODO: Return the number of distinct keys currently present.
            return -1;
        }

        void clear() {
            // TODO: Clear every word and reset count. Arrays.fill may be useful.
        }

        boolean contains(int key) {
            checkKey(key);
            // TODO: Use wordIndex(key), bitIndex(key), and wordMask(bitIndex)
            // to find and test key's membership bit.
            return false;
        }

        boolean add(int key) {
            checkKey(key);
            // TODO: Use the three helpers above. If the selected bit is already
            // 1, return false. Otherwise, set it with bitwise OR, increment
            // count once, and return true.
            return false;
        }

        boolean remove(int key) {
            checkKey(key);
            // TODO: Use the three helpers above. If the selected bit is already
            // 0, return false. Otherwise, clear it with bitwise AND and
            // complement, decrement count once, and return true.
            return false;
        }

        int[] toArray() {
            // Return the keys in increasing order for testing and iteration.
            int[] result = new int[count];
            int next = 0;
            for (int key = 0; key < universeSize; key++) {
                if (contains(key)) {
                    result[next++] = key;
                }
            }
            return result;
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

    private static void checkThrows(Runnable action, String label) {
        try {
            action.run();
            failures++;
            System.out.println("FAIL: " + label + " (no exception thrown)");
        } catch (IndexOutOfBoundsException expected) {
            System.out.println("pass: " + label);
        }
    }

    private static void checkInvalidUniverse(String label) {
        try {
            new BitVectorIntSet(0);
            failures++;
            System.out.println("FAIL: " + label + " (no exception thrown)");
        } catch (IllegalArgumentException expected) {
            System.out.println("pass: " + label);
        }
    }

    private static void testSet() {
        checkInvalidUniverse("reject nonpositive universe size");
        BitVectorIntSet set = new BitVectorIntSet(128);
        check(set.universeSize(), 128, "constructor records universe size");
        check(set.isEmpty(), true, "new set is empty");
        check(set.size(), 0, "new set has size zero");
        check(set.contains(63), false, "valid missing key is absent");
        check(set.remove(96), false, "removing a missing key changes nothing");

        int[] acrossAllWords = {0, 31, 32, 47, 63, 64, 95, 96, 127};
        for (int key : acrossAllWords) {
            check(set.add(key), true, "add key " + key);
        }
        check(set.size(), acrossAllWords.length,
                "size includes keys stored in all four words");
        checkArray(set.toArray(), acrossAllWords,
                "iteration finds boundary keys in increasing order");
        check(set.contains(31), true, "find high bit of first word");
        check(set.contains(32), true, "find low bit of second word");
        check(set.contains(64), true, "find low bit of third word");
        check(set.contains(127), true, "find high bit of fourth word");
        check(set.contains(30), false, "nearby clear bit remains absent");

        check(set.add(64), false, "reject duplicate key");
        check(set.size(), acrossAllWords.length,
                "duplicate does not change size");
        check(set.remove(31), true, "remove high bit of first word");
        check(set.remove(64), true, "remove low bit of third word");
        check(set.remove(127), true, "remove high bit of fourth word");
        check(set.remove(64), false, "cannot remove a key twice");
        checkArray(set.toArray(), new int[] {0, 32, 47, 63, 95, 96},
                "removal clears only the selected bits");

        checkThrows(() -> set.contains(-1), "reject negative key");
        checkThrows(() -> set.add(128), "reject key equal to universe size");

        set.clear();
        check(set.isEmpty(), true, "clear empties the set");
        check(set.size(), 0, "size is zero after clear");
        check(set.contains(0), false, "clear resets the first word");
        check(set.contains(96), false, "clear resets the final word");
        check(set.add(127), true, "set can be reused after clear");
    }

    public static void main(String[] args) {
        testSet();
        System.out.println(failures == 0
                ? "All tests passed."
                : failures + " test(s) failed.");
    }
}
