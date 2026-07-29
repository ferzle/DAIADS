import inspect


class IntList:
    def __init__(self, capacity):
        self.A = [0] * capacity
        self.count = 0

    def is_empty(self):
        # TODO
        return False

    def size(self):
        # TODO
        return -1

    def clear(self):
        # TODO
        pass

    def first(self):
        # TODO
        return -1

    def last(self):
        # TODO
        return -1

    def get(self, index):
        # TODO
        return -1

    def set(self, index, value):
        # TODO
        return False

    def add_first(self, value):
        # TODO
        return False

    def add_last(self, value):
        # TODO
        return False

    def insert(self, index, value):
        # TODO
        return False

    def remove_first(self):
        # TODO
        return -1

    def remove_last(self):
        # TODO
        return -1

    def remove(self, index):
        # TODO
        return -1

    def index_of(self, value):
        # TODO
        return -1

    def contains(self, value):
        # TODO
        return False

    def delete(self, value):
        # TODO
        return False


def check(actual, expected):
    if actual == expected:
        print("pass")
    else:
        line = inspect.currentframe().f_back.f_lineno
        print(f"fail at test line {line}: expected {expected!r} but got {actual!r}")


def test_list():
    values = IntList(5)

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

    check(values.set(1, 5), True)         # [2, 5, 9, 7]
    check(values.get(1), 5)
    check(values.set(4, 8), False)
    check(values.size(), 4)

    check(values.add_last(11), True)      # [2, 5, 9, 7, 11]
    check(values.size(), 5)
    check(values.add_last(13), False)     # full
    check(values.add_first(13), False)    # full
    check(values.insert(2, 13), False)    # full
    check(values.size(), 5)

    check(values.index_of(9), 2)
    check(values.index_of(100), -1)
    check(values.contains(7), True)
    check(values.contains(100), False)

    check(values.remove(2), 9)            # [2, 5, 7, 11]
    check(values.get(2), 7)
    check(values.remove_first(), 2)       # [5, 7, 11]
    check(values.remove_last(), 11)       # [5, 7]
    check(values.size(), 2)
    check(values.first(), 5)
    check(values.last(), 7)

    check(values.delete(5), True)         # [7]
    check(values.delete(5), False)
    check(values.size(), 1)
    check(values.first(), 7)
    check(values.last(), 7)

    check(values.remove_last(), 7)        # []
    check(values.is_empty(), True)
    check(values.size(), 0)
    check(values.remove_last(), -1)

    check(values.add_last(6), True)       # [6]
    check(values.add_last(8), True)       # [6, 8]
    values.clear()                        # []
    check(values.is_empty(), True)
    check(values.size(), 0)
    check(values.first(), -1)


test_list()
