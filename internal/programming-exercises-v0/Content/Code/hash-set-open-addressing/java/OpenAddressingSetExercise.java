import java.util.Arrays;

public class OpenAddressingSetExercise {
    enum ProbingType { LINEAR, QUADRATIC, DOUBLE_HASHING }

    static final class OpenAddressingIntSet {
        private enum SlotState { EMPTY, OCCUPIED, DELETED }

        private final int m;
        private final int[] keys;
        private final SlotState[] states;
        private final ProbingType probingType;
        private int count;

        OpenAddressingIntSet(int capacityExponent, ProbingType probingType) {
            if (capacityExponent < 1 || capacityExponent > 30) {
                throw new IllegalArgumentException("capacityExponent must be from 1 through 30");
            }
            if (probingType == null) {
                throw new IllegalArgumentException("probingType must not be null");
            }
            m = 1 << capacityExponent;
            keys = new int[m];
            states = new SlotState[m];
            Arrays.fill(states, SlotState.EMPTY);
            this.probingType = probingType;
        }

        int size() {
            // TODO: Return the number of distinct keys currently stored.
            return -1;
        }

        boolean isEmpty() {
            // TODO: Return whether the set contains no keys.
            return false;
        }

        int capacity() { return m; }

        boolean contains(int key) {
            // TODO: Reject a negative key. Probe at most capacity() positions,
            // stopping successfully at key or unsuccessfully at an EMPTY slot.
            return false;
        }

        boolean insert(int key) {
            // TODO: Reject a negative key. Remember the first DELETED slot, but
            // continue until finding key, an EMPTY slot, or the probe limit.
            // Add the key and increment count exactly once only when it is absent.
            return false;
        }

        boolean remove(int key) {
            // TODO: Reject a negative key. Replace a matching OCCUPIED slot with
            // DELETED, decrement count exactly once, and return whether key existed.
            return false;
        }

        void clear() {
            // TODO: Restore every slot to EMPTY and reset count to zero.
        }

        private int probeIndex(int key, int i) {
            switch (probingType) {
                case LINEAR:
                    return linearProbeIndex(key, i);
                case QUADRATIC:
                    return quadraticProbeIndex(key, i);
                case DOUBLE_HASHING:
                    return doubleHashProbeIndex(key, i);
                default:
                    throw new AssertionError("unknown probing type");
            }
        }

        private int linearProbeIndex(int key, int i) {
            // TODO: Compute probe i using linear probing.
            return 0;
        }

        private int quadraticProbeIndex(int key, int i) {
            // TODO: Compute probe i using triangular quadratic offsets.
            return 0;
        }

        private int doubleHashProbeIndex(int key, int i) {
            // TODO: Compute probe i using primaryHash and secondaryHash.
            return 0;
        }

        private int primaryHash(int key) {
            // TODO: Compute h1(key).
            return 0;
        }

        private int secondaryHash(int key) {
            // TODO: Compute the odd double-hashing step h2(key).
            return 0;
        }

        private static void checkKey(int key) {
            if (key < 0) throw new IllegalArgumentException("key must be nonnegative");
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

    private static void checkThrows(Runnable action, String label) {
        try { action.run(); failures++; System.out.println("FAIL: " + label); }
        catch (IllegalArgumentException expected) { System.out.println("pass: " + label); }
    }

    private static void testStrategy(ProbingType type) {
        String name = type.toString();
        OpenAddressingIntSet set = new OpenAddressingIntSet(3, type);
        check(set.capacity(), 8, name + ": exponent 3 gives capacity 8");
        check(set.isEmpty() && set.size() == 0, name + ": new set is empty");
        check(set.insert(1) && set.insert(9) && set.insert(17), name + ": insert colliding keys");
        check(set.contains(1) && set.contains(9) && set.contains(17), name + ": find colliding keys");
        check(!set.insert(17) && set.size() == 3, name + ": reject duplicate insertion");
        check(set.remove(9) && !set.contains(9), name + ": remove creates a tombstone");
        check(set.contains(17), name + ": lookup continues past a tombstone");
        check(set.insert(41) && set.contains(41), name + ": insertion can reuse a tombstone");
        check(!set.remove(99), name + ": absent removal changes nothing");

        OpenAddressingIntSet wraparound = new OpenAddressingIntSet(3, type);
        check(wraparound.insert(7) && wraparound.insert(15) && wraparound.contains(15),
                name + ": probe sequence wraps around");

        OpenAddressingIntSet full = new OpenAddressingIntSet(3, type);
        boolean filled = true;
        for (int key = 0; key < full.capacity(); key++) filled &= full.insert(key);
        check(filled && !full.insert(8), name + ": insertion fails when no position is available");
        check(full.remove(0) && full.insert(8) && full.contains(8),
                name + ": insertion reuses the only available tombstone");

        set.clear();
        check(set.isEmpty() && set.size() == 0 && !set.contains(1), name + ": clear resets the set");
        checkThrows(() -> set.contains(-1), name + ": reject negative lookup key");
        checkThrows(() -> set.insert(-1), name + ": reject negative insertion key");
        checkThrows(() -> set.remove(-1), name + ": reject negative removal key");
    }

    private static void testTombstoneStress(ProbingType type) {
        String name = type + " stress";
        OpenAddressingIntSet set = new OpenAddressingIntSet(7, type);
        int m = set.capacity();
        boolean result = true;

        // All 80 keys have home index 3, so every insertion must resolve collisions.
        for (int q = 0; q < 80; q++) result &= set.insert(3 + m * q);
        check(result && set.size() == 80, name + ": insert 80 colliding keys");

        result = true;
        for (int q = 0; q < 80; q++) result &= set.contains(3 + m * q);
        check(result, name + ": find all initial keys");

        // Remove 27 keys. Each replacement has the same home index and, for
        // double hashing, the same step as the key it replaces.
        result = true;
        for (int q = 0; q < 80; q += 3) result &= set.remove(3 + m * q);
        check(result && set.size() == 53, name + ": remove 27 keys");

        result = true;
        for (int q = 0; q < 80; q++) {
            int key = 3 + m * q;
            result &= q % 3 == 0 ? !set.contains(key) : set.contains(key);
        }
        check(result, name + ": searches cross tombstones correctly");

        result = true;
        for (int q = 0; q < 80; q += 3) result &= set.insert(3 + m * (q + 128));
        check(result && set.size() == 80, name + ": replace all 27 removed keys");

        // Create and refill a second wave of 14 tombstones.
        result = true;
        for (int q = 0; q < 80; q += 6) result &= set.remove(3 + m * (q + 128));
        check(result && set.size() == 66, name + ": remove 14 replacement keys");

        result = true;
        for (int q = 0; q < 80; q += 6) result &= set.insert(3 + m * (q + 256));
        check(result && set.size() == 80, name + ": refill the second tombstone wave");

        result = true;
        for (int q = 0; q < 80; q++) {
            if (q % 6 == 0) {
                result &= !set.contains(3 + m * (q + 128));
                result &= set.contains(3 + m * (q + 256));
            } else if (q % 3 == 0) {
                result &= set.contains(3 + m * (q + 128));
            } else {
                result &= set.contains(3 + m * q);
            }
        }
        check(result, name + ": final membership is correct after 41 removals and replacements");
    }

    public static void main(String[] args) {
        checkThrows(() -> new OpenAddressingIntSet(0, ProbingType.LINEAR),
                "reject an invalid capacity exponent");
        checkThrows(() -> new OpenAddressingIntSet(3, null), "reject a null probing type");
        for (ProbingType type : ProbingType.values()) {
            testStrategy(type);
            testTombstoneStress(type);
        }
        System.out.println(failures == 0 ? "All tests passed."
                : failures + " test(s) failed.");
    }
}
