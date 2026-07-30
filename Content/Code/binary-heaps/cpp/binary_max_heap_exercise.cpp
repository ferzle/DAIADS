#include <algorithm>
#include <initializer_list>
#include <iostream>
#include <optional>
#include <stdexcept>
#include <string>

using namespace std;

class FixedCapacityMaxHeap {
private:
    int* values;
    int capacity;
    int count;

    int parent(int index) const {
        // Precondition: index > 0.
        // TODO: Return the zero-based index of this entry's parent.
        (void) index;
        return -1;
    }

    int leftChild(int index) const {
        // TODO: Return the zero-based index of this entry's left child.
        (void) index;
        return -1;
    }

    int rightChild(int index) const {
        // TODO: Return the zero-based index of this entry's right child.
        (void) index;
        return -1;
    }

    void siftUp(int index) {
        // TODO: While this key is greater than its parent, swap the two
        // keys and continue from the parent's former position.
        (void) index;
    }

    void siftDown(int index) {
        // TODO: While a child exists, select the larger existing child.
        // Swap only when that child is greater than the current key, then
        // continue from the child's former position.
        (void) index;
    }

public:
    explicit FixedCapacityMaxHeap(int capacityIn)
        : values(nullptr), capacity(capacityIn), count(0) {
        if (capacityIn < 1) {
            throw invalid_argument("capacity must be positive");
        }
        values = new int[capacity];
    }

    ~FixedCapacityMaxHeap() {
        delete[] values;
    }

    FixedCapacityMaxHeap(const FixedCapacityMaxHeap&) = delete;
    FixedCapacityMaxHeap& operator=(const FixedCapacityMaxHeap&) = delete;

    bool isEmpty() const {
        // TODO: Return whether the logical heap contains no keys.
        return false;
    }

    bool isFull() const {
        // TODO: Return whether every position in the backing array is used.
        return false;
    }

    int size() const {
        // TODO: Return the number of keys in the logical heap.
        return -1;
    }

    bool insert(int key) {
        // TODO:
        // 1. If the heap is full, return false without changing it.
        // 2. Place key at the end of the used range and increment count.
        // 3. Restore max-heap order with siftUp.
        // 4. Return true.
        (void) key;
        return false;
    }

    optional<int> peekMax() const {
        // Empty-heap policy: return nullopt.
        // TODO: Otherwise return the root without changing the heap.
        return nullopt;
    }

    optional<int> extractMax() {
        // Empty-heap policy: return nullopt.
        // TODO:
        // 1. Save the root.
        // 2. Move the final used key to the root and decrement count.
        // 3. Restore max-heap order with siftDown when the heap is nonempty.
        // 4. Return the saved maximum.
        return nullopt;
    }

    bool hasValidHeapOrder() const {
        // TODO: Check every used parent-child pair. This O(n) method is
        // for testing only; public heap operations must not call it.
        return false;
    }

    bool usedValuesEqual(initializer_list<int> expected) const {
        if (static_cast<int>(expected.size()) != count) {
            return false;
        }
        return equal(expected.begin(), expected.end(), values);
    }
};

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

void check(int actual, int expected, const string& label) {
    if (actual == expected) {
        cout << "pass: " << label << '\n';
    } else {
        failures++;
        cout << "FAIL: " << label << " (expected " << expected
             << ", got " << actual << ")\n";
    }
}

void checkOptional(
    const optional<int>& actual,
    const optional<int>& expected,
    const string& label) {
    if (actual == expected) {
        cout << "pass: " << label << '\n';
    } else {
        failures++;
        cout << "FAIL: " << label << " (expected ";
        if (expected.has_value()) {
            cout << *expected;
        } else {
            cout << "empty";
        }
        cout << ", got ";
        if (actual.has_value()) {
            cout << *actual;
        } else {
            cout << "empty";
        }
        cout << ")\n";
    }
}

void checkArray(
    const FixedCapacityMaxHeap& heap,
    initializer_list<int> expected,
    const string& label) {
    check(heap.usedValuesEqual(expected), true, label);
}

void testCoreOperations() {
    FixedCapacityMaxHeap heap(7);

    check(heap.isEmpty(), true, "new heap is empty");
    check(heap.isFull(), false, "new heap is not full");
    check(heap.size(), 0, "new heap has size zero");
    checkOptional(heap.peekMax(), nullopt, "peek on empty heap");
    checkOptional(heap.extractMax(), nullopt, "extract on empty heap");

    for (int key : {40, 70, 30, 90, 60, 80, 80}) {
        check(heap.insert(key), true, "insert " + to_string(key));
        check(heap.hasValidHeapOrder(), true,
              "heap order after inserting " + to_string(key));
    }

    checkArray(heap, {90, 70, 80, 40, 60, 30, 80},
               "array after insertions");
    check(heap.isFull(), true, "heap reports full");
    check(heap.insert(100), false, "insertion fails when full");
    checkArray(heap, {90, 70, 80, 40, 60, 30, 80},
               "failed insertion leaves heap unchanged");

    checkOptional(heap.peekMax(), 90, "peek returns the maximum");
    check(heap.size(), 7, "peek leaves size unchanged");
    checkOptional(heap.extractMax(), 90, "extract returns the maximum");
    checkArray(heap, {80, 70, 80, 40, 60, 30},
               "array after extraction");
    check(heap.hasValidHeapOrder(), true, "heap order after extraction");
}

void testOnlyLeftChildAndNegativeKeys() {
    FixedCapacityMaxHeap heap(5);
    for (int key : {100, 90, 80, 70, 60}) {
        heap.insert(key);
    }

    checkOptional(heap.extractMax(), 100, "extract before left-only repair");
    checkArray(heap, {90, 70, 80, 60},
               "siftDown handles an only-left-child step");
    check(heap.hasValidHeapOrder(), true, "left-only result is a heap");

    FixedCapacityMaxHeap negatives(3);
    negatives.insert(-8);
    negatives.insert(-3);
    negatives.insert(-12);
    checkOptional(negatives.extractMax(), -3,
                  "maximum is correct for negative keys");
}

int main() {
    testCoreOperations();
    testOnlyLeftChildAndNegativeKeys();
    cout << (failures == 0
        ? "All tests passed."
        : to_string(failures) + " test(s) failed.")
         << '\n';
    return 0;
}
