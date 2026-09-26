#include <iostream>
#include <stdexcept>
#include <string>
#include <vector>

enum class InsertResult { INSERTED, ALREADY_PRESENT, COLLISION };

class IncompleteHashTable {
private:
    static constexpr int EMPTY = -1;
    std::vector<int> table;

    std::size_t homePosition(int key) const {
        checkKey(key);
        return static_cast<std::size_t>(key) % table.size();
    }

    void checkKey(int key) const {
        if (key < 0) throw std::invalid_argument("key must be nonnegative");
    }

public:
    explicit IncompleteHashTable(int capacity)
        : table(capacity > 0 ? static_cast<std::size_t>(capacity) : 0, EMPTY) {
        if (capacity < 1) throw std::invalid_argument("capacity must be positive");
    }

    InsertResult insert(int key) {
        const std::size_t index = homePosition(key);
        if (table[index] == key) return InsertResult::ALREADY_PRESENT;
        if (table[index] != EMPTY) return InsertResult::COLLISION;
        table[index] = key;
        return InsertResult::INSERTED;
    }

    bool contains(int key) const {
        const std::size_t index = homePosition(key);
        return table[index] == key;
    }

    bool remove(int key) {
        const std::size_t index = homePosition(key);
        if (table[index] != key) return false;
        table[index] = EMPTY;
        return true;
    }

    std::vector<int> tableSnapshotForTesting() const { return table; }
};

int failures = 0;

void check(bool condition, const std::string& label) {
    if (condition) std::cout << "pass: " << label << '\n';
    else { ++failures; std::cout << "FAIL: " << label << '\n'; }
}

template <typename Action>
void checkThrows(Action action, const std::string& label) {
    try { action(); ++failures; std::cout << "FAIL: " << label << '\n'; }
    catch (const std::invalid_argument&) { std::cout << "pass: " << label << '\n'; }
}

void testTable() {
    IncompleteHashTable set(7);
    check(!set.contains(8), "new table does not contain 8");
    check(set.insert(8) == InsertResult::INSERTED, "insert 8 at index 1");
    check(set.insert(10) == InsertResult::INSERTED, "insert 10 at index 3");
    check(set.insert(19) == InsertResult::INSERTED, "insert 19 at index 5");
    check(set.tableSnapshotForTesting() == std::vector<int>({-1, 8, -1, 10, -1, 19, -1}),
          "keys occupy only their home positions");
    check(set.contains(8) && set.contains(10) && set.contains(19),
          "contains finds inserted keys");
    check(set.insert(8) == InsertResult::ALREADY_PRESENT,
          "report a duplicate separately");
    check(set.insert(24) == InsertResult::COLLISION, "24 collides with 10 at index 3");
    check(!set.contains(24) && set.contains(10), "a collision does not overwrite 10");
    check(set.tableSnapshotForTesting()[3] == 10,
          "collision leaves the occupied home slot unchanged");
    check(!set.remove(24) && set.contains(10),
          "removing colliding absent key preserves 10");
    check(set.remove(10) && !set.contains(10), "remove stored key");
    check(!set.remove(10), "cannot remove a key twice");
    check(set.insert(24) == InsertResult::INSERTED, "removed position can be reused");
    check(set.tableSnapshotForTesting()[3] == 24,
          "reused home position stores the new key");
    checkThrows([&set]() { set.insert(-1); }, "reject negative insert key");
    checkThrows([&set]() { set.contains(-1); }, "reject negative lookup key");
    checkThrows([&set]() { set.remove(-1); }, "reject negative removal key");
}

void testAllHomePositionsAndCollisions() {
    const int capacity = 100000;
    IncompleteHashTable table(capacity);
    bool ok = true;
    for (int key = 0; key < capacity; key++) ok = (table.insert(key) == InsertResult::INSERTED) && ok;
    for (int key = 0; key < capacity; key++) ok = table.contains(key) && ok;
    for (int key = 0; key < capacity; key++) ok = (table.insert(key + capacity) == InsertResult::COLLISION) && ok;
    for (int key = 0; key < capacity; key += 2) ok = table.remove(key) && ok;
    for (int key = 0; key < capacity; key += 2) ok = (table.insert(key + capacity) == InsertResult::INSERTED) && ok;
    check(ok, "100,000 home positions, collisions, removals, and slot reuses");
}

int main() {
    testTable();
    testAllHomePositionsAndCollisions();
    std::cout << (failures == 0 ? "All tests passed.\n"
                                : std::to_string(failures) + " test(s) failed.\n");
    return failures == 0 ? 0 : 1;
}
