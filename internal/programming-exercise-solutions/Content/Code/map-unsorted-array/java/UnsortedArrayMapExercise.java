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
            return count == 0;
        }

        int size() {
            return count;
        }

        void clear() {
            count = 0;
        }

        boolean containsKey(int key) {
            return indexOf(key) != -1;
        }

        OptionalInt get(int key) {
            int index = indexOf(key);
            return index == -1 ? OptionalInt.empty() : OptionalInt.of(values[index]);
        }

        OptionalInt put(int key, int value) {
            int index = indexOf(key);
            if (index != -1) {
                int oldValue = values[index];
                values[index] = value;
                return OptionalInt.of(oldValue);
            }
            ensureCapacity();
            keys[count] = key;
            values[count] = value;
            count++;
            return OptionalInt.empty();
        }

        OptionalInt remove(int key) {
            int index = indexOf(key);
            if (index == -1) return OptionalInt.empty();
            int oldValue = values[index];
            keys[index] = keys[count - 1];
            values[index] = values[count - 1];
            count--;
            return OptionalInt.of(oldValue);
        }

        String[] entries() {
            String[] result = new String[count];
            for (int i = 0; i < count; i++) {
                result[i] = keys[i] + "=" + values[i];
            }
            return result;
        }

        private int indexOf(int key) {
            for (int i = 0; i < count; i++) {
                if (keys[i] == key) return i;
            }
            return -1;
        }

        private void ensureCapacity() {
            if (count == keys.length) {
                keys = Arrays.copyOf(keys, keys.length * 2);
                values = Arrays.copyOf(values, values.length * 2);
            }
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

    private static void testLargeMixedWorkload() {
        UnsortedArrayIntMap map = new UnsortedArrayIntMap(1);
        for (int key = 0; key < 1000; key++) check(map.put(key, key * 3).isEmpty(), "insert key " + key);
        check(map.size() == 1000, "size after 1000 inserts");
        for (int key = 0; key < 1000; key++) check(map.get(key).orElse(-1) == key * 3, "get key " + key);
        for (int key = 0; key < 1000; key += 3) check(map.put(key, -key).orElse(-1) == key * 3, "replace key " + key);
        check(map.size() == 1000, "replacement preserves size");
        for (int key = 0; key < 1000; key += 2) check(map.remove(key).isPresent(), "remove key " + key);
        check(map.size() == 500, "size after removing even keys");
        for (int key = 0; key < 1000; key++) check(map.containsKey(key) == (key % 2 == 1), "membership for key " + key);
        map.clear(); check(map.isEmpty() && map.put(7, 0).isEmpty() && map.get(7).orElse(-1) == 0, "clear and reuse with stored zero");
    }

    public static void main(String[] args) {
        testMap();
        testLargeMixedWorkload();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
