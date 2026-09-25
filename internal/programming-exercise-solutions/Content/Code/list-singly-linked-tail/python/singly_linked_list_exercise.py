import inspect


class Node:
    def __init__(self, value):
        self.value = value
        self.next = None


class IntList:
    def __init__(self):
        self.head = None
        self.tail = None
        self.count = 0

    def is_empty(self):
        return self.count == 0

    def size(self):
        return self.count

    def clear(self):
        self.head = None
        self.tail = None
        self.count = 0

    def first(self):
        return -1 if self.is_empty() else self.head.value

    def last(self):
        return -1 if self.is_empty() else self.tail.value

    def node_at(self, index):
        if index < 0 or index >= self.count:
            return None
        node = self.head
        for _ in range(index):
            node = node.next
        return node

    def get(self, index):
        node = self.node_at(index)
        return -1 if node is None else node.value

    def set(self, index, value):
        node = self.node_at(index)
        if node is None:
            return False
        node.value = value
        return True

    def add_first(self, value):
        node = Node(value)
        node.next = self.head
        self.head = node
        if self.tail is None:
            self.tail = node
        self.count += 1
        return True

    def add_last(self, value):
        node = Node(value)
        if self.is_empty():
            self.head = node
        else:
            self.tail.next = node
        self.tail = node
        self.count += 1
        return True

    def insert(self, index, value):
        if index < 0 or index > self.count:
            return False
        if index == 0:
            return self.add_first(value)
        if index == self.count:
            return self.add_last(value)
        previous = self.node_at(index - 1)
        node = Node(value)
        node.next = previous.next
        previous.next = node
        self.count += 1
        return True

    def remove_first(self):
        if self.is_empty():
            return -1
        value = self.head.value
        self.head = self.head.next
        self.count -= 1
        if self.head is None:
            self.tail = None
        return value

    def remove_last(self):
        if self.is_empty():
            return -1
        if self.count == 1:
            return self.remove_first()
        previous = self.node_at(self.count - 2)
        value = self.tail.value
        previous.next = None
        self.tail = previous
        self.count -= 1
        return value

    def remove(self, index):
        if index < 0 or index >= self.count:
            return -1
        if index == 0:
            return self.remove_first()
        if index == self.count - 1:
            return self.remove_last()
        previous = self.node_at(index - 1)
        value = previous.next.value
        previous.next = previous.next.next
        self.count -= 1
        return value

    def index_of(self, value):
        index = 0
        node = self.head
        while node is not None:
            if node.value == value:
                return index
            node = node.next
            index += 1
        return -1

    def contains(self, value):
        return self.index_of(value) != -1

    def delete(self, value):
        index = self.index_of(value)
        if index == -1:
            return False
        self.remove(index)
        return True


failures = 0


def check(actual, expected):
    global failures
    line = inspect.currentframe().f_back.f_lineno
    if actual == expected:
        print(f"PASS at test line {line}: got {actual!r}")
    else:
        failures += 1
        print(f"FAIL at test line {line}: expected {expected!r} but got {actual!r}")


def test_list():
    values = IntList()

    check(values.is_empty(), True)
    check(values.size(), 0)
    check(values.first(), -1)
    check(values.last(), -1)
    check(values.get(0), -1)
    check(values.remove_first(), -1)
    check(values.remove_last(), -1)
    check(values.remove(0), -1)

    check(values.add_last(4), True)       # [4]
    check(values.add_last(7), True)       # [4, 7]
    check(values.add_first(2), True)      # [2, 4, 7]
    check(values.insert(2, 9), True)      # [2, 4, 9, 7]

    check(values.size(), 4)
    check(values.is_empty(), False)
    check(values.first(), 2)
    check(values.last(), 7)
    check(values.get(0), 2)
    check(values.get(2), 9)
    check(values.get(4), -1)
    check(values.get(-1), -1)
    check(values.set(-1, 8), False)
    check(values.set(99, 8), False)
    check(values.insert(-1, 8), False)
    check(values.insert(99, 8), False)
    check(values.remove(-1), -1)
    check(values.remove(99), -1)

    check(values.set(1, 5), True)         # [2, 5, 9, 7]
    check(values.get(1), 5)
    check(values.set(4, 8), False)
    check(values.size(), 4)

    check(values.insert(0, 11), True)     # [11, 2, 5, 9, 7]
    check(values.first(), 11)
    check(values.insert(values.size(), 13), True)  # [11, 2, 5, 9, 7, 13]
    check(values.last(), 13)
    check(values.size(), 6)

    check(values.index_of(9), 3)
    check(values.index_of(100), -1)
    check(values.contains(7), True)
    check(values.contains(100), False)

    check(values.remove(3), 9)            # [11, 2, 5, 7, 13]
    check(values.get(3), 7)
    check(values.remove_first(), 11)      # [2, 5, 7, 13]
    check(values.remove_last(), 13)       # [2, 5, 7]
    check(values.size(), 3)
    check(values.first(), 2)
    check(values.last(), 7)

    check(values.delete(2), True)         # [5, 7]
    check(values.first(), 5)
    check(values.delete(7), True)         # [5]
    check(values.last(), 5)
    check(values.delete(5), True)         # []
    check(values.is_empty(), True)
    check(values.size(), 0)
    check(values.first(), -1)

    large = IntList()
    large_ok = all(large.add_last(i) for i in range(1000))
    large_ok = all(large.get(i) == i for i in range(1000)) and large_ok
    large_ok = all(large.remove_last() == i for i in range(999, -1, -1)) and large_ok
    check(large_ok and large.is_empty(), True)
    check(values.last(), -1)

    check(values.delete(5), False)
    check(values.remove_last(), -1)

    check(values.add_last(6), True)       # [6]
    check(values.first(), 6)
    check(values.last(), 6)
    check(values.remove_last(), 6)        # []
    check(values.is_empty(), True)

    check(values.add_first(8), True)      # [8]
    check(values.add_last(10), True)      # [8, 10]
    values.clear()                        # []
    check(values.is_empty(), True)
    check(values.size(), 0)
    check(values.first(), -1)

def test_large_indexed_workload():
    values = IntList()
    for i in range(200):
        check(values.add_last(i % 17), True)
    check(values.size(), 200)
    check(values.first(), 0)
    check(values.last(), 12)
    check(values.insert(100, 999), True)
    check(values.get(100), 999)
    check(values.size(), 201)
    check(values.remove(100), 999)
    check(values.size(), 200)
    check(values.index_of(5), 5)
    check(values.delete(5), True)
    check(values.index_of(5), 21)
    check(values.set(0, 777), True)
    check(values.get(0), 777)
    for _ in range(199):
        values.remove_last()
    check(values.size(), 0)
    check(values.add_first(42), True)
    check(values.remove_first(), 42)
    check(values.is_empty(), True)
    check(values.last(), -1)


def test_hundred_thousand_endpoint_operations():
    count = 100_000
    values = IntList()
    ok = all(values.add_last(i) for i in range(count))
    ok = values.size() == count and values.first() == 0 and values.last() == count - 1 and ok
    ok = all(values.remove_first() == i for i in range(count)) and ok
    check(ok and values.is_empty() and values.size() == 0 and values.first() == -1 and values.last() == -1, True)


test_list()
test_large_indexed_workload()
test_hundred_thousand_endpoint_operations()

if failures:
    raise SystemExit(1)
