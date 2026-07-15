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

void check(int actual, int expected) {
    if (actual == expected) {
        cout << "pass" << endl;
    } else {
        cout << "fail: expected " << expected << " but got " << actual << endl;
    }
}

void check(bool actual, bool expected) {
    if (actual == expected) {
        cout << "pass" << endl;
    } else {
        cout << "fail: expected " << expected << " but got " << actual << endl;
    }
}

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
}

int main() {
    testQueue();
    return 0;
}
