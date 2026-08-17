#include <algorithm>
#include <cstdint>
#include <iostream>
#include <stdexcept>
#include <string>
#include <vector>

class BitVectorIntSet {
private:
    static constexpr int BITS_PER_WORD = 32;

    int universeSizeValue;
    std::uint32_t* words;
    int wordCount;
    int count;

public:
    explicit BitVectorIntSet(int universeSize)
        : universeSizeValue(universeSize), words(nullptr), wordCount(0), count(0) {
        if (universeSize < 1) {
            throw std::invalid_argument("universeSize must be positive");
        }
        wordCount = (universeSize + BITS_PER_WORD - 1) / BITS_PER_WORD;
        words = new std::uint32_t[wordCount]{};
    }

    ~BitVectorIntSet() {
        delete[] words;
    }

    BitVectorIntSet(const BitVectorIntSet&) = delete;
    BitVectorIntSet& operator=(const BitVectorIntSet&) = delete;

private:
    // Helpers

    int wordIndex(int key) const {
        // TODO: Return the index of the 32-bit word that stores key.
        return -1;
    }

    int bitIndex(int key) const {
        // TODO: Return key's bit position within its word (0 through 31).
        return -1;
    }

    std::uint32_t wordMask(int bitIndex) const {
        // TODO: Return a 32-bit word with only bitIndex set to 1.
        // bitIndex must be between 0 and 31.
        return 0;
    }

    void checkKey(int key) const {
        if (key < 0 || key >= universeSizeValue) {
            throw std::out_of_range("key must be within the universe");
        }
    }

public:
    int universeSize() const {
        return universeSizeValue;
    }

    bool isEmpty() const {
        // TODO: Return whether the set contains no keys.
        return false;
    }

    int size() const {
        // TODO: Return the number of distinct keys currently present.
        return -1;
    }

    void clear() {
        // TODO: Clear all wordCount words and reset count.
    }

    bool contains(int key) const {
        checkKey(key);
        // TODO: Use wordIndex(key), bitIndex(key), and wordMask(bitIndex)
        // to find and test key's membership bit.
        return false;
    }

    bool add(int key) {
        checkKey(key);
        // TODO: Use the three helpers above. If the selected bit is already 1,
        // return false. Otherwise, set it with bitwise OR, increment count once,
        // and return true.
        return false;
    }

    bool remove(int key) {
        checkKey(key);
        // TODO: Use the three helpers above. If the selected bit is already 0,
        // return false. Otherwise, clear it with bitwise AND and complement,
        // decrement count once, and return true.
        return false;
    }

    std::vector<int> toVector() const {
        // Return the keys in increasing order for testing and iteration.
        std::vector<int> result;
        result.reserve(count);
        for (int key = 0; key < universeSizeValue; ++key) {
            if (contains(key)) {
                result.push_back(key);
            }
        }
        return result;
    }
};

int failures = 0;

void check(bool actual, bool expected, const std::string& label) {
    if (actual == expected) {
        std::cout << "pass: " << label << '\n';
    } else {
        ++failures;
        std::cout << "FAIL: " << label << " (expected " << std::boolalpha
                  << expected << ", got " << actual << ")\n";
    }
}

void check(int actual, int expected, const std::string& label) {
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
                 const std::string& label) {
    if (actual == expected) {
        std::cout << "pass: " << label << '\n';
    } else {
        ++failures;
        std::cout << "FAIL: " << label << " (vector contents differ)\n";
    }
}

template <typename Action>
void checkThrows(Action action, const std::string& label) {
    try {
        action();
        ++failures;
        std::cout << "FAIL: " << label << " (no exception thrown)\n";
    } catch (const std::out_of_range&) {
        std::cout << "pass: " << label << '\n';
    }
}

void checkInvalidUniverse(const std::string& label) {
    try {
        BitVectorIntSet invalid(0);
        ++failures;
        std::cout << "FAIL: " << label << " (no exception thrown)\n";
    } catch (const std::invalid_argument&) {
        std::cout << "pass: " << label << '\n';
    }
}

void testSet() {
    checkInvalidUniverse("reject nonpositive universe size");
    BitVectorIntSet set(128);
    check(set.universeSize(), 128, "constructor records universe size");
    check(set.isEmpty(), true, "new set is empty");
    check(set.size(), 0, "new set has size zero");
    check(set.contains(63), false, "valid missing key is absent");
    check(set.remove(96), false, "removing a missing key changes nothing");

    const std::vector<int> acrossAllWords{0, 31, 32, 47, 63, 64, 95, 96, 127};
    for (int key : acrossAllWords) {
        check(set.add(key), true, "add key " + std::to_string(key));
    }
    check(set.size(), static_cast<int>(acrossAllWords.size()),
          "size includes keys stored in all four words");
    checkVector(set.toVector(), acrossAllWords,
                "iteration finds boundary keys in increasing order");
    check(set.contains(31), true, "find high bit of first word");
    check(set.contains(32), true, "find low bit of second word");
    check(set.contains(64), true, "find low bit of third word");
    check(set.contains(127), true, "find high bit of fourth word");
    check(set.contains(30), false, "nearby clear bit remains absent");

    check(set.add(64), false, "reject duplicate key");
    check(set.size(), static_cast<int>(acrossAllWords.size()),
          "duplicate does not change size");
    check(set.remove(31), true, "remove high bit of first word");
    check(set.remove(64), true, "remove low bit of third word");
    check(set.remove(127), true, "remove high bit of fourth word");
    check(set.remove(64), false, "cannot remove a key twice");
    checkVector(set.toVector(), {0, 32, 47, 63, 95, 96},
                "removal clears only the selected bits");

    checkThrows([&set]() { set.contains(-1); }, "reject negative key");
    checkThrows([&set]() { set.add(128); },
                "reject key equal to universe size");

    set.clear();
    check(set.isEmpty(), true, "clear empties the set");
    check(set.size(), 0, "size is zero after clear");
    check(set.contains(0), false, "clear resets the first word");
    check(set.contains(96), false, "clear resets the final word");
    check(set.add(127), true, "set can be reused after clear");
}

int main() {
    testSet();
    std::cout << (failures == 0 ? "All tests passed.\n"
                                : std::to_string(failures) + " test(s) failed.\n");
    return failures == 0 ? 0 : 1;
}
