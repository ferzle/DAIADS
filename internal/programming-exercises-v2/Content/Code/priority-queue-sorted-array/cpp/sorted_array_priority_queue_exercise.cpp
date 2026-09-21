#include <iostream>
#include <optional>
#include <stdexcept>
#include <string>
#include <vector>

using namespace std;

struct Entry {
    string value;
    int priorityKey;

    Entry() : value(""), priorityKey(0) {}

    Entry(string valueIn, int priorityKeyIn)
        : value(valueIn), priorityKey(priorityKeyIn) {}
};

class SortedArrayMinPriorityQueue {
private:
    Entry* entries;
    int capacity;
    int count;

    bool isBetter(const Entry& first, const Entry& second) const {
        // TODO: Smaller priority keys are better. Equal keys compare as
        // tied; stable behavior comes from their physical insertion order.
        (void) first;
        (void) second;
        return false;
    }

    void ensureCapacity() {
        // TODO: When full, allocate an array with twice the old capacity,
        // copy the count used entries, release the old array, and update
        // entries and capacity.
    }

public:
    explicit SortedArrayMinPriorityQueue(int initialCapacity)
        : entries(nullptr),
          capacity(initialCapacity),
          count(0) {
        if (initialCapacity < 1) {
            throw invalid_argument("initialCapacity must be positive");
        }
        entries = new Entry[capacity];
    }

    ~SortedArrayMinPriorityQueue() {
        delete[] entries;
    }

    SortedArrayMinPriorityQueue(const SortedArrayMinPriorityQueue&) = delete;
    SortedArrayMinPriorityQueue& operator=(
        const SortedArrayMinPriorityQueue&) = delete;

    bool isEmpty() const {
        // TODO: Return whether the used portion of the array is empty.
        return false;
    }

    int size() const {
        // TODO: Return the number of stored entries, not the array capacity.
        return -1;
    }

    void insert(const string& value, int priorityKey) {
        Entry newEntry(value, priorityKey);

        // TODO:
        // 1. Grow the array if count equals capacity.
        // 2. Find the insertion index that keeps entries[0..count)
        //    ordered from worst to best. Insert a new tied entry on the
        //    left/worse side of all existing entries with the same key.
        // 3. Shift the required suffix one position to the right.
        // 4. Store newEntry and increment count.
    }

    optional<Entry> peek() const {
        // Empty-queue policy: return nullopt.
        // TODO: Otherwise return the best entry without removing it.
        return nullopt;
    }

    optional<Entry> extract() {
        // Empty-queue policy: return nullopt.
        // TODO: Remove and return the best entry at the used right end.
        // Overwrite the vacated cell with a default Entry.
        return nullopt;
    }

    vector<string> entriesForTesting() const {
        vector<string> result;
        for (int i = 0; i < count; ++i) {
            result.push_back(entries[i].value + ":" + to_string(entries[i].priorityKey));
        }
        return result;
    }
};

int failures = 0;

void check(bool actual, bool expected, const string& label) {
    if (actual == expected) {
        cout << "pass: " << label << '\n';
    } else {
        failures++;
        cout << "FAIL: " << label << " (expected " << boolalpha << expected
             << ", got " << actual << ")\n";
    }
}

void check(int actual, int expected, const string& label) {
    if (actual == expected) {
        cout << "pass: " << label << '\n';
    } else {
        failures++;
        cout << "FAIL: " << label << " (expected " << expected
             << ", got " << actual << ")\n";
    }
}

void checkEntry(
    const optional<Entry>& actual,
    const string& expectedValue,
    int expectedKey,
    const string& label) {
    if (actual.has_value()
            && actual->value == expectedValue
            && actual->priorityKey == expectedKey) {
        cout << "pass: " << label << '\n';
    } else {
        failures++;
        cout << "FAIL: " << label << " (expected " << expectedValue
             << ':' << expectedKey << ", got ";
        if (actual.has_value()) {
            cout << actual->value << ':' << actual->priorityKey;
        } else {
            cout << "empty";
        }
        cout << ")\n";
    }
}

void checkEmpty(const optional<Entry>& actual, const string& label) {
    check(!actual.has_value(), true, label);
}

void testSortedArrayPriorityQueue() {
    SortedArrayMinPriorityQueue queue(2);

    check(queue.isEmpty(), true, "new queue is empty");
    check(queue.size(), 0, "new queue has size zero");
    checkEmpty(queue.peek(), "peek on empty queue");
    checkEmpty(queue.extract(), "extract on empty queue");

    // These insertions require placement at the used beginning, middle,
    // and end, and they force the backing array to resize.
    queue.insert("A", 5);
    queue.insert("B", 2);
    queue.insert("C", 7);
    queue.insert("D", 4);
    queue.insert("E", 2);
    check(queue.entriesForTesting() == vector<string>({"C:7", "A:5", "D:4", "E:2", "B:2"}),
          true, "physical order is worst-to-best with stable ties");

    check(queue.size(), 5, "size after five insertions");
    checkEntry(queue.peek(), "B", 2, "peek returns earliest best entry");
    check(queue.size(), 5, "peek does not remove an entry");

    checkEntry(queue.extract(), "B", 2, "first extraction");
    checkEntry(queue.extract(), "E", 2, "stable tied-key extraction");
    checkEntry(queue.extract(), "D", 4, "middle-priority extraction");
    checkEntry(queue.extract(), "A", 5, "next extraction");
    checkEntry(queue.extract(), "C", 7, "worst entry is extracted last");

    check(queue.isEmpty(), true, "queue is empty after all extractions");
    check(queue.size(), 0, "size returns to zero");
    checkEmpty(queue.extract(), "extract remains safe when empty");
}

void testLargeStableDrain() {
    SortedArrayMinPriorityQueue queue(1);
    for (int i = 0; i < 500; i++) queue.insert("v" + std::to_string(i), i % 17);
    check(queue.size(), 500, "size after large insertion");
    for (int priority = 0; priority < 17; priority++)
        for (int i = priority; i < 500; i += 17)
            checkEntry(queue.extract(), "v" + std::to_string(i), priority, "stable large extraction");
    check(queue.isEmpty(), true, "empty after large drain"); checkEmpty(queue.extract(), "extract after large drain");
}

int main() {
    testSortedArrayPriorityQueue();
    testLargeStableDrain();
    cout << (failures == 0
        ? "All tests passed."
        : to_string(failures) + " test(s) failed.")
         << '\n';
    return failures == 0 ? 0 : 1;
}
