public class LinkedStackExercise {
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

        private Node head;
        private int count;

        public IntStack() {
            head = null;
            count = 0;
        }

        public boolean isEmpty() {
            // TODO
            return false;
        }

        public void push(int value) {
            // TODO
        }

        public int pop() {
            // TODO
            return -1;
        }

        public int peek() {
            // TODO
            return -1;
        }

        public int size() {
            // TODO
            return -1;
        }

        public boolean hasValidStructureForTesting() {
            Node slow = head, fast = head;
            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
                if (slow == fast) return false;
            }
            int reachable = 0;
            for (Node node = head; node != null; node = node.next) reachable++;
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

        stack.push(4);
        check(stack.isEmpty(), false);
        check(stack.size(), 1);
        check(stack.peek(), 4);
        check(stack.size(), 1);

        stack.push(7);
        check(stack.size(), 2);
        check(stack.peek(), 7);

        stack.push(9);
        check(stack.size(), 3);
        check(stack.peek(), 9);
        check(stack.hasValidStructureForTesting(), true);

        check(stack.pop(), 9);
        check(stack.size(), 2);
        check(stack.peek(), 7);

        stack.push(2);
        check(stack.size(), 3);
        check(stack.peek(), 2);

        check(stack.pop(), 2);
        check(stack.pop(), 7);
        check(stack.pop(), 4);
        check(stack.size(), 0);
        check(stack.isEmpty(), true);
        check(stack.hasValidStructureForTesting(), true);

        check(stack.pop(), -1);
        check(stack.peek(), -1);
        check(stack.size(), 0);

        stack.push(6);
        check(stack.isEmpty(), false);
        check(stack.size(), 1);
        check(stack.peek(), 6);
        check(stack.pop(), 6);
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

    public static void main(String[] args) {
        testStack();
        testLongRunsAndRepeatedReuse();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
