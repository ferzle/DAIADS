#include <algorithm>
#include <cstdlib>
#include <iostream>
#include <limits>
#include <vector>

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

    Node* bstRemove(int key, bool& removed) {
        // Perform structural BST removal. Set removed and return the first
        // ancestor whose height may have changed.
        removed = false;
        return nullptr; // TODO
    }

    void fixAfterRemove(Node* node) {
        // Continue through the root, including after rotations.
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

    bool insert(int key) {
        // Call bstInsert, update nodeCount once, then fix.
        return false; // TODO
    }

    bool remove(int key) {
        // Call bstRemove, update nodeCount once, then fix.
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

void check(bool condition, const char* description) {
    std::cout << (condition ? "pass: " : "fail: ") << description << '\n';
}

int main() {
    AVLTree tree;
    check(tree.isEmpty(), "a new tree is empty");
    int keys[] = {30, 20, 10, 40, 50, 25, 27};
    for (int key : keys) {
        check(tree.insert(key), "insert a distinct key");
        check(tree.hasValidStructure(), "invariants after insertion");
    }
    check(!tree.insert(25), "duplicate insertion leaves the set unchanged");
    check(tree.contains(27), "contains finds a stored key");
    for (int key : {40, 30, 10}) {
        check(tree.remove(key), "remove a stored key");
        check(tree.hasValidStructure(), "invariants after removal");
    }
    std::cout << "inorder:";
    for (int key : tree.inorderValues()) std::cout << ' ' << key;
    std::cout << '\n';
}
