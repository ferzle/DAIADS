import java.util.Arrays;

public class IncompleteHashTableExercise {
    enum InsertResult { INSERTED, ALREADY_PRESENT, COLLISION }

    static final class IncompleteHashTable {
        private static final int EMPTY = -1;
        private final int[] table;

        IncompleteHashTable(int capacity) {
            if (capacity < 1) {
                throw new IllegalArgumentException("capacity must be positive");
            }
            table = new int[capacity];
            Arrays.fill(table, EMPTY);
        }

        private int homePosition(int key) {
            checkKey(key);
            return key % table.length;
        }

        private void checkKey(int key) {
            if (key < 0) {
                throw new IllegalArgumentException("key must be nonnegative");
            }
        }

        InsertResult insert(int key) {
            // TODO: Inspect only key's home position. Store the key if that
            // position is empty, and return the appropriate InsertResult.
            return InsertResult.COLLISION;
        }

        boolean contains(int key) {
            // TODO: Return whether key is stored at its home position.
            return false;
        }

        boolean remove(int key) {
            // TODO: If key is stored at its home position, mark that position
            // empty and return true. Otherwise make no change and return false.
            return false;
        }
    }

    private static int failures;

    private static void check(boolean condition, String label) {
        if (condition) System.out.println("pass: " + label);
        else { failures++; System.out.println("FAIL: " + label); }
    }

    private static void checkThrows(Runnable action, String label) {
        try { action.run(); failures++; System.out.println("FAIL: " + label); }
        catch (IllegalArgumentException expected) { System.out.println("pass: " + label); }
    }

    private static void testTable() {
        checkThrows(() -> new IncompleteHashTable(0), "reject nonpositive capacity");
        IncompleteHashTable set = new IncompleteHashTable(7);
        check(!set.contains(8), "new table does not contain 8");
        check(set.insert(8) == InsertResult.INSERTED, "insert 8 at index 1");
        check(set.insert(10) == InsertResult.INSERTED, "insert 10 at index 3");
        check(set.insert(19) == InsertResult.INSERTED, "insert 19 at index 5");
        check(set.contains(8) && set.contains(10) && set.contains(19),
                "contains finds inserted keys");
        check(set.insert(8) == InsertResult.ALREADY_PRESENT,
                "report a duplicate separately");
        check(set.insert(24) == InsertResult.COLLISION,
                "24 collides with 10 at index 3");
        check(!set.contains(24) && set.contains(10),
                "a collision does not overwrite 10");
        check(!set.remove(24) && set.contains(10),
                "removing colliding absent key preserves 10");
        check(set.remove(10) && !set.contains(10), "remove stored key");
        check(!set.remove(10), "cannot remove a key twice");
        check(set.insert(24) == InsertResult.INSERTED, "removed position can be reused");
        checkThrows(() -> set.insert(-1), "reject negative insert key");
        checkThrows(() -> set.contains(-1), "reject negative lookup key");
        checkThrows(() -> set.remove(-1), "reject negative removal key");
    }

    public static void main(String[] args) {
        testTable();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
    }
}
