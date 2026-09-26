#include <algorithm>
#include <cstddef>
#include <iostream>
#include <optional>
#include <stdexcept>
#include <string>
#include <vector>

class DirectAddressIntMap {
private:
    std::vector<bool> present;
    std::vector<int> values;
    std::size_t count = 0;

    void checkKey(int key) const {
        if (key < 0 || static_cast<std::size_t>(key) >= present.size()) {
            throw std::out_of_range("key outside the universe");
        }
    }

public:
    explicit DirectAddressIntMap(std::size_t universeSize)
        : present(universeSize, false), values(universeSize) {
        if (universeSize < 1) throw std::invalid_argument("universe size must be positive");
    }

    std::size_t universeSize() const { return present.size(); }

    bool isEmpty() const {
        return count == 0;
    }

    std::size_t size() const {
        return count;
    }

    bool containsKey(int key) const {
        checkKey(key);
        return present[static_cast<std::size_t>(key)];
    }

    std::optional<int> get(int key) const {
        checkKey(key);
        const std::size_t index = static_cast<std::size_t>(key);
        return present[index] ? std::optional<int>{values[index]} : std::nullopt;
    }

    std::optional<int> put(int key, int value) {
        checkKey(key);
        const std::size_t index = static_cast<std::size_t>(key);
        if (present[index]) {
            const int oldValue = values[index];
            values[index] = value;
            return oldValue;
        }
        present[index] = true;
        values[index] = value;
        ++count;
        return std::nullopt;
    }

    std::optional<int> remove(int key) {
        checkKey(key);
        const std::size_t index = static_cast<std::size_t>(key);
        if (!present[index]) return std::nullopt;
        present[index] = false;
        --count;
        return values[index];
    }

    void clear() {
        std::fill(present.begin(), present.end(), false);
        count = 0;
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
    catch (const std::out_of_range&) { std::cout << "pass: " << label << '\n'; }
}

void testExhaustiveUniverse() {
    const int universe = 100000;
    DirectAddressIntMap map(universe);
    bool ok = true;
    for (int key = 0; key < universe; key++) ok = !map.put(key, key - 50000) && ok;
    ok = map.size() == static_cast<std::size_t>(universe) && ok;
    for (int key = 0; key < universe; key++) ok = (map.get(key).value_or(-200000) == key - 50000) && ok;
    for (int key = 0; key < universe; key += 2) ok = map.remove(key).has_value() && ok;
    for (int key = 0; key < universe; key++) ok = (map.containsKey(key) == (key % 2 == 1)) && ok;
    map.clear();
    ok = map.isEmpty() && !map.put(universe - 1, 0) && ok;
    check(ok, "100,000-key exhaustive universe workload");
}

int main() {
    DirectAddressIntMap map(10);
    check(map.universeSize() == 10 && map.isEmpty(), "new map records universe");
    check(!map.put(0, 0) && !map.put(9, -4), "insert boundary keys");
    check(map.containsKey(0) && map.get(0).value_or(-1) == 0, "zero value is present");
    check(map.put(9, 12).value_or(-1) == -4 && map.size() == 2, "replace without growing");
    check(map.remove(9).value_or(-1) == 12 && !map.remove(9), "remove once");
    checkThrows([&] { map.get(-1); }, "reject negative key");
    checkThrows([&] { map.put(10, 1); }, "reject key equal to universe size");
    map.clear();
    check(map.isEmpty() && !map.containsKey(0), "clear presence flags");
    check(!map.put(5, 50), "reuse after clear");
    testExhaustiveUniverse();
    std::cout << (failures == 0 ? "All tests passed." : std::to_string(failures) + " test(s) failed.") << '\n';
    return failures == 0 ? 0 : 1;
}
