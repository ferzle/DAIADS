#include <iostream>
using namespace std;

class IntStack {
private:
    int* A;
    int capacity;
    int top;

public:
    IntStack(int cap) {
        capacity = cap;
        A = new int[capacity];
        top = -1;
    }

    ~IntStack() {
        delete[] A;
    }

    bool isEmpty() {
        // TODO
        return false;
    }

    bool isFull() {
        // TODO
        return false;
    }

    bool push(int value) {
        // TODO
        return false;
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
    IntStack stack(4);

    check(stack.size(), 0);
    check(stack.pop(), -1);
    check(stack.peek(), -1);
    check(stack.size(), 0);

    check(stack.push(4), true);
    check(stack.size(), 1);
    check(stack.peek(), 4);
    check(stack.size(), 1);

    check(stack.push(7), true);
    check(stack.size(), 2);
    check(stack.peek(), 7);

    check(stack.push(9), true);
    check(stack.size(), 3);
    check(stack.peek(), 9);

    check(stack.push(2), true);
    check(stack.size(), 4);
    check(stack.peek(), 2);

    check(stack.push(5), false);
    check(stack.size(), 4);
    check(stack.peek(), 2);

    check(stack.pop(), 2);
    check(stack.size(), 3);

    check(stack.pop(), 9);
    check(stack.size(), 2);

    check(stack.push(6), true);
    check(stack.size(), 3);
    check(stack.peek(), 6);

    check(stack.pop(), 6);
    check(stack.size(), 2);

    check(stack.pop(), 7);
    check(stack.size(), 1);

    check(stack.pop(), 4);
    check(stack.size(), 0);

    check(stack.pop(), -1);
    check(stack.peek(), -1);
    check(stack.size(), 0);
}

int main() {
    testStack();
    return 0;
}
