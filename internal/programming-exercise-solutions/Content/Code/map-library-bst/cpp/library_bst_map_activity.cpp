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
        return tree.empty();
    }

    std::size_t size() const {
        return tree.size();
    }

    bool containsKey(int key) const {
        return tree.find(key) != tree.end();
    }

    std::optional<int> get(int key) const {
        const auto found = tree.find(key);
        return found == tree.end() ? std::nullopt
                                   : std::optional<int>{found->second};
    }

    std::optional<int> put(int key, int value) {
        auto found = tree.find(key);
        if (found != tree.end()) {
            const int oldValue = found->second;
            found->second = value;
            return oldValue;
        }
        tree.emplace(key, value);
        return std::nullopt;
    }

    std::optional<int> remove(int key) {
        auto found = tree.find(key);
        if (found == tree.end()) return std::nullopt;
        const int oldValue = found->second;
        tree.erase(found);
        return oldValue;
    }

    std::vector<std::string> entriesInRange(int low, int high) const {
        std::vector<std::string> result;
        if (low > high) return result;
        for (auto it = tree.lower_bound(low); it != tree.end() && it->first <= high; ++it) {
            result.push_back(std::to_string(it->first) + "=" + std::to_string(it->second));
        }
        return result;
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

void testHundredThousandOrderedEntries() {
    OrderedIntMap map;
    bool ok = true;
    for (int key = -50000; key < 50000; key++) ok = !map.put(key, key * 2) && ok;
    for (int key = -50000; key < 50000; key += 997) {
        ok = map.containsKey(key) && map.get(key).value_or(-200001) == key * 2 && ok;
    }
    const auto range = map.entriesInRange(-5000, 4999);
    ok = map.size() == 100000 && range.size() == 10000
         && range.front() == "-5000=-10000" && range.back() == "4999=9998" && ok;
    check(ok, "100,000-entry ordered map and 10,000-entry range");
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
    testHundredThousandOrderedEntries();
    std::cout << (failures == 0 ? "All tests passed."
                                : std::to_string(failures) + " test(s) failed.")
              << '\n';
    return failures == 0 ? 0 : 1;
}
