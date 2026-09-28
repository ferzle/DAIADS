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
        // TODO: Return whether no keys are present.
        return false;
    }

    std::size_t size() const {
        // TODO: Return the number of present keys.
        return 0;
    }

    bool containsKey(int key) const {
        checkKey(key);
        // TODO: Inspect the presence entry, not the stored value.
        return false;
    }

    std::optional<int> get(int key) const {
        checkKey(key);
        // TODO: Return the value only when its presence entry is true.
        return std::nullopt;
    }

    std::optional<int> put(int key, int value) {
        checkKey(key);
        // TODO: Replace and return the old value if present. Otherwise mark
        // present, store the value, increment count, and return nullopt.
        return std::nullopt;
    }

    std::optional<int> remove(int key) {
        checkKey(key);
        // TODO: If present, clear the flag, decrement count, and return the value.
        return std::nullopt;
    }

    void clear() {
        // TODO: Clear all presence entries and reset count. Stored values at
        // absent positions do not need to be erased.
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

int main() {
    try {
        DirectAddressIntMap invalid(0);
        ++failures;
        std::cout << "FAIL: reject nonpositive universe size\n";
    } catch (const std::invalid_argument&) {
        std::cout << "pass: reject nonpositive universe size\n";
    }
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
    std::cout << (failures == 0 ? "All tests passed." : std::to_string(failures) + " test(s) failed.") << '\n';
}
