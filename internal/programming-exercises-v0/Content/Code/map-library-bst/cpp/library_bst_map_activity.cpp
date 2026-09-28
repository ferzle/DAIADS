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
    std::cout << (failures == 0 ? "All tests passed."
                                : std::to_string(failures) + " test(s) failed.")
              << '\n';
}
