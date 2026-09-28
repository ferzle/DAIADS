public class SentinelStackExercise {
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
        private int count;

        public IntStack() {
            sentinel = new Node(-1, null); // dummy value, not part of the stack
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

        check(stack.pop(), 19);
        check(stack.size(), 2);
        check(stack.peek(), 7);

        check(stack.pop(), 7);
        check(stack.size(), 1);
        check(stack.peek(), 12);

        check(stack.pop(), 12);
        check(stack.size(), 0);
        check(stack.isEmpty(), true);

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

    public static void main(String[] args) {
        testStack();
    }
}
