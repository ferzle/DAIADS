#include <iostream>
  using namespace std;

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

int main() {
    testAlgorithms();
    return 0;
}
