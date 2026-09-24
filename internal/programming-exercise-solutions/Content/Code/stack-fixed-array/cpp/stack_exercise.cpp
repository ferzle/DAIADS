#include <iostream>
#include <vector>
using namespace std;


int failures = 0;
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
        return top == -1;
    }

    bool isFull() {
        return top == capacity - 1;
    }

    bool push(int value) {
        if (isFull()) return false;
        A[++top] = value;
        return true;
    }

    int pop() {
        if (isEmpty()) return -1;
        return A[top--];
    }

    int peek() {
        if (isEmpty()) return -1;
        return A[top];
    }

    int size() {
        return top + 1;
    }

    vector<int> usedValuesForTesting() const {
        return vector<int>(A, A + top + 1);
    }
};

void checkAtLine(int actual, int expected, int line, const char* expression) {
    if (actual == expected) {
        cout << "pass" << endl;
    } else {
        ++failures;
        cout << "fail at test line " << line << " (" << expression
             << "): expected " << expected << " but got " << actual << endl;
    }
}

void checkAtLine(bool actual, bool expected, int line, const char* expression) {
    if (actual == expected) {
        cout << "pass" << endl;
    } else {
        ++failures;
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
    check(stack.usedValuesForTesting() == vector<int>({4, 7, 9, 2}), true);
    check(stack.size(), 4);
    check(stack.peek(), 2);

    check(stack.pop(), 2);
    check(stack.size(), 3);

    check(stack.pop(), 9);
    check(stack.size(), 2);

    check(stack.push(6), true);
    check(stack.usedValuesForTesting() == vector<int>({4, 7, 6}), true);
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

void testBoundarySizesAndReuse() {
    for (int capacity : {1, 2, 5, 32}) {
        IntStack stack(capacity);
        check(stack.isEmpty(), true);
        check(stack.isFull(), false);
        for (int i = 0; i < capacity; i++) {
            check(stack.push(1000 + i), true);
            check(stack.size(), i + 1);
            check(stack.peek(), 1000 + i);
        }
        check(stack.isFull(), true);
        check(stack.push(9999), false);
        check(stack.size(), capacity);
        for (int i = capacity - 1; i >= 0; i--) {
            check(stack.pop(), 1000 + i);
            check(stack.size(), i);
        }
        check(stack.isEmpty(), true);
        check(stack.push(77), true);
        check(stack.pop(), 77);
        check(stack.isEmpty(), true);
    }
}

void testLargeAggregateWorkload() {
    const int n = 100000;
    IntStack stack(n);
    bool ok = true;
    for (int i = 0; i < n; ++i) ok = stack.push(i) && ok;
    ok = stack.size() == n && stack.isFull() && stack.peek() == n - 1 && ok;
    for (int i = n - 1; i >= 0; --i) ok = (stack.pop() == i) && ok;
    ok = stack.isEmpty() && stack.size() == 0 && ok;
    check(ok, true);
}

int main() {
    testStack();
    testBoundarySizesAndReuse();
    testLargeAggregateWorkload();
    return failures == 0 ? 0 : 1;
}
