import java.util.Arrays;
import java.util.OptionalInt;

public class BinaryMaxHeapExercise {
    static final class FixedCapacityMaxHeap {
        private final int[] values;
        private int count;

        FixedCapacityMaxHeap(int capacity) {
            if (capacity < 1) {
                throw new IllegalArgumentException("capacity must be positive");
            }
            values = new int[capacity];
            count = 0;
        }

        boolean isEmpty() {
            // TODO: Return whether the logical heap contains no keys.
            return false;
        }

        boolean isFull() {
            // TODO: Return whether every position in the backing array is used.
            return false;
        }

        int size() {
            // TODO: Return the number of keys in the logical heap.
            return -1;
        }

        private int parent(int index) {
            // Precondition: index > 0.
            // TODO: Return the zero-based index of this entry's parent.
            return -1;
        }

        private int leftChild(int index) {
            // TODO: Return the zero-based index of this entry's left child.
            return -1;
        }

        private int rightChild(int index) {
            // TODO: Return the zero-based index of this entry's right child.
            return -1;
        }

        boolean insert(int key) {
            // TODO:
            // 1. If the heap is full, return false without changing it.
            // 2. Place key at the end of the used range and increment count.
            // 3. Restore max-heap order with siftUp.
            // 4. Return true.
            return false;
        }

        OptionalInt peekMax() {
            // Empty-heap policy: return OptionalInt.empty().
            // TODO: Otherwise return the root without changing the heap.
            return OptionalInt.empty();
        }

        OptionalInt extractMax() {
            // Empty-heap policy: return OptionalInt.empty().
            // TODO:
            // 1. Save the root.
            // 2. Move the final used key to the root and decrement count.
            // 3. Restore max-heap order with siftDown when the heap is nonempty.
            // 4. Return the saved maximum.
            return OptionalInt.empty();
        }

        private void siftUp(int index) {
            // TODO: While this key is greater than its parent, swap the two
            // keys and continue from the parent's former position.
        }

        private void siftDown(int index) {
            // TODO: While a child exists, select the larger existing child.
            // Swap only when that child is greater than the current key, then
            // continue from the child's former position.
        }

        private void swap(int first, int second) {
            int temporary = values[first];
            values[first] = values[second];
            values[second] = temporary;
        }

        boolean hasValidHeapOrder() {
            // TODO: Check every used parent-child pair. This O(n) method is
            // for testing only; public heap operations must not call it.
            return false;
        }

        int[] usedValuesForTesting() {
            return Arrays.copyOf(values, count);
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

    private static void checkOptional(
            OptionalInt actual, Integer expected, String label) {
        boolean matches = expected == null
                ? actual.isEmpty()
                : actual.isPresent() && actual.getAsInt() == expected;
        if (matches) {
            System.out.println("pass: " + label);
        } else {
            failures++;
            System.out.println("FAIL: " + label + " (expected "
                    + (expected == null ? "empty" : expected) + ", got "
                    + (actual.isPresent() ? actual.getAsInt() : "empty") + ")");
        }
    }

    private static void checkArray(
            FixedCapacityMaxHeap heap, int[] expected, String label) {
        int[] actual = heap.usedValuesForTesting();
        if (Arrays.equals(actual, expected)) {
            System.out.println("pass: " + label);
        } else {
            failures++;
            System.out.println("FAIL: " + label + " (expected "
                    + Arrays.toString(expected) + ", got "
                    + Arrays.toString(actual) + ")");
        }
    }

    private static void testCoreOperations() {
        FixedCapacityMaxHeap heap = new FixedCapacityMaxHeap(7);

        check(heap.isEmpty(), true, "new heap is empty");
        check(heap.isFull(), false, "new heap is not full");
        check(heap.size(), 0, "new heap has size zero");
        checkOptional(heap.peekMax(), null, "peek on empty heap");
        checkOptional(heap.extractMax(), null, "extract on empty heap");

        int[] keys = {40, 70, 30, 90, 60, 80, 80};
        for (int key : keys) {
            check(heap.insert(key), true, "insert " + key);
            check(heap.hasValidHeapOrder(), true,
                    "heap order after inserting " + key);
        }

        checkArray(heap, new int[] {90, 70, 80, 40, 60, 30, 80},
                "array after insertions");
        check(heap.isFull(), true, "heap reports full");
        check(heap.insert(100), false, "insertion fails when full");
        checkArray(heap, new int[] {90, 70, 80, 40, 60, 30, 80},
                "failed insertion leaves heap unchanged");

        checkOptional(heap.peekMax(), 90, "peek returns the maximum");
        check(heap.size(), 7, "peek leaves size unchanged");
        checkOptional(heap.extractMax(), 90, "extract returns the maximum");
        checkArray(heap, new int[] {80, 70, 80, 40, 60, 30},
                "array after extraction");
        check(heap.hasValidHeapOrder(), true, "heap order after extraction");
    }

    private static void testOnlyLeftChildAndNegativeKeys() {
        FixedCapacityMaxHeap heap = new FixedCapacityMaxHeap(5);
        for (int key : new int[] {100, 90, 80, 70, 60}) {
            heap.insert(key);
        }

        checkOptional(heap.extractMax(), 100, "extract before left-only repair");
        checkArray(heap, new int[] {90, 70, 80, 60},
                "siftDown handles an only-left-child step");
        check(heap.hasValidHeapOrder(), true, "left-only result is a heap");

        FixedCapacityMaxHeap negatives = new FixedCapacityMaxHeap(3);
        negatives.insert(-8);
        negatives.insert(-3);
        negatives.insert(-12);
        checkOptional(negatives.extractMax(), -3,
                "maximum is correct for negative keys");
    }

    public static void main(String[] args) {
        testCoreOperations();
        testOnlyLeftChildAndNegativeKeys();
        System.out.println(failures == 0
                ? "All tests passed."
                : failures + " test(s) failed.");
    }
}
