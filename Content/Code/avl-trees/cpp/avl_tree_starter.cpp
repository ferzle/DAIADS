#include <algorithm>
#include <cstdlib>
#include <iostream>
#include <limits>
#include <set>
#include <string>
#include <vector>

int failures = 0;

class AVLTree {
private:
    struct Node {
        int key;
        Node* left;
        Node* right;
        Node* parent;
        int height;
        Node(int value, Node* p = nullptr)
            : key(value), left(nullptr), right(nullptr), parent(p), height(0) {}
    };

    struct Validation {
        bool valid;
        int height;
        int count;
    };

    Node* root = nullptr;
    int nodeCount = 0;

    int heightOf(Node* node) const {
        // Return -1 for nullptr and the stored height otherwise.
        return 0; // TODO
    }

    void updateHeight(Node* node) {
        // Recompute node->height from its children.
        // TODO
    }

    int balanceFactor(Node* node) const {
        // Use height(left) - height(right).
        return 0; // TODO
    }

    Node* searchNode(int key) const {
        // Follow one ordinary BST path.
        return nullptr; // TODO
    }

    void replaceSubtree(Node* oldRoot, Node* newRoot) {
        // Handle both an ordinary parent and replacement of root.
        // TODO
    }

    Node* rotateLeft(Node* node) {
        // Reconnect links and parents, update downward then upward,
        // and return the new local root.
        return node; // TODO
    }

    Node* rotateRight(Node* node) {
        return node; // TODO
    }

    Node* rebalance(Node* node) {
        // Update height and handle all four numeric cases.
        return node; // TODO
    }

    Node* bstInsert(int key) {
        // Perform only ordinary BST placement; do not change nodeCount.
        // Return the new leaf, or nullptr for a duplicate.
        return nullptr; // TODO
    }

    void fixAfterInsert(Node* newLeaf) {
        // Walk upward and stop after the first repair.
        // TODO
    }

    Node* minimumNode(Node* node) const {
        while (node != nullptr && node->left != nullptr) node = node->left;
        return node;
    }

    Node* bstRemove(Node* target) {
        // target is known to be in the tree. Perform ordinary BST removal,
        // using successor-key substitution for a two-child node. Return the
        // lowest node still in the tree whose height may have changed.
        // This may be nullptr after successfully removing the root.
        return nullptr; // TODO
    }

    void fixAfterRemove(Node* node) {
        // Continue upward while the repaired subtree's height decreases.
        // Stop when its height is unchanged. After a continuing rotation,
        // move to the repaired local root's parent.
        // TODO
    }

    void appendInorder(Node* node, std::vector<int>& values) const {
        if (node == nullptr) return;
        appendInorder(node->left, values);
        values.push_back(node->key);
        appendInorder(node->right, values);
    }

    Validation validate(Node* node, Node* parent, long long low, long long high) const {
        if (node == nullptr) return {true, -1, 0};
        Validation left = validate(node->left, node, low, node->key);
        Validation right = validate(node->right, node, node->key, high);
        int computed = 1 + std::max(left.height, right.height);
        bool valid = left.valid && right.valid
            && node->parent == parent && low < node->key && node->key < high
            && node->height == computed
            && std::abs(left.height - right.height) <= 1;
        return {valid, computed, 1 + left.count + right.count};
    }

    void destroy(Node* node) {
        if (node == nullptr) return;
        destroy(node->left);
        destroy(node->right);
        delete node;
    }

public:
    ~AVLTree() { destroy(root); }
    int size() const { return nodeCount; }
    bool isEmpty() const { return nodeCount == 0; }
    bool contains(int key) const { return searchNode(key) != nullptr; }

    // Test observers: expose stored metadata without showing how to maintain it.
    int treeHeightForTesting() const { return root == nullptr ? -1 : root->height; }
    bool rootKeyForTesting(int& key) const {
        if (root == nullptr) return false;
        key = root->key;
        return true;
    }

    bool insert(int key) {
        // Call bstInsert. If it succeeds, increment nodeCount exactly once,
        // then repair from the returned leaf.
        return false; // TODO
    }

    bool remove(int key) {
        // Find the target first. If present, pass it to bstRemove, decrement
        // nodeCount exactly once, and repair from the returned node.
        return false; // TODO
    }

    std::vector<int> inorderValues() const {
        std::vector<int> values;
        appendInorder(root, values);
        return values;
    }

    bool hasValidStructure() const {
        if (root != nullptr && root->parent != nullptr) return false;
        Validation result = validate(
            root, nullptr,
            std::numeric_limits<long long>::min(),
            std::numeric_limits<long long>::max()
        );
        return result.valid && result.count == nodeCount;
    }
};

void check(bool condition, const std::string& description) {
    if (!condition) ++failures;
    std::cout << (condition ? "pass: " : "fail: ") << description << '\n';
}

void testDifferentialUpdates() {
    AVLTree tree; std::set<int> expected; long long state = 0x5EED;
    for (int operation = 0; operation < 2000; operation++) {
        state = (state * 1103515245 + 12345) & 0x7fffffff;
        int key = static_cast<int>(state % 1000);
        bool actual = operation % 3 == 0 ? tree.remove(key) : tree.insert(key);
        bool oracle = operation % 3 == 0 ? expected.erase(key) > 0 : expected.insert(key).second;
        check(actual == oracle, "differential operation result " + std::to_string(operation));
        check(tree.size() == static_cast<int>(expected.size()), "differential size " + std::to_string(operation));
        check(tree.inorderValues() == std::vector<int>(expected.begin(), expected.end()), "differential contents " + std::to_string(operation));
        check(tree.hasValidStructure(), "differential AVL structure " + std::to_string(operation));
    }
    std::vector<int> remaining(expected.begin(), expected.end());
    for (int key : remaining) check(tree.remove(key), "final drain key");
    check(tree.isEmpty() && tree.hasValidStructure(), "valid empty tree after complete drain");
}

int main() {
    AVLTree tree;
    int observedRoot = 0;
    check(tree.isEmpty(), "a new tree is empty");
    check(tree.size() == 0, "a new tree has size zero");
    check(tree.treeHeightForTesting() == -1, "an empty tree has height -1");
    check(!tree.rootKeyForTesting(observedRoot), "an empty tree has no root key");
    check(!tree.contains(99), "contains reports absent key 99");
    check(!tree.remove(99), "removing absent key 99 returns false");
    int keys[] = {30, 20, 10, 40, 50, 25, 27};
    for (int key : keys) {
        check(tree.insert(key), "insert " + std::to_string(key));
        check(tree.hasValidStructure(), "invariants after inserting " + std::to_string(key));
    }
    check(!tree.insert(25), "duplicate insertion leaves the set unchanged");
    check(tree.size() == 7, "size counts seven distinct insertions");
    check(tree.contains(27), "contains finds a stored key");
    for (int key : {40, 30, 10}) {
        check(tree.remove(key), "remove " + std::to_string(key));
        check(tree.hasValidStructure(), "invariants after removing " + std::to_string(key));
    }
    int rotationPatterns[][3] = {
        {30, 20, 10}, {10, 20, 30}, {30, 10, 20}, {10, 30, 20}
    };
    for (int i = 0; i < 4; i++) {
        AVLTree rotationTree;
        bool ok = true;
        for (int key : rotationPatterns[i]) ok = rotationTree.insert(key) && ok;
        check(ok && rotationTree.size() == 3 && rotationTree.hasValidStructure(),
            "rotation pattern " + std::to_string(i + 1));
        check(rotationTree.rootKeyForTesting(observedRoot) && observedRoot == 20
                && rotationTree.treeHeightForTesting() == 1,
            "rotation pattern root and height " + std::to_string(i + 1));
    }

    int deletionPatterns[][5] = {
        {30, 20, 40, 10, 25}, {20, 10, 30, 25, 40},
        {30, 10, 40, 20, -1}, {20, 10, 40, 30, -1}
    };
    int patternLengths[] = {5, 5, 4, 4};
    int removedKeys[] = {40, 10, 40, 10};
    int expectedRoots[] = {20, 30, 20, 30};
    int expectedHeights[] = {2, 2, 1, 1};
    for (int i = 0; i < 4; i++) {
        AVLTree deletionTree;
        bool ok = true;
        for (int j = 0; j < patternLengths[i]; j++) ok = deletionTree.insert(deletionPatterns[i][j]) && ok;
        ok = deletionTree.remove(removedKeys[i]) && ok;
        check(ok && deletionTree.rootKeyForTesting(observedRoot) && observedRoot == expectedRoots[i]
                && deletionTree.treeHeightForTesting() == expectedHeights[i]
                && deletionTree.hasValidStructure(),
            "deletion rotation pattern " + std::to_string(i + 1));
    }
    AVLTree large;
    bool largeOk = true;
    const int count = 20000;
    for (int i = 0; i < count; i++) largeOk = large.insert((i * 7919) % count) && largeOk;
    for (int i = 0; i < count; i++) largeOk = large.contains(i) && largeOk;
    largeOk = large.size() == count && large.hasValidStructure()
        && large.treeHeightForTesting() <= 20 && largeOk;
    for (int i = 0; i < count; i += 2) largeOk = large.remove(i) && largeOk;
    for (int i = 0; i < count; i++) largeOk = (large.contains(i) == (i % 2 == 1)) && largeOk;
    largeOk = large.size() == count / 2 && large.hasValidStructure() && largeOk;
    check(largeOk, "20,000-key insertion, search, and 10,000-key removal stress test");
    testDifferentialUpdates();
    std::cout << "inorder:";
    for (int key : tree.inorderValues()) std::cout << ' ' << key;
    std::cout << '\n';
    return failures == 0 ? 0 : 1;
}
