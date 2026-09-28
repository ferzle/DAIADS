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
            // TODO: Return whether no keys are present.
            return false;
        }

        int size() {
            // TODO: Return the number of present keys.
            return -1;
        }

        boolean containsKey(int key) {
            checkKey(key);
            // TODO: Inspect present[key].
            return false;
        }

        OptionalInt get(int key) {
            checkKey(key);
            // TODO: Return values[key] only when present[key] is true.
            return OptionalInt.empty();
        }

        OptionalInt put(int key, int value) {
            checkKey(key);
            // TODO: Replace and return the old value if present. Otherwise set
            // the presence flag, store the value, increment count, and return empty.
            return OptionalInt.empty();
        }

        OptionalInt remove(int key) {
            checkKey(key);
            // TODO: If present, clear the flag, decrement count, and return the value.
            return OptionalInt.empty();
        }

        void clear() {
            // TODO: Clear all presence flags and reset count. The values need
            // not be erased because absent positions are never interpreted.
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
    private static void checkInvalidUniverse() {
        try {
            new DirectAddressIntMap(0);
            failures++;
            System.out.println("FAIL: reject nonpositive universe size");
        } catch (IllegalArgumentException expected) {
            System.out.println("pass: reject nonpositive universe size");
        }
    }

    private static void testMap() {
        checkInvalidUniverse();
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

    public static void main(String[] args) {
        testMap();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
    }
}
