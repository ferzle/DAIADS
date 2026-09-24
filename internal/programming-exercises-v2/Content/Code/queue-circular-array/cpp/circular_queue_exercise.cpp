#include <iostream>
#include <vector>
using namespace std;


int failures = 0;
class IntQueue {
private:
    int* A;
    int capacity;
    int frontIndex;
    int count;

public:
    IntQueue(int cap) {
        capacity = cap;
        A = new int[capacity];
        frontIndex = 0;
        count = 0;
    }

    ~IntQueue() {
        delete[] A;
    }

    bool isEmpty() {
        // TODO
        return false;
    }

    bool isFull() {
        // TODO
        return false;
    }

    bool enqueue(int value) {
        // TODO
        return false;
    }

    int dequeue() {
        // TODO
        return -1;
    }

    int front() {
        // TODO
        return -1;
    }

    int size() {
        // TODO
        return -1;
    }

    vector<int> storageStateForTesting() const {
        return {frontIndex, count, capacity};
    }
};

void checkAtLine(int actual, int expected, int line, const char* expression) {
    if (actual == expected) {
        cout << "PASS at test line " << line << " (" << expression << "): got " << actual << endl;
    } else {
        ++failures;
        cout << "FAIL at test line " << line << " (" << expression
             << "): expected " << expected << " but got " << actual << endl;
    }
}

void checkAtLine(bool actual, bool expected, int line, const char* expression) {
    if (actual == expected) {
        cout << "PASS at test line " << line << " (" << expression << "): got " << actual << endl;
    } else {
        ++failures;
        cout << "FAIL at test line " << line << " (" << expression
             << "): expected " << (expected ? "true" : "false")
             << " but got " << (actual ? "true" : "false") << endl;
    }
}

#define check(actual, expected) checkAtLine((actual), (expected), __LINE__, #actual)

void testQueue() {
    IntQueue queue(4);

    check(queue.isEmpty(), true);
    check(queue.isFull(), false);
    check(queue.dequeue(), -1);
    check(queue.front(), -1);

    check(queue.enqueue(4), true);
    check(queue.enqueue(7), true);
    check(queue.enqueue(9), true);
    check(queue.storageStateForTesting() == vector<int>({0, 3, 4}), true);

    check(queue.dequeue(), 4);
    check(queue.dequeue(), 7);
    check(queue.storageStateForTesting() == vector<int>({2, 1, 4}), true);

    check(queue.enqueue(2), true);
    check(queue.enqueue(5), true);
    check(queue.enqueue(8), true);
    check(queue.storageStateForTesting() == vector<int>({2, 4, 4}), true);
    check(queue.isFull(), true);
    check(queue.enqueue(10), false);

    check(queue.front(), 9);
    check(queue.dequeue(), 9);
    check(queue.dequeue(), 2);
    check(queue.dequeue(), 5);
    check(queue.dequeue(), 8);

    check(queue.isEmpty(), true);
    check(queue.size(), 0);
    check(queue.dequeue(), -1);

    check(queue.enqueue(11), true);
    check(queue.front(), 11);

    IntQueue wrapped(257);
    bool wrappedOk = true;
    for (int round = 0; round < 20; round++) {
        for (int i = 0; i < 257; i++) wrappedOk = wrapped.enqueue(round * 257 + i) && wrappedOk;
        for (int i = 0; i < 257; i++) wrappedOk = (wrapped.dequeue() == round * 257 + i) && wrappedOk;
    }
    check(wrappedOk && wrapped.isEmpty(), true);
}

void testRepeatedWraparound() {
    for (int capacity : {1, 2, 5, 64}) {
        IntQueue queue(capacity);
        int nextExpected = 0;
        for (int round = 0; round < 20; round++) {
            for (int i = 0; i < capacity; i++)
                check(queue.enqueue(round * capacity + i), true);
            check(queue.isFull(), true);
            check(queue.enqueue(999999), false);
            for (int i = 0; i < capacity; i++) {
                check(queue.front(), nextExpected);
                check(queue.dequeue(), nextExpected++);
                check(queue.size(), capacity - i - 1);
            }
            check(queue.isEmpty(), true);
        }
    }
}

void testLargeAggregateWraparound() {
    const int capacity = 10000;
    IntQueue queue(capacity);
    bool ok = true;
    for (int round = 0; round < 10; ++round) {
        for (int i = 0; i < capacity; ++i) ok = queue.enqueue(round * capacity + i) && ok;
        ok = queue.isFull() && !queue.enqueue(-1) && ok;
        for (int i = 0; i < capacity; ++i) ok = (queue.dequeue() == round * capacity + i) && ok;
    }
    ok = queue.isEmpty() && queue.size() == 0 && ok;
    check(ok, true);
}

int main() {
    testQueue();
    testRepeatedWraparound();
    testLargeAggregateWraparound();
    return failures == 0 ? 0 : 1;
}
