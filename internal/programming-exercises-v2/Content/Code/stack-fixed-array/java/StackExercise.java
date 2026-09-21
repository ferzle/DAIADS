public class StackExercise {
    static int failures = 0;
    static class IntStack {
        private int[] A;
        private int top;

        public IntStack(int capacity) {
            A = new int[capacity];
            top = -1;
        }

        public boolean isEmpty() {
            // TODO
            return false;
        }

        public boolean isFull() {
            // TODO
            return false;
        }

        public boolean push(int value) {
            // TODO
            return false;
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
        IntStack stack = new IntStack(4);

        check(stack.size(), 0);
        check(stack.pop(), -1);
        check(stack.peek(), -1);
        check(stack.size(), 0);

        check(stack.push(4), true);
        check(stack.size(), 1);
        check(stack.peek(), 4);
        check(stack.size(), 1);

        check(stack.push(7), true);
        check(stack.size(), 2);
        check(stack.peek(), 7);

        check(stack.push(9), true);
        check(stack.size(), 3);
        check(stack.peek(), 9);

        check(stack.push(2), true);
        check(stack.size(), 4);
        check(stack.peek(), 2);

        check(stack.push(5), false);
        check(stack.size(), 4);
        check(stack.peek(), 2);

        check(stack.pop(), 2);
        check(stack.size(), 3);

        check(stack.pop(), 9);
        check(stack.size(), 2);

        check(stack.push(6), true);
        check(stack.size(), 3);
        check(stack.peek(), 6);

        check(stack.pop(), 6);
        check(stack.size(), 2);

        check(stack.pop(), 7);
        check(stack.size(), 1);

        check(stack.pop(), 4);
        check(stack.size(), 0);

        check(stack.pop(), -1);
        check(stack.peek(), -1);
        check(stack.size(), 0);
    }

    static void testBoundarySizesAndReuse() {
        for (int capacity : new int[] {1, 2, 5, 32}) {
            IntStack stack = new IntStack(capacity);
            check(stack.isEmpty(), true);
            check(stack.isFull(), false);
            for (int i = 0; i < capacity; i++) {
                check(stack.push(1000 + i), true);
                check(stack.size(), i + 1);
                check(stack.peek(), 1000 + i);
            }
            check(stack.isFull(), true);
            check(stack.push(9999), false);
            check(stack.size(), capacity);
            for (int i = capacity - 1; i >= 0; i--) {
                check(stack.pop(), 1000 + i);
                check(stack.size(), i);
            }
            check(stack.isEmpty(), true);
            check(stack.push(77), true);
            check(stack.pop(), 77);
            check(stack.isEmpty(), true);
        }
    }

    public static void main(String[] args) {
        testStack();
        testBoundarySizesAndReuse();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
