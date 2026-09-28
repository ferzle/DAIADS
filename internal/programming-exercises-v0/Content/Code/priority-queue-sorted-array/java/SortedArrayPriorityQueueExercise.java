import java.util.Optional;

public class SortedArrayPriorityQueueExercise {
    static final class Entry {
        private final String value;
        private final int priorityKey;

        Entry(String value, int priorityKey) {
            this.value = value;
            this.priorityKey = priorityKey;
        }

        String value() {
            return value;
        }

        int priorityKey() {
            return priorityKey;
        }

        @Override
        public String toString() {
            return value + ":" + priorityKey;
        }
    }

    static final class SortedArrayMinPriorityQueue {
        private Entry[] entries;
        private int count;

        SortedArrayMinPriorityQueue(int initialCapacity) {
            if (initialCapacity < 1) {
                throw new IllegalArgumentException("initialCapacity must be positive");
            }
            entries = new Entry[initialCapacity];
            count = 0;
        }

        boolean isEmpty() {
            // TODO: Return whether the used portion of the array is empty.
            return false;
        }

        int size() {
            // TODO: Return the number of stored entries, not the array capacity.
            return -1;
        }

        void insert(String value, int priorityKey) {
            Entry newEntry = new Entry(value, priorityKey);

            // TODO:
            // 1. Grow the array if count equals entries.length.
            // 2. Find the insertion index that keeps entries[0..count)
            //    ordered from worst to best. Insert a new tied entry on the
            //    left/worse side of all existing entries with the same key.
            // 3. Shift the required suffix one position to the right.
            // 4. Store newEntry and increment count.
        }

        Optional<Entry> peek() {
            // Empty-queue policy: return Optional.empty().
            // TODO: Otherwise return the best entry without removing it.
            return Optional.empty();
        }

        Optional<Entry> extract() {
            // Empty-queue policy: return Optional.empty().
            // TODO: Remove and return the best entry at the used right end.
            // Clear the vacated array cell so it no longer holds a reference.
            return Optional.empty();
        }

        private boolean isBetter(Entry first, Entry second) {
            // TODO: Smaller priority keys are better. Equal keys compare as
            // tied; stable behavior comes from their physical insertion order.
            return false;
        }

        private void ensureCapacity() {
            // TODO: When full, allocate an array with twice the old capacity
            // and copy the count used entries into it.
        }
    }

    private static int failures = 0;

    private static void check(boolean actual, boolean expected, String label) {
        if (actual == expected) {
            System.out.println("pass: " + label);
        } else {
            failures++;
            System.out.println("FAIL: " + label
                    + " (expected " + expected + ", got " + actual + ")");
        }
    }

    private static void check(int actual, int expected, String label) {
        if (actual == expected) {
            System.out.println("pass: " + label);
        } else {
            failures++;
            System.out.println("FAIL: " + label
                    + " (expected " + expected + ", got " + actual + ")");
        }
    }

    private static void checkEntry(
            Optional<Entry> actual, String expectedValue, int expectedKey, String label) {
        if (actual.isPresent()
                && actual.get().value().equals(expectedValue)
                && actual.get().priorityKey() == expectedKey) {
            System.out.println("pass: " + label);
        } else {
            failures++;
            System.out.println("FAIL: " + label + " (expected "
                    + expectedValue + ":" + expectedKey + ", got "
                    + (actual.isPresent() ? actual.get() : "empty") + ")");
        }
    }

    private static void checkEmpty(Optional<Entry> actual, String label) {
        check(actual.isEmpty(), true, label);
    }

    private static void testSortedArrayPriorityQueue() {
        SortedArrayMinPriorityQueue queue = new SortedArrayMinPriorityQueue(2);

        check(queue.isEmpty(), true, "new queue is empty");
        check(queue.size(), 0, "new queue has size zero");
        checkEmpty(queue.peek(), "peek on empty queue");
        checkEmpty(queue.extract(), "extract on empty queue");

        // These insertions require placement at the used beginning, middle,
        // and end, and they force the backing array to resize.
        queue.insert("A", 5);
        queue.insert("B", 2);
        queue.insert("C", 7);
        queue.insert("D", 4);
        queue.insert("E", 2);

        check(queue.size(), 5, "size after five insertions");
        checkEntry(queue.peek(), "B", 2, "peek returns earliest best entry");
        check(queue.size(), 5, "peek does not remove an entry");

        checkEntry(queue.extract(), "B", 2, "first extraction");
        checkEntry(queue.extract(), "E", 2, "stable tied-key extraction");
        checkEntry(queue.extract(), "D", 4, "middle-priority extraction");
        checkEntry(queue.extract(), "A", 5, "next extraction");
        checkEntry(queue.extract(), "C", 7, "worst entry is extracted last");

        check(queue.isEmpty(), true, "queue is empty after all extractions");
        check(queue.size(), 0, "size returns to zero");
        checkEmpty(queue.extract(), "extract remains safe when empty");
    }

    public static void main(String[] args) {
        testSortedArrayPriorityQueue();
        System.out.println(failures == 0
                ? "All tests passed."
                : failures + " test(s) failed.");
    }
}
