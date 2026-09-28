#include <iostream>
using namespace std;


int failures = 0;
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

bool hasValidStructureForTesting() const {
    if ((frontNode == nullptr) != (backNode == nullptr)) return false;
    if (frontNode != nullptr && frontNode->prev != nullptr) return false;
    if (backNode != nullptr && backNode->next != nullptr) return false;
    int reachable = 0;
    Node* previous = nullptr;
    for (Node* node = frontNode; node != nullptr; node = node->next) {
        if (node->prev != previous || reachable > count) return false;
        previous = node;
        ++reachable;
    }
    if (previous != backNode || reachable != count) return false;
    int backward = 0;
    Node* next = nullptr;
    for (Node* node = backNode; node != nullptr; node = node->prev) {
        if (node->next != next || backward > count) return false;
        next = node;
        ++backward;
    }
    return next == frontNode && backward == count;
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
    check(deque.hasValidStructureForTesting(), true);

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
    check(deque.hasValidStructureForTesting(), true);
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

IntDeque transitions;
bool transitionOk = true;
for (int i = 0; i < 500; i++) {
    transitionOk = transitions.addFront(i) && transitionOk;
    transitionOk = transitions.addBack(-i) && transitionOk;
    transitionOk = (transitions.removeFront() == i) && transitionOk;
    transitionOk = (transitions.removeBack() == -i) && transitionOk;
    transitionOk = transitions.isEmpty() && transitionOk;
}
check(transitionOk, true);
}

void testLargeMixedRuns() {
    IntDeque deque;
    for (int round = 0; round < 20; round++) {
        for (int i = 0; i < 100; i++) {
            check(deque.addFront(round * 1000 + i), true);
            check(deque.addBack(round * 1000 + 500 + i), true);
        }
        check(deque.size(), 200);
        for (int i = 99; i >= 0; i--) check(deque.removeFront(), round * 1000 + i);
        for (int i = 99; i >= 0; i--) check(deque.removeBack(), round * 1000 + 500 + i);
        check(deque.isEmpty(), true);
    }
    deque.addFront(7); deque.clear();
    check(deque.isEmpty(), true); check(deque.peekFront(), -1); check(deque.peekBack(), -1);
    check(deque.addBack(8), true); check(deque.removeFront(), 8);
}

void testLargeAggregateWorkload() {
    const int n = 100000;
    IntDeque deque;
    bool ok = true;
    for (int i = 0; i < n; ++i) ok = deque.addBack(i) && ok;
    ok = deque.size() == n && deque.peekFront() == 0
        && deque.peekBack() == n - 1 && deque.hasValidStructureForTesting() && ok;
    for (int i = 0; i < n; ++i) ok = (deque.removeFront() == i) && ok;
    ok = deque.isEmpty() && deque.hasValidStructureForTesting() && ok;
    check(ok, true);
}

int main() {
    testDeque();
    testLargeMixedRuns();
    testLargeAggregateWorkload();
  return failures == 0 ? 0 : 1;
}
