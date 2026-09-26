import java.util.Arrays;

public class HeapConstructionHeapsortExercise {
    static void siftDown(int[] values, int root, int heapSize) {
        while (2 * root + 1 < heapSize) {
            int largerChild = 2 * root + 1;
            int rightChild = largerChild + 1;
            if (rightChild < heapSize
                    && values[rightChild] > values[largerChild]) {
                largerChild = rightChild;
            }
            if (values[root] >= values[largerChild]) {
                return;
            }
            swap(values, root, largerChild);
            root = largerChild;
        }
    }

    static void buildMaxHeap(int[] values) {
        for (int root = values.length / 2 - 1; root >= 0; root--) {
            siftDown(values, root, values.length);
        }
    }

    static void heapSort(int[] values) {
        buildMaxHeap(values);
        for (int heapSize = values.length - 1; heapSize > 0; heapSize--) {
            swap(values, 0, heapSize);
            siftDown(values, 0, heapSize);
        }
    }

    private static void swap(int[] values, int first, int second) {
        int temporary = values[first];
        values[first] = values[second];
        values[second] = temporary;
    }

    private static boolean isMaxHeap(int[] values, int heapSize) {
        for (int child = 1; child < heapSize; child++) {
            int parent = (child - 1) / 2;
            if (values[parent] < values[child]) {
                return false;
            }
        }
        return true;
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

    private static void checkArray(
            int[] actual, int[] expected, String label) {
        if (Arrays.equals(actual, expected)) {
            System.out.println("pass: " + label);
        } else {
            failures++;
            System.out.println("FAIL: " + label + " (expected "
                    + Arrays.toString(expected) + ", got "
                    + Arrays.toString(actual) + ")");
        }
    }

    private static void testKnownConstruction() {
        int[] values = {7, 2, 9, 1, 6, 8, 3, 5, 4};
        buildMaxHeap(values);
        checkArray(values, new int[] {9, 6, 8, 5, 2, 7, 3, 1, 4},
                "known bottom-up construction");
        check(isMaxHeap(values, values.length), true,
                "constructed array has max-heap order");

        int[] duplicatesAndNegatives = {-4, 7, 7, -9, 0, 7, -4};
        buildMaxHeap(duplicatesAndNegatives);
        check(isMaxHeap(duplicatesAndNegatives,
                duplicatesAndNegatives.length), true,
                "construction handles duplicates and negative keys");
    }

    private static void testActivePrefixBoundary() {
        int[] values = {2, 9, 8, 7, 6, 5, 1000, 2000};
        siftDown(values, 0, 6);
        checkArray(values, new int[] {9, 7, 8, 2, 6, 5, 1000, 2000},
                "siftDown stays inside the active prefix");
        check(isMaxHeap(values, 6), true,
                "active prefix has max-heap order after siftDown");
    }

    private static void testHeapSort() {
        int[][] inputs = {
            {7, 2, 9, 1, 6, 8, 3, 5, 4},
            {-5, 3, -5, 0, 12, 3, -1},
            {1, 2, 3, 4, 5, 6},
            {6, 5, 4, 3, 2, 1},
            {},
            {42}
        };
        int[][] expected = {
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {-5, -5, -1, 0, 3, 3, 12},
            {1, 2, 3, 4, 5, 6},
            {1, 2, 3, 4, 5, 6},
            {},
            {42}
        };
        String[] labels = {
            "sorts a typical input",
            "sorts duplicate and negative keys",
            "sorts an already sorted input",
            "sorts a reverse-sorted input",
            "sorts an empty input",
            "sorts a one-element input"
        };

        for (int i = 0; i < inputs.length; i++) {
            heapSort(inputs[i]);
            checkArray(inputs[i], expected[i], labels[i]);
        }
    }

    private static void testDeterministicLargeArrays() {
        for (int length : new int[] {0, 1, 2, 3, 31, 32, 33, 1000, 100_000}) {
            int[] values = new int[length];
            long state = 0x5EEDL;
            for (int i = 0; i < length; i++) { state = (state * 1103515245 + 12345) & 0x7fffffffL; values[i] = (int) (state % 101) - 50; }
            int[] expected = Arrays.copyOf(values, values.length); Arrays.sort(expected);
            heapSort(values); checkArray(values, expected, "deterministic heapSort length " + length);
            int[] heap = Arrays.copyOf(expected, expected.length); buildMaxHeap(heap);
            check(isMaxHeap(heap, heap.length), true, "buildMaxHeap length " + length);
        }
    }

    public static void main(String[] args) {
        testKnownConstruction();
        testActivePrefixBoundary();
        testHeapSort();
        testDeterministicLargeArrays();
        System.out.println(failures == 0
                ? "All tests passed."
                : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
