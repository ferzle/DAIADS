#include <iostream>
#include <sstream>
#include <stdexcept>
#include <string>
using namespace std;

class LinkedSequence {
public:
    struct Node {
        int data;
        Node* next;

        Node(int d, Node* n = nullptr) {
            data = d;
            next = n;
        }
    };

private:
    Node* head;

public:
    LinkedSequence() {
        head = nullptr;
    }

    ~LinkedSequence() {
        while (head != nullptr) {
            Node* oldHead = head;
            head = head->next;
            delete oldHead;
        }
    }

    void insertAtHead(int value) {
        // TODO: Create a new node whose next link is the old head.
        // Then update head.
    }

    int deleteAtHead() {
        // TODO: If the list is empty, throw runtime_error.
        // Otherwise, save the head value, move head to head->next,
        // delete the old head node, and return the value.
        return -999999; // temporary value so the starter code compiles
    }

    void insertAfter(Node* node, int value) {
        // TODO: If node is nullptr, throw runtime_error.
        // Otherwise, create a new node and insert it immediately after node.
    }

    int deleteAfter(Node* node) {
        // TODO: If node is nullptr or node->next is nullptr, throw runtime_error.
        // Otherwise, remove node->next, delete it, and return its value.
        return -999999; // temporary value so the starter code compiles
    }

    Node* search(int value) {
        // TODO: Return the first node containing value, or nullptr if the value is not found.
        return nullptr;
    }

    string traverse() const {
        // TODO: Return a string such as "2 -> 9 -> 4", or "" for an empty list.
        return "";
    }
};

void check(const string& actual, const string& expected) {
    if (actual != expected) {
        throw runtime_error("Expected \"" + expected + "\" but got \"" + actual + "\"");
    }
}

void check(int actual, int expected) {
    if (actual != expected) {
        throw runtime_error("Expected " + to_string(expected) + " but got " + to_string(actual));
    }
}

void check(bool condition, const string& message) {
    if (!condition) {
        throw runtime_error(message);
    }
}

void expectException(void (*action)()) {
    try {
        action();
        throw runtime_error("Expected an exception, but none was thrown");
    } catch (const runtime_error&) {
        // expected
    }
}

// These globals are only used to make simple exception tests possible.
LinkedSequence* testList = nullptr;
LinkedSequence::Node* testNode = nullptr;

void deleteAtHeadOnEmpty() {
    testList->deleteAtHead();
}

void insertAfterNull() {
    testList->insertAfter(nullptr, 8);
}

void deleteAfterTail() {
    testList->deleteAfter(testNode);
}

void deleteAfterNull() {
    testList->deleteAfter(nullptr);
}

int main() {
    LinkedSequence list;
    testList = &list;

    check(list.traverse(), "");

    list.insertAtHead(4);
    check(list.traverse(), "4");

    list.insertAtHead(9);
    check(list.traverse(), "9 -> 4");

    list.insertAtHead(2);
    check(list.traverse(), "2 -> 9 -> 4");

    LinkedSequence::Node* node9 = list.search(9);
    check(node9 != nullptr, "search(9) should find a node");
    check(node9->data, 9);

    list.insertAfter(node9, 7);
    check(list.traverse(), "2 -> 9 -> 7 -> 4");

    check(list.deleteAfter(node9), 7);
    check(list.traverse(), "2 -> 9 -> 4");

    check(list.deleteAtHead(), 2);
    check(list.traverse(), "9 -> 4");

    check(list.search(20) == nullptr, "search(20) should return nullptr");

    testNode = list.search(4);
    expectException(deleteAfterTail);
    expectException(insertAfterNull);
    expectException(deleteAfterNull);

    check(list.deleteAtHead(), 9);
    check(list.deleteAtHead(), 4);
    check(list.traverse(), "");
    expectException(deleteAtHeadOnEmpty);

    cout << "All tests passed." << endl;
    return 0;
}
