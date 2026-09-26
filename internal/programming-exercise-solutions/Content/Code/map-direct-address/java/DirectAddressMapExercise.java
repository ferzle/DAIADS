import java.util.Arrays;
import java.util.OptionalInt;

public class DirectAddressMapExercise {
    static final class DirectAddressIntMap {
        private final boolean[] present;
        private final int[] values;
        private int count;

        DirectAddressIntMap(int universeSize) {
            if (universeSize < 1) {
                throw new IllegalArgumentException("universeSize must be positive");
            }
            present = new boolean[universeSize];
            values = new int[universeSize];
        }

        int universeSize() { return present.length; }

        boolean isEmpty() {
            return count == 0;
        }

        int size() {
            return count;
        }

        boolean containsKey(int key) {
            checkKey(key);
            return present[key];
        }

        OptionalInt get(int key) {
            checkKey(key);
            return present[key] ? OptionalInt.of(values[key]) : OptionalInt.empty();
        }

        OptionalInt put(int key, int value) {
            checkKey(key);
            if (present[key]) {
                int oldValue = values[key];
                values[key] = value;
                return OptionalInt.of(oldValue);
            }
            present[key] = true;
            values[key] = value;
            count++;
            return OptionalInt.empty();
        }

        OptionalInt remove(int key) {
            checkKey(key);
            if (!present[key]) return OptionalInt.empty();
            present[key] = false;
            count--;
            return OptionalInt.of(values[key]);
        }

        void clear() {
            Arrays.fill(present, false);
            count = 0;
        }

        private void checkKey(int key) {
            if (key < 0 || key >= present.length) {
                throw new IndexOutOfBoundsException("key outside the universe");
            }
        }
    }

    private static int failures;
    private static void check(boolean condition, String label) {
        if (condition) System.out.println("pass: " + label);
        else { failures++; System.out.println("FAIL: " + label); }
    }
    private static void checkThrows(Runnable action, String label) {
        try { action.run(); failures++; System.out.println("FAIL: " + label); }
        catch (IndexOutOfBoundsException expected) { System.out.println("pass: " + label); }
    }
    private static void testMap() {
        DirectAddressIntMap map = new DirectAddressIntMap(10);
        check(map.universeSize() == 10 && map.isEmpty(), "new map records universe");
        check(map.put(0, 0).isEmpty() && map.put(9, -4).isEmpty(), "insert boundary keys");
        check(map.containsKey(0) && map.get(0).orElse(-1) == 0, "zero value is present");
        check(map.put(9, 12).orElse(-1) == -4 && map.size() == 2, "replace without growing");
        check(map.remove(9).orElse(-1) == 12 && map.remove(9).isEmpty(), "remove once");
        checkThrows(() -> map.get(-1), "reject negative key");
        checkThrows(() -> map.put(10, 1), "reject key equal to universe size");
        map.clear();
        check(map.isEmpty() && !map.containsKey(0), "clear presence flags");
        check(map.put(5, 50).isEmpty(), "reuse after clear");
    }

    private static void testExhaustiveUniverse() {
        final int universe = 100_000;
        DirectAddressIntMap map = new DirectAddressIntMap(universe);
        boolean ok = true;
        for (int key = 0; key < universe; key++) ok &= map.put(key, key - 50_000).isEmpty();
        ok &= map.size() == universe;
        for (int key = 0; key < universe; key++) ok &= map.get(key).orElse(Integer.MIN_VALUE) == key - 50_000;
        for (int key = 0; key < universe; key += 2) ok &= map.remove(key).isPresent();
        for (int key = 0; key < universe; key++) ok &= map.containsKey(key) == (key % 2 == 1);
        map.clear();
        ok &= map.isEmpty() && map.put(universe - 1, 0).isEmpty();
        check(ok, "100,000-key exhaustive universe workload");
    }

    public static void main(String[] args) {
        testMap();
        testExhaustiveUniverse();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
