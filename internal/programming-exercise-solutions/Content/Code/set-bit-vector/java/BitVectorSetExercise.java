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
            return key / BITS_PER_WORD;
        }

        private int bitIndex(int key) {
            return key % BITS_PER_WORD;
        }

        private int wordMask(int bitIndex) {
            return 1 << bitIndex;
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
            return count == 0;
        }

        int size() {
            return count;
        }

        void clear() {
            Arrays.fill(words, 0);
            count = 0;
        }

        boolean contains(int key) {
            checkKey(key);
            int mask = wordMask(bitIndex(key));
            return (words[wordIndex(key)] & mask) != 0;
        }

        boolean add(int key) {
            checkKey(key);
            int word = wordIndex(key);
            int mask = wordMask(bitIndex(key));
            if ((words[word] & mask) != 0) return false;
            words[word] |= mask;
            count++;
            return true;
        }

        boolean remove(int key) {
            checkKey(key);
            int word = wordIndex(key);
            int mask = wordMask(bitIndex(key));
            if ((words[word] & mask) == 0) return false;
            words[word] &= ~mask;
            count--;
            return true;
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

    private static void testEveryBitAroundWordBoundaries() {
        for (int universe : new int[] {1, 31, 32, 33, 63, 64, 65, 257}) {
            BitVectorIntSet set = new BitVectorIntSet(universe);
            for (int key = 0; key < universe; key++) check(set.add(key), true, "add key " + key + " in universe " + universe);
            check(set.size(), universe, "all keys counted in universe " + universe);
            for (int key = 0; key < universe; key++) check(set.contains(key), true, "find key " + key + " in universe " + universe);
            for (int key = 0; key < universe; key += 2) check(set.remove(key), true, "remove even key " + key);
            for (int key = 0; key < universe; key++) check(set.contains(key), key % 2 == 1, "post-removal membership for " + key);
            set.clear(); check(set.isEmpty(), true, "clear universe " + universe);
        }
    }

    private static void testMillionBitUniverse() {
        final int universe = 1_000_000;
        BitVectorIntSet set = new BitVectorIntSet(universe);
        boolean ok = true;
        for (int key = 0; key < universe; key++) ok &= set.add(key);
        ok &= set.size() == universe && set.contains(0) && set.contains(31)
                && set.contains(32) && set.contains(999_999);
        for (int key = 0; key < universe; key += 2) ok &= set.remove(key);
        ok &= set.size() == universe / 2 && !set.contains(0)
                && set.contains(999_999) && !set.contains(999_998);
        set.clear();
        check(ok && set.isEmpty() && set.size() == 0, true, "million-bit aggregate workload");
    }

    public static void main(String[] args) {
        testSet();
        testEveryBitAroundWordBoundaries();
        testMillionBitUniverse();
        System.out.println(failures == 0
                ? "All tests passed."
                : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
