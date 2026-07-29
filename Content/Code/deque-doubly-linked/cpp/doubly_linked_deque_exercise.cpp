#include <iostream>
using namespace std;

class IntDeque {
private:
struct Node {
    int value;
    Node* next;
    Node* prev;

    Node(int v) {
        value = v;
        next = nullptr;
        prev = nullptr;
    }
};

Node* frontNode;
Node* backNode;
int count;

public:
IntDeque() {
    frontNode = nullptr;
    backNode = nullptr;
    count = 0;
}

~IntDeque() {
    clear();
}

bool isEmpty() {
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

void checkAtLine(int actual, int expected, int line, const char* expression) {
    if (actual == expected) {
        cout << "pass" << endl;
    } else {
        cout << "fail at test line " << line << " (" << expression
             << "): expected " << expected << " but got " << actual << endl;
    }
}

void checkAtLine(bool actual, bool expected, int line, const char* expression) {
    if (actual == expected) {
        cout << "pass" << endl;
    } else {
        cout << "fail at test line " << line << " (" << expression
             << "): expected " << (expected ? "true" : "false")
             << " but got " << (actual ? "true" : "false") << endl;
    }
}

#define check(actual, expected) checkAtLine((actual), (expected), __LINE__, #actual)

void testDeque() {
IntDeque deque;

check(deque.isEmpty(), true);
check(deque.size(), 0);
check(deque.peekFront(), -1);
check(deque.peekBack(), -1);
check(deque.removeFront(), -1);
check(deque.removeBack(), -1);

check(deque.addBack(4), true);       // [4]
check(deque.peekFront(), 4);
check(deque.peekBack(), 4);
check(deque.size(), 1);
check(deque.isEmpty(), false);

check(deque.addBack(7), true);       // [4, 7]
check(deque.addFront(2), true);      // [2, 4, 7]
check(deque.addBack(9), true);       // [2, 4, 7, 9]

check(deque.size(), 4);
check(deque.peekFront(), 2);
check(deque.peekBack(), 9);

check(deque.removeFront(), 2);       // [4, 7, 9]
check(deque.peekFront(), 4);
check(deque.peekBack(), 9);
check(deque.size(), 3);

check(deque.removeBack(), 9);        // [4, 7]
check(deque.peekFront(), 4);
check(deque.peekBack(), 7);
check(deque.size(), 2);

check(deque.addFront(1), true);      // [1, 4, 7]
check(deque.addBack(11), true);      // [1, 4, 7, 11]
check(deque.addFront(0), true);      // [0, 1, 4, 7, 11]

check(deque.size(), 5);
check(deque.peekFront(), 0);
check(deque.peekBack(), 11);

check(deque.removeBack(), 11);       // [0, 1, 4, 7]
check(deque.removeFront(), 0);       // [1, 4, 7]
check(deque.removeBack(), 7);        // [1, 4]
check(deque.removeFront(), 1);       // [4]

check(deque.size(), 1);
check(deque.peekFront(), 4);
check(deque.peekBack(), 4);

check(deque.removeBack(), 4);        // []
check(deque.isEmpty(), true);
check(deque.size(), 0);
check(deque.peekFront(), -1);
check(deque.peekBack(), -1);
check(deque.removeFront(), -1);
check(deque.removeBack(), -1);

check(deque.addFront(6), true);      // [6]
check(deque.peekFront(), 6);
check(deque.peekBack(), 6);

check(deque.addBack(8), true);       // [6, 8]
check(deque.peekFront(), 6);
check(deque.peekBack(), 8);

deque.clear();                       // []
check(deque.isEmpty(), true);
check(deque.size(), 0);
check(deque.peekFront(), -1);
check(deque.peekBack(), -1);

check(deque.addBack(10), true);      // [10]
check(deque.removeFront(), 10);      // []
check(deque.isEmpty(), true);
check(deque.size(), 0);
}

int main() {
  testDeque();
  return 0;
}
