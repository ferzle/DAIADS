#include <algorithm>
#include <array>
#include <iostream>
#include <limits>
#include <optional>
#include <random>
#include <set>
#include <string>
#include <vector>

using namespace std;

/**
 * Starter implementation for an ordered set backed by a 2-3 tree.
 * Complete the methods marked TODO. std::set is used only as a test oracle;
 * it must not be used to implement the tree. During insertion repair a node
 * may temporarily use all three key slots and four child slots. During
 * removal repair a nonroot node may temporarily have no keys and one child.
 */
class TwoThreeTree {
private:
    struct Node {
        // Valid nodes use only part of this capacity. The final key and child
        // slots hold the temporary overflow created during insertion.
        array<int, 3> keys{};
        array<Node*, 4> children{};
        size_t keyCount = 0;
        size_t childCount = 0;
        Node* parent = nullptr;

        Node() = default;
        explicit Node(int key) : keys{key, 0, 0}, keyCount(1) {}
        bool isLeaf() const { return childCount == 0; }
    };

    /**
     * Describes where a search ended. When found is true, keyIndex identifies
     * the requested key. Otherwise node is the leaf and keyIndex the insertion
     * position for that absent key. A null node represents an empty tree.
     */
    struct Location {
        Node* node = nullptr;
        size_t keyIndex = 0;
        bool found = false;
    };

    /**
     * The original overfull node becomes the left half. This object carries
     * the key to promote and the new right half back to the caller.
     */
    struct SplitResult {
        int promotedKey = 0;
        Node* rightNode = nullptr;
    };

    Node* root = nullptr;
    int keyCount = 0;

    /** Return the first active index i for which keys[i] >= key. */
    size_t findPosition(const Node* node, int key) const {
        size_t i = 0;
        while (i < node->keyCount && node->keys[i] < key) i++;
        return i;
    }

    /** Locate key, or the leaf position where an absent key would be inserted. */
    Location locate(int key) const {
        Node* node = root;
        while (node != nullptr) {
            size_t index = findPosition(node, key);
            if (index < node->keyCount && node->keys[index] == key) {
                return {node, index, true};
            }
            if (node->isLeaf()) return {node, index, false};
            node = node->children[index];
        }
        return {};
    }

    /** Return the active child index, or childCount if references disagree. */
    size_t childIndex(const Node* parent, const Node* child) const {
        for (size_t i = 0; i < parent->childCount; i++) {
            if (parent->children[i] == child) return i;
        }
        return parent->childCount;
    }

    /** Insert into the active key prefix; the caller preserves sorted order. */
    void insertKeyAt(Node* node, size_t index, int key) {
        for (size_t i = node->keyCount; i > index; i--) {
            node->keys[i] = node->keys[i - 1];
        }
        node->keys[index] = key;
        node->keyCount++;
    }

    /** Remove and return an active key, closing the array gap. */
    int removeKeyAt(Node* node, size_t index) {
        int removed = node->keys[index];
        for (size_t i = index; i + 1 < node->keyCount; i++) {
            node->keys[i] = node->keys[i + 1];
        }
        node->keyCount--;
        return removed;
    }

    /** Insert a child and maintain its upward parent pointer. */
    void insertChildAt(Node* node, size_t index, Node* child) {
        for (size_t i = node->childCount; i > index; i--) {
            node->children[i] = node->children[i - 1];
        }
        node->children[index] = child;
        node->childCount++;
        if (child != nullptr) child->parent = node;
    }

    /** Remove a child, clear the stale slot, and detach its parent pointer. */
    Node* removeChildAt(Node* node, size_t index) {
        Node* removed = node->children[index];
        for (size_t i = index; i + 1 < node->childCount; i++) {
            node->children[i] = node->children[i + 1];
        }
        node->childCount--;
        node->children[node->childCount] = nullptr;
        if (removed != nullptr) removed->parent = nullptr;
        return removed;
    }

    /** Split node into a left half, promoted key, and new right half. */
    SplitResult splitOverfullNode(Node* node) {
        // TODO: Distribute four children as [T0,T1] and [T2,T3] and
        // update the parent pointers of moved children.
        (void) node;
        return {};
    }

    /** Insert a child split into its parent; left is already at leftIndex. */
    void promoteToParent(
        Node* parent,
        size_t leftIndex,
        int promotedKey,
        Node* rightChild
    ) {
        insertKeyAt(parent, leftIndex, promotedKey);
        insertChildAt(parent, leftIndex + 1, rightChild);
    }

    /** Repair a zero-key nonroot node, possibly continuing at its parent. */
    void repairDeficiency(Node* node) {
        // TODO
        (void) node;
    }

    void redistributeFromLeft(Node* parent, size_t childIndex) {
        // TODO: Rotate a parent separator down and sibling maximum up.
        (void) parent;
        (void) childIndex;
    }

    void redistributeFromRight(Node* parent, size_t childIndex) {
        // TODO: Rotate a parent separator down and sibling minimum up.
        (void) parent;
        (void) childIndex;
    }

    /** Merge children around separatorIndex into the left child. */
    void mergeChildren(Node* parent, size_t separatorIndex) {
        // TODO: Removing the separator may leave parent deficient.
        (void) parent;
        (void) separatorIndex;
    }

    void appendInorder(const Node* node, vector<int>& result) const {
        if (node == nullptr) return;
        for (size_t i = 0; i < node->keyCount; i++) {
            if (!node->isLeaf()) appendInorder(node->children[i], result);
            result.push_back(node->keys[i]);
        }
        if (!node->isLeaf()) {
            appendInorder(node->children[node->keyCount], result);
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
        if (node->keyCount < 1 || node->keyCount > 2) return false;
        if (node->keyCount == 2 && node->keys[0] >= node->keys[1]) {
            return false;
        }
        for (size_t i = 0; i < node->keyCount; i++) {
            int key = node->keys[i];
            if (key <= lower || key >= upper) return false;
        }
        countedKeys += static_cast<int>(node->keyCount);

        if (node->isLeaf()) {
            if (leafDepth == -1) leafDepth = depth;
            return leafDepth == depth;
        }
        if (node->childCount != node->keyCount + 1) return false;
        for (size_t i = node->childCount; i < node->children.size(); i++) {
            if (node->children[i] != nullptr) return false;
        }

        for (size_t i = 0; i < node->childCount; i++) {
            const Node* child = node->children[i];
            if (child == nullptr || child->parent != node) return false;
            long long childLower = i == 0 ? lower : node->keys[i - 1];
            long long childUpper = i == node->keyCount ? upper : node->keys[i];
            if (!validateNode(
                child, childLower, childUpper, depth + 1,
                leafDepth, countedKeys
            )) return false;
        }
        return true;
    }

    Location minimumLocation(Node* node) const {
        while (!node->isLeaf()) node = node->children[0];
        return {node, 0, true};
    }

    Location maximumLocation(Node* node) const {
        while (!node->isLeaf()) node = node->children[node->keyCount];
        return {node, node->keyCount - 1, true};
    }

    void destroy(Node* node) {
        if (node == nullptr) return;
        for (size_t i = 0; i < node->childCount; i++) {
            destroy(node->children[i]);
        }
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
        return locate(key).found;
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

    optional<int> minimum() const {
        if (root == nullptr) return nullopt;
        Location location = minimumLocation(root);
        return location.node->keys[location.keyIndex];
    }

    optional<int> maximum() const {
        // TODO: Use maximumLocation symmetrically to minimum().
        return nullopt;
    }

    optional<int> predecessor(int key) const {
        // TODO: Return nullopt if key is absent or has no predecessor.
        (void) key;
        return nullopt;
    }

    optional<int> successor(int key) const {
        // TODO: Return nullopt if key is absent or has no successor.
        (void) key;
        return nullopt;
    }

    vector<int> valuesInRange(int low, int high) const {
        // TODO: Return keys in the inclusive interval, pruning child ranges.
        (void) low;
        (void) high;
        return {};
    }

    /** Return true when key is removed; false when it is absent. */
    bool remove(int key) {
        // TODO:
        // 1. Move an internal target to a predecessor/successor leaf.
        // 2. Remove it from the leaf.
        // 3. Repair deficiency by redistribution or merging upward.
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

    check(tree.isEmpty(), "new tree is empty");
    check(tree.size() == 0, "new tree has size zero");
    check(!tree.minimum().has_value(), "empty tree has no minimum");
    check(!tree.maximum().has_value(), "empty tree has no maximum");
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
    check(tree.minimum() == optional<int>(1), "minimum is 1");
    check(tree.maximum() == optional<int>(75), "maximum is 75");
    check(!tree.predecessor(1).has_value(), "minimum has no predecessor");
    check(tree.predecessor(40) == optional<int>(35), "predecessor of 40 is 35");
    check(tree.successor(40) == optional<int>(45), "successor of 40 is 45");
    check(!tree.successor(75).has_value(), "maximum has no successor");
    check(!tree.predecessor(999).has_value(), "absent key has no predecessor");
    check(
        tree.valuesInRange(18, 45)
            == vector<int>({18, 20, 25, 30, 35, 40, 45}),
        "inclusive range from 18 through 45"
    );
    check(tree.valuesInRange(45, 18).empty(), "reversed range is empty");

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
