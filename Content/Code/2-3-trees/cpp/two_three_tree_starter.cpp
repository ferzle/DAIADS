#include <algorithm>
#include <iostream>
#include <limits>
#include <random>
#include <set>
#include <string>
#include <vector>

using namespace std;

/**
 * Starter implementation for an ordered set backed by a 2-3 tree.
 * Complete the methods marked TODO. std::set is used only as a test oracle;
 * it must not be used to implement the tree.
 */
class TwoThreeTree {
private:
    struct Node {
        vector<int> keys;
        vector<Node*> children;
        Node* parent = nullptr;

        Node() = default;
        explicit Node(int key) : keys{key} {}
        bool isLeaf() const { return children.empty(); }
    };

    struct Location {
        Node* node = nullptr;
        size_t keyIndex = 0;
        explicit operator bool() const { return node != nullptr; }
    };

    Node* root = nullptr;
    int keyCount = 0;

    /** Return the first index i for which keys[i] >= key. */
    size_t findPosition(const Node* node, int key) const {
        size_t i = 0;
        while (i < node->keys.size() && node->keys[i] < key) i++;
        return i;
    }

    /** Return the node and key position containing key, or an empty location. */
    Location findLocation(int key) const {
        // TODO: Follow exactly one child range at each internal node.
        (void) key;
        return {};
    }

    /** Split a temporary three-key node and promote its median. */
    void splitOverflow(Node* node) {
        // TODO: Distribute four children as [T0,T1] and [T2,T3],
        // update parent pointers, and create a new root when necessary.
        (void) node;
    }

    /** Repair a zero-key nonroot node, possibly continuing at its parent. */
    void repairUnderflow(Node* node) {
        // TODO
        (void) node;
    }

    void borrowFromLeft(Node* parent, size_t childIndex) {
        // TODO: Rotate a parent separator down and sibling maximum up.
        (void) parent;
        (void) childIndex;
    }

    void borrowFromRight(Node* parent, size_t childIndex) {
        // TODO: Rotate a parent separator down and sibling minimum up.
        (void) parent;
        (void) childIndex;
    }

    Node* mergeWithLeft(Node* parent, size_t childIndex) {
        // TODO: Return parent, which may now be underfull.
        (void) childIndex;
        return parent;
    }

    Node* mergeWithRight(Node* parent, size_t childIndex) {
        // TODO: Return parent, which may now be underfull.
        (void) childIndex;
        return parent;
    }

    void appendInorder(const Node* node, vector<int>& result) const {
        if (node == nullptr) return;
        for (size_t i = 0; i < node->keys.size(); i++) {
            if (!node->isLeaf()) appendInorder(node->children[i], result);
            result.push_back(node->keys[i]);
        }
        if (!node->isLeaf()) {
            appendInorder(node->children[node->keys.size()], result);
        }
    }

    bool validateNode(
        const Node* node,
        long long lower,
        long long upper,
        int depth,
        int& leafDepth,
        int& countedKeys
    ) const {
        if (node->keys.size() < 1 || node->keys.size() > 2) return false;
        if (node->keys.size() == 2 && node->keys[0] >= node->keys[1]) {
            return false;
        }
        for (int key : node->keys) {
            if (key <= lower || key >= upper) return false;
        }
        countedKeys += static_cast<int>(node->keys.size());

        if (node->isLeaf()) {
            if (leafDepth == -1) leafDepth = depth;
            return leafDepth == depth;
        }
        if (node->children.size() != node->keys.size() + 1) return false;

        for (size_t i = 0; i < node->children.size(); i++) {
            const Node* child = node->children[i];
            if (child == nullptr || child->parent != node) return false;
            long long childLower = i == 0 ? lower : node->keys[i - 1];
            long long childUpper = i == node->keys.size() ? upper : node->keys[i];
            if (!validateNode(
                child, childLower, childUpper, depth + 1,
                leafDepth, countedKeys
            )) return false;
        }
        return true;
    }

    void destroy(Node* node) {
        if (node == nullptr) return;
        for (Node* child : node->children) destroy(child);
        delete node;
    }

public:
    ~TwoThreeTree() { destroy(root); }

    TwoThreeTree(const TwoThreeTree&) = delete;
    TwoThreeTree& operator=(const TwoThreeTree&) = delete;
    TwoThreeTree() = default;

    int size() const { return keyCount; }
    bool isEmpty() const { return keyCount == 0; }

    bool contains(int key) const {
        return static_cast<bool>(findLocation(key));
    }

    /** Return true when a new key is added; false for a duplicate. */
    bool insert(int key) {
        // TODO:
        // 1. Search to a leaf and reject a duplicate.
        // 2. Insert in sorted order.
        // 3. Split and promote every temporary three-key node.
        // 4. Update keyCount exactly once.
        (void) key;
        return false;
    }

    bool minimum(int& result) const {
        // TODO: Return false for an empty tree; otherwise set result.
        (void) result;
        return false;
    }

    bool maximum(int& result) const {
        // TODO: Return false for an empty tree; otherwise set result.
        (void) result;
        return false;
    }

    /** Return true when key is removed; false when it is absent. */
    bool remove(int key) {
        // TODO:
        // 1. Move an internal target to a predecessor/successor leaf.
        // 2. Remove it from the leaf.
        // 3. Repair underflow by borrowing or merging upward.
        // 4. Replace an empty root by its only child when needed.
        // 5. Update keyCount exactly once.
        (void) key;
        return false;
    }

    vector<int> inorderValues() const {
        vector<int> result;
        appendInorder(root, result);
        return result;
    }

    int height() const {
        int result = -1;
        const Node* node = root;
        while (node != nullptr) {
            result++;
            node = node->isLeaf() ? nullptr : node->children[0];
        }
        return result;
    }

    bool hasValidStructure() const {
        if (root == nullptr) return keyCount == 0;
        if (root->parent != nullptr) return false;
        int leafDepth = -1;
        int countedKeys = 0;
        bool valid = validateNode(
            root,
            numeric_limits<long long>::min(),
            numeric_limits<long long>::max(),
            0,
            leafDepth,
            countedKeys
        );
        return valid && countedKeys == keyCount;
    }
};

// ---------------------------- Tests ----------------------------

static void check(bool condition, const string& description) {
    cout << (condition ? "pass: " : "FAIL: ") << description << '\n';
}

static vector<int> setValues(const set<int>& values) {
    return vector<int>(values.begin(), values.end());
}

static void checkContents(
    const TwoThreeTree& tree,
    const set<int>& expected,
    const string& description
) {
    check(tree.hasValidStructure(), description + " -- invariants");
    check(tree.size() == static_cast<int>(expected.size()), description + " -- size");
    check(tree.inorderValues() == setValues(expected), description + " -- inorder contents");
}

static bool contentsMatch(const TwoThreeTree& tree, const set<int>& expected) {
    return tree.hasValidStructure()
        && tree.size() == static_cast<int>(expected.size())
        && tree.inorderValues() == setValues(expected);
}

int main() {
    TwoThreeTree tree;
    set<int> expected;
    int extreme = 0;

    check(tree.isEmpty(), "new tree is empty");
    check(tree.size() == 0, "new tree has size zero");
    check(!tree.minimum(extreme), "empty tree has no minimum");
    check(!tree.maximum(extreme), "empty tree has no maximum");
    check(!tree.contains(10), "empty tree does not contain 10");
    check(!tree.remove(10), "cannot remove an absent key");

    const vector<int> values = {
        40, 20, 60, 10, 30, 50, 70, 5, 15, 25,
        35, 45, 55, 65, 75, 1, 8, 12, 18
    };
    for (int value : values) {
        if (!tree.insert(value)) {
            check(false, "insert " + to_string(value));
            cout << "Complete insertion before running later tests.\n";
            return 0;
        }
        check(true, "insert " + to_string(value));
        expected.insert(value);
        checkContents(tree, expected, "after inserting " + to_string(value));
    }

    check(!tree.insert(30), "duplicate insertion returns false");
    checkContents(tree, expected, "after duplicate insertion");
    check(tree.minimum(extreme) && extreme == 1, "minimum is 1");
    check(tree.maximum(extreme) && extreme == 75, "maximum is 75");

    const vector<int> removals = {1, 8, 10, 20, 60, 75, 70, 65, 40, 5, 12};
    for (int value : removals) {
        if (!tree.remove(value)) {
            check(false, "remove " + to_string(value));
            cout << "Complete removal before running randomized tests.\n";
            return 0;
        }
        check(true, "remove " + to_string(value));
        expected.erase(value);
        checkContents(tree, expected, "after removing " + to_string(value));
    }
    check(!tree.remove(999), "absent removal returns false");
    checkContents(tree, expected, "after absent removal");

    TwoThreeTree sortedTree;
    for (int value = 1; value <= 31; value++) {
        if (!sortedTree.insert(value) || !sortedTree.hasValidStructure()) {
            check(false, "sorted insertion through " + to_string(value));
            return 0;
        }
    }
    check(true, "sorted insertion through 31");

    TwoThreeTree randomTree;
    set<int> oracle;
    mt19937 generator(23023);
    uniform_int_distribution<int> valueDistribution(-50, 50);
    bernoulli_distribution operationDistribution(0.5);
    for (int step = 0; step < 500; step++) {
        int value = valueDistribution(generator);
        bool doInsert = operationDistribution(generator);
        bool expectedResult;
        if (doInsert) {
            expectedResult = oracle.insert(value).second;
        } else {
            expectedResult = oracle.erase(value) == 1;
        }
        bool actualResult = doInsert
            ? randomTree.insert(value)
            : randomTree.remove(value);
        if (actualResult != expectedResult || !contentsMatch(randomTree, oracle)) {
            check(false, "randomized differential test at update " + to_string(step));
            return 0;
        }
    }
    check(true, "500 randomized differential updates");

    vector<int> remaining(oracle.begin(), oracle.end());
    for (int value : remaining) {
        if (!randomTree.remove(value)) {
            check(false, "remove all remaining keys at " + to_string(value));
            return 0;
        }
        oracle.erase(value);
        if (!contentsMatch(randomTree, oracle)) {
            check(false, "invariants while removing all remaining keys");
            return 0;
        }
    }
    check(true, "remove all remaining keys");
    check(randomTree.isEmpty(), "tree is empty after removing every key");
    check(randomTree.height() == -1, "empty tree has height -1");
}
