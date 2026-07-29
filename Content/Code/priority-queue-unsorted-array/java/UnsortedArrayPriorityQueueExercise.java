import java.util.Optional;

public class UnsortedArrayPriorityQueueExercise {
    static final class Entry {
        private final String value;
        private final int priorityKey;
        private final long sequenceNumber;

        Entry(String value, int priorityKey, long sequenceNumber) {
            this.value = value;
            this.priorityKey = priorityKey;
            this.sequenceNumber = sequenceNumber;
        }

        String value() {
            return value;
        }

        int priorityKey() {
            return priorityKey;
        }

        long sequenceNumber() {
            return sequenceNumber;
        }

        @Override
        public String toString() {
            return value + ":" + priorityKey + "#" + sequenceNumber;
        }
    }

    static final class UnsortedArrayMinPriorityQueue {
        private Entry[] entries;
        private int count;
        private long nextSequenceNumber;

        UnsortedArrayMinPriorityQueue(int initialCapacity) {
            if (initialCapacity < 1) {
                throw new IllegalArgumentException("initialCapacity must be positive");
            }
            entries = new Entry[initialCapacity];
            count = 0;
            nextSequenceNumber = 0;
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
            Entry newEntry = new Entry(value, priorityKey, nextSequenceNumber);

            // TODO:
            // 1. Grow the array if count equals entries.length.
            // 2. Append newEntry at entries[count].
            // 3. Increment count and advance the sequence number.
        }

        Optional<Entry> peek() {
            // Empty-queue policy: return Optional.empty().
            // TODO: Find the best index and return that entry without removing it.
            return Optional.empty();
        }

        Optional<Entry> extract() {
            // Empty-queue policy: return Optional.empty().
            // TODO:
            // 1. Find and save the best entry.
            // 2. Move the final used entry into the removed entry's gap.
            // 3. Decrement count and clear the old final array cell.
            // The same steps must work when the best entry is already last.
            return Optional.empty();
        }

        private int findBestIndex() {
            // TODO: Scan entries[0..count) and return the index of the entry
            // selected by isBetter. Call this only when the queue is nonempty.
            return -1;
        }

        private boolean isBetter(Entry first, Entry second) {
            // TODO: Smaller priority keys are better. If keys tie, the entry
            // with the smaller sequence number is better.
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

    private static void testGapFillingAndStability() {
        UnsortedArrayMinPriorityQueue queue =
                new UnsortedArrayMinPriorityQueue(2);

        check(queue.isEmpty(), true, "new queue is empty");
        check(queue.size(), 0, "new queue has size zero");
        checkEmpty(queue.peek(), "peek on empty queue");
        checkEmpty(queue.extract(), "extract on empty queue");

        queue.insert("A", 5);
        queue.insert("B", 1);
        queue.insert("C", 4);
        queue.insert("D", 3);
        queue.insert("E", 1);

        check(queue.size(), 5, "resizing preserves all entries");
        checkEntry(queue.peek(), "B", 1, "peek returns earliest best entry");
        check(queue.size(), 5, "peek does not remove an entry");

        // B is not last, so extracting it must fill an interior gap.
        checkEntry(queue.extract(), "B", 1, "interior-gap extraction");
        checkEntry(queue.extract(), "E", 1, "stable tied-key extraction");
        checkEntry(queue.extract(), "D", 3, "replacement remains searchable");
        checkEntry(queue.extract(), "C", 4, "next extraction");
        checkEntry(queue.extract(), "A", 5, "worst entry is extracted last");

        check(queue.isEmpty(), true, "queue is empty after all extractions");
        check(queue.size(), 0, "size returns to zero");
    }

    private static void testBestEntryAlreadyLast() {
        UnsortedArrayMinPriorityQueue queue =
                new UnsortedArrayMinPriorityQueue(3);
        queue.insert("X", 8);
        queue.insert("Y", 6);
        queue.insert("Z", 2);

        checkEntry(queue.extract(), "Z", 2, "best entry already last");
        check(queue.size(), 2, "last-entry extraction decreases size once");
        checkEntry(queue.extract(), "Y", 6, "remaining entries stay valid");
    }

    public static void main(String[] args) {
        testGapFillingAndStability();
        testBestEntryAlreadyLast();
        System.out.println(failures == 0
                ? "All tests passed."
                : failures + " test(s) failed.");
    }
}
