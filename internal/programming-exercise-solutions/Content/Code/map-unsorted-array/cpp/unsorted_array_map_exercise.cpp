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
        for (std::size_t i = 0; i < count; ++i) {
            if (keys[i] == key) return static_cast<int>(i);
        }
        return -1;
    }

    void ensureCapacity() {
        if (count == keys.size()) {
            keys.resize(keys.size() * 2);
            values.resize(values.size() * 2);
        }
    }

public:
    explicit UnsortedArrayIntMap(std::size_t initialCapacity)
        : keys(initialCapacity), values(initialCapacity) {
        if (initialCapacity < 1) throw std::invalid_argument("capacity must be positive");
    }

    bool isEmpty() const {
        return count == 0;
    }

    std::size_t size() const {
        return count;
    }

    void clear() {
        count = 0;
    }

    bool containsKey(int key) const {
        return indexOf(key) != -1;
    }

    std::optional<int> get(int key) const {
        const int index = indexOf(key);
        return index == -1 ? std::nullopt
                           : std::optional<int>{values[static_cast<std::size_t>(index)]};
    }

    std::optional<int> put(int key, int value) {
        const int existing = indexOf(key);
        if (existing != -1) {
            const std::size_t index = static_cast<std::size_t>(existing);
            const int oldValue = values[index];
            values[index] = value;
            return oldValue;
        }
        ensureCapacity();
        keys[count] = key;
        values[count] = value;
        ++count;
        return std::nullopt;
    }

    std::optional<int> remove(int key) {
        const int existing = indexOf(key);
        if (existing == -1) return std::nullopt;
        const std::size_t index = static_cast<std::size_t>(existing);
        const int oldValue = values[index];
        keys[index] = keys[count - 1];
        values[index] = values[count - 1];
        --count;
        return oldValue;
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

void testLargeMixedWorkload() {
    UnsortedArrayIntMap map(1);
    for (int key = 0; key < 1000; key++) check(!map.put(key, key * 3), "large insert");
    check(map.size() == 1000, "size after 1000 inserts");
    for (int key = 0; key < 1000; key++) check(map.get(key).value_or(-1) == key * 3, "large get");
    for (int key = 0; key < 1000; key += 3) check(map.put(key, -key).value_or(-1) == key * 3, "large replace");
    check(map.size() == 1000, "replacement preserves size");
    for (int key = 0; key < 1000; key += 2) check(map.remove(key).has_value(), "large remove");
    for (int key = 0; key < 1000; key++) check(map.containsKey(key) == (key % 2 == 1), "post-removal membership");
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
    testLargeMixedWorkload();
    std::cout << (failures == 0 ? "All tests passed." : std::to_string(failures) + " test(s) failed.") << '\n';
    return failures == 0 ? 0 : 1;
}
