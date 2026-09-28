import java.util.Arrays;

public class HeapConstructionHeapsortExercise {
    static void siftDown(int[] values, int root, int heapSize) {
        /*
         * Restore max-heap order in values[0..heapSize), assuming the left
         * and right subtrees of root are already max-heaps.
         *
         * TODO: Repeatedly compare the root with its existing children,
         * swap with the larger child when necessary, and continue from that
         * child's former position. Do not inspect or modify values at an
         * index greater than or equal to heapSize.
         */
    }

    static void buildMaxHeap(int[] values) {
        /*
         * TODO: Starting with the final internal node, call siftDown once
         * for each internal node in decreasing index order.
         */
    }

    static void heapSort(int[] values) {
        /*
         * TODO:
         * 1. Convert the entire array into a max-heap.
         * 2. Repeatedly swap the root with the final entry in the active
         *    heap, shrink the active heap by one, and sift the new root down.
         * The completed array must be in nondecreasing order.
         */
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

    public static void main(String[] args) {
        testKnownConstruction();
        testActivePrefixBoundary();
        testHeapSort();
        System.out.println(failures == 0
                ? "All tests passed."
                : failures + " test(s) failed.");
    }
}
