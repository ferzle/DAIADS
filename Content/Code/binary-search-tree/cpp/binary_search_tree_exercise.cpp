#include <iostream>

using namespace std;

class BinarySearchTree {
private:
  struct Node {
    int key;
    Node* left;
    Node* right;
    Node* parent;

    Node(int value) {
        key = value;
        left = nullptr;
        right = nullptr;
        parent = nullptr;
    }
  };

  Node* root;
  int nodeCount;

  Node* searchNode(int key) {
      // TODO
      return nullptr;
  }

  Node* minimumNode(Node* node) {
      // TODO
      return nullptr;
  }

  Node* successorNode(Node* node) {
      // TODO
      return nullptr;
  }

  void replaceSubtree(Node* oldRoot, Node* newRoot) {
      // TODO
  }

  //--------------------------------------------------
  // The next few methods are helpers for the tests
  //--------------------------------------------------
  
  void appendInorder(Node* node, int values[], int& index) {
      if (node == nullptr) {
          return;
      }

      appendInorder(node->left, values, index);
      values[index] = node->key;
      index++;
      appendInorder(node->right, values, index);
  }

  bool hasCorrectParentReferences(Node* node) {
      if (node == nullptr) {
          return true;
      }

      if (node->left != nullptr && node->left->parent != node) {
          return false;
      }

      if (node->right != nullptr && node->right->parent != node) {
          return false;
      }

      return hasCorrectParentReferences(node->left)
          && hasCorrectParentReferences(node->right);
  }

  // Necessary memory management for C++ implementation
  void destroyTree(Node* node) {
      if (node == nullptr) {
          return;
      }

      destroyTree(node->left);
      destroyTree(node->right);
      delete node;
  }

//--------------------------------------------------
// More stuff to implement
//--------------------------------------------------
public:
  BinarySearchTree() {
    root = nullptr;
    nodeCount = 0;
  }

  ~BinarySearchTree() {
      destroyTree(root);
  }

  int size() {
      return nodeCount;
  }

  bool isEmpty() {
      return nodeCount == 0;
  }

  bool contains(int key) {
      return searchNode(key) != nullptr;
  }

  bool insert(int key) {
      // TODO
      return false;
  }

  // C++ cannot return nullptr in place of an int, so this method
  // returns whether a result exists and stores the key in result.
  bool minimum(int& result) {
      Node* node = minimumNode(root);

      if (node == nullptr) {
          return false;
      }

      result = node->key;
      return true;
  }

  // C++ cannot return nullptr in place of an int, so this method
  // returns whether a result exists and stores the key in result.
  bool successor(int key, int& result) {
      Node* node = searchNode(key);

      if (node == nullptr) {
          return false;
      }

      Node* next = successorNode(node);

      if (next == nullptr) {
          return false;
      }

      result = next->key;
      return true;
  }

  bool remove(int key) {
      // TODO
      return false;
  }

  
  //--------------------------------------------------
  // The rest of the code is for the tests
  //--------------------------------------------------
  int inorder(int values[]) {
      int index = 0;
      appendInorder(root, values, index);
      return index;
  }

  bool hasCorrectParentReferences() {
      if (root != nullptr && root->parent != nullptr) {
          return false;
      }

      return hasCorrectParentReferences(root);
  }

};

  void check(bool condition, const char* description) {
    if (condition) {
      cout << "pass: " << description << endl;
    } else {
      cout << "fail: " << description << endl;
    }
  }

  bool sameArray(int actual[],int actualLength,int expected[],int expectedLength) {
    if (actualLength != expectedLength) {
      return false;
    }

    for (int i = 0; i < actualLength; i++) {
        if (actual[i] != expected[i]) {
            return false;
        }
    }
   return true;
  }

  void testBinarySearchTree() {
    BinarySearchTree tree;
    int result = 0;

    check(tree.isEmpty(), "a new tree is empty");
    check(tree.size() == 0, "a new tree has size 0");
    check(!tree.minimum(result), "an empty tree has no minimum");
    check(!tree.contains(50), "an empty tree does not contain 50");
    check(!tree.remove(50), "an absent key cannot be removed");

    int keys[] = { 50, 30, 70, 20, 40, 60, 80, 55, 65, 57 };

    for (int i = 0; i < 10; i++) {
        check(tree.insert(keys[i]), "insert a distinct key");
        check(tree.hasCorrectParentReferences(),
            "parent references after insertion");
    }

    check(tree.size() == 10, "the tree has size 10");
    check(!tree.isEmpty(), "the tree is not empty");
    check(tree.contains(57), "the tree contains 57");
    check(!tree.contains(58), "the tree does not contain 58");

    int actual[10] = {0};
    int actualLength = tree.inorder(actual);
    int expected1[] = {
        20, 30, 40, 50, 55, 57, 60, 65, 70, 80
    };

    check(sameArray(actual, actualLength, expected1, 10),
        "inorder traversal after insertion");

    check(!tree.insert(60), "duplicate insertion fails");
    check(tree.size() == 10, "duplicate insertion does not change size");

    check(tree.minimum(result) && result == 20, "the minimum is 20");

    check(tree.successor(40, result) && result == 50,
        "the successor of 40 is 50");
    check(tree.successor(55, result) && result == 57,
        "the successor of 55 is 57");
    check(tree.successor(65, result) && result == 70,
        "the successor of 65 is 70");
    check(!tree.successor(80, result), "80 has no successor");
    check(!tree.successor(58, result), "an absent key has no successor");

    check(tree.remove(20), "remove the leaf 20");

    int expected2[] = {
        30, 40, 50, 55, 57, 60, 65, 70, 80
    };

    actualLength = tree.inorder(actual);

    check(sameArray(actual, actualLength, expected2, 9),
        "inorder traversal after removing 20");
    check(tree.hasCorrectParentReferences(),
        "parent references after removing 20");

    check(tree.remove(55), "remove the one-child node 55");

    int expected3[] = {
        30, 40, 50, 57, 60, 65, 70, 80
    };

    actualLength = tree.inorder(actual);

    check(sameArray(actual, actualLength, expected3, 8),
        "inorder traversal after removing 55");
    check(tree.hasCorrectParentReferences(),
        "parent references after removing 55");

    check(tree.remove(50), "remove the two-child root 50");

    int expected4[] = {30, 40, 57, 60, 65, 70, 80};

    actualLength = tree.inorder(actual);

    check(sameArray(actual, actualLength, expected4, 7),
        "inorder traversal after removing 50");
    check(tree.hasCorrectParentReferences(),
        "parent references after removing 50");

    check(tree.size() == 7, "the final size is 7");
    check(!tree.remove(50), "removing 50 again fails");
    check(tree.size() == 7,
        "an unsuccessful removal does not change size");

    BinarySearchTree oneNodeTree;

    check(oneNodeTree.insert(10), "insert into an empty tree");
    check(oneNodeTree.remove(10), "remove the only node");
    check(oneNodeTree.isEmpty(), "the one-node tree becomes empty");
    check(oneNodeTree.hasCorrectParentReferences(),
        "the empty tree has valid parent references");
  }

int main() {
  testBinarySearchTree();
  return 0;
}
