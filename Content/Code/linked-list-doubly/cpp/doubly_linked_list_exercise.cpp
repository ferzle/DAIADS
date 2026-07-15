#include <iostream>
#include <sstream>
#include <string>
using namespace std;

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
        // TODO:
        // Create a new node.
        // If the list is empty, head and tail should both point to it.
        // Otherwise:
        //   old tail's next should point to the new node
        //   new node's prev should point to the old tail
        //   tail should move to the new node
        // Do not forget to update count.
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
        // TODO:
        // If the list is empty, return -1.
        // Save tail->value.
        // If there is one node, delete it and set both head and tail to nullptr.
        // Otherwise:
        //   save the old tail
        //   move tail to tail->prev
        //   set tail->next to nullptr
        //   delete the old tail
        // Decrease count and return the saved value.
        return -1;
    }

    string traverseForward() {
        // TODO:
        // Return values from head to tail, such as "4 -> 7 -> 9".
        // Return "" for an empty list.
        return "";
    }

    string traverseBackward() {
        // TODO:
        // Return values from tail to head, such as "9 -> 7 -> 4".
        // Return "" for an empty list.
        return "";
    }

    Node* searchForward(int value) {
        // Optional -- time permitting.
        // Return the first node containing value, or nullptr if not found.
        return nullptr;
    }

    void insertAfter(Node* node, int value) {
        // Optional -- time permitting.
        // If node is nullptr, do nothing.
        // If node is tail, this should behave like insertAtTail(value).
        // Otherwise, insert the new node between node and node->next.
    }

    void insertBefore(Node* node, int value) {
        // Optional -- time permitting.
        // If node is nullptr, do nothing.
        // If node is head, this should behave like insertAtHead(value).
        // Otherwise, insert the new node between node->prev and node.
    }

    int deleteNode(Node* node) {
        // Optional -- time permitting.
        // If node is nullptr, return -1.
        // If node is head, use deleteFromHead().
        // If node is tail, use deleteFromTail().
        // Otherwise, unlink node from both directions, delete it, and return its value.
        return -1;
    }
};

void check(const string& actual, const string& expected) {
    if (actual == expected) {
        cout << "pass" << endl;
    } else {
        cout << "fail: expected \"" << expected << "\" but got \"" << actual << "\"" << endl;
    }
}

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
}

int main() {
    testDoublyList();
    return 0;
}
