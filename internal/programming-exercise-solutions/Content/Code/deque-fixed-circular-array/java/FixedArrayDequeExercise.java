public class FixedArrayDequeExercise {
    static int failures = 0;
static class IntDeque {
    private int[] A;
    private int front;
    private int count;

    public IntDeque(int capacity) {
        A = new int[capacity];
        front = 0;
        count = 0;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == A.length;
    }

    public int size() {
        return count;
    }

    public void clear() {
        front = 0;
        count = 0;
    }

    private int backIndex() {
        return (front + count - 1) % A.length;
    }

    public int peekFront() {
        return isEmpty() ? -1 : A[front];
    }

    public int peekBack() {
        return isEmpty() ? -1 : A[backIndex()];
    }

    public boolean addFront(int value) {
        if (isFull()) return false;
        front = (front - 1 + A.length) % A.length;
        A[front] = value;
        count++;
        return true;
    }

    public boolean addBack(int value) {
        if (isFull()) return false;
        int insertionIndex = (front + count) % A.length;
        A[insertionIndex] = value;
        count++;
        return true;
    }

    public int removeFront() {
        if (isEmpty()) return -1;
        int value = A[front];
        front = (front + 1) % A.length;
        count--;
        return value;
    }

    public int removeBack() {
        if (isEmpty()) return -1;
        int value = A[backIndex()];
        count--;
        return value;
    }

    public int[] storageStateForTesting() {
        return new int[] {front, count, A.length};
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

static void testDeque() {
    IntDeque deque = new IntDeque(5);

    check(deque.isEmpty(), true);
    check(deque.isFull(), false);
    check(deque.size(), 0);
    check(deque.peekFront(), -1);
    check(deque.peekBack(), -1);
    check(deque.removeFront(), -1);
    check(deque.removeBack(), -1);

    check(deque.addBack(4), true);       // [4]
    check(deque.peekFront(), 4);
    check(deque.peekBack(), 4);
    check(deque.size(), 1);

    check(deque.addBack(7), true);       // [4, 7]
    check(deque.addFront(2), true);      // [2, 4, 7]
    check(deque.addBack(9), true);       // [2, 4, 7, 9]
    check(java.util.Arrays.equals(deque.storageStateForTesting(), new int[]{4, 4, 5}), true);

    check(deque.size(), 4);
    check(deque.isEmpty(), false);
    check(deque.peekFront(), 2);
    check(deque.peekBack(), 9);

    check(deque.removeFront(), 2);       // [4, 7, 9]
    check(deque.peekFront(), 4);
    check(deque.removeBack(), 9);        // [4, 7]
    check(deque.peekBack(), 7);
    check(deque.size(), 2);

    check(deque.addFront(1), true);      // [1, 4, 7]
    check(deque.addFront(0), true);      // [0, 1, 4, 7]
    check(deque.addBack(11), true);      // [0, 1, 4, 7, 11]
    check(deque.isFull(), true);
    check(deque.size(), 5);
    check(deque.peekFront(), 0);
    check(deque.peekBack(), 11);

    check(deque.addBack(13), false);     // full
    check(deque.addFront(13), false);    // full
    check(deque.size(), 5);
    check(deque.peekFront(), 0);
    check(deque.peekBack(), 11);

    check(deque.removeFront(), 0);       // [1, 4, 7, 11]
    check(deque.removeFront(), 1);       // [4, 7, 11]
    check(deque.addBack(13), true);      // [4, 7, 11, 13]
    check(deque.addBack(15), true);      // [4, 7, 11, 13, 15]
    check(java.util.Arrays.equals(deque.storageStateForTesting(), new int[]{0, 5, 5}), true);
    check(deque.isFull(), true);
    check(deque.peekFront(), 4);
    check(deque.peekBack(), 15);

    check(deque.removeBack(), 15);       // [4, 7, 11, 13]
    check(deque.removeBack(), 13);       // [4, 7, 11]
    check(deque.addFront(3), true);      // [3, 4, 7, 11]
    check(deque.addFront(2), true);      // [2, 3, 4, 7, 11]
    check(deque.peekFront(), 2);
    check(deque.peekBack(), 11);

    check(deque.removeFront(), 2);       // [3, 4, 7, 11]
    check(deque.removeBack(), 11);       // [3, 4, 7]
    check(deque.removeFront(), 3);       // [4, 7]
    check(deque.removeBack(), 7);        // [4]
    check(deque.removeBack(), 4);        // []

    check(deque.isEmpty(), true);
    check(deque.size(), 0);
    check(deque.peekFront(), -1);
    check(deque.peekBack(), -1);
    check(deque.removeFront(), -1);
    check(deque.removeBack(), -1);

    check(deque.addFront(6), true);      // [6]
    check(deque.addBack(8), true);       // [6, 8]
    deque.clear();                       // []
    check(deque.isEmpty(), true);
    check(deque.size(), 0);
    check(deque.peekFront(), -1);

    IntDeque large = new IntDeque(257);
    boolean largeOk = true;
    for (int round = 0; round < 20; round++) {
        for (int i = 0; i < 257; i++) largeOk &= large.addBack(round * 257 + i);
        largeOk &= large.isFull() && !large.addFront(-1);
        for (int i = 0; i < 257; i++) largeOk &= large.removeFront() == round * 257 + i;
    }
    check(largeOk && large.isEmpty(), true);
}

static void testCapacitiesWraparoundAndReuse() {
    for (int capacity : new int[] {1, 2, 3, 8, 64}) {
        IntDeque deque = new IntDeque(capacity);
        for (int round = 0; round < 20; round++) {
            for (int i = 0; i < capacity; i++) check(deque.addBack(round * capacity + i), true);
            check(deque.isFull(), true); check(deque.addFront(999999), false);
            for (int i = 0; i < capacity; i++) check(deque.removeFront(), round * capacity + i);
            check(deque.isEmpty(), true);
            for (int i = 0; i < capacity; i++) check(deque.addFront(round * capacity + i), true);
            for (int i = 0; i < capacity; i++) check(deque.removeBack(), round * capacity + i);
            check(deque.isEmpty(), true);
        }
        deque.addBack(7); deque.clear(); check(deque.isEmpty(), true);
        check(deque.addFront(8), true); check(deque.removeBack(), 8);
    }
}

static void testLargeAggregateWraparound() {
    final int capacity = 10_000;
    IntDeque deque = new IntDeque(capacity);
    boolean ok = true;
    for (int round = 0; round < 10; round++) {
        for (int i = 0; i < capacity; i++) ok &= deque.addBack(round * capacity + i);
        for (int i = 0; i < capacity; i++) ok &= deque.removeFront() == round * capacity + i;
    }
    ok &= deque.isEmpty() && deque.size() == 0;
    check(ok, true);
}

public static void main(String[] args) {
    testDeque();
    testCapacitiesWraparoundAndReuse();
    testLargeAggregateWraparound();
    System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
    if (failures > 0) System.exit(1);
}
}
