public class DoublyLinkedListExercise {
    static class IntDoublyList {
        private static class Node {
            int value;
            Node next;
            Node prev;

            Node(int value) {
                this.value = value;
                this.next = null;
                this.prev = null;
            }
        }

        private Node head;
        private Node tail;
        private int count;

        public IntDoublyList() {
            head = null;
            tail = null;
            count = 0;
        }

        public boolean isEmpty() {
            return count == 0;
        }

        public int size() {
            return count;
        }

        public void insertAtHead(int value) {
            Node newNode = new Node(value);

            if (isEmpty()) {
                head = newNode;
                tail = newNode;
            } else {
                newNode.next = head;
                head.prev = newNode;
                head = newNode;
            }

            count++;
        }

        public void insertAtTail(int value) {
            // TODO:
            // Create a new node.
            // If the list is empty, head and tail should both point to it.
            // Otherwise:
            //   old tail's next should point to the new node
            //   new node's prev should point to the old tail
            //   tail should move to the new node
            // Do not forget to update count.
        }

        public int deleteFromHead() {
            if (isEmpty()) {
                return -1;
            }

            int value = head.value;

            if (head == tail) {
                head = null;
                tail = null;
            } else {
                head = head.next;
                head.prev = null;
            }

            count--;
            return value;
        }

        public int deleteFromTail() {
            // TODO:
            // If the list is empty, return -1.
            // Save tail.value.
            // If there is one node, set both head and tail to null.
            // Otherwise:
            //   move tail to tail.prev
            //   set tail.next to null
            // Decrease count and return the saved value.
            return -1;
        }

        public String traverseForward() {
            // TODO:
            // Return values from head to tail, such as "4 -> 7 -> 9".
            // Return "" for an empty list.
            return "";
        }

        public String traverseBackward() {
            // TODO:
            // Return values from tail to head, such as "9 -> 7 -> 4".
            // Return "" for an empty list.
            return "";
        }

        public Node searchForward(int value) {
            // Optional -- time permitting.
            // Return the first node containing value, or null if not found.
            return null;
        }

        public void insertAfter(Node node, int value) {
            // Optional -- time permitting.
            // If node is null, do nothing.
            // If node is tail, this should behave like insertAtTail(value).
            // Otherwise, insert the new node between node and node.next.
        }

        public void insertBefore(Node node, int value) {
            // Optional -- time permitting.
            // If node is null, do nothing.
            // If node is head, this should behave like insertAtHead(value).
            // Otherwise, insert the new node between node.prev and node.
        }

        public int deleteNode(Node node) {
            // Optional -- time permitting.
            // If node is null, return -1.
            // If node is head, use deleteFromHead().
            // If node is tail, use deleteFromTail().
            // Otherwise, unlink node from both directions and return its value.
            return -1;
        }
    }

    static void check(String actual, String expected) {
        if (actual.equals(expected)) {
            System.out.println("pass");
        } else {
            System.out.println("fail: expected \"" + expected + "\" but got \"" + actual + "\"");
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

    static void testDoublyList() {
        IntDoublyList list = new IntDoublyList();

        check(list.isEmpty(), true);
        check(list.size(), 0);
        check(list.deleteFromHead(), -1);
        check(list.deleteFromTail(), -1);
        check(list.traverseForward(), "");
        check(list.traverseBackward(), "");

        list.insertAtHead(7);
        check(list.traverseForward(), "7");
        check(list.traverseBackward(), "7");
        check(list.size(), 1);

        list.insertAtHead(4);
        check(list.traverseForward(), "4 -> 7");
        check(list.traverseBackward(), "7 -> 4");

        list.insertAtTail(9);
        check(list.traverseForward(), "4 -> 7 -> 9");
        check(list.traverseBackward(), "9 -> 7 -> 4");
        check(list.size(), 3);

        check(list.deleteFromHead(), 4);
        check(list.traverseForward(), "7 -> 9");
        check(list.traverseBackward(), "9 -> 7");

        check(list.deleteFromTail(), 9);
        check(list.traverseForward(), "7");
        check(list.traverseBackward(), "7");

        check(list.deleteFromTail(), 7);
        check(list.traverseForward(), "");
        check(list.traverseBackward(), "");
        check(list.isEmpty(), true);
        check(list.size(), 0);

        list.insertAtTail(12);
        check(list.traverseForward(), "12");
        check(list.traverseBackward(), "12");
        check(list.deleteFromHead(), 12);
        check(list.isEmpty(), true);
    }

    public static void main(String[] args) {
        testDoublyList();
    }
}
