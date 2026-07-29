import inspect


class Node:
    def __init__(self, value):
        self.value = value
        self.next = None
        self.prev = None


class IntDoublyList:
    def __init__(self):
        self.head = None
        self.tail = None
        self.count = 0

    def is_empty(self):
        return self.count == 0

    def size(self):
        return self.count

    def insert_at_head(self, value):
        new_node = Node(value)

        if self.is_empty():
            self.head = new_node
            self.tail = new_node
        else:
            new_node.next = self.head
            self.head.prev = new_node
            self.head = new_node

        self.count += 1

    def insert_at_tail(self, value):
        # TODO:
        # Create a new node.
        # If the list is empty, head and tail should both point to it.
        # Otherwise:
        #   old tail's next should point to the new node
        #   new node's prev should point to the old tail
        #   tail should move to the new node
        # Do not forget to update count.
        pass

    def delete_from_head(self):
        if self.is_empty():
            return -1

        value = self.head.value

        if self.head is self.tail:
            self.head = None
            self.tail = None
        else:
            self.head = self.head.next
            self.head.prev = None

        self.count -= 1
        return value

    def delete_from_tail(self):
        # TODO:
        # If the list is empty, return -1.
        # Save self.tail.value.
        # If there is one node, set both head and tail to None.
        # Otherwise:
        #   move tail to tail.prev
        #   set tail.next to None
        # Decrease count and return the saved value.
        return -1

    def traverse_forward(self):
        # TODO:
        # Return values from head to tail, such as "4 -> 7 -> 9".
        # Return "" for an empty list.
        return ""

    def traverse_backward(self):
        # TODO:
        # Return values from tail to head, such as "9 -> 7 -> 4".
        # Return "" for an empty list.
        return ""

    def search_forward(self, value):
        # Optional -- time permitting.
        # Return the first node containing value, or None if not found.
        return None

    def insert_after(self, node, value):
        # Optional -- time permitting.
        # If node is None, do nothing.
        # If node is self.tail, this should behave like insert_at_tail(value).
        # Otherwise, insert the new node between node and node.next.
        pass

    def insert_before(self, node, value):
        # Optional -- time permitting.
        # If node is None, do nothing.
        # If node is self.head, this should behave like insert_at_head(value).
        # Otherwise, insert the new node between node.prev and node.
        pass

    def delete_node(self, node):
        # Optional -- time permitting.
        # If node is None, return -1.
        # If node is self.head, use delete_from_head().
        # If node is self.tail, use delete_from_tail().
        # Otherwise, unlink node from both directions and return its value.
        return -1


def check(actual, expected):
    if actual == expected:
        print("pass")
    else:
        line = inspect.currentframe().f_back.f_lineno
        print(f"fail at test line {line}: expected {expected!r} but got {actual!r}")


def test_doubly_list():
    lst = IntDoublyList()

    check(lst.is_empty(), True)
    check(lst.size(), 0)
    check(lst.delete_from_head(), -1)
    check(lst.delete_from_tail(), -1)
    check(lst.traverse_forward(), "")
    check(lst.traverse_backward(), "")

    lst.insert_at_head(7)
    check(lst.traverse_forward(), "7")
    check(lst.traverse_backward(), "7")
    check(lst.size(), 1)

    lst.insert_at_head(4)
    check(lst.traverse_forward(), "4 -> 7")
    check(lst.traverse_backward(), "7 -> 4")

    lst.insert_at_tail(9)
    check(lst.traverse_forward(), "4 -> 7 -> 9")
    check(lst.traverse_backward(), "9 -> 7 -> 4")
    check(lst.size(), 3)

    check(lst.delete_from_head(), 4)
    check(lst.traverse_forward(), "7 -> 9")
    check(lst.traverse_backward(), "9 -> 7")

    check(lst.delete_from_tail(), 9)
    check(lst.traverse_forward(), "7")
    check(lst.traverse_backward(), "7")

    check(lst.delete_from_tail(), 7)
    check(lst.traverse_forward(), "")
    check(lst.traverse_backward(), "")
    check(lst.is_empty(), True)
    check(lst.size(), 0)

    lst.insert_at_tail(12)
    check(lst.traverse_forward(), "12")
    check(lst.traverse_backward(), "12")
    check(lst.delete_from_head(), 12)
    check(lst.is_empty(), True)


test_doubly_list()
