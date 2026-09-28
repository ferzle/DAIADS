#include <iostream>
#include <optional>
#include <stdexcept>
#include <string>

using namespace std;

struct Entry {
    string value;
    int priorityKey;
    long long sequenceNumber;

    Entry() : value(""), priorityKey(0), sequenceNumber(0) {}

    Entry(string valueIn, int priorityKeyIn, long long sequenceNumberIn)
        : value(valueIn),
          priorityKey(priorityKeyIn),
          sequenceNumber(sequenceNumberIn) {}
};

class UnsortedArrayMinPriorityQueue {
private:
    Entry* entries;
    int capacity;
    int count;
    long long nextSequenceNumber;

    bool isBetter(const Entry& first, const Entry& second) const {
        // TODO: Smaller priority keys are better. If keys tie, the entry
        // with the smaller sequence number is better.
        (void) first;
        (void) second;
        return false;
    }

    int findBestIndex() const {
        // TODO: Scan entries[0..count) and return the index of the entry
        // selected by isBetter. Call this only when the queue is nonempty.
        return -1;
    }

    void ensureCapacity() {
        // TODO: When full, allocate an array with twice the old capacity,
        // copy the count used entries, release the old array, and update
        // entries and capacity.
    }

public:
    explicit UnsortedArrayMinPriorityQueue(int initialCapacity)
        : entries(nullptr),
          capacity(initialCapacity),
          count(0),
          nextSequenceNumber(0) {
        if (initialCapacity < 1) {
            throw invalid_argument("initialCapacity must be positive");
        }
        entries = new Entry[capacity];
    }

    ~UnsortedArrayMinPriorityQueue() {
        delete[] entries;
    }

    UnsortedArrayMinPriorityQueue(
        const UnsortedArrayMinPriorityQueue&) = delete;
    UnsortedArrayMinPriorityQueue& operator=(
        const UnsortedArrayMinPriorityQueue&) = delete;

    bool isEmpty() const {
        // TODO: Return whether the used portion of the array is empty.
        return false;
    }

    int size() const {
        // TODO: Return the number of stored entries, not the array capacity.
        return -1;
    }

    void insert(const string& value, int priorityKey) {
        Entry newEntry(value, priorityKey, nextSequenceNumber);

        // TODO:
        // 1. Grow the array if count equals capacity.
        // 2. Append newEntry at entries[count].
        // 3. Increment count and advance the sequence number.
    }

    optional<Entry> peek() const {
        // Empty-queue policy: return nullopt.
        // TODO: Find the best index and return that entry without removing it.
        return nullopt;
    }

    optional<Entry> extract() {
        // Empty-queue policy: return nullopt.
        // TODO:
        // 1. Find and save the best entry.
        // 2. Move the final used entry into the removed entry's gap.
        // 3. Decrement count and overwrite the old final cell with a
        //    default Entry.
        // The same steps must work when the best entry is already last.
        return nullopt;
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
            cout << actual->value << ':' << actual->priorityKey
                 << '#' << actual->sequenceNumber;
        } else {
            cout << "empty";
        }
        cout << ")\n";
    }
}

void checkEmpty(const optional<Entry>& actual, const string& label) {
    check(!actual.has_value(), true, label);
}

void testGapFillingAndStability() {
    UnsortedArrayMinPriorityQueue queue(2);

    check(queue.isEmpty(), true, "new queue is empty");
    check(queue.size(), 0, "new queue has size zero");
    checkEmpty(queue.peek(), "peek on empty queue");
    checkEmpty(queue.extract(), "extract on empty queue");

    queue.insert("A", 5);
    queue.insert("B", 1);
    queue.insert("C", 4);
    queue.insert("D", 3);
    queue.insert("E", 1);

    check(queue.size(), 5, "resizing preserves all entries");
    checkEntry(queue.peek(), "B", 1, "peek returns earliest best entry");
    check(queue.size(), 5, "peek does not remove an entry");

    // B is not last, so extracting it must fill an interior gap.
    checkEntry(queue.extract(), "B", 1, "interior-gap extraction");
    checkEntry(queue.extract(), "E", 1, "stable tied-key extraction");
    checkEntry(queue.extract(), "D", 3, "replacement remains searchable");
    checkEntry(queue.extract(), "C", 4, "next extraction");
    checkEntry(queue.extract(), "A", 5, "worst entry is extracted last");

    check(queue.isEmpty(), true, "queue is empty after all extractions");
    check(queue.size(), 0, "size returns to zero");
}

void testBestEntryAlreadyLast() {
    UnsortedArrayMinPriorityQueue queue(3);
    queue.insert("X", 8);
    queue.insert("Y", 6);
    queue.insert("Z", 2);

    checkEntry(queue.extract(), "Z", 2, "best entry already last");
    check(queue.size(), 2, "last-entry extraction decreases size once");
    checkEntry(queue.extract(), "Y", 6, "remaining entries stay valid");
}

int main() {
    testGapFillingAndStability();
    testBestEntryAlreadyLast();
    cout << (failures == 0
        ? "All tests passed."
        : to_string(failures) + " test(s) failed.")
         << '\n';
    return 0;
}
