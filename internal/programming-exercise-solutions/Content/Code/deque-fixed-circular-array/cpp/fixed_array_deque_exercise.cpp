#include <iostream>
#include <vector>

using namespace std;


int failures = 0;
class IntDeque {
private:
int* A;
int capacity;
int front;
int count;

int backIndex() {
    return (front + count - 1) % capacity;
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
    return count == 0;
}

bool isFull() {
    return count == capacity;
}

int size() {
    return count;
}

void clear() {
    front = 0;
    count = 0;
}

int peekFront() {
    return isEmpty() ? -1 : A[front];
}

int peekBack() {
    return isEmpty() ? -1 : A[backIndex()];
}

bool addFront(int value) {
    if (isFull()) return false;
    front = (front - 1 + capacity) % capacity;
    A[front] = value;
    ++count;
    return true;
}

bool addBack(int value) {
    if (isFull()) return false;
    int insertionIndex = (front + count) % capacity;
    A[insertionIndex] = value;
    ++count;
    return true;
}

int removeFront() {
    if (isEmpty()) return -1;
    int value = A[front];
    front = (front + 1) % capacity;
    --count;
    return value;
}

int removeBack() {
    if (isEmpty()) return -1;
    int value = A[backIndex()];
    --count;
    return value;
}

vector<int> storageStateForTesting() const {
    return {front, count, capacity};
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
check(deque.storageStateForTesting() == vector<int>({4, 4, 5}), true);

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
check(deque.storageStateForTesting() == vector<int>({0, 5, 5}), true);
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

IntDeque large(257);
bool largeOk = true;
for (int round = 0; round < 20; round++) {
    for (int i = 0; i < 257; i++) largeOk = large.addBack(round * 257 + i) && largeOk;
    largeOk = large.isFull() && !large.addFront(-1) && largeOk;
    for (int i = 0; i < 257; i++) largeOk = (large.removeFront() == round * 257 + i) && largeOk;
}
check(largeOk && large.isEmpty(), true);

}

void testCapacitiesWraparoundAndReuse() {
    for (int capacity : {1, 2, 3, 8, 64}) {
        IntDeque deque(capacity);
        for (int round = 0; round < 20; round++) {
            for (int i = 0; i < capacity; i++) check(deque.addBack(round * capacity + i), true);
            check(deque.isFull(), true); check(deque.addFront(999999), false);
            for (int i = 0; i < capacity; i++) check(deque.removeFront(), round * capacity + i);
            check(deque.isEmpty(), true);
            for (int i = 0; i < capacity; i++) check(deque.addFront(round * capacity + i), true);
            for (int i = 0; i < capacity; i++) check(deque.removeBack(), round * capacity + i);
            check(deque.isEmpty(), true);
        }
        deque.addBack(7); deque.clear(); check(deque.isEmpty(), true);
        check(deque.addFront(8), true); check(deque.removeBack(), 8);
    }
}

void testLargeAggregateWraparound() {
    const int capacity = 10000;
    IntDeque deque(capacity);
    bool ok = true;
    for (int round = 0; round < 10; ++round) {
        for (int i = 0; i < capacity; ++i) ok = deque.addBack(round * capacity + i) && ok;
        for (int i = 0; i < capacity; ++i) ok = (deque.removeFront() == round * capacity + i) && ok;
    }
    ok = deque.isEmpty() && deque.size() == 0 && ok;
    check(ok, true);
}

int main() {
    testDeque();
    testCapacitiesWraparoundAndReuse();
    testLargeAggregateWraparound();
  return failures == 0 ? 0 : 1;
}
