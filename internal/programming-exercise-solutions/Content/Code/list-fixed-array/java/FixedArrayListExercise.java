public class FixedArrayListExercise {
    static int failures = 0;
static class IntList {
    private int[] A;
    private int count;

    public IntList(int capacity) {
        A = new int[capacity];
        count = 0;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public void clear() {
        count = 0;
    }

    public int first() {
        return isEmpty() ? -1 : A[0];
    }

    public int last() {
        return isEmpty() ? -1 : A[count - 1];
    }

    public int get(int index) {
        return index < 0 || index >= count ? -1 : A[index];
    }

    public boolean set(int index, int value) {
        if (index < 0 || index >= count) return false;
        A[index] = value;
        return true;
    }

    public boolean addFirst(int value) {
        return insert(0, value);
    }

    public boolean addLast(int value) {
        return insert(count, value);
    }

    public boolean insert(int index, int value) {
        if (index < 0 || index > count || count == A.length) return false;
        for (int i = count; i > index; i--) A[i] = A[i - 1];
        A[index] = value;
        count++;
        return true;
    }

    public int removeFirst() {
        return remove(0);
    }

    public int removeLast() {
        return remove(count - 1);
    }

    public int remove(int index) {
        if (index < 0 || index >= count) return -1;
        int value = A[index];
        for (int i = index; i < count - 1; i++) A[i] = A[i + 1];
        count--;
        return value;
    }

    public int indexOf(int value) {
        for (int i = 0; i < count; i++) {
            if (A[i] == value) return i;
        }
        return -1;
    }

    public boolean contains(int value) {
        return indexOf(value) != -1;
    }

    public boolean delete(int value) {
        int index = indexOf(value);
        if (index == -1) return false;
        remove(index);
        return true;
    }

    public int[] usedValuesForTesting() {
        return java.util.Arrays.copyOf(A, count);
    }
}

static String checkLocation() {
    StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
    return caller.getMethodName() + "(), line " + caller.getLineNumber();
}

static void check(int actual, int expected) {
    if (actual == expected) {
        System.out.println("PASS at " + checkLocation() + ": got " + actual);
    } else {
        failures++;
        System.out.println("FAIL at " + checkLocation() + ": expected " + expected + " but got " + actual);
    }
}

static void check(boolean actual, boolean expected) {
    if (actual == expected) {
        System.out.println("PASS at " + checkLocation() + ": got " + actual);
    } else {
        failures++;
        System.out.println("FAIL at " + checkLocation() + ": expected " + expected + " but got " + actual);
    }
}

static void testList() {
    IntList list = new IntList(5);

    check(list.isEmpty(), true);
    check(list.size(), 0);
    check(list.first(), -1);
    check(list.last(), -1);
    check(list.get(0), -1);
    check(list.removeFirst(), -1);
    check(list.removeLast(), -1);
    check(list.remove(0), -1);

    check(list.addLast(4), true);       // [4]
    check(list.addLast(7), true);       // [4, 7]
    check(list.addFirst(2), true);      // [2, 4, 7]
    check(list.insert(2, 9), true);     // [2, 4, 9, 7]
    check(java.util.Arrays.equals(list.usedValuesForTesting(), new int[]{2, 4, 9, 7}), true);

    check(list.size(), 4);
    check(list.isEmpty(), false);
    check(list.first(), 2);
    check(list.last(), 7);
    check(list.get(0), 2);
    check(list.get(2), 9);
    check(list.get(4), -1);
    check(list.get(-1), -1);
    check(list.set(-1, 8), false);
    check(list.insert(-1, 8), false);
    check(list.remove(-1), -1);

    check(list.set(1, 5), true);        // [2, 5, 9, 7]
    check(list.get(1), 5);
    check(list.set(4, 8), false);
    check(list.size(), 4);

    check(list.addLast(11), true);      // [2, 5, 9, 7, 11]
    check(list.size(), 5);
    check(list.addLast(13), false);     // full
    check(list.addFirst(13), false);    // full
    check(list.insert(2, 13), false);   // full
    check(java.util.Arrays.equals(list.usedValuesForTesting(), new int[]{2, 5, 9, 7, 11}), true);
    check(list.size(), 5);

    check(list.indexOf(9), 2);
    check(list.indexOf(100), -1);
    check(list.contains(7), true);
    check(list.contains(100), false);

    check(list.remove(2), 9);           // [2, 5, 7, 11]
    check(list.get(2), 7);
    check(list.removeFirst(), 2);       // [5, 7, 11]
    check(list.removeLast(), 11);       // [5, 7]
    check(list.size(), 2);
    check(list.first(), 5);
    check(list.last(), 7);

    check(list.delete(5), true);        // [7]
    check(list.delete(5), false);
    check(list.size(), 1);
    check(list.first(), 7);
    check(list.last(), 7);

    check(list.removeLast(), 7);        // []
    check(list.isEmpty(), true);
    check(list.size(), 0);
    check(list.removeLast(), -1);

    check(list.addLast(6), true);       // [6]
    check(list.addLast(8), true);       // [6, 8]
    list.clear();                       // []
    check(list.isEmpty(), true);
    check(list.size(), 0);
    check(list.first(), -1);

    IntList duplicates = new IntList(8);
    check(duplicates.addLast(5), true);
    check(duplicates.addLast(2), true);
    check(duplicates.addLast(5), true);
    check(duplicates.addLast(5), true);
    check(duplicates.indexOf(5), 0);
    check(duplicates.delete(5), true);
    check(duplicates.size(), 3);
    check(duplicates.indexOf(5), 1);

    IntList large = new IntList(256);
    boolean largeOk = true;
    for (int i = 0; i < 256; i++) largeOk &= large.addLast(i);
    for (int i = 0; i < 256; i++) largeOk &= large.get(i) == i;
    for (int i = 0; i < 256; i++) largeOk &= large.removeFirst() == i;
    check(largeOk && large.isEmpty(), true);
}

static void testLargeIndexedWorkload() {
    IntList list = new IntList(256);
    for (int i = 0; i < 200; i++) check(list.addLast(i % 17), true);
    check(list.size(), 200); check(list.first(), 0); check(list.last(), 12);
    check(list.insert(100, 999), true); check(list.get(100), 999); check(list.size(), 201);
    check(list.remove(100), 999); check(list.size(), 200);
    check(list.indexOf(5), 5); check(list.delete(5), true); check(list.indexOf(5), 21);
    check(list.set(0, 777), true); check(list.get(0), 777);
    check(list.insert(-1, 8), false); check(list.insert(list.size() + 1, 8), false);
    check(list.size(), 199);
    for (int i = 0; i < 199; i++) list.removeLast();
    check(list.size(), 0); check(list.addFirst(42), true); check(list.removeFirst(), 42);
}

static void testHundredThousandAppendsAndReads() {
    final int count = 100_000;
    IntList list = new IntList(count);
    boolean ok = true;
    for (int i = 0; i < count; i++) ok &= list.addLast(i);
    for (int i = 0; i < count; i++) ok &= list.get(i) == i;
    check(ok && list.size() == count && list.first() == 0 && list.last() == count - 1, true);
}

public static void main(String[] args) {
    testList();
    testLargeIndexedWorkload();
    testHundredThousandAppendsAndReads();
    System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
    if (failures > 0) System.exit(1);
}
}
