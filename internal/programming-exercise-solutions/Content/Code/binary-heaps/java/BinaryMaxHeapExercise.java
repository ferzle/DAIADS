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
            return count == 0;
        }

        boolean isFull() {
            return count == values.length;
        }

        int size() {
            return count;
        }

        private int parent(int index) {
            return (index - 1) / 2;
        }

        private int leftChild(int index) {
            return 2 * index + 1;
        }

        private int rightChild(int index) {
            return 2 * index + 2;
        }

        boolean insert(int key) {
            if (isFull()) return false;
            values[count] = key;
            siftUp(count++);
            return true;
        }

        OptionalInt peekMax() {
            return isEmpty() ? OptionalInt.empty() : OptionalInt.of(values[0]);
        }

        OptionalInt extractMax() {
            if (isEmpty()) return OptionalInt.empty();
            int maximum = values[0];
            values[0] = values[--count];
            if (!isEmpty()) siftDown(0);
            return OptionalInt.of(maximum);
        }

        private void siftUp(int index) {
            while (index > 0 && values[index] > values[parent(index)]) {
                int parentIndex = parent(index);
                swap(index, parentIndex);
                index = parentIndex;
            }
        }

        private void siftDown(int index) {
            while (leftChild(index) < count) {
                int largerChild = leftChild(index);
                int right = rightChild(index);
                if (right < count && values[right] > values[largerChild]) largerChild = right;
                if (values[largerChild] <= values[index]) return;
                swap(index, largerChild);
                index = largerChild;
            }
        }

        private void swap(int first, int second) {
            int temporary = values[first];
            values[first] = values[second];
            values[second] = temporary;
        }

        boolean hasValidHeapOrder() {
            for (int child = 1; child < count; child++) {
                if (values[parent(child)] < values[child]) return false;
            }
            return true;
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

    private static void testLargeDeterministicDrain() {
        int[] expected = new int[1000];
        long state = 0x5EEDL;
        FixedCapacityMaxHeap heap = new FixedCapacityMaxHeap(expected.length);
        for (int i = 0; i < expected.length; i++) {
            state = (state * 1103515245 + 12345) & 0x7fffffffL;
            expected[i] = (int) (state % 2001) - 1000;
            check(heap.insert(expected[i]), true, "large insert " + i);
            check(heap.hasValidHeapOrder(), true, "heap order after large insert " + i);
        }
        Arrays.sort(expected);
        for (int i = expected.length - 1; i >= 0; i--) {
            checkOptional(heap.extractMax(), expected[i], "large extraction " + (expected.length - 1 - i));
            check(heap.hasValidHeapOrder(), true, "heap order after large extraction " + i);
        }
        check(heap.isEmpty(), true, "empty after large drain");
        check(heap.insert(42), true, "reuse after large drain"); checkOptional(heap.extractMax(), 42, "extract reused value");
    }

    private static void testHundredThousandAggregateOperations() {
        final int count = 100_000;
        int[] expected = new int[count];
        long state = 0xC0FFEEL;
        FixedCapacityMaxHeap heap = new FixedCapacityMaxHeap(count);
        boolean ok = true;
        for (int i = 0; i < count; i++) {
            state = (state * 1103515245 + 12345) & 0x7fffffffL;
            expected[i] = (int) state;
            ok &= heap.insert(expected[i]);
        }
        Arrays.sort(expected);
        for (int i = count - 1; i >= 0; i--) ok &= heap.extractMax().orElse(Integer.MIN_VALUE) == expected[i];
        check(ok && heap.isEmpty() && heap.hasValidHeapOrder(), true,
                "100,000 aggregate insertions and extractions");
    }

    public static void main(String[] args) {
        testCoreOperations();
        testOnlyLeftChildAndNegativeKeys();
        testLargeDeterministicDrain();
        testHundredThousandAggregateOperations();
        System.out.println(failures == 0
                ? "All tests passed."
                : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
