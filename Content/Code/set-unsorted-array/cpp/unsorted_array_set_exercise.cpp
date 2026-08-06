#include <algorithm>
#include <iostream>
#include <stdexcept>
#include <string>
#include <vector>

class UnsortedArrayIntSet {
private:
    int* keys;
    int capacity;
    int count;

    void ensureCapacity() {
        // TODO: If the array is full, replace it with an array having twice
        // the capacity and copy all count used keys into the new array.
    }

public:
    explicit UnsortedArrayIntSet(int initialCapacity)
        : keys(nullptr), capacity(initialCapacity), count(0) {
        if (initialCapacity < 1) {
            throw std::invalid_argument("initialCapacity must be positive");
        }
        keys = new int[capacity];
    }

    ~UnsortedArrayIntSet() {
        delete[] keys;
    }

    UnsortedArrayIntSet(const UnsortedArrayIntSet&) = delete;
    UnsortedArrayIntSet& operator=(const UnsortedArrayIntSet&) = delete;

    bool isEmpty() const {
        // TODO: Return whether the used portion of the array is empty.
        return false;
    }

    int size() const {
        // TODO: Return the number of distinct stored keys, not the capacity.
        return -1;
    }

    void clear() {
        // TODO: Remove all logical contents. The allocated array may be reused.
    }

    bool contains(int key) const {
        // TODO: Search keys[0..count) sequentially.
        return false;
    }

    bool add(int key) {
        // TODO: Reject duplicates, grow if necessary, append the new key,
        // increment count, and report whether the set changed.
        return false;
    }

    bool remove(int key) {
        // TODO: Find the key, fill its gap with the final used key, decrement
        // count exactly once, and report whether the set changed.
        return false;
    }

    std::vector<int> toVector() const {
        return std::vector<int>(keys, keys + count);
    }
};

int failures = 0;

void check(bool actual, bool expected, const char* label) {
    if (actual == expected) {
        std::cout << "pass: " << label << '\n';
    } else {
        ++failures;
        std::cout << "FAIL: " << label << " (expected " << std::boolalpha
                  << expected << ", got " << actual << ")\n";
    }
}

void check(int actual, int expected, const char* label) {
    if (actual == expected) {
        std::cout << "pass: " << label << '\n';
    } else {
        ++failures;
        std::cout << "FAIL: " << label << " (expected " << expected
                  << ", got " << actual << ")\n";
    }
}

void checkVector(const std::vector<int>& actual,
                 const std::vector<int>& expected,
                 const char* label) {
    if (actual == expected) {
        std::cout << "pass: " << label << '\n';
    } else {
        ++failures;
        std::cout << "FAIL: " << label << " (array contents differ)\n";
    }
}

void testSet() {
    UnsortedArrayIntSet set(2);
    check(set.isEmpty(), true, "new set is empty");
    check(set.size(), 0, "new set has size zero");
    check(set.contains(4), false, "missing key is not contained");
    check(set.remove(4), false, "removing a missing key changes nothing");

    check(set.add(8), true, "add first key");
    check(set.add(3), true, "add second key");
    check(set.add(8), false, "reject duplicate key");
    check(set.size(), 2, "duplicate does not change size");
    checkVector(set.toVector(), {8, 3}, "keys append while space remains");

    check(set.add(11), true, "add grows the backing array");
    check(set.add(-2), true, "negative keys are supported");
    check(set.contains(11), true, "contains finds a stored key");
    check(set.size(), 4, "size after resizing");

    check(set.remove(3), true, "remove an interior key");
    checkVector(set.toVector(), {8, -2, 11},
                "interior gap is filled with the final key");
    check(set.remove(11), true, "remove the final used key");
    check(set.contains(11), false, "removed key is absent");
    check(set.remove(11), false, "cannot remove a key twice");

    set.clear();
    check(set.isEmpty(), true, "clear empties the set");
    check(set.size(), 0, "size is zero after clear");
    check(set.add(5), true, "set can be reused after clear");
}

int main() {
    testSet();
    std::cout << (failures == 0 ? "All tests passed.\n"
                                : std::to_string(failures) + " test(s) failed.\n");
    return failures == 0 ? 0 : 1;
}
