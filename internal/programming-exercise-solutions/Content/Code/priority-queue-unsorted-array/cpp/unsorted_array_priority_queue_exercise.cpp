#include <iostream>
#include <optional>
#include <stdexcept>
#include <string>
#include <vector>

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
        return first.priorityKey < second.priorityKey
            || (first.priorityKey == second.priorityKey
                && first.sequenceNumber < second.sequenceNumber);
    }

    int findBestIndex() const {
        int best = 0;
        for (int i = 1; i < count; ++i) if (isBetter(entries[i], entries[best])) best = i;
        return best;
    }

    void ensureCapacity() {
        if (count < capacity) return;
        Entry* larger = new Entry[capacity * 2];
        for (int i = 0; i < count; ++i) larger[i] = entries[i];
        delete[] entries;
        entries = larger;
        capacity *= 2;
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
        return count == 0;
    }

    int size() const {
        return count;
    }

    void insert(const string& value, int priorityKey) {
        Entry newEntry(value, priorityKey, nextSequenceNumber);

        ensureCapacity();
        entries[count++] = newEntry;
        ++nextSequenceNumber;
    }

    optional<Entry> peek() const {
        if (isEmpty()) return nullopt;
        return entries[findBestIndex()];
    }

    optional<Entry> extract() {
        if (isEmpty()) return nullopt;
        const int bestIndex = findBestIndex();
        Entry best = entries[bestIndex];
        --count;
        entries[bestIndex] = entries[count];
        entries[count] = Entry();
        return best;
    }

    vector<string> entriesForTesting() const {
        vector<string> result;
        for (int i = 0; i < count; ++i) {
            result.push_back(entries[i].value + ":" + to_string(entries[i].priorityKey)
                             + "#" + to_string(entries[i].sequenceNumber));
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
    check(queue.entriesForTesting() == vector<string>({
              "A:5#0", "B:1#1", "C:4#2", "D:3#3", "E:1#4"}),
          true, "insertions append entries with increasing sequence numbers");

    check(queue.size(), 5, "resizing preserves all entries");
    checkEntry(queue.peek(), "B", 1, "peek returns earliest best entry");
    check(queue.size(), 5, "peek does not remove an entry");

    // B is not last, so extracting it must fill an interior gap.
    checkEntry(queue.extract(), "B", 1, "interior-gap extraction");
    check(queue.entriesForTesting() == vector<string>({
              "A:5#0", "E:1#4", "C:4#2", "D:3#3"}),
          true, "last entry fills the extracted interior gap");
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

void testLargeStableDrain() {
    UnsortedArrayMinPriorityQueue queue(1);
    for (int i = 0; i < 500; i++) queue.insert("v" + std::to_string(i), i % 17);
    check(queue.size(), 500, "size after large insertion");
    for (int priority = 0; priority < 17; priority++)
        for (int i = priority; i < 500; i += 17)
            checkEntry(queue.extract(), "v" + std::to_string(i), priority, "stable large extraction");
    check(queue.isEmpty(), true, "empty after large drain"); checkEmpty(queue.extract(), "extract after large drain");
}

int main() {
    testGapFillingAndStability();
    testBestEntryAlreadyLast();
    testLargeStableDrain();
    cout << (failures == 0
        ? "All tests passed."
        : to_string(failures) + " test(s) failed.")
         << '\n';
    return failures == 0 ? 0 : 1;
}
