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
        return head == nullptr;
    }

    void push(int value) {
        head = new Node(value, head);
        ++count;
    }

    int pop() {
        if (isEmpty()) return -1;
        Node* old = head;
        int value = old->value;
        head = old->next;
        delete old;
        --count;
        return value;
    }

    int peek() {
        return isEmpty() ? -1 : head->value;
    }

    int size() {
        return count;
    }

    bool hasValidStructureForTesting() const {
        Node* slow = head;
        Node* fast = head;
        while (fast != nullptr && fast->next != nullptr) {
            slow = slow->next;
            fast = fast->next->next;
            if (slow == fast) return false;
        }
        int reachable = 0;
        for (Node* node = head; node != nullptr; node = node->next) ++reachable;
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
    check(stack.hasValidStructureForTesting(), true);

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
    check(stack.hasValidStructureForTesting(), true);

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
