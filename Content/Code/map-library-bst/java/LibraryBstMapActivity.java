import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.TreeMap;

public class LibraryBstMapActivity {
    static final class OrderedIntMap {
        private final TreeMap<Integer, Integer> tree = new TreeMap<>();

        boolean isEmpty() {
            // TODO: Delegate to the tree map.
            return false;
        }

        int size() {
            // TODO: Delegate to the tree map.
            return -1;
        }

        boolean containsKey(int key) {
            // TODO: Do not try to infer presence from the associated value.
            return false;
        }

        OptionalInt get(int key) {
            // TODO: Return an empty result when key is absent.
            return OptionalInt.empty();
        }

        OptionalInt put(int key, int value) {
            // TODO: Insert or replace, returning the old value when one existed.
            return OptionalInt.empty();
        }

        OptionalInt remove(int key) {
            // TODO: Remove key and return its old value, or return empty.
            return OptionalInt.empty();
        }

        List<String> entriesInRange(int low, int high) {
            // TODO: Return "key=value" strings for low <= key <= high in
            // increasing key order. Return an empty list when low > high.
            return new ArrayList<>();
        }
    }

    private static int failures;

    private static void check(boolean condition, String label) {
        if (condition) {
            System.out.println("pass: " + label);
        } else {
            failures++;
            System.out.println("FAIL: " + label);
        }
    }

    private static void testMap() {
        OrderedIntMap map = new OrderedIntMap();
        check(map.isEmpty() && map.size() == 0, "new map is empty");
        check(map.put(20, 4).isEmpty(), "put a new key");
        check(map.put(5, 0).isEmpty(), "store zero as an ordinary value");
        check(map.put(12, 7).isEmpty() && map.put(30, 9).isEmpty(),
                "put more keys");
        check(map.containsKey(5) && map.get(5).orElse(-1) == 0,
                "presence is distinct from value zero");
        check(map.put(12, 8).orElse(-1) == 7 && map.size() == 4,
                "replacement returns old value without growing");
        check(map.entriesInRange(6, 20).equals(List.of("12=8", "20=4")),
                "closed range is sorted");
        check(map.remove(20).orElse(-1) == 4 && !map.containsKey(20),
                "remove a present key");
        check(map.remove(99).isEmpty(), "remove an absent key");
    }

    public static void main(String[] args) {
        testMap();
        System.out.println(failures == 0 ? "All tests passed."
                : failures + " test(s) failed.");
    }
}
