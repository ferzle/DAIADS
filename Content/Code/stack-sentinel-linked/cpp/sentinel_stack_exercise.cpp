#include <iostream>
using namespace std;

class IntStack {
private:
    struct Node {
        int value;
        Node* next;

        Node(int v, Node* n) {
            value = v;
            next = n;
        }
    };

    Node* sentinel;
    int count;

public:
    IntStack() {
        sentinel = new Node(-1, nullptr); // dummy value, not part of the stack
        count = 0;
    }

    ~IntStack() {
        while (sentinel != nullptr) {
            Node* old = sentinel;
            sentinel = sentinel->next;
            delete old;
        }
    }

    bool isEmpty() {
        // TODO
        return false;
    }

    void push(int value) {
        // TODO
    }

    int pop() {
        // TODO
        return -1;
    }

    int peek() {
        // TODO
        return -1;
    }

    int size() {
        // TODO
        return -1;
    }
};

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

void testStack() {
    IntStack stack;

    check(stack.isEmpty(), true);
    check(stack.size(), 0);
    check(stack.pop(), -1);
    check(stack.peek(), -1);
    check(stack.size(), 0);

    stack.push(12);
    check(stack.isEmpty(), false);
    check(stack.size(), 1);
    check(stack.peek(), 12);

    stack.push(7);
    check(stack.size(), 2);
    check(stack.peek(), 7);

    stack.push(19);
    check(stack.size(), 3);
    check(stack.peek(), 19);

    check(stack.pop(), 19);
    check(stack.size(), 2);
    check(stack.peek(), 7);

    check(stack.pop(), 7);
    check(stack.size(), 1);
    check(stack.peek(), 12);

    check(stack.pop(), 12);
    check(stack.size(), 0);
    check(stack.isEmpty(), true);

    check(stack.pop(), -1);
    check(stack.peek(), -1);
    check(stack.size(), 0);

    stack.push(5);
    check(stack.isEmpty(), false);
    check(stack.size(), 1);
    check(stack.peek(), 5);
    check(stack.pop(), 5);
    check(stack.size(), 0);
    check(stack.isEmpty(), true);
}

int main() {
    testStack();
    return 0;
}
