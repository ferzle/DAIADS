#include <iostream>
using namespace std;


int failures = 0;
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
    Node* originalSentinel;
    int count;

public:
    IntStack() {
        sentinel = new Node(-1, nullptr); // dummy value, not part of the stack
        originalSentinel = sentinel;
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

    bool hasValidStructureForTesting() const {
        if (sentinel == nullptr || sentinel != originalSentinel) return false;
        Node* slow = sentinel->next;
        Node* fast = sentinel->next;
        while (fast != nullptr && fast->next != nullptr) {
            slow = slow->next;
            fast = fast->next->next;
            if (slow == fast) return false;
        }
        int reachable = 0;
        for (Node* node = sentinel->next; node != nullptr; node = node->next) ++reachable;
        return reachable == count;
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
    check(stack.hasValidStructureForTesting(), true);

    check(stack.pop(), 19);
    check(stack.size(), 2);
    check(stack.peek(), 7);

    check(stack.pop(), 7);
    check(stack.size(), 1);
    check(stack.peek(), 12);

    check(stack.pop(), 12);
    check(stack.size(), 0);
    check(stack.isEmpty(), true);
    check(stack.hasValidStructureForTesting(), true);

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

void testLongRunsAndRepeatedReuse() {
    IntStack stack;
    for (int round = 0; round < 25; round++) {
        check(stack.isEmpty(), true);
        for (int i = 0; i < 200; i++) {
            stack.push(round * 1000 + i);
            check(stack.size(), i + 1);
            check(stack.peek(), round * 1000 + i);
        }
        for (int i = 199; i >= 0; i--) {
            check(stack.pop(), round * 1000 + i);
            check(stack.size(), i);
        }
        check(stack.pop(), -1);
        check(stack.isEmpty(), true);
    }
}

void testLargeAggregateWorkload() {
    const int n = 100000;
    IntStack stack;
    for (int i = 0; i < n; ++i) stack.push(i);
    bool ok = stack.size() == n && stack.peek() == n - 1
        && stack.hasValidStructureForTesting();
    for (int i = n - 1; i >= 0; --i) ok = (stack.pop() == i) && ok;
    ok = stack.isEmpty() && stack.hasValidStructureForTesting() && ok;
    check(ok, true);
}

int main() {
    testStack();
    testLongRunsAndRepeatedReuse();
    testLargeAggregateWorkload();
    return failures == 0 ? 0 : 1;
}
