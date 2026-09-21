#include <iostream>
#include <queue>
#include <string>
#include <vector>
using namespace std;
int failures = 0;

struct BinaryNode {
    int value;
    BinaryNode* left;
    BinaryNode* right;

    BinaryNode(int v) {
        value = v;
        left = nullptr;
        right = nullptr;
    }
};

vector<int> levelOrder(BinaryNode* root) {
    vector<int> result;

    if (root == nullptr) {
        return result;
    }

    queue<BinaryNode*> nodes;
    nodes.push(root);

    while (!nodes.empty()) {
        BinaryNode* node = nodes.front();
        nodes.pop();

        // To visit node:
        // result.push_back(node->value);

        // TODO: Visit node and add its existing children
        // to the back of the queue in left-to-right order.
    }

    return result;
}

void printVector(const vector<int>& values) {
    cout << "[";
    for (int i = 0; i < static_cast<int>(values.size()); i++) {
        if (i > 0) {
            cout << ", ";
        }
        cout << values[i];
    }
    cout << "]";
}

void check(
    const string& name,
    const vector<int>& actual,
    const vector<int>& expected
) {
    if (actual == expected) {
        cout << "pass: " << name << endl;
    } else {
        ++failures;
        cout << "fail: " << name << "; expected ";
        printVector(expected);
        cout << " but got ";
        printVector(actual);
        cout << endl;
    }
}

BinaryNode* buildCompleteTree() {
    BinaryNode* root = new BinaryNode(1);
    root->left = new BinaryNode(2);
    root->right = new BinaryNode(3);
    root->left->left = new BinaryNode(4);
    root->left->right = new BinaryNode(5);
    root->right->left = new BinaryNode(6);
    return root;
}

BinaryNode* buildDegenerateTree() {
    BinaryNode* root = new BinaryNode(7);
    root->right = new BinaryNode(8);
    root->right->right = new BinaryNode(9);
    root->right->right->right = new BinaryNode(10);
    return root;
}

void destroyTree(BinaryNode* node) {
    if (node == nullptr) {
        return;
    }
    destroyTree(node->left);
    destroyTree(node->right);
    delete node;
}

void testLevelOrder() {
    BinaryNode* empty = nullptr;
    BinaryNode* single = new BinaryNode(11);
    BinaryNode* complete = buildCompleteTree();
    BinaryNode* degenerate = buildDegenerateTree();

    check("level order empty", levelOrder(empty), vector<int>{});
    check("level order single", levelOrder(single), vector<int>{11});
    check(
        "level order complete",
        levelOrder(complete),
        vector<int>{1, 2, 3, 4, 5, 6}
    );
    check(
        "level order left subtree",
        levelOrder(complete->left),
        vector<int>{2, 4, 5}
    );
    check(
        "level order degenerate",
        levelOrder(degenerate),
        vector<int>{7, 8, 9, 10}
    );

    destroyTree(single);
    destroyTree(complete);
    destroyTree(degenerate);
}

void testSparseWideAndIndependentResults() {
    BinaryNode* root = new BinaryNode(10);
    root->left = new BinaryNode(20); root->right = new BinaryNode(30);
    root->left->right = new BinaryNode(40); root->right->left = new BinaryNode(50);
    root->left->right->left = new BinaryNode(60); root->right->left->right = new BinaryNode(70);
    check("sparse mixed tree", levelOrder(root), {10, 20, 30, 40, 50, 60, 70});
    check("interior subtree", levelOrder(root->left), {20, 40, 60});
    vector<int> first = levelOrder(root); first.clear();
    check("returned vectors are independent", levelOrder(root), {10, 20, 30, 40, 50, 60, 70});
    BinaryNode* chain = new BinaryNode(0); BinaryNode* cursor = chain;
    vector<int> expected{0};
    for (int i = 1; i <= 200; i++) { cursor->right = new BinaryNode(i); cursor = cursor->right; expected.push_back(i); }
    check("long right-only tree", levelOrder(chain), expected);
    destroyTree(root); destroyTree(chain);
}

int main() {
    testLevelOrder();
    testSparseWideAndIndependentResults();
    return failures == 0 ? 0 : 1;
}
