#include <iostream>
using namespace std;


int failures = 0;
class IntDeque {
private:
struct BlockNode {
    int* values;
    BlockNode* prev;
    BlockNode* next;

    BlockNode(int blockSize) {
        values = new int[blockSize];
        prev = nullptr;
        next = nullptr;
    }

    ~BlockNode() {
        delete[] values;
    }
};

BlockNode* firstBlock;
BlockNode* lastBlock;
int frontIndex;
int backIndex;
int count;
int blockSize;

public:
IntDeque(int blockSizeValue) {
    blockSize = blockSizeValue;
    firstBlock = nullptr;
    lastBlock = nullptr;
    frontIndex = 0;
    backIndex = 0;
    count = 0;
}

~IntDeque() {
    clear();
}

bool isEmpty() {
    // TODO
    return false;
}

int size() {
    // TODO
    return -1;
}

void clear() {
    // TODO
}

int peekFront() {
    // TODO
    return -1;
}

int peekBack() {
    // TODO
    return -1;
}

bool addFront(int value) {
    // TODO
    return false;
}

bool addBack(int value) {
    // TODO
    return false;
}

int removeFront() {
    // TODO
    return -1;
}

int removeBack() {
    // TODO
    return -1;
}

bool hasValidStructureForTesting() const {
    if (blockSize <= 0 || count < 0) return false;
    if (count == 0) return firstBlock == nullptr && lastBlock == nullptr;
    if (firstBlock == nullptr || lastBlock == nullptr) return false;
    if (firstBlock->prev != nullptr || lastBlock->next != nullptr) return false;
    if (frontIndex < 0 || frontIndex >= blockSize
            || backIndex < 0 || backIndex >= blockSize) return false;
    int blocks = 0;
    BlockNode* previous = nullptr;
    for (BlockNode* node = firstBlock; node != nullptr; node = node->next) {
        if (node->prev != previous || blocks > count + 1) return false;
        previous = node;
        ++blocks;
    }
    return previous == lastBlock;
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

void testDeque() {
    IntDeque deque(3);
    check(deque.hasValidStructureForTesting(), true);

check(deque.isEmpty(), true);
check(deque.size(), 0);
check(deque.peekFront(), -1);
check(deque.peekBack(), -1);
check(deque.removeFront(), -1);
check(deque.removeBack(), -1);

check(deque.addBack(4), true);       // [4]
check(deque.peekFront(), 4);
check(deque.peekBack(), 4);
check(deque.size(), 1);

check(deque.addBack(7), true);       // [4, 7]
check(deque.addBack(9), true);       // [4, 7, 9]
check(deque.addBack(11), true);      // new back block: [4, 7, 9, 11]
check(deque.peekFront(), 4);
check(deque.peekBack(), 11);
check(deque.size(), 4);

check(deque.addFront(2), true);      // new front block: [2, 4, 7, 9, 11]
check(deque.addFront(1), true);      // [1, 2, 4, 7, 9, 11]
check(deque.addFront(0), true);      // [0, 1, 2, 4, 7, 9, 11]
check(deque.peekFront(), 0);
check(deque.peekBack(), 11);
check(deque.size(), 7);
check(deque.hasValidStructureForTesting(), true);

check(deque.removeFront(), 0);       // [1, 2, 4, 7, 9, 11]
check(deque.removeFront(), 1);       // [2, 4, 7, 9, 11]
check(deque.removeFront(), 2);       // first block may be removed: [4, 7, 9, 11]
check(deque.peekFront(), 4);
check(deque.peekBack(), 11);
check(deque.size(), 4);

check(deque.removeBack(), 11);       // [4, 7, 9]
check(deque.removeBack(), 9);        // [4, 7]
check(deque.peekFront(), 4);
check(deque.peekBack(), 7);
check(deque.size(), 2);

check(deque.addFront(3), true);      // [3, 4, 7]
check(deque.addBack(8), true);       // [3, 4, 7, 8]
check(deque.addBack(10), true);      // [3, 4, 7, 8, 10]
check(deque.peekFront(), 3);
check(deque.peekBack(), 10);

check(deque.removeBack(), 10);       // [3, 4, 7, 8]
check(deque.removeFront(), 3);       // [4, 7, 8]
check(deque.removeBack(), 8);        // [4, 7]
check(deque.removeFront(), 4);       // [7]
check(deque.peekFront(), 7);
check(deque.peekBack(), 7);
check(deque.size(), 1);

check(deque.removeBack(), 7);        // []
check(deque.isEmpty(), true);
check(deque.size(), 0);
check(deque.hasValidStructureForTesting(), true);
check(deque.peekFront(), -1);
check(deque.peekBack(), -1);
check(deque.removeFront(), -1);
check(deque.removeBack(), -1);

check(deque.addFront(6), true);      // [6]
check(deque.addBack(8), true);       // [6, 8]
deque.clear();                       // []
check(deque.isEmpty(), true);
check(deque.size(), 0);
check(deque.peekFront(), -1);

IntDeque tinyBlocks(1);
bool tinyOk = true;
for (int i = 0; i < 500; i++) tinyOk = tinyBlocks.addBack(i) && tinyOk;
for (int i = 0; i < 250; i++) tinyOk = (tinyBlocks.removeFront() == i) && tinyOk;
for (int i = 500; i < 750; i++) tinyOk = tinyBlocks.addFront(i) && tinyOk;
for (int i = 749; i >= 500; i--) tinyOk = (tinyBlocks.removeFront() == i) && tinyOk;
for (int i = 499; i >= 250; i--) tinyOk = (tinyBlocks.removeBack() == i) && tinyOk;
check(tinyOk && tinyBlocks.isEmpty(), true);
}

void testBlockBoundariesAndReuse() {
    for (int blockSize : {1, 2, 3, 4, 5, 8, 32}) {
        IntDeque deque(blockSize);
        int count = blockSize * 20 + 3;
        for (int i = 0; i < count; i++) check(deque.addBack(i), true);
        for (int i = 0; i < count; i++) check(deque.removeFront(), i);
        check(deque.isEmpty(), true); check(deque.size(), 0);
        for (int i = 0; i < count; i++) check(deque.addFront(i), true);
        for (int i = 0; i < count; i++) check(deque.removeBack(), i);
        check(deque.isEmpty(), true);
        for (int i = 0; i < count; i++) {
            check(deque.addBack(10000 + i), true);
            check(deque.removeFront(), 10000 + i);
        }
        deque.addFront(7); deque.clear(); check(deque.isEmpty(), true);
        check(deque.peekFront(), -1); check(deque.peekBack(), -1);
    }
}

void testLargeAggregateWorkload() {
    const int n = 100000;
    IntDeque deque(64);
    bool ok = true;
    for (int i = 0; i < n; ++i) ok = deque.addBack(i) && ok;
    ok = deque.size() == n && deque.peekFront() == 0
        && deque.peekBack() == n - 1 && deque.hasValidStructureForTesting() && ok;
    for (int i = 0; i < n; ++i) ok = (deque.removeFront() == i) && ok;
    ok = deque.isEmpty() && deque.hasValidStructureForTesting() && ok;
    check(ok, true);
}

int main() {
    testDeque();
    testBlockBoundariesAndReuse();
    testLargeAggregateWorkload();
    return failures == 0 ? 0 : 1;
}
