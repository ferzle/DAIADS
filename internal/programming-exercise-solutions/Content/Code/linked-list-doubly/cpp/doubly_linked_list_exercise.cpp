#include <iostream>
#include <sstream>
#include <string>
using namespace std;


int failures = 0;
class IntDoublyList {
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

    Node* head;
    Node* tail;
    int count;

public:
    IntDoublyList() {
        head = nullptr;
        tail = nullptr;
        count = 0;
    }

    ~IntDoublyList() {
        while (head != nullptr) {
            Node* old = head;
            head = head->next;
            delete old;
        }
    }

    bool isEmpty() {
        return count == 0;
    }

    int size() {
        return count;
    }

    void insertAtHead(int value) {
        Node* newNode = new Node(value);

        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode->next = head;
            head->prev = newNode;
            head = newNode;
        }

        count++;
    }

    void insertAtTail(int value) {
        Node* newNode = new Node(value);
        if (isEmpty()) head = newNode;
        else { tail->next = newNode; newNode->prev = tail; }
        tail = newNode;
        ++count;
    }

    int deleteFromHead() {
        if (isEmpty()) {
            return -1;
        }

        int value = head->value;

        if (head == tail) {
            delete head;
            head = nullptr;
            tail = nullptr;
        } else {
            Node* oldHead = head;
            head = head->next;
            head->prev = nullptr;
            delete oldHead;
        }

        count--;
        return value;
    }

    int deleteFromTail() {
        if (isEmpty()) return -1;
        Node* oldTail = tail;
        int value = oldTail->value;
        if (head == tail) head = tail = nullptr;
        else { tail = tail->prev; tail->next = nullptr; }
        delete oldTail;
        --count;
        return value;
    }

    string traverseForward() {
        ostringstream result;
        for (Node* node = head; node != nullptr; node = node->next) {
            if (node != head) result << " -> ";
            result << node->value;
        }
        return result.str();
    }

    string traverseBackward() {
        ostringstream result;
        for (Node* node = tail; node != nullptr; node = node->prev) {
            if (node != tail) result << " -> ";
            result << node->value;
        }
        return result.str();
    }

    Node* searchForward(int value) {
        for (Node* node = head; node != nullptr; node = node->next) {
            if (node->value == value) return node;
        }
        return nullptr;
    }

    void insertAfter(Node* node, int value) {
        if (node == nullptr) return;
        if (node == tail) { insertAtTail(value); return; }
        Node* newNode = new Node(value);
        newNode->prev = node;
        newNode->next = node->next;
        node->next->prev = newNode;
        node->next = newNode;
        ++count;
    }

    void insertBefore(Node* node, int value) {
        if (node == nullptr) return;
        if (node == head) { insertAtHead(value); return; }
        Node* newNode = new Node(value);
        newNode->prev = node->prev;
        newNode->next = node;
        node->prev->next = newNode;
        node->prev = newNode;
        ++count;
    }

    int deleteNode(Node* node) {
        if (node == nullptr) return -1;
        if (node == head) return deleteFromHead();
        if (node == tail) return deleteFromTail();
        int value = node->value;
        node->prev->next = node->next;
        node->next->prev = node->prev;
        delete node;
        --count;
        return value;
    }
};

void checkAtLine(const string& actual, const string& expected, int line, const char* expression) {
    if (actual == expected) {
        cout << "PASS at test line " << line << " (" << expression << "): got \"" << actual << "\"" << endl;
    } else {
        ++failures;
        cout << "FAIL at test line " << line << " (" << expression
             << "): expected \"" << expected << "\" but got \"" << actual << "\"" << endl;
    }
}

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

void testDoublyList() {
    IntDoublyList list;

    check(list.isEmpty(), true);
    check(list.size(), 0);
    check(list.deleteFromHead(), -1);
    check(list.deleteFromTail(), -1);
    check(list.traverseForward(), "");
    check(list.traverseBackward(), "");

    list.insertAtHead(7);
    check(list.traverseForward(), "7");
    check(list.traverseBackward(), "7");
    check(list.size(), 1);

    list.insertAtHead(4);
    check(list.traverseForward(), "4 -> 7");
    check(list.traverseBackward(), "7 -> 4");

    list.insertAtTail(9);
    check(list.traverseForward(), "4 -> 7 -> 9");
    check(list.traverseBackward(), "9 -> 7 -> 4");
    check(list.size(), 3);

    check(list.deleteFromHead(), 4);
    check(list.traverseForward(), "7 -> 9");
    check(list.traverseBackward(), "9 -> 7");

    check(list.deleteFromTail(), 9);
    check(list.traverseForward(), "7");
    check(list.traverseBackward(), "7");

    check(list.deleteFromTail(), 7);
    check(list.traverseForward(), "");
    check(list.traverseBackward(), "");
    check(list.isEmpty(), true);
    check(list.size(), 0);

    list.insertAtTail(12);
    check(list.traverseForward(), "12");
    check(list.traverseBackward(), "12");
    check(list.deleteFromHead(), 12);
    check(list.isEmpty(), true);

    list.insertAtTail(4);
    list.insertAtTail(7);
    list.insertAtTail(9);
    auto* node7 = list.searchForward(7);
    check(node7 != nullptr, true);
    check(list.searchForward(99) == nullptr, true);
    list.insertBefore(node7, 6);
    list.insertAfter(node7, 8);
    check(list.traverseForward(), "4 -> 6 -> 7 -> 8 -> 9");
    check(list.traverseBackward(), "9 -> 8 -> 7 -> 6 -> 4");
    check(list.deleteNode(node7), 7);
    check(list.traverseForward(), "4 -> 6 -> 8 -> 9");
    check(list.deleteNode(nullptr), -1);
    check(list.size(), 4);
}

void testLargeDrainAndEndpointHelpers() {
    IntDoublyList list;
    const int half = 50000;
    bool ok = true;
    for (int i = 0; i < half; i++) list.insertAtTail(i);
    ok = list.size() == half && ok;
    for (int i = 0; i < half; i++) ok = (list.deleteFromHead() == i) && ok;
    for (int i = 0; i < half; i++) list.insertAtHead(i);
    ok = list.size() == half && ok;
    for (int i = 0; i < half; i++) ok = (list.deleteFromTail() == i) && ok;
    check(ok, true);
    check(list.isEmpty(), true); check(list.traverseForward(), ""); check(list.traverseBackward(), "");
    list.insertAtTail(7); list.insertAtTail(7); list.insertAtTail(7);
    auto* first = list.searchForward(7);
    list.insertBefore(first, 6); list.insertAfter(first, 8);
    check(list.traverseForward(), "6 -> 7 -> 8 -> 7 -> 7");
    check(list.traverseBackward(), "7 -> 7 -> 8 -> 7 -> 6");
}

int main() {
    testDoublyList();
    testLargeDrainAndEndpointHelpers();
    return failures == 0 ? 0 : 1;
}
