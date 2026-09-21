#include <algorithm>
#include <cstddef>
#include <iostream>
#include <stdexcept>
#include <string>
#include <vector>

enum class ProbingType { LINEAR, QUADRATIC, DOUBLE_HASHING };

class OpenAddressingIntSet {
private:
    enum class SlotState { EMPTY, OCCUPIED, DELETED };

    std::size_t m;
    std::vector<int> keys;
    std::vector<SlotState> states;
    ProbingType probingType;
    std::size_t count = 0;

    std::size_t probeIndex(int key, std::size_t i) const {
        switch (probingType) {
            case ProbingType::LINEAR: return linearProbeIndex(key, i);
            case ProbingType::QUADRATIC: return quadraticProbeIndex(key, i);
            case ProbingType::DOUBLE_HASHING: return doubleHashProbeIndex(key, i);
        }
        throw std::logic_error("unknown probing type");
    }

    std::size_t linearProbeIndex(int key, std::size_t i) const {
        // TODO: Compute probe i using linear probing.
        return 0;
    }

    std::size_t quadraticProbeIndex(int key, std::size_t i) const {
        // TODO: Compute probe i using triangular quadratic offsets.
        return 0;
    }

    std::size_t doubleHashProbeIndex(int key, std::size_t i) const {
        // TODO: Compute probe i using primaryHash and secondaryHash.
        return 0;
    }

    std::size_t primaryHash(int key) const {
        // TODO: Compute h1(key).
        return 0;
    }

    std::size_t secondaryHash(int key) const {
        // TODO: Compute the odd double-hashing step h2(key).
        return 0;
    }

    static void checkKey(int key) {
        if (key < 0) throw std::invalid_argument("key must be nonnegative");
    }

public:
    OpenAddressingIntSet(int capacityExponent, ProbingType type)
        : m(capacityExponent >= 1 && capacityExponent <= 30
                ? std::size_t{1} << capacityExponent
                : 0),
          keys(m, 0),
          states(m, SlotState::EMPTY),
          probingType(type) {
        if (capacityExponent < 1 || capacityExponent > 30) {
            throw std::invalid_argument("capacityExponent must be from 1 through 30");
        }
    }

    std::size_t size() const {
        // TODO: Return the number of distinct keys currently stored.
        return 0;
    }

    bool isEmpty() const {
        // TODO: Return whether the set contains no keys.
        return false;
    }

    std::size_t capacity() const { return m; }

    std::vector<std::size_t> slotCountsForTesting() const {
        std::vector<std::size_t> result(3, 0); // EMPTY, OCCUPIED, DELETED
        for (SlotState state : states) {
            if (state == SlotState::EMPTY) ++result[0];
            else if (state == SlotState::OCCUPIED) ++result[1];
            else ++result[2];
        }
        return result;
    }

    bool contains(int key) const {
        // TODO: Reject a negative key. Probe at most capacity() positions,
        // stopping successfully at key or unsuccessfully at an EMPTY slot.
        return false;
    }

    bool insert(int key) {
        // TODO: Reject a negative key. Remember the first DELETED slot, but
        // continue until finding key, an EMPTY slot, or the probe limit.
        // Add the key and increment count exactly once only when it is absent.
        return false;
    }

    bool remove(int key) {
        // TODO: Reject a negative key. Replace a matching OCCUPIED slot with
        // DELETED, decrement count exactly once, and return whether key existed.
        return false;
    }

    void clear() {
        // TODO: Restore every slot to EMPTY and reset count to zero.
    }
};

int failures = 0;

void check(bool condition, const std::string& label) {
    if (condition) std::cout << "pass: " << label << '\n';
    else { ++failures; std::cout << "FAIL: " << label << '\n'; }
}

template <typename T>
void checkEqual(const T& actual, const T& expected, const std::string& label) {
    check(actual == expected, label);
}

template <typename Action>
void checkThrows(Action action, const std::string& label) {
    try { action(); ++failures; std::cout << "FAIL: " << label << '\n'; }
    catch (const std::invalid_argument&) { std::cout << "pass: " << label << '\n'; }
}

std::string typeName(ProbingType type) {
    if (type == ProbingType::LINEAR) return "LINEAR";
    if (type == ProbingType::QUADRATIC) return "QUADRATIC";
    return "DOUBLE_HASHING";
}

void testStrategy(ProbingType type) {
    const std::string name = typeName(type);
    OpenAddressingIntSet set(3, type);
    checkEqual(set.capacity(), std::size_t{8}, name + ": exponent 3 gives capacity 8");
    check(set.isEmpty() && set.size() == 0, name + ": new set is empty");
    check(set.insert(1) && set.insert(9) && set.insert(17), name + ": insert colliding keys");
    checkEqual(set.slotCountsForTesting(), std::vector<std::size_t>({5, 3, 0}),
               name + ": three inserts occupy three slots");
    check(set.contains(1) && set.contains(9) && set.contains(17), name + ": find colliding keys");
    check(!set.insert(17) && set.size() == 3, name + ": reject duplicate insertion");
    check(set.remove(9) && !set.contains(9), name + ": remove creates a tombstone");
    checkEqual(set.slotCountsForTesting(), std::vector<std::size_t>({5, 2, 1}),
               name + ": removal changes one occupied slot to deleted");
    check(set.contains(17), name + ": lookup continues past a tombstone");
    check(set.insert(41) && set.contains(41), name + ": insertion can reuse a tombstone");
    checkEqual(set.slotCountsForTesting(), std::vector<std::size_t>({5, 3, 0}),
               name + ": insertion reuses the tombstone");
    check(!set.remove(99), name + ": absent removal changes nothing");

    OpenAddressingIntSet wraparound(3, type);
    check(wraparound.insert(7) && wraparound.insert(15) && wraparound.contains(15),
          name + ": probe sequence wraps around");

    OpenAddressingIntSet full(3, type);
    bool filled = true;
    for (int key = 0; key < static_cast<int>(full.capacity()); ++key) filled &= full.insert(key);
    check(filled && !full.insert(8), name + ": insertion fails when no position is available");
    check(full.remove(0) && full.insert(8) && full.contains(8),
          name + ": insertion reuses the only available tombstone");

    set.clear();
    check(set.isEmpty() && set.size() == 0 && !set.contains(1), name + ": clear resets the set");
    checkEqual(set.slotCountsForTesting(), std::vector<std::size_t>({8, 0, 0}),
               name + ": clear restores every slot to empty");
    checkThrows([&set]() { set.contains(-1); }, name + ": reject negative lookup key");
    checkThrows([&set]() { set.insert(-1); }, name + ": reject negative insertion key");
    checkThrows([&set]() { set.remove(-1); }, name + ": reject negative removal key");
}

void testTombstoneStress(ProbingType type) {
    const std::string name = typeName(type) + " stress";
    OpenAddressingIntSet set(7, type);
    const int m = static_cast<int>(set.capacity());
    bool result = true;

    // All 80 keys have home index 3, so every insertion must resolve collisions.
    for (int q = 0; q < 80; ++q) result &= set.insert(3 + m * q);
    check(result && set.size() == 80, name + ": insert 80 colliding keys");

    result = true;
    for (int q = 0; q < 80; ++q) result &= set.contains(3 + m * q);
    check(result, name + ": find all initial keys");

    // Remove 27 keys. Each replacement has the same home index and, for
    // double hashing, the same step as the key it replaces.
    result = true;
    for (int q = 0; q < 80; q += 3) result &= set.remove(3 + m * q);
    check(result && set.size() == 53, name + ": remove 27 keys");

    result = true;
    for (int q = 0; q < 80; ++q) {
        const int key = 3 + m * q;
        result &= q % 3 == 0 ? !set.contains(key) : set.contains(key);
    }
    check(result, name + ": searches cross tombstones correctly");

    result = true;
    for (int q = 0; q < 80; q += 3) result &= set.insert(3 + m * (q + 128));
    check(result && set.size() == 80, name + ": replace all 27 removed keys");

    // Create and refill a second wave of 14 tombstones.
    result = true;
    for (int q = 0; q < 80; q += 6) result &= set.remove(3 + m * (q + 128));
    check(result && set.size() == 66, name + ": remove 14 replacement keys");

    result = true;
    for (int q = 0; q < 80; q += 6) result &= set.insert(3 + m * (q + 256));
    check(result && set.size() == 80, name + ": refill the second tombstone wave");

    result = true;
    for (int q = 0; q < 80; ++q) {
        if (q % 6 == 0) {
            result &= !set.contains(3 + m * (q + 128));
            result &= set.contains(3 + m * (q + 256));
        } else if (q % 3 == 0) {
            result &= set.contains(3 + m * (q + 128));
        } else {
            result &= set.contains(3 + m * q);
        }
    }
    check(result, name + ": final membership is correct after 41 removals and replacements");
}

void testLargeDeleteReinsertCycles(ProbingType type) {
    OpenAddressingIntSet set(10, type);
    for (int key = 0; key < 400; key++) check(set.insert(key * 17), "large insert");
    for (int key = 0; key < 400; key += 3) check(set.remove(key * 17), "large remove");
    for (int key = 0; key < 400; key += 3) check(set.insert(100000 + key * 17), "tombstone reuse");
    for (int key = 0; key < 400; key++) check(set.contains(key * 17) == (key % 3 != 0), "final membership");
    check(set.size() == 400, "size after replacements");
}

int main() {
    for (ProbingType type : {ProbingType::LINEAR,
                             ProbingType::QUADRATIC,
                             ProbingType::DOUBLE_HASHING}) {
        testStrategy(type);
        testTombstoneStress(type);
        testLargeDeleteReinsertCycles(type);
    }
    std::cout << (failures == 0 ? "All tests passed.\n"
                                : std::to_string(failures) + " test(s) failed.\n");
    return failures == 0 ? 0 : 1;
}
