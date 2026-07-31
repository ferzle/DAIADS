#include <iostream>
using namespace std;

class IntQueue {
private:
    struct Node {
        int value;
        Node* next;

        Node(int v) {
            value = v;
            next = nullptr;
        }
    };

    Node* head;
    Node* tail;
    int count;

public:
    IntQueue() {
        head = nullptr;
        tail = nullptr;
        count = 0;
    }

    ~IntQueue() {
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

    void enqueue(int value) {
        // TODO
    }

    int dequeue() {
        // TODO
        return -1;
    }

    int front() {
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
        cout << "PASS at test line " << line << " (" << expression << "): got " << actual << endl;
    } else {
        cout << "FAIL at test line " << line << " (" << expression
             << "): expected " << expected << " but got " << actual << endl;
    }
}

void checkAtLine(bool actual, bool expected, int line, const char* expression) {
    if (actual == expected) {
        cout << "PASS at test line " << line << " (" << expression << "): got " << actual << endl;
    } else {
        cout << "FAIL at test line " << line << " (" << expression
             << "): expected " << (expected ? "true" : "false")
             << " but got " << (actual ? "true" : "false") << endl;
    }
}

#define check(actual, expected) checkAtLine((actual), (expected), __LINE__, #actual)

void testQueue() {
    IntQueue queue;

    check(queue.isEmpty(), true);
    check(queue.size(), 0);
    check(queue.dequeue(), -1);
    check(queue.front(), -1);

    queue.enqueue(4);
    check(queue.isEmpty(), false);
    check(queue.size(), 1);
    check(queue.front(), 4);

    queue.enqueue(7);
    queue.enqueue(9);
    check(queue.size(), 3);
    check(queue.front(), 4);

    check(queue.dequeue(), 4);
    check(queue.front(), 7);
    check(queue.size(), 2);

    queue.enqueue(2);
    check(queue.dequeue(), 7);
    check(queue.dequeue(), 9);
    check(queue.dequeue(), 2);
    check(queue.isEmpty(), true);
    check(queue.size(), 0);

    check(queue.dequeue(), -1);
    check(queue.front(), -1);

    queue.enqueue(6);
    check(queue.isEmpty(), false);
    check(queue.front(), 6);
    check(queue.dequeue(), 6);
    check(queue.isEmpty(), true);

    IntQueue large;
    bool largeOk = true;
    for (int i = 0; i < 5000; i++) large.enqueue(i);
    largeOk = (large.size() == 5000 && large.front() == 0) && largeOk;
    for (int i = 0; i < 5000; i++) largeOk = (large.dequeue() == i) && largeOk;
    check(largeOk && large.isEmpty(), true);
}

int main() {
    testQueue();
    return 0;
}
