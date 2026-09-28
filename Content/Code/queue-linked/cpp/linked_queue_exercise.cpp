#include <iostream>
using namespace std;


int failures = 0;
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

    bool hasValidStructureForTesting() const {
        if ((head == nullptr) != (tail == nullptr)) return false;
        if (tail != nullptr && tail->next != nullptr) return false;
        Node* slow = head;
        Node* fast = head;
        while (fast != nullptr && fast->next != nullptr) {
            slow = slow->next;
            fast = fast->next->next;
            if (slow == fast) return false;
        }
        int reachable = 0;
        Node* last = nullptr;
        for (Node* node = head; node != nullptr; node = node->next) {
            last = node;
            ++reachable;
        }
        return reachable == count && last == tail;
    }
};

void checkAtLine(int actual, int expected, int line, const char* expression) {
    if (actual == expected) {
        cout << "PASS at test line " << line << " (" << expression << "): got " << actual << endl;
    } else {
        ++failures;
        cout << "FAIL at test line " << line << " (" << expression
             << "): expected " << expected << " but got " << actual << endl;
    }
}

void checkAtLine(bool actual, bool expected, int line, const char* expression) {
    if (actual == expected) {
        cout << "PASS at test line " << line << " (" << expression << "): got " << actual << endl;
    } else {
        ++failures;
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
    check(queue.hasValidStructureForTesting(), true);

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
    check(queue.hasValidStructureForTesting(), true);

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

void testLongRunsAndSingletonReuse() {
    IntQueue queue;
    for (int round = 0; round < 50; round++) {
        queue.enqueue(round);
        check(queue.front(), round);
        check(queue.dequeue(), round);
        check(queue.isEmpty(), true);
    }
    for (int i = 0; i < 1000; i++) queue.enqueue(i);
    for (int i = 0; i < 1000; i++) {
        check(queue.front(), i);
        check(queue.dequeue(), i);
        check(queue.size(), 999 - i);
    }
    queue.enqueue(77);
    check(queue.dequeue(), 77);
    check(queue.isEmpty(), true);
}

void testLargeAggregateWorkload() {
    const int n = 100000;
    IntQueue queue;
    for (int i = 0; i < n; ++i) queue.enqueue(i);
    bool ok = queue.size() == n && queue.front() == 0
        && queue.hasValidStructureForTesting();
    for (int i = 0; i < n; ++i) ok = (queue.dequeue() == i) && ok;
    ok = queue.isEmpty() && queue.hasValidStructureForTesting() && ok;
    check(ok, true);
}

int main() {
    testQueue();
    testLongRunsAndSingletonReuse();
    testLargeAggregateWorkload();
    return failures == 0 ? 0 : 1;
}
