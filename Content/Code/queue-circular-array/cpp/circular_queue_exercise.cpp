#include <iostream>
using namespace std;

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
};

void checkAtLine(int actual, int expected, int line, const char* expression) {
    if (actual == expected) {
        cout << "PASS at test line " << line << " (" << expression << "): got " << actual << endl;
    } else {
        cout << "FAIL at test line " << line << " (" << expression
             << "): expected " << expected << " but got " << actual << endl;
    }
}

void checkAtLine(bool actual, bool expected, int line, const char* expression) {
    if (actual == expected) {
        cout << "PASS at test line " << line << " (" << expression << "): got " << actual << endl;
    } else {
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

    check(queue.dequeue(), 4);
    check(queue.dequeue(), 7);

    check(queue.enqueue(2), true);
    check(queue.enqueue(5), true);
    check(queue.enqueue(8), true);
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

int main() {
    testQueue();
    return 0;
}
