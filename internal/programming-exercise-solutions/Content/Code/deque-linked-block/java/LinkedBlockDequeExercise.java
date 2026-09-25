public class LinkedBlockDequeExercise {
    static int failures = 0;
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
        return count == 0;
    }

    public int size() {
        return count;
    }

    public void clear() {
        firstBlock = null;
        lastBlock = null;
        frontIndex = 0;
        backIndex = 0;
        count = 0;
    }

    public int peekFront() {
        return isEmpty() ? -1 : firstBlock.values[frontIndex];
    }

    public int peekBack() {
        return isEmpty() ? -1 : lastBlock.values[backIndex];
    }

    public boolean addFront(int value) {
        if (isEmpty()) {
            firstBlock = new BlockNode(blockSize);
            lastBlock = firstBlock;
            frontIndex = 0;
            backIndex = 0;
        } else if (frontIndex == 0) {
            BlockNode block = new BlockNode(blockSize);
            block.next = firstBlock;
            firstBlock.prev = block;
            firstBlock = block;
            frontIndex = blockSize - 1;
        } else {
            frontIndex--;
        }
        firstBlock.values[frontIndex] = value;
        count++;
        return true;
    }

    public boolean addBack(int value) {
        if (isEmpty()) {
            firstBlock = new BlockNode(blockSize);
            lastBlock = firstBlock;
            frontIndex = 0;
            backIndex = 0;
        } else if (backIndex == blockSize - 1) {
            BlockNode block = new BlockNode(blockSize);
            block.prev = lastBlock;
            lastBlock.next = block;
            lastBlock = block;
            backIndex = 0;
        } else {
            backIndex++;
        }
        lastBlock.values[backIndex] = value;
        count++;
        return true;
    }

    public int removeFront() {
        if (isEmpty()) return -1;
        int value = firstBlock.values[frontIndex];
        if (count == 1) {
            clear();
        } else {
            count--;
            if (frontIndex == blockSize - 1) {
                firstBlock = firstBlock.next;
                firstBlock.prev = null;
                frontIndex = 0;
            } else {
                frontIndex++;
            }
        }
        return value;
    }

    public int removeBack() {
        if (isEmpty()) return -1;
        int value = lastBlock.values[backIndex];
        if (count == 1) {
            clear();
        } else {
            count--;
            if (backIndex == 0) {
                lastBlock = lastBlock.prev;
                lastBlock.next = null;
                backIndex = blockSize - 1;
            } else {
                backIndex--;
            }
        }
        return value;
    }

    public boolean hasValidStructureForTesting() {
        if (blockSize <= 0 || count < 0) return false;
        if (count == 0) return firstBlock == null && lastBlock == null;
        if (firstBlock == null || lastBlock == null) return false;
        if (firstBlock.prev != null || lastBlock.next != null) return false;
        if (frontIndex < 0 || frontIndex >= blockSize
                || backIndex < 0 || backIndex >= blockSize) return false;
        int blocks = 0;
        BlockNode previous = null;
        for (BlockNode node = firstBlock; node != null; node = node.next) {
            if (node.prev != previous || node.values.length != blockSize
                    || blocks > count + 1) return false;
            previous = node;
            blocks++;
        }
        return previous == lastBlock;
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
    IntDeque deque = new IntDeque(3);
    check(deque.hasValidStructureForTesting(), true);

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
    check(deque.hasValidStructureForTesting(), true);

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
    check(deque.hasValidStructureForTesting(), true);
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

    IntDeque tinyBlocks = new IntDeque(1);
    boolean tinyOk = true;
    for (int i = 0; i < 500; i++) tinyOk &= tinyBlocks.addBack(i);
    for (int i = 0; i < 250; i++) tinyOk &= tinyBlocks.removeFront() == i;
    for (int i = 500; i < 750; i++) tinyOk &= tinyBlocks.addFront(i);
    for (int i = 749; i >= 500; i--) tinyOk &= tinyBlocks.removeFront() == i;
    for (int i = 499; i >= 250; i--) tinyOk &= tinyBlocks.removeBack() == i;
    check(tinyOk && tinyBlocks.isEmpty(), true);
}

static void testBlockBoundariesAndReuse() {
    for (int blockSize : new int[] {1, 2, 3, 4, 5, 8, 32}) {
        IntDeque deque = new IntDeque(blockSize);
        int count = blockSize * 20 + 3;
        for (int i = 0; i < count; i++) check(deque.addBack(i), true);
        for (int i = 0; i < count; i++) check(deque.removeFront(), i);
        check(deque.isEmpty(), true); check(deque.size(), 0);
        for (int i = 0; i < count; i++) check(deque.addFront(i), true);
        for (int i = 0; i < count; i++) check(deque.removeBack(), i);
        check(deque.isEmpty(), true);
        for (int i = 0; i < count; i++) {
            check(deque.addBack(10000 + i), true);
            check(deque.removeFront(), 10000 + i);
        }
        deque.addFront(7); deque.clear(); check(deque.isEmpty(), true);
        check(deque.peekFront(), -1); check(deque.peekBack(), -1);
    }
}

static void testLargeAggregateWorkload() {
    final int n = 100_000;
    IntDeque deque = new IntDeque(64);
    boolean ok = true;
    for (int i = 0; i < n; i++) ok &= deque.addBack(i);
    ok &= deque.size() == n && deque.peekFront() == 0
            && deque.peekBack() == n - 1 && deque.hasValidStructureForTesting();
    for (int i = 0; i < n; i++) ok &= deque.removeFront() == i;
    ok &= deque.isEmpty() && deque.hasValidStructureForTesting();
    check(ok, true);
}

public static void main(String[] args) {
    testDeque();
    testBlockBoundariesAndReuse();
    testLargeAggregateWorkload();
    System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
    if (failures > 0) System.exit(1);
}
}
