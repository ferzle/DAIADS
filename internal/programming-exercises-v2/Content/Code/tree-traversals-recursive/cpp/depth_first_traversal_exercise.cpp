#include <algorithm>
#include <iostream>
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
void preorderHelper(BinaryNode* node, vector<int>& result);
void inorderHelper(BinaryNode* node, vector<int>& result);
void postorderHelper(BinaryNode* node, vector<int>& result);

vector<int> preorder(BinaryNode* node) {
    vector<int> result;
    preorderHelper(node, result);
    return result;
}

// To visit node, append its value to the result vector:
// result.push_back(node->value);

void preorderHelper(BinaryNode* node, vector<int>& result) {
    // TODO
}

vector<int> inorder(BinaryNode* node) {
    vector<int> result;
    inorderHelper(node, result);
    return result;
}

void inorderHelper(BinaryNode* node, vector<int>& result) {
    // TODO
}

vector<int> postorder(BinaryNode* node) {
    vector<int> result;
    postorderHelper(node, result);
    return result;
}

void postorderHelper(BinaryNode* node, vector<int>& result) {
    // TODO
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

void checkSummary(const string& name, bool condition) {
    if (condition) cout << "pass: " << name << endl;
    else { ++failures; cout << "fail: " << name << endl; }
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

void testTraversals() {
    BinaryNode* empty = nullptr;

    check("preorder empty", preorder(empty), vector<int>{});
    check("inorder empty", inorder(empty), vector<int>{});
    check("postorder empty", postorder(empty), vector<int>{});

    BinaryNode* single = new BinaryNode(11);

    check("preorder single", preorder(single), vector<int>{11});
    check("inorder single", inorder(single), vector<int>{11});
    check("postorder single", postorder(single), vector<int>{11});

    BinaryNode* complete = buildCompleteTree();

    check(
        "preorder complete",
        preorder(complete),
        vector<int>{1, 2, 4, 5, 3, 6}
    );
    check(
        "inorder complete",
        inorder(complete),
        vector<int>{4, 2, 5, 1, 6, 3}
    );
    check(
        "postorder complete",
        postorder(complete),
        vector<int>{4, 5, 2, 6, 3, 1}
    );

    check(
        "preorder left subtree",
        preorder(complete->left),
        vector<int>{2, 4, 5}
    );
    check(
        "inorder left subtree",
        inorder(complete->left),
        vector<int>{4, 2, 5}
    );
    check(
        "postorder left subtree",
        postorder(complete->left),
        vector<int>{4, 5, 2}
    );

    BinaryNode* degenerate = buildDegenerateTree();

    check(
        "preorder degenerate",
        preorder(degenerate),
        vector<int>{7, 8, 9, 10}
    );
    check(
        "inorder degenerate",
        inorder(degenerate),
        vector<int>{7, 8, 9, 10}
    );
    check(
        "postorder degenerate",
        postorder(degenerate),
        vector<int>{10, 9, 8, 7}
    );

    destroyTree(single);
    destroyTree(complete);
    destroyTree(degenerate);
}

void testIrregularTreeAndIndependentResults() {
    BinaryNode* root = new BinaryNode(10);
    root->left = new BinaryNode(20); root->right = new BinaryNode(30);
    root->left->right = new BinaryNode(40); root->left->right->left = new BinaryNode(20);
    root->right->left = new BinaryNode(50); root->right->left->right = new BinaryNode(60);
    check("irregular preorder", preorder(root), {10, 20, 40, 20, 30, 50, 60});
    check("irregular inorder", inorder(root), {20, 20, 40, 10, 50, 60, 30});
    check("irregular postorder", postorder(root), {20, 40, 20, 60, 50, 30, 10});
    vector<int> first = preorder(root); first.clear();
    check("returned vectors are independent", preorder(root), {10, 20, 40, 20, 30, 50, 60});
    check("interior subtree", inorder(root->right), {50, 60, 30});
    destroyTree(root);
}

void testHundredThousandNodeCompleteTree() {
    const int count = 100000;
    vector<BinaryNode*> nodes;
    nodes.reserve(count);
    for (int i = 0; i < count; i++) nodes.push_back(new BinaryNode(i));
    for (int i = 0; i < count; i++) {
        int left = 2 * i + 1, right = left + 1;
        if (left < count) nodes[i]->left = nodes[left];
        if (right < count) nodes[i]->right = nodes[right];
    }
    vector<int> pre = preorder(nodes[0]), in = inorder(nodes[0]), post = postorder(nodes[0]);
    bool ok = pre.size() == count && in.size() == count && post.size() == count
              && pre.front() == 0 && in.front() == 65535 && post.back() == 0;
    sort(pre.begin(), pre.end()); sort(in.begin(), in.end()); sort(post.begin(), post.end());
    if (ok) {
        for (int i = 0; i < count; i++) ok = pre[i] == i && in[i] == i && post[i] == i && ok;
    }
    checkSummary("100,000-node depth-first traversals", ok);
    destroyTree(nodes[0]);
}

int main() {
    testTraversals();
    testIrregularTreeAndIndependentResults();
    testHundredThousandNodeCompleteTree();
    return failures == 0 ? 0 : 1;
}
