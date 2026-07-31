class Node:
    def __init__(self, data, next=None):
        self.data = data
        self.next = next


class LinkedSequence:
    def __init__(self):
        self.head = None

    def insert_at_head(self, value):
        # TODO: Create a new node whose next link is the old head.
        # Then update self.head.
        pass

    def delete_at_head(self):
        # TODO: If the list is empty, raise an IndexError.
        # Otherwise, save the head value, move self.head to self.head.next,
        # and return the value.
        pass

    def insert_after(self, node, value):
        # TODO: If node is None, raise a ValueError.
        # Otherwise, create a new node and insert it immediately after node.
        pass

    def delete_after(self, node):
        # TODO: If node is None or node.next is None, raise a ValueError.
        # Otherwise, remove node.next and return its value.
        pass

    def search(self, value):
        # TODO: Return the first node containing value, or None if the value is not found.
        pass

    def traverse(self):
        # TODO: Return a string such as "2 -> 9 -> 4", or "" for an empty list.
        pass


def check(actual, expected):
    if actual != expected:
        raise AssertionError(f"Expected {expected!r} but got {actual!r}")


def expect_exception(action, exception_type):
    try:
        action()
        raise AssertionError("Expected an exception, but none was raised")
    except exception_type:
        pass


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

    print("All tests passed.")


test_linked_sequence()
