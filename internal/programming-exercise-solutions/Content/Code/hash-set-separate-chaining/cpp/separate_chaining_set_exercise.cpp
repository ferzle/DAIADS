#include <iostream>
#include <stdexcept>
#include <string>
#include <unordered_set>
#include <vector>

class SeparateChainingIntSet {
private:
    struct Node {
        int key;
        Node* next;
        explicit Node(int value) : key(value), next(nullptr) {}
    };

    static constexpr double MAX_LOAD_FACTOR = 0.75;
    std::vector<Node*> buckets;
    std::size_t count = 0;

    std::size_t bucketIndex(int key) const {
        return static_cast<std::size_t>(key) % buckets.size();
    }

    static void checkKey(int key) {
        if (key < 0) throw std::invalid_argument("key must be nonnegative");
    }

    void resize(std::size_t newCapacity) {
        std::vector<Node*> newBuckets(newCapacity, nullptr);
        std::vector<Node*> tails(newCapacity, nullptr);
        for (Node* head : buckets) {
            for (Node* node = head; node != nullptr; ) {
                Node* next = node->next;
                node->next = nullptr;
                std::size_t index = static_cast<std::size_t>(node->key) % newCapacity;
                if (newBuckets[index] == nullptr) newBuckets[index] = node;
                else tails[index]->next = node;
                tails[index] = node;
                node = next;
            }
        }
        buckets.swap(newBuckets);
    }

public:
    explicit SeparateChainingIntSet(int initialCapacity)
        : buckets(initialCapacity > 0 ? static_cast<std::size_t>(initialCapacity) : 0,
                  nullptr) {
        if (initialCapacity < 1) throw std::invalid_argument("initialCapacity must be positive");
    }

    ~SeparateChainingIntSet() {
        for (Node* head : buckets) {
            while (head != nullptr) {
                Node* old = head;
                head = head->next;
                delete old;
            }
        }
    }

    SeparateChainingIntSet(const SeparateChainingIntSet&) = delete;
    SeparateChainingIntSet& operator=(const SeparateChainingIntSet&) = delete;

    std::size_t size() const {
        return count;
    }

    std::size_t capacity() const { return buckets.size(); }

    bool contains(int key) const {
        checkKey(key);
        for (Node* node = buckets[bucketIndex(key)]; node != nullptr; node = node->next) {
            if (node->key == key) return true;
        }
        return false;
    }

    bool add(int key) {
        checkKey(key);
        if (contains(key)) return false;
        if (static_cast<double>(count + 1) / buckets.size() > MAX_LOAD_FACTOR) {
            resize(buckets.size() * 2);
        }
        std::size_t index = bucketIndex(key);
        Node* newNode = new Node(key);
        if (buckets[index] == nullptr) buckets[index] = newNode;
        else {
            Node* tail = buckets[index];
            while (tail->next != nullptr) tail = tail->next;
            tail->next = newNode;
        }
        ++count;
        return true;
    }

    bool remove(int key) {
        checkKey(key);
        std::size_t index = bucketIndex(key);
        Node* previous = nullptr;
        Node* node = buckets[index];
        while (node != nullptr && node->key != key) {
            previous = node;
            node = node->next;
        }
        if (node == nullptr) return false;
        if (previous == nullptr) buckets[index] = node->next;
        else previous->next = node->next;
        delete node;
        --count;
        return true;
    }

    std::vector<int> bucketSnapshot(std::size_t index) const {
        if (index >= buckets.size()) throw std::out_of_range("invalid bucket index");
        std::vector<int> keys;
        for (Node* node = buckets[index]; node != nullptr; node = node->next) {
            keys.push_back(node->key);
        }
        return keys;
    }

    bool hasValidStructureForTesting() const {
        std::unordered_set<int> seen;
        std::size_t reachable = 0;
        for (std::size_t index = 0; index < buckets.size(); ++index) {
            Node* slow = buckets[index];
            Node* fast = buckets[index];
            while (fast != nullptr && fast->next != nullptr) {
                slow = slow->next;
                fast = fast->next->next;
                if (slow == fast) return false;
            }
            for (Node* node = buckets[index]; node != nullptr; node = node->next) {
                if (node->key < 0 || bucketIndex(node->key) != index
                        || !seen.insert(node->key).second) return false;
                ++reachable;
            }
        }
        return reachable == count;
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

void testSet() {
    SeparateChainingIntSet set(8);
    checkEqual(set.size(), std::size_t{0}, "new set has size zero");
    check(!set.contains(6), "lookup in an empty bucket");
    check(!set.remove(6), "remove from an empty bucket");

    check(set.add(1), "add first key");
    check(set.add(9) && set.add(17) && set.add(25), "add colliding keys");
    checkEqual(set.bucketSnapshot(1), std::vector<int>({1, 9, 17, 25}),
               "colliding keys append at the tail");
    check(set.hasValidStructureForTesting(), "collision chain has valid structure");
    check(!set.add(17), "reject duplicate key");
    checkEqual(set.size(), std::size_t{4}, "duplicate does not change size");
    check(set.contains(1) && set.contains(17) && set.contains(25),
          "contains traverses a chain");

    check(set.remove(1), "remove first node");
    check(set.remove(17), "remove middle node");
    check(set.remove(25), "remove final node");
    checkEqual(set.bucketSnapshot(1), std::vector<int>({9}), "remaining chain is intact");
    check(!set.remove(17), "absent removal changes nothing");
    checkEqual(set.size(), std::size_t{1}, "removals update size");

    SeparateChainingIntSet growing(4);
    check(growing.add(2) && growing.add(6) && growing.add(10),
          "fill table to load factor 0.75");
    checkEqual(growing.capacity(), std::size_t{4}, "capacity unchanged at threshold");
    check(!growing.add(6) && growing.capacity() == 4,
          "duplicate does not trigger resize");
    check(growing.add(14), "next distinct key triggers resize");
    checkEqual(growing.capacity(), std::size_t{8}, "resize doubles capacity");
    check(growing.contains(2) && growing.contains(6)
              && growing.contains(10) && growing.contains(14),
          "all keys remain findable after rehashing");
    checkEqual(growing.bucketSnapshot(2), std::vector<int>({2, 10}),
               "rehashing preserves tail order in bucket 2");
    checkEqual(growing.bucketSnapshot(6), std::vector<int>({6, 14}),
               "new key appends after rehashing");
    check(growing.hasValidStructureForTesting(), "valid structure after rehashing");

    checkThrows([&set]() { set.contains(-1); }, "reject negative lookup key");
    checkThrows([&set]() { set.add(-1); }, "reject negative insertion key");
    checkThrows([&set]() { set.remove(-1); }, "reject negative removal key");
}

void testLargeResizeAndCollisionWorkload() {
    SeparateChainingIntSet set(2);
    for (int key = 0; key < 1000; key++) check(set.add(key * 16), "large add");
    checkEqual(set.size(), static_cast<std::size_t>(1000), "size after collision-heavy growth");
    for (int key = 0; key < 1000; key++) check(set.contains(key * 16), "large contains");
    for (int key = 0; key < 1000; key += 2) check(set.remove(key * 16), "large remove");
    for (int key = 0; key < 1000; key++) check(set.contains(key * 16) == (key % 2 == 1), "large membership");
    check(set.hasValidStructureForTesting(), "valid structure after collision-heavy workload");
}

void testHundredThousandDistributedKeys() {
    const int count = 100000;
    SeparateChainingIntSet set(4);
    bool ok = true;
    for (int key = 0; key < count; key++) ok = set.add(key) && ok;
    for (int key = 0; key < count; key++) ok = set.contains(key) && ok;
    for (int key = 0; key < count; key += 2) ok = set.remove(key) && ok;
    for (int key = 0; key < count; key++) ok = (set.contains(key) == (key % 2 == 1)) && ok;
    check(ok && set.size() == static_cast<std::size_t>(count / 2)
          && set.hasValidStructureForTesting(), "100,000-key distributed aggregate workload");
}

int main() {
    try { testSet(); }
    catch (const std::exception& error) {
        ++failures;
        std::cout << "FAIL: core scenarios threw " << error.what() << '\n';
    }
    try { testLargeResizeAndCollisionWorkload(); }
    catch (const std::exception& error) {
        ++failures;
        std::cout << "FAIL: large workload threw " << error.what() << '\n';
    }
    try { testHundredThousandDistributedKeys(); }
    catch (const std::exception& error) {
        ++failures;
        std::cout << "FAIL: distributed workload threw " << error.what() << '\n';
    }
    std::cout << (failures == 0 ? "All tests passed.\n"
                                : std::to_string(failures) + " test(s) failed.\n");
    return failures == 0 ? 0 : 1;
}
