#include <algorithm>
#include <iostream>
#include <string>
#include <vector>

using namespace std;

void siftDown(vector<int>& values, int root, int heapSize) {
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
        swap(values[root], values[largerChild]);
        root = largerChild;
    }
}

void buildMaxHeap(vector<int>& values) {
    for (int root = static_cast<int>(values.size()) / 2 - 1;
         root >= 0; root--) {
        siftDown(values, root, static_cast<int>(values.size()));
    }
}

void heapSort(vector<int>& values) {
    buildMaxHeap(values);
    for (int heapSize = static_cast<int>(values.size()) - 1;
         heapSize > 0; heapSize--) {
        swap(values[0], values[heapSize]);
        siftDown(values, 0, heapSize);
    }
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

void testDeterministicLargeArrays() {
    for (int length : {0, 1, 2, 3, 31, 32, 33, 1000, 100000}) {
        std::vector<int> values(length);
        long long state = 0x5EED;
        for (int i = 0; i < length; i++) { state = (state * 1103515245 + 12345) & 0x7fffffff; values[i] = static_cast<int>(state % 101) - 50; }
        std::vector<int> expected = values; std::sort(expected.begin(), expected.end());
        heapSort(values); checkArray(values, expected, "deterministic large heapSort");
        std::vector<int> heap = expected; buildMaxHeap(heap);
        check(isMaxHeap(heap, static_cast<int>(heap.size())), true, "deterministic large buildMaxHeap");
    }
}

int main() {
    testKnownConstruction();
    testActivePrefixBoundary();
    testHeapSort();
    testDeterministicLargeArrays();
    cout << (failures == 0
        ? "All tests passed."
        : to_string(failures) + " test(s) failed.")
         << '\n';
    return failures == 0 ? 0 : 1;
}
