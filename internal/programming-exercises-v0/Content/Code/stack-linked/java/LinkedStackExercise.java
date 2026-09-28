public class LinkedStackExercise {
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

    public static void main(String[] args) {
        testStack();
    }
}
