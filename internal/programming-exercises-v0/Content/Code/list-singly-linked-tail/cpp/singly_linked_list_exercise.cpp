#include <iostream>
using namespace std;

class IntList {
private:
struct Node {
    int value;
    Node* next;

    Node(int v) {
        value = v;
        next = nullptr;
    }
};

Node* head;
Node* tail;
int count;

Node* nodeAt(int index) {
    // TODO
    return nullptr;
}

public:
IntList() {
    head = nullptr;
    tail = nullptr;
    count = 0;
}


~IntList() {
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

int first() {
    // TODO
    return -1;
}

int last() {
    // TODO
    return -1;
}

int get(int index) {
    // TODO
    return -1;
}

bool set(int index, int value) {
    // TODO
    return false;
}

bool addFirst(int value) {
    // TODO
    return false;
}

bool addLast(int value) {
    // TODO
    return false;
}

bool insert(int index, int value) {
    // TODO
    return false;
}

int removeFirst() {
    // TODO
    return -1;
}

int removeLast() {
    // TODO
    return -1;
}

int remove(int index) {
    // TODO
    return -1;
}

int indexOf(int value) {
    // TODO
    return -1;
}

bool contains(int value) {
    // TODO
    return false;
}

bool deleteValue(int value) {
    // TODO
    return false;
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

void testList() {
IntList list;

check(list.isEmpty(), true);
check(list.size(), 0);
check(list.first(), -1);
check(list.last(), -1);
check(list.get(0), -1);
check(list.removeFirst(), -1);
check(list.removeLast(), -1);
check(list.remove(0), -1);

check(list.addLast(4), true);       // [4]
check(list.addLast(7), true);       // [4, 7]
check(list.addFirst(2), true);      // [2, 4, 7]
check(list.insert(2, 9), true);     // [2, 4, 9, 7]

check(list.size(), 4);
check(list.isEmpty(), false);
check(list.first(), 2);
check(list.last(), 7);
check(list.get(0), 2);
check(list.get(2), 9);
check(list.get(4), -1);
check(list.get(-1), -1);
check(list.set(-1, 8), false);
check(list.set(99, 8), false);
check(list.insert(-1, 8), false);
check(list.insert(99, 8), false);
check(list.remove(-1), -1);
check(list.remove(99), -1);

check(list.set(1, 5), true);        // [2, 5, 9, 7]
check(list.get(1), 5);
check(list.set(4, 8), false);
check(list.size(), 4);

check(list.insert(0, 11), true);    // [11, 2, 5, 9, 7]
check(list.first(), 11);
check(list.insert(list.size(), 13), true); // [11, 2, 5, 9, 7, 13]
check(list.last(), 13);
check(list.size(), 6);

check(list.indexOf(9), 3);
check(list.indexOf(100), -1);
check(list.contains(7), true);
check(list.contains(100), false);

check(list.remove(3), 9);           // [11, 2, 5, 7, 13]
check(list.get(3), 7);
check(list.removeFirst(), 11);      // [2, 5, 7, 13]
check(list.removeLast(), 13);       // [2, 5, 7]
check(list.size(), 3);
check(list.first(), 2);
check(list.last(), 7);

check(list.deleteValue(2), true);   // [5, 7]
check(list.first(), 5);
check(list.deleteValue(7), true);   // [5]
check(list.last(), 5);
check(list.deleteValue(5), true);   // []
check(list.isEmpty(), true);
check(list.size(), 0);
check(list.first(), -1);
check(list.last(), -1);

check(list.deleteValue(5), false);
check(list.removeLast(), -1);

check(list.addLast(6), true);       // [6]
check(list.first(), 6);
check(list.last(), 6);
check(list.removeLast(), 6);        // []
check(list.isEmpty(), true);

check(list.addFirst(8), true);      // [8]
check(list.addLast(10), true);      // [8, 10]
list.clear();                       // []
check(list.isEmpty(), true);
check(list.size(), 0);
check(list.first(), -1);

IntList large;
bool largeOk = true;
for (int i = 0; i < 1000; i++) largeOk = large.addLast(i) && largeOk;
for (int i = 0; i < 1000; i++) largeOk = (large.get(i) == i) && largeOk;
for (int i = 999; i >= 0; i--) largeOk = (large.removeLast() == i) && largeOk;
check(largeOk && large.isEmpty(), true);
}

int main() {
  testList();
  return 0;
}
