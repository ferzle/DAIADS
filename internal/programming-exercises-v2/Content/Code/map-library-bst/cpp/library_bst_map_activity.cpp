#include <iostream>
#include <map>
#include <optional>
#include <string>
#include <vector>

class OrderedIntMap {
private:
    std::map<int, int> tree;

public:
    bool isEmpty() const {
        // TODO: Delegate to the tree map.
        return false;
    }

    std::size_t size() const {
        // TODO: Delegate to the tree map.
        return 0;
    }

    bool containsKey(int key) const {
        // TODO: Do not try to infer presence from the associated value.
        return false;
    }

    std::optional<int> get(int key) const {
        // TODO: Return std::nullopt when key is absent.
        return std::nullopt;
    }

    std::optional<int> put(int key, int value) {
        // TODO: Insert or replace, returning the old value when one existed.
        return std::nullopt;
    }

    std::optional<int> remove(int key) {
        // TODO: Remove key and return its old value, or return std::nullopt.
        return std::nullopt;
    }

    std::vector<std::string> entriesInRange(int low, int high) const {
        // TODO: Return "key=value" strings for low <= key <= high in
        // increasing key order. Return an empty vector when low > high.
        return {};
    }
};

int failures = 0;

void check(bool condition, const std::string& label) {
    if (condition) {
        std::cout << "pass: " << label << '\n';
    } else {
        ++failures;
        std::cout << "FAIL: " << label << '\n';
    }
}

void testUpdatesAndRangeBoundaries() {
    OrderedIntMap map;
    check(map.entriesInRange(0, 10).empty(), "empty-map range is empty");
    for (int key = -500; key <= 500; key++) check(!map.put(key, key * 2), "large insert");
    check(map.size() == 1001, "size after large insertion");
    check(map.put(0, 77).value_or(-1) == 0 && map.get(0).value_or(-1) == 77, "update key and return old value");
    check(map.entriesInRange(-2, 2) == std::vector<std::string>{"-2=-4", "-1=-2", "0=77", "1=2", "2=4"}, "inclusive range endpoints");
    check(map.entriesInRange(20, 10).empty(), "reversed range is empty");
    check(map.entriesInRange(501, 700).empty(), "range beyond all keys is empty");
}

int main() {
    OrderedIntMap map;
    check(map.isEmpty() && map.size() == 0, "new map is empty");
    check(!map.put(20, 4).has_value(), "put a new key");
    check(!map.put(5, 0).has_value(), "store zero as an ordinary value");
    check(!map.put(12, 7).has_value() && !map.put(30, 9).has_value(),
          "put more keys");
    check(map.containsKey(5) && map.get(5).value_or(-1) == 0,
          "presence is distinct from value zero");
    check(map.put(12, 8).value_or(-1) == 7 && map.size() == 4,
          "replacement returns old value without growing");
    check(map.entriesInRange(6, 20) ==
              std::vector<std::string>{"12=8", "20=4"},
          "closed range is sorted");
    check(map.remove(20).value_or(-1) == 4 && !map.containsKey(20),
          "remove a present key");
    check(!map.remove(99).has_value(), "remove an absent key");
    testUpdatesAndRangeBoundaries();
    std::cout << (failures == 0 ? "All tests passed."
                                : std::to_string(failures) + " test(s) failed.")
              << '\n';
    return failures == 0 ? 0 : 1;
}
