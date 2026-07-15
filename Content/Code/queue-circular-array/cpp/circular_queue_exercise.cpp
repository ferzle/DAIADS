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
}

int main() {
    testQueue();
    return 0;
}
