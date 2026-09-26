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
            int index = homePosition(key);
            if (table[index] == key) return InsertResult.ALREADY_PRESENT;
            if (table[index] != EMPTY) return InsertResult.COLLISION;
            table[index] = key;
            return InsertResult.INSERTED;
        }

        boolean contains(int key) {
            int index = homePosition(key);
            return table[index] == key;
        }

        boolean remove(int key) {
            int index = homePosition(key);
            if (table[index] != key) return false;
            table[index] = EMPTY;
            return true;
        }

        int[] tableSnapshotForTesting() {
            return Arrays.copyOf(table, table.length);
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
        IncompleteHashTable set = new IncompleteHashTable(7);
        check(!set.contains(8), "new table does not contain 8");
        check(set.insert(8) == InsertResult.INSERTED, "insert 8 at index 1");
        check(set.insert(10) == InsertResult.INSERTED, "insert 10 at index 3");
        check(set.insert(19) == InsertResult.INSERTED, "insert 19 at index 5");
        check(Arrays.equals(set.tableSnapshotForTesting(),
                new int[]{-1, 8, -1, 10, -1, 19, -1}),
                "keys occupy only their home positions");
        check(set.contains(8) && set.contains(10) && set.contains(19),
                "contains finds inserted keys");
        check(set.insert(8) == InsertResult.ALREADY_PRESENT,
                "report a duplicate separately");
        check(set.insert(24) == InsertResult.COLLISION,
                "24 collides with 10 at index 3");
        check(!set.contains(24) && set.contains(10),
                "a collision does not overwrite 10");
        check(set.tableSnapshotForTesting()[3] == 10,
                "collision leaves the occupied home slot unchanged");
        check(!set.remove(24) && set.contains(10),
                "removing colliding absent key preserves 10");
        check(set.remove(10) && !set.contains(10), "remove stored key");
        check(!set.remove(10), "cannot remove a key twice");
        check(set.insert(24) == InsertResult.INSERTED, "removed position can be reused");
        check(set.tableSnapshotForTesting()[3] == 24,
                "reused home position stores the new key");
        checkThrows(() -> set.insert(-1), "reject negative insert key");
        checkThrows(() -> set.contains(-1), "reject negative lookup key");
        checkThrows(() -> set.remove(-1), "reject negative removal key");
    }

    private static void testAllHomePositionsAndCollisions() {
        final int capacity = 100_000;
        IncompleteHashTable table = new IncompleteHashTable(capacity);
        boolean ok = true;
        for (int key = 0; key < capacity; key++) ok &= table.insert(key) == InsertResult.INSERTED;
        for (int key = 0; key < capacity; key++) ok &= table.contains(key);
        for (int key = 0; key < capacity; key++) ok &= table.insert(key + capacity) == InsertResult.COLLISION;
        for (int key = 0; key < capacity; key += 2) ok &= table.remove(key);
        for (int key = 0; key < capacity; key += 2) ok &= table.insert(key + capacity) == InsertResult.INSERTED;
        check(ok, "100,000 home positions, collisions, removals, and slot reuses");
    }

    public static void main(String[] args) {
        testTable();
        testAllHomePositionsAndCollisions();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
