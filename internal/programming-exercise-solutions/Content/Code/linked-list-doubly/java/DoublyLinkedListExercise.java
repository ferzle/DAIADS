public class DoublyLinkedListExercise {
    static int failures = 0;
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
            Node newNode = new Node(value);
            if (isEmpty()) head = newNode;
            else {
                tail.next = newNode;
                newNode.prev = tail;
            }
            tail = newNode;
            count++;
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
            if (isEmpty()) return -1;
            int value = tail.value;
            if (head == tail) head = tail = null;
            else {
                tail = tail.prev;
                tail.next = null;
            }
            count--;
            return value;
        }

        public String traverseForward() {
            StringBuilder result = new StringBuilder();
            for (Node node = head; node != null; node = node.next) {
                if (result.length() > 0) result.append(" -> ");
                result.append(node.value);
            }
            return result.toString();
        }

        public String traverseBackward() {
            StringBuilder result = new StringBuilder();
            for (Node node = tail; node != null; node = node.prev) {
                if (result.length() > 0) result.append(" -> ");
                result.append(node.value);
            }
            return result.toString();
        }

        public Node searchForward(int value) {
            for (Node node = head; node != null; node = node.next) {
                if (node.value == value) return node;
            }
            return null;
        }

        public void insertAfter(Node node, int value) {
            if (node == null) return;
            if (node == tail) { insertAtTail(value); return; }
            Node newNode = new Node(value);
            newNode.prev = node;
            newNode.next = node.next;
            node.next.prev = newNode;
            node.next = newNode;
            count++;
        }

        public void insertBefore(Node node, int value) {
            if (node == null) return;
            if (node == head) { insertAtHead(value); return; }
            Node newNode = new Node(value);
            newNode.prev = node.prev;
            newNode.next = node;
            node.prev.next = newNode;
            node.prev = newNode;
            count++;
        }

        public int deleteNode(Node node) {
            if (node == null) return -1;
            if (node == head) return deleteFromHead();
            if (node == tail) return deleteFromTail();
            node.prev.next = node.next;
            node.next.prev = node.prev;
            count--;
            return node.value;
        }
    }

    static String checkLocation() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        return caller.getMethodName() + "(), line " + caller.getLineNumber();
    }

    static void check(String actual, String expected) {
        if (actual.equals(expected)) {
            System.out.println("PASS at " + checkLocation() + ": got \"" + actual + "\"");
        } else {
            failures++;
            System.out.println("FAIL at " + checkLocation() + ": expected \"" + expected + "\" but got \"" + actual + "\"");
        }
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

        list.insertAtTail(4);
        list.insertAtTail(7);
        list.insertAtTail(9);
        IntDoublyList.Node node7 = list.searchForward(7);
        check(node7 != null, true);
        check(list.searchForward(99) == null, true);
        list.insertBefore(node7, 6);
        list.insertAfter(node7, 8);
        check(list.traverseForward(), "4 -> 6 -> 7 -> 8 -> 9");
        check(list.traverseBackward(), "9 -> 8 -> 7 -> 6 -> 4");
        check(list.deleteNode(node7), 7);
        check(list.traverseForward(), "4 -> 6 -> 8 -> 9");
        check(list.deleteNode(null), -1);
        check(list.size(), 4);
    }

    static void testLargeDrainAndEndpointHelpers() {
        IntDoublyList list = new IntDoublyList();
        final int half = 50_000;
        boolean ok = true;
        for (int i = 0; i < half; i++) list.insertAtTail(i);
        ok &= list.size() == half;
        for (int i = 0; i < half; i++) ok &= list.deleteFromHead() == i;
        for (int i = 0; i < half; i++) list.insertAtHead(i);
        ok &= list.size() == half;
        for (int i = 0; i < half; i++) ok &= list.deleteFromTail() == i;
        check(ok, true);
        check(list.isEmpty(), true); check(list.traverseForward(), ""); check(list.traverseBackward(), "");
        list.insertAtTail(7); list.insertAtTail(7); list.insertAtTail(7);
        IntDoublyList.Node first = list.searchForward(7);
        list.insertBefore(first, 6); list.insertAfter(first, 8);
        check(list.traverseForward(), "6 -> 7 -> 8 -> 7 -> 7");
        check(list.traverseBackward(), "7 -> 7 -> 8 -> 7 -> 6");
    }

    public static void main(String[] args) {
        testDoublyList();
        testLargeDrainAndEndpointHelpers();
        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
