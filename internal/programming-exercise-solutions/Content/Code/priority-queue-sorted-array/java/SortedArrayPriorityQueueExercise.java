import java.util.Arrays;
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
            return count == 0;
        }

        int size() {
            return count;
        }

        void insert(String value, int priorityKey) {
            Entry newEntry = new Entry(value, priorityKey);

            ensureCapacity();
            int index = 0;
            while (index < count && isBetter(newEntry, entries[index])) index++;
            for (int i = count; i > index; i--) entries[i] = entries[i - 1];
            entries[index] = newEntry;
            count++;
        }

        Optional<Entry> peek() {
            return isEmpty() ? Optional.empty() : Optional.of(entries[count - 1]);
        }

        Optional<Entry> extract() {
            if (isEmpty()) return Optional.empty();
            Entry best = entries[--count];
            entries[count] = null;
            return Optional.of(best);
        }

        private boolean isBetter(Entry first, Entry second) {
            return first.priorityKey() < second.priorityKey();
        }

        private void ensureCapacity() {
            if (count == entries.length) entries = Arrays.copyOf(entries, entries.length * 2);
        }

        String[] entriesForTesting() {
            String[] result = new String[count];
            for (int i = 0; i < count; i++) result[i] = entries[i].toString();
            return result;
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
        check(Arrays.equals(queue.entriesForTesting(),
                new String[]{"C:7", "A:5", "D:4", "E:2", "B:2"}), true,
                "physical order is worst-to-best with stable ties");

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

    private static void testLargeStableDrain() {
        SortedArrayMinPriorityQueue queue = new SortedArrayMinPriorityQueue(1);
        for (int i = 0; i < 500; i++) queue.insert("v" + i, i % 17);
        check(queue.size(), 500, "size after large insertion");
        for (int priority = 0; priority < 17; priority++)
            for (int i = priority; i < 500; i += 17)
                checkEntry(queue.extract(), "v" + i, priority, "stable large extraction " + i);
        check(queue.isEmpty(), true, "empty after large drain"); checkEmpty(queue.extract(), "extract after large drain");
    }

    public static void main(String[] args) {
        testSortedArrayPriorityQueue();
        testLargeStableDrain();
        System.out.println(failures == 0
                ? "All tests passed."
                : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
