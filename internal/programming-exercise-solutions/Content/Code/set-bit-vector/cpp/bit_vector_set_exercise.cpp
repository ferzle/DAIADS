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
        return key / BITS_PER_WORD;
    }

    int bitIndex(int key) const {
        return key % BITS_PER_WORD;
    }

    std::uint32_t wordMask(int bitIndex) const {
        return std::uint32_t{1} << bitIndex;
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
        return count == 0;
    }

    int size() const {
        return count;
    }

    void clear() {
        std::fill(words, words + wordCount, std::uint32_t{0});
        count = 0;
    }

    bool contains(int key) const {
        checkKey(key);
        std::uint32_t mask = wordMask(bitIndex(key));
        return (words[wordIndex(key)] & mask) != 0;
    }

    bool add(int key) {
        checkKey(key);
        int word = wordIndex(key);
        std::uint32_t mask = wordMask(bitIndex(key));
        if ((words[word] & mask) != 0) return false;
        words[word] |= mask;
        ++count;
        return true;
    }

    bool remove(int key) {
        checkKey(key);
        int word = wordIndex(key);
        std::uint32_t mask = wordMask(bitIndex(key));
        if ((words[word] & mask) == 0) return false;
        words[word] &= ~mask;
        --count;
        return true;
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

void testEveryBitAroundWordBoundaries() {
    for (int universe : {1, 31, 32, 33, 63, 64, 65, 257}) {
        BitVectorIntSet set(universe);
        for (int key = 0; key < universe; key++) check(set.add(key), true, "add each valid key");
        check(set.size(), universe, "all valid keys counted");
        for (int key = 0; key < universe; key++) check(set.contains(key), true, "find each valid key");
        for (int key = 0; key < universe; key += 2) check(set.remove(key), true, "remove even key");
        for (int key = 0; key < universe; key++) check(set.contains(key), key % 2 == 1, "post-removal membership");
        set.clear(); check(set.isEmpty(), true, "clear boundary universe");
    }
}

void testMillionBitUniverse() {
    const int universe = 1000000;
    BitVectorIntSet set(universe);
    bool ok = true;
    for (int key = 0; key < universe; key++) ok = set.add(key) && ok;
    ok = set.size() == universe && set.contains(0) && set.contains(31)
         && set.contains(32) && set.contains(999999) && ok;
    for (int key = 0; key < universe; key += 2) ok = set.remove(key) && ok;
    ok = set.size() == universe / 2 && !set.contains(0)
         && set.contains(999999) && !set.contains(999998) && ok;
    set.clear();
    check(ok && set.isEmpty() && set.size() == 0, true, "million-bit aggregate workload");
}

int main() {
    testSet();
    testEveryBitAroundWordBoundaries();
    testMillionBitUniverse();
    std::cout << (failures == 0 ? "All tests passed.\n"
                                : std::to_string(failures) + " test(s) failed.\n");
    return failures == 0 ? 0 : 1;
}
