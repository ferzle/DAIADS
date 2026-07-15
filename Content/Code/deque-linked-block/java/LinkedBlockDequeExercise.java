public class LinkedBlockDequeExercise {
static class IntDeque {
    private static class BlockNode {
        int[] values;
        BlockNode prev;
        BlockNode next;

        BlockNode(int blockSize) {
            values = new int[blockSize];
            prev = null;
            next = null;
        }
    }

    private BlockNode firstBlock;
    private BlockNode lastBlock;
    private int frontIndex;
    private int backIndex;
    private int count;
    private int blockSize;

    public IntDeque(int blockSize) {
        this.blockSize = blockSize;
        firstBlock = null;
        lastBlock = null;
        frontIndex = 0;
        backIndex = 0;
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

static void check(int actual, int expected) {
    if (actual == expected) {
        System.out.println("pass");
    } else {
        System.out.println("fail: expected " + expected + " but got " + actual);
    }
}

static void check(boolean actual, boolean expected) {
    if (actual == expected) {
        System.out.println("pass");
    } else {
        System.out.println("fail: expected " + expected + " but got " + actual);
    }
}

static void testDeque() {
    IntDeque deque = new IntDeque(3);

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

    check(deque.addBack(7), true);       // [4, 7]
    check(deque.addBack(9), true);       // [4, 7, 9]
    check(deque.addBack(11), true);      // new back block: [4, 7, 9, 11]
    check(deque.peekFront(), 4);
    check(deque.peekBack(), 11);
    check(deque.size(), 4);

    check(deque.addFront(2), true);      // new front block: [2, 4, 7, 9, 11]
    check(deque.addFront(1), true);      // [1, 2, 4, 7, 9, 11]
    check(deque.addFront(0), true);      // [0, 1, 2, 4, 7, 9, 11]
    check(deque.peekFront(), 0);
    check(deque.peekBack(), 11);
    check(deque.size(), 7);

    check(deque.removeFront(), 0);       // [1, 2, 4, 7, 9, 11]
    check(deque.removeFront(), 1);       // [2, 4, 7, 9, 11]
    check(deque.removeFront(), 2);       // first block may be removed: [4, 7, 9, 11]
    check(deque.peekFront(), 4);
    check(deque.peekBack(), 11);
    check(deque.size(), 4);

    check(deque.removeBack(), 11);       // [4, 7, 9]
    check(deque.removeBack(), 9);        // [4, 7]
    check(deque.peekFront(), 4);
    check(deque.peekBack(), 7);
    check(deque.size(), 2);

    check(deque.addFront(3), true);      // [3, 4, 7]
    check(deque.addBack(8), true);       // [3, 4, 7, 8]
    check(deque.addBack(10), true);      // [3, 4, 7, 8, 10]
    check(deque.peekFront(), 3);
    check(deque.peekBack(), 10);

    check(deque.removeBack(), 10);       // [3, 4, 7, 8]
    check(deque.removeFront(), 3);       // [4, 7, 8]
    check(deque.removeBack(), 8);        // [4, 7]
    check(deque.removeFront(), 4);       // [7]
    check(deque.peekFront(), 7);
    check(deque.peekBack(), 7);
    check(deque.size(), 1);

    check(deque.removeBack(), 7);        // []
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
}

public static void main(String[] args) {
    testDeque();
}
}
