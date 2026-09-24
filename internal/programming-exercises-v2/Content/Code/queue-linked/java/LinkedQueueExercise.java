public class LinkedQueueExercise {
    static int failures = 0;
    static class IntQueue {
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

        public IntQueue() {
            head = null;
            tail = null;
            count = 0;
        }

        public boolean isEmpty() {
            // TODO
            return false;
        }

        public void enqueue(int value) {
            // TODO
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

        public boolean hasValidStructureForTesting() {
            if ((head == null) != (tail == null)) return false;
            if (tail != null && tail.next != null) return false;
            Node slow = head, fast = head;
            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
                if (slow == fast) return false;
            }
            int reachable = 0;
            Node last = null;
            for (Node node = head; node != null; node = node.next) {
                last = node;
                reachable++;
            }
            return reachable == count && last == tail;
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
        IntQueue queue = new IntQueue();

        check(queue.isEmpty(), true);
        check(queue.size(), 0);
        check(queue.dequeue(), -1);
        check(queue.front(), -1);

        queue.enqueue(4);
        check(queue.isEmpty(), false);
        check(queue.size(), 1);
        check(queue.front(), 4);

        queue.enqueue(7);
        queue.enqueue(9);
        check(queue.size(), 3);
        check(queue.front(), 4);
        check(queue.hasValidStructureForTesting(), true);

        check(queue.dequeue(), 4);
        check(queue.front(), 7);
        check(queue.size(), 2);

        queue.enqueue(2);
        check(queue.dequeue(), 7);
        check(queue.dequeue(), 9);
        check(queue.dequeue(), 2);
        check(queue.isEmpty(), true);
        check(queue.size(), 0);
        check(queue.hasValidStructureForTesting(), true);

        check(queue.dequeue(), -1);
        check(queue.front(), -1);

        queue.enqueue(6);
        check(queue.isEmpty(), false);
        check(queue.front(), 6);
        check(queue.dequeue(), 6);
        check(queue.isEmpty(), true);

        IntQueue large = new IntQueue();
        boolean largeOk = true;
        for (int i = 0; i < 5000; i++) large.enqueue(i);
        largeOk &= large.size() == 5000 && large.front() == 0;
        for (int i = 0; i < 5000; i++) largeOk &= large.dequeue() == i;
        check(largeOk && large.isEmpty(), true);
    }

    static void testLongRunsAndSingletonReuse() {
        IntQueue queue = new IntQueue();
        for (int round = 0; round < 50; round++) {
            queue.enqueue(round);
            check(queue.front(), round);
            check(queue.dequeue(), round);
            check(queue.isEmpty(), true);
        }
        for (int i = 0; i < 1000; i++) queue.enqueue(i);
        for (int i = 0; i < 1000; i++) {
            check(queue.front(), i);
            check(queue.dequeue(), i);
            check(queue.size(), 999 - i);
        }
        queue.enqueue(77);
        check(queue.dequeue(), 77);
        check(queue.isEmpty(), true);
    }

    static void testLargeAggregateWorkload() {
        final int n = 100_000;
        IntQueue queue = new IntQueue();
        for (int i = 0; i < n; i++) queue.enqueue(i);
        boolean ok = queue.size() == n && queue.front() == 0
                && queue.hasValidStructureForTesting();
        for (int i = 0; i < n; i++) ok &= queue.dequeue() == i;
        ok &= queue.isEmpty() && queue.hasValidStructureForTesting();
        check(ok, true);
    }

    public static void main(String[] args) {
        testQueue();
        testLongRunsAndSingletonReuse();
        testLargeAggregateWorkload();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
