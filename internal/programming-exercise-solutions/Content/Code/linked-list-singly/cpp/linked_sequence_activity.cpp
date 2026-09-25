#include <iostream>
#include <sstream>
#include <stdexcept>
#include <string>
using namespace std;

int failures = 0;

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
        head = new Node(value, head);
    }

    int deleteAtHead() {
        if (head == nullptr) throw runtime_error("list is empty");
        Node* oldHead = head;
        int value = oldHead->data;
        head = head->next;
        delete oldHead;
        return value;
    }

    void insertAfter(Node* node, int value) {
        if (node == nullptr) throw runtime_error("node must not be null");
        node->next = new Node(value, node->next);
    }

    int deleteAfter(Node* node) {
        if (node == nullptr || node->next == nullptr) {
            throw runtime_error("node must have a successor");
        }
        Node* removed = node->next;
        int value = removed->data;
        node->next = removed->next;
        delete removed;
        return value;
    }

    Node* search(int value) {
        for (Node* node = head; node != nullptr; node = node->next) {
            if (node->data == value) return node;
        }
        return nullptr;
    }

    string traverse() const {
        ostringstream result;
        for (Node* node = head; node != nullptr; node = node->next) {
            if (node != head) result << " -> ";
            result << node->data;
        }
        return result.str();
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
    bool threw = false;
    try {
        action();
    } catch (const runtime_error&) {
        threw = true;
    }
    if (!threw) throw runtime_error("Expected an exception, but none was thrown");
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

void testLargeMixedSequence() {
    LinkedSequence list;
    const int count = 100000;
    for (int i = 0; i < count; i++) list.insertAtHead(i);
    auto* tail = list.search(0);
    check(tail != nullptr && tail->data == 0, "search should find the tail after traversing 100,000 nodes");
    bool ok = true;
    for (int expected = count - 1; expected >= 0; expected--) ok = (list.deleteAtHead() == expected) && ok;
    check(ok, "100,000 head deletions should preserve reverse insertion order");
    check(list.traverse(), "");
}

int main() {
    try {
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
    } catch (const std::exception& error) {
        ++failures;
        cout << "FAIL: core linked-sequence scenario threw " << error.what() << endl;
    }

    try {
        testLargeMixedSequence();
    } catch (const std::exception& error) {
        ++failures;
        cout << "FAIL: large linked-sequence scenario threw " << error.what() << endl;
    }

    cout << (failures == 0 ? "All tests passed." : to_string(failures) + " scenario(s) failed.") << endl;
    return failures == 0 ? 0 : 1;
}
