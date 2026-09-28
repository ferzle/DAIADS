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

    Node* head;
    int count;

public:
    IntStack() {
        head = nullptr;
        count = 0;
    }

    ~IntStack() {
        while (head != nullptr) {
            Node* old = head;
            head = head->next;
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

void testStack() {
    IntStack stack;

    check(stack.isEmpty(), true);
    check(stack.size(), 0);
    check(stack.pop(), -1);
    check(stack.peek(), -1);
    check(stack.size(), 0);

    stack.push(4);
    check(stack.isEmpty(), false);
    check(stack.size(), 1);
    check(stack.peek(), 4);
    check(stack.size(), 1);

    stack.push(7);
    check(stack.size(), 2);
    check(stack.peek(), 7);

    stack.push(9);
    check(stack.size(), 3);
    check(stack.peek(), 9);

    check(stack.pop(), 9);
    check(stack.size(), 2);
    check(stack.peek(), 7);

    stack.push(2);
    check(stack.size(), 3);
    check(stack.peek(), 2);

    check(stack.pop(), 2);
    check(stack.pop(), 7);
    check(stack.pop(), 4);
    check(stack.size(), 0);
    check(stack.isEmpty(), true);

    check(stack.pop(), -1);
    check(stack.peek(), -1);
    check(stack.size(), 0);

    stack.push(6);
    check(stack.isEmpty(), false);
    check(stack.size(), 1);
    check(stack.peek(), 6);
    check(stack.pop(), 6);
    check(stack.size(), 0);
    check(stack.isEmpty(), true);
}

int main() {
    testStack();
    return 0;
}
