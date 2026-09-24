public class SentinelStackExercise {
    static int failures = 0;
    static class IntStack {
        private static class Node {
            int value;
            Node next;

            Node(int value, Node next) {
                this.value = value;
                this.next = next;
            }
        }

        private Node sentinel;
        private final Node originalSentinel;
        private int count;

        public IntStack() {
            sentinel = new Node(-1, null); // dummy value, not part of the stack
            originalSentinel = sentinel;
            count = 0;
        }

        public boolean isEmpty() {
            return sentinel.next == null;
        }

        public void push(int value) {
            sentinel.next = new Node(value, sentinel.next);
            count++;
        }

        public int pop() {
            if (isEmpty()) return -1;
            int value = sentinel.next.value;
            sentinel.next = sentinel.next.next;
            count--;
            return value;
        }

        public int peek() {
            return isEmpty() ? -1 : sentinel.next.value;
        }

        public int size() {
            return count;
        }

        public boolean hasValidStructureForTesting() {
            if (sentinel == null || sentinel != originalSentinel) return false;
            Node slow = sentinel.next, fast = sentinel.next;
            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
                if (slow == fast) return false;
            }
            int reachable = 0;
            for (Node node = sentinel.next; node != null; node = node.next) reachable++;
            return reachable == count;
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
            failures++;
            System.out.println("fail at " + checkLocation() + ": expected " + expected + " but got " + actual);
        }
    }

    static void check(boolean actual, boolean expected) {
        if (actual == expected) {
            System.out.println("pass");
        } else {
            failures++;
            System.out.println("fail at " + checkLocation() + ": expected " + expected + " but got " + actual);
        }
    }

    static void testStack() {
        IntStack stack = new IntStack();

        check(stack.isEmpty(), true);
        check(stack.size(), 0);
        check(stack.pop(), -1);
        check(stack.peek(), -1);
        check(stack.size(), 0);

        stack.push(12);
        check(stack.isEmpty(), false);
        check(stack.size(), 1);
        check(stack.peek(), 12);

        stack.push(7);
        check(stack.size(), 2);
        check(stack.peek(), 7);

        stack.push(19);
        check(stack.size(), 3);
        check(stack.peek(), 19);
        check(stack.hasValidStructureForTesting(), true);

        check(stack.pop(), 19);
        check(stack.size(), 2);
        check(stack.peek(), 7);

        check(stack.pop(), 7);
        check(stack.size(), 1);
        check(stack.peek(), 12);

        check(stack.pop(), 12);
        check(stack.size(), 0);
        check(stack.isEmpty(), true);
        check(stack.hasValidStructureForTesting(), true);

        check(stack.pop(), -1);
        check(stack.peek(), -1);
        check(stack.size(), 0);

        stack.push(5);
        check(stack.isEmpty(), false);
        check(stack.size(), 1);
        check(stack.peek(), 5);
        check(stack.pop(), 5);
        check(stack.size(), 0);
        check(stack.isEmpty(), true);
    }

    static void testLongRunsAndRepeatedReuse() {
        IntStack stack = new IntStack();
        for (int round = 0; round < 25; round++) {
            check(stack.isEmpty(), true);
            for (int i = 0; i < 200; i++) {
                stack.push(round * 1000 + i);
                check(stack.size(), i + 1);
                check(stack.peek(), round * 1000 + i);
            }
            for (int i = 199; i >= 0; i--) {
                check(stack.pop(), round * 1000 + i);
                check(stack.size(), i);
            }
            check(stack.pop(), -1);
            check(stack.isEmpty(), true);
        }
    }

    static void testLargeAggregateWorkload() {
        final int n = 100_000;
        IntStack stack = new IntStack();
        for (int i = 0; i < n; i++) stack.push(i);
        boolean ok = stack.size() == n && stack.peek() == n - 1
                && stack.hasValidStructureForTesting();
        for (int i = n - 1; i >= 0; i--) ok &= stack.pop() == i;
        ok &= stack.isEmpty() && stack.hasValidStructureForTesting();
        check(ok, true);
    }

    public static void main(String[] args) {
        testStack();
        testLongRunsAndRepeatedReuse();
        testLargeAggregateWorkload();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
