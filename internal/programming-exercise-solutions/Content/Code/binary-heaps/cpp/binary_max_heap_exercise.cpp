#include <algorithm>
#include <initializer_list>
#include <iostream>
#include <optional>
#include <stdexcept>
#include <string>
#include <vector>

using namespace std;

class FixedCapacityMaxHeap {
private:
    int* values;
    int capacity;
    int count;

    int parent(int index) const {
        return (index - 1) / 2;
    }

    int leftChild(int index) const {
        return 2 * index + 1;
    }

    int rightChild(int index) const {
        return 2 * index + 2;
    }

    void siftUp(int index) {
        while (index > 0 && values[index] > values[parent(index)]) {
            const int parentIndex = parent(index);
            std::swap(values[index], values[parentIndex]);
            index = parentIndex;
        }
    }

    void siftDown(int index) {
        while (leftChild(index) < count) {
            int largerChild = leftChild(index);
            const int right = rightChild(index);
            if (right < count && values[right] > values[largerChild]) largerChild = right;
            if (values[largerChild] <= values[index]) return;
            std::swap(values[index], values[largerChild]);
            index = largerChild;
        }
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
        return count == 0;
    }

    bool isFull() const {
        return count == capacity;
    }

    int size() const {
        return count;
    }

    bool insert(int key) {
        if (isFull()) return false;
        values[count] = key;
        siftUp(count++);
        return true;
    }

    optional<int> peekMax() const {
        return isEmpty() ? nullopt : optional<int>{values[0]};
    }

    optional<int> extractMax() {
        if (isEmpty()) return nullopt;
        const int maximum = values[0];
        values[0] = values[--count];
        if (!isEmpty()) siftDown(0);
        return maximum;
    }

    bool hasValidHeapOrder() const {
        for (int child = 1; child < count; ++child) {
            if (values[parent(child)] < values[child]) return false;
        }
        return true;
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

void testLargeDeterministicDrain() {
    std::vector<int> expected(1000);
    long long state = 0x5EED;
    FixedCapacityMaxHeap heap(static_cast<int>(expected.size()));
    for (int i = 0; i < static_cast<int>(expected.size()); i++) {
        state = (state * 1103515245 + 12345) & 0x7fffffff;
        expected[i] = static_cast<int>(state % 2001) - 1000;
        check(heap.insert(expected[i]), true, "large insert");
        check(heap.hasValidHeapOrder(), true, "heap order after large insert");
    }
    std::sort(expected.begin(), expected.end());
    for (int i = static_cast<int>(expected.size()) - 1; i >= 0; i--) {
        checkOptional(heap.extractMax(), expected[i], "large extraction");
        check(heap.hasValidHeapOrder(), true, "heap order after large extraction");
    }
    check(heap.isEmpty(), true, "empty after large drain");
    check(heap.insert(42), true, "reuse after large drain");
    checkOptional(heap.extractMax(), 42, "extract reused value");
}

void testHundredThousandAggregateOperations() {
    const int count = 100000;
    std::vector<int> expected(count);
    long long state = 0xC0FFEE;
    FixedCapacityMaxHeap heap(count);
    bool ok = true;
    for (int i = 0; i < count; i++) {
        state = (state * 1103515245 + 12345) & 0x7fffffff;
        expected[i] = static_cast<int>(state);
        ok = heap.insert(expected[i]) && ok;
    }
    std::sort(expected.begin(), expected.end());
    for (int i = count - 1; i >= 0; i--) ok = heap.extractMax().value_or(-1) == expected[i] && ok;
    check(ok && heap.isEmpty() && heap.hasValidHeapOrder(), true,
          "100,000 aggregate insertions and extractions");
}

int main() {
    testCoreOperations();
    testOnlyLeftChildAndNegativeKeys();
    testLargeDeterministicDrain();
    testHundredThousandAggregateOperations();
    cout << (failures == 0
        ? "All tests passed."
        : to_string(failures) + " test(s) failed.")
         << '\n';
    return failures == 0 ? 0 : 1;
}
