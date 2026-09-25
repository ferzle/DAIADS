#include <iostream>
using namespace std;


int failures = 0;
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
    if (index < 0 || index >= count) return nullptr;
    Node* node = head;
    for (int i = 0; i < index; ++i) node = node->next;
    return node;
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
    return count == 0;
}

int size() {
    return count;
}

void clear() {
    while (head != nullptr) {
        Node* oldHead = head;
        head = head->next;
        delete oldHead;
    }
    tail = nullptr;
    count = 0;
}

int first() {
    return isEmpty() ? -1 : head->value;
}

int last() {
    return isEmpty() ? -1 : tail->value;
}

int get(int index) {
    Node* node = nodeAt(index);
    return node == nullptr ? -1 : node->value;
}

bool set(int index, int value) {
    Node* node = nodeAt(index);
    if (node == nullptr) return false;
    node->value = value;
    return true;
}

bool addFirst(int value) {
    Node* node = new Node(value);
    node->next = head;
    head = node;
    if (tail == nullptr) tail = node;
    ++count;
    return true;
}

bool addLast(int value) {
    Node* node = new Node(value);
    if (isEmpty()) head = node;
    else tail->next = node;
    tail = node;
    ++count;
    return true;
}

bool insert(int index, int value) {
    if (index < 0 || index > count) return false;
    if (index == 0) return addFirst(value);
    if (index == count) return addLast(value);
    Node* previous = nodeAt(index - 1);
    Node* node = new Node(value);
    node->next = previous->next;
    previous->next = node;
    ++count;
    return true;
}

int removeFirst() {
    if (isEmpty()) return -1;
    Node* oldHead = head;
    int value = oldHead->value;
    head = head->next;
    delete oldHead;
    --count;
    if (head == nullptr) tail = nullptr;
    return value;
}

int removeLast() {
    if (isEmpty()) return -1;
    if (count == 1) return removeFirst();
    Node* previous = nodeAt(count - 2);
    int value = tail->value;
    delete tail;
    tail = previous;
    tail->next = nullptr;
    --count;
    return value;
}

int remove(int index) {
    if (index < 0 || index >= count) return -1;
    if (index == 0) return removeFirst();
    if (index == count - 1) return removeLast();
    Node* previous = nodeAt(index - 1);
    Node* oldNode = previous->next;
    int value = oldNode->value;
    previous->next = oldNode->next;
    delete oldNode;
    --count;
    return value;
}

int indexOf(int value) {
    int index = 0;
    for (Node* node = head; node != nullptr; node = node->next) {
        if (node->value == value) return index;
        ++index;
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

void testLargeIndexedWorkload() {
    IntList list;
    for (int i = 0; i < 200; i++) check(list.addLast(i % 17), true);
    check(list.size(), 200); check(list.first(), 0); check(list.last(), 12);
    check(list.insert(100, 999), true); check(list.get(100), 999); check(list.size(), 201);
    check(list.remove(100), 999); check(list.size(), 200);
    check(list.indexOf(5), 5); check(list.deleteValue(5), true); check(list.indexOf(5), 21);
    check(list.set(0, 777), true); check(list.get(0), 777);
    for (int i = 0; i < 199; i++) list.removeLast();
    check(list.size(), 0); check(list.addFirst(42), true); check(list.removeFirst(), 42);
    check(list.isEmpty(), true); check(list.last(), -1);
}

void testHundredThousandEndpointOperations() {
    const int count = 100000;
    IntList list;
    bool ok = true;
    for (int i = 0; i < count; i++) ok = list.addLast(i) && ok;
    ok = list.size() == count && list.first() == 0 && list.last() == count - 1 && ok;
    for (int i = 0; i < count; i++) ok = (list.removeFirst() == i) && ok;
    check(ok && list.isEmpty() && list.size() == 0 && list.first() == -1 && list.last() == -1, true);
}

int main() {
    testList();
    testLargeIndexedWorkload();
    testHundredThousandEndpointOperations();
  return failures == 0 ? 0 : 1;
}
