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
        // TODO: Inspect only key's home position. Store the key if that
        // position is empty, and return the appropriate InsertResult.
        static_cast<void>(key);
        return InsertResult::COLLISION;
    }

    bool contains(int key) const {
        // TODO: Return whether key is stored at its home position.
        static_cast<void>(key);
        return false;
    }

    bool remove(int key) {
        // TODO: If key is stored at its home position, mark that position
        // empty and return true. Otherwise make no change and return false.
        static_cast<void>(key);
        return false;
    }
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
    checkThrows([]() { IncompleteHashTable invalid(0); }, "reject nonpositive capacity");
    IncompleteHashTable set(7);
    check(!set.contains(8), "new table does not contain 8");
    check(set.insert(8) == InsertResult::INSERTED, "insert 8 at index 1");
    check(set.insert(10) == InsertResult::INSERTED, "insert 10 at index 3");
    check(set.insert(19) == InsertResult::INSERTED, "insert 19 at index 5");
    check(set.contains(8) && set.contains(10) && set.contains(19),
          "contains finds inserted keys");
    check(set.insert(8) == InsertResult::ALREADY_PRESENT,
          "report a duplicate separately");
    check(set.insert(24) == InsertResult::COLLISION, "24 collides with 10 at index 3");
    check(!set.contains(24) && set.contains(10), "a collision does not overwrite 10");
    check(!set.remove(24) && set.contains(10),
          "removing colliding absent key preserves 10");
    check(set.remove(10) && !set.contains(10), "remove stored key");
    check(!set.remove(10), "cannot remove a key twice");
    check(set.insert(24) == InsertResult::INSERTED, "removed position can be reused");
    checkThrows([&set]() { set.insert(-1); }, "reject negative insert key");
    checkThrows([&set]() { set.contains(-1); }, "reject negative lookup key");
    checkThrows([&set]() { set.remove(-1); }, "reject negative removal key");
}

int main() {
    testTable();
    std::cout << (failures == 0 ? "All tests passed.\n"
                                : std::to_string(failures) + " test(s) failed.\n");
    return failures == 0 ? 0 : 1;
}
