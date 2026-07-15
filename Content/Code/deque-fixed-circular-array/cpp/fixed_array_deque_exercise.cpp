#include <iostream>

using namespace std;

class IntDeque {
private:
int* A;
int capacity;
int front;
int count;

int backIndex() {
    // TODO
    return -1;
}

public:
IntDeque(int cap) {
    capacity = cap;
    A = new int[capacity];
    front = 0;
    count = 0;
}

~IntDeque() {
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

int size() {
    // TODO
    return -1;
}

void clear() {
    // TODO
}

int peekFront() {
    // TODO
    return -1;
}

int peekBack() {
    // TODO
    return -1;
}

bool addFront(int value) {
    // TODO
    return false;
}

bool addBack(int value) {
    // TODO
    return false;
}

int removeFront() {
    // TODO
    return -1;
}

int removeBack() {
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

void testDeque() {
IntDeque deque(5);

check(deque.isEmpty(), true);
check(deque.isFull(), false);
check(deque.size(), 0);
check(deque.peekFront(), -1);
check(deque.peekBack(), -1);
check(deque.removeFront(), -1);
check(deque.removeBack(), -1);

check(deque.addBack(4), true);       // [4]
check(deque.peekFront(), 4);
check(deque.peekBack(), 4);
check(deque.size(), 1);

check(deque.addBack(7), true);       // [4, 7]
check(deque.addFront(2), true);      // [2, 4, 7]
check(deque.addBack(9), true);       // [2, 4, 7, 9]

check(deque.size(), 4);
check(deque.isEmpty(), false);
check(deque.peekFront(), 2);
check(deque.peekBack(), 9);

check(deque.removeFront(), 2);       // [4, 7, 9]
check(deque.peekFront(), 4);
check(deque.removeBack(), 9);        // [4, 7]
check(deque.peekBack(), 7);
check(deque.size(), 2);

check(deque.addFront(1), true);      // [1, 4, 7]
check(deque.addFront(0), true);      // [0, 1, 4, 7]
check(deque.addBack(11), true);      // [0, 1, 4, 7, 11]
check(deque.isFull(), true);
check(deque.size(), 5);
check(deque.peekFront(), 0);
check(deque.peekBack(), 11);

check(deque.addBack(13), false);     // full
check(deque.addFront(13), false);    // full
check(deque.size(), 5);
check(deque.peekFront(), 0);
check(deque.peekBack(), 11);

check(deque.removeFront(), 0);       // [1, 4, 7, 11]
check(deque.removeFront(), 1);       // [4, 7, 11]
check(deque.addBack(13), true);      // [4, 7, 11, 13]
check(deque.addBack(15), true);      // [4, 7, 11, 13, 15]
check(deque.isFull(), true);
check(deque.peekFront(), 4);
check(deque.peekBack(), 15);

check(deque.removeBack(), 15);       // [4, 7, 11, 13]
check(deque.removeBack(), 13);       // [4, 7, 11]
check(deque.addFront(3), true);      // [3, 4, 7, 11]
check(deque.addFront(2), true);      // [2, 3, 4, 7, 11]
check(deque.peekFront(), 2);
check(deque.peekBack(), 11);

check(deque.removeFront(), 2);       // [3, 4, 7, 11]
check(deque.removeBack(), 11);       // [3, 4, 7]
check(deque.removeFront(), 3);       // [4, 7]
check(deque.removeBack(), 7);        // [4]
check(deque.removeBack(), 4);        // []

check(deque.isEmpty(), true);
check(deque.size(), 0);
check(deque.peekFront(), -1);
check(deque.peekBack(), -1);
check(deque.removeFront(), -1);
check(deque.removeBack(), -1);

check(deque.addFront(6), true);      // [6]
check(deque.addBack(8), true);       // [6, 8]
deque.clear();                       // []
check(deque.isEmpty(), true);
check(deque.size(), 0);
check(deque.peekFront(), -1);

}

int main() {
  testDeque();
  return 0;
}
