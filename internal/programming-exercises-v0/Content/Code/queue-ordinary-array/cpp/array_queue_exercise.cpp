#include <iostream>
using namespace std;

class IntQueue {
private:
    int* A;
    int capacity;
    int count;

public:
    IntQueue(int cap) {
        capacity = cap;
        A = new int[capacity];
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
    IntQueue queue(3);

    check(queue.isEmpty(), true);
    check(queue.isFull(), false);
    check(queue.size(), 0);
    check(queue.dequeue(), -1);
    check(queue.front(), -1);

    check(queue.enqueue(4), true);
    check(queue.enqueue(7), true);
    check(queue.enqueue(9), true);
    check(queue.isFull(), true);
    check(queue.enqueue(2), false);

    check(queue.front(), 4);
    check(queue.dequeue(), 4);
    check(queue.front(), 7);
    check(queue.size(), 2);

    check(queue.enqueue(2), true);
    check(queue.dequeue(), 7);
    check(queue.dequeue(), 9);
    check(queue.dequeue(), 2);

    check(queue.isEmpty(), true);
    check(queue.size(), 0);
    check(queue.dequeue(), -1);

    IntQueue large(1024);
    bool largeOk = true;
    for (int i = 0; i < 1024; i++) largeOk = large.enqueue(i) && largeOk;
    largeOk = large.isFull() && !large.enqueue(1024) && largeOk;
    for (int i = 0; i < 1024; i++) largeOk = (large.dequeue() == i) && largeOk;
    check(largeOk && large.isEmpty(), true);
}

int main() {
    testQueue();
    return 0;
}
