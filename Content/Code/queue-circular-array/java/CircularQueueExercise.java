public class CircularQueueExercise {
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

    static void testQueue() {
        IntQueue queue = new IntQueue(4);

        check(queue.isEmpty(), true);
        check(queue.isFull(), false);
        check(queue.dequeue(), -1);
        check(queue.front(), -1);

        check(queue.enqueue(4), true);
        check(queue.enqueue(7), true);
        check(queue.enqueue(9), true);

        check(queue.dequeue(), 4);
        check(queue.dequeue(), 7);

        check(queue.enqueue(2), true);
        check(queue.enqueue(5), true);
        check(queue.enqueue(8), true);
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
    }

    public static void main(String[] args) {
        testQueue();
    }
}
