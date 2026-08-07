#include <cstddef>
#include <iostream>
#include <optional>
#include <stdexcept>
#include <string>
#include <utility>
#include <vector>

class UnsortedArrayIntMap {
private:
    std::vector<int> keys;
    std::vector<int> values;
    std::size_t count = 0;

    int indexOf(int key) const {
        // TODO: Return key's used index, or -1 if absent.
        return -1;
    }

    void ensureCapacity() {
        // TODO: When full, double BOTH vectors' logical storage while
        // preserving all entries. The vectors model fixed backing arrays, so
        // resize them rather than push_back one element at a time.
    }

public:
    explicit UnsortedArrayIntMap(std::size_t initialCapacity)
        : keys(initialCapacity), values(initialCapacity) {
        if (initialCapacity < 1) throw std::invalid_argument("capacity must be positive");
    }

    bool isEmpty() const {
        // TODO: Return whether there are no used entries.
        return false;
    }

    std::size_t size() const {
        // TODO: Return the number of entries, not the capacity.
        return 0;
    }

    void clear() {
        // TODO: Remove all logical entries while retaining storage.
    }

    bool containsKey(int key) const {
        // TODO: Search only the used prefix.
        return false;
    }

    std::optional<int> get(int key) const {
        // TODO: Return the aligned value, or std::nullopt when absent.
        return std::nullopt;
    }

    std::optional<int> put(int key, int value) {
        // TODO: Replace and return an old value when key is present.
        // Otherwise grow if needed, append one aligned entry, and return nullopt.
        return std::nullopt;
    }

    std::optional<int> remove(int key) {
        // TODO: If present, save its value, copy BOTH parts of the final entry
        // into its gap, decrement count, and return the saved value.
        return std::nullopt;
    }

    std::vector<std::pair<int, int>> entries() const {
        std::vector<std::pair<int, int>> result;
        for (std::size_t i = 0; i < count; ++i) result.emplace_back(keys[i], values[i]);
        return result;
    }
};

int failures = 0;
void check(bool condition, const std::string& label) {
    if (condition) std::cout << "pass: " << label << '\n';
    else { ++failures; std::cout << "FAIL: " << label << '\n'; }
}

int main() {
    UnsortedArrayIntMap map(2);
    check(map.isEmpty() && map.size() == 0, "new map is empty");
    check(!map.get(4) && !map.remove(4), "missing operations");
    check(!map.put(8, 80) && !map.put(3, 0), "insert entries");
    check(map.containsKey(3) && map.get(3).value_or(-1) == 0, "stored zero is present");
    check(map.put(8, 81).value_or(-1) == 80 && map.size() == 2, "replace value");
    check(!map.put(11, 110) && !map.put(-2, -20), "resize arrays together");
    check(map.remove(3).value_or(-1) == 0, "remove interior entry");
    check(map.entries() == std::vector<std::pair<int, int>>{{8, 81}, {-2, -20}, {11, 110}},
          "copy final aligned entry into gap");
    check(map.remove(11).value_or(-1) == 110 && !map.remove(11), "remove once");
    map.clear();
    check(map.isEmpty() && !map.put(5, 50), "clear and reuse");
    std::cout << (failures == 0 ? "All tests passed." : std::to_string(failures) + " test(s) failed.") << '\n';
}
