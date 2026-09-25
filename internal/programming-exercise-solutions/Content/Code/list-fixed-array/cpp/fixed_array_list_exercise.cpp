#include <iostream>
#include <vector>

using namespace std;


int failures = 0;
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
    return count == 0;
}

int size() {
    return count;
}

void clear() {
    count = 0;
}

int first() {
    return isEmpty() ? -1 : A[0];
}

int last() {
    return isEmpty() ? -1 : A[count - 1];
}

int get(int index) {
    return index < 0 || index >= count ? -1 : A[index];
}

bool set(int index, int value) {
    if (index < 0 || index >= count) return false;
    A[index] = value;
    return true;
}

bool addFirst(int value) {
    return insert(0, value);
}

bool addLast(int value) {
    return insert(count, value);
}

bool insert(int index, int value) {
    if (index < 0 || index > count || count == capacity) return false;
    for (int i = count; i > index; --i) A[i] = A[i - 1];
    A[index] = value;
    ++count;
    return true;
}

int removeFirst() {
    return remove(0);
}

int removeLast() {
    return remove(count - 1);
}

int remove(int index) {
    if (index < 0 || index >= count) return -1;
    int value = A[index];
    for (int i = index; i < count - 1; ++i) A[i] = A[i + 1];
    --count;
    return value;
}

int indexOf(int value) {
    for (int i = 0; i < count; ++i) {
        if (A[i] == value) return i;
    }
    return -1;
}

bool contains(int value) {
    return indexOf(value) != -1;
}

bool deleteValue(int value) {
    int index = indexOf(value);
    if (index == -1) return false;
    remove(index);
    return true;
}

vector<int> usedValuesForTesting() const {
    return vector<int>(A, A + count);
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
check(list.usedValuesForTesting() == vector<int>({2, 4, 9, 7}), true);

check(list.size(), 4);
check(list.isEmpty(), false);
check(list.first(), 2);
check(list.last(), 7);
check(list.get(0), 2);
check(list.get(2), 9);
check(list.get(4), -1);
check(list.get(-1), -1);
check(list.set(-1, 8), false);
check(list.insert(-1, 8), false);
check(list.remove(-1), -1);

check(list.set(1, 5), true);        // [2, 5, 9, 7]
check(list.get(1), 5);
check(list.set(4, 8), false);
check(list.size(), 4);

check(list.addLast(11), true);      // [2, 5, 9, 7, 11]
check(list.size(), 5);
check(list.addLast(13), false);     // full
check(list.addFirst(13), false);    // full
check(list.insert(2, 13), false);   // full
check(list.usedValuesForTesting() == vector<int>({2, 5, 9, 7, 11}), true);
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

IntList duplicates(8);
check(duplicates.addLast(5), true);
check(duplicates.addLast(2), true);
check(duplicates.addLast(5), true);
check(duplicates.addLast(5), true);
check(duplicates.indexOf(5), 0);
check(duplicates.deleteValue(5), true);
check(duplicates.size(), 3);
check(duplicates.indexOf(5), 1);

IntList large(256);
bool largeOk = true;
for (int i = 0; i < 256; i++) largeOk = large.addLast(i) && largeOk;
for (int i = 0; i < 256; i++) largeOk = (large.get(i) == i) && largeOk;
for (int i = 0; i < 256; i++) largeOk = (large.removeFirst() == i) && largeOk;
check(largeOk && large.isEmpty(), true);

}

void testLargeIndexedWorkload() {
    IntList list(256);
    for (int i = 0; i < 200; i++) check(list.addLast(i % 17), true);
    check(list.size(), 200); check(list.first(), 0); check(list.last(), 12);
    check(list.insert(100, 999), true); check(list.get(100), 999); check(list.size(), 201);
    check(list.remove(100), 999); check(list.size(), 200);
    check(list.indexOf(5), 5); check(list.deleteValue(5), true); check(list.indexOf(5), 21);
    check(list.set(0, 777), true); check(list.get(0), 777);
    check(list.insert(-1, 8), false); check(list.insert(list.size() + 1, 8), false);
    check(list.size(), 199);
    for (int i = 0; i < 199; i++) list.removeLast();
    check(list.size(), 0); check(list.addFirst(42), true); check(list.removeFirst(), 42);
}

void testHundredThousandAppendsAndReads() {
    const int count = 100000;
    IntList list(count);
    bool ok = true;
    for (int i = 0; i < count; i++) ok = list.addLast(i) && ok;
    for (int i = 0; i < count; i++) ok = (list.get(i) == i) && ok;
    check(ok && list.size() == count && list.first() == 0 && list.last() == count - 1, true);
}

int main() {
    testList();
    testLargeIndexedWorkload();
    testHundredThousandAppendsAndReads();
  return failures == 0 ? 0 : 1;
}
