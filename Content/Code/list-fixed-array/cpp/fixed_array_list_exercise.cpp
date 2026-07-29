#include <iostream>

using namespace std;

class IntList {
private:
int* A;
int capacity;
int count;

public:
IntList(int cap) {
    capacity = cap;
    A = new int[capacity];
    count = 0;
}

~IntList() {
    delete[] A;
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

void testList() {
IntList list(5);

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

check(list.set(1, 5), true);        // [2, 5, 9, 7]
check(list.get(1), 5);
check(list.set(4, 8), false);
check(list.size(), 4);

check(list.addLast(11), true);      // [2, 5, 9, 7, 11]
check(list.size(), 5);
check(list.addLast(13), false);     // full
check(list.addFirst(13), false);    // full
check(list.insert(2, 13), false);   // full
check(list.size(), 5);

check(list.indexOf(9), 2);
check(list.indexOf(100), -1);
check(list.contains(7), true);
check(list.contains(100), false);

check(list.remove(2), 9);           // [2, 5, 7, 11]
check(list.get(2), 7);
check(list.removeFirst(), 2);       // [5, 7, 11]
check(list.removeLast(), 11);       // [5, 7]
check(list.size(), 2);
check(list.first(), 5);
check(list.last(), 7);

check(list.deleteValue(5), true);   // [7]
check(list.deleteValue(5), false);
check(list.size(), 1);
check(list.first(), 7);
check(list.last(), 7);

check(list.removeLast(), 7);        // []
check(list.isEmpty(), true);
check(list.size(), 0);
check(list.removeLast(), -1);

check(list.addLast(6), true);       // [6]
check(list.addLast(8), true);       // [6, 8]
list.clear();                       // []
check(list.isEmpty(), true);
check(list.size(), 0);
check(list.first(), -1);

}

int main() {
  testList();
  return 0;
}
