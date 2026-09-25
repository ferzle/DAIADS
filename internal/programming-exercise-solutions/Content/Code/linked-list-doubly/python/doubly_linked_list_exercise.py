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
        new_node = Node(value)
        if self.is_empty():
            self.head = new_node
        else:
            self.tail.next = new_node
            new_node.prev = self.tail
        self.tail = new_node
        self.count += 1

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
        if self.is_empty():
            return -1
        value = self.tail.value
        if self.head is self.tail:
            self.head = None
            self.tail = None
        else:
            self.tail = self.tail.prev
            self.tail.next = None
        self.count -= 1
        return value

    def traverse_forward(self):
        values = []
        node = self.head
        while node is not None:
            values.append(str(node.value))
            node = node.next
        return " -> ".join(values)

    def traverse_backward(self):
        values = []
        node = self.tail
        while node is not None:
            values.append(str(node.value))
            node = node.prev
        return " -> ".join(values)

    def search_forward(self, value):
        node = self.head
        while node is not None:
            if node.value == value:
                return node
            node = node.next
        return None

    def insert_after(self, node, value):
        if node is None:
            return
        if node is self.tail:
            self.insert_at_tail(value)
            return
        new_node = Node(value)
        new_node.prev = node
        new_node.next = node.next
        node.next.prev = new_node
        node.next = new_node
        self.count += 1

    def insert_before(self, node, value):
        if node is None:
            return
        if node is self.head:
            self.insert_at_head(value)
            return
        new_node = Node(value)
        new_node.prev = node.prev
        new_node.next = node
        node.prev.next = new_node
        node.prev = new_node
        self.count += 1

    def delete_node(self, node):
        if node is None:
            return -1
        if node is self.head:
            return self.delete_from_head()
        if node is self.tail:
            return self.delete_from_tail()
        value = node.value
        node.prev.next = node.next
        node.next.prev = node.prev
        self.count -= 1
        return value


failures = 0


def check(actual, expected):
    global failures
    line = inspect.currentframe().f_back.f_lineno
    if actual == expected:
        print(f"PASS at test line {line}: got {actual!r}")
    else:
        failures += 1
        print(f"FAIL at test line {line}: expected {expected!r} but got {actual!r}")


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

    lst.insert_at_tail(4)
    lst.insert_at_tail(7)
    lst.insert_at_tail(9)
    node7 = lst.search_forward(7)
    check(node7 is not None, True)
    check(lst.search_forward(99) is None, True)
    lst.insert_before(node7, 6)
    lst.insert_after(node7, 8)
    check(lst.traverse_forward(), "4 -> 6 -> 7 -> 8 -> 9")
    check(lst.traverse_backward(), "9 -> 8 -> 7 -> 6 -> 4")
    check(lst.delete_node(node7), 7)
    check(lst.traverse_forward(), "4 -> 6 -> 8 -> 9")
    check(lst.delete_node(None), -1)
    check(lst.size(), 4)


def test_large_drain_and_endpoint_helpers():
    lst = IntDoublyList()
    half = 50_000
    for value in range(half):
        lst.insert_at_tail(value)
    ok = lst.size() == half
    ok = all(lst.delete_from_head() == value for value in range(half)) and ok
    for value in range(half):
        lst.insert_at_head(value)
    ok = lst.size() == half and ok
    ok = all(lst.delete_from_tail() == value for value in range(half)) and ok
    check(ok, True)
    check(lst.is_empty(), True)
    check(lst.traverse_forward(), "")
    check(lst.traverse_backward(), "")
    for _ in range(3):
        lst.insert_at_tail(7)
    first = lst.search_forward(7)
    lst.insert_before(first, 6)
    lst.insert_after(first, 8)
    check(lst.traverse_forward(), "6 -> 7 -> 8 -> 7 -> 7")
    check(lst.traverse_backward(), "7 -> 7 -> 8 -> 7 -> 6")


test_doubly_list()
test_large_drain_and_endpoint_helpers()

if failures:
    raise SystemExit(1)
