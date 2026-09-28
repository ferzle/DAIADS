#include <iostream>
#include <string>
#include <vector>

using namespace std;

void siftDown(vector<int>& values, int root, int heapSize) {
    /*
     * Restore max-heap order in values[0..heapSize), assuming the left and
     * right subtrees of root are already max-heaps.
     *
     * TODO: Repeatedly compare the root with its existing children, swap
     * with the larger child when necessary, and continue from that child's
     * former position. Do not inspect or modify values at an index greater
     * than or equal to heapSize.
     */
    (void) values;
    (void) root;
    (void) heapSize;
}

void buildMaxHeap(vector<int>& values) {
    /*
     * TODO: Starting with the final internal node, call siftDown once for
     * each internal node in decreasing index order.
     */
    (void) values;
}

void heapSort(vector<int>& values) {
    /*
     * TODO:
     * 1. Convert the entire array into a max-heap.
     * 2. Repeatedly swap the root with the final entry in the active heap,
     *    shrink the active heap by one, and sift the new root down.
     * The completed array must be in nondecreasing order.
     */
    (void) values;
}

bool isMaxHeap(const vector<int>& values, int heapSize) {
    for (int child = 1; child < heapSize; child++) {
        int parent = (child - 1) / 2;
        if (values[parent] < values[child]) {
            return false;
        }
    }
    return true;
}

int failures = 0;

void check(bool actual, bool expected, const string& label) {
    if (actual == expected) {
        cout << "pass: " << label << '\n';
    } else {
        failures++;
        cout << "FAIL: " << label << " (expected " << boolalpha << expected
             << ", got " << actual << ")\n";
    }
}

void checkArray(
    const vector<int>& actual,
    const vector<int>& expected,
    const string& label) {
    check(actual == expected, true, label);
}

void testKnownConstruction() {
    vector<int> values = {7, 2, 9, 1, 6, 8, 3, 5, 4};
    buildMaxHeap(values);
    checkArray(values, {9, 6, 8, 5, 2, 7, 3, 1, 4},
               "known bottom-up construction");
    check(isMaxHeap(values, static_cast<int>(values.size())), true,
          "constructed array has max-heap order");

    vector<int> duplicatesAndNegatives = {-4, 7, 7, -9, 0, 7, -4};
    buildMaxHeap(duplicatesAndNegatives);
    check(isMaxHeap(duplicatesAndNegatives,
                    static_cast<int>(duplicatesAndNegatives.size())),
          true, "construction handles duplicates and negative keys");
}

void testActivePrefixBoundary() {
    vector<int> values = {2, 9, 8, 7, 6, 5, 1000, 2000};
    siftDown(values, 0, 6);
    checkArray(values, {9, 7, 8, 2, 6, 5, 1000, 2000},
               "siftDown stays inside the active prefix");
    check(isMaxHeap(values, 6), true,
          "active prefix has max-heap order after siftDown");
}

void testHeapSort() {
    vector<vector<int>> inputs = {
        {7, 2, 9, 1, 6, 8, 3, 5, 4},
        {-5, 3, -5, 0, 12, 3, -1},
        {1, 2, 3, 4, 5, 6},
        {6, 5, 4, 3, 2, 1},
        {},
        {42}
    };
    const vector<vector<int>> expected = {
        {1, 2, 3, 4, 5, 6, 7, 8, 9},
        {-5, -5, -1, 0, 3, 3, 12},
        {1, 2, 3, 4, 5, 6},
        {1, 2, 3, 4, 5, 6},
        {},
        {42}
    };
    const vector<string> labels = {
        "sorts a typical input",
        "sorts duplicate and negative keys",
        "sorts an already sorted input",
        "sorts a reverse-sorted input",
        "sorts an empty input",
        "sorts a one-element input"
    };

    for (size_t i = 0; i < inputs.size(); i++) {
        heapSort(inputs[i]);
        checkArray(inputs[i], expected[i], labels[i]);
    }
}

int main() {
    testKnownConstruction();
    testActivePrefixBoundary();
    testHeapSort();
    cout << (failures == 0
        ? "All tests passed."
        : to_string(failures) + " test(s) failed.")
         << '\n';
    return 0;
}
