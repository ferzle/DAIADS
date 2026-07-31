public class FixedArrayListExercise {
static class IntList {
    private int[] A;
    private int count;

    public IntList(int capacity) {
        A = new int[capacity];
        count = 0;
    }

    public boolean isEmpty() {
        // TODO
        return false;
    }

    public int size() {
        // TODO
        return -1;
    }

    public void clear() {
        // TODO
    }

    public int first() {
        // TODO
        return -1;
    }

    public int last() {
        // TODO
        return -1;
    }

    public int get(int index) {
        // TODO
        return -1;
    }

    public boolean set(int index, int value) {
        // TODO
        return false;
    }

    public boolean addFirst(int value) {
        // TODO
        return false;
    }

    public boolean addLast(int value) {
        // TODO
        return false;
    }

    public boolean insert(int index, int value) {
        // TODO
        return false;
    }

    public int removeFirst() {
        // TODO
        return -1;
    }

    public int removeLast() {
        // TODO
        return -1;
    }

    public int remove(int index) {
        // TODO
        return -1;
    }

    public int indexOf(int value) {
        // TODO
        return -1;
    }

    public boolean contains(int value) {
        // TODO
        return false;
    }

    public boolean delete(int value) {
        // TODO
        return false;
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
        System.out.println("FAIL at " + checkLocation() + ": expected " + expected + " but got " + actual);
    }
}

static void check(boolean actual, boolean expected) {
    if (actual == expected) {
        System.out.println("PASS at " + checkLocation() + ": got " + actual);
    } else {
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

public static void main(String[] args) {
    testList();
}

}
