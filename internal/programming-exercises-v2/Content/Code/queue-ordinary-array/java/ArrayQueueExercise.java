public class ArrayQueueExercise {
    static int failures = 0;
    static class IntQueue {
        private int[] A;
        private int count;

        public IntQueue(int capacity) {
            A = new int[capacity];
            count = 0;
        }

        public boolean isEmpty() {
            // TODO
            return false;
        }

        public boolean isFull() {
            // TODO
            return false;
        }

        public boolean enqueue(int value) {
            // TODO
            return false;
        }

        public int dequeue() {
            // TODO
            return -1;
        }

        public int front() {
            // TODO
            return -1;
        }

        public int size() {
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

    static void testQueue() {
        IntQueue queue = new IntQueue(3);

        check(queue.isEmpty(), true);
        check(queue.isFull(), false);
        check(queue.size(), 0);
        check(queue.dequeue(), -1);
        check(queue.front(), -1);

        check(queue.enqueue(4), true);
        check(queue.enqueue(7), true);
        check(queue.enqueue(9), true);
        check(queue.isFull(), true);
        check(queue.enqueue(2), false);

        check(queue.front(), 4);
        check(queue.dequeue(), 4);
        check(queue.front(), 7);
        check(queue.size(), 2);

        check(queue.enqueue(2), true);
        check(queue.dequeue(), 7);
        check(queue.dequeue(), 9);
        check(queue.dequeue(), 2);

        check(queue.isEmpty(), true);
        check(queue.size(), 0);
        check(queue.dequeue(), -1);

        IntQueue large = new IntQueue(1024);
        boolean largeOk = true;
        for (int i = 0; i < 1024; i++) largeOk &= large.enqueue(i);
        largeOk &= large.isFull() && !large.enqueue(1024);
        for (int i = 0; i < 1024; i++) largeOk &= large.dequeue() == i;
        check(largeOk && large.isEmpty(), true);
    }

    static void testCapacityBoundariesAndExhaustion() {
        for (int capacity : new int[] {1, 2, 5, 64}) {
            IntQueue queue = new IntQueue(capacity);
            for (int i = 0; i < capacity; i++) {
                check(queue.enqueue(1000 + i), true);
                check(queue.front(), 1000);
                check(queue.size(), i + 1);
            }
            check(queue.isFull(), true);
            check(queue.enqueue(9999), false);
            for (int i = 0; i < capacity; i++) {
                check(queue.dequeue(), 1000 + i);
                check(queue.size(), capacity - i - 1);
            }
            check(queue.isEmpty(), true);
            // This intentionally ordinary array queue does not reclaim its removed prefix.
            check(queue.enqueue(77), false);
        }
    }

    public static void main(String[] args) {
        testQueue();
        testCapacityBoundariesAndExhaustion();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
