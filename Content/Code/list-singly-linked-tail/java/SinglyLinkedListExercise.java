public class SinglyLinkedListExercise {
static class IntList {
    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int count;

    public IntList() {
        head = null;
        tail = null;
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

    private Node nodeAt(int index) {
        // TODO
        return null;
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
        System.out.println("pass");
    } else {
        System.out.println("fail at " + checkLocation() + ": expected " + expected + " but got " + actual);
    }
}

static void check(boolean actual, boolean expected) {
    if (actual == expected) {
        System.out.println("pass");
    } else {
        System.out.println("fail at " + checkLocation() + ": expected " + expected + " but got " + actual);
    }
}

static void testList() {
    IntList list = new IntList();

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

    check(list.set(1, 5), true);        // [2, 5, 9, 7]
    check(list.get(1), 5);
    check(list.set(4, 8), false);
    check(list.size(), 4);

    check(list.insert(0, 11), true);    // [11, 2, 5, 9, 7]
    check(list.first(), 11);
    check(list.insert(list.size(), 13), true); // [11, 2, 5, 9, 7, 13]
    check(list.last(), 13);
    check(list.size(), 6);

    check(list.indexOf(9), 3);
    check(list.indexOf(100), -1);
    check(list.contains(7), true);
    check(list.contains(100), false);

    check(list.remove(3), 9);           // [11, 2, 5, 7, 13]
    check(list.get(3), 7);
    check(list.removeFirst(), 11);      // [2, 5, 7, 13]
    check(list.removeLast(), 13);       // [2, 5, 7]
    check(list.size(), 3);
    check(list.first(), 2);
    check(list.last(), 7);

    check(list.delete(2), true);        // [5, 7]
    check(list.first(), 5);
    check(list.delete(7), true);        // [5]
    check(list.last(), 5);
    check(list.delete(5), true);        // []
    check(list.isEmpty(), true);
    check(list.size(), 0);
    check(list.first(), -1);
    check(list.last(), -1);

    check(list.delete(5), false);
    check(list.removeLast(), -1);

    check(list.addLast(6), true);       // [6]
    check(list.first(), 6);
    check(list.last(), 6);
    check(list.removeLast(), 6);        // []
    check(list.isEmpty(), true);

    check(list.addFirst(8), true);      // [8]
    check(list.addLast(10), true);      // [8, 10]
    list.clear();                       // []
    check(list.isEmpty(), true);
    check(list.size(), 0);
    check(list.first(), -1);
}

public static void main(String[] args) {
    testList();
}
}
