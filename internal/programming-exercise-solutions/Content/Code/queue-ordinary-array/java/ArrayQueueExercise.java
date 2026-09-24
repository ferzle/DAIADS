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
            return count == 0;
        }

        public boolean isFull() {
            return count == A.length;
        }

        public boolean enqueue(int value) {
            if (isFull()) return false;
            A[count++] = value;
            return true;
        }

        public int dequeue() {
            if (isEmpty()) return -1;
            int value = A[0];
            for (int i = 1; i < count; i++) A[i - 1] = A[i];
            count--;
            return value;
        }

        public int front() {
            return isEmpty() ? -1 : A[0];
        }

        public int size() {
            return count;
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
        check(java.util.Arrays.equals(queue.usedValuesForTesting(), new int[]{7, 9}), true);
        check(queue.front(), 7);
        check(queue.size(), 2);

        check(queue.enqueue(2), true);
        check(java.util.Arrays.equals(queue.usedValuesForTesting(), new int[]{7, 9, 2}), true);
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
            check(queue.enqueue(77), true);
            check(queue.dequeue(), 77);
        }
    }

    public static void main(String[] args) {
        testQueue();
        testCapacityBoundariesAndExhaustion();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
