public class LinkedSequenceActivity {
    public static class Node {
        int data;
        Node next;

        Node(int data, Node next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node head;

    public void insertAtHead(int value) {
        head = new Node(value, head);
    }

    public int deleteAtHead() {
        if (head == null) throw new IllegalStateException("list is empty");
        int value = head.data;
        head = head.next;
        return value;
    }

    public void insertAfter(Node node, int value) {
        if (node == null) throw new IllegalArgumentException("node must not be null");
        node.next = new Node(value, node.next);
    }

    public int deleteAfter(Node node) {
        if (node == null || node.next == null) {
            throw new IllegalArgumentException("node must have a successor");
        }
        int value = node.next.data;
        node.next = node.next.next;
        return value;
    }

    public Node search(int value) {
        for (Node node = head; node != null; node = node.next) {
            if (node.data == value) return node;
        }
        return null;
    }

    public String traverse() {
        StringBuilder result = new StringBuilder();
        for (Node node = head; node != null; node = node.next) {
            if (result.length() > 0) result.append(" -> ");
            result.append(node.data);
        }
        return result.toString();
    }

    private static void check(String actual, String expected) {
        if (!actual.equals(expected)) {
            throw new RuntimeException("Expected \"" + expected + "\" but got \"" + actual + "\"");
        }
    }

    private static void check(int actual, int expected) {
        if (actual != expected) {
            throw new RuntimeException("Expected " + expected + " but got " + actual);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }

    private static void expectException(Runnable action) {
        try {
            action.run();
            throw new RuntimeException("Expected an exception, but none was thrown");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            // expected
        }
    }

    private static void testLargeMixedSequence() {
        LinkedSequenceActivity list = new LinkedSequenceActivity();
        final int count = 100_000;
        for (int i = 0; i < count; i++) list.insertAtHead(i);
        Node tail = list.search(0);
        check(tail != null && tail.data == 0, "search should find the tail after traversing 100,000 nodes");
        boolean ok = true;
        for (int expected = count - 1; expected >= 0; expected--) ok &= list.deleteAtHead() == expected;
        check(ok, "100,000 head deletions should preserve reverse insertion order");
        check(list.traverse(), "");
        expectException(() -> list.deleteAtHead());
    }

    public static void main(String[] args) {
        LinkedSequenceActivity list = new LinkedSequenceActivity();

        check(list.traverse(), "");

        list.insertAtHead(4);
        check(list.traverse(), "4");

        list.insertAtHead(9);
        check(list.traverse(), "9 -> 4");

        list.insertAtHead(2);
        check(list.traverse(), "2 -> 9 -> 4");

        Node node9 = list.search(9);
        check(node9 != null, "search(9) should find a node");
        check(node9.data, 9);

        list.insertAfter(node9, 7);
        check(list.traverse(), "2 -> 9 -> 7 -> 4");

        check(list.deleteAfter(node9), 7);
        check(list.traverse(), "2 -> 9 -> 4");

        check(list.deleteAtHead(), 2);
        check(list.traverse(), "9 -> 4");

        check(list.search(20) == null, "search(20) should return null");

        Node node4 = list.search(4);
        expectException(() -> list.deleteAfter(node4));
        expectException(() -> list.insertAfter(null, 8));
        expectException(() -> list.deleteAfter(null));

        check(list.deleteAtHead(), 9);
        check(list.deleteAtHead(), 4);
        check(list.traverse(), "");
        expectException(() -> list.deleteAtHead());

        testLargeMixedSequence();

        System.out.println("All tests passed.");
    }
}
