#include <iostream>
#include <stdexcept>
#include <string>
#include <vector>

class SortedArrayIntSet {
private:
    int* keys;
    int capacity;
    int count;

    int lowerBound(int key) const {
        // TODO: Return the first index in keys[0..count) whose value is
        // greater than or equal to key. Return count if none exists.
        return -1;
    }

    void ensureCapacity() {
        // TODO: When full, double the array capacity and copy all used keys.
    }

public:
    explicit SortedArrayIntSet(int initialCapacity)
        : keys(nullptr), capacity(initialCapacity), count(0) {
        if (initialCapacity < 1) {
            throw std::invalid_argument("initialCapacity must be positive");
        }
        keys = new int[capacity];
    }

    ~SortedArrayIntSet() {
        delete[] keys;
    }

    SortedArrayIntSet(const SortedArrayIntSet&) = delete;
    SortedArrayIntSet& operator=(const SortedArrayIntSet&) = delete;

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
        // TODO: Use binary search over keys[0..count).
        return false;
    }

    bool add(int key) {
        // TODO: Find key or its insertion position with binary search. Reject
        // duplicates; otherwise grow, shift right, insert, and update count.
        return false;
    }

    bool remove(int key) {
        // TODO: Locate key with binary search. If present, shift later keys
        // left, decrement count exactly once, and return true.
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
    SortedArrayIntSet set(2);
    check(set.isEmpty(), true, "new set is empty");
    check(set.size(), 0, "new set has size zero");
    check(set.contains(4), false, "missing key is not contained");
    check(set.remove(4), false, "removing a missing key changes nothing");

    check(set.add(8), true, "add first key");
    check(set.add(3), true, "insert before a larger key");
    check(set.add(11), true, "append and grow the backing array");
    check(set.add(6), true, "insert into the middle");
    check(set.add(-2), true, "insert a new minimum");
    checkVector(set.toVector(), {-2, 3, 6, 8, 11},
                "iteration order remains sorted");

    check(set.add(6), false, "reject duplicate key");
    check(set.size(), 5, "duplicate does not change size");
    check(set.contains(-2), true, "find the first key");
    check(set.contains(8), true, "find an interior key");
    check(set.contains(12), false, "reject a key beyond the used range");

    check(set.remove(6), true, "remove an interior key");
    checkVector(set.toVector(), {-2, 3, 8, 11},
                "removal shifts the suffix left");
    check(set.remove(-2), true, "remove the first key");
    check(set.remove(11), true, "remove the final key");
    check(set.remove(11), false, "cannot remove a key twice");
    checkVector(set.toVector(), {3, 8}, "remaining keys stay sorted");

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
