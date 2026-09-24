#include <iostream>
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

  int size(BinaryNode* node) {
      // TODO
      return -1;
  }

  int height(BinaryNode* node) {
      // TODO
      return 0;
  }

  int countLeaves(BinaryNode* node) {
      // TODO
      return -1;
  }

  int countTwoChildNodes(BinaryNode* node) {
      // TODO
      return -1;
  }

  void checkAtLine(int actual, int expected, int line, const char* expression) {
      if (actual == expected) {
          cout << "PASS at test line " << line << " (" << expression << "): got " << actual << endl;
      } else {
      ++failures;
      cout << "FAIL at test line " << line << " (" << expression
           << "): expected " << expected << " but got " << actual << endl;
  }
}

#define check(actual, expected) checkAtLine((actual), (expected), __LINE__, #actual)

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

void testAlgorithms() {
  BinaryNode* empty = nullptr;

  check(size(empty), 0);
  check(height(empty), -1);
  check(countLeaves(empty), 0);
  check(countTwoChildNodes(empty), 0);

  BinaryNode* single = new BinaryNode(11);

  check(size(single), 1);
  check(height(single), 0);
  check(countLeaves(single), 1);
  check(countTwoChildNodes(single), 0);

  BinaryNode* complete = buildCompleteTree();

  check(size(complete), 6);
  check(height(complete), 2);
  check(countLeaves(complete), 3);
  check(countTwoChildNodes(complete), 2);

  check(size(complete->left), 3);
  check(height(complete->left), 1);
  check(countLeaves(complete->left), 2);
  check(countTwoChildNodes(complete->left), 1);

  BinaryNode* degenerate = buildDegenerateTree();

  check(size(degenerate), 4);
  check(height(degenerate), 3);
  check(countLeaves(degenerate), 1);
  check(countTwoChildNodes(degenerate), 0);

  destroyTree(single);
  destroyTree(complete);
  destroyTree(degenerate);
}

void testIrregularTreeAndEverySubtree() {
    BinaryNode* root = new BinaryNode(10);
    root->left = new BinaryNode(20); root->right = new BinaryNode(30);
    root->left->right = new BinaryNode(40); root->left->right->left = new BinaryNode(50);
    root->right->left = new BinaryNode(60); root->right->left->right = new BinaryNode(70);
    root->right->right = new BinaryNode(80); root->right->right->right = new BinaryNode(90);
    check(size(root), 9); check(height(root), 4); check(countLeaves(root), 3); check(countTwoChildNodes(root), 2);
    check(size(root->left), 3); check(height(root->left), 3); check(countLeaves(root->left), 1);
    check(size(root->right), 5); check(height(root->right), 3); check(countLeaves(root->right), 2);
    BinaryNode* chain = new BinaryNode(0); BinaryNode* cursor = chain;
    for (int i = 1; i < 200; i++) { cursor->right = new BinaryNode(i); cursor = cursor->right; }
    check(size(chain), 200); check(height(chain), 199); check(countLeaves(chain), 1); check(countTwoChildNodes(chain), 0);
    destroyTree(root); destroyTree(chain);
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
    check(size(nodes[0]), count);
    check(height(nodes[0]), 16);
    check(countLeaves(nodes[0]), 50000);
    check(countTwoChildNodes(nodes[0]), 49999);
    destroyTree(nodes[0]);
}

int main() {
    testAlgorithms();
    testIrregularTreeAndEverySubtree();
    testHundredThousandNodeCompleteTree();
    return failures == 0 ? 0 : 1;
}
