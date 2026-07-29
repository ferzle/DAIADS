public class DoublyLinkedDequeExercise {
static class IntDeque {
    private static class Node {
        int value;
        Node next;
        Node prev;

        Node(int value) {
            this.value = value;
            this.next = null;
            this.prev = null;
        }
    }

    private Node front;
    private Node back;
    private int count;

    public IntDeque() {
        front = null;
        back = null;
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

    public int peekFront() {
        // TODO
        return -1;
    }

    public int peekBack() {
        // TODO
        return -1;
    }

    public boolean addFront(int value) {
        // TODO
        return false;
    }

    public boolean addBack(int value) {
        // TODO
        return false;
    }

    public int removeFront() {
        // TODO
        return -1;
    }

    public int removeBack() {
        // TODO
        return -1;
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

static void testDeque() {
    IntDeque deque = new IntDeque();

    check(deque.isEmpty(), true);
    check(deque.size(), 0);
    check(deque.peekFront(), -1);
    check(deque.peekBack(), -1);
    check(deque.removeFront(), -1);
    check(deque.removeBack(), -1);

    check(deque.addBack(4), true);       // [4]
    check(deque.peekFront(), 4);
    check(deque.peekBack(), 4);
    check(deque.size(), 1);
    check(deque.isEmpty(), false);

    check(deque.addBack(7), true);       // [4, 7]
    check(deque.addFront(2), true);      // [2, 4, 7]
    check(deque.addBack(9), true);       // [2, 4, 7, 9]

    check(deque.size(), 4);
    check(deque.peekFront(), 2);
    check(deque.peekBack(), 9);

    check(deque.removeFront(), 2);       // [4, 7, 9]
    check(deque.peekFront(), 4);
    check(deque.peekBack(), 9);
    check(deque.size(), 3);

    check(deque.removeBack(), 9);        // [4, 7]
    check(deque.peekFront(), 4);
    check(deque.peekBack(), 7);
    check(deque.size(), 2);

    check(deque.addFront(1), true);      // [1, 4, 7]
    check(deque.addBack(11), true);      // [1, 4, 7, 11]
    check(deque.addFront(0), true);      // [0, 1, 4, 7, 11]

    check(deque.size(), 5);
    check(deque.peekFront(), 0);
    check(deque.peekBack(), 11);

    check(deque.removeBack(), 11);       // [0, 1, 4, 7]
    check(deque.removeFront(), 0);       // [1, 4, 7]
    check(deque.removeBack(), 7);        // [1, 4]
    check(deque.removeFront(), 1);       // [4]

    check(deque.size(), 1);
    check(deque.peekFront(), 4);
    check(deque.peekBack(), 4);

    check(deque.removeBack(), 4);        // []
    check(deque.isEmpty(), true);
    check(deque.size(), 0);
    check(deque.peekFront(), -1);
    check(deque.peekBack(), -1);
    check(deque.removeFront(), -1);
    check(deque.removeBack(), -1);

    check(deque.addFront(6), true);      // [6]
    check(deque.peekFront(), 6);
    check(deque.peekBack(), 6);

    check(deque.addBack(8), true);       // [6, 8]
    check(deque.peekFront(), 6);
    check(deque.peekBack(), 8);

    deque.clear();                       // []
    check(deque.isEmpty(), true);
    check(deque.size(), 0);
    check(deque.peekFront(), -1);
    check(deque.peekBack(), -1);

    check(deque.addBack(10), true);      // [10]
    check(deque.removeFront(), 10);      // []
    check(deque.isEmpty(), true);
    check(deque.size(), 0);
}

public static void main(String[] args) {
    testDeque();
}
}
