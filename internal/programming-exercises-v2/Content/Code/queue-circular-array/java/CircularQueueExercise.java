public class CircularQueueExercise {
    static int failures = 0;
    static class IntQueue {
        private int[] A;
        private int frontIndex;
        private int count;

        public IntQueue(int capacity) {
            A = new int[capacity];
            frontIndex = 0;
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

        public int[] storageStateForTesting() {
            return new int[] {frontIndex, count, A.length};
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
        IntQueue queue = new IntQueue(4);

        check(queue.isEmpty(), true);
        check(queue.isFull(), false);
        check(queue.dequeue(), -1);
        check(queue.front(), -1);

        check(queue.enqueue(4), true);
        check(queue.enqueue(7), true);
        check(queue.enqueue(9), true);
        check(java.util.Arrays.equals(queue.storageStateForTesting(), new int[]{0, 3, 4}), true);

        check(queue.dequeue(), 4);
        check(queue.dequeue(), 7);
        check(java.util.Arrays.equals(queue.storageStateForTesting(), new int[]{2, 1, 4}), true);

        check(queue.enqueue(2), true);
        check(queue.enqueue(5), true);
        check(queue.enqueue(8), true);
        check(java.util.Arrays.equals(queue.storageStateForTesting(), new int[]{2, 4, 4}), true);
        check(queue.isFull(), true);
        check(queue.enqueue(10), false);

        check(queue.front(), 9);
        check(queue.dequeue(), 9);
        check(queue.dequeue(), 2);
        check(queue.dequeue(), 5);
        check(queue.dequeue(), 8);

        check(queue.isEmpty(), true);
        check(queue.size(), 0);
        check(queue.dequeue(), -1);

        check(queue.enqueue(11), true);
        check(queue.front(), 11);

        IntQueue wrapped = new IntQueue(257);
        boolean wrappedOk = true;
        for (int round = 0; round < 20; round++) {
            for (int i = 0; i < 257; i++) wrappedOk &= wrapped.enqueue(round * 257 + i);
            for (int i = 0; i < 257; i++) wrappedOk &= wrapped.dequeue() == round * 257 + i;
        }
        check(wrappedOk && wrapped.isEmpty(), true);
    }

    static void testRepeatedWraparound() {
        for (int capacity : new int[] {1, 2, 5, 64}) {
            IntQueue queue = new IntQueue(capacity);
            int nextExpected = 0;
            for (int round = 0; round < 20; round++) {
                for (int i = 0; i < capacity; i++)
                    check(queue.enqueue(round * capacity + i), true);
                check(queue.isFull(), true);
                check(queue.enqueue(999999), false);
                for (int i = 0; i < capacity; i++) {
                    check(queue.front(), nextExpected);
                    check(queue.dequeue(), nextExpected++);
                    check(queue.size(), capacity - i - 1);
                }
                check(queue.isEmpty(), true);
            }
        }
    }

    static void testLargeAggregateWraparound() {
        final int capacity = 10_000;
        IntQueue queue = new IntQueue(capacity);
        boolean ok = true;
        for (int round = 0; round < 10; round++) {
            for (int i = 0; i < capacity; i++) ok &= queue.enqueue(round * capacity + i);
            ok &= queue.isFull() && !queue.enqueue(-1);
            for (int i = 0; i < capacity; i++) ok &= queue.dequeue() == round * capacity + i;
        }
        ok &= queue.isEmpty() && queue.size() == 0;
        check(ok, true);
    }

    public static void main(String[] args) {
        testQueue();
        testRepeatedWraparound();
        testLargeAggregateWraparound();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
