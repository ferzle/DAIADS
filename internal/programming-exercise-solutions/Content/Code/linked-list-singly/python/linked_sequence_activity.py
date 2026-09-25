class Node:
    def __init__(self, data, next=None):
        self.data = data
        self.next = next


class LinkedSequence:
    def __init__(self):
        self.head = None

    def insert_at_head(self, value):
        self.head = Node(value, self.head)

    def delete_at_head(self):
        if self.head is None:
            raise IndexError("list is empty")
        value = self.head.data
        self.head = self.head.next
        return value

    def insert_after(self, node, value):
        if node is None:
            raise ValueError("node must not be None")
        node.next = Node(value, node.next)

    def delete_after(self, node):
        if node is None or node.next is None:
            raise ValueError("node must have a successor")
        value = node.next.data
        node.next = node.next.next
        return value

    def search(self, value):
        node = self.head
        while node is not None:
            if node.data == value:
                return node
            node = node.next
        return None

    def traverse(self):
        values = []
        node = self.head
        while node is not None:
            values.append(str(node.data))
            node = node.next
        return " -> ".join(values)


def check(actual, expected):
    if actual != expected:
        raise AssertionError(f"Expected {expected!r} but got {actual!r}")


def expect_exception(action, exception_type):
    try:
        action()
        raise AssertionError("Expected an exception, but none was raised")
    except exception_type:
        pass


def test_large_mixed_sequence():
    lst = LinkedSequence()
    count = 100_000
    for value in range(count):
        lst.insert_at_head(value)
    tail = lst.search(0)
    check(tail is not None and tail.data == 0, True)
    ok = all(lst.delete_at_head() == expected for expected in range(count - 1, -1, -1))
    check(ok, True)
    check(lst.traverse(), "")
    expect_exception(lambda: lst.delete_at_head(), IndexError)


def test_linked_sequence():
    lst = LinkedSequence()

    check(lst.traverse(), "")

    lst.insert_at_head(4)
    check(lst.traverse(), "4")

    lst.insert_at_head(9)
    check(lst.traverse(), "9 -> 4")

    lst.insert_at_head(2)
    check(lst.traverse(), "2 -> 9 -> 4")

    node9 = lst.search(9)
    assert node9 is not None
    check(node9.data, 9)

    lst.insert_after(node9, 7)
    check(lst.traverse(), "2 -> 9 -> 7 -> 4")

    check(lst.delete_after(node9), 7)
    check(lst.traverse(), "2 -> 9 -> 4")

    check(lst.delete_at_head(), 2)
    check(lst.traverse(), "9 -> 4")

    check(lst.search(20), None)

    node4 = lst.search(4)
    expect_exception(lambda: lst.delete_after(node4), ValueError)
    expect_exception(lambda: lst.insert_after(None, 8), ValueError)
    expect_exception(lambda: lst.delete_after(None), ValueError)

    check(lst.delete_at_head(), 9)
    check(lst.delete_at_head(), 4)
    check(lst.traverse(), "")
    expect_exception(lambda: lst.delete_at_head(), IndexError)

    test_large_mixed_sequence()

    print("All tests passed.")


test_linked_sequence()
