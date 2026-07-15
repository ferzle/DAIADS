public class LinkedQueueExercise {
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
    }

    static void check(int actual, int expected) {
        if (actual == expected) {
            System.out.println("pass");
        } else {
            System.out.println("fail: expected " + expected + " but got " + actual);
        }
    }

    static void check(boolean actual, boolean expected) {
        if (actual == expected) {
            System.out.println("pass");
        } else {
            System.out.println("fail: expected " + expected + " but got " + actual);
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

        check(queue.dequeue(), 4);
        check(queue.front(), 7);
        check(queue.size(), 2);

        queue.enqueue(2);
        check(queue.dequeue(), 7);
        check(queue.dequeue(), 9);
        check(queue.dequeue(), 2);
        check(queue.isEmpty(), true);
        check(queue.size(), 0);

        check(queue.dequeue(), -1);
        check(queue.front(), -1);

        queue.enqueue(6);
        check(queue.isEmpty(), false);
        check(queue.front(), 6);
        check(queue.dequeue(), 6);
        check(queue.isEmpty(), true);
    }

    public static void main(String[] args) {
        testQueue();
    }
}
