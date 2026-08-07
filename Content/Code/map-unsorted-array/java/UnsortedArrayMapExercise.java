import java.util.Arrays;
import java.util.OptionalInt;

public class UnsortedArrayMapExercise {
    static final class UnsortedArrayIntMap {
        private int[] keys;
        private int[] values;
        private int count;

        UnsortedArrayIntMap(int initialCapacity) {
            if (initialCapacity < 1) {
                throw new IllegalArgumentException("initialCapacity must be positive");
            }
            keys = new int[initialCapacity];
            values = new int[initialCapacity];
        }

        boolean isEmpty() {
            // TODO: Return whether there are no used entries.
            return false;
        }

        int size() {
            // TODO: Return the number of entries, not the capacity.
            return -1;
        }

        void clear() {
            // TODO: Remove all logical entries while retaining the arrays.
        }

        boolean containsKey(int key) {
            // TODO: Search only keys[0..count).
            return false;
        }

        OptionalInt get(int key) {
            // TODO: Return the aligned value, or empty when key is absent.
            return OptionalInt.empty();
        }

        OptionalInt put(int key, int value) {
            // TODO: Replace and return an old value when key is present.
            // Otherwise grow if needed, append key and value, and return empty.
            return OptionalInt.empty();
        }

        OptionalInt remove(int key) {
            // TODO: If present, save its value, copy BOTH parts of the final
            // entry into its gap, decrement count, and return the saved value.
            return OptionalInt.empty();
        }

        String[] entries() {
            String[] result = new String[count];
            for (int i = 0; i < count; i++) {
                result[i] = keys[i] + "=" + values[i];
            }
            return result;
        }

        private int indexOf(int key) {
            // TODO: Return key's used index, or -1 if absent.
            return -1;
        }

        private void ensureCapacity() {
            // TODO: When full, double BOTH arrays and preserve all entries.
        }
    }

    private static int failures;
    private static void check(boolean condition, String label) {
        if (condition) System.out.println("pass: " + label);
        else { failures++; System.out.println("FAIL: " + label); }
    }

    private static void testMap() {
        UnsortedArrayIntMap map = new UnsortedArrayIntMap(2);
        check(map.isEmpty() && map.size() == 0, "new map is empty");
        check(map.get(4).isEmpty() && map.remove(4).isEmpty(), "missing operations");
        check(map.put(8, 80).isEmpty() && map.put(3, 0).isEmpty(), "insert entries");
        check(map.containsKey(3) && map.get(3).orElse(-1) == 0, "stored zero is present");
        check(map.put(8, 81).orElse(-1) == 80 && map.size() == 2, "replace value");
        check(map.put(11, 110).isEmpty() && map.put(-2, -20).isEmpty(), "resize arrays together");
        check(map.remove(3).orElse(-1) == 0, "remove interior entry");
        check(Arrays.equals(map.entries(), new String[]{"8=81", "-2=-20", "11=110"}),
                "copy final aligned entry into gap");
        check(map.remove(11).orElse(-1) == 110 && map.remove(11).isEmpty(), "remove once");
        map.clear();
        check(map.isEmpty() && map.put(5, 50).isEmpty(), "clear and reuse");
    }

    public static void main(String[] args) {
        testMap();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
    }
}
